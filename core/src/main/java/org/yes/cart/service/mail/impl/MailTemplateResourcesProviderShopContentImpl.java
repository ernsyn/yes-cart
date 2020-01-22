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

package org.yes.cart.service.mail.impl;

import org.apache.commons.lang.StringUtils;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.Content;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.mail.MailTemplateResourcesProvider;
import org.yes.cart.service.media.MediaFileNameStrategy;

import java.io.IOException;
import java.util.List;

/**
 * Shop aware mail template resource provider that uses shop specific content in order to resolve templates.
 *
 * User: denispavlov
 * Date: 09/01/2015
 * Time: 17:43
 */
public class MailTemplateResourcesProviderShopContentImpl implements MailTemplateResourcesProvider {

    private final ContentService contentService;
    private final MediaFileNameStrategy contentImageStrategy;
    private final SystemService systemService;
    private final ImageService imageService;

    public MailTemplateResourcesProviderShopContentImpl(final ContentService contentService,
                                                        final MediaFileNameStrategy contentImageStrategy,
                                                        final SystemService systemService,
                                                        final ImageService imageService) {
        this.contentService = contentService;
        this.contentImageStrategy = contentImageStrategy;
        this.systemService = systemService;
        this.imageService = imageService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplate(final List<String> mailTemplateChain,
                              final String shopCode,
                              final String locale,
                              final String templateName,
                              final String ext) throws IOException {

        final String uri = shopCode.concat("_mail_").concat(templateName).concat(ext);

        // No need for dynamic content since this is already part of the mail composer
        final String content = contentService.getContentBody(uri, locale);

        if (StringUtils.isNotBlank(content)) {
            return content;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getResource(final List<String> mailTemplateChain,
                              final String shopCode,
                              final String locale,
                              final String templateName,
                              final String resourceFilename) throws IOException {

        final String templateUri = shopCode.concat("_mail_").concat(templateName).concat("_").concat(resourceFilename);

        final byte[] templateSpecific = getResource(templateUri, locale);
        if (templateSpecific == null) {
            final String globalUri = shopCode.concat("_mail_").concat(resourceFilename);
            return getResource(globalUri, locale);
        }
        return templateSpecific;

    }

    private byte[] getResource(final String contentFullUri,
                               final String locale) throws IOException {

        final Long contentId = contentService.findContentIdBySeoUri(contentFullUri);

        if (contentId != null) {

            final Content content = contentService.getById(contentId);

            final byte[] localeSpecificImage = getImageAsBytes(content, AttributeNamesKeys.Category.CATEGORY_IMAGE + "_" + locale, contentFullUri);
            if (localeSpecificImage != null && localeSpecificImage.length > 0) {
                return localeSpecificImage;
            }

            final byte[] defaultImage = getImageAsBytes(content, AttributeNamesKeys.Category.CATEGORY_IMAGE, contentFullUri);
            if (defaultImage != null && defaultImage.length > 0) {
                return defaultImage;
            }

        }

        return null;

    }

    private byte[] getImageAsBytes(final Content content, final String imageAttribute, final String contentFullUri) throws IOException {
        final String imageValue = content.getAttributeValueByCode(imageAttribute);
        if (StringUtils.isNotBlank(imageValue)) {

            final String path = systemService.getImageRepositoryDirectory();
            final String urlPath = contentImageStrategy.getUrlPath();

            if (imageService.isImageInRepository(imageValue, contentFullUri, urlPath, path)) {
                return imageService.imageToByteArray(imageValue, contentFullUri, urlPath, path);
            }

            if (imageService.isImageInRepository(imageValue, content.getGuid(), urlPath, path)) {
                return imageService.imageToByteArray(imageValue, content.getGuid(), urlPath, path);
            }

        }
        return null;
    }


}
