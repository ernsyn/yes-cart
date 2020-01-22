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

package org.yes.cart.bulkcommon.service.support.common.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.bulkcommon.csv.CsvValueAdapter;
import org.yes.cart.bulkcommon.service.support.csv.impl.ColumnValueLookUpQueryParameterStrategyValueProviderImpl;
import org.yes.cart.bulkcommon.service.support.query.LookUpQuery;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategy;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategyValueProvider;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.bulkimport.csv.CsvImportTuple;
import org.yes.cart.domain.entity.Identifiable;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 10:21 AM
 */
public class ImpExColumnLookUpQueryStrategyTest {

    private final Mockery mockery = new JUnit4Mockery();

    @Test
    public void testGetQuery() throws Exception {

        final ImpExColumnLookUpQueryStrategy strategy = new ImpExColumnLookUpQueryStrategy();

        strategy.setProviders(new HashMap<String, LookUpQueryParameterStrategyValueProvider>() {{
            put(LookUpQueryParameterStrategy.MASTER_ID, new MasterObjectIdLookUpQueryParameterStrategyValueProviderImpl());
        }});
        strategy.setDefaultProvider(new ColumnValueLookUpQueryParameterStrategyValueProviderImpl());

        final Identifiable master = mockery.mock(Identifiable.class, "master");
        final CsvImportDescriptor descriptor = mockery.mock(CsvImportDescriptor.class, "descriptor");
        final CsvImportColumn codeColumn = mockery.mock(CsvImportColumn.class, "codeColumn");
        final CsvImportTuple tuple = mockery.mock(CsvImportTuple.class, "tuple");
        final CsvValueAdapter adapter = mockery.mock(CsvValueAdapter.class, "adapter");


        mockery.checking(new Expectations() {{
            oneOf(master).getId(); will(returnValue(10L));
            oneOf(descriptor).getColumn("code"); will(returnValue(codeColumn));
            oneOf(tuple).getColumnValue(codeColumn, adapter); will(returnValue("A''BC"));
        }});

        final LookUpQuery query = strategy.getQuery(descriptor, master, tuple, adapter,
                "select * from Entity e where e.parentId = {masterObjectId} and e.code = {code} ");

        assertNotNull(query);
        assertEquals(query.getQueryString(), "select * from Entity e where e.parentId = ?1 and e.code = ?2 ");
        assertNotNull(query.getParameters());
        assertEquals(query.getParameters().length, 2);
        assertEquals(query.getParameters()[0], Long.valueOf(10L));
        assertEquals(query.getParameters()[1], "A''BC"); // escaped value

        mockery.assertIsSatisfied();

    }
}
