package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

/**
 * StockAdjustGet库存调整单查询 Response JSON
 * @author kangzc
 *
 */
public class DCP_StockAdjustQueryRes extends JsonRes
{
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

    @Data
	public class level1Elm
	{
		private String adjustNo;
		private String bDate;
		private String memo;
		private String status;
		private String docType;
		private String oType;
		private String ofNo;
		private String loadDocType;
		private String loadDocNo;
		private String warehouse;
		private String warehouseName;
		private String createBy;
		private String createByName;
		private String createDate;
		private String createTime;
		private String modifyBy;
		private String modifyByName;
		private String modifyDate;
		private String modifyTime;
		private String submitBy;
		private String submitByName;
		private String submitDate;
		private String submitTime;
		private String confirmBy;
		private String confirmByName;
		private String confirmDate;
		private String confirmTime;
		private String cancelBy;
		private String cancelByName;
		private String cancelDate;
		private String cancelTime;
		private String accountBy;
		private String accountByName;
		private String accountDate;
		private String accountTime;
		private String update_time;
		private String process_status;
		private String totPqty;
		private String totAmt;
		private String totCqty;
		private String totDistriAmt;

        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;

	}



}
