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
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.shoppingcart.MutableShoppingCart;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-10-30
 * Time: 8:15 AM
 */
public class ItemAmountOffPromotionAction extends AbstractItemPromotionAction implements PromotionAction {

    private static final Logger LOG = LoggerFactory.getLogger(ItemAmountOffPromotionAction.class);

    /** {@inheritDoc} */
    @Override
    public BigDecimal testDiscountValue(final Map<String, Object> context) {
        final CartItem cartItem = getShoppingCartItem(context);
        return getDiscountValue(getRawPromotionActionContext(context), cartItem.getSalePrice());
    }

    private BigDecimal getDiscountValue(final String ctx, final BigDecimal salePrice) {
        if (MoneyUtils.isPositive(salePrice))
            try {
                final BigDecimal amountOff = getAmountValue(ctx);
                if (amountOff.compareTo(BigDecimal.ZERO) > 0) {
                    return amountOff.divide(salePrice, RoundingMode.HALF_UP);
                }
            } catch (Exception exp) {
                LOG.error(
                        "Unable top parse amountOff for promotion action context: {}", ctx);
            }
        return MoneyUtils.ZERO;
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
            final MutableShoppingCart cart = getShoppingCart(context);
            final CartItem cartItem = getShoppingCartItem(context);

            // we may have compound discounts so need to use final price
            final BigDecimal promoPrice;
            if (MoneyUtils.isFirstBiggerThanSecond(amountOff, cartItem.getPrice())) {
                promoPrice = MoneyUtils.ZERO;
            } else {
                promoPrice = cartItem.getPrice().subtract(amountOff);
            }

            cart.setProductSkuPromotion(cartItem.getSupplierCode(), cartItem.getProductSkuCode(), promoPrice, getPromotionCode(context));

        }
    }

}
