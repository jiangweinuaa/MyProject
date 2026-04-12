package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 生产计划新增
 * @author Huawei
 *
 */
public class DCP_PlanCreateReq extends JsonBasicReq {
	
	private String beginDate;
	private String endDate;
	
	private String bDate;
	private String holiday;
	private String modifRatio;
	
	private String eNo1; // 天气
	private String eName1;
	private String eRatio1;
	
	private String eNo2; // 特殊事件
	private String eName2;
	private String eRatio2;
	
	private String eNo3; // 节假日
	private String eName3;
	private String eRatio3;
	
	private String maxAmt; // 大额订单限制
	
	
//	private List<level1Elm> datas;

	public String getBeginDate() {
		return beginDate;
	}

	public String geteRatio1() {
		return eRatio1;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getbDate() {
		return bDate;
	}

	public void setbDate(String bDate) {
		this.bDate = bDate;
	}

	public String getHoliday() {
		return holiday;
	}

	public void setHoliday(String holiday) {
		this.holiday = holiday;
	}

	public String getModifRatio() {
		return modifRatio;
	}

	public void setModifRatio(String modifRatio) {
		this.modifRatio = modifRatio;
	}

	public String geteNo1() {
		return eNo1;
	}

	public void seteNo1(String eNo1) {
		this.eNo1 = eNo1;
	}

	public String geteName1() {
		return eName1;
	}

	public void seteName1(String eName1) {
		this.eName1 = eName1;
	}

	public String eRatio1() {
		return eRatio1;
	}

	public void seteRatio1(String eRatio1) {
		this.eRatio1 = eRatio1;
	}

	public String geteNo2() {
		return eNo2;
	}

	public void seteNo2(String eNo2) {
		this.eNo2 = eNo2;
	}

	public String geteName2() {
		return eName2;
	}

	public void seteName2(String eName2) {
		this.eName2 = eName2;
	}

	public String geteRatio2() {
		return eRatio2;
	}

	public void seteRatio2(String eRatio2) {
		this.eRatio2 = eRatio2;
	}

	public String geteNo3() {
		return eNo3;
	}

	public void seteNo3(String eNo3) {
		this.eNo3 = eNo3;
	}

	public String geteName3() {
		return eName3;
	}

	public void seteName3(String eName3) {
		this.eName3 = eName3;
	}

	public String geteRatio3() {
		return eRatio3;
	}

	public void seteRatio3(String eRatio3) {
		this.eRatio3 = eRatio3;
	}

	public String getMaxAmt() {
		return maxAmt;
	}

	public void setMaxAmt(String maxAmt) {
		this.maxAmt = maxAmt;
	}
	
	
//	public List<level1Elm> getDatas() {
//		return datas;
//	}
//
//	public void setDatas(List<level1Elm> datas) {
//		this.datas = datas;
//	}
//	
//	public  class level1Elm {
//		
//		private String eType;
//		private String eNo;
//		private String eName;
//		private String eRatio;
//		public String geteType() {
//			return eType;
//		}
//		public void seteType(String eType) {
//			this.eType = eType;
//		}
//		public String geteNo() {
//			return eNo;
//		}
//		public void seteNo(String eNo) {
//			this.eNo = eNo;
//		}
//		public String geteName() {
//			return eName;
//		}
//		public void seteName(String eName) {
//			this.eName = eName;
//		}
//		public String geteRatio() {
//			return eRatio;
//		}
//		public void seteRatio(String eRatio) {
//			this.eRatio = eRatio;
//		}
//		
//	}
	
}
