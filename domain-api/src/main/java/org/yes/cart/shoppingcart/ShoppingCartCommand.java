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

package org.yes.cart.shoppingcart;


import java.io.Serializable;
import java.util.Map;

/**
 * .
 * <p/>
 * User: dogma
 * Date: Jan 22, 2011
 * Time: 5:07:03 PM
 */
public interface ShoppingCartCommand extends Serializable {

    String CMD_P_QTY = "qty";
    String CMD_P_SUPPLIER = "supplier";

    String CMD_ADDTOWISHLIST = "addToWishListCmd";
    String CMD_ADDTOWISHLIST_P_TYPE = "type";
    String CMD_ADDTOWISHLIST_P_TAGS = "tags";
    String CMD_ADDTOWISHLIST_P_TAGS_REPLACE = "tagsr";
    String CMD_ADDTOWISHLIST_P_VISIBILITY = "wlv";
    String CMD_ADDTOWISHLIST_P_NOTIFICATION = "wln";
    String CMD_REMOVEFROMWISHLIST = "removeFromWishListCmd";
    String CMD_REMOVEFROMWISHLIST_P_ID = "i";

    String CMD_ADDCOUPON = "addCouponCmd";
    String CMD_REMOVECOUPON = "removeCouponCmd";

    String CMD_ADDTOCART = "addToCartCmd";
    String CMD_REMOVEALLSKU = "removeAllSkuCmd";
    String CMD_REMOVEONESKU = "removeOneSkuCmd";
    String CMD_SETQTYSKU = "setQuantityToCartCmd";

    String CMD_RECALCULATEPRICE = "recalculatePriceCmd";

    String CMD_SETPRICE = "setPriceCmd";
    String CMD_SETPRICE_P_PRICE = "cip";
    String CMD_SETPRICE_P_AUTH = "aup";

    String CMD_SEPARATEBILLING = "setBillingAddressSeparateCmd";

    String CMD_SETADDRESES = "setAddressesCmd";
    String CMD_SETADDRESES_P_DELIVERY_ADDRESS = "d";
    String CMD_SETADDRESES_P_BILLING_ADDRESS = "b";

    String CMD_SPLITCARTITEMS = "splitCartItemsCmd";

    String CMD_SETCARRIERSLA = "setCarrierSlaCmd";
    String CMD_SETCARRIERSLA_P_DELIVERY_ADDRESS = "d";
    String CMD_SETCARRIERSLA_P_BILLING_ADDRESS = "b";
    String CMD_SETCARRIERSLA_P_DELIVERY_NOT_REQUIRED = "dr";
    String CMD_SETCARRIERSLA_P_BILLING_NOT_REQUIRED = "br";

    String CMD_MULTIPLEDELIVERY = "setMultipleDeliveryCmd";

    String CMD_SETPGLABEL = "setPgLabelCmd";
    String CMD_SETSHOP = "setShopIdCmd";

    String CMD_RESTORE = "restoreCmd";

    String CMD_SETTAXOPTIONS = "setTaxOptionsCmd";
    String CMD_SETTAXOPTIONS_P_NET = "txn";
    String CMD_SETTAXOPTIONS_P_AMOUNT = "txa";

    String CMD_SETORDERMSG = "setOrderMsgCmd";

    String CMD_SETORDERDETAILS = "setOrderDetailsCmd";

    String CMD_CHANGECURRENCY = "changeCurrencyCmd";
    String CMD_CHANGELOCALE = "changeLocaleCmd";

    String CMD_INTERNAL_VIEWSKU = "viewSkuInternalCmd";
    String CMD_INTERNAL_SETIP = "setIpInternalCmd";

    String CMD_CLEAN = "cleanCartCmd";
    String CMD_EXPIRE = "expireCartCmd";
    String CMD_LOGIN = "loginCmd";
    String CMD_LOGIN_ON_BEHALF = "loginOnBehalfCmd";
    String CMD_LOGIN_P_EMAIL = "email";
    String CMD_LOGIN_P_PASS = "password";
    String CMD_LOGOUT = "logoutCmd";
    String CMD_LOGOUT_ON_BEHALF = "logoutOnBehalfCmd";

    String CMD_RESET_PASSWORD = "resetPasswordCmd";
    String CMD_RESET_PASSWORD_PW = "resetPasswordCmd_pw";

    String CMD_DELETE_ACCOUNT = "deleteAccountCmd";
    String CMD_DELETE_ACCOUNT_PW = "deleteAccountCmd_pw";

    /**
     * Execute command on shopping cart to perform changes.
     *
     * @param shoppingCart the shopping cart
     * @param parameters parameters
     */
    void execute(ShoppingCart shoppingCart, Map<String, Object> parameters);

    /**
     * @return command key
     */
    String getCmdKey();

    /**
     * Priority for determining the order in which bulk commands are executed.
     *
     * @return 0 - first priority to max int - last priority
     */
    int getPriority();

}
