package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_PinPeiGoodsQuery
 * 服务说明：拼胚商品查询
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiGoodsQueryRes extends JsonRes{
	private List<level1Elm> datas;
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	public class level1Elm{
		private String pluNo;
		private String pluName;
		private String listImage;
		private String status;
		private String createOpId;
		private String createOpName;
		private String createTime;
		private String lastModiOpId;
		private String lastModiOpName;
		private String lastModiTime;

		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getPluName() {
			return pluName;
		}
		public void setPluName(String pluName) {
			this.pluName = pluName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getListImage() {
			return listImage;
		}
		public void setListImage(String listImage) {
			this.listImage = listImage;
		}
		public String getCreateOpId() {
			return createOpId;
		}
		public void setCreateOpId(String createOpId) {
			this.createOpId = createOpId;
		}
		public String getCreateOpName() {
			return createOpName;
		}
		public void setCreateOpName(String createOpName) {
			this.createOpName = createOpName;
		}
		public String getLastModiOpId() {
			return lastModiOpId;
		}
		public void setLastModiOpId(String lastModiOpId) {
			this.lastModiOpId = lastModiOpId;
		}
		public String getLastModiOpName() {
			return lastModiOpName;
		}
		public void setLastModiOpName(String lastModiOpName) {
			this.lastModiOpName = lastModiOpName;
		}
		public String getLastModiTime() {
			return lastModiTime;
		}
		public void setLastModiTime(String lastModiTime) {
			this.lastModiTime = lastModiTime;
		}


	}
}
