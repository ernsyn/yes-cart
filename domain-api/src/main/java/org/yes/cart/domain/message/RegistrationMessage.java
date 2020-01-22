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

package org.yes.cart.domain.message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Registration message. Used for user or customer notification in case of registration
 * or password reset
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface RegistrationMessage extends Serializable {

    /**
     * Email or the person registered.
     *
     * @return email
     */
    String getEmail();

    /**
     * Email or the person registered.
     *
     * @param email email
     */
    void setEmail(String email);

    /**
     * Registration shop PK.
     *
     * @return shop PK
     */
    long getShopId();

    /**
     * Registration shop PK.
     *
     * @param shopId shop PK
     */
    void setShopId(long shopId);

    /**
     * Locale used when registering.
     *
     * @return locale
     */
    String getLocale();

    /**
     * Locale used when registering.
     *
     * @param locale locale
     */
    void setLocale(String locale);

    /**
     * Password for new account.
     *
     * @return password
     */
    String getPassword();

    /**
     * Password for new account.
     *
     * @param password password
     */
    void setPassword(String password);

    /**
     * Authorisation token.
     *
     * @return auth token (for resetting passwords)
     */
    String getAuthToken();

    /**
     * Authorisation token.
     *
     * @param authToken auth token (for resetting passwords)
     */
    void setAuthToken(String authToken);

    /**
     * Get shop mail from addr.
     *
     * @return shop mail from addr.
     */
    String getShopMailFrom();

    /**
     * Set shop mail from addr.
     *
     * @param shopMailFrom shop mail from addr.
     */
    void setShopMailFrom(String shopMailFrom);


    /**
     * Get template name.
     * @return  template name.
     */
    String getTemplateName();

    /**
     * Set  template name.
     * @param templateName  template name.
     */
    void setTemplateName(String templateName);


    /**
     * Get path to template folder.
     * Example /some/path/shop/mailtemplates/ must hold folders with concrete templates
     *
     * @return path to template folder.
     */
    List<String> getMailTemplatePathChain();

    /**
     * Set path to template folder.
     *
     * @param mailTemplatePathChain  path to template folder.
     */
    void setMailTemplatePathChain(List<String> mailTemplatePathChain);


    /**
     * Get shop code.
     *
     * @return shop code
     */
    String getShopCode();

    /**
     * Set sho pcode.
     *
     * @param shopCode shop code.
     */
    void setShopCode(String shopCode);

    /**
     * Get shop name.
     *
     * @return sho pname.
     */
    String getShopName();

    /**
     * Set shop name.
     *
     * @param shopName shop name
     */
    void setShopName(String shopName);

    /**
     * Get shop urls.
     *
     * @return shop urls.
     */
    Set<String> getShopUrl();

    /**
     * Set shop urls.
     *
     * @param shopUrl shop HTTP urls.
     */
    void setShopUrl(Set<String> shopUrl);

    /**
     * Get shop urls.
     *
     * @return shop urls.
     */
    Set<String> getShopSecureUrl();

    /**
     * Set shop urls.
     *
     * @param shopSecureUrl shop HTTPS urls.
     */
    void setShopSecureUrl(Set<String> shopSecureUrl);

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
     * Set middle name
     *
     * @param middlename value to set
     */
    void setMiddlename(String middlename);

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
     * Additional data for registration message.
     *
     * @return additional data
     */
    Map<String, Object> getAdditionalData();

    /**
     * Additional data for registration message.
     *
     * @param additionalData additional data
     */
    void setAdditionalData(Map<String, Object> additionalData);

}
