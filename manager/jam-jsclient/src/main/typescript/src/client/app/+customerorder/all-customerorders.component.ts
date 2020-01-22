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
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { CustomerOrderService, I18nEventBus, ErrorEventBus, UserEventBus } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { CustomerOrderInfoVO, CustomerOrderVO, CustomerOrderDeliveryInfoVO, CustomerOrderTransitionResultVO, Pair, SearchResultVO } from './../shared/model/index';
import { Futures, Future } from './../shared/event/index';
import { CookieUtil } from './../shared/cookies/index';
import { Config } from './../shared/config/env.config';
import { UiUtil } from './../shared/ui/index';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-all-customerorders',
  moduleId: module.id,
  templateUrl: 'all-customerorders.component.html',
})

export class AllCustomerOrdersComponent implements OnInit, OnDestroy {

  private static CUSTOMERORDERS:string = 'customerorders';
  private static CUSTOMERORDER:string = 'customerorder';

  private static _statuses:Pair<string, boolean>[] = [
    { first: 'os.none', second: false },
    { first: 'os.pending', second: true },
    { first: 'os.waiting.approve', second: true },
    { first: 'os.waiting.payment', second: true },
    { first: 'os.waiting', second: true },
    { first: 'os.in.progress', second: true },
    { first: 'os.cancelled', second: true },
    { first: 'os.cancelled.waiting.payment', second: true },
    { first: 'os.returned', second: true },
    { first: 'os.returned.waiting.payment', second: true },
    { first: 'os.partially.shipped', second: true },
    { first: 'os.completed', second: true },
    { first: 'qs.none', second: false },
    { first: 'qs.pending', second: false },
    { first: 'qs.cancelled', second: false },
    { first: 'qs.ready', second: false },
    { first: 'qs.rejected', second: false },
    { first: 'qs.expired', second: false },
    { first: 'qs.ordered', second: false },
  ];

  private static _open:string[] = [
    'os.pending', 'os.waiting.approve', 'os.waiting', 'os.waiting.payment', 'os.in.progress', 'os.partially.shipped'
  ];

  private static _pendingPayments:string[] = [
    'os.waiting.payment', 'os.cancelled.waiting.payment', 'os.returned.waiting.payment'
  ];

  private static _cancelled:string[] = [
    'os.cancelled', 'os.cancelled.waiting.payment', 'os.returned', 'os.returned.waiting.payment'
  ];

  private static _completed:string[] = [
    'os.completed'
  ];

  private searchHelpShow:boolean = false;
  private forceShowAll:boolean = false;
  private viewMode:string = AllCustomerOrdersComponent.CUSTOMERORDERS;

  private customerorders:SearchResultVO<CustomerOrderInfoVO>;
  private customerorderFilter:string;
  private customerorderFilterRequired:boolean = true;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private selectedCustomerorder:CustomerOrderInfoVO;

  private selectedCustomerorderApprovable:boolean = false;
  private selectedCustomerorderCancellable:boolean = false;
  private selectedCustomerorderReturnable:boolean = false;
  private selectedCustomerorderRefundable:boolean = false;
  private selectedCustomerorderRefundableManual:boolean = false;

  private customerorderEdit:CustomerOrderVO;

  private selectedCustomerdelivery:CustomerOrderDeliveryInfoVO;

  private selectedCustomerdeliveryPackable:boolean = false;
  private selectedCustomerdeliveryPacked:boolean = false;
  private selectedCustomerdeliveryShippable:boolean = false;
  private selectedCustomerdeliveryShippableManual:boolean = false;
  private selectedCustomerdeliveryDelivered:boolean = false;

  @ViewChild('orderTransitionConfirmationModalDialog')
  private orderTransitionConfirmationModalDialog:ModalComponent;
  private orderTransitionName:string = '';
  private orderTransitionNameOfflineNote:boolean;
  private orderTransitionNumber:string = '';
  private orderTransitionRequiresMessage:boolean;
  private orderTransitionMessage:string;
  private orderTransitionValid:boolean = false;


