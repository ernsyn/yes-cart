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

package org.yes.cart.domain.ro;

import org.yes.cart.domain.ro.xml.impl.StringMapAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 22/03/2015
 * Time: 15:29
 */
@XmlRootElement(name = "search-result")
public abstract class AbstractSearchResultRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    private SearchRO search;

    private int totalResults;

    private String itemImageWidth;
    private String itemImageHeight;

    private List<String> pageAvailableSize;
    private Map<String, String> pageAvailableSort;

    private FilteredNavigationRO filteredNavigation;

    private String uitemplate;
    private String uitemplateFallback;

    public SearchRO getSearch() {
        return search;
    }

    public void setSearch(final SearchRO search) {
        this.search = search;
    }

    @XmlElement(name = "total-results")
    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final int totalResults) {
        this.totalResults = totalResults;
    }

    @XmlElement(name = "image-width")
    public String getItemImageWidth() {
        return itemImageWidth;
    }

    public void setItemImageWidth(final String itemImageWidth) {
        this.itemImageWidth = itemImageWidth;
    }

    @XmlElement(name = "image-height")
    public String getItemImageHeight() {
        return itemImageHeight;
    }

    public void setItemImageHeight(final String itemImageHeight) {
        this.itemImageHeight = itemImageHeight;
    }

    @XmlElementWrapper(name = "page-available-sizes")
    @XmlElement(name = "page-available-size")
    public List<String> getPageAvailableSize() {
        return pageAvailableSize;
    }

    public void setPageAvailableSize(final List<String> pageAvailableSize) {
        this.pageAvailableSize = pageAvailableSize;
    }

    @XmlJavaTypeAdapter(StringMapAdapter.class)
    @XmlElement(name = "page-available-sort")
    public Map<String, String> getPageAvailableSort() {
        return pageAvailableSort;
    }

    public void setPageAvailableSort(final Map<String, String> pageAvailableSort) {
        this.pageAvailableSort = pageAvailableSort;
    }

    @XmlElement(name = "filtered-navigation")
    public FilteredNavigationRO getFilteredNavigation() {
        return filteredNavigation;
    }

    public void setFilteredNavigation(final FilteredNavigationRO filteredNavigation) {
        this.filteredNavigation = filteredNavigation;
    }


    public String getUitemplate() {
        return uitemplate;
    }

    public void setUitemplate(final String uitemplate) {
        this.uitemplate = uitemplate;
    }

    @XmlElement(name = "uitemplate-fallback")
    public String getUitemplateFallback() {
        return uitemplateFallback;
    }

    public void setUitemplateFallback(final String uitemplateFallback) {
        this.uitemplateFallback = uitemplateFallback;
    }

}
