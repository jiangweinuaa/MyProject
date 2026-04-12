package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_PayInQueryRes extends JsonRes
{
	private level1Elm datas;

	public level1Elm getDatas()
	{
		return datas;
	}

	public void setDatas(level1Elm datas)
	{
		this.datas = datas;
	}

	public class level1Elm
	{
		private List<level2Elm> payInList;

		public List<level2Elm> getPayInList()
		{
			return payInList;
		}

		public void setPayInList(List<level2Elm> payInList)
		{
			this.payInList = payInList;
		}

	}

	public class level2Elm
	{
		private String payInNo;
		private String bDate;
		private String rDate;
		private String memo;
		private String status;
		private String process_status;
		private String totAmt;
		private String comment;

		private String createOpId;
		private String createOpName;
		private String createTime;
		private String lastModiOpId;
		private String lastModiOpName;
		private String lastModiTime;

		public String getPayInNo()
		{
			return payInNo;
		}

		public void setPayInNo(String payInNo)
		{
			this.payInNo = payInNo;
		}

		public String getbDate()
		{
			return bDate;
		}

		public void setbDate(String bDate)
		{
			this.bDate = bDate;
		}

		public String getrDate()
		{
			return rDate;
		}

		public void setrDate(String rDate)
		{
			this.rDate = rDate;
		}

		public String getMemo()
		{
			return memo;
		}

		public void setMemo(String memo)
		{
			this.memo = memo;
		}

		public String getStatus()
		{
			return status;
		}

		public void setStatus(String status)
		{
			this.status = status;
		}

		public String getProcess_status()
		{
			return process_status;
		}

		public void setProcess_status(String process_status)
		{
			this.process_status = process_status;
		}

		public String getTotAmt()
		{
			return totAmt;
		}

		public void setTotAmt(String totAmt)
		{
			this.totAmt = totAmt;
		}

		public String getCreateOpId()
		{
			return createOpId;
		}

		public void setCreateOpId(String createOpId)
		{
			this.createOpId = createOpId;
		}

		public String getCreateOpName()
		{
			return createOpName;
		}

		public void setCreateOpName(String createOpName)
		{
			this.createOpName = createOpName;
		}

		public String getCreateTime()
		{
			return createTime;
		}

		public void setCreateTime(String createTime)
		{
			this.createTime = createTime;
		}

		public String getLastModiOpId()
		{
			return lastModiOpId;
		}

		public void setLastModiOpId(String lastModiOpId)
		{
			this.lastModiOpId = lastModiOpId;
		}

		public String getLastModiOpName()
		{
			return lastModiOpName;
		}

		public void setLastModiOpName(String lastModiOpName)
		{
			this.lastModiOpName = lastModiOpName;
		}

		public String getLastModiTime()
		{
			return lastModiTime;
		}

		public void setLastModiTime(String lastModiTime)
		{
			this.lastModiTime = lastModiTime;
		}

		public String getComment()
		{
			return comment;
		}

		public void setComment(String comment)
		{
			this.comment = comment;
		}

	}

}
