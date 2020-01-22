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

import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.service.order.DeliveryBucket;
import org.yes.cart.service.order.OrderSplittingStrategy;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.InventoryResolver;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.utils.TimeContext;

import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 18/02/2016
 * Time: 16:16
 */
public class OrderSplittingStrategyImpl implements OrderSplittingStrategy {

    private final ShopService shopService;
    private final ProductService productService;
    private final WarehouseService warehouseService;
    private final InventoryResolver inventoryResolver;

    public OrderSplittingStrategyImpl(final ShopService shopService,
                                      final ProductService productService,
                                      final WarehouseService warehouseService,
                                      final InventoryResolver inventoryResolver) {
        this.shopService = shopService;
        this.productService = productService;
        this.warehouseService = warehouseService;
        this.inventoryResolver = inventoryResolver;
    }


    /** {@inheritDoc} */
    @Override
    public DeliveryBucket determineDeliveryBucket(final CartItem item,
                                                  final ShoppingCart cart) {

        final Map<String, Boolean> onePhysicalDelivery = new HashMap<>();
        for (final Map.Entry<String, Boolean> isAllowed : cart.getOrderInfo().getMultipleDeliveryAvailable().entrySet()) {
            onePhysicalDelivery.put(isAllowed.getKey(), !cart.getOrderInfo().isMultipleDelivery() || !isAllowed.getValue());
        }

        final Map<DeliveryBucket, List<CartItem>> deliveryGroups = new HashMap<>();

        DeliveryBucket itemBucket = item.getDeliveryBucket();
        if (item.getDeliveryBucket() == null) {
            final Map<String, Warehouse> warehouses = warehouseService.getByShopIdMapped(cart.getShoppingContext().getCustomerShopId(), false);
            final Pair<String, String> deliveryGroup = getDeliveryGroup(item, warehouses);
            final Warehouse warehouse = warehouses.get(deliveryGroup.getSecond());
            itemBucket = new DeliveryBucketImpl(deliveryGroup.getFirst(), deliveryGroup.getSecond());
            onePhysicalDelivery.put(deliveryGroup.getSecond(), !cart.getOrderInfo().isMultipleDelivery() || warehouse == null || !warehouse.isMultipleShippingSupported());
        }

        for (CartItem cartItem : cart.getCartItemList()) {
            final boolean itemToCheck = item.isGift() == cartItem.isGift() && item.getProductSkuCode().equals(cartItem.getProductSkuCode());
            final DeliveryBucket cartItemBucket = itemToCheck ? itemBucket : cartItem.getDeliveryBucket();
            if (cartItemBucket != null) {
                List<CartItem> items = deliveryGroups.computeIfAbsent(cartItemBucket, k -> new ArrayList<>());
                items.add(cartItem);
            }
        }


        groupDeliveriesIntoMixedIfNecessary(deliveryGroups, onePhysicalDelivery);


        for (final Map.Entry<DeliveryBucket, List<CartItem>> group : deliveryGroups.entrySet()) {

            for (final CartItem groupItem : group.getValue()) {

                final boolean itemToCheck = item.isGift() == groupItem.isGift() && item.getProductSkuCode().equals(groupItem.getProductSkuCode());
                if (itemToCheck) {
                    return group.getKey();
                }

            }

        }

        return itemBucket;
    }

    /** {@inheritDoc} */
    @Override
    public Map<DeliveryBucket, List<CartItem>> determineDeliveryBuckets(final long shopId,
                                                                        final Collection<CartItem> items,
                                                                        final Map<String, Boolean> onePhysicalDelivery) {

        // use tree map to preserve natural order by delivery group i.e. D1, D2, D3 etc.
        final Map<DeliveryBucket, List<CartItem>> deliveryGroups = new TreeMap<>();

        final Map<String, Warehouse> warehouses = warehouseService.getByShopIdMapped(shopId, false);

        for (final CartItem cartItem : items) {

            final Pair<String, String> deliveryGroup = getDeliveryGroup(cartItem, warehouses);

            final DeliveryBucket bucket = new DeliveryBucketImpl(deliveryGroup.getFirst(), deliveryGroup.getSecond());

            if (!deliveryGroups.containsKey(bucket)) {
                deliveryGroups.put(bucket, new ArrayList<>());
            }

            deliveryGroups.get(bucket).add(cartItem);

        }


        groupDeliveriesIntoMixedIfNecessary(deliveryGroups, onePhysicalDelivery);

        return deliveryGroups;
    }

