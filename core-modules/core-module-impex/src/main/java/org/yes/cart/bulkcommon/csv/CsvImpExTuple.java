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

package org.yes.cart.bulkcommon.csv;

import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ImpExTuple;

import java.util.List;

/**
 * Single tuple of data from import source.
 *
 * User: denispavlov
 * Date: 12-08-11
 * Time: 12:54 PM
 */
public interface CsvImpExTuple<S, T, D extends ImpExDescriptor, C extends CsvImpExColumn> extends ImpExTuple<S, T> {

    /**
     * @param column column descriptor
     * @param adapter value adapter
     * @return column value (or values) depending on data
     */
    Object getColumnValue(C column, CsvValueAdapter adapter);

    /**
     * @param importDescriptor import descriptor
     * @param column column descriptor
     * @param adapter value adapter
     * @return sub tuple from a column
     */
    <I extends CsvImpExTuple<S, T, D, C>> List<I> getSubTuples(D importDescriptor, C column, CsvValueAdapter adapter);

}
