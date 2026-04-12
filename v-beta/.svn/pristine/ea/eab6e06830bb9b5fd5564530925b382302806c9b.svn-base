package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateQueryReq.levelRequest;

public class DCP_GoodsTemplateGoodsQueryReq extends JsonBasicReq
{
private levelRequest request;		
	
	
	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}




	public class levelRequest
	{
		private String keyTxt;
		private String templateId;
		private String sortType;
        private String redisUpdateSuccess;//同步缓存是否成功Y/N
        private String[] category;

		public String getKeyTxt()
		{
			return keyTxt;
		}
		public void setKeyTxt(String keyTxt)
		{
			this.keyTxt = keyTxt;
		}
		public String getTemplateId()
		{
			return templateId;
		}
		public void setTemplateId(String templateId)
		{
			this.templateId = templateId;
		}
		public String getSortType()
		{
			return sortType;
		}
		public void setSortType(String sortType)
		{
			this.sortType = sortType;
		}
        public String getRedisUpdateSuccess()
        {
            return redisUpdateSuccess;
        }

        public void setRedisUpdateSuccess(String redisUpdateSuccess)
        {
            this.redisUpdateSuccess = redisUpdateSuccess;
        }

        public String[] getCategory() {
            return category;
        }

        public void setCategory(String[] category) {
            this.category = category;
        }
    }

}
