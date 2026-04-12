package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服務函數：TGCommissionCreateDCP
 * 服务说明：团务拆账新增
 * @author jinzma 
 * @since  2019-02-11
 */
public class DCP_TGCommissionCreateReq extends JsonBasicReq {

	private String travelNO ;
	private String tgCategoryNO;
	private String shopBonus;
	private String tempTourGroup;
	private String memo;
	private String status;
	private List<level1Elm> datas;

	public  class level1Elm
	{
		private String guideNO;
		private String travelRate;
		private String guideRate;
		private String memo;
		public String getGuideNO() {
			return guideNO;
		}
		public void setGuideNO(String guideNO) {
			this.guideNO = guideNO;
		}
		public String getTravelRate() {
			return travelRate;
		}
		public void setTravelRate(String travelRate) {
			this.travelRate = travelRate;
		}
		public String getGuideRate() {
			return guideRate;
		}
		public void setGuideRate(String guideRate) {
			this.guideRate = guideRate;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}


	}

	public String getTravelNO() {
		return travelNO;
	}

	public void setTravelNO(String travelNO) {
		this.travelNO = travelNO;
	}

	public String getTgCategoryNO() {
		return tgCategoryNO;
	}

	public void setTgCategoryNO(String tgCategoryNO) {
		this.tgCategoryNO = tgCategoryNO;
	}

	public String getShopBonus() {
		return shopBonus;
	}

	public void setShopBonus(String shopBonus) {
		this.shopBonus = shopBonus;
	}

	public String getTempTourGroup() {
		return tempTourGroup;
	}

	public void setTempTourGroup(String tempTourGroup) {
		this.tempTourGroup = tempTourGroup;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

}
