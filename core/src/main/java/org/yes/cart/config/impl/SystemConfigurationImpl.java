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

package org.yes.cart.config.impl;

import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.domain.SecurityAccessControlService;

import java.util.Properties;

/**
 * User: denispavlov
 * Date: 21/10/2019
 * Time: 11:49
 */
public class SystemConfigurationImpl extends AbstractConfigurationImpl {

    public SystemConfigurationImpl(final SystemService systemService) {
        super(systemService);
    }

    void registerCustomSecurityControlService(final Properties properties) {

        final SecurityAccessControlService tsSf = determineConfiguration(properties, "SYS.httpSecurityAccessControlService", SecurityAccessControlService.class);

        customise("SYS", "SYS.httpSecurityAccessControlService", "httpSecurityAccessControlService", SecurityAccessControlService.class, tsSf);

    }

    @Override
    protected void onConfigureEvent(final Properties properties) {

        registerCustomSecurityControlService(properties);

    }

}
