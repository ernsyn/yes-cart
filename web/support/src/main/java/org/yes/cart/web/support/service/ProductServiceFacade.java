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

package org.yes.cart.web.support.service;

import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.ShoppingCart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 20/11/2014
 * Time: 19:53
 */
public interface ProductServiceFacade {

    /**
     * Get SKU with attributes (better caching).
     *
     * @param skuId SKU
     *
     * @return SKU with attributes
     */
    ProductSku getSkuById(Long skuId);

    /**
     * Get Product with attributes (better caching).
     *
     * @param productId PK
     *
     * @return product with attributes
     */
    Product getProductById(Long productId);

    /**
     * Get product sku by code.
     *
     * @param skuCode given sku code.
     * @return product sku if found, otherwise null
     */
    ProductSku getProductSkuBySkuCode(String skuCode);


    /**
     * Get the grouped product attributes, with values. The result can be represented in following form:
     * Shipment details:
     * weight: 17 Kg
     * length: 15 Cm
     * height: 20 Cm
     * width: 35 Cm
     * Power:
     * Charger: 200/110
     * Battery type: Lithium
     *
     * So the hierarchy returned for the above example will be:
     * Map
     *    Entry[1001, Shipment details] =>
     *      Map
     *          Entry [10010, weight] =>
     *              List
     *                  [100001, 17 Kg]
     *          Entry [10011, length] =>
     *              List
     *                  [100002, 15 cm]
     *  ... etc
     *
     *  If this is SKU then it should inherit the attributes of the product,
     *  If this is just product then we only display product attributes
     *
     * @param locale locale
     * @param productId  product ID
     * @param skuId sku ID
     * @param productTypeId product type id
     *
     * @return hierarchy of attributes for this product or sku.
     */
    Map<Pair<String, String>, Map<Pair<String, String>, List<Pair<String, String>>>> getProductAttributes(String locale,
                                                                                                          long productId,
                                                                                                          long skuId,
                                                                                                          long productTypeId);

    /**
     * Get the grouped product attributes, with values. The result can be represented in following form:
     *
     *                   Prod A    SKU B    Prod C
     * Shipment details:
     * weight:            17 Kg    15kg      14kg
     * length:            15 Cm    15 Cm     15 Cm
     * height:            20 Cm    20 Cm     20 Cm
     * width:             35 Cm    35 Cm     35 Cm
     * Power:
     * Charger:           200/110            200/115
     * Battery type:      Lithium  Lithium
     *
     * So the hierarchy returned for the above example will be:
     * Map
     *    Entry[1001, Shipment details] =>
     *      Map
     *          Entry [10010, weight] =>
     *               Map
     *                  Entry[ p_10001 =>
     *                      List
     *                         [100001, 17 Kg]
     *                  ]
     *                  Entry[ s_10001 =>
     *                      List
     *                         [100001, 15 Kg]
     *                  ]
     *                  Entry[ p_10002 =>
     *                      List
     *                         [100001, 14 Kg]
     *                  ]
     *  ... etc
     *
     *  If this is SKU then it should inherit the attributes of the product,
     *  If this is just product then we only display product attributes
     *
     * @param locale locale
     * @param productId  product ID
     * @param skuId sku ID
     *              
     * @return hierarchy of attributes for this product or sku.
     */
    Map<Pair<String, String>, Map<Pair<String, String>, Map<String, List<Pair<String, String>>>>> getCompareAttributes(String locale,
                                                                                                                       List<Long> productId,
                                                                                                                       List<Long> skuId);


    /**
     * Get all product associations by association type.
     *
     * @param productId       product primary key
     * @param shopId          current shop
     * @param customerShopId  current customer shop
     * @param associationType association code [up, cross, etc]
     *
     * @return list of product associations
     */
    List<ProductSearchResultDTO> getProductAssociations(long productId, long shopId, final long customerShopId, String associationType);

    /**
     * Get featured products for given category. Limit is set by the category.
     *
     * @param categoryId      category (optional)
     * @param shopId          current shop
     * @param customerShopId  current customer shop
     *
     * @return list of featured products
     */
    List<ProductSearchResultDTO> getFeaturedProducts(long categoryId,
                                                     long shopId,
                                                     long customerShopId);

    /**
     * Get new products for given category. Limit is set by the category.
     *
     * @param categoryId      category (optional)
     * @param shopId          current shop
     * @param customerShopId  current customer shop
     *
     * @return list of new products
     */
    List<ProductSearchResultDTO> getNewProducts(long categoryId,
                                                long shopId,
                                                long customerShopId);

