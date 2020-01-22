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

import org.yes.cart.cluster.service.QueryDirector;
import org.yes.cart.cluster.service.QueryDirectorPlugin;

import java.util.List;

/**
 * User: denispavlov
 * Date: 13/04/2019
 * Time: 17:40
 */
public class LocalQueryDirectorImpl extends QueryDirectorImpl implements QueryDirector {

    public LocalQueryDirectorImpl(final List<QueryDirectorPlugin> plugins) {
        super(plugins);
    }
    
}
