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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.util.Map;
import java.util.Set;

/**
 * Customer DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface CustomerDTO extends Identifiable {

    /**
     * Get person id.
     *
     * @return customer email.
     */
    String getEmail();

    /**
     * Set customer email
     *
     * @param email email
     */
    void setEmail(String email);

    /**
     * Get first name.
     *
     * @return first name
     */
    String getFirstname();

    /**
     * Set first name
     *
     * @param firstname value to set
     */
    void setFirstname(String firstname);

    /**
     * Get last name.
     *
     * @return last name
     */
    String getLastname();

    /**
     * Set last name
     *
     * @param lastname value to set
     */
    void setLastname(String lastname);

    /**
     * Get middle name
     *
     * @return middle name
     */
    String getMiddlename();

    /**
     * Set middle name
     *
     * @param middlename value to set
     */
    void setMiddlename(String middlename);


    /**
     * Get salutation
     *
     * @return salutation
     */
    String getSalutation();

    /**
     * Set salutation
     *
     * @param salutation value to set
     */
    void setSalutation(String salutation);


    /**
     * Get primary key.
     *
     * @return pk value.
     */
    long getCustomerId();

    /**
     * Set pk value.
     *
     * @param customerId pk value to set
     */
    void setCustomerId(long customerId);

    /**
     * Get attributes.
     *
     * @return list of attributes
     */
    Set<AttrValueCustomerDTO> getAttributes();

    /**
     * Set list of attributes.
     *
     * @param attribute list of attributes
     */
    void setAttributes(Set<AttrValueCustomerDTO> attribute);

    /**
     * Get customer tags.
     *
     * @return tag line.
     */
    String getTag();

    /**
     * Set customer tag.
     *
     * @param tag customer tag line
     */
    void setTag(String tag);


    /**
     * Get customer type.
     *
     * @return customer type
     */
    String getCustomerType();

    /**
     * Set customer type.
     *
     * @param customerType customer type
     */
    void setCustomerType(String customerType);


    /**
     * Get price type. Price type defines customer segment that has access to this price.
     *
     * @return price type
     */
    String getPricingPolicy();

    /**
     * Set price type. Price type defines customer segment that has access to this price.
     *
     * @param priceType price type
     */
    void setPricingPolicy(String priceType);


    /**
     * Company name for this person.
     *
     * @return company name
     */
    String getCompanyName1();

    /**
     * Company name for this person.
     *
     * @param companyName1 name
     */
    void setCompanyName1(String companyName1);

    /**
     * Company name for this person.
     *
     * @return company name
     */
    String getCompanyName2();

    /**
     * Company name for this person.
     *
     * @param companyName2 name
     */
    void setCompanyName2(String companyName2);


    /**
     * Company department for this person.
     *
     * @return company department
     */
    String getCompanyDepartment();

    /**
     * Company department for this person.
     *
     * @param companyDepartment department
     */
    void setCompanyDepartment(String companyDepartment);

    /**
     * Shop Assignments.
     *
     * @return shop map
     */
    Map<Long, Boolean> getAssignedShops();

    /**
     * Shop assignments.
     *
     * @param assignedShops assignments
     */
    void setAssignedShops(Map<Long, Boolean> assignedShops);

}
