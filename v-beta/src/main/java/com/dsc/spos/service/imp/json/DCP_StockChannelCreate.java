package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_StockChannelCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelCreateReq.OrganizationList;
import com.dsc.spos.json.cust.req.DCP_StockChannelCreateReq.PluList;
import com.dsc.spos.json.cust.req.DCP_StockChannelCreateReq.WarehouseList;
import com.dsc.spos.json.cust.res.DCP_StockChannelCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道分货商品新增 
 * 2020-11-12 记录： 该服务根据SA-孙红艳之设计， 
 *  选择商品和组织信息之后， 由服务端整理数据，添加数据至 DCP_STOCK_BILL 和 DCP_STOCK_BILLGOODS , 同时更新/添加 DCP_STOCKCHANNEL， 更新 DCP_STOCK 线上数量（OnLineQty）
 *  
 *  以下内容为后续规划：
 *  商品来源信息从 DCP_GoodsFeatureQuery 选择， 不能从 渠道白名单中获取， 因为目前是 先为渠道分配白名单商品， 再在渠道库存分配中为每个商品分配数量， 操作太麻烦。
 *  可设计为以下步骤：  
 *  1： 直接在渠道库存分配中选择基础商品信息（商品查询开窗DCP_GoodsFeatureQuery服务），  选择分配机构 （ 机构查询 DCP_OrgTreeQuery服务）
 *  2： 统一分配商品库存（调用DCP_StockChannelCreate）， 再反写表DCP_STOCK_CHANNEL_WHITE。
 *  
 *  这样设计的好处： 
 *  1：为机构和渠道分配商品更快，可统一分配数量，不必一个一个商品去调整。     
 *  2：为渠道分配白名单商品和数量合为一体， 一个作业即可
 *  
 * @author http://183.233.190.204:10004/project/144/interface/api/3240
 *
 */
public class DCP_StockChannelCreate extends SPosAdvanceService<DCP_StockChannelCreateReq, DCP_StockChannelCreateRes> {

	@Override
	protected void processDUID(DCP_StockChannelCreateReq req, DCP_StockChannelCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String opNo = req.getOpNO();
			String opName = req.getOpName();
			
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = df.format(cal.getTime());
			
			String channelId = req.getRequest().getChannelId();
			String sQty = req.getRequest().getsQty();
			
			List<PluList> pluList = req.getRequest().getPluList(); 
			List<OrganizationList> orgList = req.getRequest().getOrganizationList();
			
			String billType = "QDFH001"; //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
			String billNo = this.queryMaxBillNo(req);
			
			String direction = "1"; //调整方向   1：新增。   -1：减少
			
			String dealType = "0"; //处理类型（0新增，1调整，2删除）
			
			// ********** DCP_STOCK_CHANNEL_BILL *************
			
			String[] channelColumn = {"EID","CHANNELID","ORGANIZATIONNO","PLUNO","SUNIT","FEATURENO","WAREHOUSE",
					"ONLINEQTY","LOCKQTY","BASEUNIT","BONLINEQTY","BLOCKQTY","CREATEOPID","CREATEOPNAME","CREATETIME"};
			
			String[] columns1 = {"EID", "CHANNELID","BILLNO","BILLTYPE","TOTPQTY" ,"TOTCQTY","MEMO",
					"CREATEOPID","CREATEOPNAME" , "CREATETIME" , "LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME",
					"DEALTYPE" };
			
			// ********** DCP_STOCK_CHANNEL_BILLGOODS *************
			String[] columnsGoods = {"EID", "CHANNELID","BILLNO","BILLTYPE","ITEM" ,"ORGANIZATIONNO","PLUNO",
					"SUNIT","FEATURENO" , "WAREHOUSE" , "DIRECTION","SQTY","BASEUNIT",
					"BQTY" };

			int item = 1;
			List<String> existPluDatas = new ArrayList<String>();
			List<Map<String, Object>> repeatPluDatas = new ArrayList<Map<String, Object>>();
			
			for (PluList pluMap : pluList) {
				
				String pluNo = pluMap.getPluNo();
				String baseUnit = pluMap.getBaseUnit();
				String sUnit = pluMap.getsUnit();
				String featureNo = pluMap.getFeatureNo();
				
				String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, sQty);
				
				for (OrganizationList orgMap : orgList) {
					
					String organizationNo = orgMap.getOrganizationNo();
					
					List<WarehouseList> warehouseList = orgMap.getWarehouseList();
					
					for (WarehouseList warehouseMap : warehouseList) {
						
						String warehouse = warehouseMap.getWarehouse();
						String evPluInfo = eId+"_" + channelId + "_"+organizationNo+"_"+pluNo+"_"+featureNo+"_"+warehouse;
						
						existPluDatas.add(evPluInfo);

						Map<String, Object> rePluMap = new HashMap<String, Object>();
						
						rePluMap.put("EID", eId);
						rePluMap.put("CHANNELID", channelId);
						rePluMap.put("ORGANIZATIONNO", organizationNo);
						rePluMap.put("PLUNO", pluNo);
						rePluMap.put("FEATURENO", featureNo);
						rePluMap.put("SUNIT", sUnit);
						rePluMap.put("WAREHOUSE", warehouse);
						rePluMap.put("DIRECTION", "1");
						rePluMap.put("SQTY", sQty);
						rePluMap.put("BASEUNIT",baseUnit);
						rePluMap.put("BASEQTY",baseQty);
						
						repeatPluDatas.add(rePluMap);
						
					}
					
				}
				
			}
			
