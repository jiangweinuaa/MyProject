package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_NoticeQueryRes extends JsonRes
{
	private String totQty;
	private String unReadQty;	
	private List<level1Elm> datas;

	public String getTotQty() {
		return totQty;
	}
	public void setTotQty(String totQty) {
		this.totQty = totQty;
	}
	
	public String getUnReadQty() {
		return unReadQty;
	}
	public void setUnReadQty(String unReadQty) {
		this.unReadQty = unReadQty;
	}
	
	public List<level1Elm> getDatas() {
		return datas;
	}
	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm
	{
		private String noticeID;
		private String type;
		private String title;
		private String content;
		private String status;
		private String issueDate;
		private String issueByName;
		private String isRead;
		private String isFile;
		private List<level2Elm> datas;
		
		public String getNoticeID() {
			return noticeID;
		}
		public void setNoticeID(String noticeID) {
			this.noticeID = noticeID;
		}
		
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
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
		public String getIssueDate() {
			return issueDate;
		}
		public void setIssueDate(String issueDate) {
			this.issueDate = issueDate;
		}
		
		public String getIssueByName() {
			return issueByName;
		}
		public void setIssueByName(String issueByName) {
			this.issueByName = issueByName;
		}
		
		public String getIsRead() {
			return isRead;
		}
		public void setIsRead(String isRead) {
			this.isRead = isRead;
		}
		
		public String getIsFile() {
			return isFile;
		}
		public void setIsFile(String isFile) {
			this.isFile = isFile;
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
		private String fileName;
		private String filePath;
		
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
	}	
}

