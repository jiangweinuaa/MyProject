package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PlanQtyUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PlanQtyUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_PlanQtyUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 沽清数量调整
 * @author yuanyy 
 * 	
 */
public class DCP_PlanQtyUpdate extends SPosAdvanceService<DCP_PlanQtyUpdateReq, DCP_PlanQtyUpdateRes> {

	@Override
	protected void processDUID(DCP_PlanQtyUpdateReq req, DCP_PlanQtyUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.getRequest().getoEId();
		String shopId =req.getRequest().getoShopId();
		String guQingNo = req.getRequest().getGuQingNo();
		String pfNo = req.getRequest().getPfNo() == null?"":req.getRequest().getPfNo();
		String pfOrderType = req.getRequest().getPfOrderType() == null?"":req.getRequest().getPfOrderType() ;
		
		String modifyBy = req.getRequest().getModifyBy();
		String modifyByName = req.getRequest().getModifyByName();
		String pluNo = req.getRequest().getPluNo();
		String pUnit = req.getRequest().getpUnit() == null?"":req.getRequest().getpUnit();
		String guQingType = req.getRequest().getGuQingType() == null?"0":req.getRequest().getGuQingType();
		String price = req.getRequest().getPrice() == null?"0":req.getRequest().getPrice();
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String modifyDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String modifyTime = df.format(cal.getTime());
		
		List<level2Elm> mealDatas = req.getRequest().getMealData();
		
		//******************* DCP_GUQINGORDER_DINNERTIME *******************
		String[] columnsDinner = {"EID", "SHOPID","GUQINGNO","PLUNO","DTNO","DTNAME",
				"BEGIN_TIME","END_TIME","KQTY","QTY","SALEQTY","RESTQTY","ISCLEAR",
				"MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME","PUNIT"};
		
		//根据沽清单号查询 对应餐段沽清信息，对比数量是否变化，变化的数据要更新
		String sql = " SELECT a.guqingNo , b.SHOPID , b.pluNO , b.dtNO , b.qty , b.saleQty, b.restQty  "
				+ " FROM DCP_GUQINGORDER a "
				+ " LEFT JOIN DCP_GUQINGORDER_DINNERTIME  b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.guqingNo = b.guqingNo "
				+ " where a.EID = '"+eId+"' and a.SHOPID = '"+shopId+"' "
				+ " and  a.Rdate = '"+modifyDate+"' ";
				
		if(!Check.Null(guQingNo)){
			sql = sql + " and a.guqingNo = '"+guQingNo+"'";
		}
		
		List<Map<String, Object>> dtDatas = this.doQueryData(sql, null);
		if(dtDatas != null && dtDatas.size() > 0 && mealDatas != null && mealDatas.size() > 0){
			
			String itemStr  = this.queryMaxItem(req);
			int item = Integer.parseInt(itemStr);
			
			guQingNo = dtDatas.get(0).get("GUQINGNO").toString();
			
			boolean isExist = false;
			for (Map<String, Object> map : dtDatas) {
				if(pluNo.equals(map.get("PLUNO").toString())){
					isExist = true;
				}
			}
			
			if(isExist){ // 如果商品今天已经做过沽清了， 再判断该商品的餐段信息是不是已存在，不存在就添加
	
				// 单头主键字段
				Map<String, Object> condition = new HashMap<String, Object>(); // 查询条件
				condition.put("PLUNO", pluNo);
				// 调用过滤函数
				List<Map<String, Object>> pluDatas = MapDistinct.getWhereMap(dtDatas, condition, true);

				List<Map<String,Object>> mealDatasCopy = new ArrayList<Map<String,Object>>();
				
				for (level2Elm lv2 : mealDatas) {
					Map<String , Object> ecMap = new HashMap<>();
					ecMap.put("DTNO", lv2.getDtNo());
					ecMap.put("DTNAME", lv2.getDtName());
					ecMap.put("BEGINTIME", lv2.getBeginTime());
					ecMap.put("ENDTIME", lv2.getEndTime());
					ecMap.put("RESTQTY", lv2.getRestQty());
					ecMap.put("QTY", lv2.getQty());
					ecMap.put("SALEQTY", lv2.getSaleQty());
					ecMap.put("UPDATETYPE", lv2.getUpdateType());
					mealDatasCopy.add(ecMap);
				}
				
				for (Map<String, Object> pluMap : pluDatas) {
					String pluDtNo = pluMap.get("DTNO").toString();
					for (int i = 0; i < mealDatasCopy.size(); i++) { //这里必须用 i 循环，不能用 foreach 对象循环。 remove会报序列化异常
						if(mealDatasCopy.get(i).get("DTNO").toString().equals(pluDtNo)){
							mealDatasCopy.remove(mealDatasCopy.get(i));
						}
					}
				}
				
				//******************* DCP_GUQINGORDER_DINNERTIME *******************
//					int item = 1;
				for (Map<String , Object> map : mealDatasCopy) {
					
					String dtNo = map.get("DTNO").toString();
					String dtName = map.get("DTNAME").toString();
					String beginTime = map.get("BEGINTIME").toString();
					String endTime = map.get("ENDTIME").toString();
					String qty = map.get("QTY").toString();
					String restQty = map.get("RESTQTY").toString();
					
					if(Check.Null(pluNo) || Check.Null(dtNo)){
						continue;
					}
					
					DataValue[] insValueDT = null;
					insValueDT = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(guQingNo, Types.VARCHAR), 
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(dtNo, Types.VARCHAR), 
						new DataValue(dtName, Types.VARCHAR), 
						new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
						new DataValue(endTime.replace(":", ""), Types.VARCHAR),
						
						new DataValue("0", Types.VARCHAR),//KQTY
						new DataValue(qty, Types.VARCHAR),//QTY
						new DataValue("0", Types.VARCHAR), // saleQty 已售数量
						new DataValue(restQty, Types.VARCHAR), // 剩余数量， 新增单子时默认给 qty 的值
						new DataValue("N", Types.VARCHAR), // 是否沽清 ，默认 N 
						
						new DataValue(modifyBy, Types.VARCHAR),
						new DataValue(modifyByName, Types.VARCHAR),
						new DataValue(modifyDate, Types.VARCHAR),
						new DataValue(modifyTime, Types.VARCHAR),
						new DataValue(pUnit, Types.VARCHAR)
					};
					
					InsBean ibDT = new InsBean("DCP_GUQINGORDER_DINNERTIME", columnsDinner);
					ibDT.addValues(insValueDT);
					this.addProcessData(new DataProcessBean(ibDT)); // 新增單頭
					
					//************* 沽清调整记录 DCP_GUQINGORDER_PLQTYUPRECORD ***********
					String[] columns_hm ={"EID","SHOPID","GUQINGNO","PLUNO","ITEM","UPDATETYPE","DTNO","DTNAME",
							"BEGIN_TIME","END_TIME","PFNO","PFORDERTYPE","QTY","MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME",
							"SALEQTY","RESTQTY","PUNIT"
					};
					
					DataValue[] insValue_hm = new DataValue[] 
							{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR), 
									new DataValue(guQingNo, Types.VARCHAR),
									new DataValue(pluNo, Types.VARCHAR),
									new DataValue(item+"" ,Types.VARCHAR),
									new DataValue("pos", Types.VARCHAR),//枚举: initial：初始化,guQing：沽清,backEnd：后台,pos：POS端
									new DataValue(dtNo, Types.VARCHAR),
									new DataValue(dtName , Types.VARCHAR),
									new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
									new DataValue(endTime.replace(":", ""), Types.VARCHAR),
									new DataValue(pfNo, Types.VARCHAR),
									new DataValue(pfOrderType, Types.VARCHAR),
									new DataValue(qty, Types.VARCHAR),
									new DataValue(modifyBy, Types.VARCHAR),
									new DataValue(modifyByName, Types.VARCHAR),
									new DataValue(modifyDate, Types.VARCHAR),
									new DataValue(modifyTime, Types.VARCHAR),
									new DataValue("0", Types.VARCHAR),
									new DataValue(restQty, Types.VARCHAR),
									new DataValue(pUnit, Types.VARCHAR)
							};
					
					InsBean ib_hm = new InsBean("DCP_GUQINGORDER_PLQTYUPRECORD", columns_hm);
					ib_hm.addValues(insValue_hm);
					this.addProcessData(new DataProcessBean(ib_hm)); 
					item = item + 1;
					
				}
				
				
				for (Map<String, Object> map : pluDatas) {
//					if(pluNo.equals(map.get("PLUNO").toString())){
						String evDtNo = map.get("DTNO").toString();
						String evQty = map.get("QTY").toString()==null? "0":map.get("QTY").toString(); 
						String evSaleQty = map.get("SALEQTY").toString()==null? "0":map.get("SALEQTY").toString();
						mealDatas = req.getRequest().getMealData();
						for(level2Elm lv2 : mealDatas){
							
							// 同一餐段，判断数量是否改变，若变即更新，没变就不动
							if(evDtNo.equals(lv2.getDtNo()) && !evQty.equals(lv2.getQty())){
								
								String qty = lv2.getQty();
								String updateType = lv2.getUpdateType();
								String dtName = lv2.getDtName();
//								String beginTime = lv2.getBegin_time() == null ? "":lv2.getBegin_time();
//								String endTime = lv2.getEnd_time() == null ? "":lv2.getEnd_time();
								
								String beginTime = lv2.getBeginTime() == null ? "":lv2.getBeginTime() ;
								String endTime = lv2.getEndTime() == null ? "" : lv2.getEndTime() ;
								
								double restQtyInt = Double.parseDouble(qty) - Double.parseDouble(evSaleQty);
//								int restQtyInt = Integer.parseInt(qty) - Integer.parseInt(evSaleQty); // 实际数量 - 已售数量 = 剩余可售数量
								
								UptBean ub1 = null;
								ub1 = new UptBean("DCP_GUQINGORDER_DINNERTIME");
								ub1.addUpdateValue("QTY", new DataValue(qty, Types.VARCHAR)); //调整后的实际数量
								ub1.addUpdateValue("RESTQTY", new DataValue(restQtyInt+"", Types.VARCHAR)); //调整后的可售数量
								// condition
								ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
								ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
								ub1.addCondition("GUQINGNO", new DataValue(guQingNo, Types.VARCHAR));
								ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
								ub1.addCondition("DTNO", new DataValue(evDtNo, Types.VARCHAR));
								this.addProcessData(new DataProcessBean(ub1));
								
								//************* 调整记录 DCP_GUQINGORDER_PLQTYUPRECORD ***********
								String[] columns_hm ={"EID","SHOPID","GUQINGNO","PLUNO","ITEM","UPDATETYPE","DTNO","DTNAME",
										"BEGIN_TIME","END_TIME","PFNO","PFORDERTYPE","QTY","MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME",
										"SALEQTY","RESTQTY","PUNIT"
								};
								
								DataValue[] insValue_hm = new DataValue[] 
										{
												new DataValue(eId, Types.VARCHAR),
												new DataValue(shopId, Types.VARCHAR), 
												new DataValue(guQingNo, Types.VARCHAR),
												new DataValue(pluNo, Types.VARCHAR),
												new DataValue(item+"" ,Types.VARCHAR),
												new DataValue(updateType, Types.VARCHAR),
												new DataValue(evDtNo, Types.VARCHAR),
												new DataValue(dtName , Types.VARCHAR),
												new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
												new DataValue(endTime.replace(":", ""), Types.VARCHAR),
												new DataValue(pfNo, Types.VARCHAR),
												new DataValue(pfOrderType, Types.VARCHAR),
												new DataValue(qty, Types.VARCHAR),
												new DataValue(modifyBy, Types.VARCHAR),
												new DataValue(modifyByName, Types.VARCHAR),
												new DataValue(modifyDate, Types.VARCHAR),
												new DataValue(modifyTime, Types.VARCHAR),
												new DataValue("0", Types.VARCHAR),
												new DataValue(restQtyInt+"", Types.VARCHAR),
												new DataValue(pUnit, Types.VARCHAR)
										};
								
								InsBean ib_hm = new InsBean("DCP_GUQINGORDER_PLQTYUPRECORD", columns_hm);
								ib_hm.addValues(insValue_hm);
								this.addProcessData(new DataProcessBean(ib_hm)); 
								item = item + 1;
							}
							
						}
						
//					}
					
					
				}
			}
			else{
				// 今天的沽清单中不存在该商品，就添加进去
				
				//******************* DCP_GUQINGORDER_DETAIL *******************
				String[] columnsDetail = {"EID", "SHOPID","GUQINGNO","ORGANIZATIONNO","PLUNO","PUNIT",
						"PRICE","GUQINGTYPE","PFNO","PFORDERTYPE",
						"MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME"};
				
				DataValue[] insValue = null;
				
				insValue = new DataValue[]{
					new DataValue(eId, Types.VARCHAR), 
					new DataValue(shopId, Types.VARCHAR), 
					new DataValue(guQingNo, Types.VARCHAR), 
					new DataValue(shopId, Types.VARCHAR), 
					new DataValue(pluNo, Types.VARCHAR),
					new DataValue(pUnit, Types.VARCHAR), 
					new DataValue(price, Types.VARCHAR), 
					new DataValue(guQingType, Types.VARCHAR),
					new DataValue(pfNo, Types.VARCHAR), //PFNO 
					new DataValue(pfOrderType, Types.VARCHAR), //pfOrderType
					new DataValue(modifyBy, Types.VARCHAR),
					new DataValue(modifyByName, Types.VARCHAR),
					new DataValue(modifyDate, Types.VARCHAR),
					new DataValue(modifyTime, Types.VARCHAR)
				};
				
				InsBean ib1 = new InsBean("DCP_GUQINGORDER_DETAIL", columnsDetail);
				ib1.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib1));  
					
				//******************* DCP_GUQINGORDER_DINNERTIME *******************
