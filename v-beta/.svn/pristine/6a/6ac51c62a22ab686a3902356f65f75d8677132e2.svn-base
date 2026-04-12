package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PeriodQueryReq;
import com.dsc.spos.json.cust.res.DCP_PeriodQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_PeriodQuery extends SPosBasicService<DCP_PeriodQueryReq, DCP_PeriodQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PeriodQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_PeriodQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PeriodQueryReq>(){} ;
	}

	@Override
	protected DCP_PeriodQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PeriodQueryRes();
	}

	@Override
	protected DCP_PeriodQueryRes processJson(DCP_PeriodQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_PeriodQueryRes res = this.getResponse();
		String sql = null;
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		
		sql = this.getQuerySql(req);
		
		res.setDatas(new ArrayList<DCP_PeriodQueryRes.level1Elm>());
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			
		//分类
			Map<String, Boolean> condition_class = new HashMap<String, Boolean>(); //查詢條件				
			condition_class.put("PERIODNO", true);
		  //调用过滤函数
			List<Map<String, Object>> classDatas=MapDistinct.getMap(getQData, condition_class);
			
		  //付款方式
			Map<String, Boolean> condition_lang = new HashMap<String, Boolean>(); //查詢條件		
			condition_lang.put("PERIODNO", true);
			condition_lang.put("LANG_TYPE", true);
		  //调用过滤函数
			List<Map<String, Object>> langDatas=MapDistinct.getMap(getQData, condition_lang);
			
			 //付款方式
			Map<String, Boolean> condition_range = new HashMap<String, Boolean>(); //查詢條件		
			condition_range.put("PERIODNO", true);
			condition_range.put("SHOPID", true);
		  //调用过滤函数
			List<Map<String, Object>> rangeDatas=MapDistinct.getMap(getQData, condition_range);
			
			 //付款方式
			Map<String, Boolean> condition_time = new HashMap<String, Boolean>(); //查詢條件		
			condition_time.put("PERIODNO", true);
			condition_time.put("TIME", true);
		  //调用过滤函数
			List<Map<String, Object>> timeDatas=MapDistinct.getMap(getQData, condition_time);
			
			for (Map<String, Object> map : classDatas) 
			{
				String periodNo = map.get("PERIODNO").toString();
				//String periodName = map.get("PERIODNAME").toString();
				String beginTime = map.get("BEGINTIME").toString();
				String endTime = map.get("ENDTIME").toString();
				String memo = map.get("MEMO").toString();
				String status = map.get("STATUS").toString();
				String restrictShop = map.get("RESTRICTSHOP").toString();
				DCP_PeriodQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setPeriodNo(periodNo);
				//oneLv1.setPeriodName(periodName);
				oneLv1.setBeginTime(beginTime);
				oneLv1.setEndTime(endTime);
				oneLv1.setMemo(memo);
				oneLv1.setStatus(status);
				oneLv1.setRestrictShop(restrictShop);
				oneLv1.setCreateopid(map.get("CREATEOPID").toString());
				oneLv1.setCreateopname(map.get("CREATEOPNAME").toString());			
				oneLv1.setLastmodiname(map.get("LASTMODIOPNAME").toString());
				oneLv1.setLastmodiopid(map.get("LASTMODIOPID").toString());
				oneLv1.setLastmoditime(map.get("LASTMODITIME")==null?"":map.get("LASTMODITIME").toString());
				oneLv1.setCreatetime(map.get("CREATETIME")==null?"":map.get("CREATETIME").toString());
				
				
				oneLv1.setPeriodName_lang(new ArrayList<DCP_PeriodQueryRes.periodName>());
				oneLv1.setRangeList(new ArrayList<DCP_PeriodQueryRes.range>());
				oneLv1.setTimeList(new ArrayList<String>());
				
				for (Map<String, Object> oneData : langDatas) 
				{
					DCP_PeriodQueryRes.periodName oneLv2 = res.new periodName();
					
					String periodNo_lang = oneData.get("PERIODNO").toString();
					if(periodNo.equals(periodNo_lang))
					{
						String langType = oneData.get("LANG_TYPE").toString();
						String name = oneData.get("PERIODNAME").toString();
						if(curLangType.equals(langType))
						{
							oneLv1.setPeriodName(name);
						}
						oneLv2.setLangType(langType);
						oneLv2.setName(name);															
						oneLv1.getPeriodName_lang().add(oneLv2);
					}
							
				}
				
				for (Map<String, Object> oneData : rangeDatas) 
				{
					DCP_PeriodQueryRes.range oneLv2 = res.new range();
					String shopId = oneData.get("SHOPID").toString();
					if(shopId==null||shopId.isEmpty())
					{
						continue;
					}
					
					String periodNo_lang = oneData.get("PERIODNO").toString();
					if(periodNo.equals(periodNo_lang))
					{
						
						String shopName = oneData.get("SHOPNAME").toString();
						
						oneLv2.setShopId(shopId);
						oneLv2.setShopName(shopName);													
						oneLv1.getRangeList().add(oneLv2);
					}
							
				}
				
				for (Map<String, Object> oneData : timeDatas) 
				{				
					String time = oneData.get("TIME").toString();
					if(time==null||time.isEmpty())
					{
						continue;
					}
					
					String periodNo_time = oneData.get("PERIODNO").toString();
					if(periodNo.equals(periodNo_time))
					{
						oneLv1.getTimeList().add(time);
					}
					
							
				}
				
				res.getDatas().add(oneLv1);
		
		
			}
			
			
			
			
		}
		
	
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_PeriodQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	String eId = req.geteId();
	String langtype = req.getLangType();
	if(langtype==null||langtype.isEmpty())
	{
		langtype = "zh_CN";
	}
	
	String ketTxt = null;
	String appType = null;
	String channelId = null;
	String shopId = null;
	String status = null;
	if(req.getRequest()!=null)
	{
		ketTxt = req.getRequest().getKeyTxt();
		status = req.getRequest().getStatus();
	}
	String sql = null;

	StringBuffer sqlbuf=new StringBuffer(" select * from ( "
		  + " select a.*,B.LANG_TYPE,B.PERIODNAME,"
		  + " D.SHOPID,DL.ORG_NAME as SHOPNAME,e.TIME "
		  + " from DCP_PERIOD a "			
			+ " left join DCP_PERIOD_LANG b on a.eid=b.eid and a.PERIODNO=b.PERIODNO "			
			+ " left join DCP_PERIOD_RANGE d on a.eid=d.eid and a.PERIODNO=d.PERIODNO "
			+ " left join DCP_ORG_LANG DL on a.eid=DL.eid and D.shopID=DL.ORGANIZATIONNO and DL.Lang_Type='"+langtype+"' "
			+ " left join DCP_PERIOD_TIME e on a.eid=e.eid and a.PERIODNO=e.PERIODNO "
			+ " where a.eid='"+eId+"' ");
	if(status != null && status.length() >0)
	{
		sqlbuf.append(" and a.status="+status +" ");
	}

	if(ketTxt != null && ketTxt.length() >0)
	{
		sqlbuf.append(" and (a.PERIODNO like '%%"+ketTxt+"%%' or b.PERIODNAME like '%%"+ketTxt+"%%') ");
	}			
			
	sqlbuf.append(" ) order by periodNo");
	sql = sqlbuf.toString();

	return sql;
	
	}

}
