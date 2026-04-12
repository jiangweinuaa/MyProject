package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECDCShippingQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDCShippingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 大物流中心资料查询
 * @author yuanyy 2019-03-27
 *
 */
public class DCP_OrderECDCShippingQuery extends SPosBasicService<DCP_OrderECDCShippingQueryReq, DCP_OrderECDCShippingQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECDCShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECDCShippingQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDCShippingQueryReq>(){};
	}

	@Override
	protected DCP_OrderECDCShippingQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDCShippingQueryRes();
	}

	@Override
	protected DCP_OrderECDCShippingQueryRes processJson(DCP_OrderECDCShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECDCShippingQueryRes res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		
		String sql = null;
		sql = this.getQuerySql(req);
		List<Map<String , Object>> getLangLabelDatas = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_OrderECDCShippingQueryRes.level1Elm>());
		if(getLangLabelDatas.size() > 0){
//			String num = getLangLabelDatas.get(0).get("NUM").toString();
//			totalRecords=Integer.parseInt(num);
//
//			//算總頁數
//			totalPages = totalRecords / req.getPageSize();
//			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
//			
//			res.setPageNumber(req.getPageNumber());
//			res.setPageSize(req.getPageSize());
//			res.setTotalRecords(totalRecords);
//			res.setTotalPages(totalPages);
			
			for (Map<String, Object> oneData : getLangLabelDatas) 
			{
				DCP_OrderECDCShippingQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String shipmentNo = oneData.get("SHIPMENTNO").toString();
				String lgPlatformNo = oneData.get("LGPLATFORMNO").toString();
				String lgPlatformName = oneData.get("LGPLATFORMNAME").toString();
				String pickupWay = oneData.get("PICKUPWAY").toString();
				String getshopNo = oneData.get("GETSHOPNO").toString();
				String getshopName = oneData.get("GETSHOPNAME").toString();
				String expressNo = oneData.get("EXPRESSNO").toString();
				String shipDate = oneData.get("SHIPDATE").toString();
				String shipHourType = oneData.get("SHIPHOURTYPE").toString();
				String ecplatformNo = oneData.get("ECPLATFORMNO").toString();
				String ecplatformName = oneData.get("ECPLATFORMNAME").toString();
				String ecOrderNo = oneData.get("ECORDERNO").toString();
				
				oneLv1.setShipmentNo(shipmentNo);
				oneLv1.setLgPlatformNo(lgPlatformNo);
				oneLv1.setLgPlatformName(lgPlatformName);
				oneLv1.setPickupWay(pickupWay);
				oneLv1.setGetshopNo(getshopNo);
				oneLv1.setGetshopName(getshopName);
				oneLv1.setExpressNo(expressNo);
				oneLv1.setShipDate(shipDate);
				oneLv1.setShipHourType(shipHourType);
				oneLv1.setEcplatformNo(ecplatformNo);
				oneLv1.setEcplatformName(ecplatformName);
				oneLv1.setEcOrderNo(ecOrderNo);
				res.getDatas().add(oneLv1);
				
				oneLv1 = null;
			}
		}
		else{
			res.setDatas(new ArrayList<DCP_OrderECDCShippingQueryRes.level1Elm>());
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderECDCShippingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String lgPlatformNo = req.getLgPlatformNo();
			
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT  COUNT(*) OVER() NUM , row_number() OVER(ORDER BY shipmentNo) rn ,"
				+ " shipmentNo , lgPlatformNo , lgPlatformName ,"
				+ " pickupWay , getshop AS getshopno , getshopName ,"
				+ " expressNo , shipDate , shipHourType, "
				+ " ecPlatformNo , ecPlatformName , ec_OrderNo AS   ecOrderNo "
				+ " FROM DCP_shipment a "
				+ " where EID = '"+eId+"'  AND a.DCEXPRESS <> 'Y'"
				);
		
		if(lgPlatformNo != null && lgPlatformNo.length() > 0){
			sqlbuf.append("AND lgPlatformNo = '"+lgPlatformNo+"' ");
		}
		sqlbuf.append(" order by shipmentNo ) "
//				+ "where rn > "+startRow+" and rn <= "+(startRow+pageSize)+"   "
				+ " order by shipmentNo  ");
		sql = sqlbuf.toString();
		return sql;
	}
	
}
