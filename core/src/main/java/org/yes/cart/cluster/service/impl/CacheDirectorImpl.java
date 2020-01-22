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
package org.yes.cart.cluster.service.impl;

import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.statistics.StatisticsGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.yes.cart.cluster.service.CacheDirector;
import org.yes.cart.domain.dto.impl.CacheInfoDTO;
import org.yes.cart.domain.misc.Pair;

import java.util.*;

/**
 * Service responsible  to evict particular cache(s) depending on entity and operation.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 18 Aug 2013
 * Time: 9:50 AM
 */
public class CacheDirectorImpl implements CacheDirector {

    private static final Logger LOG = LoggerFactory.getLogger(CacheDirectorImpl.class);

    private Map<String, Map<String, Set<Pair<String, String>>>> entityOperationCache;

    private CacheManager cacheManager;

    private Set<String> skipEvictAll = Collections.emptySet();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CacheInfoDTO> getCacheInfo() {
        final Collection<String> cacheNames = cacheManager.getCacheNames();
        final List<CacheInfoDTO> rez = new ArrayList<>(cacheNames.size());
        for (String cacheName : cacheNames) {
            final Cache cache = cacheManager.getCache(cacheName);
            final net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
            final CacheConfiguration cacheConfiguration = nativeCache.getCacheConfiguration();
            final StatisticsGateway stats = nativeCache.getStatistics();

            rez.add(
                    new CacheInfoDTO(
                            nativeCache.getName(),
                            nativeCache.getSize(),
                            stats.getLocalHeapSize(),
                            cacheConfiguration.getMaxEntriesLocalHeap(),
                            cacheConfiguration.isOverflowToDisk(),
                            cacheConfiguration.isEternal(),
                            cacheConfiguration.getTimeToLiveSeconds(),
                            cacheConfiguration.getTimeToIdleSeconds(),
                            cacheConfiguration.getMemoryStoreEvictionPolicy().toString(),
                            stats.getLocalDiskSize(),
                            stats.getCore().get().value(CacheOperationOutcomes.GetOutcome.HIT),
                            stats.getExtended().allMiss().count().value(),
                            stats.getLocalHeapSizeInBytes(),
                            stats.getLocalDiskSizeInBytes(),
                            nativeCache.isDisabled()
                    )
            );

        }
        return rez;
    }

    Set<String> getSkipEvictAll() {
        return skipEvictAll;
    }

    CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictAllCache(final boolean force) {
        final CacheManager cm = getCacheManager();
        for (String cacheName : cm.getCacheNames()) {
            if (force || !this.skipEvictAll.contains(cacheName)) {
                final Cache cache = cm.getCache(cacheName);
                cache.clear();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictCache(final String cacheName) {
        final CacheManager cm = getCacheManager();
        final Cache cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableCache(final String cacheName) {
        final CacheManager cm = getCacheManager();
        final Cache cache = cm.getCache(cacheName);
        if (cache != null) {
            final net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
            nativeCache.setDisabled(false);
        }
    }

    @Override
    public void disableCache(final String cacheName) {
        final CacheManager cm = getCacheManager();
        final Cache cache = cm.getCache(cacheName);
        if (cache != null) {
            final net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
            nativeCache.setDisabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onCacheableChange(final String entityOperation, final String entityName, final Long pkValue) {

        int cnt = 0;

        final Set<Pair<String, String>> cacheNames = resolveCacheNames(entityOperation, entityName);

        if (cacheNames != null) {

            final CacheManager cm = getCacheManager();

            for (Pair<String, String> cacheStrategy : cacheNames) {

                final Cache cache = cm.getCache(cacheStrategy.getFirst());

                if (cache != null) {

                    if("all".equals(cacheStrategy.getSecond())) {

                        cache.clear();

                        cnt ++;

                    } else if("key".equals(cacheStrategy.getSecond())) {

                        cache.evict(pkValue);

                        cnt ++;

                    } else {

                        LOG.warn("The [{}] cache eviction strategy not supported", cacheStrategy.getSecond());

                    }

                }

            }

        }

        return cnt;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onCacheableBulkChange(final String entityOperation, final String entityName, final Long[] pkValues) {

        int cnt = 0;

        final Set<Pair<String, String>> cacheNames = resolveCacheNames(entityOperation, entityName);

        if (cacheNames != null) {

            final CacheManager cm = getCacheManager();

            for (Pair<String, String> cacheStrategy : cacheNames) {

                final Cache cache = cm.getCache(cacheStrategy.getFirst());

                if (cache != null) {

                    if("all".equals(cacheStrategy.getSecond())) {

                        cache.clear();

                        cnt += pkValues.length;

                    } else if("key".equals(cacheStrategy.getSecond())) {

                        for (final Long pkValue : pkValues) {

                            cache.evict(pkValue);

                            cnt++;

                        }

                    } else {

                        LOG.warn("The [{}] cache eviction strategy not supported", cacheStrategy.getSecond());

                    }

                }

            }

        }

        return cnt;
    }

    /**
     * Resolve caches names for invalidation for given entity and operation.
     * @param entityOperation given operation
     * @param entityName given entity name
     * @return set of cache names
     */
    Set<Pair<String, String>> resolveCacheNames(final String entityOperation, final String entityName) {

        final Map<String, Set<Pair<String, String>>> entOperations = this.entityOperationCache.get(entityName);

        if (entOperations != null) {
            return entOperations.get(entityOperation);
        }

        return null;

    }

    /** IoC. Set configuration. */
    public void setEntityOperationCache(final Map<String, Map<String, Set<Pair<String, String>>>> entityOperationCache) {
        this.entityOperationCache = entityOperationCache;
    }

    /** IoC. Set cache manager.  */
    public void setCacheManager(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /** IoC. Set cachecs that should not be evicted during evict all.  */
    public void setSkipEvictAll(final Set<String> skipEvictAll) {
        this.skipEvictAll = skipEvictAll;
    }
}
