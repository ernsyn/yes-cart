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
import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.yes.cart.constants.Constants;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.misc.LanguageService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.shoppingcart.support.ShoppingCartPersister;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.service.wicketsupport.WicketSupportFacade;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WicketServiceSpringKeys;
import org.yes.cart.web.support.entity.decorator.DecoratorFacade;
import org.yes.cart.web.support.i18n.I18NWebSupport;
import org.yes.cart.web.theme.WicketPagesMounter;
import org.yes.cart.web.utils.WicketUtil;

import java.util.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/10/11
 * Time: 10:22 AM
 */
public class AbstractWebPage extends WebPage {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWebPage.class);

    public static final String FEEDBACK = "feedback";
    public static final String FOOTER = "footer";
    public static final String HEADER = "header";

    @SpringBean(name = WicketServiceSpringKeys.WICKET_UTIL)
    private WicketUtil wicketUtil;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;

    @SpringBean(name = StorefrontServiceSpringKeys.CART_PERSISTER)
    private ShoppingCartPersister shoppingCartPersister;

    @SpringBean(name = ServiceSpringKeys.LANGUAGE_SERVICE)
    private LanguageService languageService;

    @SpringBean(name = StorefrontServiceSpringKeys.I18N_SUPPORT)
    private I18NWebSupport i18NWebSupport;

    @SpringBean(name = StorefrontServiceSpringKeys.DECORATOR_FACADE)
    private DecoratorFacade decoratorFacade;

    @SpringBean(name = WicketServiceSpringKeys.WICKET_SUPPORT_FACADE)
    private WicketSupportFacade wicketSupportFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.WICKET_PAGES_MOUNTER)
    private WicketPagesMounter wicketPagesMounter;


    /**
     * Page title.
     */
    private static final String PAGE_TITLE = "pageTitle";
    /**
     * Meta created.
     */
    private static final String CREATED = "created";
    /**
     * Meta description.
     */
    private static final String DESCRIPTION = "description";
    /**
     * Meta keywords.
     */
    private static final String KEYWORDS = "keywords";
    /**
     * Meta keywords.
     */
    private static final String CANONICAL = "relCanonical";


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public AbstractWebPage(final PageParameters params) {
        super(params);

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        if (cart == null) {
            // Abnormal flow (possibly due incorrect URL pattern)
            setResponsePage(Application.get().getHomePage());
        }

        if (cart != null) {
            if (StringUtils.isBlank(cart.getCurrentLocale())) {
                getShoppingCartCommandFactory().execute(cart,
                        Collections.singletonMap(
                                ShoppingCartCommand.CMD_CHANGELOCALE,
                                getSession().getLocale().getLanguage()
                        ));
            }
            // reinstate the current cart language as our session is transient
            getSession().setLocale(new Locale(cart.getCurrentLocale()));
        }
        setStatelessHint(true);

    }


    @Override
    protected void onBeforeRender() {

        super.onBeforeRender();
        // need to call super first because components need to perform before render to initialise first
        addOrReplace(new Label(
                PAGE_TITLE,
                getPageTitle()));

        final Label desc = new Label(DESCRIPTION, "");
        final IModel<String> descModel = getDescription();
        desc.add(new AttributeAppender("content", descModel, " "));
        addOrReplace(desc);
        desc.setVisible(descModel != null);

        final Label keywords = new Label(KEYWORDS, "");
        final IModel<String> keywordsModel = getKeywords();
        keywords.add(new AttributeAppender("content", keywordsModel, " "));
        addOrReplace(keywords);
        keywords.setVisible(keywordsModel != null);

        Label created = new Label(CREATED, "");
        created.add(new AttributeAppender("content", getCreated(), " "));
        addOrReplace(created);

        final Label relCanonical = new Label(CANONICAL, "");
        final IModel<String> relCanonicalModel = getRelCanonical();
        relCanonical.add(new AttributeAppender("href", relCanonicalModel, " "));
        addOrReplace(relCanonical);
        relCanonical.setVisible(relCanonicalModel != null);

        determineStatefulComponentOnDev();

    }

    private void determineStatefulComponentOnDev() {

        if (Application.get().getConfigurationType() == RuntimeConfigurationType.DEVELOPMENT && !isPageStateless()) {

            final List<String> statefulComponentIds = new ArrayList<>();
            this.visitChildren(Component.class, (object, objectIVisit) -> {
                if (!object.isStateless()) {
                    statefulComponentIds.add(object.getId());
                }
            });
            LOG.warn("[DEV] Page {} is stateful because of the following components: {}", getClass().getCanonicalName(), statefulComponentIds);

        }
    }


    /**
     * Executes Http commands that are posted via http and are available from
     * this.getPageParameters() method. This method should be the first thing that
     * is executed if a page is using shopping cart.
     * <p/>
     * This method DOES NOT persist the cart to cookies.
     * <p/>
     * This method should only be called once per page request.
     */
    public void executeHttpPostedCommands() {

        final PageParameters params = getPageParameters();
        final Map<String, String> paramsMap = wicketUtil.pageParametersAsMap(params);

        this.executeCommands(paramsMap);

    }

    /**
     * Executes Http commands that are posted via http and are available from
     * this.getPageParameters() method. This method should be the first thing that
     * is executed if a page is using shopping cart.
     * <p/>
     * This method DOES NOT persist the cart to cookies.
     * <p/>
     * This method should only be called once per page request.
     */
    public void executeCommands(Map paramsMap) {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();

        try {

            getShoppingCartCommandFactory().execute(cart, (Map) paramsMap);

            if (paramsMap.containsKey(ShoppingCartCommand.CMD_RESET_PASSWORD)) {
                info(getLocalizer().getString("newPasswordEmailSent", this));
            }

        } catch (BadCredentialsException bce) {

            if (Constants.PASSWORD_RESET_AUTH_TOKEN_INVALID.equals(bce.getMessage())) {
                error(getLocalizer().getString("newPasswordInvalidToken", this));
            } else if (Constants.PASSWORD_RESET_PASSWORD_INVALID.equals(bce.getMessage())) {
                final PageParameters resetParams = new PageParameters();
                resetParams.add("token", paramsMap.get(ShoppingCartCommand.CMD_RESET_PASSWORD));
                setResponsePage(wicketPagesMounter.getPageProviderByUri("/reset").get(), resetParams);
            } else if (Constants.DELETE_ACCOUNT_PASSWORD_INVALID.equals(bce.getMessage())) {
                final PageParameters resetParams = new PageParameters();
                resetParams.add("token", paramsMap.get(ShoppingCartCommand.CMD_DELETE_ACCOUNT));
                setResponsePage(wicketPagesMounter.getPageProviderByUri("/deleteaccount").get(), resetParams);
            }

        } catch (Exception exp) {

            LOG.error("Could not execute shopping cart command", exp);

        }

    }

    /**
     * Issue a call to cart persistence engine to save all updates to the cart.
     * The persistence only occurs when cart is marked dirty.
     */
    public void persistCartIfNecessary() {

        final ShoppingCart cart = ApplicationDirector.getShoppingCart();
        if (cart.isModified()) {
            getShoppingCartPersister().persistShoppingCart(
                    getRequest().getContainerRequest(),
                    getResponse().getContainerResponse(),
                    cart
            );
        }
    }


    /**
     * Retrieve current cart.
     *
     * @return cart object
     */
    public ShoppingCart getCurrentCart() {
        return ApplicationDirector.getShoppingCart();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop object
     */
    public Shop getCurrentShop() {
        return ApplicationDirector.getCurrentShop();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop object
     */
    public Shop getCurrentCustomerShop() {
        return ApplicationDirector.getCurrentCustomerShop();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop PK
     */
    public long getCurrentShopId() {
        return getCurrentCart().getShoppingContext().getShopId();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop code
     */
    public String getCurrentShopCode() {
        return getCurrentCart().getShoppingContext().getShopCode();
    }


    /**
     * Retrieve current shop.
     *
     * @return shop PK
     */
    public long getCurrentCustomerShopId() {
        return getCurrentCart().getShoppingContext().getCustomerShopId();
    }

    /**
     * Retrieve current shop.
     *
     * @return shop code
     */
    public String getCurrentCustomerShopCode() {
        return getCurrentCart().getShoppingContext().getCustomerShopCode();
    }

    /**
     * Current shoppers IP address.
     *
     * @return IP address
     */
    public String getShopperIPAddress() {
        return ApplicationDirector.getShopperIPAddress();
    }


    /**
     * @return shopping cart command factory
     */
    public ShoppingCartCommandFactory getShoppingCartCommandFactory() {
        return shoppingCartCommandFactory;
    }

    /**
     * @return shopping cart persister
     */
    public ShoppingCartPersister getShoppingCartPersister() {
        return shoppingCartPersister;
    }

    /**
     * @return I18n support object
     */
    public I18NWebSupport getI18NSupport() {
        return i18NWebSupport;
    }

    /**
     * @return decorator facade
     */
    public DecoratorFacade getDecoratorFacade() {
        return decoratorFacade;
    }

    /**
     * @return wicket support facade
     */
    public WicketSupportFacade getWicketSupportFacade() {
        return wicketSupportFacade;
    }

    /**
     * @return wicket utils bean
     */
    public WicketUtil getWicketUtil() {
        return wicketUtil;
    }

    /**
     * Get page title.
     *
     * @return page title
     */
    public IModel<String> getPageTitle() {
        final Shop shop = ApplicationDirector.getCurrentShop();
        if (shop.getSeo() != null) {
            final String lang = getLocale().getLanguage();
            final String title = getPageTitle(shop.getSeo(), lang);
            if (title != null) {
                return new Model<>(title);
            }
        }
        return new Model<>(shop.getName());
    }

    protected String getPageTitle(final Seo seo, final String language) {
        if (seo != null) {
            final String title = getI18NSupport().getFailoverModel(seo.getDisplayTitle(), seo.getTitle()).getValue(language);
            if (StringUtils.isNotBlank(title)) {
                return title;
            }
        }
        return null;
    }


    /**
     * Get page description
     *
     * @return description
     */
    public IModel<String> getDescription() {
        final Shop shop = ApplicationDirector.getCurrentShop();
        if (shop.getSeo() != null) {
            final String lang = getLocale().getLanguage();
            final String title = getDescription(shop.getSeo(), lang);
            if (title != null) {
                return new Model<>(title);
            }
        }
        return null;
    }

    protected String getDescription(final Seo seo, final String language) {
        if (seo != null) {
            final String desc = getI18NSupport().getFailoverModel(seo.getDisplayMetadescription(), seo.getMetadescription()).getValue(language);
            if (StringUtils.isNotBlank(desc)) {
                return desc;
            }
        }
        return null;
    }

    /**
     * Get keywords.
     *
     * @return keywords
     */
    public IModel<String> getKeywords() {
        final Shop shop = ApplicationDirector.getCurrentShop();
        if (shop.getSeo() != null) {
            final String lang = getLocale().getLanguage();
            final String title = getKeywords(shop.getSeo(), lang);
            if (title != null) {
                return new Model<>(title);
            }
        }
        return null;
    }

    /**
     * Get created date time.
     *
     * @return page created
     */
    public IModel<String> getCreated() {
        return new Model<>(DateUtils.formatSDT());
    }

    protected String getKeywords(final Seo seo, final String language) {
        if (seo != null) {
            final String desc = getI18NSupport().getFailoverModel(seo.getDisplayMetakeywords(), seo.getMetakeywords()).getValue(language);
            if (StringUtils.isNotBlank(desc)) {
                return desc;
            }
        }
        return null;
    }

    boolean isAuthenticated() {
        final ShoppingCart cart = getCurrentCart();
        return ((AuthenticatedWebSession) getSession()).isSignedIn()
                && cart.getLogonState() == ShoppingCart.LOGGED_IN;
    }

    void forceLogoutRedirect() {
        final PageParameters params = new PageParameters();
        params.set(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
        setResponsePage(Application.get().getHomePage(), params);
    }

    /**
     * Get rel-canonical link.
     *
     * @return link
     */
    public IModel<String> getRelCanonical() {

        return null;

    }

}