//				int item = 1;
				for (level2Elm lv2 : mealDatas) {
					
					String dtNo = lv2.getDtNo();
					String dtName = lv2.getDtName();
					String beginTime = lv2.getBeginTime();
					String endTime = lv2.getEndTime();
					String qty = lv2.getQty();
					String restQty = lv2.getRestQty();
					if(Check.Null(pluNo) || Check.Null(dtNo)){
						continue;
					}
					
					DataValue[] insValueDT = null;
					insValueDT = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(guQingNo, Types.VARCHAR), 
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(dtNo, Types.VARCHAR), 
						new DataValue(dtName, Types.VARCHAR), 
						new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
						new DataValue(endTime.replace(":", ""), Types.VARCHAR),
						
						new DataValue("0", Types.VARCHAR),//KQTY
						new DataValue(qty, Types.VARCHAR),//QTY
						new DataValue("0", Types.VARCHAR), // saleQty 已售数量
						new DataValue(restQty, Types.VARCHAR), // 剩余数量， 新增单子时默认给 qty 的值
						new DataValue("N", Types.VARCHAR), // 是否沽清 ，默认 N 
						
						new DataValue(modifyBy, Types.VARCHAR),
						new DataValue(modifyByName, Types.VARCHAR),
						new DataValue(modifyDate, Types.VARCHAR),
						new DataValue(modifyTime, Types.VARCHAR),
						new DataValue(pUnit, Types.VARCHAR)
					};
					
					InsBean ibDT = new InsBean("DCP_GUQINGORDER_DINNERTIME", columnsDinner);
					ibDT.addValues(insValueDT);
					this.addProcessData(new DataProcessBean(ibDT)); // 新增單頭
					
					//************* 沽清调整记录 DCP_GUQINGORDER_PLQTYUPRECORD ***********
					String[] columns_hm ={"EID","SHOPID","GUQINGNO","PLUNO","ITEM","UPDATETYPE","DTNO","DTNAME",
							"BEGIN_TIME","END_TIME","PFNO","PFORDERTYPE","QTY","MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME",
							"SALEQTY","RESTQTY","PUNIT"
					};
					
					DataValue[] insValue_hm = new DataValue[] 
							{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR), 
									new DataValue(guQingNo, Types.VARCHAR),
									new DataValue(pluNo, Types.VARCHAR),
									new DataValue(item+"" ,Types.VARCHAR),
									new DataValue("pos", Types.VARCHAR),//枚举: initial：初始化,guQing：沽清,backEnd：后台,pos：POS端
									new DataValue(dtNo, Types.VARCHAR),
									new DataValue(dtName , Types.VARCHAR),
									new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
									new DataValue(endTime.replace(":", ""), Types.VARCHAR),
									new DataValue(pfNo, Types.VARCHAR),
									new DataValue(pfOrderType, Types.VARCHAR),
									new DataValue(qty, Types.VARCHAR),
									new DataValue(modifyBy, Types.VARCHAR),
									new DataValue(modifyByName, Types.VARCHAR),
									new DataValue(modifyDate, Types.VARCHAR),
									new DataValue(modifyTime, Types.VARCHAR),
									new DataValue("0", Types.VARCHAR),
									new DataValue(restQty, Types.VARCHAR),
									new DataValue(pUnit, Types.VARCHAR)
							};
					
					InsBean ib_hm = new InsBean("DCP_GUQINGORDER_PLQTYUPRECORD", columns_hm);
					ib_hm.addValues(insValue_hm);
					this.addProcessData(new DataProcessBean(ib_hm)); 
					item = item + 1;
					
				}
				
				
			}
			
			
			
		}
		
		else{ // 如果查不到当前日期的沽清单，就新建沽清单
			//******************* DCP_GUQINGORDER *******************
			String[] columns = {"EID", "SHOPID","GUQINGNO","ORGANIZATIONNO","BDATE","RDATE",
						"CREATEBY","CREATE_DATE","CREATE_TIME"};
			
			guQingNo = this.getGuQingNO(req);
			
			//从POS端 新建的沽清单需求日期和单据日期都是当天
			String bDate = modifyDate; 
			String rDate = modifyDate;
			DataValue[] insValue = null;
			insValue = new DataValue[]{
				new DataValue(eId, Types.VARCHAR), 
				new DataValue(shopId, Types.VARCHAR), 
				new DataValue(guQingNo, Types.VARCHAR), 
				new DataValue(shopId, Types.VARCHAR), 
				new DataValue(bDate, Types.VARCHAR),
				new DataValue(rDate, Types.VARCHAR), 
				new DataValue(modifyBy, Types.VARCHAR), 
				new DataValue(modifyDate, Types.VARCHAR), 
				new DataValue(modifyTime, Types.VARCHAR) 
			};
			
			InsBean ib1 = new InsBean("DCP_GUQINGORDER", columns);
			ib1.addValues(insValue);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
			
			//******************* DCP_GUQINGORDER_DETAIL *******************
			String[] columnsDetail = {"EID", "SHOPID","GUQINGNO","ORGANIZATIONNO","PLUNO","PUNIT",
					"PRICE","GUQINGTYPE","PFNO","PFORDERTYPE",
					"MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME"};
				
			DataValue[] insDetailValue = null;
			insDetailValue = new DataValue[]{
				new DataValue(eId, Types.VARCHAR), 
				new DataValue(shopId, Types.VARCHAR), 
				new DataValue(guQingNo, Types.VARCHAR), 
				new DataValue(shopId, Types.VARCHAR), 
				new DataValue(pluNo, Types.VARCHAR),
				new DataValue(pUnit, Types.VARCHAR), 
				new DataValue(price, Types.VARCHAR), 
				new DataValue(guQingType, Types.VARCHAR),
				new DataValue(pfNo, Types.VARCHAR),
				new DataValue(pfOrderType, Types.VARCHAR),
				new DataValue(modifyBy, Types.VARCHAR), 
				new DataValue(modifyByName, Types.VARCHAR), 
				new DataValue(modifyDate, Types.VARCHAR), 
				new DataValue(modifyTime, Types.VARCHAR) 
			};
			
			InsBean ibDet = new InsBean("DCP_GUQINGORDER_DETAIL", columnsDetail);
			ibDet.addValues(insDetailValue);
			this.addProcessData(new DataProcessBean(ibDet)); // 新增單頭
			
			int item = 1;
			for (level2Elm lv2 : mealDatas) {
				
				String dtNo = lv2.getDtNo();
				String dtName = lv2.getDtName();
				String beginTime = lv2.getBeginTime();
				String endTime = lv2.getEndTime();
				String qty = lv2.getQty();
				String restQty = lv2.getRestQty();
				
				if(Check.Null(pluNo) || Check.Null(dtNo)){
					continue;
				}
				
				DataValue[] insValueDT = null;
				insValueDT = new DataValue[]{
					new DataValue(eId, Types.VARCHAR), 
					new DataValue(shopId, Types.VARCHAR), 
					new DataValue(guQingNo, Types.VARCHAR), 
					new DataValue(pluNo, Types.VARCHAR),
					new DataValue(dtNo, Types.VARCHAR), 
					new DataValue(dtName, Types.VARCHAR), 
					new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
					new DataValue(endTime.replace(":", ""), Types.VARCHAR),
					
					new DataValue("0", Types.VARCHAR),
					new DataValue(qty, Types.VARCHAR),
					new DataValue("0", Types.VARCHAR), // saleQty 已售数量
					new DataValue(restQty, Types.VARCHAR), // 剩余数量， 新增单子时默认给 qty 的值
					new DataValue("N", Types.VARCHAR), // 是否沽清 ，默认 N 
					
					new DataValue(modifyBy, Types.VARCHAR), 
					new DataValue(modifyByName, Types.VARCHAR), 
					new DataValue(modifyDate, Types.VARCHAR), 
					new DataValue(modifyTime, Types.VARCHAR),
					new DataValue(pUnit, Types.VARCHAR) 
				};
				
				InsBean ibDT = new InsBean("DCP_GUQINGORDER_DINNERTIME", columnsDinner);
				ibDT.addValues(insValueDT);
				this.addProcessData(new DataProcessBean(ibDT)); // 新增單頭
				
				
				//************* 沽清调整记录 DCP_GUQINGORDER_PLQTYUPRECORD ***********
				String[] columns_hm ={"EID","SHOPID","GUQINGNO","PLUNO","ITEM","UPDATETYPE","DTNO","DTNAME",
						"BEGIN_TIME","END_TIME","PFNO","PFORDERTYPE","QTY","MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME",
						"SALEQTY","RESTQTY","PUNIT"
				};
				
				DataValue[] insValue_hm = new DataValue[] 
						{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR), 
								new DataValue(guQingNo, Types.VARCHAR),
								new DataValue(pluNo, Types.VARCHAR),
								new DataValue(item+"" ,Types.VARCHAR),
								new DataValue("pos", Types.VARCHAR),//枚举: initial：初始化,guQing：沽清,backEnd：后台,pos：POS端
								new DataValue(dtNo, Types.VARCHAR),
								new DataValue(dtName , Types.VARCHAR),
								new DataValue(beginTime.replace(":", ""), Types.VARCHAR),
								new DataValue(endTime.replace(":", ""), Types.VARCHAR),
								new DataValue(pfNo, Types.VARCHAR),
								new DataValue(pfOrderType, Types.VARCHAR),
								new DataValue(qty, Types.VARCHAR),
								new DataValue(modifyBy, Types.VARCHAR), 
								new DataValue(modifyByName, Types.VARCHAR), 
								new DataValue(modifyDate, Types.VARCHAR), 
								new DataValue(modifyTime, Types.VARCHAR),
								new DataValue("0", Types.VARCHAR),
								new DataValue(restQty, Types.VARCHAR),
								new DataValue(pUnit, Types.VARCHAR)
						};
				
				InsBean ib_hm = new InsBean("DCP_GUQINGORDER_PLQTYUPRECORD", columns_hm);
				ib_hm.addValues(insValue_hm);
				this.addProcessData(new DataProcessBean(ib_hm)); 
				item = item + 1;
				
			}
			
		}
		
		this.doExecuteDataToDB();
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PlanQtyUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PlanQtyUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PlanQtyUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PlanQtyUpdateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		List<level2Elm> mealDatas = req.getRequest().getMealData();
		String pluNo = req.getRequest().getPluNo();
		String guQingType = req.getRequest().getGuQingType();
		String eId = req.getRequest().getoEId();
		String shopId = req.getRequest().getoShopId();
		
		if(Check.Null(pluNo)){
			errCt++;
			errMsg.append("商品编码不可为空 ");
			isFail = true;
		}
		if(Check.Null(guQingType)){
			errCt++;
			errMsg.append("商品沽清类型编码不可为空 ");
			isFail = true;
		}
		if(Check.Null(eId)){
			errCt++;
			errMsg.append("企业编码不可为空 ");
			isFail = true;
		}
		if(Check.Null(shopId)){
			errCt++;
			errMsg.append("门店编码不可为空 ");
			isFail = true;
		}
		
		
		if (mealDatas == null || mealDatas.size() < 1) {
			errCt++;
			errMsg.append("餐段信息不可为空 ");
			isFail = true;
		}
		
		if(mealDatas !=null && mealDatas.size() > 0){
			for (level2Elm lv2 : mealDatas) {
				String dtNo = lv2.getDtNo().toString();
				String beginTime = lv2.getBeginTime().toString();
				String endTime = lv2.getEndTime().toString();
				if(Check.Null(dtNo)){
					errCt++;
					errMsg.append("餐段编码不可为空 ");
					isFail = true;
				}
				if(Check.Null(beginTime)){
					errCt++;
					errMsg.append("餐段开始时间不可为空 ");
					isFail = true;
				}
				if(Check.Null(endTime)){
					errCt++;
					errMsg.append("餐段截止时间不可为空 ");
					isFail = true;
				}
			}
			
		}
		

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanQtyUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanQtyUpdateReq>(){};
	}

	@Override
	protected DCP_PlanQtyUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanQtyUpdateRes();
	}
	
	private String queryMaxItem(DCP_PlanQtyUpdateReq req) throws Exception{
		String maxItem = "1";
		
		String sql = " select nvl(max(item),'0') + 1 as item from DCP_GUQINGORDER_PLQTYUPRECORD "
				+ " where EID = '"+req.getRequest().getoEId()+"' and SHOPID = '"+req.getRequest().getoShopId()+"'"
				+ " and guqingNo = '"+req.getRequest().getGuQingNo()+"' "
//				+ " and pluNo = '"+req.getRequest().getPluNo()+"' "
				;
		
		List<Map<String, Object>> itemDatas = this.doQueryData(sql, null);
		if(itemDatas != null){
			maxItem = itemDatas.get(0).get("ITEM").toString();
		}
		return maxItem;
	}
	
	private String getGuQingNO(DCP_PlanQtyUpdateReq req) throws Exception{
		
		String guQingNo = "";
		String sql = "";
		String eId = req.geteId();
		String shopId = req.getShopId();

		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		
		sql = "select MAX(guQingNo) AS guQingNo from DCP_GUQINGORDER where EID = '"+eId+"' and SHOPID = '"+shopId+"' and guQingNo like '%%"+bDate+"%%'";
		List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
		if(getDatas.size() > 0 ){
			guQingNo = (String) getDatas.get(0).get("GUQINGNO");

			if (guQingNo != null && guQingNo.length() > 0) {
				long i;
				guQingNo = guQingNo.substring(2, guQingNo.length());
				i = Long.parseLong(guQingNo) + 1;
				guQingNo = i + "";
				guQingNo = "GQ" + guQingNo;
				
			} else {
				guQingNo = "GQ" + bDate + "00001";
			}
		
		}
		
		return guQingNo;
		
	}
	
	
	
}