    void groupDeliveriesIntoMixedIfNecessary(final Map<DeliveryBucket, List<CartItem>> deliveryGroups, final Map<String, Boolean> onePhysicalDelivery) {

        final Map<String, Integer> deliveryQty = getPhysicalDeliveriesQty(deliveryGroups);

        final List<DeliveryBucket> removeBuckets = new ArrayList<>();
        final Map<DeliveryBucket, List<CartItem>> collectors = new HashMap<>();

        for (Map.Entry<DeliveryBucket, List<CartItem>> entry : deliveryGroups.entrySet()) {

            final String group = entry.getKey().getGroup();
            final String supplier = entry.getKey().getSupplier();

            final Boolean forceSingleDelivery = onePhysicalDelivery.get(supplier);

            if (forceSingleDelivery != null && forceSingleDelivery && deliveryQty.get(supplier) > 1) {

                if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(group) &&
                        !CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP.equals(group) &&
                        !CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP.equals(group)) {

                    final DeliveryBucket mix = new DeliveryBucketImpl(CustomerOrderDelivery.MIX_DELIVERY_GROUP, supplier);

                    List<CartItem> collector = collectors.computeIfAbsent(mix, k -> new ArrayList<>());

                    removeBuckets.add(entry.getKey());
                    collector.addAll(entry.getValue());
                }

            }
        }

