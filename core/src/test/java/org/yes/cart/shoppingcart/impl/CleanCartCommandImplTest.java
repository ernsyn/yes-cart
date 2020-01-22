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

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CleanCartCommandImplTest extends BaseCoreDBTestCase {

    @Test
    public void testExecute() {

        ShopService shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);
        Shop shop10 = shopService.getById(10L);

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.getShoppingContext().setShopCode(shop10.getCode());
        shoppingCart.getShoppingContext().setShopId(shop10.getShopId());

        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        shoppingCart.addProductSkuToCart("Main", "ABC", "ABC", BigDecimal.ONE);
        shoppingCart.getOrderInfo().setOrderMessage("hi, I'm a cart");
        String oldGuid = shoppingCart.getGuid();

        commands.execute(shoppingCart,
                (Map) Collections.singletonMap(ShoppingCartCommand.CMD_CLEAN, ShoppingCartCommand.CMD_CLEAN));

        assertNull(shoppingCart.getOrderMessage());
        assertNotNull(shoppingCart.getModifiedTimestamp());
        assertTrue(shoppingCart.getCartItemList().isEmpty());
        assertNotSame(oldGuid, shoppingCart.getGuid());
    }
}

