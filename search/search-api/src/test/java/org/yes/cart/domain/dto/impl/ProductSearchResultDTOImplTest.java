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

import org.junit.Test;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSkuSearchResultDTO;
import org.yes.cart.domain.i18n.impl.StringI18NModel;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * User: Denis
 * Date: 6/9/14
 * Time: 1:19 PM
 */
public class ProductSearchResultDTOImplTest {
    @Test
    public void testCopy() throws Exception {

        final ProductSkuSearchResultDTOImpl firstSKU = new ProductSkuSearchResultDTOImpl();
        firstSKU.setId(2);
        firstSKU.setCode("First SKU");
        firstSKU.setManufacturerCode("ManFirst SKU");
        firstSKU.setFulfilmentCentreCode("Main");
        firstSKU.setName("FirstName SKU");
        firstSKU.setDisplayName(new StringI18NModel("EN#~#FirstDisplayName SKU"));
        firstSKU.setDescription("FirstDescription SKU");
        firstSKU.setDisplayDescription(new StringI18NModel("EN#~#FirstDisplayDescription SKU"));
        firstSKU.setAvailablefrom(LocalDateTime.now());
        firstSKU.setAvailableto(LocalDateTime.now());
        firstSKU.setReleaseDate(LocalDateTime.now());
        firstSKU.setRestockDate(LocalDateTime.now());
        firstSKU.setRestockNotes(new StringI18NModel("EN#~#Some text"));
        firstSKU.setCreatedTimestamp(Instant.now());
        firstSKU.setUpdatedTimestamp(Instant.now());
        firstSKU.setAvailability(2);
        firstSKU.setQtyOnWarehouse(new HashMap<Long, BigDecimal>() {{
            put(10L, BigDecimal.ONE);
        }});
        firstSKU.setFeatured(true);
        firstSKU.setTag("tag1 tag2");
        firstSKU.setDefaultImage("FirstDefaultImageSKU.jpg");

        final ProductSearchResultDTOImpl first = new ProductSearchResultDTOImpl();
        first.setId(1);
        first.setCode("First");
        first.setManufacturerCode("ManFirst");
        first.setDefaultSkuCode("FirstCode");
        first.setName("FirstName");
        first.setDisplayName(new StringI18NModel("EN#~#FirstDisplayName"));
        first.setDescription("FirstDescription");
        first.setDisplayDescription(new StringI18NModel("EN#~#FirstDisplayDescription"));
        first.setType("typeA");
        first.setDisplayType(new StringI18NModel("EN#~#displayTypeA"));
        first.setService(true);
        first.setEnsemble(true);
        first.setShippable(true);
        first.setDigital(true);
        first.setDownloadable(true);
        first.setTag("tag");
        first.setBrand("brand");
        first.setCreatedTimestamp(Instant.now());
        first.setUpdatedTimestamp(Instant.now());
        first.setDefaultImage("FirstDefaultImage.jpg");

        first.setBaseSkus(Collections.singletonMap(firstSKU.getId(), firstSKU));

        final ProductSearchResultDTO copy = first.copy();

        assertEquals(first.getId(), copy.getId());
        assertEquals(first.getCode(), copy.getCode());
        assertEquals(first.getManufacturerCode(), copy.getManufacturerCode());
        assertEquals(first.isMultisku(), copy.isMultisku());
        assertEquals(first.getDefaultSkuCode(), copy.getDefaultSkuCode());
        assertEquals(first.getName(), copy.getName());
        assertEquals(first.getDisplayName(), copy.getDisplayName());
        assertEquals(first.getDescription(), copy.getDescription());
        assertEquals(first.getDisplayDescription(), copy.getDisplayDescription());
        assertEquals(first.getType(), copy.getType());
        assertEquals(first.getDisplayType(), copy.getDisplayType());
        assertTrue(copy.isService());
        assertTrue(copy.isEnsemble());
        assertTrue(copy.isShippable());
        assertTrue(copy.isDigital());
        assertTrue(copy.isDownloadable());
        assertEquals(first.getTag(), copy.getTag());
        assertEquals(first.getBrand(), copy.getBrand());
        assertNull(first.getAvailablefrom());
        assertEquals(first.getAvailablefrom(), copy.getAvailablefrom());
        assertNull(first.getAvailableto());
        assertEquals(first.getAvailableto(), copy.getAvailableto());
        assertNull(first.getReleaseDate());
        assertEquals(first.getReleaseDate(), copy.getReleaseDate());
        assertNull(first.getRestockDate());
        assertEquals(first.getRestockDate(), copy.getRestockDate());
        assertNull(first.getRestockNote("EN"));
        assertEquals(first.getRestockNote("EN"), copy.getRestockNote("EN"));
        assertEquals(first.getCreatedTimestamp(), copy.getCreatedTimestamp());
        assertEquals(first.getUpdatedTimestamp(), copy.getUpdatedTimestamp());
        assertEquals(1, first.getAvailability());
        assertEquals(first.getAvailability(), copy.getAvailability());
        assertNotNull(first.getQtyOnWarehouse(10L));
        assertNull(first.getQtyOnWarehouse(10L).get("First SKU"));
        assertNull(copy.getQtyOnWarehouse(10L).get("First SKU"));
        assertEquals(first.getDefaultImage(), copy.getDefaultImage());
        assertFalse(first.isFeatured());
        assertEquals(first.isFeatured(), copy.isFeatured());
        assertNull("Search SKU will be set on a copy by search service", copy.getSearchSkus());

        // Do not override equals and hash code for ProductSearchResultDTO because we can have
        // multiple copies in memory used by hash maps and hash sets
        assertFalse(first.equals(copy));
        assertFalse(first.hashCode() == copy.hashCode());

        // Check SKU dependent defaults
        copy.setSearchSkus(Collections.singletonList(copy.getBaseSku(2)));
        assertEquals(first.getId(), copy.getId());
        assertEquals(first.getCode(), copy.getCode());
        assertEquals(first.getManufacturerCode(), copy.getManufacturerCode());
        assertEquals(first.isMultisku(), copy.isMultisku());
        assertEquals(firstSKU.getCode(), copy.getDefaultSkuCode());
        assertEquals(first.getName(), copy.getName());
        assertEquals(first.getDisplayName(), copy.getDisplayName());
        assertEquals(first.getDescription(), copy.getDescription());
        assertEquals(first.getDisplayDescription(), copy.getDisplayDescription());
        assertEquals(first.getType(), copy.getType());
        assertEquals(first.getDisplayType(), copy.getDisplayType());
        assertTrue(copy.isService());
        assertTrue(copy.isEnsemble());
        assertTrue(copy.isShippable());
        assertTrue(copy.isDigital());
        assertTrue(copy.isDownloadable());
        assertEquals(first.getTag(), copy.getTag());
        assertEquals(first.getBrand(), copy.getBrand());
        assertNull(first.getAvailablefrom());
        assertNotNull(copy.getAvailablefrom());
        assertEquals(firstSKU.getAvailablefrom(), copy.getAvailablefrom());
        assertNull(first.getAvailableto());
        assertNotNull(copy.getAvailableto());
        assertEquals(firstSKU.getAvailableto(), copy.getAvailableto());
        assertNull(first.getReleaseDate());
        assertNotNull(copy.getReleaseDate());
        assertEquals(firstSKU.getReleaseDate(), copy.getReleaseDate());
        assertNull(first.getRestockDate());
        assertNotNull(copy.getRestockDate());
        assertEquals(firstSKU.getRestockDate(), copy.getRestockDate());
        assertNull(first.getRestockNote("EN"));
        assertNotNull(copy.getRestockNote("EN"));
        assertEquals(firstSKU.getRestockNote("EN"), copy.getRestockNote("EN"));
        assertEquals(first.getCreatedTimestamp(), copy.getCreatedTimestamp());
        assertEquals(first.getUpdatedTimestamp(), copy.getUpdatedTimestamp());
        assertEquals(1, first.getAvailability());
        assertEquals(firstSKU.getAvailability(), copy.getAvailability());
        assertNotNull(first.getQtyOnWarehouse(10L));
        assertNull(first.getQtyOnWarehouse(10L).get("First SKU"));
        assertNotNull(copy.getQtyOnWarehouse(10L).get("First SKU"));
        assertEquals("FirstDefaultImage.jpg", first.getDefaultImage());
        assertEquals(firstSKU.getDefaultImage(), copy.getDefaultImage());
        assertFalse(first.isFeatured());
        assertTrue(copy.isFeatured());

    }

