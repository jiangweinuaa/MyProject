package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * JOB 执行时间设置 
 * 设置JOB 执行时间点， 如 12：00：00 ， 20：00：00
 * 不同于轮询时间
 * @author yuanyy 2019-06-24 
 *
 */
public class DCP_JobDetailSetQueryRes extends JsonRes {
	
	private String jobName;
	
	private List<level1Elm> datas;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	
	public class level1Elm {
		private String item;
		private String beginTime;
		private String endTime;
		
		
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
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
		
		
	}
	
	
}
