    create  table TADDRESS (
        ADDRESS_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CITY varchar(128) not null,
        POSTCODE varchar(16),
        ADDRLINE1 varchar(255) not null,
        ADDRLINE2 varchar(255),
        ADDRESS_TYPE varchar(1) not null,
        COUNTRY_CODE varchar(64) not null,
        STATE_CODE varchar(64),
        PHONES varchar(255),
        FIRSTNAME varchar(128) not null,
        LASTNAME varchar(128) not null,
        MIDDLENAME varchar(128),
        DEFAULT_ADDR smallint,
        CUSTOMER_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ADDRESS_ID)
    );


    create  table TASSOCIATION (
        ASSOCIATION_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null,
        NAME varchar(255) not null,
        DESCRIPTION varchar(4000),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ASSOCIATION_ID)
    );


    create  table TATTRIBUTE (
        ATTRIBUTE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null  unique,
        MANDATORY smallint not null,
        ALLOWDUPLICATE smallint default 0 not null,
        ALLOWFAILOVER smallint default 0 not null,
        VAL varchar(4000),
        REXP varchar(4000),
        V_FAILED_MSG varchar(4000),
        RANK integer default 500,
        CHOICES varchar(4000),
        NAME varchar(255) not null,
        DISPLAYNAME varchar(4000),
        DESCRIPTION varchar(4000),
        ETYPE_ID bigint not null,
        ATTRIBUTEGROUP_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRIBUTE_ID)
    );


    create  table TATTRIBUTEGROUP (
        ATTRIBUTEGROUP_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null,
        NAME varchar(64) not null,
        DESCRIPTION varchar(4000),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRIBUTEGROUP_ID)
    );


    create  table TBRAND (
        BRAND_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        NAME varchar(255) not null,
        DESCRIPTION varchar(4000),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (BRAND_ID)
    );


    create  table TBRANDATTRVALUE (
        ATTRVALUE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        BRAND_ID bigint not null,
        VAL varchar(4000),
        DISPLAYVAL varchar(4000),
        CODE varchar(255) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    );

    create  table TCARRIER (
        CARRIER_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        NAME varchar(255) not null,
        DISPLAYNAME varchar(4000),
        DESCRIPTION varchar(1000),
        DISPLAYDESCRIPTION varchar(4000),
        WORLDWIDE smallint,
        COUNTRY smallint,
        STATE smallint,
        LOCAL smallint,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CARRIER_ID)
    );


    create  table TCARRIERSLA (
        CARRIERSLA_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        NAME varchar(255) not null,
        DISPLAYNAME varchar(4000),
        DESCRIPTION varchar(1000),
        DISPLAYDESCRIPTION varchar(4000),
        CURRENCY varchar(3) not null,
        MAX_DAYS integer,
        SLA_TYPE varchar(1) not null,
        PRICE numeric(19,2),
        PER_CENT numeric(19,2),
        SCRIPT varchar(4000),
        PRICE_NOTLESS numeric(19,2),
        PERCENT_NOTLESS numeric(19,2),
        COST_NOTLESS numeric(19,2),
        CARRIER_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CARRIERSLA_ID)
    );


    create  table TCATEGORY (
        CATEGORY_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        PARENT_ID bigint,
        RANK integer,
        PRODUCTTYPE_ID bigint,
        NAME varchar(255) not null,
        DISPLAYNAME varchar(4000),
        DESCRIPTION varchar(4000),
        UITEMPLATE varchar(255),
        AVAILABLEFROM timestamp,
        AVAILABLETO timestamp,
        URI varchar(255),
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        NAV_BY_ATTR smallint,
        NAV_BY_BRAND smallint,
        NAV_BY_PRICE smallint,
        NAV_BY_PRICE_TIERS varchar(4000),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CATEGORY_ID)
    );


    create  table TCATEGORYATTRVALUE (
        ATTRVALUE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        VAL varchar(4000),
        DISPLAYVAL varchar(4000),
        CATEGORY_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    );

    create  table TCOUNTRY (
        COUNTRY_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        COUNTRY_CODE varchar(2) not null,
        ISO_CODE varchar(3) not null,
        NAME varchar(64) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (COUNTRY_ID)
    );


    create  table TCUSTOMER (
        CUSTOMER_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        EMAIL varchar(255) not null unique,
        FIRSTNAME varchar(128) not null,
        LASTNAME varchar(128) not null,
        MIDDLENAME varchar(128),
        PASSWORD varchar(255) not null,
        TAG varchar(255),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMER_ID)
    );

    create  table TCUSTOMERATTRVALUE (
        ATTRVALUE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        VAL varchar(4000),
        DISPLAYVAL varchar(4000),
        CUSTOMER_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    );

    create  table TCUSTOMERORDER (
        CUSTOMERORDER_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        PG_LABEL varchar(255),
        ORDERNUM varchar(255),
        CART_GUID varchar(36) not null,
        CURRENCY varchar(3) not null,
        LOCALE varchar(5) not null,
        PRICE numeric(19,2) not null,
        LIST_PRICE numeric(19,2) not null,
        IS_PROMO_APPLIED smallint not null default 0,
        APPLIED_PROMO varchar(255),
        MESSAGE varchar(255),
        ORDERSTATUS varchar(64) not null,
        CUSTOMER_ID bigint,
        SHOP_ID bigint not null,
        BILLING_ADDRESS varchar(255),
        SHIPPING_ADDRESS varchar(255),
        MULTIPLE_SHIPMENT smallint default 0,
        ORDER_TIMESTAMP timestamp not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERORDER_ID)
    );


    create  table TCUSTOMERORDERDELIVERY (
        CUSTOMERORDERDELIVERY_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        DELIVERYNUM varchar(255),
        REF_NO varchar(255),
        PRICE numeric(19,2) not null,
        LIST_PRICE numeric(19,2) not null,
        IS_PROMO_APPLIED smallint not null default 0,
        APPLIED_PROMO varchar(255),
        DELIVERYSTATUS varchar(64) not null,
        CARRIERSLA_ID bigint,
        CUSTOMERORDER_ID bigint not null,
        DELIVERY_GROUP varchar(16) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERORDERDELIVERY_ID)
    );


    create  table TCUSTOMERORDERDELIVERYDET (
        CUSTOMERORDERDELIVERYDET_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        QTY numeric(19,2) not null,
        PRICE numeric(19,2) not null,
        SALE_PRICE numeric(19,2) not null,
        LIST_PRICE numeric(19,2) not null,
        IS_GIFT  smallint not null default 0,
        IS_PROMO_APPLIED smallint not null default 0,
        APPLIED_PROMO varchar(255),
        CODE varchar(255) not null,
        PRODUCTNAME varchar(4000) not null,
        CUSTOMERORDERDELIVERY_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERORDERDELIVERYDET_ID)
    );


    create  table TCUSTOMERORDERDET (
        CUSTOMERORDERDET_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        QTY numeric(19,2) not null,
        PRICE numeric(19,2) not null,
        SALE_PRICE numeric(19,2) not null,
        LIST_PRICE numeric(19,2) not null,
        IS_GIFT  smallint not null default 0,
        IS_PROMO_APPLIED smallint not null default 0,
        APPLIED_PROMO varchar(255),
        CODE varchar(255) not null,
        PRODUCTNAME varchar(4000) not null,
        CUSTOMERORDER_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERORDERDET_ID)
    );


    create  table TCUSTOMERSHOP (
        CUSTOMERSHOP_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CUSTOMER_ID bigint not null,
        SHOP_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERSHOP_ID)
    );

    create  table TCUSTOMERWISHLIST (
        CUSTOMERWISHLIST_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        SKU_ID bigint not null,
        CUSTOMER_ID bigint not null,
        WL_TYPE varchar(1) default 'W',
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERWISHLIST_ID)
    );


    create  table TENSEMBLEOPT (
        ENSEMBLEOPT_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        QTY integer not null,
        PRODUCT_ID bigint not null,
        SKU_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ENSEMBLEOPT_ID)
    );


    create  table TETYPE (
        ETYPE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        JAVATYPE varchar(255) not null,
        BUSINESSTYPE varchar(255),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ETYPE_ID)
    );


    create  table TMAILTEMPLATE (
        MAILTEMPLATE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null,
        FSPOINTER varchar(4000) not null,
        NAME varchar(255) not null,
        DESCRIPTION varchar(255),
        MAILTEMPLATEGROUP_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MAILTEMPLATE_ID)
    );


    create  table TMAILTEMPLATEGROUP (
        MAILTEMPLATEGROUP_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        NAME varchar(64) not null,
        DESCRIPTION varchar(4000),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MAILTEMPLATEGROUP_ID)
    );

    create  table TMANAGER (
        MANAGER_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        EMAIL varchar(255) not null unique,
        FIRSTNAME varchar(128) not null,
        LASTNAME varchar(128) not null,
        MIDDLENAME varchar(128),
        PASSWORD varchar(255) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MANAGER_ID)
    );


    create  table TMANAGERROLE (
        MANAGERROLE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        EMAIL varchar(255) not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MANAGERROLE_ID)
    );


    create  table TPRODTYPEATTRVIEWGROUP (
        PRODTYPEATTRIBUTEGROUP_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        PRODUCTTYPE_ID bigint not null,
        ATTRCODELIST varchar(4000),
        RANK integer,
        NAME varchar(64) not null,
        DISPLAYNAME varchar(255),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODTYPEATTRIBUTEGROUP_ID)
    );

    create  table TPRODUCT (
        PRODUCT_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null  unique,
        AVAILABLEFROM timestamp,
        AVAILABLETO timestamp,
        NAME varchar(255) not null,
        DISPLAYNAME varchar(4000),
        DESCRIPTION varchar(4000),
        TAG varchar(255),
        BRAND_ID bigint not null,
        PRODUCTTYPE_ID bigint not null,
        AVAILABILITY integer default 1 not null,
        FEATURED smallint,
        URI varchar(255),
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCT_ID)
    );


    create  table TPRODUCTASSOCIATION (
        PRODUCTASSOCIATION_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        RANK integer,
        ASSOCIATION_ID bigint not null,
        PRODUCT_ID bigint not null,
        ASSOCIATEDPRODUCT_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCTASSOCIATION_ID)
    );


    create  table TPRODUCTATTRVALUE (
        ATTRVALUE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        VAL varchar(4000),
        DISPLAYVAL varchar(4000),
        PRODUCT_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    );

    create  table TPRODUCTCATEGORY (
        PRODUCTCATEGORY_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        PRODUCT_ID bigint not null,
        CATEGORY_ID bigint not null,
        RANK integer,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCTCATEGORY_ID)
    );


    create  table TPRODUCTSKUATTRVALUE (
        ATTRVALUE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        VAL varchar(4000),
        DISPLAYVAL varchar(4000),
        SKU_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    );

    create  table TPRODUCTTYPE (
        PRODUCTTYPE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        NAME varchar(255),
        DESCRIPTION varchar(255),
        UITEMPLATE varchar(255),
        UISEARCHTEMPLATE varchar(255),
        SERVICE smallint,
        ENSEMBLE smallint,
        SHIPPABLE smallint,
        DIGITAL smallint default 0,
        DOWNLOADABLE smallint default 0,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCTTYPE_ID)
    );


    create  table TPRODUCTTYPEATTR (
        PRODTYPEATTR_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null,
        PRODUCTTYPE_ID bigint not null,
        RANK integer default 500,
        VISIBLE smallint,
        SIMILARITY smallint,
        NAV smallint,
        NAV_TYPE varchar(1) default 'S',
        RANGE_NAV varchar(4000),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODTYPEATTR_ID)
    );


    create  table TROLE (
        ROLE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null unique,
        DESCRIPTION varchar(255),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ROLE_ID)
    );


    create  table TSEOIMAGE (
        SEOIMAGE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        IMAGE_NAME varchar(255),
        ALT varchar(255),
        TITLE varchar(255),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SEOIMAGE_ID)
    );


    create  table TSHOP (
        SHOP_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null,
        NAME varchar(64) not null,
        DESCRIPTION varchar(4000),
        FSPOINTER varchar(4000) not null,
        IMGVAULT varchar(4000) not null,
        URI varchar(255),
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOP_ID)
    );


    create  table TSHOPADVPLACE (
        SHOPADVPLACE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        NAME varchar(255) not null,
        DESCRIPTION varchar(4000),
        SHOP_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPADVPLACE_ID)
    );


    create  table TSHOPADVRULES (
        SHOPADVRULES_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        rank integer,
        NAME varchar(255) not null,
        DESCRIPTION varchar(4000),
        AVAILABLEFROM timestamp,
        AVAILABLETO timestamp,
        RULE varchar(4000),
        SHOPADVPLACE_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPADVRULES_ID)
    );


    create  table TSHOPATTRVALUE (
        ATTRVALUE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        VAL varchar(4000),
        DISPLAYVAL varchar(4000),
        SHOP_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    );

    create  table TSHOPCATEGORY (
        SHOPCATEGORY_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        RANK integer,
        SHOP_ID bigint not null,
        CATEGORY_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPCATEGORY_ID)
    );


    create  table TSHOPEXCHANGERATE (
        SHOPEXCHANGERATE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        FROMCURRENCY varchar(3) not null,
        TOCURRENCY varchar(3) not null,
        SHOP_ID bigint not null,
        RATE numeric(19,2) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPEXCHANGERATE_ID)
    );

    create  table TSHOPTOPSELLER (
        SHOPTOPSELLER_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        SHOP_ID bigint not null,
        PRODUCT_ID bigint not null,
        COUNTER numeric(19,2),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPTOPSELLER_ID)
    );


    create  table TSHOPURL (
        STOREURL_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        URL varchar(512) not null,
        SHOP_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (STOREURL_ID)
    );

    create  table TSHOPWAREHOUSE (
        SHOPWAREHOUSE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        SHOP_ID bigint not null,
        WAREHOUSE_ID bigint not null,
        RANK integer,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPWAREHOUSE_ID)
    );



    create  table TSKU (
        SKU_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null  unique,
        NAME varchar(255) not null,
        DISPLAYNAME varchar(4000),
        DESCRIPTION varchar(4000),
        PRODUCT_ID bigint,
        RANK integer,
        BARCODE varchar(128),
        URI varchar(255),
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SKU_ID)
    );


    create  table TSKUPRICE (
        SKUPRICE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        SKU_ID bigint not null,
        SHOP_ID bigint not null,
        CURRENCY varchar(3) not null,
        QTY numeric(19,2) not null,
        REGULAR_PRICE numeric(19,2) not null,
        SALE_PRICE numeric(19,2),
        MINIMAL_PRICE numeric(19,2),
        SALE_FROM timestamp,
        SALE_TO timestamp,
        TAG varchar(45),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null,
        primary key (SKUPRICE_ID)
    );


    create  table TSKUWAREHOUSE (
        SKUWAREHOUSE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        WAREHOUSE_ID bigint not null,
        SKU_ID bigint not null,
        QUANTITY numeric(19,2) not null,
        RESERVED numeric(19,2) default 0,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SKUWAREHOUSE_ID)
    );


    create  table TSTATE (
        STATE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        COUNTRY_CODE varchar(2) not null,
        STATE_CODE varchar(64) not null,
        NAME varchar(64) not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (STATE_ID)
    );


    create  table TSYSTEM (
        SYSTEM_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null,
        NAME varchar(64) not null,
        DESCRIPTION varchar(4000),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SYSTEM_ID)
    );

    create  table TSYSTEMATTRVALUE (
        ATTRVALUE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        VAL varchar(4000),
        DISPLAYVAL varchar(4000),
        CODE varchar(255) not null,
        SYSTEM_ID bigint not null,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    );

    create  table TWAREHOUSE (
        WAREHOUSE_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null DEFAULT 0,
        CODE varchar(255) not null,
        NAME varchar(64) not null,
        DESCRIPTION varchar(4000),
        COUNTRY_CODE varchar(64),
        STATE_CODE varchar(64),
        CITY varchar(128),
        POSTCODE varchar(16),
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (WAREHOUSE_ID)
    );


    create table TPROMOTION (
        PROMOTION_ID bigint not null GENERATED BY DEFAULT AS IDENTITY,
        VERSION bigint not null default 0,
        CODE varchar(255) not null,
        SHOP_CODE varchar(255) not null,
        CURRENCY varchar(5) not null,
        PROMO_TYPE varchar(1) not null,
        PROMO_ACTION varchar(1) not null,
        ELIGIBILITY_CONDITION varchar(4000) not null,
        PROMO_ACTION_CONTEXT varchar(255),
        NAME varchar(255) not null,
        DISPLAYNAME varchar(4000),
        DESCRIPTION varchar(100),
        DISPLAYDESCRIPTION varchar(4000),
        TAG varchar(255),
        CAN_BE_COMBINED smallint not null,
        ENABLED smallint not null,
        ENABLED_FROM timestamp,
        ENABLED_TO timestamp,
        CREATED_TIMESTAMP timestamp,
        UPDATED_TIMESTAMP timestamp,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PROMOTION_ID)
    );

    alter table TADDRESS         add constraint FKADDRCUSTOMER 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER 
        on delete cascade;



    alter table TATTRIBUTE 
        add constraint FK_ATTRIBUTE_ETYPE 
        foreign key (ETYPE_ID) 
        references TETYPE;


    alter table TATTRIBUTE  
        add constraint FK_ATTRIBUTE_AG 
        foreign key (ATTRIBUTEGROUP_ID) 
        references TATTRIBUTEGROUP;


    alter table TBRANDATTRVALUE 
        add constraint FKFA06C3CD5CB3C6AB 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);


    alter table TBRANDATTRVALUE 
        add constraint FKFA06C3CDEF74BF7C 
        foreign key (BRAND_ID) 
        references TBRAND on delete cascade;


    alter table TCARRIERSLA 
        add constraint FK_CSLA_CARR 
        foreign key (CARRIER_ID) 
        references TCARRIER;


    alter table TCATEGORY 
        add constraint FK_CAT_PRODTYPE 
        foreign key (PRODUCTTYPE_ID) 
        references TPRODUCTTYPE;


    alter table TCATEGORYATTRVALUE 
        add constraint FK_CAT_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TCATEGORYATTRVALUE 
        add constraint FKBAB98EE5FA5E3ED 
        foreign key (CATEGORY_ID) 
        references TCATEGORY 
        on delete cascade;


    alter table TCUSTOMERATTRVALUE 
        add constraint FK_C_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TCUSTOMERATTRVALUE 
        add constraint FKB44A120EAF56CFED 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER 
        on delete cascade;



    alter table TCUSTOMERORDER 
        add constraint FK_ORDER_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP;


    alter table TCUSTOMERORDER 
        add constraint FK_ORDER_CUSTOMER 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER;

    create index CUSTOMERORDER_NUM on TCUSTOMERORDER (ORDERNUM);
    create index CUSTOMERORDER_CART on TCUSTOMERORDER (CART_GUID);



    alter table TCUSTOMERORDERDELIVERY 
        add constraint FK_OD_ORD 
        foreign key (CUSTOMERORDER_ID) 
        references TCUSTOMERORDER on delete cascade;

    alter table TCUSTOMERORDERDELIVERY 
        add constraint FK_OD_CSLA 
        foreign key (CARRIERSLA_ID) 
        references TCARRIERSLA;


    alter table TCUSTOMERORDERDELIVERYDET
        add constraint FK_CODD_CDELIVERY
        foreign key (CUSTOMERORDERDELIVERY_ID)
        references TCUSTOMERORDERDELIVERY;


    alter table TCUSTOMERORDERDET 
        add constraint FKCB358C37A7F39C2D 
        foreign key (CUSTOMERORDER_ID) 
        references TCUSTOMERORDER;



    alter table TCUSTOMERSHOP 
        add constraint FK_CS_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP;


    alter table TCUSTOMERSHOP 
        add constraint FK_CS_CUSTOMER 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER         on delete cascade;


    alter table TCUSTOMERWISHLIST 
        add constraint FK_WL_SKU 
        foreign key (SKU_ID) 
        references TSKU;


    alter table TCUSTOMERWISHLIST 
        add constraint FK_WL_CUSTOMER 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER;


    alter table TENSEMBLEOPT 
        add constraint FK_END_SKU 
        foreign key (SKU_ID) 
        references TSKU;


    alter table TENSEMBLEOPT 
        add constraint FK_ENS_PROD 
        foreign key (PRODUCT_ID) 
        references TPRODUCT;


    alter table TMAILTEMPLATE 
        add constraint FK_M_TEMPLATEGROUP 
        foreign key (MAILTEMPLATEGROUP_ID) 
        references TMAILTEMPLATEGROUP;


    alter table TPRODTYPEATTRVIEWGROUP 
        add constraint FK4589D8C42AD8F70D 
        foreign key (PRODUCTTYPE_ID) 
        references TPRODUCTTYPE;



    alter table TPRODUCT 
        add constraint FK_PROD_PRODTYPE 
        foreign key (PRODUCTTYPE_ID) 
        references TPRODUCTTYPE;

    alter table TPRODUCT 
        add constraint FK_PROD_BRAND 
        foreign key (BRAND_ID) 
        references TBRAND;

    alter table TPRODUCTASSOCIATION 
        add constraint FK_PA_ASSOC 
        foreign key (ASSOCIATION_ID) 
        references TASSOCIATION;

    alter table TPRODUCTASSOCIATION 
        add constraint FK_PA_ASSOCPROD 
        foreign key (ASSOCIATEDPRODUCT_ID) 
        references TPRODUCT;


    alter table TPRODUCTASSOCIATION 
        add constraint FK_PA_PRODUCT 
        foreign key (PRODUCT_ID) 
        references TPRODUCT;


    alter table TPRODUCTATTRVALUE 
        add constraint FK_P_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TPRODUCTATTRVALUE 
        add constraint FK215F4E65FFF5E8AD 
        foreign key (PRODUCT_ID) 
        references TPRODUCT 
        on delete cascade;




    alter table TPRODUCTCATEGORY 
        add constraint FK_PC_CAT 
        foreign key (CATEGORY_ID) 
        references TCATEGORY;


    alter table TPRODUCTCATEGORY 
        add constraint FK_PC_PRODUCT 
        foreign key (PRODUCT_ID) 
        references TPRODUCT;



    alter table TPRODUCTSKUATTRVALUE 
        add constraint FK23B3D31E4EC4B749 
        foreign key (SKU_ID) 
        references TSKU;

    alter table TPRODUCTSKUATTRVALUE 
        add constraint FK_S_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);


    alter table TPRODUCTTYPEATTR 
        add constraint FK_PTA_PRODTYPE 
        foreign key (PRODUCTTYPE_ID) 
        references TPRODUCTTYPE         on delete cascade;

    alter table TPRODUCTTYPEATTR 
        add constraint FK_PTA_ATTR 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);


    create index SHOP_CODE on TSHOP (CODE);



    alter table TSHOPADVPLACE 
        add constraint FK_ADVP_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP;



    alter table TSHOPADVRULES 
        add constraint FK_ADVR_ADVPLACE 
        foreign key (SHOPADVPLACE_ID) 
        references TSHOPADVPLACE;


    alter table TSHOPATTRVALUE 
        add constraint FK_ATTR_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP;

    alter table TSHOPATTRVALUE 
        add constraint FK_SHOP_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);



    alter table TSHOPCATEGORY 
        add constraint FK_SC_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP;

    alter table TSHOPCATEGORY 
        add constraint FK_SC_CAT 
        foreign key (CATEGORY_ID) 
        references TCATEGORY;



    alter table TSHOPEXCHANGERATE 
        add constraint FK_ER_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP;



    alter table TSHOPTOPSELLER 
        add constraint FKB33456EAE13125FC 
        foreign key (PRODUCT_ID) 
        references TPRODUCT;



    alter table TSHOPURL 
        add constraint FK_SHOPURL_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP;


    alter table TSHOPWAREHOUSE 
        add constraint FK13C59499F65CA98 
        foreign key (SHOP_ID) 
        references TSHOP;

    alter table TSHOPWAREHOUSE 
        add constraint FK13C594991C1544FC 
        foreign key (WAREHOUSE_ID) 
        references TWAREHOUSE;







    alter table TSKU 
        add constraint FK_SKU_PROD 
        foreign key (PRODUCT_ID) 
        references TPRODUCT;



    alter table TSKUPRICE 
        add constraint FK_SP_SKU 
        foreign key (SKU_ID) 
        references TSKU;

    alter table TSKUPRICE 
        add constraint FK_SP_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP;



    alter table TSKUWAREHOUSE 
        add constraint FKAC00F89A4EC4B749 
        foreign key (SKU_ID) 
        references TSKU;

    alter table TSKUWAREHOUSE 
        add constraint FKAC00F89A1C1544FC 
        foreign key (WAREHOUSE_ID) 
        references TWAREHOUSE;

    alter table TSKUWAREHOUSE
        add constraint U_SKUINVENTORY unique (WAREHOUSE_ID, SKU_ID);

    alter table TSYSTEMATTRVALUE 
        add constraint FK_SYS_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TSYSTEMATTRVALUE 
        add constraint FK_ATTR_SYS 
        foreign key (SYSTEM_ID) 
        references TSYSTEM;

    create index IMAGE_NAME_IDX on TSEOIMAGE (IMAGE_NAME);

    create index PROMO_SHOP_CODE on TPROMOTION (SHOP_CODE);
    create index PROMO_CURRENCY on TPROMOTION (CURRENCY);
    create index PROMO_CODE on TPROMOTION (CODE);
    create index PROMO_PTYPE on TPROMOTION (PROMO_TYPE);
    create index PROMO_PACTION on TPROMOTION (PROMO_ACTION);
    create index PROMO_ENABLED on TPROMOTION (ENABLED);
    create index PROMO_ENABLED_FROM on TPROMOTION (ENABLED_FROM);
    create index PROMO_ENABLED_TO on TPROMOTION (ENABLED_TO);


    create table HIBERNATE_UNIQUE_KEYS (
         value integer 
    );

    insert into HIBERNATE_UNIQUE_KEYS values ( 0 );
