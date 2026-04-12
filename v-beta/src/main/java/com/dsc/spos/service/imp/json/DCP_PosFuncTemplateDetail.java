package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PosFuncTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_PosFuncTemplateDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PosFuncTemplateDetail extends SPosBasicService<DCP_PosFuncTemplateDetailReq,DCP_PosFuncTemplateDetailRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PosFuncTemplateDetailReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String templateId = req.getRequest().getTemplateId();
		if (Check.Null(templateId) ) 
		{
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PosFuncTemplateDetailReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PosFuncTemplateDetailReq>() {};
	}

	@Override
	protected DCP_PosFuncTemplateDetailRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_PosFuncTemplateDetailRes();
	}

	@Override
	protected DCP_PosFuncTemplateDetailRes processJson(DCP_PosFuncTemplateDetailReq req) throws Exception 
	{
		DCP_PosFuncTemplateDetailRes res=this.getResponse();

		String eId=req.geteId();
		String templateId = req.getRequest().getTemplateId();

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		DCP_PosFuncTemplateDetailRes.level1Elm lv1 = res.new level1Elm();

		if (getData!=null && getData.isEmpty()==false) 
		{

			Map<String, Boolean> condV = new HashMap<String, Boolean>(); //查詢條件
			condV.put("TEMPLATEID", true);			
			List<Map<String, Object>> templateList= MapDistinct.getMap(getData, condV);		

			condV.clear();
			condV.put("TEMPLATEID", true);		
			condV.put("LANG_TYPE", true);			
			List<Map<String, Object>> template_LangList= MapDistinct.getMap(getData, condV);	

			condV.clear();
			condV.put("TEMPLATEID", true);			
			condV.put("FUNCGROUP", true);			
			List<Map<String, Object>> template_FuncGroupList= MapDistinct.getMap(getData, condV);	

			condV.clear();
			condV.put("TEMPLATEID", true);			
			condV.put("FUNCNO", true);			
			List<Map<String, Object>> template_FuncNoList= MapDistinct.getMap(getData, condV);

			condV.clear();
			condV.put("TEMPLATEID", true);			
			condV.put("SHOPID", true);
			List<Map<String, Object>> template_RangeList= MapDistinct.getMap(getData, condV);	


			//

			lv1.setMemo(templateList.get(0).get("MEMO").toString());


			lv1.setRestrictShop(templateList.get(0).get("RESTRICTSHOP").toString());
			lv1.setStatus(templateList.get(0).get("STATUS").toString());
			lv1.setTemplateId(templateList.get(0).get("TEMPLATEID").toString());			
			lv1.setPageType(templateList.get(0).get("PAGETYPE").toString());
			lv1.setPageName(templateList.get(0).get("PAGENAME").toString());
			lv1.setCreateopid(templateList.get(0).get("CREATEOPID").toString());
			lv1.setCreateopname(templateList.get(0).get("CREATEOPNAME").toString());			
			lv1.setLastmodiname(templateList.get(0).get("LASTMODIOPNAME").toString());
			lv1.setLastmodiopid(templateList.get(0).get("LASTMODIOPID").toString());

			lv1.setLastmoditime(templateList.get(0).get("LASTMODITIME").toString());
			lv1.setCreatetime(templateList.get(0).get("CREATETIME").toString());

			String templatename="";
			//
			lv1.setTemplateName_lang(new ArrayList<DCP_PosFuncTemplateDetailRes.levelTemplateName>());
			for (Map<String, Object> map : template_LangList) 
			{
				DCP_PosFuncTemplateDetailRes.levelTemplateName Template_lang=res.new levelTemplateName();
				Template_lang.setLangType(map.get("LANG_TYPE").toString());
				Template_lang.setName(map.get("TEMPLATENAME").toString());
				lv1.getTemplateName_lang().add(Template_lang);

				if (map.get("LANG_TYPE").toString().equals(req.getLangType()))
				{
					templatename=map.get("TEMPLATENAME").toString();
				}
				Template_lang=null;
			}
			lv1.setTemplateName(templatename);

			//
			lv1.setFuncList(new ArrayList<DCP_PosFuncTemplateDetailRes.levelFuncGroup>());
			for (Map<String, Object> map : template_FuncGroupList) 
			{
				DCP_PosFuncTemplateDetailRes.levelFuncGroup groups=res.new levelFuncGroup();
				String funcGroup = map.get("FUNCGROUP").toString();
				groups.setFuncNoList(new ArrayList<DCP_PosFuncTemplateDetailRes.funcNo>());
				groups.setFuncGroup(funcGroup);
				groups.setSortId(map.get("SORTID").toString());
				groups.setFuncGroupName(map.get("FUNCGROUPNAME").toString());
				groups.setMemo(map.get("E_MEMO").toString());
				
				for (Map<String, Object> map_funcNo : template_FuncNoList) 
				{
					String funcGroup_detail = map_funcNo.get("FUNCGROUP").toString();
					if(funcGroup.equals(funcGroup_detail))
					{
						DCP_PosFuncTemplateDetailRes.funcNo oneLv2=res.new funcNo();
						oneLv2.setFuncNo(map_funcNo.get("FUNCNO").toString());
						oneLv2.setFuncName(map_funcNo.get("FUNCNAME").toString());
						oneLv2.setSortId(map_funcNo.get("SORTID").toString());

						groups.getFuncNoList().add(oneLv2);
					}

				}								

				lv1.getFuncList().add(groups);
				groups=null;				
			}


			//
			lv1.setRangeList(new ArrayList<DCP_PosFuncTemplateDetailRes.levelRange>());
			for (Map<String, Object> map : template_RangeList) 
			{
				DCP_PosFuncTemplateDetailRes.levelRange Template_range=res.new levelRange();
				Template_range.setShopId(map.get("SHOPID").toString());
				Template_range.setShopName(map.get("SHOPNAME").toString());		
				lv1.getRangeList().add(Template_range);
				Template_range=null;
			}


			//



		}

		res.setDatas(lv1);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_PosFuncTemplateDetailReq req) throws Exception 
	{
		String langType = req.getLangType();
		if(langType==null||langType.isEmpty())
		{
			langType = "zh_CN";
		}
		String eId = req.geteId();
		String templateId = req.getRequest().getTemplateId();
		String sql = null;

		StringBuffer sqlbuf=new StringBuffer(" select * from ( "
				+ " select a.*,aa.PAGENAME,B.LANG_TYPE,B.TEMPLATENAME,"
				+ " C.FUNCNO, C.FUNCGROUP,C.SORTID,CL.SORTID as SORTID_FUNCNO,CL.FUNCNAME, D.SHOPID,DL.ORG_NAME as SHOPNAME, "
				+ " e.funcgroupname,e.memo as E_MEMO  "
				+ " from DCP_POSFUNCTEMPLATE a "
				+ " left join DCP_POSPAGE aa on a.eid=aa.eid and a.pagetype=aa.pagetype "
				+ " left join DCP_POSFUNCTEMPLATE_LANG b on a.eid=b.eid and a.templateid=b.templateid "
				+ " left join DCP_POSFUNCTEMPLATE_FUNC c on a.eid=c.eid and a.templateid=c.templateid "
				+ " left join DCP_POSFUNC cL on a.eid=c.eid and C.FUNCNO=cL.FUNCNO "
				+ " left join DCP_POSFUNCTEMPLATE_RANGE d on a.eid=d.eid and a.templateid=d.templateid "
				+ " left join DCP_ORG_LANG DL on a.eid=DL.eid and D.shopID=DL.ORGANIZATIONNO and DL.Lang_Type='"+langType+"' "
				+ " left join DCP_POSPAGE_FUNCGROUP e on a.eid=e.eid and c.funcgroup=e.funcgroup and aa.pagetype=e.pagetype "
				+ " where a.eid='"+eId+"' and a.templateid='"+templateId+"' "
				+ " ) order by shopID,SORTID");
		sql = sqlbuf.toString();
		return sql;
	}



}
