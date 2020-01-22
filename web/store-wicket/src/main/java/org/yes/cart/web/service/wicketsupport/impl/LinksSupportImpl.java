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

package org.yes.cart.web.service.wicketsupport.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.yes.cart.domain.entity.CustomerWishList;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.component.customer.wishlist.WishListItemAddPage;
import org.yes.cart.web.service.wicketsupport.LinksSupport;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.utils.WicketUtil;

/**
 * User: denispavlov
 * Date: 13-06-28
 * Time: 8:43 AM
 */
public class LinksSupportImpl implements LinksSupport {

    private final WicketUtil wicketUtil;

    public LinksSupportImpl(final WicketUtil wicketUtil) {
        this.wicketUtil = wicketUtil;
    }

    /** {@inheritDoc} */
    @Override
    public PageParameters getFilteredCurrentParameters(final PageParameters pageParameters) {
        return wicketUtil.getFilteredRequestParameters(pageParameters);
    }

    /** {@inheritDoc} */
    @Override
    public Link newLink(final String linkId,
                        final PageParameters pageParameters) {

        return new BookmarkablePageLink(linkId, getHomePage(), pageParameters);
    }

    /** {@inheritDoc} */
    @Override
    public Link newCategoryLink(final String linkId,
                                final Object categoryRef) {

        return newBookmarkableLink(linkId, WebParametersKeys.CATEGORY_ID, categoryRef);
    }

    /** {@inheritDoc} */
    @Override
    public Link newCategoryLink(final String linkId,
                                final long categoryRef,
                                final PageParameters pageParameters) {

        return newBookmarkableLink(linkId, WebParametersKeys.CATEGORY_ID, categoryRef,
                getFilteredCurrentParameters(pageParameters));
    }

    /** {@inheritDoc} */
    @Override
    public Link newContentLink(final String linkId,
                               final Object contentRef) {

        return newBookmarkableLink(linkId, WebParametersKeys.CONTENT_ID, contentRef);
    }

    /** {@inheritDoc} */
    @Override
    public Link newContentLink(final String linkId,
                               final Object contentRef,
                               final PageParameters pageParameters) {

        return newBookmarkableLink(linkId, WebParametersKeys.CONTENT_ID, contentRef,
                getFilteredCurrentParameters(pageParameters));
    }

    /** {@inheritDoc} */
    @Override
    public Link newProductLink(final String linkId,
                               final String supplier,
                               final Object productRef) {

        return newBookmarkableLink(linkId, supplier, WebParametersKeys.PRODUCT_ID, productRef);
    }

    /** {@inheritDoc} */
    @Override
    public Link newProductLink(final String linkId,
                               final String supplier,
                               final Object productRef,
                               final PageParameters pageParameters) {

        return newBookmarkableLink(linkId, supplier, WebParametersKeys.PRODUCT_ID, productRef,
                getFilteredCurrentParameters(pageParameters));
    }

    /** {@inheritDoc} */
    @Override
    public Link newProductSkuLink(final String linkId,
                                  final String supplier,
                                  final Object productRef) {

        return newBookmarkableLink(linkId, supplier, WebParametersKeys.SKU_ID, productRef);
    }

