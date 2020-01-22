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

package org.yes.cart.web.service.apisupport.impl;

import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.theme.templates.TemplateProcessor;
import org.yes.cart.web.application.ApplicationDirector;

/**
 * User: denispavlov
 * Date: 25/08/2014
 * Time: 00:54
 */
public class ApiUrlTemplateFunctionProviderImpl implements TemplateProcessor.FunctionProvider {

    private final String contextPath;
    private final String paramName;

    public ApiUrlTemplateFunctionProviderImpl() {
        this("", "/");
    }

    public ApiUrlTemplateFunctionProviderImpl(final String contextPath, final String paramName) {
        this.contextPath = contextPath;
        this.paramName = "/" + paramName;
    }

    @Override
    public Object doAction(final Object... params) {

        final Shop shop = ApplicationDirector.getCurrentShop();
        final String defaultUrl = shop.getDefaultShopUrl();
        final StringBuilder url = new StringBuilder(defaultUrl);
        if (defaultUrl.endsWith("/")) {
            url.append(contextPath.substring(1));
        } else {
            url.append(contextPath);
        }
        if (params != null && params.length >= 1) {

            final String uri = String.valueOf(params[0]);
            url.append(this.paramName).append('/').append(uri);

        }
        return url.toString();
    }



}
