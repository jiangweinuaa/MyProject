package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrgTreeQueryReq;
import com.dsc.spos.json.cust.res.DCP_ModularQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrgTreeQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrgTreeQueryRes.WarehouseList;
import com.dsc.spos.json.cust.res.DCP_ModularQueryRes.function;
import com.dsc.spos.json.cust.res.DCP_ModularQueryRes.level1Elm;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 组织树形结构查询
 * @author Young
 *
 */
public class DCP_OrgTreeQuery extends SPosBasicService<DCP_OrgTreeQueryReq, DCP_OrgTreeQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrgTreeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrgTreeQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrgTreeQueryReq>(){};
	}

	@Override
	protected DCP_OrgTreeQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrgTreeQueryRes();
	}

	@Override
	protected DCP_OrgTreeQueryRes processJson(DCP_OrgTreeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrgTreeQueryRes res = null;
		res = this.getResponse();
		try {
			
			String sql = this.getQuerySql(req);
			List<Map<String , Object>> orgDatas = this.doQueryData(sql, null);
			
			if(orgDatas != null && orgDatas.size() > 0 && !orgDatas.isEmpty()){
				
				// 主键字段
				Map<String, Object> condition = new HashMap<String, Object>(); // 查詢條件
				condition.put("ORGFORM", "0");
				// 调用过滤函数
				List<Map<String, Object>> formDatas = MapDistinct.getWhereMap(orgDatas, condition, true);
				
				// condition.put("ORGANIZATIONNO", true); 
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查詢條件
				condition2.put("ORGANIZATIONNO", true);
				// 调用过滤函数
				List<Map<String, Object>> childrenDatas = MapDistinct.getMap(formDatas, condition2);
				
				res.setDatas( new ArrayList<DCP_OrgTreeQueryRes.OrgDatas>());
				
				for (Map<String, Object> map : childrenDatas) {
					String organizationNo = map.get("ORGANIZATIONNO").toString();
					
					DCP_OrgTreeQueryRes.OrgDatas lv1 = res.new OrgDatas();
					String orgName = map.get("ORGNAME").toString(); 
					String orgType = map.get("ORGTYPE").toString(); 
					String orgForm = map.get("ORGFORM").toString(); //组织类型 0-公司  1-组织  2-门店 3-其它
					String upOrg = map.get("UPORG").toString(); 
					String belfirm = map.get("BELFIRM").toString(); 
					String orgStatus = map.get("STATUS").toString(); 
					
					if(Check.Null(organizationNo ) || (!orgForm.equals("0") ) ){
						continue;
					}
					
					lv1.setOrganizationNo(organizationNo);
					lv1.setOrgName(orgName);
					lv1.setOrgType(orgType);
					lv1.setOrgForm(orgForm);
					lv1.setUpOrg(upOrg);
					lv1.setBelfirm(belfirm);
					lv1.setStatus(orgStatus);
					
					lv1.setWarehouseList(new ArrayList<DCP_OrgTreeQueryRes.WarehouseList>() );
					lv1.setChildren(new ArrayList<DCP_OrgTreeQueryRes.OrgDatas>() );
					
					for (Map<String, Object> warehouseMap : orgDatas) {
						if(organizationNo.equals( warehouseMap.get("ORGANIZATIONNO") )){
							DCP_OrgTreeQueryRes.WarehouseList lv2 = res.new WarehouseList();
							
							String warehouse = warehouseMap.get("WAREHOUSE").toString();
							if(Check.Null(warehouse)){
								continue;
							}else{
								String warehouseName = warehouseMap.get("WAREHOUSENAME").toString();
								lv2.setWarehouse(warehouseName);
								lv2.setWarehouseName(warehouseName);
								lv1.getWarehouseList().add(lv2);
							}
							
						}
						
					}
					
					setChildrenDatas(lv1, orgDatas);
					res.getDatas().add(lv1);
				}
				
			}
			
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrgTreeQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		try {
			
			StringBuffer sqlbuf = new StringBuffer();
			
			String keyTxt = req.getRequest().getKeyTxt();
			String status = req.getRequest().getStatus();
			String langType = req.getLangType();
			
			sqlbuf.append(" SELECT  temp.* "
					+ " FROM ( "
					+ " SELECT 	COUNT( DISTINCT belfirm  ) OVER() AS NUM , dense_rank() OVER( ORDER BY A.belfirm ) AS rn , "
					+ " A.eID , A.Organizationno , C1.ORG_NAME AS orgName  , B.UP_ORG AS upOrg , A.Org_Form as orgForm ,a.org_Type as orgType , A.Belfirm, A.STATUS  ,   "
//					+ " C2.ORG_NAME AS upOrgName "
					+ " W1.warehouse ,W1.WAREHOUSE_NAME AS warehouseName "
					+ " FROM DCP_ORG A "
					+ " LEFT JOIN DCP_ORG_Level B ON A.eId = B.eId AND A.organizationNo = b.organizationNO "
					+ " left JOIN Dcp_Org_Lang C1 ON A.eId = C1.eId AND A.Organizationno = C1.Organizationno AND C1.Lang_Type = '"+langType+"' "
					//+ " left JOIN Dcp_Org_Lang C2 ON B.eId = C2.eId AND B.Up_org = C2.Organizationno AND C2.Lang_Type = '"+langType+"' "
					+ " LEFT JOIN DCP_WAREHOUSE W on A.eId = W.eid and A.organizationNo = W.organizationNo and W.status = '100' "
					+ " LEFT JOIN DCP_warehouse_lang W1 ON w.eId = W1.eid AND w.Organizationno = W1.Organizationno and W.warehouse = W1.warehouse  "
					+ " AND W1.Lang_Type = '"+langType+"' AND W1.status = '100' " );
	 
			sqlbuf.append( " WHERE A.eid = '"+req.geteId()+"' " );
				
			// 默认查询 已启用的组织信息
			if(!Check.Null(status)){
				sqlbuf.append( " and  A.status  = '"+status+"' " );
			}else{
				sqlbuf.append( " and  A.status = '100' " );
			}
			
			if(!Check.Null(keyTxt)){
				sqlbuf.append( " and ( A.organizationNO like '%%"+keyTxt+"%%' or C1.org_name like '%%"+keyTxt+"%%' ) " );
			}
				
			sqlbuf.append(" ORDER BY rn ) temp "
					+ " WHERE temp.rn > 0 AND rn <= 8 ");
			
			sql = sqlbuf.toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return sql;
	}
	
	protected List<Map<String, Object>> getChildDatas(List<Map<String, Object>> allDatas, String organizationNo) {
		List<Map<String, Object>> orgDataTemp = new ArrayList<>();
		for (Map<String, Object> map : allDatas) {
			if (map.get("UPORG").toString().equals(organizationNo)) {
				orgDataTemp.add(map);
			}
		}
		return orgDataTemp;
	}
	
	
	// 这里写一个递归的调用当前的方法
	protected void setChildrenDatas(DCP_OrgTreeQueryRes.OrgDatas oneLv1, List<Map<String, Object>> allDatas)
			throws Exception {

		try {
			
			List<Map<String, Object>> upOrgList = getChildDatas(allDatas, oneLv1.getOrganizationNo());

			// 主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
			condition.put("ORGANIZATIONNO", true);
			// 调用过滤函数
			List<Map<String, Object>> upOrgList2 = MapDistinct.getMap(upOrgList, condition);

			if (upOrgList2 != null && !upOrgList2.isEmpty()) {
				for (Map<String, Object> orgList : upOrgList2) {
					DCP_OrgTreeQueryRes.OrgDatas lv1 = new DCP_OrgTreeQueryRes(). new OrgDatas();
					lv1.setChildren(new ArrayList<DCP_OrgTreeQueryRes.OrgDatas>());
					lv1.setWarehouseList(new ArrayList<DCP_OrgTreeQueryRes.WarehouseList>());
					
					String organizationNo = orgList.get("ORGANIZATIONNO").toString();

					String orgName = orgList.get("ORGNAME").toString(); 
					String orgType = orgList.get("ORGTYPE").toString(); 
					String orgForm = orgList.get("ORGFORM").toString(); 
					String upOrg = orgList.get("UPORG").toString(); 
					String belfirm = orgList.get("BELFIRM").toString(); 
					String orgStatus = orgList.get("STATUS").toString(); 
					
					lv1.setOrganizationNo(organizationNo);
					lv1.setOrgName(orgName);
					lv1.setOrgType(orgType);
					lv1.setOrgForm(orgForm);
					lv1.setUpOrg(upOrg);
					lv1.setBelfirm(belfirm);
					lv1.setStatus(orgStatus);
					
					lv1.setWarehouseList(new ArrayList<DCP_OrgTreeQueryRes.WarehouseList>() );
					lv1.setChildren(new ArrayList<DCP_OrgTreeQueryRes.OrgDatas>() );
					
					for (Map<String, Object> warehouseMap : allDatas) {
						if(organizationNo.equals( warehouseMap.get("ORGANIZATIONNO") )){
							DCP_OrgTreeQueryRes.WarehouseList lv2 =new DCP_OrgTreeQueryRes().new WarehouseList();
							
							String warehouse = warehouseMap.get("WAREHOUSE").toString();
							if(Check.Null(warehouse)){
								continue;
							}else{
								String warehouseName = warehouseMap.get("WAREHOUSENAME").toString();
								lv2.setWarehouse(warehouseName);
								lv2.setWarehouseName(warehouseName);
								lv1.getWarehouseList().add(lv2);
							}
							
						}
						
					}

					setChildrenDatas(lv1, allDatas);
					oneLv1.getChildren().add(lv1);
				}

			}
		} catch (Exception e) {
			
		}

	}

	
	
}
