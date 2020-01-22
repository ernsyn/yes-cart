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

import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.service.domain.PromotionService;
import org.yes.cart.utils.DateUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 04/06/2014
 * Time: 16:34
 */
public class PromotionCouponServiceImplTest extends BaseCoreDBTestCase {

    @Test
    public void testGenerateNamedCoupon() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionCouponService promotionCouponService = ctx().getBean("promotionCouponService", PromotionCouponService.class);

        Promotion couponPromotion = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        couponPromotion.setCode("TESTCOUPON1");
        couponPromotion.setCouponTriggered(true);
        couponPromotion.setCanBeCombined(false);
        couponPromotion.setEnabled(true);
        couponPromotion.setCurrency("EUR");
        couponPromotion.setShopCode("SHOP10");
        couponPromotion.setName("TESTCOUPON1");
        couponPromotion.setEligibilityCondition("");
        couponPromotion.setPromoType(Promotion.TYPE_ORDER);
        couponPromotion.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        couponPromotion.setPromoActionContext("10");

        promotionService.create(couponPromotion);

        promotionCouponService.create(couponPromotion.getPromotionId(), "TESTCOUPON1", 1, 0);

        List<PromotionCoupon> coupons;

        final Map<String, List> filter = Collections.singletonMap("promotionIds", Collections.singletonList(couponPromotion.getPromotionId()));

        coupons = promotionCouponService.findPromotionCoupons(0, 10, null, false, filter);

        assertNotNull(coupons);
        assertEquals(1, coupons.size());

        final PromotionCoupon coupon = coupons.get(0);

        assertEquals("TESTCOUPON1", coupon.getCode());
        assertEquals(1, coupon.getUsageLimit());
        assertEquals(0, coupon.getUsageLimitPerCustomer());
        assertEquals(0, coupon.getUsageCount());

        // nothing happens for duplicate code in same promo
        promotionCouponService.create(couponPromotion.getPromotionId(), "TESTCOUPON1", 1, 0);

