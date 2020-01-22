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

package org.yes.cart.service.dto.impl;

import com.inspiresoftware.lib.dto.geda.adapter.repository.AdaptersRepository;
import com.inspiresoftware.lib.dto.geda.assembler.Assembler;
import com.inspiresoftware.lib.dto.geda.assembler.DTOAssembler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.dto.InventoryDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.domain.entity.SkuWarehouse;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;
import org.yes.cart.service.domain.SkuWarehouseService;
import org.yes.cart.service.dto.DtoInventoryService;
import org.yes.cart.service.dto.DtoWarehouseService;
import org.yes.cart.utils.HQLUtils;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 12-11-29
 * Time: 7:11 PM
 */
public class DtoInventoryServiceImpl implements DtoInventoryService {

    private final DtoWarehouseService dtoWarehouseService;

    private final SkuWarehouseService skuWarehouseService;
    private final GenericDAO<SkuWarehouse, Long> skuWarehouseDAO;
    private final GenericDAO<ProductSku, Long> productSkuDAO;
    private final GenericDAO<Warehouse, Long> warehouseDAO;
    private final DtoFactory dtoFactory;
    private final AdaptersRepository adaptersRepository;

    private final Assembler skuWarehouseAsm;

    public DtoInventoryServiceImpl(final DtoWarehouseService dtoWarehouseService,
                                   final SkuWarehouseService skuWarehouseService,
                                   final GenericDAO<ProductSku, Long> productSkuDAO,
                                   final GenericDAO<Warehouse, Long> warehouseDAO,
                                   final DtoFactory dtoFactory,
                                   final AdaptersRepository adaptersRepository) {
        this.dtoWarehouseService = dtoWarehouseService;
        this.skuWarehouseService = skuWarehouseService;
        this.skuWarehouseDAO = skuWarehouseService.getGenericDao();
        this.productSkuDAO = productSkuDAO;
        this.warehouseDAO = warehouseDAO;
        this.dtoFactory = dtoFactory;
        this.adaptersRepository = adaptersRepository;

        this.skuWarehouseAsm = DTOAssembler.newAssembler(
                this.dtoFactory.getImplClass(InventoryDTO.class),
                this.skuWarehouseDAO.getEntityFactory().getImplClass(SkuWarehouse.class));
    }

