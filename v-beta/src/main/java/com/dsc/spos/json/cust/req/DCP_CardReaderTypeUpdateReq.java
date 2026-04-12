package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_CardReaderTypeQuery
 * 服务说明：读卡器类型修改
 * @author wangzyc
 * @since  2020-12-8
 */
public class DCP_CardReaderTypeUpdateReq extends JsonBasicReq{
	
	private level1Elm request;
	
	
	public level1Elm getRequest() {
		return request;
	}

	public void setRequest(level1Elm request) {
		this.request = request;
	}

	public class level1Elm{
		private List<level2ELm> readerTypeList;

		public List<level2ELm> getReaderTypeList() {
			return readerTypeList;
		}

		public void setReaderTypeList(List<level2ELm> readerTypeList) {
			this.readerTypeList = readerTypeList;
		} 
		
	}
	
	public class level2ELm{
		private String readerType; // 读卡器编号
		private String readerName; //读卡器名称
		private String mediaType; // 介质类型：RF-射频卡 IC-接触式IC卡 RFID-射频卡ID卡 HCE-磁条卡，不可修改
		private String baud; // 默认波特率
		private String port; // 默认端口号
		private String sortId; // 显示序号
		private String memo; // 备注信息
		private String status; // 100-有效0-无效
		
		public String getReaderType() {
			return readerType;
		}
		public void setReaderType(String readerType) {
			this.readerType = readerType;
		}
		public String getReaderName() {
			return readerName;
		}
		public void setReaderName(String readerName) {
			this.readerName = readerName;
		}
		public String getMediaType() {
			return mediaType;
		}
		public void setMediaType(String mediaType) {
			this.mediaType = mediaType;
		}
		public String getBaud() {
			return baud;
		}
		public void setBaud(String baud) {
			this.baud = baud;
		}
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
			this.port = port;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
	}
}
