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

import org.yes.cart.domain.i18n.I18NModel;

import java.util.Collection;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 * <p/>
 * Product Stock Keeping Unit (SKU). Sku is product or product variation.
 * By default one product has at least one sku. Product can also have several SKU
 * in case of multisku product.
 *
 * Attribute values refine the base product definition with specifics
 * e.g. Product is a T-Shirt, therefore SKU would be T-Shirt, color: red, size: M.
 */
public interface ProductSku extends Auditable, Attributable, Rankable, Nameable, Seoable, Codable, Taggable {

    /**
     * @return sku primary key
     */
    long getSkuId();

    /**
     * Set primary key value.
     *
     * @param skuId primary key value.
     */
    void setSkuId(long skuId);

    /**
     * Get the sku code.
     *
     * @return sku code
     */
    @Override
    String getCode();

    /**
     * Stock keeping unit code.
     * Limitation code must not contains underscore
     *
     * @param code code
     */
    @Override
    void setCode(String code);

    /**
     * Get the non unique product code.
     *
     * @return product code.
     */
    String getManufacturerCode();

    /**
     * Manufacturer non unique product code.
     * Limitation code must not contains underscore
     *
     * @param code manufacturer code
     */
    void setManufacturerCode(String code);

    /**
     * Get the non unique product code.
     *
     * @return product code.
     */
    String getManufacturerPartCode();

    /**
     * Manufacturer non unique product code.
     * Limitation code must not contains underscore
     *
     * @param code manufacturer code
     */
    void setManufacturerPartCode(String code);

    /**
     * Get the non unique product code.
     *
     * @return product code.
     */
    String getSupplierCode();

    /**
     * Supplier non unique product code.
     * Limitation code must not contains underscore
     *
     * @param code supplier code
     */
    void setSupplierCode(String code);

    /**
     * Get the non unique product catalog code.
     *
     * @return catalog code.
     */
    String getSupplierCatalogCode();

    /**
     * Supplier non unique catalog code.
     * Limitation code must not contains underscore
     *
     * @param code catalog code
     */
    void setSupplierCatalogCode(String code);

    /**
     * Get the product.
     *
     * @return {@link Product}
     */
    Product getProduct();

    /**
     * Set {@link Product}.
     *
     * @param product {@link Product}
     */
    void setProduct(Product product);

    /**
     * Get all products attributes.
     *
     * @return collection of product attributes.
     */
    Collection<AttrValueProductSku> getAttributes();


    /**
     * Set collection of products attributes.
     *
     * @param attribute collection of products attributes
     */
    void setAttributes(Collection<AttrValueProductSku> attribute);



    /**
     * Get the sku name.
     *
     * @return sku name.
     */
    @Override
    String getName();

    /**
     * Set sku name.
     *
     * @param name sku name.
     */
    @Override
    void setName(String name);

    /**
     * display name.
     *
     * @return display name.
     */
    I18NModel getDisplayName();

    /**
     * Get display name
     *
     * @param name display name
     */
    void setDisplayName(I18NModel name);


    /**
     * Get sku decription.
     *
     * @return sku description.
     */
    @Override
    String getDescription();

    /**
     * Set sku description.
     *
     * @param description sku description.
     */
    @Override
    void setDescription(String description);

    /**
     * Get description for indexing
     *
     * @return as is description
     */
    I18NModel getDisplayDescription();

    /**
     * {@inheritDoc}
     */
    @Override
    int getRank();

    /**
     * {@inheritDoc}
     */
    @Override
    void setRank(int rank);

    /**
     * Get the sku bar code.
     *
     * @return Sku bar code
     */
    String getBarCode();

    /**
     * Set sku bar code.
     *
     * @param barCode bar code.
     */
    void setBarCode(String barCode);

}


