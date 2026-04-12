package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * 服務函數：TemplateShopUpdateDCP
 *    說明：模板生效门店修改
 * 服务说明：模板生效门店修改
 * @author jzma 
 * @since  2017-03-03
 */
public class DCP_DeliverySettingShopUpdateReq extends JsonBasicReq
{
	private levelRequest request;		


	public levelRequest getRequest() {
		return request;
	}
	public void setRequest(levelRequest request) {
		this.request = request;
	}

	public class levelRequest
	{
		private String appId;
		private String deliveryType;
		private List<level1Elm> datas;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getDeliveryType() {
			return deliveryType;
		}

		public void setDeliveryType(String deliveryType) {
			this.deliveryType = deliveryType;
		}

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}


	public  class level1Elm
	{		
		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
	}

}

