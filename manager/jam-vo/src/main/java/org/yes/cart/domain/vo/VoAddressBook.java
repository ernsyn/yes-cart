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

package org.yes.cart.domain.vo;

import org.yes.cart.domain.misc.MutablePair;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 31/01/2017
 * Time: 14:38
 */
public class VoAddressBook {

    private List<VoAddress> addresses = new ArrayList<>();

    private long formattingShopId;

    private List<MutablePair<Long, String>> formattedAddresses = new ArrayList<>();

    private List<MutablePair<String, List<VoAttrValue>>> addressForm = new ArrayList<>();

    private List<VoLocation> billingCountries = new ArrayList<>();
    private List<VoLocation> shippingCountries = new ArrayList<>();


    public List<VoAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<VoAddress> addresses) {
        this.addresses = addresses;
    }

    public long getFormattingShopId() {
        return formattingShopId;
    }

    public void setFormattingShopId(final long formattingShopId) {
        this.formattingShopId = formattingShopId;
    }

    public List<MutablePair<Long, String>> getFormattedAddresses() {
        return formattedAddresses;
    }

    public void setFormattedAddresses(final List<MutablePair<Long, String>> formattedAddresses) {
        this.formattedAddresses = formattedAddresses;
    }

    public List<MutablePair<String, List<VoAttrValue>>> getAddressForm() {
        return addressForm;
    }

    public void setAddressForm(final List<MutablePair<String, List<VoAttrValue>>> addressForm) {
        this.addressForm = addressForm;
    }

    public List<VoLocation> getBillingCountries() {
        return billingCountries;
    }

    public void setBillingCountries(final List<VoLocation> billingCountries) {
        this.billingCountries = billingCountries;
    }

    public List<VoLocation> getShippingCountries() {
        return shippingCountries;
    }

    public void setShippingCountries(final List<VoLocation> shippingCountries) {
        this.shippingCountries = shippingCountries;
    }
}
