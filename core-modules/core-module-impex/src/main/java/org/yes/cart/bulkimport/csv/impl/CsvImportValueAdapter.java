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

package org.yes.cart.bulkimport.csv.impl;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkcommon.csv.CsvImpExTuple;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkcommon.model.ImpExValues;
import org.yes.cart.bulkcommon.model.impl.AbstractExtensibleValueAdapter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 12-08-11
 * Time: 1:04 PM
 */
public class CsvImportValueAdapter extends AbstractExtensibleValueAdapter<CsvValueAdapter> implements CsvValueAdapter {

    private final GenericConversionService extendedConversionService;

    private static final Map<String, Class> MAPPING = new HashMap<String, Class>() {{
        put(ImpExValues.STRING,    String.class);
        put(ImpExValues.BOOLEAN,   Boolean.class);
        put(ImpExValues.INT,       Integer.class);
        put(ImpExValues.LONG,      Long.class);
        put(ImpExValues.DECIMAL,   BigDecimal.class);
        put(ImpExValues.DATE,      LocalDate.class);
        put(ImpExValues.DATETIME,  LocalDateTime.class);
        put(ImpExValues.ZONEDTIME, ZonedDateTime.class);
        put(ImpExValues.INSTANT,   Instant.class);
    }};

    public CsvImportValueAdapter(final GenericConversionService extendedConversionService) {
        this.extendedConversionService = extendedConversionService;
    }

    @Override
    public Object fromRaw(final Object rawValue, final String requiredType, final CsvImpExColumn csvImpExColumn, final CsvImpExTuple tuple) {
        if (requiredType == null) {
            return rawValue;
        }

        final CsvValueAdapter specific = getTypeSpecific(requiredType);
        if (specific != null) {
            return specific.fromRaw(rawValue, requiredType, csvImpExColumn, tuple);
        }
        if (!MAPPING.containsKey(requiredType)) {
            return rawValue;
        }
        return this.extendedConversionService.convert(rawValue,
                TypeDescriptor.valueOf(rawValue.getClass()),
                TypeDescriptor.valueOf(MAPPING.get(requiredType)));
    }
}
