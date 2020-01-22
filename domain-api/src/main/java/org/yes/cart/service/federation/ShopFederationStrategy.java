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

package org.yes.cart.service.federation;

import org.yes.cart.domain.dto.ShopDTO;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:31
 */
public interface ShopFederationStrategy {

    /**
     * @return master access to all features
     */
    boolean isCurrentUserSystemAdmin();

    /**
     * @return access to specific role
     */
    boolean isCurrentUser(String role);

    /**
     * @param shopCode shop code
     *
     * @return true if current manager has access to this shop
     */
    boolean isShopAccessibleByCurrentManager(final String shopCode);

    /**
     * @param shopId shop PK
     *
     * @return true if current manager has access to this shop
     */
    boolean isShopAccessibleByCurrentManager(final Long shopId);

    /**
     * @param catalogCode supplier catalog code
     *
     * @return true if current manager has access to this catalog
     */
    boolean isSupplierCatalogAccessibleByCurrentManager(final String catalogCode);

    /**
     * @return set of PK's of shops to which current manager has access
     */
    Set<Long> getAccessibleShopIdsByCurrentManager();

    /**
     * @return set of shop codes to which current manager has access
     */
    Set<String> getAccessibleShopCodesByCurrentManager();

    /**
     * @return set fo supplier catalog codes that are accessible
     */
    Set<String> getAccessibleSupplierCatalogCodesByCurrentManager();

    /**
     * @return set of category catalog codes that are accessible
     */
    Set<String> getAccessibleCategoryCatalogCodesByCurrentManager();

    /**
     * @return set of immediate category catalog codes that are accessible
     */
    Set<Long> getAccessibleCatalogIdsByCurrentManager();

    /**
     * @param employeeId employee ID (email)
     *
     * @return true if current manager has access to this shop
     */
    boolean isEmployeeManageableByCurrentManager(final String employeeId);

}
