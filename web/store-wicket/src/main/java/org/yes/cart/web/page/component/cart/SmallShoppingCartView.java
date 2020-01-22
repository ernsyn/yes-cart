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

package org.yes.cart.web.page.component.cart;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.PriceModel;
import org.yes.cart.service.misc.PluralFormService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.web.page.ShoppingCartPage;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ProductServiceFacade;

/**
 * Igor Azarny iazarny@yahoo.com
 * Date: 18-Sep-2011
 * Time: 09:56:58
 */
public class SmallShoppingCartView extends BaseComponent {


    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private static final String QTY_LABEL = "qtyLabel";
    private static final String EMPTY_LABEL = "emptyCart";
    private static final String SUB_TOTAL_VIEW = "subTotal";
    private static final String CART_LINK = "cartLink";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    private static final String [] pluralForms = new String [] {
            "itemForm0",
            "itemForm1",
            "itemForm2"
    };

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    private ProductServiceFacade productServiceFacade;

    @SpringBean(name = ServiceSpringKeys.PLURAL_FORM_SERVICE)
    private PluralFormService pluralFormService;

    /**
     * Construct small cart view.
     *
     * @param id component id.
     */
    public SmallShoppingCartView(final String id) {
        super(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        final ShoppingCart cart = getCurrentCart();

        final PriceModel model = productServiceFacade.getCartItemsTotal(cart);

        final int itemsInCart = cart.getCartItemsCount();
        final String linkTarget = getLinkTarget(itemsInCart > 0 && MoneyUtils.isPositive(cart.getTotal().getTotalAmount()));
        final boolean showPrices = !cart.getShoppingContext().isHidePrices();

        add(
                new ExternalLink(
                        CART_LINK,
                        linkTarget
                )
                        .add(
                                new PriceView(
                                        SUB_TOTAL_VIEW,
                                        model,
                                        null,
                                        true, false,
                                        model.isTaxInfoEnabled(), model.isTaxInfoShowAmount()
                                ) {
                                    @Override
                                    public boolean isVisible() {
                                        return showPrices;
                                    }
                                }
                        ).
                        add(
                                new Label(
                                        QTY_LABEL, "(" + itemsInCart + ")"
                                )
                        ).
                        add(
                                new Label(
                                        "cartIcon0", ""
                                ).setVisible(isCartEmpty())
                        ).
                        add(
                                new Label(
                                        "cartIcon1", ""
                                ).setVisible(!isCartEmpty())
                        )
        );

        super.onBeforeRender();
    }

    private String getLinkTarget(final boolean allowCheckout) {

        if (allowCheckout && (getPage() instanceof ShoppingCartPage)) {
            return getWicketUtil().getHttpServletRequest().getContextPath() + "/checkout";
        }
        return getWicketUtil().getHttpServletRequest().getContextPath() + "/cart";
    }

    /**
     *
     * @return true in case of empty cart
     */
    public boolean isCartEmpty() {
        return getCurrentCart().getCartItemsCount() == 0;
    }

}
