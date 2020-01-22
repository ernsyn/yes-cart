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

package org.yes.cart.service.federation.impl;

import org.yes.cart.domain.dto.ContentDTO;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.Collection;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class ContentUiFederationFilterImpl implements FederationFilter {

    private final ShopFederationStrategy shopFederationStrategy;
    private final ShopService shopService;

    public ContentUiFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                         final ShopService shopService) {
        this.shopFederationStrategy = shopFederationStrategy;
        this.shopService = shopService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyFederationFilter(final Collection list, final Class objectType) {

        final Set<Long> manageableCategoryIds = getManageableContentIds();

        list.removeIf(cn -> !manageableCategoryIds.contains(((ContentDTO) cn).getContentId()));
    }

    Set<Long> getManageableContentIds() {
        final Set<Long> manageableShopIds = shopFederationStrategy.getAccessibleShopIdsByCurrentManager();
        return shopService.getShopsContentIds(manageableShopIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManageable(final Object object, final Class objectType) {

        final Set<Long> manageableContentIds = getManageableContentIds();

        return manageableContentIds.contains(object);
    }

}