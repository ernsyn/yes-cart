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

import java.io.Serializable;
import java.util.List;

/**
 * Responsible to hold shopping context data like viewed products and categories, security context, geo data and
 * other stuff
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 12-May-2011
 * Time: 10:31:36
 */
public interface ShoppingContext extends Serializable {

    /**
     * Get customer email.
     * @return customer email.
     */
    String getCustomerEmail();

    /**
     * Get customer name.
     *
     * @return customer name or null if customer is anonymous
     */
    String getCustomerName();

    /**
     * Get customer active shops.
     *
     * @return customer active shops
     */
    List<String> getCustomerShops();

    /**
     * Get current shop id
     *
     * @return current shop id.
     */
    long getShopId();

    /**
     * Get current shop code
     *
     * @return current shop id.
     */
    String getShopCode();

    /**
     * Get current customer shop id
     *
     * @return current customer shop id.
     */
    long getCustomerShopId();

    /**
     * Get current customer shop code
     *
     * @return current customer shop id.
     */
    String getCustomerShopCode();

    /**
     * Get current country code
     *
     * @return current country id.
     */
    String getCountryCode();

    /**
     * Get current country code
     *
     * @return current country id.
     */
    String getStateCode();

    /**
     * Get flag to indicate if tax info view change is enabled.
     *
     * @return true if tax info view change is enabled
     */
    boolean isTaxInfoChangeViewEnabled();

    /**
     * Get flag to indicate if tax info is enabled.
     *
     * @return true if tax info is enabled
     */
    boolean isTaxInfoEnabled();

    /**
     * Get flag to indicate to use net prices to display.
     *
     * @return true if net prices to be displayed
     */
    boolean isTaxInfoUseNet();

    /**
     * Get flag to indicate to display amount of tax.
     *
     * @return true if tax amount to be displayed
     */
    boolean isTaxInfoShowAmount();

    /**
     * Get flag to indicate to display prices.
     *
     * @return true if prices should not be displayed
     */
    boolean isHidePrices();

    /**
     * Flag to determine if given cart is managed by sales operative on behalf of the
     * customer.
     *
     * @return true if this is managed cart
     */
    boolean isManagedCart();

    /**
     * Email of the manager that manages this cart.
     *
     * @return email if cart is managed
     */
    String getManagerEmail();

    /**
     * Name of the manager that manages this cart.
     *
     * @return name if cart is managed
     */
    String getManagerName();

    /**
     * Get shopper ip address
     *
     * @return customer's IP
     */
    String getResolvedIp();


    /**
     * Get last viewed sku codes.
     * E.g. SKU001|FC01,SKU002|FC01,SKU003|FC02
     *
     * @return comma separated string of viewed skus and suppliers.
     */
    List<String> getLatestViewedSkus();


    /**
     * Get last viewed categories.
     *
     * TODO: YC-360 Track latest viewed categories in shopping context
     *
     * @return comma separated string of category ids.
     */
    List<String> getLatestViewedCategories();


}
