/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo;

import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttrValueBrand;
import org.yes.cart.domain.vo.VoBrand;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoSearchResult;

import java.util.List;

/**
 * User: denispavlov
 */
public interface VoBrandService {

    /**
     * Get all brands in the system, filtered by criteria and according to rights, up to max
     *
     * @return list of brands
     *
     * @throws Exception errors
     */
    VoSearchResult<VoBrand> getFilteredBrands(VoSearchContext filter) throws Exception;

    /**
     * Get brand by id.
     *
     * @param id brand id
     *
     * @return brand vo
     *
     * @throws Exception errors
     */
    VoBrand getBrandById(long id) throws Exception;

    /**
     * Update given brand.
     *
     * @param vo brand to update
     *
     * @return updated instance
     *
     * @throws Exception errors
     */
    VoBrand updateBrand(VoBrand vo) throws Exception;

    /**
     * Create new brand
     *
     * @param vo given instance to persist
     *
     * @return persisted instance
     *
     * @throws Exception errors
     */
    VoBrand createBrand(VoBrand vo) throws Exception;

    /**
     * Remove brand by id.
     *
     * @param id brand id
     *
     * @throws Exception errors
     */
    void removeBrand(long id) throws Exception;


    /**
     * Get supported attributes by given brand
     *
     * @param brandId given brand id
     *
     * @return attributes
     *
     * @throws Exception errors
     */
    List<VoAttrValueBrand> getBrandAttributes(long brandId) throws Exception;


    /**
     * Update the brand attributes.
     *
     * @param vo brand attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     *
     * @return brand attributes.
     *
     * @throws Exception errors
     */
    List<VoAttrValueBrand> updateBrandAttributes(List<MutablePair<VoAttrValueBrand, Boolean>> vo) throws Exception;


}
