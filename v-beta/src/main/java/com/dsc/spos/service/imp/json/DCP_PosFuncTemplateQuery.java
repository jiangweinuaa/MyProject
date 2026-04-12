package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PosFuncTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_PosFuncTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_PosFuncTemplateQuery extends SPosBasicService<DCP_PosFuncTemplateQueryReq,DCP_PosFuncTemplateQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_PosFuncTemplateQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PosFuncTemplateQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PosFuncTemplateQueryReq>() {};
	}

	@Override
	protected DCP_PosFuncTemplateQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_PosFuncTemplateQueryRes();
	}

	@Override
	protected DCP_PosFuncTemplateQueryRes processJson(DCP_PosFuncTemplateQueryReq req) throws Exception 
	{
		DCP_PosFuncTemplateQueryRes res=this.getResponse();

		int totalRecords = 0; //总笔数
		int totalPages = 0;	

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_PosFuncTemplateQueryRes.level1Elm>());
		
		if (getData!=null && getData.isEmpty()==false) 
		{
			String num = getData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

			for (Map<String, Object> oneData : getData) 
			{
				DCP_PosFuncTemplateQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setCreateopid(oneData.get("CREATEOPID").toString());
				lv1.setCreateopname(oneData.get("CREATEOPNAME").toString());
				lv1.setCreatetime(oneData.get("CREATETIME").toString());
				lv1.setLastmodiname(oneData.get("LASTMODIOPNAME").toString());
				lv1.setLastmodiopid(oneData.get("LASTMODIOPID").toString());
				lv1.setLastmoditime(oneData.get("LASTMODITIME").toString());
				lv1.setMemo(oneData.get("MEMO").toString());
			
				
				lv1.setRestrictShop(oneData.get("RESTRICTSHOP").toString());
				lv1.setStatus(oneData.get("STATUS").toString());
				lv1.setTemplateId(oneData.get("TEMPLATEID").toString());
				lv1.setTemplateName(oneData.get("TEMPLATENAME").toString());
				lv1.setPageType(oneData.get("PAGETYPE").toString());
				lv1.setPageName(oneData.get("PAGENAME").toString());
				
				res.getDatas().add(lv1);
				lv1=null;
			}
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_PosFuncTemplateQueryReq req) throws Exception 
	{

		String eId = req.geteId();
		String langtype = req.getLangType();
		if(langtype==null||langtype.isEmpty())
		{
			langtype = "zh_CN";
		}
		String ketTxt = req.getRequest().getKeyTxt();	
		String status = req.getRequest().getStatus();

		String sql = null;

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		


		StringBuffer sqlbuf=new StringBuffer("select * from ( "
				+ " select count(*) over() num, rownum rn,a.templateid,b.templatename,a.PAGETYPE,a.restrictshop, "
				+ " a.memo,a.status,to_char(a.createtime,'yyyy-MM-dd HH24:mi:ss') createtime,a.createopid,a.createopname,to_char(a.lastmoditime,'yyyy-MM-dd HH24:mi:ss') lastmoditime,a.lastmodiopid,a.lastmodiopname, "
				+ " c.pagename "
				+ " from DCP_POSFUNCTEMPLATE a "
				+ " left join DCP_POSFUNCTEMPLATE_LANG b on a.eid=b.eid and a.templateid=b.templateid and b.lang_type='"+langtype+"' "
				+ " left join DCP_POSPAGE c on a.eid=c.eid and a.pagetype=c.pagetype"
				+ " where a.eid='"+eId+"' "); 

		if(status != null && status.length() >0)
		{
			sqlbuf.append("and a.status="+status +" ");
		}

		if(ketTxt != null && ketTxt.length() >0)
		{
			sqlbuf.append("and (a.templateid like '%%"+ketTxt+"%%' or b.templatename like '%%"+ketTxt+"%%') ");
		}

		/*if(shopId != null && shopId.length() >0) //rangetype=2 是适用门店
		{
			sqlbuf.append("and "
					+ "(a.templateid in "
					+ "(select templateid from DCP_POSFUNCTEMPLATE_FUNC d  where d.eid='"+eId+"' and (d.rangetype=2 and d.id='"+shopId+"') "
					+ ") or a.restrictshop=0 "
					+ ") ");
		}
		
		if(channelId != null && channelId.length() >0) //rangetype=3 是适用渠道
		{
			sqlbuf.append("and "
					+ "(a.templateid in "
					+ "(select templateid from DCP_POSFUNCTEMPLATE_FUNC d  where d.eid='"+eId+"' and (d.rangetype=3 and d.id='"+channelId+"') "
					+ ") or a.restrictchannel=0 "
					+ ") ");
		}
		
		if(appType != null && appType.length() >0) //rangetype=4 是适用应用
		{
			sqlbuf.append("and "
					+ "(a.templateid in "
					+ "(select templateid from DCP_POSFUNCTEMPLATE_FUNC d  where d.eid='"+eId+"' and (d.rangetype=4 and d.id='"+appType+"') "
					+ ") or a.restrictapptype=0 "
					+ ") ");
		}*/

		sqlbuf.append(" order by a.templateid ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlbuf.toString();
		return sql;
	}




}
