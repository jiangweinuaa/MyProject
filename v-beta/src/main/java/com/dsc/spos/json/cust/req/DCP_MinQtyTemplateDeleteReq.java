package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;


/**
 * 服务函数：DCP_MinQtyTemplateDelete 
 * 服务说明：商品起售量模板删除
 * @author wangzyc
 * @since 2020-11-11
 */
public class DCP_MinQtyTemplateDeleteReq extends JsonBasicReq{

	private level1Elm request;

	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm {
		private List<level2Elm> templateList; // 模板编码

		public List<level2Elm> getTemplateList() {
			return templateList;
		}

		public void setTemplateList(List<level2Elm> templateList) {
			this.templateList = templateList;
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
