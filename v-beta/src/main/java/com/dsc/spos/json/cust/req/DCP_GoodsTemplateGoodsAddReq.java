package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsTemplateGoodsAddReq extends JsonBasicReq
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
		private String templateId;
		private List<levelPlu> pluList;//

		public List<levelPlu> getPluList()
		{
			return pluList;
		}

		public void setPluList(List<levelPlu> pluList)
		{
			this.pluList = pluList;
		}

		public String getTemplateId()
		{
			return templateId;
		}

		public void setTemplateId(String templateId)
		{
			this.templateId = templateId;
		}

			
	}
	
	public class levelPlu
	{
		private String pluNo;//模板编码
        private String supplierType;
        private String supplierId;

		public String getPluNo()
		{
			return pluNo;
		}

		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}


        public String getSupplierType() {
            return supplierType;
        }

        public void setSupplierType(String supplierType) {
            this.supplierType = supplierType;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }
    }
}
