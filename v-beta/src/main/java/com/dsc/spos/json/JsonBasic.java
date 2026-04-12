package com.dsc.spos.json;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * RES, REQ 確定都會通用的, 才會寫在這邊
 * Created by Xavier on 2016/4/21.
 */
public class JsonBasic {

  private String shopId; /*门店编号*/
  private String shopName; //门店名称
  private String org_Form;//0:公司  2:门店
  private String organizationNO; /*组织编号*/
  private String eId; //企业编码

  private String langType = "zh_CN"; /*语言别*/
  private String opNO; //用户编码
  private String opName; //用户名称

  private String viewAbleDay; //业务单据可查看天数

  private String in_cost_warehouse;
  private String in_non_cost_warehouse;
  private String out_cost_warehouse;
  private String out_non_cost_warehouse;
  private String inv_cost_warehouse;
  private String inv_non_cost_warehouse;

  private String return_cost_warehouse;
  private String return_cost_warehouseName;
  private String in_cost_warehouseName;
  private String in_non_cost_warehouseName;
  private String out_cost_warehouseName;
  private String out_non_cost_warehouseName;
  private String inv_cost_warehouseName;
  private String inv_non_cost_warehouseName;

  @JSONField(name = "CITY")
  private String CITY;//城市

  @JSONField(name = "DISTRICT")
  private String DISTRICT;//区县

  @JSONField(name = "ENABLECREDIT")
  private String ENABLECREDIT;//启用信用额度,用于加盟店要货单确认

  @JSONField(name = "BELFIRM")
  private String BELFIRM;     //所属公司

  @JSONField(name = "BELFIRM_NAME")
  private String BELFIRM_NAME;//所属公司名称

  private String multiWarehouse;//是否多仓标记

  @JSONField(name = "EnableMultiLang")
  private String EnableMultiLang;//启用多语言

  @JSONField(name = "PageSizeDetail")
  private String PageSizeDetail;//单身分页大小

  private String defDepartNo;
  private String defDepartName;
  private String chatUserId;  //企业微信用户ID

  private String employeeNo;
  private String departmentNo;

  private String employeeName;

  private String departmentName;

  private String orgRange;

  private String defaultOrg;

  private String belOrgNo;
  private String belOrgName;
  private String upDepartNo;
  private String corp;
  private String corpName;

  private String taxPayerType;
  private String outputTaxCode;
  private String outputTaxName;
  private String outputTaxRate;
  private String inputTaxCode;
  private String inputTaxName;
  private String inputTaxRate;

  private String inputTaxCalType;
  private String inputTaxInclTax;

  private String outputTaxCalType;
  private String outputTaxInclTax;

  public String getDefDepartNo() {
    return defDepartNo;
  }

  public void setDefDepartNo(String defDepartNo) {
    this.defDepartNo = defDepartNo;
  }

  public String getDefDepartName() {
    return defDepartName;
  }

  public void setDefDepartName(String defDepartName) {
    this.defDepartName = defDepartName;
  }

  public String getPageSizeDetail() {
    return PageSizeDetail;
  }

  public void setPageSizeDetail(String pageSizeDetail) {
    PageSizeDetail = pageSizeDetail;
  }

  public String getEnableMultiLang() {
    return EnableMultiLang;
  }

  public void setEnableMultiLang(String enableMultiLang) {
    EnableMultiLang = enableMultiLang;
  }

  public String getBELFIRM() {
    return BELFIRM;
  }

  public void setBELFIRM(String bELFIRM) {
    BELFIRM = bELFIRM;
  }

  public String getBELFIRM_NAME() {
    return BELFIRM_NAME;
  }

