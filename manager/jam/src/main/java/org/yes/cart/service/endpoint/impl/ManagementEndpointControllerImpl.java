/*
 * Copyright 2009- 2016 Denys Pavlov, Igor Azarnyi
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
package org.yes.cart.service.endpoint.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.domain.vo.VoLicenseAgreement;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.service.cluster.ClusterService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.endpoint.ManagementEndpointController;
import org.yes.cart.service.vo.VoManagementService;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/07/2016
 * Time: 09:21
 */
@Component
public class ManagementEndpointControllerImpl implements ManagementEndpointController {

    private final ClusterService clusterService;
    private final VoManagementService voManagementService;
    private final SystemService systemPreferencesService;
    private final ServletContext servletContext;

    @Autowired
    public ManagementEndpointControllerImpl(final ClusterService clusterService,
                                            final VoManagementService voManagementService,
                                            final SystemService systemPreferencesService,
                                            final ServletContext servletContext) {
        this.clusterService = clusterService;
        this.voManagementService = voManagementService;
        this.systemPreferencesService = systemPreferencesService;
        this.servletContext = servletContext;
    }

    @Override
    public @ResponseBody
    VoManager getMyself() throws Exception {
        return this.voManagementService.getMyself();
    }

    @Override
    public @ResponseBody
    VoLicenseAgreement getMyAgreement() throws Exception {
        return voManagementService.getMyAgreement();
    }

    @Override
    public @ResponseBody
    VoLicenseAgreement acceptMyAgreement() throws Exception {
        return voManagementService.acceptMyAgreement();
    }

    @Override
    public @ResponseBody
    Map<String, String> getMyUiPreferences() throws Exception {
        final Map<String, String> vals = new HashMap<>();
        vals.put("SYSTEM_PANEL_HELP_DOCS", systemPreferencesService.getAttributeValue("SYSTEM_PANEL_HELP_DOCS"));
        vals.put("SYSTEM_PANEL_HELP_COPYRIGHT", systemPreferencesService.getAttributeValue("SYSTEM_PANEL_HELP_COPYRIGHT"));
        vals.put("SYSTEM_PANEL_LABEL", insertLabelPlaceholders(systemPreferencesService.getSystemEnvironmentLabel(), clusterService.getCurrentNode()));
        vals.put("CONTEXT_PATH", servletContext.getContextPath());

        return vals;
    }

    private String insertLabelPlaceholders(final String systemEnvironmentLabel, final Node currentNode) {
        String out = systemEnvironmentLabel;
        out = out.replace("{{cluster}}", currentNode.getClusterId());
        out = out.replace("{{buildNo}}", currentNode.getBuildNo());
        out = out.replace("{{version}}", currentNode.getVersion());
        out = out.replace("{{fullVersion}}", currentNode.getFullVersion());
        return out;
    }
}
