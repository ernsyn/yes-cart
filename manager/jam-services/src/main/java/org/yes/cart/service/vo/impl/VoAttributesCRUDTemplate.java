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

package org.yes.cart.service.vo.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.yes.cart.domain.dto.AttrValueDTO;
import org.yes.cart.domain.dto.AttributeDTO;
import org.yes.cart.domain.entity.Etype;
import org.yes.cart.domain.misc.MutablePair;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.vo.VoAttrValue;
import org.yes.cart.domain.vo.VoAttribute;
import org.yes.cart.service.dto.DtoAttributeService;
import org.yes.cart.service.dto.GenericAttrValueService;
import org.yes.cart.service.vo.VoAssemblySupport;
import org.yes.cart.service.vo.VoIOSupport;

import java.util.*;

/**
 * User: denispavlov
 * Date: 21/08/2016
 * Time: 14:50
 */
public abstract class VoAttributesCRUDTemplate<V extends VoAttrValue, D extends AttrValueDTO> {

    private static final Logger LOG = LoggerFactory.getLogger(VoAttributesCRUDTemplate.class);

    private final Class<V> voClass;
    private final Class<D> dtoClass;
    private final GenericAttrValueService genericAttrValueService;
    private final DtoAttributeService dtoAttributeService;
    private final VoAssemblySupport voAssemblySupport;
    private final VoIOSupport voIOSupport;

    protected VoAttributesCRUDTemplate(final Class<V> voClass,
                                       final Class<D> dtoClass,
                                       final GenericAttrValueService genericAttrValueService,
                                       final DtoAttributeService dtoAttributeService,
                                       final VoAssemblySupport voAssemblySupport,
                                       final VoIOSupport voIOSupport) {
        this.voClass = voClass;
        this.dtoClass = dtoClass;
        this.genericAttrValueService = genericAttrValueService;
        this.dtoAttributeService = dtoAttributeService;
        this.voAssemblySupport = voAssemblySupport;
        this.voIOSupport = voIOSupport;
    }

    /**
     * Template for retrieving object attributes.
     *
     * @param objectId master object id
     * @param includeSecure include secure attributes
     *
     * @return vo attributes
     *
     * @throws Exception errors
     */
    public List<V> verifyAccessAndGetAttributes(final long objectId, final boolean includeSecure) throws Exception {

        final Pair<Boolean, String> pair = verifyAccessAndDetermineObjectCode(objectId, includeSecure);
        if (!pair.getFirst()) {
            throw new AccessDeniedException("Access is denied");
        }

        final String imageObjectCode = pair.getSecond();
        return getAttributes(objectId, imageObjectCode, includeSecure);

    }


    /**
     * Template for retrieving object attributes.
     *
     * @param objectId master object id
     * @param includeSecure include secure attributes
     *
     * @return vo attributes
     *
     * @throws Exception errors
     */
    public List<V> getAttributes(final long objectId, final String imageObjectCode, final boolean includeSecure) throws Exception {

        final List<D> attributes = (List) this.genericAttrValueService.getEntityAttributes(objectId);

        final List<V> all = voAssemblySupport.assembleVos(voClass, dtoClass, attributes);
        // Filter out special attributes that are managed by specialised editors
        final Iterator<V> allIt = all.iterator();
        while (allIt.hasNext()) {
            final V next = allIt.next();
            if (skipAttributesInView(next.getAttribute().getCode(), includeSecure)) {
                allIt.remove();
            } else if (skipSecure(next, next.getAttribute(), includeSecure)) {
                allIt.remove();
            } else if (next.getAttrvalueId() > 0L && Etype.IMAGE_BUSINESS_TYPE.equals(next.getAttribute().getEtypeName())) {
                if (StringUtils.isNotBlank(next.getVal())) {
                    next.setValBase64Data(
                            voIOSupport.getImageAsBase64(next.getVal(), imageObjectCode, this.genericAttrValueService.getImageRepositoryUrlPattern())
                    );
                    // TODO: SEO data for image
                }
            }
        }
        return all;

    }


    /**
     * Extension hook to feed in entity specific hidden attributes
     *
     * @param code attribute code
     * @param includeSecure include secure attributes
     *
     * @return set of attributes to skip
     */
    protected abstract boolean skipAttributesInView(String code, boolean includeSecure);

