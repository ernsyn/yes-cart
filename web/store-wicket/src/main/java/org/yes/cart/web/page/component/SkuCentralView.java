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

package org.yes.cart.web.page.component;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.constants.AttributeNamesKeys;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.entity.*;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.shoppingcart.ShoppingCart;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.shoppingcart.ShoppingCartCommandFactory;
import org.yes.cart.web.page.component.customer.wishlist.WishListNotification;
import org.yes.cart.web.page.component.price.PriceTierView;
import org.yes.cart.web.page.component.price.PriceView;
import org.yes.cart.web.page.component.product.*;
import org.yes.cart.web.page.component.social.AddAnyButton;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.ObjectDecorator;
import org.yes.cart.web.support.entity.decorator.impl.ProductSeoableDecoratorImpl;
import org.yes.cart.web.support.entity.decorator.impl.ProductSkuSeoableDecoratorImpl;
import org.yes.cart.web.support.service.ProductServiceFacade;

import java.math.BigDecimal;
import java.util.*;

/**
 * Central view to show product sku.
 * Supports products is multisku and default sku is
 * <p/>
 * Igor Azarny iazarny@yahoo.com
 * Date: 10-Sep-2011
 * Time: 11:11:08
 */
public class SkuCentralView extends AbstractCentralView {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /**
     * Single item price panel
     */
    private final static String PRICE_VIEW = "priceView";
    /**
     * Price tiers if any
     */
    private final static String PRICE_TIERS_VIEW = "priceTiersView";
    /**
     * Add single item to cart
     */
    private final static String ADD_TO_CART_LINK = "addToCartLink";
    /**
     * Add single item to wish list
     */
    private final static String ADD_TO_WISHLIST_LINK = "addToWishListLink";
    /**
     * Quantity picker
     */
    private final static String QTY_PICKER = "quantityPicker";
    /**
     * Add single item to cart label
     */
    private final static String ADD_TO_CART_LINK_LABEL = "addToCartLinkLabel";
    /**
     * Add single item to cart label
     */
    private final static String ADD_TO_WISHLIST_LINK_LABEL = "addToWishListLinkLabel";
    /**
     * AddToAny social button
     */
    private final static String SOCIAL_ADD_TO_ANY_BUTTON = "addToAnyButton";
    /**
     * Product sku code
     */
    private final static String SKU_CODE_LABEL = "skuCode";
    /**
     * Product name
     */
    private final static String PRODUCT_NAME_LABEL = "name";
    /**
     * Product description
     */
    private final static String PRODUCT_DESCRIPTION_LABEL = "description";
    /**
     * Product image panel
     */
    private final static String PRODUCT_IMAGE_VIEW = "imageView";
    /**
     * Product sku list
     */
    private final static String SKU_LIST_VIEW = "skuList";
    /**
     * View to show sku attributes
     */
    private final static String SKU_ATTR_VIEW = "skuAttrView";
    /**
     * Product accessories or cross sell
     */
    private final static String PRODUCTS_VIEW = "productsView";

    /**
     * Product accessories head container name
     */
    private final static String ACCESSORIES_HEAD_CONTAINER = "accessoriesHeadContainer";
    /**
     * Product accessories body container name
     */
    private final static String ACCESSORIES_BODY_CONTAINER = "accessoriesBodyContainer";
    /**
     * Product accessories head container name
     */
    private final static String ACCESSORIES_HEAD = "accessoriesHead";
    /**
     * Product accessories body container name
     */
    private final static String ACCESSORIES_BODY = "accessoriesBody";

    /**
     * Product exprendables head container name
     */
    private final static String EXPENDABLES_HEAD_CONTAINER = "expendableHeadContainer";
    /**
     * Product exprendables body container name
     */
    private final static String EXPENDABLES_BODY_CONTAINER = "expendableBodyContainer";
    /**
     * Product exprendables head container name
     */
    private final static String EXPENDABLES_HEAD = "expendableHead";
    /**
     * Product exprendables body container name
     */
    private final static String EXPENDABLES_BODY = "expendableBody";

    /**
     * Product upcrosssell head container name
     */
    private final static String SELL_HEAD_CONTAINER = "upcrosssellHeadContainer";
    /**
     * Product upcrosssell body container name
     */
    private final static String SELL_BODY_CONTAINER = "upcrosssellBodyContainer";
    /**
     * Product upcrosssell head container name
     */
    private final static String SELL_HEAD = "upcrosssellHead";
    /**
     * Product upcrosssell body container name
     */
    private final static String SELL_BODY = "upcrosssellBody";

