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
package org.yes.cart.cluster.service;


import org.yes.cart.domain.dto.impl.CacheInfoDTO;

import java.util.List;

/**
 * Service responsible  to evict particular cache(s) depending on entity and operation.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 18 Aug 2013
 * Time: 9:50 AM
 */
public interface CacheDirector {

    interface EntityOperation {

        String CREATE = "Create";
        String DELETE = "Delete";
        String UPDATE = "Update";

    }

    /**
     * @return true if service is online
     */
    boolean ping();

    /**
     * Get cache information.
     * @return list of information per each cache.
     */
    List<CacheInfoDTO> getCacheInfo();

    /**
     * Evict all caches, which are represent in getCacheInfo list.
     *
     * @param force if true will remove all caches, if false will skip the evict all list setting
     */
    void evictAllCache(boolean force);

    /**
     * Evict specific cache.
     */
    void evictCache(String cache);

    /**
     * Enable specific cache statistics.
     */
    void enableCache(String cache);

    /**
     * Enable specific cache statistics.
     */
    void disableCache(String cache);

    /**
     * Fire event entity change event
     *
     * @param entityOperation operation type
     * @param entityName entity type
     * @param pkValue primary key
     */
    int onCacheableChange(String entityOperation, String entityName, Long pkValue);

    /**
     * Fire event entity change event
     *
     * @param entityOperation operation type
     * @param entityName entity type
     * @param pkValues primary keys
     */
    int onCacheableBulkChange(String entityOperation, String entityName, Long[] pkValues);

}
