package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：PStockInProcess
 *    說明：完工入库单执行
 * 服务说明：完工入库单执行
 * @author luoln 
 * @since  2017-03-31
 */

@Data
public class DCP_PStockInProcessReq extends JsonBasicReq {

    @JSONFieldRequired
	private levelElm request;

    @Data
	public class levelElm{
		private String pStockInNo;

        private String ofNo;
        private String docType;
        private String opType;
        private String oType;

        private String accountDate;

	}
}
