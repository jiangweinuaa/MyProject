package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_DinnerTimeQueryReq;
import com.dsc.spos.json.cust.res.DCP_DinnerTimeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 餐段查询	
 * @author yuanyy 2019-09-17
 *
 */
public class DCP_DinnerTimeQuery extends SPosBasicService<DCP_DinnerTimeQueryReq, DCP_DinnerTimeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_DinnerTimeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	protected DCP_DinnerTimeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DinnerTimeQueryRes();
	}

	@Override
	protected DCP_DinnerTimeQueryRes processJson(DCP_DinnerTimeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;		
		
		//查詢資料
		DCP_DinnerTimeQueryRes res = null;
		res = this.getResponse();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_DinnerTimeQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			
			int pageNumber=req.getPageNumber();
			int pageSize=req.getPageSize();
			
			if(pageNumber == 0){
				pageNumber = 1;
			}

			if(pageSize == 0){
				pageSize = 10000;
			}
				
			//算總頁數
			totalPages = totalRecords / pageSize;
			totalPages = (totalRecords % pageSize > 0) ? totalPages + 1 : totalPages;			
			
			for (Map<String, Object> oneData : getQData) 
			{
				
				DCP_DinnerTimeQueryRes.level1Elm oneLv1 = new DCP_DinnerTimeQueryRes().new level1Elm();
				oneLv1.setDtNo(oneData.get("DTNO").toString());
				oneLv1.setDtName(oneData.get("DTNAME").toString());
				oneLv1.setBeginTime(oneData.get("BEGINTIME").toString());
				oneLv1.setEndTime(oneData.get("ENDTIME").toString());
				oneLv1.setCreateDateTime(oneData.get("CREATEDATETIME").toString());
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setWorkNo(oneData.getOrDefault("WORKNO","").toString());
				oneLv1.setWorkName(oneData.getOrDefault("WORKNAME","").toString());
				oneLv1.setPriority(oneData.getOrDefault("PRIORITY", "0").toString());
				res.getDatas().add(oneLv1);
				oneLv1 = null;
			}
			
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;			
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
	protected String getQuerySql(DCP_DinnerTimeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		String eId = "";
		String shopId = "";
		
		if(Check.Null(req.getRequest().getoEId())){
			eId = req.geteId();
		}else{
			eId = req.getRequest().getoEId();
		}
		
		if(Check.Null(req.getShopId())){
			shopId = req.getShopId();
		}else{
			shopId = req.getShopId();
		}
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		
		if(pageNumber == 0){
			pageNumber = 1;
		}

		if(pageSize == 0){
			pageSize = 10000;
		}

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		String status=req.getRequest().getStatus();
		String keyTxt=req.getRequest().getKeyTxt();

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from "
				+ "("
				+ "select count(*) over() num,a.dtNo ,a.dtName , a.begin_time as beginTime, a.end_time as endTime ,"
				+ " a.create_Datetime as createDateTime , a.status  ,row_number() over (order by a.priority,a.dtno) rn ,"
				+ " a.workNo , b.workName , a.priority  "
				+ " from  DCP_DINNERTIme a "
				+ " LEFT JOIN dcp_planWORK b on a.EID = b.EID and a.workNo = b.workNo "
				+ " where a.EID='"+eId+"' and a.SHOPID = '"+shopId+"'   ");

		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and a.status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.dtNo LIKE '%%"+ keyTxt +"%%' OR a.dtName LIKE '%%"+ keyTxt +"%%' ) ");
		}

		sqlbuf.append("  order by b.workNo, a.dtNo ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize) + "  order by rn " );

		sql=sqlbuf.toString();
		return sql;  
		
	}


	@Override
	protected TypeToken<DCP_DinnerTimeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DinnerTimeQueryReq>(){};
	}
	
}
