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

package org.yes.cart.domain.dto;

import org.yes.cart.domain.entity.Identifiable;

import java.util.List;
import java.util.Map;

/**
 * Warehouse DTO.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface WarehouseDTO extends Identifiable {


    /**
     * Get primary key.
     *
     * @return primary key
     */
    long getWarehouseId();

    /**
     * Set primary key
     *
     * @param warehouseId primary key.
     */
    void setWarehouseId(long warehouseId);

    /**
     * Get warehouse code.
     *
     * @return warehouse code.
     */
    String getCode();

    /**
     * Set warehouse code.
     *
     * @param code warehouse code.
     */
    void setCode(String code);


    /**
     * Warehouse name.
     *
     * @return warehouse name.
     */
    String getName();

    /**
     * Set Warehouse name.
     *
     * @param name name of warehouse
     */
    void setName(String name);



    /**
     * Warehouse name.
     *
     * @return localised locale => name pairs.
     */
    Map<String, String> getDisplayNames();

    /**
     * Get warehouse name
     *
     * @param names localised locale => name pairs
     */
    void setDisplayNames(Map<String, String> names);


    /**
     * @return description.
     */
    String getDescription();

    /**
     * Set description.
     *
     * @param description warehouse description.
     */
    void setDescription(String description);


    // address part of warehouse begin

    /**
     * Get country.
     *
     * @return country.
     */
    String getCountryCode();

    /**
     * Set country.
     *
     * @param countryCode country to set
     */
    void setCountryCode(String countryCode);

    /**
     * State or province code.
     *
     * @return state or province code
     */
    String getStateCode();

    /**
     * Set state or province.
     *
     * @param stateCode state.
     */
    void setStateCode(final String stateCode);

    /**
     * Get city.
     *
     * @return city
     */
    String getCity();

    /**
     * Set city
     *
     * @param city value to set
     */
    void setCity(String city);

    /**
     * Get postcode.
     *
     * @return post code
     */
    String getPostcode();

    /**
     * Set post code
     *
     * @param postcode value to set
     */
    void setPostcode(String postcode);

    // address part of warehouse end



    /**
     * Lead time necessary to prepare the order before the items are physically shipped out.
     *
     * @return lead time
     */
    int getDefaultStandardStockLeadTime();

    /**
     * Lead time necessary to prepare the order before the items are physically shipped out.
     * This is default (average time). Product specific lead times will override this.
     *
     * @param defaultStandardStockLeadTime lead time
     */
    void setDefaultStandardStockLeadTime(int defaultStandardStockLeadTime);



    /**
     * Lead time necessary to prepare the order before the items are physically shipped out.
     * This is default (average time). Product specific lead times will override this.
     *
     * @return lead time
     */
    int getDefaultBackorderStockLeadTime();

    /**
     * Lead time necessary to prepare the order before the items are physically shipped out.
     * This is default (average time). Product specific lead times will override this.
     *
     * @param defaultBackorderStockLeadTime lead time
     */
    void setDefaultBackorderStockLeadTime(int defaultBackorderStockLeadTime);



    /**
     * Determine if this warehouse supports multiple shipment or not.
     *
     * @return true if multiple shipment is supported
     */
    boolean isMultipleShippingSupported();

    /**
     * Determine if this warehouse supports multiple shipment or not.
     *
     * @param multipleShippingSupported multiple shipment
     */
    void setMultipleShippingSupported(boolean multipleShippingSupported);


    /**
     * Get shop warehouse relation.
     *
     * @return  shop warehouse relation.
     */
    List<ShopWarehouseDTO> getWarehouseShop();

    /**
     * Set shop warehouse relation.
     * 
     * @param warehouseShop  shop warehouse relation.
     */
    void setWarehouseShop(List<ShopWarehouseDTO> warehouseShop);

}
