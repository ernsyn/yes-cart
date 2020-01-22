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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkimport.csv.CsvImportColumn;

import static org.junit.Assert.assertEquals;

/**
 * User: denispavlov
 * Date: 12-08-11
 * Time: 2:15 PM
 */
public class CsvImportTupleImplTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGetColumnValue() throws Exception {

        final CsvImportColumn column = mockery.mock(CsvImportColumn.class, "column");
        final CsvValueAdapter adapter = mockery.mock(CsvValueAdapter.class, "adapter");

        final String[] line = new String[] { "A'''BC", "123" };
        final CsvImportTupleImpl tuple = new CsvImportTupleImpl("file", 1, line);

        mockery.checking(new Expectations() {{
            allowing(column).getColumnIndex(); will(returnValue(0));
            allowing(column).getValue("A'''BC", adapter, tuple); will(returnValue("A'''BC"));
            allowing(column).getGroupCount("A'''BC"); will(returnValue(1));
        }});

        assertEquals(tuple.getColumnValue(column, adapter), "A'''BC");

        mockery.assertIsSatisfied();

    }

    @Test
    public void testGetColumnConstant() throws Exception {

        final CsvImportColumn column = mockery.mock(CsvImportColumn.class, "column");
        final CsvValueAdapter adapter = mockery.mock(CsvValueAdapter.class, "adapter");

        final String[] line = new String[] { "val1", "val2" };
        final CsvImportTupleImpl tuple = new CsvImportTupleImpl("file", 1, line);

        mockery.checking(new Expectations() {{
            allowing(column).getColumnIndex(); will(returnValue(-1));
            allowing(column).getValue(null, adapter, tuple); will(returnValue("const"));
        }});

        assertEquals(tuple.getColumnValue(column, adapter), "const");

        mockery.assertIsSatisfied();

    }
}
