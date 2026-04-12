package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 服務函數：DCP_SStockInQuery
 * 說明：自采入库查询
 * 服务说明：自采入库查询
 * @author jinzma 
 * @since  2018-11-21
 */
@Setter
@Getter
public class DCP_SStockInQueryReq  extends JsonBasicReq {
	private levelElm request;

    @Setter
    @Getter
    public class levelElm{
		private String beginDate;
		private String endDate;
		private String status;
//		private String docType;
		private String keyTxt;
		private String supplier;
        private String dateType;
	    private List<String> stockInType;

    }


}
