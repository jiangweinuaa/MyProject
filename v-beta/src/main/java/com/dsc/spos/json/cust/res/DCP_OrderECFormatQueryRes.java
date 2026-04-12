package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 电商订单导入格式查询
 * @author yuanyy	2019-03-07
 *
 */
public class DCP_OrderECFormatQueryRes extends JsonRes {
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm {
		private String orderFormatNo;
		private String orderFormatName;

		private String ecplatformNo;
		private String ecplatformName;
		private String pickupWay;
		private String pickupWayName;
		private String startLine;
		private String fileFrom;
		private String ftp_uid;
		private String ftp_pwd;
		private String filePath;
		private String memberGet;
		private String orderShop;
		private String orderWarehouse;

		private String orderShopName;
		private String orderWarehouseName;

		private String currencyNo;
		private String status;

		private String customerNO;
		private String customerName;
		private String canInvoice;

		private List<level2Elm> datas;


		public String getOrderShopName() {
			return orderShopName;
		}

		public void setOrderShopName(String orderShopName) {
			this.orderShopName = orderShopName;
		}

		public String getOrderWarehouseName() {
			return orderWarehouseName;
		}

		public void setOrderWarehouseName(String orderWarehouseName) {
			this.orderWarehouseName = orderWarehouseName;
		}

		public String getPickupWayName() {
			return pickupWayName;
		}

		public String getOrderFormatNo() {
			return orderFormatNo;
		}

		public void setOrderFormatNo(String orderFormatNo) {
			this.orderFormatNo = orderFormatNo;
		}

		public String getOrderFormatName() {
			return orderFormatName;
		}

		public void setOrderFormatName(String orderFormatName) {
			this.orderFormatName = orderFormatName;
		}

		public String getEcplatformNo() {
			return ecplatformNo;
		}

		public void setEcplatformNo(String ecplatformNo) {
			this.ecplatformNo = ecplatformNo;
		}

		public String getEcplatformName() {
			return ecplatformName;
		}

		public void setEcplatformName(String ecplatformName) {
			this.ecplatformName = ecplatformName;
		}

		public String getPickupWay() {
			return pickupWay;
		}

		public void setPickupWay(String pickupWay) {
			this.pickupWay = pickupWay;
		}

		public String getPickupWayNname() {
			return pickupWayName;
		}

		public void setPickupWayName(String pickupWayName) {
			this.pickupWayName = pickupWayName;
		}

		public String getStartLine() {
			return startLine;
		}

		public void setStartLine(String startLine) {
			this.startLine = startLine;
		}

		public String getFileFrom() {
			return fileFrom;
		}

		public void setFileFrom(String fileFrom) {
			this.fileFrom = fileFrom;
		}

		public String getFtp_uid() {
			return ftp_uid;
		}

		public void setFtp_uid(String ftp_uid) {
			this.ftp_uid = ftp_uid;
		}

		public String getFtp_pwd() {
			return ftp_pwd;
		}

		public void setFtp_pwd(String ftp_pwd) {
			this.ftp_pwd = ftp_pwd;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

		public String getMemberGet() {
			return memberGet;
		}

		public void setMemberGet(String memberGet) {
			this.memberGet = memberGet;
		}

		public String getOrderShop() {
			return orderShop;
		}

		public void setOrderShop(String orderShop) {
			this.orderShop = orderShop;
		}

		public String getOrderWarehouse() {
			return orderWarehouse;
		}

		public void setOrderWarehouse(String orderWarehouse) {
			this.orderWarehouse = orderWarehouse;
		}

		public String getCurrencyNo() {
			return currencyNo;
		}

		public void setCurrencyNo(String currencyNo) {
			this.currencyNo = currencyNo;
		}




		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getCustomerNO() {
			return customerNO;
		}

		public void setCustomerNO(String customerNO) {
			this.customerNO = customerNO;
		}

		public String getCustomerName() {
			return customerName;
		}

		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}

		public String getCanInvoice() {
			return canInvoice;
		}

		public void setCanInvoice(String canInvoice) {
			this.canInvoice = canInvoice;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
	}

	public class level2Elm{
		private String item;
		private String tableName;
		private String fieldName;
		private String fieldMemo;
		private String fromType;
		private String fromValue;
		private String splitColumn;
		private String status;
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getFieldMemo() {
			return fieldMemo;
		}
		public void setFieldMemo(String fieldMemo) {
			this.fieldMemo = fieldMemo;
		}
		public String getFromType() {
			return fromType;
		}
		public void setFromType(String fromType) {
			this.fromType = fromType;
		}
		public String getFromValue() {
			return fromValue;
		}
		public void setFromValue(String fromValue) {
			this.fromValue = fromValue;
		}
		public String getSplitColumn() {
			return splitColumn;
		}
		public void setSplitColumn(String splitColumn) {
			this.splitColumn = splitColumn;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}


	}

}
