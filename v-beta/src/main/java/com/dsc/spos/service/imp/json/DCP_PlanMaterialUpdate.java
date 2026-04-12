package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PlanMaterialUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PlanMaterialUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PlanMaterialUpdateRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 生产计划原料更新
 * @author yuanyy 2019-10-28
 *
 */
public class DCP_PlanMaterialUpdate extends SPosAdvanceService<DCP_PlanMaterialUpdateReq, DCP_PlanMaterialUpdateRes> {

	Logger logger = LogManager.getLogger(DCP_PlanMaterialUpdate.class.getName());
	
	@Override
	protected void processDUID(DCP_PlanMaterialUpdateReq req, DCP_PlanMaterialUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			
			List<DCP_PlanMaterialUpdateReq.level1Elm> materialDatas = req.getDatas();
			
			String eId = req.geteId();
			String shopId = req.getShopId();
			String fNo = req.getfNo();
			String fType = req.getfType();
			
			String revokeBeginTime = req.getBeginTime();
			String revokeEndTime = req.getEndTime();
			
			String isRevoke = "N";
			if(req.getIsRevoke() == null || !req.getIsRevoke().equals("Y")){
				isRevoke = "N";
			}else{
				isRevoke = req.getIsRevoke();
			}
			
			Date date = new Date();
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
			String outTime = formatter2.format(date);
			
			StringBuffer pluNoBuf = new StringBuffer();
			pluNoBuf.append("''");
			for (level1Elm map : materialDatas) {
				pluNoBuf.append(",'"+map.getPluNo()+"'");
			}
			
			String isOutSql = "select pluNo , isOut , max(OutTime) as outTime  from DCP_plan_material where EID = '"+req.geteId()+"' "
					+ " and SHOPID = '"+req.getShopId()+"' and planNo = '"+req.getPlanNo()+"' "
					+ " and ftype = '"+req.getfType()+"' and fNO = '"+req.getfNo()+"' "
					+ " and isOut = 'Y' "
					+ " group by pluNo , isOut ";
			
			List<Map<String, Object>> outDatas = this.doQueryData(isOutSql, null);
			
			boolean isNew = false;
			
			if(outDatas != null && outDatas.size() > 0 ){
				isNew = false;
			}
			else{
				isNew = true;
			}
			
			if(isRevoke.equals("N")){
				
				for (level1Elm map : materialDatas) {

					UptBean ub2 = new UptBean("DCP_PLAN_MATERIAL");		
					
					ub2.addCondition("EID",new DataValue(eId, Types.VARCHAR));
					ub2.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
					ub2.addCondition("PLANNO",new DataValue(req.getPlanNo(), Types.VARCHAR));
					ub2.addCondition("FTYPE",new DataValue(req.getfType(), Types.VARCHAR));
					ub2.addCondition("FNO",new DataValue(fNo, Types.VARCHAR));
					ub2.addCondition("PLUNO",new DataValue(map.getPluNo(), Types.VARCHAR));
					
					ub2.addUpdateValue("PREDICTQTY",new DataValue(map.getPredictQty(), Types.VARCHAR));
					ub2.addUpdateValue("RESIDUEQTY",new DataValue(map.getResidueQty(), Types.VARCHAR)); //实时库存
					ub2.addUpdateValue("OUTQTY",new DataValue(map.getOutQty(), Types.VARCHAR));
					ub2.addUpdateValue("CHANGEQTY",new DataValue(map.getChangeQty(), Types.VARCHAR));
					ub2.addUpdateValue("ACTQTY",new DataValue(map.getActQty(), Types.VARCHAR));
					ub2.addUpdateValue("TOTAMT",new DataValue(map.getTotAmt(), Types.VARCHAR));
					ub2.addUpdateValue("DISTRIAMT",new DataValue(map.getDistriAmt(), Types.VARCHAR));

					String isOut = map.getIsOut();
					
					BigDecimal actqty = new BigDecimal(map.getActQty().toString());
					
					if(actqty.compareTo(new BigDecimal("0")) == 0){ // 如果实际值为0，控制不导出到KDS
						ub2.addUpdateValue("ISOUT",new DataValue("N", Types.VARCHAR));
					}
					
					else{
						ub2.addUpdateValue("ISOUT",new DataValue(map.getIsOut(), Types.VARCHAR));
						
						if(isNew == true){
							ub2.addUpdateValue("OUTTIME",new DataValue(outTime, Types.VARCHAR));
						}
					}

					this.addProcessData(new DataProcessBean(ub2));	
					
					this.doExecuteDataToDB();
					
				}
				
				String sql = "select a.* , d.begin_Time as beginTime, d.end_time as endTime , d.dtName as fName "
						+ " from DCP_plan_material a "
						+ " LEFT JOIN DCP_DINNERTime d ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.fNo = d.dtNo AND a.ftype = '1' " // 1:批次/餐段
						+ " where a.EID = '"+req.geteId()+"' and a.SHOPID = '"+req.getShopId()+"' "
						+ " and a.planNo = '"+req.getPlanNo()+"'  and  a.fType = '1' and a.isOut = 'Y' ";
			
				List<Map<String, Object>> pluDatas = this.doQueryData(sql, null);
				if(pluDatas != null && pluDatas.size() > 0){

					// 单头主键字段
					Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
					condition.put("PLUNO", true);
					// 调用过滤函数
					List<Map<String, Object>> getQHeader = MapDistinct.getMap(pluDatas, condition);
					
					// 初始化
					RedisPosPub RP = new RedisPosPub();
//					String[] pluArr = new String[] {};
					List<String> pluList = new ArrayList<>();
					
					Calendar cal = Calendar.getInstance();//获得当前时间
//					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //这里时分秒不能用冒号间隔，下边有个从缓存取数，删除特定键， 之前会先转换字符串，会把冒号换为等号，再转JSON 会报错。 
					SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
					cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));  
					String preDate = format.format(cal.getTime());  
					
					for (Map<String, Object> map : getQHeader) {
						String pluNo = map.getOrDefault("PLUNO", "").toString();
						
						for (Map<String, Object> map2 : pluDatas) {
							if (pluNo.equals(map2.getOrDefault("PLUNO", "").toString())) {
								String beginTime = map2.getOrDefault("BEGINTIME", "").toString();
								String endTime = map2.getOrDefault("ENDTIME", "").toString();
								String FNO = map2.getOrDefault("FNO", "").toString();
								
								String actQty = map2.getOrDefault("ACTQTY", "0").toString();

								String json = RP.getHashMap("SaleExpect:" + req.geteId() + ":" + req.getShopId(),  pluNo);
								String soldQty = "0"; //已售数量
								
								if(json != null && json.length() > 0){
									json = json.replace("\"", "");
									json = json.replace(":", "=");

									// 使用Gson转换
									Gson gson = new Gson();
									List<Map<String,String>> list = gson.fromJson(json.trim(), new TypeToken<List<Map<String, String>>>() {}.getType());
									//完成
									
									for (Map<String, String> evTimeMap : list) {
										
										if(FNO.equals(evTimeMap.get("timeID").toString())){
											soldQty = evTimeMap.get("soldCount").toString();
										}
									}
								}
								
								Map<String, String> plan = new HashMap<String, String>();
								plan.put("timeID", FNO);
//								plan.put("fType", fType);
								plan.put("timeStart", beginTime);
								plan.put("timeEnd", endTime);
								plan.put("sellOutCount", actQty + "");
								plan.put("soldCount", soldQty);//已售数量
								plan.put("readTime", preDate);
								// 同时写入REDIS缓存
								pluList.add(JSONObject.toJSONString(plan));
								
							}

						}

						RP.setHashMap("SaleExpect:" + req.geteId() + ":" + req.getShopId(), pluNo, pluList.toString());
						pluList = new ArrayList<>();

					}
					RP.Close();
				}
				
			}
			
			else{ // 批次生产计划撤销操作。
				
					
				for (level1Elm map : materialDatas) {
					
					UptBean ub2 = new UptBean("DCP_PLAN_MATERIAL");		
					
					ub2.addCondition("EID",new DataValue(eId, Types.VARCHAR));
					ub2.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
					ub2.addCondition("PLANNO",new DataValue(req.getPlanNo(), Types.VARCHAR));
					ub2.addCondition("FTYPE",new DataValue(req.getfType(), Types.VARCHAR));
					ub2.addCondition("FNO",new DataValue(fNo, Types.VARCHAR));
					ub2.addCondition("PLUNO",new DataValue(map.getPluNo(), Types.VARCHAR));
					
					ub2.addUpdateValue("ISOUT",new DataValue( "N", Types.VARCHAR));
					
					this.addProcessData(new DataProcessBean(ub2));	
					
					this.doExecuteDataToDB();
					

					if(fType.equals("1")){ // 批次撤销要清理对应时段的缓存。班次没有缓存，所以不用清理。
						
						RedisPosPub RP = new RedisPosPub(); 
	
						String json = RP.getHashMap("SaleExpect:" + req.geteId() + ":" + req.getShopId(),  map.getPluNo());
						
						//撤销操作是只删除当前批次对应时间段的缓存， 其他时间段的缓存不应该删除
						// 先将该商品的缓存信息读取出来，  然后删掉对应时间段的信息,然后再重新插入信息。
						RP.DeleteHkey("SaleExpect:" + req.geteId() + ":" + req.getShopId(), map.getPluNo());					
	//						RP.Close();
						
						List<String> pluList = new ArrayList<>();
						
	
	//						List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
						//去除两端方括号
	//						json = json.replaceAll("\\[", "\\{");
	//						json = json.replace("\\]", "\\}");
						//第一次分割
	//						String[] splitArray = json.split("},");
	//						String json = "[{\"timeEnd\":\"170000\",\"soldCount\":\"0\",\"timeStart\":\"133000\",\"timeID\":\"004\",\"sellOutCount\":\"14\"},"
	//								+ "{\"timeEnd\":\"200000\",\"soldCount\":\"0\",\"timeStart\":\"170000\",\"timeID\":\"005\",\"sellOutCount\":\"7\"},"
	//								+ "{\"timeEnd\":\"223000\",\"soldCount\":\"0\",\"timeStart\":\"200000\",\"timeID\":\"006\",\"sellOutCount\":\"17\"}]";		
	
						if(json != null && json.length() > 0){
							json = json.replace("\"", "");
							json = json.replace(":", "=");
							
							// 使用Gson转换
		
							Gson gson = new Gson();
		
							List<Map<String,String>> list = gson.fromJson(json.trim(), new TypeToken<List<Map<String, String>>>() {}.getType());
		
							//完成
							
							for (Map<String, String> map2 : list) {
		//							System.out.println(map2.get("timeEnd").toString());
								
								if(fNo.equals(map2.get("timeID").toString())){
								
									continue;
								}
								else{
									pluList.add(JSONObject.toJSONString(map2));
								}
								
							}
							
							if(pluList != null && pluList.size() > 0){ //只插入有批次数据的商品

								RP.setHashMap("SaleExpect:" + req.geteId() + ":" + req.getShopId(), map.getPluNo(), pluList.toString());
								pluList = new ArrayList<>();
							}
							
						}
						RP.Close();
					}
						
				}
					
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PlanMaterialUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PlanMaterialUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PlanMaterialUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PlanMaterialUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PlanMaterialUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanMaterialUpdateReq>(){};
	}

	@Override
	protected DCP_PlanMaterialUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanMaterialUpdateRes();
	}
	

}
