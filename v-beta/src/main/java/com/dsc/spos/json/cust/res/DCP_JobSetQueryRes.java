package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_JobSetQueryRes extends JsonRes
{

	private List<level1Elm> datas;


	public class level1Elm
	{
		private String jobName;
		private String jobDescription;
		private String jobTime;
		private String status;
		private String kettle;
		private String kettleFolder;
		private String mainType;
		private String onSale;
		private String initJobTime;
		private String initCnfflg;


		public String getJobName() {
			return jobName;
		}
		public void setJobName(String jobName) {
			this.jobName = jobName;
		}
		public String getJobDescription() {
			return jobDescription;
		}
		public void setJobDescription(String jobDescription) {
			this.jobDescription = jobDescription;
		}
		public String getJobTime() {
			return jobTime;
		}
		public void setJobTime(String jobTime) {
			this.jobTime = jobTime;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getKettle() {
			return kettle;
		}
		public void setKettle(String kettle) {
			this.kettle = kettle;
		}
		public String getKettleFolder() {
			return kettleFolder;
		}
		public void setKettleFolder(String kettleFolder) {
			this.kettleFolder = kettleFolder;
		}
		public String getMainType() {
			return mainType;
		}
		public void setMainType(String mainType) {
			this.mainType = mainType;
		}
		public String getOnSale() {
			return onSale;
		}
		public void setOnSale(String onSale) {
			this.onSale = onSale;
		}
		public String getInitJobTime() {
			return initJobTime;
		}
		public void setInitJobTime(String initJobTime) {
			this.initJobTime = initJobTime;
		}
		public String getInitCnfflg() {
			return initCnfflg;
		}
		public void setInitCnfflg(String initCnfflg) {
			this.initCnfflg = initCnfflg;
		}

	}





	public List<level1Elm> getDatas() {
		return datas;
	}



	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}


}
