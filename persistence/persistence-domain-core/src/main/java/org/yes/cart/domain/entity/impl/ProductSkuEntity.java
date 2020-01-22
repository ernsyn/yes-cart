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
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueProductSku;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */

public class ProductSkuEntity implements org.yes.cart.domain.entity.ProductSku, java.io.Serializable {

    private long skuId;
    private long version;

    private String code;
    private String manufacturerCode;
    private String manufacturerPartCode;
    private String supplierCode;
    private String supplierCatalogCode;
    private String name;
    private String displayNameInternal;
    private I18NModel displayName;
    private String description;
    private String tag;
    private Product product;
    private int rank;
    private String barCode;
    private Collection<AttrValueProductSku> attributes = new ArrayList<>(0);
    private SeoEntity seoInternal;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductSkuEntity() {
    }



    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public String getManufacturerCode() {
        return manufacturerCode;
    }

    @Override
    public void setManufacturerCode(final String manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    @Override
    public String getManufacturerPartCode() {
        return manufacturerPartCode;
    }

    @Override
    public void setManufacturerPartCode(final String manufacturerPartCode) {
        this.manufacturerPartCode = manufacturerPartCode;
    }

    @Override
    public String getSupplierCode() {
        return supplierCode;
    }

    @Override
    public void setSupplierCode(final String supplierCode) {
        this.supplierCode = supplierCode;
    }

    @Override
    public String getSupplierCatalogCode() {
        return supplierCatalogCode;
    }

    @Override
    public void setSupplierCatalogCode(final String supplierCatalogCode) {
        this.supplierCatalogCode = supplierCatalogCode;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    public String getDisplayNameInternal() {
        return displayNameInternal;
    }

    public void setDisplayNameInternal(final String displayNameInternal) {
        this.displayNameInternal = displayNameInternal;
        this.displayName = new StringI18NModel(displayNameInternal);
    }

    @Override
    public I18NModel getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(final I18NModel displayName) {
        this.displayName = displayName;
        this.displayNameInternal = displayName != null ? displayName.toString() : null;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(final String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public I18NModel getDisplayDescription() {
        final StringI18NModel model = new StringI18NModel();
        for (AttrValue attr : attributes) {
            if (attr.getAttributeCode() != null &&
                    attr.getAttributeCode().startsWith(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX)) {
                model.putValue(getLocale(attr.getAttributeCode()), attr.getVal());
            }
        }
        return model;
    }

    String getLocale(final String attrCode) {
        return attrCode.substring(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX.length());
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setTag(final String tag) {
        this.tag = tag;
    }

    @Override
    public Product getProduct() {
        return this.product;
    }

    @Override
    public void setProduct(final Product product) {
        this.product = product;
    }

    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public void setRank(final int rank) {
        this.rank = rank;
    }

    @Override
    public String getBarCode() {
        return this.barCode;
    }

    @Override
    public void setBarCode(final String barCode) {
        this.barCode = barCode;
    }

    @Override
    public Collection<AttrValueProductSku> getAttributes() {
        return this.attributes;
    }

    @Override
    public void setAttributes(final Collection<AttrValueProductSku> attributes) {
        this.attributes = attributes;
    }

    public SeoEntity getSeoInternal() {
        return this.seoInternal;
    }

    public void setSeoInternal(final SeoEntity seo) {
        this.seoInternal = seo;
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
    public long getSkuId() {
        return this.skuId;
    }

    @Override
    public long getId() {
        return this.skuId;
    }

    @Override
    public void setSkuId(final long skuId) {
        this.skuId = skuId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }

    @Override
    public Collection<AttrValueProductSku> getAttributesByCode(final String attributeCode) {
        final Collection<AttrValueProductSku> result = new ArrayList<>();
        if (attributeCode != null && this.attributes != null) {
            for (AttrValueProductSku attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    result.add(attrValue);
                }
            }
        }
        return result;
    }

    @Override
    public AttrValueProductSku getAttributeByCode(final String attributeCode) {
        if (attributeCode == null) {
            return null;
        }
        if (this.attributes != null) {
            for (AttrValueProductSku attrValue : this.attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    return attrValue;
                }
            }
        }
        return null;
    }



    @Override
    public String getAttributeValueByCode(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null ? val.getVal() : null;
    }


    @Override
    public boolean isAttributeValueByCodeTrue(final String attributeCode) {
        final AttrValue val = getAttributeByCode(attributeCode);
        return val != null && Boolean.valueOf(val.getVal());
    }



    @Override
    public Map<String, AttrValue> getAllAttributesAsMap() {
        final Map<String, AttrValue> rez = new HashMap<>();
        if (this.attributes != null) {
            for (AttrValue attrValue : this.attributes) {
                if (attrValue != null && attrValue.getAttributeCode() != null) {
                    rez.put(attrValue.getAttributeCode(), attrValue);
                }
            }
        }
        return rez;
    }

    @Override
    public Collection<AttrValue> getAllAttributes() {
        return new ArrayList<>(attributes);
    }


    @Override
    public Seo getSeo() {
        SeoEntity seo = getSeoInternal();
        if (seo == null) {
            seo = new SeoEntity();
            this.setSeoInternal(seo);
        }
        return seo;
    }

    @Override
    public void setSeo(final Seo seo) {
        this.setSeoInternal((SeoEntity) seo);
    }

    /**
     * Get default image, which is stored into lucene index, to reduce db hit.
     * @return default product image if found, otherwise no image constant.
     */
    public String getDefaultImage() {
        final String attr = getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0");
        if (StringUtils.isBlank(attr)) {

            return Constants.NO_IMAGE;

        }
        return attr;
    }


}


