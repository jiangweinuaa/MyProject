package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import lombok.Getter;
import lombok.Setter;

/**
 * 服務函數：POrderGet
 *   說明：要货单查询
 * 服务说明：要货单查询
 * @author panjing 
 * @since  2016-10-8
 */
public class DCP_POrderQueryReq extends JsonBasicReq{

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}

	public void setRequest(levelElm request) {
		this.request = request;
	}
	@Getter
	@Setter
	public class levelElm
	{
		private String status;
		private String keyTxt;

		//2018-11-09 yyy 新增beginDate 和 endDate  
		private String beginDate;
		private String endDate;

		//2020-03-27 JZMA 新增日期类型dateType  枚举: bDate：单据日期,rDate：需求日期
		private String dateType;

		//2021-05-12 wangzyc 新增porderNo 用于精确查询 非必传
        private String pOrderNo;
        private String pTemplateNo;
        private String queryType;//0 空 默认  1 要货督审
        
        private String receiptOrgNo;
        private String supplierType;
        private String isTemplateControl;
        private String searchScope;
        private String[] orgList;
        private String isInclTemplate;


    }


}
