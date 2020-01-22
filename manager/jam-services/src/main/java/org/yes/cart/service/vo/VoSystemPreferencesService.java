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
import org.yes.cart.domain.vo.VoAttrValueSystem;

import java.util.List;

/**
 * User: denispavlov
 * Date: 12/08/2016
 * Time: 17:35
 */
public interface VoSystemPreferencesService {

    /**
     * Get supported attributes by system
     *
     * @param includeSecure include secure attributes
     *
     * @return attributes
     *
     * @throws Exception errors
     */
    List<VoAttrValueSystem> getSystemPreferences(boolean includeSecure) throws Exception;


    /**
     * Update the system attributes.
     *
     * @param vo shop attributes to update, boolean indicates if this attribute is to be removed (true) or not (false)
     * @param includeSecure include secure attributes
     *
     * @return shop attributes.
     *
     * @throws Exception errors
     */
    List<VoAttrValueSystem> update(List<MutablePair<VoAttrValueSystem, Boolean>> vo, boolean includeSecure) throws Exception;

}