        deliveryGroups.putAll(collectors);
        deliveryGroups.keySet().removeAll(removeBuckets);

    }

    /**
     * Get the delivery group of product sku.
     * <p/>
     * All physical goods, that has not limitation by quantity or availability
     * will be collected into {@link CustomerOrderDelivery}#STANDARD_DELIVERY_GROUP
     *
     * @param item              sku + required quantity
     * @param warehouses        warehouses to consider
     *
     * @return Pair: delivery group label, warehouse.code
     */
    Pair<String, String> getDeliveryGroup(final CartItem item, final Map<String, Warehouse> warehouses) {

        final LocalDateTime now = now();

        final String sku = item.getProductSkuCode();
        final String name = item.getProductName();

        final ProductSku productSku = productService.getProductSkuByCode(sku);
        boolean digital = false;
        if (productSku != null) {
            final Product product = productSku.getProduct();
            digital = product.getProducttype().isDigital();
        }

        Warehouse supplier = null;

        // Specific supplier is chosen for this item (note blank string means default, so need to check foe empty)
        if (StringUtils.isNotBlank(item.getSupplierCode())) {
            supplier = warehouses.get(item.getSupplierCode());
        }

        // suppliers are either chosen one or we use all to "guess" one
        final Collection<Warehouse> suppliers = supplier == null ? warehouses.values() : Collections.singleton(supplier);

        Pair<String, String> lastError = null;

        for (final Warehouse warehouse : suppliers) {

            final SkuWarehouse inventory = inventoryResolver.findByWarehouseSku(warehouse, sku);

            if (inventory != null) { // we need inventory for inventory tracked items

                final int availability = inventory.getAvailability();
                final boolean isAvailableNow = availability != SkuWarehouse.AVAILABILITY_SHOWROOM && inventory.isAvailable(now);

                // Must not create orders with items that are unavailable
                if (!isAvailableNow) {
                    lastError = new Pair<>(
                            CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP,
                            warehouse.getCode()
                    );
                    continue;
                }

                // We do not track inventory for this item, so supplier is the same as stated on the item (could be null)
                if (digital) {
                    return new Pair<>(
                            CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP,
                            warehouse.getCode()
                    );
                }


                final boolean preorderNotYetAvailable = !inventory.isReleased(now);
                final boolean backorder = availability == SkuWarehouse.AVAILABILITY_BACKORDER;
                final boolean always = availability == SkuWarehouse.AVAILABILITY_ALWAYS;

                // preorders that are launched become standard later in the flow but for now we allow all orders
                if (preorderNotYetAvailable && (backorder || always)) {
                    return new Pair<>(
                            CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP,
                            warehouse.getCode()
                    );
                }

                final boolean inStock = inventory.isAvailableToSell(item.getQty(), false);

                if (inStock || always) {
                    // Enough quantity
                    return new Pair<>(
                            preorderNotYetAvailable ? CustomerOrderDelivery.DATE_WAIT_DELIVERY_GROUP : CustomerOrderDelivery.STANDARD_DELIVERY_GROUP,
                            warehouse.getCode()
                    );
                } else {
                    if (backorder) {
                        // Not-enough quantity but available on backorder
                        return new Pair<>(
                                CustomerOrderDelivery.INVENTORY_WAIT_DELIVERY_GROUP,
                                warehouse.getCode()
                        );
                    } else { // else we go to another warehouse but track last one
                        lastError = new Pair<>(
                                CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP,
                                warehouse.getCode()
                        );
                    }
                }

            }

        }

        if (lastError == null) {
            return new Pair<>(
                    CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP,
                    StringUtils.isNotBlank(item.getSupplierCode()) ? item.getSupplierCode() : ""
            );
        }
        return lastError;

    }


    /**
     * Return physical deliveries per supplier.
     *
     * @param deliveryMap map of delivery buckets
     *
     * @return map where key is supplier and value is physical delivery count
     */
    Map<String, Integer> getPhysicalDeliveriesQty(final Map<DeliveryBucket, List<CartItem>> deliveryMap) {
        final Map<String, Integer> counts = new HashMap<>();
        for (final DeliveryBucket bucket : deliveryMap.keySet()) {

            int delta = CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(bucket.getGroup()) ||
                    CustomerOrderDelivery.NOSTOCK_DELIVERY_GROUP.equals(bucket.getGroup()) ||
                    CustomerOrderDelivery.OFFLINE_DELIVERY_GROUP.equals(bucket.getGroup()) ? 0 : 1;

            counts.merge(bucket.getSupplier(), delta, (a, b) -> a + b);
        }
        return counts;
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Boolean> isMultipleDeliveriesAllowed(final long shopId, final Collection<CartItem> items) {

        // Pre-select single option to disable multi-delivery for non-supporting fulfilment centres
        final Map<String, Warehouse> warehouses = this.warehouseService.getByShopIdMapped(shopId, false);
        final Map<String, Boolean> single = new HashMap<>();
        single.put("", Boolean.TRUE); // By default only non stock items are in shop so it is single anyway TODO: remove this after YC-668
        for (final Map.Entry<String, Warehouse> warehouse : warehouses.entrySet()) {
            single.put(warehouse.getKey(), !warehouse.getValue().isMultipleShippingSupported());
        }

        // Determine buckets with default settings
        final Map<DeliveryBucket, List<CartItem>> deliveryGroups = determineDeliveryBuckets(shopId, items, single);
        final Map<String, Integer> countBySupplier = getPhysicalDeliveriesQty(deliveryGroups);
        final Map<String, Boolean> multipleBySupplier = new HashMap<>();
        for (final Map.Entry<String, Integer> entry : countBySupplier.entrySet()) {
            multipleBySupplier.put(entry.getKey(), entry.getValue() > 1);
        }
        return multipleBySupplier;

    }

    LocalDateTime now() {
        return TimeContext.getLocalDateTime();
    }

}
