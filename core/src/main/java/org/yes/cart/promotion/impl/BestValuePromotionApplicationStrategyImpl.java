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

package org.yes.cart.promotion.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.promotion.PromoTriplet;
import org.yes.cart.promotion.PromotionApplicationStrategy;
import org.yes.cart.promotion.PromotionCondition;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.utils.MoneyUtils;
import org.yes.cart.utils.log.Markers;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: denispavlov
 * Date: 06/06/2014
 * Time: 09:02
 */
public class BestValuePromotionApplicationStrategyImpl implements PromotionApplicationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(BestValuePromotionApplicationStrategyImpl.class);

    private final PromotionCouponService promotionCouponService;

    public BestValuePromotionApplicationStrategyImpl(final PromotionCouponService promotionCouponService) {
        this.promotionCouponService = promotionCouponService;
    }

    /**
     * best deal is the industry standard since it provides customer with lowest price possible. However
     * there are use cases when we want alternative promotion strategy - e.g. by priority or some other.
     *
     * In such cases the bean definition must be overriden with custom implementation.
     *
     * {@inheritDoc}
     */
    @Override
    public void applyPromotions(final List<List<PromoTriplet>> promoBuckets, final Map<String, Object> context) {

        List<PromoTriplet> bestValue = null;
        BigDecimal bestDiscountValue = BigDecimal.ZERO;

        final Map<Long, PromotionCoupon> validCoupons = loadCoupons(context);

        for (final List<PromoTriplet> promoBucket : promoBuckets) {

            try {
                if (promoBucket.isEmpty()) {
                    continue;
                }

                final List<PromoTriplet> applicable = new ArrayList<>(promoBucket.size());

                BigDecimal discountValue = BigDecimal.ZERO;

                for (final PromoTriplet promo : promoBucket) {

                    context.put(PromotionCondition.VAR_ACTION_CONTEXT, promo.getPromotion().getPromoActionContext());
                    context.put(PromotionCondition.VAR_PROMOTION, promo.getPromotion());

                    boolean eligible = true;

                    if (promo.getPromotion().isCouponTriggered()) {
                        // Only allow coupon promotion if we have a valid coupon
                        eligible = validCoupons.containsKey(promo.getPromotion().getPromotionId());
                    }

                    if (eligible) {

                        // Eligibility condition may be heavy, so we check it after coupons
                        eligible = promo.getCondition().isEligible(context);

                        if (eligible) {

                            final BigDecimal pdisc = promo.getAction().testDiscountValue(context);
                            if (MoneyUtils.isPositive(pdisc)) {
                                // only if we get some discount from test this promo qualifies to be applicable
                                applicable.add(promo);
                                // cumulative discount value is total value in percent relative to sale price
                                discountValue = discountValue.add(pdisc);
                            }
                        }
                    }

                }

                if (!applicable.isEmpty() && MoneyUtils.isFirstBiggerThanSecond(discountValue, bestDiscountValue)) {

                    bestDiscountValue = discountValue;
                    bestValue = applicable;

                }
            } catch (Exception exp) {
                LOG.error(Markers.alert(), "Unable to apply best value promotions: " + promoBucket + ", cause: " + exp.getMessage(), exp);
            }
        }

        if (CollectionUtils.isNotEmpty(bestValue)) {

            for (final PromoTriplet promo : bestValue) {

                context.put(PromotionCondition.VAR_ACTION_CONTEXT, promo.getPromotion().getPromoActionContext());
                context.put(PromotionCondition.VAR_PROMOTION, promo.getPromotion());
                context.put(PromotionCondition.VAR_PROMOTION_CODE, getPromotionCode(promo, validCoupons.get(promo.getPromotion().getPromotionId())));

                promo.getAction().perform(context);

            }

        }
    }

    private String getPromotionCode(final PromoTriplet promo, final PromotionCoupon promotionCoupon) {
        if (promo.getPromotion().isCouponTriggered()) {
            return promo.getPromotion().getCode() + ":" + promotionCoupon.getCode();
        }
        return promo.getPromotion().getCode();
    }

    private Map<Long, PromotionCoupon> loadCoupons(final Map<String, Object> context) {

        final ShoppingCart cart = (ShoppingCart) context.get(PromotionCondition.VAR_CART);

        if (cart == null) {
            return Collections.emptyMap();
        }

        final List<String> couponCodes = cart.getCoupons();

        if (couponCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        final Map<Long, PromotionCoupon> map = new HashMap<>();
        for (final String couponCode : couponCodes) {

            final PromotionCoupon coupon = promotionCouponService.findValidPromotionCoupon(couponCode, cart.getCustomerEmail());
            if (coupon != null) {
                map.put(coupon.getPromotion().getPromotionId(), coupon);
            }

        }
        return Collections.unmodifiableMap(map);
    }
}