  @ViewChild('orderExportModalDialog')
  private orderExportModalDialog:ModalComponent;
  @ViewChild('orderExportConfirmationModalDialog')
  private orderExportConfirmationModalDialog:ModalComponent;
  private exportAction:string = 'PROCEED';

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _customerorderService:CustomerOrderService) {
    LogUtil.debug('AllCustomerOrdersComponent constructed');
    this.customerorders = this.newSearchResultInstance();
  }

  newSearchResultInstance():SearchResultVO<CustomerOrderInfoVO> {
    return {
      searchContext: {
        parameters: {
          filter: [],
          statuses: []
        },
        start: 0,
        size: Config.UI_TABLE_PAGE_SIZE,
        sortBy: null,
        sortDesc: false
      },
      items: [],
      total: 0
    };
  }

  ngOnInit() {
    LogUtil.debug('AllCustomerOrdersComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredCustomerorders();
    }, this.delayedFilteringMs);

  }

  get statuses():Pair<string, boolean>[] {
    return AllCustomerOrdersComponent._statuses;
  }

  set statuses(value:Pair<string, boolean>[]) {
    AllCustomerOrdersComponent._statuses = value;
  }

  get showGrossTotal():boolean {
    return Config.UI_ORDER_TOTALS === 'gross';
  }

  set showGrossTotal(showGrossTotal:boolean) {
    Config.UI_ORDER_TOTALS = showGrossTotal ? 'gross' : 'net';
    let cookieName = 'ADM_UI_ORDER_TOTALS';
    CookieUtil.createCookie(cookieName, Config.UI_ORDER_TOTALS, 360);
  }

  onShowGrossTotalClick() {
    let _gross = this.showGrossTotal;
    this.showGrossTotal = !_gross;
  }

  ngOnDestroy() {
    LogUtil.debug('AllCustomerOrdersComponent ngOnDestroy');
  }


  protected onFilterChange(event:any) {
    this.customerorders.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  protected onRefreshHandler() {
    LogUtil.debug('AllCustomerOrdersComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      if (this.customerorderEdit != null) {
        this.onRowEditCustomerorder(this.customerorderEdit);
      } else {
        this.getFilteredCustomerorders();
      }
    }
  }

  protected onPageSelected(page:number) {
    LogUtil.debug('AllCustomerOrdersComponent onPageSelected', page);
    this.customerorders.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  protected onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('AllCustomersComponent ononSortSelected', sort);
    if (sort == null) {
      this.customerorders.searchContext.sortBy = null;
      this.customerorders.searchContext.sortDesc = false;
    } else {
      this.customerorders.searchContext.sortBy = sort.first;
      this.customerorders.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  protected onCustomerorderSelected(data:CustomerOrderInfoVO) {
    LogUtil.debug('AllCustomerOrdersComponent onCustomerorderSelected', data);
    this.selectedCustomerorder = data;

    this.orderTransitionMessage = null;
    this.orderTransitionRequiresMessage = false;
    this.orderTransitionName = '';
    this.orderTransitionNameOfflineNote = false;
    this.orderTransitionValid = false;

    let selectedCustomerorderApprovable = false;
    let selectedCustomerorderCancellable = false;
    let selectedCustomerorderReturnable = false;
    let selectedCustomerorderRefundable = false;
    let selectedCustomerorderRefundableManual = false;

    if (this.selectedCustomerorder != null) {

      this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' ' + this.selectedCustomerorder.lastname;

      let options = data.orderStatusNextOptions;
      if (options != null && options.length > 0) {
        selectedCustomerorderApprovable = options.indexOf('approve.order') != -1;
        selectedCustomerorderCancellable = options.indexOf('cancel.order') != -1;
        selectedCustomerorderReturnable = options.indexOf('return.order') != -1;
        selectedCustomerorderRefundable = options.indexOf('cancel.order.refund') != -1;
        selectedCustomerorderRefundableManual = options.indexOf('cancel.order.manual.refund') != -1;
      }
    } else {

      this.orderTransitionNumber = '';

    }

    this.selectedCustomerorderApprovable = selectedCustomerorderApprovable;
    this.selectedCustomerorderCancellable = selectedCustomerorderCancellable;
    this.selectedCustomerorderReturnable = selectedCustomerorderReturnable;
    this.selectedCustomerorderRefundable = selectedCustomerorderRefundable;
    this.selectedCustomerorderRefundableManual = selectedCustomerorderRefundableManual;
  }


  protected onCustomerdeliverySelected(data:CustomerOrderDeliveryInfoVO) {
    LogUtil.debug('AllCustomerOrdersComponent onCustomerdeliverySelected', data);
    this.selectedCustomerdelivery = data;

    if (this.selectedCustomerdelivery) {

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = '';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;
      this.selectedCustomerorderApprovable = false;
      this.selectedCustomerorderCancellable = false;
      this.selectedCustomerorderReturnable = false;
      this.selectedCustomerorderRefundable = false;
      this.selectedCustomerorderRefundableManual = false;

      this.orderTransitionNumber = this.selectedCustomerorder.ordernum + ' ' + this.selectedCustomerorder.lastname + ' / ' + this.selectedCustomerdelivery.deliveryNum;

      let selectedCustomerdeliveryPackable:boolean = false;
      let selectedCustomerdeliveryPacked:boolean = false;
      let selectedCustomerdeliveryShippable:boolean = false;
      let selectedCustomerdeliveryShippableManual:boolean = false;
      let selectedCustomerdeliveryDelivered:boolean = false;

      let options = data.deliveryStatusNextOptions;
      if (options != null && options.length > 0) {
        selectedCustomerdeliveryPackable = options.indexOf('pack.delivery') != -1;
        selectedCustomerdeliveryPacked = options.indexOf('mark.ready.for.shipment') != -1;
        selectedCustomerdeliveryShippable = options.indexOf('start.shipment') != -1;
        selectedCustomerdeliveryShippableManual = options.indexOf('start.shipment.manual.payment') != -1;
        selectedCustomerdeliveryDelivered = options.indexOf('mark.shipped') != -1;
      }

      this.selectedCustomerdeliveryPackable = selectedCustomerdeliveryPackable;
      this.selectedCustomerdeliveryPacked = selectedCustomerdeliveryPacked;
      this.selectedCustomerdeliveryShippable = selectedCustomerdeliveryShippable;
      this.selectedCustomerdeliveryShippableManual = selectedCustomerdeliveryShippableManual;
      this.selectedCustomerdeliveryDelivered = selectedCustomerdeliveryDelivered;

    } else {

      this.onCustomerorderSelected(this.selectedCustomerorder);

      this.selectedCustomerdeliveryPackable = false;
      this.selectedCustomerdeliveryPacked = false;
      this.selectedCustomerdeliveryShippable = false;
      this.selectedCustomerdeliveryShippableManual = false;
      this.selectedCustomerdeliveryDelivered = false;

    }

  }

  protected onSearchId() {
    this.searchHelpShow = false;
    this.customerorderFilter = '*';
  }

  protected onSearchNumber() {
    this.searchHelpShow = false;
    this.customerorderFilter = '#';
  }

  protected onSearchCustomer() {
    this.searchHelpShow = false;
    this.customerorderFilter = '?';
  }

  protected onSearchAddress() {
    this.searchHelpShow = false;
    this.customerorderFilter = '@';
  }

  protected onSearchReserved() {
    this.searchHelpShow = false;
    this.customerorderFilter = '!';
  }

  protected onSearchInshop() {
    this.searchHelpShow = false;
    this.customerorderFilter = '^';
  }

  protected onSearchDate() {
    this.searchHelpShow = false;
    this.customerorderFilter = UiUtil.exampleDateSearch();
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusOpen() {
    //this.searchHelpShow = false;
    //this.customerorderFilter = '~~';
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      _st.second = AllCustomerOrdersComponent._open.includes(_st.first);
    });
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusPayment() {
    //this.searchHelpShow = false;
    //this.customerorderFilter = '$$';
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      _st.second = AllCustomerOrdersComponent._pendingPayments.includes(_st.first);
    });
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusCancelled() {
    //this.searchHelpShow = false;
    //this.customerorderFilter = '--';
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      _st.second = AllCustomerOrdersComponent._cancelled.includes(_st.first);
    });
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusCompleted() {
    //this.searchHelpShow = false;
    //this.customerorderFilter = '++';
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      _st.second = AllCustomerOrdersComponent._completed.includes(_st.first);
    });
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusAllOrders() {
    let _state:boolean = false;
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      if (_st.first.indexOf('os.') == 0) {
        _state = _state || _st.second;
      }
    });
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      if (_st.first.indexOf('os.') == 0) {
        _st.second = !_state;
      }
    });
    this.getFilteredCustomerorders();
  }

  protected onSearchStatusAllRFQ() {
    let _state:boolean = false;
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      if (_st.first.indexOf('qs.') == 0) {
        _state = _state || _st.second;
      }
    });
    this.statuses.forEach((_st:Pair<string, boolean>) => {
      if (_st.first.indexOf('qs.') == 0) {
        _st.second = !_state;
      }
    });
    this.getFilteredCustomerorders();
  }

  protected onSearchHelpToggle() {
    this.searchHelpShow = !this.searchHelpShow;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredCustomerorders();
  }

  protected onBackToList() {
    LogUtil.debug('AllCustomerOrdersComponent onBackToList handler');
    if (this.viewMode === AllCustomerOrdersComponent.CUSTOMERORDER) {
      this.customerorderEdit = null;
      this.onCustomerdeliverySelected(null);
      this.onCustomerorderSelected(this.selectedCustomerorder);
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
    }
  }


  protected onRowEditCustomerorder(row:CustomerOrderInfoVO) {
    LogUtil.debug('AllCustomerOrdersComponent onRowEditCustomerorder handler', row);

    this.loading = true;
    let lang = I18nEventBus.getI18nEventBus().current();
    let _sub:any = this._customerorderService.getOrderById(lang, row.customerorderId).subscribe( customerorder => {
      LogUtil.debug('AllCustomerOrdersComponent getOrderById', customerorder);

      this.customerorderEdit = customerorder;
      this.selectedCustomerorder = customerorder;

      this.changed = false;
      this.validForSave = false;
      this.loading = false;
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDER;

      _sub.unsubscribe();
    });

  }

  protected onRowEditSelected() {
    if (this.selectedCustomerorder != null) {
      this.onRowEditCustomerorder(this.selectedCustomerorder);
    }
  }

  protected onApproveSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onApproveSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'approve.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onCancelSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onCancelSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'cancel.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onReturnSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'return.order';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onRefundSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'cancel.order.refund';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onRefundManualSelected() {
    if (this.selectedCustomerorder != null) {
      LogUtil.debug('AllCustomerOrdersComponent onReturnSelected handler', this.selectedCustomerorder);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionName = 'cancel.order.manual.refund';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onTransitionMessageChange(event:any) {

    this.orderTransitionValid = this.orderTransitionMessage != null && /\S+.*\S+/.test(this.orderTransitionMessage);

  }

  protected onOrderTransitionConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedCustomerorder != null && this.selectedCustomerdelivery == null) {
        LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult', this.selectedCustomerorder);

        this.loading = true;
        let _sub:any = this._customerorderService.transitionOrder(
          this.selectedCustomerorder, this.orderTransitionName, this.orderTransitionMessage).subscribe((res:CustomerOrderTransitionResultVO) => {
            LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result', res);
            if (res.errorCode === '0') {

              let lang = I18nEventBus.getI18nEventBus().current();
              let _sub2:any = this._customerorderService.getOrderById(lang, this.selectedCustomerorder.customerorderId).subscribe( customerorder => {
                LogUtil.debug('AllCustomerOrdersComponent getOrderById', customerorder);

                if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
                  // We are editing
                  this.customerorderEdit = customerorder;
                }

                let idx = this.customerorders.items.findIndex(order => {
                  return order.customerorderId === customerorder.customerorderId;
                });

                this.selectedCustomerorder = this.customerorders.items[idx != -1 ? idx : 0];
                this.customerorders = this.customerorders; // hack to retrigger change
                this.onCustomerorderSelected(this.selectedCustomerorder);

                this.changed = false;
                this.validForSave = false;
                _sub2.unsubscribe();
              });

            } else {
              ErrorEventBus.getErrorEventBus().emit({ status: 500, message: res.errorCode, key: res.localizationKey, param: res.localizedMessageParameters });
            }
            this.loading = false;
            _sub.unsubscribe();
        });

      } else if (this.selectedCustomerorder != null && this.selectedCustomerdelivery != null) {
        LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult', this.selectedCustomerorder);

        this.loading = true;
        let _sub:any = this._customerorderService.transitionDelivery(
          this.selectedCustomerorder, this.selectedCustomerdelivery, this.orderTransitionName, this.orderTransitionMessage).subscribe((res:CustomerOrderTransitionResultVO) => {
            LogUtil.debug('AllCustomerOrdersComponent onOrderTransitionConfirmationResult result', res);
            if (res.errorCode === '0') {

              let lang = I18nEventBus.getI18nEventBus().current();
              let _sub2:any = this._customerorderService.getOrderById(lang, this.selectedCustomerorder.customerorderId).subscribe((customerorder:CustomerOrderVO) => {
                LogUtil.debug('AllCustomerOrdersComponent getOrderById', customerorder);

                if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
                  // We are editing
                  this.customerorderEdit = customerorder;
                }

                let idx = this.customerorders.items.findIndex(order => {
                  return order.customerorderId === customerorder.customerorderId;
                });

                this.selectedCustomerorder = this.customerorders.items[idx != -1 ? idx : 0];
                this.customerorders = this.customerorders; // hack to retrigger change
                this.onCustomerorderSelected(this.selectedCustomerorder);

                let delivery:CustomerOrderDeliveryInfoVO = null;
                if (this.selectedCustomerorder != null && this.selectedCustomerdelivery != null) {
                  LogUtil.debug('AllCustomerOrdersComponent trying to re-select delivery', this.selectedCustomerdelivery);
                  let idx2 = customerorder.deliveries.findIndex(delivery => {
                    return delivery.deliveryNum == this.selectedCustomerdelivery.deliveryNum;
                  });
                  if (idx2 != -1) {
                    delivery = customerorder.deliveries[idx2];
                    LogUtil.debug('AllCustomerOrdersComponent re-selected delivery', delivery);
                  }
                }
                this.onCustomerdeliverySelected(delivery);

                this.changed = false;
                this.validForSave = false;
                _sub2.unsubscribe();
              });

            } else {
              ErrorEventBus.getErrorEventBus().emit({ status: 500, message: res.errorCode, key: res.localizationKey, param: res.localizedMessageParameters });
            }
            this.loading = false;
            _sub.unsubscribe();
          });


      }
    }
  }



  protected onUpdateDeliveryRefSelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onUpdateDeliveryRefSelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = this.selectedCustomerdelivery.refNo;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionName = 'update.external.ref';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onPackDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onPackDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'pack.delivery';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onMarkReadyForShippingDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onMarkReadyForShippingDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'mark.ready.for.shipment';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onStartDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onStartDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'start.shipment';
      this.orderTransitionNameOfflineNote = false;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }

  protected onShipDeliveryManualSelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onShipDeliveryManualSelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = true;
      this.orderTransitionName = 'start.shipment.manual.payment';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = false;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }


  protected onMarkShippedDeliverySelected() {
    if (this.selectedCustomerdelivery != null) {
      LogUtil.debug('AllCustomerOrdersComponent onMarkShippedDeliverySelected handler', this.selectedCustomerdelivery);

      this.orderTransitionMessage = null;
      this.orderTransitionRequiresMessage = false;
      this.orderTransitionName = 'mark.shipped';
      this.orderTransitionNameOfflineNote = true;
      this.orderTransitionValid = true;

      this.orderTransitionConfirmationModalDialog.show();
    }
  }



  protected onExportSelected() {
    LogUtil.debug('AllCustomerOrdersComponent onExportSelected handler');
    this.orderExportModalDialog.show();
  }


  protected onExportSelectedOption(modalresult: ModalResult) {
    LogUtil.debug('AllCustomerOrdersComponent onExportSelectedOption handler');
    if (ModalAction.POSITIVE == modalresult.action) {

      this.orderExportConfirmationModalDialog.show();

    }
  }


  protected onExportSelectedConfirmed(modalresult: ModalResult) {
    LogUtil.debug('AllCustomerOrdersComponent onExportSelectedConfirmed handler');
    if (ModalAction.POSITIVE == modalresult.action) {

      let lang = I18nEventBus.getI18nEventBus().current();
      this.loading = true;
      let _sub:any = this._customerorderService.exportOrder(lang, this.selectedCustomerorder.customerorderId, this.exportAction == 'PROCEED').subscribe(exportedOrder => {
        LogUtil.debug('AllCustomerOrdersComponent exportOrder', exportedOrder);

        if (this.customerorderEdit != null && this.customerorderEdit.customerorderId == this.selectedCustomerorder.customerorderId) {
          // We are editing
          this.customerorderEdit = exportedOrder;
        }

        let idx = this.customerorders.items.findIndex(order => {
          return order.customerorderId === exportedOrder.customerorderId;
        });

        if (idx != -1) {
          this.customerorders.items[idx] = exportedOrder;
        } else {
          this.customerorders.items.splice(0, 0, exportedOrder);
          idx = 0;
        }
        this.selectedCustomerorder = this.customerorders.items[idx];
        this.customerorders = this.customerorders; // hack to retrigger change
        this.onCustomerorderSelected(this.selectedCustomerorder);

        _sub.unsubscribe();
        this.loading = false;
      });
    }
  }



  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.customerorderEdit != null) {

        LogUtil.debug('AllCustomerOrdersComponent Save handler customerorder', this.customerorderEdit);

      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('AllCustomerOrdersComponent discard handler');
    if (this.viewMode === AllCustomerOrdersComponent.CUSTOMERORDER) {
      if (this.selectedCustomerorder != null) {
        this.onRowEditSelected();
      }
    }
  }


  protected onClearFilter() {

    this.customerorderFilter = '';
    this.getFilteredCustomerorders();

  }


  private getFilteredCustomerorders() {
    this.customerorderFilterRequired = !this.forceShowAll && (this.customerorderFilter == null || this.customerorderFilter.length < 2);

    LogUtil.debug('AllCustomerOrdersComponent getFilteredCustomerorders' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.customerorderFilterRequired) {
      this.loading = true;
      let lang = I18nEventBus.getI18nEventBus().current();

      let sts:string[] = [];
      AllCustomerOrdersComponent._statuses.forEach((_st:Pair<string, boolean>) => {
        if (_st.second) {
          sts.push(_st.first);
        }
      });

      this.customerorders.searchContext.parameters.filter = [ this.customerorderFilter ];
      this.customerorders.searchContext.parameters.statuses = sts;
      this.customerorders.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      let _sub:any = this._customerorderService.getFilteredOrders(lang, this.customerorders.searchContext).subscribe( allcustomerorders => {
        LogUtil.debug('AllCustomerOrdersComponent getFilteredCustomerorders', allcustomerorders);
        this.customerorders = allcustomerorders;
        this.selectedCustomerorder = null;
        this.customerorderEdit = null;
        this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.customerorders = this.newSearchResultInstance();
      this.selectedCustomerorder = null;
      this.customerorderEdit = null;
      this.viewMode = AllCustomerOrdersComponent.CUSTOMERORDERS;
      this.changed = false;
      this.validForSave = false;
    }
  }

}
