package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_DinnerCreateReq extends JsonBasicReq {

	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{


		// 2018-10-24 全部采用骆驼命名法 重新命名
		private String dinnerNo;
		private String guestNum;
		//	private String priority;// 優先級由後端查詢后生成
		private String teaPluNo;

		private String teaQty;
		private String tissuePluNo;
		private String tissueQty;
		private String ricePluNo;
		private String riceQty;

		private String useType;
		private String dinnerClass;
		private String dinnerGroup;
		private String status;
        private int maxGuestNum;

        private List<levelElmMeal> mealList;


        public List<levelElmMeal> getMealList()
        {
            return mealList;
        }

        public void setMealList(List<levelElmMeal> mealList)
        {
            this.mealList = mealList;
        }

        public String getDinnerClass() {
			return dinnerClass;
		}

		public void setDinnerClass(String dinnerClass) {
			this.dinnerClass = dinnerClass;
		}

		public String getDinnerNo() {
			return dinnerNo;
		}

		public void setDinnerNo(String dinnerNo) {
			this.dinnerNo = dinnerNo;
		}

		public String getTeaPluNo() {
			return teaPluNo;
		}

		public void setTeaPluNo(String teaPluNo) {
			this.teaPluNo = teaPluNo;
		}

		public String getGuestNum() {
			return guestNum;
		}

		public void setGuestNum(String guestNum) {
			this.guestNum = guestNum;
		}


		public String getDinnerGroup() {
			return dinnerGroup;
		}

		public void setDinnerGroup(String dinnerGroup) {
			this.dinnerGroup = dinnerGroup;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getTeaQty() {
			return teaQty;
		}

		public void setTeaQty(String teaQty) {
			this.teaQty = teaQty;
		}

		public String getTissuePluNo() {
			return tissuePluNo;
		}

		public void setTissuePluNo(String tissuePluNo) {
			this.tissuePluNo = tissuePluNo;
		}

		public String getTissueQty() {
			return tissueQty;
		}

		public void setTissueQty(String tissueQty) {
			this.tissueQty = tissueQty;
		}

		public String getRicePluNo() {
			return ricePluNo;
		}

		public void setRicePluNo(String ricePluNo) {
			this.ricePluNo = ricePluNo;
		}

		public String getRiceQty() {
			return riceQty;
		}

		public void setRiceQty(String riceQty) {
			this.riceQty = riceQty;
		}

		public String getUseType() {
			return useType;
		}

		public void setUseType(String useType) {
			this.useType = useType;
		}

        public int getMaxGuestNum()
        {
            return maxGuestNum;
        }

        public void setMaxGuestNum(int maxGuestNum)
        {
            this.maxGuestNum = maxGuestNum;
        }
    }


    public class levelElmMeal
    {
        private String pluNo;
        private String unitId;
        private String qty;
        private String defMode;


        public String getPluNo()
        {
            return pluNo;
        }

        public void setPluNo(String pluNo)
        {
            this.pluNo = pluNo;
        }

        public String getUnitId()
        {
            return unitId;
        }

        public void setUnitId(String unitId)
        {
            this.unitId = unitId;
        }

        public String getQty()
        {
            return qty;
        }

        public void setQty(String qty)
        {
            this.qty = qty;
        }

        public String getDefMode()
        {
            return defMode;
        }

        public void setDefMode(String defMode)
        {
            this.defMode = defMode;
        }
    }
}
