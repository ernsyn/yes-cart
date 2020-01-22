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
import { PaymentGatewayInfoVO, PaymentGatewayVO, PaymentGatewayParameterVO, Pair } from '../model/index';
import { ErrorEventBus } from './error-event-bus.service';
import { Util } from './util';
import { LogUtil } from './../log/index';
import { Observable }     from 'rxjs/Observable';
import 'rxjs/Rx';

/**
 * Shop service has all methods to work with shop.
 */
@Injectable()
export class PaymentService {

  private _serviceBaseUrl = Config.API + 'service/payment';  // URL to web api

  /**
   * Construct service, which has methods to work with information related to shop(s).
   * @param http http client.
   */
  constructor (private http: Http) {
    LogUtil.debug('PaymentService constructed');
  }

  /**
   * Get list of all payment gateways, which are accessible to manage or view,
   * @param lang language
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getPaymentGateways(lang:string) {
    return this.http.get(this._serviceBaseUrl + '/gateways/all/' + lang, Util.requestOptions())
      .map(res => <PaymentGatewayInfoVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all payment gateways enabled for shop, which are accessible to manage or view,
   * @param lang language
   * @param shopCode shop code
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getPaymentGatewaysForShop(lang:string, shopCode:string) {
    return this.http.get(this._serviceBaseUrl + '/gateways/shop/' + shopCode + '/' + lang, Util.requestOptions())
      .map(res => <PaymentGatewayInfoVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Get list of all payment gateways enabled on system, which are accessible to manage or view,
   * @param lang language
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getAllowedPaymentGatewaysForShops(lang:string) {
    return this.http.get(this._serviceBaseUrl + '/gateways/shop/allowed/' + lang, Util.requestOptions())
      .map(res => <PaymentGatewayInfoVO[]> this.json(res))
      .catch(this.handleError);
  }


  /**
   * Get list of all payment gateways, which are accessible to manage or view,
   * @param lang language
   * @param shopCode shop code
   * @param includeSecure
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  getPaymentGatewaysWithParameters(lang:string, shopCode:string, includeSecure:boolean) {

    let _secure = includeSecure ? 'secure/' : '';
    let _path = shopCode != null ? ('/gateways/configure/' + _secure + 'shop/' + shopCode + '/') : ('/gateways/configure/' + _secure + 'all/');

    return this.http.get(this._serviceBaseUrl + _path + lang, Util.requestOptions())
      .map(res => <PaymentGatewayVO[]> this.json(res))
      .catch(this.handleError);
  }

  /**
   * Update PG on/off flag.
   * @param shopCode
   * @param pgLabel
   * @param state
   * @returns {Promise<IteratorResult<T>>|Promise<T>|Q.Promise<IteratorResult<T>>}
   */
  updateDisabledFlag(shopCode: string, pgLabel:string, state:boolean) {
    LogUtil.debug('PaymentService change state PG ' + pgLabel + ' for ' + shopCode + ' to ' + state ? 'online' : 'offline');

    let _path = shopCode != null ? '/gateways/offline/' + shopCode : '/gateways/offline';

    return this.http.post(this._serviceBaseUrl + _path + '/' + pgLabel + '/' + state, null, Util.requestOptions())
      .catch(this.handleError);
  }


  /**
   * Update supported attributes.
   * @param shopCode shop code
   * @param pgLabel
   * @param attrs
   * @param includeSecure
   * @returns {Observable<R>}
   */
  savePaymentGatewayParameters(shopCode: string, pgLabel:string, attrs:Array<Pair<PaymentGatewayParameterVO, boolean>>, includeSecure:boolean) {
    let body = JSON.stringify(attrs);

    let _secure = includeSecure ? 'secure/' : '';
    let _path = shopCode != null ? ('/gateways/configure/'  + _secure + pgLabel + '/' + shopCode + '/') : ('/gateways/configure/' + _secure + pgLabel + '/');

    return this.http.post(this._serviceBaseUrl + _path , body, Util.requestOptions())
      .map(res => <PaymentGatewayParameterVO[]> this.json(res))
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

    LogUtil.error('PaymentService Server error: ', error);
    ErrorEventBus.getErrorEventBus().emit(error);
    let message = Util.determineErrorMessage(error);
    return Observable.throw(message.message || 'Server error');
  }

}
