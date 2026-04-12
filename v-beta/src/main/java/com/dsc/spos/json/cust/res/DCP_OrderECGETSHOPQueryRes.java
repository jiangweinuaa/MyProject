package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;

/**
 * 超商取货门店查询
 * @author yuanyy 2019-05-17
 *
 */
public class DCP_OrderECGETSHOPQueryRes extends JsonRes {
	
	/**
	 *   {	
	       "success": true,	成功否
	       "serviceStatus": "000",	服务状态代码
	       "serviceDescription": "服务执行成功",	服务状态说明
	       "datas": [	
	        {	
	             "distributorNo": "",	通路商代码
	             "distributorName": ""	通路商名称
	             "getshopNo": ""	取货门店编码
	             "getshopName": ""	取货门店名称
	             "tel": ""	电话
	             "address": ""	地址
	             "sDate": ""	日期
	             "sTime": ""	时间
	             "ecOrderNo": ""	电商单号
	             "timeNo":""  前端时间戳
	         }	
		 	]	
		 }	
	 */
	
	private List<level1Elm> datas;

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}
	
	public class level1Elm{
		private String distributorNo;
		private String distributorName;
		private String getshopNo;
		private String getshopName;
		private String tel;
		private String address;
		private String sDate;
		private String sTime;
		private String ecOrderNo;
		
		private String timeNo;
		
		
		public String getDistributorNo() {
			return distributorNo;
		}
		public void setDistributorNo(String distributorNo) {
			this.distributorNo = distributorNo;
		}
		public String getDistributorName() {
			return distributorName;
		}
		public void setDistributorName(String distributorName) {
			this.distributorName = distributorName;
		}
		public String getGetshopNo() {
			return getshopNo;
		}
		public void setGetshopNo(String getshopNo) {
			this.getshopNo = getshopNo;
		}
		public String getGetshopName() {
			return getshopName;
		}
		public void setGetshopName(String getshopName) {
			this.getshopName = getshopName;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getsDate() {
			return sDate;
		}
		public void setsDate(String sDate) {
			this.sDate = sDate;
		}
		public String getsTime() {
			return sTime;
		}
		public void setsTime(String sTime) {
			this.sTime = sTime;
		}
		public String getEcOrderNo() {
			return ecOrderNo;
		}
		public void setEcOrderNo(String ecOrderNo) {
			this.ecOrderNo = ecOrderNo;
		}
		public String getTimeNo() {
			return timeNo;
		}
		public void setTimeNo(String timeNo) {
			this.timeNo = timeNo;
		}
		
		
	}
	
	
}