    @Test
    public void testTypeMask() throws Exception {

        final ProductSearchResultDTOImpl dto = new ProductSearchResultDTOImpl();

        assertFalse(dto.isService());
        assertFalse(dto.isEnsemble());
        assertFalse(dto.isShippable());
        assertFalse(dto.isDigital());
        assertFalse(dto.isDownloadable());

        dto.setService(true);
        dto.setEnsemble(false);
        dto.setShippable(false);
        dto.setDigital(false);
        dto.setDownloadable(false);

        assertTrue(dto.isService());
        assertFalse(dto.isEnsemble());
        assertFalse(dto.isShippable());
        assertFalse(dto.isDigital());
        assertFalse(dto.isDownloadable());

        dto.setService(false);
        dto.setEnsemble(true);
        dto.setShippable(false);
        dto.setDigital(false);
        dto.setDownloadable(false);

        assertFalse(dto.isService());
        assertTrue(dto.isEnsemble());
        assertFalse(dto.isShippable());
        assertFalse(dto.isDigital());
        assertFalse(dto.isDownloadable());

        dto.setService(false);
        dto.setEnsemble(false);
        dto.setShippable(true);
        dto.setDigital(false);
        dto.setDownloadable(false);

        assertFalse(dto.isService());
        assertFalse(dto.isEnsemble());
        assertTrue(dto.isShippable());
        assertFalse(dto.isDigital());
        assertFalse(dto.isDownloadable());

        dto.setService(false);
        dto.setEnsemble(false);
        dto.setShippable(false);
        dto.setDigital(true);
        dto.setDownloadable(false);

        assertFalse(dto.isService());
        assertFalse(dto.isEnsemble());
        assertFalse(dto.isShippable());
        assertTrue(dto.isDigital());
        assertFalse(dto.isDownloadable());

        dto.setService(false);
        dto.setEnsemble(false);
        dto.setShippable(false);
        dto.setDigital(false);
        dto.setDownloadable(true);

        assertFalse(dto.isService());
        assertFalse(dto.isEnsemble());
        assertFalse(dto.isShippable());
        assertFalse(dto.isDigital());
        assertTrue(dto.isDownloadable());

        dto.setService(false);
        dto.setEnsemble(false);
        dto.setShippable(false);
        dto.setDigital(false);
        dto.setDownloadable(false);

        assertFalse(dto.isService());
        assertFalse(dto.isEnsemble());
        assertFalse(dto.isShippable());
        assertFalse(dto.isDigital());
        assertFalse(dto.isDownloadable());

        dto.setService(false);
        dto.setEnsemble(false);
        dto.setShippable(false);
        dto.setDigital(false);
        dto.setDownloadable(false);

        assertFalse(dto.isService());
        assertFalse(dto.isEnsemble());
        assertFalse(dto.isShippable());
        assertFalse(dto.isDigital());
        assertFalse(dto.isDownloadable());

        dto.setService(true);
        dto.setEnsemble(true);
        dto.setShippable(true);
        dto.setDigital(true);
        dto.setDownloadable(true);

        assertTrue(dto.isService());
        assertTrue(dto.isEnsemble());
        assertTrue(dto.isShippable());
        assertTrue(dto.isDigital());
        assertTrue(dto.isDownloadable());

        dto.setService(false);
        dto.setEnsemble(false);
        dto.setShippable(false);
        dto.setDigital(false);
        dto.setDownloadable(false);

        assertFalse(dto.isService());
        assertFalse(dto.isEnsemble());
        assertFalse(dto.isShippable());
        assertFalse(dto.isDigital());
        assertFalse(dto.isDownloadable());

    }
}
