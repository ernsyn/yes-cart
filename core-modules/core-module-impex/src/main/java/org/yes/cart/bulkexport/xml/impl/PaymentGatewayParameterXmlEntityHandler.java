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

package org.yes.cart.bulkexport.xml.impl;

import org.yes.cart.bulkcommon.model.ImpExTuple;
import org.yes.cart.bulkcommon.xml.XmlValueAdapter;
import org.yes.cart.bulkexport.xml.XmlExportDescriptor;
import org.yes.cart.payment.persistence.entity.PaymentGatewayParameter;
import org.yes.cart.service.async.JobStatusListener;

import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 26/10/2018
 * Time: 08:08
 */
public class PaymentGatewayParameterXmlEntityHandler extends AbstractXmlEntityHandler<PaymentGatewayParameter> {

    public PaymentGatewayParameterXmlEntityHandler() {
        super("payment-gateway-parameters");
    }

    @Override
    public void handle(final JobStatusListener statusListener,
                       final XmlExportDescriptor xmlExportDescriptor,
                       final ImpExTuple<String, PaymentGatewayParameter> tuple,
                       final XmlValueAdapter xmlValueAdapter,
                       final String fileToExport,
                       final OutputStreamWriter writer,
                       final Map<String, Integer> entityCount) throws Exception {

        handleInternal(tagParam(null, tuple.getData()), writer, entityCount);

    }


    Tag tagParam(final Tag parent, final PaymentGatewayParameter param) {

        return tag(parent, "payment-gateway-parameter")
                .attr("id", param.getPaymentGatewayParameterId())
                .attr("guid", param.getGuid())
                    .tagChars("payment-gateway", param.getPgLabel())
                    .tagChars("code", param.getLabel())
                    .tagChars("name", param.getName())
                    .tagCdata("value", param.getValue())
                    .tagChars("business-type", param.getBusinesstype())
                    .tagBool("secure", param.isSecure())
                    .tagTime(param)
                .end();

    }

}