			String[] pluArr = new String[existPluDatas.size() ] ;
			for (int j = 0; j < existPluDatas.size(); j++) {
				String evPLuStr = existPluDatas.get(j);
				pluArr[j] = evPLuStr;
			}
			
			String evPluStr = getString(pluArr);
			String repeatSql = "SELECT eid, channelId , organizationNo, pluNo,featureNo, sUnit, warehouse, ONLINEQTY, baseUnit FROM DCP_stock_channel a "
					+ " WHERE a.eId = '"+eId+"' "
					+ " AND a.eId||'_'||a.channelid||'_'||a.organizationno||'_'||a.pluno||'_'||a.featureno||'_'||a.warehouse "
					+ " IN ( "+evPluStr+" )";
			
			List<Map<String, Object>> repeatDatas = this.doQueryData(repeatSql, null);
			List<Map<String, Object>> allDatas = repeatPluDatas;
			
			if(repeatDatas != null && repeatDatas.size() > 0){
				
				for (Map<String, Object> map : repeatDatas) {
					
					String organizationNo = map.get("ORGANIZATIONNO").toString();
					String pluNo = map.get("PLUNO").toString();
					String sUnit = map.get("SUNIT").toString();
					String featureNo = map.get("FEATURENO").toString();
					String warehouse = map.get("WAREHOUSE") == null ? "" : map.get("WAREHOUSE").toString();
					
					Iterator<Map<String, Object>> it = allDatas.iterator();
//					for (Map<String, Object> evPluMap : allDatas) {
						//******************* 去除已存在的商品信息 ******************
				        while (it.hasNext()){
				        	Map<String, Object> s = it.next();
				        	
				        	if(channelId.equals(s.get("CHANNELID").toString() )    
									&& pluNo.equals(s.get("PLUNO").toString() ) 
									&& organizationNo.equals(s.get("ORGANIZATIONNO").toString() ) 
									&& featureNo.equals(s.get("FEATURENO").toString() ) 
									&& warehouse.equals(s.get("WAREHOUSE")==null ? "" : s.get("WAREHOUSE")) ){
				        		
				        		it.remove();
				        		
								String baseQty = s.get("BASEQTY").toString(); 
				        		
				        		if(Check.Null(sQty)){
									sQty = "0";
								}
								
								// 更新单头
								UptBean ub1 = null;
								ub1 = new UptBean("DCP_STOCK_CHANNEL");
								
								if(direction.equals("1")){ // 1：增加 ，  -1：减少
									ub1.addUpdateValue("ONLINEQTY", new DataValue(sQty, Types.VARCHAR,DataExpression.UpdateSelf));
									ub1.addUpdateValue("BONLINEQTY", new DataValue(baseQty, Types.VARCHAR,DataExpression.UpdateSelf));
								}
								
								// 加这一句 是防止前端 direction 传错或不传，造成执行失败的问题
								ub1.addUpdateValue("CREATEOPID", new DataValue(opNo, Types.VARCHAR));
								ub1.addUpdateValue("CREATEOPNAME", new DataValue(opName, Types.VARCHAR));
								ub1.addUpdateValue("CREATETIME", new DataValue(createTime, Types.DATE));
								
								// condition
								ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								ub1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
								ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
								ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
								ub1.addCondition("SUNIT", new DataValue(sUnit, Types.VARCHAR));
								ub1.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
								ub1.addCondition("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
								
								this.addProcessData(new DataProcessBean(ub1));
				        		
								UptBean ub2 = null;
								ub2 = new UptBean("DCP_STOCK");
								// condition
								ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
								ub2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
								ub2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
								ub2.addCondition("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
								
								if(direction.equals("1")){ // 1：增加 ，  -1：减少
									ub2.addUpdateValue("ONLINEQTY", new DataValue(baseQty, Types.VARCHAR,DataExpression.UpdateSelf));
								}
								if(direction.equals("-1")){
									ub2.addUpdateValue("ONLINEQTY", new DataValue(baseQty, Types.VARCHAR,DataExpression.SubSelf));
								}
								
								
								ub2.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
								ub2.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));
								ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
								
								this.addProcessData(new DataProcessBean(ub2));
								
							}
				        	
				        }
						
//					}
					
				}
				
			}
			/// repeatDatas 为空
			else{
				
			}
			
