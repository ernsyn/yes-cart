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

package org.yes.cart.domain.entity.impl;


import org.apache.commons.lang.StringUtils;
import org.yes.cart.domain.entity.ManagerShop;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ManagerEntity implements org.yes.cart.domain.entity.Manager, java.io.Serializable {

    private long managerId;
    private long version;

    private Collection<ManagerShop> shops = new ArrayList<>(0);

    private Collection<String> productSupplierCatalogs = Collections.emptyList();
    private String productSupplierCatalogsInternal;

    private Collection<String> categoryCatalogs = Collections.emptyList();
    private String categoryCatalogsInternal;

    private String email;
    private String salutation;
    private String firstname;
    private String lastname;
    private String middlename;
    private String dashboardWidgets;
    private String password;
    private Instant passwordExpiry;
    private String authToken;
    private boolean enabled;
    private Instant authTokenExpiry;
    private String companyName1;
    private String companyName2;
    private String companyDepartment;

    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ManagerEntity() {
    }



    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public String getFirstname() {
        return this.firstname;
    }

    @Override
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    @Override
    public String getLastname() {
        return this.lastname;
    }

    @Override
    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getMiddlename() {
        return this.middlename;
    }

    @Override
    public void setMiddlename(final String middlename) {
        this.middlename = middlename;
    }

    @Override
    public String getSalutation() {
        return salutation;
    }

    @Override
    public void setSalutation(final String salutation) {
        this.salutation = salutation;
    }

    @Override
    public String getDashboardWidgets() {
        return dashboardWidgets;
    }

    public void setDashboardWidgets(final String dashboardWidgets) {
        this.dashboardWidgets = dashboardWidgets;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public Instant getPasswordExpiry() {
        return passwordExpiry;
    }

    @Override
    public void setPasswordExpiry(final Instant passwordExpiry) {
        this.passwordExpiry = passwordExpiry;
    }

    @Override
    public String getAuthToken() {
        return authToken;
    }

    @Override
    public void setAuthToken(final String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Instant getAuthTokenExpiry() {
        return authTokenExpiry;
    }

    @Override
    public void setAuthTokenExpiry(final Instant authTokenExpiry) {
        this.authTokenExpiry = authTokenExpiry;
    }

    @Override
    public String getCompanyName1() {
        return companyName1;
    }

    @Override
    public void setCompanyName1(final String companyName1) {
        this.companyName1 = companyName1;
    }

    @Override
    public String getCompanyName2() {
        return companyName2;
    }

    @Override
    public void setCompanyName2(final String companyName2) {
        this.companyName2 = companyName2;
    }

    @Override
    public String getCompanyDepartment() {
        return companyDepartment;
    }

    @Override
    public void setCompanyDepartment(final String companyDepartment) {
        this.companyDepartment = companyDepartment;
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Instant getCreatedTimestamp() {
        return this.createdTimestamp;
    }

    @Override
    public void setCreatedTimestamp(final Instant createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    @Override
    public Instant getUpdatedTimestamp() {
        return this.updatedTimestamp;
    }

    @Override
    public void setUpdatedTimestamp(final Instant updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public String getCreatedBy() {
        return this.createdBy;
    }

    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String getUpdatedBy() {
        return this.updatedBy;
    }

    @Override
    public void setUpdatedBy(final String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getGuid() {
        return this.guid;
    }

    @Override
    public void setGuid(final String guid) {
        this.guid = guid;
    }

    @Override
    public long getManagerId() {
        return this.managerId;
    }

    @Override
    public long getId() {
        return this.managerId;
    }

    @Override
    public void setManagerId(final long managerId) {
        this.managerId = managerId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public Collection<ManagerShop> getShops() {
        return this.shops;
    }

    @Override
    public void setShops(final Collection<ManagerShop> shops) {
        this.shops = shops;
    }

    @Override
    public Collection<String> getProductSupplierCatalogs() {
        return productSupplierCatalogs;
    }

    @Override
    public void setProductSupplierCatalogs(final Collection<String> productSupplierCatalogs) {
        this.productSupplierCatalogs = productSupplierCatalogs;
        if (productSupplierCatalogs != null) {
            this.productSupplierCatalogsInternal = StringUtils.join(productSupplierCatalogs, ",");
        } else {
            this.productSupplierCatalogsInternal = null;
        }
    }

    public String getProductSupplierCatalogsInternal() {
        return productSupplierCatalogsInternal;
    }

    public void setProductSupplierCatalogsInternal(final String productSupplierCatalogsInternal) {
        this.productSupplierCatalogsInternal = productSupplierCatalogsInternal;
        if (StringUtils.isNotBlank(productSupplierCatalogsInternal)) {
            this.productSupplierCatalogs = Arrays.asList(StringUtils.split(productSupplierCatalogsInternal, ','));
        } else {
            this.productSupplierCatalogs = Collections.emptyList();
        }
    }

    @Override
    public Collection<String> getCategoryCatalogs() {
        return categoryCatalogs;
    }

    @Override
    public void setCategoryCatalogs(final Collection<String> categoryCatalogs) {
        this.categoryCatalogs = categoryCatalogs;
        if (categoryCatalogs != null) {
            this.categoryCatalogsInternal = StringUtils.join(categoryCatalogs, ",");
        } else {
            this.categoryCatalogsInternal = null;
        }
    }

    public String getCategoryCatalogsInternal() {
        return categoryCatalogsInternal;
    }

    public void setCategoryCatalogsInternal(final String categoryCatalogsInternal) {
        this.categoryCatalogsInternal = categoryCatalogsInternal;
        if (StringUtils.isNotBlank(categoryCatalogsInternal)) {
            this.categoryCatalogs = Arrays.asList(StringUtils.split(categoryCatalogsInternal, ','));
        } else {
            this.categoryCatalogs = Collections.emptyList();
        }
    }
}


