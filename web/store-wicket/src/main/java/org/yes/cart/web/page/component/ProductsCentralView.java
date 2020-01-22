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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.yes.cart.domain.dto.ProductSearchResultDTO;
import org.yes.cart.domain.dto.ProductSearchResultPageDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Seoable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.web.page.component.data.SortableProductDataProvider;
import org.yes.cart.web.page.component.filterednavigation.AttributeProductFilter;
import org.yes.cart.web.page.component.filterednavigation.PriceProductFilter;
import org.yes.cart.web.page.component.navigation.ProductPerPageListView;
import org.yes.cart.web.page.component.navigation.ProductSorter;
import org.yes.cart.web.page.component.navigation.URLPagingNavigator;
import org.yes.cart.web.page.component.product.ProductInListView;
import org.yes.cart.web.service.wicketsupport.PaginationSupport;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.impl.CategorySeoableDecoratorImpl;
import org.yes.cart.web.support.service.ProductServiceFacade;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 10:29 PM
 */
public class ProductsCentralView extends AbstractCentralView {

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /**
     * Named place for  per page links
     */
    private static final String ITEMS_PER_PAGE_LIST = "itemsPerPageList";
    /**
     * Name of product view.
     */
    private static final String PAGINATOR = "paginator";
    private static final String PAGINATOR2 = "paginator2";
    /**
     * Name of product view.
     */
    private static final String SORTER = "sorter";
    /**
     * Name of product view.
     */
    private static final String PRODUCT = "product";
    /**
     * Name of products list view.
     */
    private static final String PRODUCT_LIST = "rows";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.PRODUCT_SERVICE_FACADE)
    protected ProductServiceFacade productServiceFacade;

    private transient Category category;

    /**
     * Construct panel.
     *
     * @param id           panel id
     * @param categoryId   current category id.
     * @param navigationContext navigation context.
     */
    public ProductsCentralView(final String id, final long categoryId, final NavigationContext navigationContext) {
        super(id, categoryId, navigationContext);

    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {


        final long browsingShopId = getCurrentCustomerShopId();
        final long categoryId = getCategoryId();
        final NavigationContext context = getNavigationContext();

        add(new TopCategories("topCategories"));
        add(new AttributeProductFilter("attributeFilter", categoryId, context));
        add(new PriceProductFilter("priceFilter", categoryId, context));

        final List<String> itemsPerPageValues = categoryServiceFacade.getItemsPerPageOptionsConfig(categoryId, browsingShopId);
        final List<String> pageSortingValues = categoryServiceFacade.getPageSortingOptionsConfig(categoryId, browsingShopId);
        final Pair<String, String> widthHeight = categoryServiceFacade.getProductListImageSizeConfig(categoryId, browsingShopId);
        final int columns = categoryServiceFacade.getProductListColumnOptionsConfig(categoryId, browsingShopId);

        final PageParameters pageParameters = getPage().getPageParameters();
        final PaginationSupport pagination = getWicketSupportFacade().pagination();
        int currentPageIdx = pagination.getCurrentPage(pageParameters);
        if (currentPageIdx < 0) {
            // if we have gone overboard restart from 0 by redirect!
            final PageParameters params = new PageParameters(getPage().getPageParameters());
            getWicketSupportFacade().pagination().removePageParam(params);
            setResponsePage(getPage().getClass(), params);
            currentPageIdx = 0;
        }

        final int selectedItemPerPage = pagination.getCurrentItemsPerPage(pageParameters, itemsPerPageValues);
        final Pair<String, Boolean> sortResult = getSortField();

        ProductSearchResultPageDTO products = productServiceFacade.getListProducts(
                context, currentPageIdx * selectedItemPerPage, selectedItemPerPage,
                sortResult.getFirst(), sortResult.getSecond());

        if (currentPageIdx * selectedItemPerPage > products.getTotalHits()) {
            // if we have gone overboard restart from 0 by redirect!
            final PageParameters params = new PageParameters(getPage().getPageParameters());
            getWicketSupportFacade().pagination().removePageParam(params);
            setResponsePage(getPage().getClass(), params);
        }

        final boolean hasResults = products.getTotalHits() > 0L;

        final SortableProductDataProvider dataProvider = new SortableProductDataProvider(products);

        final GridView<ProductSearchResultDTO> productDataView = new GridView<ProductSearchResultDTO>(PRODUCT_LIST, dataProvider) {

            @Override
            protected void populateItem(Item<ProductSearchResultDTO> productItem) {
                productItem.add(
                        new ProductInListView(PRODUCT, productItem.getModelObject(), widthHeight)
                );
            }

            @Override
            protected void populateEmptyItem(Item<ProductSearchResultDTO> productItem) {
                productItem.add(
                        new Label(PRODUCT, StringUtils.EMPTY).setVisible(false)
                );

            }



        };

        productDataView.setColumns(columns);
        productDataView.setRows(
                selectedItemPerPage % columns == 0 ?
                        selectedItemPerPage / columns :
                        (selectedItemPerPage / columns) + 1);
        productDataView.setItemsPerPage(selectedItemPerPage);
        productDataView.setCurrentPage(currentPageIdx);

        add(new ProductSorter(SORTER, pageSortingValues).setVisible(hasResults));
        add(new URLPagingNavigator(PAGINATOR, productDataView, getPage().getPageParameters()).setVisible(hasResults));
        add(new URLPagingNavigator(PAGINATOR2, productDataView, getPage().getPageParameters()).setVisible(hasResults));
        add(new ProductPerPageListView(ITEMS_PER_PAGE_LIST, itemsPerPageValues).setVisible(hasResults));
        add(productDataView.setVisible(hasResults));

        if (!hasResults) {
            info(getLocalizer().getString("noResultsFound", this));
        }

        super.onBeforeRender();
    }

    /**
     * Get the sort field and sort direction.
     *
     * @return {@link Pair} of sort filed and sort direction (true - asc order)
     */
    private Pair<String, Boolean> getSortField() {
        String sort = getPage().getPageParameters().get(WebParametersKeys.SORT).toString();
        if (sort != null) {
            return new Pair<>(sort, false);
        }
        sort = getPage().getPageParameters().get(WebParametersKeys.SORT_REVERSE).toString();
        if (sort != null) {
            return new Pair<>(sort, true);
        }
        return new Pair<>(null, false);
    }


    /**
     * Get category.
     * @return {@link Category}
     */
    public Category getContent() {
        if (category == null) {
            category = categoryServiceFacade.getCategory(getCategoryId(), getCurrentCustomerShopId());
        }
        return category;
    }


    /**
     * Hook for subclasses to utilise Seo mechanism
     *
     * @return main seo object for given page
     */
    protected Seoable getSeoObject() {
        final Category cat = getContent();
        if (cat != null) {
            return new CategorySeoableDecoratorImpl(cat, getPage().getLocale().getLanguage());
        }
        return null;
    }

}
