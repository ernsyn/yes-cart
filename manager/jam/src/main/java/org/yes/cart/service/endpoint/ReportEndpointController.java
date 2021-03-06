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

package org.yes.cart.service.endpoint;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.yes.cart.domain.vo.VoReportDescriptor;
import org.yes.cart.domain.vo.VoReportRequest;

import java.util.List;

/**
 * User: denispavlov
 * Date: 02/10/2016
 * Time: 12:35
 */
@Controller
@RequestMapping("/reports")
public interface ReportEndpointController {

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/report/all", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    List<VoReportDescriptor> getReportDescriptors();

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/report/configure", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    VoReportRequest getParameterValues(@RequestBody VoReportRequest reportRequest);

    @PreAuthorize("isFullyAuthenticated()")
    @RequestMapping(value = "/report/generate", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    String generateReport(@RequestBody VoReportRequest reportRequest) throws Exception;

}
