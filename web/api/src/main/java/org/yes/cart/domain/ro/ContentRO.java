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

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.matcher.impl.NoopMatcher;
import org.yes.cart.domain.entity.AttrValueContent;
import org.yes.cart.domain.ro.xml.impl.I18nMapAdapter;
import org.yes.cart.domain.ro.xml.impl.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 19/08/2014
 * Time: 23:44
 */
@Dto
@XmlRootElement(name = "content")
public class ContentRO implements Serializable {

    private static final long serialVersionUID = 20150301L;

    @DtoField(value = "contentId", readOnly = true)
    private long contentId;

    @DtoField(value = "parentId", readOnly = true)
    private long parentId;

    @DtoField(value = "rank", readOnly = true)
    private int rank;

    @DtoField(value = "name", readOnly = true)
    private String name;

    @DtoField(value = "displayName", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> displayNames;

    @DtoField(value = "description", readOnly = true)
    private String description;

    @DtoField(value = "uitemplate", readOnly = true)
    private String uitemplate;
    private String uitemplateFallback;

    @DtoField(value = "disabled", readOnly = true)
    private boolean disabled;

    @DtoField(value = "availablefrom", readOnly = true)
    private LocalDateTime availablefrom;

    @DtoField(value = "availableto", readOnly = true)
    private LocalDateTime availableto;

    @DtoField(value = "seo.uri", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String uri;

    @DtoField(value = "seo.title", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String title;

    @DtoField(value = "seo.metakeywords", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String metakeywords;

    @DtoField(value = "seo.metadescription", entityBeanKeys = "org.yes.cart.domain.entity.Seo", readOnly = true)
    private String metadescription;

    @DtoField(value = "seo.displayTitle", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> displayTitles;

    @DtoField(value = "seo.displayMetakeywords", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> displayMetakeywords;

    @DtoField(value = "seo.displayMetadescription", converter = "i18nModelConverter", readOnly = true)
    private Map<String, String> displayMetadescriptions;

    @DtoCollection(
            value = "attributes",
            dtoBeanKey = "org.yes.cart.domain.ro.AttrValueContentRO",
            entityGenericType = AttrValueContent.class,
            entityCollectionClass = HashSet.class,
            dtoCollectionClass = HashSet.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private Set<AttrValueContentRO> attributes;

    private String contentBody;

    private List<BreadcrumbRO> breadcrumbs = Collections.EMPTY_LIST;

    @XmlElement(name = "content-body")
    public String getContentBody() {
        return contentBody;
    }

    public void setContentBody(final String contentBody) {
        this.contentBody = contentBody;
    }

    @XmlAttribute(name = "content-id")
    public long getContentId() {
        return contentId;
    }

    public void setContentId(final long contentId) {
        this.contentId = contentId;
    }

    @XmlAttribute(name = "parent-id")
    public long getParentId() {
        return parentId;
    }

    public void setParentId(final long parentId) {
        this.parentId = parentId;
    }

    @XmlElementWrapper(name = "breadcrumbs")
    @XmlElement(name = "breadcrumb")
    public List<BreadcrumbRO> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(final List<BreadcrumbRO> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-names")
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
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

    @XmlAttribute(name = "disabled")
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getAvailablefrom() {
        return availablefrom;
    }

    public void setAvailablefrom(final LocalDateTime availablefrom) {
        this.availablefrom = availablefrom;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    public LocalDateTime getAvailableto() {
        return availableto;
    }

    public void setAvailableto(final LocalDateTime availableto) {
        this.availableto = availableto;
    }

    @XmlElementWrapper(name = "attribute-values")
    @XmlElement(name = "attribute-value")
    public Set<AttrValueContentRO> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Set<AttrValueContentRO> attributes) {
        this.attributes = attributes;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-titles")
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    public String getMetakeywords() {
        return metakeywords;
    }

    public void setMetakeywords(final String metakeywords) {
        this.metakeywords = metakeywords;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-metakeywords")
    public Map<String, String> getDisplayMetakeywords() {
        return displayMetakeywords;
    }

    public void setDisplayMetakeywords(final Map<String, String> displayMetakeywords) {
        this.displayMetakeywords = displayMetakeywords;
    }

    public String getMetadescription() {
        return metadescription;
    }

    public void setMetadescription(final String metadescription) {
        this.metadescription = metadescription;
    }

    @XmlJavaTypeAdapter(I18nMapAdapter.class)
    @XmlElement(name = "display-metadescription")
    public Map<String, String> getDisplayMetadescriptions() {
        return displayMetadescriptions;
    }

    public void setDisplayMetadescriptions(final Map<String, String> displayMetadescriptions) {
        this.displayMetadescriptions = displayMetadescriptions;
    }


}
