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

package org.yes.cart.service.media.impl;

import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.service.media.MediaFileNameStrategy;

/**
 * User: denispavlov
 * Date: 27/04/2019
 * Time: 15:56
 */
public class MediaFileNameStrategyConfigurableCachedImpl extends MediaFileNameStrategyCachedImpl implements Configuration {

    private ConfigurationContext cfgContext;

    public MediaFileNameStrategyConfigurableCachedImpl(final MediaFileNameStrategy mediaFileNameStrategy) {
        super(mediaFileNameStrategy);
    }


    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }

}
