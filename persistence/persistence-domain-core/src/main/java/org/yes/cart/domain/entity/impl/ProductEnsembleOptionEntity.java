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


import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductEnsembleOption;
import org.yes.cart.domain.entity.ProductSku;

import java.time.Instant;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 27 0ct 2012
 * Time: 9:10 AM
 */
public class ProductEnsembleOptionEntity implements ProductEnsembleOption, java.io.Serializable {

    private long ensembleOptId;
    private long version;

    private int qty;
    private Product product;
    private ProductSku sku;
    private Instant createdTimestamp;
    private Instant updatedTimestamp;
    private String createdBy;
    private String updatedBy;
    private String guid;

    public ProductEnsembleOptionEntity() {
    }




    @Override
    public int getQty() {
        return this.qty;
    }

    @Override
    public void setQty(final int qty) {
        this.qty = qty;
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
    public ProductSku getSku() {
        return this.sku;
    }

    @Override
    public void setSku(final ProductSku sku) {
        this.sku = sku;
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
    public long getEnsembleOptId() {
        return this.ensembleOptId;
    }

    @Override
    public long getId() {
        return this.ensembleOptId;
    }

    @Override
    public void setEnsembleOptId(final long ensembleOptId) {
        this.ensembleOptId = ensembleOptId;
    }

    @Override
    public long getVersion() {
        return version;
    }

    public void setVersion(final long version) {
        this.version = version;
    }
}


