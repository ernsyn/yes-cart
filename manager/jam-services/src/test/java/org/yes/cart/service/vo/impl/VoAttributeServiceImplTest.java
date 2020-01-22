/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.service.vo.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.vo.VoAttribute;
import org.yes.cart.domain.vo.VoAttributeGroup;
import org.yes.cart.domain.vo.VoEtype;
import org.yes.cart.service.vo.VoAttributeService;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 22/09/2019
 * Time: 14:58
 */
public class VoAttributeServiceImplTest extends BaseCoreDBTestCase {

    private VoAttributeService voAttributeService;

    @Before
    public void setUp() {
        voAttributeService = (VoAttributeService) ctx().getBean("voAttributeService");
        super.setUp();
    }

    @Test
    public void testGetEtypes() throws Exception {

        final List<VoEtype> types = voAttributeService.getAllEtypes();
        assertNotNull(types);
        assertFalse(types.isEmpty());

    }

    @Test
    public void testGetGroups() throws Exception {

        final List<VoAttributeGroup> groups = voAttributeService.getAllGroups();
        assertNotNull(groups);
        assertFalse(groups.isEmpty());

    }


    @Test
    public void testGetAttributes() throws Exception {

        final List<VoAttribute> systemAttrs = voAttributeService.getAllAttributes("SYSTEM");
        assertNotNull(systemAttrs);
        assertFalse(systemAttrs.isEmpty());

        final List<VoAttribute> systemAttrsNoFilter = voAttributeService.getFilteredAttributes("SYSTEM", null, 10);
        assertNotNull(systemAttrsNoFilter);
        assertFalse(systemAttrsNoFilter.isEmpty());

        final List<VoAttribute> systemAttrsPayment = voAttributeService.getFilteredAttributes("SYSTEM", "PAYMENT", 10);
        assertNotNull(systemAttrsPayment);
        assertFalse(systemAttrsPayment.isEmpty());

        final List<VoAttribute> systemAttrsExact = voAttributeService.getFilteredAttributes("SYSTEM", "#SYSTEM_DEFAULT_SHOP", 10);
        assertNotNull(systemAttrsExact);
        assertEquals(1, systemAttrsExact.size());
        assertEquals("SYSTEM_DEFAULT_SHOP", systemAttrsExact.get(0).getCode());

        final List<MutablePair<Long, String>> typeWithMaterialAttr = voAttributeService.getProductTypesByAttributeCode("MATERIAL");
        assertNotNull(typeWithMaterialAttr);
        assertFalse(typeWithMaterialAttr.isEmpty());
        assertEquals("Robots", typeWithMaterialAttr.get(0).getSecond());

    }

    @Test
    public void testAttributeCRUD() throws Exception {

        final VoAttribute attribute = new VoAttribute();
        attribute.setEtypeId(1000L);
        attribute.setCode("TESTCRUD");
        attribute.setAttributegroupId(1000L);
        attribute.setName("TEST CRUD");
        attribute.setDisplayNames(Collections.singletonList(MutablePair.of("en", "Test")));
        attribute.setChoiceData(Collections.singletonList(MutablePair.of("en", "Test,Test1,Test2")));

        final VoAttribute created = voAttributeService.createAttribute(attribute);
        assertTrue(created.getAttributeId() > 0L);

        VoAttribute afterCreated = voAttributeService.getAttributeById(created.getAttributeId());
        assertNotNull(afterCreated);
        assertEquals("TEST CRUD", afterCreated.getName());

        created.setName("TEST CRUD UPDATE");

        final VoAttribute updated = voAttributeService.updateAttribute(created);
        assertEquals("TEST CRUD UPDATE", updated.getName());

        assertFalse(voAttributeService.getFilteredAttributes("SYSTEM","TEST CRUD UPDATE", 10).isEmpty());

        voAttributeService.removeAttribute(updated.getAttributeId());

        assertTrue(voAttributeService.getFilteredAttributes("SYSTEM","TEST CRUD UPDATE", 10).isEmpty());

    }
}