package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：PStockInGet
 *    說明：完工入库查询
 * 服务说明：完工入库查询
 * @author luoln
 * @since  2017-04-01
 */
public class DCP_PStockInQueryRes extends JsonRes {
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm {
		private String pStockInNo;
		private String bDate;
		private String memo;
		private String status;
		private String oType;
		private String ofNo;
		private String loadDocType;
		private String loadDocNo;
		private String createByName;
		private String pTemplateNo;
		private String pTemplateName;
		private String warehouse;
		private String warehouseName;
		private String materialWarehouseNo;
		private String materialWarehouseName;
		//2018-11-14 添加modifyBy 等参数
		private String createBy;
		private String createDate;
		private String createTime;
		private String submitBy;
		private String submitDate;
		private String submitTime;
		private String modifyBy;
		private String modifyDate;
		private String modifyTime;
		private String confirmBy;
		private String confirmDate;
		private String confirmTime;
		private String cancelBy;
		private String cancelDate;
		private String cancelTime;
		private String accountBy;
		private String accountDate;
		private String accountTime;
		private String modifyByName;
		private String cancelByName;
		private String confirmByName;
		private String submitByName;
		private String accountByName;
		private String totPqty;
		private String totAmt;
		private String totCqty;
		private String totDistriAmt;
		private String update_time;
		private String process_status;
		private String processERPNo;
		//2018-08-08添加docType
		private String docType; // 0-完工入库  1-组合单   2-拆解单
		//完工入库红冲 用到
		private String pStockInNo_origin;//原完工入库单号
		private String pStockInNo_refund;//关联的红冲完工入库单号，不想改查询语句了，直接加个字段吧
		private String refundStatus;//关联红冲的完工入库单状态 ，

        private String processPlanNo;
        private String task0No;
        private String dtNo;
        private String dtName;
        private String dtBeginTime;
        private String dtEndTime;

        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String createDeptId;
        private String createDeptName;

        //prodType，oOType，oOfNo
        private String prodType;
        private String oOType;
        private String oOfNo;


		public String getpStockInNo() {
			return pStockInNo;
		}
		public void setpStockInNo(String pStockInNo) {
			this.pStockInNo = pStockInNo;
		}
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getoType() {
			return oType;
		}
		public void setoType(String oType) {
			this.oType = oType;
		}
		public String getOfNo() {
			return ofNo;
		}
		public void setOfNo(String ofNo) {
			this.ofNo = ofNo;
		}
		public String getLoadDocType() {
			return loadDocType;
		}
		public void setLoadDocType(String loadDocType) {
			this.loadDocType = loadDocType;
		}
		public String getLoadDocNo() {
			return loadDocNo;
		}
		public void setLoadDocNo(String loadDocNo) {
			this.loadDocNo = loadDocNo;
		}
		public String getCreateByName() {
			return createByName;
		}
		public void setCreateByName(String createByName) {
			this.createByName = createByName;
		}
		public String getpTemplateNo() {
			return pTemplateNo;
		}
		public void setpTemplateNo(String pTemplateNo) {
			this.pTemplateNo = pTemplateNo;
		}
		public String getpTemplateName() {
			return pTemplateName;
		}
		public void setpTemplateName(String pTemplateName) {
			this.pTemplateName = pTemplateName;
		}
		public String getWarehouse() {
			return warehouse;
		}
		public void setWarehouse(String warehouse) {
			this.warehouse = warehouse;
		}
		public String getWarehouseName() {
			return warehouseName;
		}
		public void setWarehouseName(String warehouseName) {
			this.warehouseName = warehouseName;
		}
		public String getMaterialWarehouseNo() {
			return materialWarehouseNo;
		}
		public void setMaterialWarehouseNo(String materialWarehouseNo) {
			this.materialWarehouseNo = materialWarehouseNo;
		}
		public String getMaterialWarehouseName() {
			return materialWarehouseName;
		}
		public void setMaterialWarehouseName(String materialWarehouseName) {
			this.materialWarehouseName = materialWarehouseName;
		}
		public String getCreateBy() {
			return createBy;
		}
		public void setCreateBy(String createBy) {
			this.createBy = createBy;
		}
		public String getCreateDate() {
			return createDate;
		}
		public void setCreateDate(String createDate) {
			this.createDate = createDate;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getSubmitBy() {
			return submitBy;
		}
		public void setSubmitBy(String submitBy) {
			this.submitBy = submitBy;
		}
		public String getSubmitDate() {
			return submitDate;
		}
		public void setSubmitDate(String submitDate) {
			this.submitDate = submitDate;
		}
		public String getSubmitTime() {
			return submitTime;
		}
		public void setSubmitTime(String submitTime) {
			this.submitTime = submitTime;
		}
		public String getModifyBy() {
			return modifyBy;
		}
		public void setModifyBy(String modifyBy) {
			this.modifyBy = modifyBy;
		}
		public String getModifyDate() {
			return modifyDate;
		}
		public void setModifyDate(String modifyDate) {
			this.modifyDate = modifyDate;
		}
		public String getModifyTime() {
			return modifyTime;
		}
		public void setModifyTime(String modifyTime) {
			this.modifyTime = modifyTime;
		}
		public String getConfirmBy() {
			return confirmBy;
		}
		public void setConfirmBy(String confirmBy) {
			this.confirmBy = confirmBy;
		}
		public String getConfirmDate() {
			return confirmDate;
		}
		public void setConfirmDate(String confirmDate) {
			this.confirmDate = confirmDate;
		}
		public String getConfirmTime() {
			return confirmTime;
		}
		public void setConfirmTime(String confirmTime) {
			this.confirmTime = confirmTime;
		}
		public String getCancelBy() {
			return cancelBy;
		}
		public void setCancelBy(String cancelBy) {
			this.cancelBy = cancelBy;
		}
		public String getCancelDate() {
			return cancelDate;
		}
		public void setCancelDate(String cancelDate) {
			this.cancelDate = cancelDate;
		}
		public String getCancelTime() {
			return cancelTime;
		}
		public void setCancelTime(String cancelTime) {
			this.cancelTime = cancelTime;
		}
		public String getAccountBy() {
			return accountBy;
		}
		public void setAccountBy(String accountBy) {
			this.accountBy = accountBy;
		}
		public String getAccountDate() {
			return accountDate;
		}
		public void setAccountDate(String accountDate) {
			this.accountDate = accountDate;
		}
		public String getAccountTime() {
			return accountTime;
		}
		public void setAccountTime(String accountTime) {
			this.accountTime = accountTime;
		}
		public String getModifyByName() {
			return modifyByName;
		}
		public void setModifyByName(String modifyByName) {
			this.modifyByName = modifyByName;
		}
		public String getCancelByName() {
			return cancelByName;
		}
		public void setCancelByName(String cancelByName) {
			this.cancelByName = cancelByName;
		}
		public String getConfirmByName() {
			return confirmByName;
		}
		public void setConfirmByName(String confirmByName) {
			this.confirmByName = confirmByName;
		}
		public String getSubmitByName() {
			return submitByName;
		}
		public void setSubmitByName(String submitByName) {
			this.submitByName = submitByName;
		}
		public String getAccountByName() {
			return accountByName;
		}
		public void setAccountByName(String accountByName) {
			this.accountByName = accountByName;
		}
		public String getTotPqty() {
			return totPqty;
		}
		public void setTotPqty(String totPqty) {
			this.totPqty = totPqty;
		}
		public String getTotAmt() {
			return totAmt;
		}
		public void setTotAmt(String totAmt) {
			this.totAmt = totAmt;
		}
		public String getTotCqty() {
			return totCqty;
		}
		public void setTotCqty(String totCqty) {
			this.totCqty = totCqty;
		}
		public String getTotDistriAmt() {
			return totDistriAmt;
		}
		public void setTotDistriAmt(String totDistriAmt) {
			this.totDistriAmt = totDistriAmt;
		}
		public String getUpdate_time() {
			return update_time;
		}
		public void setUpdate_time(String update_time) {
			this.update_time = update_time;
		}
		public String getProcess_status() {
			return process_status;
		}
		public void setProcess_status(String process_status) {
			this.process_status = process_status;
		}
		public String getProcessERPNo() {
			return processERPNo;
		}
		public void setProcessERPNo(String processERPNo) {
			this.processERPNo = processERPNo;
		}
		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getpStockInNo_origin() {
			return pStockInNo_origin;
		}
		public void setpStockInNo_origin(String pStockInNo_origin) {
			this.pStockInNo_origin = pStockInNo_origin;
		}
		public String getpStockInNo_refund() {
			return pStockInNo_refund;
		}
		public void setpStockInNo_refund(String pStockInNo_refund) {
			this.pStockInNo_refund = pStockInNo_refund;
		}
		public String getRefundStatus() {
			return refundStatus;
		}
		public void setRefundStatus(String refundStatus) {
			this.refundStatus = refundStatus;
		}

        public String getProcessPlanNo()
        {
            return processPlanNo;
        }

        public void setProcessPlanNo(String processPlanNo)
        {
            this.processPlanNo = processPlanNo;
        }

        public String getTask0No()
        {
            return task0No;
        }

        public void setTask0No(String task0No)
        {
            this.task0No = task0No;
        }

        public String getDtNo()
        {
            return dtNo;
        }

        public void setDtNo(String dtNo)
        {
            this.dtNo = dtNo;
        }

        public String getDtName()
        {
            return dtName;
        }

        public void setDtName(String dtName)
        {
            this.dtName = dtName;
        }

        public String getDtBeginTime()
        {
            return dtBeginTime;
        }

        public void setDtBeginTime(String dtBeginTime)
        {
            this.dtBeginTime = dtBeginTime;
        }

        public String getDtEndTime()
        {
            return dtEndTime;
        }

        public void setDtEndTime(String dtEndTime)
        {
            this.dtEndTime = dtEndTime;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public String getDepartId() {
            return departId;
        }

        public void setDepartId(String departId) {
            this.departId = departId;
        }

        public String getDepartName() {
            return departName;
        }

        public void setDepartName(String departName) {
            this.departName = departName;
        }

        public String getCreateDeptId() {
            return createDeptId;
        }

        public void setCreateDeptId(String createDeptId) {
            this.createDeptId = createDeptId;
        }

        public String getCreateDeptName() {
            return createDeptName;
        }

        public void setCreateDeptName(String createDeptName) {
            this.createDeptName = createDeptName;
        }

		public String getProdType() {
			return prodType;
		}

		public void setProdType(String prodType) {
			this.prodType = prodType;
		}

		public String getoOType() {
			return oOType;
		}

		public void setoOType(String oOType) {
			this.oOType = oOType;
		}

		public String getoOfNo() {
			return oOfNo;
		}

		public void setoOfNo(String oOfNo) {
			this.oOfNo = oOfNo;
		}
	}

	
}
