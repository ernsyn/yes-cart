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

package org.yes.cart.service.theme.templates;

import java.util.Map;

/**
 * User: denispavlov
 * Date: 21/12/2015
 * Time: 18:00
 */
public interface TemplateSupport {

    interface Template {

        String make(Map binding);

    }

    /**
     * Template.
     *
     * @param template template
     *
     * @return template to use
     */
    Template get(String template);

}