    private final static char[] CODE = new char[] { '!' };
    private final static char[] LOW_OR_RESERVED = new char[] { '-', '+' };
    static {
        Arrays.sort(LOW_OR_RESERVED);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResult<InventoryDTO> findInventory(final SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final Map<String, List> params = filter.reduceParameters("filter", "centreId");
        final String textFilter = FilterSearchUtils.getStringFilter(params.get("filter"));
        final long warehouseId = FilterSearchUtils.getIdFilter(params.get("centreId"));

        final int pageSize = filter.getSize();
        final int startIndex = filter.getStart() * pageSize;

        final SkuWarehouseService skuWarehouseService = this.skuWarehouseService;

        if (warehouseId > 0L) {
            // only allow lists for warehouse inventory lists

            final Map<String, List> currentFilter = new HashMap<>();

            if (StringUtils.isNotBlank(textFilter)) {

                final Pair<LocalDateTime, LocalDateTime> dateSearch = ComplexSearchUtils.checkDateRangeSearch(textFilter);

                if (dateSearch != null) {

                    final LocalDateTime from = dateSearch.getFirst();
                    final LocalDateTime to = dateSearch.getSecond();

                    if (from != null) {
                        currentFilter.put("availablefrom", Collections.singletonList(SearchContext.MatchMode.GE.toParam(from)));
                    }
                    if (to != null) {
                        currentFilter.put("availableto", Collections.singletonList(SearchContext.MatchMode.LE.toParam(to)));
                    }

                } else {


                    final Pair<String, BigDecimal> lowOrReserved = ComplexSearchUtils.checkNumericSearch(textFilter, LOW_OR_RESERVED, Constants.INVENTORY_SCALE);

                    if (lowOrReserved != null) {

                        if ("-".equals(lowOrReserved.getFirst())) {
                            currentFilter.put("quantity", Collections.singletonList(SearchContext.MatchMode.LE.toParam(lowOrReserved.getSecond())));
                        } else if ("+".equals(lowOrReserved.getFirst())) {
                            currentFilter.put("reserved", Collections.singletonList(SearchContext.MatchMode.GE.toParam(lowOrReserved.getSecond())));
                        }

                    } else {

                        final Pair<String, String> byCode = ComplexSearchUtils.checkSpecialSearch(textFilter, CODE);

                        if (byCode != null) {

                            final List<ProductSku> skus = productSkuDAO.findRangeByCriteria(
                                    " where lower(e.code) = ?1 or lower(e.product.code) = ?1 or lower(e.product.manufacturerCode) = ?1 or lower(e.product.pimCode) = ?1 or lower(e.barCode) = ?1 or lower(e.manufacturerCode) = ?1",
                                    0, pageSize,
                                    HQLUtils.criteriaIeq(byCode.getSecond())
                            );

                            final List<String> skuCodes = new ArrayList<>();
                            for (final ProductSku sku : skus) {
                                skuCodes.add(sku.getCode()); // sku codes from product match
                            }

                            if (skuCodes.isEmpty()) {

                                currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(byCode.getSecond())));

                            } else {

                                SearchContext.JoinMode.OR.setMode(currentFilter);
                                currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.EQ.toParam(byCode.getSecond())));
                                currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(skuCodes)));

                            }

                        } else {

                            final List<ProductSku> skus = productSkuDAO.findRangeByCriteria(
                                    " where lower(e.code) = ?1 or  lower(e.product.code) like ?1 or lower(e.product.name) like ?1 or lower(e.name) like ?1",
                                    0, pageSize,
                                    HQLUtils.criteriaIlikeAnywhere(textFilter)
                            );

                            final List<String> skuCodes = new ArrayList<>();
                            for (final ProductSku sku : skus) {
                                skuCodes.add(sku.getCode()); // sku codes from product match
                            }

                            if (skuCodes.isEmpty()) {

                                currentFilter.put("skuCode", Collections.singletonList(textFilter));

                            } else {

                                SearchContext.JoinMode.OR.setMode(currentFilter);
                                currentFilter.put("skuCode", Collections.singletonList(textFilter));
                                currentFilter.put("skuCode", Collections.singletonList(SearchContext.MatchMode.ANY.toParam(skuCodes)));

                            }

                        }
                    }

                }
            }

            currentFilter.put("warehouseIds", Collections.singletonList(warehouseId));

            final int count = skuWarehouseService.findSkuWarehouseCount(currentFilter);
            if (count > startIndex) {

                final List<InventoryDTO> entities = new ArrayList<>();
                final List<SkuWarehouse> skuWarehouses = skuWarehouseService.findSkuWarehouses(startIndex, pageSize, filter.getSortBy(), filter.isSortDesc(), currentFilter);

                final Map<String, Object> adapters = adaptersRepository.getAll();
                for (final SkuWarehouse entity : skuWarehouses) {
                    final InventoryDTO dto = dtoFactory.getByIface(InventoryDTO.class);
                    skuWarehouseAsm.assembleDto(dto, entity, adapters, dtoFactory);
                    entities.add(dto);
                }

                return new SearchResult<>(filter, entities, count);

            }
        }
        return new SearchResult<>(filter, Collections.emptyList(), 0);
    }

    /** {@inheritDoc} */
    @Override
    public void removeInventory(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        dtoWarehouseService.removeSkuOnWarehouse(skuWarehouseId);
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO getInventory(final long skuWarehouseId) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        final InventoryDTO dto = dtoFactory.getByIface(InventoryDTO.class);
        final Map<String, Object> adapters = adaptersRepository.getAll();
        final SkuWarehouse entity = skuWarehouseDAO.findById(skuWarehouseId);
        skuWarehouseAsm.assembleDto(dto, entity, adapters, dtoFactory);

        return dto;
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO createInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return saveInventory(inventory);
    }

    /** {@inheritDoc} */
    @Override
    public InventoryDTO updateInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {
        return saveInventory(inventory);
    }

    private InventoryDTO saveInventory(final InventoryDTO inventory) throws UnmappedInterfaceException, UnableToCreateInstanceException {

        SkuWarehouse entity = null;
        if (inventory.getSkuWarehouseId() > 0) {
            // check by id
            entity = skuWarehouseDAO.findById(inventory.getSkuWarehouseId());
        }
        if (entity == null) {

            // check by unique combination
            final List<SkuWarehouse> candidates = skuWarehouseDAO.findRangeByCriteria(
                    " where e.warehouse.code = ?1 and e.skuCode = ?2",
                    0, 1,
                    inventory.getWarehouseCode(),
                    inventory.getSkuCode()
            );
            if (CollectionUtils.isNotEmpty(candidates)) {
                // TODO: this will work but may be potentially dangerous feature since we do edit from Add.
                // entity = candidates.get(0);
                throw new IllegalArgumentException("Duplicate entry for product: " + inventory.getSkuCode() + " for warehouse: " + inventory.getWarehouseCode());
            }
        }

        if (entity == null) {
            final List<Warehouse> warehouses = warehouseDAO.findByCriteria(" where e.code = ?1", inventory.getWarehouseCode());
            if (warehouses == null || warehouses.size() != 1) {
                throw new UnableToCreateInstanceException("Invalid warehouse: " + inventory.getWarehouseCode(), null);
            }

            entity = skuWarehouseDAO.getEntityFactory().getByIface(SkuWarehouse.class);
            entity.setSkuCode(inventory.getSkuCode());
            entity.setWarehouse(warehouses.get(0));
        }

        cleanOrderQuantities(inventory);

        final Map<String, Object> adapters = adaptersRepository.getAll();

        skuWarehouseAsm.assembleEntity(inventory, entity, adapters, dtoFactory);
        // use service since we flush cache there
        if (entity.getSkuWarehouseId() > 0L) {
            skuWarehouseService.update(entity);
        } else {
            skuWarehouseService.create(entity);
        }

        skuWarehouseAsm.assembleDto(inventory, entity, adapters, dtoFactory);

        return inventory;
    }

    private void cleanOrderQuantities(final InventoryDTO inventoryDTO) {
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, inventoryDTO.getMinOrderQuantity())) {
            inventoryDTO.setMinOrderQuantity(null);
        }
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, inventoryDTO.getMaxOrderQuantity())) {
            inventoryDTO.setMaxOrderQuantity(null);
        }
        if (MoneyUtils.isFirstEqualToSecond(BigDecimal.ZERO, inventoryDTO.getStepOrderQuantity())) {
            inventoryDTO.setStepOrderQuantity(null);
        }
    }

}
