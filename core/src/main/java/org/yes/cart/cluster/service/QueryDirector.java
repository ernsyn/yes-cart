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

import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 16:27
 */
public interface QueryDirector {

    /**
     * List of supported queries.
     *
     * @return list of supported queries
     */
    List<String> supportedQueries();

    /**
     * Execute query and return a result.
     *
     * @param type type of execute.
     * @param query query to execute.
     *
     * @return list of rows
     */
    List<Object[]> runQuery(String type, String query);

}