    /**
     * Product buywiththis head container name
     */
    private final static String BUY_HEAD_CONTAINER = "buywiththisHeadContainer";
    /**
     * Product buywiththis body container name
     */
    private final static String BUY_BODY_CONTAINER = "buywiththisBodyContainer";
    /**
     * Product buywiththis head container name
     */
    private final static String BUY_HEAD = "buywiththisHead";
    /**
     * Product buywiththis body container name
     */
    private final static String BUY_BODY = "buywiththisBody";

   /**
     * Product accessories head container name
     */
    private final static String VOLUME_DISCOUNT_HEAD_CONTAINER = "volumeDiscountsHeadContainer";
    /**
     * Product accessories head container name
     */
    private final static String VOLUME_DISCOUNT_HEAD = "volumeDiscountsHead";
    /**
     * Product accessories head container name
     */
    private final static String VOLUME_DISCOUNT_BODY_CONTAINER = "priceTiersView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //

    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    protected ProductServiceFacade productServiceFacade;

    @SpringBean(name = ServiceSpringKeys.CART_COMMAND_FACTORY)
    private ShoppingCartCommandFactory shoppingCartCommandFactory;


    private boolean isProduct;
    private Product product;
    private ProductSku sku;
    private String supplier;


    /**
     * Construct panel.
     *
     * @param id           panel id
     * @param categoryId   current category id.
     * @param navigationContext navigation context.
     */
    public SkuCentralView(final String id, final long categoryId, final NavigationContext navigationContext) {
        super(id, categoryId, navigationContext);
    }

