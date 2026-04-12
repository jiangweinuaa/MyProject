package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_FlavorGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_FlavorGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_FlavorGroupQuery extends SPosBasicService<DCP_FlavorGroupQueryReq,DCP_FlavorGroupQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_FlavorGroupQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FlavorGroupQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorGroupQueryReq>() {};
	}

	@Override
	protected DCP_FlavorGroupQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_FlavorGroupQueryRes();
	}

	@Override
	protected DCP_FlavorGroupQueryRes processJson(DCP_FlavorGroupQueryReq req) throws Exception 
	{
		DCP_FlavorGroupQueryRes res=this.getResponse();

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_FlavorGroupQueryRes.level1Elm>());

		if (getData!=null && getData.isEmpty()==false) 
		{
			Map<String, Boolean> condV = new HashMap<String, Boolean>(); //查詢條件
			condV.put("GROUPID", true);			
			List<Map<String, Object>> HeaderList= MapDistinct.getMap(getData, condV);		

			for (Map<String, Object> map : HeaderList) 
			{
				DCP_FlavorGroupQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setExclusived(map.get("EXCLUSIVED").toString());
				lv1.setGroupId(map.get("GROUPID").toString());
				lv1.setGroupName(map.get("GROUPNAME").toString());
				lv1.setSortId(map.get("A_SORTID").toString());
				lv1.setGroupName_lang(new ArrayList<DCP_FlavorGroupQueryRes.levelGroup>() );
				lv1.setSubFlavorList(new ArrayList<DCP_FlavorGroupQueryRes.levelGroupFlavor>());

				//
				Map<String, Object> cond = new HashMap<String, Object>(); //查詢條件
				cond.put("GROUPID", map.get("GROUPID").toString());				
				List<Map<String, Object>> tempList=MapDistinct.getWhereMap(getData, cond, true);

				condV.clear(); //查詢條件
				condV.put("B_LANGTYPE", true);
				List<Map<String, Object>> groupnameList=MapDistinct.getMap(tempList, condV);		

				for (Map<String, Object> map2 : groupnameList) 
				{					
					if (map2.get("B_LANGTYPE").toString().equals("")==false) 
					{
						DCP_FlavorGroupQueryRes.levelGroup group=res.new levelGroup();

						group.setLangType(map2.get("B_LANGTYPE").toString());
						group.setName(map2.get("GROUPNAME").toString());

						lv1.getGroupName_lang().add(group);
						group=null;
					}

				}

				condV.clear(); //查詢條件
				condV.put("FLAVORID", true);
				condV.put("D_LANGTYPE", true);
				List<Map<String, Object>> flavorList=MapDistinct.getMap(tempList, condV);		

				for (Map<String, Object> map3 : flavorList) 
				{					
					if (map3.get("FLAVORID").toString().equals("")==false) 
					{
						DCP_FlavorGroupQueryRes.levelGroupFlavor flavor=res.new levelGroupFlavor();
						flavor.setFlavorId(map3.get("FLAVORID").toString());
						flavor.setFlavorName(map3.get("FLAVORNAME").toString());
						flavor.setSortId(map3.get("C_SORTID").toString());					
						lv1.getSubFlavorList().add(flavor);
						flavor=null;
					}					
				}

				res.getDatas().add(lv1);
				lv1=null;
			}

		}		

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_FlavorGroupQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}

		String sql = null;

		StringBuffer sqlbuf=new StringBuffer("select a.groupid,b.groupname,b.lang_type B_LANGTYPE,a.sortid A_SORTID,a.exclusived,c.sortid C_SORTID,d.flavorid,d.flavorname,d.lang_type D_LANGTYPE  "
				+ "from dcp_flavorgroup a "
				+ "left join dcp_flavorgroup_lang b on a.eid=b.eid and a.groupid=b.groupid "
				+ "left join dcp_flavor c on a.eid=c.eid and a.groupid=c.groupid "
				+ "left join dcp_flavor_lang d on c.eid=d.eid and c.flavorid=d.flavorid and d.lang_type='"+curLangType+"' "
				+ "where a.eid='"+eId+"' "); 


		if(ketTxt != null && ketTxt.length() >0)
		{
			sqlbuf.append("and (a.groupid like '%%"+ketTxt+"%%' or b.groupname like '%%"+ketTxt+"%%') ");
		}

		sql = sqlbuf.toString();
		return sql;
	}




}
