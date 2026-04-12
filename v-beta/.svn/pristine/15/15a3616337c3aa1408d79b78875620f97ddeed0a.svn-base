package com.dsc.spos.json.cust.res;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import com.dsc.spos.json.JsonBasicRes;

public class DCP_LoginRetailRes extends JsonBasicRes
{
	private String token;
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	
	public class level1Elm {
		private String viewAbleDay;//业务单据可查看天数
		private String isNew; //是否第一次登录中台
		private String isCheckUser; // 首次登陆是否修改密码
        private String shopId;//登录默认组织
		private String shopName;

		private String org_Form;     //0:公司  2:门店
		private String disCentre;   //是否配送中心 //Y是
		private String warehouse;
		private String organizationNo;
		private String eId;
		private String bDate;
		private String langType;
		private String opNo;
		private String opName;
		private String dayType;//查询界面日期默认值  0:本日  1：本周
		private String token;
		private String in_cost_warehouse;
		private String in_non_cost_warehouse;
		private String out_cost_warehouse;
		private String out_non_cost_warehouse;
		private String inv_cost_warehouse;
		private String inv_non_cost_warehouse;

        private String return_cost_warehouse;
        private String return_cost_warehouse_name;

		private String in_cost_warehouse_name;
		private String in_non_cost_warehouse_name;
		private String out_cost_warehouse_name;
		private String out_non_cost_warehouse_name;
		private String inv_cost_warehouse_name;
		private String inv_non_cost_warehouse_name;

		@JSONField(name = "CITY")
		private String CITY;

		@JSONField(name = "DISTRICT")
		private String DISTRICT;

		@JSONField(name = "ENABLECREDIT")
		private String ENABLECREDIT;//启用信用额度

		@JSONField(name = "BELFIRM")
		private String BELFIRM;     //所属公司

		@JSONField(name = "BELFIRM_NAME")
		private String BELFIRM_NAME;//所属公司名称

		private String multiWarehouse;//是否多仓标记

		@JSONField(name = "EnableMultiLang")
		private String EnableMultiLang;//启用多语言

		@JSONField(name = "PageSizeDetail")
		private String PageSizeDetail;//单身分页大小

		private String trade; //行业
		private String orderstatustype;
		private String oschannel;
		private String isShowDistriPrice; //是否显示配送价
		private String disc;
		private List<paras> dataParas;
		private String defDepartNo;
		private String defDepartName;
		private String chatUserId;   //企业微信用户ID
		private String org_type;    //组织类型 0-直营  1-加盟(强)  2加盟（弱）


		private String employeeNo;

        private String employeeName;

        private String departmentName;

		private String departmentNo;
        private String corp;
        private String corpName;

		private String orgRange;

		private String defaultOrg;

        private String belOrgNo;

        private String belOrgName;
		private String upDepartNo;

        private String taxPayerType;
        private String inputTaxCode;
		private String inputTaxName;
        private String inputTaxRate;
        private String outputTaxRate;
        private String outputTaxCode;
		private String outputTaxName;

        private String inputTaxCalType;
        private String inputTaxInclTax;
		private String outputTaxCalType;
		private String outputTaxInclTax;