    /**
     * Get new products for given category. Limit is set by the category.
     *
     * @param categoryId      category (optional)
     * @param shopId          current shop
     * @param customerShopId  current customer shop
     * @param tag             tag
     *
     * @return list of new products
     */
    List<ProductSearchResultDTO> getTaggedProducts(long categoryId,
                                                   long shopId,
                                                   long customerShopId,
                                                   String tag);

    /**
     * Get new products for given category. Limit is set by the category.
     *
     * @param SKUs            list of products
     * @param categoryId      category (optional), specify -1 for no limit
     * @param shopId          current shop
     * @param customerShopId  current customer shop
     *
     * @return list of new products
     */
    List<ProductSearchResultDTO> getListProductSKUs(List<String> SKUs,
                                                    long categoryId,
                                                    long shopId,
                                                    long customerShopId);

    /**
     * Get new products for given category. Limit is set by the category.
     *
     * @param productIds      list of products
     * @param categoryId      category (optional), specify -1 for no limit
     * @param shopId          current shop
     * @param customerShopId  current customer shop
     *
     * @return list of new products
     */
    List<ProductSearchResultDTO> getListProducts(List<String> productIds,
                                                 long categoryId,
                                                 long shopId,
                                                 long customerShopId);

    /**
     * Get the all products , that match the given query
     *
     * @param context       navigation context
     * @param firstResult   index of first result
     * @param maxResults    quantity results to return
     * @param sortFieldName sort field name (specify null for no sorting)
     * @param descendingSort sort the search result in reverse if true
     * @return list of products
     */
    ProductSearchResultPageDTO getListProducts(NavigationContext context,
                                               int firstResult,
                                               int maxResults,
                                               String sortFieldName,
                                               boolean descendingSort);

    /**
     * Get product availability.
     *
     * @param product           product
     * @param customerShopId    current shop
     *
     * @return availability model
     */
    ProductAvailabilityModel getProductAvailability(ProductSearchResultDTO product,
                                                    long customerShopId);

    /**
     * Get product availability.
     *
     * @param sku               product
     * @param customerShopId    current shop
     *
     * @return availability model
     */
    ProductAvailabilityModel getProductAvailability(ProductSkuSearchResultDTO sku,
                                                    long customerShopId);

    /**
     * Get product availability.
     *
     * @param product           product
     * @param customerShopId    current shop
     * @param supplier          supplier
     *
     * @return availability model
     */
    ProductAvailabilityModel getProductAvailability(Product product,
                                                    long customerShopId,
                                                    String supplier);

    /**
     * Get product availability.
     *
     * @param product           product
     * @param customerShopId    current shop
     * @param supplier          supplier
     *
     * @return availability model
     */
    ProductAvailabilityModel getProductAvailability(ProductSku product,
                                                    long customerShopId,
                                                    String supplier);

    /**
     * Get product availability.
     *
     * @param skuCode           SKU code (not product code)
     * @param customerShopId    current shop
     * @param supplier          supplier
     *
     * @return availability model
     */
    ProductAvailabilityModel getProductAvailability(String skuCode,
                                                    long customerShopId,
                                                    String supplier);

    /**
     * Quantity model.
     *
     * @param cartQty quantity of given sku in cart
     * @param product required product
     * @param customerShopId    current shop
     * @param supplier          supplier
     *
     * @return quantity model
     */
    QuantityModel getProductQuantity(BigDecimal cartQty,
                                     Product product,
                                     long customerShopId,
                                     String supplier);

    /**
     * Quantity model.
     *
     * @param cartQty           quantity of given sku in cart
     * @param product           required product
     * @param customerShopId    current shop
     * @param supplier          supplier
     *
     * @return quantity model
     */
    QuantityModel getProductQuantity(BigDecimal cartQty,
                                     ProductSku product,
                                     long customerShopId,
                                     String supplier);

    /**
     * Quantity model.
     *
     * @param cartQty           quantity of given sku in cart
     * @param product           required product
     * @param customerShopId    current shop
     *
     * @return quantity model
     */
    QuantityModel getProductQuantity(BigDecimal cartQty,
                                     ProductSearchResultDTO product,
                                     long customerShopId);

    /**
     * Quantity model.
     *
     * @param cartQty           quantity of given sku in cart
     * @param sku           required product
     * @param customerShopId    current shop
     *
     * @return quantity model
     */
    QuantityModel getProductQuantity(BigDecimal cartQty,
                                     ProductSkuSearchResultDTO sku,
                                     long customerShopId);