  public void setBELFIRM_NAME(String bELFIRM_NAME) {
    BELFIRM_NAME = bELFIRM_NAME;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public String getShopId() {
    return shopId;
  }

  public void setShopId(String shopId) {
    this.shopId = shopId;
  }

  public String getOrganizationNO() {
    return organizationNO;
  }

  public void setOrganizationNO(String organizationNO) {
    this.organizationNO = organizationNO;
  }

  public String geteId() {
    return eId;
  }

  public void seteId(String eId) {
    this.eId = eId;
  }

  public String getLangType() {
    return langType;
  }

  public void setLangType(String langType) {
    this.langType = langType;
  }

  public String getOpNO() {
    return opNO;
  }

  public void setOpNO(String opNO) {
    this.opNO = opNO;
  }

  public String getOpName() {
    return opName;
  }

  public void setOpName(String opName) {
    this.opName = opName;
  }

  public String getIn_cost_warehouse() {
    return in_cost_warehouse;
  }

  public void setIn_cost_warehouse(String in_cost_warehouse) {
    this.in_cost_warehouse = in_cost_warehouse;
  }

  public String getIn_non_cost_warehouse() {
    return in_non_cost_warehouse;
  }

  public void setIn_non_cost_warehouse(String in_non_cost_warehouse) {
    this.in_non_cost_warehouse = in_non_cost_warehouse;
  }

  public String getOut_cost_warehouse() {
    return out_cost_warehouse;
  }

  public void setOut_cost_warehouse(String out_cost_warehouse) {
    this.out_cost_warehouse = out_cost_warehouse;
  }

  public String getOut_non_cost_warehouse() {
    return out_non_cost_warehouse;
  }

  public void setOut_non_cost_warehouse(String out_non_cost_warehouse) {
    this.out_non_cost_warehouse = out_non_cost_warehouse;
  }

  public String getInv_cost_warehouse() {
    return inv_cost_warehouse;
  }

  public void setInv_cost_warehouse(String inv_cost_warehouse) {
    this.inv_cost_warehouse = inv_cost_warehouse;
  }

  public String getInv_non_cost_warehouse() {
    return inv_non_cost_warehouse;
  }

  public void setInv_non_cost_warehouse(String inv_non_cost_warehouse) {
    this.inv_non_cost_warehouse = inv_non_cost_warehouse;
  }

  public String getIn_cost_warehouseName() {
    return in_cost_warehouseName;
  }

  public void setIn_cost_warehouseName(String in_cost_warehouseName) {
    this.in_cost_warehouseName = in_cost_warehouseName;
  }

  public String getIn_non_cost_warehouseName() {
    return in_non_cost_warehouseName;
  }

  public void setIn_non_cost_warehouseName(String in_non_cost_warehouseName) {
    this.in_non_cost_warehouseName = in_non_cost_warehouseName;
  }

  public String getOut_cost_warehouseName() {
    return out_cost_warehouseName;
  }

  public void setOut_cost_warehouseName(String out_cost_warehouseName) {
    this.out_cost_warehouseName = out_cost_warehouseName;
  }

  public String getOut_non_cost_warehouseName() {
    return out_non_cost_warehouseName;
  }

  public void setOut_non_cost_warehouseName(String out_non_cost_warehouseName) {
    this.out_non_cost_warehouseName = out_non_cost_warehouseName;
  }

  public String getInv_cost_warehouseName() {
    return inv_cost_warehouseName;
  }

  public void setInv_cost_warehouseName(String inv_cost_warehouseName) {
    this.inv_cost_warehouseName = inv_cost_warehouseName;
  }

  public String getInv_non_cost_warehouseName() {
    return inv_non_cost_warehouseName;
  }

  public void setInv_non_cost_warehouseName(String inv_non_cost_warehouseName) {
    this.inv_non_cost_warehouseName = inv_non_cost_warehouseName;
  }

  public String getCITY() {
    return CITY;
  }

  public void setCITY(String cITY) {
    CITY = cITY;
  }

  public String getDISTRICT() {
    return DISTRICT;
  }

  public void setDISTRICT(String dISTRICT) {
    DISTRICT = dISTRICT;
  }

  public String getENABLECREDIT() {
    return ENABLECREDIT;
  }

  public void setENABLECREDIT(String eNABLECREDIT) {
    ENABLECREDIT = eNABLECREDIT;
  }

  public String getOrg_Form() {
    return org_Form;
  }

  public void setOrg_Form(String org_Form) {
    this.org_Form = org_Form;
  }

  public String getMultiWarehouse() {
    return multiWarehouse;
  }

  public void setMultiWarehouse(String multiWarehouse) {
    this.multiWarehouse = multiWarehouse;
  }

  public String getViewAbleDay() {
    return viewAbleDay;
  }

  public void setViewAbleDay(String viewAbleDay) {
    this.viewAbleDay = viewAbleDay;
  }

  public String getChatUserId() {
    return chatUserId;
  }

  public void setChatUserId(String chatUserId) {
    this.chatUserId = chatUserId;
  }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getDepartmentNo() {
        return departmentNo;
    }

    public void setDepartmentNo(String departmentNo) {
        this.departmentNo = departmentNo;
    }

    public String getOrgRange() {
        return orgRange;
    }

    public void setOrgRange(String orgRange) {
        this.orgRange = orgRange;
    }

  public String getDefaultOrg() {
    return defaultOrg;
  }

  public void setDefaultOrg(String defaultOrg) {
    this.defaultOrg = defaultOrg;
  }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

  public String getBelOrgNo() {
    return belOrgNo;
  }

  public void setBelOrgNo(String belOrgNo) {
    this.belOrgNo = belOrgNo;
  }

  public String getBelOrgName() {
    return belOrgName;
  }

  public void setBelOrgName(String belOrgName) {
    this.belOrgName = belOrgName;
  }

  public String getUpDepartNo() {
    return upDepartNo;
  }

  public void setUpDepartNo(String upDepartNo) {
    this.upDepartNo = upDepartNo;
  }

    public String getReturn_cost_warehouse() {
        return return_cost_warehouse;
    }

    public void setReturn_cost_warehouse(String return_cost_warehouse) {
        this.return_cost_warehouse = return_cost_warehouse;
    }

    public String getReturn_cost_warehouseName() {
        return return_cost_warehouseName;
    }

    public void setReturn_cost_warehouseName(String return_cost_warehouseName) {
        this.return_cost_warehouseName = return_cost_warehouseName;
    }

    public String getCorp() {
        return corp;
    }

    public void setCorp(String corp) {
        this.corp = corp;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }



    public String getOutputTaxCode() {
        return outputTaxCode;
    }

    public void setOutputTaxCode(String outputTaxCode) {
        this.outputTaxCode = outputTaxCode;
    }

    public String getOutputTaxName() {
        return outputTaxName;
    }

    public void setOutputTaxName(String outputTaxName) {
        this.outputTaxName = outputTaxName;
    }

    public String getOutputTaxRate() {
        return outputTaxRate;
    }

    public void setOutputTaxRate(String outputTaxRate) {
        this.outputTaxRate = outputTaxRate;
    }

    public String getInputTaxCode() {
        return inputTaxCode;
    }

    public void setInputTaxCode(String inputTaxCode) {
        this.inputTaxCode = inputTaxCode;
    }

    public String getInputTaxName() {
        return inputTaxName;
    }

    public void setInputTaxName(String inputTaxName) {
        this.inputTaxName = inputTaxName;
    }

    public String getInputTaxRate() {
        return inputTaxRate;
    }

    public void setInputTaxRate(String inputTaxRate) {
        this.inputTaxRate = inputTaxRate;
    }

  public String getTaxPayerType() {
    return taxPayerType;
  }

  public void setTaxPayerType(String taxPayerType) {
    this.taxPayerType = taxPayerType;
  }

    public String getInputTaxCalType() {
        return inputTaxCalType;
    }

    public void setInputTaxCalType(String inputTaxCalType) {
        this.inputTaxCalType = inputTaxCalType;
    }

    public String getInputTaxInclTax() {
        return inputTaxInclTax;
    }

    public void setInputTaxInclTax(String inputTaxInclTax) {
        this.inputTaxInclTax = inputTaxInclTax;
    }

  public String getOutputTaxCalType() {
    return outputTaxCalType;
  }

  public void setOutputTaxCalType(String outputTaxCalType) {
    this.outputTaxCalType = outputTaxCalType;
  }

  public String getOutputTaxInclTax() {
    return outputTaxInclTax;
  }

  public void setOutputTaxInclTax(String outputTaxInclTax) {
    this.outputTaxInclTax = outputTaxInclTax;
  }
}
