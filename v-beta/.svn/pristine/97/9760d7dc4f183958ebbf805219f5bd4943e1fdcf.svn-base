package com.dsc.spos.json.cust.res;

import java.security.PrivateKey;
import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

/**
 * StockOutGet 專用的 response json
 * 
 * @author panjing
 * @since 2016-09-22
 */
public class DCP_StockOutQueryRes extends JsonRes {

	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

    @Data
	public class level1Elm {
		private String shopId;
		private String eId;
		private String stockOutNo;
		private String processERPNo;
		private String bDate;
		private String memo;
		private String status;
		private String docType;
		private String transferShop;
		private String transferShopName;
		private String oType;
		private String ofNo;
		private String bsNo;
		private String bsName;
		private String loadDocType;
		private String loadDocNo;
		private String createBy;
		private String createDate;
		private String createTime;
		private String createByName;
		private String confirmBy;
		private String confirmDate;
		private String confirmTime;
		private String confirmByName;
		private String receiptOrg;
		private String receiptOrgName;
		private String accountBy;
		private String accountDate;
		private String accountTime;
		private String accountByName;
		private String cancelBy;
		private String cancelDate;
		private String cancelTime;
		private String cancelByName;
		private String submitBy;
		private String submitDate;
		private String submitTime;
		private String submitByName;
		private String modifyBy;
		private String modifyDate;
		private String modifyTime;
		private String modifyByName;
		private String totPqty;
		private String totAmt;
		private String totCqty;
		private String totDistriAmt;
		private String deliveryNo;
		private String diffStatus;
		private String warehouse;
		private String warehouseName;
		private String transferWarehouse;
		private String transferWarehouseName;
		private String update_time;
		private String process_status;
		private String pTemplateNo;//模板编码
		private String pTemplateName;//模板名称
		private String sourceMenu;
        private String stockOutNo_refund;
        private String stockOutNo_origin;
		private String rejectReason;
        private String deliveryBy;
		private String deliveryName;
		private String deliveryTel;

        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String receiptDate;
        private String packingNo;
        private String invWarehouse;
        private String invWarehouseName;
        private String isLocation;
        private String isBatchManage;
        private String deliveryDate;
        private String ooType;
        private String oofNo;
        private String isTranInConfirm;
        private List<StockInInfo> stockInNo;

        private String corp;
        private String receiptCorp;

    }

    @Data
    public class StockInInfo{
        private String stockInNo;
        private String stockInNo_origin;
    }
}
