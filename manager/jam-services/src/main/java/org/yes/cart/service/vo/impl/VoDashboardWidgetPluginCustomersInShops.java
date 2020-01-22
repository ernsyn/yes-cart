/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.vo.VoDashboardWidget;
import org.yes.cart.domain.vo.VoManager;
import org.yes.cart.domain.vo.VoManagerRole;
import org.yes.cart.domain.vo.VoManagerShop;
import org.yes.cart.service.domain.AttributeService;
import org.yes.cart.service.domain.CustomerService;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.vo.VoDashboardWidgetPlugin;
import org.yes.cart.utils.DateUtils;
import org.yes.cart.utils.TimeContext;

import java.time.ZonedDateTime;
import java.util.*;

/**
 * User: denispavlov
 * Date: 25/09/2016
 * Time: 18:14
 */
public class VoDashboardWidgetPluginCustomersInShops extends AbstractVoDashboardWidgetPluginImpl implements VoDashboardWidgetPlugin {

    private List<String> roles = Collections.emptyList();

    private final CustomerService customerService;
    private final ShopService shopService;

    public VoDashboardWidgetPluginCustomersInShops(final CustomerService customerService,
                                                   final ShopService shopService,
                                                   final AttributeService attributeService,
                                                   final String widgetName) {
        super(attributeService, widgetName);
        this.customerService = customerService;
        this.shopService = shopService;
    }

    @Override
    public boolean applicable(final VoManager manager) {
        if (manager.getManagerShops().size() > 0) {
            for (final VoManagerRole role : manager.getManagerRoles()) {
                if (roles.contains(role.getCode())) {
                    return manager.getManagerShops().size() > 0;
                }
            }
        }
        return false;
    }

    @Override
    protected void processWidgetData(final VoManager manager, final VoDashboardWidget widget, final Attribute config) {

        final Set<Long> shops = new HashSet<>();
        for (final VoManagerShop shop : manager.getManagerShops()) {
            shops.add(shop.getShopId());
            final Set<Long> subs = this.shopService.getAllShopsAndSubs().get(shop.getShopId());
            if (subs != null) {
                shops.addAll(subs);
            }
        }

        final ZonedDateTime today = TimeContext.getZonedDateTime();

        final String criteria = " join e.shops cshop where cshop.shop.shopId in (?1) and e.createdTimestamp >= ?2";

        final int ordersToday = this.customerService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfDay(today).toInstant()
        );

        final int ordersWeek = this.customerService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfWeek(today).toInstant()
        );

        final int ordersMonth = this.customerService.findCountByCriteria(
                criteria, shops, DateUtils.zdtAtStartOfMonth(today).toInstant()
        );


        final Map<String, Integer> data = new HashMap<>();
        data.put("customersToday", ordersToday);
        data.put("customersThisWeek", ordersWeek);
        data.put("customersThisMonth", ordersMonth);

        widget.setData(data);

    }

    /**
     * Spring IoC.
     *
     * @param roles roles for accessing this widget
     */
    public void setRoles(final List<String> roles) {
        this.roles = roles;
    }

}
