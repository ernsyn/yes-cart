/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.*;
import org.yes.cart.service.cluster.ProductAsyncSupport;
import org.yes.cart.service.endpoint.FulfilmentEndpointController;
import org.yes.cart.service.vo.VoFulfilmentService;

import java.util.List;

/**
 * User: denispavlov
 * Date: 19/08/2016
 * Time: 11:57
 */
@Component
public class FulfilmentEndpointControllerImpl implements FulfilmentEndpointController {

    private final VoFulfilmentService voFulfilmentService;
    private final ProductAsyncSupport productAsyncSupport;

    @Autowired
    public FulfilmentEndpointControllerImpl(final VoFulfilmentService voFulfilmentService,
                                            final ProductAsyncSupport productAsyncSupport) {
        this.voFulfilmentService = voFulfilmentService;
        this.productAsyncSupport = productAsyncSupport;
    }

    @Override
    public @ResponseBody
    VoSearchResult<VoFulfilmentCentre> getFilteredFulfilmentCentres(@RequestBody final VoSearchContext filter) throws Exception {
        return voFulfilmentService.getFilteredFulfilmentCentres(filter);
    }

    @Override
    public @ResponseBody
    List<VoShopFulfilmentCentre> getShopFulfilmentCentres(@PathVariable("id") final long shopId) throws Exception {
        return voFulfilmentService.getShopFulfilmentCentres(shopId);
    }

    @Override
    public @ResponseBody
    VoFulfilmentCentre getFulfilmentCentreById(@PathVariable("id") final long id) throws Exception {
        return voFulfilmentService.getFulfilmentCentreById(id);
    }

    @Override
    public @ResponseBody
    VoFulfilmentCentre createFulfilmentCentre(@RequestBody final VoFulfilmentCentre vo) throws Exception {
        return voFulfilmentService.createFulfilmentCentre(vo);
    }

    @Override
    public @ResponseBody
    VoFulfilmentCentre createShopFulfilmentCentre(@RequestBody final VoFulfilmentCentreInfo vo,@PathVariable("id")  final long shopId) throws Exception {
        return voFulfilmentService.createShopFulfilmentCentre(vo, shopId);
    }

    @Override
    public @ResponseBody
    VoFulfilmentCentre updateFulfilmentCentre(@RequestBody final VoFulfilmentCentre vo) throws Exception {
        return voFulfilmentService.updateFulfilmentCentre(vo);
    }

    @Override
    public @ResponseBody
    List<VoShopFulfilmentCentre> updateShopFulfilmentCentres(@RequestBody final List<VoShopFulfilmentCentre> vo) throws Exception {
        return voFulfilmentService.updateShopFulfilmentCentres(vo);
    }

    @Override
    public @ResponseBody
    void removeFulfilmentCentre(@PathVariable("id") final long id) throws Exception {
        voFulfilmentService.removeFulfilmentCentre(id);
    }


    @Override
    public @ResponseBody
    VoSearchResult<VoInventory> getFilteredInventory(@RequestBody final VoSearchContext filter) throws Exception {
        return voFulfilmentService.getFilteredInventory(filter);
    }

    @Override
    public @ResponseBody
    VoInventory getInventoryById(@PathVariable("id") final long id) throws Exception {
        return voFulfilmentService.getInventoryById(id);
    }

    @Override
    public @ResponseBody
    VoInventory createInventory(@RequestBody final VoInventory vo) throws Exception {
        final VoInventory inventory = voFulfilmentService.createInventory(vo);
        productAsyncSupport.asyncIndexSku(inventory.getSkuCode());
        return inventory;
    }

    @Override
    public @ResponseBody
    VoInventory updateInventory(@RequestBody final VoInventory vo) throws Exception {
        final VoInventory inventory = voFulfilmentService.updateInventory(vo);
        productAsyncSupport.asyncIndexSku(inventory.getSkuCode());
        return inventory;
    }

    @Override
    public @ResponseBody
    void removeInventory(@PathVariable("id") final long id) throws Exception {
        final VoInventory inventory = voFulfilmentService.getInventoryById(id);
        if (inventory != null) {
            voFulfilmentService.removeInventory(id);
            productAsyncSupport.asyncIndexSku(inventory.getSkuCode());
        }
    }
}
