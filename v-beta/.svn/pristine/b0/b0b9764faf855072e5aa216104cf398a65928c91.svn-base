package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_FlavorQueryReq;
import com.dsc.spos.json.cust.res.DCP_FlavorQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

public class DCP_FlavorQuery extends SPosBasicService<DCP_FlavorQueryReq,DCP_FlavorQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_FlavorQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		String groupId = req.getRequest().getGroupId();

		/*if (Check.Null(groupId)) 
		{
			isFail = true;
			errMsg.append("口味分组编码不能为空 ");			
		}*/

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FlavorQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorQueryReq>() {};
	}

	@Override
	protected DCP_FlavorQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_FlavorQueryRes();
	}

	@Override
	protected DCP_FlavorQueryRes processJson(DCP_FlavorQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		DCP_FlavorQueryRes res=this.getResponse();

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_FlavorQueryRes.level1Elm>());
		
		
		String sqlDetail_Category=" select * from ( select count(*) num,flavorid from DCP_FLAVOR_CATEGORY "
				+ "where eid='"+eId+"' group by flavorid )";
		List<Map<String , Object>> getData_Detail_Category=this.doQueryData(sqlDetail_Category, null);

		String sqlDetail_Goods=" select * from ( select count(*) num,flavorid from DCP_FLAVOR_GOODS "
				+ "where eid='"+eId+"' group by flavorid )";
		List<Map<String , Object>> getData_Detail_Goods=this.doQueryData(sqlDetail_Goods, null);

		Map<String, Boolean> condV = new HashMap<String, Boolean>(); //查詢條件
		condV.put("FLAVORID", true);			
		List<Map<String, Object>> HeaderList= MapDistinct.getMap(getData, condV);	

		for (Map<String, Object> oneData : HeaderList) 
		{
			DCP_FlavorQueryRes.level1Elm lv1=res.new level1Elm();
			String flavorId = oneData.get("FLAVORID").toString();
			lv1.setFlavorId(flavorId);
			lv1.setFlavorName(oneData.get("FLAVORNAME").toString());			
			lv1.setGroupId(oneData.get("GROUPID").toString());
			lv1.setGroupName(oneData.get("GROUPNAME").toString() );
			lv1.setSortId(oneData.get("SORTID").toString());
			lv1.setFlavorName_lang(new ArrayList<DCP_FlavorQueryRes.levelFlavorName>());						
			for (Map<String, Object> map : getData) 
			{	
				if(map.get("LANG_TYPE")==null||map.get("LANG_TYPE").toString().isEmpty())
				{
					continue;
				}
				if(flavorId.equals(map.get("FLAVORID").toString()))
				{
					DCP_FlavorQueryRes.levelFlavorName flavor=res.new levelFlavorName();
					String langType = map.get("LANG_TYPE").toString();
					String name = map.get("FLAVORNAME").toString();
					flavor.setLangType(langType);
					flavor.setName(name);
					if(langType.equals(curLangType))
					{
						lv1.setFlavorName(name);						
					}				
					lv1.getFlavorName_lang().add(flavor);
					flavor=null;		
				}
				
				
					
			}
			
			lv1.setSubCategoryCount("0");
			lv1.setSubGoodsCount("0");
			if(getData_Detail_Category!=null)
			{
				for (Map<String, Object> par : getData_Detail_Category)
				{
					if(flavorId.equals(par.get("FLAVORID").toString()))
					{
						lv1.setSubCategoryCount(par.get("NUM").toString());
						break;
					}
					
				}
			}

			if(getData_Detail_Goods!=null)
			{
				for (Map<String, Object> par : getData_Detail_Goods)
				{
					if(flavorId.equals(par.get("FLAVORID").toString()))
					{
						lv1.setSubGoodsCount(par.get("NUM").toString());
						break;
					}
					
				}
			}			
			res.getDatas().add(lv1);
			lv1=null;
		}



	


		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_FlavorQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		String groupId = req.getRequest().getGroupId();


		String sql = null;


		StringBuffer sqlbuf=new StringBuffer("select * from ( "
				+ "select A.*,B.LANG_TYPE,B.FLAVORNAME , d.groupName "
				+ "from dcp_flavor a "
				+ "left join dcp_flavor_lang b on a.eid=b.eid and a.flavorid=b.flavorid  "
				+ " left join dcp_flavorgroup_lang d on a.eid = d.eid and a.groupId = d.groupId and d.lang_type = '"+req.getLangType()+"'  "
				+ "where a.eid='"+eId+"' "); 

		if(groupId != null && groupId.length() >0)
		{
			sqlbuf.append("and a.groupid='"+groupId +"' ");
		}

		if(ketTxt != null && ketTxt.length() >0)
		{
			sqlbuf.append("and (a.flavorid like '%%"+ketTxt+"%%' or b.flavorname like '%%"+ketTxt+"%%') ");
		}

		sqlbuf.append(" )");

		sql = sqlbuf.toString();
		return sql;
	}
	
	
	
	protected String getQuerySql_Detail_Goods(DCP_FlavorQueryReq req,String withasSql) throws Exception 
	{
		String eId = req.geteId();

		String sql = null;
		StringBuffer sqlbuf=new StringBuffer(""
				+ "with p1 AS ( "+withasSql+" ) "
				+ "select p1.FLAVORID,p1.FLAVORNAME,a.groupid,a.sortid,c.pluno "
				+ "FROM p1 "
				+ "inner join dcp_flavor a on p1.flavorid=a.flavorid "
				+ "inner join dcp_flavor_goods c on p1.flavorid=c.flavorid "
				+ "where a.eid='"+eId+"' "
				+ "order by a.sortid "); 

		sql = sqlbuf.toString();
		return sql;			
	}

	protected String getQuerySql_Detail_Category(DCP_FlavorQueryReq req,String withasSql) throws Exception 
	{
		String eId = req.geteId();

		String sql = null;
		StringBuffer sqlbuf=new StringBuffer(""
				+ "with p1 AS ( "+withasSql+" ) "
				+ "select p1.FLAVORID,p1.FLAVORNAME,a.groupid,a.sortid,c.category "
				+ "FROM p1 "
				+ "inner join dcp_flavor a on p1.flavorid=a.flavorid "
				+ "inner join dcp_flavor_category c on a.flavorid=c.flavorid "
				+ "where a.eid='"+eId+"' "
				+ "order by a.sortid "); 

		sql = sqlbuf.toString();
		return sql;			
	}

	protected String getQuerySql_Detail_Lang(DCP_FlavorQueryReq req,String withasSql) throws Exception 
	{
		String eId = req.geteId();

		String sql = null;
		StringBuffer sqlbuf=new StringBuffer(""
				+ "with p1 AS ( "+withasSql+" ) "
				+ "select p1.FLAVORID,p1.FLAVORNAME,a.groupid,a.sortid,c.lang_type,c.flavorname C_FLAVORNAME "
				+ "FROM p1 "
				+ "inner join dcp_flavor a on p1.flavorid=a.flavorid "
				+ "left join dcp_flavor_lang c on a.flavorid=c.flavorid "
				+ "where a.eid='"+eId+"' "
				+ "order by a.sortid "); 

		sql = sqlbuf.toString();
		return sql;			
	}





}
