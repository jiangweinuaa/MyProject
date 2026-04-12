package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PFOrderOldUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PFOrderOldUpdateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_PFOrderOldUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PFOrderOldUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 营业预估更新
 * 更新服务，可能会重新计算日期期间内的千元用量，查出来的商品就会发生变化
 * 所以，应删除单身和原料， 重新插入新查出来的商品和原料
 * @author yuanyy
 *
 */
public class DCP_PFOrderOldUpdate extends SPosAdvanceService<DCP_PFOrderOldUpdateReq, DCP_PFOrderOldUpdateRes> {

	Logger logger = LogManager.getLogger(this.getClass());
	@Override
	protected void processDUID(DCP_PFOrderOldUpdateReq req, DCP_PFOrderOldUpdateRes res) throws Exception {
		try {
			// TODO Auto-generated method stub
			String eId = req.geteId();
			String shopId = req.getShopId();
			String bDate = req.getRequest().getbDate();
			String memo = req.getRequest().getMemo();
			String pfId = req.getRequest().getPfId();
			String rDate = req.getRequest().getrDate();
			String predictAmt = req.getRequest().getPredictAmt();
			String beginDate = req.getRequest().getBeginDate();
			String endDate = req.getRequest().getEndDate();
			String avgSaleAmt = req.getRequest().getAvgSaleAmt();
			String modifRatio = req.getRequest().getModifRatio();
			String pfWeather = req.getRequest().getPfWeather();
			String pfDay = req.getRequest().getPfDay();
			String pfHoliday = req.getRequest().getPfHoliday();
			String pfMatter = req.getRequest().getPfMatter();
			String PFNO = req.getRequest().getPfNo();
			
			String pfWeatherNo = req.getRequest().getPfWeatherNo();
			String pfHolidayNo = req.getRequest().getPfHolidayNo();
			String pfMatterNo = req.getRequest().getPfMatterNo();
			String companyId = req.getBELFIRM();
			if(req.getRequest().getPfMatterNo() != null && req.getRequest().getPfMatterNo().length() > 0){
				pfMatterNo = req.getRequest().getPfMatterNo();
			}
			else{
				pfMatterNo = req.getRequest().getPfMatter();
			}
			
			String updateBy = req.getOpNO();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
			String updateDate = df.format(cal.getTime());
			df=new SimpleDateFormat("HHmmss");
			String updateTime = df.format(cal.getTime());
			// 先将 detail 和 material 表中的数据删除， 再重新插入
			DelBean db1 = new DelBean("DCP_PORDER_FORECAST_MATERIAL");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db1.addCondition("PFNO", new DataValue(PFNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1)); // 
			
			DelBean db2 = new DelBean("DCP_PORDER_FORECAST_DETAIL");
			db2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db2.addCondition("PFNO", new DataValue(PFNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2)); // 
			
			// pluNoStr 查BOM的时候用 
			String pluNoStr = "' '"; 
			
			float totPqty = 0;
			float totAmt = 0;
			float totCqty = 0;
			float wqty = 0;
			BigDecimal unitRatio;
			float unitRatio1 = 0;
			
			//新增單身 (多筆)
			List<level1Elm> jsonDatas = req.getRequest().getDatas();
			
			// 将成品放入集合中， 便于下面过滤原料
			List<Map<String, Object>> mainDatas = new ArrayList<>();
			
			for (level1Elm par : jsonDatas) {
				
				Map<String, Object> main = new HashMap<String, Object>();
				
				//******************** 添加完 子表（DCP_PORDER_FORECAST_DETAIL）之后， 在原料表（DCP_PORDER_FORECAST_MATERIAL）中添加数据 ************8
				String[] columnsName = {
						"EID", "SHOPID", "ORGANIZATIONNO", "PFNO", "ITEM", "PLUNO", "PUNIT", "PQTY", "BASEUNIT", 
						"BASEQTY", "PRICE", "AMT", "REQBASEQTY","KQTY", //13
						"KADJQTY","REF_AMT","ADJ_AMT","MEMO", "ISCLEAR", "LASTSALETIME" , "PRESALEQTY", "TRUEQTY",
						"AVGQTY","AVGPRICE","AVGAMT"
				};
				int insColCt = 0;	
				DataValue[] columnsVal = new DataValue[columnsName.length];

				String pluNO = "";
				String punit = "";
				String wunit = "";
				double baseQty = 0;
				for (int i = 0; i < columnsVal.length; i++) { 
					String keyVal = null;
					switch (i) {
					case 0:
						keyVal = eId;	 
						totCqty = totCqty + 1;
						break;
					case 1:
						keyVal = shopId;
						break;
					case 2:
						keyVal = shopId;
						break;
					case 3:  
						keyVal = PFNO;
						break;
					case 4:  
						keyVal = par.getItem();
						break;
					case 5:  
						keyVal = par.getPluNo();
						pluNO = keyVal;
						pluNoStr = pluNoStr + " ,'"+keyVal+"'";
						main.put("pluNo", keyVal);
						break;
					case 6:  
						keyVal = par.getpUnit();
						punit = keyVal;
						break;			
					case 7:  
						keyVal = par.getQty();
						totPqty = totPqty + Float.parseFloat(keyVal);
						main.put("qty", keyVal);
                        Map<String, Object> map_base=PosPub.getBaseQty(this.dao, req.geteId(),par.getPluNo(),par.getpUnit(), par.getQty());
                        if(PosPub.isNumericTypeMinus(map_base.get("baseQty").toString()))
                        {
                        	baseQty= Double.valueOf(map_base.get("baseQty").toString());
                        }else
                        {
                        	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 [" + pluNO +"],单位["+par.getpUnit()+"]找不到对应的换算单位");
                        }
						main.put("wqty", baseQty);
						break;
					case 8:  
						keyVal = wunit;
						break;
					case 9:  
						keyVal = String.format("%.2f", wqty);
//						keyVal = String.valueOf(wqty);     
						break;
					case 10:  
						keyVal = par.getPrice();    //price
						if(par.getPrice()==null || par.getPrice().toString().isEmpty()){
							keyVal = "0";
						}
						break;
					case 11:  
						keyVal = par.getkAmt();
						totAmt = totAmt + Float.parseFloat(keyVal);
						break;
					case 12:  
						keyVal = par.getReqWQty();
						break;
					case 13:  
						keyVal = par.getkQty();
						break;
					case 14:  
						keyVal = par.getkAdjQty();
						if(par.getkAdjQty()==null || par.getkAdjQty().toString().isEmpty()){
							keyVal = "0";
						}
						break;	
					case 15:  
						keyVal = par.getkAmt();
						if(par.getkAmt()==null || par.getkAmt().toString().isEmpty()){
							keyVal = "0";
						}
						break;	
					case 16:  
						keyVal = par.getkAdjAmt();
						if(par.getkAdjAmt()==null || par.getkAdjAmt().toString().isEmpty()){
							keyVal = "0";
						}
						break;	
					case 17:  
						keyVal = par.getMemo();
						break;	
					case 18:  
						keyVal = par.getIsClear();
						break;	
						
					case 19:  
						keyVal = par.getLastSaleTime();
						break;
						
					case 20:  
						keyVal = par.getPreSaleQty();
						break;	
						
					case 21:  
						keyVal = par.getTrueQty(); //实备数量默认等于实报数量
						break;	
						
					case 22:  
						String avgQty = "0";
						if(!Check.Null(par.getAvgQty())){
							avgQty = par.getAvgQty();
						}
						keyVal = avgQty;
						
						break;	
					case 23:  
						String avgPrice = "0";
						if(!Check.Null(par.getAvgPrice())){
							avgPrice = par.getAvgPrice();
						}
						keyVal = avgPrice;
						break;	
					case 24:  
						
						String avgAmt = "0";
						if(!Check.Null(par.getAvgAmt())){
							avgAmt = par.getAvgAmt();
						}
						keyVal = avgAmt;
						break;	
						
					default:
						break;
					}

					if (keyVal != null) {
						insColCt++;
//					if (i == 2 ){
//						columnsVal[i] = new DataValue(keyVal, Types.INTEGER);	
//					}else if (i == 5 || i == 7 || i == 8 || i == 9 || i == 10|| i == 14|| i == 15|| i == 16|| i == 17|| i == 18 || i == 19 || i == 20|| i == 21  ){
//						columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
//					}else{
							columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
//					}
					} else {
						columnsVal[i] = null;
					}
				}
				
				String[] columns2 = new String[insColCt];
				DataValue[] insValue2 = new DataValue[insColCt];
				// 依照傳入參數組譯要insert的欄位與數值；
				insColCt = 0;

				for (int i=0;i<columnsVal.length;i++){
					if (columnsVal[i] != null){
						columns2[insColCt] = columnsName[i];
						insValue2[insColCt] = columnsVal[i];
						insColCt ++;
						if (insColCt >= insValue2.length) break;
					}
				}

				InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_DETAIL", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2));
				
				mainDatas.add(main);
				
			}
			
			UptBean ub1 = new UptBean("DCP_PORDER_FORECAST");		
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("SHOPID",new DataValue(shopId, Types.VARCHAR));
			ub1.addCondition("PFNO",new DataValue(PFNO, Types.VARCHAR));
			
			ub1.addUpdateValue("RDATE",new DataValue(rDate, Types.VARCHAR));
			ub1.addUpdateValue("MEMO",new DataValue(memo, Types.VARCHAR));
			ub1.addUpdateValue("MODIFYBY",new DataValue(updateBy, Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_DATE",new DataValue(updateDate, Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_TIME",new DataValue(updateTime, Types.VARCHAR));
			ub1.addUpdateValue("TOT_PQTY",new DataValue(String.format("%.2f", totPqty), Types.VARCHAR));
			ub1.addUpdateValue("TOT_AMT",new DataValue(String.format("%.2f", totAmt), Types.VARCHAR));
			ub1.addUpdateValue("TOT_CQTY",new DataValue(totCqty, Types.VARCHAR));
			ub1.addUpdateValue("PREDICTAMT",new DataValue(String.format("%.2f", Double.parseDouble(predictAmt)), Types.VARCHAR));
			ub1.addUpdateValue("BEGINDATE",new DataValue(beginDate, Types.VARCHAR));
			ub1.addUpdateValue("ENDDATE",new DataValue(endDate, Types.VARCHAR));
			ub1.addUpdateValue("AVGSALEAMT",new DataValue(String.format("%.2f", Double.parseDouble(avgSaleAmt)), Types.VARCHAR));
			ub1.addUpdateValue("MODIFRATIO",new DataValue(modifRatio, Types.VARCHAR));
			ub1.addUpdateValue("PFWEATHER",new DataValue(pfWeatherNo, Types.VARCHAR));
			ub1.addUpdateValue("PFDAY",new DataValue(pfDay, Types.VARCHAR));
			ub1.addUpdateValue("PFHOLIDAY",new DataValue(pfHolidayNo, Types.VARCHAR));
			ub1.addUpdateValue("PFMATTER",new DataValue(pfMatterNo, Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(ub1));	

			//*******成品根据BOM查到商品原料，插入第三张原料表（DCP_PORDER_FORECAST_MATERIAL）
			String bomSql = this.getBomDatas(req, pluNoStr);
			List<Map<String, Object>> bomDatas = this.doQueryData(bomSql, null);
            //商品取价计算
            List<Map<String, Object>> QpriceList =new ArrayList<Map<String,Object>>();
            MyCommon mcprice = new MyCommon();
            if(!bomDatas.isEmpty())
            {
            	for (int i = 0; i < bomDatas.size(); i++) {
            		 Map<String, Object> mapprice=new HashMap<String, Object>();
            		 mapprice.put("PLUNO", bomDatas.get(i).get("MATERIAL_PLUNO"));
            		 mapprice.put("PUNIT", bomDatas.get(i).get("MATERIAL_PUNIT"));
            		 mapprice.put("BASEUNIT", bomDatas.get(i).get("MATERIAL_BASEUNIT"));
            		 mapprice.put("UNITRATIO", bomDatas.get(i).get("MATERIAL_UNITRATIO")); 
            		 QpriceList.add(mapprice);
            	}
            }
            List<Map<String, Object>> getPrice = mcprice.getSalePrice_distriPrice(dao, eId, companyId, shopId, QpriceList,companyId);
			
			
			if(!bomDatas.isEmpty()){
				
				//第一步：过滤出所有的原料
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("MATERIAL_PLUNO", true);		
				//调用过滤函数
				List<Map<String, Object>> materilDatas=MapDistinct.getMap(bomDatas, condition);
				
				for (int i = 0; i < materilDatas.size(); i++) {
					
					String material_pluNo = materilDatas.get(i).get("MATERIAL_PLUNO").toString();
					String material_pUnit = materilDatas.get(i).get("MATERIAL_PUNIT").toString(); //原料配方单位
					String material_wUnit = materilDatas.get(i).get("MATERIAL_WUNIT").toString(); //原料库存单位
					String punit = materilDatas.get(i).get("PUNIT").toString(); //原料要货单位
					String pUnitUdLengthStr = materilDatas.get(i).get("PUNITUDLENGTH").toString(); //原料要货单位保留位数
					int pUnitUdLength = Integer.parseInt(pUnitUdLengthStr);
					
					double bomUnitRatio = 1; // 配方单位到库存单位的单位转换率
					double pUnitRatio = 1; // 要货单位到库存单位的单位转换率					
					bomUnitRatio=Double.valueOf(materilDatas.get(i).get("MATERIAL_UNITRATIO").toString());
					pUnitRatio  =Double.valueOf(materilDatas.get(i).get("MATERIAL_PUNITRATIO").toString());
					String sQty = materilDatas.get(i).getOrDefault("SQTY","0").toString();
					//String material_price = materilDatas.get(i).getOrDefault("MATERIAL_PRICE", "0").toString();
                    Map<String, Object> condiV= new HashMap<>();
                    condiV.put("PLUNO",material_pluNo);
                    condiV.put("PUNIT",punit);
                    List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPrice, condiV, false);
                    condiV.clear();
                    String material_price="0";
                    String material_distriPrice="0";
                    if(priceList!=null && priceList.size()>0 ) {
                    	material_price=priceList.get(0).get("PRICE").toString();
                    	material_distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
                    }
					
					
					//					double minQty = 0; // 最小要货量（每个成品单位1对应的原料数 相加得到） 不能这么算，不合逻辑
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
							
//							minQty = Float.parseFloat(material_baseQty) / Float.parseFloat(baseQty); 
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
							
//							
//							materialPqty = MyCommon.getNumberThree(dArr, materialPqty);
							MyCommon mc = new MyCommon();
							materialPqty = mc.getNumberThree(dArr, materialPqty);
							mc = null;
							
						}
						
						
						if(materialPqty > Double.parseDouble(maxQty)){ //如果预估量 > 最大要货量， 预估量==最大要货量即可
							materialPqty = Double.parseDouble(maxQty);
						}
						
					}
					
//					logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"原料："+material_pluNo +" pUnit:"+ punit + " material_punit:"+ material_pUnit + " material_wunit:"+ mwunit + "  pqty:"+pqty +"  materialPQty:"+ materialPqty  );
					
					if (unitRatio2 > 0){
						reqWQty = Float.parseFloat(sQty) * pUnitRatio; // sQty 是库存单位对应的数量， 需要转换为要货单位对应的数量
					}
					else{
						reqWQty = 0 ;  // 防止出现查不到换算率、 或者  换算率设置为 0  这种情况，属于资料不正确
					}
					
					String[] columns2 = {
							"SHOPID", "OrganizationNO","EID","PFNO","ITEM", "MATERIAL_PLUNO",   "PUNIT", 
							"BASEUNIT", "BASEQTY", "PQTY", "MAX_QTY", "MIN_QTY", "PRICE", 
							"AMT" , "KQTY", "KADJQTY" , "REQ_BASEQTY", "REF_AMT" , "ADJ_AMT" , "MEMO",
							"MAINPLU" , "BDATE" ,
							// 2019-10-28 增加以下字段，用于物料报单
							"DBYQTY","YBASEQTY","RQTY","UQTY","DQTY","TQTY","STATUS"
							
					};
					DataValue[] insValue2 = null;
					
					// 插入主表数据
					insValue2 = new DataValue[]{
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR), 
							new DataValue(eId, Types.VARCHAR),
							new DataValue(PFNO, Types.VARCHAR), 
							new DataValue(i+1, Types.VARCHAR), 
							new DataValue(material_pluNo, Types.VARCHAR),
							new DataValue(punit, Types.VARCHAR),
							new DataValue(material_wUnit,  Types.VARCHAR), // status 0:新建
							new DataValue(String.format("%.2f", material_wqty), Types.VARCHAR),
//							new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialPqty - reqWQty), Types.VARCHAR),  // 实报数量 = 应报数量 - 实存
							new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialPqty ), Types.VARCHAR),  
							new DataValue("999999", Types.VARCHAR), //最大最小要货量， 给默认值
							new DataValue(minQty, Types.VARCHAR), 
//						new DataValue(material_price, Types.VARCHAR), 
							new DataValue(String.format("%.2f", Double.parseDouble(material_price)), Types.VARCHAR), 
							new DataValue(String.format("%.2f", amt), Types.VARCHAR),
							new DataValue(String.format("%."+pUnitUdLengthStr+"f", materialKQty), Types.VARCHAR), // 应报数量（千元用量）
//						new DataValue(pqty, Types.VARCHAR),
							new DataValue("0", Types.VARCHAR), // 默认给 0 ，调整量 和 调整金额都默认给0， 以后如果客户需要添加试菜/员工餐，这个就能用到
							new DataValue(String.format("%.2f", reqWQty), Types.VARCHAR), // req_wqty 参考库存量
							new DataValue(String.format("%.2f", amt), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue(memo, Types.VARCHAR),
							new DataValue(mainPluStr, Types.VARCHAR),
							new DataValue(bDate, Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue("1", Types.VARCHAR)
							
					};

					InsBean ib2 = new InsBean("DCP_PORDER_FORECAST_MATERIAL", columns2);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
					
				}
				
			}
			
