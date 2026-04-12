package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECDCQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDCQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 大物流中心资料查询
 * @author yuanyy 2019-03-27
 *
 */
public class DCP_OrderECDCQuery extends SPosBasicService<DCP_OrderECDCQueryReq, DCP_OrderECDCQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECDCQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECDCQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDCQueryReq>(){};
	}

	@Override
	protected DCP_OrderECDCQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDCQueryRes();
	}

	@Override
	protected DCP_OrderECDCQueryRes processJson(DCP_OrderECDCQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECDCQueryRes res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		
		String sql = null;
		sql = this.getQuerySql(req);
		List<Map<String , Object>> getLangLabelDatas = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_OrderECDCQueryRes.level1Elm>());
		if(getLangLabelDatas.size() > 0){
			String num = getLangLabelDatas.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			for (Map<String, Object> oneData : getLangLabelDatas) 
			{
				DCP_OrderECDCQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String dcNo = oneData.get("DCNO").toString();
				String dcName = oneData.get("DCNAME").toString();
				String lgPlatformNo = oneData.get("LGPLATFORMNO").toString();
				String lgPlatformName = oneData.get("LGPLATFORMNAME").toString();
				String dcContactman = oneData.get("DCCONTACTMAN").toString();
				String dcPhone = oneData.get("DCPHONE").toString();
				String dcAddress = oneData.get("DCADDRESS").toString();
				String status = oneData.get("STATUS").toString();
				oneLv1.setDcNo(dcNo);
				oneLv1.setDcName(dcName);
				oneLv1.setLgPlatformNo(lgPlatformNo);
				oneLv1.setLgPlatformName(lgPlatformName);
				oneLv1.setDcContactman(dcContactman);
				oneLv1.setDcPhone(dcPhone);
				oneLv1.setDcAddress(dcAddress);
				oneLv1.setStatus(status);
				res.getDatas().add(oneLv1);
				oneLv1 = null;
			}
		}
		else{
			res.setDatas(new ArrayList<DCP_OrderECDCQueryRes.level1Elm>());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderECDCQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String keyTxt = req.getKeyTxt();
		
		String lgPlatformNo = req.getLgPlatformNo();
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		sqlbuf.append(" select * from ( "
				+ " select  COUNT(*) OVER() NUM , row_number() OVER(ORDER BY DCNO) rn ,"
				+ " DCNO , DCNAME , DCADDRESS , DCPHONE, "
				+ " DCCONTACTMAN, status , LGPLATFORMNO , LGPLATFORMNAME "
				+ " from  OC_LOGISTICS_DC "
				+ " where EID = '"+eId+"' " );
		if(keyTxt != null && keyTxt.length() > 0){
			sqlbuf.append("AND  ( DCNO LIKE '%%"+keyTxt+"%%%'  OR DCNAME LIKE '%%"+keyTxt+"%%' )  ");
		}
		
		if(lgPlatformNo != null && lgPlatformNo.length() > 0){
			sqlbuf.append("AND   LGPLATFORMNO = '"+lgPlatformNo+"' ");
		}
		
		sqlbuf.append(" order by DCNO ) where rn > "+startRow+" and rn <= "+(startRow+pageSize)+"   "
				+ " order by DCNO  ");
		sql = sqlbuf.toString();
		return sql;
	}
	
}
