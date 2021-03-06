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

package org.yes.cart.icecat.transform.csv

import org.yes.cart.icecat.transform.xml.ProductPointerHandler
import org.yes.cart.icecat.transform.domain.ProductPointer
import org.yes.cart.icecat.transform.Util

/**
 * User: denispavlov
 * Date: 12-08-09
 * Time: 10:15 PM
 */
class ProductCategoryCsvAdapter {

    Map<String, ProductPointer> productMap;

    ProductCategoryCsvAdapter(final Map<String, ProductPointer> productMap) {
        this.productMap = productMap
    }

    public toCsvFile(String filename) {

        StringBuilder builder = new StringBuilder();
        builder.append("product guid;model;category guid;category name\n");

        productMap.values().each {
            def pp = it;
            it.categories.values().each {
                def cat = it;
                builder.append('"')
                builder.append(pp.Product_ID_valid).append('";"')
                builder.append(Util.escapeCSV(pp.Model_Name)).append('";"')
                builder.append(cat.id).append('";"')
                builder.append(Util.escapeCSV(cat.getNameFor('en'))).append('"\n')
            }
        }
        new File(filename).write(builder.toString(), 'UTF-8');

    }
}
