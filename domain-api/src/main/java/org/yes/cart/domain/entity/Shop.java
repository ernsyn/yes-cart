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

package org.yes.cart.domain.entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Shop.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */

public interface Shop extends Auditable, Attributable, Seoable, Codable {

    /**
     * Get shop code.
     *
     * @return shop code.
     */
    @Override
    String getCode();

    /**
     * Set shop code.
     *
     * @param code shop code.
     */
    @Override
    void setCode(String code);


    /**
     * Get pk value.
     *
     * @return pk value.
     */
    long getShopId();

    /**
     * Set pk value.
     *
     * @param shopId shop pk value.
     */
    void setShopId(long shopId);

    /**
     * Master for this shop.
     *
     * @return master shop
     */
    Shop getMaster();

    /**
     * Master for this shop.
     *
     * @param master master shop
     */
    void setMaster(Shop master);

    /**
     * Get shop name.
     *
     * @return shop name.
     */
    @Override
    String getName();

    /**
     * Set shop name.
     *
     * @param name shop name.
     */
    @Override
    void setName(String name);

    /**
     * Get shop description.
     *
     * @return description.
     */
    @Override
    String getDescription();

    /**
     * Set shop description.
     *
     * @param description shop description
     */
    @Override
    void setDescription(String description);

    /**
     * Get the path to shop theme. Default failover path will be used,
     * if some resource can not be found in shop theme.
     *
     * @return path to shop theme.
     */
    String getFspointer();

    /**
     * Set shop theme path.
     *
     * @param fspointer path to theme.
     */
    void setFspointer(String fspointer);

    /**
     * Get shop supported urls. Example shop.domain, www.shop.domain, www1.shop.domain, wap.shop.domain.
     *
     * @return list of supported urls.
     */
    Set<ShopUrl> getShopUrl();

    /**
     * Set list of supported urls.
     *
     * @param shopUrl supported urls.
     */
    void setShopUrl(Set<ShopUrl> shopUrl);


    /**
     * Get shop aliases.
     *
     * @return list of aliases.
     */
    Set<ShopAlias> getShopAlias();

    /**
     * Set list of aliases.
     *
     * @param shopAlias aliases.
     */
    void setShopAlias(Set<ShopAlias> shopAlias);


    /**
     * Get the named advertising places.
     *
     * @return named advertising places.
     */
    Collection<ShopAdvPlace> getAdvertisingPlaces();

    /**
     * Set named advertising places.
     *
     * @param advertisingPlaces named advertising places.
     */
    void setAdvertisingPlaces(Collection<ShopAdvPlace> advertisingPlaces);

    /**
     * Get all  attributes.
     *
     * @return collection of product attributes.
     */
    Collection<AttrValueShop> getAttributes();

    /**
     * Get all  attributes filtered by given attribute code.
     *
     * @param attributeCode code of attribute
     * @return collection of product attributes filtered by attribute name or empty collection if no attribute were found.
     */
    @Override
    Collection<AttrValueShop> getAttributesByCode(String attributeCode);

    /**
     * Is B2B profile set. B2B profile is used for setup of shop that contains sub shops. Each of the
     * sub shops is considered a B2B organisation with its own settings for inventory, catalog and pricing.
     *
     * @return true if B2B profile is set.
     */
    boolean isB2BProfileActive();

    /**
     * Is B2B address book enforced. Enforces single address book for all customers registered under B2B
     * account, customer only have read access.
     *
     * @return true if B2B address mode is set.
     */
    boolean isB2BAddressBookActive();

    /**
     * Is B2B strict pricing is enforced. By default B2B can use its own price settings as well as have
     * fallback to the master shop. Strict mode disabled fallback to master prices.
     *
     * @return true is B2B profile is set.
     */
    boolean isB2BStrictPriceActive();

    /**
     * Is page caching on.
     *
     * @return true if tracing is enabled set.
     */
    boolean isSfPageTraceOn();

    /**
     * Flag to indicate that this shop requires customer to be always authenticated to see any information.
     * If this returns true, this means that only login/registration pages should be available for anonymous access on SF.
     *
     * @return true if customers must be authenticated to browse store
     */
    boolean isSfRequireCustomerLogin();

    /**
     * Flag to indicate that this shop requires new customer registration to be approved by shop admin.
     *
     * @param customerType type of this registration
     *
     * @return true if this type of registration requires approval
     */
    boolean isSfRequireCustomerRegistrationApproval(String customerType);

    /**
     * Flag to indicate that this shop requires new customer registration notification to shop admin.
     *
     * @param customerType type of this registration
     *
     * @return true if this type of registration requires notification
     */
    boolean isSfRequireCustomerRegistrationNotification(String customerType);

