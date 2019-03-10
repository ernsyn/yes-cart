
package org.yes.cart.bulkimport.xml.internal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.yes.cart.bulkimport.xml.internal package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TaxConfigs_QNAME = new QName("", "tax-configs");
    private final static QName _Country_QNAME = new QName("", "country");
    private final static QName _ShippingProviders_QNAME = new QName("", "shipping-providers");
    private final static QName _PromotionCoupons_QNAME = new QName("", "promotion-coupons");
    private final static QName _PriceRule_QNAME = new QName("", "price-rule");
    private final static QName _Taxes_QNAME = new QName("", "taxes");
    private final static QName _ShippingMethods_QNAME = new QName("", "shipping-methods");
    private final static QName _DataGroup_QNAME = new QName("", "data-group");
    private final static QName _Price_QNAME = new QName("", "price");
    private final static QName _Stock_QNAME = new QName("", "stock");
    private final static QName _Sku_QNAME = new QName("", "sku");
    private final static QName _Brand_QNAME = new QName("", "brand");
    private final static QName _PriceList_QNAME = new QName("", "price-list");
    private final static QName _EType_QNAME = new QName("", "e-type");
    private final static QName _Tax_QNAME = new QName("", "tax");
    private final static QName _ETypes_QNAME = new QName("", "e-types");
    private final static QName _Countries_QNAME = new QName("", "countries");
    private final static QName _CountryState_QNAME = new QName("", "country-state");
    private final static QName _System_QNAME = new QName("", "system");
    private final static QName _AttributeGroups_QNAME = new QName("", "attribute-groups");
    private final static QName _ProductsLinks_QNAME = new QName("", "products-links");
    private final static QName _ProductLinks_QNAME = new QName("", "product-links");
    private final static QName _ProductTypes_QNAME = new QName("", "product-types");
    private final static QName _Skus_QNAME = new QName("", "skus");
    private final static QName _OrganisationUsers_QNAME = new QName("", "organisation-users");
    private final static QName _Cms_QNAME = new QName("", "cms");
    private final static QName _Inventory_QNAME = new QName("", "inventory");
    private final static QName _Content_QNAME = new QName("", "content");
    private final static QName _Products_QNAME = new QName("", "products");
    private final static QName _FulfilmentCentre_QNAME = new QName("", "fulfilment-centre");
    private final static QName _ProductCategories_QNAME = new QName("", "product-categories");
    private final static QName _FulfilmentCentres_QNAME = new QName("", "fulfilment-centres");
    private final static QName _Systems_QNAME = new QName("", "systems");
    private final static QName _AttributeGroup_QNAME = new QName("", "attribute-group");
    private final static QName _Attribute_QNAME = new QName("", "attribute");
    private final static QName _Categories_QNAME = new QName("", "categories");
    private final static QName _TaxConfig_QNAME = new QName("", "tax-config");
    private final static QName _ProductType_QNAME = new QName("", "product-type");
    private final static QName _OrganisationUser_QNAME = new QName("", "organisation-user");
    private final static QName _Product_QNAME = new QName("", "product");
    private final static QName _Brands_QNAME = new QName("", "brands");
    private final static QName _DataGroups_QNAME = new QName("", "data-groups");
    private final static QName _CountryStates_QNAME = new QName("", "country-states");
    private final static QName _ShippingMethod_QNAME = new QName("", "shipping-method");
    private final static QName _Promotions_QNAME = new QName("", "promotions");
    private final static QName _ProductsCategories_QNAME = new QName("", "products-categories");
    private final static QName _DataDescriptors_QNAME = new QName("", "data-descriptors");
    private final static QName _ShippingProvider_QNAME = new QName("", "shipping-provider");
    private final static QName _PriceRules_QNAME = new QName("", "price-rules");
    private final static QName _DataDescriptor_QNAME = new QName("", "data-descriptor");
    private final static QName _Attributes_QNAME = new QName("", "attributes");
    private final static QName _PromotionCoupon_QNAME = new QName("", "promotion-coupon");
    private final static QName _Category_QNAME = new QName("", "category");
    private final static QName _Promotion_QNAME = new QName("", "promotion");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.yes.cart.bulkimport.xml.internal
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NavigationByPriceTiersType }
     * 
     */
    public NavigationByPriceTiersType createNavigationByPriceTiersType() {
        return new NavigationByPriceTiersType();
    }

    /**
     * Create an instance of {@link ProductTypeAttributeNavigationRangeListType }
     * 
     */
    public ProductTypeAttributeNavigationRangeListType createProductTypeAttributeNavigationRangeListType() {
        return new ProductTypeAttributeNavigationRangeListType();
    }

    /**
     * Create an instance of {@link PromotionType }
     * 
     */
    public PromotionType createPromotionType() {
        return new PromotionType();
    }

    /**
     * Create an instance of {@link CategoryType }
     * 
     */
    public CategoryType createCategoryType() {
        return new CategoryType();
    }

    /**
     * Create an instance of {@link ProductType }
     * 
     */
    public ProductType createProductType() {
        return new ProductType();
    }

    /**
     * Create an instance of {@link ContentType }
     * 
     */
    public ContentType createContentType() {
        return new ContentType();
    }

    /**
     * Create an instance of {@link SkuType }
     * 
     */
    public SkuType createSkuType() {
        return new SkuType();
    }

    /**
     * Create an instance of {@link PriceType }
     * 
     */
    public PriceType createPriceType() {
        return new PriceType();
    }

    /**
     * Create an instance of {@link PriceRuleType }
     * 
     */
    public PriceRuleType createPriceRuleType() {
        return new PriceRuleType();
    }

    /**
     * Create an instance of {@link TaxConfigsType }
     * 
     */
    public TaxConfigsType createTaxConfigsType() {
        return new TaxConfigsType();
    }

    /**
     * Create an instance of {@link CountryType }
     * 
     */
    public CountryType createCountryType() {
        return new CountryType();
    }

    /**
     * Create an instance of {@link ShippingProvidersType }
     * 
     */
    public ShippingProvidersType createShippingProvidersType() {
        return new ShippingProvidersType();
    }

    /**
     * Create an instance of {@link PromotionCouponsType }
     * 
     */
    public PromotionCouponsType createPromotionCouponsType() {
        return new PromotionCouponsType();
    }

    /**
     * Create an instance of {@link TaxesType }
     * 
     */
    public TaxesType createTaxesType() {
        return new TaxesType();
    }

    /**
     * Create an instance of {@link ShippingMethodsType }
     * 
     */
    public ShippingMethodsType createShippingMethodsType() {
        return new ShippingMethodsType();
    }

    /**
     * Create an instance of {@link DataGroupType }
     * 
     */
    public DataGroupType createDataGroupType() {
        return new DataGroupType();
    }

    /**
     * Create an instance of {@link StockType }
     * 
     */
    public StockType createStockType() {
        return new StockType();
    }

    /**
     * Create an instance of {@link BrandType }
     * 
     */
    public BrandType createBrandType() {
        return new BrandType();
    }

    /**
     * Create an instance of {@link PriceListType }
     * 
     */
    public PriceListType createPriceListType() {
        return new PriceListType();
    }

    /**
     * Create an instance of {@link ETypeType }
     * 
     */
    public ETypeType createETypeType() {
        return new ETypeType();
    }

    /**
     * Create an instance of {@link TaxType }
     * 
     */
    public TaxType createTaxType() {
        return new TaxType();
    }

    /**
     * Create an instance of {@link ETypesType }
     * 
     */
    public ETypesType createETypesType() {
        return new ETypesType();
    }

    /**
     * Create an instance of {@link CountriesType }
     * 
     */
    public CountriesType createCountriesType() {
        return new CountriesType();
    }

    /**
     * Create an instance of {@link CountryStateType }
     * 
     */
    public CountryStateType createCountryStateType() {
        return new CountryStateType();
    }

    /**
     * Create an instance of {@link SystemType }
     * 
     */
    public SystemType createSystemType() {
        return new SystemType();
    }

    /**
     * Create an instance of {@link AttributeGroupsType }
     * 
     */
    public AttributeGroupsType createAttributeGroupsType() {
        return new AttributeGroupsType();
    }

    /**
     * Create an instance of {@link ProductsLinksCodeType }
     * 
     */
    public ProductsLinksCodeType createProductsLinksCodeType() {
        return new ProductsLinksCodeType();
    }

    /**
     * Create an instance of {@link ProductLinksCodeType }
     * 
     */
    public ProductLinksCodeType createProductLinksCodeType() {
        return new ProductLinksCodeType();
    }

    /**
     * Create an instance of {@link ProductTypesType }
     * 
     */
    public ProductTypesType createProductTypesType() {
        return new ProductTypesType();
    }

    /**
     * Create an instance of {@link SkusType }
     * 
     */
    public SkusType createSkusType() {
        return new SkusType();
    }

    /**
     * Create an instance of {@link OrganisationUsersType }
     * 
     */
    public OrganisationUsersType createOrganisationUsersType() {
        return new OrganisationUsersType();
    }

    /**
     * Create an instance of {@link CmsType }
     * 
     */
    public CmsType createCmsType() {
        return new CmsType();
    }

    /**
     * Create an instance of {@link InventoryType }
     * 
     */
    public InventoryType createInventoryType() {
        return new InventoryType();
    }

    /**
     * Create an instance of {@link ProductsType }
     * 
     */
    public ProductsType createProductsType() {
        return new ProductsType();
    }

    /**
     * Create an instance of {@link FulfilmentCentreType }
     * 
     */
    public FulfilmentCentreType createFulfilmentCentreType() {
        return new FulfilmentCentreType();
    }

    /**
     * Create an instance of {@link ProductCategoriesCodeType }
     * 
     */
    public ProductCategoriesCodeType createProductCategoriesCodeType() {
        return new ProductCategoriesCodeType();
    }

    /**
     * Create an instance of {@link FulfilmentCentresType }
     * 
     */
    public FulfilmentCentresType createFulfilmentCentresType() {
        return new FulfilmentCentresType();
    }

    /**
     * Create an instance of {@link SystemsType }
     * 
     */
    public SystemsType createSystemsType() {
        return new SystemsType();
    }

    /**
     * Create an instance of {@link AttributeGroupType }
     * 
     */
    public AttributeGroupType createAttributeGroupType() {
        return new AttributeGroupType();
    }

    /**
     * Create an instance of {@link AttributeType }
     * 
     */
    public AttributeType createAttributeType() {
        return new AttributeType();
    }

    /**
     * Create an instance of {@link CategoriesType }
     * 
     */
    public CategoriesType createCategoriesType() {
        return new CategoriesType();
    }

    /**
     * Create an instance of {@link TaxConfigType }
     * 
     */
    public TaxConfigType createTaxConfigType() {
        return new TaxConfigType();
    }

    /**
     * Create an instance of {@link ProductTypeTypeType }
     * 
     */
    public ProductTypeTypeType createProductTypeTypeType() {
        return new ProductTypeTypeType();
    }

    /**
     * Create an instance of {@link OrganisationUserType }
     * 
     */
    public OrganisationUserType createOrganisationUserType() {
        return new OrganisationUserType();
    }

    /**
     * Create an instance of {@link BrandsType }
     * 
     */
    public BrandsType createBrandsType() {
        return new BrandsType();
    }

    /**
     * Create an instance of {@link DataGroupsType }
     * 
     */
    public DataGroupsType createDataGroupsType() {
        return new DataGroupsType();
    }

    /**
     * Create an instance of {@link CountryStatesType }
     * 
     */
    public CountryStatesType createCountryStatesType() {
        return new CountryStatesType();
    }

    /**
     * Create an instance of {@link ShippingMethodType }
     * 
     */
    public ShippingMethodType createShippingMethodType() {
        return new ShippingMethodType();
    }

    /**
     * Create an instance of {@link PromotionsType }
     * 
     */
    public PromotionsType createPromotionsType() {
        return new PromotionsType();
    }

    /**
     * Create an instance of {@link ProductsCategoriesCodeType }
     * 
     */
    public ProductsCategoriesCodeType createProductsCategoriesCodeType() {
        return new ProductsCategoriesCodeType();
    }

    /**
     * Create an instance of {@link DataDescriptorsType }
     * 
     */
    public DataDescriptorsType createDataDescriptorsType() {
        return new DataDescriptorsType();
    }

    /**
     * Create an instance of {@link ShippingProviderType }
     * 
     */
    public ShippingProviderType createShippingProviderType() {
        return new ShippingProviderType();
    }

    /**
     * Create an instance of {@link PriceRulesType }
     * 
     */
    public PriceRulesType createPriceRulesType() {
        return new PriceRulesType();
    }

    /**
     * Create an instance of {@link DataDescriptorType }
     * 
     */
    public DataDescriptorType createDataDescriptorType() {
        return new DataDescriptorType();
    }

    /**
     * Create an instance of {@link AttributesType }
     * 
     */
    public AttributesType createAttributesType() {
        return new AttributesType();
    }

    /**
     * Create an instance of {@link PromotionCouponType }
     * 
     */
    public PromotionCouponType createPromotionCouponType() {
        return new PromotionCouponType();
    }

    /**
     * Create an instance of {@link QuantityType }
     * 
     */
    public QuantityType createQuantityType() {
        return new QuantityType();
    }

    /**
     * Create an instance of {@link NavigationByPriceType }
     * 
     */
    public NavigationByPriceType createNavigationByPriceType() {
        return new NavigationByPriceType();
    }

    /**
     * Create an instance of {@link ShippingMethodExclusionsCustomerTypesType }
     * 
     */
    public ShippingMethodExclusionsCustomerTypesType createShippingMethodExclusionsCustomerTypesType() {
        return new ShippingMethodExclusionsCustomerTypesType();
    }

    /**
     * Create an instance of {@link OrganisationUserRoleType }
     * 
     */
    public OrganisationUserRoleType createOrganisationUserRoleType() {
        return new OrganisationUserRoleType();
    }

    /**
     * Create an instance of {@link ShippingProviderShippingMethodsType }
     * 
     */
    public ShippingProviderShippingMethodsType createShippingProviderShippingMethodsType() {
        return new ShippingProviderShippingMethodsType();
    }

    /**
     * Create an instance of {@link ProductSkuType }
     * 
     */
    public ProductSkuType createProductSkuType() {
        return new ProductSkuType();
    }

    /**
     * Create an instance of {@link RateType }
     * 
     */
    public RateType createRateType() {
        return new RateType();
    }

    /**
     * Create an instance of {@link NavigationByAttributesType }
     * 
     */
    public NavigationByAttributesType createNavigationByAttributesType() {
        return new NavigationByAttributesType();
    }

    /**
     * Create an instance of {@link NavigationProductTypeType }
     * 
     */
    public NavigationProductTypeType createNavigationProductTypeType() {
        return new NavigationProductTypeType();
    }

    /**
     * Create an instance of {@link FulfilmentCentreConfigurationType }
     * 
     */
    public FulfilmentCentreConfigurationType createFulfilmentCentreConfigurationType() {
        return new FulfilmentCentreConfigurationType();
    }

    /**
     * Create an instance of {@link ShippingMethodExclusionsDatesType }
     * 
     */
    public ShippingMethodExclusionsDatesType createShippingMethodExclusionsDatesType() {
        return new ShippingMethodExclusionsDatesType();
    }

    /**
     * Create an instance of {@link ProductTypeType }
     * 
     */
    public ProductTypeType createProductTypeType() {
        return new ProductTypeType();
    }

    /**
     * Create an instance of {@link CustomAttributesType }
     * 
     */
    public CustomAttributesType createCustomAttributesType() {
        return new CustomAttributesType();
    }

    /**
     * Create an instance of {@link BodyContentType }
     * 
     */
    public BodyContentType createBodyContentType() {
        return new BodyContentType();
    }

    /**
     * Create an instance of {@link SeoType }
     * 
     */
    public SeoType createSeoType() {
        return new SeoType();
    }

    /**
     * Create an instance of {@link ShippingMethodSupportedType }
     * 
     */
    public ShippingMethodSupportedType createShippingMethodSupportedType() {
        return new ShippingMethodSupportedType();
    }

    /**
     * Create an instance of {@link OrganisationUserShopsType }
     * 
     */
    public OrganisationUserShopsType createOrganisationUserShopsType() {
        return new OrganisationUserShopsType();
    }

    /**
     * Create an instance of {@link BaseCategoryType }
     * 
     */
    public BaseCategoryType createBaseCategoryType() {
        return new BaseCategoryType();
    }

    /**
     * Create an instance of {@link TaxRegionType }
     * 
     */
    public TaxRegionType createTaxRegionType() {
        return new TaxRegionType();
    }

    /**
     * Create an instance of {@link I18NType }
     * 
     */
    public I18NType createI18NType() {
        return new I18NType();
    }

    /**
     * Create an instance of {@link ShippingMethodExclusionsWeekdaysType }
     * 
     */
    public ShippingMethodExclusionsWeekdaysType createShippingMethodExclusionsWeekdaysType() {
        return new ShippingMethodExclusionsWeekdaysType();
    }

    /**
     * Create an instance of {@link ProductLinksType }
     * 
     */
    public ProductLinksType createProductLinksType() {
        return new ProductLinksType();
    }

    /**
     * Create an instance of {@link OrganisationUserRolesType }
     * 
     */
    public OrganisationUserRolesType createOrganisationUserRolesType() {
        return new OrganisationUserRolesType();
    }

    /**
     * Create an instance of {@link ProductCategoriesType }
     * 
     */
    public ProductCategoriesType createProductCategoriesType() {
        return new ProductCategoriesType();
    }

    /**
     * Create an instance of {@link ShippingMethodSupportedFulfilmentCentresType }
     * 
     */
    public ShippingMethodSupportedFulfilmentCentresType createShippingMethodSupportedFulfilmentCentresType() {
        return new ShippingMethodSupportedFulfilmentCentresType();
    }

    /**
     * Create an instance of {@link I18NsType }
     * 
     */
    public I18NsType createI18NsType() {
        return new I18NsType();
    }

    /**
     * Create an instance of {@link OrganisationUserShopType }
     * 
     */
    public OrganisationUserShopType createOrganisationUserShopType() {
        return new OrganisationUserShopType();
    }

    /**
     * Create an instance of {@link NavigationType }
     * 
     */
    public NavigationType createNavigationType() {
        return new NavigationType();
    }

    /**
     * Create an instance of {@link PromotionCouponConfigurationType }
     * 
     */
    public PromotionCouponConfigurationType createPromotionCouponConfigurationType() {
        return new PromotionCouponConfigurationType();
    }

    /**
     * Create an instance of {@link ProductTypeGroupType }
     * 
     */
    public ProductTypeGroupType createProductTypeGroupType() {
        return new ProductTypeGroupType();
    }

    /**
     * Create an instance of {@link ShippingMethodExclusionsType }
     * 
     */
    public ShippingMethodExclusionsType createShippingMethodExclusionsType() {
        return new ShippingMethodExclusionsType();
    }

    /**
     * Create an instance of {@link PromotionConfigurationType }
     * 
     */
    public PromotionConfigurationType createPromotionConfigurationType() {
        return new PromotionConfigurationType();
    }

    /**
     * Create an instance of {@link NavigationByPriceTiersCurrencyTiersType }
     * 
     */
    public NavigationByPriceTiersCurrencyTiersType createNavigationByPriceTiersCurrencyTiersType() {
        return new NavigationByPriceTiersCurrencyTiersType();
    }

    /**
     * Create an instance of {@link ContentBodyType }
     * 
     */
    public ContentBodyType createContentBodyType() {
        return new ContentBodyType();
    }

    /**
     * Create an instance of {@link ProductTypeConfigurationType }
     * 
     */
    public ProductTypeConfigurationType createProductTypeConfigurationType() {
        return new ProductTypeConfigurationType();
    }

    /**
     * Create an instance of {@link ProductTypeGroupAttributesType }
     * 
     */
    public ProductTypeGroupAttributesType createProductTypeGroupAttributesType() {
        return new ProductTypeGroupAttributesType();
    }

    /**
     * Create an instance of {@link ContentTemplatesType }
     * 
     */
    public ContentTemplatesType createContentTemplatesType() {
        return new ContentTemplatesType();
    }

    /**
     * Create an instance of {@link ProductTypeAttributeNavigationType }
     * 
     */
    public ProductTypeAttributeNavigationType createProductTypeAttributeNavigationType() {
        return new ProductTypeAttributeNavigationType();
    }

    /**
     * Create an instance of {@link ShippingMethodExclusionsDatesDateType }
     * 
     */
    public ShippingMethodExclusionsDatesDateType createShippingMethodExclusionsDatesDateType() {
        return new ShippingMethodExclusionsDatesDateType();
    }

    /**
     * Create an instance of {@link ProductLinkType }
     * 
     */
    public ProductLinkType createProductLinkType() {
        return new ProductLinkType();
    }

    /**
     * Create an instance of {@link OrganisationUserContactDetailsType }
     * 
     */
    public OrganisationUserContactDetailsType createOrganisationUserContactDetailsType() {
        return new OrganisationUserContactDetailsType();
    }

    /**
     * Create an instance of {@link ProductTypeAttributeNavigationRangeListRangeDisplayValuesType }
     * 
     */
    public ProductTypeAttributeNavigationRangeListRangeDisplayValuesType createProductTypeAttributeNavigationRangeListRangeDisplayValuesType() {
        return new ProductTypeAttributeNavigationRangeListRangeDisplayValuesType();
    }

    /**
     * Create an instance of {@link CustomAttributeType }
     * 
     */
    public CustomAttributeType createCustomAttributeType() {
        return new CustomAttributeType();
    }

    /**
     * Create an instance of {@link ShippingMethodSupportedPaymentGatewaysType }
     * 
     */
    public ShippingMethodSupportedPaymentGatewaysType createShippingMethodSupportedPaymentGatewaysType() {
        return new ShippingMethodSupportedPaymentGatewaysType();
    }

    /**
     * Create an instance of {@link ShippingProviderConfigurationType }
     * 
     */
    public ShippingProviderConfigurationType createShippingProviderConfigurationType() {
        return new ShippingProviderConfigurationType();
    }

    /**
     * Create an instance of {@link FulfilmentCentreLocationType }
     * 
     */
    public FulfilmentCentreLocationType createFulfilmentCentreLocationType() {
        return new FulfilmentCentreLocationType();
    }

    /**
     * Create an instance of {@link NavigationByPriceTiersCurrencyType }
     * 
     */
    public NavigationByPriceTiersCurrencyType createNavigationByPriceTiersCurrencyType() {
        return new NavigationByPriceTiersCurrencyType();
    }

    /**
     * Create an instance of {@link ProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType }
     * 
     */
    public ProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType createProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType() {
        return new ProductTypeAttributeNavigationRangeListRangeDisplayValuesValueType();
    }

    /**
     * Create an instance of {@link OrganisationUserCredentialsType }
     * 
     */
    public OrganisationUserCredentialsType createOrganisationUserCredentialsType() {
        return new OrganisationUserCredentialsType();
    }

    /**
     * Create an instance of {@link ProductTypeGroupsType }
     * 
     */
    public ProductTypeGroupsType createProductTypeGroupsType() {
        return new ProductTypeGroupsType();
    }

    /**
     * Create an instance of {@link ProductCategoryType }
     * 
     */
    public ProductCategoryType createProductCategoryType() {
        return new ProductCategoryType();
    }

    /**
     * Create an instance of {@link OrganisationUserPreferencesType }
     * 
     */
    public OrganisationUserPreferencesType createOrganisationUserPreferencesType() {
        return new OrganisationUserPreferencesType();
    }

    /**
     * Create an instance of {@link DataGroupDescriptorsType }
     * 
     */
    public DataGroupDescriptorsType createDataGroupDescriptorsType() {
        return new DataGroupDescriptorsType();
    }

    /**
     * Create an instance of {@link ProductTypeAttributeType }
     * 
     */
    public ProductTypeAttributeType createProductTypeAttributeType() {
        return new ProductTypeAttributeType();
    }

    /**
     * Create an instance of {@link ProductTypeAttributeNavigationRangeListRangeType }
     * 
     */
    public ProductTypeAttributeNavigationRangeListRangeType createProductTypeAttributeNavigationRangeListRangeType() {
        return new ProductTypeAttributeNavigationRangeListRangeType();
    }

    /**
     * Create an instance of {@link ProductTypeTemplatesType }
     * 
     */
    public ProductTypeTemplatesType createProductTypeTemplatesType() {
        return new ProductTypeTemplatesType();
    }

    /**
     * Create an instance of {@link NavigationByPriceTiersCurrencyTiersTierType }
     * 
     */
    public NavigationByPriceTiersCurrencyTiersTierType createNavigationByPriceTiersCurrencyTiersTierType() {
        return new NavigationByPriceTiersCurrencyTiersTierType();
    }

    /**
     * Create an instance of {@link PromotionCouponsCouponsType }
     * 
     */
    public PromotionCouponsCouponsType createPromotionCouponsCouponsType() {
        return new PromotionCouponsCouponsType();
    }

    /**
     * Create an instance of {@link ProductTypeAttributesType }
     * 
     */
    public ProductTypeAttributesType createProductTypeAttributesType() {
        return new ProductTypeAttributesType();
    }

    /**
     * Create an instance of {@link BaseContentType }
     * 
     */
    public BaseContentType createBaseContentType() {
        return new BaseContentType();
    }

    /**
     * Create an instance of {@link OrganisationUserOrganisationType }
     * 
     */
    public OrganisationUserOrganisationType createOrganisationUserOrganisationType() {
        return new OrganisationUserOrganisationType();
    }

    /**
     * Create an instance of {@link CategoryTemplatesType }
     * 
     */
    public CategoryTemplatesType createCategoryTemplatesType() {
        return new CategoryTemplatesType();
    }

    /**
     * Create an instance of {@link ShippingMethodConfigurationType }
     * 
     */
    public ShippingMethodConfigurationType createShippingMethodConfigurationType() {
        return new ShippingMethodConfigurationType();
    }

    /**
     * Create an instance of {@link NavigationByPriceTiersType.Currencies }
     * 
     */
    public NavigationByPriceTiersType.Currencies createNavigationByPriceTiersTypeCurrencies() {
        return new NavigationByPriceTiersType.Currencies();
    }

    /**
     * Create an instance of {@link ProductTypeAttributeNavigationRangeListType.Ranges }
     * 
     */
    public ProductTypeAttributeNavigationRangeListType.Ranges createProductTypeAttributeNavigationRangeListTypeRanges() {
        return new ProductTypeAttributeNavigationRangeListType.Ranges();
    }

    /**
     * Create an instance of {@link PromotionType.Availability }
     * 
     */
    public PromotionType.Availability createPromotionTypeAvailability() {
        return new PromotionType.Availability();
    }

    /**
     * Create an instance of {@link CategoryType.Availability }
     * 
     */
    public CategoryType.Availability createCategoryTypeAvailability() {
        return new CategoryType.Availability();
    }

    /**
     * Create an instance of {@link ProductType.Manufacturer }
     * 
     */
    public ProductType.Manufacturer createProductTypeManufacturer() {
        return new ProductType.Manufacturer();
    }

    /**
     * Create an instance of {@link ProductType.Supplier }
     * 
     */
    public ProductType.Supplier createProductTypeSupplier() {
        return new ProductType.Supplier();
    }

    /**
     * Create an instance of {@link ProductType.Pim }
     * 
     */
    public ProductType.Pim createProductTypePim() {
        return new ProductType.Pim();
    }

    /**
     * Create an instance of {@link ProductType.Availability }
     * 
     */
    public ProductType.Availability createProductTypeAvailability() {
        return new ProductType.Availability();
    }

    /**
     * Create an instance of {@link ProductType.InventoryConfig }
     * 
     */
    public ProductType.InventoryConfig createProductTypeInventoryConfig() {
        return new ProductType.InventoryConfig();
    }

    /**
     * Create an instance of {@link ContentType.Availability }
     * 
     */
    public ContentType.Availability createContentTypeAvailability() {
        return new ContentType.Availability();
    }

    /**
     * Create an instance of {@link SkuType.Manufacturer }
     * 
     */
    public SkuType.Manufacturer createSkuTypeManufacturer() {
        return new SkuType.Manufacturer();
    }

    /**
     * Create an instance of {@link SkuType.Supplier }
     * 
     */
    public SkuType.Supplier createSkuTypeSupplier() {
        return new SkuType.Supplier();
    }

    /**
     * Create an instance of {@link PriceType.PricingPolicy }
     * 
     */
    public PriceType.PricingPolicy createPriceTypePricingPolicy() {
        return new PriceType.PricingPolicy();
    }

    /**
     * Create an instance of {@link PriceType.Availability }
     * 
     */
    public PriceType.Availability createPriceTypeAvailability() {
        return new PriceType.Availability();
    }

    /**
     * Create an instance of {@link PriceRuleType.Availability }
     * 
     */
    public PriceRuleType.Availability createPriceRuleTypeAvailability() {
        return new PriceRuleType.Availability();
    }

    /**
     * Create an instance of {@link PriceRuleType.Configuration }
     * 
     */
    public PriceRuleType.Configuration createPriceRuleTypeConfiguration() {
        return new PriceRuleType.Configuration();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaxConfigsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "tax-configs")
    public JAXBElement<TaxConfigsType> createTaxConfigs(TaxConfigsType value) {
        return new JAXBElement<TaxConfigsType>(_TaxConfigs_QNAME, TaxConfigsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "country")
    public JAXBElement<CountryType> createCountry(CountryType value) {
        return new JAXBElement<CountryType>(_Country_QNAME, CountryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShippingProvidersType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "shipping-providers")
    public JAXBElement<ShippingProvidersType> createShippingProviders(ShippingProvidersType value) {
        return new JAXBElement<ShippingProvidersType>(_ShippingProviders_QNAME, ShippingProvidersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PromotionCouponsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "promotion-coupons")
    public JAXBElement<PromotionCouponsType> createPromotionCoupons(PromotionCouponsType value) {
        return new JAXBElement<PromotionCouponsType>(_PromotionCoupons_QNAME, PromotionCouponsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PriceRuleType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "price-rule")
    public JAXBElement<PriceRuleType> createPriceRule(PriceRuleType value) {
        return new JAXBElement<PriceRuleType>(_PriceRule_QNAME, PriceRuleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaxesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "taxes")
    public JAXBElement<TaxesType> createTaxes(TaxesType value) {
        return new JAXBElement<TaxesType>(_Taxes_QNAME, TaxesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShippingMethodsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "shipping-methods")
    public JAXBElement<ShippingMethodsType> createShippingMethods(ShippingMethodsType value) {
        return new JAXBElement<ShippingMethodsType>(_ShippingMethods_QNAME, ShippingMethodsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "data-group")
    public JAXBElement<DataGroupType> createDataGroup(DataGroupType value) {
        return new JAXBElement<DataGroupType>(_DataGroup_QNAME, DataGroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PriceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "price")
    public JAXBElement<PriceType> createPrice(PriceType value) {
        return new JAXBElement<PriceType>(_Price_QNAME, PriceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StockType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "stock")
    public JAXBElement<StockType> createStock(StockType value) {
        return new JAXBElement<StockType>(_Stock_QNAME, StockType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SkuType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "sku")
    public JAXBElement<SkuType> createSku(SkuType value) {
        return new JAXBElement<SkuType>(_Sku_QNAME, SkuType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BrandType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "brand")
    public JAXBElement<BrandType> createBrand(BrandType value) {
        return new JAXBElement<BrandType>(_Brand_QNAME, BrandType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PriceListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "price-list")
    public JAXBElement<PriceListType> createPriceList(PriceListType value) {
        return new JAXBElement<PriceListType>(_PriceList_QNAME, PriceListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ETypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "e-type")
    public JAXBElement<ETypeType> createEType(ETypeType value) {
        return new JAXBElement<ETypeType>(_EType_QNAME, ETypeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaxType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "tax")
    public JAXBElement<TaxType> createTax(TaxType value) {
        return new JAXBElement<TaxType>(_Tax_QNAME, TaxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ETypesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "e-types")
    public JAXBElement<ETypesType> createETypes(ETypesType value) {
        return new JAXBElement<ETypesType>(_ETypes_QNAME, ETypesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountriesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "countries")
    public JAXBElement<CountriesType> createCountries(CountriesType value) {
        return new JAXBElement<CountriesType>(_Countries_QNAME, CountriesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountryStateType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "country-state")
    public JAXBElement<CountryStateType> createCountryState(CountryStateType value) {
        return new JAXBElement<CountryStateType>(_CountryState_QNAME, CountryStateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SystemType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "system")
    public JAXBElement<SystemType> createSystem(SystemType value) {
        return new JAXBElement<SystemType>(_System_QNAME, SystemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributeGroupsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "attribute-groups")
    public JAXBElement<AttributeGroupsType> createAttributeGroups(AttributeGroupsType value) {
        return new JAXBElement<AttributeGroupsType>(_AttributeGroups_QNAME, AttributeGroupsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductsLinksCodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "products-links")
    public JAXBElement<ProductsLinksCodeType> createProductsLinks(ProductsLinksCodeType value) {
        return new JAXBElement<ProductsLinksCodeType>(_ProductsLinks_QNAME, ProductsLinksCodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductLinksCodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "product-links")
    public JAXBElement<ProductLinksCodeType> createProductLinks(ProductLinksCodeType value) {
        return new JAXBElement<ProductLinksCodeType>(_ProductLinks_QNAME, ProductLinksCodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductTypesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "product-types")
    public JAXBElement<ProductTypesType> createProductTypes(ProductTypesType value) {
        return new JAXBElement<ProductTypesType>(_ProductTypes_QNAME, ProductTypesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SkusType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "skus")
    public JAXBElement<SkusType> createSkus(SkusType value) {
        return new JAXBElement<SkusType>(_Skus_QNAME, SkusType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrganisationUsersType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "organisation-users")
    public JAXBElement<OrganisationUsersType> createOrganisationUsers(OrganisationUsersType value) {
        return new JAXBElement<OrganisationUsersType>(_OrganisationUsers_QNAME, OrganisationUsersType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CmsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "cms")
    public JAXBElement<CmsType> createCms(CmsType value) {
        return new JAXBElement<CmsType>(_Cms_QNAME, CmsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InventoryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "inventory")
    public JAXBElement<InventoryType> createInventory(InventoryType value) {
        return new JAXBElement<InventoryType>(_Inventory_QNAME, InventoryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "content")
    public JAXBElement<ContentType> createContent(ContentType value) {
        return new JAXBElement<ContentType>(_Content_QNAME, ContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "products")
    public JAXBElement<ProductsType> createProducts(ProductsType value) {
        return new JAXBElement<ProductsType>(_Products_QNAME, ProductsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FulfilmentCentreType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fulfilment-centre")
    public JAXBElement<FulfilmentCentreType> createFulfilmentCentre(FulfilmentCentreType value) {
        return new JAXBElement<FulfilmentCentreType>(_FulfilmentCentre_QNAME, FulfilmentCentreType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductCategoriesCodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "product-categories")
    public JAXBElement<ProductCategoriesCodeType> createProductCategories(ProductCategoriesCodeType value) {
        return new JAXBElement<ProductCategoriesCodeType>(_ProductCategories_QNAME, ProductCategoriesCodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FulfilmentCentresType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "fulfilment-centres")
    public JAXBElement<FulfilmentCentresType> createFulfilmentCentres(FulfilmentCentresType value) {
        return new JAXBElement<FulfilmentCentresType>(_FulfilmentCentres_QNAME, FulfilmentCentresType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SystemsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "systems")
    public JAXBElement<SystemsType> createSystems(SystemsType value) {
        return new JAXBElement<SystemsType>(_Systems_QNAME, SystemsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributeGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "attribute-group")
    public JAXBElement<AttributeGroupType> createAttributeGroup(AttributeGroupType value) {
        return new JAXBElement<AttributeGroupType>(_AttributeGroup_QNAME, AttributeGroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "attribute")
    public JAXBElement<AttributeType> createAttribute(AttributeType value) {
        return new JAXBElement<AttributeType>(_Attribute_QNAME, AttributeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CategoriesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "categories")
    public JAXBElement<CategoriesType> createCategories(CategoriesType value) {
        return new JAXBElement<CategoriesType>(_Categories_QNAME, CategoriesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TaxConfigType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "tax-config")
    public JAXBElement<TaxConfigType> createTaxConfig(TaxConfigType value) {
        return new JAXBElement<TaxConfigType>(_TaxConfig_QNAME, TaxConfigType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductTypeTypeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "product-type")
    public JAXBElement<ProductTypeTypeType> createProductType(ProductTypeTypeType value) {
        return new JAXBElement<ProductTypeTypeType>(_ProductType_QNAME, ProductTypeTypeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrganisationUserType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "organisation-user")
    public JAXBElement<OrganisationUserType> createOrganisationUser(OrganisationUserType value) {
        return new JAXBElement<OrganisationUserType>(_OrganisationUser_QNAME, OrganisationUserType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "product")
    public JAXBElement<ProductType> createProduct(ProductType value) {
        return new JAXBElement<ProductType>(_Product_QNAME, ProductType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BrandsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "brands")
    public JAXBElement<BrandsType> createBrands(BrandsType value) {
        return new JAXBElement<BrandsType>(_Brands_QNAME, BrandsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataGroupsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "data-groups")
    public JAXBElement<DataGroupsType> createDataGroups(DataGroupsType value) {
        return new JAXBElement<DataGroupsType>(_DataGroups_QNAME, DataGroupsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CountryStatesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "country-states")
    public JAXBElement<CountryStatesType> createCountryStates(CountryStatesType value) {
        return new JAXBElement<CountryStatesType>(_CountryStates_QNAME, CountryStatesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShippingMethodType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "shipping-method")
    public JAXBElement<ShippingMethodType> createShippingMethod(ShippingMethodType value) {
        return new JAXBElement<ShippingMethodType>(_ShippingMethod_QNAME, ShippingMethodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PromotionsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "promotions")
    public JAXBElement<PromotionsType> createPromotions(PromotionsType value) {
        return new JAXBElement<PromotionsType>(_Promotions_QNAME, PromotionsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProductsCategoriesCodeType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "products-categories")
    public JAXBElement<ProductsCategoriesCodeType> createProductsCategories(ProductsCategoriesCodeType value) {
        return new JAXBElement<ProductsCategoriesCodeType>(_ProductsCategories_QNAME, ProductsCategoriesCodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataDescriptorsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "data-descriptors")
    public JAXBElement<DataDescriptorsType> createDataDescriptors(DataDescriptorsType value) {
        return new JAXBElement<DataDescriptorsType>(_DataDescriptors_QNAME, DataDescriptorsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShippingProviderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "shipping-provider")
    public JAXBElement<ShippingProviderType> createShippingProvider(ShippingProviderType value) {
        return new JAXBElement<ShippingProviderType>(_ShippingProvider_QNAME, ShippingProviderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PriceRulesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "price-rules")
    public JAXBElement<PriceRulesType> createPriceRules(PriceRulesType value) {
        return new JAXBElement<PriceRulesType>(_PriceRules_QNAME, PriceRulesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataDescriptorType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "data-descriptor")
    public JAXBElement<DataDescriptorType> createDataDescriptor(DataDescriptorType value) {
        return new JAXBElement<DataDescriptorType>(_DataDescriptor_QNAME, DataDescriptorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AttributesType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "attributes")
    public JAXBElement<AttributesType> createAttributes(AttributesType value) {
        return new JAXBElement<AttributesType>(_Attributes_QNAME, AttributesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PromotionCouponType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "promotion-coupon")
    public JAXBElement<PromotionCouponType> createPromotionCoupon(PromotionCouponType value) {
        return new JAXBElement<PromotionCouponType>(_PromotionCoupon_QNAME, PromotionCouponType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CategoryType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "category")
    public JAXBElement<CategoryType> createCategory(CategoryType value) {
        return new JAXBElement<CategoryType>(_Category_QNAME, CategoryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PromotionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "promotion")
    public JAXBElement<PromotionType> createPromotion(PromotionType value) {
        return new JAXBElement<PromotionType>(_Promotion_QNAME, PromotionType.class, null, value);
    }

}
