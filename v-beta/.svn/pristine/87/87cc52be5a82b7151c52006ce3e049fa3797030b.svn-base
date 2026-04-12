
package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PFOrderCreateReq;
import com.dsc.spos.json.cust.req.DCP_PFOrderCreateReq;
import com.dsc.spos.json.cust.req.DCP_PFOrderCreateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_PFOrderCreateReq.level3Elm;
import com.dsc.spos.json.cust.res.DCP_PFOrderCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_PFOrderCreate extends SPosAdvanceService<DCP_PFOrderCreateReq, DCP_PFOrderCreateRes> {
	
	Logger logger = LogManager.getLogger(this.getClass());
	
	@Override
	protected void processDUID(DCP_PFOrderCreateReq req, DCP_PFOrderCreateRes res) throws Exception {
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String shop = req.getShopId();
		String pfNo = this.getPFNO(req);
		
		String pfId = req.getRequest().getPfId();
		
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
		String[] columnsMain = {
				"Shop", "OrganizationNO","eId","PFNO","BDATE", "RDATE",   "MEMO", 
				"STATUS", "CREATEBY","CREATE_DATE", "CREATE_TIME", "TOT_PQTY", "TOT_AMT", "TOT_CQTY", 
				"PF_ID" , "DATEREFERTYPE" , "PFORDERTYPE"
		};
		
		DataValue[] insValueMain = null;
		
		// 插入主表数据
		insValueMain = new DataValue[]{
				new DataValue(shop, Types.VARCHAR), 
				new DataValue(shop, Types.VARCHAR), 
				new DataValue(eId, Types.VARCHAR),
				new DataValue(pfNo, Types.VARCHAR), 
				new DataValue(bDate.replace("-", ""), Types.VARCHAR), 
				new DataValue(rDate.replace("-", ""), Types.VARCHAR),
				new DataValue(memo, Types.VARCHAR),
				new DataValue("0", Types.VARCHAR), // status 0:新建
				new DataValue(createBy, Types.VARCHAR),
				new DataValue(createDate, Types.VARCHAR), 
				new DataValue(createTime, Types.VARCHAR),
				new DataValue(totPQty, Types.VARCHAR), 
				new DataValue(totAmt, Types.VARCHAR), 
				new DataValue(totCQty, Types.VARCHAR),
				new DataValue(pfId, Types.VARCHAR),
				new DataValue(dateReferType, Types.VARCHAR),
				new DataValue(pfOrderType, Types.VARCHAR)
				
		};

		InsBean ibMain = new InsBean("DCP_PORDER_FORECAST", columnsMain);
		ibMain.addValues(insValueMain);
		this.addProcessData(new DataProcessBean(ibMain)); // 新增單頭
		
		//******************** 添加完 子表（DCP_PORDER_FORECAST_DETAIL）之后， 在原料表（DCP_PORDER_FORECAST_MATERIAL）中添加数据 ************8
		List<level2Elm> pluDatas = req.getRequest().getPluList();
		String[] columnsDetail = {
				"eId", "SHOP", "ORGANIZATIONNO", "PFNO", "ITEM", "PLUNO", "PUNIT", //"WUNIT", "WQTY",
				"KQTY","REF_AMT","PRICE", "PRESALEQTY", "TRUEQTY" , "AMT" ,"GUQINGTYPE" 
		};
		// 将成品放入集合中， 便于下面过滤原料
		List<Map<String, Object>> mainDatas = new ArrayList<>();
		
		String pluNoStr = "''";
		
		String evPluStr = "''";
		for (level2Elm evPlu : pluDatas) {
			String pluNo = evPlu.getPluNo();
			evPluStr = evPluStr + " ,'"+pluNo+"'";
			
			if(Check.Null(pluNo)){
				continue;
			}
			
		}
		
		String punitSql = "select a.pluNO, a.pUnit , a.sUnit ,  b.udLength as punitUdlength from DCP_goods a "
				+ " LEFT JOIN DCP_unit b on a.eId = b.eId and a.punit = b.unit  "
				+ " where a.eId = '"+eId+"' and a.pluNO in( "+evPluStr+" )";
		
		List<Map<String , Object>> punitList = this.doQueryData(punitSql, null);
		
		if(pluDatas != null){
			for (level2Elm lv2 : pluDatas) {
				DataValue[] insValueDetail = null;
				String pluNo = lv2.getPluNo();
				String qty = lv2.getQty()==null?"0":lv2.getQty();
				pluNoStr = pluNoStr + " ,'"+pluNo+"'";
				
				if(Check.Null(pluNo)){
					continue;
				}
				
				String pUnit = "";
				String punitUdLength = "2";
				String sUnit = lv2.getsUnit();
				
				if(punitList != null && punitList.isEmpty() == false){
					
					Map<String, Object> conditionUnit = new HashMap<String, Object>(); //查詢條件
					conditionUnit.put("PLUNO", pluNo);		
					//调用过滤函数
					List<Map<String, Object>> getQHeader = MapDistinct.getWhereMap(punitList, conditionUnit, false);
					
					if(getQHeader != null && getQHeader.isEmpty() == false){
						pUnit = getQHeader.get(0).get("PUNIT").toString();
						punitUdLength = getQHeader.get(0).get("PUNITUDLENGTH").toString();
					}
					
				}
				
				// 成品的销售单位==》库存单位
				double sUnitRatio = 1;
//				List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio(dao, req.geteId(),
//						pluNo, sUnit); 
//					
//				if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + pluNo + " 找不到对应的 "+sUnit+" 到库存单位的换算关系");
//				}else{
//					sUnitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
//				}
				
				
				//成品的销售单位 和 要货单位是否相等，如果不相等，需要再查一下要货单位到库存单位的转换率
				double pUnitRatio = 1;
				if(!sUnit.equals(pUnit)){

//					List<Map<String, Object>> getQData_Ratio2 = PosPub.getUnit_Ratio(dao, req.geteId(),
//							pluNo, pUnit); 
//						
//					if (getQData_Ratio2 == null || getQData_Ratio2.isEmpty()) {
//						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + pluNo + " 找不到对应的 "+pUnit+" 到库存单位的换算关系");
//					}else{
//						pUnitRatio = Double.parseDouble(getQData_Ratio2.get(0).getOrDefault("UNIT_RATIO","1").toString());
//					}
				}else{
					pUnitRatio = sUnitRatio;
				}
				
				Map<String, Object> main = new HashMap<String, Object>();
				main.put("pluNo", pluNo);//成品
				main.put("qty", qty);//成品销售单位对应的数量
				main.put("sUnit", sUnit);// 成品销售单位
				main.put("pUnit", pUnit);// 成品要货单位
				main.put("sUnitRatio", sUnitRatio+"");// 成品销售单位到库存单位的换算率
				main.put("pUnitRatio", pUnitRatio+"");// 成品要货单位到库存单位的换算率
				
				mainDatas.add(main);
				
				List<level3Elm> mealData = lv2.getMealData(); 
				if(mealData != null){
					for (level3Elm lv3 : mealData) {
						
						String[] mealColumn = { "eId", "SHOP", "PFNO", "DTNO" , "DTNAME", "PLUNO", "BEGIN_TIME","END_TIME",
								"PUNIT", "QTY", "LASTSALETIME", "KQTY"};
						String dtNo = lv3.getDtNo();
						if(Check.Null(dtNo)){
							continue;
						}
						DataValue[] sValue1 = null;
						sValue1 = new DataValue[] {
								// qty实际数量， kqty 参考数量 。 新增时 qty 默认给参考数量
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shop, Types.VARCHAR),
								new DataValue(pfNo, Types.VARCHAR), 
								new DataValue(dtNo, Types.VARCHAR),
								new DataValue(lv3.getDtName(), Types.VARCHAR),
								new DataValue(pluNo, Types.VARCHAR), 
								new DataValue(lv3.getBeginTime(), Types.VARCHAR),
								new DataValue(lv3.getEndTime(), Types.VARCHAR),
								new DataValue(sUnit, Types.VARCHAR),
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
						new DataValue(shop, Types.VARCHAR), 
						new DataValue(shop, Types.VARCHAR), 
						new DataValue(pfNo, Types.VARCHAR), 
						new DataValue(lv2.getItem(), Types.VARCHAR), 
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(sUnit, Types.VARCHAR),
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
					
//					List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio(dao, req.geteId(),
//						material_pluNo, material_pUnit); 
//					
//					if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
//						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配方商品 " + material_pluNo + " 找不到对应的 "+material_pUnit+" 到"+material_wUnit+" 的换算关系");
//					}
//					
//					bomUnitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
					
				}
					
				//第一步：如果配方单位和库存单位相等， 就直接查库存单位到要货单位的转换率
//				List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio(dao, req.geteId(),
//						material_pluNo, punit);
//				
//				if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配方商品 " + material_pluNo + " 找不到对应的 "+punit+" 到"+material_wUnit+" 的换算关系");
//				}
				
//				pUnitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
					
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
						String mainUnit = pluMap.get("MAINUNIT").toString();// 成品BOM单位
						
						String qty = "0";
						double mainWQty  = 0;  
						// 下面这个循环体从上面的成品中提取
						for (Map<String, Object> mainMap : mainDatas) {
							if(pluNo.equals(mainMap.get("pluNo").toString())){
								String mainPUnit = mainMap.get("pUnit").toString();//成品要货单位
								String mainSUnit = mainMap.get("sUnit").toString();//成品销售单位
								String mainSUnitRatio = mainMap.get("sUnitRatio").toString();//成品销售单位 到库存单位换算率
								String sUnitQty = mainMap.getOrDefault("qty","0").toString(); //成品销售单位对应的数量
								
								if(mainUnit.equals(mainSUnit)){ //当成品BOM单位和销售单位一样时，  销售单位对应数量就是 BOM单位对应的数量
									mainWQty = Double.parseDouble(sUnitQty); 
								}else {
									
									double mainBomUnitRatio = 1;
									//计算成品BOM单位到库存单位换算率
//									List<Map<String, Object>> getQData_Ratio_mainUnit = PosPub.getUnit_Ratio(dao, req.geteId(),
//											pluNo, mainUnit);
//									
//									if (getQData_Ratio_mainUnit == null || getQData_Ratio_mainUnit.isEmpty()) {
//										throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + material_pluNo + " 找不到对应的BOM单位 "+mainUnit+" 到库存单位的换算关系");
//									}else{
//										mainBomUnitRatio = Double.parseDouble(getQData_Ratio_mainUnit.get(0).getOrDefault("UNIT_RATIO","1").toString());
//									}
									
									// 成品 销售单位 == 》库存单位 ==》 BOM单位
									mainWQty = Double.parseDouble(sUnitQty)*Double.parseDouble(mainSUnitRatio) / mainBomUnitRatio;
									
								}
								
								// 销售单位 ==》 库存单位 ==》BOM单位 
							}
						}
						String material_baseQty = pluMap.getOrDefault("MATERIAL_BASEQTY","0").toString();//原料BOM单位基础用量
						String baseQty = pluMap.getOrDefault("BASEQTY","0").toString(); // 成品BOM单位基础用量
						
						double epqty = 0 ;
						if(Double.parseDouble(baseQty) != 0){
							epqty = Double.parseDouble(material_baseQty)/Double.parseDouble(baseQty) * mainWQty;
						}
						
						pqty = pqty + epqty;
						
//						minQty = Float.parseFloat(material_baseQty) / Float.parseFloat(baseQty); 
						mainPluStr = mainPluStr+ pluName+"("+mainWQty+")\r";
						
					}
				}
				
				logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"原料："+material_pluNo +" pUnit:"+ punit + " material_punit:"+ material_pUnit + " material_wunit:"+ material_wUnit +
						 "   bomUnitRatio:" +bomUnitRatio+	"   pUnitRatio:" +pUnitRatio+ "   pqty:"+pqty   );
				
				material_wqty = pqty * pUnitRatio;
				
				amt = pqty * Float.parseFloat(material_price);
				
				double materialPqty = 0;
				
				// 举个栗子： 配方单位是g， 库存单位是 BAO， 要货单位是xiang 
				// 单位转换信息： 克==》包 （1000：1） ，箱==》包 （1：10），设置不同的单位到库存单位的转换信息即可
				materialPqty = pqty * bomUnitRatio / pUnitRatio ;
				
				Double materialKQty = materialPqty; //materialKQty 对应原料表的KQty 字段， 是根据成品用量计算出原料的bom 用量，不经过倍量和最小要货量计算的数值。
				
				//2020-05-21 SA孙红艳要求： 如果单位小数位数大于两位的，报单参考KQTY列数据的小数位数跟着单位的走
				String materialKQtyStr = materialKQty + "";
				if(pUnitUdLength > 2){
					materialKQtyStr = String.format("%."+pUnitUdLengthStr+"f", materialKQty);
				}else{
					materialKQtyStr = String.format("%.2f", materialKQty);
				}
				
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
				
				// 2020-04-30 根据SA孙红艳需求， 后端不再根据经济批量计算，就计算成品数量对应的原料数量即可。
//				if(materialPqty <= minQtyDou){ //如果 预估数量 < 最小要货量， 预估数量==最小要货量。
//					materialPqty = minQtyDou;
//				}else{
//					
//					if(mulQtyDou > 0){ //倍量有可能为0 ，表示任意数都可以。
//
//						double zh = materialPqty / mulQtyDou ;
//						BigDecimal zhb = new BigDecimal(zh);
//						double maxNum = zhb.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
//						String a = new DecimalFormat("0").format(maxNum);
//						num = Integer.parseInt(a);
//						
//						Double[] dArr = new Double[num];
//						if(num > 0 ){
//							for (int j = 0; j < num ; j++) {
//								double d2 = mulQtyDou * j ;
//								BigDecimal b = new BigDecimal(d2);
//						        /*setScale 第一个参数为保留位数 第二个参数为舍入机制
//						         BigDecimal.ROUND_DOWN 表示不进位 
//						         BigDecimal.ROUND_UP表示进位*/
//						        d2 = b.setScale(pUnitUdLength, BigDecimal.ROUND_HALF_UP).doubleValue();
//								dArr[j] = new Double(d2);
//							}
//							
//						}
//						
//						MyCommon mc = new MyCommon();
//						materialPqty = mc.getNumberThree(dArr, materialPqty);
//						mc = null;
//					}
//					
//					
//					if(materialPqty > Double.parseDouble(maxQty)){ //如果预估量 > 最大要货量， 预估量==最大要货量即可
//						materialPqty = Double.parseDouble(maxQty);
//					}
//					
//				}
				
				if (unitRatio2 > 0){
					reqWQty = Float.parseFloat(sQty) * pUnitRatio; // sQty 是库存单位对应的数量， 需要转换为要货单位对应的数量
				}
				else{
					reqWQty = 0 ;  // 防止出现查不到换算率、 或者  换算率设置为 0  这种情况，属于资料不正确
				}
				
				String[] columns2 = {
						"Shop", "OrganizationNO","eId","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
						"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
						"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
						"MAINPLU" , "BDATE" ,
						// 2019-10-28 增加以下字段，用于物料报单
						"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
						
				};
				DataValue[] insValue2 = null;
				
				// 插入主表数据
				insValue2 = new DataValue[]{
						new DataValue(shop, Types.VARCHAR), 
						new DataValue(shop, Types.VARCHAR), 
						new DataValue(eId, Types.VARCHAR),
						new DataValue(pfNo, Types.VARCHAR), 
						new DataValue(item + 1 +"", Types.VARCHAR), 
						new DataValue(material_pluNo, Types.VARCHAR),
						new DataValue(punit, Types.VARCHAR),
						new DataValue(material_wUnit,  Types.VARCHAR), // status 0:新建
						new DataValue(String.format("%.2f", material_wqty), Types.VARCHAR),
						new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialKQty ), Types.VARCHAR), // 实报数量 = 应报数量 - 实存
//						new DataValue(String.format("%.2f", materialKQty ), Types.VARCHAR), // 实报数量 = 应报数量 - 实存
						new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
						new DataValue(minQty, Types.VARCHAR), 
						new DataValue(String.format("%.2f", Double.parseDouble(material_price)), Types.VARCHAR), 
						new DataValue(String.format("%.2f", amt), Types.VARCHAR),
						new DataValue(materialKQtyStr, Types.VARCHAR), // 应报数量（根据BOM关系推算出的原料数量，不经过最小量和倍量等计算）
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
					
					if(Check.Null(mainPluNo)){
						continue;
					}
					
//					String punitUdLength = "2";
//					
//					if(Check.Null(punitUdLength)){
//						punitUdLength = lv2.getPunitUdLength();
//					}
//					
//					String pUnit = lv2.getpUnit();
//					String sUnit = lv2.getsUnit();
//					

					String pUnit = "";
					String punitUdLength = "2";
					String sUnit = lv2.getsUnit();
					
					if(punitList != null && punitList.isEmpty() == false){
						
						Map<String, Object> conditionUnit = new HashMap<String, Object>(); //查詢條件
						conditionUnit.put("PLUNO", mainPluNo);		
						//调用过滤函数
						List<Map<String, Object>> getQHeader = MapDistinct.getWhereMap(punitList, conditionUnit, false);
						
						if(getQHeader != null && getQHeader.isEmpty() == false){
							pUnit = getQHeader.get(0).get("PUNIT").toString();
							punitUdLength = getQHeader.get(0).get("PUNITUDLENGTH").toString();
						}
						
					}
					
					
					// 成品的销售单位==》库存单位
					double sUnitRatio = 1;
//					List<Map<String, Object>> getQData_Ratio_SUnit = PosPub.getUnit_Ratio(dao, req.geteId(),
//							mainPluNo, sUnit); 
//						
//					if (getQData_Ratio_SUnit == null || getQData_Ratio_SUnit.isEmpty()) {
//						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + mainPluNo + " 找不到对应的 "+sUnit+" 到库存单位的换算关系");
//					}else{
//						sUnitRatio = Double.parseDouble(getQData_Ratio_SUnit.get(0).getOrDefault("UNIT_RATIO","1").toString());
//					}
					
					//成品的销售单位 和 要货单位是否相等，如果不相等，需要再查一下要货单位到库存单位的转换率
					double pUnitRatio = 1;
					if(!sUnit.equals(pUnit)){

//						List<Map<String, Object>> getQData_Ratio2 = PosPub.getUnit_Ratio(dao, req.geteId(),
//								mainPluNo, pUnit); 
//							
//						if (getQData_Ratio2 == null || getQData_Ratio2.isEmpty()) {
//							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + mainPluNo + " 找不到对应的 "+pUnit+" 到库存单位的换算关系");
//						}else{
//							pUnitRatio = Double.parseDouble(getQData_Ratio2.get(0).getOrDefault("UNIT_RATIO","1").toString());
//						}
					}else{
						pUnitRatio = sUnitRatio;
					}
					
					
					String[] columns2 = {
							"Shop", "OrganizationNO","eId","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
							"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
							"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
							"MAINPLU" , "BDATE" ,
							// 2019-10-28 增加以下字段，用于物料报单
							"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
							
					};
					DataValue[] insValue2 = null;
					String wunit = "";
					double punitRatio = 0 ;
//					List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio(dao, req.geteId(),
//							mainPluNo, pUnit); 
//					if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
//						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + mainPluNo + " 找不到对应的 "+pUnit+" 到库存单位 的换算关系");
//					}else{
//						wunit = getQData_Ratio.get(0).get("WUNIT").toString();
//						punitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
//					}
					
					double mainPQty =  Double.parseDouble(qty)* sUnitRatio / pUnitRatio; //销售单位 ==》库存单位 ==》 要货单位
					
					double wqty = mainPQty * punitRatio; //库存单位数量
					
					//根据SA孙红艳要求，数量保留两位小数，不再根据单位长度保留
//					String mainPQtyStr = String.format("%."+punitUdLength+"f", mainPQty);
					String mainPQtyStr = String.format("%.2f", mainPQty);

					//2020-05-21 SA孙红艳要求： 如果单位小数位数大于两位的，报单参考列数据的小数位数跟着单位的走
					int pUnitUdLengthInt = Integer.parseInt(punitUdLength);
					
					if(pUnitUdLengthInt > 2){
						mainPQtyStr = String.format("%."+punitUdLength+"f", mainPQty);
					}else{
						mainPQtyStr = String.format("%.2f", mainPQty);
					}
					
					
					// 插入主表数据
					insValue2 = new DataValue[]{
							new DataValue(shop, Types.VARCHAR), 
							new DataValue(shop, Types.VARCHAR), 
							new DataValue(eId, Types.VARCHAR),
							new DataValue(pfNo, Types.VARCHAR), 
							new DataValue(item + 1, Types.VARCHAR), 
							new DataValue(mainPluNo, Types.VARCHAR),
							new DataValue(pUnit, Types.VARCHAR),
							new DataValue(wunit,  Types.VARCHAR), //wunit 
							new DataValue(wqty+"", Types.VARCHAR), //WQTY
							new DataValue(String.format("%."+punitUdLength+"f", mainPQty), Types.VARCHAR), 
							new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
							new DataValue("0", Types.VARCHAR), 
							new DataValue(lv2.getPrice(), Types.VARCHAR), 
							new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
							new DataValue(mainPQtyStr, Types.VARCHAR), // 应报数量（千元用量）
							new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
							new DataValue("0", Types.VARCHAR), // req_wqty 参考库存量
							new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("", Types.VARCHAR),
							new DataValue(mainPluName + "("+qty+")", Types.VARCHAR),
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
			
		}else{
			
			//开始过滤没有BOM 的商品，这些也需要插入到 DCP_PORDER_FORECAST_MATERIAL 中
			int item = 0;
			for (level2Elm lv2 : pluDatas) {
				
				String mainPluNo = lv2.getPluNo();
				String mainPluName = lv2.getPluName();
				String qty = lv2.getQty(); //成品销售单位对应数量
				if(Check.Null(mainPluNo)){
					continue;
				}
				
//				String punitUdLength = "2";
//				
//				if(Check.Null(punitUdLength)){
//					punitUdLength = lv2.getPunitUdLength();
//				}
//				
//				String pUnit = lv2.getpUnit();
//				String sUnit = lv2.getsUnit();
				
				String pUnit = "";
				String punitUdLength = "2";
				String sUnit = lv2.getsUnit();
				
				if(punitList != null && punitList.isEmpty() == false){
					
					Map<String, Object> conditionUnit = new HashMap<String, Object>(); //查詢條件
					conditionUnit.put("PLUNO", mainPluNo);		
					//调用过滤函数
					List<Map<String, Object>> getQHeader = MapDistinct.getWhereMap(punitList, conditionUnit, false);
					
					if(getQHeader != null && getQHeader.isEmpty() == false){
						pUnit = getQHeader.get(0).get("PUNIT").toString();
						punitUdLength = getQHeader.get(0).get("PUNITUDLENGTH").toString();
					}
					
				}
				
				// 成品的销售单位==》库存单位
				double sUnitRatio = 1;
//				List<Map<String, Object>> getQData_Ratio_SUnit = PosPub.getUnit_Ratio(dao, req.geteId(),
//						mainPluNo, sUnit); 
//					
//				if (getQData_Ratio_SUnit == null || getQData_Ratio_SUnit.isEmpty()) {
//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + mainPluNo + " 找不到对应的 "+sUnit+" 到库存单位的换算关系");
//				}else{
//					sUnitRatio = Double.parseDouble(getQData_Ratio_SUnit.get(0).getOrDefault("UNIT_RATIO","1").toString());
//				}
//				
				//成品的销售单位 和 要货单位是否相等，如果不相等，需要再查一下要货单位到库存单位的转换率
				double pUnitRatio = 1;
				if(!sUnit.equals(pUnit)){

//					List<Map<String, Object>> getQData_Ratio2 = PosPub.getUnit_Ratio(dao, req.geteId(),
//							mainPluNo, pUnit); 
//						
//					if (getQData_Ratio2 == null || getQData_Ratio2.isEmpty()) {
//						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + mainPluNo + " 找不到对应的 "+pUnit+" 到库存单位的换算关系");
//					}else{
//						pUnitRatio = Double.parseDouble(getQData_Ratio2.get(0).getOrDefault("UNIT_RATIO","1").toString());
//					}
				}else{
					pUnitRatio = sUnitRatio;
				}
				
				String[] columns2 = {
						"Shop", "OrganizationNO","eId","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
						"WUNIT", "WQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
						"AMT" , "KQTY", "KADJQTY" , "REQ_WQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
						"MAINPLU" , "BDATE" ,
						// 2019-10-28 增加以下字段，用于物料报单
						"DBYQTY","YWQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
						
				};
				DataValue[] insValue2 = null;
				String wunit = "";
				double punitRatio = 0 ;
//				List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio(dao, req.geteId(),
//						mainPluNo, pUnit); 
//			
//				if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + mainPluNo + " 找不到对应的 "+pUnit+" 到库存单位 的换算关系");
//				}else{
//					wunit = getQData_Ratio.get(0).get("WUNIT").toString();
//					punitRatio = Double.parseDouble(getQData_Ratio.get(0).getOrDefault("UNIT_RATIO","1").toString());
//				}
				
				double mainPQty =  Double.parseDouble(qty)* sUnitRatio / pUnitRatio; //销售单位 ==》库存单位 ==》 要货单位
				
				double wqty = mainPQty * punitRatio; //库存单位数量
				
				//根据SA孙红艳要求，数量保留两位小数，不再根据单位长度保留
//				String mainPQtyStr = String.format("%."+punitUdLength+"f", mainPQty);
				String mainPQtyStr = String.format("%.2f", mainPQty);

				//2020-05-21 SA孙红艳要求： 如果单位小数位数大于两位的，报单参考列数据的小数位数跟着单位的走
				int pUnitUdLengthInt = Integer.parseInt(punitUdLength);
				
				if(pUnitUdLengthInt > 2){
					mainPQtyStr = String.format("%."+punitUdLength+"f", mainPQty);
				}else{
					mainPQtyStr = String.format("%.2f", mainPQty);
				}
				
				
				// 插入主表数据
				insValue2 = new DataValue[]{
						new DataValue(shop, Types.VARCHAR), 
						new DataValue(shop, Types.VARCHAR), 
						new DataValue(eId, Types.VARCHAR),
						new DataValue(pfNo, Types.VARCHAR), 
						new DataValue(item + 1, Types.VARCHAR), 
						new DataValue(mainPluNo, Types.VARCHAR),
						new DataValue(pUnit, Types.VARCHAR),
						new DataValue("",  Types.VARCHAR), //wunit 
						new DataValue(wqty+"", Types.VARCHAR), //WQTY
						new DataValue(String.format("%."+punitUdLength+"f", mainPQty), Types.VARCHAR), 
						new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
						new DataValue("0", Types.VARCHAR), 
						new DataValue(lv2.getPrice(), Types.VARCHAR), 
						new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
						new DataValue(mainPQtyStr, Types.VARCHAR), // 应报数量（千元用量）
						new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
						new DataValue("0", Types.VARCHAR), // req_wqty 参考库存量
						new DataValue(lv2.getkAdjAmt(), Types.VARCHAR),
						new DataValue("0", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue(mainPluName + "("+ qty +")", Types.VARCHAR),
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
		
		res.setDatas(new ArrayList<DCP_PFOrderCreateRes.level1Elm>());
		DCP_PFOrderCreateRes.level1Elm pfLev = res.new level1Elm();
		pfLev.setPfNo(pfNo);
		res.getDatas().add(pfLev);
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PFOrderCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PFOrderCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PFOrderCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PFOrderCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PFOrderCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderCreateReq>(){};
	}

	@Override
	protected DCP_PFOrderCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderCreateRes();
	}


	/**
	 * 生成单号
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	private String getPFNO(DCP_PFOrderCreateReq req) throws Exception{
		
		String PFNO = "";
		String sql = "";
		String eId = req.geteId();
		String shop = req.getShopId();
		String bDate = req.getRequest().getbDate(); //单据日期（营业日期），不必再查询， 前端能获取到
		
		bDate = bDate.replace("-", "");
		sql = "select MAX(PFNO) AS PFNO from DCP_porder_forecast where eId = '"+eId+"' and shop = '"+shop+"' and PFNO like '%%"+bDate+"%%'";
		List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
		if(getDatas.size() > 0 ){
			PFNO = (String) getDatas.get(0).get("PFNO");

			if (PFNO != null && PFNO.length() > 0) {
				long i;
				PFNO = PFNO.substring(4, PFNO.length());
				i = Long.parseLong(PFNO) + 1;
				PFNO = i + "";
				PFNO = "YGYH" + PFNO;
				
			} else {
				PFNO = "YGYH" + bDate + "00001";
			}
		
		}
		
		return PFNO;
		
	}
	
	/**
	 * 获取原料数据
	 * @param req
	 * @param pluNoStr
	 * @return
	 */
	protected String getBomDatas(DCP_PFOrderCreateReq req ,String pluNoStr){
		String eId = req.geteId();
		String shop = req.getShopId();
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
  + " from (select distinct a.pluno , a.unit as mainUnit ,  gn.pluName, a.baseqty, a.material_pluno, a.material_punit, a.material_baseqty   ,   "
  + " c.pluname as material_pluname, d.unit_name as material_punitname, a.bom_type, c.wunit as material_wunit, c.punit ,"
  + " e.unit_name as material_wunitname, "
  + " c.isbatch as material_isbatch, nvl(b1.price1, 0 ) as material_price  , nvl(SUM(f.qty) , '0')AS sQty "
  + " , nvl(gs.min_qty, 0 ) AS minQty, nvl(gs.max_qty , 99999) AS maxQty ,NVL( gs.mul_qty , 0 ) AS mulQty "
  + " , nvl(u.udlength,'2') pUnitUdLength  "
  + " from (select a.eId, a.pluno, a.unit, a.material_pluno, a.material_qty as material_baseqty, "
  + " a.material_unit as material_punit, a.qty as baseqty, a.bom_type "
  + " from DCP_bom a "
  + " left join DCP_bom_shop b"
  + " on a.eId = b.eId"
  + " and a.pluno = b.pluno " 
  + " and b.eId = '"+eId+"' "
  + " and b.material_bdate <= '"+sysDate+"' "
  + " and (b.material_edate >= '"+sysDate+"' or b.material_edate is null) "
  + " and b.organizationno = '"+shop+"' and b.cnfflg = 'Y' "
  + " and a.bom_type = b.bom_type "
  + " where a.eId = '"+eId+"' "
  + " and a.effdate <= '"+sysDate+"'" 
  + " and a.material_bdate <= '"+sysDate+"'"
  + " and (a.material_edate >= '"+sysDate+"' or a.material_edate is null)"
  + " and a.cnfflg = 'Y' "
  + " and a.bom_type = '0' "
  + " and b.pluno is null "
  + " and a.pluno in ("+pluNoStr+") "
  + " union all "
  + " select eId, pluno, unit, material_pluno, material_qty as material_baseqty, material_unit as material_punit, "
  + " qty as baseqty, bom_type "
  + " from DCP_bom_shop"
  + " where eId = '"+eId+"' "
  + " and material_bdate <= '"+sysDate+"' "
  + " and (material_edate >= '"+sysDate+"' or material_edate is null) "
  + " and organizationno = '"+shop+"'"
  + " and cnfflg = 'Y'"
  + " and bom_type = '0' "
  + " and pluno in ("+pluNoStr+")) a "
  + " inner join DCP_goods c "
  + " on a.eId = c.eId "
  + " and a.material_pluno = c.pluno "
  + " and c.cnfflg = 'Y' "
  + " LEFT JOIN DCP_goods gn ON a.eId = gn.eId AND a.pluNO = gn.pluNo "
  
  // 2019-12-19 增加查询商品适用门店， 查商品最大、最小、要货倍量
  + " LEFT JOIN DCP_goods_shop gs ON a.eId = gs.eId AND  a.material_pluNo = gs.pluNo  "
  + " AND nvl(gs.fpod, 'Y' ) = 'Y' AND gs.cnfflg='Y' AND gs.organizationNO = '"+req.getShopId()+"'  "
  + " LEFT JOIN DCP_unit u ON c.punit = u.unit AND c.eId = u.eId AND u.cnfflg = 'Y' "
  
  + " left join (select * "
  + " 		from (select eId, pluno, unit, price1, idx, row_number() over(partition by eId, pluno, unit order by idx) rn "
  + " 			from (select eId, pluno, unit, price1, 1 as idx "
  + " 						from DCP_price_shop "
			                    + " where eId = '"+eId+"' "
			                    + " and organizationno = '"+shop+"' "
			                    + " and effdate <= '"+sysDate+"'" 
			                    + " and (ledate >= '"+sysDate+"' or ledate is null) "
			                    + " and cnfflg = 'Y' "
			                    + " union"
			                    + " select eId, pluno, unit, price1, 2 as idx "
			                    + " from DCP_price "
			                    + " where eId = '"+eId+"'"
			                    + " and effdate <= '"+sysDate+"'"
			                    + " and (ledate >= '"+sysDate+"' or  ledate is null) "
			                    + " and cnfflg = 'Y')) "
			              + " where rn = 1) b1 "
			     + " on a.eId = b1.eId "
			     + " and a.material_pluno = b1.pluno "
			     + " and (a.material_punit = b1.unit) " 
			     + " left join DCP_unit_lang d "
			     + " on d.eId = a.eId " 
			     + " and a.material_punit = d.unit " 
			     + " and d.lang_type = '"+langType+"' "
			     + " and d.cnfflg = 'Y' "
			     + " left join DCP_unit_lang e "
			     + " on e.eId = a.eId "
			     + " and c.wunit = e.unit "
			     + " and e.lang_type = '"+langType+"' "
			     + " and e.cnfflg = 'Y'  "
			     + " LEFT JOIN DCP_stock_day_static f ON a.eId = f.eId AND a.material_pluno = f.pluNo "

			     + " AND f.eDate = '"+preDate+"' AND f.organizationNo = '"+shop+"' "
			     + " LEFT JOIN DCP_warehouse g ON f.eId=g.eId and f.warehouse=g.warehouse AND g.WAREHOUSE_TYPE<>'3'    "
			     + " GROUP BY  a.pluno , gn.pluName, a.Unit , a.baseqty, a.material_pluno, a.material_punit, a.material_baseqty   ,   "
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
