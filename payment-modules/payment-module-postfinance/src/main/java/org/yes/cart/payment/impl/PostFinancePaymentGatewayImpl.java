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

package org.yes.cart.payment.impl;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentAddress;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.impl.BasicCallbackInfoImpl;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.service.payment.PaymentLocaleTranslator;
import org.yes.cart.service.payment.impl.PaymentLocaleTranslatorImpl;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.utils.HttpParamsUtils;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.RegExUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;

/**
 * User: denispavlov
 * Date: 07/10/2015
 * Time: 14:47
 */
public class PostFinancePaymentGatewayImpl extends AbstractPostFinancePaymentGatewayImpl
        implements PaymentGatewayExternalForm, CallbackAware {

    private static final Logger LOG = LoggerFactory.getLogger(PostFinancePaymentGatewayImpl.class);

    private final static PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, false,
            true, true, true, true,
            null,
            false, false
    );


    // form post acton url
    static final String PF_POST_URL = "PF_POST_URL";
    // result_url  shopper will be redirected to
    static final String PF_RESULT_URL_HOME = "PF_RESULT_URL_HOME";
    static final String PF_RESULT_URL_CATALOG = "PF_RESULT_URL_CATALOG";
    static final String PF_RESULT_URL_ACCEPT = "PF_RESULT_URL_ACCEPT";
    static final String PF_RESULT_URL_DECLINE = "PF_RESULT_URL_DECLINE";
    static final String PF_RESULT_URL_EXCEPTION = "PF_RESULT_URL_EXCEPTION";
    static final String PF_RESULT_URL_CANCEL = "PF_RESULT_URL_CANCEL";

    // Affiliation name in PostFinance system
    static final String PF_PSPID = "PF_PSPID";

    // SHA-1 signature hash
    static final String PF_SHA_IN = "PF_SHA_IN";
    static final String PF_SHA_OUT = "PF_SHA_OUT";

    // Styling
    static final String PF_STYLE_TITLE = "PF_STYLE_TITLE";
    static final String PF_STYLE_BGCOLOR = "PF_STYLE_BGCOLOR";
    static final String PF_STYLE_TXTCOLOR = "PF_STYLE_TXTCOLOR";
    static final String PF_STYLE_TBLBGCOLOR = "PF_STYLE_TBLBGCOLOR";
    static final String PF_STYLE_TBLTXTCOLOR = "PF_STYLE_TBLTXTCOLOR";
    static final String PF_STYLE_BUTTONBGCOLOR = "PF_STYLE_BUTTONBGCOLOR";
    static final String PF_STYLE_BUTTONTXTCOLOR = "PF_STYLE_BUTTONTXTCOLOR";
    static final String PF_STYLE_FONTTYPE = "PF_STYLE_FONTTYPE";
    static final String PF_STYLE_LOGO = "PF_STYLE_LOGO";
    static final String PF_STYLE_TP = "PF_STYLE_TP";

    // Payment method configuration
    static final String PF_PM = "PF_PM";
    static final String PF_BRAND = "PF_BRAND";
    static final String PF_WIN3DS = "PF_WIN3DS";
    static final String PF_PMLIST = "PF_PMLIST";
    static final String PF_EXCLPMLIST = "PF_EXCLPMLIST";
    static final String PF_PMLISTTYPE = "PF_PMLISTTYPE";

    static final String PF_ITEMISED = "PF_ITEMISED";
    static final String PF_ITEMISED_ITEM_CAT = "PF_ITEMISED_ITEM_CAT";
    static final String PF_ITEMISED_SHIP_CAT = "PF_ITEMISED_SHIP_CAT";
    static final String PF_ITEMISED_USE_TAX_AMOUNT = "PF_ITEMISED_USE_TAX_AMOUNT";

    // Delivery & Invoice info enabled
    static final String PF_DELIVERY_AND_INVOICE_ON = "PF_DELIVERY_AND_INVOICE_ON";
    static final String PF_DELIVERY_AND_INVOICE_ADDR2_IS_NUMBER = "PF_DELIVERY_AND_INVOICE_ADDR2_IS_NUMBER";
    static final String PF_DELIVERY_AND_INVOICE_ADDR1_NUMBER_REGEX = "PF_DELIVERY_AND_INVOICE_ADDR1_NUMBER_REGEX";

    private final PaymentLocaleTranslator paymentLocaleTranslator = new PaymentLocaleTranslatorImpl();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostActionUrl() {
        return getParameterValue(PF_POST_URL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubmitButton(final String locale) {
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Callback convertToCallback(final Map privateCallBackParameters,
                                      final boolean forceProcessing) {

        if (privateCallBackParameters != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);
            final Map<String, String> sorted = new TreeMap<>();
            final String signature = copyHttpParamsAndRemoveSignature(params, sorted);
            final String verify = sha1sign(sorted, getParameterValue(PF_SHA_OUT));

            final boolean valid = verify.equals(signature);

            if (valid || forceProcessing) {
                if (valid) {
                    LOG.debug("Signature is valid");
                } else {
                    LOG.warn("Signature is not valid ... forced processing");
                }

                BigDecimal callbackAmount = null;
                try {
                    callbackAmount = new BigDecimal(sorted.get("AMOUNT"));
                } catch (Exception exp) {
                    LOG.error("Callback for {} did not have a valid amount {}", sorted.get("ORDERID"), sorted.get("AMOUNT"));
                }

                return new BasicCallbackInfoImpl(
                        sorted.get("ORDERID"),
                        CallbackOperation.PAYMENT,
                        callbackAmount,
                        privateCallBackParameters,
                        valid
                );
            } else {
                LOG.warn("Signature is not valid");
            }

        }

        return new BasicCallbackInfoImpl(
                null,
                CallbackOperation.INVALID,
                null,
                privateCallBackParameters,
                false
        );

    }

    protected String copyHttpParamsAndRemoveSignature(final Map<String, String> params, final Map<String, String> sorted) {
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            // need to recode keys to upper
            sorted.put(entry.getKey().toUpperCase(), entry.getValue());
        }
        return sorted.remove("SHASIGN");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preProcess(final Payment payment, final Callback callback, final String processorOperation) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcess(final Payment payment, final Callback callback, final String processorOperation) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallbackAware.CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult,
                                                                  final boolean forceProcessing) {

        String statusRes = null;

        if (callbackResult != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(callbackResult);
            final Map<String, String> sorted = new TreeMap<>();
            final String signature = copyHttpParamsAndRemoveSignature(params, sorted);
            final String verify = sha1sign(sorted, getParameterValue(PF_SHA_OUT));

            final boolean valid = verify.equals(signature);

            if (valid || forceProcessing) {
                if (valid) {
                    LOG.debug("Signature is valid");
                } else {
                    LOG.warn("Signature is not valid ... forced processing");
                }
                statusRes = sorted.get("STATUS");
            } else {
                LOG.warn("Signature is not valid");
            }

        }

        final boolean success = statusRes != null &&
                ("5".equalsIgnoreCase(statusRes)
                        || "9".equalsIgnoreCase(statusRes)
                        || "51".equalsIgnoreCase(statusRes)
                        || "91".equalsIgnoreCase(statusRes));


        if (LOG.isDebugEnabled()) {
            LOG.debug(HttpParamsUtils.stringify("PostFinance callback", callbackResult));
        }

        if (success) {
            if ("51".equalsIgnoreCase(statusRes) || "91".equalsIgnoreCase(statusRes)) {
                LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.UNSETTLED);
                return CallbackAware.CallbackResult.UNSETTLED;
            }
            LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.OK);
            return CallbackAware.CallbackResult.OK;
        }
        LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.FAILED);
        return CallbackAware.CallbackResult.FAILED;

    }



    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderReference, final Payment payment) {


        // Parameters must be in alpha order for hash
        final Map<String, String> params = new TreeMap<>();

        // 1. general parameters

        // Your affiliation name in our system
        params.put("PSPID", getParameterValue(PF_PSPID));
        // Your unique order number (merchant reference). The system checks that a payment has not been requested twice
        // for the same order. The ORDERID has to be assigned dynamically.
        params.put("ORDERID", orderReference);
        // Amount to be paid MULTIPLIED BY 100 since the format of the amount must not contain any decimals or other separators.
        // The amount must be assigned dynamically.
        params.put("AMOUNT", amount.multiply(MoneyUtils.HUNDRED).setScale(0, RoundingMode.HALF_UP).toPlainString());
        // ISO alpha order currency code, for example: EUR, USD, GBP, CHF, ...
        params.put("CURRENCY", currencyCode);
        // Language of the customer, for example: en_US, nl_NL, fr_FR, ...
        params.put("LANGUAGE", paymentLocaleTranslator.translateLocale(this, locale));

        // 2. optional customer details, highly recommended for fraud prevention

        // Customer name. It will be pre-initialised (but still editable) in the cardholder name field of the credit card details.
        setValueIfNotNull(params, "CN", cardHolderName);
        // Customer’s e-mail address
        setValueIfNotNull(params, "EMAIL", payment.getBillingEmail());

        // Delivery & Invoice info enabled
        final boolean invoiceDetailsEnabled = Boolean.valueOf(getParameterValue(PF_DELIVERY_AND_INVOICE_ON));
        if (invoiceDetailsEnabled) {

            final boolean useAddress2AsNumber = Boolean.valueOf(getParameterValue(PF_DELIVERY_AND_INVOICE_ADDR2_IS_NUMBER));
            final String address1NumberRegEx = getParameterValue(PF_DELIVERY_AND_INVOICE_ADDR1_NUMBER_REGEX);

            // Billing Address
            final PaymentAddress billing = payment.getBillingAddress() != null ? payment.getBillingAddress() : payment.getShippingAddress();
            if (billing != null) {
                setValueIfNotNull(params, "ECOM_BILLTO_COMPANY", billing.getCompanyName1());
                setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_NAME_PREFIX", billing.getSalutation());
                setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_NAME_FIRST", billing.getFirstname());
                setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_NAME_LAST", billing.getLastname());
                final Pair<String, String> numberAndStreet = determineAddressNumberAndStreet(billing, useAddress2AsNumber, address1NumberRegEx);
                setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_STREET_NUMBER", numberAndStreet.getFirst());
                setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_STREET_LINE1", numberAndStreet.getSecond());
                // setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_STREET_LINE2", billing.getAddrline2());
                setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_POSTALCODE", billing.getPostcode());
                setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_CITY", billing.getCity());
                setValueIfNotNull(params, "ECOM_BILLTO_POSTAL_COUNTRYCODE", billing.getCountryCode());
                setValueIfNotNull(params, "ECOM_BILLTO_TELECOM_MOBILE_NUMBER", billing.getMobile1());
                setValueIfNotNull(params, "ECOM_BILLTO_TELECOM_PHONE_NUMBER", billing.getPhone1());
            }

            // Delivery Address
            final PaymentAddress shipping = payment.getShippingAddress() != null ? payment.getShippingAddress() : payment.getBillingAddress();
            if (shipping != null) {
                setValueIfNotNull(params, "ECOM_SHIPTO_COMPANY", shipping.getCompanyName1());
                setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_NAME_PREFIX", shipping.getSalutation());
                setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_NAME_FIRST", shipping.getFirstname());
                setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_NAME_LAST", shipping.getLastname());
                final Pair<String, String> numberAndStreet = determineAddressNumberAndStreet(shipping, useAddress2AsNumber, address1NumberRegEx);
                setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_STREET_NUMBER", numberAndStreet.getFirst());
                setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_STREET_LINE1", numberAndStreet.getSecond());
                // setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_STREET_LINE2", shipping.getAddrline2());
                setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_POSTALCODE", shipping.getPostcode());
                setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_CITY", shipping.getCity());
                setValueIfNotNull(params, "ECOM_SHIPTO_POSTAL_COUNTRYCODE", shipping.getCountryCode());
                setValueIfNotNull(params, "ECOM_SHIPTO_TELECOM_MOBILE_NUMBER", shipping.getMobile1());
                setValueIfNotNull(params, "ECOM_SHIPTO_TELECOM_PHONE_NUMBER", shipping.getPhone1());
            }
        } else {
            final PaymentAddress address = payment.getBillingAddress();
            if (address != null) {
                // Customer’s street name and number
                setValueIfNotNull(params, "OWNERADDRESS", getAddressLines(address));
                // Customer’s postcode
                setValueIfNotNull(params, "OWNERZIP", address.getPostcode());
                // Customer’s town/city name
                setValueIfNotNull(params, "OWNERTOWN", address.getCity());
                // Customer’s country
                setValueIfNotNull(params, "OWNERCTY", address.getCountryCode());
                // Customer’s telephone number
                setValueIfNotNull(params, "OWNERTELNO", address.getPhone1());
            }
        }

        // Order description
        final boolean itemised = Boolean.valueOf(getParameterValue(PF_ITEMISED));
        params.put("COM", getDescription(payment, itemised));
        if (itemised) {
            populateItems(payment, params);
        }


        // 3. layout information: see Look & Feel of the Payment Page

        // Title and header of the page
        setParameterIfNotNull(params, "TITLE", PF_STYLE_TITLE);
        // Background colour
        setParameterIfNotNull(params, "BGCOLOR", PF_STYLE_BGCOLOR);
        // Text colour
        setParameterIfNotNull(params, "TXTCOLOR", PF_STYLE_TXTCOLOR);
        // Table background colour
        setParameterIfNotNull(params, "TBLBGCOLOR", PF_STYLE_TBLBGCOLOR);
        // Table text colour
        setParameterIfNotNull(params, "TBLTXTCOLOR", PF_STYLE_TBLTXTCOLOR);
        // Button background colour
        setParameterIfNotNull(params, "BUTTONBGCOLOR", PF_STYLE_BUTTONBGCOLOR);
        // Button text colour
        setParameterIfNotNull(params, "BUTTONTXTCOLOR", PF_STYLE_BUTTONTXTCOLOR);
        // Font family
        setParameterIfNotNull(params, "FONTTYPE", PF_STYLE_FONTTYPE);
        // URL/filename of the logo you want to display at the top of the payment page, next to the title.
        // The URL must be absolute (i.e. contain the full path), it cannot be relative.
        setParameterIfNotNull(params, "LOGO", PF_STYLE_LOGO);

        // 4. dynamic template page: see Look & Feel of the Payment Page
        setParameterIfNotNull(params, "TP", PF_STYLE_TP);

        // 5. payment methods/page specifics: see Payment method and payment page specifics
        // Payment method e.g. CreditCard, iDEAL or blank
        setParameterIfNotNull(params, "PM", PF_PM);
        // Credit card brand e.g. VISA or blank
        setParameterIfNotNull(params, "BRAND", PF_BRAND);
        // 3-D secure: MAINW or POPUP
        setParameterIfNotNull(params, "WIN3DS", PF_WIN3DS);
        // List of selected payment methods and/or credit card brands. Separated by a “;” (semicolon).  e.g. VISA;iDEAL
        setParameterIfNotNull(params, "PMLIST", PF_PMLIST);
        // List of payment methods and/or credit card brands that should NOT be shown. Separated by a “;” (semicolon).
        setParameterIfNotNull(params, "EXCLPMLIST", PF_EXCLPMLIST);
        // The possible values are 0, 1 and 2:
        // 0: Horizontally grouped logos with the group name on the left (default value)
        // 1: Horizontally grouped logos with no group names
        // 2: Vertical list of logos with specific payment method or brand name
        setParameterIfNotNull(params, "PMLISTTYPE", PF_PMLISTTYPE);

        // 6. link to your website: see Default reaction
        setParameterIfNotNull(params, "HOMEURL", PF_RESULT_URL_HOME);
        setParameterIfNotNull(params, "CATALOGURL", PF_RESULT_URL_CATALOG);

        // 7. post payment parameters: see Redirection depending on the payment result
        // <input type="hidden" name="COMPLUS" value="">
        // <input type="hidden" name="PARAMPLUS" value="">

        // 8. post payment parameters: see Direct feedback requests (Post-payment)
        // <input type="hidden" name="PARAMVAR" value="">

        // 9.  post payment redirection: see Redirection depending on the payment result
        setParameterIfNotNull(params, "ACCEPTURL", PF_RESULT_URL_ACCEPT);
        setParameterIfNotNull(params, "DECLINEURL", PF_RESULT_URL_DECLINE);
        setParameterIfNotNull(params, "EXCEPTIONURL", PF_RESULT_URL_EXCEPTION);
        setParameterIfNotNull(params, "CANCELURL", PF_RESULT_URL_CANCEL);

        // 10. optional operation field: see Operation
        // Operation code for the transaction. Possible values for new orders:
        // RES: request for authorisation
        // SAL: request for sale (payment)
        // NOTE: for now we shall support only SAL (AUTH_CAPTURE) because PostFinance does not have CAPTURE API and merchants
        //       need to login to their system to CAPTURE payments, which is a bit inconvenient and does nothave integration
        //       with YC.
        params.put("OPERATION", getExternalFormOperation());

        // 11. optional extra login detail field: see User field
        setValueIfNotNull(params, "USERID", payment.getBillingEmail());

        // 12. Alias details: see Alias Management documentation
        //        <input type="hidden" name="ALIAS" value="">
        //        <input type="hidden" name="ALIASUSAGE" value="">
        //        <input type="hidden" name="ALIASOPERATION" value="">
        //        <input type="submit" id="submit2" name="SUBMIT2" value="">

        // 13. check before the payment: see SHA-IN signature
        params.put("SHASIGN", sha1sign(params, getParameterValue(PF_SHA_IN)));

        final StringBuilder form = new StringBuilder();
        for (final Map.Entry<String, String> param : params.entrySet()) {
            form.append(getHiddenField(param.getKey(), param.getValue()));
        }

        LOG.debug("PostFinance form request: {}", form);

        return form.toString();

    }

    Pair<String, String> determineAddressNumberAndStreet(final PaymentAddress address,
                                                         final boolean useAddress2AsNumber,
                                                         final String address1NumberRegEx) {

        if (useAddress2AsNumber) {
            return new Pair<>(address.getAddrline2(), address.getAddrline1());
        }

        final String validExp = StringUtils.isNotBlank(address1NumberRegEx) ? address1NumberRegEx :
                "(\\s\\d+([a-zA-Z])*)|(\\d+([a-zA-Z])*\\s)"; // Street 12, 12b Street or Street 12ab

        final RegExUtils.RegEx regex = RegExUtils.getInstance(validExp);

        final Matcher matcher = regex.getPattern().matcher(address.getAddrline1());

        if (matcher.find()) {

            final String rawNumber = matcher.group(0);
            final String street = address.getAddrline1().replace(rawNumber, "").trim();
            final String number = rawNumber.trim();
            return new Pair<>(number, street);

        }

        return new Pair<>("", address.getAddrline1());
    }

    private static final int ITEMID = 15;
    private static final int ITEMNAME = 40;

    private void populateItems(final Payment payment, final Map<String, String> params) {

        final String itemCategory = getParameterValue(PF_ITEMISED_ITEM_CAT);
        final String shippingCategory = getParameterValue(PF_ITEMISED_SHIP_CAT);
        final boolean useTaxAmount = Boolean.valueOf(getParameterValue(PF_ITEMISED_USE_TAX_AMOUNT));

        BigDecimal totalItemsGross = Total.ZERO;

        for (final PaymentLine item : payment.getOrderItems()) {
            totalItemsGross = totalItemsGross.add(item.getQuantity().multiply(item.getUnitPrice()));
        }

        final int it = payment.getOrderItems().size();

        BigDecimal orderDiscountRemainder = Total.ZERO;
        BigDecimal orderDiscountPercent = Total.ZERO;
        final BigDecimal payGross = payment.getPaymentAmount();
        if (payGross.compareTo(totalItemsGross) < 0) {
            orderDiscountRemainder = totalItemsGross.subtract(payGross);
            orderDiscountPercent = orderDiscountRemainder.divide(totalItemsGross, 10, RoundingMode.HALF_UP);
        }


        int i = 1;
        boolean hasOrderDiscount = MoneyUtils.isPositive(orderDiscountRemainder);
        for (final PaymentLine item : payment.getOrderItems()) {

            final BigDecimal itemGrossAmount = item.getUnitPrice().multiply(item.getQuantity()).setScale(Total.ZERO.scale(), RoundingMode.HALF_UP);
            final BigDecimal unitTax = item.getTaxAmount().divide(item.getQuantity(), Total.ZERO.scale(), RoundingMode.CEILING);
            final BigDecimal taxRate = MoneyUtils.isPositive(item.getTaxAmount()) && MoneyUtils.isPositive(itemGrossAmount) ?
                    item.getTaxAmount().divide(itemGrossAmount.subtract(item.getTaxAmount()),3, BigDecimal.ROUND_HALF_EVEN).movePointRight(2) : BigDecimal.ZERO.setScale(1);
            setValueIfNotNull(params, "ITEMID" + i, item.getSkuCode().length() > ITEMID ? item.getSkuCode().substring(0, ITEMID - 1) + "~" : item.getSkuCode());
            setValueIfNotNull(params, "ITEMNAME" + i, item.getSkuName().length() > ITEMNAME ? item.getSkuName().substring(0, ITEMNAME - 1) + "~" : item.getSkuName());
            setValueIfNotNull(params, "ITEMQUANT" + i, item.getQuantity().stripTrailingZeros().toPlainString());
            if (hasOrderDiscount
                    && MoneyUtils.isPositive(orderDiscountRemainder)
                    && MoneyUtils.isPositive(itemGrossAmount)) {
                BigDecimal discount;
                if (i == it) {
                    // last item
                    discount = orderDiscountRemainder;
                } else {
                    BigDecimal itemDiscount = itemGrossAmount.multiply(orderDiscountPercent).setScale(Total.ZERO.scale(), RoundingMode.CEILING);
                    if (MoneyUtils.isFirstBiggerThanSecond(orderDiscountRemainder, itemDiscount)) {
                        discount = itemDiscount.divide(item.getQuantity(), Total.ZERO.scale(), RoundingMode.CEILING);
                        orderDiscountRemainder = orderDiscountRemainder.subtract(discount.multiply(item.getQuantity()).setScale(Total.ZERO.scale(), RoundingMode.CEILING));
                    } else {
                        discount = orderDiscountRemainder.divide(item.getQuantity(), Total.ZERO.scale(), RoundingMode.CEILING);
                        orderDiscountRemainder = Total.ZERO;
                    }

                }
                final BigDecimal scaleRate = discount.divide(item.getUnitPrice().subtract(discount), 10, RoundingMode.CEILING);
                final BigDecimal scaledTax = unitTax.multiply(scaleRate).setScale(Total.ZERO.scale(), RoundingMode.FLOOR);
                //setValueIfNotNull(params, "ITEMDISCOUNT" + i, discount.setScale(4, RoundingMode.FLOOR).toPlainString());
                setValueIfNotNull(params, "ITEMPRICE" + i, item.getUnitPrice().subtract(discount).setScale(4, RoundingMode.FLOOR).toPlainString());
                if (useTaxAmount) {
                    setValueIfNotNull(params, "ITEMVAT" + i, unitTax.subtract(scaledTax).setScale(4, RoundingMode.FLOOR).toPlainString());
                } else {
                    setValueIfNotNull(params, "ITEMVATCODE" + i, taxRate.toPlainString());
                }
                setValueIfNotNull(params, "TAXINCLUDED" + i, "1");
            } else {
                setValueIfNotNull(params, "ITEMPRICE" + i, item.getUnitPrice().setScale(4, RoundingMode.FLOOR).toPlainString());
                if (useTaxAmount) {
                    setValueIfNotNull(params, "ITEMVAT" + i, unitTax.toPlainString());
                } else {
                    setValueIfNotNull(params, "ITEMVATCODE" + i, taxRate.toPlainString());
                }
                setValueIfNotNull(params, "TAXINCLUDED" + i, "1");
            }

            if (item.isShipment() && StringUtils.isNotBlank(shippingCategory)) {
                setValueIfNotNull(params, "ITEMCATEGORY" + i, shippingCategory);
            }
            if (!item.isShipment() && StringUtils.isNotBlank(itemCategory)) {
                setValueIfNotNull(params, "ITEMCATEGORY" + i, itemCategory);
            }

            i++;
        }
    }

    /**
     * Supported operations are:
     * SAL: AUTH_CAPTURE
     * RES: AUTH (Capture is configured in PostFinance and thus only manual marker should be supported in YC)
     *
     * @return operation
     */
    protected String getExternalFormOperation() {
        return "SAL";
    }

    void setValueIfNotNull(final Map<String, String> params, final String key, final String value) {
        if (StringUtils.isNotBlank(value)) {
            params.put(key, StringUtils.remove(value, '\''));
        }
    }

    void setParameterIfNotNull(final Map<String, String> params, final String key, final String valueKey) {
        setValueIfNotNull(params, key, getParameterValue(valueKey));
    }

    private String getAddressLines(final PaymentAddress address) {
        final StringBuilder address1 = new StringBuilder();
        if (StringUtils.isNotBlank(address.getAddrline1())) {
            address1.append(address.getAddrline1());
        }
        if (StringUtils.isNotBlank(address.getAddrline2())) {
            if (address1.length() > 0) {
                address1.append(" ");
            }
            address1.append(address.getAddrline2());
        }
        return address1.toString();
    }


    /**
     * Get order description.
     *
     * @param payment payment
     * @return order description.
     */
    private String getDescription(final Payment payment, final boolean itemised) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (!itemised) {
            for (PaymentLine line : payment.getOrderItems()) {
                if (line.isShipment()) {
                    stringBuilder.append(line.getSkuName().replace("\"", "")).append(", ");
                } else {
                    stringBuilder.append(line.getSkuCode().replace("\"", ""));
                    stringBuilder.append("x");
                    stringBuilder.append(line.getQuantity().stripTrailingZeros().toPlainString());
                    stringBuilder.append(", ");
                }
            }
        }
        stringBuilder.append(payment.getBillingEmail());
        stringBuilder.append(", ");
        stringBuilder.append(payment.getOrderNumber());
        if (stringBuilder.length() > 100) {
            // Only 100 chars allowed
            return stringBuilder.substring(0, 100);
        }
        return stringBuilder.toString();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Payment authorizeCapture(final Payment payment, final boolean forceProcessing) {
        return payment;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Shipment not included. Will be added at capture operation.
     */
    @Override
    public Payment authorize(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment reverseAuthorization(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment capture(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment voidCapture(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment refund(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REFUND);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createPaymentPrototype(final String operation,
                                          final Map map,
                                          final boolean forceProcessing) {

        final Payment payment = new PaymentImpl();
        final Map<String, String> raw = HttpParamsUtils.createSingleValueMap(map);
        final Map<String, String> sorted = new TreeMap<>();
        copyHttpParamsAndRemoveSignature(raw, sorted);
        final String amount = sorted.get("AMOUNT");
        if (amount != null) {
            payment.setPaymentAmount(new BigDecimal(amount));
        }
        payment.setOrderCurrency(sorted.get("CURRENCY"));
        payment.setTransactionReferenceId(sorted.get("PAYID"));
        payment.setTransactionAuthorizationCode(sorted.get("ORDERID")); // this is order guid - we need it for refunds
        payment.setCardNumber(sorted.get("CARDNO"));
        payment.setCardType(sorted.get("BRAND"));
        payment.setCardHolderName(sorted.get("CN"));
        if (StringUtils.isNotBlank(sorted.get("ED"))) {
            payment.setCardExpireMonth(sorted.get("ED").substring(0, 2));
            payment.setCardExpireYear(sorted.get("ED").substring(2, 4));
        }

        final boolean prepare = PaymentGateway.AUTH.equals(operation) && MapUtils.isEmpty(map);
        final CallbackAware.CallbackResult res = prepare ? CallbackResult.PREPARE : getExternalCallbackResult(raw, forceProcessing);
        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());
        final StringBuilder msg = new StringBuilder();
        msg.append(sorted.get("STATUS"));
        if (StringUtils.isNotBlank(sorted.get("ACCEPTANCE"))) {
            msg.append(" ").append(sorted.get("ACCEPTANCE"));
        }
        if (StringUtils.isNotBlank(sorted.get("NCERROR"))) {
            msg.append(" ").append(sorted.get("NCERROR"));
        }
        payment.setTransactionOperationResultMessage(msg.toString());

        payment.setShopperIpAddress(sorted.get("IP"));

        return payment;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return PAYMENT_GATEWAY_FEATURE;
    }


}
