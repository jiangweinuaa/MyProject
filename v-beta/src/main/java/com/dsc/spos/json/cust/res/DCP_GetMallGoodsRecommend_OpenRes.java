package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_GetMallGoodsList_OpenRes.level1Elm;;

/**
 * 服务函数：DCP_GetMallGoodsRecommend_Open
 * 服务说明：获取线上商品推荐
 * @author jinzma 
 * @since  2020-09-27
 */
public class DCP_GetMallGoodsRecommend_OpenRes extends JsonRes{
	private levelElm datas;	
	public levelElm getDatas() {
		return datas;
	}
	public void setDatas(levelElm datas) {
		this.datas = datas;
	}

	public class levelElm{
		private List<level1Elm> referSame;
		private List<level1Elm> referAppend;

		public List<level1Elm> getReferSame() {
			return referSame;
		}
		public void setReferSame(List<level1Elm> referSame) {
			this.referSame = referSame;
		}
		public List<level1Elm> getReferAppend() {
			return referAppend;
		}
		public void setReferAppend(List<level1Elm> referAppend) {
			this.referAppend = referAppend;
		}	
	}

}
