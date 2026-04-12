package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;


public class DCP_DinnerAreaQueryRes extends JsonRes
{

	private List<level1Elm> datas;
	
	
	
	
	public List<level1Elm> getDatas() {
		return datas;
	}




	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}




	public class level1Elm
	{
	    private String shopId;      // 门店编号
		private String dinnerGroup;
		private String dinnerGroupName;
		private String status;
		private String priority;
		private String updateTime;

		private String restrictGroup; // 启用区域点单Y/N
        private String groupType; // 点单类型 0.用餐后结账 1.预先结账

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getDinnerGroup() {
			return dinnerGroup;
		}
		public void setDinnerGroup(String dinnerGroup) {
			this.dinnerGroup = dinnerGroup;
		}
		public String getDinnerGroupName() {
			return dinnerGroupName;
		}
		public void setDinnerGroupName(String dinnerGroupName) {
			this.dinnerGroupName = dinnerGroupName;
		}
		public String getPriority() {
			return priority;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		public String getUpdateTime() {
			return updateTime;
		}
		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}

        public String getRestrictGroup() {
            return restrictGroup;
        }

        public void setRestrictGroup(String restrictGroup) {
            this.restrictGroup = restrictGroup;
        }

        public String getGroupType() {
            return groupType;
        }

        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }
    }
}
