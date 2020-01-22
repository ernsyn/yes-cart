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

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:26
 */
public interface ClusterUtilsService {

    /**
     * @return true if service is online
     */
    boolean ping();

    /**
     * Preload main caches.
     */
    void warmUp();

    /**
     * Reload system configurations.
     */
    void reloadConfigurations();

}
