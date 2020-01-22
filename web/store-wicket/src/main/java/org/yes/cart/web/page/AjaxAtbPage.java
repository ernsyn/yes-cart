package org.yes.cart.web.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.value.ValueMap;
import org.yes.cart.domain.entity.QuantityModel;
import org.yes.cart.domain.entity.ProductSku;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.component.cart.SmallShoppingCartView;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ProductServiceFacade;
import org.yes.cart.web.utils.WicketUtil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 13-08-31
 * Time: 7:16 PM
 */
public class AjaxAtbPage extends AbstractWebPage {

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    protected ProductServiceFacade productServiceFacade;

    public AjaxAtbPage(final PageParameters params) {
        super(params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        executeHttpPostedCommands();

        final StringValue skuValue = getPageParameters().get(ShoppingCartCommand.CMD_ADDTOCART);
        final String sku = skuValue.toString();
        final StringValue supplierValue = getPageParameters().get(ShoppingCartCommand.CMD_P_SUPPLIER);
        final String supplier = supplierValue.toString();

        addOrReplace(new SmallShoppingCartView("smallCart"));
        addOrReplace(new Label("productAddedMsg",
                WicketUtil.createStringResourceModel(this, "itemAdded",
                        Collections.singletonMap("sku", skuValue))));

        final ProductSku productSku = productServiceFacade.getProductSkuBySkuCode(sku);
        final ShoppingCart cart = getCurrentCart();
        final BigDecimal cartQty = cart.getProductSkuQuantity(supplier, sku);
        final QuantityModel pqm = productServiceFacade.getProductQuantity(
                cartQty,
                productSku.getProduct(),
                cart.getShoppingContext().getCustomerShopId(),
                supplier
        );

        final String message;
        if (!pqm.isCanOrderMore()) {

            final Map<String, Object> params = new HashMap<>();
            params.put("cart", pqm.getCartQty().toPlainString());

            message = getLocalizer().getString("quantityPickerFullTooltip", this,
                    new Model<Serializable>(new ValueMap(params)));

        } else if (pqm.isHasMax()) {

            final Map<String, Object> params = new HashMap<>();
            params.put("min", pqm.getMin().toPlainString());
            params.put("step", pqm.getStep().toPlainString());
            params.put("max", pqm.getMax().toPlainString());
            params.put("cart", pqm.getCartQty().toPlainString());

            message = getLocalizer().getString("quantityPickerTooltip", this,
                    new Model<Serializable>(new ValueMap(params)));

        } else {

            final Map<String, Object> params = new HashMap<>();
            params.put("min", pqm.getMin().toPlainString());
            params.put("step", pqm.getStep().toPlainString());
            params.put("cart", pqm.getCartQty().toPlainString());

            message = getLocalizer().getString("quantityPickerTooltipNoMax", this,
                    new Model<Serializable>(new ValueMap(params)));
        }

        final StringBuilder outJson = new StringBuilder();
        outJson.append("{ \"SKU\": \"").append(sku.replace("\"", "")).append("\"")
                .append(", \"min\": ").append(pqm.getMinOrder().toPlainString())
                .append(", \"max\": ").append(pqm.getMaxOrder().toPlainString())
                .append(", \"step\": ").append(pqm.getStep().toPlainString())
                .append(", \"value\": ").append(pqm.getMinOrder().toPlainString())
                .append(", \"title\": \"").append(message.replace("\"", "")).append("\" }");

        addOrReplace(new Label("productAddedObj", outJson.toString()).setEscapeModelStrings(false));
        super.onBeforeRender();

        persistCartIfNecessary();

    }

}
