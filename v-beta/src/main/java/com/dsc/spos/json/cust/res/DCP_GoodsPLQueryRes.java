package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 服務函數：GoodsPLGet
 *    說明：商品损益查询
 * 服务说明：商品损益查询
 * @author luoln 
 * @since  2017-06-29
 */
public class DCP_GoodsPLQueryRes extends JsonRes{
	/**JSON Response
	 * {	
	 		"success": true,	成功否
			"serviceStatus": "000",	服務狀態代碼
			"serviceDescription": "服務執行成功",	服務狀態說明
			"totQty": "1",	总销量
			"totAmt": "100",	总销额
			datas: [	
			   {	
			      "bDate": "20110101",	日期
			      "datas": [	
          			{	
              			"category": "001",	品类编码
              			"categoryName": "水电费",	品类名称
            			"datas": [	
		                  	{	
			                  "pluNO": "1",	商品编码
			                  "pluName": "001",	商品名称
			                  "featureNO": "001",	特征编码
			                  "featureName": "001",	特征名称
			                  "wunitName": "001",	单位名称
			                  "pScrapQty": "100",	生产报废数
                  			  "lossOutQty": "001",	报损出库数
                  			  "plQty": "001",	盈亏数量
			                  "price": "001",	单价
                  			  "amt": "001",	金额
		           			}
		           		]
                	}	
      			]	
	 * }	  
	**/
	
	private String totQty;
	private String totAmt;
	
	public String getTotQty() {
		return totQty;
	}
	public void setTotQty(String totQty) {
		this.totQty = totQty;
	}
	
	public String getTotAmt() {
		return totAmt;
	}
	public void setTotAmt(String totAmt) {
		this.totAmt = totAmt;
	}

	private List<level1Elm> datas;
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{
		private String bDate;
		private List<level2Elm> datas;
		
		public String getbDate() {
			return bDate;
		}
		public void setbDate(String bDate) {
			this.bDate = bDate;
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
		private String category;
		private String categoryName;
		private List<level3Elm> datas;

		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		
		public String getCategoryName() {
			return categoryName;
		}
		public void setCategoryName(String categoryName) {
			this.categoryName = categoryName;
		}
		
		public List<level3Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level3Elm> datas) {
			this.datas = datas;
		}
	}
	
	public class level3Elm
	{
		private String pluNO;
		private String pluName;
		private String featureNO;
		private String featureName;
		private String wunitName;
		private String pScrapQty;
		private String lossOutQty;
		private String plQty;
		private String price;
		private String amt;
		
		public String getPluNO() {
			return pluNO;
		}
		public void setPluNO(String pluNO) {
			this.pluNO = pluNO;
		}

		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		
		public String getFeatureNO() {
			return featureNO;
		}
		public void setFeatureNO(String featureNO) {
			this.featureNO = featureNO;
		}
		
		public String getFeatureName() {
			return featureName;
		}
		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}
		
		public String getWunitName() {
			return wunitName;
		}
		public void setWunitName(String wunitName) {
			this.wunitName = wunitName;
		}
		
		public String getpScrapQty() {
			return pScrapQty;
		}
		public void setpScrapQty(String pScrapQty) {
			this.pScrapQty = pScrapQty;
		}
		
		public String getLossOutQty() {
			return lossOutQty;
		}
		public void setLossOutQty(String lossOutQty) {
			this.lossOutQty = lossOutQty;
		}
		
		public String getPlQty() {
			return plQty;
		}
		public void setPlQty(String plQty) {
			this.plQty = plQty;
		}
		
		public String getPrice() {
			return price;
		}
		public void setPrice(String price) {
			this.price = price;
		}
		
		public String getAmt() {
			return amt;
		}
		public void setAmt(String amt) {
			this.amt = amt;
		}
	}
}
