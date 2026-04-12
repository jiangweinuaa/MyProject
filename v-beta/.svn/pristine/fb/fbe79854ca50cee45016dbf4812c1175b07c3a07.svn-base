package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_OrderCategoryQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderCategoryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderCategoryQuery extends SPosBasicService<DCP_OrderCategoryQueryReq,DCP_OrderCategoryQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderCategoryQueryReq req) throws Exception {				
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderCategoryQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderCategoryQueryReq>(){};
	}

	@Override
	protected DCP_OrderCategoryQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderCategoryQueryRes();
	}

	@Override
	protected DCP_OrderCategoryQueryRes processJson(DCP_OrderCategoryQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;		
		DCP_OrderCategoryQueryRes res = this.getResponse();	
		try
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			if (getQData != null && getQData.isEmpty() == false) 
			{
				res.setDatas(new ArrayList<DCP_OrderCategoryQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQData) 
				{
					DCP_OrderCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm(); 
					String categoryNO = oneData.get("CATEGORYNO").toString();
					String categoryName = oneData.get("CATEGORYNAME").toString();
					String priority = oneData.get("PRIORITY").toString();
					oneLv1.setCategoryNO(categoryNO);
					oneLv1.setCategoryName(categoryName);
					oneLv1.setPriority(priority);
					res.getDatas().add(oneLv1);
				}
			}
			else
			{
				res.setDatas(new ArrayList<DCP_OrderCategoryQueryRes.level1Elm>());				
			}
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
	protected String getQuerySql(DCP_OrderCategoryQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String keyTxt = req.getKeyTxt();
		String belFirm = req.getOrganizationNO();

		sqlbuf.append(" select CATEGORYNO,CATEGORYNAME,PRIORITY from OC_CATEGORY where EID='" + eId + "' ");

		if(belFirm!=null&&belFirm.length()>0)
		{
			sqlbuf.append(" and (BELFIRM = '"+belFirm+"'  ) ");
		}
		if (!Check.Null(keyTxt))
		{	  	
			sqlbuf.append(" and (CATEGORYNO like '%"+keyTxt+"%' or CATEGORYNAME like '%"+keyTxt+"%')");
		}
		sqlbuf.append(" order by PRIORITY ");
		sql = sqlbuf.toString();
		return sql;
	}





}
