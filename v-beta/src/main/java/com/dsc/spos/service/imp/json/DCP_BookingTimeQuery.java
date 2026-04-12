package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_BookingTimeQueryReq;
import com.dsc.spos.json.cust.res.DCP_BookingTimeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_BookingTimeQuery extends SPosBasicService<DCP_BookingTimeQueryReq, DCP_BookingTimeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_BookingTimeQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_BookingTimeQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_BookingTimeQueryReq>(){} ;
	}

	@Override
	protected DCP_BookingTimeQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_BookingTimeQueryRes();
	}

	@Override
	protected DCP_BookingTimeQueryRes processJson(DCP_BookingTimeQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_BookingTimeQueryRes res = this.getResponse();
		String sql = null;
		String curLangType = req.getLangType();
		if(curLangType==null||curLangType.isEmpty())
		{
			curLangType = "zh_CN";
		}
		
		sql = this.getQuerySql(req);
		
		res.setDatas(new ArrayList<DCP_BookingTimeQueryRes.level1Elm>());
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if(getQData!=null&&getQData.isEmpty()==false)
		{
			
			Map<String, Boolean> condition_class = new HashMap<String, Boolean>(); //查詢條件				
			condition_class.put("BOOKTIMENO", true);
		  //调用过滤函数
			List<Map<String, Object>> classDatas=MapDistinct.getMap(getQData, condition_class);
					 		
			Map<String, Boolean> condition_range = new HashMap<String, Boolean>(); //查詢條件		
			condition_range.put("BOOKTIMENO", true);
			condition_range.put("SHOPID", true);
		  //调用过滤函数
			List<Map<String, Object>> rangeDatas=MapDistinct.getMap(getQData, condition_range);
			
			for (Map<String, Object> map : classDatas) 
			{
				String bookingTimeNo = map.get("BOOKTIMENO").toString();				
				String bookingTime = map.get("BOOKTIME").toString();			
				String memo = map.get("MEMO").toString();
				String status = map.get("STATUS").toString();
				String restrictShop = map.get("RESTRICTSHOP").toString();
				DCP_BookingTimeQueryRes.level1Elm oneLv1 = res.new level1Elm();	
				oneLv1.setBookingTimeNo(bookingTimeNo);
				oneLv1.setBookingTime(bookingTime);
				oneLv1.setMemo(memo);
				oneLv1.setStatus(status);
				oneLv1.setRestrictShop(restrictShop);
				oneLv1.setCreateopid(map.get("CREATEOPID").toString());
				oneLv1.setCreateopname(map.get("CREATEOPNAME").toString());			
				oneLv1.setLastmodiname(map.get("LASTMODIOPNAME").toString());
				oneLv1.setLastmodiopid(map.get("LASTMODIOPID").toString());
				oneLv1.setLastmoditime(map.get("LASTMODITIME").toString());
				oneLv1.setCreatetime(map.get("CREATETIME").toString());			
				oneLv1.setRangeList(new ArrayList<DCP_BookingTimeQueryRes.range>());			
				for (Map<String, Object> oneData : rangeDatas) 
				{
					DCP_BookingTimeQueryRes.range oneLv2 = res.new range();
					
					String bookingTimeNo_range = oneData.get("BOOKTIMENO").toString();
					if(bookingTimeNo.equals(bookingTimeNo_range))
					{
						String shopId = oneData.get("SHOPID").toString();
						String shopName = oneData.get("SHOPNAME").toString();
						
						oneLv2.setShopId(shopId);
						oneLv2.setShopName(shopName);													
						oneLv1.getRangeList().add(oneLv2);
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
	protected String getQuerySql(DCP_BookingTimeQueryReq req) throws Exception {
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
		  + " select a.*,"
		  + " D.SHOPID,DL.ORG_NAME as SHOPNAME "
		  + " from DCP_BOOKINGTIME a "							
			+ " left join DCP_BOOKINGTIME_RANGE d on a.eid=d.eid and a.BOOKTIMENO=d.BOOKTIMENO "
			+ " left join DCP_ORG_LANG DL on a.eid=DL.eid and D.shopID=DL.ORGANIZATIONNO and DL.Lang_Type='"+langtype+"' "
			+ " where a.eid='"+eId+"' ");
	if(status != null && status.length() >0)
	{
		sqlbuf.append(" and a.status="+status +" ");
	}

	if(ketTxt != null && ketTxt.length() >0)
	{
		sqlbuf.append(" and (a.BOOKTIMENO like '%%"+ketTxt+"%%' or a.BOOKTIME like '%%"+ketTxt+"%%') ");
	}			
			
	sqlbuf.append(" ) order by BOOKTIMENO ");
	sql = sqlbuf.toString();

	return sql;
	
	}

}
