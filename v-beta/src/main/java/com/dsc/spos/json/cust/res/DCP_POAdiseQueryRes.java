package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：POAdiseGet
 *    說明：要货建议量查询
 * 服务说明：要货建议量查询
 * @author luoln 
 * @since  2017-07-04
 */
public class DCP_POAdiseQueryRes extends JsonRes{
	/**
	 * 	"datas": [		
		{		
            "pluNO": "SP2001",	必傳且非空，商品编码	
            "featureNO": "2001",	必傳，商品特征码	
            "refWQty": "1",	参考库存数	
            "refPQty": "1",	已要货数	
            "datas": [		
             {		
                  "refDate": "1",	参考日期	
                  "refSQty": "1",	参考销量数	
             }		
		}
	 */
	
	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{
		/**
		 * 	"pluNO": "SP2001",	必傳且非空，商品编码	
            "featureNO": "2001",	必傳，商品特征码	
            "refWQty": "1",	参考库存数	
            "refPQty": "1",	已要货数
		 */
		
		private String pluNO;
		//private String featureNO;
		private float  refWQty;
		private float refPQty;
		private List<level2Elm> datas;
		
		public String getPluNO() {
			return pluNO;
		}
		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}
		
		
		
		public float getRefWQty() {
			return refWQty;
		}
		public void setRefWQty(float refWQty) {
			this.refWQty = refWQty;
		}
		
		public float getRefPQty() {
			return refPQty;
		}
		public void setRefPQty(float refPQty) {
			this.refPQty = refPQty;
		}
		
		public List<level2Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
	}
	
	public class level2Elm
	{
		/**
		 * 	"refDate": "1",	参考日期	
          	"refSQty": "1",	参考销量数
		 */
		
		private String refDate;
		private float refSQty;
		
		public String getRefDate() {
			return refDate;
		}
		public void setRefDate(String refDate) {
			this.refDate = refDate;
		}
		
		public float getRefSQty() {
			return refSQty;
		}
		public void setRefSQty(float refSQty) {
			this.refSQty = refSQty;
		}
	}
}
