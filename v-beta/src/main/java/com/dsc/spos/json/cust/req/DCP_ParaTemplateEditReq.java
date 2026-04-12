package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * V3模版参数设置#add
 * @author 2020-06-02
 *
 */
public class DCP_ParaTemplateEditReq extends JsonBasicReq {
	
	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		
		private String templateId;
		private String templateName;
		private String templateType;
		private String restrictShop;
		private String restrictMachine;
		
		public List<ShopList> shopList;
		private List<MachineList> machineList; 
		private List<ClassList> classList;
		
		public String getTemplateId() {
			return templateId;
		}
		public String getTemplateName() {
			return templateName;
		}
		public String getTemplateType() {
			return templateType;
		}
		public String getRestrictShop() {
			return restrictShop;
		}
		public String getRestrictMachine() {
			return restrictMachine;
		}
		public List<ShopList> getShopList() {
			return shopList;
		}
		public List<MachineList> getMachineList() {
			return machineList;
		}
		public List<ClassList> getClassList() {
			return classList;
		}
		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
		public void setTemplateType(String templateType) {
			this.templateType = templateType;
		}
		public void setRestrictShop(String restrictShop) {
			this.restrictShop = restrictShop;
		}
		public void setRestrictMachine(String restrictMachine) {
			this.restrictMachine = restrictMachine;
		}
		public void setShopList(List<ShopList> shopList) {
			this.shopList = shopList;
		}
		public void setMachineList(List<MachineList> machineList) {
			this.machineList = machineList;
		}
		public void setClassList(List<ClassList> classList) {
			this.classList = classList;
		} 
		
		
		
	}
	
	public class ShopList{
		
		private String shopId;
		private String shopName;
		
		public String getShopId() {
			return shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		
	}
	public class MachineList{
		private String shopId;
		private String shopName;
		private String machineId;
		private String machineName;
		
		public String getShopId() {
			return shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public String getMachineId() {
			return machineId;
		}
		public String getMachineName() {
			return machineName;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}
		public void setMachineId(String machineId) {
			this.machineId = machineId;
		}
		public void setMachineName(String machineName) {
			this.machineName = machineName;
		}
		
	}
	public class ClassList{
		private String classNo;// classNo 规格中没有，方便后期维护和查问题，我先加上
		private String className;
		
		private List<ItemList> itemList;
		
		public String getClassNo() {
			return classNo;
		}

		public void setClassNo(String classNo) {
			this.classNo = classNo;
		}

		public String getClassName() {
			return className;
		}

		public List<ItemList> getItemList() {
			return itemList;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public void setItemList(List<ItemList> itemList) {
			this.itemList = itemList;
		}
		
	}
	

	public class ItemList {
		private String item;
		private String itemValue;
		
		public String getItem() {
			return item;
		}
		public String getItemValue() {
			return itemValue;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public void setItemValue(String itemValue) {
			this.itemValue = itemValue;
		}
		
	}





}
