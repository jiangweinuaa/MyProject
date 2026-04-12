package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECDCShippingCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderECSalePickupCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDCShippingCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 大物流中心配送新增
 * @author yuanyy 
 *
 */
public class DCP_OrderECDCShippingCreate extends SPosAdvanceService<DCP_OrderECDCShippingCreateReq, DCP_OrderECDCShippingCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECDCShippingCreateReq req, DCP_OrderECDCShippingCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			String subShipmentNoList[] = req.getSubShipmentNo();
			String shipmentNo = this.getShipmentNO(req);
			String eId = req.geteId();
			String shopId = req.getShopId();
			String lgPlatformNo = req.getLgPlatformNo();
			String lgPlatformName = req.getLgPlatformName();
			String dcNo = req.getDcNo();
			String dcName = req.getDcName();
			String status = req.getStatus();
			
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sTime = df.format(cal.getTime());
			
			sql = this.isRepeat(eId, shipmentNo);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData.isEmpty()) {
				String[] columns = {
						 "EID","SHOPID","SHIPMENTNO","LGPLATFORMNO", 
						 "LGPLATFORMNAME","SDATE","STIME", 
				};
				
				if(subShipmentNoList.length>0 && subShipmentNoList != null){
					int item = 0;
					String[] columns1 = {
							 "EID","SHOPID","SHIPMENTNO","LGPLATFORMNO", 
							 "LGPLATFORMNAME","ITEM","SUBSHIPMENTNO","SDATE",
							 "STIME","DCNO","DCNAME","STATUS"
					};
					for (String subShipmentNo : subShipmentNoList) {
						item = item +1;
						DataValue[] insValue1 = null;
						insValue1 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(shipmentNo, Types.VARCHAR),
								new DataValue(lgPlatformNo, Types.VARCHAR),
								new DataValue(lgPlatformName, Types.VARCHAR),
								new DataValue(item, Types.VARCHAR),
								new DataValue(subShipmentNo, Types.VARCHAR), 
								new DataValue(sDate, Types.VARCHAR),
								new DataValue(sTime, Types.VARCHAR),
								new DataValue(dcNo, Types.VARCHAR),
								new DataValue(dcName, Types.VARCHAR),
								new DataValue(status, Types.VARCHAR)
						};
						InsBean ib1 = new InsBean("OC_SHIPMENT_DC", columns1);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
						
						UptBean ub1 = null;	
						ub1 = new UptBean("OC_SHIPMENT");
						ub1.addUpdateValue("DCEXPRESS", new DataValue("Y", Types.VARCHAR));
						ub1.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
						
						ub1.addCondition("SHIPMENTNO", new DataValue(subShipmentNo, Types.VARCHAR));
						ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub1));
						
					}
				}

				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");	
				
			}
			else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("编码为" +shipmentNo+" 货运单信息 已存在，请勿重复添加");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECDCShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECDCShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECDCShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECDCShippingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECDCShippingCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDCShippingCreateReq>(){};
	}

	@Override
	protected DCP_OrderECDCShippingCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDCShippingCreateRes();
	}
	
	/**
	 * 验证是否重复
	 * @param eId
	 * @param shipmentNo
	 * @return
	 */
	private String isRepeat(String eId, String shipmentNo){
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		
		sqlbuf.append("select shipmentNo from OC_SHIPMENT_DC where EID = '"+eId+"' "
				+ " and shipmentNo = '"+shipmentNo+ "' ");
		sql = sqlbuf.toString();
		return sql;
	}
	
	private String getShipmentNO(DCP_OrderECDCShippingCreateReq req) throws Exception  {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如HYD201607010001)，流水号取门店该单据最大流水号+1)
		 */
		String sql = null;
		String shipmentNo = null;
		String shopId = req.getShopId();
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer("");
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

		String[] conditionValues = { eId, shopId }; // 查询要货单号
		shipmentNo = "HYD" + bDate;
		sqlbuf.append("" + "select shipmentNo  from ( " + "select max(shipmentNo) as shipmentNo "
				+ "  from OC_SHIPMENT_DC " + " where EID = ? " + " and SHOPID = ? "
				+ " and shipmentNo like '%%" + shipmentNo + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		if (getQData != null && getQData.isEmpty() == false) {
			shipmentNo = (String) getQData.get(0).get("SHIPMENTNO");
			if (shipmentNo != null && shipmentNo.length() > 0) {
				long i;
				shipmentNo = shipmentNo.substring(3, shipmentNo.length());
				i = Long.parseLong(shipmentNo) + 1;
				shipmentNo = i + "";
				shipmentNo = "HYD" + shipmentNo;    
			} 
			else {
				shipmentNo = "HYD" + bDate + "00001";
			}
		} 
		else {
			shipmentNo = "HYD" + bDate + "00001";
		}

		return shipmentNo;
	}
	
	

}
