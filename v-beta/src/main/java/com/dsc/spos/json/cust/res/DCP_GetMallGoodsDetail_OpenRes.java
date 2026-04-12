package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_GetMallGoodsDetail_Open
 * 服务说明：获取线上商品详情图文
 * @author jinzma 
 * @since  2020-10-13
 */
public class DCP_GetMallGoodsDetail_OpenRes extends JsonRes{
	private level1Elm datas;
	public level1Elm getDatas() {
		return datas;
	}
	public void setDatas(level1Elm datas) {
		this.datas = datas;
	}
	public class level1Elm{
		private List<level2ElmDetailcomponents> detailcomponents;
		private List<level2ElmProductParam> productParam;
		public List<level2ElmDetailcomponents> getDetailcomponents() {
			return detailcomponents;
		}
		public void setDetailcomponents(List<level2ElmDetailcomponents> detailcomponents) {
			this.detailcomponents = detailcomponents;
		}
		public List<level2ElmProductParam> getProductParam() {
			return productParam;
		}
		public void setProductParam(List<level2ElmProductParam> productParam) {
			this.productParam = productParam;
		}
	}
	public class level2ElmDetailcomponents{
		private String serialNo;
		private String type;
		private String content;
		public String getSerialNo() {
			return serialNo;
		}
		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
	public class level2ElmProductParam{
		private String serialNo;
		private String paramName;
		private String paramId;
		private String param;
		
		public String getSerialNo() {
			return serialNo;
		}
		public void setSerialNo(String serialNo) {
			this.serialNo = serialNo;
		}
		public String getParamName() {
			return paramName;
		}
		public void setParamName(String paramName) {
			this.paramName = paramName;
		}
		public String getParamId() {
			return paramId;
		}
		public void setParamId(String paramId) {
			this.paramId = paramId;
		}
		public String getParam() {
			return param;
		}
		public void setParam(String param) {
			this.param = param;
		}

	}
}
