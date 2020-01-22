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

package org.yes.cart.service.dto.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.domain.dto.PriceListDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.dto.impl.PriceListDTOImpl;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.service.dto.DtoPriceListsService;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 29/05/2015
 * Time: 16:59
 */
public class DtoPriceListsServiceImplTezt extends BaseCoreDBTestCase {

    private DtoPriceListsService dtoService;
    private DtoFactory dtoFactory;


    @Before
    public void setUp() {
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoPriceListsService) ctx().getBean("dtoPriceListService");
        super.setUp();
    }



    @Test
    public void testFindPrice() throws Exception {

        // No price list without shop
        final SearchContext filterNone = createSearchContext("skuCode", false, 0, 10);
        SearchResult<PriceListDTO> pl = dtoService.findPrices(filterNone);
        assertTrue(pl.getTotal() == 0);


        // Price list available for shop and currency
        pl = dtoService.findPrices(createSearchContext(filterNone,
                "shopCode", "SHOIP1",
                "currency", "EUR"
        ));
        assertFalse(pl.getTotal() == 0);

        // Test partial SKU match
        final SearchContext filterPartial = createSearchContext("skuCode", false,  0, 10,
                "filter", "CC_TEST",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        pl = dtoService.findPrices(filterPartial);
        assertFalse(pl.getTotal() == 0);
        assertEquals(3, pl.getItems().stream().filter(
                price -> "CC_TEST1".equals(price.getSkuCode())).count());

        // Test name SKU match
        final SearchContext filterNamePartial = createSearchContext( "skuCode", false, 0, 10,
                "filter", "cc test 11",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        pl = dtoService.findPrices(filterNamePartial);
        assertEquals(1, pl.getTotal());
        assertEquals("CC_TEST11", pl.getItems().get(0).getSkuCode());

        // Test exact SKU match
        final SearchContext filterCodeExact = createSearchContext( "skuCode", false, 0, 10,
                "filter", "!CC_TEST1",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        pl = dtoService.findPrices(filterCodeExact);
        assertEquals(3, pl.getTotal());
        assertEquals("CC_TEST1", pl.getItems().get(0).getSkuCode());

        // Test SKU no match
        final SearchContext filterNoMatch = createSearchContext("skuCode", false,  0, 10,
                "filter", "!something really weird not matching",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        pl = dtoService.findPrices(filterNoMatch);
        assertEquals(0, pl.getTotal());

        // Pricing policy
        final SearchContext filterByPolicy = createSearchContext("skuCode", false,  0, 10,
                "filter", "#P1",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        pl = dtoService.findPrices(filterByPolicy);
        assertFalse(pl.getTotal() == 0);
        assertTrue(pl.getItems().stream().allMatch(price -> "P1".equals(price.getPricingPolicy())));

        // Shipping prices
        final SearchContext filterByTag = createSearchContext("skuCode", false, 0, 10,
                "filter", "#shipping",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        pl = dtoService.findPrices(filterByTag);
        assertFalse(pl.getTotal() == 0);
        assertEquals("4_CARRIERSLA", pl.getItems().get(0).getSkuCode());


    }


    @Test
    public void testCreatePrice() throws Exception {

        final SearchContext filter = createSearchContext("skuCode", false, 0, 10,
                "filter", "!133456-a",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        assertEquals(0, dtoService.findPrices(filter).getTotal());

        PriceListDTO skuPriceDTO = new PriceListDTOImpl();
        skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
        skuPriceDTO.setSalePrice(new BigDecimal("1.22"));
        skuPriceDTO.setMinimalPrice(new BigDecimal("1.21"));
        skuPriceDTO.setSkuCode("133456-a");
        skuPriceDTO.setShopCode("SHOIP1");
        skuPriceDTO.setCurrency("EUR");
        skuPriceDTO.setQuantity(BigDecimal.ONE);
        dtoService.createPrice(skuPriceDTO);

        final SearchResult<PriceListDTO> saved = dtoService.findPrices(filter);
        assertEquals(1, saved.getTotal());

        final PriceListDTO pl = saved.getItems().get(0);

        assertEquals("EUR", pl.getCurrency());
        assertEquals("133456-a", pl.getSkuCode());
        assertTrue((new BigDecimal("1.23")).equals(pl.getRegularPrice()));
        assertTrue((new BigDecimal("1.22")).equals(pl.getSalePrice()));
        assertTrue((new BigDecimal("1.21")).equals(pl.getMinimalPrice()));

    }

    @Test
    public void testCreatePriceZeroSales() throws Exception {

        final SearchContext filter = createSearchContext("skuCode", false, 0, 10,
                "filter", "!133456-a0",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        assertEquals(0, dtoService.findPrices(filter).getTotal());

        PriceListDTO skuPriceDTO = new PriceListDTOImpl();
        skuPriceDTO.setRegularPrice(new BigDecimal("0.00"));
        skuPriceDTO.setSalePrice(new BigDecimal("0.00"));
        skuPriceDTO.setMinimalPrice(new BigDecimal("0.00"));
        skuPriceDTO.setSkuCode("133456-a0");
        skuPriceDTO.setShopCode("SHOIP1");
        skuPriceDTO.setCurrency("EUR");
        skuPriceDTO.setQuantity(BigDecimal.ONE);
        dtoService.createPrice(skuPriceDTO);

        final SearchResult<PriceListDTO> saved = dtoService.findPrices(filter);
        assertEquals(1, saved.getTotal());

        final PriceListDTO pl = saved.getItems().get(0);

        assertEquals("EUR", pl.getCurrency());
        assertEquals("133456-a0", pl.getSkuCode());
        // Zero prices must be null, otherwise flex when flex puts in zero it breaks minimal price calculation
        assertTrue((new BigDecimal("0.00")).equals(pl.getRegularPrice()));
        assertNull(pl.getSalePrice());
        assertNull(pl.getMinimalPrice());

    }

    @Test
    public void testUpdatePrice() throws Exception {

        final SearchContext filter = createSearchContext("skuCode", false, 0, 10,
                "filter", "!133456-b",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        assertEquals(0, dtoService.findPrices(filter).getTotal());

        PriceListDTO skuPriceDTO = new PriceListDTOImpl();
        skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
        skuPriceDTO.setSalePrice(new BigDecimal("1.22"));
        skuPriceDTO.setMinimalPrice(new BigDecimal("1.21"));
        skuPriceDTO.setSkuCode("133456-b");
        skuPriceDTO.setShopCode("SHOIP1");
        skuPriceDTO.setCurrency("EUR");
        skuPriceDTO.setQuantity(BigDecimal.ONE);
        dtoService.createPrice(skuPriceDTO);

        SearchResult<PriceListDTO> saved = dtoService.findPrices(filter);
        assertEquals(1, saved.getTotal());

        PriceListDTO pl = saved.getItems().get(0);

        assertEquals("EUR", pl.getCurrency());
        assertEquals("133456-b", pl.getSkuCode());
        assertTrue((new BigDecimal("1.23")).equals(pl.getRegularPrice()));
        assertTrue((new BigDecimal("1.22")).equals(pl.getSalePrice()));
        assertTrue((new BigDecimal("1.21")).equals(pl.getMinimalPrice()));

        pl.setRegularPrice(new BigDecimal("2.23"));
        pl.setSalePrice(new BigDecimal("2.22"));
        pl.setMinimalPrice(new BigDecimal("2.21"));
        pl.setQuantity(BigDecimal.TEN);

        dtoService.updatePrice(pl);

        saved = dtoService.findPrices(filter);
        assertEquals(1, saved.getTotal());

        pl = saved.getItems().get(0);

        assertEquals("EUR", pl.getCurrency());
        assertEquals("133456-b", pl.getSkuCode());
        assertTrue((new BigDecimal("2.23")).equals(pl.getRegularPrice()));
        assertTrue((new BigDecimal("2.22")).equals(pl.getSalePrice()));
        assertTrue((new BigDecimal("2.21")).equals(pl.getMinimalPrice()));

    }

    @Test
    public void testUpdatePriceZeroSales() throws Exception {

        final SearchContext filter = createSearchContext("skuCode", false, 0, 10,
                "filter", "!133456-b0",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        assertEquals(0, dtoService.findPrices(filter).getTotal());

        PriceListDTO skuPriceDTO = new PriceListDTOImpl();
        skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
        skuPriceDTO.setSalePrice(new BigDecimal("1.22"));
        skuPriceDTO.setMinimalPrice(new BigDecimal("1.21"));
        skuPriceDTO.setSkuCode("133456-b0");
        skuPriceDTO.setShopCode("SHOIP1");
        skuPriceDTO.setCurrency("EUR");
        skuPriceDTO.setQuantity(BigDecimal.ONE);
        dtoService.createPrice(skuPriceDTO);

        SearchResult<PriceListDTO> saved = dtoService.findPrices(filter);
        assertEquals(1, saved.getTotal());

        PriceListDTO pl = saved.getItems().get(0);

        assertEquals("EUR", pl.getCurrency());
        assertEquals("133456-b0", pl.getSkuCode());
        assertTrue((new BigDecimal("1.23")).equals(pl.getRegularPrice()));
        assertTrue((new BigDecimal("1.22")).equals(pl.getSalePrice()));
        assertTrue((new BigDecimal("1.21")).equals(pl.getMinimalPrice()));

        pl.setRegularPrice(new BigDecimal("1.25"));
        pl.setSalePrice(new BigDecimal("0.00"));
        pl.setMinimalPrice(new BigDecimal("0.00"));
        pl.setQuantity(BigDecimal.TEN);

        dtoService.updatePrice(pl);

        saved = dtoService.findPrices(filter);
        assertEquals(1, saved.getTotal());

        pl = saved.getItems().get(0);

        assertEquals("EUR", pl.getCurrency());
        assertEquals("133456-b0", pl.getSkuCode());
        assertTrue((new BigDecimal("1.25")).equals(pl.getRegularPrice()));
        assertNull(pl.getSalePrice());
        assertNull(pl.getMinimalPrice());

    }

    @Test
    public void testRemovePrice() throws Exception {

        final SearchContext filter = createSearchContext("skuCode", false, 0, 10,
                "filter", "!133456-c",
                "shopCode", "SHOIP1",
                "currency", "EUR"
        );
        assertEquals(0, dtoService.findPrices(filter).getTotal());

        PriceListDTO skuPriceDTO = new PriceListDTOImpl();
        skuPriceDTO.setRegularPrice(new BigDecimal("1.23"));
        skuPriceDTO.setSkuCode("133456-c");
        skuPriceDTO.setShopCode("SHOIP1");
        skuPriceDTO.setCurrency("EUR");
        skuPriceDTO.setQuantity(BigDecimal.ONE);
        dtoService.createPrice(skuPriceDTO);

        SearchResult<PriceListDTO> saved = dtoService.findPrices(filter);
        assertEquals(1, saved.getTotal());

        PriceListDTO pl = saved.getItems().get(0);

        assertEquals("EUR", pl.getCurrency());
        assertEquals("133456-c", pl.getSkuCode());
        assertTrue((new BigDecimal("1.23")).equals(pl.getRegularPrice()));

        dtoService.removePrice(pl.getSkuPriceId());

        saved = dtoService.findPrices(filter);
        assertEquals(0, saved.getTotal());

    }
}
