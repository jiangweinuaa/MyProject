package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import com.dsc.spos.json.cust.req.DCP_HorseLampQueryReq;
import com.dsc.spos.json.cust.res.DCP_HorseLampQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_HorseLampQuery extends SPosBasicService<DCP_HorseLampQueryReq, DCP_HorseLampQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_HorseLampQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_HorseLampQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_HorseLampQueryReq>(){};
	}

	@Override
	protected DCP_HorseLampQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_HorseLampQueryRes();
	}

	@Override
	protected DCP_HorseLampQueryRes processJson(DCP_HorseLampQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_HorseLampQueryRes res = null;
		res = this.getResponse();
		
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
			String sysDate =  matter.format(dt);
			
			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_HorseLampQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> map : queryDatas) {
					DCP_HorseLampQueryRes.level1Elm lv1 = res.new level1Elm();
					String billNo = map.getOrDefault("BILLNO", "").toString();
					String billName = map.getOrDefault("BILLNAME", "").toString();
					String beginDate = map.getOrDefault("BEGINDATE", "").toString();
					String endDate = map.getOrDefault("ENDDATE", "").toString();
//					
					String status = map.getOrDefault("STATUS", "").toString();
					String createopid = map.getOrDefault("CREATEOPID", "").toString();
					String createTime = map.getOrDefault("CREATETIME", "").toString();
					String lastmodiopid = map.getOrDefault("LASTMODIOPID", "").toString();
					String lastmodiTime = map.getOrDefault("LASTMODITIME", "").toString();
					
					
					String validStatus = "invalid";
					
					if(beginDate == null || beginDate.equals("")){
						beginDate = "1900-01-01"; 
					}
					
					if(endDate == null || endDate.equals("")){
						endDate = "2099-12-31"; 
					}
					
					if(!Check.Null(status) && status.equals("100") ){
						
						int result=sysDate.compareTo(beginDate);
						int result2 = sysDate.compareTo(endDate);
						
					    if(result >= 0 && result2 <= 0 ){ // beginDate <= 系统日期  < =endDate 
					    	validStatus = "valid";
					    }
					}
					
					lv1.setBillNo(billNo);
					lv1.setBillName(billName);
					lv1.setBeginDate(beginDate);
					lv1.setEndDate(endDate);
					lv1.setValidStatus(validStatus);
					lv1.setStatus(status);
					lv1.setCreateopid(createopid);
					lv1.setCreateTime(createTime);
					lv1.setLastmodiopid(lastmodiopid);
					lv1.setLastmoditime(lastmodiTime);
					
					res.getDatas().add(lv1);
					
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常！");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_HorseLampQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		

		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		String sysDate =  matter.format(dt);
		
		String validStatus = req.getRequest().getValidStatus();
		String status = req.getRequest().getStatus();
		String billNo = req.getRequest().getBillNo();
		String searchString = req.getRequest().getSearchString();
		
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT  count(*) OVER() as num, row_number() over (order by a.billno) rn,  "
				+ " a.billNO, a.billName  ,  to_char(a.beginDate,'yyyy-MM-dd') AS beginDate , to_char(a.endDate , 'yyyy-MM-dd') AS endDate  ,  "
				+ " a.status , a.createopid, to_char( a.createTime , 'yyyy-MM-dd hh24:mi:ss' ) AS createTime , a.lastmodiopid ,   "
				+ " to_char( a.lastmodiTime , 'yyyy-MM-dd hh24:mi:ss' ) AS lastmodiTime  "
				+ " FROM dcp_horselamp a "
				+ " WHERE a.eID = '"+req.geteId()+"' "
				);

		if (!Check.Null(status))
		{
			sqlbuf.append(" and a.status = '"+status+"' ");
		}

		
		if( !Check.Null(validStatus) && validStatus.equals("valid") ){
			
			if(Check.Null(status) || status.equals("100")){
				sqlbuf.append(" and 1=1 and ( a.status = 100 "
						+ " and '"+sysDate+"'  between  NVL( to_char(a.beginDate , 'yyyy-MM-dd') , '1900-01-01' )   "
					    + " and NVL( to_char(a.enddate , 'yyyy-MM-dd') , '2099-12-31' )  )   ");
			}
//			
		}
		if (!Check.Null(validStatus) && validStatus.equals("invalid") )
		{
			sqlbuf.append(" and ( 1=1 " );
			
			// 红艳姐姐搞这设计很无语， 逻辑很混乱
			if(Check.Null(status) && Check.Null(billNo) && Check.Null(searchString) ){ 
				
				sqlbuf.append( " or a.status = '0' or a.status = '-1' "  );
			}

			if(Check.Null(status) || status.equals("100")){
				sqlbuf.append("  or ( a.status = 100 "
						+ " and  NVL( to_char(a.beginDate , 'yyyy-MM-dd') , '1900-01-01' ) > '"+sysDate+"' "
					    + " or '"+sysDate+"' > NVL( to_char(a.enddate , 'yyyy-MM-dd') , '2099-12-31' ) ) " );
			}
			
			sqlbuf.append(" ) ");
			
		}
		
		if (!Check.Null(billNo))
		{
			sqlbuf.append(" and a.billNo = '"+billNo+"' ");
		}
		
		if (!Check.Null(searchString))
		{
			sqlbuf.append(" and ( a.billName like '%%"+searchString+"%%' or a.billNo like '%%"+searchString+"%%') ");
		}
		sqlbuf.append(" order by a.status , a.billNO   "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)  + "order by billNO desc "  );
		sql = sqlbuf.toString();
		return sql;
	}

}
