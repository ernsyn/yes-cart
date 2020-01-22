/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.web.page;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.persistence.entity.PaymentGatewayDescriptor;
import org.yes.cart.service.order.CouponCodeInvalidException;
import org.yes.cart.service.order.OrderAssemblyException;
import org.yes.cart.service.order.PlaceOrderDisabledException;
import org.yes.cart.service.order.SkuUnavailableException;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MessageFormatUtils;
import org.yes.cart.web.page.component.cart.ShoppingCartPaymentVerificationView;
import org.yes.cart.web.page.component.customer.address.AddressForm;
import org.yes.cart.web.page.component.customer.address.ManageAddressesView;
import org.yes.cart.web.page.component.customer.auth.GuestPanel;
import org.yes.cart.web.page.component.customer.auth.LoginPanel;
import org.yes.cart.web.page.component.customer.auth.RegisterPanel;
import org.yes.cart.web.page.component.footer.CheckoutFooter;
import org.yes.cart.web.page.component.header.CheckoutHeader;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.page.component.shipping.ShippingDeliveriesView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.service.*;
import org.yes.cart.web.theme.WicketPagesMounter;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Checkout page has following main steps:
 * <p/>
 * 1. big shopping cart with coupons, taxes, items manipulations.
 * 2. quick registration, can be skipped if customer is registered.
 * 3. billing and shipping addresses
 * 4. payment page with payment method selection
 * 5. successful/unsuccessful callback page
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/8/11
 * Time: 8:06 PM
 */
@RequireHttps
public class CheckoutPage extends AbstractWebPage {

    private static final long serialVersionUID = 20101107L;

    private static final Logger LOG = LoggerFactory.getLogger(CheckoutPage.class);

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String NAVIGATION_THREE_FRAGMENT = "threeStepNavigationFragment";
    private static final String NAVIGATION_FOUR_FRAGMENT = "fourStepNavigationFragment";
    private static final String LOGIN_FRAGMENT = "loginFragment";

    private static final String ADDRESS_FRAGMENT = "addressFragment";
    private static final String ADDRESS_EDIT_FRAGMENT = "createEditAddressFragment";
    private static final String ADDRESS_FORM = "addressForm";

    private static final String SHIPPING_ADDRESS_VIEW = "shippingAddress";
    private static final String BILLING_ADDRESS_VIEW = "billingAddress";
    private static final String BILLING_THE_SAME_FORM = "billingTheSameForm";
    private static final String BILLING_THE_SAME = "billingTheSame";


    private static final String SHIPMENT_FRAGMENT = "shipmentFragment";
    private static final String SHIPMENT_VIEW = "shipmentView";

    private static final String PAYMENT_FRAGMENT = "paymentFragment";
    private static final String PAYMENT_FRAGMENT_OPTIONS_FORM = "paymentOptionsForm";
    private static final String PAYMENT_FRAGMENT_MD_CHECKBOX = "multipleDelivery";
    private static final String PAYMENT_FRAGMENT_MD_LABEL = "multipleDeliveryLabel";
    private static final String PAYMENT_FRAGMENT_GATEWAY_CHECKBOX = "paymentGateway";
    private static final String PAYMENT_FRAGMENT_PAYMENT_FORM = "dynamicPaymentForm";


    private static final String CONTENT_VIEW = "content";
    private static final String NAVIGATION_VIEW = "navigation";

    private static final String PART_REGISTER_VIEW = "registerView";
    private static final String PART_LOGIN_VIEW = "loginView";
    private static final String PART_GUEST_VIEW = "guestView";

    private static final String ERROR = "e";
    private static final String ERROR_COUPON = "ec";
    private static final String ERROR_SKU = "es";

    public static final String STEP = "step";
    private static final String GUEST = "guest";

