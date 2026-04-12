package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_GoodsTemplateGoodsQueryRes extends JsonRes
{
	private List<level1Elm> datas ;

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm {
		private String pluNo ;//商品编码
		private String pluName ;//商品名称		
		private String warningQty ;//警戒库存量
		private String safeQty ;//安全库存量
		private String canSale ;//是否可销售Y/N
		private String canFree ;//是否可免单Y/N
		private String canOrder ;//是否可预订Y/N
		private String canReturn ;//是否可退货Y/N
		private String canPurchase ;//是否可采购Y/N	
		private String canRequire ;//是否可要货Y/N
		private String minQty ;//最小要货量
		private String maxQty ;//最大要货量
		private String multiQty ;//要货倍量
		private String canRequireBack ;//是否可退仓Y/N
		private String isAutoSubtract ;//是否自动扣料Y/N
		private String canEstimate ;//可预估Y/N
		private String clearType ;//估清方式N-不估清 PERIOD-按餐段估清 DAY-按天估清
		private String status ;//是否有效：0-无效100有效
		private String redisUpdateSuccess;//同步缓存是否成功Y/N
		private String isNewGoods;   // 是否新品（Y/N）
		private String isAllot;      // 是否可调拨(Y/N)
        private String category;
        private String categoryName;

        private String supplierType;
        private String supplierId;
        private String supplierName;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
        private String createOpId;
        private String createOpName;
        private String createTime;

        public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getWarningQty() {
			return warningQty;
		}
		public void setWarningQty(String warningQty) {
			this.warningQty = warningQty;
		}
		public String getSafeQty() {
			return safeQty;
		}
		public void setSafeQty(String safeQty) {
			this.safeQty = safeQty;
		}
		public String getCanSale() {
			return canSale;
		}
		public void setCanSale(String canSale) {
			this.canSale = canSale;
		}
		public String getCanFree() {
			return canFree;
		}
		public void setCanFree(String canFree) {
			this.canFree = canFree;
		}
		public String getCanOrder() {
			return canOrder;
		}
		public void setCanOrder(String canOrder) {
			this.canOrder = canOrder;
		}
		public String getCanReturn() {
			return canReturn;
		}
		public void setCanReturn(String canReturn) {
			this.canReturn = canReturn;
		}
		public String getCanPurchase() {
			return canPurchase;
		}
		public void setCanPurchase(String canPurchase) {
			this.canPurchase = canPurchase;
		}
		public String getCanRequire() {
			return canRequire;
		}
		public void setCanRequire(String canRequire) {
			this.canRequire = canRequire;
		}
		public String getMinQty() {
			return minQty;
		}
		public void setMinQty(String minQty) {
			this.minQty = minQty;
		}
		public String getMaxQty() {
			return maxQty;
		}
		public void setMaxQty(String maxQty) {
			this.maxQty = maxQty;
		}
		public String getMultiQty() {
			return multiQty;
		}
		public void setMultiQty(String multiQty) {
			this.multiQty = multiQty;
		}
		public String getCanRequireBack() {
			return canRequireBack;
		}
		public void setCanRequireBack(String canRequireBack) {
			this.canRequireBack = canRequireBack;
		}
		public String getIsAutoSubtract() {
			return isAutoSubtract;
		}
		public void setIsAutoSubtract(String isAutoSubtract) {
			this.isAutoSubtract = isAutoSubtract;
		}
		public String getCanEstimate() {
			return canEstimate;
		}
		public void setCanEstimate(String canEstimate) {
			this.canEstimate = canEstimate;
		}
		public String getClearType() {
			return clearType;
		}
		public void setClearType(String clearType) {
			this.clearType = clearType;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getRedisUpdateSuccess()
		{
			return redisUpdateSuccess;
		}
		public void setRedisUpdateSuccess(String redisUpdateSuccess)
		{
			this.redisUpdateSuccess = redisUpdateSuccess;
		}
		public String getPluName()
		{
			return pluName;
		}
		public void setPluName(String pluName)
		{
			this.pluName = pluName;
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

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
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

        public String getLastModiOpId() {
            return lastModiOpId;
        }

        public void setLastModiOpId(String lastModiOpId) {
            this.lastModiOpId = lastModiOpId;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public String getLastModiOpName() {
            return lastModiOpName;
        }

        public void setLastModiOpName(String lastModiOpName) {
            this.lastModiOpName = lastModiOpName;
        }

        public String getLastModiTime() {
            return lastModiTime;
        }

        public void setLastModiTime(String lastModiTime) {
            this.lastModiTime = lastModiTime;
        }

        public String getCreateOpId() {
            return createOpId;
        }

        public void setCreateOpId(String createOpId) {
            this.createOpId = createOpId;
        }

        public String getCreateOpName() {
            return createOpName;
        }

        public void setCreateOpName(String createOpName) {
            this.createOpName = createOpName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }

}
