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

package org.yes.cart.service.domain.impl;

import org.junit.Before;
import org.junit.Test;
import org.yes.cart.BaseCoreDBTestCase;
import org.yes.cart.constants.ServiceSpringKeys;
import org.yes.cart.dao.EntityFactory;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Product;
import org.yes.cart.domain.entity.ProductCategory;
import org.yes.cart.service.domain.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public class CategoryServiceImplTest extends BaseCoreDBTestCase {



    private CategoryService categoryService;

    @Override
    @Before
    public void setUp() {
        categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        super.setUp();
    }

    @Test
    public void testGetByProductId() {
        ProductCategoryService productCategoryService = (ProductCategoryService) ctx().getBean(ServiceSpringKeys.PRODUCT_CATEGORY_SERVICE);
        EntityFactory entityFactory = productCategoryService.getGenericDao().getEntityFactory();
        ProductService productService = (ProductService) ctx().getBean(ServiceSpringKeys.PRODUCT_SERVICE);
        ProductTypeService productTypeService = (ProductTypeService) ctx().getBean(ServiceSpringKeys.PRODUCT_TYPE_SERVICE);
        BrandService brandService = (BrandService) ctx().getBean(ServiceSpringKeys.BRAND_SERVICE);
        Product product = entityFactory.getByIface(Product.class);
        product.setCode("PROD_CODE");
        product.setName("product");
        product.setDescription("description");
        product.setProducttype(productTypeService.findById(1L));
        product.setBrand(brandService.findById(101L));
        product = productService.create(product);
        assertTrue(product.getProductId() > 0);
        // assign created product it to categories
        ProductCategory productCategory = entityFactory.getByIface(ProductCategory.class);
        productCategory.setProduct(product);
        productCategory.setCategory(categoryService.findById(128L));
        productCategory.setRank(0);
        productCategory = productCategoryService.create(productCategory);
        assertTrue(productCategory.getProductCategoryId() > 0);
        productCategory = entityFactory.getByIface(ProductCategory.class);
        productCategory.setProduct(product);
        productCategory.setCategory(categoryService.findById(133L));
        productCategory.setRank(0);
        productCategory = productCategoryService.create(productCategory);
        assertTrue(productCategory.getProductCategoryId() > 0);
        List<Category> list = categoryService.findByProductId(product.getProductId());
        assertEquals(2, list.size());
    }

    @Test
    public void testGetUIVariationTestNoFailover() {
        Category category = categoryService.findById(139L);
        assertNotNull(category);
        assertNull(category.getUitemplate());
        String uiVariation = categoryService.getCategoryTemplate(139L);
        assertNull(uiVariation);
    }

    @Test
    public void testGetUIVariationTestExists() {
        Category category = categoryService.findById(101L);
        assertNotNull(category);
        assertEquals("boys", category.getUitemplate());
        String uiVariation = categoryService.getCategoryTemplate(101L);
        assertEquals(category.getUitemplate(), uiVariation);
    }

    @Test
    public void testGetUIVariationTestFailover() {
        Category category = categoryService.findById(107L);
        assertNotNull(category);
        assertNull(category.getUitemplate());
        String uiVariation = categoryService.getCategoryTemplate(107L);
        assertEquals("fun", uiVariation);
    }

    @Test
    public void testGetChildCategoriesRecursiveTest() {
        Set<Long> categoryIds = new HashSet<>();
        categoryIds.addAll(Arrays.asList(101L, 102L, 103L, 104L, 105L, 143L, 144L));
        Set<Category> categories = categoryService.getChildCategoriesRecursive(101L);
        for (Category category : categories) {
            assertTrue(categoryIds.contains(category.getCategoryId()));
        }
    }

    @Test
    public void testGetChildCategoriesRecursiveIdsTest() {
        Set<Long> categoryIds = new HashSet<>();
        categoryIds.addAll(Arrays.asList(101L, 102L, 103L, 104L, 105L, 143L, 144L));
        List<Long> categories = categoryService.getChildCategoriesRecursiveIds(101L);
        assertEquals(categoryIds.size(), categories.size());
        assertTrue(categoryIds.containsAll(categories));
    }

    @Test
    public void testGetChildCategoriesRecursiveIdsWithLinksTest() {
        Set<Long> categoryIds = new HashSet<>();
        categoryIds.addAll(Arrays.asList(101L, 102L, 103L, 104L, 105L, 143L, 144L));
        List<Long> categories = categoryService.getChildCategoriesRecursiveIdsAndLinkIds(101L);
        assertEquals(categoryIds.size(), categories.size());
        assertTrue(categoryIds.containsAll(categories));
    }

    @Test
    public void testGetChildCategoriesRecursiveLinkedTest() {
        Set<Long> categoryIds = new HashSet<>();
        categoryIds.addAll(Arrays.asList(401L, 411L, 313L));
        Set<Category> categories = categoryService.getChildCategoriesRecursive(401L);
        for (Category category : categories) {
            assertTrue(categoryIds.contains(category.getCategoryId()));
        }
    }

    @Test
    public void testGetChildCategoriesRecursiveIdsLinkedTest() {
        Set<Long> categoryIds = new HashSet<>();
        categoryIds.addAll(Arrays.asList(401L, 411L, 313L));
        List<Long> categories = categoryService.getChildCategoriesRecursiveIds(401L);
        assertEquals(categoryIds.size(), categories.size());
        assertTrue(categoryIds.containsAll(categories));
    }

    @Test
    public void testGetChildCategoriesRecursiveIdsWithLinksLinkedTest() {
        Set<Long> categoryIds = new HashSet<>();
        categoryIds.addAll(Arrays.asList(401L, 411L, 312L, 313L));
        List<Long> categories = categoryService.getChildCategoriesRecursiveIdsAndLinkIds(401L);
        assertEquals(categoryIds.size(), categories.size());
        assertTrue(categoryIds.containsAll(categories));
    }

    @Test
    public void testGetChildCategoriesRecursiveNullTest() {
        Set<Category> categories = categoryService.getChildCategoriesRecursive(0L);
        assertTrue(categories.isEmpty());
    }

    @Test
    public void testIsCategoryHasChildrenTrue() throws Exception {
        assertTrue(categoryService.isCategoryHasChildren(101L));
    }

    @Test
    public void testIsCategoryHasChildrenLinkedTrue() throws Exception {
        assertTrue(categoryService.isCategoryHasChildren(411L));
    }

    @Test
    public void testIsCategoryHasChildrenFalse() throws Exception {

        final Category newCategory = categoryService.getGenericDao().getEntityFactory().getByIface(Category.class);
        newCategory.setGuid("TEST-CHILDREN");
        newCategory.setName("TEST-CHILDREN");

        final Category saved = categoryService.create(newCategory);
        assertFalse(categoryService.isCategoryHasChildren(saved.getCategoryId()));

        categoryService.delete(saved);

    }

    @Test
    public void testIsCategoryHasSubcategory() {
        CategoryService categoryService = (CategoryService) ctx().getBean(ServiceSpringKeys.CATEGORY_SERVICE);
        assertTrue(categoryService.isCategoryHasSubcategory(300, 304));
        assertTrue(categoryService.isCategoryHasSubcategory(301, 304));
        assertFalse(categoryService.isCategoryHasSubcategory(301, 312));
        assertFalse(categoryService.isCategoryHasSubcategory(50, 312));      // not existing root
        assertFalse(categoryService.isCategoryHasSubcategory(50, 98));      // not existing root   and given sub category
        assertFalse(categoryService.isCategoryHasSubcategory(300, 98));      // existing root   and not existing given sub category
        assertFalse(categoryService.isCategoryHasSubcategory(304, 300)); //reverse
        assertFalse(categoryService.isCategoryHasSubcategory(411, 313)); //linked
    }
}
