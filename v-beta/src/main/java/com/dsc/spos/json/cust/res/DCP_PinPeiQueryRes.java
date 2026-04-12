package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
/**
 * 服务函数：DCP_PinPeiQuery
 * 服务说明：拼胚查询
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiQueryRes extends JsonRes{

	private level1Elm datas;

	public level1Elm getDatas() {
		return datas;
	}
	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}

	public class level1Elm{
		private List<level2Elm> pinPeiList;

		public List<level2Elm> getPinPeiList() {
			return pinPeiList;
		}
		public void setPinPeiList(List<level2Elm> pinPeiList) {
			this.pinPeiList = pinPeiList;
		}
	}
	public class level2Elm{
		private String memo;
		private String pinPeiNo;
		private String bDate;
		private String stockInNo;
		private String stockOutNo;
		private String stockInWearehouse;
		private String stockInWearehouseName;
		private String stockOutWearehouse;
		private String stockOutWearehouseName;
		private String stockInBsNo;
		private String stockInBsName;
		private String stockOutBsNo;
		private String stockOutBsName;
		private String status;
		private String totPqty;
		private String totCqty;
		private String totAmt;
		private String totDistriAmt;
		private String createOpId;
		private String createOpName;
		private String createTime;
		private String lastModiOpId;
		private String lastModiOpName;
		private String lastModiTime;

		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getPinPeiNo() {
			return pinPeiNo;
		}
		public void setPinPeiNo(String pinPeiNo) {
			this.pinPeiNo = pinPeiNo;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getStockInNo() {
			return stockInNo;
		}
		public void setStockInNo(String stockInNo) {
			this.stockInNo = stockInNo;
		}
		public String getStockOutNo() {
			return stockOutNo;
		}
		public void setStockOutNo(String stockOutNo) {
			this.stockOutNo = stockOutNo;
		}
		public String getStockInWearehouse() {
			return stockInWearehouse;
		}
		public void setStockInWearehouse(String stockInWearehouse) {
			this.stockInWearehouse = stockInWearehouse;
		}
		public String getStockInWearehouseName() {
			return stockInWearehouseName;
		}
		public void setStockInWearehouseName(String stockInWearehouseName) {
			this.stockInWearehouseName = stockInWearehouseName;
		}
		public String getStockOutWearehouse() {
			return stockOutWearehouse;
		}
		public void setStockOutWearehouse(String stockOutWearehouse) {
			this.stockOutWearehouse = stockOutWearehouse;
		}
		public String getStockOutWearehouseName() {
			return stockOutWearehouseName;
		}
		public void setStockOutWearehouseName(String stockOutWearehouseName) {
			this.stockOutWearehouseName = stockOutWearehouseName;
		}
		public String getStockInBsNo() {
			return stockInBsNo;
		}
		public void setStockInBsNo(String stockInBsNo) {
			this.stockInBsNo = stockInBsNo;
		}
		public String getStockInBsName() {
			return stockInBsName;
		}
		public void setStockInBsName(String stockInBsName) {
			this.stockInBsName = stockInBsName;
		}
		public String getStockOutBsNo() {
			return stockOutBsNo;
		}
		public void setStockOutBsNo(String stockOutBsNo) {
			this.stockOutBsNo = stockOutBsNo;
		}
		public String getStockOutBsName() {
			return stockOutBsName;
		}
		public void setStockOutBsName(String stockOutBsName) {
			this.stockOutBsName = stockOutBsName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getTotPqty() {
			return totPqty;
		}
		public void setTotPqty(String totPqty) {
			this.totPqty = totPqty;
		}
		public String getTotCqty() {
			return totCqty;
		}
		public void setTotCqty(String totCqty) {
			this.totCqty = totCqty;
		}
		public String getTotAmt() {
			return totAmt;
		}
		public void setTotAmt(String totAmt) {
			this.totAmt = totAmt;
		}
		public String getTotDistriAmt() {
			return totDistriAmt;
		}
		public void setTotDistriAmt(String totDistriAmt) {
			this.totDistriAmt = totDistriAmt;
		}
		public String getCreateOpId() {
			return createOpId;
		}
		public void setCreateOpId(String createOpId) {
			this.createOpId = createOpId;
		}
		public String getCreateOpName() {
			return createOpName;
		}
		public void setCreateOpName(String createOpName) {
			this.createOpName = createOpName;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getLastModiOpId() {
			return lastModiOpId;
		}
		public void setLastModiOpId(String lastModiOpId) {
			this.lastModiOpId = lastModiOpId;
		}
		public String getLastModiOpName() {
			return lastModiOpName;
		}
		public void setLastModiOpName(String lastModiOpName) {
			this.lastModiOpName = lastModiOpName;
		}
		public String getLastModiTime() {
			return lastModiTime;
		}
		public void setLastModiTime(String lastModiTime) {
			this.lastModiTime = lastModiTime;
		}
	}
}
