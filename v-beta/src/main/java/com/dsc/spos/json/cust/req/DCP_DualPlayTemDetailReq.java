package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：DualPlayGetDCP
 *    說明：双屏播放查询
 * 服务说明：双屏播放查询
 * @author jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayTemDetailReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class  levelRequest
    {
        private String templateNo;

        public String getTemplateNo() {
            return templateNo;
        }

        public void setTemplateNo(String templateNo) {
            this.templateNo = templateNo;
        }
    }
	
}
