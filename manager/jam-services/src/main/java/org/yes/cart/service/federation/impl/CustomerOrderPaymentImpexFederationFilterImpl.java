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

import org.yes.cart.payment.persistence.entity.CustomerOrderPayment;
import org.yes.cart.service.federation.FederationFilter;
import org.yes.cart.service.federation.ShopFederationStrategy;

import java.util.List;
import java.util.Set;

/**
 * User: denispavlov
 * Date: 16/09/2014
 * Time: 14:27
 */
public class CustomerOrderPaymentImpexFederationFilterImpl extends AbstractImpexFederationFilterImpl implements FederationFilter {

    public CustomerOrderPaymentImpexFederationFilterImpl(final ShopFederationStrategy shopFederationStrategy,
                                                         final List<String> roles) {
        super(shopFederationStrategy, roles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isManageable(final Object object, final Class objectType) {

        if (!hasAccessRole()) {
            return false;
        }

        final Set<String> manageableShopIds = shopFederationStrategy.getAccessibleShopCodesByCurrentManager();

        final CustomerOrderPayment payment = (CustomerOrderPayment) object;

        return manageableShopIds.contains(payment.getShopCode());

    }

}
