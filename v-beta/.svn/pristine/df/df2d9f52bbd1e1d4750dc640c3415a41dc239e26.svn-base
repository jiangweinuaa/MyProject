package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xml.resolver.apps.resolver;

import com.dsc.spos.json.cust.req.DCP_PosPageTypeQueryReq;
import com.dsc.spos.json.cust.res.DCP_PosPageTypeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PosPageTypeQuery extends SPosBasicService<DCP_PosPageTypeQueryReq,DCP_PosPageTypeQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PosPageTypeQueryReq req) throws Exception 
	{		
		return false;		
	}

	@Override
	protected TypeToken<DCP_PosPageTypeQueryReq> getRequestType() 
	{
		return new TypeToken<DCP_PosPageTypeQueryReq>(){};
	}

	@Override
	protected DCP_PosPageTypeQueryRes getResponseType() 
	{
		return new DCP_PosPageTypeQueryRes();
	}

	@Override
	protected DCP_PosPageTypeQueryRes processJson(DCP_PosPageTypeQueryReq req) throws Exception 
	{
		String sql = null;		

		//查詢資料
		DCP_PosPageTypeQueryRes res = null;
		res = this.getResponse();
		//
		sql = this.getQuerySql(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);	
		
		res.setDatas(new ArrayList<DCP_PosPageTypeQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			//
			Map<String, Boolean> condV=new HashMap<String, Boolean>();
			condV.put("PAGETYPE", true);			
			List<Map<String, Object>> header=MapDistinct.getMap(getQData, condV);			
			
			for (Map<String, Object> oneData : header) 
			{
				DCP_PosPageTypeQueryRes.level1Elm oneLv1 = res.new level1Elm();

				oneLv1.setPageType(oneData.get("PAGETYPE").toString());
				oneLv1.setPageName(oneData.get("PAGENAME").toString());				
				oneLv1.setSortId(oneData.get("SORTID").toString());
				oneLv1.setModularNo(oneData.get("MODULARNO").toString());

				oneLv1.setFuncList(new ArrayList<DCP_PosPageTypeQueryRes.posPageGroup>());
				
				//过滤当前页面类型
				Map<String, Object> condG=new HashMap<>();
				condG.put("PAGETYPE", oneData.get("PAGETYPE").toString());				
				List<Map<String, Object>> groupList=MapDistinct.getWhereMap(getQData, condG, true);
				
				//过滤组
				Map<String, Boolean> condP=new HashMap<String, Boolean>();
				condP.put("FUNCGROUP", true);		
				List<Map<String, Object>> mygroupList=MapDistinct.getMap(groupList, condP);		
				for (Map<String, Object> map : mygroupList)
				{
					DCP_PosPageTypeQueryRes.posPageGroup group=res.new posPageGroup();					
					group.setFuncGroup(map.get("FUNCGROUP").toString());
					group.setFuncGroupName(map.get("FUNCGROUPNAME").toString());
					group.setMemo(map.get("MEMO").toString());					
					group.setFuncNoList(new ArrayList<DCP_PosPageTypeQueryRes.posPageGroupFunc>());
					
					//
					condG.clear();
					condG.put("PAGETYPE", map.get("PAGETYPE").toString());	
					condG.put("FUNCGROUP", map.get("FUNCGROUP").toString());	
					List<Map<String, Object>> groupFuncList=MapDistinct.getWhereMap(getQData, condG, true);
										
					for (Map<String, Object> map2 : groupFuncList)
					{
						DCP_PosPageTypeQueryRes.posPageGroupFunc func= res.new posPageGroupFunc();
						func.setFuncNo(map2.get("FUNCNO").toString());
						func.setFuncName(map2.get("FUNCNAME").toString());
						func.setSortId(map2.get("D_SORTID").toString());
						group.getFuncNoList().add(func);
					}
					
					oneLv1.getFuncList().add(group);
					group=null;					
				}				
				res.getDatas().add(oneLv1);
				oneLv1=null;
			}

		}

		return res;

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_PosPageTypeQueryReq req) throws Exception 
	{

		String sql=null;

		String eId = req.geteId();
		String keyTxt = null;
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();			
		}


		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("select a.*,b.funcgroup,b.funcgroupname,b.memo,c.funcno,d.funcname,d.sortid D_SORTID from DCP_POSPAGE a  "
				+ "left join DCP_POSPAGE_FUNCGROUP b on a.eid=b.eid and a.pagetype=b.pagetype "
				+ "left join DCP_POSPAGE_FUNC c on a.eid=c.eid and a.pagetype=c.pagetype and b.funcgroup=c.funcgroup "
				+ "left join DCP_POSFUNC d on a.eid=d.eid and c.funcno=d.funcno "
				+ ""
				+ "where a.EID='"+eId+"' ");	
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.PAGETYPE LIKE '%%"+ keyTxt +"%%' OR a.PAGENAME LIKE '%%"+ keyTxt +"%%' ) ");
		}
		sqlbuf.append(" order by a.SORTID,b.funcgroup,d.sortid ");
		sql=sqlbuf.toString();
		return sql;

	}



}
