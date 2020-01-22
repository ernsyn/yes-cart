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

package org.yes.cart.service.locator.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.locator.InstantiationStrategy;
import org.yes.cart.service.locator.ServiceLocator;
import org.yes.cart.utils.MessageFormatUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service locator use particular strategy, that depends from protocol in service url, to
 * instantiate service. At thi moment tree strategies available - web service , jnp an spring local.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class ServiceLocatorImpl implements ServiceLocator {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceLocatorImpl.class);

    private final Map<String, InstantiationStrategy> protocolStrategyMap = new HashMap<>();

    /**
     * Construct the service locator.
     */
    public ServiceLocatorImpl() {
    }


    /**
     * Construct the service locator.
     *
     * @param strategies strategy  map to instantiate service.
     */
    public ServiceLocatorImpl(final List<InstantiationStrategy> strategies) {
        if (strategies != null) {
            for (final InstantiationStrategy strategy : strategies) {
                register(strategy);
            }
        }
    }


    /**
     * Get {@link InstantiationStrategy} by given service url.
     *
     * @param serviceUrl given service url

     * @return {@link InstantiationStrategy} to create particular service instance.
     */
    InstantiationStrategy getInstantiationStrategy(final String serviceUrl) {
        final String strategyKey = getStrategyKey(serviceUrl);
        final InstantiationStrategy instantiationStrategy = protocolStrategyMap.get(strategyKey);
        if (instantiationStrategy == null) {
            throw new RuntimeException(
                    MessageFormatUtils.format(
                            "Instantiation strategy can not be found for key {} from url {}",
                            strategyKey,
                            serviceUrl
                    )
            );
        }
        return instantiationStrategy;
    }

    /**
     * Get protocol from url. Possible values - http,https,jnp.
     * Null will be returned for spring.
     *
     * @param url given url
     * @return protocol
     */
    String getStrategyKey(final String url) {
        if (url.indexOf(':') > -1) {
            return url.substring(0, url.indexOf(':'));
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public <T> T getServiceInstance(final String serviceUrl,
                                    final Class<T> iface,
                                    final String loginName,
                                    final String password) {

        LOG.debug("Get {} as {}", serviceUrl, iface.getName());

        try {
            return getInstantiationStrategy(serviceUrl).getInstance(serviceUrl, iface, loginName, password);
        } catch (Exception e) {
            throw new RuntimeException(
                    MessageFormatUtils.format
                            ("Can not create {} instance. Given interface is {}. See root cause for more detail",
                                    serviceUrl, iface.getName()), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void register(final InstantiationStrategy instantiationStrategy) {
        for (final String protocol : instantiationStrategy.getProtocols()) {
            protocolStrategyMap.put(protocol, instantiationStrategy);
        }
    }
}
