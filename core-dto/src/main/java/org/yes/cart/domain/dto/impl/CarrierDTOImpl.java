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
import org.yes.cart.domain.dto.CarrierDTO;

import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
@Dto
public class CarrierDTOImpl implements CarrierDTO {

    @DtoField(value = "carrierId", readOnly = true)
    private long carrierId;

    @DtoField(value = "name")
    private String name;

    @DtoField(value = "description")
    private String description;

    @DtoField(value = "worldwide")
    private boolean worldwide;

    @DtoField(value = "country")
    private boolean country;

    @DtoField(value = "state")
    private boolean state;

    @DtoField(value = "local")
    private boolean local;

    @DtoField(value = "displayName", converter = "i18nModelConverter")
    private Map<String, String> displayNames;

    @DtoField(value = "displayDescription", converter = "i18nModelConverter")
    private Map<String, String> displayDescriptions;

    /** {@inheritDoc} */
    @Override
    public long getCarrierId() {
        return carrierId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getId() {
        return carrierId;
    }

    /** {@inheritDoc} */
    @Override
    public void setCarrierId(final long carrierId) {
        this.carrierId = carrierId;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(final String name) {
        this.name = name;
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayNames(final Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }


    /** {@inheritDoc} */
    @Override
    public Map<String, String> getDisplayDescriptions() {
        return displayDescriptions;
    }

    /** {@inheritDoc} */
    @Override
    public void setDisplayDescriptions(final Map<String, String> displayDescriptions) {
        this.displayDescriptions = displayDescriptions;
    }


    /** {@inheritDoc} */
    @Override
    public boolean isWorldwide() {
        return worldwide;
    }

    /** {@inheritDoc} */
    @Override
    public void setWorldwide(final boolean worldwide) {
        this.worldwide = worldwide;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCountry() {
        return country;
    }

    /** {@inheritDoc} */
    @Override
    public void setCountry(final boolean country) {
        this.country = country;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isState() {
        return state;
    }

    /** {@inheritDoc} */
    @Override
    public void setState(final boolean state) {
        this.state = state;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isLocal() {
        return local;
    }

    /** {@inheritDoc} */
    @Override
    public void setLocal(final boolean local) {
        this.local = local;
    }

    @Override
    public String toString() {
        return "CarrierDTOImpl{" +
                "carrierId=" + carrierId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", worldwide=" + worldwide +
                ", country=" + country +
                ", state=" + state +
                ", local=" + local +
                '}';
    }
}
