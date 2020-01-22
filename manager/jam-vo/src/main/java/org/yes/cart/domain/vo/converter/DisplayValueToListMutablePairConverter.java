/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the 'License');
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an 'AS IS' BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.yes.cart.domain.vo.converter;

import com.inspiresoftware.lib.dto.geda.adapter.BeanFactory;
import com.inspiresoftware.lib.dto.geda.adapter.ValueConverter;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.i18n.impl.StringI18NModel;
import org.yes.cart.domain.vo.VoUtils;

import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 28/07/2016
 * Time: 17:40
 */
public class DisplayValueToListMutablePairConverter implements ValueConverter {

    @Override
    public Object convertToDto(final Object object, final BeanFactory beanFactory) {
        final I18NModel model = new StringI18NModel((String) object);
        return VoUtils.adaptMapToPairs(model.getAllValues());
    }

    @Override
    public Object convertToEntity(final Object object, final Object oldEntity, final BeanFactory beanFactory) {
        final Map<String, String> map = VoUtils.adaptPairsToMap((List) object);
        final I18NModel model = new StringI18NModel(map);
        return model.toString();
    }
}
