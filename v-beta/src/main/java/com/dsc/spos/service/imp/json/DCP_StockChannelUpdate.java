package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockChannelUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelUpdateReq.PluList;
import com.dsc.spos.json.cust.res.DCP_StockChannelUpdateRes;
import com.dsc.spos.json.cust.res.DCP_StockChannelUpdateRes.levelRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.CheckIFUtil;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道库存分配增加减少
 * @author 2020-06-05
 *
 */
public class DCP_StockChannelUpdate extends SPosAdvanceService<DCP_StockChannelUpdateReq, DCP_StockChannelUpdateRes> {

	@Override
	protected void processDUID(DCP_StockChannelUpdateReq req, DCP_StockChannelUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			
			String eId = req.geteId();
			
			String opNo = req.getOpNO();
			String opName = req.getOpName();
			
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String modiDate =  matter.format(dt);
			
			List<PluList> pluDatas = req.getRequest().getPluList();
			
			List<Map<String, Object>> repeatPluDatas = new ArrayList<Map<String, Object>>();

			HashSet<String> channels = new HashSet<>();
			
			if(pluDatas != null && !pluDatas.isEmpty()){
				
				//**********筛选出不存在的商品再添加进去，已存在的商品就修改***********
				
				String[] pluArr = new String[pluDatas.size()] ;
				int i = 0;
				for (PluList lvPlu : pluDatas) {
					String channelId = lvPlu.getChannelId();
					channels.add(channelId);
					String organizationNo = lvPlu.getOrganizationNo();
					String pluNo = lvPlu.getPluNo();
					String featureNo = Check.Null(lvPlu.getFeatureNo())==true?" ":lvPlu.getFeatureNo();
					String sUnit = lvPlu.getsUnit();
					String warehouse = lvPlu.getWarehouse();
					String direction = lvPlu.getDirection();
					String sQty = lvPlu.getsQty();
					String baseUnit = lvPlu.getBaseUnit();
					
					String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, sQty);
					
					String evPluInfo = eId+"_" + channelId + "_"+organizationNo+"_"+pluNo+"_"+featureNo+"_"+warehouse;
					pluArr[i] = evPluInfo;
					i++;
					
					Map<String, Object> pluMap = new HashMap<String, Object>();
					
					pluMap.put("EID", eId);
					pluMap.put("CHANNELID", channelId);
					pluMap.put("ORGANIZATIONNO", organizationNo);
					pluMap.put("PLUNO", pluNo);
					pluMap.put("FEATURENO", featureNo);
					pluMap.put("SUNIT", sUnit);
					pluMap.put("WAREHOUSE", warehouse);
					pluMap.put("DIRECTION", direction);
					pluMap.put("SQTY", sQty);
					pluMap.put("BASEUNIT",baseUnit);
					pluMap.put("BASEQTY",baseQty);
					
					repeatPluDatas.add(pluMap);
					
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
						
						String channelId = map.get("CHANNELID").toString();
						String organizationNo = map.get("ORGANIZATIONNO").toString();
						String pluNo = map.get("PLUNO").toString();
						String sUnit = map.get("SUNIT").toString();
						String featureNo = map.get("FEATURENO").toString();
                        featureNo =  Check.Null(featureNo)==true?" ":featureNo;
						String warehouse = map.get("WAREHOUSE") == null ? "" : map.get("WAREHOUSE").toString();
//						String direction = map.get("DIRECTION").toString();
//						String sQty = map.get("SQTY").toString(); 
//						String baseQty = map.get("BASEQTY").toString(); 
						
						Iterator<Map<String, Object>> it = allDatas.iterator();
//						for (Map<String, Object> evPluMap : allDatas) {
							//******************* 去除已存在的商品信息 ******************
					        while (it.hasNext()){
					        	Map<String, Object> s = it.next();
					        	
					        	if(channelId.equals(s.get("CHANNELID").toString() )    
										&& pluNo.equals(s.get("PLUNO").toString() ) 
										&& organizationNo.equals(s.get("ORGANIZATIONNO").toString() ) 
										&& featureNo.equals(s.get("FEATURENO").toString() ) 
										&& warehouse.equals(s.get("WAREHOUSE")==null ? "" : s.get("WAREHOUSE")) ){
					        		
//					        		allDatas.remove(s);
					        		it.remove();
					        		
					        		String direction = s.get("DIRECTION").toString();
					        		String sQty = s.get("SQTY").toString(); 
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
									if(direction.equals("-1")){
										ub1.addUpdateValue("ONLINEQTY", new DataValue(sQty, Types.VARCHAR,DataExpression.SubSelf));
										ub1.addUpdateValue("BONLINEQTY", new DataValue(baseQty, Types.VARCHAR,DataExpression.SubSelf));
									}
									
									// 加这一句 是防止前端 direction 传错或不传，造成执行失败的问题
									ub1.addUpdateValue("CREATEOPID", new DataValue(opNo, Types.VARCHAR));
									ub1.addUpdateValue("CREATEOPNAME", new DataValue(opName, Types.VARCHAR));
									ub1.addUpdateValue("CREATETIME", new DataValue(modiDate, Types.DATE));
									
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
									
//									ub2.addUpdateValue("QTY", new DataValue(opNo, Types.VARCHAR));
//									ub2.addUpdateValue("LOCKQTY", new DataValue(opName, Types.VARCHAR));
//									ub2.addUpdateValue("ONLINEQTY", new DataValue(modiDate, Types.DATE));
									
									if(direction.equals("1")){ // 1：增加 ，  -1：减少
										ub2.addUpdateValue("ONLINEQTY", new DataValue(baseQty, Types.VARCHAR,DataExpression.UpdateSelf));
									}
									if(direction.equals("-1")){
										ub2.addUpdateValue("ONLINEQTY", new DataValue(baseQty, Types.VARCHAR,DataExpression.SubSelf));
									}
									
									
									ub2.addUpdateValue("LASTMODITIME", new DataValue(modiDate, Types.DATE));
									ub2.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));
									ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
									
									this.addProcessData(new DataProcessBean(ub2));
									
								}
					        	
					        }
							
//						}
						
					}
					
				}
				/// repeatDatas 为空
				else{
					
				}
				
				//**********筛选结束 *************
				
				String[] channelColumn = {"EID","CHANNELID","ORGANIZATIONNO","PLUNO","SUNIT","FEATURENO","WAREHOUSE",
						"ONLINEQTY","LOCKQTY","BASEUNIT","BONLINEQTY","BLOCKQTY","CREATEOPID","CREATEOPNAME","CREATETIME"};
				
				String[] billColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","TOTPQTY","TOTCQTY","MEMO",
						"CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","DEALTYPE"};
				
				String[] billGoodsColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","ITEM","ORGANIZATIONNO","PLUNO",
						"SUNIT","FEATURENO","WAREHOUSE","DIRECTION","SQTY","BASEUNIT","BQTY"};
				


