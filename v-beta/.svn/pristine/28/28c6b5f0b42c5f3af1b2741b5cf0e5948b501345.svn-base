
package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PFOrderUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PFOrderUpdateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_PFOrderUpdateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_PFOrderUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PFOrderUpdate extends SPosAdvanceService<DCP_PFOrderUpdateReq, DCP_PFOrderUpdateRes> {
	
	@Override
	protected void processDUID(DCP_PFOrderUpdateReq req, DCP_PFOrderUpdateRes res) throws Exception {
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String shopId = req.getShopId();
		String pfNo = req.getRequest().getPfNo();
		
		String bDate = req.getRequest().getbDate();
		String rDate = req.getRequest().getrDate();
		String pfOrderType = req.getRequest().getPfOrderType();
		String dateReferType = req.getRequest().getDateReferType(); // 枚举: avg：平均,fix：固定（默认）
		String memo = req.getRequest().getMemo();
		String totPQty = req.getRequest().getTotPQty();
		String totCQty = req.getRequest().getTotCQty();
		String totAmt = req.getRequest().getTotAmt();
		
		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String _SysDATE = df.format(cal.getTime());

		String createBy = req.getOpNO();
		String createDate = df.format(cal.getTime());
		df=new SimpleDateFormat("HHmmss");
		String createTime = df.format(cal.getTime());
		
		//********* DCP_PORDER_FORECAST 营业预估主表 *********
		UptBean ub1 = null;
		ub1 = new UptBean("DCP_PORDER_FORECAST");
		ub1.addUpdateValue("BDATE", new DataValue(bDate.replace("-", ""), Types.VARCHAR));
		ub1.addUpdateValue("RDATE", new DataValue(rDate.replace("-", ""), Types.VARCHAR));
		ub1.addUpdateValue("PFORDERTYPE", new DataValue(pfOrderType, Types.VARCHAR));
		ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
		ub1.addUpdateValue("TOT_CQTY", new DataValue(totCQty, Types.VARCHAR));
		ub1.addUpdateValue("TOT_PQTY", new DataValue(totPQty, Types.VARCHAR));
		
		ub1.addUpdateValue("MODIFY_DATE", new DataValue(createDate, Types.VARCHAR));
		ub1.addUpdateValue("MODIFY_TIME", new DataValue(createTime, Types.VARCHAR));
		ub1.addUpdateValue("MODIFYBY", new DataValue(req.getOpNO(), Types.VARCHAR));
		
		// condition
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		ub1.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));

		this.addProcessData(new DataProcessBean(ub1));

		DelBean db2 = new DelBean("DCP_PORDER_FORECAST_DETAIL");
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db2.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db2.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		DelBean db3 = new DelBean("DCP_PORDER_FORECAST_DINNERTIME");
		db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db3.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db3.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		DelBean db4 = new DelBean("DCP_PORDER_FORECASTCALCULATE");
		db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db4.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db4.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		DelBean db5 = new DelBean("DCP_PORDER_FORECAST_MATERIAL");
		db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db5.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
		db5.addCondition("PFNO", new DataValue(pfNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db4)); 
		this.addProcessData(new DataProcessBean(db5));  
		this.addProcessData(new DataProcessBean(db3));  
		this.addProcessData(new DataProcessBean(db2));  
		
		//******************** 添加完 子表（DCP_PORDER_FORECAST_DETAIL）之后， 在原料表（DCP_PORDER_FORECAST_MATERIAL）中添加数据 ************8
		List<level2Elm> pluDatas = req.getRequest().getPluList();
		String[] columnsDetail = {
				"EID", "SHOPID", "ORGANIZATIONNO", "PFNO", "ITEM", "PLUNO", "PUNIT", //"WUNIT", "WQTY",
				"KQTY","REF_AMT","PRICE", "PRESALEQTY", "TRUEQTY" , "AMT" ,"GUQINGTYPE"
		};
		
		// 将成品放入集合中， 便于下面过滤原料
		List<Map<String, Object>> mainDatas = new ArrayList<>();
		
		String pluNoStr = "''";
		
		if(pluDatas != null){
			for (level2Elm lv2 : pluDatas) {
				DataValue[] insValueDetail = null;
				String pluNo = lv2.getPluNo();
				String pUnit = lv2.getpUnit();
				String qty = lv2.getQty()==null?"0":lv2.getQty();
				
				pluNoStr = pluNoStr + " ,'"+pluNo+"'";
				Map<String, Object> main = new HashMap<String, Object>();
				main.put("pluNo", pluNo);
				main.put("qty", qty);
				mainDatas.add(main);
				
				if(Check.Null(pluNo)){
					continue;
				}
				List<level3Elm> mealData = lv2.getMealData(); 
				if(mealData != null){
					for (level3Elm lv3 : mealData) {
						
						String[] mealColumn = { "EID", "SHOPID", "PFNO", "DTNO" , "DTNAME", "PLUNO", "BEGIN_TIME","END_TIME",
								"PUNIT", "QTY", "LASTSALETIME", "KQTY"};
						String dtNo = lv3.getDtNo();
						if(Check.Null(dtNo)){
							continue;
						}
						DataValue[] sValue1 = null;
						sValue1 = new DataValue[] {
								// qty实际数量， kqty 参考数量 。 新增时 qty 默认给参考数量
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(pfNo, Types.VARCHAR), 
								new DataValue(dtNo, Types.VARCHAR),
								new DataValue(lv3.getDtName(), Types.VARCHAR),
								new DataValue(pluNo, Types.VARCHAR), 
								new DataValue(lv3.getBeginTime(), Types.VARCHAR),
								new DataValue(lv3.getEndTime(), Types.VARCHAR),
								new DataValue(pUnit, Types.VARCHAR),
								new DataValue(lv3.getQty(), Types.VARCHAR), 
								new DataValue(lv3.getLastSaleTime(), Types.VARCHAR), //最近消费时间
								new DataValue(lv3.getkQty(), Types.VARCHAR)  
						};
						
						InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_DINNERTIME", mealColumn);
						ib2.addValues(sValue1);
						this.addProcessData(new DataProcessBean(ib2)); 
						
					}
				}
				
				
				// 插入主表数据
				insValueDetail = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(pfNo, Types.VARCHAR), 
						new DataValue(lv2.getItem(), Types.VARCHAR), 
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(lv2.getpUnit(), Types.VARCHAR),
						new DataValue(lv2.getkQty(), Types.VARCHAR), 
						new DataValue(lv2.getkAmt(), Types.VARCHAR),
						new DataValue(lv2.getPrice(), Types.VARCHAR), 
						new DataValue(lv2.getPreSaleQty(), Types.VARCHAR),
						new DataValue(lv2.getQty(), Types.VARCHAR), 
						new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
						new DataValue(lv2.getGuQingType(), Types.VARCHAR) 
						
				};

				InsBean ibDetail = new InsBean("DCP_PORDER_FORECAST_DETAIL", columnsDetail);
				ibDetail.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ibDetail)); // 新增單頭
				
			}
		}
		else{
			
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, rDate+"没有可供参考的商品销售记录，请重新选择");
		}

		//*******成品根据BOM查到商品原料，插入第三张原料表（DCP_PORDER_FORECAST_MATERIAL）
		String bomSql = this.getBomDatas(req, pluNoStr);
		
		List<Map<String, Object>> bomDatas = this.doQueryData(bomSql, null);
		if(!bomDatas.isEmpty()){
			
			//第一步：过滤出所有的原料
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("MATERIAL_PLUNO", true);		
			//调用过滤函数
			List<Map<String, Object>> materilDatas=MapDistinct.getMap(bomDatas, condition);
			
			//过滤出有BOM信息的成品，这些成品不能插入到原料表， 没有BOM的成品需要插入到原料表
			Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查詢條件
			condition2.put("PLUNO", true);		
			//调用过滤函数
			List<Map<String, Object>> mainPluDatas = MapDistinct.getMap(bomDatas, condition2);
			
			int item = 0; 
			for (int i = 0; i < materilDatas.size(); i++) {
				
				String material_pluNo = materilDatas.get(i).get("MATERIAL_PLUNO").toString();
				String material_pluName = materilDatas.get(i).get("MATERIAL_PLUNAME").toString();
				
				String material_pUnit = materilDatas.get(i).get("MATERIAL_PUNIT").toString(); //原料配方单位
				String material_wUnit = materilDatas.get(i).get("MATERIAL_WUNIT").toString(); //原料库存单位
				String punit = materilDatas.get(i).get("PUNIT").toString(); //原料要货单位
				String pUnitUdLengthStr = materilDatas.get(i).get("PUNITUDLENGTH").toString(); //原料要货单位保留位数
				int pUnitUdLength = Integer.parseInt(pUnitUdLengthStr);
				
				double bomUnitRatio = 1; // 配方单位到库存单位的单位转换率
				double pUnitRatio = 1; // 要货单位到库存单位的单位转换率
				
				//2019-12-03 需要判断原料的配方单位和库存单位是否相等，如果不等，先转换为库存单位，再转换为要货单位。
				//需要计算不同单位对应数量和进货价。
				if(!material_pUnit.equals(material_wUnit)){ 
					
					List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
						material_pluNo, material_pUnit); 
					
					if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配方商品 " + material_pluNo + " 找不到对应的 "+material_pUnit+" 到"+material_wUnit+" 的换算关系");
					}
					
					bomUnitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
					
				}
					
				//第一步：如果配方单位和库存单位相等， 就直接查库存单位到要货单位的转换率
				List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
						material_pluNo, punit);
				
				if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配方商品 " + material_pluNo + " 找不到对应的 "+punit+" 到"+material_wUnit+" 的换算关系");
				}
				
				pUnitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
					
				String sQty = materilDatas.get(i).getOrDefault("SQTY","0").toString();
				String material_price = materilDatas.get(i).getOrDefault("MATERIAL_PRICE", "0").toString();
