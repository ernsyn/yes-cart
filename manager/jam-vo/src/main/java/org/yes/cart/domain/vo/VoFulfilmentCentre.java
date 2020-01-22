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

package org.yes.cart.domain.vo;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import org.yes.cart.domain.dto.ShopWarehouseDTO;
import org.yes.cart.domain.vo.matcher.NoopMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * User: denispavlov
 * Date: 19/08/2016
 * Time: 10:35
 */
@Dto
public class VoFulfilmentCentre extends VoFulfilmentCentreInfo {

    @DtoCollection(
            value = "warehouseShop",
            dtoBeanKey = "VoFulfilmentCentreShopLink",
            entityGenericType = ShopWarehouseDTO.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = NoopMatcher.class,
            readOnly = true
    )
    private List<VoFulfilmentCentreShopLink> fulfilmentShops = new ArrayList<>();

    public List<VoFulfilmentCentreShopLink> getFulfilmentShops() {
        return fulfilmentShops;
    }

    public void setFulfilmentShops(final List<VoFulfilmentCentreShopLink> fulfilmentShops) {
        this.fulfilmentShops = fulfilmentShops;
    }
}