        try {
            // not allowed duplicate coupon codes
            promotionCouponService.create(1000001L, "TESTCOUPON1", 1, 0);
        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().contains("TESTCOUPON1"));
        }


    }

    @Test
    public void testGenerateRandomCoupons() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionCouponService promotionCouponService = ctx().getBean("promotionCouponService", PromotionCouponService.class);

        Promotion couponPromotion = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        couponPromotion.setCode("TESTCOUPON2");
        couponPromotion.setCouponTriggered(true);
        couponPromotion.setCanBeCombined(false);
        couponPromotion.setEnabled(true);
        couponPromotion.setCurrency("EUR");
        couponPromotion.setShopCode("SHOP10");
        couponPromotion.setName("TESTCOUPON2");
        couponPromotion.setEligibilityCondition("");
        couponPromotion.setPromoType(Promotion.TYPE_ORDER);
        couponPromotion.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        couponPromotion.setPromoActionContext("10");

        promotionService.create(couponPromotion);

        promotionCouponService.create(couponPromotion.getPromotionId(), 5, 2, 1);

        List<PromotionCoupon> coupons;

        final Map<String, List> filter = Collections.singletonMap("promotionIds", Collections.singletonList(couponPromotion.getPromotionId()));

        coupons = promotionCouponService.findPromotionCoupons(0, 10, null, false, filter);

        assertNotNull(coupons);
        assertEquals(5, coupons.size());

        for (final PromotionCoupon coupon : coupons) {
            assertNotNull(coupon.getCode());
            assertEquals(2, coupon.getUsageLimit());
            assertEquals(1, coupon.getUsageLimitPerCustomer());
            assertEquals(0, coupon.getUsageCount());
        }

    }

    @Test
    public void testGetValidPromotionCouponDisablePromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionCouponService promotionCouponService = ctx().getBean("promotionCouponService", PromotionCouponService.class);

        Promotion couponPromotion = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        couponPromotion.setCode("TESTCOUPON3");
        couponPromotion.setCouponTriggered(true);
        couponPromotion.setCanBeCombined(false);
        couponPromotion.setEnabled(true);
        couponPromotion.setCurrency("EUR");
        couponPromotion.setShopCode("SHOP10");
        couponPromotion.setName("TESTCOUPON3");
        couponPromotion.setEligibilityCondition("");
        couponPromotion.setPromoType(Promotion.TYPE_ORDER);
        couponPromotion.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        couponPromotion.setPromoActionContext("10");

        promotionService.create(couponPromotion);

        promotionCouponService.create(couponPromotion.getPromotionId(), "TESTCOUPON3", 1, 0);

        List<PromotionCoupon> coupons;
        PromotionCoupon validCoupon;

        final Map<String, List> filter = Collections.singletonMap("promotionIds", Collections.singletonList(couponPromotion.getPromotionId()));

        coupons = promotionCouponService.findPromotionCoupons(0, 10, null, false, filter);

        assertNotNull(coupons);
        assertEquals(1, coupons.size());

        validCoupon = promotionCouponService.findValidPromotionCoupon(coupons.get(0).getCode(), "bob@doe.com");
        assertNotNull(validCoupon);

        final PromotionCoupon coupon = coupons.get(0);

        assertEquals("TESTCOUPON3", coupon.getCode());


        couponPromotion.setEnabled(false);
        promotionService.update(couponPromotion);

        coupons = promotionCouponService.findPromotionCoupons(0, 10, null, false, filter);

        assertNotNull(coupons);
        assertEquals(1, coupons.size());

        validCoupon = promotionCouponService.findValidPromotionCoupon(coupons.get(0).getCode(), "bob@doe.com");
        assertNull(validCoupon);

    }

    @Test
    public void testGetValidPromotionCouponPastPromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionCouponService promotionCouponService = ctx().getBean("promotionCouponService", PromotionCouponService.class);

        Promotion couponPromotion = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        couponPromotion.setCode("TESTCOUPON4");
        couponPromotion.setCouponTriggered(true);
        couponPromotion.setCanBeCombined(false);
        couponPromotion.setEnabled(true);
        couponPromotion.setEnabledTo(DateUtils.ldtParseSDT("1999-01-01"));
        couponPromotion.setCurrency("EUR");
        couponPromotion.setShopCode("SHOP10");
        couponPromotion.setName("TESTCOUPON4");
        couponPromotion.setEligibilityCondition("");
        couponPromotion.setPromoType(Promotion.TYPE_ORDER);
        couponPromotion.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        couponPromotion.setPromoActionContext("10");

        promotionService.create(couponPromotion);

        promotionCouponService.create(couponPromotion.getPromotionId(), "TESTCOUPON4", 1, 0);

        List<PromotionCoupon> coupons;
        PromotionCoupon validCoupon;

        final Map<String, List> filter = Collections.singletonMap("promotionIds", Collections.singletonList(couponPromotion.getPromotionId()));

        coupons = promotionCouponService.findPromotionCoupons(0, 10, null, false, filter);

        assertNotNull(coupons);
        assertEquals(1, coupons.size());

        validCoupon = promotionCouponService.findValidPromotionCoupon(coupons.get(0).getCode(), "bob@doe.com");
        assertNull(validCoupon);

        final PromotionCoupon coupon = coupons.get(0);

        assertEquals("TESTCOUPON4", coupon.getCode());

    }

    @Test
    public void testGetValidPromotionCouponFuturePromotion() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionCouponService promotionCouponService = ctx().getBean("promotionCouponService", PromotionCouponService.class);

        Promotion couponPromotion = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        couponPromotion.setCode("TESTCOUPON5");
        couponPromotion.setCouponTriggered(true);
        couponPromotion.setCanBeCombined(false);
        couponPromotion.setEnabled(true);
        couponPromotion.setEnabledFrom(DateUtils.ldtParseSDT("2099-01-01"));
        couponPromotion.setCurrency("EUR");
        couponPromotion.setShopCode("SHOP10");
        couponPromotion.setName("TESTCOUPON5");
        couponPromotion.setEligibilityCondition("");
        couponPromotion.setPromoType(Promotion.TYPE_ORDER);
        couponPromotion.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        couponPromotion.setPromoActionContext("10");

        promotionService.create(couponPromotion);

        promotionCouponService.create(couponPromotion.getPromotionId(), "TESTCOUPON5", 1, 0);

        List<PromotionCoupon> coupons;
        PromotionCoupon validCoupon;

        final Map<String, List> filter = Collections.singletonMap("promotionIds", Collections.singletonList(couponPromotion.getPromotionId()));

        coupons = promotionCouponService.findPromotionCoupons(0, 10, null, false, filter);

        assertNotNull(coupons);
        assertEquals(1, coupons.size());

        validCoupon = promotionCouponService.findValidPromotionCoupon(coupons.get(0).getCode(), "bob@doe.com");
        assertNull(validCoupon);

        final PromotionCoupon coupon = coupons.get(0);

        assertEquals("TESTCOUPON5", coupon.getCode());

    }

    @Test
    public void testGetValidPromotionCouponAllUsed() throws Exception {

        final PromotionService promotionService = ctx().getBean("promotionService", PromotionService.class);
        final PromotionCouponService promotionCouponService = ctx().getBean("promotionCouponService", PromotionCouponService.class);

        Promotion couponPromotion = promotionService.getGenericDao().getEntityFactory().getByIface(Promotion.class);
        couponPromotion.setCode("TESTCOUPON6");
        couponPromotion.setCouponTriggered(true);
        couponPromotion.setCanBeCombined(false);
        couponPromotion.setEnabled(true);
        couponPromotion.setCurrency("EUR");
        couponPromotion.setShopCode("SHOP10");
        couponPromotion.setName("TESTCOUPON6");
        couponPromotion.setEligibilityCondition("");
        couponPromotion.setPromoType(Promotion.TYPE_ORDER);
        couponPromotion.setPromoAction(Promotion.ACTION_PERCENT_DISCOUNT);
        couponPromotion.setPromoActionContext("10");

        promotionService.create(couponPromotion);

        promotionCouponService.create(couponPromotion.getPromotionId(), "TESTCOUPON6", 1, 0);

        List<PromotionCoupon> coupons;
        PromotionCoupon validCoupon;

        final Map<String, List> filter = Collections.singletonMap("promotionIds", Collections.singletonList(couponPromotion.getPromotionId()));

        coupons = promotionCouponService.findPromotionCoupons(0, 10, null, false, filter);

        assertNotNull(coupons);
        assertEquals(1, coupons.size());

        validCoupon = promotionCouponService.findValidPromotionCoupon(coupons.get(0).getCode(), "bob@doe.com");
        assertNotNull(validCoupon);

        final PromotionCoupon coupon = coupons.get(0);

        assertEquals("TESTCOUPON6", coupon.getCode());

        validCoupon.setUsageCount(1);
        promotionCouponService.update(validCoupon);


        validCoupon = promotionCouponService.findValidPromotionCoupon(coupons.get(0).getCode(), "bob@doe.com");
        assertNull(validCoupon);


    }
}
