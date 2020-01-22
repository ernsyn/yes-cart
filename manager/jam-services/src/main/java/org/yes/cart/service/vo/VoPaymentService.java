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

package org.yes.cart.service.vo;

import org.yes.cart.domain.vo.VoPayment;
import org.yes.cart.domain.vo.VoSearchContext;
import org.yes.cart.domain.vo.VoSearchResult;

import java.util.List;

/**
 * User: denispavlov
 * Date: 05/09/2016
 * Time: 18:13
 */
public interface VoPaymentService {

    /**
     * Get all payments for given filter
     *
     * @param filter filter
     *
     * @return orders
     *
     * @throws Exception errors
     */
    VoSearchResult<VoPayment> getFilteredPayments(VoSearchContext filter) throws Exception;

}
