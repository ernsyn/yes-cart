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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.ResultsIterator;
import org.yes.cart.domain.entity.Address;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.domain.ProductSkuService;
import org.yes.cart.service.order.OrderEventHandler;
import org.yes.cart.service.order.impl.OrderEventImpl;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class TestCustomerOrderServiceImpl extends BaseCoreDBTestCase {

    private CustomerOrderService customerOrderService;
    private ProductSkuService productSkuService;

    private OrderEventHandler handler;


    @Before
    public void setUp() {
        customerOrderService = (CustomerOrderService) ctx().getBean(ServiceSpringKeys.CUSTOMER_ORDER_SERVICE);
        productSkuService = (ProductSkuService) ctx().getBean(ServiceSpringKeys.PRODUCT_SKU_SERVICE);
        handler = (OrderEventHandler) ctx().getBean("pendingOrderEventHandler");
        super.setUp();
    }


    @Test
    public void testOrderAmountCalculation() throws Exception {
        String prefix = UUID.randomUUID().toString();
        Customer customer = createCustomer(prefix);
        assertFalse(customer.getAddress().isEmpty());
        assertNotNull(customer.getDefaultAddress(Address.ADDR_TYPE_BILLING));
        assertNotNull(customer.getDefaultAddress(Address.ADDR_TYPE_SHIPPING));

        ShoppingCart shoppingCart = getEmptyCartByPrefix(getTestName() + prefix);

        assertEquals(getTestName() + prefix + "jd@domain.com", shoppingCart.getCustomerEmail());
        assertEquals(customer.getEmail(), shoppingCart.getCustomerEmail());

        final ShoppingCartCommandFactory commands = ctx().getBean("shoppingCartCommandFactory", ShoppingCartCommandFactory.class);

        //one delivery 16.77 usd
        commands.execute(shoppingCart,
                (Map) singletonMap(ShoppingCartCommand.CMD_SETCARRIERSLA, "3-WAREHOUSE_1|3-WAREHOUSE_2"));


        Map<String, String> params;

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST1");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);
        // 3 x 180  usd

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);

        params = new HashMap<>();
        params.put(ShoppingCartCommand.CMD_ADDTOCART, "CC_TEST3");
        params.put(ShoppingCartCommand.CMD_P_SUPPLIER, "WAREHOUSE_1");
        commands.execute(shoppingCart, (Map) params);
        //2 x 7.99  usd

        prepareMultiDeliveriesAndRecalculate(shoppingCart, false);

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);

        assertEquals(CustomerOrder.ORDER_STATUS_NONE, order.getOrderStatus());
        order.setPgLabel("testPaymentGatewayLabel");
        order = customerOrderService.update(order);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        order,
                        null,
                        Collections.EMPTY_MAP)));

        BigDecimal amount = order.getOrderTotal();

        assertTrue("payment must be 16.77 + 3 * 190.01 + 2 * 70.99 = 728.78, but was  " + amount
                , new BigDecimal("728.78").compareTo(amount) == 0);


        assertEquals(1, order.getDelivery().size());
    }


    @Test
    public void testFindDeliveryAwaitingForInventory() throws Exception {
        final Customer customer = createCustomer();
        final ShoppingCart shoppingCart = getShoppingCartWithPreorderItems(getTestName(), 1, true);

        CustomerOrder order = customerOrderService.createFromCart(shoppingCart);
        assertEquals(CustomerOrder.ORDER_STATUS_NONE, order.getOrderStatus());
        order.setPgLabel("testPaymentGatewayLabel");
        customerOrderService.update(order);

        assertTrue(handler.handle(
                new OrderEventImpl("", //evt.pending
                        order,
                        null,
                        Collections.EMPTY_MAP)));
        customerOrderService.update(order);
        order = customerOrderService.findByReference(shoppingCart.getGuid());
        assertEquals(CustomerOrder.ORDER_STATUS_IN_PROGRESS, order.getOrderStatus());

        final List<Long> expected = new ArrayList<Long>();
        for (CustomerOrderDelivery delivery : order.getDelivery()) {
            assertEquals(CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT, delivery.getDeliveryStatus());
            expected.add(delivery.getCustomerOrderDeliveryId());
        }
        assertEquals(1, expected.size());

        final int[] count = new int[1];

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {

                List<Long> rezIds = customerOrderService.findAwaitingDeliveriesIds(
                        Arrays.asList(productSkuService.findById(15330L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                        Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));

                ResultsIterator<CustomerOrderDelivery> rez = customerOrderService.findAwaitingDeliveries(
                        Arrays.asList(productSkuService.findById(15330L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                        Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));

                for (count[0] = 0; rez.hasNext(); rez.next()) {
                    count[0]++;
                    assertTrue(rezIds.contains(rez.next().getCustomerOrderDeliveryId()));
                }
                assertEquals(count[0], rezIds.size());
                assertTrue(rezIds.contains(expected.get(0)));

                transactionStatus.setRollbackOnly();
            }
        });
        assertTrue("Expect one order with preorder sku id = 15330", count[0] >= 1);

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {

                List<Long> rezIds  = customerOrderService.findAwaitingDeliveriesIds(
                        Arrays.asList(productSkuService.findById(15340L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                        Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));

                ResultsIterator<CustomerOrderDelivery> rez  = customerOrderService.findAwaitingDeliveries(
                        Arrays.asList(productSkuService.findById(15340L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                        Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));

                for (count[0] = 0; rez.hasNext(); rez.next()) {
                    count[0]++;
                    assertTrue(rezIds.contains(rez.next().getCustomerOrderDeliveryId()));
                }
                assertEquals(count[0], rezIds.size());
                assertTrue(rezIds.contains(expected.get(0)));

                transactionStatus.setRollbackOnly();
            }
        });
        assertTrue("Expect one order with preorder sku id = 15340", count[0] >= 1);

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {

                List<Long> rezIds = customerOrderService.findAwaitingDeliveriesIds(
                        Arrays.asList(productSkuService.findById(15129L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                        Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));

                ResultsIterator<CustomerOrderDelivery> rez = customerOrderService.findAwaitingDeliveries(
                        Arrays.asList(productSkuService.findById(15129L).getCode()), CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                        Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));

                for (count[0] = 0; rez.hasNext(); rez.next()) {
                    count[0]++;
                    assertTrue(rezIds.contains(rez.next().getCustomerOrderDeliveryId()));
                }
                assertEquals(count[0], rezIds.size());
                assertFalse(rezIds.contains(expected.get(0)));

                transactionStatus.setRollbackOnly();
            }
        });
        assertEquals("Not expected orders waiting for inventory sku id = 15129" ,0, count[0]);

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus transactionStatus) {

                List<Long> rezIds = customerOrderService.findAwaitingDeliveriesIds(null, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                        Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));

                ResultsIterator<CustomerOrderDelivery> rez = customerOrderService.findAwaitingDeliveries(null, CustomerOrderDelivery.DELIVERY_STATUS_DATE_WAIT,
                        Arrays.asList(CustomerOrder.ORDER_STATUS_IN_PROGRESS));

                for (count[0] = 0; rez.hasNext(); rez.next()) {
                    count[0]++;
                    assertTrue(rezIds.contains(rez.next().getCustomerOrderDeliveryId()));
                }
                assertEquals(count[0], rezIds.size());
                assertTrue(rezIds.contains(expected.get(0)));

                transactionStatus.setRollbackOnly();
            }
        });
        assertTrue("Expect one order to wait for inventory", count[0] >= 1);



    }

}
