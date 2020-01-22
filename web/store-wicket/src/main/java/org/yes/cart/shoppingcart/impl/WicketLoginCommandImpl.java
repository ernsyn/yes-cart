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

package org.yes.cart.shoppingcart.impl;

import org.apache.wicket.Application;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

import java.util.Map;

/**
 * Extends default login command to include wicket specific authentication flow.
 *
 * Wicket version of login command invokes Wicket session authentication
 * and also hooks into cart repository to force shopping cart persistence,
 * which also triggers cart merging.

 *
 * User: denispavlov
 * Date: 26/08/2014
 * Time: 21:07
 */
public class WicketLoginCommandImpl extends LoginCommandImpl {

    private static final long serialVersionUID = 20101026L;

    private final CartRepository cartRepository;

    /**
     * Construct command.
     *
     * @param registry shopping cart command registry
     * @param customerResolver customer service
     * @param shopService shop service
     * @param priceResolver price service
     * @param pricingPolicyProvider pricing policy provider
     * @param productService product service
     * @param cartRepository cart repository
     */
    public WicketLoginCommandImpl(final ShoppingCartCommandRegistry registry,
                                  final CustomerResolver customerResolver,
                                  final ShopService shopService,
                                  final PriceResolver priceResolver,
                                  final PricingPolicyProvider pricingPolicyProvider,
                                  final ProductService productService,
                                  final CartRepository cartRepository) {
        super(registry, customerResolver, shopService, priceResolver, pricingPolicyProvider, productService);
        this.cartRepository = cartRepository;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(final MutableShoppingCart shoppingCart, final Map<String, Object> parameters) {
        super.execute(shoppingCart, parameters);
        if (parameters.containsKey(getCmdKey()) && shoppingCart.getLogonState() == ShoppingCart.LOGGED_IN) {

            final String email = (String) parameters.get(CMD_LOGIN_P_EMAIL);
            final String passw = (String) parameters.get(CMD_LOGIN_P_PASS);

            final IAuthenticationStrategy strategy = Application.get().getSecuritySettings()
                    .getAuthenticationStrategy();
            strategy.save(email, passw);
        }
    }

    /**
     * Merges and recalculates the cart.
     */
    @Override
    protected void recalculate(final MutableShoppingCart shoppingCart) {

        // This call will merge the cart
        cartRepository.storeShoppingCart(shoppingCart);

        super.recalculate(shoppingCart);

    }
}
