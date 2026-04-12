package com.dsc.spos.json.cust.res;
import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_ParaSetQueryRes.level1Elm;

/**
 * 服務函數：DualPlayShopGetDCP
 *   說明：双屏播放门店查询
 * 服务说明：双屏播放门店查询
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayShopQueryRes extends JsonRes {

	private List<level1Elm> datas;
	public class level1Elm{
		private String shopId;
		private String shopName;

		public String getShopId() {
			return shopId;
		}
		public void setShopId(String shopId) {
			this.shopId = shopId;
		}
		public String getShopName() {
			return shopName;
		}
		public void setShopName(String shopName) {
			this.shopName = shopName;
		}

	}

	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}	
}
