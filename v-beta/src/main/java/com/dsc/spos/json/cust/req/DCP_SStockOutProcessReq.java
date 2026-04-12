package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：SStockOutProcess
 *   說明：自采出库处理
 * 服务说明：自采出库处理
 * @author JZMA 
 * @since  2018-11-20
 */
public class DCP_SStockOutProcessReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private String sStockOutNo;

        private String opType;

		public String getsStockOutNo() {
			return sStockOutNo;
		}

		public void setsStockOutNo(String sStockOutNo) {
			this.sStockOutNo = sStockOutNo;
		}

        public String getOpType() {
            return opType;
        }

        public void setOpType(String opType) {
            this.opType = opType;
        }
    }


}
