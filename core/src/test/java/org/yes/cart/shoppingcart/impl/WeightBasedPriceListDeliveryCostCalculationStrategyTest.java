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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 12/07/2016
 * Time: 09:37
 */
public class WeightBasedPriceListDeliveryCostCalculationStrategyTest {


    private static final BigDecimal MAX = new BigDecimal(Integer.MAX_VALUE).setScale(2, RoundingMode.HALF_UP);

    private final Mockery context = new JUnit4Mockery();


    @Test
    public void testCalculateNoneOf() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.emptyMap()));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, null, null, null).calculate(cart);

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

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, null, null, null).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingleRegularWeightAndVolume() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.750")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.80")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(345L));
            allowing(costKg).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("10.00"), null)));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3).getSkuPriceId(); will(returnValue(445L));
            allowing(costM3).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("9.00"), null))); // Note we choose Kg because it is more expensive
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            oneOf(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("10.00"), new BigDecimal("10.00"));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("10.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("10.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("10.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingleRegularWeightAndVolumeWeightExcess() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.80")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("0.50")));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }



    @Test
    public void testCalculateSingleRegularWeightAndVolumeVolumeExcess() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.750")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(345L));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("1.00")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }




    @Test
    public void testCalculateSingleRegularWeightAndVolumeNoMaxWeight() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.80")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(0L));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }



    @Test
    public void testCalculateSingleRegularWeightAndVolumeNoMaxVolume() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.750")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(345L));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(0L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleRegularWeigthOnly() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableOrderInfo orderInfo = context.mock(MutableOrderInfo.class, "orderInfo");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue(null));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue(null));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.750")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            allowing(costKg).getSkuPriceId(); will(returnValue(345L));
            allowing(costKg).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("10.00"), null)));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            oneOf(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("10.00"), new BigDecimal("10.00"));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("10.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("10.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("10.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleRegularWeigthOnlyWeightExcess() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue(null));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue(null));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("0.50")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleRegularWeigthOnlyNoMaxWeight() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue(null));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue(null));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(0L));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleRegularVolumeOnly() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue(null));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue(null));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.80")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costM3).getSkuPriceId(); will(returnValue(445L));
            allowing(costM3).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("10.00"), null)));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            oneOf(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("10.00"), new BigDecimal("10.00"));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("10.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("10.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("10.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("10.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleRegularVolumeOnlyVolumeExcess() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue(null));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue(null));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("1.00")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingleRegularVolumeOnlyNoMaxVolume() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue(null));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue(null));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(0L));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }




    @Test
    public void testCalculateSingleSaleWeightAndVolume() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.750")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.80")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(345L));
            allowing(costKg).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("10.00"), new BigDecimal("7.00"))));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3).getSkuPriceId(); will(returnValue(445L));
            allowing(costM3).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("8.00"), null))); // Note we choose M3 because it is more expensive
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            oneOf(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("8.00"), new BigDecimal("8.00"));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("8.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("8.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("8.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("8.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("8.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("8.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateSingleNoPriceWeight() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.750")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.80")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(0L));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3).getSkuPriceId(); will(returnValue(445L));
            allowing(costM3).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("9.00"), null)));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }

    @Test
    public void testCalculateSingleNoPriceVolume() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(Collections.singletonMap(bucket1, Arrays.asList(item1, item2))));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.750")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.80")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(345L));
            allowing(costKg).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("10.00"), null)));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3).getSkuPriceId(); will(returnValue(0L));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateMultiRegular() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<>();
        buckets.put(bucket1, Collections.singletonList(item1));
        buckets.put(bucket2, Collections.singletonList(item2));

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(buckets));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(bucket2).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.250")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("0.3")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.500")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.50")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(345L));
            allowing(costKg).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("10.00"), null)));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3).getSkuPriceId(); will(returnValue(445L));
            allowing(costM3).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("9.00"), null))); // Note we choose Kg because it is more expensive
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            oneOf(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("10.00"), new BigDecimal("10.00"));
            oneOf(cart).addShippingToCart(bucket2, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket2, new BigDecimal("10.00"), new BigDecimal("10.00"));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("20.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("20.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("20.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("20.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("20.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("20.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }


    @Test
    public void testCalculateMultiSale() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<>();
        buckets.put(bucket1, Collections.singletonList(item1));
        buckets.put(bucket2, Collections.singletonList(item2));

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(buckets));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(bucket2).getSupplier(); will(returnValue("Main"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.250")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("0.3")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.500")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.50")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(345L));
            allowing(costKg).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("10.00"), new BigDecimal("7.00"))));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3).getSkuPriceId(); will(returnValue(445L));
            allowing(costM3).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("8.00"), null))); // Note we choose M3 because it is more expensive
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            oneOf(cart).addShippingToCart(bucket1, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket1, new BigDecimal("8.00"), new BigDecimal("8.00"));
            oneOf(cart).addShippingToCart(bucket2, "CSL001", "CSL001", new BigDecimal("1.00"));
            oneOf(cart).setShippingPrice("CSL001", bucket2, new BigDecimal("8.00"), new BigDecimal("8.00"));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertEquals("0.00", delTotal.getListSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSaleSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getNonSaleSubTotal().toPlainString()); // only A-001 was not on sale
        assertEquals("0.00", delTotal.getPriceSubTotal().toPlainString());
        assertFalse(delTotal.isOrderPromoApplied());
        assertNull(delTotal.getAppliedOrderPromo());
        assertEquals("0.00", delTotal.getSubTotal().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalTax().toPlainString());
        assertEquals("0.00", delTotal.getSubTotalAmount().toPlainString());
        assertEquals("16.00", delTotal.getDeliveryListCost().toPlainString());
        assertEquals("16.00", delTotal.getDeliveryCost().toPlainString());
        assertFalse(delTotal.isDeliveryPromoApplied());
        assertNull(delTotal.getAppliedDeliveryPromo());
        assertEquals("0.00", delTotal.getDeliveryTax().toPlainString());
        assertEquals("16.00", delTotal.getDeliveryCostAmount().toPlainString());
        assertEquals("16.00", delTotal.getTotal().toPlainString());
        assertEquals("0.00", delTotal.getTotalTax().toPlainString());
        assertEquals("16.00", delTotal.getListTotalAmount().toPlainString());
        assertEquals("16.00", delTotal.getTotalAmount().toPlainString());

        context.assertIsSatisfied();
    }



    @Test
    public void testCalculateMultiNoPrice() throws Exception {

        final CarrierSlaService carrierSlaService = context.mock(CarrierSlaService.class, "carrierSlaService");
        final ProductService productService = context.mock(ProductService.class, "productService");
        final MutableShoppingCart cart = context.mock(MutableShoppingCart.class, "cart");
        final MutableShoppingContext shoppingContext = context.mock(MutableShoppingContext.class, "shoppingContext");
        final CarrierSla carrierSla = context.mock(CarrierSla.class, "carrierSla");
        final CartItem item1 = context.mock(CartItem.class, "item1");
        final Product product1 = context.mock(Product.class, "product1");
        final CartItem item2 = context.mock(CartItem.class, "item2");
        final Product product2 = context.mock(Product.class, "product2");
        final CartItem item3 = context.mock(CartItem.class, "item3");
        final PricingPolicyProvider pricingPolicyProvider = context.mock(PricingPolicyProvider.class, "pricingPolicyProvider");
        final PricingPolicyProvider.PricingPolicy pricingPolicy = context.mock(PricingPolicyProvider.PricingPolicy.class, "pricingPolicy");
        final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver = context.mock(DeliveryCostRegionalPriceResolver.class, "deliveryCostRegionalPriceResolver");
        final SkuPrice costKg = context.mock(SkuPrice.class, "costKg");
        final SkuPrice costKgMax = context.mock(SkuPrice.class, "costKgMax");
        final SkuPrice costM3 = context.mock(SkuPrice.class, "costM3");
        final SkuPrice costM3Max = context.mock(SkuPrice.class, "costM3Max");
        final DeliveryBucket bucket1 = context.mock(DeliveryBucket.class, "bucket1");
        final DeliveryBucket bucket2 = context.mock(DeliveryBucket.class, "bucket2");

        final Map<DeliveryBucket, List<CartItem>> buckets = new HashMap<>();
        buckets.put(bucket1, Arrays.asList(item1, item2));
        buckets.put(bucket2, Collections.singletonList(item3));

        context.checking(new Expectations() {{
            allowing(cart).getCarrierSlaId(); will(returnValue(Collections.singletonMap("Main", 123L)));
            allowing(cart).getShoppingContext(); will(returnValue(shoppingContext));
            oneOf(shoppingContext).getShopCode(); will(returnValue("SHOP10"));
            oneOf(shoppingContext).getCountryCode(); will(returnValue("GB"));
            oneOf(shoppingContext).getStateCode(); will(returnValue("LON"));
            oneOf(carrierSlaService).getById(123L); will(returnValue(carrierSla));
            oneOf(carrierSla).getGuid(); will(returnValue("CSL001"));
            oneOf(carrierSla).getSlaType(); will(returnValue(CarrierSla.WEIGHT_VOLUME));
            oneOf(carrierSla).getDisplayName(); will(returnValue(new StringI18NModel()));
            oneOf(carrierSla).getName(); will(returnValue("CSL001"));
            oneOf(cart).getCurrentLocale(); will(returnValue("en"));
            oneOf(item1).getProductSkuCode(); will(returnValue("SKU001"));
            oneOf(productService).getProductBySkuCode("SKU001"); will(returnValue(product1));
            oneOf(product1).getProductId(); will(returnValue(10001L));
            oneOf(productService).getProductById(10001L, true); will(returnValue(product1));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.250"));
            oneOf(product1).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.3"));
            allowing(item1).getQty(); will(returnValue(BigDecimal.ONE));
            oneOf(item2).getProductSkuCode(); will(returnValue("SKU002"));
            oneOf(productService).getProductBySkuCode("SKU002"); will(returnValue(product2));
            oneOf(product2).getProductId(); will(returnValue(10002L));
            oneOf(productService).getProductById(10002L, true); will(returnValue(product2));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_WEIGHT_KG); will(returnValue("0.050"));
            oneOf(product2).getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_VOLUME_M3); will(returnValue("0.15"));
            allowing(item2).getQty(); will(returnValue(BigDecimal.TEN));
            oneOf(cart).getCurrencyCode(); will(returnValue("USD"));
            oneOf(cart).getCustomerEmail(); will(returnValue("bob@doe.com"));
            oneOf(cart).getCartItemMap(); will(returnValue(buckets));
            oneOf(bucket1).getSupplier(); will(returnValue("Main"));
            oneOf(bucket2).getSupplier(); will(returnValue("Backorder"));
            oneOf(pricingPolicyProvider).determinePricingPolicy("SHOP10", "USD", "bob@doe.com", "GB", "LON"); will(returnValue(pricingPolicy));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KG", pricingPolicy, "Main", new BigDecimal("0.750")); will(returnValue(costKg));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_KGMAX", pricingPolicy, "Main", MAX); will(returnValue(costKgMax));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3", pricingPolicy, "Main", new BigDecimal("1.80")); will(returnValue(costM3));
            oneOf(deliveryCostRegionalPriceResolver).getSkuPrice(cart, "CSL001_M3MAX", pricingPolicy, "Main", MAX); will(returnValue(costM3Max));
            allowing(costKg).getSkuPriceId(); will(returnValue(0L));
            allowing(costKgMax).getSkuPriceId(); will(returnValue(346L));
            allowing(costKgMax).getQuantity(); will(returnValue(new BigDecimal("10.00")));
            allowing(costM3).getSkuPriceId(); will(returnValue(445L));
            allowing(costM3).getSalePriceForCalculation(); will(returnValue(new Pair<>(new BigDecimal("9.00"), null)));
            allowing(costM3Max).getSkuPriceId(); will(returnValue(446L));
            allowing(costM3Max).getQuantity(); will(returnValue(new BigDecimal("10.00")));
        }});

        final Total delTotal = new WeightBasedPriceListDeliveryCostCalculationStrategy(carrierSlaService, productService, pricingPolicyProvider, deliveryCostRegionalPriceResolver).calculate(cart);

        assertNull(delTotal);

        context.assertIsSatisfied();
    }



}