    /**
     * Flag to indicate that this shop requires approval for new customer orders.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer requires approval
     */
    boolean isSfRequireCustomerOrderApproval(String customerType);

    /**
     * Flag to indicate that this shop prevents new customer orders for given types.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer cannot place
     */
    boolean isSfBlockCustomerCheckout(String customerType);

    /**
     * Flag to indicate that this shop allows repeat orders feature.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer can repeat order
     */
    boolean isSfRepeatOrdersEnabled(String customerType);

    /**
     * Flag to indicate that this shop allows shopping lists feature.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer can create shopping lists
     */
    boolean isSfShoppingListsEnabled(String customerType);

    /**
     * Flag to indicate that this shop allows managed lists feature.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer can use managed lists
     */
    boolean isSfManagedListsEnabled(String customerType);

    /**
     * Flag to indicate that this shop allows per line remarks.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer can add per line remarks
     */
    boolean isSfB2BOrderLineRemarksEnabled(String customerType);

    /**
     * Flag to indicate that this shop allows B2B form to be populated.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer can add information to B2B order form
     */
    boolean isSfB2BOrderFormEnabled(String customerType);

    /**
     * Flag to indicate that this shop allows RFQ.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer can create RFQ
     */
    boolean isSfRFQEnabled(String customerType);

    /**
     * Flag to indicate that this shop allows coupon codes.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer can add coupon codes
     */
    boolean isSfPromoCouponsEnabled(String customerType);

    /**
     * Flag to indicate that this shop allows order message.
     *
     * @param customerType type of customer
     *
     * @return true if this type of customer can add order message
     */
    boolean isSfOrderMessageEnabled(String customerType);


    /**
     * Determine if address book management is enabled for customer type.
     *
     * @param customerType type of customer
     *
     * @return true if B2B address mode is set.
     */
    boolean isSfAddressBookEnabled(String customerType);


    /**
     * Determine if address book management for billing address is enabled for customer type.
     *
     * @param customerType type of customer
     *
     * @return true if B2B address mode is set.
     */
    boolean isSfAddressBookBillingEnabled(String customerType);

    /**
     * Flag whether to display tax information for given customer type.
     *
     * @param customerType  type of customer
     *
     * @return true if tax information should be shown
     */
    boolean isSfShowTaxInfo(String customerType);

    /**
     * Flag whether to display NET (as opposed to GROSS) prices for given customer type.
     *
     * @param customerType  type of customer
     *
     * @return true if prices are NET (without tax), false if the prices are GROSS (include tax)
     */
    boolean isSfShowTaxNet(String customerType);

    /**
     * Flag whether to display percentage or actual tax amount for given customer type.
     *
     * @param customerType  type of customer
     *
     * @return true if tax is displayed as amount, false if tax is displayed as percentage
     */
    boolean isSfShowTaxAmount(String customerType);

    /**
     * Flag whether to allow changing price view options for given customer type.
     *
     * @param customerType  type of customer
     *
     * @return true customer can select various price views (mostly in B2B, e.g. show with/without tax), false if only display one default view (default)
     */
    boolean isSfShowTaxOptions(String customerType);

    /**
     * Flag whether to disable same address checkbox option for given customer type.
     *
     * @param customerType  type of customer
     *
     * @return true customer can see "billing same as shipping" checkbox
     */
    boolean isSfShowSameBillingAddressDisabledTypes(String customerType);

    /**
     * Flag whether to disable delete account option for given customer type.
     *
     * @param customerType  type of customer
     *
     * @return true customer can see "delete account" section
     */
    boolean isSfDeleteAccountDisabled(String customerType);

    /**
     * Flag whether to hide all prices for given customer type.
     *
     * @param customerType  type of customer
     *
     * @return true customer cannot see any prices
     */
    boolean isSfHidePricesTypes(String customerType);

    /**
     * Enable login for managers with ROLE_SMCALLCENTERLOGINSF role.
     *
     * @return set of roles
     */
    Boolean isSfManagersLoginEnabled();

    /**
     * Flag to denote if shop is disabled on not.
     *
     * @return true if shop is disabled
     */
    boolean isDisabled();

    /**
     * Flag to denote if shop is disabled on not.
     *
     * @param disabled true if shop is disabled
     */
    void setDisabled(boolean disabled);

    /**
     * Get default shop HTTP url. localhost will never be return.
     * @return  default shop url
     */
    String getDefaultShopUrl();

