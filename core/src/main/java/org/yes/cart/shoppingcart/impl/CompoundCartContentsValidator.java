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

import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.shoppingcart.CartContentsValidator;
import org.yes.cart.shoppingcart.CartValidityModel;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.util.List;

/**
 * User: denispavlov
 * Date: 15/11/2017
 * Time: 13:59
 */
public class CompoundCartContentsValidator extends AbstractCartContentsValidatorImpl implements CartContentsValidator, Configuration {

    private final CartContentsValidator[] validators;

    private ConfigurationContext cfgContext;

    public CompoundCartContentsValidator(final List<CartContentsValidator> validators) {
        this.validators = validators.toArray(new CartContentsValidator[validators.size()]);
    }

    /** {@inheritDoc} */
    @Override
    public CartValidityModel validate(final ShoppingCart cart) {

        final CartValidityModel rez = new CartValidityModelImpl();

        for (final CartContentsValidator validator : this.validators) {

            final CartValidityModel subRez = validator.validate(cart);
            if (subRez != null) {
                rez.append(subRez);
            }

            if (rez.isCheckoutBlocked()) {
                return rez; // Stop at first checkout blocking result
            }

        }

        return rez;
    }

    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }

}
