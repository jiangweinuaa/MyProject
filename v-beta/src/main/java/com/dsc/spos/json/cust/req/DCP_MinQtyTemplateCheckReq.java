package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_MinQtyTemplateCheck
 * 服务说明：商品起售量模板启用/禁用
 * @author wangzyc 
 * @since  2020-11-11
 */
public class DCP_MinQtyTemplateCheckReq extends JsonBasicReq{
	
	private level1Elm request;
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm {
		private List<level2Elm> templateList; // 模板编码
        private String oprType; // 修改状态，1启用 2禁用

		public List<level2Elm> getTemplateList() {
			return templateList;
		}

		public void setTemplateList(List<level2Elm> templateList) {
			this.templateList = templateList;
		}

		public String getOprType() {
			return oprType;
		}

		public void setOprType(String oprType) {
			this.oprType = oprType;
		}
		
	}

	public class level2Elm {
		private String templateId;

		public String getTemplateId() {
			return templateId;
		}

		public void setTemplateId(String templateId) {
			this.templateId = templateId;
		}

	}

}
