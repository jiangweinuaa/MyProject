package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * 服務函數：StockOutProcess
 *   說明：出库单处理
 * 服务说明：出库单处理
 * @author panjing 
 * @since  2016-09-22
 */
@Getter
@Setter
public class DCP_StockOutProcessReq extends JsonBasicReq {

	private levelElm request;

	@Getter
	@Setter
	public class levelElm {
		@JSONFieldRequired
		private String stockOutNo;
		@JSONFieldRequired
		private String docType;
		private String ofNo;
		@JSONFieldRequired
		private String status;

        private String orgNo; //自动审批时切换组织别

		//钉钉审批
		private String oEId;
		private String oShopId;
		private String o_createBy;
		private String o_inv_cost_warehouse;
		private String o_langType;

	}

}

