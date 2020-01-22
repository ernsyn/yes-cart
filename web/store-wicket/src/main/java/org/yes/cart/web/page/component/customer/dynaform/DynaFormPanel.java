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

package org.yes.cart.web.page.component.customer.dynaform;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.domain.entity.AttrValueWithAttribute;
import org.yes.cart.domain.entity.Customer;
import org.yes.cart.domain.entity.Shop;
import org.yes.cart.domain.i18n.I18NModel;
import org.yes.cart.domain.misc.Pair;
import org.yes.cart.web.page.component.BaseComponent;
import org.yes.cart.web.support.constants.StorefrontServiceSpringKeys;
import org.yes.cart.web.support.service.ContentServiceFacade;
import org.yes.cart.web.support.service.CustomerServiceFacade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dynamic form to work with different attribute values. Form fields and field editors
 * depends from attributes, that described for customers.
 * Panel can be refactored, in case if some dynamic behaviour will be need for other entities, that
 * has attributes. Just add callback to store particular entity when, attributes will be submitted.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 10/23/11
 * Time: 8:53 PM
 */
public class DynaFormPanel extends BaseComponent {

    private static final Logger LOG = LoggerFactory.getLogger(DynaFormPanel.class);

    // ------------------------------------- MARKUP IDs BEGIN ---------------------------------- //
    private final static String FORM = "form";
    private final static String SAVE_LINK = "saveLink";
    private final static String FIELDS = "fields";
    private final static String NAME = "name";
    private final static String EDITOR = "editor";
    private final static String CONTENT = "dynaformContent";
    // ------------------------------------- MARKUP IDs END ---------------------------------- //


    @SpringBean(name = StorefrontServiceSpringKeys.CUSTOMER_SERVICE_FACADE)
    private CustomerServiceFacade customerService;

    @SpringBean(name = StorefrontServiceSpringKeys.CONTENT_SERVICE_FACADE)
    private ContentServiceFacade contentServiceFacade;

    private final EditorFactory editorFactory = new EditorFactory();


    /**
     * Construct dynamic form.
     *
     * @param id component id.
     */
    public DynaFormPanel(final String id) {
        super(id);
    }

    /**
     * Construct dynamic form.
     *
     * @param id            component id.
     * @param customerModel customer model
     */
    public DynaFormPanel(final String id, final IModel<Customer> customerModel) {

        super(id, customerModel);

        final Shop shop = getCurrentShop();
        final Customer customer = (Customer) getDefaultModelObject();

        final List<Pair<AttrValueWithAttribute, Boolean>> attrValueCollection = customerService.getCustomerProfileAttributes(shop, customer);

        final Form form = new Form<Object>(FORM) {

            @Override
            protected void onSubmit() {
                LOG.debug("Attributes will be updated for customer [{}]", customer.getEmail());

                final Map<String, String> values = new HashMap<>();
                for (Pair<? extends AttrValueWithAttribute, Boolean> av : attrValueCollection) {
                    LOG.debug("Attribute with code [{}] has value [{}], readonly [{}]",
                                    av.getFirst().getAttribute().getCode(),
                                    av.getFirst().getVal(),
                                    av.getSecond()
                            );
                    if (av.getSecond() != null && !av.getSecond()) {
                        if ("salutation".equals(av.getFirst().getAttribute().getCode())) {
                            customer.setSalutation(av.getFirst().getVal());
                        } else if ("firstname".equals(av.getFirst().getAttribute().getCode())) {
                            if (StringUtils.isNotBlank(av.getFirst().getVal())) {
                                customer.setFirstname(av.getFirst().getVal());
                            }
                        } else if ("middlename".equals(av.getFirst().getAttribute().getCode())) {
                            customer.setMiddlename(av.getFirst().getVal());
                        } else if ("lastname".equals(av.getFirst().getAttribute().getCode())) {
                            if (StringUtils.isNotBlank(av.getFirst().getVal())) {
                                customer.setLastname(av.getFirst().getVal());
                            }
                        } else if ("companyname1".equals(av.getFirst().getAttribute().getCode())) {
                            customer.setCompanyName1(av.getFirst().getVal());
                        } else if ("companyname2".equals(av.getFirst().getAttribute().getCode())) {
                            customer.setCompanyName2(av.getFirst().getVal());
                        } else if ("companydepartment".equals(av.getFirst().getAttribute().getCode())) {
                            customer.setCompanyDepartment(av.getFirst().getVal());
                        } else if (!av.getFirst().getAttribute().isMandatory() || StringUtils.isNotBlank(av.getFirst().getVal())) {
                            values.put(av.getFirst().getAttribute().getCode(), av.getFirst().getVal());
                        }
                    }
                }

                customerService.updateCustomerAttributes(getCurrentShop(), customer, values);
                info(getLocalizer().getString("profileUpdated", this));
            }
        };

        addOrReplace(form);

        RepeatingView fields = new RepeatingView(FIELDS);

        form.add(fields);

        final String lang = getLocale().getLanguage();

        for (Pair<? extends AttrValueWithAttribute, Boolean> attrValue : attrValueCollection) {

            WebMarkupContainer row = new WebMarkupContainer(fields.newChildId());

            row.add(getLabel(attrValue.getFirst(), lang));

            row.add(getEditor(attrValue.getFirst(), attrValue.getSecond()));

            fields.add(row);

        }

        form.add( new SubmitLink(SAVE_LINK) );

        form.add(new Label(CONTENT, ""));

        form.setVisible(!attrValueCollection.isEmpty());

    }

    @Override
    protected void onBeforeRender() {

        final String lang = getLocale().getLanguage();

        String dynaformInfo = getContentInclude(getCurrentShopId(), "profile_dynaform_content_include", lang);
        get(FORM).get(CONTENT).replaceWith(new Label(CONTENT, dynaformInfo).setEscapeModelStrings(false));


        super.onBeforeRender();
    }

    private String getContentInclude(long shopId, String contentUri, String lang) {
        String content = contentServiceFacade.getContentBody(contentUri, shopId, lang);
        if (content == null) {
            content = "";
        }
        return content;
    }


    private Label getLabel(final AttrValueWithAttribute attrValue, final String lang) {

        final I18NModel model = getI18NSupport().getFailoverModel(
                attrValue.getAttribute().getDisplayName(),
                attrValue.getAttribute().getName());

        return new Label(NAME, new IModel<String>() {

            private final I18NModel m = model;

            @Override
            public String getObject() {
                final String lang1 = getLocale().getLanguage();
                return m.getValue(lang1);
            }
        });
    }


    /**
     * Get the particular editor for given attribute value. Type of editor depends from type of attribute value.
     *
     * @param attrValue give {@link org.yes.cart.domain.entity.AttrValue}
     * @param readOnly  if true this component is read only
     *
     * @return editor
     */
    protected Component getEditor(final AttrValueWithAttribute attrValue, final Boolean readOnly) {

        return editorFactory.getEditor(EDITOR, this, getLocale().getLanguage(), attrValue, readOnly);
    }


}