			this.doExecuteDataToDB();	
//			res.setSuccess(true);
		} catch (Exception e) {

			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
//			res.setSuccess(false);
//			res.setServiceStatus("200");
//			res.setServiceDescription("服务执行失败");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PFOrderOldUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PFOrderOldUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PFOrderOldUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PFOrderOldUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String pfId =req.getRequest().getPfId();
		String pfNo = req.getRequest().getPfNo();
		String bDate = req.getRequest().getbDate();
		String rDate = req.getRequest().getrDate();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();

		if (Check.Null(pfId)) 
		{
			errMsg.append("单据唯一标识符不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(pfNo)) 
		{
			errMsg.append("单据编号不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(bDate)) 
		{
			errMsg.append("单据日期不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(rDate)) 
		{
			errMsg.append("需求日期不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(beginDate)) 
		{
			errMsg.append("计算千元用量开始日期不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(endDate)) 
		{
			errMsg.append("计算千元用量结束日期不可为空值, ");
			isFail = true;
		} 

		List<level1Elm> jsonDatas=req.getRequest().getDatas();

		for (level1Elm par : jsonDatas) 
		{			
			if (Check.Null(par.getItem())) 
			{
				errMsg.append("项次编码不可为空值, ");
				isFail = true;
			}	
			
			if (Check.Null(par.getPluNo())) 
			{
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
			}
			if (Check.Null(par.getpUnit())) 
			{
				errMsg.append("商品单位不可为空值, ");
				isFail = true;
			}
			
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PFOrderOldUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderOldUpdateReq>(){};
	}

	@Override
	protected DCP_PFOrderOldUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderOldUpdateRes();
	}
	
	/**
	 * 获取原料数据
	 * @param req
	 * @param pluNoStr
	 * @return
	 */
	protected String getBomDatas(DCP_PFOrderOldUpdateReq req ,String pluNoStr){
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
  + " from (select distinct a.pluno,a.baseqty, a.material_pluno, a.material_unit, a.material_baseqty   ,   "
  + " c.wunit as material_wunit, c.punit ,"
  + " c.isbatch as material_isbatch "
  + " , nvl(SUM(f.qty) , '0')AS sQty "
  + " , nvl(gs.minqty, 0 ) AS minQty, nvl(gs.maxqty , 99999) AS maxQty ,NVL( gs.mulqty , 0 ) AS mulQty "
  + " , nvl(u.udlength,'2') pUnitUdLength "
  + " , c.BASEUNIT,d.unitratio,cm.BASEUNIT AS material_baseunit,e.unitratio as material_unitratio "
  + " , cm.PUNIT as material_punit,f.unitratio as  material_punitratio ,cl.PLU_NAME AS PLUNAME "
  + " from ("
  + " select a.EID,a.pluno,a.mulqty,a.unit,c.material_pluno,c.material_unit as material_unit,c.material_qty as material_baseqty,c.qty,c.loss_rate,c.isbuckle,c.isreplace,c.sortid,c.qty as baseqty "
  + " from ( select a.eid,a.bomno,a.pluno,a.mulqty,a.unit,row_number() over (partition by a.pluno order by effdate desc) as rn from dcp_bom a "
  + " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"' "
  + " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and status='100' and bomtype = '0' "
  + " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null)) AND a.pluno in ("+pluNoStr+") "
  + " )a "
  + " inner join dcp_bom_material c on a.bomno=c.bomno and c.eid='"+eId+"' and trunc(c.material_bdate)<=trunc(sysdate) and trunc(material_edate)>=trunc(sysdate) "
  + " where a.rn=1 ) a "
  + " inner join DCP_GOODS c  on a.EID = c.EID  and a.pluno = c.pluno  and c.status='100'  "
  + " left  join DCP_GOODS_LANG cl on a.EID = cl.EID  and a.pluno = cl.pluno  and cl.lang_type='"+req.getLangType()+"' "
  + " inner join DCP_GOODS cm  on a.EID = cm.EID  and a.material_pluno = cm.pluno  and cm.status='100' " 
  + " inner join dcp_goods_unit d on a.eid=d.eid and d.pluno=a.pluno and d.ounit=a.unit " 
  + " inner join dcp_goods_unit e on a.eid=e.eid and e.pluno=a.material_pluno and e.ounit=a.material_unit "
  + " inner join dcp_goods_unit f on f.eid=cm.eid and f.pluno=cm.pluno and f.ounit=cm.punit "
  // 2019-12-19 增加查询商品适用门店， 查商品最大、最小、要货倍量
+ " LEFT JOIN ( select b.* from dcp_goodstemplate a inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
+ " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')) gs ON a.EID = gs.EID AND  a.material_pluNo = gs.pluNo   "
  + " LEFT JOIN DCP_UNIT u ON c.punit = u.unit AND c.EID = u.EID AND u.status='100' "
			     + " LEFT JOIN DCP_stock_day_static f ON a.EID = f.EID AND a.material_pluno = f.pluNo "
			     + " AND f.eDate = '"+preDate+"' AND f.organizationNo = '"+shopId+"' "
			     + " LEFT JOIN DCP_WAREHOUSE g ON f.EID=g.EID and f.warehouse=g.warehouse AND g.WAREHOUSE_TYPE<>'3'    "
			     + " GROUP BY  a.pluno , a.baseqty, a.material_pluno, a.material_unit, a.material_baseqty   ,   "
			     + "   c.wunit , "
			     + " c.isbatch "
			     + ", c.punit  "
			     + " ,  gs.minqty , gs.maxqty , gs.mulqty , u.udlength,c.BASEUNIT,d.unitratio,cm.BASEUNIT ,e.unitratio,cm.punit,f.unitratio,cl.PLU_NAME  ) dtl  ");
			sql = sqlbuf.toString();			
			return sql;
			
		} catch (Exception e) {

			return "";
		}
	}
	
	
	
	
	
}
