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

package org.yes.cart.service.order.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDet;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.service.order.OrderAssembler;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class OrderAssemblerImplTest extends BaseCoreDBTestCase {

    private OrderAssembler orderAssembler;
    private CustomerOrderService customerOrderService;

    @Override
    @Before
    public void setUp()  {
        orderAssembler = (OrderAssembler) ctx().getBean(ServiceSpringKeys.ORDER_ASSEMBLER);
        customerOrderService =  ctx().getBean("customerOrderService", CustomerOrderService.class);
        super.setUp();
    }

    @Test
    public void testAssembleCustomerOrder() throws Exception {
        Customer customer = createCustomer();

        ShoppingCart shoppingCart = getShoppingCart2(customer.getEmail(), false);
        setIPAddress(shoppingCart, "127.0.0.1");
        setCustomOrderDetail(shoppingCart, "someDetail", "order detail");
        setCustomItemDetail(shoppingCart, "WAREHOUSE_1", "CC_TEST1", "someDetail", "item detail");

        CustomerOrder customerOrder = orderAssembler.assembleCustomerOrder(shoppingCart, RandomStringUtils.random(10));
        assertNotNull(customerOrder);
        customerOrder =  customerOrderService.create(customerOrder);
        assertNotNull(customerOrder);

        assertNotNull(customerOrder.getBillingAddress());
        assertEquals("By default billing and shipping addresses the same",
                customerOrder.getBillingAddress(),
                customerOrder.getShippingAddress());
        assertTrue("By Default billing address is shipping address ",
                customerOrder.getBillingAddress().contains("shipping addr"));
        assertEquals("Order must be in ORDER_STATUS_NONE state",
                CustomerOrder.ORDER_STATUS_NONE,
                customerOrder.getOrderStatus());
        assertNotNull("Order num must be set", customerOrder.getOrdernum());
        assertTrue("Order in pending state must not have delivery", customerOrder.getDelivery().isEmpty());
        assertEquals("Shopping cart guid and order guid are equals",
                shoppingCart.getGuid(),
                customerOrder.getGuid());
        assertEquals("127.0.0.1", customerOrder.getOrderIp());
        assertEquals(8, customerOrder.getOrderDetail().size());
        assertFalse(customerOrder.isMultipleShipmentOption());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getListPrice());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getPrice());
        assertEquals(new BigDecimal("4551.88"), customerOrder.getNetPrice());
        assertEquals(new BigDecimal("5463.91"), customerOrder.getGrossPrice());

        final Pair<String, I18NModel> orderDetail = customerOrder.getValue(AttributeNamesKeys.Cart.ORDER_INFO_ORDER_ATTRIBUTE_ID + ":someDetail");
        assertNotNull(orderDetail);
        assertEquals("order detail", orderDetail.getFirst());
        assertEquals("someDetail: order detail", orderDetail.getSecond().getValue(I18NModel.DEFAULT));

        CustomerOrderDet detCC_TEST1 = null;
        for (final CustomerOrderDet det : customerOrder.getOrderDetail()) {
            if (det.getProductSkuCode().equals("CC_TEST1")) {
                detCC_TEST1 = det;
                break;
            }
        }

        final Pair<String, I18NModel> orderDetCC_TEST1 = detCC_TEST1.getValue(AttributeNamesKeys.Cart.ORDER_INFO_ORDER_LINE_ATTRIBUTE_ID + ":someDetail");
        assertNotNull(orderDetCC_TEST1);
        assertEquals("item detail", orderDetCC_TEST1.getFirst());
        assertEquals("someDetail: item detail", orderDetCC_TEST1.getSecond().getValue(I18NModel.DEFAULT));

    }

}
