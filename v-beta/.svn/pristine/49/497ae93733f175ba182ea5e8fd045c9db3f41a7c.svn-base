package com.dsc.spos.json.cust.req;
import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.cust.req.DCP_TemplateShopQueryReq.levelRequest;
/**
 * 服務函數：TemplateShopUpdateDCP
 *    說明：模板生效门店修改
 * 服务说明：模板生效门店修改
 * @author jzma 
 * @since  2017-03-03
 */
public class DCP_TemplateShopUpdateReq extends JsonBasicReq 
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
		private String templateNo;
		private String docType;
		private List<level1Elm> datas;

		public List<level1Elm> getDatas() {
			return datas;
		}
		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}

		public String getDocType() {
			return docType;
		}
		public void setDocType(String docType) {
			this.docType = docType;
		}
		public String getTemplateNo()
		{
			return templateNo;
		}
		public void setTemplateNo(String templateNo)
		{
			this.templateNo = templateNo;
		}

		
	}


	public  class level1Elm
	{		
		private String shopId;
		private String goodsWarehouseNo;
		private String materialWarehouseNo;
		
		
		public String getShopId()
		{
			return shopId;
		}
		public void setShopId(String shopId)
		{
			this.shopId = shopId;
		}
		public String getGoodsWarehouseNo()
		{
			return goodsWarehouseNo;
		}
		public void setGoodsWarehouseNo(String goodsWarehouseNo)
		{
			this.goodsWarehouseNo = goodsWarehouseNo;
		}
		public String getMaterialWarehouseNo()
		{
			return materialWarehouseNo;
		}
		public void setMaterialWarehouseNo(String materialWarehouseNo)
		{
			this.materialWarehouseNo = materialWarehouseNo;
		}



	}

}

