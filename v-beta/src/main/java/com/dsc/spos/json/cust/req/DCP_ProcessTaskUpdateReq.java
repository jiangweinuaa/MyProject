package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：ProcessTaskUpdate
 *    說明：加工任务修改
 * 服务说明：加工任务修改
 * @author luoln 
 * @since  2017-03-30
 */
public class DCP_ProcessTaskUpdateReq extends JsonBasicReq {

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

    @Data
	public class levelElm{
		private String processTaskNo;
		private String bDate;
		private String memo;
		private String pTemplateNo;
		private String pDate;
		private String warehouse;
		private String materialWarehouseNo;
		private String totPqty;
		private String totAmt;
		private String totDistriAmt;
		private String totCqty;
        private String dtNo;

        private List<level1Elm> detailList;

        private String employeeId;
        private String departId;
        private String prodType;
	}

    @Data
	public  class level1Elm
	{
		private String item;
		private String pluNo;
		private String punit;
		private String pqty;
		private String price;
		private String amt;
		private String mulQty;
		private String distriPrice;
		private String distriAmt;	
		private String featureNo;
		private String baseUnit;
		private String baseQty;
		private String unitRatio;

        private String minQty;
        private String dispType;
        private String semiWoType;
        private String oddValue;
        private String remainType;
        private String bomNo;
        private String versionNum;
        private String beginDate;
        private String endDate;


	}

}
