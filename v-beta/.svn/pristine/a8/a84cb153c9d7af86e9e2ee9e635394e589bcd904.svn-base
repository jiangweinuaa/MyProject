package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECDZTBookQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDZTBookQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 大智通配送流水查询
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECDZTBookQuery extends SPosBasicService<DCP_OrderECDZTBookQueryReq, DCP_OrderECDZTBookQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECDZTBookQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECDZTBookQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDZTBookQueryReq>(){};
	}

	@Override
	protected DCP_OrderECDZTBookQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDZTBookQueryRes();
	}

	@Override
	protected DCP_OrderECDZTBookQueryRes processJson(DCP_OrderECDZTBookQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECDZTBookQueryRes res = this.getResponse();
		String sql = null;
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			sql = this.getQuerySql(req);
			String[] conditionValues = {}; //查詢條件
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues);
			
			String num = getQDataDetail.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(new ArrayList<DCP_OrderECDZTBookQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_OrderECDZTBookQueryRes.level1Elm lev1 = res.new level1Elm();
					String dztNo = oneData.get("DZTNO").toString();
					String dztDescription = oneData.get("DZTDESCRIPTION").toString();
					String status = oneData.get("STATUS").toString();
					String startNo = oneData.get("STARTNO").toString();
					String endNo = oneData.get("ENDNO").toString();
					String lastNo = oneData.get("LASTNO").toString();
					String inputDate = oneData.get("INPUTDATE").toString();
				
					lev1.setDztNo(dztNo);
					lev1.setDztDescription(dztDescription);
					lev1.setStatus(status);
					lev1.setStartNo(startNo);
					lev1.setEndNo(endNo);
					lev1.setLastNo(lastNo);
					lev1.setInputDate(inputDate);
					
					res.getDatas().add(lev1);
					
					lev1 = null;
					
				}
				
			}
		} catch (Exception e) {

		}
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderECDZTBookQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String keyTxt = req.getKeyTxt();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append("SELECT * FROM ("
					+ " SELECT COUNT(DISTINCT DZTNO ) OVER() NUM ,dense_rank() over(ORDER BY DZTNO) rn ,"
					+ " DZTNO ,DZTDESCRIPTION,STATUS ,DZT_STARTNO AS startNO , DZT_ENDNO AS endNO  ,"
					+ " DZT_LASTNO AS lastNO , INPUTDATE"
					+ " FROM OC_SHIPBOOKDZT "
					+ " WHERE EID = '"+eId+"'  "
				);
		
		if (keyTxt != null && keyTxt.length()!=0) { 	
			sqlbuf.append( " AND  DZTNO LIKE '%%"+keyTxt+"%%' or  DZTDESCRIPTION like '%%"+keyTxt+"%%'  ");
		}
		sqlbuf.append( " ORDER BY  DZTNO  ) "
				+ " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by DZTNO  ");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
