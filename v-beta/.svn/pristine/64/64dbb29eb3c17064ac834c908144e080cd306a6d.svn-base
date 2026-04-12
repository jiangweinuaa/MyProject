package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CityShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_CityShopQueryRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_CityShopQuery extends SPosAdvanceService<DCP_CityShopQueryReq, DCP_CityShopQueryRes> {
	
	Logger logger = LogManager.getLogger(this.getClass().getName());
	@Override
	protected boolean isVerifyFail(DCP_CityShopQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if (req.getRequest() == null) {
			isFail = true;
			errMsg.append("request不能为空 ");
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_CityShopQueryReq> getRequestType() {
		return new TypeToken<DCP_CityShopQueryReq>() {};
	}
	
	@Override
	protected DCP_CityShopQueryRes getResponseType() {
		return new DCP_CityShopQueryRes();
	}
	
	@Override
	protected void processDUID(DCP_CityShopQueryReq req, DCP_CityShopQueryRes res) throws Exception {
		// 单头总数
		String sql = this.getPayCityShop_SQL(req);
		//		logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"下属门店查询："+sql);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		res.setDatas(new ArrayList<>());
		if (getQData != null && !getQData.isEmpty()) {
			Map<String, Boolean> map_condition = new HashMap<>();
			map_condition.put("CITY", true);
			List<Map<String, Object>> getQDataFunc = MapDistinct.getMap(getQData, map_condition);
			
			Map<String, Boolean> condition = new HashMap<>();
			condition.put("CITY", true);
			condition.put("SHOPID", true);
			List<Map<String, Object>> getQDataShopList = MapDistinct.getMap(getQData, condition);
			
			if (getQDataFunc != null && !getQDataFunc.isEmpty()) {
				for (Map<String, Object> oneData1 : getQDataFunc) {
					DCP_CityShopQueryRes.level1Elm lv1 = res.new level1Elm();
					String city = oneData1.get("CITY").toString();
					lv1.setCityCode(oneData1.get("CITY").toString());
					lv1.setCityName(oneData1.get("CITY").toString());
					
					lv1.setChildren(new ArrayList<>());
					
					//					Map<String, Object> condition = new HashMap<String, Object>();
					//					condition.put("CITY", oneData1.get("CITY").toString());
					//					List<Map<String, Object>> getQDataShopList = MapDistinct.getWhereMap(getQData, condition, true);
					
					if (getQDataShopList != null && !getQDataShopList.isEmpty()) {
						for (Map<String, Object> oneData2 : getQDataShopList) {
							
							if(city.equals(oneData2.get("CITY").toString())){
								
								DCP_CityShopQueryRes.level2Elm lv2 = res.new level2Elm();
								String shopId = oneData2.get("SHOPID").toString();
								lv2.setShopId(oneData2.get("SHOPID").toString());
								lv2.setShopName(oneData2.get("SHOPNAME").toString());
								lv2.setOrgType(oneData2.get("ORG_TYPE").toString());    ///#20201106红艳：请求和返回增加orgType
								lv2.setInCostWarehouse(oneData2.get("IN_COST_WAREHOUSE").toString());
								lv2.setOutCostWarehouse(oneData2.get("OUT_COST_WAREHOUSE").toString());
								lv2.setInvCostWarehouse(oneData2.get("INV_COST_WAREHOUSE").toString());
								lv2.setInNonCostWarehouse(oneData2.get("IN_NON_COST_WAREHOUSE").toString());
								lv2.setOutNonCostWarehouse(oneData2.get("OUT_NON_COST_WAREHOUSE").toString());
								lv2.setInvNonCostWarehouse(oneData2.get("INV_NON_COST_WAREHOUSE").toString());
								lv2.setWarehouseList(new ArrayList<>());
								//								Map<String, Object> condition3 = new HashMap<String, Object>();
								//								condition3.put("CITY", city);
								//								condition3.put("SHOPID", shopId);
								//								List<Map<String, Object>> getQDataShopList2 = MapDistinct.getWhereMap(getQData, condition, true);
								
								for(Map<String, Object> oneData3 : getQData){
									if(city.equals(oneData3.get("CITY").toString()) && shopId.equals(oneData3.get("SHOPID").toString())){
										DCP_CityShopQueryRes.WarehouseList lv3 = res.new WarehouseList();
										if(Check.Null(oneData3.get("WAREHOUSE").toString())){
											continue;
										}
										lv3.setWarehouse(oneData3.get("WAREHOUSE").toString());
										lv3.setWarehouseName(oneData3.get("WAREHOUSENAME").toString());
										lv3.setWarehouseType(oneData3.get("WAREHOUSE_TYPE").toString());
										lv2.getWarehouseList().add(lv3);
									}
								}
								lv1.getChildren().add(lv2);
							}
							
						}
					}
					
					res.getDatas().add(lv1);
					
				}
				
			}
			
		}
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_CityShopQueryReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_CityShopQueryReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_CityShopQueryReq req) throws Exception {
		return null;
	}
	
	protected String getPayCityShop_SQL(DCP_CityShopQueryReq req) throws Exception {
		String sql;
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		String range = req.getRequest().getRange();
		String orgType = req.getRequest().getOrgType();   /// #20201106红艳：请求和返回增加orgType
		String businessType = req.getRequest().getBusinessType();
		String[] orgFormarry = req.getRequest().getOrgForm();
		String orgForms=getString(orgFormarry);
		if(Check.Null(orgForms)) {
			orgForms="'2'";
		}
		String langType = req.getLangType();
		
		StringBuffer sqlbuf = new StringBuffer();
		if (Check.Null(range) || range.equals("0")) {
			if(!Check.Null(businessType) && businessType.equals("0") )//业务类型 0：支付设置处理加盟支付店号
			{
				sqlbuf.append("SELECT A.EID,NVL2(A.THIRD_SHOP,A.THIRD_SHOP,A.ORGANIZATIONNO) SHOPID,NVL2(A.THIRD_SHOP,B.ORG_NAME||'_加盟支付店号',B.ORG_NAME) SHOPNAME,A.CITY , "
						+ " D.warehouse ,E.warehouse_name as warehouseName,a.org_type"
						+ " ,A.IN_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.INV_COST_WAREHOUSE"
						+ " ,A.IN_NON_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE"
						+ " ,D.WAREHOUSE_TYPE "
						+ " FROM (SELECT EID,ORGANIZATIONNO,CITY,ORG_TYPE,STATUS,cast('' as nvarchar2(10)) THIRD_SHOP,ORG_FORM,"
						+ " IN_COST_WAREHOUSE,OUT_COST_WAREHOUSE,INV_COST_WAREHOUSE,IN_NON_COST_WAREHOUSE,OUT_NON_COST_WAREHOUSE,INV_NON_COST_WAREHOUSE "
						+ " FROM DCP_ORG "
						+ " UNION ALL"
						+ " SELECT EID,ORGANIZATIONNO,CITY,ORG_TYPE,STATUS,THIRD_SHOP,ORG_FORM,"
						+ " IN_COST_WAREHOUSE,OUT_COST_WAREHOUSE,INV_COST_WAREHOUSE,IN_NON_COST_WAREHOUSE,OUT_NON_COST_WAREHOUSE,INV_NON_COST_WAREHOUSE "
						+ " FROM DCP_ORG WHERE ENABLECREDIT='Y' AND THIRD_SHOP IS NOT NULL "
						+ " ) A  ");
			}else
			{
				sqlbuf.append("SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.CITY , "
						+ " D.warehouse , E.warehouse_name as warehouseName,a.org_type "
						+ " ,A.IN_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.INV_COST_WAREHOUSE"
						+ " ,A.IN_NON_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE"
						+ " ,D.WAREHOUSE_TYPE "
						+ " FROM DCP_ORG A ");
			}
			sqlbuf.append("LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"'"
					+ " LEFT JOIN DCP_WAREHOUSE D ON A.Eid = D.eId and a.organizationNo = D.organizationNo  and D.status = '100' AND D.WAREHOUSE_TYPE<>'3' "
					+ " LEFT join DCP_WAREHOUSE_LANG E ON D.Eid = E.eId and D.organizationNo = E.organizationNo and D.warehouse = E.warehouse "
					+ " and E.status = '100' and E.lang_Type = '"+langType+"' "
					//+ " WHERE A.EID='" + req.geteId() + "' and a.org_form='2' ");
					+" WHERE A.EID='" + req.geteId() + "' and a.org_form in ("+orgForms+") ");
			if (status != null && status.length() > 0) {
				sqlbuf.append(" AND A.status='"+status+"' ");
			}
			if (!Check.Null(orgType)){
				sqlbuf.append(" and a.org_type='"+orgType+"' ");
			}
			
			if (keyTxt != null && keyTxt.length() > 0) {
				sqlbuf.append(
						" AND (A.ORGANIZATIONNO like '%%" + keyTxt + "%%' or B.ORG_NAME like '%%" + keyTxt + "%%'  ) ");
			}
			
			sqlbuf.append("ORDER BY A.CITY ,A.ORGANIZATIONNO ");
			
		} else if (range.equals("1")) {
			
			
			// 加下面这个判断，是因为服务重启、同一账号重新登陆、切换总部视角组织 的时候， token 里存的BELFIRM 会清空。
			String belfirm = "";
			//			logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"下属门店查询，shopId："+req.getShopId() + "   belfirm："+req.getBELFIRM());
			String orgForm = req.getOrg_Form();
			if(orgForm.equals("0")){
				belfirm = req.getShopId();
			}
			
			else if (Check.Null(req.getBELFIRM())) {
				
				String belSql = "  select org_form,belfirm from dcp_org where eid='" + req.geteId() + "' and organizationno='"
						+ req.getShopId() + "' ";
				List<Map<String, Object>> belFirmDatas = this.doQueryData(belSql, null);
				if(belFirmDatas != null && belFirmDatas.size() > 0 ){
					belfirm = belFirmDatas.get(0).get("BELFIRM").toString();
				}
			}else{
				belfirm = req.getBELFIRM();
			}
			
			sqlbuf.append("SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.CITY ,"
					+ " D.warehouse, E.warehouse_name as warehouseName,a.org_type"
					+ " ,A.IN_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.INV_COST_WAREHOUSE"
					+ " ,A.IN_NON_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE"
					+ " ,D.WAREHOUSE_TYPE "
					+ " FROM DCP_ORG A  "
					+ "LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"'"
					+ " LEFT JOIN DCP_WAREHOUSE D ON A.Eid = D.eId and a.organizationNo = D.organizationNo  and D.status = '100' AND D.WAREHOUSE_TYPE<>'3' "
					+ " LEFT join DCP_WAREHOUSE_LANG E ON D.Eid = E.eId and D.organizationNo = E.organizationNo and D.warehouse = E.warehouse "
					+ " and E.status = '100' and E.lang_Type = '"+langType+"' "
					+ "WHERE A.EID='" + req.geteId() + "' AND A.BELFIRM ='"
					//+ belfirm + "'  and a.org_form='2' ");
					+ belfirm + "'  and a.org_form in ("+orgForms+") ");
			if (status != null && status.length() > 0) {
				sqlbuf.append(" AND A.status= '"+status+"' ");
			}
			if (!Check.Null(orgType)){
				sqlbuf.append(" and a.org_type='"+orgType+"' ");
			}
			
			if (keyTxt != null && keyTxt.length() > 0) {
				sqlbuf.append(
						" AND (A.ORGANIZATIONNO like '%%" + keyTxt + "%%' or B.ORG_NAME like '%%" + keyTxt + "%%'  ) ");
			}
			
			sqlbuf.append("ORDER BY A.CITY ,A.ORGANIZATIONNO ");
			
		} else if (range.equals("2")) {
			sqlbuf.append("select * from (");
			sqlbuf.append("SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.CITY , "
					+ " D.warehouse, E.warehouse_name as warehouseName,a.org_type"
					+ " ,A.IN_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.INV_COST_WAREHOUSE"
					+ " ,A.IN_NON_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE"
					+ " ,D.WAREHOUSE_TYPE "
					+ " FROM DCP_ORG A  "
					+ " LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"'"
					+ " INNER JOIN PLATFORM_STAFFS_SHOP C on a.EID = c.EID and a.ORGANIZATIONNO = c.SHOPID "
					+ " LEFT JOIN DCP_WAREHOUSE D ON A.Eid = D.eId and a.organizationNo = D.organizationNo  and D.status = '100' AND D.WAREHOUSE_TYPE<>'3' "
					+ " LEFT join DCP_WAREHOUSE_LANG E ON D.Eid = E.eId and D.organizationNo = E.organizationNo and D.warehouse = E.warehouse "
					+ " and E.status = '100' and E.lang_Type = '"+langType+"' "
					//+ " WHERE a.org_form='2'  and A.EID='" + req.geteId() + "' " + " and c.opNO = '" + req.getOpNO() + "'  ");
					+ " WHERE a.org_form in ("+orgForms+") and A.EID='" + req.geteId() + "' " + " and c.opNO = '" + req.getOpNO() + "'  ");
			if (status != null && status.length() > 0) {
				sqlbuf.append(" AND A.status= '" + status + "' and C.status='" + status + "' ");
			}
			if (!Check.Null(orgType)){
				sqlbuf.append(" and a.org_type='"+orgType+"' ");
			}
			if (keyTxt != null && keyTxt.length() > 0) {
				sqlbuf.append(
						" AND (A.ORGANIZATIONNO like '%%" + keyTxt + "%%' or B.ORG_NAME like '%%" + keyTxt + "%%'  ) ");
			}
			//如果设置的是公司，那么需要查询下该公司对应的所有门店
			sqlbuf.append(" union all ");
			sqlbuf.append("SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.CITY , "
					+ " D.warehouse, E.warehouse_name as warehouseName,a.org_type"
					+ " ,A.IN_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.INV_COST_WAREHOUSE"
					+ " ,A.IN_NON_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE"
					+ " ,D.WAREHOUSE_TYPE "
					+ " FROM DCP_ORG A  "
					+ " LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"'"
					+ " INNER JOIN PLATFORM_STAFFS_SHOP C on a.EID = c.EID and a.belfirm = c.SHOPID "
					+ " LEFT JOIN DCP_WAREHOUSE D ON A.Eid = D.eId and a.organizationNo = D.organizationNo  and D.status = '100' AND D.WAREHOUSE_TYPE<>'3' "
					+ " LEFT join DCP_WAREHOUSE_LANG E ON D.Eid = E.eId and D.organizationNo = E.organizationNo and D.warehouse = E.warehouse "
					+ " and E.status = '100' and E.lang_Type = '"+langType+"' "
					//+ " WHERE a.org_form='2'  and A.EID='" + req.geteId() + "' " + " and c.opNO = '" + req.getOpNO() + "'  ");
					+ " WHERE a.org_form in ("+orgForms+")  and A.EID='" + req.geteId() + "' " + " and c.opNO = '" + req.getOpNO() + "'  ");
			
			if (status != null && status.length() > 0) {
				sqlbuf.append(" AND A.status= '" + status + "' and C.status='" + status + "' ");
			}
			if (!Check.Null(orgType)){
				sqlbuf.append(" and a.org_type='"+orgType+"' ");
			}
			if (keyTxt != null && keyTxt.length() > 0) {
				sqlbuf.append(" AND (A.ORGANIZATIONNO like '%%" + keyTxt + "%%' or B.ORG_NAME like '%%" + keyTxt + "%%'  ) ");
			}
			sqlbuf.append(")");
			sqlbuf.append("ORDER BY CITY,SHOPID ");
		} else if (range.equals("3")) {
			sqlbuf.append("SELECT A.EID,A.ORGANIZATIONNO SHOPID,B.ORG_NAME SHOPNAME,A.CITY , "
					+ " D.warehouse, E.warehouse_name as warehouseName,a.org_type"
					+ " ,A.IN_COST_WAREHOUSE,A.OUT_COST_WAREHOUSE,A.INV_COST_WAREHOUSE"
					+ " ,A.IN_NON_COST_WAREHOUSE,A.OUT_NON_COST_WAREHOUSE,A.INV_NON_COST_WAREHOUSE"
					+ " ,D.WAREHOUSE_TYPE "
					+ " FROM DCP_ORG A  "
					+ " LEFT JOIN DCP_ORG_LANG B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO AND B.LANG_TYPE='"+langType+"' "
					+ " INNER JOIN PLATFORM_STAFFS_SHOP C on a.EID = c.EID and a.ORGANIZATIONNO = c.SHOPID "
					+ " LEFT JOIN DCP_WAREHOUSE D ON A.Eid = D.eId and a.organizationNo = D.organizationNo  and D.status = '100' AND D.WAREHOUSE_TYPE<>'3' "
					+ " LEFT join DCP_WAREHOUSE_LANG E ON D.Eid = E.eId and D.organizationNo = E.organizationNo and D.warehouse = E.warehouse "
					+ " and E.status = '100' and E.lang_Type = '"+langType+"' "
					//+ " WHERE a.org_form='2'  and A.EID='" + req.geteId() + "' " + " and c.opNO = '" + req.getOpNO() + "'  ");
					+ " WHERE a.org_form in ("+orgForms+") and A.EID='" + req.geteId() + "' " + " and c.opNO = '" + req.getOpNO() + "'  ");
			if (status != null && status.length() > 0) {
				sqlbuf.append(" AND A.status= '" + status + "' and C.status='" + status + "' ");
			}
			if (!Check.Null(orgType)){
				sqlbuf.append(" and a.org_type='"+orgType+"' ");
			}
			if (keyTxt != null && keyTxt.length() > 0) {
				sqlbuf.append(" AND (A.ORGANIZATIONNO like '%%" + keyTxt + "%%' or B.ORG_NAME like '%%" + keyTxt + "%%'  ) ");
			}
			sqlbuf.append("ORDER BY CITY,SHOPID ");
		}
		sql = sqlbuf.toString();
		return sql;
	}
	private String getString(String[] str) {
		StringBuffer str2 = new StringBuffer();
		if (str!=null && str.length>0) {
			for (String s:str) {
				str2.append("'").append(s).append("'").append(",");
			}
			if (str2.length()>0) {
				str2 = new StringBuffer(str2.substring(0, str2.length() - 1));
			}
		}
		return str2.toString();
	}
}
