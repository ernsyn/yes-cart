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

import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.service.domain.CarrierSlaService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.shoppingcart.*;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13-10-20
 * Time: 6:07 PM
 */
public class FreeDeliveryCostCalculationStrategy implements DeliveryCostCalculationStrategy {

    private static final BigDecimal ZERO = MoneyUtils.ZERO;

    private static final BigDecimal QTY = MoneyUtils.ONE;

    private final CarrierSlaService carrierSlaService;
    private final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver;
    private final PricingPolicyProvider pricingPolicyProvider;

    public FreeDeliveryCostCalculationStrategy(final CarrierSlaService carrierSlaService,
                                               final PricingPolicyProvider pricingPolicyProvider,
                                               final DeliveryCostRegionalPriceResolver deliveryCostRegionalPriceResolver) {
        this.carrierSlaService = carrierSlaService;
        this.deliveryCostRegionalPriceResolver = deliveryCostRegionalPriceResolver;
        this.pricingPolicyProvider = pricingPolicyProvider;
    }

    /** {@inheritDoc} */
    @Override
    public Total calculate(final MutableShoppingCart cart) {

        if (!cart.getCarrierSlaId().isEmpty()) {

            Total total = null;

            final Set<DeliveryBucket> cartBuckets = new HashSet<>(cart.getCartItemMap().keySet());
            final Set<DeliveryBucket> supplierBuckets = new HashSet<>();

            for (final Map.Entry<String, Long> supplierCarrierSla : cart.getCarrierSlaId().entrySet()) {

                final String supplier = supplierCarrierSla.getKey();

                supplierBuckets.clear();
                for (final DeliveryBucket bucket : cartBuckets) {
                    if (bucket.getSupplier().equals(supplier)) {
                        supplierBuckets.add(bucket);
                    }

                }

                if (supplierBuckets.isEmpty()) {
                    continue; // no buckets for this selection
                }

                final CarrierSla carrierSla = carrierSlaService.getById(supplierCarrierSla.getValue());
                if (carrierSla != null && CarrierSla.FREE.equals(carrierSla.getSlaType())) {

                    final String carrierSlaGUID = carrierSla.getGuid();
                    final String carrierSlaName = new FailoverStringI18NModel(
                            carrierSla.getDisplayName(),
                            carrierSla.getName()).getValue(cart.getCurrentLocale());

                    final PricingPolicyProvider.PricingPolicy policy = pricingPolicyProvider.determinePricingPolicy(
                            cart.getShoppingContext().getShopCode(), cart.getCurrencyCode(), cart.getCustomerEmail(),
                            cart.getShoppingContext().getCountryCode(),
                            cart.getShoppingContext().getStateCode()
                    );

                    final BigDecimal qty = QTY;

                    final SkuPrice price = getSkuPrice(cart, carrierSlaGUID, policy, supplier, qty);

                    if (price != null && price.getSkuPriceId() > 0L) {

                        // Price acts only as a marker for regional availability
                        final BigDecimal listPrice = ZERO;
                        final BigDecimal deliveryListCost = ZERO;

                        for (final DeliveryBucket bucket : supplierBuckets) {
                            // Add shipping line for every bucket by this supplier (e.g. if we have multi delivery)
                            cart.addShippingToCart(bucket, carrierSlaGUID, carrierSlaName, qty);
                            cart.setShippingPrice(carrierSlaGUID, bucket, listPrice, listPrice);
                        }

                        total = new TotalImpl(
                                Total.ZERO,
                                Total.ZERO,
                                Total.ZERO,
                                Total.ZERO,
                                false,
                                null,
                                Total.ZERO,
                                Total.ZERO,
                                Total.ZERO,
                                deliveryListCost,
                                deliveryListCost,
                                false,
                                null,
                                Total.ZERO,
                                deliveryListCost,
                                deliveryListCost,
                                Total.ZERO,
                                deliveryListCost,
                                deliveryListCost
                        );

                    }

                }
            }

            return total;
        }
        return null;
    }

    protected SkuPrice getSkuPrice(final MutableShoppingCart cart,
                                   final String carrierSlaId,
                                   final PricingPolicyProvider.PricingPolicy policy,
                                   final String supplier,
                                   final BigDecimal qty) {

        return deliveryCostRegionalPriceResolver.getSkuPrice(cart, carrierSlaId, policy, supplier, qty);

    }

}
