package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
/**
 * 款式品类修改 2018-10-12
 * @author yuanyy
 *
 */
public class DCP_GoodsStyleUpdateReq extends JsonBasicReq {
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String styleNo;
		private String styleName;
		private String fileName;
		private String fileData;
		private String status;

		private List<level1Elm> datas;

		public String getStyleNo() {
			return styleNo;
		}
		public void setStyleNo(String styleNo) {
			this.styleNo = styleNo;
		}
		public String getStyleName() {
			return styleName;
		}
		public void setStyleName(String styleName) {
			this.styleName = styleName;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileData() {
			return fileData;
		}

		public void setFileData(String fileData) {
			this.fileData = fileData;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<level1Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level1Elm> datas) {
			this.datas = datas;
		}
	}
	public  class level1Elm{
		private String pluNo;
		private String pluShowName;
		private String specNo;
		private String flavorNo;
		private String status;

		public String getPluNo() {
			return pluNo;
		}
		public void setPluNo(String pluNo) {
			this.pluNo = pluNo;
		}
		public String getSpecNo() {
			return specNo;
		}
		public void setSpecNo(String specNo) {
			this.specNo = specNo;
		}
		public String getFlavorNo() {
			return flavorNo;
		}
		public void setFlavorNo(String flavorNo) {
			this.flavorNo = flavorNo;
		}
		public String getPluShowName() {
			return pluShowName;
		}
		public void setPluShowName(String pluShowName) {
			this.pluShowName = pluShowName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}

}
