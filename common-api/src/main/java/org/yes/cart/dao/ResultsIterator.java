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

package org.yes.cart.dao;

import java.util.Iterator;

/**
 * User: denispavlov
 * Date: 07/11/2013
 * Time: 07:53
 */
public interface ResultsIterator<T> extends Iterator<T> {

    /**
     * Always throw exception as this is a read only scroll.
     */
    @Override
    void remove();

    /**
     * Release resources if the results are not fully consumed.
     */
    void close();

}