    /**
     * Extension hook to feed in entity specific secure attributes
     *
     * @param av av value
     * @param attribute attribute
     * @param includeSecure include secure attributes
     *
     * @return set of attributes to skip
     */
    protected boolean skipSecure(VoAttrValue av, VoAttribute attribute, boolean includeSecure) {
        if (includeSecure) {
            return false;
        }
        return attribute == null || attribute.isSecure();
    }

    /**
     * Extension hook to feed in entity specific secure attributes
     *
     * @param av av value
     * @param attribute attribute
     * @param includeSecure include secure attributes
     *
     * @return set of attributes to skip
     */
    protected boolean skipSecure(AttrValueDTO av, AttributeDTO attribute, boolean includeSecure) {
        if (includeSecure) {
            return false;
        }
        return attribute == null || attribute.isSecure();
    }

    /**
     * Update attribute values for a single master object.
     *
     * @param vo vo's of attributes belonging to the same object
     * @param includeSecure include secure attributes
     *
     * @return master object id
     *
     * @throws Exception errors
     */
    public long verifyAccessAndUpdateAttributes(final List<MutablePair<V, Boolean>> vo, final boolean includeSecure) throws Exception {

        long objectId = 0L;
        String objectCode = null;
        final VoAssemblySupport.VoAssembler<V, D> asm =
                voAssemblySupport.with(this.voClass, this.dtoClass);

        Map<Long, D> existing = Collections.emptyMap();
        for (final MutablePair<V, Boolean> item : vo) {
            if (objectId == 0L) {
                objectId = determineObjectId(item.getFirst());
                final Pair<Boolean, String> pair = verifyAccessAndDetermineObjectCode(objectId, includeSecure);
                if (!pair.getFirst()) {
                    throw new AccessDeniedException("Access is denied");
                }
                objectCode = pair.getSecond();
                existing = mapAvById((List) genericAttrValueService.getEntityAttributes(objectId));
            } else if (objectId != determineObjectId(item.getFirst())) {
                throw new AccessDeniedException("Access is denied");
            }

            if (skipAttributesInView(item.getFirst().getAttribute().getCode(), includeSecure)) {
                LOG.warn("Attribute {} value cannot be updated using general AV update ... skipped", item.getFirst().getAttribute().getCode());
                continue;
            }

            if (Boolean.valueOf(item.getSecond())) {
                if (item.getFirst().getAttrvalueId() > 0L) {

                    if (skipSecure(item.getFirst(), item.getFirst().getAttribute(), includeSecure)) {
                        throw new AccessDeniedException("Access is denied");
                    }

                    // delete mode
                    genericAttrValueService.deleteAttributeValue(item.getFirst().getAttrvalueId());
                }
            } else if (item.getFirst().getAttrvalueId() > 0L) {
                // update mode
                final D dto = existing.get(item.getFirst().getAttrvalueId());
                if (dto != null) {

                    if (skipSecure(dto, dto.getAttributeDTO(), includeSecure)) {
                        throw new AccessDeniedException("Access is denied");
                    }

                    boolean shouldIndex = false;
                    if (Etype.IMAGE_BUSINESS_TYPE.equals(dto.getAttributeDTO().getEtypeName())) {
                        final String existingImage = voIOSupport.
                                getImageAsBase64(dto.getVal(), objectCode, this.genericAttrValueService.getImageRepositoryUrlPattern());
                        if (existingImage == null || !existingImage.equals(item.getFirst().getValBase64Data())) {
                            String formattedFilename = item.getFirst().getVal();
                            formattedFilename = voIOSupport.
                                    addImageToRepository(
                                            formattedFilename,
                                            objectCode,
                                            dto.getAttributeDTO().getCode(),
                                            item.getFirst().getValBase64Data(),
                                            this.genericAttrValueService.getImageRepositoryUrlPattern()
                                    );
                            item.getFirst().setVal(formattedFilename);
                            shouldIndex = true;
                            // TODO Image SEO
                        }
                    } else if (Etype.FILE_BUSINESS_TYPE.equals(dto.getAttributeDTO().getEtypeName())) {
                        String formattedFilename = item.getFirst().getVal();
                        formattedFilename = voIOSupport.
                                addFileToRepository(
                                        formattedFilename,
                                        objectCode,
                                        dto.getAttributeDTO().getCode(),
                                        item.getFirst().getValBase64Data(),
                                        this.genericAttrValueService.getFileRepositoryUrlPattern()
                                );
                        item.getFirst().setVal(formattedFilename);
                        shouldIndex = true;
                    } else if (Etype.SYSFILE_BUSINESS_TYPE.equals(dto.getAttributeDTO().getEtypeName())) {
                        String formattedFilename = item.getFirst().getVal();
                        formattedFilename = voIOSupport.
                                addSystemFileToRepository(
                                        formattedFilename,
                                        objectCode,
                                        dto.getAttributeDTO().getCode(),
                                        item.getFirst().getValBase64Data(),
                                        this.genericAttrValueService.getSysFileRepositoryUrlPattern()
                                );
                        item.getFirst().setVal(formattedFilename);
                        shouldIndex = true;
                    }
                    asm.assembleDto(dto, item.getFirst());
                    if (shouldIndex) {
                        dto.setIndexedVal(dto.getVal());
                    }
                    genericAttrValueService.updateEntityAttributeValue(dto);
                } else {
                    LOG.warn("Update skipped for inexistent ID {}", item.getFirst().getAttrvalueId());
                }
            } else {
                // insert mode
                final D dto = (D) genericAttrValueService.getNewAttribute(objectId);
                dto.setAttributeDTO(dtoAttributeService.getById(item.getFirst().getAttribute().getAttributeId()));

                if (dto.getAttributeDTO() == null) {
                    LOG.warn("Unknown attribute {} value cannot be inserted using generic AV update ... skipped", item.getFirst().getAttribute().getCode());
                    continue;
                }

                if (skipSecure(dto, dto.getAttributeDTO(), includeSecure)) {
                    throw new AccessDeniedException("Access is denied");
                }

                boolean shouldIndex = false;
                if (Etype.IMAGE_BUSINESS_TYPE.equals(dto.getAttributeDTO().getEtypeName())) {
                    String formattedFilename = item.getFirst().getVal();
                    formattedFilename = voIOSupport.
                            addImageToRepository(
                                    formattedFilename,
                                    objectCode,
                                    dto.getAttributeDTO().getCode(),
                                    item.getFirst().getValBase64Data(),
                                    this.genericAttrValueService.getImageRepositoryUrlPattern()
                            );
                    item.getFirst().setVal(formattedFilename);
                    shouldIndex = true;
                    // TODO Image SEO
                } else if (Etype.FILE_BUSINESS_TYPE.equals(dto.getAttributeDTO().getEtypeName())) {
                    String formattedFilename = item.getFirst().getVal();
                    formattedFilename = voIOSupport.
                            addFileToRepository(
                                    formattedFilename,
                                    objectCode,
                                    dto.getAttributeDTO().getCode(),
                                    item.getFirst().getValBase64Data(),
                                    this.genericAttrValueService.getFileRepositoryUrlPattern()
                            );
                    item.getFirst().setVal(formattedFilename);
                    shouldIndex = true;
                } else if (Etype.SYSFILE_BUSINESS_TYPE.equals(dto.getAttributeDTO().getEtypeName())) {
                    String formattedFilename = item.getFirst().getVal();
                    formattedFilename = voIOSupport.
                            addSystemFileToRepository(
                                    formattedFilename,
                                    objectCode,
                                    dto.getAttributeDTO().getCode(),
                                    item.getFirst().getValBase64Data(),
                                    this.genericAttrValueService.getSysFileRepositoryUrlPattern()
                            );
                    item.getFirst().setVal(formattedFilename);
                    shouldIndex = true;
                }
                asm.assembleDto(dto, item.getFirst());
                if (shouldIndex) {
                    dto.setIndexedVal(dto.getVal());
                }
                this.genericAttrValueService.createEntityAttributeValue(dto);
            }

        }

        return objectId;
    }

    private Map<Long, D> mapAvById(final List<D> entityAttributes) {
        Map<Long, D> map = new HashMap<>();
        for (final D dto : entityAttributes) {
            map.put(dto.getAttrvalueId(), dto);
        }
        return map;
    }

    /**
     * Determine pk of the master object.
     *
     * @param vo attribute
     *
     * @return PK
     */
    protected abstract long determineObjectId(V vo);

    /**
     * Verify access to object and provide object image code.
     *
     * @param objectId master object pk
     * @param includeSecure include secure attributes
     *
     * @return first is accessible flag, second is image object code
     */
    protected abstract Pair<Boolean, String> verifyAccessAndDetermineObjectCode(long objectId, boolean includeSecure) throws Exception;

}