    private void configureContext() {
        final ShoppingCart cart = getCurrentCart();
        long browsingShopId = cart.getShoppingContext().getCustomerShopId();
        String productId = getPage().getPageParameters().get(WebParametersKeys.PRODUCT_ID).toString();
        String skuId = getPage().getPageParameters().get(WebParametersKeys.SKU_ID).toString();
        this.supplier = getPage().getPageParameters().get(WebParametersKeys.FULFILMENT_CENTRE_ID).toString();
        if (skuId != null) {
            isProduct = false;
            try {
                final Long skuPK = Long.valueOf(skuId);
                sku = productServiceFacade.getSkuById(skuPK);
                product = productServiceFacade.getProductById(sku.getProduct().getProductId());
            } catch (Exception exp) {
                throw new RestartResponseException(Application.get().getHomePage());
            }
        } else if (productId != null) {
            isProduct = true;
            try {
                final Long prodPK = Long.valueOf(productId);
                product = productServiceFacade.getProductById(prodPK);
                final ProductAvailabilityModel pam = productServiceFacade.getProductAvailability(product, browsingShopId, supplier);
                if (StringUtils.isBlank(supplier)) {
                    this.supplier = pam.getSupplier();
                }
                sku = getDefault(product, pam, browsingShopId);
                sku = productServiceFacade.getSkuById(sku.getSkuId());
            } catch (Exception exp) {
                throw new RestartResponseException(Application.get().getHomePage());
            }
        } else {
            throw new RuntimeException("Product or Sku id expected");
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        configureContext();

        add(new TopCategories("topCategories"));

        final ShoppingCart cart = getCurrentCart();
        final Shop shop = getCurrentShop();
        final long browsingStoreId = getCurrentCustomerShopId();

        final String selectedLocale = getLocale().getLanguage();

        final ObjectDecorator decorator = getDecorator();

        String desc = decorator.getDescription(selectedLocale);
        if (!isProduct && StringUtils.isBlank(desc)) {
            desc = product.getAttributeValueByCode(AttributeNamesKeys.Product.PRODUCT_DESCRIPTION_PREFIX + selectedLocale);
            if (StringUtils.isBlank(desc)) {
                desc = product.getDescription();
            }
        }

        final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(sku, browsingStoreId, supplier);
        if (StringUtils.isBlank(supplier)) {
            this.supplier = skuPam.getSupplier();
        }

        final PriceModel model = productServiceFacade.getSkuPrice(
                getCurrentCart(),
                null,
                sku.getCode(), /* We always preselect a SKU */
                BigDecimal.ONE,
                supplier
        );

        add(getPriceView(model));

        final List<ProductSku> skus = new ArrayList<>(product.getSku());
        if (skus.size() > 1) {
            final Iterator<ProductSku> skusIt = skus.iterator();
            while (skusIt.hasNext()) {
                final ProductSku skuNext = skusIt.next();
                if (!sku.getCode().equals(skuNext.getCode())) {
                    final ProductAvailabilityModel skuNextPam = productServiceFacade.getProductAvailability(skuNext.getCode(), browsingStoreId, supplier);
                    if (skuNextPam.getAvailability() == SkuWarehouse.AVAILABILITY_NA) {
                        skusIt.remove();
                    }
                }
            }
        }

        add(new SkuListView(SKU_LIST_VIEW, skus, sku, isProduct, supplier));
        add(new Label(SKU_CODE_LABEL, getDisplaySkuCode(shop, sku)));
        add(new Label(PRODUCT_NAME_LABEL, decorator.getName(selectedLocale)));
        add(new Label(PRODUCT_DESCRIPTION_LABEL, desc).setEscapeModelStrings(false));
        add(new AddAnyButton(SOCIAL_ADD_TO_ANY_BUTTON, product, supplier));

        final boolean qtyPickVisible = !model.isPriceUponRequest() && skuPam.isAvailable() && shop.isAttributeValueByCodeTrue(AttributeNamesKeys.Shop.CART_ADD_ENABLE_QTY_PICKER);

        add(new QuantityPickerPanel(QTY_PICKER, product.getProductId(), sku.getCode(), supplier).setVisible(qtyPickVisible));

        add(
                getWicketSupportFacade().links().newAddToCartLink(ADD_TO_CART_LINK, supplier, sku.getCode(), null, getPage().getPageParameters())
                        .add(new Label(ADD_TO_CART_LINK_LABEL, skuPam.isInStock() || skuPam.isPerpetual() ?
                                getLocalizer().getString("addToCart", this) :
                                getLocalizer().getString("preorderCart", this)))
                        .setVisible(!model.isPriceUponRequest() && skuPam.isAvailable())
        );

        add(
                getWicketSupportFacade().links().newAddToWishListLink(ADD_TO_WISHLIST_LINK, supplier, sku.getCode(), null, null, null, getPage().getPageParameters())
                        .add(new Label(ADD_TO_WISHLIST_LINK_LABEL, getLocalizer().getString("addToWishlist", this)))
        );

        add(
                new SkuAttributesView(SKU_ATTR_VIEW, sku, isProduct)
        );
        add(
                new ImageView(PRODUCT_IMAGE_VIEW, decorator)
        );


        final List<ProductSearchResultDTO> productsAccessories = productServiceFacade.getProductAssociations(
                isProduct ? product.getProductId() : sku.getProduct().getProductId(),
                shop.getShopId(), browsingStoreId, Association.ACCESSORIES
        );

        final boolean accessoriesVisible = CollectionUtils.isNotEmpty(productsAccessories);
        add(new Fragment(ACCESSORIES_HEAD_CONTAINER, ACCESSORIES_HEAD, this).setVisible(accessoriesVisible));
        add(new Fragment(ACCESSORIES_BODY_CONTAINER, ACCESSORIES_BODY, this)
                .add(
                        new ProductAssociationsView(PRODUCTS_VIEW, productsAccessories)
                ).setVisible(accessoriesVisible));


        final List<ProductSearchResultDTO> productsExpendables = productServiceFacade.getProductAssociations(
                isProduct ? product.getProductId() : sku.getProduct().getProductId(),
                shop.getShopId(), browsingStoreId, Association.EXPENDABLE
        );

        final boolean expendablesVisible = CollectionUtils.isNotEmpty(productsExpendables);
        add(new Fragment(EXPENDABLES_HEAD_CONTAINER, EXPENDABLES_HEAD, this).setVisible(expendablesVisible));
        add(new Fragment(EXPENDABLES_BODY_CONTAINER, EXPENDABLES_BODY, this)
                .add(
                        new ProductAssociationsView(PRODUCTS_VIEW, productsExpendables)
                ).setVisible(expendablesVisible));


        final List<ProductSearchResultDTO> productsUp = productServiceFacade.getProductAssociations(
                isProduct ? product.getProductId() : sku.getProduct().getProductId(),
                shop.getShopId(), browsingStoreId, Association.UP_SELL
        );
        final List<ProductSearchResultDTO> productsCross = productServiceFacade.getProductAssociations(
                isProduct ? product.getProductId() : sku.getProduct().getProductId(),
                shop.getShopId(), browsingStoreId, Association.CROSS_SELL
        );

        final List<ProductSearchResultDTO> productsSell = new ArrayList<>();
        if (productsUp != null) {
            productsSell.addAll(productsUp);
        }
        if (productsCross != null) {
            productsSell.addAll(productsCross);
        }

        final boolean sellVisible = CollectionUtils.isNotEmpty(productsSell);
        add(new Fragment(SELL_HEAD_CONTAINER, SELL_HEAD, this).setVisible(sellVisible));
        add(new Fragment(SELL_BODY_CONTAINER, SELL_BODY, this)
                .add(
                        new ProductAssociationsView(PRODUCTS_VIEW, productsSell)
                ).setVisible(sellVisible));



        final List<ProductSearchResultDTO> productsBuy = productServiceFacade.getProductAssociations(
                isProduct ? product.getProductId() : sku.getProduct().getProductId(),
                shop.getShopId(), browsingStoreId, Association.BUY_WITH_THIS
        );

        final boolean buyVisible = CollectionUtils.isNotEmpty(productsBuy);
        add(new Fragment(BUY_HEAD_CONTAINER, BUY_HEAD, this).setVisible(buyVisible));
        add(new Fragment(BUY_BODY_CONTAINER, BUY_BODY, this)
                .add(
                        new ProductAssociationsView(PRODUCTS_VIEW, productsBuy)
                ).setVisible(buyVisible));

        final Collection<PriceModel> prices = getSkuPrices();
        final boolean multibuy = CollectionUtils.isNotEmpty(prices) && prices.size() > 1;
        add(new Fragment(VOLUME_DISCOUNT_HEAD_CONTAINER, VOLUME_DISCOUNT_HEAD, this).setVisible(multibuy));
        add(new PriceTierView(VOLUME_DISCOUNT_BODY_CONTAINER, prices));

        add(new WishListNotification("wishListNotification"));

        shoppingCartCommandFactory.execute(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, cart, new HashMap<String, Object>() {{
            put(ShoppingCartCommand.CMD_INTERNAL_VIEWSKU, sku.getCode());
            put(ShoppingCartCommand.CMD_P_SUPPLIER, supplier);
        }});

        super.onBeforeRender();

    }

    private String getDisplaySkuCode(final Shop shop, final ProductSku sku) {

        if (StringUtils.isNotBlank(sku.getManufacturerCode())) {
            final String displayAttrValue = shop.getAttributeValueByCode(AttributeNamesKeys.Shop.SHOP_PRODUCT_DISPLAY_MANUFACTURER_CODE);
            if (displayAttrValue != null && Boolean.valueOf(displayAttrValue)) {
                return sku.getManufacturerCode();
            }
        }
        return sku.getCode();

    }

    private ObjectDecorator getDecorator() {
        if (isProduct) {
            return getDecoratorFacade().decorate(product, getWicketUtil().getHttpServletRequest().getContextPath(), true);
        }
        return getDecoratorFacade().decorate(sku, getWicketUtil().getHttpServletRequest().getContextPath(), true);
    }

    /**
     * Get sku prices from default sku in case of product or from particular sku.
     *
     * @return collection of sku prices.
     */
    private List<PriceModel> getSkuPrices() {
        /* We always preselect a SKU */
        return productServiceFacade.getSkuPrices(
                getCurrentCart(),
                product.getProductId(),
                sku.getCode(),
                supplier
        );
    }

    /*
    * Return first available sku rather than default to improve customer experience.
    */
    private ProductSku getDefault(final Product product,
                                  final ProductAvailabilityModel productPam,
                                  final long shopId) {
        if (productPam.isAvailable()) {
            if (product.isMultiSkuProduct()) {
                for (final ProductSku sku : product.getSku()) {
                    final ProductAvailabilityModel skuPam = productServiceFacade.getProductAvailability(sku, shopId, productPam.getSupplier());
                    if (skuPam.isAvailable()) {
                        return sku;
                    }
                }
            }
        }
        // single SKU and N/A product just use default
        return product.getDefaultSku();
    }


    private PriceView getPriceView(final PriceModel model) {

        return new PriceView(PRICE_VIEW, model, null, true, true, model.isTaxInfoEnabled(), model.isTaxInfoShowAmount());
    }

    @Override
    protected Seoable getSeoObject() {
        if (isProduct) {
            return new ProductSeoableDecoratorImpl(product, getPage().getLocale().getLanguage());
        }
        return new ProductSkuSeoableDecoratorImpl(product, sku, getPage().getLocale().getLanguage());
    }


    @Override
    protected String getRelCanonical(final Seo seo, final String language) {

        if (isProduct) {

            final String uri = getBookmarkService().saveBookmarkForProduct(String.valueOf(product.getProductId()));

            return getWicketUtil().getHttpServletRequest().getContextPath() + "/"
                    + WebParametersKeys.FULFILMENT_CENTRE_ID + "/" + supplier
                    + WebParametersKeys.PRODUCT_ID + "/" + uri + "/" + ShoppingCartCommand.CMD_CHANGELOCALE + "/" + language;

        }

        final String uri = getBookmarkService().saveBookmarkForSku(String.valueOf(sku.getSkuId()));

        return getWicketUtil().getHttpServletRequest().getContextPath() + "/"
                + WebParametersKeys.FULFILMENT_CENTRE_ID + "/" + supplier
                + WebParametersKeys.SKU_ID + "/" + uri + "/" + ShoppingCartCommand.CMD_CHANGELOCALE + "/" + language;

    }


}