        private List<levelwarehouseElm> myWarehouse;
		
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
		public String getViewAbleDay() {
			return viewAbleDay;
		}
		public void setViewAbleDay(String viewAbleDay) {
			this.viewAbleDay = viewAbleDay;
		}
		public String getOrganizationNo() {
			return organizationNo;
		}
		public void setOrganizationNo(String organizationNo) {
			this.organizationNo = organizationNo;
		}
		public String getOpNo() {
			return opNo;
		}
		public void setOpNo(String opNo) {
			this.opNo = opNo;
		}
		public List<paras> getDataParas() {
			return dataParas;
		}
		public void setDataParas(List<paras> dataParas) {
			this.dataParas = dataParas;
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
		private List<level2Shops> myShops;
		private List<level2Powers> myPower;

		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getLangType() {
			return langType;
		}
		public void setLangType(String langType) {
			this.langType = langType;
		}
		public String getOpName() {
			return opName;
		}
		public void setOpName(String opName) {
			this.opName = opName;
		}
		public String getDayType() {
			return dayType;
		}
		public void setDayType(String dayType) {
			this.dayType = dayType;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public List<level2Shops> getMyShops() {
			return myShops;
		}
		public void setMyShops(List<level2Shops> myShops) {
			this.myShops = myShops;
		}
		public List<level2Powers> getMyPower() {
			return myPower;
		}
		public void setMyPower(List<level2Powers> myPower) {
			this.myPower = myPower;
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
		public String getIn_cost_warehouse_name() {
			return in_cost_warehouse_name;
		}
		public void setIn_cost_warehouse_name(String in_cost_warehouse_name) {
			this.in_cost_warehouse_name = in_cost_warehouse_name;
		}
		public String getIn_non_cost_warehouse_name() {
			return in_non_cost_warehouse_name;
		}
		public void setIn_non_cost_warehouse_name(String in_non_cost_warehouse_name) {
			this.in_non_cost_warehouse_name = in_non_cost_warehouse_name;
		}
		public String getOut_cost_warehouse_name() {
			return out_cost_warehouse_name;
		}
		public void setOut_cost_warehouse_name(String out_cost_warehouse_name) {
			this.out_cost_warehouse_name = out_cost_warehouse_name;
		}
		public String getOut_non_cost_warehouse_name() {
			return out_non_cost_warehouse_name;
		}
		public void setOut_non_cost_warehouse_name(String out_non_cost_warehouse_name) {
			this.out_non_cost_warehouse_name = out_non_cost_warehouse_name;
		}
		public String getInv_cost_warehouse_name() {
			return inv_cost_warehouse_name;
		}
		public void setInv_cost_warehouse_name(String inv_cost_warehouse_name) {
			this.inv_cost_warehouse_name = inv_cost_warehouse_name;
		}
		public String getInv_non_cost_warehouse_name() {
			return inv_non_cost_warehouse_name;
		}
		public void setInv_non_cost_warehouse_name(String inv_non_cost_warehouse_name) {
			this.inv_non_cost_warehouse_name = inv_non_cost_warehouse_name;
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
		public String getIsNew() {
			return isNew;
		}
		public void setIsNew(String isNew) {
			this.isNew = isNew;
		}
		public String getIsCheckUser() {
			return isCheckUser;
		}
		public void setIsCheckUser(String isCheckUser) {
			this.isCheckUser = isCheckUser;
		}
		public String getMultiWarehouse() {
			return multiWarehouse;
		}
		public void setMultiWarehouse(String multiWarehouse) {
			this.multiWarehouse = multiWarehouse;
		}
		public String getTrade() {
			return trade;
		}
		public void setTrade(String trade) {
			this.trade = trade;
		}
		public String getOrderstatustype() {
			return orderstatustype;
		}
		public void setOrderstatustype(String orderstatustype) {
			this.orderstatustype = orderstatustype;
		}
		public String getOschannel() {
			return oschannel;
		}
		public void setOschannel(String oschannel) {
			this.oschannel = oschannel;
		}
		public String getIsShowDistriPrice() {
			return isShowDistriPrice;
		}
		public void setIsShowDistriPrice(String isShowDistriPrice) {
			this.isShowDistriPrice = isShowDistriPrice;
		}
		public String getDisc() {
			return disc;
		}
		public void setDisc(String disc) {
			this.disc = disc;
		}
		public String getDisCentre() {
			return disCentre;
		}
		public void setDisCentre(String disCentre) {
			this.disCentre = disCentre;
		}
		public String getChatUserId() {
			return chatUserId;
		}
		public void setChatUserId(String chatUserId) {
			this.chatUserId = chatUserId;
		}
		public String getOrg_type() {
			return org_type;
		}
		public void setOrg_type(String org_type) {
			this.org_type = org_type;
		}

		public List<levelwarehouseElm> getMyWarehouse()
		{
			return myWarehouse;
		}

		public void setMyWarehouse(List<levelwarehouseElm> myWarehouse)
		{
			this.myWarehouse = myWarehouse;
		}

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public String getShopName() {
			return shopName;
		}

		public void setShopName(String shopName) {
			this.shopName = shopName;
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

        public String getReturn_cost_warehouse_name() {
            return return_cost_warehouse_name;
        }

        public void setReturn_cost_warehouse_name(String return_cost_warehouse_name) {
            this.return_cost_warehouse_name = return_cost_warehouse_name;
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

        public String getTaxPayerType() {
            return taxPayerType;
        }

        public void setTaxPayerType(String taxPayerType) {
            this.taxPayerType = taxPayerType;
        }

        public String getInputTaxCode() {
            return inputTaxCode;
        }

        public void setInputTaxCode(String inputTaxCode) {
            this.inputTaxCode = inputTaxCode;
        }

        public String getInputTaxRate() {
            return inputTaxRate;
        }

        public void setInputTaxRate(String inputTaxRate) {
            this.inputTaxRate = inputTaxRate;
        }

        public String getOutputTaxRate() {
            return outputTaxRate;
        }

        public void setOutputTaxRate(String outputTaxRate) {
            this.outputTaxRate = outputTaxRate;
        }

        public String getOutputTaxCode() {
            return outputTaxCode;
        }

        public void setOutputTaxCode(String outputTaxCode) {
            this.outputTaxCode = outputTaxCode;
        }

		public String getInputTaxName() {
			return inputTaxName;
		}

		public void setInputTaxName(String inputTaxName) {
			this.inputTaxName = inputTaxName;
		}

		public String getOutputTaxName() {
			return outputTaxName;
		}

		public void setOutputTaxName(String outputTaxName) {
			this.outputTaxName = outputTaxName;
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
	public class level2Shops {
		//private String shopId;
		//private String shopName;

		private String orgNo;

		private String orgName;
		private String org_Form;     //0:公司  2:门店
		//是否配送中心
		private String disCentre;    //Y是
		private String isDefault;

		private String isExpend;

		//private List<level2Shops> children;
		
		public String getIsDefault() {
			return isDefault;
		}
		public void setIsDefault(String isDefault) {
			this.isDefault = isDefault;
		}

		public String getOrg_Form() {
			return org_Form;
		}
		public void setOrg_Form(String org_Form) {
			this.org_Form = org_Form;
		}
		public String getDisCentre() {
			return disCentre;
		}
		public void setDisCentre(String disCentre) {
			this.disCentre = disCentre;
		}

		public String getOrgNo() {
			return orgNo;
		}

		public void setOrgNo(String orgNo) {
			this.orgNo = orgNo;
		}

		public String getOrgName() {
			return orgName;
		}

		public void setOrgName(String orgName) {
			this.orgName = orgName;
		}

		//public List<level2Shops> getChildren() {
			//return children;
		//}

		//public void setChildren(List<level2Shops> children) {
		//	this.children = children;
		//}

		public String getIsExpend() {
			return isExpend;
		}

		public void setIsExpend(String isExpend) {
			this.isExpend = isExpend;
		}
	}
	public class level2Powers {
		private String modularNo;
		private String modularName;
		private String upModularNo;
		private String modularLevel;
		private String isCollection;
		private String sysReportURL;//报表路径:http://172.16.100.153:8080/WebReport/ReportServer?reportlet=myWebReports
		private String proName;//报表名称:myPara1.cpt
		private String Mftype;//报表类型：3
		private String Mparameter;//报表参数:
		private String isMask; //遮罩
		private List<level2Powers> children;
		private List<lv_Func> datas;
		
		public String getModularNo() {
			return modularNo;
		}
		public void setModularNo(String modularNo) {
			this.modularNo = modularNo;
		}
		public String getUpModularNo() {
			return upModularNo;
		}
		public void setUpModularNo(String upModularNo) {
			this.upModularNo = upModularNo;
		}
		public List<level2Powers> getChildren() {
			return children;
		}
		public void setChildren(List<level2Powers> children) {
			this.children = children;
		}
		public String getIsMask() {
			return isMask;
		}
		public void setIsMask(String isMask) {
			this.isMask = isMask;
		}
		public String getModularName() {
			return modularName;
		}
		public void setModularName(String modularName) {
			this.modularName = modularName;
		}
		public String getModularLevel() {
			return modularLevel;
		}
		public void setModularLevel(String modularLevel) {
			this.modularLevel = modularLevel;
		}
		public String getIsCollection() {
			return isCollection;
		}
		public void setIsCollection(String isCollection) {
			this.isCollection = isCollection;
		}
		public String getSysReportURL() {
			return sysReportURL;
		}
		public void setSysReportURL(String sysReportURL) {
			this.sysReportURL = sysReportURL;
		}
		public String getProName() {
			return proName;
		}
		public void setProName(String proName) {
			this.proName = proName;
		}
		public List<lv_Func> getDatas() {
			return datas;
		}
		public void setDatas(List<lv_Func> datas) {
			this.datas = datas;
		}
		public String getMftype()
		{
			return Mftype;
		}
		public void setMftype(String mftype)
		{
			Mftype = mftype;
		}
		public String getMparameter()
		{
			return Mparameter;
		}
		public void setMparameter(String mparameter)
		{
			Mparameter = mparameter;
		}
		//		public List<lv2_modular> getChildren() {
		//			return children;
		//		}
		//		public void setChildren(List<lv2_modular> children) {
		//			this.children = children;
		//		}
		
		
	}
	public class lv_Func {
		private String functionNo;
		private String functionName;
		private String powerType;
		private String proName;
		
		public String getFunctionNo() {
			return functionNo;
		}
		public void setFunctionNo(String functionNo) {
			this.functionNo = functionNo;
		}
		public String getFunctionName() {
			return functionName;
		}
		public void setFunctionName(String functionName) {
			this.functionName = functionName;
		}
		public String getPowerType() {
			return powerType;
		}
		public void setPowerType(String powerType) {
			this.powerType = powerType;
		}
		public String getProName() {
			return proName;
		}
		public void setProName(String proName) {
			this.proName = proName;
		}
	}
	/**
	 * 二级菜单
	 * @author Administrator
	 *
	 */
	public class lv2_modular {
		private String modularNo;
		private String modularName;
		private String upModularNo;
		private String modularLevel;
		private String isCollection;
		private String sysReportURL;//报表路径
		private String proName;//报表名称:myPara1.cpt
		private String Mftype;//报表类型：3
		private String Mparameter;//报表参数:
		private String isMask;//遮罩
		private List<lv3_modular> children;
		private List<lv_Func> datas;
		
		public String getModularNo() {
			return modularNo;
		}
		public void setModularNo(String modularNo) {
			this.modularNo = modularNo;
		}
		public String getUpModularNo() {
			return upModularNo;
		}
		public void setUpModularNo(String upModularNo) {
			this.upModularNo = upModularNo;
		}
		public String getIsMask() {
			return isMask;
		}
		public void setIsMask(String isMask) {
			this.isMask = isMask;
		}
		public String getModularName() {
			return modularName;
		}
		public void setModularName(String modularName) {
			this.modularName = modularName;
		}
		public String getModularLevel() {
			return modularLevel;
		}
		public void setModularLevel(String modularLevel) {
			this.modularLevel = modularLevel;
		}
		public String getIsCollection() {
			return isCollection;
		}
		public void setIsCollection(String isCollection) {
			this.isCollection = isCollection;
		}
		public String getSysReportURL() {
			return sysReportURL;
		}
		public void setSysReportURL(String sysReportURL) {
			this.sysReportURL = sysReportURL;
		}
		public String getProName() {
			return proName;
		}
		public void setProName(String proName) {
			this.proName = proName;
		}
		public String getMftype() {
			return Mftype;
		}
		public void setMftype(String mftype) {
			Mftype = mftype;
		}
		public String getMparameter() {
			return Mparameter;
		}
		public void setMparameter(String mparameter) {
			Mparameter = mparameter;
		}
		public List<lv_Func> getDatas() {
			return datas;
		}
		public void setDatas(List<lv_Func> datas) {
			this.datas = datas;
		}
		public List<lv3_modular> getChildren() {
			return children;
		}
		public void setChildren(List<lv3_modular> children) {
			this.children = children;
		}
		
	}
	public class lv3_modular {
		private String modularNo;
		private String modularName;
		private String upModularNo;
		private String modularLevel;
		private String isCollection;
		private String sysReportURL;//报表路径:http://172.16.100.153:8080/WebReport/ReportServer?reportlet=myWebReports
		private String proName;//报表名称:myPara1.cpt
		private String Mftype;//报表类型：3
		private String Mparameter;//报表参数
		private String isMask;//遮罩
		private List<lv_Func> datas;
		
		public String getModularNo() {
			return modularNo;
		}
		public void setModularNo(String modularNo) {
			this.modularNo = modularNo;
		}
		public String getUpModularNo() {
			return upModularNo;
		}
		public void setUpModularNo(String upModularNo) {
			this.upModularNo = upModularNo;
		}
		public String getIsMask() {
			return isMask;
		}
		public void setIsMask(String isMask) {
			this.isMask = isMask;
		}
		public String getModularName() {
			return modularName;
		}
		public void setModularName(String modularName) {
			this.modularName = modularName;
		}
		public String getModularLevel() {
			return modularLevel;
		}
		public void setModularLevel(String modularLevel) {
			this.modularLevel = modularLevel;
		}
		public String getIsCollection() {
			return isCollection;
		}
		public void setIsCollection(String isCollection) {
			this.isCollection = isCollection;
		}
		public String getSysReportURL() {
			return sysReportURL;
		}
		public void setSysReportURL(String sysReportURL) {
			this.sysReportURL = sysReportURL;
		}
		public String getProName() {
			return proName;
		}
		public void setProName(String proName) {
			this.proName = proName;
		}
		public String getMftype() {
			return Mftype;
		}
		public void setMftype(String mftype) {
			Mftype = mftype;
		}
		public String getMparameter() {
			return Mparameter;
		}
		public void setMparameter(String mparameter) {
			Mparameter = mparameter;
		}
		public List<lv_Func> getDatas() {
			return datas;
		}
		public void setDatas(List<lv_Func> datas) {
			this.datas = datas;
		}
		
	}
	//系统参数列表
	public class paras {
		private String paraName;
		private String paraValue;
		
		public String getParaName() {
			return paraName;
		}
		public void setParaName(String paraName) {
			this.paraName = paraName;
		}
		public String getParaValue() {
			return paraValue;
		}
		public void setParaValue(String paraValue) {
			this.paraValue = paraValue;
		}
	}

    public class levelwarehouseElm
    {
        private String warehouseNo;
        private String warehouseName;
        private String isDefault;
        private String organizationNo;
        private String organizationName;



        public String getWarehouseNo()
        {
            return warehouseNo;
        }

        public void setWarehouseNo(String warehouseNo)
        {
            this.warehouseNo = warehouseNo;
        }

        public String getWarehouseName()
        {
            return warehouseName;
        }

        public void setWarehouseName(String warehouseName)
        {
            this.warehouseName = warehouseName;
        }

        public String getIsDefault()
        {
            return isDefault;
        }

        public void setIsDefault(String isDefault)
        {
            this.isDefault = isDefault;
        }

        public String getOrganizationNo()
        {
            return organizationNo;
        }

        public void setOrganizationNo(String organizationNo)
        {
            this.organizationNo = organizationNo;
        }

        public String getOrganizationName()
        {
            return organizationName;
        }

        public void setOrganizationName(String organizationName)
        {
            this.organizationName = organizationName;
        }
    }

}
