package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_OrderMemoQueryRes extends JsonRes {
	
	/**
	 * JSON Response		
	  {		
	       "success": true,	成功否	
	       "serviceStatus": "000",	服务状态代码	
	       "serviceDescription": "服务执行成功",	服务状态说明	
	       "datas": [		
	        {		
	             "msgName": "刀叉数量"	留言名称	
	             "msgType": "1"	留言类型	1.文本 2.数字 3.日期 4.图片 5.音频 （图片和音频暂时不用考虑）
	             "message": "8套"	留言内容	
	         },		
	        {		
	             "msgName": "刀叉数量"		
	             "msgType": "1"		
	             "message": "8套"		
	         }		
	 ]		
	 }		

	 */
	
	
	private List<level1Elm> datas;
	
	public class level1Elm
	{
		private String msgName;
		private String msgType;
		private String message;
		public String getMsgName() {
			return msgName;
		}
		public void setMsgName(String msgName) {
			this.msgName = msgName;
		}
		public String getMsgType() {
			return msgType;
		}
		public void setMsgType(String msgType) {
			this.msgType = msgType;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
}
	
