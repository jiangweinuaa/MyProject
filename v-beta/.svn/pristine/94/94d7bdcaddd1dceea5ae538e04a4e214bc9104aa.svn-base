package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_PosPageTypeQueryRes extends JsonRes
{

	private List<level1Elm> datas;
	
	
	public class level1Elm
	{		
		private String pageType;
		private String pageName;		
		private String sortId;//显示顺序，排序第一列
		private String modularNo;
		private List<posPageGroup> funcList;
		
		
		public String getPageType() {
			return pageType;
		}
		public void setPageType(String pageType) {
			this.pageType = pageType;
		}
		public String getPageName() {
			return pageName;
		}
		public void setPageName(String pageName) {
			this.pageName = pageName;
		}
		public String getSortId() {
			return sortId;
		}
		public void setSortId(String sortId) {
			this.sortId = sortId;
		}
		public String getModularNo() {
			return modularNo;
		}
		public void setModularNo(String modularNo) {
			this.modularNo = modularNo;
		}
		public List<posPageGroup> getFuncList()
		{
			return funcList;
		}
		public void setFuncList(List<posPageGroup> funcList)
		{
			this.funcList = funcList;
		}
		
	}


	public List<level1Elm> getDatas() {
		return datas;
	}


	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	
	public class posPageGroup
	{
		private String funcGroup;//
		private String funcGroupName;//
		private String memo;//
		private List<posPageGroupFunc> funcNoList;//
		public String getFuncGroup()
		{
			return funcGroup;
		}
		public void setFuncGroup(String funcGroup)
		{
			this.funcGroup = funcGroup;
		}
		public String getFuncGroupName()
		{
			return funcGroupName;
		}
		public void setFuncGroupName(String funcGroupName)
		{
			this.funcGroupName = funcGroupName;
		}
		public String getMemo()
		{
			return memo;
		}
		public void setMemo(String memo)
		{
			this.memo = memo;
		}
		public List<posPageGroupFunc> getFuncNoList()
		{
			return funcNoList;
		}
		public void setFuncNoList(List<posPageGroupFunc> funcNoList)
		{
			this.funcNoList = funcNoList;
		}
		
		
		
	}
	public class posPageGroupFunc
	{
		private String funcNo;//
		private String funcName;//
		private String sortId;//
		
		public String getFuncNo()
		{
			return funcNo;
		}
		public void setFuncNo(String funcNo)
		{
			this.funcNo = funcNo;
		}
		public String getFuncName()
		{
			return funcName;
		}
		public void setFuncName(String funcName)
		{
			this.funcName = funcName;
		}
		public String getSortId()
		{
			return sortId;
		}
		public void setSortId(String sortId)
		{
			this.sortId = sortId;
		}		
		
		
	}
	
}
