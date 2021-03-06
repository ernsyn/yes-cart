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

package org.yes.cart.promotion.impl.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.promotion.PromotionAction;
import org.yes.cart.shoppingcart.Total;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-30
 * Time: 8:15 AM
 */
public class OrderAmountOffPromotionAction extends AbstractOrderPromotionAction implements PromotionAction {

    private static final Logger LOG = LoggerFactory.getLogger(OrderAmountOffPromotionAction.class);

    /** {@inheritDoc} */
    @Override
    public BigDecimal testDiscountValue(final Map<String, Object> context) {
        final Total itemTotal = getItemTotal(context);
        return getDiscountValue(getRawPromotionActionContext(context), itemTotal.getPriceSubTotal());
    }

    private BigDecimal getDiscountValue(final String ctx, final BigDecimal salePrice) {
        try {
            final BigDecimal amountOff = getAmountValue(ctx);
            if (amountOff.compareTo(BigDecimal.ZERO) > 0) {
                return amountOff.divide(salePrice, RoundingMode.HALF_UP);
            }
        } catch (Exception exp) {
            LOG.error("Unable to parse discount for promotion action context: {}", ctx);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal getAmountValue(final String ctx) {
        try {
            return new BigDecimal(ctx).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception exp) {
            return MoneyUtils.ZERO;
        }
    }


    /** {@inheritDoc} */
    @Override
    public void perform(final Map<String, Object> context) {
        final BigDecimal amountOff = getAmountValue(getRawPromotionActionContext(context));
        if (MoneyUtils.isPositive(amountOff)) {

            subtractPromotionValue(context, amountOff);

        }
    }

}