    private static final String STEP_LOGIN = "login";
    public static final String STEP_ADDR = "address";
    private static final String STEP_SHIPMENT = "ship";
    private static final String STEP_PAY = "payment";

    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    // ---------------------------------- PARAMETER NAMES BEGIN ------------------------------ //
    //three steps checkout process, because customer already logged in
    // or registered
    public static final String THREE_STEPS_PROCESS = "thp";
    // ---------------------------------- PARAMETER NAMES  END ------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CHECKOUT_SERVICE_FACADE)
    private CheckoutServiceFacade checkoutServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.SHIPPING_SERVICE_FACADE)
    private ShippingServiceFacade shippingServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.ADDRESS_BOOK_FACADE)
    private AddressBookFacade addressBookFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    protected ContentServiceFacade contentServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_PAGES_MOUNTER)
    private WicketPagesMounter wicketPagesMounter;

    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public CheckoutPage(final PageParameters params) {

        super(params);

        final ShoppingCart cart = getCurrentCart();
        final Shop shop = getCurrentShop();

        final Customer customer = customerServiceFacade.getCheckoutCustomer(shop, cart);

        final boolean guestInProgress =
                (params.getNamedKeys().contains(STEP) || "1".equals(params.get(GUEST).toString()))
                    && customer != null && customer.isGuest();

        final boolean sessionSignedIn = ((AuthenticatedWebSession) getSession()).isSignedIn();

        final boolean threeStepsProcess =
                guestInProgress ||
                        (params.get(THREE_STEPS_PROCESS).toBoolean(sessionSignedIn) && sessionSignedIn);

        final String addressId = params.get(WebParametersKeys.ADDRESS_ID).toString();
        final String addressType = params.get(WebParametersKeys.ADDRESS_TYPE).toString();

        final String currentStep =
                params.get(STEP).toString(threeStepsProcess ? null : STEP_LOGIN);
        if (currentStep == null) {
            if (shippingServiceFacade.isSkippableAddress(cart)) {
                final PageParameters parameters = new PageParameters(getPageParameters());
                parameters.set(STEP, STEP_SHIPMENT);
                setResponsePage(this.getClass(), parameters);
            } else {
                final PageParameters parameters = new PageParameters(getPageParameters());
                parameters.set(STEP, STEP_ADDR);
                setResponsePage(this.getClass(), parameters);
            }
        }

        add(
                new FeedbackPanel(FEEDBACK)
        ).add(
                new Fragment(NAVIGATION_VIEW, threeStepsProcess ?
                        NAVIGATION_THREE_FRAGMENT : NAVIGATION_FOUR_FRAGMENT, this)
        ).add(
                getContent(currentStep, customer, cart, guestInProgress, sessionSignedIn, addressId, addressType)
        ).addOrReplace(
                new CheckoutFooter(FOOTER)
        ).addOrReplace(
                new CheckoutHeader(HEADER)
        ).add(
                new ServerSideJs("serverSideJs")
        ).add(
                new HeaderMetaInclude("headerInclude")
        );


    }






    /**
     * Resolve content by given current step.
     *
     * @param currentStep current step label
     * @param customer checkout customer (registered or guest)
     * @param cart current cart
     * @param guestInProgress guest checkout in progress (i.e. URL has step)
     * @param sessionSignedIn wicket session is authenticated
     * @param address address id to edit
     * @param addressType address type for new addresses
     *
     * @return markup container
     */
    private MarkupContainer getContent(final String currentStep,
                                       final Customer customer,
                                       final ShoppingCart cart,
                                       final boolean guestInProgress,
                                       final boolean sessionSignedIn,
                                       final String address,
                                       final String addressType) {

        if (!STEP_LOGIN.equals(currentStep)
                && !guestInProgress
                && (!sessionSignedIn || cart.getLogonState() != ShoppingCart.LOGGED_IN)) {
            final PageParameters parameters = new PageParameters(getPageParameters());
            parameters.set(STEP, STEP_LOGIN);
            setResponsePage(this.getClass(), parameters);
            return createLoginFragment();
        }

        if (STEP_ADDR.equals(currentStep)) {
            if (cart.isBillingAddressNotRequired() && cart.isDeliveryAddressNotRequired()) {
                final PageParameters parameters = new PageParameters(getPageParameters());
                parameters.set(STEP, STEP_SHIPMENT);
                setResponsePage(this.getClass(), parameters);
                return createShipmentFragment();
            }
            if (customerNeedsToEnterAddress(customer, cart) ||
                    StringUtils.isNotBlank(address) || StringUtils.isNotBlank(addressType)) {
                return createEditAddressFragment(address, addressType);
            }
            return createAddressFragment();
        } else if (STEP_SHIPMENT.equals(currentStep)) {

            performOrderSplittingBeforeShipping();

            return createShipmentFragment();
        } else if (STEP_PAY.equals(currentStep)) {
            // Need to make sure we execute commands before we recreate order (we may need to choose another SLA)
            executeHttpPostedCommands();
            // For final step we:
            if (customerNeedsToEnterAddress(customer, cart)) {
                // Must have an address if it is required
                final PageParameters parameters = new PageParameters(getPageParameters());
                parameters.set(STEP, STEP_ADDR);
                setResponsePage(this.getClass(), parameters);
                return createAddressFragment();
            }
            if (!cart.isAllCarrierSlaSelected() || cart.getShippingList().isEmpty()) {
                // Must select a carrier
                final PageParameters parameters = new PageParameters(getPageParameters());
                parameters.set(STEP, STEP_SHIPMENT);
                setResponsePage(this.getClass(), parameters);
                return createShipmentFragment();
            }

            recreateOrderBeforePayment();

            return createPaymentFragment();
        } else {
            return createLoginFragment();
        }
    }

    private boolean customerNeedsToEnterAddress(final Customer customer, final ShoppingCart cart) {
        return (!cart.isBillingAddressNotRequired() || !cart.isDeliveryAddressNotRequired())
                && !addressBookFacade.customerHasAtLeastOneAddress(customer.getEmail(), getCurrentCustomerShop());
    }

    private boolean customerNeedsToEnterAddress(final Customer customer, final ShoppingCart cart, final String type) {
        return ((Address.ADDR_TYPE_BILLING.equals(type) && !cart.isBillingAddressNotRequired())
                || (Address.ADDR_TYPE_SHIPPING.equals(type) && !cart.isDeliveryAddressNotRequired()))
                && !addressBookFacade.customerHasAtLeastOneAddress(customer.getEmail(), getCurrentCustomerShop(), type);
    }


    private void performOrderSplittingBeforeShipping() {

        final ShoppingCart cart = getCurrentCart();

        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SPLITCARTITEMS,
                cart,
                Collections.singletonMap(ShoppingCartCommand.CMD_SPLITCARTITEMS, ShoppingCartCommand.CMD_SPLITCARTITEMS));

        persistCartIfNecessary();

    }

    private void recreateOrderBeforePayment() {

        final ShoppingCart cart = getCurrentCart();

        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_RECALCULATEPRICE,
                cart,
                Collections.singletonMap(ShoppingCartCommand.CMD_RECALCULATEPRICE, ShoppingCartCommand.CMD_RECALCULATEPRICE));

        persistCartIfNecessary();

        try {

            checkoutServiceFacade.createFromCart(cart);

        } catch (PlaceOrderDisabledException checkoutDisabled) {

            LOG.warn(checkoutDisabled.getMessage());

            setResponsePage(wicketPagesMounter.getPageProviderByUri("/cart").get(), new PageParameters().set(ERROR, "1"));


        } catch (CouponCodeInvalidException invalidCoupon) {

            LOG.warn(invalidCoupon.getMessage());

            setResponsePage(wicketPagesMounter.getPageProviderByUri("/cart").get(), new PageParameters()
                            .set(ERROR, ERROR_COUPON)
                            .set(ERROR_COUPON, invalidCoupon.getCoupon()));

        } catch (SkuUnavailableException skuUnavailable) {

            LOG.warn(skuUnavailable.getMessage());

            setResponsePage(wicketPagesMounter.getPageProviderByUri("/cart").get(), new PageParameters()
                    .set(ERROR, ERROR_SKU)
                    .set(ERROR_SKU, "(" + skuUnavailable.getSkuCode() + ") " + skuUnavailable.getSkuName()));

        } catch (OrderAssemblyException assembly) {

            LOG.error(assembly.getMessage(), assembly);

            setResponsePage(wicketPagesMounter.getPageProviderByUri("/cart").get(), new PageParameters().set(ERROR, "1"));

        }
    }

    /**
     * The default fragment is login/register page.
     *
     * @return login fragment
     */
    private MarkupContainer createLoginFragment() {
        return new Fragment(CONTENT_VIEW, LOGIN_FRAGMENT, this)
                .add(new LoginPanel(PART_LOGIN_VIEW, true))
                .add(new RegisterPanel(PART_REGISTER_VIEW, true))
                .add(new GuestPanel(PART_GUEST_VIEW));
    }

    /**
     * Create payment fragment with order verification and payment methods forms.
     * <p/>
     * Shopping cart form. Used to show products in cart , adjust product quantity.
     * <p/>
     * <p/>
     * Complex form with several deliveries the shopping cart form will show following items:
     * <pre>
     *  -----------------------------------
     * name             price   qty    amount
     * sku item 1        2       2      4
     * sku item 2        3       3      6
     * subtotal                         10
     * delivery                         2
     * tax                              3
     * total                            15
     *
     * sku item 3        1       3      3
     * sku item 4        1       5      5
     * subtotal                         8
     * delivery                         2
     * tax                              3
     * total                            13
     *
     * grand total                      28
     *
     * ----------------------------------------
     * payment form
     * ----------------------------------------
     * </pre>
     *
     * @return payment fragment of checkout process.
     */
    private MarkupContainer createPaymentFragment() {

        final MarkupContainer rez = new Fragment(CONTENT_VIEW, PAYMENT_FRAGMENT, this);

        final ShoppingCart shoppingCart = getCurrentCart();

        final CustomerOrder customerOrder = checkoutServiceFacade.findByReference(shoppingCart.getGuid());
        if (customerOrder == null) {
            return rez;
        }

        final OrderInfo orderInfo = shoppingCart.getOrderInfo();
        boolean showMultipleDelivery = false;
        for (final Map.Entry<String, Boolean> available : orderInfo.getMultipleDeliveryAvailable().entrySet()) {
            if (available.getValue() != null && available.getValue()) {
                showMultipleDelivery = true;
                break; // At least one supplier can do multi
            }
        }
        final boolean multipleDelivery = orderInfo.isMultipleDelivery();

        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETPGLABEL,
                shoppingCart,
                Collections.singletonMap(ShoppingCartCommand.CMD_SETPGLABEL, null));
        persistCartIfNecessary();

        rez.addOrReplace(new Label(PAYMENT_FRAGMENT_PAYMENT_FORM));
        rez.addOrReplace(new ShoppingCartPaymentVerificationView("orderVerificationView", shoppingCart.getGuid(), false));

        final Component multiDelivery = new CheckBox(PAYMENT_FRAGMENT_MD_CHECKBOX, new Model<>(multipleDelivery));
        multiDelivery.add(new FormComponentUpdatingBehavior() {
            @Override
            protected void onUpdate() {
                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_MULTIPLEDELIVERY,
                        getCurrentCart(),
                        Collections.singletonMap(ShoppingCartCommand.CMD_MULTIPLEDELIVERY, Boolean.toString((Boolean) multiDelivery.getDefaultModelObject())));
                persistCartIfNecessary();
                setResponsePage(
                        getPage().getPageClass(),
                        new PageParameters().set(
                                CheckoutPage.THREE_STEPS_PROCESS,
                                "true"
                        ).set(
                                CheckoutPage.STEP,
                                CheckoutPage.STEP_PAY
                        )
                );
            }
        }).setVisible(showMultipleDelivery);

        final List<Pair<PaymentGatewayDescriptor, String>> available =
                checkoutServiceFacade.getPaymentGatewaysDescriptors(getCurrentShop(), getCurrentCart());

        final RadioGroup pgSelector = new RadioGroup<String>(
                PAYMENT_FRAGMENT_GATEWAY_CHECKBOX,
                new PropertyModel<>(orderInfo, "paymentGatewayLabel"));
        pgSelector.add(new FormComponentUpdatingBehavior() {
            @Override
            protected void onUpdate() {
                final ShoppingCart cart = getCurrentCart();
                final Shop shop = getCurrentShop();

                final Customer customer = customerServiceFacade.getCheckoutCustomer(shop, cart);

                if ((!((AuthenticatedWebSession) getSession()).isSignedIn()
                        || cart.getLogonState() != ShoppingCart.LOGGED_IN) &&
                        (customer == null || !customer.isGuest())) {
                    // Make sure we are logged in on the very last step
                    final PageParameters parameters = new PageParameters(getPageParameters());
                    parameters.set(STEP, STEP_LOGIN);
                    setResponsePage(getPage().getClass(), parameters);
                }

                final CustomerOrder order = checkoutServiceFacade.findByReference(cart.getGuid());
                final Total total = checkoutServiceFacade.getOrderTotal(order);
                final BigDecimal grandTotal = total.getTotalAmount();

                // update pgLabel and delivery info on the order
                order.setPgLabel(orderInfo.getPaymentGatewayLabel());
                checkoutServiceFacade.estimateDeliveryTimeForOnlinePaymentOrder(order);
                checkoutServiceFacade.update(order);

                // Refresh the order view
                rez.addOrReplace(new ShoppingCartPaymentVerificationView("orderVerificationView", shoppingCart.getGuid(), false));


                final String htmlForm = getPaymentForm(order, grandTotal);

                rez.addOrReplace(
                        new Label(PAYMENT_FRAGMENT_PAYMENT_FORM, htmlForm)
                                .setEscapeModelStrings(false)
                );

                shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SETPGLABEL,
                        cart,
                        Collections.singletonMap(ShoppingCartCommand.CMD_SETPGLABEL, orderInfo.getPaymentGatewayLabel()));

                persistCartIfNecessary();
            }
        });
        pgSelector.add(
                new ListView<Pair<PaymentGatewayDescriptor, String>>("pgList", available) {
                    @Override
                    protected void populateItem(final ListItem<Pair<PaymentGatewayDescriptor, String>> pgListItem) {
                        pgListItem.add(new Radio<>("pgListLabel", new Model<>(pgListItem.getModelObject().getFirst().getLabel())));
                        pgListItem.add(new Label("pgListName", pgListItem.getModelObject().getSecond()));
                        final boolean infoVisible = pgListItem.getModelObject().getFirst().getLabel().equals(orderInfo.getPaymentGatewayLabel());
                        final long contentShopId = getCurrentShopId();
                        pgListItem.add(new Label("pgInfo", contentServiceFacade.getContentBody("checkout_payment_" + pgListItem.getModelObject().getFirst().getLabel(),
                                contentShopId, getLocale().getLanguage())).setEscapeModelStrings(false).setVisible(infoVisible));
                    }
                });

        rez.addOrReplace(
                new Form(PAYMENT_FRAGMENT_OPTIONS_FORM)
                        .add(multiDelivery)
                        .add(
                                new Label(PAYMENT_FRAGMENT_MD_LABEL,
                                        new StringResourceModel(PAYMENT_FRAGMENT_MD_LABEL, this)

                                ).setVisible(showMultipleDelivery)

                        )
                        .add(pgSelector)
        );


        return rez;
    }

    /**
     * Get html form for payment.
     *
     * @param order      order
     * @param grandTotal amount
     * @return payment form
     */
    protected String getPaymentForm(final CustomerOrder order,
                                    final BigDecimal grandTotal) {

        final ShoppingCart cart = getCurrentCart();

        String fullName = (order.getFirstname()
                        + " "
                        + order.getLastname()).toUpperCase();

        final PaymentGateway gateway = checkoutServiceFacade.getOrderPaymentGateway(order);
        final Payment payment = checkoutServiceFacade.createPaymentToAuthorize(order);

        final String submitBtnValue = getSubmitButton(gateway, cart.getCurrentLocale());
        final String postActionUrl = getPostActionUrl(gateway);

        final String htmlFragment = gateway.getHtmlForm(
                fullName,
                cart.getCurrentLocale(),
                grandTotal,
                cart.getCurrencyCode(),
                StringUtils.isNotBlank(order.getOrdernum()) ? order.getOrdernum() : cart.getGuid(),
                payment);


        return MessageFormatUtils.format(
                "<form method=\"POST\" action=\"{}\" class=\"form-horizontal\">\n" +
                        "{}\n" +
                        "<div id=\"paymentDiv\">\n" +
                        "{}" +
                        "</div></form>",
                postActionUrl,
                htmlFragment,
                submitBtnValue
        );

    }

    /**
     * Get submit button html code.
     *
     * @return html code for submit button.
     */
    private String getSubmitButton(final PaymentGateway gateway, final String locale) {
        String rez = null;
        if (gateway instanceof PaymentGatewayExternalForm) {
            rez = ((PaymentGatewayExternalForm) gateway).getSubmitButton(locale);
        }
        if (StringUtils.isBlank(rez)) {
            if (gateway.getPaymentGatewayFeatures().isOnlineGateway()) {
                rez = "<input type=\"submit\" value=\"" + getLocalizer().getString("paymentSubmit", this) + "\">";
            } else {
                rez = "<input type=\"submit\" value=\"" + getLocalizer().getString("orderPlace", this) + "\">";
            }
        }
        return rez;
    }

    /**
     * Get the post action url for payment.
     *
     * @param gateway gateway
     * @return url for post
     */
    private String getPostActionUrl(final PaymentGateway gateway) {
        if (gateway instanceof PaymentGatewayExternalForm) {
            // some pgs will point to local mounted page (e.g. paypal express points to paymentpaypalexpress)
            // that triggers internal payment information processing via filter
            return ((PaymentGatewayExternalForm) gateway).getPostActionUrl();
        }
        // By default all payment processors and gateways  parked to page, that mounted with this url
        return "payment";
    }

    /**
     * Create shipment method selection fragment.
     *
     * @return shipment method fragment
     */

    private MarkupContainer createShipmentFragment() {
        return new Fragment(CONTENT_VIEW, SHIPMENT_FRAGMENT, this)
                .add(
                        new ShippingDeliveriesView(SHIPMENT_VIEW, false)
                );
    }


    /**
     * Create address edit fragment to facilitate address entry form.
     *
     * @return address fragment.
     */
    private MarkupContainer createEditAddressFragment(final String addrId, final String addrType) {

        MarkupContainer rez;

        final Shop shop = getCurrentShop();
        final ShoppingCart cart = getCurrentCart();

        final Customer customer = customerServiceFacade.getCheckoutCustomer(shop, cart);

        final Address originalAddress = addressBookFacade.getAddress(customer, getCurrentCustomerShop(), addrId, addrType);
        final Address address;
        if (originalAddress.getAddressId() > 0L) {
            // Make a copy so that we do not mutate the original
            address = addressBookFacade.copyAddress(customer, getCurrentCustomerShop(), addrId, originalAddress.getAddressType());
            address.setAddressId(originalAddress.getAddressId());
        } else {
            address = originalAddress;
        }

        final PageParameters parameters = new PageParameters();
        if (customer.isGuest()) {
            parameters.set("guest", "1");
        }
        parameters.set(STEP, STEP_ADDR);

        rez = new Fragment(CONTENT_VIEW, ADDRESS_EDIT_FRAGMENT, this);

        rez.add(
                new AddressForm(
                        ADDRESS_FORM,
                        new Model<>(address),
                        addrType,
                        this.getClass(),
                        parameters,
                        this.getClass(),
                        parameters
                )
        );


        if (Address.ADDR_TYPE_SHIPPING.equals(address.getAddressType())) {

            info(getLocalizer().getString("selectDeliveryAddress", this));

        } else {

            info(getLocalizer().getString("selectBillingAddress", this));

        }

        return rez;
    }

    /**
     * Create address fragment to manage shipping and billing addresses.
     *
     * @return address fragment.
     */
    private MarkupContainer createAddressFragment() {

        MarkupContainer rez;

        final Shop shop = getCurrentShop();
        final ShoppingCart cart = getCurrentCart();

        final boolean forceTwoAddresses = cart.getOrderInfo().isSeparateBillingAddressEnabled();
        final boolean billingAddressHidden = !cart.getOrderInfo().isSeparateBillingAddress();

        final Customer customer = customerServiceFacade.getCheckoutCustomer(shop, cart);

        final Model<Customer> customerModel = new Model<>(customer);

        final ManageAddressesView shippingAddress =
                new ManageAddressesView(SHIPPING_ADDRESS_VIEW, customerModel, Address.ADDR_TYPE_SHIPPING, true) {
                    @Override
                    protected String getAddressPageUri() {
                        return "/checkout";
                    }
                };

        final ManageAddressesView billingAddress =
                new ManageAddressesView(BILLING_ADDRESS_VIEW, customerModel, Address.ADDR_TYPE_BILLING, true) {
                    @Override
                    protected String getAddressPageUri() {
                        return "/checkout";
                    }
                };

        rez = new Fragment(CONTENT_VIEW, ADDRESS_FRAGMENT, this);

        rez.add(
                shippingAddress
        ).add(
                billingAddress.setVisible(!billingAddressHidden)
        );

        rez.add(
                new Form(BILLING_THE_SAME_FORM).add(
                        new CheckBox(BILLING_THE_SAME, new Model<>(billingAddressHidden)) {{
                            add(new FormComponentUpdatingBehavior() {
                                @Override
                                protected void onUpdate() {
                                    billingAddress.setVisible(!billingAddressHidden);
                                    shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_SEPARATEBILLING, getCurrentCart(),
                                            new HashMap() {{
                                                put(ShoppingCartCommand.CMD_SEPARATEBILLING, Boolean.valueOf(!billingAddressHidden));
                                            }}
                                    );
                                    persistCartIfNecessary();

                                    final Customer customer = customerServiceFacade.getCheckoutCustomer(getCurrentShop(), getCurrentCart());
                                    if (!billingAddressHidden && customerNeedsToEnterAddress(customer, getCurrentCart(), Address.ADDR_TYPE_BILLING)) {
                                        final PageParameters parameters = new PageParameters();
                                        parameters.set(STEP, STEP_ADDR);
                                        parameters.set(WebParametersKeys.ADDRESS_TYPE, Address.ADDR_TYPE_BILLING);
                                        setResponsePage(CheckoutPage.this.getClass(), parameters);
                                    } else {
                                        addFeedbackForAddressSelection();
                                    }
                                }
                            });
                        }}
                ).setVisible(!forceTwoAddresses)
        );

        addFeedbackForAddressSelection();

        return rez;
    }


    private void addFeedbackForAddressSelection() {

        final Shop shop = getCurrentShop();
        final Shop customerShop = getCurrentCustomerShop();
        final ShoppingCart cart = getCurrentCart();

        final Customer customer = customerServiceFacade.getCheckoutCustomer(
                shop,
                cart);

        if (addressBookFacade.getAddresses(customer, customerShop, Address.ADDR_TYPE_SHIPPING).isEmpty()) {

            info(getLocalizer().getString("selectDeliveryAddress", this));

        } else if (cart.getOrderInfo().isSeparateBillingAddress()) {

            if (addressBookFacade.getAddresses(customer, customerShop, Address.ADDR_TYPE_BILLING).isEmpty()) {
                info(getLocalizer().getString("selectBillingAddress", this));
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();
        super.onBeforeRender();
        persistCartIfNecessary();

    }

}
