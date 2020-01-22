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

package org.yes.cart.shoppingcart.impl;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.yes.cart.shoppingcart.AmountCalculationStrategy;
import org.yes.cart.shoppingcart.CartItem;
import org.yes.cart.utils.MoneyUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: dogma
 * Date: Jan 16, 2011
 * Time: 1:19:22 AM
 */
public class ShoppingCartImplTest {

    private Mockery mockery = new JUnit4Mockery();

    private ShoppingCartImpl cart = new ShoppingCartImpl();

    @Test
    public void testIndexOfSkuInexistent() {
        assertEquals("Size should be 0", 0, cart.getCartItemsCount());
        assertEquals("Index must be -1 for inexistent sku", -1, cart.indexOfProductSku("s01","sku"));
    }

    @Test
    public void testAddProductSkuDTOToCartInexistent() {
        boolean newItem = cart.addProductSkuToCart("s01","sku", "SKU name", BigDecimal.TEN);
        assertTrue("Must create new item", newItem);
        assertEquals("Size should be 10", 10, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku", 0, cart.indexOfProductSku("s01", "sku"));
        assertEquals("Items must have 1 element", 1, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuDTOToCartExistent() {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN);
        boolean newItem2 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN);
        assertTrue("Must create new item", newItem1);
        assertFalse("Must not create new item", newItem2);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku", 0, cart.indexOfProductSku("s01", "sku"));
        assertEquals("Items must have 1 element", 1, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuDTOToCartExistentDifferentSupplier() {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku", "SKU name", BigDecimal.TEN);
        boolean newItem2 = cart.addProductSkuToCart("s02", "sku", "SKU name", BigDecimal.TEN);
        assertTrue("Must create new item", newItem1);
        assertTrue("Must create new item", newItem2);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku", 0, cart.indexOfProductSku("s01", "sku"));
        assertEquals("Index must be 1 for sku", 1, cart.indexOfProductSku("s02", "sku"));
        assertEquals("Items must have 2 elements", 2, cart.getCartItemList().size());
        assertEquals("1st element must be sku", "sku", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("2nd element must be sku", "sku", cart.getCartItemList().get(1).getProductSkuCode());
    }

    @Test
    public void testAddProductSkuToCartExistentAndInexistent() {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        boolean newItem2 = cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        boolean newItem3 = cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        assertTrue("Must create new item", newItem1);
        assertFalse("Must not create new item", newItem2);
        assertTrue("Must create new item", newItem3);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku01", 0, cart.indexOfProductSku("s01", "sku01"));
        assertEquals("Index must be 1 for sku02", 1, cart.indexOfProductSku("s01", "sku02"));
        assertEquals("Items must have 2 elements", 2, cart.getCartItemList().size());
        assertEquals("1st element must be sku01", "sku01", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("1st element must be sku02", "sku02", cart.getCartItemList().get(1).getProductSkuCode());
    }

    @Test
    public void testAddGiftToCart() throws Exception {
        boolean newItem1 = cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        boolean newItem2 = cart.addGiftToCart("s01", "sku01", "SKU name", BigDecimal.ONE, "TEST01");
        boolean newItem3 = cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean newItem4 = cart.addGiftToCart("s01", "sku01", "SKU name", BigDecimal.ONE, "TEST02");
        assertTrue("Must create new item", newItem1);
        assertTrue("Must not create new item", newItem2);
        assertTrue("Must create new item", newItem3);
        assertFalse("Must not create new item", newItem4);
        assertEquals("Size should be 30", 22, cart.getCartItemsCount());
        assertEquals("Index must be 0 for sku01", 0, cart.indexOfProductSku("s01", "sku01"));
        assertEquals("Index must be 0 for sku01", 0, cart.indexOfGift("s01", "sku01")); // gift index is separate from product
        assertEquals("Index must be 1 for sku02", 1, cart.indexOfProductSku("s01", "sku02"));
        assertEquals("Items must have 2 elements", 3, cart.getCartItemList().size());
        assertEquals("1st element must be sku01", "sku01", cart.getCartItemList().get(0).getProductSkuCode());
        assertEquals("2nd element must be sku02", "sku02", cart.getCartItemList().get(1).getProductSkuCode());
        assertEquals("3rd element must be sku01", "sku01", cart.getCartItemList().get(2).getProductSkuCode()); // index = count(prod) + giftIndex
        assertTrue("3rd element must be gift", cart.getCartItemList().get(2).isGift());
        assertEquals("3rd element must have promos", "TEST01,TEST02", cart.getCartItemList().get(2).getAppliedPromo());
    }

    @Test
    public void testRemoveCartItemInexistent() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean removed = cart.removeCartItem("s01", "sku03");
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
    }

    @Test
    public void testRemoveCartItemExistent() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean removed = cart.removeCartItem("s01", "sku02");
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOfProductSku("s01", "sku02"));
    }

    @Test
    public void testRemoveCartItemExistentWrongSupplier() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean removed = cart.removeCartItem("s02", "sku02");
        assertFalse("Must be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertEquals("Index of sku should be 1", 1, cart.indexOfProductSku("s01", "sku02"));
    }

    @Test
    public void testRemoveCartItemQuantityInexistent() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("s01", "sku03", BigDecimal.TEN);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
    }

    @Test
    public void testRemoveCartItemQuantityExistent() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("s01", "sku02", BigDecimal.ONE);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 29", 29, cart.getCartItemsCount());
        assertEquals("Index of removed should be 1", 1, cart.indexOfProductSku("s01", "sku02"));
        assertTrue("Quantity should change to 9", MoneyUtils.isFirstEqualToSecond(new BigDecimal(9), cart.getCartItemList().get(1).getQty()));
    }

