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

package org.yes.cart.bulkexport.csv;

import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 08:00
 */
public interface CsvExportColumn extends CsvImpExColumn {

    /**
     * Get the field value. Regular expression will be used for obtain value if reg exp is set.
     *
     * @param rawValue the whole value from property
     * @param adapter value adapter
     * @param tuple tuple
     *
     * @return value
     */
    String getValue(Object rawValue, CsvValueAdapter adapter, CsvExportTuple tuple);

    /**
     * Column header for csv file
     *
     * @return column index
     */
    String getColumnHeader();


    /**
     * Boolean flag needed to use master object for fk in case of subimport.
     *
     * @return true if need to use master object.
     */
    @Override
    boolean isUseMasterObject();

    /**
     * Set use master object in case of fk subimport.
     *
     * @param useMasterObject use master object flag.
     */
    @Override
    void setUseMasterObject(boolean useMasterObject);

    /**
     * Get the constant for field. Some fields can be field with constants
     *
     * @return filed constant
     */
    @Override
    String getValueConstant();

    /**
     * @return entity type for FK's
     */
    @Override
    String getEntityType();

    /**
     * @return language of the localisable value (or null if this is not localisable)
     */
    @Override
    String getLanguage();

    /**
     * {@inheritDoc}
     */
    @Override
    CsvExportDescriptor getDescriptor();

    /**
     * {@inheritDoc}
     */
    @Override
    CsvExportDescriptor getParentDescriptor();

    /**
     * Set parent descriptor.
     *
     * @param parentDescriptor parent
     */
    void setParentDescriptor(final CsvExportDescriptor parentDescriptor);

}