package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DsmDAO;
import com.dsc.spos.json.cust.req.DCP_ChannelGoodsQueryReq;
import com.dsc.spos.json.cust.req.DCP_ChannelGoodsQueryReq.OrganizationList;
import com.dsc.spos.json.cust.res.DCP_ChannelGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道商品查询
 * @author 2020-06-05
 *
 */
public class DCP_ChannelGoodsQuery extends SPosBasicService<DCP_ChannelGoodsQueryReq, DCP_ChannelGoodsQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_ChannelGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_ChannelGoodsQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ChannelGoodsQueryReq>(){};
	}

	@Override
	protected DCP_ChannelGoodsQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ChannelGoodsQueryRes();
	}

	@Override
	protected DCP_ChannelGoodsQueryRes processJson(DCP_ChannelGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ChannelGoodsQueryRes res = null;
		res = this.getResponse();
		
		try {
			
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
			DCP_ChannelGoodsQueryRes.levelRes lvRes = res.new levelRes();
			
			lvRes.setPluList( new ArrayList<DCP_ChannelGoodsQueryRes.PluList>());
			
			int totalRecords = 0;
			int totalPages = 0;
			
			if(queryDatas.size() > 0 && !queryDatas.isEmpty()){
				String num = queryDatas.get(0).get("NUM").toString();
				if(req.getPageSize() != 0 && req.getPageNumber() == 0){
					totalRecords=Integer.parseInt(num);
					totalPages = totalRecords / req.getPageSize();
					totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				}
				
				//商品信息
				Map<String, Boolean> headCondition = new HashMap<String, Boolean>(); //查詢條件
				headCondition.put("EID", true);	
//				headCondition.put("CHANNELID", true);	
				headCondition.put("PLUNO", true);	
				//调用过滤函数
				List<Map<String, Object>> headDatas = MapDistinct.getMap(queryDatas, headCondition);
				
				//商品所属门店
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("EID", true);	
//				condition.put("CHANNELID", true);	
				condition.put("ORGANIZATIONNO", true);	
				condition.put("PLUNO", true);	
				//调用过滤函数
				List<Map<String, Object>> orgDatas = MapDistinct.getMap(queryDatas, condition);
				
				//门店下属组织
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
				condition2.put("EID", true);	
//				condition2.put("CHANNELID", true);	
				condition2.put("ORGANIZATIONNO", true);	
//				condition2.put("PLUNO", true);	
				condition2.put("WAREHOUSE", true);	
				//调用过滤函数
				List<Map<String, Object>> warehouseDatas = MapDistinct.getMap(queryDatas, condition2);
				
				//商品特征码信息
				Map<String, Boolean> condition3 = new HashMap<String, Boolean>(); //查詢條件
				condition3.put("EID", true);	
//				condition3.put("CHANNELID", true);	
				condition3.put("PLUNO", true);	
				condition3.put("FEATURENO", true);	
				//调用过滤函数
				List<Map<String, Object>> featureDatas = MapDistinct.getMap(queryDatas, condition3);

				//商品单位信息
				Map<String, Boolean> unitCondition = new HashMap<String, Boolean>(); //查詢條件
				unitCondition.put("EID", true);	
//				unitCondition.put("CHANNELID", true);	
				unitCondition.put("PLUNO", true);	
				unitCondition.put("SUNIT", true);	
				//调用过滤函数
				List<Map<String, Object>> unitDatas = MapDistinct.getMap(queryDatas, unitCondition);
				
				
				for (Map<String, Object> map : headDatas) {
					DCP_ChannelGoodsQueryRes.PluList lvPlu = res.new PluList();
					
//					String channelId = map.get("CHANNELID").toString();
//					String channelName = map.getOrDefault("CHANNELNAME","").toString();
//					String orgNo = map.getOrDefault("ORGANIZATIONNO","").toString();
//					String orgName = map.getOrDefault("ORGANIZATIONNAME","").toString();
					String pluNo = map.getOrDefault("PLUNO","").toString();
					String pluName = map.getOrDefault("PLUNAME","").toString();
					String baseUnit = map.getOrDefault("BASEUNIT","").toString();
//					String featureNo = map.getOrDefault("FEATURENO","").toString();
//					String featureName = map.getOrDefault("FEATURENAME","").toString();
//					String sUnit = map.getOrDefault("SUNIT","").toString();
//					String sUnitName = map.getOrDefault("SUNITNAME","").toString();
//					String warehouse = map.getOrDefault("WAREHOUSE","").toString();
//					String warehouseName = map.getOrDefault("WAREHOUSENAME","").toString();
			
//					lvPlu.setChannelId(channelId);
//					lvPlu.setOrganizationNo(orgNo);
//					lvPlu.setOrganizationName(orgName);
					lvPlu.setPluNo(pluNo);
					lvPlu.setPluName(pluName);
					lvPlu.setBaseUnit(baseUnit);
//					lvPlu.setFeatureNo(featureNo);
//					lvPlu.setFeatureName(featureName);
//					lvPlu.setsUnit(sUnit);
//					lvPlu.setsUnitName(sUnitName);
//					lvPlu.setWarehouse(warehouse);
//					lvPlu.setWarehouseName(warehouseName);
					
					lvPlu.setFeatureList(new ArrayList<DCP_ChannelGoodsQueryRes.FeatureList>());
					lvPlu.setOrganizationList(new ArrayList<DCP_ChannelGoodsQueryRes.OrgList>() );
					lvPlu.setsUnitList(new ArrayList<DCP_ChannelGoodsQueryRes.UnitList>() );
					
					// featureDatas ..商品多特征码
					for (Map<String, Object> map2 : featureDatas) {
						DCP_ChannelGoodsQueryRes.FeatureList ft =res.new FeatureList();
						if(Check.Null( map2.get("FEATURENO").toString() ))
						{
							continue;
						};
						
						String featureNo = map2.get("FEATURENO").toString();
						String featureName = map2.get("FEATURENAME").toString();
						if( pluNo.equals(map2.get("PLUNO"))  ){
							ft.setFeatureNo(featureNo);
							ft.setFeatureName(featureName);
							lvPlu.getFeatureList().add(ft);
						}
					}
					
					// 2020-11-04  嘉华上线， 根据红艳规划， 该服务不再关联模板， 组织信息节点也要去掉
					// orgDatas ..商品所属多组织 , 以及 warehouseDatas 组织下的门店
//					for (Map<String, Object> orgMap : orgDatas) {
//						DCP_ChannelGoodsQueryRes.OrgList ot =res.new OrgList();
//						if(Check.Null( orgMap.get("ORGANIZATIONNO").toString() ))
//						{
//							continue;
//						};
//						String organizationNo = orgMap.get("ORGANIZATIONNO").toString();
//						String organizationName = orgMap.get("ORGANIZATIONNAME").toString();
//						if( pluNo.equals(orgMap.get("PLUNO"))  ){
//							ot.setOrganizationNo(organizationNo);
//							ot.setOrganizationName(organizationName);
//							ot.setWarehouseList(new ArrayList<DCP_ChannelGoodsQueryRes.WarehouseList>());
//							
//							for (Map<String, Object> whMap : warehouseDatas) {
//								DCP_ChannelGoodsQueryRes.WarehouseList wh =res.new WarehouseList();
//								if(Check.Null( whMap.get("WAREHOUSE").toString() ))
//								{
//									continue;
//								};
//								String warehouse = whMap.get("WAREHOUSE").toString();
//								String warehouseName = whMap.get("WAREHOUSENAME").toString();
//								if( organizationNo.equals(whMap.get("ORGANIZATIONNO").toString() ) ){
//									wh.setWarehouse(warehouse);
//									wh.setWarehouseName(warehouseName);
//									ot.getWarehouseList().add(wh);
//								}
//								
//							}
//							lvPlu.getOrganizationList().add(ot);
//							
//						}
//					}
					
					
					// sUnitDatas ..商品销售单位（ SA设计为List，不理解什么情况下，一个商品会设置多个销售单位 ）
					for (Map<String, Object> map2 : unitDatas) {
						DCP_ChannelGoodsQueryRes.UnitList st =res.new UnitList();
						if(Check.Null( map2.get("SUNIT").toString() ))
						{
							continue;
						};
						
						String sUnit = map2.get("SUNIT").toString();
						String sUnitName = map2.get("SUNITNAME").toString();
						if( pluNo.equals(map2.get("PLUNO")) ){
							st.setsUnit(sUnit);
							st.setsUnitName(sUnitName);
							lvPlu.getsUnitList().add(st);
							
						}
					}
					
					lvRes.getPluList().add(lvPlu);
				}
				
			}
			
			res.setDatas(lvRes);
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
			
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ChannelGoodsQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
				
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		if(pageNumber == 0 || pageSize == 0){
			pageNumber = 1;
			pageSize = 99999;
		}
		// 計算起啟位置
		int startRow = (pageNumber - 1) * pageSize;
		String langType = req.getLangType();
		
		String channelId = req.getRequest().getChannelId();
		String keyTxt = req.getRequest().getKeyTxt();
		
//		String orgStr = "''";
//		if(req.getRequest().getOrganizationList() != null ){
//			String[] OrgArr = new String[req.getRequest().getOrganizationList().size()] ;
//			int i=0;
//			for (OrganizationList lv1 : req.getRequest().getOrganizationList()) 
//			{
//				String orgNo = "";
//				if(!Check.Null(lv1.getOrganizationNo())){
//					orgNo = lv1.getOrganizationNo();
//				}
//				OrgArr[i] = orgNo;
//				i++;
//			}
//			orgStr = getString(OrgArr);
//		}
		
		sqlbuf.append(""
				+ " SELECT * from ( "
				+ "  SELECT "
				+ " count(*) OVER() AS NUM,  dense_rank() over (order BY  a.eId , a.pluNo"
//				+ "  ,  w.warehouse,f.featureNo  "
				+ " ) rn,"
				+ " a.eid , a.channelid , a.pluNo , "
				+ " b.sunit , d.uName AS sunitName , b.baseunit , c.plu_name AS pluName , e.channelname ,"
				+ " f.featureNo , g.featureName "
//				+ ", temp.organizationNO , temp.organizationName , w.warehouse  , w.warehouse_name AS warehouseName "
				+ " FROM Dcp_Stock_Channel_White a "
				+ " LEFT JOIN Dcp_Goods b ON a.eid = b.eid AND a.pluNo = b.pluNo AND b.status = '100' "
				+ " LEFT JOIN DCP_goods_lang c ON a.eid = c.eid AND a.pluNo = c.pluNo AND c.lang_type = '"+langType+"'"
				+ " LEFT JOIN Dcp_Unit_Lang d ON b.eId = d.eid AND b.sunit = d.unit AND d.lang_type = '"+langType+"'"
				+ " LEFT JOIN CRM_channel e ON a.eid = e.eid AND a.channelid = e.channelid "
				+ " LEFT JOIN Dcp_Goods_Feature f ON a.eid = f.eid AND a.pluno = f.pluno AND f.status = '100' "
				+ " LEFT JOIN Dcp_Goods_Feature_Lang g ON f.eid = g.eid AND f.pluno = g.pluNo AND f.featureno = g.featureno AND g.lang_type = '"+langType+"'"
//				+ " INNER JOIN ("
// 
//				+ " SELECT a.eId,  a.organizationNO , a.org_name AS  organizationName  , dt.pluNO   "
//				+ " 	FROM DCP_org_lang a "
//				+ " 	LEFT JOIN ("
//				+ " select b.*, A.organizationNo  from ("
//				+ " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn , c2.Id as organizationNO "
//				+ " from dcp_goodstemplate a"
////				+ " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
//				+ " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' "
//				//and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店 
//				// and c2.id='"+shopId+"'
//				+ " where a.eid='"+eId+"' and a.status='100' "
//				+ " and ( a.templatetype='SHOP' and c2.id is not null )"  );
//				
//				if(!Check.Null( orgStr ) && orgStr.length()>2 && !orgStr.toUpperCase().equals("ALL")){
//					sqlbuf.append( "and c2.id IN ( "+orgStr+" ) " );
//				}
//
//				sqlbuf.append( " ) a"
//				+ " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
//				+ " where a.rn=1 "
////				+ " )" 
//				);
//				
//				sqlbuf.append( ""
//				+ " 	) dt ON a.eId = dt.eId AND (  a.organizationNo = dt.organizationNO  )"
//				+ " 	WHERE a.eiD = '"+eId+"' AND a.status = '100' AND a.lang_type= '"+langType+"'"
//			
//				+ " ) temp ON a.eid = temp.eid AND a.pluNo = temp.pluNO  "
//				+ " LEFT JOIN Dcp_Warehouse_lang w ON temp.eId = w.eId AND temp.organizationNo = w.organizationno AND w.lang_type = '"+langType+"'"
 
				+ " WHERE a.eId = '"+eId+"' "  );
		
		if(!Check.Null(channelId) && !channelId.equals("all") && !channelId.equals("ALL") ){
			sqlbuf.append(" AND a.channelid  = '"+channelId+"' ");
		}
		
		if(!Check.Null(keyTxt)){
			sqlbuf.append(" AND ( a.pluNO LIKE  '%%"+keyTxt+"%%' or c.plu_name like  '%%"+keyTxt+"%%' or e.channelName  like  '%%"+keyTxt+"%%' "
//					+ " or temp.ORGANIZATIONNAME like  '%%"+keyTxt+"%%' or  w.warehouse_name  like  '%%"+keyTxt+"%%' "
							+ "   ) ");
		}
		
		sqlbuf.append(" order by  a.eId , a.channelId , a.pluNo, f.featureNo "
//				+ ",  w.warehouse, temp.organizationno "
				+ " )  t WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize) 
				+ " order by channelId   , pluNo  ");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	
	
	protected String getString(String[] str)
	{
		String str2 = "";
		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		return str2;
	}
	
	
	/**
	 * 查询单位换算率，要做成通用方法，暂时不放通用类里
	 * @param dao
	 * @param eId
	 * @param pluNO
	 * @param punit
	 * @return
	 * @throws Exception 
	 */
	private List<Map<String, Object>> Unit_RatioDatas(DsmDAO dao,String eId,String pluNo,String oUnit) throws Exception{
		
		List<Map<String, Object>> UnitRatio = new ArrayList<Map<String, Object>>();
		
		String sql = "SELECT ounit , unit , oqty ,qty , nvl(QTY,1)/nvl(OQTY,1) AS UNIT_RATIO  "
				+ " FROM DCP_GOODS_UNIT WHERE eID = '"+eId+"' AND pluNo = '"+pluNo+"' AND ounit = '"+oUnit+"' AND sunit_use = 'Y' ";
		
		List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);
		
		if(queryDatas != null && !queryDatas.isEmpty() && queryDatas.size() > 0 ){
			
			for (Map<String, Object> map : queryDatas) {
				
			}
			UnitRatio = queryDatas;
		}
		
		return UnitRatio;
	}
	
	
	
	
}
