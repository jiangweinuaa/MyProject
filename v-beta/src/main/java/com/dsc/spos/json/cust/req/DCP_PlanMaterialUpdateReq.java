package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 生产计划原料修改
 * @author yuanyy 2019-10-28
 *
 */
public class DCP_PlanMaterialUpdateReq extends JsonBasicReq {
	
	private String planNo;
	private String bDate;
	private String fNo;
	private String beginTime;
	private String endTime;
	private String fType;
	private String avgAmt;
	private String predictAmt;
	private String status;
	
	private String isRevoke; //是否撤销， 默认传N
	
	private List<level1Elm> datas;

	public String getPlanNo() {
		return planNo;
	}

	public void setPlanNo(String planNo) {
		this.planNo = planNo;
	}

	public String getbDate() {
		return bDate;
	}

	public void setbDate(String bDate) {
		this.bDate = bDate;
	}

	public String getfNo() {
		return fNo;
	}

	public void setfNo(String fNo) {
		this.fNo = fNo;
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

	public String getfType() {
		return fType;
	}

	public void setfType(String fType) {
		this.fType = fType;
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

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIsRevoke() {
		return isRevoke;
	}

	public void setIsRevoke(String isRevoke) {
		this.isRevoke = isRevoke;
	}

	public  class level1Elm {
		
		private String pluNo;
		private String unit;
		private String avgQty;
		private String predictQty;
		private String residueQty;
		private String outQty;
		private String changeQty;
		private String actQty;
		private String totAmt;
		private String distriAmt;
		private String isOut;
//		private String qty; // 实时库存
		
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getAvgQty() {
			return avgQty;
		}
		public void setAvgQty(String avgQty) {
			this.avgQty = avgQty;
		}
		public String getPredictQty() {
			return predictQty;
		}
		public void setPredictQty(String predictQty) {
			this.predictQty = predictQty;
		}
		public String getResidueQty() {
			return residueQty;
		}
		public void setResidueQty(String residueQty) {
			this.residueQty = residueQty;
		}
		public String getOutQty() {
			return outQty;
		}
		public void setOutQty(String outQty) {
			this.outQty = outQty;
		}
		public String getChangeQty() {
			return changeQty;
		}
		public void setChangeQty(String changeQty) {
			this.changeQty = changeQty;
		}
		public String getActQty() {
			return actQty;
		}
		public void setActQty(String actQty) {
			this.actQty = actQty;
		}
		public String getTotAmt() {
			return totAmt;
		}
		public void setTotAmt(String totAmt) {
			this.totAmt = totAmt;
		}
		public String getDistriAmt() {
			return distriAmt;
		}
		public void setDistriAmt(String distriAmt) {
			this.distriAmt = distriAmt;
		}
		public String getIsOut() {
			return isOut;
		}
		public void setIsOut(String isOut) {
			this.isOut = isOut;
		}
		
	}
	
	
}
