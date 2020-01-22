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
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 16/01/2014
 * Time: 18:33
 */
public class ViewProductSkuInternalCommandImplTest extends BaseCoreDBTestCase {


    @Test
    public void testExecute() {

        final Customer customer = createCustomer();

        MutableShoppingCart shoppingCart = new ShoppingCartImpl();
        shoppingCart.initialise(ctx().getBean("amountCalculationStrategy", AmountCalculationStrategy.class));
        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        commands.execute(shoppingCart, new HashMap<String, Object>() {{
            put(ShoppingCartCommand.CMD_CHANGECURRENCY, "EUR");
            put(ShoppingCartCommand.CMD_CHANGELOCALE, "en");
            put(ShoppingCartCommand.CMD_SETSHOP, "10");
        }});

        assertNull(shoppingCart.getShoppingContext().getLatestViewedSkus());

        // Test adding single sku

        Map<String, Object> params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, "1");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "s01");
        commands.execute(shoppingCart, (Map) params);

        List<String> skus = shoppingCart.getShoppingContext().getLatestViewedSkus();
        assertNotNull(skus);
        assertEquals(1, skus.size());
        assertEquals("1|s01", skus.get(0));

        // Test adding duplicate sku 1 and 9 others to have full 10 items

        for (int i = 1; i <= 10; i++) {

            params.put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, String.valueOf(i));
            params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "s01");
            commands.execute(shoppingCart, (Map) params);

        }

        skus = shoppingCart.getShoppingContext().getLatestViewedSkus();
        assertNotNull(skus);
        assertEquals(10, skus.size());
        for (int i = 1; i <= 10; i++) {

            assertEquals(String.valueOf(i) + "|s01", skus.get(i - 1));

        }

        // Test adding 11th item
        params.put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, "11");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "s01");
        commands.execute(shoppingCart, (Map) params);

        skus = shoppingCart.getShoppingContext().getLatestViewedSkus();
        assertNotNull(skus);
        assertEquals(10, skus.size());
        for (int i = 1; i <= 10; i++) {

            assertEquals(String.valueOf(i + 1) + "|s01", skus.get(i - 1));

        }

        // Test adding multiple items

        params.put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, Arrays.asList("12", "13"));
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "s01");
        commands.execute(shoppingCart, (Map) params);

        skus = shoppingCart.getShoppingContext().getLatestViewedSkus();
        assertNotNull(skus);
        assertEquals(10, skus.size());
        for (int i = 1; i <= 10; i++) {

            assertEquals(String.valueOf(i + 3) + "|s01", skus.get(i - 1));

        }


    }


}
