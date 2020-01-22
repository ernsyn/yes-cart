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

package org.yes.cart.service.dto;

import org.yes.cart.domain.dto.ProductTypeDTO;
import org.yes.cart.domain.entity.Attribute;
import org.yes.cart.domain.misc.SearchContext;
import org.yes.cart.domain.misc.SearchResult;
import org.yes.cart.exception.UnableToCreateInstanceException;
import org.yes.cart.exception.UnmappedInterfaceException;

import java.util.List;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface DtoProductTypeService extends GenericDTOService<ProductTypeDTO> {

    /**
     * Find product types by name.
     *
     * @param name product type name for partial match.
     *
     * @return list of types
     */
    List<ProductTypeDTO> findProductTypes(String name)  throws UnmappedInterfaceException, UnableToCreateInstanceException;

    /**
     * Find product types by filter.
     *
     * @param filter filter
     *
     * @return list of types
     */
    SearchResult<ProductTypeDTO> findProductTypes(SearchContext filter) throws UnmappedInterfaceException, UnableToCreateInstanceException;


    /**
     * Find all product types that use attribute code.
     *
     * @param code {@link Attribute#getCode()}
     *
     * @return list of product types
     */
    List<ProductTypeDTO> findByAttributeCode(String code)  throws UnmappedInterfaceException, UnableToCreateInstanceException;


}
