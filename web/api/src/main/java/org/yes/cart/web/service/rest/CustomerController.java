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

package org.yes.cart.web.service.rest;

import io.swagger.annotations.Api;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.util.ListHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.domain.i18n.impl.FailoverStringI18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.ro.*;
import org.yes.cart.service.domain.CustomerOrderComparator;
import org.yes.cart.service.domain.CustomerOrderService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.RegExUtils;
import org.yes.cart.utils.TimeContext;
import org.yes.cart.web.service.rest.impl.AddressSupportMixin;
import org.yes.cart.web.service.rest.impl.CartMixin;
import org.yes.cart.web.service.rest.impl.RoMappingMixin;
import org.yes.cart.web.service.rest.impl.SearchSupportMixin;
import org.yes.cart.web.support.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 30/03/2015
 * Time: 07:52
 */
@Controller
@Api(value = "Customer", tags = "customer")
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerServiceFacade customerServiceFacade;

    @Autowired
    private AddressBookFacade addressBookFacade;

    @Autowired
    private ProductServiceFacade productServiceFacade;

    @Autowired
    private CurrencySymbolService currencySymbolService;

    @Autowired
    private CustomerOrderService customerOrderService;

    @Autowired
    private CheckoutServiceFacade checkoutServiceFacade;


    @Autowired
    private CartMixin cartMixin;
    @Autowired
    @Qualifier("restRoMappingMixin")
    private RoMappingMixin mappingMixin;
    @Autowired
    private AddressSupportMixin addressSupportMixin;
    @Autowired
    private SearchSupportMixin searchSupportMixin;


    /**
     * Interface: GET /api/rest/customer/summary
     * <p>
     * <p>
     * Display customer profile information.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CustomerRO</td><td>
     * <pre><code>
     * {
     *     "customerId": 1,
     *     "email": "bob.doe@yc-json.com",
     *     "firstname": "Bob",
     *     "lastname": "Doe",
     *     "middlename": null,
     *     "tag": null,
     *     "attributes": [{
     *         "attrvalueId": 1,
     *         "val": "123123123",
     *         "displayVals": null,
     *         "attributeId": 1030,
     *         "attributeName": "Customer phone",
     *         "attributeDisplayNames": null,
     *         "customerId": 1
     *     }]
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CustomerRO</td><td>
     * <pre><code>
     * &lt;customer&gt;
     * 	&lt;attribute-values&gt;
     * 		&lt;attribute-value attribute-id="1030" attrvalue-id="2" customer-id="2"&gt;
     * 			&lt;attribute-name&gt;Customer phone&lt;/attribute-name&gt;
     * 			&lt;val&gt;123123123&lt;/val&gt;
     * 		&lt;/attribute-value&gt;
     * 	&lt;/attribute-values&gt;
     * 	&lt;customerId&gt;2&lt;/customerId&gt;
     * 	&lt;email&gt;bob.doe@yc-xml.com&lt;/email&gt;
     * 	&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 	&lt;lastname&gt;Doe&lt;/lastname&gt;
     * &lt;/customer&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return customer object
     */
    @RequestMapping(
            value = "/summary",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CustomerRO viewSummary(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                final HttpServletRequest request,
                                                final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        final Shop shop = cartMixin.getCurrentShop();
        final ShoppingCart cart = cartMixin.getCurrentCart();

        final Customer customer = customerServiceFacade.getCustomerByEmail(shop, cart.getCustomerEmail());

        final CustomerRO ro = mappingMixin.map(customer, CustomerRO.class, Customer.class);

        // Only map allowed attributes
        final List<AttrValueCustomerRO> profileAttrs = new ArrayList<>();
        for (final Pair<AttrValueWithAttribute, Boolean> av : customerServiceFacade.getCustomerProfileAttributes(shop, customer)) {
            final AttrValueAndAttributeRO ava = mappingMixin.map(av.getFirst(), AttrValueAndAttributeRO.class, AttrValueWithAttribute.class);
            profileAttrs.add(new AttrValueCustomerRO(ava, ro.getCustomerId()));
        }
        ro.setAttributes(profileAttrs);

        return ro;

    }

    /**
     * Interface: PUT /api/rest/customer/summary
     * <p>
     * <p>
     * Display customer profile information.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <p>
     * <table border="1">
     *     <tr><td>JSON object CustomerRO</td><td>
     * <pre><code>
     * {
     *     "customerId": 1,
     *     "email": "bob.doe@yc-json.com",
     *     "firstname": "Bob",
     *     "lastname": "Doe",
     *     "middlename": null,
     *     "tag": null,
     *     "attributes": [{
     *         "attrvalueId": 1,
     *         "val": "123123123",
     *         "displayVals": null,
     *         "attributeId": 1030,
     *         "attributeName": "Customer phone",
     *         "attributeDisplayNames": null,
     *         "customerId": 1
     *     }]
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CustomerUpdatedRO</td><td>
     * <pre><code>
     * &lt;customer&gt;
     * 	&lt;attribute-values&gt;
     * 		&lt;attribute-value attribute-id="1030" attrvalue-id="2" customer-id="2"&gt;
     * 			&lt;attribute-name&gt;Customer phone&lt;/attribute-name&gt;
     * 			&lt;val&gt;123123123&lt;/val&gt;
     * 		&lt;/attribute-value&gt;
     * 	&lt;/attribute-values&gt;
     * 	&lt;customerId&gt;2&lt;/customerId&gt;
     * 	&lt;email&gt;bob.doe@yc-xml.com&lt;/email&gt;
     * 	&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 	&lt;lastname&gt;Doe&lt;/lastname&gt;
     * &lt;/customer&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON object CustomerUpdatedRO</td><td>
     * <pre><code>
     * {
     *   "problems" : {},
     *   "success" : true,
     *   "customer" : {
     *     "lastname" : "Doe",
     *     "firstname" : "Bob",
     *     "middlename" : null,
     *     "email" : "bob1zzzzz@bob.com",
     *     "customerId" : 25,
     *     "tag" : null,
     *     "attributes" : [
     *       {
     *         "attrvalueId" : 48,
     *         "attributeCode" : "CUSTOMER_PHONE",
     *         "val" : "1234",
     *         "displayVals" : null,
     *         "attributeName" : "Phone",
     *         "attributeId" : 11050,
     *         "attributeDisplayNames" : null,
     *         "attributeDisplayChoices" : null,
     *         "customerId" : 25
     *       },
     *       {
     *         "attrvalueId" : 49,
     *         "attributeCode" : "MARKETING_OPT_IN",
     *         "val" : "true",
     *         "displayVals" : null,
     *         "attributeName" : "Marketing Opt in",
     *         "attributeId" : 11051,
     *         "attributeDisplayNames" : {},
     *         "attributeDisplayChoices" : {},
     *         "customerId" : 25
     *       },
     *       {
     *         "attrvalueId" : 50,
     *         "attributeCode" : "CUSTOMERTYPE",
     *         "val" : "B",
     *         "displayVals" : null,
     *         "attributeName" : "Customer Type",
     *         "attributeId" : 1611,
     *         "attributeDisplayNames" : {
     *           "uk" : "тип пользователя",
     *           "en" : "Customer Type"
     *         },
     *         "attributeDisplayChoices" : {
     *           "uk" : "B-Покупатель,S-Продавец",
     *           "ru" : "B-Покупатель,S-Продавец",
     *           "en" : "B-Buyer,S-Seller"
     *         },
     *         "customerId" : 25
     *       }
     *     ]
     *   }
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML object CustomerUpdatedRO</td><td>
     * &lt;customer-updated success="true"&gt;
     *     &lt;customer&gt;
     *         &lt;attribute-values&gt;
     *             &lt;attribute-value attribute-code="CUSTOMER_PHONE" attribute-id="11050" attrvalue-id="48" customer-id="25"&gt;
     *                 &lt;attribute-name&gt;Phone&lt;/attribute-name&gt;
     *                 &lt;val&gt;1234&lt;/val&gt;
     *             &lt;/attribute-value&gt;
     *             &lt;attribute-value attribute-code="MARKETING_OPT_IN" attribute-id="11051" attrvalue-id="49" customer-id="25"&gt;
     *                 &lt;attribute-display-choices/&gt;
     *                 &lt;attribute-display-names/&gt;
     *                 &lt;attribute-name&gt;Marketing Opt in&lt;/attribute-name&gt;
     *                 &lt;val&gt;true&lt;/val&gt;
     *             &lt;/attribute-value&gt;
     *             &lt;attribute-value attribute-code="CUSTOMERTYPE" attribute-id="1611" attrvalue-id="50" customer-id="25"&gt;
     *                 &lt;attribute-display-choices&gt;
     *                     &lt;entry lang="uk"&gt;B-Покупатель,S-Продавец&lt;/entry&gt;
     *                     &lt;entry lang="en"&gt;B-Buyer,S-Seller&lt;/entry&gt;
     *                     &lt;entry lang="ru"&gt;B-Покупатель,S-Продавец&lt;/entry&gt;
     *                 &lt;/attribute-display-choices&gt;
     *                 &lt;attribute-display-names&gt;
     *                     &lt;entry lang="uk"&gt;тип пользователя&lt;/entry&gt;
     *                     &lt;entry lang="en"&gt;Customer Type&lt;/entry&gt;
     *                 &lt;/attribute-display-names&gt;
     *                 &lt;attribute-name&gt;Customer Type&lt;/attribute-name&gt;
     *                 &lt;val&gt;B&lt;/val&gt;
     *             &lt;/attribute-value&gt;
     *         &lt;/attribute-values&gt;
     *         &lt;customerId&gt;25&lt;/customerId&gt;
     *         &lt;email&gt;bob1zzzzz@bob.com&lt;/email&gt;
     *         &lt;firstname&gt;Bob&lt;/firstname&gt;
     *         &lt;lastname&gt;Doe&lt;/lastname&gt;
     *     &lt;/customer&gt;
     *     &lt;problems/&gt;
     * &lt;/customer-updated&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <h3>Error codes</h3><p>
     * <table border="1">
     *     <tr><td>FIRSTNAME_FAILED</td><td>must be not blank</td></tr>
     *     <tr><td>LASTNAME_FAILED</td><td>must be not blank</td></tr>
     *     <tr><td>[ATTRIBUTE CODE]_FAILED</td><td>
     *         E.g. CUSTOMERTYPE_FAILED denoting that mandatory value was missing or regex is not matching.
     *         RegEx and Message come from {@link org.yes.cart.domain.entity.Attribute#getRegexp()} and
     *         {@link org.yes.cart.domain.entity.Attribute#getValidationFailedMessage()} respectively
     *         Localised validation error message will be stored in problems under [ATTRIBUTE CODE]_FAILED key.
     *     </td></tr>
     * </table>
     *
     * @param update customer update object
     * @param request request
     * @param response response
     *
     * @return customer object
     */
    @RequestMapping(
            value = "/summary",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CustomerUpdatedRO updateSummary(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                         final @RequestBody CustomerRO update,
                                                         final HttpServletRequest request,
                                                         final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);

        final Shop shop = cartMixin.getCurrentShop();
        final ShoppingCart cart = cartMixin.getCurrentCart();

        final Customer customer = customerServiceFacade.getCustomerByEmail(shop, cart.getCustomerEmail());

        final CustomerUpdatedRO result = new CustomerUpdatedRO();
        result.setSuccess(true);
        final Map<String, String> problems = new HashMap<>();
        result.setProblems(problems);

        if (StringUtils.isBlank(update.getFirstname())) {

            result.setSuccess(false);
            problems.put("firstname", "FIRSTNAME_FAILED");

        }

        if (StringUtils.isBlank(update.getLastname())) {

            result.setSuccess(false);
            problems.put("lastname", "LASTNAME_FAILED");

        }

        final Map<String, String> valuesToUpdate = new HashMap<>();
        if (CollectionUtils.isNotEmpty(update.getAttributes())) {

            final Map<String, AttrValueCustomerRO> valuesInThisUpdate = new HashMap<>();
            for (final AttrValueCustomerRO avRO : update.getAttributes()) {
                valuesInThisUpdate.put(avRO.getAttributeCode(), avRO);
            }

            final List<String> readonly = shop.getSupportedProfileFormReadOnlyAttributesAsList(customer.getCustomerType());
            for (final Pair<AttrValueWithAttribute, Boolean> av : customerServiceFacade.getCustomerProfileAttributes(shop, customer)) {

                final Attribute attr = av.getFirst().getAttribute();
                final AttrValueCustomerRO avRO = valuesInThisUpdate.get(attr.getCode());
                if (avRO != null && !readonly.contains(attr.getCode())) {

                    if (attr.isMandatory() && StringUtils.isBlank(avRO.getVal())) {

                        result.setSuccess(false);
                        problems.put(attr.getCode(), attr.getCode() + "_FAILED");

                    } else if (StringUtils.isNotBlank(attr.getRegexp())
                            && !!RegExUtils.getInstance(attr.getRegexp()).matches(avRO.getVal())) {

                        final String regexError = new FailoverStringI18NModel(
                                attr.getValidationFailedMessage(),
                                null).getValue(cart.getCurrentLocale());

                        result.setSuccess(false);
                        problems.put(attr.getCode(), attr.getCode() + "_FAILED");
                        if (StringUtils.isNotBlank(regexError)) {
                            problems.put(attr.getCode() + "_FAILED", regexError);
                        }

                    } else {

                        valuesToUpdate.put(attr.getCode(), avRO.getVal());

                    }

                }

            }
        }

        if (!result.isSuccess()) {

            return result;

        }

        customer.setSalutation(update.getSalutation());
        customer.setFirstname(update.getFirstname());
        customer.setLastname(update.getLastname());
        customer.setMiddlename(update.getMiddlename());
        customer.setCompanyName1(update.getCompanyName1());
        customer.setCompanyName2(update.getCompanyName2());
        customer.setCompanyDepartment(update.getCompanyDepartment());

        customerServiceFacade.updateCustomerAttributes(shop, customer, valuesToUpdate);

        result.setCustomer(viewSummary(requestToken, request, response));

        return result;

    }


    private List<AddressRO> viewAddressbookInternal(final String type) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();
        final Shop customerShop = cartMixin.getCurrentCustomerShop();

        return addressSupportMixin.viewAddressOptions(cart, shop, customerShop, type);

    }


    /**
     * Interface: GET /api/rest/customer/addressbook/{type}
     * <p>
     * <p>
     * Display customer address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object AddressRO</td><td>
     * <pre><code>
     * [{
     *     "addressId": 1,
     *     "city": "Nowhere",
     *     "postcode": "0001",
     *     "addrline1": "In the middle of",
     *     "addrline2": null,
     *     "addressType": "S",
     *     "countryCode": "UA",
     *     "countryName": "Ukraine",
     *     "countryLocalName": null,
     *     "stateCode": "UA-UA",
     *     "stateName": "Ukraine",
     *     "stateLocalName": null,
     *     "firstname": "Bob",
     *     "lastname": "Doe",
     *     "middlename": null,
     *     "defaultAddress": true,
     *     "phone1": "123123123",
     *     "customerId": 1
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of addresses
     */
    @RequestMapping(
            value = "/addressbook/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<AddressRO> viewAddressbook(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                         final @PathVariable(value = "type") String type,
                                                         final HttpServletRequest request,
                                                         final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewAddressbookInternal(type);

    }


    /**
     * Interface: GET /api/rest/customer/addressbook/{type}
     * <p>
     * <p>
     * Display customer address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object AddressRO</td><td>
     * <pre><code>
     * &lt;addresses&gt;
     * &lt;address address-id="3" address-type="S" customer-id="2" default-address="true"&gt;
     * 	&lt;addrline1&gt;In the middle of&lt;/addrline1&gt;
     * 	&lt;city&gt;Nowhere&lt;/city&gt;
     * 	&lt;country-code&gt;UA&lt;/country-code&gt;
     * 	&lt;country-name&gt;Ukraine&lt;/country-name&gt;
     * 	&lt;country-local-name&gt;Україна&lt;/country-local-name&gt;
     * 	&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 	&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 	&lt;phone-1&gt;123123123&lt;/phone-1&gt;
     * 	&lt;postcode&gt;0001&lt;/postcode&gt;
     * 	&lt;state-code&gt;UA-KI&lt;/state-code&gt;
     * 	&lt;state-name&gt;Kiev region&lt;/state-name&gt;
     * 	&lt;state-local-name&gt;Київська обл&lt;/state-local-name&gt;
     * &lt;/address&gt;
     * &lt;/addresses&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of addresses
     */
    @RequestMapping(
            value = "/addressbook/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AddressListRO viewAddressbookXML(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                          final @PathVariable(value = "type") String type,
                                                          final HttpServletRequest request,
                                                          final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new AddressListRO(viewAddressbookInternal(type));

    }


    private List<CountryRO> viewAddressbookCountriesInternal(final String type) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        return addressSupportMixin.viewAddressCountryOptions(cart, shop, type);

    }



    /**
     * Interface: GET /api/rest/customer/addressbook/{type}/options/countries
     * <p>
     * <p>
     * Display country options for address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object CountryRO</td><td>
     * <pre><code>
     * [{
     *     "countryId": 218,
     *     "countryCode": "UA",
     *     "isoCode": "804",
     *     "name": "Ukraine",
     *     "displayName": "Україна"
     * }, {
     *     "countryId": 220,
     *     "countryCode": "GB",
     *     "isoCode": "826",
     *     "name": "United Kingdom",
     *     "displayName": null
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of countries
     */
    @RequestMapping(
            value = "/addressbook/{type}/options/countries",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<CountryRO> viewAddressbookCountries(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                                  final @PathVariable(value = "type") String type,
                                                                  final HttpServletRequest request,
                                                                  final HttpServletResponse response) {
        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewAddressbookCountriesInternal(type);

    }



    /**
     * Interface: GET /api/rest/customer/addressbook/{type}/options/countries
     * <p>
     * <p>
     * Display country options for address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object CountryRO</td><td>
     * <pre><code>
     * &lt;countries&gt;
     * 	&lt;country country-code="UA" country-id="218" country-iso="804"&gt;
     * 		&lt;name&gt;Ukraine&lt;/name&gt;
     * 		&lt;display-name&gt;Україна&lt;/display-name&gt;
     * 	&lt;/country&gt;
     * 	&lt;country country-code="GB" country-id="220" country-iso="826"&gt;
     * 		&lt;name&gt;United Kingdom&lt;/name&gt;
     * 	&lt;/country&gt;
     * &lt;/countries&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of countries
     */
    @RequestMapping(
            value = "/addressbook/{type}/options/countries",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody CountryListRO viewAddressbookCountriesXML(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                                   final @PathVariable(value = "type") String type,
                                                                   final HttpServletRequest request,
                                                                   final HttpServletResponse response) {
        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new CountryListRO(viewAddressbookCountriesInternal(type));

    }



    private List<StateRO> viewAddressbookCountryStatesInternal(final String country) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();

        return addressSupportMixin.viewAddressCountryStateOptions(cart, shop, country);

    }




    /**
     * Interface: GET /api/rest/customer/addressbook/{type}/options/country/{code}
     * <p>
     * <p>
     * Display country options for address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     *     <tr><td>code</td><td>country code {@link Country}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object StateRO</td><td>
     * <pre><code>
     * [{
     *     "stateId": 218,
     *     "countryCode": "UA",
     *     "stateCode": "UA-UA",
     *     "name": "Kiev province",
     *     "displayName": "Київська область"
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param code two letter country code (see {@link Country} )
     * @param request request
     * @param response response
     *
     * @return list of country states
     */
    @RequestMapping(
            value = "/addressbook/{type}/options/country/{code}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<StateRO> viewAddressbookCountries(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                                final @PathVariable(value = "type") String type,
                                                                final @PathVariable(value = "code") String code,
                                                                final HttpServletRequest request,
                                                                final HttpServletResponse response) {
        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewAddressbookCountryStatesInternal(code);

    }


    /**
     * Interface: GET /api/rest/customer/addressbook/{type}/options/country/{code}
     * <p>
     * <p>
     * Display country options for address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     *     <tr><td>code</td><td>country code {@link Country}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object StateRO</td><td>
     * <pre><code>
     * &lt;states&gt;
     * 	&lt;state country-code="UA" state-code="UA-UA" state-id="218"&gt;
     * 		&lt;name&gt;Kiev province&lt;/name&gt;
     * 		&lt;display-name&gt;Київська область&lt;/display-name&gt;
     * 	&lt;/state&gt;
     * &lt;/states&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param code two letter country code (see {@link Country} )
     * @param request request
     * @param response response
     *
     * @return list of country states
     */
    @RequestMapping(
            value = "/addressbook/{type}/options/country/{code}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody StateListRO viewAddressbookCountriesXML(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                                 final @PathVariable(value = "type") String type,
                                                                 final @PathVariable(value = "code") String code,
                                                                 final HttpServletRequest request,
                                                                 final HttpServletResponse response) {
        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new StateListRO(viewAddressbookCountryStatesInternal(code));

    }



    private List<AddressRO> updateAddressbookInternal(final String type, final AddressRO address) {

        final ShoppingCart cart = cartMixin.getCurrentCart();
        final Shop shop = cartMixin.getCurrentShop();
        final Shop customerShop = cartMixin.getCurrentCustomerShop();

        final Customer customer = customerServiceFacade.getCheckoutCustomer(shop, cart);
        if (customer == null) {
            cartMixin.throwSecurityExceptionIfNotLoggedIn();
        }

        final Address addressEntity = addressBookFacade.getAddress(customer, customerShop, String.valueOf(address.getAddressId()), type);

        addressEntity.setName(address.getName());
        if (StringUtils.isNotBlank(address.getSalutation())) {
            addressEntity.setSalutation(address.getSalutation());
        }
        if (StringUtils.isNotBlank(address.getFirstname())) {
            addressEntity.setFirstname(address.getFirstname());
        }
        if (StringUtils.isNotBlank(address.getMiddlename())) {
            addressEntity.setMiddlename(address.getMiddlename());
        }
        if (StringUtils.isNotBlank(address.getLastname())) {
            addressEntity.setLastname(address.getLastname());
        }

        addressEntity.setAddrline1(address.getAddrline1());
        addressEntity.setAddrline2(address.getAddrline2());

        addressEntity.setCity(address.getCity());
        addressEntity.setPostcode(address.getPostcode());
        addressEntity.setStateCode(address.getStateCode());
        addressEntity.setCountryCode(address.getCountryCode());

        if (StringUtils.isNotBlank(address.getPhone1())) {
            addressEntity.setPhone1(address.getPhone1());
        }

        addressEntity.setCompanyName1(address.getCompanyName1());
        addressEntity.setCompanyName2(address.getCompanyName2());
        addressEntity.setCompanyDepartment(address.getCompanyDepartment());

        addressBookFacade.createOrUpdate(addressEntity, customerShop);

        return viewAddressbookInternal(type);
    }


    /**
     * Interface: PUT /api/rest/customer/addressbook/{type}
     * <p>
     * <p>
     * Display customer address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     *     <tr><td>JSON body AddressRO</td><td>
     * <pre><code>
     * {
     *     "addressId": 1,
     *     "city": "Nowhere",
     *     "postcode": "0001",
     *     "addrline1": "In the middle of",
     *     "addrline2": null,
     *     "addressType": "S",
     *     "countryCode": "UA",
     *     "countryName": "Ukraine",
     *     "countryLocalName": null,
     *     "stateCode": "UA-UA",
     *     "stateName": "Ukraine",
     *     "stateLocalName": null,
     *     "firstname": "Bob",
     *     "lastname": "Doe",
     *     "middlename": null,
     *     "defaultAddress": true,
     *     "phone1": "123123123",
     *     "customerId": 1
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML body AddressRO</td><td>
     * <pre><code>
     * &lt;address address-id="3" address-type="S" customer-id="2" default-address="true"&gt;
     * 	&lt;addrline1&gt;In the middle of&lt;/addrline1&gt;
     * 	&lt;city&gt;Nowhere&lt;/city&gt;
     * 	&lt;country-code&gt;UA&lt;/country-code&gt;
     * 	&lt;country-name&gt;Ukraine&lt;/country-name&gt;
     * 	&lt;country-local-name&gt;Україна&lt;/country-local-name&gt;
     * 	&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 	&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 	&lt;phone-1&gt;123123123&lt;/phone-1&gt;
     * 	&lt;postcode&gt;0001&lt;/postcode&gt;
     * 	&lt;state-code&gt;UA-KI&lt;/state-code&gt;
     * 	&lt;state-name&gt;Kiev region&lt;/state-name&gt;
     * 	&lt;state-local-name&gt;Київська обл&lt;/state-local-name&gt;
     * &lt;/address&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object AddressRO</td><td>
     * <pre><code>
     * [{
     *     "addressId": 1,
     *     "city": "Nowhere",
     *     "postcode": "0001",
     *     "addrline1": "In the middle of",
     *     "addrline2": null,
     *     "addressType": "S",
     *     "countryCode": "UA",
     *     "countryName": "Ukraine",
     *     "countryLocalName": null,
     *     "stateCode": "UA-UA",
     *     "stateName": "Ukraine",
     *     "stateLocalName": null,
     *     "firstname": "Bob",
     *     "lastname": "Doe",
     *     "middlename": null,
     *     "defaultAddress": true,
     *     "phone1": "123123123",
     *     "customerId": 1
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of addresses
     */
    @RequestMapping(
            value = "/addressbook/{type}",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody List<AddressRO> updateAddressbook(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                           final @PathVariable(value = "type") String type,
                                                           final @RequestBody AddressRO address,
                                                           final HttpServletRequest request,
                                                           final HttpServletResponse response) {
        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return updateAddressbookInternal(type, address);

    }

    /**
     * Interface: PUT /api/rest/customer/addressbook/{type}
     * <p>
     * <p>
     * Display customer address book.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Content-Type</td><td>application/json or application/xml</td></tr>
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link Address}</td></tr>
     *     <tr><td>JSON body AddressRO</td><td>
     * <pre><code>
     * {
     *     "addressId": 1,
     *     "city": "Nowhere",
     *     "postcode": "0001",
     *     "addrline1": "In the middle of",
     *     "addrline2": null,
     *     "addressType": "S",
     *     "countryCode": "UA",
     *     "countryName": "Ukraine",
     *     "countryLocalName": null,
     *     "stateCode": "UA-UA",
     *     "stateName": "Ukraine",
     *     "stateLocalName": null,
     *     "firstname": "Bob",
     *     "lastname": "Doe",
     *     "middlename": null,
     *     "defaultAddress": true,
     *     "phone1": "123123123",
     *     "customerId": 1
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML body AddressRO</td><td>
     * <pre><code>
     * &lt;address address-id="3" address-type="S" customer-id="2" default-address="true"&gt;
     * 	&lt;addrline1&gt;In the middle of&lt;/addrline1&gt;
     * 	&lt;city&gt;Nowhere&lt;/city&gt;
     * 	&lt;country-code&gt;UA&lt;/country-code&gt;
     * 	&lt;country-name&gt;Ukraine&lt;/country-name&gt;
     * 	&lt;country-local-name&gt;Україна&lt;/country-local-name&gt;
     * 	&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 	&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 	&lt;phone-1&gt;123123123&lt;/phone-1&gt;
     * 	&lt;postcode&gt;0001&lt;/postcode&gt;
     * 	&lt;state-code&gt;UA-KI&lt;/state-code&gt;
     * 	&lt;state-name&gt;Kiev region&lt;/state-name&gt;
     * 	&lt;state-local-name&gt;Київська обл&lt;/state-local-name&gt;
     * &lt;/address&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object AddressRO</td><td>
     * <pre><code>
     * &lt;addresses&gt;
     * &lt;address address-id="3" address-type="S" customer-id="2" default-address="true"&gt;
     * 	&lt;addrline1&gt;In the middle of&lt;/addrline1&gt;
     * 	&lt;city&gt;Nowhere&lt;/city&gt;
     * 	&lt;country-code&gt;UA&lt;/country-code&gt;
     * 	&lt;country-name&gt;Ukraine&lt;/country-name&gt;
     * 	&lt;country-local-name&gt;Україна&lt;/country-local-name&gt;
     * 	&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 	&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 	&lt;phone-1&gt;123123123&lt;/phone-1&gt;
     * 	&lt;postcode&gt;0001&lt;/postcode&gt;
     * 	&lt;state-code&gt;UA-KI&lt;/state-code&gt;
     * 	&lt;state-name&gt;Kiev region&lt;/state-name&gt;
     * 	&lt;state-local-name&gt;Київська обл&lt;/state-local-name&gt;
     * &lt;/address&gt;
     * &lt;/addresses&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link Address} )
     * @param request request
     * @param response response
     *
     * @return list of addresses
     */
    @RequestMapping(
            value = "/addressbook/{type}",
            method = RequestMethod.PUT,
            produces = { MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody AddressListRO updateAddressbookXML(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                            final @PathVariable(value = "type") String type,
                                                            final @RequestBody AddressRO address,
                                                            final HttpServletRequest request,
                                                            final HttpServletResponse response) {
        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new AddressListRO(updateAddressbookInternal(type, address));

    }


    private List<ProductWishlistRO> viewWishlistInternal(final String type, final String tag) {


        final Shop shop = cartMixin.getCurrentShop();
        final ShoppingCart cart = cartMixin.getCurrentCart();
        final long browsingShopId = cartMixin.getCurrentCustomerShopId();

        final List<CustomerWishList> wishList = customerServiceFacade.getCustomerWishListByEmail(
                shop,
                type, cart.getCustomerEmail(), null,
                tag != null ? new String[] { tag } : null);

        if (CollectionUtils.isNotEmpty(wishList)) {

            final List<String> skuCodes = new ArrayList<>();

            for (final CustomerWishList item : wishList) {

                skuCodes.add(String.valueOf(item.getSkuCode()));

            }

            final List<ProductSearchResultDTO> uniqueProducts = productServiceFacade.getListProductSKUs(
                    skuCodes, -1L, shop.getShopId(), browsingShopId);

            final List<ProductWishlistRO> wlRo = new ArrayList<>();

            for (final CustomerWishList item : wishList) {

                final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(cart.getCurrencyCode());

                final String symbolAbr = symbol.getFirst();
                final String symbolPos = symbol.getSecond() != null && symbol.getSecond() ? "after" : "before";

                for (final ProductSearchResultDTO uniqueProduct : uniqueProducts) {

                    if (uniqueProduct.getSearchSkus() != null) {
                        for (final ProductSkuSearchResultDTO sku : uniqueProduct.getSearchSkus()) {

                            if (sku.getCode().equals(item.getSkuCode()) && sku.getFulfilmentCentreCode().equals(item.getSupplierCode())) {
                                final ProductWishlistRO wl = mappingMixin.map(uniqueProduct, ProductWishlistRO.class, ProductSearchResultDTO.class);
                                wl.setDefaultSkuCode(item.getSkuCode());
                                wl.setQuantity(item.getQuantity());

                                final ProductAvailabilityModel wlPam = productServiceFacade.getProductAvailability(sku, browsingShopId);
                                final ProductAvailabilityModelRO wlAmRo = mappingMixin.map(wlPam, ProductAvailabilityModelRO.class, ProductAvailabilityModel.class);
                                wl.setProductAvailabilityModel(wlAmRo);

                                final PriceModel currentWlPrice = productServiceFacade.getSkuPrice(
                                        cart,
                                        null,
                                        item.getSkuCode(),
                                        BigDecimal.ONE,
                                        item.getSupplierCode()
                                );

                                final SkuPriceRO currentWlPriceRo = mappingMixin.map(currentWlPrice, SkuPriceRO.class, PriceModel.class);
                                currentWlPriceRo.setSymbol(symbolAbr);
                                currentWlPriceRo.setSymbolPosition(symbolPos);

                                wl.setPrice(currentWlPriceRo);

                                final SkuPriceRO wlPrice = new SkuPriceRO();
                                wlPrice.setQuantity(BigDecimal.ONE);
                                wlPrice.setCurrency(item.getRegularPriceCurrencyWhenAdded());
                                wlPrice.setRegularPrice(item.getRegularPriceWhenAdded());
                                wl.setPriceWhenAdded(wlPrice);
                                final Pair<String, Boolean> wlSymbol = currencySymbolService.getCurrencySymbol(wlPrice.getCurrency());
                                wlPrice.setSymbol(wlSymbol.getFirst());
                                wlPrice.setSymbolPosition(wlSymbol.getSecond() != null && wlSymbol.getSecond() ? "after" : "before");

                                final ProductSkuSearchResultRO rvs = mappingMixin.map(sku, ProductSkuSearchResultRO.class, ProductSkuSearchResultDTO.class);
                                rvs.setSkuAvailabilityModel(wlAmRo);
                                rvs.setPrice(currentWlPriceRo);

                                final BigDecimal cartQty = cart.getProductSkuQuantity(sku.getFulfilmentCentreCode(), sku.getCode());
                                final QuantityModel wlQm = productServiceFacade.getProductQuantity(cartQty, sku, browsingShopId);
                                final ProductQuantityModelRO wlQmRo = mappingMixin.map(wlQm, ProductQuantityModelRO.class, QuantityModel.class);
                                rvs.setSkuQuantityModel(wlQmRo);

                                wl.setSkus(new ArrayList<>(Collections.singletonList(rvs)));

                                wlRo.add(wl);
                            }

                        }
                    }

                }

            }

            return wlRo;

        }

        return Collections.emptyList();

    }



    /**
     * Interface: GET /api/rest/customer/wishlist/{type}
     * <p>
     * <p>
     * Display customer default wishlist.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link CustomerWishList}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object ProductWishlistRO</td><td>
     * <pre><code>
     * [{
     *     "id": 9998,
     *     "code": "BENDER-ua",
     *     "manufacturerCode": null,
     *     "multisku": false,
     *     "defaultSkuCode": "BENDER-ua",
     *     "name": "...",
     *     "displayName": {
     *         "ua": "...",
     *         "en": "Bender Bending Rodriguez",
     *         "ru": "..."
     *     },
     *     "description": "...",
     *     "displayDescription": null,
     *     "availablefrom": 1270721717451,
     *     "availableto": 2217492917451,
     *     "availability": 1,
     *     "productAvailabilityModel": {
     *         "available": true,
     *         "inStock": true,
     *         "perpetual": false,
     *         "defaultSkuCode": "BENDER-ua",
     *         "firstAvailableSkuCode": "BENDER-ua",
     *         "skuCodes": ["BENDER-ua"],
     *         "availableToSellQuantity": {
     *             "BENDER-ua": 1.000
     *         }
     *     },
     *     "price": {
     *         "currency": "EUR",
     *         "symbol": "€",
     *         "symbolPosition": "before",
     *         "quantity": 1,
     *         "regularPrice": 0.00,
     *         "salePrice": null,
     *         "discount": null
     *     },
     *     "defaultImage": "noimage.jpeg",
     *     "featured": false,
     *     "minOrderQuantity": 1.00,
     *     "maxOrderQuantity": 100000.00,
     *     "stepOrderQuantity": 1.00,
     *     "skus": null,
     *     "priceWhenAdded": {
     *         "currency": "EUR",
     *         "symbol": "€",
     *         "symbolPosition": "before",
     *         "quantity": 1,
     *         "regularPrice": 0.00,
     *         "salePrice": null,
     *         "discount": null
     *     },
     *     "quantity": 10.00
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link CustomerWishList}) )
     * @param request request
     * @param response response
     *
     * @return list of wish list items
     */
    @RequestMapping(
            value = "/wishlist/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductWishlistRO> viewWishlist(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                              final @PathVariable(value = "type") String type,
                                                              final HttpServletRequest request,
                                                              final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewWishlistInternal(type, null);

    }


    /**
     * Interface: GET /api/rest/customer/wishlist/{type}/{tag}
     * <p>
     * <p>
     * Display customer wishlist by tag.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link CustomerWishList}</td></tr>
     *     <tr><td>tag</td><td>wishlist tag {@link CustomerWishList}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array object ProductWishlistRO</td><td>
     * <pre><code>
     * [{
     *     "id": 9998,
     *     "code": "BENDER-ua",
     *     "manufacturerCode": null,
     *     "multisku": false,
     *     "defaultSkuCode": "BENDER-ua",
     *     "name": "...",
     *     "displayName": {
     *         "ua": "...",
     *         "en": "Bender Bending Rodriguez",
     *         "ru": "..."
     *     },
     *     "description": "...",
     *     "displayDescription": null,
     *     "availablefrom": 1270721717451,
     *     "availableto": 2217492917451,
     *     "availability": 1,
     *     "productAvailabilityModel": {
     *         "available": true,
     *         "inStock": true,
     *         "perpetual": false,
     *         "defaultSkuCode": "BENDER-ua",
     *         "firstAvailableSkuCode": "BENDER-ua",
     *         "skuCodes": ["BENDER-ua"],
     *         "availableToSellQuantity": {
     *             "BENDER-ua": 1.000
     *         }
     *     },
     *     "price": {
     *         "currency": "EUR",
     *         "symbol": "€",
     *         "symbolPosition": "before",
     *         "quantity": 1,
     *         "regularPrice": 0.00,
     *         "salePrice": null,
     *         "discount": null
     *     },
     *     "defaultImage": "noimage.jpeg",
     *     "featured": false,
     *     "minOrderQuantity": 1.00,
     *     "maxOrderQuantity": 100000.00,
     *     "stepOrderQuantity": 1.00,
     *     "skus": null,
     *     "priceWhenAdded": {
     *         "currency": "EUR",
     *         "symbol": "€",
     *         "symbolPosition": "before",
     *         "quantity": 1,
     *         "regularPrice": 0.00,
     *         "salePrice": null,
     *         "discount": null
     *     },
     *     "quantity": 10.00
     * }]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link CustomerWishList}) )
     * @param request request
     * @param response response
     *
     * @return list of wish list items
     */
    @RequestMapping(
            value = "/wishlist/{type}/{tag}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductWishlistRO> viewWishlist(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                              final @PathVariable(value = "type") String type,
                                                              final @PathVariable(value = "tag") String tag,
                                                              final HttpServletRequest request,
                                                              final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewWishlistInternal(type, tag);

    }




    /**
     * Interface: GET /api/rest/customer/wishlist/{type}
     * <p>
     * <p>
     * Display customer default wishlist.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link CustomerWishList}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object ProductWishlistRO</td><td>
     * <pre><code>
     * &lt;wishlist&gt;
     * 	&lt;product&gt;
     * 		&lt;availability&gt;1&lt;/availability&gt;
     * 		&lt;availablefrom&gt;2010-04-08T11:15:17.451+01:00&lt;/availablefrom&gt;
     * 		&lt;availableto&gt;2040-04-08T11:15:17.451+01:00&lt;/availableto&gt;
     * 		&lt;code&gt;BENDER-ua&lt;/code&gt;
     * 		&lt;default-image&gt;noimage.jpeg&lt;/default-image&gt;
     * 		&lt;default-sku-code&gt;BENDER-ua&lt;/default-sku-code&gt;
     * 		&lt;description&gt;...&lt;/description&gt;
     * 		&lt;display-names&gt;
     * 			&lt;entry lang="ua"&gt;...&lt;/entry&gt;
     * 			&lt;entry lang="en"&gt;Bender Bending Rodriguez&lt;/entry&gt;
     * 			&lt;entry lang="ru"&gt;...&lt;/entry&gt;
     * 		&lt;/display-names&gt;
     * 		&lt;featured&gt;false&lt;/featured&gt;
     * 		&lt;id&gt;9998&lt;/id&gt;
     * 		&lt;max-order-quantity&gt;100000.00&lt;/max-order-quantity&gt;
     * 		&lt;min-order-quantity&gt;1.00&lt;/min-order-quantity&gt;
     * 		&lt;multisku&gt;false&lt;/multisku&gt;
     * 		&lt;name&gt;...&lt;/name&gt;
     * 		&lt;price&gt;
     *   			&lt;currency&gt;EUR&lt;/currency&gt;
     * 			&lt;quantity&gt;1&lt;/quantity&gt;
     * 			&lt;regular-price&gt;0.00&lt;/regular-price&gt;
     * 			&lt;symbol&gt;€&lt;/symbol&gt;
     * 			&lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 		&lt;/price&gt;
     * 		&lt;product-availability&gt;
     * 			&lt;available&gt;true&lt;/available&gt;
     * 			&lt;ats-quantity&gt;
     * 				&lt;entry sku="BENDER-ua"&gt;1.000&lt;/entry&gt;
     * 			&lt;/ats-quantity&gt;
     * 			&lt;default-sku&gt;BENDER-ua&lt;/default-sku&gt;
     * 			&lt;first-available-sku&gt;BENDER-ua&lt;/first-available-sku&gt;
     * 			&lt;in-stock&gt;true&lt;/in-stock&gt;
     * 			&lt;perpetual&gt;false&lt;/perpetual&gt;
     * 			&lt;sku-codes&gt;BENDER-ua&lt;/sku-codes&gt;
     * 		&lt;/product-availability&gt;
     * 		&lt;step-order-quantity&gt;1.00&lt;/step-order-quantity&gt;
     * 		&lt;priceWhenAdded&gt;
     * 			&lt;currency&gt;EUR&lt;/currency&gt;
     * 			&lt;quantity&gt;1&lt;/quantity&gt;
     * 			&lt;regular-price&gt;0.00&lt;/regular-price&gt;
     * 			&lt;symbol&gt;€&lt;/symbol&gt;
     * 			&lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 		&lt;/priceWhenAdded&gt;
     * 		&lt;quantity&gt;10.00&lt;/quantity&gt;
     * 	&lt;/product&gt;
     * &lt;/wishlist&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link CustomerWishList}) )
     * @param request request
     * @param response response
     *
     * @return list of wish list items
     */
    @RequestMapping(
            value = "/wishlist/{type}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductWishlistListRO viewWishlistXML(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                               final @PathVariable(value = "type") String type,
                                                               final HttpServletRequest request,
                                                               final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new ProductWishlistListRO(viewWishlistInternal(type, null));

    }


    /**
     * Interface: GET /api/rest/customer/wishlist/{type}/{tag}
     * <p>
     * <p>
     * Display customer wishlist by tag.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <table border="1">
     *     <tr><td>type</td><td>type of address book {@link CustomerWishList}</td></tr>
     *     <tr><td>tag</td><td>wishlist tag {@link CustomerWishList}</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array object ProductWishlistRO</td><td>
     * <pre><code>
     * &lt;wishlist&gt;
     * 	&lt;product&gt;
     * 		&lt;availability&gt;1&lt;/availability&gt;
     * 		&lt;availablefrom&gt;2010-04-08T11:15:17.451+01:00&lt;/availablefrom&gt;
     * 		&lt;availableto&gt;2040-04-08T11:15:17.451+01:00&lt;/availableto&gt;
     * 		&lt;code&gt;BENDER-ua&lt;/code&gt;
     * 		&lt;default-image&gt;noimage.jpeg&lt;/default-image&gt;
     * 		&lt;default-sku-code&gt;BENDER-ua&lt;/default-sku-code&gt;
     * 		&lt;description&gt;...&lt;/description&gt;
     * 		&lt;display-names&gt;
     * 			&lt;entry lang="ua"&gt;...&lt;/entry&gt;
     * 			&lt;entry lang="en"&gt;Bender Bending Rodriguez&lt;/entry&gt;
     * 			&lt;entry lang="ru"&gt;...&lt;/entry&gt;
     * 		&lt;/display-names&gt;
     * 		&lt;featured&gt;false&lt;/featured&gt;
     * 		&lt;id&gt;9998&lt;/id&gt;
     * 		&lt;max-order-quantity&gt;100000.00&lt;/max-order-quantity&gt;
     * 		&lt;min-order-quantity&gt;1.00&lt;/min-order-quantity&gt;
     * 		&lt;multisku&gt;false&lt;/multisku&gt;
     * 		&lt;name&gt;...&lt;/name&gt;
     * 		&lt;price&gt;
     *   			&lt;currency&gt;EUR&lt;/currency&gt;
     * 			&lt;quantity&gt;1&lt;/quantity&gt;
     * 			&lt;regular-price&gt;0.00&lt;/regular-price&gt;
     * 			&lt;symbol&gt;€&lt;/symbol&gt;
     * 			&lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 		&lt;/price&gt;
     * 		&lt;product-availability&gt;
     * 			&lt;available&gt;true&lt;/available&gt;
     * 			&lt;ats-quantity&gt;
     * 				&lt;entry sku="BENDER-ua"&gt;1.000&lt;/entry&gt;
     * 			&lt;/ats-quantity&gt;
     * 			&lt;default-sku&gt;BENDER-ua&lt;/default-sku&gt;
     * 			&lt;first-available-sku&gt;BENDER-ua&lt;/first-available-sku&gt;
     * 			&lt;in-stock&gt;true&lt;/in-stock&gt;
     * 			&lt;perpetual&gt;false&lt;/perpetual&gt;
     * 			&lt;sku-codes&gt;BENDER-ua&lt;/sku-codes&gt;
     * 		&lt;/product-availability&gt;
     * 		&lt;step-order-quantity&gt;1.00&lt;/step-order-quantity&gt;
     * 		&lt;priceWhenAdded&gt;
     * 			&lt;currency&gt;EUR&lt;/currency&gt;
     * 			&lt;quantity&gt;1&lt;/quantity&gt;
     * 			&lt;regular-price&gt;0.00&lt;/regular-price&gt;
     * 			&lt;symbol&gt;€&lt;/symbol&gt;
     * 			&lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 		&lt;/priceWhenAdded&gt;
     * 		&lt;quantity&gt;10.00&lt;/quantity&gt;
     * 	&lt;/product&gt;
     * &lt;/wishlist&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param type address book type (see {@link CustomerWishList}) )
     * @param request request
     * @param response response
     *
     * @return list of wish list items
     */
    @RequestMapping(
            value = "/wishlist/{type}/{tag}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductWishlistListRO viewWishlistXML(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                               final @PathVariable(value = "type") String type,
                                                               final @PathVariable(value = "tag") String tag,
                                                               final HttpServletRequest request,
                                                               final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new ProductWishlistListRO(viewWishlistInternal(type, tag));

    }




    private List<ProductSearchResultRO> viewRecentlyViewedInternal() {


        final ShoppingCart cart = cartMixin.getCurrentCart();
        final long shopId = cartMixin.getCurrentShopId();
        final long browsingShopId = cartMixin.getCurrentCustomerShopId();

        final List<String> viewedSkus = cart.getShoppingContext().getLatestViewedSkus();
        final Map<String, String> skuAndSupplier = new ListHashMap<>();
        for (final String viewedSku : viewedSkus) {
            final int pos = viewedSku.indexOf("|");
            if (pos != -1) {
                skuAndSupplier.put(viewedSku.substring(0, pos), viewedSku.substring(pos + 1));
            }
        }

        final List<ProductSearchResultDTO> viewedProducts = new ArrayList<>(productServiceFacade.getListProductSKUs(
                new ArrayList<>(skuAndSupplier.keySet()), -1L, shopId, browsingShopId));
        viewedProducts.removeIf(viewed -> viewed.getFulfilmentCentreCode().equals(skuAndSupplier.get(viewed.getFulfilmentCentreCode())));

        return searchSupportMixin.map(viewedProducts, cart);

    }


    /**
     * Interface: GET /customer/recentlyviewed
     * <p>
     * <p>
     * Display list of recently viewed products.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON array of object ProductSearchResultRO</td><td>
     * <pre><code>
     * 	[
     * 	  {
     * 	    "maxOrderQuantity" : null,
     * 	    "minOrderQuantity" : null,
     * 	    "displayDescription" : {
     * 	      "uk" : "Trust Isotto Wired Mini Mouse...",
     * 	      "ru" : "Trust Isotto Wired Mini Mouse...",
     * 	      "en" : "Trust Isotto Wired Mini Mouse..."
     * 	    },
     * 	    "code" : "19733",
     * 	    "availability" : 1,
     * 	    "displayName" : {
     * 	      "uk" : "Isotto Wired Mini Mouse",
     * 	      "ru" : "Isotto Wired Mini Mouse",
     * 	      "en" : "Isotto Wired Mini Mouse"
     * 	    },
     * 	    "defaultSkuCode" : "19733",
     * 	    "name" : "Isotto Wired Mini Mouse",
     * 	    "stepOrderQuantity" : null,
     * 	    "availablefrom" : null,
     * 	    "id" : 184,
     * 	    "availableto" : null,
     * 	    "manufacturerCode" : null,
     * 	    "skus" : null,
     * 	    "productAvailabilityModel" : {
     * 	      "firstAvailableSkuCode" : "19733",
     * 	      "available" : true,
     * 	      "defaultSkuCode" : "19733",
     * 	      "inStock" : true,
     * 	      "skuCodes" : [
     * 	        "19733"
     * 	      ],
     * 	      "perpetual" : false,
     * 	      "availableToSellQuantity" : {
     * 	        "19733" : 99
     * 	      }
     * 	    },
     * 	    "price" : {
     * 	      "symbol" : "€",
     * 	      "quantity" : 1,
     * 	      "regularPrice" : 643.2,
     * 	      "salePrice" : null,
     * 	      "discount" : null,
     * 	      "currency" : "EUR",
     * 	      "symbolPosition" : "before"
     * 	    },
     * 	    "featured" : false,
     * 	    "defaultImage" : "Trust-Isotto-Wired-Mini-Mouse_19733_a.jpg",
     * 	    "multisku" : false,
     * 	    "description" : "Trust Isotto Wired Mini Mouse..."
     * 	  },
     * 	...
     * 	]
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of products
     */
    @RequestMapping(
            value = "/recentlyviewed",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody List<ProductSearchResultRO> viewRecent(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                                final HttpServletRequest request,
                                                                final HttpServletResponse response) {
        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return viewRecentlyViewedInternal();

    }


    /**
     * Interface: GET /customer/recentlyviewed
     * <p>
     * <p>
     * Display list of recently viewed products.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>XML array of object ProductSearchResultRO</td><td>
     * <pre><code>
     * 	&lt;products&gt;
     * 	    &lt;product&gt;
     * 	        &lt;availability&gt;1&lt;/availability&gt;
     * 	        &lt;code&gt;19733&lt;/code&gt;
     * 	        &lt;default-image&gt;Trust-Isotto-Wired-Mini-Mouse_19733_a.jpg&lt;/default-image&gt;
     * 	        &lt;default-sku-code&gt;19733&lt;/default-sku-code&gt;
     * 	        &lt;description&gt;Trust Isotto Wired Mini Mouse...&lt;/description&gt;
     * 	        &lt;display-descriptions&gt;
     * 	            &lt;entry lang="uk"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	            &lt;entry lang="en"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	            &lt;entry lang="ru"&gt;Trust Isotto Wired Mini Mouse...&lt;/entry&gt;
     * 	        &lt;/display-descriptions&gt;
     * 	        &lt;display-names&gt;
     * 	            &lt;entry lang="uk"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	            &lt;entry lang="en"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	            &lt;entry lang="ru"&gt;Isotto Wired Mini Mouse&lt;/entry&gt;
     * 	        &lt;/display-names&gt;
     * 	        &lt;featured&gt;false&lt;/featured&gt;
     * 	        &lt;id&gt;184&lt;/id&gt;
     * 	        &lt;multisku&gt;false&lt;/multisku&gt;
     * 	        &lt;name&gt;Isotto Wired Mini Mouse&lt;/name&gt;
     * 	        &lt;price&gt;
     * 	            &lt;currency&gt;EUR&lt;/currency&gt;
     * 	            &lt;quantity&gt;1.00&lt;/quantity&gt;
     * 	            &lt;regular-price&gt;643.20&lt;/regular-price&gt;
     * 	            &lt;symbol&gt;€&lt;/symbol&gt;
     * 	            &lt;symbol-position&gt;before&lt;/symbol-position&gt;
     * 	        &lt;/price&gt;
     * 	        &lt;product-availability&gt;
     * 	            &lt;available&gt;true&lt;/available&gt;
     * 	            &lt;ats-quantity&gt;
     * 	                &lt;entry sku="19733"&gt;99.000&lt;/entry&gt;
     * 	            &lt;/ats-quantity&gt;
     * 	            &lt;default-sku&gt;19733&lt;/default-sku&gt;
     * 	            &lt;first-available-sku&gt;19733&lt;/first-available-sku&gt;
     * 	            &lt;in-stock&gt;true&lt;/in-stock&gt;
     * 	            &lt;perpetual&gt;false&lt;/perpetual&gt;
     * 	            &lt;sku-codes&gt;19733&lt;/sku-codes&gt;
     * 	        &lt;/product-availability&gt;
     * 	    &lt;/product&gt;
     * 	...
     * 	&lt;/products&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of products
     */
    @RequestMapping(
            value = "/recentlyviewed",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE }
    )
    public @ResponseBody ProductSearchResultListRO viewRecentXML(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                                 final HttpServletRequest request,
                                                                 final HttpServletResponse response) {
        cartMixin.throwSecurityExceptionIfRequireLoggedIn();
        cartMixin.persistShoppingCart(request, response);
        return new ProductSearchResultListRO(viewRecentlyViewedInternal());

    }



    /**
     * Interface: GET /customer/orders
     * <p>
     * <p>
     * Display list of all customer orders.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <p>
     * NONE
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example of OrderHistoryRO</td><td>
     * <pre><code>
     * {
     *     "since": null,
     *     "orders": [{
     *         "customerorderId": 1,
     *         "ordernum": "150407172907-1",
     *         "pgLabel": "courierPaymentGatewayLabel",
     *         "billingAddress": "In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123",
     *         "shippingAddress": "In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123",
     *         "cartGuid": "47a030e4-a681-4f15-a851-ff150e0107c6",
     *         "currency": "EUR",
     *         "orderMessage": "My Message",
     *         "orderStatus": "os.waiting",
     *         "multipleShipmentOption": false,
     *         "orderTimestamp": 1428424147489,
     *         "email": "bob.doe@yc-checkout-json.com",
     *         "firstname": "Bob",
     *         "lastname": "Doe",
     *         "middlename": null,
     *         "customerId": 1,
     *         "shopId": 10,
     *         "code": "SHOIP1",
     *         "total": {
     *             "listSubTotal": 99.99,
     *             "saleSubTotal": 99.99,
     *             "nonSaleSubTotal": 99.99,
     *             "priceSubTotal": 99.99,
     *             "orderPromoApplied": false,
     *             "appliedOrderPromo": null,
     *             "subTotal": 99.99,
     *             "subTotalTax": 16.67,
     *             "subTotalAmount": 99.99,
     *             "deliveryListCost": 10.00,
     *             "deliveryCost": 10.00,
     *             "deliveryPromoApplied": false,
     *             "appliedDeliveryPromo": null,
     *             "deliveryTax": 1.67,
     *             "deliveryCostAmount": 10.00,
     *             "total": 109.99,
     *             "totalTax": 18.34,
     *             "listTotalAmount": 109.99,
     *             "totalAmount": 109.99
     *         },
     *         "deliveries": [{
     *             "customerOrderDeliveryId": 1,
     *             "deliveryNum": "150407172907-1-0",
     *             "refNo": null,
     *             "carrierSlaId": 4,
     *             "carrierSlaName": "14",
     *             "carrierSlaDisplayNames": null,
     *             "carrierId": 1,
     *             "carrierName": "Test carrier 1",
     *             "carrierDisplayNames": null,
     *             "deliveryStatus": "ds.inventory.reserved",
     *             "deliveryGroup": "D1",
     *             "total": {
     *                 "listSubTotal": 99.99,
     *                 "saleSubTotal": 99.99,
     *                 "nonSaleSubTotal": 99.99,
     *                 "priceSubTotal": 99.99,
     *                 "orderPromoApplied": false,
     *                 "appliedOrderPromo": null,
     *                 "subTotal": 99.99,
     *                 "subTotalTax": 16.67,
     *                 "subTotalAmount": 99.99,
     *                 "deliveryListCost": 10.00,
     *                 "deliveryCost": 10.00,
     *                 "deliveryPromoApplied": false,
     *                 "appliedDeliveryPromo": null,
     *                 "deliveryTax": 1.67,
     *                 "deliveryCostAmount": 10.00,
     *                 "total": 109.99,
     *                 "totalTax": 18.34,
     *                 "listTotalAmount": 109.99,
     *                 "totalAmount": 109.99
     *             },
     *             "deliveryItems": [{
     *                 "productSkuCode": "BENDER-ua",
     *                 "quantity": 1.00,
     *                 "price": 99.99,
     *                 "salePrice": 99.99,
     *                 "listPrice": 99.99,
     *                 "netPrice": 83.32,
     *                 "grossPrice": 99.99,
     *                 "taxRate": 20.00,
     *                 "taxCode": "VAT",
     *                 "taxExclusiveOfPrice": false,
     *                 "gift": false,
     *                 "promoApplied": false,
     *                 "appliedPromo": null,
     *                 "customerOrderDeliveryDetId": 0
     *             }]
     *         }],
     *         "orderItems": [{
     *             "productSkuCode": "BENDER-ua",
     *             "quantity": 1.00,
     *             "price": 99.99,
     *             "salePrice": 99.99,
     *             "listPrice": 99.99,
     *             "netPrice": 83.32,
     *             "grossPrice": 99.99,
     *             "taxRate": 20.00,
     *             "taxCode": "VAT",
     *             "taxExclusiveOfPrice": false,
     *             "gift": false,
     *             "promoApplied": false,
     *             "appliedPromo": null,
     *             "customerOrderDetId": 0
     *         }]
     *     }]
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example of OrderHistoryRO</td><td>
     * <pre><code>
     * &lt;order-history&gt;
     * 	&lt;order cart-guid="83bdd599-1a0a-4cb6-95e9-77dbd724e886" shop-code="SHOIP1" currency="EUR" customer-id="2" customer-order-id="2"
     * 	          multiple-shipment-option="false" order-status="os.waiting" order-timestamp="2015-04-07T17:29:11.530+01:00"
     * 	          ordernum="150407172911-2" pg-label="courierPaymentGatewayLabel" shop-id="10"&gt;
     * 		&lt;billing-address&gt;In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123&lt;/billing-address&gt;
     * 		&lt;deliveries&gt;
     * 			&lt;delivery carrier-id="1" carriersla-id="4" customer-order-delivery-id="2" delivery-group="D1" delivery-num="150407172911-2-0"
     * 		       	         delivery-status="ds.inventory.reserved"&gt;
     * 				&lt;carrier-name&gt;Test carrier 1&lt;/carrier-name&gt;
     * 				&lt;carriersla-name&gt;14&lt;/carriersla-name&gt;
     * 				&lt;delivery-items&gt;
     * 					&lt;delivery-item customer-order-delivery-det-id="0" gift="false" gross-price="99.99" list-price="99.99" net-price="83.32"
     * 					                  price="99.99" product-sku-code="BENDER-ua" promo-applied="false" quantity="1.00" sale-price="99.99"
     * 					                  tax-code="VAT" tax-exclusive-of-price="false" tax-rate="20.00"/&gt;
     * 				&lt;/delivery-items&gt;
     * 				&lt;total delivery-cost="10.00" delivery-cost-amount="10.00" delivery-list-cost="10.00" delivery-promo-applied="false"
     * 				          delivery-tax="1.67" list-sub-total="99.99" list-total-amount="109.99" non-sale-sub-total="99.99" promo-applied="false"
     * 				          price-sub-total="99.99" sale-sub-total="99.99" sub-total="99.99" sub-total-amount="99.99" sub-total-tax="16.67"
     * 				          total="109.99" total-amount="109.99" total-tax="18.34"/&gt;
     * 			&lt;/delivery&gt;
     * 		&lt;/deliveries&gt;
     * 		&lt;email&gt;bob.doe@-checkout-xml.com&lt;/email&gt;
     * 		&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 		&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 		&lt;order-items&gt;
     * 			&lt;order-item customer-order-det-id="0" gift="false" gross-price="99.99" list-price="99.99" net-price="83.32" price="99.99"
     * 			               product-sku-code="BENDER-ua" promo-applied="false" quantity="1.00" sale-price="99.99" tax-code="VAT"
     * 			               tax-exclusive-of-price="false" tax-rate="20.00"/&gt;
     * 		&lt;/order-items&gt;
     * 		&lt;order-message&gt;My Message&lt;/order-message&gt;
     * 		&lt;shipping-address&gt;In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123&lt;/shipping-address&gt;
     * 		&lt;total delivery-cost="10.00" delivery-cost-amount="10.00" delivery-list-cost="10.00" delivery-promo-applied="false"
     * 		          delivery-tax="1.67" list-sub-total="99.99" list-total-amount="109.99" non-sale-sub-total="99.99" promo-applied="false"
     * 		          price-sub-total="99.99" sale-sub-total="99.99" sub-total="99.99" sub-total-amount="99.99" sub-total-tax="16.67"
     * 		          total="109.99" total-amount="109.99" total-tax="18.34"/&gt;
     * 	&lt;/order&gt;
     * &lt;/order-history&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param request request
     * @param response response
     *
     * @return list of all orders
     */
    @RequestMapping(
            value = "/orders",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody OrderHistoryRO viewOrderHistory(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                         final HttpServletRequest request,
                                                         final HttpServletResponse response) {

        return viewOrderHistorySince(requestToken, null, request, response);

    }


    /**
     * Interface: GET /customer/orders/{date}
     * <p>
     * <p>
     * Display list of all customer orders.
     * <p>
     * <p>
     * <h3>Headers for operation</h3><p>
     * <table border="1">
     *     <tr><td>Accept</td><td>application/json or application/xml</td></tr>
     *     <tr><td>yc</td><td>token uuid</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Parameters for operation</h3><p>
     * <p>
     * <table border="1">
     *     <tr><td>date</td><td>date in "yyyy-MM-dd" format</td></tr>
     * </table>
     * <p>
     * <p>
     * <h3>Output</h3><p>
     * <table border="1">
     *     <tr><td>JSON example of OrderHistoryRO</td><td>
     * <pre><code>
     * {
     *     "since": 1388534400000,
     *     "orders": [{
     *         "customerorderId": 1,
     *         "ordernum": "150407172907-1",
     *         "pgLabel": "courierPaymentGatewayLabel",
     *         "billingAddress": "In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123",
     *         "shippingAddress": "In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123",
     *         "cartGuid": "47a030e4-a681-4f15-a851-ff150e0107c6",
     *         "currency": "EUR",
     *         "orderMessage": "My Message",
     *         "orderStatus": "os.waiting",
     *         "multipleShipmentOption": false,
     *         "orderTimestamp": 1428424147489,
     *         "email": "bob.doe@yc-checkout-json.com",
     *         "firstname": "Bob",
     *         "lastname": "Doe",
     *         "middlename": null,
     *         "customerId": 1,
     *         "shopId": 10,
     *         "code": "SHOIP1",
     *         "total": {
     *             "listSubTotal": 99.99,
     *             "saleSubTotal": 99.99,
     *             "nonSaleSubTotal": 99.99,
     *             "priceSubTotal": 99.99,
     *             "orderPromoApplied": false,
     *             "appliedOrderPromo": null,
     *             "subTotal": 99.99,
     *             "subTotalTax": 16.67,
     *             "subTotalAmount": 99.99,
     *             "deliveryListCost": 10.00,
     *             "deliveryCost": 10.00,
     *             "deliveryPromoApplied": false,
     *             "appliedDeliveryPromo": null,
     *             "deliveryTax": 1.67,
     *             "deliveryCostAmount": 10.00,
     *             "total": 109.99,
     *             "totalTax": 18.34,
     *             "listTotalAmount": 109.99,
     *             "totalAmount": 109.99
     *         },
     *         "deliveries": [{
     *             "customerOrderDeliveryId": 1,
     *             "deliveryNum": "150407172907-1-0",
     *             "refNo": null,
     *             "carrierSlaId": 4,
     *             "carrierSlaName": "14",
     *             "carrierSlaDisplayNames": null,
     *             "carrierId": 1,
     *             "carrierName": "Test carrier 1",
     *             "carrierDisplayNames": null,
     *             "deliveryStatus": "ds.inventory.reserved",
     *             "deliveryGroup": "D1",
     *             "total": {
     *                 "listSubTotal": 99.99,
     *                 "saleSubTotal": 99.99,
     *                 "nonSaleSubTotal": 99.99,
     *                 "priceSubTotal": 99.99,
     *                 "orderPromoApplied": false,
     *                 "appliedOrderPromo": null,
     *                 "subTotal": 99.99,
     *                 "subTotalTax": 16.67,
     *                 "subTotalAmount": 99.99,
     *                 "deliveryListCost": 10.00,
     *                 "deliveryCost": 10.00,
     *                 "deliveryPromoApplied": false,
     *                 "appliedDeliveryPromo": null,
     *                 "deliveryTax": 1.67,
     *                 "deliveryCostAmount": 10.00,
     *                 "total": 109.99,
     *                 "totalTax": 18.34,
     *                 "listTotalAmount": 109.99,
     *                 "totalAmount": 109.99
     *             },
     *             "deliveryItems": [{
     *                 "productSkuCode": "BENDER-ua",
     *                 "quantity": 1.00,
     *                 "price": 99.99,
     *                 "salePrice": 99.99,
     *                 "listPrice": 99.99,
     *                 "netPrice": 83.32,
     *                 "grossPrice": 99.99,
     *                 "taxRate": 20.00,
     *                 "taxCode": "VAT",
     *                 "taxExclusiveOfPrice": false,
     *                 "gift": false,
     *                 "promoApplied": false,
     *                 "appliedPromo": null,
     *                 "customerOrderDeliveryDetId": 0
     *             }]
     *         }],
     *         "orderItems": [{
     *             "productSkuCode": "BENDER-ua",
     *             "quantity": 1.00,
     *             "price": 99.99,
     *             "salePrice": 99.99,
     *             "listPrice": 99.99,
     *             "netPrice": 83.32,
     *             "grossPrice": 99.99,
     *             "taxRate": 20.00,
     *             "taxCode": "VAT",
     *             "taxExclusiveOfPrice": false,
     *             "gift": false,
     *             "promoApplied": false,
     *             "appliedPromo": null,
     *             "customerOrderDetId": 0
     *         }]
     *     }]
     * }
     * </code></pre>
     *     </td></tr>
     *     <tr><td>XML example of OrderHistoryRO</td><td>
     * <pre><code>
     * &lt;order-history since="2014-01-01T00:00:00Z"&gt;
     * 	&lt;order cart-guid="83bdd599-1a0a-4cb6-95e9-77dbd724e886" shop-code="SHOIP1" currency="EUR" customer-id="2" customer-order-id="2"
     * 	          multiple-shipment-option="false" order-status="os.waiting" order-timestamp="2015-04-07T17:29:11.530+01:00"
     * 	          ordernum="150407172911-2" pg-label="courierPaymentGatewayLabel" shop-id="10"&gt;
     * 		&lt;billing-address&gt;In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123&lt;/billing-address&gt;
     * 		&lt;deliveries&gt;
     * 			&lt;delivery carrier-id="1" carriersla-id="4" customer-order-delivery-id="2" delivery-group="D1" delivery-num="150407172911-2-0"
     * 		       	         delivery-status="ds.inventory.reserved"&gt;
     * 				&lt;carrier-name&gt;Test carrier 1&lt;/carrier-name&gt;
     * 				&lt;carriersla-name&gt;14&lt;/carriersla-name&gt;
     * 				&lt;delivery-items&gt;
     * 					&lt;delivery-item customer-order-delivery-det-id="0" gift="false" gross-price="99.99" list-price="99.99" net-price="83.32"
     * 					                  price="99.99" product-sku-code="BENDER-ua" promo-applied="false" quantity="1.00" sale-price="99.99"
     * 					                  tax-code="VAT" tax-exclusive-of-price="false" tax-rate="20.00"/&gt;
     * 				&lt;/delivery-items&gt;
     * 				&lt;total delivery-cost="10.00" delivery-cost-amount="10.00" delivery-list-cost="10.00" delivery-promo-applied="false"
     * 				          delivery-tax="1.67" list-sub-total="99.99" list-total-amount="109.99" non-sale-sub-total="99.99" promo-applied="false"
     * 				          price-sub-total="99.99" sale-sub-total="99.99" sub-total="99.99" sub-total-amount="99.99" sub-total-tax="16.67"
     * 				          total="109.99" total-amount="109.99" total-tax="18.34"/&gt;
     * 			&lt;/delivery&gt;
     * 		&lt;/deliveries&gt;
     * 		&lt;email&gt;bob.doe@-checkout-xml.com&lt;/email&gt;
     * 		&lt;firstname&gt;Bob&lt;/firstname&gt;
     * 		&lt;lastname&gt;Doe&lt;/lastname&gt;
     * 		&lt;order-items&gt;
     * 			&lt;order-item customer-order-det-id="0" gift="false" gross-price="99.99" list-price="99.99" net-price="83.32" price="99.99"
     * 			               product-sku-code="BENDER-ua" promo-applied="false" quantity="1.00" sale-price="99.99" tax-code="VAT"
     * 			               tax-exclusive-of-price="false" tax-rate="20.00"/&gt;
     * 		&lt;/order-items&gt;
     * 		&lt;order-message&gt;My Message&lt;/order-message&gt;
     * 		&lt;shipping-address&gt;In the middle of  0001 Nowhere GB GB-GB Bob Doe 123123123&lt;/shipping-address&gt;
     * 		&lt;total delivery-cost="10.00" delivery-cost-amount="10.00" delivery-list-cost="10.00" delivery-promo-applied="false"
     * 		          delivery-tax="1.67" list-sub-total="99.99" list-total-amount="109.99" non-sale-sub-total="99.99" promo-applied="false"
     * 		          price-sub-total="99.99" sale-sub-total="99.99" sub-total="99.99" sub-total-amount="99.99" sub-total-tax="16.67"
     * 		          total="109.99" total-amount="109.99" total-tax="18.34"/&gt;
     * 	&lt;/order&gt;
     * &lt;/order-history&gt;
     * </code></pre>
     *     </td></tr>
     * </table>
     *
     * @param date date in "yyyy-MM-dd" format
     * @param request request
     * @param response response
     *
     * @return list of all orders since date
     */
    @RequestMapping(
            value = "/orders/{date}",
            method = RequestMethod.GET,
            produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public @ResponseBody OrderHistoryRO viewOrderHistorySince(final @RequestHeader(value = "yc", required = false) String requestToken,
                                                              final @PathVariable(value = "date") String date,
                                                              final HttpServletRequest request,
                                                              final HttpServletResponse response) {

        cartMixin.throwSecurityExceptionIfNotLoggedIn();
        LocalDateTime since = null;
        if (StringUtils.isNotBlank(date)) {
            since = DateUtils.ldtParseSDT(date);
            if (since == null) {
                since = TimeContext.getLocalDateTime().minusDays(30);
                LOG.warn("Invalid date {} using {}", date, since);
            }
        }

        cartMixin.persistShoppingCart(request, response);
        final Shop shop = cartMixin.getCurrentShop();
        final ShoppingCart cart = cartMixin.getCurrentCart();

        final Customer customer = customerServiceFacade.getCustomerByEmail(shop, cart.getCustomerEmail());
        final OrderHistoryRO history = new OrderHistoryRO();
        history.setSince(since);

        if (customer != null) {

            // all in DB
            final List<CustomerOrder> orders = customerOrderService.findCustomerOrders(customer, since);

            // remove temporary orders
            orders.removeIf(order -> CustomerOrder.ORDER_STATUS_NONE.equals(order.getOrderStatus())
                    || order.getShop().getShopId() != cart.getShoppingContext().getCustomerShopId());

            // sort
            orders.sort(new CustomerOrderComparator());

            final List<OrderRO> ros = new ArrayList<>();
            for (final CustomerOrder order : orders) {

                final Pair<String, Boolean> symbol = currencySymbolService.getCurrencySymbol(order.getCurrency());
                final String cartCurrencySymbol = symbol.getFirst();
                final String cartCurrencySymbolPosition = symbol.getSecond() != null && symbol.getSecond() ? "after" : "before";

                final Total total = checkoutServiceFacade.getOrderTotal(order);
                final OrderRO ro = mappingMixin.map(order, OrderRO.class, CustomerOrder.class);
                ro.setTotal(mappingMixin.map(total, CartTotalRO.class, Total.class));
                ro.setSymbol(cartCurrencySymbol);
                ro.setSymbolPosition(cartCurrencySymbolPosition);

                for (final CustomerOrderDelivery delivery : order.getDelivery()) {
                    final DeliveryRO dro = mappingMixin.map(delivery, DeliveryRO.class, CustomerOrderDelivery.class);
                    final Total dtotal = checkoutServiceFacade.getOrderDeliveryTotal(order, delivery);
                    dro.setTotal(mappingMixin.map(dtotal, CartTotalRO.class, Total.class));
                    ro.getDeliveries().add(dro);
                }

                ros.add(ro);
            }

            history.setOrders(ros);

        }

        return history;

    }

}
