
package org.yes.cart.bulkimport.xml.internal;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for price-ruleType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="price-ruleType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tags" type="{}tagsType" minOccurs="0"/>
 *         &lt;element name="availability">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="available-from" type="{}dateTimeType" minOccurs="0"/>
 *                   &lt;element name="available-to" type="{}dateTimeType" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="configuration" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="margin-percent" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="margin-amount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="rounding-unit" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="add-default-tax" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="price-tag" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="price-ref" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="price-policy" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="created-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="created-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="updated-timestamp" type="{}dateTimeType" minOccurs="0"/>
 *         &lt;element name="updated-by" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="guid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="code" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="shop" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="currency" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="rank" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="import-mode" type="{}entityImportModeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "price-ruleType", propOrder = {
    "name",
    "description",
    "tags",
    "availability",
    "configuration",
    "createdTimestamp",
    "createdBy",
    "updatedTimestamp",
    "updatedBy"
})
public class PriceRuleType {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String description;
    protected TagsType tags;
    @XmlElement(required = true)
    protected PriceRuleType.Availability availability;
    protected PriceRuleType.Configuration configuration;
    @XmlElement(name = "created-timestamp")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String createdTimestamp;
    @XmlElement(name = "created-by")
    protected String createdBy;
    @XmlElement(name = "updated-timestamp")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String updatedTimestamp;
    @XmlElement(name = "updated-by")
    protected String updatedBy;
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "guid")
    protected String guid;
    @XmlAttribute(name = "code", required = true)
    protected String code;
    @XmlAttribute(name = "shop", required = true)
    protected String shop;
    @XmlAttribute(name = "currency", required = true)
    protected String currency;
    @XmlAttribute(name = "rank")
    protected Integer rank;
    @XmlAttribute(name = "import-mode")
    protected EntityImportModeType importMode;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link TagsType }
     *     
     */
    public TagsType getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link TagsType }
     *     
     */
    public void setTags(TagsType value) {
        this.tags = value;
    }

    /**
     * Gets the value of the availability property.
     * 
     * @return
     *     possible object is
     *     {@link PriceRuleType.Availability }
     *     
     */
    public PriceRuleType.Availability getAvailability() {
        return availability;
    }

    /**
     * Sets the value of the availability property.
     * 
     * @param value
     *     allowed object is
     *     {@link PriceRuleType.Availability }
     *     
     */
    public void setAvailability(PriceRuleType.Availability value) {
        this.availability = value;
    }

    /**
     * Gets the value of the configuration property.
     * 
     * @return
     *     possible object is
     *     {@link PriceRuleType.Configuration }
     *     
     */
    public PriceRuleType.Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Sets the value of the configuration property.
     * 
     * @param value
     *     allowed object is
     *     {@link PriceRuleType.Configuration }
     *     
     */
    public void setConfiguration(PriceRuleType.Configuration value) {
        this.configuration = value;
    }

    /**
     * Gets the value of the createdTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    /**
     * Sets the value of the createdTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedTimestamp(String value) {
        this.createdTimestamp = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the updatedTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    /**
     * Sets the value of the updatedTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedTimestamp(String value) {
        this.updatedTimestamp = value;
    }

    /**
     * Gets the value of the updatedBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUpdatedBy() {
        return updatedBy;
    }

    /**
     * Sets the value of the updatedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUpdatedBy(String value) {
        this.updatedBy = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the guid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuid(String value) {
        this.guid = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the shop property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShop() {
        return shop;
    }

    /**
     * Sets the value of the shop property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShop(String value) {
        this.shop = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the rank property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRank() {
        return rank;
    }

    /**
     * Sets the value of the rank property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRank(Integer value) {
        this.rank = value;
    }

    /**
     * Gets the value of the importMode property.
     * 
     * @return
     *     possible object is
     *     {@link EntityImportModeType }
     *     
     */
    public EntityImportModeType getImportMode() {
        return importMode;
    }

    /**
     * Sets the value of the importMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityImportModeType }
     *     
     */
    public void setImportMode(EntityImportModeType value) {
        this.importMode = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="available-from" type="{}dateTimeType" minOccurs="0"/>
     *         &lt;element name="available-to" type="{}dateTimeType" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="disabled" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "availableFrom",
        "availableTo"
    })
    public static class Availability {

        @XmlElement(name = "available-from")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String availableFrom;
        @XmlElement(name = "available-to")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "token")
        protected String availableTo;
        @XmlAttribute(name = "disabled", required = true)
        protected boolean disabled;

        /**
         * Gets the value of the availableFrom property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAvailableFrom() {
            return availableFrom;
        }

        /**
         * Sets the value of the availableFrom property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAvailableFrom(String value) {
            this.availableFrom = value;
        }

        /**
         * Gets the value of the availableTo property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAvailableTo() {
            return availableTo;
        }

        /**
         * Sets the value of the availableTo property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAvailableTo(String value) {
            this.availableTo = value;
        }

        /**
         * Gets the value of the disabled property.
         * 
         */
        public boolean isDisabled() {
            return disabled;
        }

        /**
         * Sets the value of the disabled property.
         * 
         */
        public void setDisabled(boolean value) {
            this.disabled = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="action" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="margin-percent" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *       &lt;attribute name="margin-amount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *       &lt;attribute name="rounding-unit" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *       &lt;attribute name="add-default-tax" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="price-tag" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="price-ref" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="price-policy" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class Configuration {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "action", required = true)
        protected String action;
        @XmlAttribute(name = "margin-percent")
        protected BigDecimal marginPercent;
        @XmlAttribute(name = "margin-amount")
        protected BigDecimal marginAmount;
        @XmlAttribute(name = "rounding-unit")
        protected BigDecimal roundingUnit;
        @XmlAttribute(name = "add-default-tax")
        protected Boolean addDefaultTax;
        @XmlAttribute(name = "price-tag")
        protected String priceTag;
        @XmlAttribute(name = "price-ref")
        protected String priceRef;
        @XmlAttribute(name = "price-policy")
        protected String pricePolicy;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the action property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getAction() {
            return action;
        }

        /**
         * Sets the value of the action property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setAction(String value) {
            this.action = value;
        }

        /**
         * Gets the value of the marginPercent property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getMarginPercent() {
            return marginPercent;
        }

        /**
         * Sets the value of the marginPercent property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setMarginPercent(BigDecimal value) {
            this.marginPercent = value;
        }

        /**
         * Gets the value of the marginAmount property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getMarginAmount() {
            return marginAmount;
        }

        /**
         * Sets the value of the marginAmount property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setMarginAmount(BigDecimal value) {
            this.marginAmount = value;
        }

        /**
         * Gets the value of the roundingUnit property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getRoundingUnit() {
            return roundingUnit;
        }

        /**
         * Sets the value of the roundingUnit property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setRoundingUnit(BigDecimal value) {
            this.roundingUnit = value;
        }

        /**
         * Gets the value of the addDefaultTax property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isAddDefaultTax() {
            return addDefaultTax;
        }

        /**
         * Sets the value of the addDefaultTax property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setAddDefaultTax(Boolean value) {
            this.addDefaultTax = value;
        }

        /**
         * Gets the value of the priceTag property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPriceTag() {
            return priceTag;
        }

        /**
         * Sets the value of the priceTag property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPriceTag(String value) {
            this.priceTag = value;
        }

        /**
         * Gets the value of the priceRef property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPriceRef() {
            return priceRef;
        }

        /**
         * Sets the value of the priceRef property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPriceRef(String value) {
            this.priceRef = value;
        }

        /**
         * Gets the value of the pricePolicy property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPricePolicy() {
            return pricePolicy;
        }

        /**
         * Sets the value of the pricePolicy property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPricePolicy(String value) {
            this.pricePolicy = value;
        }

    }

}
