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

import org.junit.Test;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 11/27/11
 * Time: 1:32 PM
 */
public class CsvImportColumnImplTest {

    @Test
    public void testGetValue() {

        final CsvValueAdapter adapter = (rawValue, requiredType, impExColumn, tuple) -> rawValue;

        String reExpKey = "((\\D?\\S{4,}){1,}).*-?.*";
        String reExpValue = ".*-.*\\s{1,}((\\D?\\S{4,}){1,})\\s{0,}.*";


        CsvImportColumnImpl column = new CsvImportColumnImpl();
        CsvImportTupleImpl tuple = new CsvImportTupleImpl("file", 1, new String[0]);
        assertNull(column.getValue(null, adapter, tuple));

        String rawValue = "rawValue";
        assertEquals(rawValue, column.getValue(rawValue, adapter, tuple));

        column.setValueRegEx("(.*)");
        assertEquals(rawValue, column.getValue(rawValue, adapter, tuple));

        column.setValueRegEx("(raw)");
        assertEquals("raw", column.getValue(rawValue, adapter, tuple));

        column.setValueRegEx("(Value)");
        assertEquals("Value", column.getValue(rawValue, adapter, tuple));


        column.setValueRegEx(reExpKey);
        assertEquals("SomeKey", column.getValue("SomeKey       -  Value", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Ключик", column.getValue("Ключик       -  Значение", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Ключик", column.getValue(" Ключик       -  Значение", adapter, tuple));

        //The same test on unicode string with some noise
        column.setValueRegEx(reExpKey);
        assertEquals(getprops(), "result", column.getValue("as фыф result - шум 34 123.56 значение sd ыв #", adapter, tuple));


        //The same test on unicode string with some noise
        column.setValueRegEx(reExpKey);
        assertEquals("результат", column.getValue("as фыф результат - шум 34 123.56 значение sd ыв #", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Діамант жовтий", column.getValue("фыв 3 фвк Діамант жовтий 3/ 6 - 0,10 Ct", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Кубічний цирконій", column.getValue("2 Кубічний цирконій - 66 шт", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Онікс,перли,кварц димчастий", column.getValue("0 Онікс,перли,кварц димчастий -  гр", adapter, tuple));

        column.setValueRegEx(reExpKey);
        assertEquals("Онікс", column.getValue("Онікс -", adapter, tuple));


        // a value in key - value pair
        column.setValueRegEx(".*\\s{0,}\\-\\s{0,}(Value)");
        assertEquals("Value", column.getValue("SomeKey       -  ValueZZZ", adapter, tuple));

        column.setValueRegEx(reExpValue);
        assertEquals("Value", column.getValue("SomeKey       -  Value", adapter, tuple));


        column.setValueRegEx(reExpValue);
        assertEquals("Значение", column.getValue(" Ключ       -  Значение", adapter, tuple));

        column.setValueRegEx(reExpValue);
        assertEquals("значение", column.getValue("as фыф результат-шум 34 123.56 значение sd ыв #", adapter, tuple));


        //some single unicode word regex
        column.setValueRegEx(".*\\b([a-z]{3,})\\b.*");
        assertEquals("word", column.getValue("1234sdcvdfv hfkdf34 word 2134", adapter, tuple));

        column.setValueRegExTemplate("My '$1'");
        assertEquals("My 'word'", column.getValue("1234sdcvdfv hfkdf34 word 2134", adapter, tuple));

        column.setValueRegExTemplate("");
        column.setValueRegEx("\\b(\\S\\D{3,})\\b");
        assertEquals("word", column.getValue("1234sdcvdfv hfkdf34 word 2134", adapter, tuple));

        column.setValueRegEx("\\b(\\S\\D{3,})\\b");
        assertEquals("слово", column.getValue("1234sdcvdfv hfkdf34 слово 2134", adapter, tuple));

        /*
        real examples
        35 Діамант кр57 3/5 - 0,10 Ct; 1 Сапфір   - 0,62 Ct; 1 Рубін   - 0,54 Ct
        2 Кубічний цирконій - 66 шт,                Аметист - 2,68 гр
        0 Кварц димчастий   -  Ct
        0 Онікс,перли,кварц димчастий -  гр
         */


    }

    String getprops() {
        StringBuilder stringBuilder = new StringBuilder();
        Properties prop = System.getProperties();
        Set<Map.Entry<Object, Object>> set = prop.entrySet();
        for (Map.Entry<Object, Object> ent : set) {
            stringBuilder.append(ent.getKey());
            stringBuilder.append("=");
            stringBuilder.append(ent.getValue());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}