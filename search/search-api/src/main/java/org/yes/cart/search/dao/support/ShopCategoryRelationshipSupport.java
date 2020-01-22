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

package org.yes.cart.search.dao.support;

import org.yes.cart.domain.dto.CategoryRelationDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 13-10-01
 * Time: 1:52 PM
 */
public interface ShopCategoryRelationshipSupport {

    /**
     * Get all entities.
     *
     * @return list of all entities
     */
    List<Shop> getAll();


    /**
     * Get all categories with their corresponding child categories.
     *
     * @return mapped level representation of category tree
     */
    Map<Long, Set<Long>> getAllCategoriesIdsMap();


    /**
     * Get all categories including child categories, that belong to given shop.
     *
     * @param shopId given shop PK
     *
     * @return linear representation of category tree
     */
    Set<Long> getShopCategoriesIds(long shopId);

    /**
     * Get all categories including child categories, that belong to given category branch.
     *
     * @param branchId branch of master catalog to scan
     *
     * @return linear representation of category tree
     */
    Set<Long> getCatalogCategoriesIds(long branchId);

    /**
     * Get category by id.
     *
     * @param categoryId given category id
     *
     * @return category
     */
    Category getCategoryById(long categoryId);

    /**
     * Get category by id.
     *
     * @param categoryId given category id
     * 
     * @return category
     */
    CategoryRelationDTO getCategoryRelationById(long categoryId);


    /**
     * Get all category parents including links.
     *
     * @param categoryId given category PK
     *
     * @return immediate parents
     */
    Set<Long> getCategoryParentsIds(long categoryId);

    /**
     * Get all category links.
     *
     * @param categoryId given category PK
     *
     * @return immediate links
     */
    Set<Long> getCategoryLinkedIds(long categoryId);

}
