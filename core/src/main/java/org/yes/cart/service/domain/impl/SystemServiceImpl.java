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

package org.yes.cart.service.domain.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.constants.AttributeGroupNames;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.dao.GenericDAO;
import org.yes.cart.domain.entity.AttrValue;
import org.yes.cart.domain.entity.AttrValueSystem;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.entity.System;
import org.yes.cart.domain.entity.impl.AttrValueEntitySystem;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.RuntimeAttributeService;
import org.yes.cart.service.domain.SystemService;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class SystemServiceImpl implements SystemService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemServiceImpl.class);

    private final GenericDAO<System, Long> systemDao;

    private final GenericDAO<AttrValueEntitySystem, Long> attrValueEntitySystemDao;

    private final AttributeService attributeService;

    private final RuntimeAttributeService runtimeAttributeService;

    private final Cache PREF_CACHE;

    /**
     * Construct system services, which is determinate shop set.
     *
     * @param systemDao               system dao
     * @param attributeService        attribute service.
     * @param runtimeAttributeService runtime attribute service
     * @param cacheManager            cache manager to use
     */
    public SystemServiceImpl(final GenericDAO<System, Long> systemDao,
                             final GenericDAO<AttrValueEntitySystem, Long> attrValueEntitySystemDao,
                             final AttributeService attributeService,
                             final RuntimeAttributeService runtimeAttributeService,
                             final CacheManager cacheManager) {
        this.systemDao = systemDao;
        this.attributeService = attributeService;
        this.runtimeAttributeService = runtimeAttributeService;
        this.attrValueEntitySystemDao = attrValueEntitySystemDao;
        PREF_CACHE = cacheManager.getCache("systemService-attributeValue");
    }

    private String getStringFromValueWrapper(final Cache.ValueWrapper wrapper) {
        if (wrapper != null) {
            return (String) wrapper.get();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeValue(final String key) {

        final String value = getStringFromValueWrapper(PREF_CACHE.get(key));
        if (value == null) {
            final Map<String, AttrValueSystem> attrs = proxy().findAttributeValues();
            preloadAttributeValues(attrs);
            AttrValue attrValue = attrs.get(key);
            if (attrValue != null) {
                return attrValue.getVal();
            }
        }
        return value;
    }

    private void preloadAttributeValues(final Map<String, AttrValueSystem> attrs) {
        for (final Map.Entry<String, AttrValueSystem> entry : attrs.entrySet()) {
            PREF_CACHE.put(entry.getKey(), entry.getValue().getVal());
            PREF_CACHE.put(entry.getValue().getAttrvalueId(), entry.getValue().getVal());
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getAttributeValueOrDefault(final String key, final String defaultValue) {
        final String original = getAttributeValue(key);
        if (StringUtils.isBlank(original)) {
            return defaultValue;
        }
        return original;
    }

    /**
     * {@inheritDoc}
     */
    public String createOrGetAttributeValue(final String key, final String eType) {
        final String value = this.getAttributeValueOrDefault(key, null);
        if (value == null) {
            synchronized (SystemService.class) {
                final Map<String, AttrValueSystem> current = findAttributeValues();
                if (!current.containsKey(key)) {
                    runtimeAttributeService.create(key, AttributeGroupNames.SYSTEM, eType);
                }
            }
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, AttrValueSystem> findAttributeValues() {
        final System system = getSystem();
        if (system == null) {
            return Collections.emptyMap();
        }
        return system.getAttributes();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void updateAttributeValue(final String key, final String value) {

        AttrValueSystem attrVal = attrValueEntitySystemDao.findSingleByCriteria(" where e.attributeCode = ?1", key);

        if (attrVal == null) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("updating system preference {} with {} (previous value was absent)", key, value);
            }

            final System system = getSystem();

            if (system != null) {

                Attribute attr = attributeService.findByAttributeCode(key);

                if (attr != null) {

                    attrVal = systemDao.getEntityFactory().getByIface(AttrValueSystem.class);
                    attrVal.setVal(value);
                    attrVal.setAttributeCode(attr.getCode());
                    attrVal.setSystem(system);
                    system.getAttributes().put(key, attrVal);
                } else {
                    LOG.warn("Unable to update system preference because {} attribute does not exists", key);
                }
            } else {
                LOG.error("TSYSTEM entry is not found");
            }
        } else {

            if (LOG.isDebugEnabled()) {
                LOG.debug("updating system preference {} with {} (previous value was {})", new Object[]{key, value, attrVal.getVal()});
            }

            attrVal.setVal(value);
        }

        attrValueEntitySystemDao.saveOrUpdate((AttrValueEntitySystem) attrVal);
        attrValueEntitySystemDao.flushClear();

        if (attrVal != null) {
            PREF_CACHE.put(key, value);
            PREF_CACHE.put(attrVal.getAttrvalueId(), attrVal.getVal());
        }
    }


    /**
     * {@inheritDoc}
     */
    public String getDefaultShopURL() {
        return proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_DEFAULT_SHOP);
    }

    /**
     * {@inheritDoc}
     */
    public String getPreviewShopURLTemplate() {
        return proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_PREVIEW_URL_TEMPLATE);
    }

    /**
     * {@inheritDoc}
     */
    public String getPreviewShopURICss() {
        return proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_PREVIEW_URI_CSS);
    }

    /**
     * {@inheritDoc}
     */
    public String getMailResourceDirectory() {
        return addTailFileSeparator(proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_MAILTEMPLATES_FSPOINTER));
    }


    /**
     * {@inheritDoc}
     */
    public String getDefaultResourceDirectory() {
        return proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_DEFAULT_FSPOINTER);
    }

    /**
     * {@inheritDoc}
     */
    public String getImageRepositoryDirectory() {

        final String attrValue = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_IMAGE_VAULT);
        if (StringUtils.isBlank(attrValue)) {
            return "context://../imagevault/";
        }

        return addTailFileSeparator(attrValue);
    }

    /**
     * {@inheritDoc}
     */
    public String getFileRepositoryDirectory() {

        final String attrValue = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_FILE_VAULT);
        if (StringUtils.isBlank(attrValue)) {
            return "context://../filevault/";
        }

        return addTailFileSeparator(attrValue);
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemFileRepositoryDirectory() {

        final String attrValue = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_SYSFILE_VAULT);
        if (StringUtils.isBlank(attrValue)) {
            return "context://../sysfilevault/";
        }

        return addTailFileSeparator(attrValue);
    }


    /**
     * {@inheritDoc}
     */
    public Integer getEtagExpirationForImages() {
        final String expirationTimeout = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_ETAG_CACHE_IMAGES_TIME);
        if (expirationTimeout != null) {
            return Integer.valueOf(expirationTimeout);
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getEtagExpirationForPages() {
        final String expirationTimeout = proxy().getAttributeValue(AttributeNamesKeys.System.SYSTEM_ETAG_CACHE_PAGES_TIME);
        if (expirationTimeout != null) {
            return Integer.valueOf(expirationTimeout);
        }
        return 0;
    }


    private String addTailFileSeparator(final String str) {
        if (!str.endsWith(File.separator)) {
            return str + File.separator;
        }
        return str;
    }


    private System getSystem() {
        final List<System> sys = systemDao.findAll();
        if (sys.isEmpty()) {
            return null;
        }
        return sys.get(0);
    }


    /**
     * {@inheritDoc}
     */
    public GenericDAO getGenericDao() {
        return systemDao;
    }

    private SystemService proxy;

    private SystemService proxy() {
        if (proxy == null) {
            proxy = getSelf();
        }
        return proxy;
    }

    /**
     * @return self proxy
     */
    public SystemService getSelf() {
        // Strping AOP
        return null;
    }


}
