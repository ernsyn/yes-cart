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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/06/2016
 * Time: 18:21
 */
public class FreeDeliveryCostCalculationStrategyTest {

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCalculateNoneOf() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.emptyMap()));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, null, null).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleCarrierNotFound() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.emptyList())));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(null));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, null, null).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingle() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.FREE));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.emptyList())));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, "Main", new BigDecimal("1.00")); will(returnValue(cost));
            oneOf(cost).getSkuPriceId(); will(returnValue(345L));
            oneOf(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("0.00"), new BigDecimal("0.00"));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingleNoBucket() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Collections.emptyMap())));
            oneOf(bucket1).getSupplier(); will(returnValue(""));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateMulti() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<>();
        buckets.put(bucket1, Collections.EMPTY_LIST);
        buckets.put(bucket2, Collections.EMPTY_LIST);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.FREE));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(buckets));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(bucket2).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, "Main", new BigDecimal("1.00")); will(returnValue(cost));
            oneOf(cost).getSkuPriceId(); will(returnValue(345L));
            oneOf(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).addShippingToCart(bucket2, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("0.00"), new BigDecimal("0.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket2, new BigDecimal("0.00"), new BigDecimal("0.00"));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("0.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("0.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateMultiNoPrice() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice cost = context.mock(SkuPrice.class, "cost");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<>();
        buckets.put(bucket1, Collections.EMPTY_LIST);
        buckets.put(bucket2, Collections.EMPTY_LIST);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.FREE));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(buckets));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(bucket2).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001", pricingPolicy, "Main", new BigDecimal("1.00")); will(returnValue(cost));
            oneOf(cost).getSkuPriceId(); will(returnValue(0L));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateMultiNoBucket() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<>();
        buckets.put(bucket1, Collections.EMPTY_LIST);
        buckets.put(bucket2, Collections.EMPTY_LIST);

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            oneOf(cart).getCartItemMap(); will(returnValue(buckets));
            oneOf(bucket1).getSupplier(); will(returnValue(""));
            oneOf(bucket2).getSupplier(); will(returnValue(""));
        }});

        final Total delTotal = new FreeDeliveryCostCalculationStrategy(carrierSlaService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


}