    /**
     * Quantity model.
     *
     * @param cartQty           quantity of given sku in cart
     * @param sku               required product SKU
     * @param supplier          supplier
     * @param customerShopId    shop PK
     *
     * @return quantity model
     */
    QuantityModel getProductQuantity(BigDecimal cartQty,
                                     String sku,
                                     String supplier,
                                     long customerShopId);


    /**
     * Quantity model.
     *
     * @param cartQty           quantity of given sku in cart
     * @param supplier          supplier
     * @param skuCode           SKU code
     * @param min               min order quantity
     * @param max               max order quantity
     * @param step              step order quantity
     * @param customerShopId    current shop
     *
     * @return quantity model
     */
    QuantityModel getProductQuantity(BigDecimal cartQty,
                                     String skuCode,
                                     String supplier,
                                     BigDecimal min,
                                     BigDecimal max,
                                     BigDecimal step,
                                     long customerShopId);

    /**
     * Get price model (or blank object) with respect to current shop tax display settings.
     *
     * If tax info is enabled then prices can shown as net or gross.
     *
     * @param cart      current cart
     * @param item      item to create price model for
     *
     * @return price (or blank object)
     */
    Pair<PriceModel, CustomerWishList.PriceChange> getSkuPrice(ShoppingCart cart,
                                                               CustomerWishList item);


    /**
     * Get currently active SKU price (or blank object) with respect to current shop tax display settings.
     *
     * If tax info is enabled then prices can shown as net or gross.
     *
     * @param cart      current cart
     * @param productId product id (optional)
     * @param skuCode   selected SKU (optional)
     * @param quantity  quantity tier
     * @param supplier  supplier
     *
     * @return active product/SKU price (or blank object)
     */
    PriceModel getSkuPrice(ShoppingCart cart,
                           Long productId,
                           String skuCode,
                           BigDecimal quantity,
                           String supplier);

    /**
     * Get price model (or blank object) with respect to current shop tax display settings.
     *
     * If tax info is enabled then prices can shown as net or gross.
     *
     * @param cart      current cart
     * @param ref       reference (selected SKU, shipping or label)
     * @param quantity  quantity tier
     * @param listPrice base list price
     * @param salePrice base sale price
     * @param supplier  supplier
     *
     * @return price (or blank object)
     */
    PriceModel getSkuPrice(ShoppingCart cart,
                           String ref,
                           BigDecimal quantity,
                           BigDecimal listPrice,
                           BigDecimal salePrice,
                           String supplier);

    /**
     * Get price model (or blank object) with respect to current shop tax display settings.
     *
     * If tax info is enabled then prices can shown as net or gross.
     *
     * @param cart      current cart
     * @param item      item to create price model for
     * @param total     true indicates that we what total amount, false indicates list and sale price
     *
     * @return price (or blank object)
     */
    PriceModel getSkuPrice(ShoppingCart cart,
                           CartItem item,
                           boolean total);

    /**
     * Get price model (or blank object) with respect to current shop tax display settings. (used by YCE)
     *
     * If tax info is enabled then prices can shown as net or gross.
     *
     * @param currency  item currency
     * @param showTax   show tax
     * @param showTaxNet use net price
     * @param showTaxAmount use tax amount
     * @param item      item to create price model for
     * @param total     true indicates that we what total amount, false indicates list and sale price
     * @param hide      true mean no price should be displayed
     *
     * @return price (or blank object)
     */
    PriceModel getSkuPrice(String currency,
                           boolean showTax,
                           boolean showTaxNet,
                           boolean showTaxAmount,
                           CartItem item,
                           boolean total,
                           final boolean hide);

    /**
     * Get prices for all SKU quantity tiers sorted by tier.
     *
     * @param cart      current cart
     * @param productId product id (optional)
     * @param skuCode   selected SKU (optional)
     * @param supplier  supplier
     *
     * @return active product/SKU prices (or blank object)
     */
    List<PriceModel> getSkuPrices(ShoppingCart cart,
                                  Long productId,
                                  String skuCode,
                                  String supplier);


    /**
     * Get cart total price model (or blank object) with respect to current shop tax display settings.
     *
     * If tax info is enabled then prices can be shown as net or gross.
     *
     * @param cart      current cart
     *
     * @return price (or blank object)
     */
    PriceModel getCartItemsTotal(ShoppingCart cart);

    /**
     * Generate promotion model map from given applied promotion codes.
     *
     * @return map code to model in order specified by applied promo
     */
    Map<String, PromotionModel> getPromotionModel(String appliedPromo);

    /**
     * Brand are lazily initialised. This method allows to retrieve brand by name and cache it for later use.
     *
     * @param brandId brand PK
     *
     * @return brand
     */
    Brand getBrandById(long brandId);

}
