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
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.GridView;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Seo;
import org.yes.cart.domain.entity.Seoable;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.shoppingcart.ShoppingCartCommand;
import org.yes.cart.web.page.component.data.SortableCategoryDataProvider;
import org.yes.cart.web.support.constants.WebParametersKeys;
import org.yes.cart.web.support.entity.decorator.CategoryDecorator;
import org.yes.cart.web.support.entity.decorator.DecoratorFacade;
import org.yes.cart.web.support.entity.decorator.impl.CategorySeoableDecoratorImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 7/13/11
 * Time: 9:32 AM
 */
public class SubCategoriesCentralView extends AbstractCentralView {

    /**
     * Default quantity of columns to show subcategories in list
     */
    private static final String DEFAULT_SUBCATEGORIES_COLUMNS = "2";

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    /**
     * Name of category list view.
     */
    private static final String CATEGORY_LIST = "rows";
    /**
     * Name of category list view.
     */
    private static final String CATEGORY_VIEW = "categoryView";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    private transient Category category;

    /**
     * Construct panel.
     *
     * @param id           panel id
     * @param categoryId   current category id.
     * @param navigationContext navigation context.
     */
    public SubCategoriesCentralView(final String id, final long categoryId, final NavigationContext navigationContext) {
        super(id, categoryId, navigationContext);
    }

    private List<CategoryDecorator> decorate(final List<Category> categories) {
        final DecoratorFacade facade = getDecoratorFacade();
        final List<CategoryDecorator> rez = new ArrayList<>();
        for (Category cat : categories) {
            rez.add((CategoryDecorator) facade.decorate(cat, getWicketUtil().getHttpServletRequest().getContextPath(), true));
        }
        return rez;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeRender() {

        add(new TopCategories("topCategories"));

        final long categoryId = getCategoryId();
        final long browsingShopId = getCurrentCustomerShopId();
        final String lang = getLocale().getLanguage();

        final List<CategoryDecorator> categories = decorate(categoryServiceFacade.getCurrentCategoryMenu(categoryId, browsingShopId, lang));

        final SortableDataProvider<CategoryDecorator, String> dataProvider = new SortableCategoryDataProvider(categories);

        final Pair<String, String> imageSize = categoryServiceFacade.getCategoryListImageSizeConfig(categoryId, browsingShopId);
        final int columns = categoryServiceFacade.getCategoryListColumnOptionsConfig(categoryId, browsingShopId);

        final GridView<CategoryDecorator> categoryView = new GridView<CategoryDecorator>(CATEGORY_LIST, dataProvider) {

            /** {@inheritDoc} */
            @Override
            protected void populateItem(Item<CategoryDecorator> categoryItem) {

                categoryItem.add(

                        new CategoryView(CATEGORY_VIEW, imageSize)

                );

            }

            @Override
            protected void populateEmptyItem(Item<CategoryDecorator> categoryItem) {

                categoryItem.add(

                        new Label(CATEGORY_VIEW, StringUtils.EMPTY).setVisible(false)

                );

            }

        };

        categoryView.setColumns(columns);
        categoryView.setRows(categories.size() % columns == 0 ?
                                categories.size() / columns :
                                (categories.size() / columns) + 1);
        categoryView.setItemsPerPage(categories.size());

        add(categoryView);

        super.onBeforeRender();
    }


    @Override
    protected String getRelCanonical(final Seo seo, final String language) {

        if (getCategoryId() > 0L) {

            final String uri = getBookmarkService().saveBookmarkForContent(String.valueOf(getCategoryId()));

            return getWicketUtil().getHttpServletRequest().getContextPath() + "/"
                    + WebParametersKeys.CATEGORY_ID + "/" + uri + "/" + ShoppingCartCommand.CMD_CHANGELOCALE + "/" + language;

        }

        return super.getRelCanonical(seo, language);
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
