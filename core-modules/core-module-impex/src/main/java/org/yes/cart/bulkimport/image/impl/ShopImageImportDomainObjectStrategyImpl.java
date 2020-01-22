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

package org.yes.cart.bulkimport.image.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.yes.cart.bulkimport.image.ImageImportDomainObjectStrategy;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.AttrValueShop;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.async.JobStatusListener;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.federation.FederationFacade;

import java.util.Collection;
import java.util.List;

/**
 * User: denispavlov
 * Date: 01/09/2014
 * Time: 22:26
 */
public class ShopImageImportDomainObjectStrategyImpl extends AbstractImageImportDomainObjectStrategyImpl implements ImageImportDomainObjectStrategy {

    private final ShopService shopService;
    private final AttributeService attributeService;

    public ShopImageImportDomainObjectStrategyImpl(final FederationFacade federationFacade,
                                                   final ShopService shopService,
                                                   final AttributeService attributeService) {
        super(federationFacade);
        this.shopService = shopService;
        this.attributeService = attributeService;
    }

    /** {@inheritDoc} */
    @Override
    public boolean supports(final String uriPattern) {
        return Constants.SHOP_IMAGE_REPOSITORY_URL_PATTERN.equals(uriPattern);
    }

    /** {@inheritDoc} */
    @Override
    public boolean doImageImport(final JobStatusListener statusListener, final String fileName, final String code, final String suffix, final String locale) {

        final Shop shop = shopService.getShopByCode(code);
        if (shop == null) {
            statusListener.notifyWarning("shop with code {} not found.", code);
            return false;
        }

        validateAccessBeforeUpdate(shop, Shop.class);

        final String attributeCode = AttributeNamesKeys.Shop.SHOP_IMAGE_PREFIX + suffix + (StringUtils.isNotEmpty(locale) ? "_" + locale : "");

        AttrValueShop imageAttributeValue = null;
        final Collection<AttrValueShop> attributes = shop.getAttributes();
        if (attributes != null) {
            for (AttrValueShop attrValue : attributes) {
                if (attributeCode.equals(attrValue.getAttributeCode())) {
                    imageAttributeValue = attrValue;
                    break;
                }
            }
        }
        if (imageAttributeValue == null) {
            final List<Attribute> imageAttributes = attributeService.getAvailableImageAttributesByGroupCode(AttributeGroupNames.SHOP);
            Attribute attribute = null;
            for (final Attribute imageAttribute : imageAttributes) {
                if (attributeCode.equals(imageAttribute.getCode())) {
                    attribute = imageAttribute;
                    break;
                }
            }
            if (attribute == null) {
                statusListener.notifyWarning("attribute with code {} not found.", attributeCode);
                return false;
            }
            imageAttributeValue = shopService.getGenericDao().getEntityFactory().getByIface(AttrValueShop.class);
            imageAttributeValue.setShop(shop);
            imageAttributeValue.setAttributeCode(attribute.getCode());
            shop.getAttributes().add(imageAttributeValue);
        } else if (isInsertOnly()) {
            return false;
        }
        imageAttributeValue.setVal(fileName);
        imageAttributeValue.setIndexedVal(fileName);
        statusListener.notifyMessage("file {} attached as {} to shop {}", fileName, attributeCode, shop.getName());

        try {
            shopService.update(shop);
            return true;

        } catch (DataIntegrityViolationException e) {
            statusListener.notifyError("image {} for shop with name {} could not be added (db error).", e, fileName, shop.getName());
            return false;

        }

    }
}
