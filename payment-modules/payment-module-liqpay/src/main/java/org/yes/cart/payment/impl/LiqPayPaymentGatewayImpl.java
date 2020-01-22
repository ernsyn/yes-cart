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

import com.liqpay.LiqPay;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.payment.CallbackAware;
import org.yes.cart.payment.PaymentGateway;
import org.yes.cart.payment.PaymentGatewayExternalForm;
import org.yes.cart.payment.dto.Payment;
import org.yes.cart.payment.dto.PaymentGatewayFeature;
import org.yes.cart.payment.dto.PaymentLine;
import org.yes.cart.payment.dto.PaymentMiscParam;
import org.yes.cart.payment.dto.impl.BasicCallbackInfoImpl;
import org.yes.cart.payment.dto.impl.PaymentGatewayFeatureImpl;
import org.yes.cart.payment.dto.impl.PaymentImpl;
import org.yes.cart.utils.HttpParamsUtils;
import org.yes.cart.utils.log.Markers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * LiqPay payment gateway implementation.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 1/22/12
 * Time: 12:53 PM
 */
public class LiqPayPaymentGatewayImpl extends AbstractLiqPayPaymentGatewayImpl
        implements PaymentGatewayExternalForm, CallbackAware {

    private static final Logger LOG = LoggerFactory.getLogger(LiqPayPaymentGatewayImpl.class);

    private final static PaymentGatewayFeature PAYMENT_GATEWAY_FEATURE = new PaymentGatewayFeatureImpl(
            false, false, false, true,
            false, false, true,
            true, true, true, true,
            null,
            false, false
    );

    // merchant id
    static final String LP_MERCHANT_ID = "LP_MERCHANT_ID";

    // key
    static final String LP_MERCHANT_KEY = "LP_MERCHANT_KEY";

    // result_url  shopper will be redirected to
    static final String LP_RESULT_URL = "LP_RESULT_URL";

    // server_url back url for server - server communications
    static final String LP_SERVER_URL = "LP_SERVER_URL";

    // form post acton url
    static final String LP_POST_URL = "LP_POST_URL";

    // payment way
    static final String LP_PAYWAY_URL = "LP_PAYWAY_URL";


    /**
     * {@inheritDoc}
     */
    @Override
    public String getPostActionUrl() {
        final String url = getParameterValue(LP_POST_URL);
        if (url.endsWith("/")) {
            return url + "pay";
        }
        return url + "/pay";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubmitButton(final String locale) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Callback convertToCallback(final Map privateCallBackParameters,
                                      final boolean forceProcessing) {

        if (privateCallBackParameters != null) {

            final Map<String, String> params = HttpParamsUtils.createSingleValueMap(privateCallBackParameters);

            final LiqPay api = getLiqPayAPI();

            final String privateKey = getParameterValue(LP_MERCHANT_KEY);
            final String publicKey = getParameterValue(LP_MERCHANT_ID);

            final String amount = params.get("amount");
            final String currency = params.get("currency");
            final String description = params.get("description");
            final String order_id = params.get("order_id");
            final String type = params.get("type");
            final String sender_phone = params.get("sender_phone");
            final String status = params.get("status");
            final String transaction_id = params.get("transaction_id");
            final String signature = params.get("signature");

            final String validSignature = api.str_to_sign(privateKey +
                    amount  +
                    currency +
                    publicKey +
                    order_id +
                    type +
                    description  +
                    status +
                    transaction_id +
                    sender_phone);

            final boolean valid = validSignature.equals(signature);

            if (valid || forceProcessing) {

                BigDecimal callbackAmount = null;
                try {
                    callbackAmount = new BigDecimal(amount);
                } catch (Exception exp) {
                    LOG.error("Callback for {} did not have a valid amount {}", order_id, amount);
                }

                if (valid) {
                    LOG.debug("Signature is valid");
                } else {
                    LOG.warn("Signature is not valid ... forced processing");
                }
                return new BasicCallbackInfoImpl(
                        order_id,
                        CallbackOperation.PAYMENT,
                        callbackAmount,
                        privateCallBackParameters,
                        valid
                );
            } else {
                LOG.debug("Signature is not valid");
            }

        }

        return new BasicCallbackInfoImpl(
                null,
                CallbackOperation.INVALID,
                null,
                privateCallBackParameters,
                false
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CallbackAware.CallbackResult getExternalCallbackResult(final Map<String, String> callbackResult,
                                                                  final boolean forceProcessing) {

        String statusRes = null;

        if (callbackResult != null) {

            final LiqPay api = getLiqPayAPI();

            final String privateKey = getParameterValue(LP_MERCHANT_KEY);
            final String publicKey = getParameterValue(LP_MERCHANT_ID);

            final String amount = callbackResult.get("amount");
            final String currency = callbackResult.get("currency");
            final String description = callbackResult.get("description");
            final String order_id = callbackResult.get("order_id");
            final String type = callbackResult.get("type");
            final String sender_phone = callbackResult.get("sender_phone");
            final String status = callbackResult.get("status");
            final String transaction_id = callbackResult.get("transaction_id");
            final String signature = callbackResult.get("signature");

            final String validSignature = api.str_to_sign(privateKey +
                    amount +
                    currency +
                    publicKey +
                    order_id +
                    type +
                    description +
                    status +
                    transaction_id +
                    sender_phone);

            final boolean valid = validSignature.equals(signature);

            if (valid || forceProcessing) {
                if (valid) {
                    LOG.debug("Signature is valid");
                } else {
                    LOG.warn("Signature is not valid ... forced processing");
                }
                statusRes = status;
            } else {
                LOG.debug("Signature is not valid");
            }

        }

        /*
            success      - успешный платеж
            failure      - неуспешный платеж
            wait_secure  - платеж на проверке
            wait_accept  - Деньги с клиента списаны, но магазин еще не прошел проверку
            wait_lc      - Аккредитив. Деньги с клиента списаны, ожидается подтверждение доставки товара
            processing   - Платеж обрабатывается
            sandbox      - тестовый платеж
            subscribed   - Подписка успешно оформлена
            unsubscribed - Подписка успешно деактивирована
            reversed     - Возврат клиенту после списания
            cash_wait    - Ожидание оплаты счета
         */

        final boolean success = statusRes != null &&
                ("success".equalsIgnoreCase(statusRes)
                  || "wait_secure".equalsIgnoreCase(statusRes)
                  || "sandbox".equalsIgnoreCase(statusRes));


        if (LOG.isDebugEnabled()) {
            LOG.debug(HttpParamsUtils.stringify("LiqPay callback", callbackResult));
        }

        if (success) {
            if ("wait_secure".equalsIgnoreCase(statusRes)) {
                LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.UNSETTLED);
                return CallbackAware.CallbackResult.UNSETTLED;
            }
            LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.OK);
            return CallbackAware.CallbackResult.OK;
        }
        LOG.debug("Payment result is {}: {}", statusRes, CallbackAware.CallbackResult.FAILED);
        return CallbackAware.CallbackResult.FAILED;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preProcess(final Payment payment, final Callback callback, final String processorOperation) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postProcess(final Payment payment, final Callback callback, final String processorOperation) {

    }

    private LiqPay getLiqPayAPI() {
        return new LiqPay(getParameterValue(LP_MERCHANT_ID), getParameterValue(LP_MERCHANT_KEY), getParameterValue(LP_POST_URL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHtmlForm(final String cardHolderName, final String locale, final BigDecimal amount,
                              final String currencyCode, final String orderReference, final Payment payment) {

        final LiqPay api = getLiqPayAPI();

        final HashMap<String, String> params = new LinkedHashMap<>();
        params.put("amount", amount.setScale(2, RoundingMode.HALF_UP).toPlainString());
        params.put("server_url", getParameterValue(LP_SERVER_URL));
        params.put("description", getDescription(payment));
        params.put("pay_way", getParameterValue(LP_PAYWAY_URL));
        params.put("result_url", getParameterValue(LP_RESULT_URL));

        params.put("type", "buy");
        params.put("order_id", orderReference);

        params.put("currency", currencyCode);
        params.put("formDataOnly", "formDataOnly"); // YC specific

        final String form = api.cnb_form(params);

        LOG.debug("LiqPay form request: {}", form);

        return form;

    }

    /**
     * Get order description.
     *
     * @param payment payment
     * @return order description.
     */
    private String getDescription(final Payment payment) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (PaymentLine line : payment.getOrderItems()) {
            if (line.isShipment()) {
                stringBuilder.append(line.getSkuName().replace("\"","")).append(", ");
            } else {
                stringBuilder.append(line.getSkuCode().replace("\"",""));
                stringBuilder.append(" x ");
                stringBuilder.append(line.getQuantity());
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(payment.getBillingEmail());
        stringBuilder.append(", ");
        stringBuilder.append(payment.getOrderNumber());
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment authorizeCapture(final Payment payment, final boolean forceProcessing) {
        return payment;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Shipment not included. Will be added at capture operation.
     */
    @Override
    public Payment authorize(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment reverseAuthorization(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(REVERSE_AUTH);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment capture(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment voidCapture(final Payment paymentIn, final boolean forceProcessing) {
        final Payment payment = (Payment) SerializationUtils.clone(paymentIn);
        payment.setTransactionOperation(VOID_CAPTURE);
        payment.setTransactionReferenceId(UUID.randomUUID().toString());
        payment.setTransactionAuthorizationCode(UUID.randomUUID().toString());
        payment.setPaymentProcessorResult(Payment.PAYMENT_STATUS_MANUAL_PROCESSING_REQUIRED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment refund(final Payment payment, final boolean forceProcessing) {

        final LiqPay api = getLiqPayAPI();

        final HashMap params = new HashMap();
        params.put("order_id", payment.getTransactionAuthorizationCode()); // this is populated in prototype when we capture

        boolean success = false;
        try {
            final HashMap res = api.api("payment/refund", params);
            success = "ok".equals(res.get("result"));
        } catch (Exception exp) {
            LOG.error(Markers.alert(), "LiqPay transaction [" + payment.getOrderNumber() + "] failed, cause: " + exp.getMessage(), exp);
            LOG.error("LiqPay transaction failed, payment: " + payment, exp);
        }
        payment.setTransactionOperation(REFUND);
        payment.setPaymentProcessorResult(success ? Payment.PAYMENT_STATUS_OK : Payment.PAYMENT_STATUS_FAILED);
        payment.setPaymentProcessorBatchSettlement(false);
        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment createPaymentPrototype(final String operation,
                                          final Map map,
                                          final boolean forceProcessing) {

        final Payment payment = new PaymentImpl();
        final Map<String, String> singleParamMap = HttpParamsUtils.createSingleValueMap(map);

        final String amount = singleParamMap.get("amount");
        if (amount != null) {
            payment.setPaymentAmount(new BigDecimal(amount));
        }
        payment.setOrderCurrency(singleParamMap.get("currency"));
        payment.setTransactionReferenceId(singleParamMap.get("transaction_id"));
        payment.setTransactionAuthorizationCode(singleParamMap.get("order_id")); // this is order guid - we need it for refunds

        final boolean prepare = PaymentGateway.AUTH.equals(operation) && MapUtils.isEmpty(map);
        final CallbackAware.CallbackResult res = prepare ? CallbackResult.PREPARE : getExternalCallbackResult(singleParamMap, forceProcessing);
        payment.setPaymentProcessorResult(res.getStatus());
        payment.setPaymentProcessorBatchSettlement(res.isSettled());

        payment.setShopperIpAddress(singleParamMap.get(PaymentMiscParam.CLIENT_IP));

        return payment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaymentGatewayFeature getPaymentGatewayFeatures() {
        return PAYMENT_GATEWAY_FEATURE;
    }

}
