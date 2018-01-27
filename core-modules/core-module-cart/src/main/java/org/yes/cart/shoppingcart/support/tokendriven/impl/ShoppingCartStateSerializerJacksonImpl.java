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

package org.yes.cart.shoppingcart.support.tokendriven.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.shoppingcart.impl.*;
import org.yes.cart.shoppingcart.support.tokendriven.ShoppingCartStateSerializer;

import java.io.IOException;

/**
 * User: denispavlov
 * Date: 21/04/2015
 * Time: 10:10
 */
public class ShoppingCartStateSerializerJacksonImpl implements ShoppingCartStateSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartStateSerializerJacksonImpl.class);

    private final ObjectMapper mapper;

    public ShoppingCartStateSerializerJacksonImpl() {
        mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule module = new SimpleModule("cart", new Version(2, 3, 0, null, "org.yes", "core-module-cart"));
        module.addAbstractTypeMapping(Total.class, TotalImpl.class);
        module.addAbstractTypeMapping(MutableShoppingContext.class, ShoppingContextImpl.class);
        module.addAbstractTypeMapping(MutableOrderInfo.class, OrderInfoImpl.class);
        module.addAbstractTypeMapping(CartItem.class, CartItemImpl.class);

        mapper.registerModule(module);
    }

    /** {@inheritDoc} */
    @Override
    public ShoppingCart restoreState(final byte[] bytes) {

        try {
            return mapper.readValue(bytes, ShoppingCartImpl.class);
        } catch (IOException exception) {
            LOG.error("Unable to convert bytes assembled from tuple into object: " + exception.getMessage(), exception);
            return null;
        }

    }

    /** {@inheritDoc} */
    @Override
    public byte[] saveState(final ShoppingCart shoppingCart) {

        try {
            return mapper.writeValueAsBytes(shoppingCart);
        } catch (IOException ioe) {
            LOG.error(
                    "Unable to serialize object: " + shoppingCart,
                    ioe
            );
        }

        return null;
    }
}
