package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_GoodsMappingDeleteReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String platformType;
		private List<level1Elm> datas;

		public String getPlatformType() {
			return platformType;
		}

		public void setPlatformType(String platformType) {
			this.platformType = platformType;
		}

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}
	public class level1Elm {
		private String pluNo;
        private String platformPluNo;
        private String unitId;
		public String getPluNo() {
			return pluNo;
		}

		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}

        public String getPlatformPluNo() {
            return platformPluNo;
        }

        public void setPlatformPluNo(String platformPluNo) {
            this.platformPluNo = platformPluNo;
        }

        public String getUnitId() {
            return unitId;
        }

        public void setUnitId(String unitId) {
            this.unitId = unitId;
        }
    }

}
