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

import org.yes.cart.config.Configuration;
import org.yes.cart.config.ConfigurationContext;
import org.yes.cart.service.domain.TaxConfigService;
import org.yes.cart.service.domain.TaxService;
import org.yes.cart.shoppingcart.TaxProvider;

import java.math.BigDecimal;

/**
 * User: denispavlov
 * Date: 04/11/2014
 * Time: 17:49
 */
public class TaxProviderDefaultImpl implements TaxProvider, Configuration {

    private final TaxService taxService;
    private final TaxConfigService taxConfigService;

    private ConfigurationContext cfgContext;

    private static class TaxImpl implements Tax {

        private final BigDecimal rate;
        private final String code;
        private final boolean excluded;

        private TaxImpl(final BigDecimal rate, final String code, final boolean excluded) {
            this.rate = rate;
            this.code = code;
            this.excluded = excluded;
        }

        /** {@inheritDoc} */
        @Override
        public BigDecimal getRate() {
            return rate;
        }

        /** {@inheritDoc} */
        @Override
        public String getCode() {
            return code;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isExcluded() {
            return excluded;
        }

        /** {@inheritDoc} */
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Tax)) return false;

            final Tax tax = (Tax) o;

            if (excluded != tax.isExcluded()) return false;
            if (rate != null ? !rate.equals(tax.getRate()) : tax.getRate() != null) return false;
            return !(code != null ? !code.equals(tax.getCode()) : tax.getCode() != null);

        }

        /** {@inheritDoc} */
        public int hashCode() {
            int result = rate != null ? rate.hashCode() : 0;
            result = 31 * result + (code != null ? code.hashCode() : 0);
            result = 31 * result + (excluded ? 1 : 0);
            return result;
        }
    }

    private static final Tax NULL = new TaxImpl(BigDecimal.ZERO, "", false);

    public TaxProviderDefaultImpl(final TaxService taxService,
                                  final TaxConfigService taxConfigService) {
        this.taxService = taxService;
        this.taxConfigService = taxConfigService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tax determineTax(final String shopCode,
                            final String currency,
                            final String countryCode,
                            final String stateCode,
                            final String itemCode) {

        final Long taxId = taxConfigService.getTaxIdBy(shopCode, currency, countryCode, stateCode, itemCode);
        if (taxId == null) {
            return NULL;
        }
        final org.yes.cart.domain.entity.Tax tax = taxService.getById(taxId);
        return new TaxImpl(tax.getTaxRate(), tax.getCode(), tax.getExclusiveOfPrice());
    }


    /** {@inheritDoc} */
    @Override
    public ConfigurationContext getCfgContext() {
        return cfgContext;
    }

    public void setCfgContext(final ConfigurationContext cfgContext) {
        this.cfgContext = cfgContext;
    }

}
