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

package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.SeoImageDTO;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class SeoImageDTOImpl implements SeoImageDTO {

    private static final long serialVersionUID = 2010717L;

    @DtoField(value = "seoImageId", readOnly = true)
    private long seoImageId;

    @DtoField(value = "imageName")
    private String imageName;

    @DtoField(value = "alt")
    private String alt;

    @DtoField(value = "title")
    private String title;

    @DtoField(value = "displayAlt", converter = "i18nModelConverter")
    private Map<String, String> displayAlts;

    @DtoField(value = "displayTitle", converter = "i18nModelConverter")
    private Map<String, String> displayTitles;


    /** {@inheritDoc}*/
    @Override
    public long getSeoImageId() {
        return seoImageId;
    }

    /** {@inheritDoc}*/
    @Override
    public long getId() {
        return seoImageId;
    }

    /** {@inheritDoc}*/
    @Override
    public void setSeoImageId(final long seoImageId) {
        this.seoImageId = seoImageId;
    }

    /** {@inheritDoc}*/
    @Override
    public String getImageName() {
        return imageName;
    }

    /** {@inheritDoc}*/
    @Override
    public void setImageName(final String imageName) {
        this.imageName = imageName;
    }

    /** {@inheritDoc}*/
    @Override
    public String getAlt() {
        return alt;
    }

    /** {@inheritDoc}*/
    @Override
    public void setAlt(final String alt) {
        this.alt = alt;
    }

    /** {@inheritDoc}*/
    @Override
    public Map<String, String> getDisplayAlts() {
        return displayAlts;
    }

    /** {@inheritDoc}*/
    @Override
    public void setDisplayAlts(final Map<String, String> displayAlts) {
        this.displayAlts = displayAlts;
    }

    /** {@inheritDoc}*/
    @Override
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc}*/
    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    /** {@inheritDoc}*/
    @Override
    public Map<String, String> getDisplayTitles() {
        return displayTitles;
    }

    /** {@inheritDoc}*/
    @Override
    public void setDisplayTitles(final Map<String, String> displayTitles) {
        this.displayTitles = displayTitles;
    }

    @Override
    public String toString() {
        return "SeoImageDTOImpl{" +
                "seoImageId=" + seoImageId +
                ", imageName='" + imageName + '\'' +
                ", alt='" + alt + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
