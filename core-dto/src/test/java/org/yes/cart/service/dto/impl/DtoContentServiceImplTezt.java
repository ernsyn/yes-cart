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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.config.ConfigurationRegistry;
import org.yes.cart.constants.DtoServiceSpringKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.AttrValueContentDTO;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.dto.ContentDTO;
import org.yes.cart.domain.dto.factory.DtoFactory;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.DtoContentService;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Denis Pavlov
 * Date: 15-June-2013
 */
@RunWith(Parameterized.class)
public class DtoContentServiceImplTezt extends BaseCoreDBTestCase {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<String[]> data() {
        return Arrays.asList(
                new String[] { "contentServiceCMS1", "dtoContentServiceCMS1" },
                new String[] { "contentServiceCMS3", "dtoContentServiceCMS3" }
                );
    }

    private DtoFactory dtoFactory;
    private ContentService service;
    private ShopService shopService;
    private DtoContentService dtoService;
    private DtoAttributeService dtoAttrService;

    public static final int QTY = 10;


    private String cmsServiceMode;
    private String cmsDtoServiceMode;

    public DtoContentServiceImplTezt(final String cmsServiceMode, final String cmsDtoServiceMode) {
        this.cmsServiceMode = cmsServiceMode;
        this.cmsDtoServiceMode = cmsDtoServiceMode;
    }


