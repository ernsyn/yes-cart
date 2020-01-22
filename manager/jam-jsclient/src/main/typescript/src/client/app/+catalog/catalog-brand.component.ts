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
import { CatalogService, UserEventBus, Util } from './../shared/services/index';
import { ModalComponent, ModalResult, ModalAction } from './../shared/modal/index';
import { BrandVO, AttrValueBrandVO, Pair, SearchResultVO } from './../shared/model/index';
import { FormValidationEvent, Futures, Future } from './../shared/event/index';
import { Config } from './../shared/config/env.config';
import { LogUtil } from './../shared/log/index';

@Component({
  selector: 'yc-catalog-brands',
  moduleId: module.id,
  templateUrl: 'catalog-brand.component.html',
})

export class CatalogBrandComponent implements OnInit, OnDestroy {

  private static BRANDS:string = 'brands';
  private static BRAND:string = 'brand';

  private forceShowAll:boolean = false;
  private viewMode:string = CatalogBrandComponent.BRANDS;

  private brands:SearchResultVO<BrandVO>;
  private brandFilter:string;
  private brandFilterRequired:boolean = true;

  private delayedFiltering:Future;
  private delayedFilteringMs:number = Config.UI_INPUT_DELAY;

  private selectedBrand:BrandVO;

  private brandEdit:BrandVO;
  private brandEditAttributes:AttrValueBrandVO[] = [];
  private brandAttributesUpdate:Array<Pair<AttrValueBrandVO, boolean>>;

  @ViewChild('deleteConfirmationModalDialog')
  private deleteConfirmationModalDialog:ModalComponent;

  private deleteValue:String;

  private loading:boolean = false;

  private changed:boolean = false;
  private validForSave:boolean = false;

  constructor(private _brandService:CatalogService) {
    LogUtil.debug('CatalogBrandComponent constructed');
    this.brands = this.newSearchResultInstance();
  }

  newBrandInstance():BrandVO {
    return { brandId: 0, name: '', description: null};
  }

