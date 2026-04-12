package com.dsc.spos.json.cust.req;
import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

/**
 * 服務函數：StockInProcess
 *   說明：收货单执行
 * 服务说明：收货单执行
 * @author luoln 
 * @since  2017-07-05
 */
@Getter
@Setter
public class DCP_StockInProcessReq extends JsonBasicReq{

	private levelElm request;

    @Getter
	@Setter
	public class levelElm{
		@JSONFieldRequired(display = "单号")
		private String stockInNo;
		@JSONFieldRequired(display = "单据类型")
		private String docType;
		private String ofNo;
		@JSONFieldRequired(display = "状态")
		private String status;
		private String differenceID;
		private String loadDocNo;

		private String orgNo; //自动审批时切换组织别

	}

}
