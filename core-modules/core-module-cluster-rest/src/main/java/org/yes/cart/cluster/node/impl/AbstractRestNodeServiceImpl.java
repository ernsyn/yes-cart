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

package org.yes.cart.cluster.node.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.cluster.node.Message;
import org.yes.cart.cluster.node.MessageListener;
import org.yes.cart.cluster.node.Node;
import org.yes.cart.cluster.node.NodeService;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.stream.xml.XStreamProvider;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * User: denispavlov
 * Date: 24/05/2019
 * Time: 20:43
 */
public abstract class AbstractRestNodeServiceImpl implements NodeService, ServletContextAware, DisposableBean {

    protected Logger log;

    private final Map<String, String> configuration = new HashMap<>();
    private Node node = new NodeImpl(true, "ND0", null, "DEFAULT", "CL0", "N/A", "", true);
    private final List<Node> cluster = new CopyOnWriteArrayList<>();
    private final List<Node> blacklisted = new CopyOnWriteArrayList<>();

    protected Map<String, List<MessageListener>> listeners = new HashMap<>();

    private final SystemService systemService;

    private Resource restConfiguration;
    private XStreamProvider<List<Node>> restConfigurationLoader;

    public AbstractRestNodeServiceImpl(final SystemService systemService) {
        this.systemService = systemService;
    }

