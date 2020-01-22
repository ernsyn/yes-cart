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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.yes.cart.config.*;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.domain.SystemService;

import java.io.StringReader;
import java.util.*;

/**
 * User: denispavlov
 * Date: 07/10/2017
 * Time: 16:47
 */
public abstract class AbstractConfigurationImpl
        implements ConfigurationListener, ActiveConfigurationDetector, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger("CONFIG");

    protected ApplicationContext applicationContext;

    private final SystemService systemService;

    private final List<ActiveConfiguration> active = new ArrayList<>();

    public AbstractConfigurationImpl(final SystemService systemService) {
        this.systemService = systemService;
    }


    /** {@inheritDoc} */
    @Override
    public final void reload() {

        LOG.debug("Loading custom configurations {} ...", this);

        this.active.clear();
        final String cfg = this.systemService.getAttributeValue(AttributeNamesKeys.System.SYSTEM_EXTENSION_CFG_PROPERTIES);

        try {
            final Properties properties = new Properties();
            if (StringUtils.isNotBlank(cfg)) {
                properties.load(new StringReader(cfg));
            }
            this.onConfigureEvent(properties);
        } catch (Exception exp) {
            LOG.warn("Loading custom configurations ... error reading configurations", exp);
        }

        LOG.debug("Loading custom configurations {} ... completed", this);

    }

    /** {@inheritDoc} */
    @Override
    public final void onApplicationEvent(final ContextRefreshedEvent contextRefreshedEvent) {

        this.reload();

    }

    /**
     * Start configuration process.
     *
     * @param properties configuration properties
     */
    protected abstract void onConfigureEvent(final Properties properties);


    /**
     * Lookup configuration bean.
     *
     * @param properties properties
     * @param key        key to look up
     * @param clazz      expected configuration
     * @param <T>        type
     *
     * @return config or null
     */
    protected <T> T determineConfiguration(final Properties properties, final String key, final Class<T> clazz) {
        final String cfg = properties.getProperty(key);
        if (StringUtils.isNotBlank(cfg)) {
            try {
                if (this.applicationContext.containsBean(cfg.trim())) {
                    return this.applicationContext.getBean(cfg.trim(), clazz);
                } else {
                    LOG.warn("Loading custom configurations ... error retrieving bean " + cfg);
                }
            } catch (Exception exp) {
                LOG.error("Loading custom configurations ... error retrieving bean " + cfg, exp);
            }
        }
        return null;
    }


    /**
     * Call to register of the configuration.
     *
     * @param ref reference, e.g. code
     * @param key object key, e.g. shop code, mnemonic
     * @param cfgProperty configuration property
     * @param configurationType configuration type
     * @param configuration configuration to set
     */
    protected void customise(final String ref, final Object key, final String cfgProperty, final Class configurationType, final Object configuration) {

        final Map<String, ConfigurationRegistry> registries = this.applicationContext.getBeansOfType(ConfigurationRegistry.class);
        for (final ConfigurationRegistry registry : registries.values()) {
            if (registry.supports(cfgProperty, configurationType)) {
                registry.register(key, configuration);
                if (configuration instanceof Configuration) {
                    final ConfigurationContext ctx = ((Configuration) configuration).getCfgContext();
                    this.active.add(new ActiveConfigurationImpl(ctx.getName(), ctx.getCfgInterface(), ref));
                }
                if (configuration instanceof RegistrationAware) {
                    ((RegistrationAware) configuration).onRegisterEvent();
                }
                LOG.debug("Custom configurations for {}/{}/{} ... registering {}", ref, key, configurationType, configuration);
            }
        }

    }

    /** {@inheritDoc} */
    @Override
    public List<ActiveConfiguration> getActive() {
        return Collections.unmodifiableList(this.active);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
