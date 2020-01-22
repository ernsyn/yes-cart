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

package org.yes.cart.domain.entity;


import org.yes.cart.domain.i18n.I18NModel;

/**
 * Attr value object.
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */

public interface AttrValue extends Auditable {

    /**
     * Get primary key.
     *
     * @return pk value
     */
    long getAttrvalueId();

    /**
     * Set pk value.
     *
     * @param attrvalueId pk value.
     */
    void setAttrvalueId(long attrvalueId);

    /**
     * Get the string representation of attribute value.
     *
     * @return attribute value.
     */
    String getVal();

    /**
     * Set attribute value.
     *
     * @param val value
     */
    void setVal(String val);

    /**
     * Get the string representation of attribute value.
     *
     * @return attribute value.
     */
    String getIndexedVal();

    /**
     * Set attribute value.
     *
     * @param val value
     */
    void setIndexedVal(String val);

    /**
     * Get display value.
     *
     * @return display value.
     */
    I18NModel getDisplayVal();

    /**
     * Set display value.
     * @param displayVal display value.
     */
    void setDisplayVal(I18NModel displayVal);

    /**
     * Get the attribute.
     *
     * @return {@link Attribute}
     */
    String getAttributeCode();

    /**
     * Set attribute.
     *
     * @param attributeCode attribute.
     */
    void setAttributeCode(String attributeCode);


}


