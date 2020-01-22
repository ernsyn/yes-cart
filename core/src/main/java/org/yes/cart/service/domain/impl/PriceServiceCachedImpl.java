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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.dao.ResultsIteratorCallback;
import org.yes.cart.domain.entity.SkuPrice;
import org.yes.cart.service.domain.PriceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 17:33
 */
public class PriceServiceCachedImpl implements PriceService {

    private final PriceService priceService;

    public PriceServiceCachedImpl(final PriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "priceService-minimalPrice")
    public SkuPrice getMinimalPrice(final Long productId,
                                    final String selectedSku,
                                    final long customerShopId,
                                    final Long masterShopId,
                                    final String currencyCode,
                                    final BigDecimal quantity,
                                    final boolean enforceTier,
                                    final String pricingPolicy,
                                    final String supplier) {

        return priceService.getMinimalPrice(productId, selectedSku, customerShopId, masterShopId, currencyCode, quantity, enforceTier, pricingPolicy, supplier);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "priceService-allCurrentPrices")
    public List<SkuPrice> getAllCurrentPrices(final Long productId,
                                              final String selectedSku,
                                              final long customerShopId,
                                              final Long masterShopId,
                                              final String currencyCode,
                                              final String pricingPolicy,
                                              final String supplier) {

        return priceService.getAllCurrentPrices(productId, selectedSku, customerShopId, masterShopId, currencyCode, pricingPolicy, supplier);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "priceService-allPrices")
    public List<SkuPrice> getAllPrices(final Long productId, final String selectedSku, final String currencyCode) {
        return priceService.getAllPrices(productId, selectedSku, currencyCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SkuPrice> findAll() {
        return priceService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<SkuPrice> callback) {
        priceService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<SkuPrice> callback) {
        priceService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SkuPrice findById(final long pk) {
        return priceService.findById(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public SkuPrice create(final SkuPrice instance) {
        return priceService.create(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public SkuPrice update(final SkuPrice instance) {
        return priceService.update(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "imageService-seoImage" ,
            "priceService-minimalPrice",
            "priceService-allCurrentPrices",
            "priceService-allPrices"
    }, allEntries = true)
    public void delete(final SkuPrice instance) {
        priceService.delete(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SkuPrice> findPrices(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return priceService.findPrices(start, offset, sort, sortDescending, filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findPriceCount(final Map<String, List> filter) {
        return priceService.findPriceCount(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SkuPrice> findByCriteria(final String eCriteria, final Object... parameters) {
        return priceService.findByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return priceService.findCountByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SkuPrice findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return priceService.findSingleByCriteria(eCriteria, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GenericDAO<SkuPrice, Long> getGenericDao() {
        return priceService.getGenericDao();
    }
}
