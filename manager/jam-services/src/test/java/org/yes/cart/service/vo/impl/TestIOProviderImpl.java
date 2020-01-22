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
package org.yes.cart.service.vo.impl;

import org.yes.cart.stream.io.FileSystemIOProvider;
import org.yes.cart.stream.io.IOProvider;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 23/09/2019
 * Time: 08:31
 */
public class TestIOProviderImpl implements IOProvider, FileSystemIOProvider {

    @Override
    public File resolveFileFromUri(final String uri, final Map<String, Object> context) {
        return null;
    }

    @Override
    public boolean supports(final String uri) {
        return true;
    }

    @Override
    public boolean exists(final String uri, final Map<String, Object> context) {
        return false;
    }

    @Override
    public boolean isNewerThan(final String uriToCheck, final String uriToCheckAgainst, final Map<String, Object> context) {
        return false;
    }

    @Override
    public byte[] read(final String uri, final Map<String, Object> context) throws IOException {
        return new byte[] { 0 };
    }

    @Override
    public void write(final String uri, final byte[] content, final Map<String, Object> context) throws IOException {

    }

    @Override
    public void delete(final String uri, final Map<String, Object> context) throws IOException {

    }
    
}
