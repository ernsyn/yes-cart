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

import org.yes.cart.bulkcommon.model.ImpExDescriptor;
import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.service.support.query.LookUpQueryParameterStrategyValueProvider;
import org.yes.cart.utils.TimeContext;

/**
 * User: denispavlov
 * Date: 11/06/2015
 * Time: 12:22
 */
public class NowLookUpQueryParameterStrategyValueProviderImpl
        implements LookUpQueryParameterStrategyValueProvider<ImpExDescriptor, ImpExTuple, Object> {

    private enum Type {
        LDT, LD, I
    }

    private Type javaType = Type.LDT;

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getPlaceholderValue(final String placeholder,
                                      final ImpExDescriptor descriptor,
                                      final Object masterObject,
                                      final ImpExTuple tuple,
                                      final Object adapter,
                                      final String queryTemplate) {

        if (javaType == Type.LDT) {
            return TimeContext.getLocalDateTime();
        } else if (javaType == Type.LD) {
            return TimeContext.getLocalDate();
        }
        return TimeContext.getTime();
    }

    public void setJavaType(final String javaType) {
        if ("java.time.LocalDateTime".equals(javaType)) {
            this.javaType = Type.LDT;
        } else if ("java.time.LocalDate".equals(javaType)) {
            this.javaType = Type.LD;
        } else if ("java.time.Instant".equals(javaType)) {
            this.javaType = Type.I;
        } else {
            throw new IllegalArgumentException("Unsupported time class " + javaType);
        }
    }
}
