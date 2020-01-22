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

package org.yes.cart.web.filter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.service.domain.ShopService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.theme.ThemeService;
import org.yes.cart.utils.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;
import org.yes.cart.web.support.request.IPResolver;
import org.yes.cart.web.support.request.impl.HttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 4:46:09 PM
 * <p/>
 * Shop resolver filter.
 * If shop can not be resolved by server/domain name
 * filter redirect to default url.
 */
public class ShopResolverFilter extends AbstractFilter implements Filter, ServletContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ShopResolverFilter.class);

    private final ShopService shopService;
    private final SystemService systemService;
    private final IPResolver ipResolver;
    private final ThemeService themeService;

    private ServletContext servletContext;

    private boolean skipRequestWrapper = false;


    public ShopResolverFilter(final ShopService shopService,
                              final SystemService systemService,
                              final IPResolver ipResolver,
                              final ThemeService themeService) {
        this.shopService = shopService;
        this.systemService = systemService;
        this.ipResolver = ipResolver;
        this.themeService = themeService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {

        final String serverDomainName = servletRequest.getServerName().toLowerCase();

        final Shop shop = shopService.getShopByDomainName(serverDomainName);

        if (shop == null) {
            final String url = systemService.getDefaultShopURL();
            if (LOG.isWarnEnabled()) {
                LOG.warn("Shop can not be resolved. For server name [" + serverDomainName + "] Redirect to : [" + url + "]");
            }
            ((HttpServletResponse) servletResponse).sendRedirect(url);
            return null;
        } else if (shop.isDisabled()) {
            final String url = systemService.getDefaultShopURL();
            if (LOG.isWarnEnabled()) {
                LOG.warn("Shop is disabled. For server name [" + serverDomainName + "] Redirect to : [" + url + "]" );
            }
            ((HttpServletResponse) servletResponse).sendRedirect(url);
            return null;
        }

        ApplicationDirector.setCurrentDomain(serverDomainName);
        ApplicationDirector.setCurrentShop(shop);
        ApplicationDirector.setCurrentCustomerShop(shop);
        ApplicationDirector.setShopperIPAddress(getRemoteIpAddr(servletRequest));
        ApplicationDirector.setCurrentThemeChain(themeService.getThemeChainByShopId(shop.getShopId(), serverDomainName));
        ShopCodeContext.setShopCode(shop.getCode());
        ShopCodeContext.setShopId(shop.getShopId());

        return getModifiedRequest(servletRequest, ApplicationDirector.getCurrentThemeChain());

    }

    private String getRemoteIpAddr(final ServletRequest servletRequest) {
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        return ipResolver.resolve(httpRequest);
    }


    /**
     * Create http servlet wrapper to handle multi store requests.
     *
     * @param servletRequest current request
     * @param themes         theme chain
     * @return servlet wrapper
     */
    private ServletRequest getModifiedRequest(final ServletRequest servletRequest, final List<String> themes) {

        if (skipRequestWrapper) {
            return servletRequest;
        }

        final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        final String servletPath = httpServletRequest.getServletPath();

        if (StringUtils.isNotEmpty(servletPath)) {
            final String newServletPath = "/" + themes.get(0) + "/markup" + servletPath;
            try {
                return new HttpServletRequestWrapper(httpServletRequest, newServletPath);
            } catch (/*MalformedURL*/Exception e) {
                LOG.error("Wrong URL for path : " + newServletPath, e);
            }
        }

        return servletRequest;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void doAfter(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws IOException, ServletException {

        ApplicationDirector.clear();
        ShopCodeContext.clear();

    }

    /** {@inheritDoc} */
    @Override
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);

        final String skip = filterConfig.getInitParameter("skipRequestWrapper");
        this.skipRequestWrapper = skip != null && Boolean.valueOf(skip);

    }

    @Override
    public void destroy() {
        ApplicationDirector.destroy();
        ShopCodeContext.destroy();
        super.destroy();
    }
}
