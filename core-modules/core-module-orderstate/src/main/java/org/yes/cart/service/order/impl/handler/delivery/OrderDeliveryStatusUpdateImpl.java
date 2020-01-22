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

package org.yes.cart.service.order.impl.handler.delivery;

import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 17/02/2017
 * Time: 12:13
 */
public class OrderDeliveryStatusUpdateImpl implements OrderDeliveryStatusUpdate {

    private final String orderNumber;
    private final String supplierCode;
    private final List<OrderDeliveryLineStatusUpdate> lineStatus;
    private final Map<String, Pair<String, I18NModel>> additionalData = new HashMap<>();

    public OrderDeliveryStatusUpdateImpl(final String orderNumber,
                                         final String supplierCode,
                                         final List<OrderDeliveryLineStatusUpdate> lineStatus) {
        this.orderNumber = orderNumber;
        this.supplierCode = supplierCode;
        this.lineStatus = lineStatus;
    }

    public OrderDeliveryStatusUpdateImpl(final String orderNumber,
                                         final String supplierCode,
                                         final List<OrderDeliveryLineStatusUpdate> lineStatus,
                                         final Map<String, Pair<String, I18NModel>> additionalData) {
        this.orderNumber = orderNumber;
        this.supplierCode = supplierCode;
        this.lineStatus = lineStatus;
        this.additionalData.putAll(additionalData);
    }

    @Override
    public String getOrderNumber() {
        return orderNumber;
    }

    @Override
    public String getSupplierCode() {
        return supplierCode;
    }

    @Override
    public List<OrderDeliveryLineStatusUpdate> getLineStatus() {
        return lineStatus;
    }

    @Override
    public Map<String, Pair<String, I18NModel>> getAdditionalData() {
        return additionalData;
    }

    @Override
    public String toString() {
        return "OrderDeliveryStatusUpdateImpl{" +
                "orderNumber='" + orderNumber + '\'' +
                ", supplierCode='" + supplierCode + '\'' +
                ", lineStatus=" + lineStatus +
                '}';
    }
}
