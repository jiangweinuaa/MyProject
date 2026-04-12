package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;
public class DCP_NoticeCreateReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{
		private String type;
		private String title;
		private String content;
		private String noticeID;
		private List<level2Elm> datas;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getNoticeID() {
			return noticeID;
		}

		public void setNoticeID(String noticeID) {
			this.noticeID = noticeID;
		}

		public List<level2Elm> getDatas() {
			return datas;
		}

		public void setDatas(List<level2Elm> datas) {
			this.datas = datas;
		}
	}
	public  class level2Elm
	{
		private String fileName;
		private String filePath;
		private String fileData;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getFilePath() {
			return filePath;
		}
		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
		public String getFileData() {
			return fileData;
		}
		public void setFileData(String fileData) {
			this.fileData = fileData;
		}

	}
}