    @Test
    public void testRemoveCartItemQuantityExistentWrongSupplier() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("s02", "sku02", BigDecimal.ONE);
        assertFalse("Must not be removed", removed);
        assertEquals("Size should be 30", 30, cart.getCartItemsCount());
        assertEquals("Index of removed should be 1", 1, cart.indexOfProductSku("s01", "sku02"));
    }

    @Test
    public void testRemoveCartItemQuantityExistentFull() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("s01", "sku02", BigDecimal.TEN);
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOfProductSku("s01", "sku02"));
    }

    @Test
    public void testRemoveCartItemQuantityExistentMoreThanInCart() {
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku01", "SKU name", BigDecimal.TEN);
        cart.addProductSkuToCart("s01", "sku02", "SKU name", BigDecimal.TEN);
        boolean removed = cart.removeCartItemQuantity("s01", "sku02", new BigDecimal(100));
        assertTrue("Must be removed", removed);
        assertEquals("Size should be 20", 20, cart.getCartItemsCount());
        assertEquals("Index of removed should be -1", -1, cart.indexOfProductSku("s01", "sku02"));
    }

    @Test
    public void testAddCoupons() throws Exception {

        cart.addCoupon("ABC");

        List<String> coupons, applied;

        coupons = cart.getCoupons();
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        assertEquals("ABC", coupons.get(0));

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(0, applied.size());

        cart.addCoupon("CDE");

        coupons = cart.getCoupons();
        assertNotNull(coupons);
        assertEquals(2, coupons.size());
        assertEquals("ABC", coupons.get(0));
        assertEquals("CDE", coupons.get(1));

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(0, applied.size());

        cart.addGiftToCart("s01", "gift-001", "SKU name", BigDecimal.ONE, "PROMO-001:ABC");

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(1, applied.size());
        assertEquals("ABC", applied.get(0));

        cart.addGiftToCart("s01", "gift-002", "SKU name", BigDecimal.ONE, "PROMO-001:CDE");

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(2, applied.size());
        assertEquals("ABC", applied.get(0));
        assertEquals("CDE", applied.get(1));

        cart.removeItemPromotions();

        coupons = cart.getCoupons();
        assertNotNull(coupons);
        assertEquals(2, coupons.size());
        assertEquals("ABC", coupons.get(0));
        assertEquals("CDE", coupons.get(1));

        applied = cart.getAppliedCoupons();
        assertNotNull(applied);
        assertEquals(0, applied.size());

    }

    @Test
    public void testModificaion() throws Exception {

        final AmountCalculationStrategy strategy = mockery.mock(AmountCalculationStrategy.class);

        cart.initialise(strategy);
        assertFalse(cart.isModified());
        cart.markDirty();
        assertTrue(cart.isModified());

    }

    /**
     * Test shows that only promotions are removed.
     */
    @Test
    public void testRemoveItemPromotions() throws Exception {

        cart.addProductSkuToCart("s01", "ABC001", "SKU name", BigDecimal.ONE);
        cart.setProductSkuPrice("s01", "ABC001", new BigDecimal("9.99"), new BigDecimal("10.99"));
        cart.setProductSkuPromotion("s01", "ABC001", new BigDecimal("5.99"), "-50");

        cart.addGiftToCart("s01", "G001", "SKU name", BigDecimal.ONE, "G001");
        cart.setGiftPrice("s01", "G001", new BigDecimal("4.99"), new BigDecimal("4.99"));

        cart.addProductSkuToCart("s01", "ABC002", "SKU name", BigDecimal.ONE);
        cart.setProductSkuPrice("s01", "ABC002", new BigDecimal("9.99"), new BigDecimal("10.99"));
        cart.setProductSkuOffer("s01", "ABC002", new BigDecimal("5.99"), "AUTH001");

        assertEquals(3, cart.getCartItemList().size());

        final CartItem abc001 = cart.getCartItemList().get(0);
        assertNotNull(abc001);
        assertFalse(abc001.isGift());
        assertTrue(abc001.isPromoApplied());
        assertFalse(abc001.isFixedPrice());
        assertEquals("-50", abc001.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc001.getPrice());

        final CartItem abc002 = cart.getCartItemList().get(1);
        assertNotNull(abc002);
        assertFalse(abc002.isGift());
        assertFalse(abc002.isPromoApplied());
        assertTrue(abc002.isFixedPrice());
        assertEquals("AUTH001", abc002.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc002.getPrice());

        final CartItem gift = cart.getCartItemList().get(2);
        assertNotNull(gift);
        assertTrue(gift.isGift());
        assertTrue(gift.isPromoApplied());
        assertFalse(gift.isFixedPrice());
        assertEquals("G001", gift.getAppliedPromo());
        assertEquals(new BigDecimal("0.00"), gift.getPrice());

        cart.removeItemPromotions();

        assertEquals(2, cart.getCartItemList().size());

        final CartItem abc001_2 = cart.getCartItemList().get(0);
        assertNotNull(abc001_2);
        assertFalse(abc001_2.isGift());
        assertFalse(abc001_2.isPromoApplied());
        assertFalse(abc001_2.isFixedPrice());
        assertNull(abc001_2.getAppliedPromo());
        assertEquals(new BigDecimal("9.99"), abc001_2.getPrice());

        final CartItem abc002_2 = cart.getCartItemList().get(1);
        assertNotNull(abc002_2);
        assertFalse(abc002_2.isGift());
        assertFalse(abc002_2.isPromoApplied());
        assertTrue(abc002_2.isFixedPrice());
        assertEquals("AUTH001", abc002_2.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc002_2.getPrice());


    }


    /**
     * Test shows that only offers are removed.
     */
    @Test
    public void testRemoveItemOffers() throws Exception {

        cart.addProductSkuToCart("s01", "ABC001", "SKU name", BigDecimal.ONE);
        cart.setProductSkuPrice("s01", "ABC001", new BigDecimal("9.99"), new BigDecimal("10.99"));
        cart.setProductSkuPromotion("s01", "ABC001", new BigDecimal("5.99"), "-50");

        cart.addGiftToCart("s01", "G001", "SKU name", BigDecimal.ONE, "G001");
        cart.setGiftPrice("s01", "G001", new BigDecimal("4.99"), new BigDecimal("4.99"));

        cart.addProductSkuToCart("s01", "ABC002", "SKU name", BigDecimal.ONE);
        cart.setProductSkuPrice("s01", "ABC002", new BigDecimal("9.99"), new BigDecimal("10.99"));
        cart.setProductSkuOffer("s01", "ABC002", new BigDecimal("5.99"), "AUTH001");

        assertEquals(3, cart.getCartItemList().size());

        final CartItem abc001 = cart.getCartItemList().get(0);
        assertNotNull(abc001);
        assertFalse(abc001.isGift());
        assertTrue(abc001.isPromoApplied());
        assertFalse(abc001.isFixedPrice());
        assertEquals("-50", abc001.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc001.getPrice());

        final CartItem abc002 = cart.getCartItemList().get(1);
        assertNotNull(abc002);
        assertFalse(abc002.isGift());
        assertFalse(abc002.isPromoApplied());
        assertTrue(abc002.isFixedPrice());
        assertEquals("AUTH001", abc002.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc002.getPrice());

        final CartItem gift = cart.getCartItemList().get(2);
        assertNotNull(gift);
        assertTrue(gift.isGift());
        assertTrue(gift.isPromoApplied());
        assertFalse(gift.isFixedPrice());
        assertEquals("G001", gift.getAppliedPromo());
        assertEquals(new BigDecimal("0.00"), gift.getPrice());

        cart.removeItemOffers();

        assertEquals(3, cart.getCartItemList().size());

        final CartItem abc001_2 = cart.getCartItemList().get(0);
        assertNotNull(abc001_2);
        assertFalse(abc001_2.isGift());
        assertTrue(abc001_2.isPromoApplied());
        assertFalse(abc001_2.isFixedPrice());
        assertEquals("-50", abc001_2.getAppliedPromo());
        assertEquals(new BigDecimal("5.99"), abc001_2.getPrice());

        final CartItem abc002_2 = cart.getCartItemList().get(1);
        assertNotNull(abc002_2);
        assertFalse(abc002_2.isGift());
        assertFalse(abc002_2.isPromoApplied());
        assertFalse(abc002_2.isFixedPrice());
        assertNull("AUTH001", abc002_2.getAppliedPromo());
        assertEquals(new BigDecimal("9.99"), abc002_2.getPrice());

        final CartItem gift_2 = cart.getCartItemList().get(2);
        assertNotNull(gift_2);
        assertTrue(gift_2.isGift());
        assertTrue(gift_2.isPromoApplied());
        assertFalse(gift_2.isFixedPrice());
        assertEquals("G001", gift_2.getAppliedPromo());
        assertEquals(new BigDecimal("0.00"), gift_2.getPrice());


    }


    @Test
    public void testSetProductSkuPrice() throws Exception {

        cart.addProductSkuToCart("s01", "ABC", "SKU name", BigDecimal.ONE);

        final CartItem noPrice = cart.getCartItemList().get(0);

        assertEquals(BigDecimal.ZERO, noPrice.getListPrice());
        assertEquals(BigDecimal.ZERO, noPrice.getSalePrice());
        assertEquals(BigDecimal.ZERO, noPrice.getPrice());
        assertFalse(noPrice.isPromoApplied());
        assertFalse(noPrice.isFixedPrice());
        assertNull(noPrice.getAppliedPromo());

        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("9.99"), new BigDecimal("10.99"));

        final CartItem hasPrice = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), hasPrice.getListPrice());
        assertEquals(new BigDecimal("9.99"), hasPrice.getSalePrice());
        assertEquals(new BigDecimal("9.99"), hasPrice.getPrice());
        assertFalse(hasPrice.isPromoApplied());
        assertFalse(hasPrice.isFixedPrice());
        assertNull(hasPrice.getAppliedPromo());

        // Do promotion
        cart.setProductSkuPromotion("s01", "ABC", new BigDecimal("4.99"), "50OFF");

        final CartItem promoPrice = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), promoPrice.getListPrice());
        assertEquals(new BigDecimal("9.99"), promoPrice.getSalePrice());
        assertEquals(new BigDecimal("4.99"), promoPrice.getPrice());
        assertTrue(promoPrice.isPromoApplied());
        assertFalse(promoPrice.isFixedPrice());
        assertEquals("50OFF", promoPrice.getAppliedPromo());

        // Perform set price
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("9.99"), new BigDecimal("10.99"));

        final CartItem promoPriceAfterReset = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), promoPriceAfterReset.getListPrice());
        assertEquals(new BigDecimal("9.99"), promoPriceAfterReset.getSalePrice());
        assertEquals(new BigDecimal("9.99"), promoPriceAfterReset.getPrice());
        assertFalse(promoPriceAfterReset.isPromoApplied());
        assertFalse(promoPriceAfterReset.isFixedPrice());
        assertNull(promoPriceAfterReset.getAppliedPromo());

        // Do offer
        cart.setProductSkuOffer("s01", "ABC", new BigDecimal("8.99"), "AUTH001");

        final CartItem offerPrice = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("10.99"), offerPrice.getListPrice());
        assertEquals(new BigDecimal("9.99"), offerPrice.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPrice.getPrice());
        assertFalse(offerPrice.isPromoApplied());
        assertTrue(offerPrice.isFixedPrice());
        assertEquals("AUTH001", offerPrice.getAppliedPromo());

        // Perform set price higher than offer
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("11.99"), new BigDecimal("12.99"));

        final CartItem offerPriceAfterResetHigher = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("12.99"), offerPriceAfterResetHigher.getListPrice());
        assertEquals(new BigDecimal("11.99"), offerPriceAfterResetHigher.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetHigher.getPrice());
        assertFalse(offerPriceAfterResetHigher.isPromoApplied());
        assertTrue(offerPriceAfterResetHigher.isFixedPrice());
        assertEquals("AUTH001", offerPriceAfterResetHigher.getAppliedPromo());

        // Perform set price lower than offer
        cart.setProductSkuPrice("s01", "ABC", new BigDecimal("4.99"), new BigDecimal("5.99"));

        final CartItem offerPriceAfterResetLower = cart.getCartItemList().get(0);

        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower.getListPrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower.getSalePrice());
        assertEquals(new BigDecimal("8.99"), offerPriceAfterResetLower.getPrice());
        assertFalse(offerPriceAfterResetLower.isPromoApplied());
        assertTrue(offerPriceAfterResetLower.isFixedPrice());
        assertEquals("AUTH001", offerPriceAfterResetLower.getAppliedPromo());

    }
}
