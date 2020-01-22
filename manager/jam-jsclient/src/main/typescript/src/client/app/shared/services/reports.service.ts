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


import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Config } from '../config/env.config';
import { DashboardWidgetInfoVO, DashboardWidgetVO, ReportDescriptorVO, ReportRequestVO } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class ReportsService {

  private _serviceBaseUrl = Config.API + 'service/reports';  // URL to web api

  /**
   * Construct management service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('ReportsService constructed');
  }

  /**
   * Get current user widgets,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getDashboard(lang:string) {
    return this.http.get(this._serviceBaseUrl + '/dashboard/' + lang + '/', Util.requestOptions())
      .map(res => <DashboardWidgetVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get current user widgets,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getDashboardWidget(widget:string, lang:string) {
    return this.http.get(this._serviceBaseUrl + '/dashboard/' + lang + '/' + widget + '/', Util.requestOptions())
      .map(res => <DashboardWidgetVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get current user widgets,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAvailableWidgets(lang:string) {
    return this.http.get(this._serviceBaseUrl + '/dashboard/' + lang + '/available/', Util.requestOptions())
      .map(res => <DashboardWidgetInfoVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get current user widgets,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  updateDashboardWidgets(widgets:DashboardWidgetInfoVO[], lang:string) {

    let widgetCsv = '';
    widgets.forEach(function(widget) {
       widgetCsv += (widgetCsv.length > 0 ? ',' : '') + widget.widgetId;
    });

    let body = widgetCsv;

    return this.http.post(this._serviceBaseUrl + '/dashboard/' + lang + '/', body,
          Util.requestOptions({ type:'text/plain; charset=utf-8' }))
      .catch(this.handleError);

  }

  /**
   * Get current user reports,
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getReportDescriptors() {
    return this.http.get(this._serviceBaseUrl + '/report/all', Util.requestOptions())
      .map(res => <ReportDescriptorVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Refine report options.
   * @param req report request with selections
   * @returns {Observable<R>}
   */
  updateReportRequestValues(req:ReportRequestVO) {

    let body = JSON.stringify(req);

    return this.http.post(this._serviceBaseUrl + '/report/configure', body, Util.requestOptions())
        .map(res => <ReportRequestVO> this.json(res))
        .catch(this.handleError);
  }


  /**
   * Generate report and return file name on server.
   * @param req report request with selections
   * @returns {Observable<R>}
   */
  generateReport(req:ReportRequestVO) {

    let body = JSON.stringify(req);

    return this.http.post(this._serviceBaseUrl + '/report/generate', body, Util.requestOptions())
        .map(res => res.text())
        .catch(this.handleError);
  }


  private json(res: Response): any {
    let contentType = res.headers.get('Content-Type');
    LogUtil.debug('Processing JSON response', contentType, res.text().includes('loginForm'));
    if (contentType != null && contentType.includes('text/html') && res.text().includes('loginForm')) {
      throw new Error('MODAL_ERROR_MESSAGE_AUTH');
    }
    return res.json();
  }


  private handleError (error:any) {

    LogUtil.error('ReportsService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
