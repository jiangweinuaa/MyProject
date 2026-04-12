package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_GoodsTemplateGoodsUpdateReq extends JsonBasicReq
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
		private String warningQty;
		private String safeQty;
		private String canSale;
		private String canFree;
		private String canOrder;
		private String canReturn;
		private String canPurchase;
		private String canRequire;
		private String minQty;
		private String maxQty;
		private String multiQty;
		private String canRequireBack;
		private String isAutoSubtract;
		private String canEstimate;
		private String clearType;
		private String isNewGoods;
		private String isAllot;

        private String supplierId;
        private String supplierType;

		public String getPluNo()
		{
			return pluNo;
		}
		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}
		public String getWarningQty()
		{
			return warningQty;
		}
		public void setWarningQty(String warningQty)
		{
			this.warningQty = warningQty;
		}
		public String getSafeQty()
		{
			return safeQty;
		}
		public void setSafeQty(String safeQty)
		{
			this.safeQty = safeQty;
		}
		public String getCanSale()
		{
			return canSale;
		}
		public void setCanSale(String canSale)
		{
			this.canSale = canSale;
		}
		public String getCanFree()
		{
			return canFree;
		}
		public void setCanFree(String canFree)
		{
			this.canFree = canFree;
		}
		public String getCanOrder()
		{
			return canOrder;
		}
		public void setCanOrder(String canOrder)
		{
			this.canOrder = canOrder;
		}
		public String getCanReturn()
		{
			return canReturn;
		}
		public void setCanReturn(String canReturn)
		{
			this.canReturn = canReturn;
		}
		public String getCanPurchase()
		{
			return canPurchase;
		}
		public void setCanPurchase(String canPurchase)
		{
			this.canPurchase = canPurchase;
		}
		public String getCanRequire()
		{
			return canRequire;
		}
		public void setCanRequire(String canRequire)
		{
			this.canRequire = canRequire;
		}
		public String getMinQty()
		{
			return minQty;
		}
		public void setMinQty(String minQty)
		{
			this.minQty = minQty;
		}
		public String getMaxQty()
		{
			return maxQty;
		}
		public void setMaxQty(String maxQty)
		{
			this.maxQty = maxQty;
		}
		public String getMultiQty()
		{
			return multiQty;
		}
		public void setMultiQty(String multiQty)
		{
			this.multiQty = multiQty;
		}
		public String getCanRequireBack()
		{
			return canRequireBack;
		}
		public void setCanRequireBack(String canRequireBack)
		{
			this.canRequireBack = canRequireBack;
		}
		public String getIsAutoSubtract()
		{
			return isAutoSubtract;
		}
		public void setIsAutoSubtract(String isAutoSubtract)
		{
			this.isAutoSubtract = isAutoSubtract;
		}
		public String getCanEstimate()
		{
			return canEstimate;
		}
		public void setCanEstimate(String canEstimate)
		{
			this.canEstimate = canEstimate;
		}
		public String getClearType()
		{
			return clearType;
		}
		public void setClearType(String clearType)
		{
			this.clearType = clearType;
		}
		public String getIsNewGoods() {
			return isNewGoods;
		}
		public void setIsNewGoods(String isNewGoods) {
			this.isNewGoods = isNewGoods;
		}
		public String getIsAllot() {
			return isAllot;
		}
		public void setIsAllot(String isAllot) {
			this.isAllot = isAllot;
		}

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public String getSupplierType() {
            return supplierType;
        }

        public void setSupplierType(String supplierType) {
            this.supplierType = supplierType;
        }
    }
}
