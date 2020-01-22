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

package org.yes.cart.report.impl;

import org.apache.xmlgraphics.io.ResourceResolver;
import org.apache.xmlgraphics.io.TempResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.report.ReportDescriptor;
import org.yes.cart.report.ReportDescriptorPDF;
import org.yes.cart.service.domain.ContentService;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.theme.ThemeService;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 24/10/2015
 * Time: 12:27
 */
public abstract class AbstractThemeAwareFopReportGenerator extends AbstractFopReportGenerator implements ServletContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractThemeAwareFopReportGenerator.class);

    private final ThemeService themeService;
    private final ShopService shopService;
    private final ContentService contentService;
    private final SystemService systemService;
    private final ImageService imageService;

    private ServletContext servletContext;

    protected AbstractThemeAwareFopReportGenerator(final ThemeService themeService,
                                                   final ShopService shopService,
                                                   final ContentService contentService,
                                                   final SystemService systemService,
                                                   final ImageService imageService) {
        this.themeService = themeService;
        this.shopService = shopService;
        this.contentService = contentService;
        this.systemService = systemService;
        this.imageService = imageService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InputStream getFopUserConfigInputStream(final ReportDescriptor descriptor,
                                                      final Map<String, Object> parameters,
                                                      final Object data,
                                                      final String lang) {

        final Shop shop = resolveShop(descriptor, parameters, data, lang);
        try {
            return new FopThemeResourceResolver(
                    shop, lang, "fop-userconfig.xml", "fop-userconfig.xml",
                    themeService, contentService, servletContext, systemService, imageService
            ).getResource((URI) null);
        } catch (Exception exp) {
            LOG.error("Unable to load report template URI fop-userconfig.xml", exp);
            return null;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Source getXsltFile(final ReportDescriptor descriptor,
                               final Map<String, Object> parameters,
                               final Object data,
                               final String lang) {

        final Shop shop = resolveShop(descriptor, parameters, data, lang);

        try {
            final String langXslfo = ((ReportDescriptorPDF) descriptor).getLangXslfo(lang);
            return new StreamSource(new FopThemeResourceResolver(
                    shop, lang, descriptor.getReportId(), langXslfo,
                    themeService, contentService, servletContext, systemService, imageService
            ).getResource((URI) null));
        } catch (Exception exp) {
            LOG.error("Unable to load report template URI " + descriptor.getReportId(), exp);
            return null;
        }

    }

    /**
     * Resolve shop instance from parameters.
     *
     * @param descriptor descriptor
     * @param parameters passed in parameter values
     * @param data data object for report
     * @param lang language
     *
     * @return shop instance for given parameters
     */
    protected final Shop resolveShop(final ReportDescriptor descriptor,
                                     final Map<String, Object> parameters,
                                     final Object data,
                                     final String lang) {

        Shop shop = null;
        if (parameters.get("shop") instanceof Shop) {
            shop = (Shop) parameters.get("shop");
        } else if (parameters.get("shopId") instanceof Long) {
            shop = shopService.getById((Long) parameters.get("shopId"));
        } else if (parameters.get("shopCode") instanceof String) {
            shop = shopService.getShopByCode((String) parameters.get("shopCode"));
        }

        return shop;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TempResourceResolver getTempResourceResolver(final ReportDescriptor descriptor, final Map<String, Object> parameters, final Object data, final String lang) {
        final Shop shop = resolveShop(descriptor, parameters, data, lang);
        return new FopThemeResourceResolver(shop, lang, themeService, contentService, servletContext, systemService, imageService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ResourceResolver getResourceResolver(final ReportDescriptor descriptor, final Map<String, Object> parameters, final Object data, final String lang) {
        final Shop shop = resolveShop(descriptor, parameters, data, lang);
        return new FopThemeResourceResolver(shop, lang, themeService, contentService, servletContext, systemService, imageService);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