    /** {@inheritDoc} */
    @Override
    public String getCurrentNodeId() {
        return node.getId();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getConfiguration() {
        final Map<String, String> all = new HashMap<>();
        all.putAll(configuration);
        all.put(
                AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.IMPORT_JOB_LOG_SIZE, "10000")
        );
        all.put(
                AttributeNamesKeys.System.IMPORT_JOB_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.IMPORT_JOB_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_CONNECTOR_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_CONNECTOR_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_CONNECTOR_PRODUCT_BULK_INDEX_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_CONNECTOR_PRODUCT_BULK_INDEX_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_CONNECTOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_CONNECTOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_CONNECTOR_QUERY_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_CONNECTOR_QUERY_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_CONNECTOR_CACHE_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_CONNECTOR_CACHE_TIMEOUT_MS, "60000")
        );
        all.put(
                AttributeNamesKeys.System.SYSTEM_CONNECTOR_IMAGE_TIMEOUT_MS,
                systemService.getAttributeValueOrDefault(AttributeNamesKeys.System.SYSTEM_CONNECTOR_IMAGE_TIMEOUT_MS, "60000")
        );
        return all;
    }

    /** {@inheritDoc} */
    @Override
    public List<Node> getCluster() {

        return Collections.unmodifiableList(this.cluster);

    }

    /** {@inheritDoc} */
    @Override
    public List<Node> getBlacklisted() {

        return Collections.unmodifiableList(this.blacklisted);

    }

    /** {@inheritDoc} */
    @Override
    public Node getCurrentNode() {
        return node;
    }

    /** {@inheritDoc} */
    @Override
    public Node getAdminNode() {

        final List<Node> cluster = getCluster();
        for (final Node node : cluster) {
            if (node.isAdmin()) {
                return node;
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public List<Node> getSfNodes() {

        final List<Node> cluster = getCluster();
        final List<Node> yes = new ArrayList<>();
        for (final Node node : cluster) {
            if (!node.isAdmin()) {
                yes.add(node);
            }
        }
        return Collections.unmodifiableList(yes);
    }

    /** {@inheritDoc} */
    @Override
    public List<Node> getOtherSfNodes() {

        final List<Node> cluster = getCluster();
        final List<Node> yes = new ArrayList<>();
        for (final Node node : cluster) {
            if (!node.isAdmin() && !node.isCurrent()) {
                yes.add(node);
            }
        }
        return Collections.unmodifiableList(yes);
    }

    /** {@inheritDoc} */
    @Override
    public abstract void broadcast(final Message message);


    /** {@inheritDoc} */
    @Override
    public void subscribe(final String subject, final MessageListener listener) {
        synchronized (this) {
            final List<MessageListener> subjectListeners = listeners.computeIfAbsent(subject, k -> new ArrayList<>());
            subjectListeners.add(listener);
            log.debug("Registering listener for topic {}", subject);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setServletContext(final ServletContext servletContext) {

        initNodeFromServletContext(servletContext);
        loadClusterConfiguration();

    }

    /**
     * Blacklist a node so it is not seen as part of the cluster.
     *
     * @param nodeId node id
     */
    protected void blacklist(final String nodeId) {

        if (nodeId != null) {
            synchronized (this.cluster) {

                Node toBlacklist = null;
                for (final Node node : this.cluster) {
                    if (nodeId.equals(node.getId())) {
                        toBlacklist = node;
                        break;
                    }
                }

                if (toBlacklist != null) {
                    this.cluster.remove(toBlacklist);
                    this.blacklisted.add(toBlacklist);
                }
            }
        }

    }

    /**
     * Reload configuration for this cluster.
     */
    protected void reloadClusterConfiguration() {

        synchronized (this.cluster) {

            loadClusterConfiguration();
        }

    }


    void loadClusterConfiguration() {

        try {
            this.cluster.clear();
            this.blacklisted.clear();

            final List<Node> cluster = restConfigurationLoader.fromXML(restConfiguration.getInputStream());

            final List<Node> all = new ArrayList<>();
            all.add(this.node);
            for (final Node node : cluster) {
                if (!node.getId().equals(this.node.getId())) {
                    if (StringUtils.isNotBlank(node.getChannel()) || node.isAdmin()) {
                        // only add nodes with specified channels
                        all.add(new NodeImpl(false, node));
                    }
                }
            }
            this.cluster.addAll(all);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void initNodeFromServletContext(final ServletContext servletContext) {


        final Enumeration parameters = servletContext.getInitParameterNames();
        while (parameters.hasMoreElements()) {

            final String key = String.valueOf(parameters.nextElement());
            final String value = servletContext.getInitParameter(key);

            configuration.put(key, value);

        }

        final String luceneDisabled = configuration.get(LUCENE_INDEX_DISABLED);
        final String luceneDisabledValue = luceneDisabled != null ?
                Boolean.valueOf(luceneDisabled).toString() : Boolean.FALSE.toString();
        configuration.put(LUCENE_INDEX_DISABLED, luceneDisabledValue);

        NodeImpl node = new NodeImpl(true,
                configuration.get(NODE_ID),
                configuration.get(NODE_TYPE),
                configuration.get(NODE_CONFIG),
                configuration.get(CLUSTER_ID),
                configuration.get(VERSION),
                configuration.get(BUILD_NO),
                Boolean.valueOf(luceneDisabledValue)
        );
        node.setChannel(configuration.get(CHANNEL));
        this.node = node;
        this.cluster.add(node);

        log = LoggerFactory.getLogger(node.getClusterId() + "." + node.getNodeId());

        if (log.isInfoEnabled()) {

            log.info("== REST configurations =========================================");
            log.info("");
            log.info("Node: {}", node);

            for (final Map.Entry<String, String> entry : configuration.entrySet()) {
                log.info("{}: {}", entry.getKey(), entry.getValue());
            }

            log.info("");
            log.info("================================================================");
            log.info("");

        }

    }

    /**
     * Spring IoC setter.
     *
     * @param restConfiguration configuration
     */
    public void setRestConfiguration(final Resource restConfiguration) {
        this.restConfiguration = restConfiguration;
    }

    /**
     * Spring IoC setter.
     *
     * @param restConfigurationLoader configuration loader
     */
    public void setRestConfigurationLoader(final XStreamProvider<List<Node>> restConfigurationLoader) {
        this.restConfigurationLoader = restConfigurationLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {

        log.info("Closing REST channel for node {}", node.getId());

    }

}