			for (Map<String,Object> lvPlu : allDatas) {
				
				String organizationNo = lvPlu.get("ORGANIZATIONNO").toString();
				String pluNo = lvPlu.get("PLUNO").toString();
				String featureNo = lvPlu.get("FEATURENO").toString();
				String sUnit = lvPlu.get("SUNIT").toString();
				String warehouse = lvPlu.get("WAREHOUSE") == null ? "":lvPlu.get("WAREHOUSE").toString();
				//3.0 获取单位换算信息，可能会调整为 各单位到基准单位的换算率。 
				String baseUnit = lvPlu.get("BASEUNIT").toString();
				
				String baseQty = lvPlu.get("BASEQTY").toString();
				
				if(Check.Null(sQty)){
					sQty = "0";
				}
				
				// *************** DCP_Stock_Channel 如果商品不存在 ，就新插入一条，已经存在的商品，不修改 *************
				/**
				 * String[] channelColumn = {"EID","CHANNELID","ORGANIZATIONNO","PLUNO","SUNIT","FEATURENO","WAREHOUSE",
					"ONLINEQTY","LOCKQTY","BASEUNIT","BONLINEQTY","BLOCKQTY","CREATEOPID","CREATEOPNAME","CREATETIME"};
				 */
				
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(channelId, Types.VARCHAR),
						new DataValue(organizationNo, Types.VARCHAR),
						
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(sUnit, Types.VARCHAR),
						new DataValue(featureNo, Types.VARCHAR),
						new DataValue(warehouse, Types.VARCHAR),
						new DataValue(sQty, Types.VARCHAR), //ONLINEQTY
						new DataValue("0", Types.VARCHAR), //LOCKQTY 
						new DataValue(baseUnit, Types.VARCHAR), //baseUnit 
						
						new DataValue(baseQty , Types.VARCHAR),//BONLINEQTY
						new DataValue("0", Types.VARCHAR),//BLOCKQTY
						new DataValue(opNo, Types.VARCHAR),
						new DataValue(opName, Types.VARCHAR),
						new DataValue(createTime, Types.DATE)
						
						
				};
				InsBean ib1 = new InsBean("DCP_STOCK_CHANNEL", channelColumn);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));
				
				//**************** DCP_Stock_Channel 插入结束 ************************
			
			}

			for (PluList pluMap : pluList) {
				
				String pluNo = pluMap.getPluNo();
				String baseUnit = pluMap.getBaseUnit();
				String sUnit = pluMap.getsUnit();
				String featureNo = pluMap.getFeatureNo();
				
				String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, sQty);
				
				for (OrganizationList orgMap : orgList) {
					
					String organizationNo = orgMap.getOrganizationNo();
					
					List<WarehouseList> warehouseList = orgMap.getWarehouseList();
					
					for (WarehouseList warehouseMap : warehouseList) {
						
						String warehouse = warehouseMap.getWarehouse();
						DataValue[] insValue2 = null;
						insValue2 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(channelId, Types.VARCHAR),
								new DataValue(billNo, Types.VARCHAR),
								new DataValue(billType, Types.VARCHAR),
								new DataValue(item, Types.VARCHAR),
								new DataValue(organizationNo, Types.VARCHAR),
								new DataValue(pluNo, Types.VARCHAR),
								new DataValue(sUnit, Types.VARCHAR), //
								new DataValue(featureNo, Types.VARCHAR),
								new DataValue(warehouse, Types.VARCHAR),
								new DataValue("1", Types.VARCHAR),//DIRECTION
								new DataValue(sQty, Types.VARCHAR),
								new DataValue(baseUnit, Types.VARCHAR),
								new DataValue(baseQty, Types.VARCHAR)
							};
						
						InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", columnsGoods);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2)); // 新增单身
						
						item += 1;
						
					}
					
				}
				
			}
			
			BigDecimal totPQty = new BigDecimal(sQty).multiply(new BigDecimal(item));
			
			DataValue[] insValue1 = null;
			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR), 
					new DataValue(channelId, Types.VARCHAR),
					new DataValue(billNo, Types.VARCHAR),
					new DataValue(billType, Types.VARCHAR),
					new DataValue(totPQty, Types.VARCHAR),
					new DataValue(item, Types.VARCHAR),
					new DataValue("", Types.VARCHAR), //memo
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(opName, Types.VARCHAR),
					new DataValue(createTime, Types.DATE),
					
					new DataValue(opNo, Types.VARCHAR),
					new DataValue(opName, Types.VARCHAR),
					new DataValue(createTime, Types.DATE),
					new DataValue(dealType, Types.VARCHAR) 
				};
			
			InsBean ib1 = new InsBean("DCP_STOCK_CHANNEL_BILL", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
			
			this.doExecuteDataToDB();
			
			res.setBillNo(billNo);
			res.setServiceStatus("000");
			res.setSuccess(true);
			res.setServiceDescription("服务执行成功");
					
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockChannelCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockChannelCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockChannelCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StockChannelCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockChannelCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockChannelCreateReq> (){};
	}

	@Override
	protected DCP_StockChannelCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockChannelCreateRes();
	}

	
	/**
	 * 获取最新单据号
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	private String queryMaxBillNo(DCP_StockChannelCreateReq req) throws Exception{
		String billNo = "";
		String eId = req.geteId();
		String billType = "QDFH001";
		
//		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String bDate = sdf.format(now);
		
		String sql = " select max(billNo) as billNo from DCP_STOCK_CHANNEL_BILL where eId = '"+eId+"' and billNo like 'QDFH"+bDate+"%%%' "
//				 + " and  channelId = '"+channelId+"' "
				;
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		if (getQData != null && getQData.isEmpty() == false) {

			billNo = (String) getQData.get(0).get("BILLNO");

			if (billNo != null && billNo.length() > 0) {
				long i;
				billNo = billNo.substring(4, billNo.length());
				i = Long.parseLong(billNo) + 1;
				billNo = i + "";
				billNo = "QDFH" + billNo;

			} else {
				billNo = "QDFH" + bDate + "00001";
			}
		} else {
			billNo = "QDFH" + bDate + "00001";
		}
		
		return billNo;
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
	
}
