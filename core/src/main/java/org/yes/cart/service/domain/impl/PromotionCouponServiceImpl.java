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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.Promotion;
import org.yes.cart.domain.entity.PromotionCoupon;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.promotion.PromotionCouponCodeGenerator;
import org.yes.cart.service.domain.PromotionCouponService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.TimeContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 03/06/2014
 * Time: 18:35
 */
public class PromotionCouponServiceImpl extends BaseGenericServiceImpl<PromotionCoupon> implements PromotionCouponService {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionCouponServiceImpl.class);

    private final GenericDAO<Promotion, Long> promotionDao;
    private final PromotionCouponCodeGenerator couponCodeGenerator;

    public PromotionCouponServiceImpl(final GenericDAO<PromotionCoupon, Long> promotionCouponDao,
                                      final GenericDAO<Promotion, Long> promotionDao,
                                      final PromotionCouponCodeGenerator couponCodeGenerator) {
        super(promotionCouponDao);
        this.promotionDao = promotionDao;
        this.couponCodeGenerator = couponCodeGenerator;
    }

    @Override
    public PromotionCoupon findByCode(final String couponCode) {
        return getGenericDao().findSingleByNamedQuery("COUPON.BY.CODE", couponCode);
    }

    /** {@inheritDoc} */
    @Override
    public void create(Long promotionId, String couponCode, int limit, int limitPerUser) {

        final List<Object> promoIdAndCode = getGenericDao().findQueryObjectByNamedQuery("PROMOTION.ID.AND.CODE.BY.COUPON.CODE", couponCode);
        if (!promoIdAndCode.isEmpty()) {

            final Pair<Long, String> promoIdAndCodePair = (Pair<Long, String>) promoIdAndCode.get(0);

            if (promoIdAndCodePair.getFirst().equals(promotionId)) {
                return; // we already have this
            }
            throw new IllegalArgumentException("Coupon code '" + couponCode + "' already used in promotion: " + promoIdAndCodePair.getSecond());
        }

        final Promotion promotion = this.promotionDao.findById(promotionId);
        if (promotion == null || !promotion.isCouponTriggered()) {
            throw new IllegalArgumentException("Coupon code '" + couponCode + "' cannot be added to non-coupon promotion: " + promotionId);
        }
        final PromotionCoupon coupon = getGenericDao().getEntityFactory().getByIface(PromotionCoupon.class);
        coupon.setPromotion(promotion);
        coupon.setCode(couponCode);
        coupon.setUsageLimit(limit);
        coupon.setUsageLimitPerCustomer(limitPerUser);
        coupon.setUsageCount(0);

        this.getGenericDao().saveOrUpdate(coupon);

    }

    /** {@inheritDoc} */
    @Override
    public void create(Long promotionId, int couponCount, int limit, int limitPerUser) {

        final Promotion promotion = this.promotionDao.findById(promotionId);
        if (promotion == null || !promotion.isCouponTriggered()) {
            throw new IllegalArgumentException("Coupon codes cannot be added to non-coupon promotion: " + promotionId);
        }

        for (int i = 0; i < couponCount; i++) {

            String couponCode;
            List<Object> promoIdAndCode;

            do {
                couponCode = this.couponCodeGenerator.generate(promotion.getShopCode());
                promoIdAndCode = getGenericDao().findQueryObjectByNamedQuery("PROMOTION.ID.AND.CODE.BY.COUPON.CODE", couponCode);
            } while (!promoIdAndCode.isEmpty());

            final PromotionCoupon coupon = getGenericDao().getEntityFactory().getByIface(PromotionCoupon.class);
            coupon.setPromotion(promotion);
            coupon.setCode(couponCode);
            coupon.setUsageLimit(limit);
            coupon.setUsageLimitPerCustomer(limitPerUser);
            coupon.setUsageCount(0);

            this.getGenericDao().saveOrUpdate(coupon);

        }

    }

    /** {@inheritDoc} */
    @Override
    public PromotionCoupon findValidPromotionCoupon(String coupon, String customerEmail) {

        // Get enabled coupon code usage limit of which is greater than usage count
        final LocalDateTime now = now();
        final PromotionCoupon couponEntity = getGenericDao().findSingleByNamedQuery("ENABLED.COUPON.BY.CODE",
                coupon, true, now, now);
        if (couponEntity == null) {
            LOG.debug("Coupon {}, is not yet active", coupon);
            return null;
        }

        // if we have customer usage limit
        if (couponEntity.getUsageLimitPerCustomer() > 0) {
            final List<Object> count = getGenericDao()
                    .findQueryObjectByNamedQuery("COUPON.USAGE.BY.CODE.AND.EMAIL",
                            couponEntity.getCode(), customerEmail, CustomerOrder.ORDER_STATUS_NONE);
            if (!count.isEmpty()) {

                final Number usage = (Number) count.get(0);
                // valid coupon only when we have not exceeded the limit
                if (usage.intValue() >= couponEntity.getUsageLimitPerCustomer()) {
                    LOG.debug("Coupon {} usage limit is exceeded", coupon);
                    return null;
                }

            }
        }

        return couponEntity;
    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

    /** {@inheritDoc} */
    @Override
    public void updateUsage(final PromotionCoupon promotionCoupon, final int offset) {

        final List<Object> count = getGenericDao()
                .findQueryObjectByNamedQuery("COUPON.USAGE.BY.COUPON.CODE",
                        promotionCoupon.getCode(), CustomerOrder.ORDER_STATUS_NONE);

        int usage = offset;
        if (!count.isEmpty()) {

            final Number usageRecordsCount = (Number) count.get(0);
            usage += usageRecordsCount.intValue();

        }

        promotionCoupon.setUsageCount(usage);
        getGenericDao().saveOrUpdate(promotionCoupon);

    }

    /** {@inheritDoc} */
    @Override
    public void removeAll(final long promotionId) {

        getGenericDao().executeUpdate("REMOVE.ALL.COUPONS.BY.PROMOTION.ID", promotionId);

    }





    private Pair<String, Object[]> findPromotionCouponQuery(final boolean count,
                                                            final String sort,
                                                            final boolean sortDescending,
                                                            final Map<String, List> filter) {

        final Map<String, List> currentFilter = filter != null ? new HashMap<>(filter) : null;

        final StringBuilder hqlCriteria = new StringBuilder();
        final List<Object> params = new ArrayList<>();

        if (count) {
            hqlCriteria.append("select count(p.promotioncouponId) from PromotionCouponEntity p ");
        } else {
            hqlCriteria.append("select p from PromotionCouponEntity p ");
        }

        final List promotionIds = currentFilter != null ? currentFilter.remove("promotionIds") : null;
        if (promotionIds != null) {
            hqlCriteria.append(" where (p.promotion.promotionId in (?1)) ");
            params.add(promotionIds);
        }

        HQLUtils.appendFilterCriteria(hqlCriteria, params, "p", currentFilter);

        if (StringUtils.isNotBlank(sort)) {

            hqlCriteria.append(" order by p." + sort + " " + (sortDescending ? "desc" : "asc"));

        }

        return new Pair<>(
                hqlCriteria.toString(),
                params.toArray(new Object[params.size()])
        );

    }




    /** {@inheritDoc} */
    @Override
    public List<PromotionCoupon> findPromotionCoupons(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {

        final Pair<String, Object[]> query = findPromotionCouponQuery(false, sort, sortDescending, filter);

        return getGenericDao().findRangeByQuery(
                query.getFirst(),
                start, offset,
                query.getSecond()
        );
    }

    /** {@inheritDoc} */
    @Override
    public int findPromotionCouponCount(final Map<String, List> filter) {

        final Pair<String, Object[]> query = findPromotionCouponQuery(true, null, false, filter);

        return getGenericDao().findCountByQuery(
                query.getFirst(),
                query.getSecond()
        );
    }
}
