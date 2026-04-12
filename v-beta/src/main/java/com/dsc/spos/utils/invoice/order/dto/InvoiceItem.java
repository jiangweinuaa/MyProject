package com.dsc.spos.utils.invoice.order.dto;

import java.math.BigDecimal;

/**
 * 发票项目明细
 */
public class InvoiceItem {
	/**
	 * 发票行性质 0 正常行、1 折扣行、2 被折扣行
	 */
	private String type;

	/**
	 * 商品编码
	 */
	private String code;

	/**
	 * 商品名称
	 */
	private String name;

	/**
	 * 规格型号
	 */
	private String spec;

	/**
	 * 含税商品单价
	 */
	private BigDecimal price;

	/**
     * 数量
     */
    private BigDecimal quantity;

	/**
	 * 单位
	 */
	private String uom;

    /**
     * 税率
     */
    private BigDecimal taxRate;

	/**
	 * 税价合计金额
	 */
	private BigDecimal amount;

	/**
	 * 不含税金额
	 */
	private BigDecimal noTaxAmount;

	/**
	 * 税额
	 */
	private BigDecimal taxAmount;

	/**
	 * 商品分类编码
	 */
	private String catalogCode;

	/**
	 * 优惠政策标识
	 */
	private String preferentialPolicyFlg;

	/**
	 * 增值税特殊管理
	 */
	private String addedValueTaxFlg;

	/**
	 * 零税率标识
	 */
	private String zeroTaxRateFlg;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getNoTaxAmount() {
		return noTaxAmount;
	}

	public void setNoTaxAmount(BigDecimal noTaxAmount) {
		this.noTaxAmount = noTaxAmount;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getCatalogCode() {
		return catalogCode;
	}

	public void setCatalogCode(String catalogCode) {
		this.catalogCode = catalogCode;
	}

	public String getPreferentialPolicyFlg() {
		return preferentialPolicyFlg;
	}

	public void setPreferentialPolicyFlg(String preferentialPolicyFlg) {
		this.preferentialPolicyFlg = preferentialPolicyFlg;
	}

	public String getAddedValueTaxFlg() {
		return addedValueTaxFlg;
	}

	public void setAddedValueTaxFlg(String addedValueTaxFlg) {
		this.addedValueTaxFlg = addedValueTaxFlg;
	}

	public String getZeroTaxRateFlg() {
		return zeroTaxRateFlg;
	}

	public void setZeroTaxRateFlg(String zeroTaxRateFlg) {
		this.zeroTaxRateFlg = zeroTaxRateFlg;
	}
}