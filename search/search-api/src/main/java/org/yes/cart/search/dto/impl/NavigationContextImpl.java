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

package org.yes.cart.search.dto.impl;

import org.springframework.util.CollectionUtils;
import org.yes.cart.search.dto.NavigationContext;

import java.util.*;

/**
 * User: denispavlov
 * Date: 25/11/2014
 * Time: 22:25
 */
public class NavigationContextImpl<T> implements NavigationContext<T> {

    private final long shopId;
    private final long customerShopId;
    private final String customerLanguage;
    private final List<Long> categories;
    private final boolean includeSubCategories;
    private final Map<String, List<String>> navigationParameters;

    private final T productQuery;
    private final T productSkuQuery;

    public NavigationContextImpl(final long shopId,
                                 final long customerShopId,
                                 final String customerLanguage,
                                 final List<Long> categories,
                                 final boolean includeSubCategories,
                                 final Map<String, List<String>> navigationParameters,
                                 final T productQuery,
                                 final T productSkuQuery) {
        this.shopId = shopId;
        this.customerShopId = customerShopId;
        this.customerLanguage = customerLanguage;
        this.categories = categories;
        this.includeSubCategories = includeSubCategories;
        if (navigationParameters == null) {
            this.navigationParameters = Collections.emptyMap();
        } else {
            this.navigationParameters = getMutableCopyFilterParametersInternal(navigationParameters);
        }
        this.productSkuQuery = productSkuQuery;
        this.productQuery = productQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getShopId() {
        return shopId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCustomerShopId() {
        return customerShopId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustomerLanguage() {
        return customerLanguage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Long> getCategories() {
        if (categories == null) {
            return null;
        }
        return Collections.unmodifiableList(categories);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIncludeSubCategories() {
        return includeSubCategories;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGlobal() {
        return CollectionUtils.isEmpty(categories);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFilteredBy(final String attribute) {
        return navigationParameters.containsKey(attribute);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getFilterParametersNames() {
        return Collections.unmodifiableSet(navigationParameters.keySet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getFilterParameterValues(final String parameterName) {
        final List<String> values = navigationParameters.get(parameterName);
        if (values == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<String>> getMutableCopyFilterParameters() {
        return getMutableCopyFilterParametersInternal(navigationParameters);
    }

    private Map<String, List<String>> getMutableCopyFilterParametersInternal(final Map<String, List<String>> origin) {
        final Map<String, List<String>> copy = new LinkedHashMap<>();
        for (final Map.Entry<String, List<String>> entry : origin.entrySet()) {
            copy.put(entry.getKey(), entry.getValue() == null ? null : new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getProductQuery() {
        return productQuery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getProductSkuQuery() {
        return productSkuQuery;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NavigationContextImpl)) return false;

        final NavigationContextImpl that = (NavigationContextImpl) o;

        if (shopId != that.shopId) return false;
        if (customerShopId != that.customerShopId) return false;
        if (includeSubCategories != that.includeSubCategories) return false;
        if (customerLanguage != null ? !customerLanguage.equals(that.customerLanguage) : that.customerLanguage != null)
            return false;
        if (categories != null) {
            if (that.categories == null || categories.size() != that.categories.size()) return false;
            for (int i = 0; i < categories.size(); i++) {
                if (!categories.get(i).equals(that.categories.get(i))) return false;
            }
        } else if (that.categories != null) return false;

        if (navigationParameters.size() != that.navigationParameters.size()) return false;
        for (final Map.Entry<String, List<String>> entry : navigationParameters.entrySet()) {
            final List<String> thatValue = (List<String>) that.navigationParameters.get(entry.getKey());
            if (entry.getValue() != null) {
                if (thatValue == null || entry.getValue().size() != thatValue.size()) return false;
                for (int i = 0; i < thatValue.size(); i++) {
                    if (!entry.getValue().get(i).equals(thatValue.get(i))) return false;
                }
            } else if (thatValue != null) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (shopId ^ (shopId >>> 32));
        result = 31 * result + (int) (customerShopId ^ (customerShopId >>> 32));
        result = 31 * result + (customerLanguage != null ? customerLanguage.hashCode() : 0);
        result = 31 * result + (includeSubCategories ? 1 : 0);
        if (categories != null) {
            for (final Long category : categories) {
                result = 31 * result + category.hashCode();
            }
        }
        for (final Map.Entry<String, List<String>> entry : navigationParameters.entrySet()) {
            result = 31 * result + entry.getKey().hashCode();
            if (entry.getValue() != null) {
                for (final String value : entry.getValue()) {
                    result = 31 * result + value.hashCode();
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "NavigationContextImpl{" +
                "shopId=" + shopId +
                ", customerShopId=" + customerShopId +
                ", customerLanguage='" + customerLanguage + '\'' +
                ", categories=" + categories +
                ", includeSubCategories=" + includeSubCategories +
                ", navigationParameters=" + navigationParameters +
                '}';
    }
}