    /**
     * Get default shop HTTPS url. localhost will never be return.
     * @return  default shop url
     */
    String getDefaultShopSecureUrl();


    /**
     * Get single attribute.
     *
     * @param attributeCode code of attribute
     * @return single {@link AttrValue} or null if not found.
     */
    @Override
    AttrValueShop getAttributeByCode(String attributeCode);


    /**
     * Set collection of  attributes.
     *
     * @param attribute collection of attributes
     */
    void setAttributes(Collection<AttrValueShop> attribute);



    /**
     * Get categories, that assigned to shop.
     *
     * @return categories, that assigned to shop.
     */
    Collection<ShopCategory> getShopCategory();

    /**
     * Set categories, that assigned to shop.
     *
     * @param shopCategory categories, that assigned to shop.
     */
    void setShopCategory(Collection<ShopCategory> shopCategory);

    /**
     * Get the default shop currency.
     *
     * @return default currency code or null is there no settings.
     */
    String getDefaultCurrency();

    /**
     * Get all supported currencies. First in list is default.
     *
     * @return separated by comma all supported currencies by shop or null if not set.
     */
    String getSupportedCurrencies();

    /**
     * Get all supported currencies. First in list is default.
     *
     * @return list of currency codes.
     */
    List<String> getSupportedCurrenciesAsList();

    /**
     * List of supported shipping countries.
     *
     * @return list of country codes
     */
    String getSupportedShippingCountries();

    /**
     * List of supported shipping countries.
     *
     * @return list of country codes
     */
    List<String> getSupportedShippingCountriesAsList();

    /**
     * List of supported billing countries.
     *
     * @return  list of country codes
     */
    String getSupportedBillingCountries();

    /**
     * List of supported billing countries.
     *
     * @return  list of country codes
     */
    List<String> getSupportedBillingCountriesAsList();

    /**
     * List of supported language codes.
     *
     * @return  list of language codes
     */
    String getSupportedLanguages();

    /**
     * List of supported language code.
     *
     * @return  list of language codes
     */
    List<String> getSupportedLanguagesAsList();

    /**
     * Get address formatting per country per locale.
     *
     * @param countryCode  country for which to provide address format
     * @param locale       language for which to provide address format
     * @param customerType (optional) customer type
     * @param addressType  address type ({@link Address#getAddressType()})
     *
     * @return format
     */
    String getAddressFormatByCountryAndLocaleAndCustomerTypeAndAddressType(String countryCode, String locale, String customerType, final String addressType);

    /**
     * Get all supported customer attributes.
     *
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return separated by comma all supported attributes.
     */
    String getSupportedRegistrationFormAttributes(String customerType);

    /**
     * Get all supported customer attributes.
     *
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return list of attribute codes.
     */
    List<String> getSupportedRegistrationFormAttributesAsList(String customerType);

    /**
     * Get all supported customer attributes.
     *
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return separated by comma all supported attributes.
     */
    String getSupportedProfileFormAttributes(String customerType);

    /**
     * Get all supported customer attributes.
     *
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return list of attribute codes.
     */
    List<String> getSupportedProfileFormAttributesAsList(String customerType);

    /**
     * Get all supported customer attributes.
     *
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return separated by comma all supported attributes.
     */
    String getSupportedProfileFormReadOnlyAttributes(String customerType);

    /**
     * Get all supported customer attributes.
     *
     * @param customerType customer type {@link Customer#getCustomerType()}
     *
     * @return list of attribute codes.
     */
    List<String> getSupportedProfileFormReadOnlyAttributesAsList(String customerType);

    /**
     * Get all product attributes that need to be copied to order line.
     *
     * @return CSV list of attribute codes.
     */
    String getProductStoredAttributes();


    /**
     * Get all product attributes that need to be copied to order line.
     *
     * @return list of attribute codes.
     */
    List<String> getProductStoredAttributesAsList();


    /**
     * Get CSV of PKs for disabled carrier SLA for given shop.
     *
     * @return CSV of carrier SLA PKs
     */
    String getDisabledCarrierSla();

    /**
     * Get CSV of PKs for disabled carrier SLA for given shop.
     *
     * @return carrier SLA PKs
     */
    Set<Long> getDisabledCarrierSlaAsSet();

    /**
     * Get properties of PKs and ranks for carrier SLA for given shop.
     *
     * @return Properties of pk=rank
     */
    String getSupportedCarrierSlaRanks();

    /**
     * Get properties of PKs and ranks for carrier SLA for given shop.
     *
     * @return map of pk=rank
     */
    Map<Long, Integer> getSupportedCarrierSlaRanksAsMap();



}


