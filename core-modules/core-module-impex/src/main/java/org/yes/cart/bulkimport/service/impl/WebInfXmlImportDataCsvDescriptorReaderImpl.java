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

package org.yes.cart.bulkimport.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.yes.cart.bulkcommon.service.DataDescriptorReader;
import org.yes.cart.bulkimport.model.ImportDescriptor;
import org.yes.cart.domain.entity.DataDescriptor;
import org.yes.cart.stream.xml.XStreamProvider;
import org.yes.cart.utils.log.Markers;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: denispavlov
 * Date: 02/06/2015
 * Time: 14:04
 */
public class WebInfXmlImportDataCsvDescriptorReaderImpl implements DataDescriptorReader<ImportDescriptor>, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(WebInfXmlImportDataCsvDescriptorReaderImpl.class);


    private final String pathToImportDescriptors;
    private XStreamProvider<ImportDescriptor> importDescriptorXStreamProvider;

    private ApplicationContext applicationContext;

    public WebInfXmlImportDataCsvDescriptorReaderImpl(final String pathToImportDescriptors) {
        this.pathToImportDescriptors = pathToImportDescriptors;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(final DataDescriptor dataDescriptor) {
        return dataDescriptor != null &&
                (DataDescriptor.TYPE_WEBINF_XML.equals(dataDescriptor.getType())
                        || DataDescriptor.TYPE_WEBINF_XML_CSV.equals(dataDescriptor.getType()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImportDescriptor toDescriptorObject(final DataDescriptor dataDescriptor) {
        if (supports(dataDescriptor)) {

            if (DataDescriptor.TYPE_RAWINF_XML.equals(dataDescriptor.getType())) {
                LOG.warn(Markers.alert(), "Descriptor {} uses deprecated namespace", dataDescriptor.getDatadescriptorId());
            }

            final String path = "WEB-INF/" + pathToImportDescriptors + "/" + dataDescriptor.getValue();
            final Resource res = applicationContext.getResource(path);

            try {
                return getImportDescriptorFromXML(res.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Unable to load import descriptor configuration from WEB-INF: " + path + " for " + dataDescriptor.getName());
            }
        }
        return null;
    }



    /**
     * IoC. XStream provider for import descriptor files.
     *
     * @param importDescriptorXStreamProvider xStream provider
     */
    public void setImportDescriptorXStreamProvider(final XStreamProvider importDescriptorXStreamProvider) {
        this.importDescriptorXStreamProvider = importDescriptorXStreamProvider;
    }

    protected ImportDescriptor getImportDescriptorFromXML(InputStream is) {
        return importDescriptorXStreamProvider.fromXML(is);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
