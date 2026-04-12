package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 生产计划查询
 * @author yuanyy 
 *
 */
public class DCP_PlanQueryRes extends JsonRes {
	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		
		private String planNo;
		private String status;
		private String bDate;
		private String holiday;
		private String modifRatio;
		private String beginDate;
		private String endDate;
		
		private String eNo1;//天气
		private String eName1;
		private String eRatio1;
		
		private String eNo2;//特殊事件
		private String eName2;
		private String eRatio2;
		
		private String eNo3;//节假日
		private String eName3;
		private String eRatio3;
		
		private String maxAmt;// 大额订单限制
		
//		private List<level2Elm> eDatas;
		private List<level3Elm> fDatas;
		
		
		public String getBeginDate() {
			return beginDate;
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
		public String getPlanNo() {
			return planNo;
		}
		public void setPlanNo(String planNo) {
			this.planNo = planNo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
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
//		public List<level2Elm> geteDatas() {
//			return eDatas;
//		}
//		public void seteDatas(List<level2Elm> eDatas) {
//			this.eDatas = eDatas;
//		}
		public List<level3Elm> getfDatas() {
			return fDatas;
		}
		public void setfDatas(List<level3Elm> fDatas) {
			this.fDatas = fDatas;
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
		public String geteRatio1() {
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
	}
	
//	public static class level2Elm{
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
//		
//	}
	
	public class level3Elm{
		
		private String fType;
		private String fNo;
		private String fName;
		private String beginTime;
		private String endTime;
		private String avgAmt;
		private String predictAmt;
		
		private String priority;
		
		public String getfType() {
			return fType;
		}
		public void setfType(String fType) {
			this.fType = fType;
		}
		public String getfNo() {
			return fNo;
		}
		public void setfNo(String fNo) {
			this.fNo = fNo;
		}
		public String getfName() {
			return fName;
		}
		public void setfName(String fName) {
			this.fName = fName;
		}
		public String getBeginTime() {
			return beginTime;
		}
		public void setBeginTime(String beginTime) {
			this.beginTime = beginTime;
		}
		public String getEndTime() {
			return endTime;
		}
		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
		public String getAvgAmt() {
			return avgAmt;
		}
		public void setAvgAmt(String avgAmt) {
			this.avgAmt = avgAmt;
		}
		public String getPredictAmt() {
			return predictAmt;
		}
		public void setPredictAmt(String predictAmt) {
			this.predictAmt = predictAmt;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
	}

	
	
}
