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

package org.yes.cart.payment.impl;

import org.yes.cart.payment.PaymentGatewayInternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;

/**
 * Courier offline payment gateway.
 *
* User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CourierPaymentGatewayImpl extends AbstractPaymentGatewayImpl implements PaymentGatewayInternalForm {


    private static final PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            true, true, true, false,
            false, true, true,
            false, false, false, false,
            null,
            true, true
    );



    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return PAYMENT_GATEWAY_FEATURE;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Payment authorize(final Payment paymentIn, final boolean forceProcessing) {
        return runDefaultOperation(paymentIn, AUTH, Payment.PAYMENT_STATUS_OK, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment reverseAuthorization(final Payment paymentIn, final boolean forceProcessing) {
        return runDefaultOperation(paymentIn, REVERSE_AUTH, Payment.PAYMENT_STATUS_OK, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment capture(final Payment paymentIn, final boolean forceProcessing) {
        return runDefaultOperation(paymentIn, CAPTURE, Payment.PAYMENT_STATUS_OK, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment authorizeCapture(final Payment paymentIn, final boolean forceProcessing) {
        return runDefaultOperation(paymentIn, AUTH_CAPTURE, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment voidCapture(final Payment paymentIn, final boolean forceProcessing) {
        return runDefaultOperation(paymentIn, VOID_CAPTURE, Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment refund(final Payment paymentIn, final boolean forceProcessing) {
        return runDefaultOperation(paymentIn, REFUND, Payment.PAYMENT_STATUS_OK, false);
    }
}
