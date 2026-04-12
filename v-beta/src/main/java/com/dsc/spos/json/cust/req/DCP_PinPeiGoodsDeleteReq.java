package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
/**
 * 服务函数：DCP_PinPeiGoodsDeleteReq
 * 服务说明：拼胚商品删除
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiGoodsDeleteReq extends JsonBasicReq{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

	public class levelElm{
		private List<level1Elm> pluList;

		public List<level1Elm> getPluList() {
			return pluList;
		}

		public void setPluList(List<level1Elm> pluList) {
			this.pluList = pluList;
		}
	}
	public class level1Elm{
		private String pluNo;
		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
	}
}
