package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsTemplateGoodsCopyReq extends JsonBasicReq
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
		private String sourceTemplateId;//来源模板id
		private String targetTemplateId;//目标模板id
		private String isAllGoods;//是否全部商品Y/N
		private String isCover;//存在时是否覆盖Y/N
		private List<levelPlu> pluList;//
		
		public String getSourceTemplateId()
		{
			return sourceTemplateId;
		}
		public void setSourceTemplateId(String sourceTemplateId)
		{
			this.sourceTemplateId = sourceTemplateId;
		}
		public String getTargetTemplateId()
		{
			return targetTemplateId;
		}
		public void setTargetTemplateId(String targetTemplateId)
		{
			this.targetTemplateId = targetTemplateId;
		}
		public String getIsCover()
		{
			return isCover;
		}
		public void setIsCover(String isCover)
		{
			this.isCover = isCover;
		}
		public String getIsAllGoods()
		{
			return isAllGoods;
		}
		public void setIsAllGoods(String isAllGoods)
		{
			this.isAllGoods = isAllGoods;
		}
		public List<levelPlu> getPluList()
		{
			return pluList;
		}
		public void setPluList(List<levelPlu> pluList)
		{
			this.pluList = pluList;
		}
		
		

			
	}
	
	public class levelPlu
	{
		private String pluNo;//模板编码


		public String getPluNo()
		{
			return pluNo;
		}

		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}


    }
}
