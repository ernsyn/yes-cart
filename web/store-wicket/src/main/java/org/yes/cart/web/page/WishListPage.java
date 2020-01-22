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
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.https.RequireHttps;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.component.customer.wishlist.WishListView;
import org.yes.cart.web.page.component.footer.StandardFooter;
import org.yes.cart.web.page.component.header.HeaderMetaInclude;
import org.yes.cart.web.page.component.header.StandardHeader;
import org.yes.cart.web.page.component.js.ServerSideJs;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;
import org.yes.cart.web.support.utils.HttpUtil;

/**
 *
 * Customer self care page to view orders, wish list, etc.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/11/11
 * Time: 9:51 PM
 */
@RequireHttps
public class WishListPage extends AbstractWebPage {

    private static final Logger LOG = LoggerFactory.getLogger(WishListPage.class);

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String WISHLIST_PANEL = "wishlistView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerServiceFacade;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;


    /**
     * Construct page.
     *
     * @param params page parameters
     */
    public WishListPage(final PageParameters params) {
        super(params);

        final String email;
        final Customer customer;
        final String publicKey;
        final String key = params.get("token").toString();
        final String tag = params.get("tag").toString();

        if (StringUtils.isBlank(key)) {
            // Trying to view own wish list
            final ShoppingCart cart = getCurrentCart();

            if (cart.getLogonState() == ShoppingCart.LOGGED_IN && ((AuthenticatedWebSession) getSession()).isSignedIn()) {
                email = cart.getCustomerEmail();
                customer = customerServiceFacade.getCustomerByEmail(getCurrentShop(), email);
                publicKey = customer != null ? customerServiceFacade.getCustomerPublicKey(customer) : null;
            } else {
                email = "";
                customer = null;
                publicKey = null;
                // Redirect away from profile!
                final PageParameters logOutParams = new PageParameters();
                logOutParams.set(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
                setResponsePage(Application.get().getHomePage(), logOutParams);
            }
        } else {
            publicKey = null;
            customer = customerServiceFacade.getCustomerByPublicKey(key);
            if (customer == null) {
                info(getLocalizer().getString("wishListNotFound", this));
                email = "";
            } else {
                email = customer.getEmail();
            }

        }


        String safePublicKey = publicKey;
        if (StringUtils.isNotBlank(publicKey)) {
            try {
                safePublicKey = HttpUtil.encodeUtf8UriParam(publicKey);
            } catch (Exception exp) {
                LOG.error(exp.getMessage(), exp);
            }
        }

        add(new FeedbackPanel(FEEDBACK));
        add(
                new WishListView(WISHLIST_PANEL, new Model<>(email), new Model<>(CustomerWishList.SIMPLE_WISH_ITEM), new Model<>(tag))
                    .setVisible(customer != null)
                    .add(new AttributeModifier("data-publickey", safePublicKey))
        );
        add(new StandardFooter(FOOTER));
        add(new StandardHeader(HEADER));
        add(new ServerSideJs("serverSideJs"));
        add(new HeaderMetaInclude("headerInclude"));

        final long contentShopId = getCurrentShopId();
        if (StringUtils.isNotBlank(publicKey)) {

            String content = contentServiceFacade.getContentBody(
                    "profile_wishlist_owner_include", contentShopId, getLocale().getLanguage());

            add(new Label("wishListOwnerInfo", content).setEscapeModelStrings(false));
            add(new Label("wishListViewerInfo", ""));

        } else {

            String content = contentServiceFacade.getContentBody(
                    "profile_wishlist_viewer_include", contentShopId, getLocale().getLanguage());

            add(new Label("wishListOwnerInfo", ""));
            add(new Label("wishListViewerInfo", content).setEscapeModelStrings(false));

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

    /**
     * Get page title.
     *
     * @return page title
     */
    @Override
    public IModel<String> getPageTitle() {
        return new StringResourceModel("wishlist",this);
    }


}
