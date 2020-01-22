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
import org.yes.cart.bulkcommon.csv.CsvImpExColumn;
import org.yes.cart.bulkimport.csv.CsvImportColumn;
import org.yes.cart.bulkimport.csv.CsvImportDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: denispavlov
 * Date: 12-08-03
 * Time: 9:35 AM
 */
public class CsvImportDescriptorXStreamProviderTest {

    @Test
    public void testProvide() throws Exception {

        testProvide("src/test/resources/import/csv/schematest001.xml");
        testProvide("src/test/resources/import/csv/schematest002.xml");

    }

    public void testProvide(final String filename) throws Exception {
        final XStreamProvider<CsvImportDescriptor> provider = new CsvImportDescriptorXStreamProvider();

        final InputStream inputStream = new FileInputStream(filename);
        final CsvImportDescriptor desc = provider.fromXML(inputStream);

        assertNotNull(desc);

        assertEquals(CsvImportDescriptor.ImportMode.DELETE, desc.getMode());
        assertEquals(CsvImportDescriptor.ImportMode.DELETE.name(), desc.getModeName());

        assertNotNull(desc.getContext());
        assertEquals("SHOP10", desc.getContext().getShopCode());
        assertEquals("CSV", desc.getContext().getImpExService());

        assertEquals("org.yes.cart.domain.entity.Attribute", desc.getEntityType());
        assertNull(desc.getImportDirectory());
        assertNotNull(desc.getImportFileDescriptor());

        assertEquals("UTF-8", desc.getImportFileDescriptor().getFileEncoding());
        assertEquals("attributenames.csv", desc.getImportFileDescriptor().getFileNameMask());
        assertTrue(desc.getImportFileDescriptor().isIgnoreFirstLine());
        assertEquals(';', desc.getImportFileDescriptor().getColumnDelimiter());
        assertEquals('"', desc.getImportFileDescriptor().getTextQualifier());

        assertEquals("select b from AttributeEntity b where b.code = {code}", desc.getSelectCmd());
        assertEquals("INSERT INTO TATTRIBUTE (VERSION, GUID, CODE) VALUES (0, {GUID}, {code})", desc.getInsertCmd());
        assertEquals("delete from AttributeEntity b where b.code = {code}", desc.getDeleteCmd());

        assertNotNull(desc.getColumns());
        assertEquals(11, desc.getColumns().size());

        final List<CsvImportColumn> columns = new ArrayList<>(desc.getColumns());

        final CsvImportColumn col0 = columns.get(0);
        assertNotNull(col0);
        assertEquals(0, col0.getColumnIndex());
        assertEquals(CsvImpExColumn.FK_FIELD, col0.getFieldType());
        assertEquals("attributeGroup", col0.getName());
        assertNull(col0.getValueRegEx());
        assertEquals(Integer.valueOf(1), col0.getValueRegExGroup());
        assertEquals("select b from AttributeGroupEntity b where b.code = {attributeGroup}", col0.getLookupQuery());
        assertEquals("CTX001", col0.getContext());

        final CsvImportColumn col5 = columns.get(5);
        assertNotNull(col5);
        assertEquals(4, col5.getColumnIndex());
        assertEquals(CsvImpExColumn.FIELD, col5.getFieldType());
        assertEquals("displayName", col5.getName());
        assertEquals("(.{0,255})(.*)", col5.getValueRegEx());
        assertEquals(Integer.valueOf(1), col5.getValueRegExGroup());
        assertEquals("Rus: $1", col5.getValueRegExTemplate());
        assertEquals("ru", col5.getLanguage());
        assertNull(col5.getLookupQuery());

    }

}
