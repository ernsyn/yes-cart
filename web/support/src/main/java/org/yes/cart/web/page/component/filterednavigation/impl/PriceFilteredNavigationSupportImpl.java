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

package org.yes.cart.web.page.component.filterednavigation.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.yes.cart.domain.dto.ProductSearchResultNavDTO;
import org.yes.cart.domain.dto.ProductSearchResultNavItemDTO;
import org.yes.cart.domain.entity.Category;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.domain.misc.navigation.price.PriceTierNode;
import org.yes.cart.domain.misc.navigation.price.PriceTierTree;
import org.yes.cart.search.PriceNavigation;
import org.yes.cart.search.SearchQueryFactory;
import org.yes.cart.search.dto.FilteredNavigationRecord;
import org.yes.cart.search.dto.FilteredNavigationRecordRequest;
import org.yes.cart.search.dto.NavigationContext;
import org.yes.cart.search.dto.impl.FilteredNavigationRecordImpl;
import org.yes.cart.search.dto.impl.FilteredNavigationRecordRequestImpl;
import org.yes.cart.search.query.ProductSearchQueryBuilder;
import org.yes.cart.search.utils.SearchUtil;
import org.yes.cart.service.domain.CategoryService;
import org.yes.cart.service.domain.ProductService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.web.page.component.filterednavigation.PriceFilteredNavigationSupport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: denispavlov
 * Date: 13-09-28
 * Time: 1:12 AM
 */
public class PriceFilteredNavigationSupportImpl extends AbstractFilteredNavigationSupportImpl implements PriceFilteredNavigationSupport {

    private final Logger LOGFTQ = LoggerFactory.getLogger("FTQ");

    private final CategoryService categoryService;
    private final ShopService shopService;
    private final PriceNavigation priceNavigation;

    public PriceFilteredNavigationSupportImpl(final SearchQueryFactory searchQueryFactory,
                                              final ProductService productService,
                                              final CategoryService categoryService,
                                              final ShopService shopService,
                                              final PriceNavigation priceNavigation) {
        super(searchQueryFactory, productService);
        this.categoryService = categoryService;
        this.shopService = shopService;
        this.priceNavigation = priceNavigation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "filteredNavigationSupport-priceFilteredNavigationRecords")
    public List<FilteredNavigationRecord> getFilteredNavigationRecords(final NavigationContext navigationContext,
                                                                       final Long categoryId,
                                                                       final String currency,
                                                                       final String locale,
                                                                       final String recordName) {

        final List<FilteredNavigationRecord> navigationList = new ArrayList<>();

        if (!navigationContext.isFilteredBy(ProductSearchQueryBuilder.PRODUCT_PRICE)) {

            final Category category = navigationContext.isGlobal() ?
                    shopService.getDefaultNavigationCategory(navigationContext.getCustomerShopId()) : categoryService.getById(categoryId);
            if (category == null) {
                return Collections.emptyList();
            }
            final PriceTierTree priceTierTree = category.getNavigationByPriceTree();
            final boolean filteredNavigationByPriceAllowed = (category.getNavigationByPrice() == null || priceTierTree == null)
                    ? false : category.getNavigationByPrice();

            if (!filteredNavigationByPriceAllowed) {
                return Collections.emptyList();
            }

            final Shop shop = shopService.getById(navigationContext.getCustomerShopId());
            final List<FilteredNavigationRecord> allNavigationRecords = getPriceNavigationRecords(
                    priceTierTree,
                    currency,
                    shop);

            final String priceFacet = SearchUtil.priceFacetName(navigationContext.getCustomerShopId(), currency);
            final List<Pair<String, String>> rangeValues = new ArrayList<>();
            for (FilteredNavigationRecord record : allNavigationRecords) {

                final Pair<String, Pair<BigDecimal, BigDecimal>> range =
                    priceNavigation.decomposePriceRequestParams(record.getValue());

                rangeValues.add(new Pair<>(
                        String.valueOf(SearchUtil.priceToLong(range.getSecond().getFirst())),
                        String.valueOf(SearchUtil.priceToLong(range.getSecond().getSecond()))
                ));
            }
            final FilteredNavigationRecordRequest request = new FilteredNavigationRecordRequestImpl("priceFacet", priceFacet, rangeValues);

            final ProductSearchResultNavDTO counts =
                    getProductService().findFilteredNavigationRecords(navigationContext, Collections.singletonList(request));

            final List<ProductSearchResultNavItemDTO> rangeCounts = counts.getItems("priceFacet");

            if (CollectionUtils.isEmpty(rangeCounts)) {
                LOGFTQ.debug("Unable to get price filtered navigation for query: {}, request: {}", navigationContext.getProductQuery(), request);
                return Collections.emptyList();
            }

            if (rangeCounts.size() != allNavigationRecords.size()) {
                LOGFTQ.debug("Price ranges do not match configuration for query: {}, request: {}", navigationContext.getProductQuery(), request);
                return Collections.emptyList();
            }

            for (int i = 0; i < allNavigationRecords.size(); i++) {
                final Integer candidateResultCount = rangeCounts.get(i).getCount();
                if (candidateResultCount != null && candidateResultCount > 0) {
                    FilteredNavigationRecord record = allNavigationRecords.get(i);
                    record.setName(recordName);
                    record.setCode(ProductSearchQueryBuilder.PRODUCT_PRICE);
                    record.setCount(candidateResultCount);
                    navigationList.add(record);
                }
            }


        }
        return navigationList;
    }

    List<FilteredNavigationRecord> getPriceNavigationRecords(final PriceTierTree priceTierTree,
                                                             final String currency,
                                                             final Shop customerShop) {
        final List<PriceTierNode> priceTierNodes = getPriceTierNodes(priceTierTree, currency, customerShop);
        final List<FilteredNavigationRecord> result = new ArrayList<>(priceTierNodes.size());
        for (PriceTierNode priceTierNode : priceTierNodes) {
            result.add(
                    new FilteredNavigationRecordImpl(
                            ProductSearchQueryBuilder.PRODUCT_PRICE,
                            ProductSearchQueryBuilder.PRODUCT_PRICE,
                            priceNavigation.composePriceRequestParams(
                                    currency,
                                    priceTierNode.getFrom(),
                                    priceTierNode.getTo()
                            ),
                            0
                    )
            );
        }
        return result;
    }

    private List<PriceTierNode> getPriceTierNodes(final PriceTierTree priceTierTree, final String currency, final Shop shop) {
        List<PriceTierNode> priceTierNodes = priceTierTree.getPriceTierNodes(currency);
        if (priceTierNodes == null) {
            return Collections.emptyList();
        }
        return priceTierNodes;
    }

}
