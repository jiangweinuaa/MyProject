package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_DingApproveGet
 * 服务说明：审批查询
 * @author jinzma 
 * @since  2019-12-31
 */
public class DCP_DingApproveQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String eId;
		private String processId;
		private String shopId;
		private String shopName;
		private String billNo;
		private String billType;
		private String funcNo;
		private String funcName;
		private String createTime;
		private String createOpName;
		private String applyReason;
		private String rejectReason;
		private String checkTime;
		private String checkOpName;
		private String status;
		private String processStatus;
		private List<level2Elm> approveDetail;

		public String geteId() {
			return eId;
		}
		public void seteId(String eId) {
			this.eId = eId;
		}
		public String getProcessId() {
			return processId;
		}
		public void setProcessId(String processId) {
			this.processId = processId;
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
		public String getBillNo() {
			return billNo;
		}
		public void setBillNo(String billNo) {
			this.billNo = billNo;
		}
		public String getBillType() {
			return billType;
		}
		public void setBillType(String billType) {
			this.billType = billType;
		}
		public String getFuncNo() {
			return funcNo;
		}
		public void setFuncNo(String funcNo) {
			this.funcNo = funcNo;
		}
		public String getFuncName() {
			return funcName;
		}
		public void setFuncName(String funcName) {
			this.funcName = funcName;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getCreateOpName() {
			return createOpName;
		}
		public void setCreateOpName(String createOpName) {
			this.createOpName = createOpName;
		}
		public String getApplyReason() {
			return applyReason;
		}
		public void setApplyReason(String applyReason) {
			this.applyReason = applyReason;
		}
		public String getRejectReason() {
			return rejectReason;
		}
		public void setRejectReason(String rejectReason) {
			this.rejectReason = rejectReason;
		}
		public String getCheckTime() {
			return checkTime;
		}
		public void setCheckTime(String checkTime) {
			this.checkTime = checkTime;
		}
		public String getCheckOpName() {
			return checkOpName;
		}
		public void setCheckOpName(String checkOpName) {
			this.checkOpName = checkOpName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getProcessStatus() {
			return processStatus;
		}
		public void setProcessStatus(String processStatus) {
			this.processStatus = processStatus;
		}
		public List<level2Elm> getApproveDetail() {
			return approveDetail;
		}
		public void setApproveDetail(List<level2Elm> approveDetail) {
			this.approveDetail = approveDetail;
		}


	}
	public class level2Elm
	{
		private String item;
		private String goodsId;
		private String goodsName;
		private String unitName;
		private String qty;
		private String price;
		private String amt;
		private String discAmt;

		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		public String getGoodsId() {
			return goodsId;
		}
		public void setGoodsId(String goodsId) {
			this.goodsId = goodsId;
		}
		public String getGoodsName() {
			return goodsName;
		}
		public void setGoodsName(String goodsName) {
			this.goodsName = goodsName;
		}
		public String getUnitName() {
			return unitName;
		}
		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}
		public String getQty() {
			return qty;
		}
		public void setQty(String qty) {
			this.qty = qty;
		}
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
		public String getDiscAmt() {
			return discAmt;
		}
		public void setDiscAmt(String discAmt) {
			this.discAmt = discAmt;
		}




	}

}