//				double minQty = 0; // 最小要货量（每个成品单位1对应的原料数 相加得到） 不能这么算，不合逻辑
				double pqty = 0; // 所有成品所需的原料数相加得到该值
				double material_wqty = 0;
				double amt = 0;
				double unitRatio2 = 0;
				double reqWQty = 0;
				String mainPluStr = "";
				
				String mwunit = "";
				//第二步：计算原料的应要数量
				for (Map<String, Object> pluMap : bomDatas) {
					
					if(material_pluNo.equals(pluMap.get("MATERIAL_PLUNO").toString())){
						String pluNo = pluMap.get("PLUNO").toString();
						
						String pluName = pluMap.get("PLUNAME").toString();
						String qty = "0";
						double mainWQty  = 0;  
						// 下面这个循环体从上面的成品中提取
						for (Map<String, Object> mainMap : mainDatas) {
							if(pluNo.equals(mainMap.get("pluNo").toString())){
								mainWQty = Double.parseDouble(mainMap.getOrDefault("qty","0").toString()); //要货单位对应的数量
							}
						}
						String material_baseQty = pluMap.getOrDefault("MATERIAL_BASEQTY","0").toString();
						String baseQty = pluMap.getOrDefault("BASEQTY","0").toString();
						
						double epqty = 0 ;
						if(Double.parseDouble(baseQty) != 0){
							epqty = Double.parseDouble(material_baseQty)/Double.parseDouble(baseQty) * mainWQty;
						}
						
						pqty = pqty + epqty;
						
//						minQty = Float.parseFloat(material_baseQty) / Float.parseFloat(baseQty); 
						mainPluStr = mainPluStr+ pluName+"("+mainWQty+")\r";
						
					}
				}
				
				material_wqty = pqty * pUnitRatio;
				
				amt = pqty * Float.parseFloat(material_price);
				
				double materialPqty = 0;
				
				// 举个栗子： 配方单位是g， 库存单位是 BAO， 要货单位是xiang 
				// 单位转换信息： 克==》包 （1000：1） ，箱==》包 （1：10），设置不同的单位到库存单位的转换信息即可
				materialPqty = pqty * bomUnitRatio / pUnitRatio ;
				Double materialKQty = materialPqty; //materialKQty 对应原料表的KQty 字段， 是根据成品用量计算出原料的bom 用量，不经过倍量和最小要货量计算的数值。
				
				//2019-12-23 根据返回的最小要货量，最大要货量，要货倍量，根据二分法再算一遍要货量
				//举个例子： 如 商品最小要货量为0，最大要货量为10000， 倍量为5， 
				//千元用量计算出的预估数为12， 最接近12的5的倍数为10， 所以10就是最终预估量。
				//千元用量计算出的预估数为13， 最接近13的5的倍数为15， 所以15就是最终预估量。
				
				String minQty = materilDatas.get(i).get("MINQTY").toString(); //最小要货量
				String maxQty = materilDatas.get(i).get("MAXQTY").toString(); //最大要货量
				String mulQty = materilDatas.get(i).get("MULQTY").toString(); //要货倍量
				
				int num = 1 ;
				
				double mulQtyDou = Double.parseDouble(mulQty);
				double minQtyDou = Double.parseDouble(minQty);
				if(materialPqty <= minQtyDou){ //如果 预估数量 < 最小要货量， 预估数量==最小要货量。
					materialPqty = minQtyDou;
				}else{
					
					if(mulQtyDou > 0){ //倍量有可能为0 ，表示任意数都可以。

						double zh = materialPqty / mulQtyDou ;
						BigDecimal zhb = new BigDecimal(zh);
						double maxNum = zhb.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
						String a = new DecimalFormat("0").format(maxNum);
						num = Integer.parseInt(a);
						
						Double[] dArr = new Double[num];
						if(num > 0 ){
							for (int j = 0; j < num ; j++) {
								double d2 = mulQtyDou * j ;
								BigDecimal b = new BigDecimal(d2);
						        /*setScale 第一个参数为保留位数 第二个参数为舍入机制
						         BigDecimal.ROUND_DOWN 表示不进位 
						         BigDecimal.ROUND_UP表示进位*/
						        d2 = b.setScale(pUnitUdLength, BigDecimal.ROUND_HALF_UP).doubleValue();
								dArr[j] = new Double(d2);
							}
							
						}
						
						MyCommon mc = new MyCommon();
						materialPqty = mc.getNumberThree(dArr, materialPqty);
						mc = null;
					}
					
					
					if(materialPqty > Double.parseDouble(maxQty)){ //如果预估量 > 最大要货量， 预估量==最大要货量即可
						materialPqty = Double.parseDouble(maxQty);
					}
					
				}
				
				if (unitRatio2 > 0){
					reqWQty = Float.parseFloat(sQty) * pUnitRatio; // sQty 是库存单位对应的数量， 需要转换为要货单位对应的数量
				}
				else{
					reqWQty = 0 ;  // 防止出现查不到换算率、 或者  换算率设置为 0  这种情况，属于资料不正确
				}
				
				String[] columns2 = {
						"SHOPID", "OrganizationNO","EID","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
						"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
						"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
						"MAINPLU" , "BDATE" ,
						// 2019-10-28 增加以下字段，用于物料报单
						"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
						
				};
				DataValue[] insValue2 = null;
				
				// 插入主表数据
				insValue2 = new DataValue[]{
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(eId, Types.VARCHAR),
						new DataValue(pfNo, Types.VARCHAR), 
						new DataValue(item + 1 +"", Types.VARCHAR), 
						new DataValue(material_pluNo, Types.VARCHAR),
						new DataValue(punit, Types.VARCHAR),
						new DataValue(material_wUnit,  Types.VARCHAR), // status 0:新建
						new DataValue(String.format("%.2f", material_wqty), Types.VARCHAR),
//						new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialPqty - reqWQty), Types.VARCHAR),  // 实报数量 = 应报数量 - 实存
						new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialPqty ), Types.VARCHAR),  // 实报数量 = 应报数量 - 实存
						new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
						new DataValue(minQty, Types.VARCHAR), 
						new DataValue(String.format("%.2f", Double.parseDouble(material_price)), Types.VARCHAR), 
						new DataValue(String.format("%.2f", amt), Types.VARCHAR),
						new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialKQty), Types.VARCHAR), // 应报数量（千元用量）
						new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
						new DataValue(String.format("%.2f", reqWQty), Types.VARCHAR), // req_wqty 参考库存量
						new DataValue(String.format("%.2f", amt), Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue(memo, Types.VARCHAR),
						new DataValue(mainPluStr, Types.VARCHAR),
						new DataValue(bDate.replace("-", ""), Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("", Types.VARCHAR)
						
				};

				InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_MATERIAL", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
				
				item = item + 1;
				
			}
			
			// 先去重，去掉有BOM的成品，剩下没有BOM的成品， 需要插入到子表中
			for (level2Elm lv2 : pluDatas) {
				String mainPluNo = lv2.getPluNo();
				String mainPluName = lv2.getPluName();
				String qty = lv2.getQty();
				
				if(Check.Null(mainPluNo)){
					continue;
				}
				
				boolean hasBom = false;
				if( mainPluDatas != null && !mainPluDatas.isEmpty() ){
					for (Map<String, Object> map : mainPluDatas) {
						
//						Iterator<Map<String, Object>> it = mainDatas.iterator();
//				        while (it.hasNext()){
//				        	Map<String, Object> s = it.next();
//				        	if(mainPluNo.equals(map.get("PLUNO").toString())){ // 如果请求传进来的 pluNo 里有 BOM资料， 该pluNo就不插入原料表
//				            	mainDatas.remove(s); //注意这里调用的是集合的方法
//				            }
//				        }
						
						if(mainPluNo.equals(map.get("PLUNO").toString())){ // 如果该商品 有BOM信息，就不能插入到原料表
							hasBom = true;
						}
					}
				}
				
				
				if(hasBom == false){ //没有BOM信息的商品要插入到原料表中
					
					String[] columns2 = {
							"SHOPID", "OrganizationNO","EID","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
							"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
							"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
							"MAINPLU" , "BDATE" ,
							// 2019-10-28 增加以下字段，用于物料报单
							"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
							
					};
					DataValue[] insValue2 = null;
					
					List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
							mainPluNo, lv2.getpUnit()); 
					String wunit = "";
					double punitRatio = 0 ;
					if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + mainPluNo + " 找不到对应的 "+lv2.getpUnit()+" 到库存单位 的换算关系");
					}else{
						wunit = getQData_Ratio.get(0).get("WUNIT").toString();
						punitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
					}
					
					double wqty = Double.parseDouble(qty) * punitRatio;
					
					// 插入主表数据
					insValue2 = new DataValue[]{
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(eId, Types.VARCHAR),
							new DataValue(pfNo, Types.VARCHAR), 
							new DataValue(item + 1, Types.VARCHAR), 
							new DataValue(mainPluNo, Types.VARCHAR),
							new DataValue(lv2.getpUnit(), Types.VARCHAR),
							new DataValue(wunit,  Types.VARCHAR), //wunit 
							new DataValue(wqty, Types.VARCHAR), //WQTY
							new DataValue(lv2.getQty(), Types.VARCHAR), 
							new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
							new DataValue("0", Types.VARCHAR), 
							new DataValue(lv2.getPrice(), Types.VARCHAR), 
							new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
							new DataValue(lv2.getQty(), Types.VARCHAR), // 应报数量（千元用量）
							new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
							new DataValue("0", Types.VARCHAR), // req_wqty 参考库存量
							new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("", Types.VARCHAR),
							new DataValue(mainPluName + "("+lv2.getQty()+")", Types.VARCHAR),
							new DataValue(bDate.replace("-", ""), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("", Types.VARCHAR)
							
					};

					InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_MATERIAL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
					
					item = item + 1;
					
				
				}
				
			}
			
			
		}
		else{
			
			//开始过滤没有BOM 的商品，这些也需要插入到 DCP_PORDER_FORECAST_MATERIAL 中
			int item = 0;
			for (level2Elm lv2 : pluDatas) {
				
				String mainPluNo = lv2.getPluNo();
				String mainPluName = lv2.getPluName();
				
				String qty = lv2.getQty();
				if(Check.Null(mainPluNo)){
					continue;
				}
				
				String[] columns2 = {
						"SHOPID", "OrganizationNO","EID","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
						"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
						"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
						"MAINPLU" , "BDATE" ,
						// 2019-10-28 增加以下字段，用于物料报单
						"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
						
				};
				DataValue[] insValue2 = null;
				
				List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
						mainPluNo, lv2.getpUnit()); 
				String wunit = "";
				double punitRatio = 0 ;
				if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + mainPluNo + " 找不到对应的 "+lv2.getpUnit()+" 到库存单位 的换算关系");
				}else{
					wunit = getQData_Ratio.get(0).get("WUNIT").toString();
					punitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
				}
				
				double wqty = Double.parseDouble(qty) * punitRatio;
				
				// 插入主表数据
				insValue2 = new DataValue[]{
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(shopId, Types.VARCHAR), 
						new DataValue(eId, Types.VARCHAR),
						new DataValue(pfNo, Types.VARCHAR), 
						new DataValue(item + 1, Types.VARCHAR), 
						new DataValue(mainPluNo, Types.VARCHAR),
						new DataValue(lv2.getpUnit(), Types.VARCHAR),
						new DataValue("",  Types.VARCHAR), //wunit 
						new DataValue("0", Types.VARCHAR), //WQTY
						new DataValue(lv2.getQty(), Types.VARCHAR), 
						new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
						new DataValue("0", Types.VARCHAR), 
						new DataValue(lv2.getPrice(), Types.VARCHAR), 
						new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
						new DataValue(lv2.getQty(), Types.VARCHAR), // 应报数量（千元用量）
						new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
						new DataValue("0", Types.VARCHAR), // req_wqty 参考库存量
						new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue(mainPluName + "("+lv2.getQty()+")", Types.VARCHAR),
						new DataValue(bDate.replace("-", ""), Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("", Types.VARCHAR)
						
				};

				InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_MATERIAL", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
				
				item = item + 1;
				
			}
			
			
			
			
		}
		
		

		this.doExecuteDataToDB();
		
		res.setDatas(new ArrayList<DCP_PFOrderUpdateRes.level1Elm>());
		DCP_PFOrderUpdateRes.level1Elm pfLev = res.new level1Elm();
		pfLev.setPfNo(pfNo);
		res.getDatas().add(pfLev);
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PFOrderUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PFOrderUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PFOrderUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PFOrderUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PFOrderUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderUpdateReq>(){};
	}

	@Override
	protected DCP_PFOrderUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderUpdateRes();
	}
	
	
	/**
	 * 获取原料数据
	 * @param req
	 * @param pluNoStr
	 * @return
	 */
	protected String getBomDatas(DCP_PFOrderUpdateReq req ,String pluNoStr){
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();
		
		String sql;
		try {
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			String sysDate = df.format(cal.getTime());
			
			Calendar calendar = Calendar.getInstance();//获得当前时间
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");  
			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);  
			String preDate = format.format(calendar.getTime());  // 昨天 (取昨天日结后该商品的库存)
			
			StringBuffer sqlbuf = new StringBuffer();
			sql = "";
			sqlbuf.append( "select * "
  + " from (select distinct a.pluno , gn.pluName, a.baseqty, a.material_pluno, a.material_punit, a.material_baseqty   ,   "
  + " c.pluname as material_pluname, d.unit_name as material_punitname, a.bom_type, c.wunit as material_wunit, c.punit ,"
  + " e.unit_name as material_wunitname, "
  + " c.isbatch as material_isbatch, nvl(b1.price1, 0 ) as material_price  , nvl(SUM(f.qty) , '0')AS sQty "
  + " , nvl(gs.min_qty, 0 ) AS minQty, nvl(gs.max_qty , 99999) AS maxQty ,NVL( gs.mul_qty , 0 ) AS mulQty "
  + " , nvl(u.udlength,'2') pUnitUdLength  "
  + " from (select a.EID, a.pluno, a.unit, a.material_pluno, a.material_qty as material_baseqty, "
  + " a.material_unit as material_punit, a.qty as baseqty, a.bom_type "
  + " from DCP_BOM a "
  + " left join DCP_BOM_shop b"
  + " on a.EID = b.EID"
  + " and a.pluno = b.pluno " 
  + " and b.EID = '"+eId+"' "
  + " and b.material_bdate <= '"+sysDate+"' "
  + " and (b.material_edate >= '"+sysDate+"' or b.material_edate is null) "
  + " and b.organizationno = '"+shopId+"' and b.status='100' "
  + " and a.bom_type = b.bom_type "
  + " where a.EID = '"+eId+"' "
  + " and a.effdate <= '"+sysDate+"'" 
  + " and a.material_bdate <= '"+sysDate+"'"
  + " and (a.material_edate >= '"+sysDate+"' or a.material_edate is null)"
  + " and a.status='100' "
  + " and a.bom_type = '0' "
  + " and b.pluno is null "
  + " and a.pluno in ("+pluNoStr+") "
  + " union all "
  + " select EID, pluno, unit, material_pluno, material_qty as material_baseqty, material_unit as material_punit, "
  + " qty as baseqty, bom_type "
  + " from DCP_BOM_shop"
  + " where EID = '"+eId+"' "
  + " and material_bdate <= '"+sysDate+"' "
  + " and (material_edate >= '"+sysDate+"' or material_edate is null) "
  + " and organizationno = '"+shopId+"'"
  + " and status='100'"
  + " and bom_type = '0' "
  + " and pluno in ("+pluNoStr+")) a "
  + " inner join DCP_GOODS c "
  + " on a.EID = c.EID "
  + " and a.material_pluno = c.pluno "
  + " and c.status='100' "
  + " LEFT JOIN DCP_GOODS gn ON a.EID = gn.EID AND a.pluNO = gn.pluNo "
  
  // 2019-12-19 增加查询商品适用门店， 查商品最大、最小、要货倍量
  + " LEFT JOIN DCP_GOODS_shop gs ON a.EID = gs.EID AND  a.material_pluNo = gs.pluNo  "
  + " AND nvl(gs.fpod, 'Y' ) = 'Y' AND gs.status='100' AND gs.organizationNO = '"+req.getShopId()+"'  "
  + " LEFT JOIN DCP_UNIT u ON c.punit = u.unit AND c.EID = u.EID AND u.status='100' "
  
  + " left join (select * "
  + " 		from (select EID, pluno, unit, price1, idx, row_number() over(partition by EID, pluno, unit order by idx) rn "
  + " 			from (select EID, pluno, unit, price1, 1 as idx "
  + " 						from DCP_PRICE_shop "
			                    + " where EID = '"+eId+"' "
			                    + " and organizationno = '"+shopId+"' "
			                    + " and effdate <= '"+sysDate+"'" 
			                    + " and (ledate >= '"+sysDate+"' or ledate is null) "
			                    + " and status='100' "
			                    + " union"
			                    + " select EID, pluno, unit, price1, 2 as idx "
			                    + " from DCP_PRICE "
			                    + " where EID = '"+eId+"'"
			                    + " and effdate <= '"+sysDate+"'"
			                    + " and (ledate >= '"+sysDate+"' or  ledate is null) "
			                    + " and status='100')) "
			              + " where rn = 1) b1 "
			     + " on a.EID = b1.EID "
			     + " and a.material_pluno = b1.pluno "
			     + " and (a.material_punit = b1.unit) " 
			     + " left join DCP_UNIT_lang d "
			     + " on d.EID = a.EID " 
			     + " and a.material_punit = d.unit " 
			     + " and d.lang_type = '"+langType+"' "
			     + " and d.status='100' "
			     + " left join DCP_UNIT_lang e "
			     + " on e.EID = a.EID "
			     + " and c.wunit = e.unit "
			     + " and e.lang_type = '"+langType+"' "
			     + " and e.status='100'  "
			     + " LEFT JOIN DCP_stock_day_static f ON a.EID = f.EID AND a.material_pluno = f.pluNo "

			     + " AND f.eDate = '"+preDate+"' AND f.organizationNo = '"+shopId+"' "
			     + " LEFT JOIN DCP_WAREHOUSE g ON f.EID=g.EID and f.warehouse=g.warehouse AND g.WAREHOUSE_TYPE<>'3'    "
			     + " GROUP BY  a.pluno , gn.pluName, a.baseqty, a.material_pluno, a.material_punit, a.material_baseqty   ,   "
			     + " c.pluname , d.unit_name  , a.bom_type, c.wunit , e.unit_name  , "
			     + " c.isbatch , b1.price1 , c.punit  "
			     + " ,  gs.min_qty , gs.max_qty , gs.mul_qty , u.udlength ) dtl  ");
			sql = sqlbuf.toString();
			
			return sql;
			
		} catch (Exception e) {

			return "";
		}
	}
	

}