    /** {@inheritDoc} */
    @Override
    public Link newProductSkuLink(final String linkId,
                                  final String supplier,
                                  final Object productRef,
                                  final PageParameters pageParameters) {

        return newBookmarkableLink(linkId, supplier, WebParametersKeys.SKU_ID, productRef,
                getFilteredCurrentParameters(pageParameters));
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newAddToCartLink(final String linkId,
                                 final String supplier,
                                 final String skuCode,
                                 final String quantity,
                                 final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_ADDTOCART, skuCode);
        params.set(ShoppingCartCommand.CMD_P_SUPPLIER, supplier);
        if (quantity != null) { // null quantity will pick min from product
            params.set(ShoppingCartCommand.CMD_P_QTY, quantity);
        }
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newAddToCartLink(final String linkId,
                                 final String supplier,
                                 final String skuCode,
                                 final String quantity,
                                 final String wishlistId,
                                 final Class<Page> target,
                                 final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_ADDTOCART, skuCode);
        params.set(ShoppingCartCommand.CMD_P_SUPPLIER, supplier);
        params.set(ShoppingCartCommand.CMD_REMOVEFROMWISHLIST, skuCode);
        params.set(ShoppingCartCommand.CMD_REMOVEFROMWISHLIST_P_ID, wishlistId);
        if (quantity != null) { // null quantity will pick min from product
            params.set(ShoppingCartCommand.CMD_P_QTY, quantity);
        }
        return new BookmarkablePageLink(linkId, target, params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newAddToWishListLink(final String linkId,
                                     final String supplier,
                                     final String skuCode,
                                     final String quantity,
                                     final String wishList,
                                     final String tags,
                                     final PageParameters pageParameters) {

        return newAddToWishListLink(linkId, supplier, skuCode, quantity, wishList, tags, null, pageParameters);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newAddToWishListLink(final String linkId,
                                     final String supplier,
                                     final String skuCode,
                                     final String quantity,
                                     final String wishList,
                                     final String tags,
                                     final String visibility,
                                     final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_ADDTOWISHLIST, skuCode);
        params.set(ShoppingCartCommand.CMD_P_SUPPLIER, supplier);
        if (quantity != null) { // null quantity will pick min from product
            params.set(ShoppingCartCommand.CMD_P_QTY, quantity);
        }
        params.set(ShoppingCartCommand.CMD_ADDTOWISHLIST_P_TYPE, wishList != null ? wishList : CustomerWishList.SIMPLE_WISH_ITEM);
        if (visibility != null) {
            params.set(ShoppingCartCommand.CMD_ADDTOWISHLIST_P_VISIBILITY, visibility);
        } else {
            params.remove(ShoppingCartCommand.CMD_ADDTOWISHLIST_P_VISIBILITY);
        }
        if (tags != null) {
            params.set(ShoppingCartCommand.CMD_ADDTOWISHLIST_P_TAGS, tags);
        } else {
            params.remove(ShoppingCartCommand.CMD_ADDTOWISHLIST_P_TAGS);
        }
        return new BookmarkablePageLink(linkId, WishListItemAddPage.class, params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newRemoveFromWishListLink(final String linkId,
                                          final String supplier,
                                          final String skuCode,
                                          final Long itemId,
                                          final Class<Page> target,
                                          final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_REMOVEFROMWISHLIST, skuCode);
        params.set(ShoppingCartCommand.CMD_P_SUPPLIER, supplier);
        params.set(ShoppingCartCommand.CMD_REMOVEFROMWISHLIST_P_ID, itemId);
        return new BookmarkablePageLink(linkId, target, params);
    }

    /** {@inheritDoc} */
    @Override
    public Link newAddCouponLink(final String linkId,
                                 final String coupon,
                                 final Class<Page> target,
                                 final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_ADDCOUPON, coupon);
        return new BookmarkablePageLink(linkId, target, params);
    }

    /** {@inheritDoc} */
    @Override
    public Link newRemoveCouponLink(final String linkId,
                                    final String coupon,
                                    final Class<Page> target,
                                    final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_REMOVECOUPON, coupon);
        return new BookmarkablePageLink(linkId, target, params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newLogOffLink(final String linkId,
                              final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_LOGOUT, ShoppingCartCommand.CMD_LOGOUT);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newChangeLocaleLink(final String linkId,
                                    final String language,
                                    final Class<? extends Page> target,
                                    final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_CHANGELOCALE, language);
        return new BookmarkablePageLink(
                linkId,
                target == null ? getHomePage() : target,
                params);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public Link newChangeCurrencyLink(final String linkId,
                                      final String currency,
                                      final Class<? extends Page> target,
                                      final PageParameters pageParameters) {

        final PageParameters params = getFilteredCurrentParameters(pageParameters);
        params.set(ShoppingCartCommand.CMD_CHANGECURRENCY, currency);
        return new BookmarkablePageLink(
                linkId,
                target == null ? getHomePage() : target,
                params);
    }

    @SuppressWarnings("unchecked")
    private Class<Page> getHomePage() {
        return (Class<Page>) Application.get().getHomePage();
    }


    /*
     * Generate bookmarkable link for current shop home page. This covers all pa
     */
    @SuppressWarnings("unchecked")
    private Link newBookmarkableLink(final String linkId,
                                     final String uriContext,
                                     final Object uri) {

        return newBookmarkableLink(linkId, null, uriContext, uri);
    }

    /*
     * Generate bookmarkable link for current shop home page. This covers all pa
     */
    @SuppressWarnings("unchecked")
    private Link newBookmarkableLink(final String linkId,
                                     final String supplier,
                                     final String uriContext,
                                     final Object uri) {

        final PageParameters params = new PageParameters();
        if (StringUtils.isNotBlank(supplier)) {
            params.add(WebParametersKeys.FULFILMENT_CENTRE_ID, supplier);
        }
        params.add(uriContext, uri);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

    /*
     * Generate bookmarkable link for current shop home page. This covers all pa
     */
    @SuppressWarnings("unchecked")
    private Link newBookmarkableLink(final String linkId,
                                     final String uriContext,
                                     final Object uri,
                                     final PageParameters carried) {

        return newBookmarkableLink(linkId, null, uriContext, uri, carried);
    }

    /*
     * Generate bookmarkable link for current shop home page. This covers all pa
     */
    @SuppressWarnings("unchecked")
    private Link newBookmarkableLink(final String linkId,
                                     final String supplier,
                                     final String uriContext,
                                     final Object uri,
                                     final PageParameters carried) {
        
        final PageParameters params = new PageParameters(carried);
        if (StringUtils.isNotBlank(supplier)) {
            params.set(WebParametersKeys.FULFILMENT_CENTRE_ID, supplier);
        }
        params.set(uriContext, uri);
        return new BookmarkablePageLink(linkId, getHomePage(), params);
    }

}
