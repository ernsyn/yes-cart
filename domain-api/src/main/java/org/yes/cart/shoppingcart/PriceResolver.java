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

package org.yes.cart.shoppingcart;

import org.yes.cart.domain.entity.SkuPrice;

import java.math.BigDecimal;
import java.util.List;

/**
 * User: denispavlov
 * Date: 09/07/2017
 * Time: 17:00
 */
public interface PriceResolver {


    /**
     * Get minimal price for given product skus (all), shop, currency and quantity.
     *
     *
     * @param productId      optional product to filter the prices. If null the price will be chosen by selectedSku.
     * @param selectedSku    optional sku to filter the prices. if null all product skus will be  considered to
     *                       determine minimal price
     * @param customerShopId shop for which to get the price for
     * @param masterShopId   optional fallback shop (if specified the result will be a merge of prices available in both shops)
     * @param currencyCode   desirable currency
     * @param quantity       quantity tier
     * @param enforceTier    force to pick closest tier price rather than cheapest
     * @param pricingPolicy  optional pricing policy
     * @param supplier       optional supplier specific
     *
     * @return lowest available sku price
     */
    SkuPrice getMinimalPrice(Long productId,
                             String selectedSku,
                             long customerShopId,
                             Long masterShopId,
                             String currencyCode,
                             BigDecimal quantity,
                             boolean enforceTier,
                             String pricingPolicy,
                             String supplier);

    /**
     * Get all prices for given product skus (all), shop, currency and quantity.
     *
     * @param productId      optional product to filter the prices. If null the price will be chosen by selectedSku.
     * @param selectedSku    optional sku to filter the prices. if null all product skus will be  considered to
     *                       determine minimal price
     * @param customerShopId shop for which to get the price for
     * @param masterShopId   optional fallback shop (if specified the result will be a merge of prices available in both shops)
     * @param currencyCode   desirable currency
     * @param pricingPolicy  optional pricing policy
     * @param supplier       optional supplier specific
     *
     * @return lowest available sku price
     */
    List<SkuPrice> getAllCurrentPrices(Long productId,
                                       String selectedSku,
                                       long customerShopId,
                                       Long masterShopId,
                                       String currencyCode,
                                       String pricingPolicy,
                                       String supplier);


}
