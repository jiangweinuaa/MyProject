package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsTemplateQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.regexp.internal.RE;

public class DCP_GoodsTemplateQuery extends SPosBasicService<DCP_GoodsTemplateQueryReq,DCP_GoodsTemplateQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateQueryReq req) throws Exception 
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
	protected TypeToken<DCP_GoodsTemplateQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateQueryReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateQueryRes();
	}

	@Override
	protected DCP_GoodsTemplateQueryRes processJson(DCP_GoodsTemplateQueryReq req) throws Exception 
	{
		DCP_GoodsTemplateQueryRes res=this.getResponse();
		String curLangtype = req.getLangType();
		if(curLangtype==null||curLangtype.isEmpty())
		{
			curLangtype = "zh_CN";
		}
		int totalRecords = 0; //总笔数
		int totalPages = 0;	

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_GoodsTemplateQueryRes.level1Elm>());
		
		if (getData!=null && getData.isEmpty()==false) 
		{
			String num = getData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
			condition.put("TEMPLATEID", true);
			List<Map<String , Object>> getHeader= MapDistinct.getMap(getData, condition);
			condition.put("LANG_TYPE", true);
			List<Map<String , Object>> getLang= MapDistinct.getMap(getData, condition);
			for (Map<String, Object> oneData : getHeader) 
			{
				DCP_GoodsTemplateQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setCreateopid(oneData.get("CREATEOPID").toString());
				lv1.setCreateopname(oneData.get("CREATEOPNAME").toString());
				lv1.setCreatetime(oneData.get("CREATETIME").toString());
				lv1.setLastmodiname(oneData.get("LASTMODIOPNAME").toString());
				lv1.setLastmodiopid(oneData.get("LASTMODIOPID").toString());
				lv1.setLastmoditime(oneData.get("LASTMODITIME").toString());
				lv1.setMemo(oneData.get("MEMO").toString());
                lv1.setRedisUpdateSuccess(oneData.get("REDISUPDATESUCCESS").toString());
				/*lv1.setRestrictAppType(oneData.get("RESTRICTAPPTYPE").toString());
				lv1.setRestrictChannel(oneData.get("RESTRICTCHANNEL").toString());
				
				lv1.setRestrictShop(oneData.get("RESTRICTSHOP").toString());*/
				lv1.setStatus(oneData.get("STATUS").toString());
				lv1.setTemplateId(oneData.get("TEMPLATEID").toString());
				lv1.setTemplateName(oneData.get("TEMPLATENAME").toString());
				lv1.setTemplateType(oneData.get("TEMPLATETYPE").toString());
				lv1.setCreateDeptId(oneData.get("CREATEDEPTID").toString());
				lv1.setCreateDeptName(oneData.get("CREATEDEPTNAME").toString());
				lv1.setRangeList(new ArrayList<DCP_GoodsTemplateQueryRes.levelRange>());
				
				String templateId = oneData.get("TEMPLATEID").toString();
				for (Map<String, Object> oneData2 : getData)
				{
					String rangeId = oneData2.get("ID").toString();
					String rangeName = oneData2.get("NAME").toString();
					String templateId_detail = oneData2.get("TEMPLATEID").toString();
					if(rangeId==null||rangeId.isEmpty())
					{
						continue;
					}
					if(templateId.equals(templateId_detail))
					{
						DCP_GoodsTemplateQueryRes.levelRange lv2=res.new levelRange();
						lv2.setId(rangeId);
						lv2.setName(rangeName);
						lv1.getRangeList().add(lv2);
					}																	
					
				}
				lv1.setTemplateName_lang(new ArrayList<DCP_GoodsTemplateQueryRes.levelTemplate>());
				for (Map<String, Object> oneDataLang : getLang)
				{
					String langType = oneDataLang.get("LANG_TYPE").toString();
					String name = oneDataLang.get("TEMPLATENAME").toString();
					String templateId_detail = oneDataLang.get("TEMPLATEID").toString();
					if(langType==null||langType.isEmpty())
					{
						continue;
					}
					if(templateId.equals(templateId_detail))
					{
						DCP_GoodsTemplateQueryRes.levelTemplate lv2=res.new levelTemplate();
						lv2.setLangType(langType);
						lv2.setName(name);
						if(curLangtype.equals(langType))
						{
							lv1.setTemplateName(name);
						}
						
						lv1.getTemplateName_lang().add(lv2);
					}																	
					
				}
				
				res.getDatas().add(lv1);
				lv1=null;
			}
		}

		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_GoodsTemplateQueryReq req) throws Exception 
	{

		String eId = req.geteId();
		String langtype = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		//String appType = req.getRequest().getAppType();
		//String channelId = req.getRequest().getChannelId();
		String shopId = req.getRequest().getShopId();
		String status = req.getRequest().getStatus();
        String redisUpdateSuccess = req.getRequest().getRedisUpdateSuccess();

		String sql = null;

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;		


		StringBuffer sqlbuf=new StringBuffer("select * from ( "
				+ "select count(distinct a.templateid ) over() num, dense_rank() over( order by a.templateid)  rn,a.templateid,b.LANG_TYPE,b.templatename,a.templatetype, "
				+ "a.memo,a.status,a.REDISUPDATESUCCESS,to_char(a.createtime,'yyyy-MM-dd HH24:mi:ss') createtime,a.createopid,e1.op_name as createopname,to_char(a.lastmoditime,'yyyy-MM-dd HH24:mi:ss') lastmoditime,a.lastmodiopid,e2.op_name as lastmodiopname,a.CREATEDEPTID,d.departname as createdeptname, r.id,r.name "
				+ "from dcp_goodstemplate a "
				+ "left join dcp_goodstemplate_lang b on a.eid=b.eid and a.templateid=b.templateid "
				+ "left join DCP_GOODSTEMPLATE_RANGE r on a.eid=r.eid and a.templateid=r.templateid " +
				" left join dcp_department_lang d on d.departno=a.CREATEDEPTID and a.eid=d.eid and d.lang_type='"+req.getLangType()+"' " +
				" left join PLATFORM_STAFFS_LANG e1 on a.createopid=e1.opno and a.eid=e1.eid and e1.lang_type='"+req.getLangType()+"' " +
				" left join PLATFORM_STAFFS_LANG e2 on a.lastmodiopid=e2.opno and a.eid=e2.eid and e2.lang_type='"+req.getLangType()+"' "
				+ "where a.eid='"+eId+"' "); 

		if(status != null && status.length() >0)
		{
			sqlbuf.append("and a.status="+status +" ");
		}
        if(redisUpdateSuccess != null && redisUpdateSuccess.length() >0)
        {
            sqlbuf.append("and a.REDISUPDATESUCCESS='"+redisUpdateSuccess +"' ");
        }

		if(ketTxt != null && ketTxt.length() >0)
		{
			sqlbuf.append("and (b.templateid like '%%"+ketTxt+"%%' or b.templatename like '%%"+ketTxt+"%%') ");
		}

		if(shopId != null && shopId.length() >0) //rangetype=2 是适用门店
		{
			sqlbuf.append("and "
					+ "(a.templateid in "
					+ "(select templateid from dcp_goodstemplate_range d  where d.eid='"+eId+"' and ((d.rangetype=2 and d.id='"+shopId+"') or " +
                    "(d.RANGETYPE  = 1 AND d.id = (select BELFIRM from dcp_org where ORGANIZATIONNO = '"+shopId+"' and eid = '"+eId+"'))) "
					+ ") "
					+ ") ");
		}
		
		/*if(channelId != null && channelId.length() >0) //rangetype=3 是适用渠道
		{
			sqlbuf.append("and "
					+ "(a.templateid in "
					+ "(select templateid from dcp_goodstemplate_range d  where d.eid='"+eId+"' and (d.rangetype=3 and d.id='"+channelId+"') "
					+ ") or a.restrictchannel=0 "
					+ ") ");
		}
		
		if(appType != null && appType.length() >0) //rangetype=4 是适用应用
		{
			sqlbuf.append("and "
					+ "(a.templateid in "
					+ "(select templateid from dcp_goodstemplate_range d  where d.eid='"+eId+"' and (d.rangetype=4 and d.id='"+appType+"') "
					+ ") or a.restrictapptype=0 "
					+ ") ");
		}*/

		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));

		sql = sqlbuf.toString();
		return sql;
	}




}
