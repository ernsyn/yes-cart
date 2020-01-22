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
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.SkuWarehouseService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/01/2017
 * Time: 18:37
 */
public class SkuWarehouseServiceCachedImpl implements SkuWarehouseService {

    private final SkuWarehouseService skuWarehouseService;

    public SkuWarehouseServiceCachedImpl(final SkuWarehouseService skuWarehouseService) {
        this.skuWarehouseService = skuWarehouseService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "skuWarehouseService-productSkusOnWarehouse")
    public List<SkuWarehouse> getProductSkusOnWarehouse(final long productId, final long warehouseId) {
        return skuWarehouseService.getProductSkusOnWarehouse(productId, warehouseId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty) {
        return skuWarehouseService.reservation(warehouse, productSkuCode, reserveQty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal reservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal reserveQty, final boolean allowBackorder) {
        return skuWarehouseService.reservation(warehouse, productSkuCode, reserveQty, allowBackorder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal voidReservation(final Warehouse warehouse, final String productSkuCode, final BigDecimal voidQty) {
        return skuWarehouseService.voidReservation(warehouse, productSkuCode, voidQty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal credit(final Warehouse warehouse, final String productSkuCode, final BigDecimal addQty) {
        return skuWarehouseService.credit(warehouse, productSkuCode, addQty);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public BigDecimal debit(final Warehouse warehouse, final String productSkuCode, final BigDecimal debitQty) {
        return skuWarehouseService.debit(warehouse, productSkuCode, debitQty);
    }

    /** {@inheritDoc}*/
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public SkuWarehouse create(final SkuWarehouse instance) {
        return skuWarehouseService.create(instance);
    }

    /** {@inheritDoc}*/
    @Override
    @CacheEvict(value = {
            "skuWarehouseService-productSkusOnWarehouse"
    }, allEntries = true)
    public SkuWarehouse update(final SkuWarehouse instance) {
        return skuWarehouseService.update(instance);
    }

    /** {@inheritDoc} */
    @Override
    public SkuWarehouse findByWarehouseSku(final Warehouse warehouse, final String productSkuCode) {
        return skuWarehouseService.findByWarehouseSku(warehouse, productSkuCode);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> findProductSkuForWhichInventoryChangedAfter(final Instant lastUpdate) {
        return skuWarehouseService.findProductSkuForWhichInventoryChangedAfter(lastUpdate);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> findProductSkuByUnavailableBefore(final LocalDateTime before) {
        return skuWarehouseService.findProductSkuByUnavailableBefore(before);
    }

    /** {@inheritDoc} */
    @Override
    public List<SkuWarehouse> findSkuWarehouses(final int start, final int offset, final String sort, final boolean sortDescending, final Map<String, List> filter) {
        return skuWarehouseService.findSkuWarehouses(start, offset, sort, sortDescending, filter);
    }

    /** {@inheritDoc} */
    @Override
    public int findSkuWarehouseCount(final Map<String, List> filter) {
        return skuWarehouseService.findSkuWarehouseCount(filter);
    }

    /** {@inheritDoc} */
    @Override
    public List<SkuWarehouse> findAll() {
        return skuWarehouseService.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllIterator(final ResultsIteratorCallback<SkuWarehouse> callback) {
        skuWarehouseService.findAllIterator(callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findByCriteriaIterator(final String eCriteria, final Object[] parameters, final ResultsIteratorCallback<SkuWarehouse> callback) {
        skuWarehouseService.findByCriteriaIterator(eCriteria, parameters, callback);
    }

    /** {@inheritDoc} */
    @Override
    public SkuWarehouse findById(final long pk) {
        return skuWarehouseService.findById(pk);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final SkuWarehouse instance) {
        skuWarehouseService.delete(instance);
    }

    /** {@inheritDoc} */
    @Override
    public List<SkuWarehouse> findByCriteria(final String eCriteria, final Object... parameters) {
        return skuWarehouseService.findByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public int findCountByCriteria(final String eCriteria, final Object... parameters) {
        return skuWarehouseService.findCountByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public SkuWarehouse findSingleByCriteria(final String eCriteria, final Object... parameters) {
        return skuWarehouseService.findSingleByCriteria(eCriteria, parameters);
    }

    /** {@inheritDoc} */
    @Override
    public GenericDAO<SkuWarehouse, Long> getGenericDao() {
        return skuWarehouseService.getGenericDao();
    }
}
