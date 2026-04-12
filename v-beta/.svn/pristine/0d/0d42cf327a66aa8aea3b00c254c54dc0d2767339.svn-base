package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECLogisticsStatusQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECLogisticsStatusQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECLogisticsStatusQuery extends SPosBasicService<DCP_OrderECLogisticsStatusQueryReq, DCP_OrderECLogisticsStatusQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECLogisticsStatusQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECLogisticsStatusQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECLogisticsStatusQueryReq>(){};
	}

	@Override
	protected DCP_OrderECLogisticsStatusQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECLogisticsStatusQueryRes();
	}

	@Override
	protected DCP_OrderECLogisticsStatusQueryRes processJson(DCP_OrderECLogisticsStatusQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		DCP_OrderECLogisticsStatusQueryRes res = this.getResponse();
		sql = this.getQuerySql(req);
		List<Map<String , Object>> trackDatas = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_OrderECLogisticsStatusQueryRes.level1Elm>());
		if(trackDatas.isEmpty() == false){
			
			for (Map<String, Object> oneData : trackDatas) 
			{
				DCP_OrderECLogisticsStatusQueryRes.level1Elm lev1 = res.new level1Elm();
				String shipmentNo = oneData.get("SHIPMENTNO").toString();
		        String item = oneData.get("ITEM").toString();
		        String shipDate = oneData.get("SHIPDATE").toString();
		        String shipTime = oneData.get("SHIPTIME").toString();
		        String shipDescription = oneData.get("DESCRIPTION").toString(); 
		        
		        lev1.setShipmentNo(shipmentNo);
		        lev1.setItem(item);
		        lev1.setShipDate(shipDate);
		        lev1.setShipTime(shipTime);
		        lev1.setShipDescription(shipDescription);
		        
		        res.getDatas().add(lev1);
		        
		        lev1 = null;
			}
			
		}
		else{
			res.setDatas(new ArrayList<DCP_OrderECLogisticsStatusQueryRes.level1Elm>() );
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderECLogisticsStatusQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String shipmentNo = req.getShipmentNo();
	
		sqlbuf.append(" select  shipmentNo ,  item , shipDate , shipTime , DESCRIPTION  from DCP_shipment_track  "
				+ " where EID = '"+eId+"'  AND SHOPID = '"+shopId+"' "
				+ " and shipmentNo = '"+shipmentNo+"' "
				+ " order by shipmentNo, item   ");
		
		
		sql = sqlbuf.toString();
		return sql;
	}

}