  newSearchResultInstance():SearchResultVO<BrandVO> {
    return {
      searchContext: {
        parameters: {
          filter: []
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
    LogUtil.debug('CatalogBrandComponent ngOnInit');
    this.onRefreshHandler();
    let that = this;
    this.delayedFiltering = Futures.perpetual(function() {
      that.getFilteredBrands();
    }, this.delayedFilteringMs);

  }

  ngOnDestroy() {
    LogUtil.debug('CatalogBrandComponent ngOnDestroy');
  }


  protected onFilterChange(event:any) {
    this.brands.searchContext.start = 0; // changing filter means we need to start from first page
    this.delayedFiltering.delay();
  }

  protected onRefreshHandler() {
    LogUtil.debug('CatalogBrandComponent refresh handler');
    if (UserEventBus.getUserEventBus().current() != null) {
      this.getFilteredBrands();
    }
  }

  protected onPageSelected(page:number) {
    LogUtil.debug('CatalogBrandComponent onPageSelected', page);
    this.brands.searchContext.start = page;
    this.delayedFiltering.delay();
  }

  protected onSortSelected(sort:Pair<string, boolean>) {
    LogUtil.debug('CatalogBrandComponent ononSortSelected', sort);
    if (sort == null) {
      this.brands.searchContext.sortBy = null;
      this.brands.searchContext.sortDesc = false;
    } else {
      this.brands.searchContext.sortBy = sort.first;
      this.brands.searchContext.sortDesc = sort.second;
    }
    this.delayedFiltering.delay();
  }

  protected onBrandSelected(data:BrandVO) {
    LogUtil.debug('CatalogBrandComponent onBrandSelected', data);
    this.selectedBrand = data;
  }

  protected onBrandChanged(event:FormValidationEvent<Pair<BrandVO, Array<Pair<AttrValueBrandVO, boolean>>>>) {
    LogUtil.debug('CatalogBrandComponent onBrandChanged', event);
    this.changed = true;
    this.validForSave = event.valid;
    this.brandEdit = event.source.first;
    this.brandAttributesUpdate = event.source.second;
  }

  protected onForceShowAll() {
    this.forceShowAll = !this.forceShowAll;
    this.getFilteredBrands();
  }

  protected onBackToList() {
    LogUtil.debug('CatalogBrandComponent onBackToList handler');
    if (this.viewMode === CatalogBrandComponent.BRAND) {
      this.brandEdit = null;
      this.viewMode = CatalogBrandComponent.BRANDS;
    }
  }

  protected onRowNew() {
    LogUtil.debug('CatalogBrandComponent onRowNew handler');
    this.changed = false;
    this.validForSave = false;
    if (this.viewMode === CatalogBrandComponent.BRANDS) {
      this.brandEdit = this.newBrandInstance();
      this.brandEditAttributes = [];
      this.viewMode = CatalogBrandComponent.BRAND;
    }
  }

  protected onRowDelete(row:any) {
    LogUtil.debug('CatalogBrandComponent onRowDelete handler', row);
    this.deleteValue = row.name;
    this.deleteConfirmationModalDialog.show();
  }

  protected onRowDeleteSelected() {
    if (this.selectedBrand != null) {
      this.onRowDelete(this.selectedBrand);
    }
  }


  protected onRowEditBrand(row:BrandVO) {
    LogUtil.debug('CatalogBrandComponent onRowEditBrand handler', row);
    this.brandEdit = Util.clone(row);
    this.brandEditAttributes = [];
    this.changed = false;
    this.validForSave = false;
    this.viewMode = CatalogBrandComponent.BRAND;
    if (this.brandEdit.brandId > 0) {
      this.loading = true;
      let _sub:any = this._brandService.getBrandAttributes(this.brandEdit.brandId).subscribe(attrs => {
        this.brandEditAttributes = attrs;
        this.loading = false;
        _sub.unsubscribe();
      });
    }
  }

  protected onRowEditSelected() {
    if (this.selectedBrand != null) {
      this.onRowEditBrand(this.selectedBrand);
    }
  }

  protected onSaveHandler() {

    if (this.validForSave && this.changed) {

      if (this.brandEdit != null) {

        LogUtil.debug('CatalogBrandComponent Save handler brand', this.brandEdit);

        this.loading = true;
        let _sub:any = this._brandService.saveBrand(this.brandEdit).subscribe(
            rez => {
              _sub.unsubscribe();
              let pk = this.brandEdit.brandId;
              LogUtil.debug('CatalogBrandComponent brand changed', rez);
              this.changed = false;
              this.selectedBrand = rez;
              this.brandEdit = null;
              this.loading = false;
              this.viewMode = CatalogBrandComponent.BRANDS;

              if (pk > 0 && this.brandAttributesUpdate != null && this.brandAttributesUpdate.length > 0) {

                this.loading = true;
                let _sub2:any = this._brandService.saveBrandAttributes(this.brandAttributesUpdate).subscribe(rez => {
                  _sub2.unsubscribe();
                  LogUtil.debug('CatalogBrandComponent brand attributes updated', rez);
                  this.brandAttributesUpdate = null;
                  this.loading = false;
                  this.getFilteredBrands();
                });
              } else {
                if (this.brandFilter == null || this.brandFilter == '') {
                  this.brandFilter = rez.name;
                }
                this.getFilteredBrands();
              }
          }
        );
      }

    }

  }

  protected onDiscardEventHandler() {
    LogUtil.debug('CatalogBrandComponent discard handler');
    if (this.viewMode === CatalogBrandComponent.BRAND) {
      if (this.selectedBrand != null) {
        this.onRowEditSelected();
      } else {
        this.onRowNew();
      }
    }
  }

  protected onDeleteConfirmationResult(modalresult: ModalResult) {
    LogUtil.debug('CatalogBrandComponent onDeleteConfirmationResult modal result is ', modalresult);
    if (ModalAction.POSITIVE === modalresult.action) {

      if (this.selectedBrand != null) {
        LogUtil.debug('CatalogBrandComponent onDeleteConfirmationResult', this.selectedBrand);

        this.loading = true;
        let _sub:any = this._brandService.removeBrand(this.selectedBrand).subscribe(res => {
          _sub.unsubscribe();
          LogUtil.debug('CatalogBrandComponent removeBrand', this.selectedBrand);
          this.selectedBrand = null;
          this.brandEdit = null;
          this.loading = false;
          this.getFilteredBrands();
        });
      }
    }
  }

  protected onClearFilter() {

    this.brandFilter = '';
    this.getFilteredBrands();

  }

  private getFilteredBrands() {
    this.brandFilterRequired = !this.forceShowAll && (this.brandFilter == null || this.brandFilter.length < 2);

    LogUtil.debug('CatalogBrandComponent getFilteredBrands' + (this.forceShowAll ? ' forcefully': ''));

    if (!this.brandFilterRequired) {
      this.loading = true;

      this.brands.searchContext.parameters.filter = [ this.brandFilter ];
      this.brands.searchContext.size = Config.UI_TABLE_PAGE_SIZE;

      let _sub:any = this._brandService.getFilteredBrands(this.brands.searchContext).subscribe( allbrands => {
        LogUtil.debug('CatalogBrandComponent getFilteredBrands', allbrands);
        this.brands = allbrands;
        this.selectedBrand = null;
        this.brandEdit = null;
        this.viewMode = CatalogBrandComponent.BRANDS;
        this.changed = false;
        this.validForSave = false;
        this.loading = false;
        _sub.unsubscribe();
      });
    } else {
      this.brands = this.newSearchResultInstance();
      this.selectedBrand = null;
      this.brandEdit = null;
      this.brandEditAttributes = null;
      this.viewMode = CatalogBrandComponent.BRANDS;
      this.changed = false;
      this.validForSave = false;
    }
  }

}
