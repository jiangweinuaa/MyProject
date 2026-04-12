package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.req.DCP_GoodsStuffQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsStuffQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 统一加料查询 
 * 2018-09-21
 * @author yuanyy
 */
public class DCP_GoodsStuffQuery extends SPosBasicService<DCP_GoodsStuffQueryReq, DCP_GoodsStuffQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsStuffQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsStuffQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsStuffQueryReq>(){};
	}

	@Override
	protected DCP_GoodsStuffQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsStuffQueryRes();
	}

	@Override
	protected DCP_GoodsStuffQueryRes processJson(DCP_GoodsStuffQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_GoodsStuffQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try 
		{
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};
			List<Map<String , Object>> getGoodsStuffDatas = this.doQueryData(sql, conditionValues);
			res.setDatas(new ArrayList<DCP_GoodsStuffQueryRes.level1Elm>());

			if(getGoodsStuffDatas.size() > 0){

				String num = getGoodsStuffDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

				for (Map<String, Object> oneData : getGoodsStuffDatas) 
				{
					DCP_GoodsStuffQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String stuffNO = oneData.get("STUFFNO").toString();
					String stuffName = oneData.get("STUFFNAME").toString();
					String priority = oneData.get("PRIORITY").toString();
					String status = oneData.get("STATUS").toString();
					oneLv1.setStuffNo(stuffNO);
					oneLv1.setStuffName(stuffName);
					oneLv1.setPriority(priority);
					oneLv1.setStatus(status);
					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}


			}
			else{
				res.setDatas(new ArrayList<DCP_GoodsStuffQueryRes.level1Elm>());
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

			return res;
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_GoodsStuffQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		String eId = req.geteId();
		String stuffNO  = req.getRequest().getStuffNo();
		String stuffName = req.getRequest().getStuffName();
		String status = req.getRequest().getStatus();
		String priority = req.getRequest().getPriority();
		String keyTxt = req.getRequest().getKeyTxt();

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( " select * from (SELECT COUNT(*) OVER() NUM , row_number() OVER(ORDER BY stuffNO) rn  ,"
				+ " stuffNO, stuffName, priority, status ,EID  "
				+ " FROM DCP_STUFF   "
				+ " WHERE EID = '"+eId+"' ");

		if(stuffNO != null && stuffNO.length() > 0){
			sqlbuf.append(" and stuffNO = '"+stuffNO+"'");
		}
		if(stuffName != null && stuffName.length() > 0){
			sqlbuf.append(" and stuffName like '%%"+stuffName+"%%'");
		}
		if(status != null && status.length() > 0){
			sqlbuf.append(" and status = '"+status+"'");
		}
		if(priority != null && priority.length() > 0){
			sqlbuf.append(" and priority = '"+priority+"'");
		}
		if(keyTxt != null && keyTxt.length() > 0){
			sqlbuf.append(" and (stuffNO like  '%%"+keyTxt+"%%' or stuffName like '%%"+keyTxt+"%%' ) ");
		}

		sqlbuf.append(" ) where rn > "+startRow+" and rn <= "+(startRow+pageSize)+" order by priority  ");

		sql = sqlbuf.toString();
		return sql;
	}

}