    @Override
    @Before
    public void setUp() {
        dtoFactory = (DtoFactory) ctx().getBean(DtoServiceSpringKeys.DTO_FACTORY);
        dtoService = (DtoContentService) ctx().getBean(DtoServiceSpringKeys.DTO_CONTENT_SERVICE);
        dtoAttrService = (DtoAttributeService) ctx().getBean(DtoServiceSpringKeys.DTO_ATTRIBUTE_SERVICE);
        service = (ContentService) ctx().getBean(ServiceSpringKeys.CONTENT_SERVICE);
        shopService = (ShopService) ctx().getBean(ServiceSpringKeys.SHOP_SERVICE);

        final ContentService serviceMode = (ContentService) ctx().getBean(cmsServiceMode);
        ((ConfigurationRegistry) service).register("CMS", serviceMode);
        final DtoContentService dtoServiceMode = (DtoContentService) ctx().getBean(cmsDtoServiceMode);
        ((ConfigurationRegistry) dtoService).register("CMS", dtoServiceMode);

        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {

        ((ConfigurationRegistry) service).register("CMS", null);
        ((ConfigurationRegistry) dtoService).register("CMS", null);

        super.tearDown();
    }

    @Test
    public void testFindContent() throws Exception {

        final List<Long> contentIds = new ArrayList<>(shopService.getShopContentIds(10L));

        final Map<String, List> filterByParentParams = new HashMap<>();
        filterByParentParams.put("filter", Collections.singletonList("^ SHOIP1"));
        filterByParentParams.put("contentIds", contentIds);
        final SearchContext filterByParent = new SearchContext(filterByParentParams, 0, 10, "guid", false, "filter", "contentIds");
        SearchResult<ContentDTO> list = dtoService.findContent(filterByParent);
        assertTrue(list.getTotal() > 1);
        assertEquals("SHOIP1", list.getItems().get(0).getName());

        final Map<String, List> filterByUriParams = new HashMap<>();
        filterByUriParams.put("filter", Collections.singletonList("@SHOIP1_mail_customer-registered.html"));
        filterByUriParams.put("contentIds", contentIds);
        final SearchContext filterByUri = new SearchContext(filterByUriParams, 0, 10, "name", false, "filter", "contentIds");
        list = dtoService.findContent(filterByUri);
        assertEquals(1, list.getTotal());
        assertEquals("SHOIP1_mail_customer-registered.html", list.getItems().get(0).getUri());

        final Map<String, List> filterNoneParams = new HashMap<>();
        filterNoneParams.put("contentIds", contentIds);
        final SearchContext filterNone = new SearchContext(filterNoneParams, 0, 10, "name", false, "filter", "contentIds");
        list = dtoService.findContent(filterNone);
        assertTrue(list.getTotal() > 1);
        list.getItems().forEach(content -> {
            assertTrue(content.getGuid(), content.getGuid().contains("SHOIP1"));
        });


    }


    @Test
    public void testGetAll() throws Exception {

        List<ContentDTO> list = dtoService.getAllByShopId(20L);
        assertFalse(list.isEmpty());

    }

    @Test
    public void testGetAllWithAvailabilityFilter() throws Exception {

        List<ContentDTO> list = dtoService.getAllWithAvailabilityFilter(20L, true);
        assertFalse(list.isEmpty());
        assertFalse(isContentPresent(list, 10110L));  //content 2008
        assertFalse(isContentPresent(list, 10111L));  //content 2108

    }

    private boolean isContentPresent(final List<ContentDTO> list, final long pk) {
        for (ContentDTO dto : list) {
            if (dto.getContentId() == pk) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testCreate() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    ContentDTO dto = getDto();
                    dto = dtoService.create(dto);
                    assertTrue(dto.getContentId() > 0);
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testUpdate() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    ContentDTO dto = getDto();
                    dto = dtoService.create(dto);
                    assertTrue(dto.getContentId() > 0);
                    dto.setDescription("description");
                    dto = dtoService.update(dto);
                    assertEquals("description", dto.getDescription());
                } catch (Exception e) {
                    assertTrue(e.getMessage(), false);

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testRemove() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    ContentDTO dto = getDto();
                    dto = dtoService.create(dto);
                    assertTrue(dto.getContentId() > 0);
                    long id = dto.getContentId();
                    dtoService.remove(id);
                    dto = dtoService.getById(id);
                    assertNull(dto);
                } catch (Exception e) {
                    fail(e.getMessage());

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testGetAllByShopIdNoContent() throws Exception {

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    List<ContentDTO> list = dtoService.getAllByShopId(50L);
                    assertNull(list); // No Content Root
                } catch (Exception e) {

                    fail(ExceptionUtils.getStackTrace(e));

                }

                status.setRollbackOnly();
            }
        });


    }

    @Test
    public void testGetAllByShopIdWithCreate() throws Exception {
        clearCache();
        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    dtoService.createContentRoot(50L);

                    List<ContentDTO> list = dtoService.getAllByShopId(50L);
                    assertNotNull(list);
                    assertFalse(list.isEmpty());
                    assertEquals(1, list.size());
                } catch (Exception e) {
                    fail(e.getMessage());

                }

                status.setRollbackOnly();
            }
        });


    }


    @Test
    public void testUpdateEntityAttributeValue() throws Exception {


        final List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(10105);
        for (AttrValueDTO dto : list) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                assertFalse("5,15,35".equals(dto.getVal()));
                dto.setVal("5,15,35");
                AttrValueDTO dtoUpdated = dtoService.updateEntityAttributeValue(dto);
                assertEquals("5,15,35", dtoUpdated.getVal());
                break;
            }
        }



    }

    @Test
    public void testDeleteAttributeValue() throws Exception {

        final List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(10105L);

        getTx().execute(new TransactionCallbackWithoutResult() {
            @Override
            public void doInTransactionWithoutResult(TransactionStatus status) {
                try {


                    for (final AttrValueDTO dto : list) {
                        if (dto.getVal() != null) {
                            dtoService.deleteAttributeValue(dto.getAttrvalueId());
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e.getMessage());

                }

                //status.setRollbackOnly();
            }
        });
        clearCache();

        final List<? extends AttrValueDTO> list2 = dtoService.getEntityAttributes(10105L);
        //assertEquals(totalSize, list2.size());
        for (AttrValueDTO dto : list2) {
            assertNull(dto.getVal());
        }


    }

    @Test
    public void testzCreateEntityAttributeValue() throws Exception {
        clearCache();
        List<? extends AttrValueDTO> list = dtoService.getEntityAttributes(10105L);
        assertEquals(QTY, list.size());
        for (int i = 0; i < QTY; i++) {
            AttrValueDTO dto = list.get(i);
            if (dto.getVal() != null) {
                dtoService.deleteAttributeValue(dto.getAttrvalueId());
            }
        }
        clearCache();
        list = dtoService.getEntityAttributes(10105L);
        assertEquals(QTY, list.size());
        for (AttrValueDTO dto : list) {
            assertNull(dto.getVal());
        }
        AttrValueContentDTO attrValueContent = dtoFactory.getByIface(AttrValueContentDTO.class);
        attrValueContent.setAttributeDTO(dtoAttrService.getById(1002L));//CATEGORY_ITEMS_PER_PAGE
        attrValueContent.setVal("1,2,3");
        attrValueContent.setContentId(10105L);
        dtoService.createEntityAttributeValue(attrValueContent);
        attrValueContent = dtoFactory.getByIface(AttrValueContentDTO.class);
        attrValueContent.setAttributeDTO(dtoAttrService.getById(1004L));//URI
        attrValueContent.setVal("val2");
        attrValueContent.setContentId(10105L);
        dtoService.createEntityAttributeValue(attrValueContent);
        getCacheMap().get("contentService-byId").clear();
        list = dtoService.getEntityAttributes(10105L);
        assertEquals(QTY, list.size());
        for (AttrValueDTO dto : list) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                assertEquals("1,2,3", dto.getVal());
            } else if (dto.getAttributeDTO().getCode().equals("URI")) {
                assertEquals("val2", dto.getVal());

            }
        }


        for (AttrValueDTO dto : dtoService.getEntityAttributes(10105L)) {
            if (dto.getAttributeDTO().getCode().equals("CATEGORY_ITEMS_PER_PAGE")) {
                dto.setVal("6,12,24");
                dtoService.updateEntityAttributeValue(dto);
                break;
            }
        }

    }

    private ContentDTO getDto() {
        ContentDTO dto = dtoFactory.getByIface(ContentDTO.class);
        dto.setName("testcontent");
        dto.setParentId(10105L);
        return dto;
    }

    @Test
    public void testUpdateContentWithMoreThan4000chars() throws Exception {

        final List<AttrValueDTO> avsBefore = (List) dtoService.getEntityAttributes(10106L);
        for (final AttrValueDTO av : avsBefore) {
            if (av.getAttributeDTO().getCode().startsWith("CONTENT_BODY_en")) {
                assertTrue(av.getAttrvalueId() == 0L);
            }
        }
        final List<AttrValueDTO> avsBeforeC = (List) dtoService.getEntityContentAttributes(10106L);
        for (final AttrValueDTO av : avsBeforeC) {
            if (av.getAttributeDTO().getCode().equals("CONTENT_BODY_en") || av.getAttributeDTO().getCode().equals("CONTENT_BODY_ru")) {
                assertEquals("", av.getVal());
            } else {
                fail("Invalid attribute code");
            }
        }

        final AttributeDTO attributeDTO = dtoFactory.getByIface(AttributeDTO.class);
        attributeDTO.setCode("CONTENT_BODY_en");
        final AttrValueContentDTO contentLarge = dtoFactory.getByIface(AttrValueContentDTO.class);
        contentLarge.setContentId(10106L);
        contentLarge.setAttributeDTO(attributeDTO);
        contentLarge.setVal(LARGE_CONTENT);

        dtoService.updateEntityAttributeValue(contentLarge);
        clearCache();

        final String bodyLarge = service.getContentBody(10106L, "en");
        assertEquals(bodyLarge, LARGE_CONTENT);
        final List<AttrValueDTO> avsAfterLarge = (List) dtoService.getEntityAttributes(10106L);
        for (final AttrValueDTO av : avsAfterLarge) {
            if (av.getAttributeDTO().getCode().startsWith("CONTENT_BODY_en")) {
                assertTrue(av.getAttrvalueId() > 0L);
            }
        }
        final List<AttrValueDTO> avsAfterLargeC = (List) dtoService.getEntityContentAttributes(10106L);
        for (final AttrValueDTO av : avsAfterLargeC) {
            if (av.getAttributeDTO().getCode().equals("CONTENT_BODY_en")) {
                assertEquals(LARGE_CONTENT, av.getVal());
            } else if (av.getAttributeDTO().getCode().equals("CONTENT_BODY_ru")) {
                assertEquals("", av.getVal());
            } else {
                fail("Invalid attribute code");
            }
        }


        clearCache();

        final AttrValueContentDTO contentSmall = dtoFactory.getByIface(AttrValueContentDTO.class);
        contentSmall.setContentId(10106L);
        contentSmall.setAttributeDTO(attributeDTO);
        contentSmall.setVal("Small");

        dtoService.updateEntityAttributeValue(contentSmall);
        final String bodySmall = service.getContentBody(10106L, "en");
        assertEquals(bodySmall, "Small");
        getCacheMap().get("contentService-byId").clear();
        final List<AttrValueDTO> avsAfterSmall = (List) dtoService.getEntityAttributes(10106L);
        for (final AttrValueDTO av : avsAfterSmall) {
            if (av.getAttributeDTO().getCode().startsWith("CONTENT_BODY_en_1")) {
                assertTrue(av.getAttrvalueId() > 0L);
            }
            if (av.getAttributeDTO().getCode().startsWith("CONTENT_BODY_en_2")) {
                assertTrue(av.getAttrvalueId() == 0L);
            }
        }
        final List<AttrValueDTO> avsAfterSmallC = (List) dtoService.getEntityContentAttributes(10106L);
        for (final AttrValueDTO av : avsAfterSmallC) {
            if (av.getAttributeDTO().getCode().equals("CONTENT_BODY_en")) {
                assertEquals("Small", av.getVal());
            } else if (av.getAttributeDTO().getCode().equals("CONTENT_BODY_ru")) {
                assertEquals("", av.getVal());
            } else {
                fail("Invalid attribute code");
            }
        }




    }


    final String LARGE_CONTENT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam volutpat est ac ultrices tincidunt. Donec sodales nec libero suscipit cursus. Duis vel pulvinar purus, eu adipiscing justo. Donec porttitor aliquet lacinia. Quisque pellentesque venenatis nunc eu dignissim. Quisque eros leo, pellentesque facilisis iaculis eu, rutrum posuere arcu. Pellentesque blandit nunc at mauris semper, eu tempor felis pellentesque. Maecenas sodales nunc id rhoncus vestibulum. Quisque at est eros. Vestibulum euismod sapien urna, sit amet luctus orci fermentum id. Fusce nec lacus vel eros fringilla commodo non eu purus.\n" +
            "Morbi convallis dui sed justo elementum, non blandit velit lobortis. Mauris venenatis dignissim odio in iaculis. Aliquam ac felis accumsan, blandit lectus nec, fringilla nisi. Cras blandit ut mauris sit amet placerat. Donec quis sollicitudin massa. Donec ut luctus diam. Nullam viverra iaculis neque.\n" +
            "Duis rutrum, diam ac vulputate tincidunt, augue nisl vehicula turpis, id luctus lacus dui ut massa. Donec convallis odio interdum turpis eleifend, sed iaculis tortor sodales. Vivamus quis massa a turpis dapibus condimentum eget vitae odio. Aliquam lobortis orci arcu, at fringilla sapien consequat quis. Nam at turpis eget lacus sodales accumsan sit amet ut libero. Nam in faucibus magna. Nunc nisi purus, vehicula ut porttitor a, malesuada id tortor.\n" +
            "Proin malesuada urna a arcu egestas luctus. Aliquam volutpat, sapien at tempus luctus, metus odio viverra turpis, ut tincidunt velit nisi non tortor. Nulla pretium volutpat scelerisque. Suspendisse potenti. Proin eu facilisis urna, aliquet pellentesque orci. Nam fringilla fringilla justo, eget varius est fringilla quis. Curabitur suscipit odio in lectus faucibus sodales at sit amet massa. Proin dictum turpis sapien, id tristique turpis sollicitudin sit amet. Maecenas eget leo sem. Praesent in risus a mauris tincidunt varius eu id mi.\n" +
            "Nulla aliquam sollicitudin rutrum. Mauris et diam id sapien mollis venenatis. Phasellus blandit volutpat imperdiet. Integer vel malesuada dolor. Pellentesque aliquam in nunc vitae consectetur. Quisque viverra venenatis ultrices. Nam sit amet consectetur nisl. Aliquam molestie tortor vitae pretium convallis. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus.\n" +
            "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam erat volutpat. Nulla facilisi. Ut viverra malesuada luctus. Morbi iaculis dolor sit amet euismod ultrices. Praesent ullamcorper lorem non arcu molestie porta. Maecenas dictum augue non quam consequat, eget molestie diam molestie. Sed ac est ac dolor aliquet posuere a quis nisi. Sed eget ante sit amet orci egestas lobortis. Ut mollis varius molestie. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Duis nec urna volutpat, ultrices justo sit amet, tincidunt erat.\n" +
            "Curabitur egestas sodales ligula quis feugiat. Nullam elit nisl, fringilla non sem non, viverra fermentum justo. Sed mi urna, dictum et lobortis vel, egestas a velit. Nam vel pretium augue. Praesent scelerisque vulputate massa, non mattis augue molestie id. Curabitur quis pellentesque orci. Pellentesque vehicula enim in diam tempor scelerisque.\n" +
            "Fusce ante nisl, elementum vel magna sit amet, lacinia facilisis libero. Proin lacinia massa nec turpis placerat, at volutpat dolor fermentum. Fusce sed interdum eros. Proin dapibus sagittis tellus convallis bibendum. Maecenas nec tortor turpis. Mauris porttitor neque accumsan, iaculis urna ut, auctor purus. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Nulla magna nunc, scelerisque sollicitudin tincidunt ut, mollis at sapien. Maecenas ornare vehicula eros, ut tincidunt purus lacinia non. Nulla sapien ante, porta a lorem sed, porta pretium libero. Proin rhoncus, quam eget lobortis placerat, nibh elit imperdiet augue, a sollicitudin purus nisl ornare libero. Praesent malesuada purus a facilisis tempus. Sed vel lacus vitae lacus euismod commodo ac at ante. Donec at turpis sit amet nulla ultricies lobortis et sed libero. Phasellus ac magna interdum, dapibus dui sit amet, vestibulum diam. Vivamus sed eros id dui condimentum consectetur.\n" +
            "Curabitur rutrum sem vitae purus egestas volutpat. Phasellus egestas neque vitae velit varius accumsan. Suspendisse posuere tristique ultrices. Vivamus eu odio eu turpis pharetra lacinia. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vestibulum nibh nisl, egestas et sodales eget, semper in est. Mauris mollis, enim ac sagittis sollicitudin, nulla sapien bibendum nisi, ac eleifend mi massa lobortis lorem. Aliquam elit libero, luctus sit amet facilisis eu, blandit tristique orci. Mauris auctor placerat risus convallis gravida. Quisque accumsan auctor lorem vel venenatis. Morbi eu dolor felis. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. In porttitor iaculis nulla a posuere.\n" +
            "Maecenas condimentum non lacus id pretium. Nunc mi neque, euismod vitae scelerisque eget, dictum et leo. Proin vitae gravida metus. Nulla sodales at mi ac sodales. Donec ante ligula, aliquam quis est sit amet, fermentum malesuada odio. Morbi risus orci, bibendum eu pulvinar ut, pellentesque tempor urna. Pellentesque eu mi placerat, scelerisque mauris quis, commodo quam.\n" +
            "Phasellus pharetra orci ut rhoncus fermentum. Pellentesque eleifend, eros ut hendrerit tempus, nunc turpis gravida nibh, sit amet pharetra urna est at metus. Aliquam aliquam non sem non interdum. Donec turpis mi, lacinia sit amet velit vel, sodales mollis odio. Cras ac enim vitae nunc molestie vehicula et nec nisl. Curabitur vel tincidunt dui, ac ultricies mauris. Quisque risus dui, rhoncus sodales eleifend at, suscipit non lectus. Ut eros elit, elementum in imperdiet in, ullamcorper ut sapien. In pellentesque mattis ipsum, sed sagittis turpis. Sed ut nibh sit amet urna laoreet suscipit sed in nisi. Aenean cursus magna at eleifend suscipit. Integer vel mattis dui. Fusce facilisis orci ante. In hac habitasse platea dictumst. Cras rhoncus volutpat felis eu blandit.\n" +
            "In adipiscing consequat lorem, at vestibulum arcu ultrices et. Aenean dignissim metus ut orci bibendum feugiat. Sed vel velit nisl. Morbi rutrum nunc vitae ante sodales ultricies. Sed nec mi a purus sagittis suscipit ut nec turpis. Vivamus orci nibh, lacinia nec mauris eu, pellentesque bibendum eros. Interdum et malesuada fames ac ante ipsum primis in faucibus. Quisque congue mi sed ullamcorper consequat. Nam sodales varius semper.\n" +
            "\n";
}