//				if(repeatDatas == null || repeatDatas.isEmpty()){
//					for (PluList lvPlu : pluDatas) {
//						String channelId = lvPlu.getChannelId();
//						String organizationNo = lvPlu.getOrganizationNo();
//						String pluNo = lvPlu.getPluNo();
//						String featureNo = lvPlu.getFeatureNo();
//						String sUnit = lvPlu.getsUnit();
//						String warehouse = lvPlu.getWarehouse();
//						String direction = lvPlu.getDirection();
//						String sQty = lvPlu.getsQty();
//						String baseUnit = lvPlu.getBaseUnit();
//						
////						String baseQty = CheckIFUtil.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, sQty);
//						
//						// *************** DCP_Stock_Channel 如果商品不存在 ，就新插入一条，已经存在的商品，不修改 *************
//						/**
//						 * String[] channelColumn = {"EID","CHANNELID","ORGANIZATIONNO","PLUNO","SUNIT","FEATURENO","WAREHOUSE",
//							"ONLINEQTY","LOCKQTY","BASEUNIT","BONLINEQTY","BLOCKQTY","CREATEOPID","CREATEOPNAME","CREATETIME"};
//						 */
//						
//						if(Check.Null(sQty)){
//							sQty = "0";
//						}
//						
//						DataValue[] insValue1 = null;
//						insValue1 = new DataValue[]{
//								new DataValue(eId, Types.VARCHAR),
//								new DataValue(channelId, Types.VARCHAR),
//								new DataValue(organizationNo, Types.VARCHAR),
//								
//								new DataValue(pluNo, Types.VARCHAR),
//								new DataValue(sUnit, Types.VARCHAR),
//								new DataValue(featureNo, Types.VARCHAR),
//								new DataValue(warehouse, Types.VARCHAR),
//								new DataValue(sQty, Types.VARCHAR), //ONLINEQTY
//								new DataValue("0", Types.VARCHAR), //LOCKQTY 
//								new DataValue(baseUnit, Types.VARCHAR), //baseUnit 
//								
//								new DataValue("0", Types.VARCHAR),//BONLINEQTY
//								new DataValue("0", Types.VARCHAR),//BLOCKQTY
//								new DataValue(opNo, Types.VARCHAR),
//								new DataValue(opName, Types.VARCHAR),
//								new DataValue(modiDate, Types.DATE)
//								
//								
//						};
//						InsBean ib1 = new InsBean("DCP_STOCK_CHANNEL", channelColumn);
//						ib1.addValues(insValue1);
//						this.addProcessData(new DataProcessBean(ib1));
//						
//					}
//				}
				for (Map<String,Object> lvPlu : allDatas) {

					String channelId = lvPlu.get("CHANNELID").toString();
					String organizationNo = lvPlu.get("ORGANIZATIONNO").toString();
					String pluNo = lvPlu.get("PLUNO").toString();
					String featureNo = lvPlu.get("FEATURENO").toString();
                    featureNo = Check.Null(featureNo)==true?" ":featureNo;
					String sUnit = lvPlu.get("SUNIT").toString();
					String warehouse = lvPlu.get("WAREHOUSE") == null ? "":lvPlu.get("WAREHOUSE").toString();
					String sQty = lvPlu.get("SQTY") == null ? "" : lvPlu.get("SQTY").toString();
					//3.0 获取单位换算信息，可能会调整为 各单位到基准单位的换算率。 
//					String baseUnit = "";
					String baseUnit = lvPlu.get("BASEUNIT").toString();
					
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
							
							new DataValue("0", Types.VARCHAR),//BONLINEQTY
							new DataValue("0", Types.VARCHAR),//BLOCKQTY
							new DataValue(opNo, Types.VARCHAR),
							new DataValue(opName, Types.VARCHAR),
							new DataValue(modiDate, Types.DATE)
							
							
					};
					InsBean ib1 = new InsBean("DCP_STOCK_CHANNEL", channelColumn);
					ib1.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib1));
					
					//**************** DCP_Stock_Channel 插入结束 ************************
				
				}

				// **************** 插入 DCP_STOCK_CHANNEL_BILL 表数据 **************
				String billType = "QDFH001"; //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
				for (String channel : channels) {
					int item = 1;
					 Integer totpQty = 0;
					String billNo = this.queryMaxBillNo(req);
					for (PluList lvPlu : pluDatas) {
						String channelId = lvPlu.getChannelId();
						if(!channel.equals(channelId)){
							continue;
						}
						String organizationNo = lvPlu.getOrganizationNo();
						String pluNo = lvPlu.getPluNo();
						String featureNo = lvPlu.getFeatureNo();
						featureNo = Check.Null(featureNo)==true?" ":featureNo;
						String sUnit = lvPlu.getsUnit();
						String warehouse = lvPlu.getWarehouse();
						String direction = lvPlu.getDirection();
						String sQty = lvPlu.getsQty();
						String baseUnit = lvPlu.getBaseUnit();
						if(Check.Null(direction)){
							direction = "1";
						}
						if(Check.Null(sQty)){
							sQty = "0";
						}
						totpQty+=Integer.parseInt(sQty);
						/**
						 * String[] billGoodsColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","ITEM","ORGANIZATIONNO","PLUNO",
						 "SUNIT","FEATURENO","WAREHOUSE","DIRECTION","SQTY","BASEUNIT","BQTY"};
						 */
						DataValue[] insValue2 = null;
						insValue2 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(channelId, Types.VARCHAR),
								new DataValue(billNo, Types.VARCHAR), // 没有
								new DataValue(billType, Types.VARCHAR), //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
								new DataValue(item, Types.VARCHAR),
								new DataValue(organizationNo, Types.VARCHAR),

								new DataValue(pluNo, Types.VARCHAR),
								new DataValue(sUnit, Types.VARCHAR),
								new DataValue(featureNo, Types.VARCHAR),
								new DataValue(warehouse, Types.VARCHAR),
								new DataValue(direction, Types.VARCHAR), //DIRECTION ,调整方向 ： 1增加，  -1 减少
								new DataValue(sQty, Types.VARCHAR),

								new DataValue(baseUnit, Types.VARCHAR),
								new DataValue("0", Types.VARCHAR)

						};
						InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", billGoodsColumn);
						ib2.addValues(insValue2);
						this.addProcessData(new DataProcessBean(ib2));

						item = item + 1 ;

					}

					DataValue[] insValue3 = null;
					insValue3 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(channel, Types.VARCHAR),
							new DataValue(billNo, Types.VARCHAR),
							new DataValue(billType, Types.VARCHAR),
							new DataValue(totpQty, Types.VARCHAR), //总数量
							new DataValue(item-1, Types.VARCHAR), // 种类数（有多少个商品 ，即item）

							new DataValue("", Types.VARCHAR),//memo
							new DataValue(opNo, Types.VARCHAR),
							new DataValue(opName, Types.VARCHAR),
							new DataValue(modiDate, Types.DATE),
							new DataValue(opNo, Types.VARCHAR),
							new DataValue(opName, Types.VARCHAR),
							new DataValue(modiDate, Types.DATE),
							new DataValue("1", Types.VARCHAR), //dealType  0:新增   1：调整   2：删除

					};
					InsBean ib3 = new InsBean("DCP_STOCK_CHANNEL_BILL", billColumn);
					ib3.addValues(insValue3);
					this.addProcessData(new DataProcessBean(ib3));

					this.doExecuteDataToDB();
					levelRes lvRes =res.new levelRes();
					lvRes.setBillNo(billNo);
					res.setDatas(lvRes);
				}

				
				
				
				// ***************** DCP_STOCK_CHANNEL_BILL 结束 *****************
				
				
				
				/**
				 * String[] billColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","TOTPQTY","TOTCQTY","MEMO",
						"CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
				 */


			}
			
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockChannelUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockChannelUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockChannelUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StockChannelUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockChannelUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockChannelUpdateReq>(){};
	}

	@Override
	protected DCP_StockChannelUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockChannelUpdateRes();
	}
	
	
	/**
	 * 获取最新单据号
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	private String queryMaxBillNo(DCP_StockChannelUpdateReq req) throws Exception{
		String billNo = "";
		String eId = req.geteId();
		String billType = "QDFH001";
		String channelId = req.getRequest().getPluList().get(0).getChannelId();
		
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
