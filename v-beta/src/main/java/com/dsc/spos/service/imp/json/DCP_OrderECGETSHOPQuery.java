package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECGETSHOPQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECGETSHOPQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECGETSHOPQuery extends SPosBasicService<DCP_OrderECGETSHOPQueryReq, DCP_OrderECGETSHOPQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECGETSHOPQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		String ecOrderNo = req.getEcOrderNo();
		
		if(Check.Null(ecOrderNo))
		{
			errCt++;
			errMsg.append("电商单号不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECGETSHOPQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECGETSHOPQueryReq>(){};
	}

	@Override
	protected DCP_OrderECGETSHOPQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECGETSHOPQueryRes();
	}

	@Override
	protected DCP_OrderECGETSHOPQueryRes processJson(DCP_OrderECGETSHOPQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECGETSHOPQueryRes res = null;
		res = this.getResponse();
		
		String sql = "";
		sql = this.getQuerySql(req);
		List<Map<String, Object>> allDatas=this.doQueryData(sql, null);
		if(allDatas!=null&&!allDatas.isEmpty())
		{
			res.setDatas(new ArrayList< DCP_OrderECGETSHOPQueryRes.level1Elm>());
			
			for (Map<String, Object> map : allDatas) {
				DCP_OrderECGETSHOPQueryRes.level1Elm lev1 = res.new level1Elm();
				String distributorNo = map.get("DISTRIBUTORNO").toString();
				String distributorName = map.get("DISTRIBUTORNAME").toString();
				String getshopNo = map.get("GETSHOPNO").toString();
				String getshopName = map.get("GETSHOPNAME").toString();
				String tel = map.get("TEL").toString();
				String address = map.get("ADDRESS").toString();
				String sDate = map.get("SDATE").toString();
				String sTime = map.get("STIME").toString();
				String ecOrderNo = map.get("ECORDERNO").toString(); 
				String timeNo = map.get("TIMENO").toString();
				
				lev1.setDistributorNo(distributorNo);
				lev1.setDistributorName(distributorName);
				lev1.setGetshopNo(getshopNo);
				lev1.setGetshopName(getshopName);
				lev1.setTel(tel);
				lev1.setAddress(address);
				lev1.setsDate(sDate);
				lev1.setsTime(sTime);
				lev1.setEcOrderNo(ecOrderNo);
				lev1.setTimeNo(timeNo);
				
				res.getDatas().add(lev1);
				lev1 = null;
			}
			
			
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderECGETSHOPQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String ecOrderNo = req.getEcOrderNo();
		String timeNo = req.getTimeNo();
		
		sqlbuf.append(" SELECT orderNo AS ecorderNo, distributorNo , distributorName , "
				+ " getshopNO , getShopName, tel , address , status  ,sDate , sTime , timeNo "
				+ " FROM OC_lgMap "
				+ " where EID = '"+eId+"' and SHOPID = '"+shopId+"' ");

		if(!ecOrderNo.equals("") && ecOrderNo.length() > 0 ){
			sqlbuf.append(" and  orderNo = '"+ecOrderNo+"'   ");
		}
		
		if(!timeNo.equals("") && timeNo.length() > 0 ){
			sqlbuf.append(" and  timeNo = '"+timeNo+"'   ");
		}
		
		sql = sqlbuf.toString();
		return sql;
	}

	

}
