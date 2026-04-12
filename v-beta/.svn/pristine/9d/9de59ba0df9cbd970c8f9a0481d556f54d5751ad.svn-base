package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_WorkQueryReq;
import com.dsc.spos.json.cust.res.DCP_WorkQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_WorkQuery extends SPosBasicService<DCP_WorkQueryReq, DCP_WorkQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_WorkQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_WorkQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_WorkQueryReq>(){};
	}

	@Override
	protected DCP_WorkQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_WorkQueryRes();
	}

	@Override
	protected DCP_WorkQueryRes processJson(DCP_WorkQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		DCP_WorkQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};
			List<Map<String , Object>> workDatas = this.doQueryData(sql, conditionValues);
			res.setDatas(new ArrayList<DCP_WorkQueryRes.level1Elm>());
			
			if(workDatas.size() > 0){
				
				String num = workDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
				
				for (Map<String, Object> oneData : workDatas) 
				{
					DCP_WorkQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String workNO = oneData.get("WORKNO").toString();
					String workName = oneData.get("WORKNAME").toString();
					String bTime = oneData.get("BTIME").toString();
					String eTime = oneData.get("ETIME").toString();
					String status = oneData.get("STATUS").toString();
					oneLv1.setWorkNo(workNO);
					oneLv1.setWorkName(workName);
					oneLv1.setbTime(bTime);
					oneLv1.seteTime(eTime);
					oneLv1.setStatus(status);
					res.getDatas().add(oneLv1);
					oneLv1=null;
				}
					
				
			}
			else{
				res.setDatas(new ArrayList<DCP_WorkQueryRes.level1Elm>());
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			return res;
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_WorkQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String workNO = req.getRequest().getWorkNo();
		String workName = req.getRequest().getWorkName();
		String status = req.getRequest().getStatus();
		String keyTxt = req.getRequest().getKeyTxt();

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		
		sqlbuf.append("SELECT * FROM ( "
					+ " SELECT COUNT(*) OVER() NUM,  row_number() OVER(ORDER BY workno ) rn ,  "
					+ "workNO, workName, BTIME , ETIME ,status ,EID  FROM DCP_WORK "
					+ "WHERE EID='"+eId+"' ");
		if(workNO != null && workNO.length() > 0){
			sqlbuf.append(" and workNO = '"+workNO+"'");
		}
		if(workName != null && workName.length() > 0){
			sqlbuf.append(" and workName like '%%"+workName+"%%'");
		}
		if(status != null && status.length() > 0){
			sqlbuf.append(" and status = '"+status+"'");
		}
		if(keyTxt != null && keyTxt.length() > 0){
			sqlbuf.append(" and ( workName like '%%"+keyTxt+"%%'  or workNo like '%%"+keyTxt+"%%'  ) ");
		}
		sqlbuf.append(")  WHERE rn > "+startRow+" and rn <= "+(startRow+pageSize)+" order by workNO  ");
		sql = sqlbuf.toString();
		return sql;
	}
	
}
