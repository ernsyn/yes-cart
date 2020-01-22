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

package org.yes.cart.web.service.rest;

import org.hamcrest.CustomMatchers;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.yes.cart.domain.entity.ShoppingCartState;
import org.yes.cart.domain.ro.ProductReferenceListRO;
import org.yes.cart.domain.ro.ProductReferenceRO;
import org.yes.cart.domain.ro.SearchRO;
import org.yes.cart.service.domain.ShoppingCartStateService;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.support.tokendriven.CartRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.YcMockMvcResultHandlers.print;

/**
 * User: denispavlov
 * Date: 28/03/2015
 * Time: 19:01
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:testApplicationContext.xml")
@WebAppConfiguration(value = "src/test/webapp")
public class BrowsingSuiteTest extends AbstractSuiteTest {

    private final Locale locale = Locale.ENGLISH;

    @Autowired
    private ShoppingCartStateService shoppingCartStateService;

    @Autowired
    private CartRepository cartRepository;


    @Test
    public void testCategoryJson() throws Exception {

        mockMvc.perform(get("/categories/menu")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));


        mockMvc.perform(get("/categories/menu/106")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Retro Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/categories/view/106")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }

    @Test
    public void testCategoryXML() throws Exception {

        mockMvc.perform(get("/categories/menu")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/categories/menu/106")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Retro Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/categories/view/106")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Fun Gadgets")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }



    @Test
    public void testContentJson() throws Exception {

        mockMvc.perform(get("/content/menu/SHOIP1_menu_item_1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("menu item 1")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/content/view/SHOIP1_menu_item_1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Menu Item Content")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }

    @Test
    public void testContentXML() throws Exception {

        mockMvc.perform(get("/content/menu/SHOIP1_menu_item_1")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("menu item 1")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

        mockMvc.perform(get("/content/view/SHOIP1_menu_item_1")
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Menu Item Content")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }


    @Test
    public void testProductJson() throws Exception {

        reindex();


        final MvcResult firstLoad =
        mockMvc.perform(get("/cart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                .andReturn();

        final String uuid = firstLoad.getResponse().getHeader("yc");

        final ShoppingCartState state = shoppingCartStateService.findByGuid(uuid);
        assertNotNull(uuid, state);
        assertNull(state.getCustomerEmail());

        final ShoppingCart cart = cartRepository.getShoppingCart(uuid);
        assertNotNull(uuid, cart);
        assertNull(cart.getCustomerEmail());


        mockMvc.perform(get("/products/9998/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("productAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuQuantityModel")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));

        final ProductReferenceListRO pRefs = new ProductReferenceListRO();
        pRefs.setReferences(new ArrayList<>());
        final ProductReferenceRO pRef9998 = new ProductReferenceRO();
        pRef9998.setReference("9998");
        pRef9998.setSupplier("WAREHOUSE_2");
        pRefs.getReferences().add(pRef9998);
        final ProductReferenceRO pRef9999 = new ProductReferenceRO();
        pRef9999.setReference("9999");
        pRef9999.setSupplier("WAREHOUSE_2");
        pRefs.getReferences().add(pRef9999);

        final byte[] bodyPRefs = toJsonBytes(pRefs);

        mockMvc.perform(post("/products/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(bodyPRefs))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"BENDER-ua\"")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("\"BENDER\"")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(content().string(StringContains.containsString("productAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuQuantityModel")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/skus/9998/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("skuAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuQuantityModel")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/skus/BENDER-ua/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("skuAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuQuantityModel")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));

        final ProductReferenceListRO sRefs = new ProductReferenceListRO();
        sRefs.setReferences(new ArrayList<>());
        final ProductReferenceRO sRef9998 = new ProductReferenceRO();
        sRef9998.setReference("9998");
        sRef9998.setSupplier("WAREHOUSE_2");
        sRefs.getReferences().add(sRef9998);
        final ProductReferenceRO sRef9999 = new ProductReferenceRO();
        sRef9999.setReference("9999");
        sRef9999.setSupplier("WAREHOUSE_2");
        sRefs.getReferences().add(sRef9999);

        final byte[] bodySRefs = toJsonBytes(sRefs);

        mockMvc.perform(post("/skus/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(bodySRefs))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"BENDER-ua\"")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("\"BENDER\"")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(content().string(StringContains.containsString("skuAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuQuantityModel")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/products/9999/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/skus/9999/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/products/9998/associations/accessories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(content().string(StringContains.containsString("\"BENDER\"")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(content().string(StringContains.containsString("skuAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuQuantityModel")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/recentlyviewed")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("skuAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuQuantityModel")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));


    }


    @Test
    public void testProductXML() throws Exception {

        reindex();

        final MvcResult firstLoad =
        mockMvc.perform(get("/cart")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()))
                .andReturn();

        final String uuid = firstLoad.getResponse().getHeader("yc");


        final ShoppingCartState state = shoppingCartStateService.findByGuid(uuid);
        assertNotNull(uuid, state);
        assertNull(state.getCustomerEmail());

        final ShoppingCart cart = cartRepository.getShoppingCart(uuid);
        assertNotNull(uuid, cart);
        assertNull(cart.getCustomerEmail());


        mockMvc.perform(get("/products/9998/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("product-availability")))
                .andExpect(content().string(StringContains.containsString("sku-availability")))
                .andExpect(content().string(StringContains.containsString("sku-quantity")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));


        final ProductReferenceListRO pRefs = new ProductReferenceListRO();
        pRefs.setReferences(new ArrayList<>());
        final ProductReferenceRO pRef9998 = new ProductReferenceRO();
        pRef9998.setReference("9998");
        pRef9998.setSupplier("WAREHOUSE_2");
        pRefs.getReferences().add(pRef9998);
        final ProductReferenceRO pRef9999 = new ProductReferenceRO();
        pRef9999.setReference("9999");
        pRef9999.setSupplier("WAREHOUSE_2");
        pRefs.getReferences().add(pRef9999);

        final byte[] bodyPRefs = toJsonBytes(pRefs);


        mockMvc.perform(post("/products/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(bodyPRefs))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString(">BENDER-ua<")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString(">BENDER<")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(content().string(StringContains.containsString("product-availability")))
                .andExpect(content().string(StringContains.containsString("sku-availability")))
                .andExpect(content().string(StringContains.containsString("sku-quantity")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));


        mockMvc.perform(get("/skus/9998/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("sku-availability")))
                .andExpect(content().string(StringContains.containsString("sku-quantity")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/skus/BENDER-ua/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("sku-availability")))
                .andExpect(content().string(StringContains.containsString("sku-quantity")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));


        final ProductReferenceListRO sRefs = new ProductReferenceListRO();
        sRefs.setReferences(new ArrayList<>());
        final ProductReferenceRO sRef9998 = new ProductReferenceRO();
        sRef9998.setReference("9998");
        sRef9998.setSupplier("WAREHOUSE_2");
        sRefs.getReferences().add(sRef9998);
        final ProductReferenceRO sRef9999 = new ProductReferenceRO();
        sRef9999.setReference("9999");
        sRef9999.setSupplier("WAREHOUSE_2");
        sRefs.getReferences().add(sRef9999);

        final byte[] bodySRefs = toJsonBytes(sRefs);

        mockMvc.perform(get("/skus/list")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid)
                    .content(bodySRefs))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString(">BENDER-ua<")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString(">BENDER<")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(content().string(StringContains.containsString("sku-availability")))
                .andExpect(content().string(StringContains.containsString("sku-quantity")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/products/9999/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/skus/9999/supplier/WAREHOUSE_2")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/products/9998/associations/accessories")
                    .contentType(MediaType.APPLICATION_XML)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("Bender Bending Rodriguez")))
                .andExpect(content().string(StringContains.containsString(">BENDER<")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9999")))
                .andExpect(content().string(StringContains.containsString("sku-availability")))
                .andExpect(content().string(StringContains.containsString("sku-quantity")))
                .andExpect(header().string("yc", uuid));

        mockMvc.perform(get("/customer/recentlyviewed")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .header("yc", uuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("9998")))
                .andExpect(content().string(StringContains.containsString("sku-availability")))
                .andExpect(content().string(StringContains.containsString("sku-quantity")))
                .andExpect(content().string(StringContains.containsString("Available in 2010 Q2")))
                .andExpect(header().string("yc", uuid));


    }

    @Test
    public void testSearchJson() throws Exception {

        reindex();

        final SearchRO search = new SearchRO();
        search.setParameters(Collections.singletonMap("query", Collections.singletonList("BENDER")));
        search.setSortField("sku.code_sort");

        final byte[] body = toJsonBytes(search);

        mockMvc.perform(post("/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .locale(locale)
                    .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("productAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuAvailabilityModel")))
                .andExpect(content().string(StringContains.containsString("skuQuantityModel")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }

    @Test
    public void testSearchXML() throws Exception {

        reindex();

        final SearchRO search = new SearchRO();
        search.setParameters(Collections.singletonMap("query", Collections.singletonList("BENDER")));
        search.setSortField("sku.code_sort");

        final byte[] body = toJsonBytes(search);


        mockMvc.perform(post("/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_XML)
                    .locale(locale)
                    .content(body))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("BENDER-ua")))
                .andExpect(content().string(StringContains.containsString("WAREHOUSE_2")))
                .andExpect(content().string(StringContains.containsString("product-availability")))
                .andExpect(content().string(StringContains.containsString("sku-availability")))
                .andExpect(content().string(StringContains.containsString("sku-quantity")))
                .andExpect(header().string("yc", CustomMatchers.isNotBlank()));

    }


}
