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

package org.yes.cart.service.domain;

import org.yes.cart.domain.entity.AttrValueCustomer;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface CustomerService extends GenericService<Customer> {


    /**
     * Find customer by given search criteria. Search will be performed using like operation.
     *
     * @param start             start
     * @param offset            page size
     * @param sort              optional sort property
     * @param sortDescending    optional sort property direction
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return list of persons, that match search criteria or empty list if nobody found or null if no search criteria provided.
     */
    List<Customer> findCustomers(int start,
                                 int offset,
                                 String sort,
                                 boolean sortDescending,
                                 Map<String, List> filter);

    /**
     * Find customer by given search criteria. Search will be performed using like operation.
     *
     * @param filter            optional filters (e.g. firstname, lastname)
     *
     * @return count
     */
    int findCustomerCount(Map<String, List> filter);


    /**
     * Get customer by email.
     *
     * @param email email
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByEmail(String email, Shop shop);

    /**
     * Get customer by auth token.
     *
     * @param token auth token
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByToken(String token);

    /**
     * Get customer by public key exact match.
     *
     * @param publicKey public key
     * @param lastName last name
     *
     * @return {@link Customer} or null if customer not found
     */
    Customer getCustomerByPublicKey(String publicKey, String lastName);

    /**
     * Get customer shops by email.
     *
     * @param customer customer
     * @return List of {@link Shop} or null if customer not found
     */
    List<Shop> getCustomerShops(Customer customer);

    /**
     * Get name as specified by shop name formatting.
     *
     * @param customer customer
     * @param shop shop
     *
     * @return name
     */
    String formatNameFor(Customer customer, Shop shop);

    /**
     * Check is customer already registered.
     *
     * @param email email to check
     * @param shop shop
     *
     * @return true in case if email unique.
     */
    boolean isCustomerExists(String email, Shop shop);

    /**
     * Check is provided password for customer valid.
     *
     * @param email    email to check
     * @param shop     shop
     * @param password password
     * @return true in case if email unique.
     */
    boolean isPasswordValid(String email, Shop shop, String password);

    /**
     * Reset password to given user and send generated password via email.
     *
     * @param customer customer to create
     * @param shop     shop to assign
     * @param authToken authentication token for password reset
     */
    void resetPassword(Customer customer, Shop shop, String authToken);

    /**
     * Update password to given user and send generated password via email.
     *
     * @param customer customer to create
     * @param shop     shop to assign
     * @param newPassword new password
     */
    void updatePassword(Customer customer, Shop shop, String newPassword);


    /**
     * Create customer and assign it to particular shop
     *
     * @param customer customer to create
     * @param shop     shop to assign
     * @return customer instance
     */
    Customer create(Customer customer, Shop shop);


    /**
     * Activate account for a particular shop
     *
     * @param customer customer to create
     * @param shop     shop to assign
     * @param soft     keep assignment disabled
     * @return customer instance
     */
    Customer updateActivate(Customer customer, Shop shop, boolean soft);

    /**
     * De-activate account for a particular shop
     *
     * @param customer customer to create
     * @param shop     shop to assign
     * @param soft     keep assignment disabled
     * @return customer instance
     */
    Customer updateDeactivate(Customer customer, Shop shop, boolean soft);


    /**
     * Update customer and assign to particular shop
     *
     * @param email customer to update
     * @param shopCode shop to assign
     * @return customer instance
     */
    Customer update(String email, String shopCode);


    /**
     * Get sorted by attribute rank collection of customer attributes.
     * Not all customers attributes can be filled out new attributes can
     * be added, so the result list contains filled values and
     * possible values to fill.
     *
     * @param customer customer
     * @return sorted by attribute.
     */
    List<AttrValueCustomer> getRankedAttributeValues(Customer customer);

}
