/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.*;

import java.util.List;

/**
 * User: denispavlov
 */
public interface VoCustomerService {

    /**
     * Get all customers in the system, filtered by criteria and according to rights, up to max
     *
     * @return list of customers
     *
     * @throws Exception errors
     */
    VoSearchResult<VoCustomerInfo> getFilteredCustomers(VoSearchContext filter) throws Exception;

    /**
     * Get customer by id.
     *
     * @param id customer id
     *
     * @return customer vo
     *
     * @throws Exception errors
     */
    VoCustomer getCustomerById(long id) throws Exception;

    /**
     * Update given customer.
     *
     * @param vo customer to update
     *
     * @return updated instance
     *
     * @throws Exception errors
     */
    VoCustomer updateCustomer(VoCustomer vo) throws Exception;

    /**
     * Create new customer
     *
     * @param vo given instance to persist
     *
     * @return persisted instance
     *
     * @throws Exception errors
     */
    VoCustomer createCustomer(VoCustomer vo) throws Exception;

    /**
     * Remove customer by id.
     *
     * @param id customer id
     *
     * @throws Exception errors
     */
    void removeCustomer(long id) throws Exception;


    /**
     * Get supported attributes by given customer
     *
     * @param customerId given customer id
     *
     * @return attributes
     *
     * @throws Exception errors
     */
    List<VoAttrValueCustomer> getCustomerAttributes(long customerId) throws Exception;


    /**
     * Update the customer attributes.
     *
     * @param vo customer attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     *
     * @return customer attributes.
     *
     * @throws Exception errors
     */
    List<VoAttrValueCustomer> updateCustomerAttributes(List<MutablePair<VoAttrValueCustomer, Boolean>> vo) throws Exception;

    /**
     * Reset password to given vo.
     *
     * @param customerId customerId
     *
     * @param shopId shopId
     *
     * @throws Exception errors
     */
    void resetPassword(long customerId, long shopId) throws Exception;


}
