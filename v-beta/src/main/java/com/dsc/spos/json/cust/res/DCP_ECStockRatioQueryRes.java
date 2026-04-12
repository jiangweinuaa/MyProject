package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_ECStockRatioGet
 * 服务说明：电商平台库存比例查询
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockRatioQueryRes extends JsonRes {
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public class level1Elm
	{
		private String ecPlatformNo;
		private String ecPlatformName;
		private String ecStockRatio;
		
		public String getEcPlatformNo() {
			return ecPlatformNo;
		}
		public void setEcPlatformNo(String ecPlatformNo) {
			this.ecPlatformNo = ecPlatformNo;
		}
		public String getEcPlatformName() {
			return ecPlatformName;
		}
		public void setEcPlatformName(String ecPlatformName) {
			this.ecPlatformName = ecPlatformName;
		}
		public String getEcStockRatio() {
			return ecStockRatio;
		}
		public void setEcStockRatio(String ecStockRatio) {
			this.ecStockRatio = ecStockRatio;
		}

	}



}
