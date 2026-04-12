package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_TagGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_TagGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_TagGroupQuery extends SPosBasicService<DCP_TagGroupQueryReq, DCP_TagGroupQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_TagGroupQueryReq req) throws Exception 
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
	protected TypeToken<DCP_TagGroupQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TagGroupQueryReq>() {
		};
	}

	@Override
	protected DCP_TagGroupQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TagGroupQueryRes();
	}

	@Override
	protected DCP_TagGroupQueryRes processJson(DCP_TagGroupQueryReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		DCP_TagGroupQueryRes res = this.getResponse();

	
		String sql=getQuerySql(req);
		List<Map<String , Object>> getData_Detail=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_TagGroupQueryRes.level1Elm>());

		Map<String, Boolean> condV = new HashMap<String, Boolean>(); //查詢條件
		condV.put("TAGGROUPTYPE", true);	
		condV.put("TAGGROUPNO", true);	
		List<Map<String, Object>> tagGroupList= MapDistinct.getMap(getData_Detail, condV);	
			
	    condV.clear(); //查詢條件
		condV.put("TAGGROUPTYPE", true);	
		condV.put("TAGGROUPNO", true);	
		condV.put("LANG_TYPE", true);
		List<Map<String, Object>> tagGroup_LangList= MapDistinct.getMap(getData_Detail, condV);
		
		condV.clear(); //查詢條件
		condV.put("TAGGROUPTYPE", true);	
		condV.put("TAGGROUPNO", true);	
		condV.put("TAGNO", true);
		List<Map<String, Object>> tagTypeList= MapDistinct.getMap(getData_Detail, condV);


		for (Map<String, Object> oneData : tagGroupList) 
		{
			DCP_TagGroupQueryRes.level1Elm lv1=res.new level1Elm();
			String tagGroupType = oneData.get("TAGGROUPTYPE").toString();
			String tagGroupNo = oneData.get("TAGGROUPNO").toString();
			
			lv1.setExclusived(oneData.get("EXCLUSIVED").toString());
			lv1.setSortId(oneData.get("A_SORTID").toString());				
			lv1.setTagGroupName(oneData.get("TAGGROUPNAME").toString());
			lv1.setTagGroupNo(tagGroupNo);
			lv1.setTagGroupType(tagGroupType);
		
			//
			lv1.setTagGroupName_lang(new ArrayList<DCP_TagGroupQueryRes.levelGroup_lang>());
			for (Map<String, Object> map : tagGroup_LangList) 
			{
				String langType_tagGroup = map.get("LANG_TYPE").toString();
				if(langType_tagGroup==null||langType_tagGroup.isEmpty())
				{
					continue;
				}
				String tagGroupType_detail = map.get("TAGGROUPTYPE").toString();
				String tagGroupNo_detail = map.get("TAGGROUPNO").toString();
				if(tagGroupType_detail.equals(tagGroupType)&&tagGroupNo_detail.equals(tagGroupNo))
				{
					DCP_TagGroupQueryRes.levelGroup_lang group_lang=res.new levelGroup_lang();
					group_lang.setLangType(map.get("LANG_TYPE").toString());
					group_lang.setName(map.get("TAGGROUPNAME").toString());
					lv1.getTagGroupName_lang().add(group_lang);
					group_lang=null;
				}				
								
			}
		
			lv1.setSubTagList(new ArrayList<DCP_TagGroupQueryRes.levelTypeTag>());
			for (Map<String, Object> map : tagTypeList) 
			{
				String tagNo = map.get("TAGNO").toString();
				if (tagNo==null||tagNo.isEmpty())
				{
					continue;
				}
				String tagGroupType_detail = map.get("TAGGROUPTYPE").toString();
				String tagGroupNo_detail = map.get("TAGGROUPNO").toString();
				if(tagGroupType_detail.equals(tagGroupType)&&tagGroupNo_detail.equals(tagGroupNo))
				{
					DCP_TagGroupQueryRes.levelTypeTag typeTag=res.new levelTypeTag();
					typeTag.setTagNo(map.get("TAGNO").toString());
					typeTag.setTagName(map.get("TAGNAME").toString());
					typeTag.setSortId(map.get("C_SORTID").toString());
					lv1.getSubTagList().add(typeTag);
					typeTag=null;
				}					
			}

			res.getDatas().add(lv1);
			lv1=null;
		}
	
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");



		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	protected String getQuerySql_Detail(DCP_TagGroupQueryReq req,String withasSql) throws Exception 
	{
		String eId = req.geteId();

		String sql = null;
		StringBuffer sqlbuf=new StringBuffer(""
				+ "with p1 AS ( "+withasSql+" ) "
				+ "select a.taggrouptype,a.taggroupno,b.taggroupname,b.lang_type,a.sortid AS A_SORTID,a.exclusived,c.tagno,d.tagname,d.lang_type as D_LANG_TYPE,c.sortid as C_SORTID "
				+ "FROM p1 "
				+ "inner join dcp_taggroup a on p1.taggrouptype=a.taggrouptype and p1.taggroupno=a.taggroupno "
				+ "left join dcp_taggroup_lang b on a.eid=b.eid and a.taggrouptype=b.taggrouptype and a.taggroupno=b.taggroupno "
				+ "left join dcp_tagtype c on a.eid=c.eid and a.taggrouptype=c.taggrouptype and a.taggroupno=c.taggroupno "
				+ "left join dcp_tagtype_lang d on a.eid=d.eid and a.taggrouptype=d.taggrouptype and c.tagno=d.tagno "
				+ "where a.eid='"+eId+"' "
				+ "order by a.taggroupno,c.tagno "); 

		sql = sqlbuf.toString();
		return sql;			
	}

	@Override
	protected String getQuerySql(DCP_TagGroupQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		String tagGrouptype = req.getRequest().getTagGroupType();


		String sql = null;
/*
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		*/


		StringBuffer sqlbuf=new StringBuffer("select * from ( "
				+ " select a.taggrouptype,a.taggroupno,b.taggroupname,b.lang_type,a.sortid AS A_SORTID,a.exclusived,c.tagno,d.tagname,d.lang_type as D_LANG_TYPE,c.sortid as C_SORTID "				
				+ " from dcp_taggroup a "
				+ " left join dcp_taggroup_lang b on a.eid=b.eid and a.taggrouptype=b.taggrouptype and a.taggroupno=b.taggroupno "
				+ " left join dcp_tagtype c on a.eid=c.eid and a.taggrouptype=c.taggrouptype and a.taggroupno=c.taggroupno "
				+ " left join dcp_tagtype_lang d on a.eid=d.eid and a.taggrouptype=d.taggrouptype and c.tagno=d.tagno  and d.lang_type='"+langtype+"' "
				+ " where a.eid='"+eId+"' "); 

		if(tagGrouptype != null && tagGrouptype.length() >0)
		{
			sqlbuf.append("and a.taggrouptype='"+tagGrouptype +"' ");
		}

		if(ketTxt != null && ketTxt.length() >0)
		{
			sqlbuf.append("and (b.taggroupno like '%%"+ketTxt+"%%' or b.taggroupname like '%%"+ketTxt+"%%') ");
		}

		sqlbuf.append(" order by a.taggroupno,c.tagno )");

		sql = sqlbuf.toString();
		return sql;

	}

}
