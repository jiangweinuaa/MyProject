package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class GoodsComplete extends InitJob {
	static boolean bRun = false;   // 标记此服务是否正在执行中
	public GoodsComplete(){
	}
	
	public String doExe() {
		//返回信息
		String sReturnInfo="";
		try {
			loger.info("\r\n*********倒扣料***完工入库GoodsCompleted定时开始:************\r\n");
			// 此服务是否正在执行中
			if (bRun) {
				loger.info("\r\n*********完工入库GoodsComplete正在执行中,本次调用取消:************\r\n");
				sReturnInfo="完工入库GoodsComplete正在执行中,本次调用取消:！";
				return sReturnInfo;
			}
			
			bRun = true;
			
			String sdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String stime = new SimpleDateFormat("HHmmss").format(new Date());
			
			//String sqlshop = "select * from dcp_org where org_form='2' and status='100' order by eid,organizationno ";
			String sqlshop = "select A.EID,A.ORGANIZATIONNO,A.BELFIRM,A.OUT_COST_WAREHOUSE,A.IN_COST_WAREHOUSE ,B.WAREHOUSE from dcp_org A LEFT JOIN DCP_WAREHOUSE B ON A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO "
					+ " where  A.ORG_FORM='2' AND A.STATUS=100 order by A.EID,A.organizationno,B.WAREHOUSE ";
			List<Map<String, Object>> slshop = this.doQueryData(sqlshop, null);
			
			int i_count=0;
			if(slshop!=null) {
				i_count=slshop.size();
			}
			
			loger.info("\r\n*********倒扣料***完工入库GoodsCompleted 总门店仓库数： "+i_count+"个************\r\n");
			int iiShop=0;
			for (Map<String, Object> shopdetail : slshop) {
				boolean isError=false; //记录是否有异常，用于数据库保存判断
				iiShop++;
				// 这里重新定义listdate用于一次事务执行
				List<DataProcessBean> data = new ArrayList<>();
				String eId = shopdetail.get("EID").toString();
				String shopId = shopdetail.get("ORGANIZATIONNO").toString();
				String warehouseId=shopdetail.get("WAREHOUSE").toString();//入库仓库
				String companyId = shopdetail.get("BELFIRM").toString();
				
				StringBuffer error_msg = new StringBuffer();
				///处理单价和金额小数位数  BY JZMA 20200401   
				String amtLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "amtLength");
				if (Check.Null(amtLength)||!PosPub.isNumeric(amtLength)) {
					amtLength="2";
				}
				int amtLengthInt = Integer.parseInt(amtLength);
				
				String distriAmtLength = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "distriAmtLength");
				if (Check.Null(distriAmtLength)||!PosPub.isNumeric(distriAmtLength)) {
					distriAmtLength="2";
				}
				int distriAmtLengthInt = Integer.parseInt(distriAmtLength);
				
				// 这里还要get一下入库和出库成本仓
				//String warehouse = shopdetail.get("IN_COST_WAREHOUSE").toString();
				String materialWarehouse = shopdetail.get("OUT_COST_WAREHOUSE").toString();
				
				//【ID1026974】【菲尔雪3.0】公司环境，门店开启了多仓，设置了加工模板，商品自动完工入库，收货仓库和扣料仓库不正确。
				// 要和2.0逻辑城市花园逻辑一致  by jinzma 20220704 以下注释
				
				/*String sql=" select materialwarehouse from dcp_ptemplate a1 "
						+ " inner join dcp_ptemplate_shop a2 on a1.eid=a2.eid and a1.ptemplateno=a2.ptemplateno "
						+ " and a1.doc_type=a2.doc_type and a1.status='100' and a2.status='100' "
						+ " where a1.eid='"+eId+"' and a1.doc_type='2' and a2.shopid='"+shopId+"' order by a1.tran_time desc ";
				
				List<Map<String, Object>> getMaterialWarehouse = this.doQueryData(sql, null);
				if (getMaterialWarehouse !=null && !getMaterialWarehouse.isEmpty()) {
					if (!Check.Null(getMaterialWarehouse.get(0).get("MATERIALWAREHOUSE").toString())) {
						materialWarehouse=getMaterialWarehouse.get(0).get("MATERIALWAREHOUSE").toString();
					}
				}*/
				
				//判断仓库不能为空格或空  BY JZMA 20191118
				if (Check.Null(materialWarehouse)||materialWarehouse.equals(" ")) {
					loger.error("\r\n" + "倒扣料***完工入库GoodsCompleted：门店编号:"+ shopId + "原料出货仓为空" + "\r\n");
					error_msg.append("倒扣料***完工入库GoodsCompleted：门店编号:"+ shopId + "原料出货仓为空"+"\r\n");
					try {
						InsertWSLOG.insert_JOBLOG(eId,shopId,"GoodsComplete","自动完工入库",error_msg.toString());
					} catch (Exception ignored) {
					}
					continue;  //下个门店
				}
				
				//营业日期
				String accountDate=PosPub.getAccountDate_SMS(StaticInfo.dao, eId, shopId);
				//启用批号管理时，查询所有原料的库存
				String isBatchNO = PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "Is_BatchNO");
				if (Check.Null(isBatchNO)) {
					isBatchNO = "N";
				}
				
				// 生成完工入库单号
				String pStockinNO = this.getPStockInNO(eId, shopId, accountDate);
				
				//这里只查询自动倒扣标记为DCP_GOODS_SHOP.IS_AUTO_SUBTRACT=Y的商品
				String sPstockinWGRK = this.getpstockinSQL(eId, companyId, shopId,warehouseId);
				loger.info("\r\n" + "倒扣料***完工入库GoodsCompleted：EID="+eId+" SHOPID="+shopId+" WAREHOUSEID="+warehouseId+" sPstockinWGRK 查询开始\r\n SQL="+sPstockinWGRK);
				List<Map<String, Object>> getpstockinWGRK = this.doQueryData(sPstockinWGRK, null);
				loger.info("\r\n" + "倒扣料***完工入库GoodsCompleted：EID="+eId+"SHOPID="+shopId+" WAREHOUSEID="+warehouseId+" sPstockinWGRK 查询结束\r\n");
				try {
					//是否存在倒扣料的商品
					if (getpstockinWGRK != null && !getpstockinWGRK.isEmpty()) {
						String isMaterialReplace=PosPub.getPARA_SMS(StaticInfo.dao, eId, shopId, "MaterialReplace");
						if(isMaterialReplace==null||isMaterialReplace.isEmpty())
						{
							isMaterialReplace="N";
						}
						//isMaterialReplace="N";
						if (isMaterialReplace.equals("Y"))
						{
							String out_cost_warehouse = shopdetail.get("OUT_COST_WAREHOUSE").toString();
							if(!PosPub.GoodsMaterialReplace(StaticInfo.dao, eId, shopId, getpstockinWGRK,
									//	"PLUNO","PUNIT" ,"WQTY","WUNIT","UNIT_RATIO","MATERIAL_PLUNO","MATERIAL_UNIT","MATERIAL_QTY","MATERIAL_WUNIT",
									"PLUNO","PUNIT" ,"BASEQTY","BASEUNIT","UNITRATIO","MATERIAL_PLUNO","MATERIAL_PUNIT","MATERIAL_PQTY","MATERIAL_BASEUNIT",
									"MATERIAL_UNITRATIO","MATERIALWAREHOUSE",out_cost_warehouse))
							{
								error_msg.append("门店编号:"+ shopId +"替代料处理失败" + "\r\n");
								isError=true;
							}
						}
						BigDecimal totPqty = new BigDecimal("0");
						BigDecimal totAmt = new BigDecimal("0");
						BigDecimal totDistriAmt = new BigDecimal("0");
						int totCqty = 0;
						
						//处理成品零售价和配送价
						List<Map<String, Object>> plus = new ArrayList<>();
						Map<String, Boolean> condition = new HashMap<>(); //查詢條件
						condition.put("PLUNO", true);
						List<Map<String, Object>> getQPlu=MapDistinct.getMap(getpstockinWGRK, condition);
						for (Map<String, Object> onePlu :getQPlu ) {
							Map<String, Object> plu = new HashMap<>();
							plu.put("PLUNO", onePlu.get("PLUNO").toString());
							plu.put("PUNIT", onePlu.get("PUNIT").toString());
							plu.put("BASEUNIT", onePlu.get("BASEUNIT").toString());
							plu.put("UNITRATIO", onePlu.get("UNITRATIO").toString());
							plus.add(plu);
						}
						MyCommon mc = new MyCommon();
						List<Map<String, Object>> getPluPrice = mc.getSalePrice_distriPrice(StaticInfo.dao,eId,companyId, shopId,plus,companyId);
						
						//处理原料零售价和配送价
						List<Map<String, Object>> materialPlus = new ArrayList<>();
						condition.clear();
						condition.put("MATERIAL_PLUNO", true);
						List<Map<String, Object>> getQMaterialPlu=MapDistinct.getMap(getpstockinWGRK, condition);
						for (Map<String, Object> oneMaterialPlu :getQMaterialPlu ) {
							Map<String, Object> materialPlu = new HashMap<>();
							materialPlu.put("PLUNO", oneMaterialPlu.get("MATERIAL_PLUNO").toString());
							materialPlu.put("PUNIT", oneMaterialPlu.get("MATERIAL_PUNIT").toString());
							materialPlu.put("BASEUNIT", oneMaterialPlu.get("MATERIAL_BASEUNIT").toString());
							materialPlu.put("UNITRATIO", oneMaterialPlu.get("MATERIAL_UNITRATIO").toString());
							materialPlus.add(materialPlu);
						}
						List<Map<String, Object>> getMaterialPluPrice = mc.getSalePrice_distriPrice(StaticInfo.dao,eId,companyId, shopId,materialPlus,companyId);
						
						//查原料单位转换率==0的商品，从倒扣料中剔除掉,并保存原料的单位换算率和对应的库存单位
						StringBuffer  materialPlunoMulti=new StringBuffer();
						for (Map<String, Object> par : getpstockinWGRK) {
							String pluNo=par.get("PLUNO").toString();
							String material_pluno=par.get("MATERIAL_PLUNO").toString();
							String material_punit=par.get("MATERIAL_PUNIT").toString();
							String material_UnitRatio=par.get("MATERIAL_UNITRATIO").toString();
							BigDecimal material_UnitRatio_b = new BigDecimal(material_UnitRatio);
							//找不到原料单位换算率
							if (material_UnitRatio_b.compareTo(BigDecimal.ZERO) == 0){
								loger.error("\r\n" + "倒扣料***完工入库GoodsCompleted：门店编号:"+ shopId + "原料编号:"+material_pluno+"单位编号:"+material_punit+"没有单位换算率" + "\r\n");
								error_msg.append("门店编号:"+ shopId +"成品编号:"+ pluNo +"原料编号:"+material_pluno+"原料单位编号:"+material_punit+"没有单位换算率" + "\r\n");
								isError=true;
							}
							String materialIsBatch = par.getOrDefault("MATERIAL_ISBATCH","N").toString();
							//Y.启用不检查库存量 N.不启用批号管理 T.启用且检查库存量 W.仅需警告
							if (!Check.Null(materialIsBatch) && !materialIsBatch.equals("N")) {
								materialPlunoMulti .append( "'" + material_pluno + "',");
							}
						}
						if (isError) {
							try {
								InsertWSLOG.insert_JOBLOG(eId,shopId,"GoodsComplete","自动完工入库",error_msg.toString());
							} catch (Exception ignored) {
							}
							continue; //下个门店
						}
						
						//启用批号管理时，查询所有原料的库存
						List<Map<String, Object>> materialBatchStock = new ArrayList<>();
						if (isBatchNO.equals("Y")) {
							materialPlunoMulti.deleteCharAt(materialPlunoMulti.length()-1);
							
							StringBuffer sba=new StringBuffer();
							sba.append(" "
									+ " select * from ("
									+ " select a.pluno,a.featureno,a.batchno,max(a.proddate) as proddate,a.baseunit,sum(a.baseqty) as baseqty,a.warehouse"
									+ " from ("
									+ " select a.pluno,trim(a.featureno) as featureno,trim(a.batchno) as batchno,a.proddate,a.baseunit,a.qty as baseqty,a.warehouse"
									+ " from DCP_Stock_Day a"
									+ " where a.EID='"+eId+"' and a.organizationno='"+shopId+"' and a.pluno in ("+ materialPlunoMulti.toString() +") "
									+ " union all"
									+ " select a.pluno,trim(a.featureno) as featureno,trim(a.batchno) as batchno,a.proddate,a.baseunit,a.baseqty*a.stocktype as baseqty,a.warehouse"
									+ " from DCP_STOCK_DETAIL a"
									+ " where a.EID='"+eId+"' and a.organizationno='"+shopId+"' and"
									+ " billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','14','15','16','17','18','19','20','21','12','13','30','31','32','33','34','35','36','37','38','39','40','41','42')"
									+ " and a.pluno in ("+materialPlunoMulti.toString()+")"
									+ " ) a group by a.pluno,a.featureno,a.batchno,a.baseunit,a.warehouse "
									+ " ) where baseqty>0  order by pluno,proddate "
									+ " ");
							
							materialBatchStock = this.doQueryData(sba.toString(), null);
						}
						
						//成品和原料循环计算
						condition.clear();
						condition.put("PLUNO", true);
						condition.put("FEATURENO", true);
						condition.put("BATCHNO", true);
						//调用过滤函数
						List<Map<String, Object>> getQHeader=MapDistinct.getMap(getpstockinWGRK, condition);
						int i = 0 ;
						int materialStockItem=0;  //原料库存流水项次
						int materialItem=0;       //DCP_PSTOCKIN_MATERIAL 原料项次
						
						outer:  //跳出成品循环标记
						for (Map<String, Object> maphead : getQHeader) {
							String pluNo=maphead.get("PLUNO").toString();
							String featureNo=maphead.get("FEATURENO").toString();
							String batchNo=maphead.get("BATCHNO").toString();
							String prodDate=maphead.get("PRODDATE").toString();
							String punit=maphead.get("PUNIT").toString();
							String pqty=maphead.get("PQTY").toString();
							String baseUnit=maphead.get("BASEUNIT").toString();
							String baseQty=maphead.get("BASEQTY").toString();
							warehouseId=maphead.get("WAREHOUSE").toString();
							String unitRatio=maphead.get("UNITRATIO").toString();
							
							//【ID1026974】【菲尔雪3.0】公司环境，门店开启了多仓，设置了加工模板，商品自动完工入库，收货仓库和扣料仓库不正确。
							// 要和2.0逻辑城市花园逻辑一致  by jinzma 20220704
							if (!Check.Null(maphead.get("MATERIALWAREHOUSE").toString())){
								materialWarehouse = maphead.get("MATERIALWAREHOUSE").toString();
							}
							
							//判断仓库不能为空格或空  BY JZMA 20191118
							if (Check.Null(warehouseId)||warehouseId.equals(" ")) {
								loger.error("\r\n" + "倒扣料***完工入库GoodsCompleted：门店编号:"+ shopId + "成品编号:"+pluNo+"仓库为空:"+"\r\n");
								error_msg.append( "门店编号:"+ shopId + "成品编号:"+pluNo+"仓库为空" + "\r\n");
								continue;   //下一个成品
							}
							
							if (new BigDecimal(baseQty).compareTo(BigDecimal.ZERO)==0||new BigDecimal(unitRatio).compareTo(BigDecimal.ZERO)==0) {
								loger.error("\r\n" + "倒扣料***完工入库GoodsCompleted：门店编号:"+ shopId + "成品编号:"+pluNo+"单位编号:"+punit+"没有单位换算率" + "\r\n");
								error_msg.append( "门店编号:"+ shopId + "成品编号:"+pluNo+"成品单位编号:"+punit+"没有单位换算率" + "\r\n");
								continue;   //下一个成品
							}
							
							//获取成品的零售价和配送价
							String price="0";
							String distriPrice="0";
							BigDecimal amt=new BigDecimal("0");
							BigDecimal distriAmt=new BigDecimal("0");
							Map<String, Object> condiV= new HashMap<>();
							condiV.put("PLUNO",pluNo);
							condiV.put("PUNIT",punit);
							List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
							if(priceList!=null && priceList.size()>0 ) {
								price=priceList.get(0).get("PRICE").toString();
								distriPrice=priceList.get(0).get("DISTRIPRICE").toString();
								BigDecimal price_b=new BigDecimal(price);
								BigDecimal distriPrice_b=new BigDecimal(distriPrice);
								amt = price_b.multiply(new BigDecimal(pqty)).setScale(amtLengthInt, RoundingMode.HALF_UP);
								distriAmt =  distriPrice_b.multiply(new BigDecimal(pqty)).setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
							}
							
							i = i + 1 ; //成品项次
							//写DCP_PSTOCKIN_MATERIAL以及原料的库存流水
							for (Map<String, Object> onemap : getpstockinWGRK) {
								
								if(pluNo.equals(onemap.get("PLUNO").toString())
										&& (Check.Null(featureNo) || featureNo.equals(onemap.get("FEATURENO").toString()))
										&& (Check.Null(batchNo) || batchNo.equals(onemap.get("BATCHNO").toString()))) {
									String material_pluno=onemap.get("MATERIAL_PLUNO").toString();
									String material_punit=onemap.get("MATERIAL_PUNIT").toString();
									String material_pqty=onemap.get("MATERIAL_PQTY").toString();
									String material_baseUnit=onemap.get("MATERIAL_BASEUNIT").toString();
									String material_unitRatio=onemap.get("MATERIAL_UNITRATIO").toString();
									String material_baseQty=onemap.get("MATERIAL_BASEQTY").toString();
									String material_isbuckle=onemap.get("ISBUCKLE").toString();
									String finalprodbaseqty=onemap.get("FINALPRODBASEQTY").toString();
									String rawmaterialbaseqty=onemap.get("RAWMATERIALBASEQTY").toString();
									
									if (Check.Null(material_unitRatio)) {
										material_unitRatio = "0";
									}
									//LIST中找不到原料库存单位或单位转换率
									if (Check.Null(material_baseUnit) || new BigDecimal(material_unitRatio).compareTo(BigDecimal.ZERO)==0) {
										loger.error("\r\n" + "倒扣料失败***完工入库GoodsCompleted：门店编号:"+ shopId + "原料编号:"+material_pluno+"原料单位编号:"+material_punit+"没有单位换算率  "+"\r\n");
										error_msg.append("门店编号:"+ shopId +"成品编号:"+pluNo+ "原料编号:"+material_pluno+"原料单位编号:"+material_punit+"没有单位换算率  "+"\r\n");
										isError = true;
										break outer;  //跳出外层商品循环
									}
									
									String[] materialColumnsName = {
											"EID","ORGANIZATIONNO","PSTOCKINNO",
											"SHOPID","WAREHOUSE","ITEM","MPLUNO","MITEM",
											"PLUNO","PUNIT","PQTY","BASEUNIT","BASEQTY","UNIT_RATIO",
											"FINALPRODBASEQTY","RAWMATERIALBASEQTY",
											"PRICE","AMT","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE",
											"ISBUCKLE","FEATURENO"
									};
									
									//获取原料的零售价和配送价
									String material_price="0";
									String material_distriPrice="0";
									BigDecimal material_amt=new BigDecimal("0");
									BigDecimal material_distriAmt=new BigDecimal("0");
									Map<String, Object> material_condiV= new HashMap<>();
									material_condiV.put("PLUNO",material_pluno);
									material_condiV.put("PUNIT",material_punit);
									List<Map<String, Object>> material_priceList= MapDistinct.getWhereMap(getMaterialPluPrice, material_condiV, false);
									if(material_priceList!=null && material_priceList.size()>0 ) {
										material_price=material_priceList.get(0).get("PRICE").toString();
										material_distriPrice=material_priceList.get(0).get("DISTRIPRICE").toString();
										BigDecimal material_price_b=new BigDecimal(material_price);
										BigDecimal material_distriPrice_b=new BigDecimal(material_distriPrice);
										material_amt = material_price_b.multiply(new BigDecimal(material_pqty)).setScale(amtLengthInt, RoundingMode.HALF_UP);
										material_distriAmt =  material_distriPrice_b.multiply(new BigDecimal(material_pqty)).setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
									}
									
									//启用批号管理且商品属性<>"N"   Y.启用不检查库存量 N.不启用批号管理 T.启用且检查库存量 W.仅需警告
									String material_isBatch = onemap.getOrDefault("MATERIAL_ISBATCH","N").toString();
									if (isBatchNO.equals("Y") && !material_isBatch.equals("N")) {
										boolean isZero = false ;
										String materialStock_featureNo="";
										String materialStock_batchNO ="";
										String materialStock_prodDate="";
										String materialStock_unitRatio;
										int listItem = 0;
										for (Map<String, Object> par : materialBatchStock) {
											String materialStock_pluNO = par.get("PLUNO").toString();
											String materialStock_warehouse = par.get("WAREHOUSE").toString();
											String materialStock_baseQty = par.get("BASEQTY").toString();
											BigDecimal materialStock_baseQty_b = new BigDecimal(materialStock_baseQty);
											double material_baseQty_old = 0;
											String material_pqty_old = "";
											
											if (material_pluno.equals(materialStock_pluNO)&& materialWarehouse.equals(materialStock_warehouse)  && materialStock_baseQty_b.compareTo(BigDecimal.ZERO)>0) {
												materialItem= materialItem + 1 ;
												materialStock_featureNo=par.get("FEATURENO").toString();
												materialStock_batchNO = par.get("BATCHNO").toString();
												materialStock_prodDate = par.get("PROD_DATE").toString();
												materialStock_unitRatio= par.get("MATERIAL_UNITRATIO").toString();
												//原料库存数量>=原料应扣减数量
												if (materialStock_baseQty_b.compareTo(new BigDecimal(material_baseQty))>=0) {
													par.put("BASEQTY", materialStock_baseQty_b.subtract(new BigDecimal(material_baseQty)));      //原料库存数量=原料库存数量-原料应扣减数量
													materialBatchStock.set(listItem, par);
													isZero = true ;
												} else {
													//原料库存数量<原料应扣减数量
													par.put("BASEQTY", "0");      //原料库存数量=0
													materialBatchStock.set(listItem, par);
													material_baseQty_old = Double.parseDouble(material_baseQty) - Double.parseDouble(materialStock_baseQty);
													material_pqty_old = String.valueOf(material_baseQty_old / Double.parseDouble(materialStock_unitRatio)) ;
													material_baseQty= materialStock_baseQty;            //原料数=库存数量
													material_pqty =String.valueOf(Double.parseDouble(material_baseQty) / Double.parseDouble(materialStock_unitRatio)) ;
												}
												//特征码必须给值，为空给空格
												if(Check.Null(materialStock_featureNo)) {
													materialStock_featureNo = " ";
												}
												DataValue[] materialValue = new DataValue[]{
														new DataValue(eId, Types.VARCHAR),
														new DataValue(shopId, Types.VARCHAR),
														new DataValue(pStockinNO, Types.VARCHAR),
														new DataValue(shopId, Types.VARCHAR),
														new DataValue(materialWarehouse, Types.VARCHAR),
														new DataValue(materialItem, Types.VARCHAR),
														new DataValue(pluNo, Types.VARCHAR),
														new DataValue(i, Types.VARCHAR),
														new DataValue(material_pluno, Types.VARCHAR),
														new DataValue(material_punit, Types.VARCHAR),
														new DataValue(material_pqty, Types.VARCHAR),
														new DataValue(material_baseUnit, Types.VARCHAR),
														new DataValue(material_baseQty, Types.VARCHAR),
														new DataValue(material_unitRatio, Types.VARCHAR),
														new DataValue(finalprodbaseqty, Types.VARCHAR),
														new DataValue(rawmaterialbaseqty, Types.VARCHAR),
														new DataValue(material_price, Types.VARCHAR),
														new DataValue(material_amt, Types.VARCHAR),
														new DataValue(materialStock_batchNO, Types.VARCHAR),
														new DataValue(materialStock_prodDate, Types.VARCHAR),
														new DataValue(material_distriPrice, Types.VARCHAR),
														new DataValue(material_distriAmt, Types.VARCHAR),
														new DataValue(accountDate, Types.VARCHAR),
														new DataValue(material_isbuckle, Types.VARCHAR),
														new DataValue(materialStock_featureNo, Types.VARCHAR),
												};
												InsBean materialIB = new InsBean("DCP_PSTOCKIN_MATERIAL", materialColumnsName);
												materialIB.addValues(materialValue);
												data.add(new DataProcessBean(materialIB));
												
												//写原料的库存流水
												///是否扣料件
												if (material_isbuckle.equals("Y")) {
													materialStockItem=materialStockItem + 1 ;
													String procedure="SP_DCP_StockChange";
													Map<Integer,Object> inputParameter = new HashMap<>();
													inputParameter.put(1,eId);                       //--企业ID
													inputParameter.put(2,shopId);                    //--组织
													inputParameter.put(3,"11");                      //--单据类型
													inputParameter.put(4,pStockinNO);	               //--单据号
													inputParameter.put(5,materialStockItem);         //--单据行号
													inputParameter.put(6,"-1");                      //--异动方向 1=加库存 -1=减库存
													inputParameter.put(7,accountDate);               //--营业日期 yyyy-MM-dd
													inputParameter.put(8,material_pluno);            //--品号
													inputParameter.put(9,materialStock_featureNo);   //--特征码
													inputParameter.put(10,materialWarehouse);        //--仓库
													inputParameter.put(11,materialStock_batchNO);    //--批号
													inputParameter.put(12,material_punit);           //--交易单位
													inputParameter.put(13,material_pqty);            //--交易数量
													inputParameter.put(14,material_baseUnit);        //--基准单位
													inputParameter.put(15,material_baseQty);         //--基准数量
													inputParameter.put(16,material_unitRatio);       //--换算比例
													inputParameter.put(17,material_price);           //--零售价
													inputParameter.put(18,material_amt);             //--零售金额
													inputParameter.put(19,material_distriPrice);     //--进货价
													inputParameter.put(20,material_distriAmt);       //--进货金额
													inputParameter.put(21,accountDate);              //--入账日期 yyyy-MM-dd
													inputParameter.put(22,materialStock_prodDate);   //--批号的生产日期 yyyy-MM-dd
													inputParameter.put(23,accountDate);              //--单据日期
													inputParameter.put(24,"");                       //--异动原因
													inputParameter.put(25,"系统自动产生原料倒扣");    //--异动描述
													inputParameter.put(26,"admin");                  //--操作员
													
													ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
													data.add(new DataProcessBean(pdb));
													
												}
												
												if (isZero) {
													break;
												}
												material_baseQty = String.valueOf(material_baseQty_old) ;
												material_pqty = material_pqty_old ;
												
											}
											listItem++;
										}
										//原料库存不足的处理     //Y.启用不检查库存量   N.不启用批号管理 T.启用且检查库存量 W.仅需警告
										if (!isZero) {
											//满足以下条件之一的，倒扣料失败
											//Y.启用不检查库存量 但库存档无资料
											//T.启用且检查库存量
											if ((material_isBatch.equals("Y") && Check.Null(materialStock_batchNO))||material_isBatch.equals("T") ) {
												loger.error("\r\n" + "倒扣料***完工入库GoodsCompleted：门店编号:"+ shopId + "成品编号:"+pluNo+"原料编号:"+material_pluno+"启用批号且库存不足，倒扣料失败" + "\r\n");
												error_msg.append("门店编号:"+ shopId + "成品编号:"+pluNo+"原料编号:"+material_pluno+"启用批号且库存不足，倒扣料失败" + "\r\n");
												isError = true;
												data.clear();
												break outer;  //跳出外层商品循环
											} else {
												materialItem= materialItem + 1 ;
												DataValue[] materialValue = new DataValue[]{
														new DataValue(eId, Types.VARCHAR),
														new DataValue(shopId, Types.VARCHAR),
														new DataValue(pStockinNO, Types.VARCHAR),
														new DataValue(shopId, Types.VARCHAR),
														new DataValue(materialWarehouse, Types.VARCHAR),
														new DataValue(materialItem, Types.VARCHAR),
														new DataValue(pluNo, Types.VARCHAR),
														new DataValue(i, Types.VARCHAR),
														new DataValue(material_pluno, Types.VARCHAR),
														new DataValue(material_punit, Types.VARCHAR),
														new DataValue(material_pqty, Types.VARCHAR),
														new DataValue(material_baseUnit, Types.VARCHAR),
														new DataValue(material_baseQty, Types.VARCHAR),
														new DataValue(material_unitRatio, Types.VARCHAR),
														new DataValue(finalprodbaseqty, Types.VARCHAR),
														new DataValue(rawmaterialbaseqty, Types.VARCHAR),
														new DataValue(material_price, Types.VARCHAR),
														new DataValue(material_amt, Types.VARCHAR),
														new DataValue(materialStock_batchNO, Types.VARCHAR),
														new DataValue(materialStock_prodDate, Types.VARCHAR),
														new DataValue(material_distriPrice, Types.VARCHAR),
														new DataValue(material_distriAmt, Types.VARCHAR),
														new DataValue(accountDate, Types.VARCHAR),
														new DataValue(material_isbuckle, Types.VARCHAR),
														new DataValue(materialStock_featureNo, Types.VARCHAR),
												};
												InsBean materialIB = new InsBean("DCP_PSTOCKIN_MATERIAL", materialColumnsName);
												materialIB.addValues(materialValue);
												data.add(new DataProcessBean(materialIB));
												
												//写原料的库存流水
												///是否扣料件
												if (material_isbuckle.equals("Y")) {
													materialStockItem=materialStockItem + 1 ;
													String procedure="SP_DCP_StockChange";
													Map<Integer,Object> inputParameter = new HashMap<>();
													inputParameter.put(1,eId);                       //--企业ID
													inputParameter.put(2,shopId);                    //--组织
													inputParameter.put(3,"11");                      //--单据类型
													inputParameter.put(4,pStockinNO);	               //--单据号
													inputParameter.put(5,materialStockItem);         //--单据行号
													inputParameter.put(6,"-1");                      //--异动方向 1=加库存 -1=减库存
													inputParameter.put(7,accountDate);               //--营业日期 yyyy-MM-dd
													inputParameter.put(8,material_pluno);            //--品号
													inputParameter.put(9,materialStock_featureNo);   //--特征码
													inputParameter.put(10,materialWarehouse);        //--仓库
													inputParameter.put(11,materialStock_batchNO);    //--批号
													inputParameter.put(12,material_punit);           //--交易单位
													inputParameter.put(13,material_pqty);            //--交易数量
													inputParameter.put(14,material_baseUnit);        //--基准单位
													inputParameter.put(15,material_baseQty);         //--基准数量
													inputParameter.put(16,material_unitRatio);       //--换算比例
													inputParameter.put(17,material_price);           //--零售价
													inputParameter.put(18,material_amt);             //--零售金额
													inputParameter.put(19,material_distriPrice);     //--进货价
													inputParameter.put(20,material_distriAmt);       //--进货金额
													inputParameter.put(21,accountDate);              //--入账日期 yyyy-MM-dd
													inputParameter.put(22,materialStock_prodDate);   //--批号的生产日期 yyyy-MM-dd
													inputParameter.put(23,accountDate);              //--单据日期
													inputParameter.put(24,"");                       //--异动原因
													inputParameter.put(25,"系统自动产生原料倒扣");    //--异动描述
													inputParameter.put(26,"admin");                  //--操作员
													
													ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
													data.add(new DataProcessBean(pdb));
													
												}
											}
										}
									} else {
										materialItem= materialItem + 1 ;
										DataValue[] materialValue = new DataValue[]{
												new DataValue(eId, Types.VARCHAR),
												new DataValue(shopId, Types.VARCHAR),
												new DataValue(pStockinNO, Types.VARCHAR),
												new DataValue(shopId, Types.VARCHAR),
												new DataValue(materialWarehouse, Types.VARCHAR),
												new DataValue(materialItem, Types.INTEGER),
												new DataValue(pluNo, Types.VARCHAR),
												new DataValue(i, Types.INTEGER),
												new DataValue(material_pluno, Types.VARCHAR),
												new DataValue(material_punit, Types.VARCHAR),
												new DataValue(material_pqty, Types.DOUBLE),
												new DataValue(material_baseUnit, Types.VARCHAR),
												new DataValue(material_baseQty, Types.DOUBLE),
												new DataValue(material_unitRatio, Types.DOUBLE),
												new DataValue(finalprodbaseqty, Types.DOUBLE),
												new DataValue(rawmaterialbaseqty, Types.DOUBLE),
												new DataValue(material_price, Types.VARCHAR),
												new DataValue(material_amt, Types.VARCHAR),
												new DataValue("", Types.VARCHAR),
												new DataValue("", Types.VARCHAR),
												new DataValue(material_distriPrice, Types.VARCHAR),
												new DataValue(material_distriAmt, Types.VARCHAR),
												new DataValue(accountDate, Types.VARCHAR),
												new DataValue(material_isbuckle, Types.VARCHAR),
												new DataValue(" ", Types.VARCHAR),     //特征码必须给值，为空给空格
											
										};
										InsBean materialIB = new InsBean("DCP_PSTOCKIN_MATERIAL", materialColumnsName);
										materialIB.addValues(materialValue);
										data.add(new DataProcessBean(materialIB));
										
										//写原料的库存流水
										///是否扣料件
										if (material_isbuckle.equals("Y")) {
											materialStockItem=materialStockItem + 1 ;
											String procedure="SP_DCP_StockChange";
											Map<Integer,Object> inputParameter = new HashMap<>();
											inputParameter.put(1,eId);                       //--企业ID
											inputParameter.put(2,shopId);                    //--组织
											inputParameter.put(3,"11");                      //--单据类型
											inputParameter.put(4,pStockinNO);	               //--单据号
											inputParameter.put(5,materialStockItem);         //--单据行号
											inputParameter.put(6,"-1");                      //--异动方向 1=加库存 -1=减库存
											inputParameter.put(7,accountDate);               //--营业日期 yyyy-MM-dd
											inputParameter.put(8,material_pluno);            //--品号
											inputParameter.put(9," ");                       //--特征码   //特征码必须给值，为空给空格
											inputParameter.put(10,materialWarehouse);        //--仓库
											inputParameter.put(11,"");    //--批号
											inputParameter.put(12,material_punit);           //--交易单位
											inputParameter.put(13,material_pqty);            //--交易数量
											inputParameter.put(14,material_baseUnit);        //--基准单位
											inputParameter.put(15,material_baseQty);         //--基准数量
											inputParameter.put(16,material_unitRatio);       //--换算比例
											inputParameter.put(17,material_price);           //--零售价
											inputParameter.put(18,material_amt);             //--零售金额
											inputParameter.put(19,material_distriPrice);     //--进货价
											inputParameter.put(20,material_distriAmt);       //--进货金额
											inputParameter.put(21,accountDate);              //--入账日期 yyyy-MM-dd
											inputParameter.put(22,"");                       //--批号的生产日期 yyyy-MM-dd
											inputParameter.put(23,accountDate);              //--单据日期
											inputParameter.put(24,"");                       //--异动原因
											inputParameter.put(25,"系统自动产生原料倒扣");    //--异动描述
											inputParameter.put(26,"admin");                  //--操作员
											
											ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
											data.add(new DataProcessBean(pdb));
											
										}
									}
									
									if (material_priceList != null) {
										material_priceList.clear();
									}
								}
							}
							
							//写DCP_PSTOCKIN_DETAIL
							String[] pstockindetailColumnsName = {
									"EID","ORGANIZATIONNO","SHOPID","PSTOCKINNO","ITEM","OITEM",
									"PLUNO","PUNIT","PQTY","UNIT_RATIO","BASEUNIT","BASEQTY",
									"PRICE","AMT","SCRAP_QTY","TASK_QTY","MUL_QTY",
									"WAREHOUSE","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT",
									"ACCOUNT_DATE","FEATURENO",
							};
							////featureNo 不能为空，为空给空格
							if (Check.Null(featureNo)) {
								featureNo = " ";
							}
							DataValue[] pstockindetailValue = new DataValue[]{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(shopId, Types.VARCHAR),
									new DataValue(pStockinNO, Types.VARCHAR),
									new DataValue(i, Types.VARCHAR),
									new DataValue(0, Types.VARCHAR),
									new DataValue(pluNo, Types.VARCHAR),
									new DataValue(punit, Types.VARCHAR),
									new DataValue(pqty, Types.VARCHAR),
									new DataValue(unitRatio, Types.VARCHAR),
									new DataValue(baseUnit, Types.VARCHAR),
									new DataValue(baseQty, Types.VARCHAR),
									new DataValue(price, Types.VARCHAR),
									new DataValue(amt.toPlainString(), Types.VARCHAR),
									new DataValue("0", Types.VARCHAR),
									new DataValue("0", Types.VARCHAR),
									new DataValue("0", Types.VARCHAR),
									new DataValue(warehouseId, Types.VARCHAR),
									new DataValue(batchNo, Types.VARCHAR),
									new DataValue(prodDate, Types.VARCHAR),
									new DataValue(distriPrice, Types.VARCHAR),
									new DataValue(distriAmt.toPlainString(), Types.VARCHAR),
									new DataValue(accountDate, Types.VARCHAR),
									new DataValue(featureNo, Types.VARCHAR),
							};
							InsBean pstockindetailIB = new InsBean("DCP_PSTOCKIN_DETAIL", pstockindetailColumnsName);
							pstockindetailIB.addValues(pstockindetailValue);
							data.add(new DataProcessBean(pstockindetailIB));
							
							totAmt = totAmt.add(amt);
							totDistriAmt = totDistriAmt.add(distriAmt);
							totPqty = totPqty.add(new BigDecimal(pqty));
							totCqty+= 1;
							
							
							String procedure="SP_DCP_StockChange";
							Map<Integer,Object> inputParameter = new HashMap<>();
							inputParameter.put(1,eId);                           //--企业ID
							inputParameter.put(2,shopId);                        //--组织
							inputParameter.put(3,"08");                          //--单据类型
							inputParameter.put(4,pStockinNO);	                   //--单据号
							inputParameter.put(5,i);                             //--单据行号
							inputParameter.put(6,"1");                           //--异动方向 1=加库存 -1=减库存
							inputParameter.put(7,accountDate);                   //--营业日期 yyyy-MM-dd
							inputParameter.put(8,pluNo);                         //--品号
							inputParameter.put(9,featureNo);                     //--特征码
							inputParameter.put(10,warehouseId);                    //--仓库
							inputParameter.put(11,batchNo);                      //--批号
							inputParameter.put(12,punit);                        //--交易单位
							inputParameter.put(13,pqty);                         //--交易数量
							inputParameter.put(14,baseUnit);                     //--基准单位
							inputParameter.put(15,baseQty);                      //--基准数量
							inputParameter.put(16,unitRatio);                    //--换算比例
							inputParameter.put(17,price);                        //--零售价
							inputParameter.put(18,amt.toPlainString());          //--零售金额
							inputParameter.put(19,distriPrice);                  //--进货价
							inputParameter.put(20,distriAmt.toPlainString());    //--进货金额
							inputParameter.put(21,accountDate);                  //--入账日期 yyyy-MM-dd
							inputParameter.put(22,prodDate);                     //--批号的生产日期 yyyy-MM-dd
							inputParameter.put(23,accountDate);                  //--单据日期
							inputParameter.put(24,"");                           //--异动原因
							inputParameter.put(25,"系统自动产生成品入库");        //--异动描述
							inputParameter.put(26,"admin");                      //--操作员
							
							ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
							data.add(new DataProcessBean(pdb));
							
							if (priceList != null) {
								priceList.clear();
							}
						}
						
						//完工入库单单头(DCP_PSTOCKIN)
						String[] pstockinColumnsName = {
								"SHOPID", "ORGANIZATIONNO", "EID", "PSTOCKINNO", "MEMO", "BDATE",
								"LOAD_DOCTYPE", "LOAD_DOCNO", "OTYPE", "OFNO",
								"CREATEBY", "CREATE_DATE", "CREATE_TIME",
								"MODIFYBY",	"MODIFY_DATE", "MODIFY_TIME",
								"CONFIRMBY", "CONFIRM_DATE", "CONFIRM_TIME",
								"ACCOUNTBY", "ACCOUNT_DATE", "ACCOUNT_TIME",
								"CANCELBY", "CANCEL_DATE", "CANCEL_TIME",
								"SUBMITBY","SUBMIT_DATE","SUBMIT_TIME",
								"TOT_PQTY", "TOT_AMT", "TOT_CQTY", "STATUS",
								"WAREHOUSE","DOC_TYPE","TOT_DISTRIAMT","PROCESS_STATUS",
								"UPDATE_TIME","TRAN_TIME"};
						
						totAmt = totAmt.setScale(amtLengthInt, RoundingMode.HALF_UP);
						totDistriAmt = totDistriAmt.setScale(distriAmtLengthInt, RoundingMode.HALF_UP);
						
						DataValue[] pstockinValue = new DataValue[] {
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(eId, Types.VARCHAR),
								new DataValue(pStockinNO, Types.VARCHAR),
								new DataValue("系统自动产生倒扣料", Types.VARCHAR),
								new DataValue(accountDate, Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("admin", Types.VARCHAR),
								new DataValue(sdate, Types.VARCHAR),
								new DataValue(stime, Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("admin", Types.VARCHAR),
								new DataValue(sdate, Types.VARCHAR),
								new DataValue(stime, Types.VARCHAR),
								new DataValue("admin", Types.VARCHAR),
								new DataValue(accountDate, Types.VARCHAR),
								new DataValue(stime, Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("", Types.VARCHAR),
								new DataValue("admin", Types.VARCHAR),
								new DataValue(sdate, Types.VARCHAR),
								new DataValue(stime, Types.VARCHAR),
								new DataValue(totPqty.toPlainString(), Types.VARCHAR),
								new DataValue(totAmt.toPlainString(), Types.VARCHAR),
								new DataValue(totCqty, Types.VARCHAR),
								new DataValue("2", Types.VARCHAR),
								new DataValue(warehouseId, Types.VARCHAR),   //取单身最后一个商品的入库仓库
								new DataValue("0", Types.VARCHAR),
								new DataValue(totDistriAmt.toPlainString(), Types.VARCHAR),
								new DataValue("N", Types.VARCHAR),
								new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
								new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
						};
						InsBean pstockinIB = new InsBean("DCP_PSTOCKIN", pstockinColumnsName);
						pstockinIB.addValues(pstockinValue);
						data.add(new DataProcessBean(pstockinIB));
						loger.info("\r\n*********倒扣料***完工入库GoodsCompleted 商品总数:"+totCqty+"************\r\n");
						
						//TOT_CQTY 防止出现有单头没有单身
						if (!isError && totCqty>0) {
							
							StaticInfo.dao.useTransactionProcessData(data);
							
							
							
							//***********调用库存同步给三方，这是个异步，不会影响效能*****************
							try
							{
								WebHookService.stockSync(eId,shopId,pStockinNO);
							}
							catch (Exception ignored)
							{
							
							}
						}else{
							data.clear();
						}
						
						if (getQHeader != null) {
							getQHeader.clear();
						}
						
						if (materialBatchStock!=null) {
							materialBatchStock.clear();
							materialBatchStock=null;
						}
						
						if (materialBatchStock != null) {
							materialBatchStock.clear();
						}
						
						materialPlunoMulti.setLength(0);
						
						if (getMaterialPluPrice != null) {
							getMaterialPluPrice.clear();
						}
						if (getQMaterialPlu != null) {
							getQMaterialPlu.clear();
						}
						if (materialPlus != null) {
							materialPlus.clear();
						}
						if (getPluPrice != null) {
							getPluPrice.clear();
						}
						if (getQPlu != null) {
							getQPlu.clear();
						}
					} else {
						loger.info("\r\n" + "倒扣料***完工入库GoodsCompleted 查询无数据  " + "\r\n");
					}
				} catch (Exception e) {
					
					//记录JOB异常日志  BY JZMA 20190527
					//error_msg.append(e.getMessage());
					
					//【ID1026515】【法美味3.0】浦西和睦店，发喜全牛奶和小满咖啡豆，这两个原材料不减库存，自动完工入库报错 by jinzma 20220613
					StringWriter errors = new StringWriter();
					PrintWriter pw=new PrintWriter(errors);
					e.printStackTrace(pw);
					pw.flush();
					pw.close();
					errors.flush();
					errors.close();
					loger.error("\r\n" + "倒扣料***完工入库GoodsCompleted：EID="+eId+"SHOPID="+shopId+" WAREHOUSEID="+warehouseId+" 执行发生异常\r\n"+errors.toString());
					
					//【ID1032555】【乐沙儿3.3.0.3】在门店管理出库单据点确定时，存在负库存时的提示返回error executing work，
					// 需要能够返回SP_DCP_StockChange返回的报错，提示门店  by jinzma 20230526
					
					if (errors.toString().indexOf("品号")>0 && errors.toString().indexOf("库存出库")>0) {
						error_msg.append("企业编号:"+eId+" 门店编号:"+shopId+ " "+ errors.toString().substring(errors.toString().indexOf("品号"), errors.toString().indexOf("库存出库") + 4));
					}else{
						error_msg.append(errors.toString());
					}
				}
				
				loger.info("\r\n*********倒扣料***完工入库GoodsCompleted 已完成门店仓库数： "+iiShop+"个************\r\n");
				try {
					if(error_msg.length()>0) {
						InsertWSLOG.insert_JOBLOG(eId,shopId,"GoodsComplete","自动完工入库",error_msg.toString());
					} else {
						InsertWSLOG.delete_JOBLOG(eId,shopId,"GoodsComplete");
					}
				} catch(Exception e) {
					loger.error("*********倒扣料***完工入库GoodsCompleted 企业编号:"+eId+"门店编号： "+shopId+ " WAREHOUSEID="+warehouseId+"日志记录失败************\r\n");
				}
				
				
				if (getpstockinWGRK != null) {
					getpstockinWGRK.clear();
				}
				
				error_msg.setLength(0);
				
				if (data != null) {
					data.clear();
				}
			}
			
			//
			if (slshop != null) {
				slshop.clear();
			}
		} catch (Exception e) {
			loger.error("\r\n******倒扣料***完工入库GoodsCompleted报错信息" + e.getMessage() + "******\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
		}
		
		finally {
			bRun=false;//
			loger.info("\r\n*********倒扣料***完工入库GoodsCompleted定时结束:************\r\n");
		}
		
		return sReturnInfo;
		
	}
	
	private String getPStockInNO(String eId, String shopId, String accountDate) throws Exception {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		
		StringBuffer sqlbuf = new StringBuffer();
		String pStockInNO = "WGRK" + accountDate;  //取了公共的营业日期
		sqlbuf.append(" select max(pstockinno) as pstockinno from dcp_pstockin "
				+ " where eid = '" + eId + "' and shopid = '" + shopId + "' and pstockinno like '%%"+ pStockInNO +"%%' ");
		List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
		if (getQData != null && !getQData.isEmpty()) {
			pStockInNO = (String) getQData.get(0).get("PSTOCKINNO");
			
			if (pStockInNO != null && pStockInNO.length() > 0) {
				long i;
				pStockInNO = pStockInNO.substring(4);
				i = Long.parseLong(pStockInNO) + 1;
				pStockInNO = i + "";
				pStockInNO = "WGRK" + pStockInNO;
			} else {
				pStockInNO = "WGRK" + accountDate + "00001";
			}
		} else {
			pStockInNO = "WGRK" + accountDate + "00001";
		}
		
		if (getQData != null) {
			getQData.clear();
		}
		
		return pStockInNO;
	}
	
	protected String getpstockinSQL(String eId,String companyId,String shopId,String warehouseId) {
		StringBuffer sqlbuf = new StringBuffer();
		//【ID1030455】with goodstemplate as 效率优化，这个SQL效率很低，货郎执行大概需要8秒。需要优化  by jinzma 20230110
		/*sqlbuf.append(" "
				+ " with goodstemplate as ("
				+ " select b.* from ("
				+ " select a.*,row_number() over (partition by a.eid order by a.templatetype desc,a.createtime desc) as rn"
				+ " from dcp_goodstemplate a"
				+ " left  join dcp_goodstemplate_range c1 on c1.eid=a.eid and c1.templateid=a.templateid and c1.RANGETYPE='1' and c1.id='"+companyId+"'"
				+ " left  join dcp_goodstemplate_range c2 on c2.eid=a.eid and c2.templateid=a.templateid and c2.RANGETYPE='2' and c2.id='"+shopId+"'"
				//and ((a.restrictshop='1' and c2.id is not null) or a.restrictshop='0' or c1.id is not null) 20200701 小凤通知拿掉全部门店 
				+ " where a.eid='"+eId+"' and a.status='100' "
				+ " and ((a.templatetype='COMPANY' and c1.id is not null) or (a.templatetype='SHOP' and c2.id is not null))"
				+ " ) a"
				+ " inner join dcp_goodstemplate_goods b on b.eid=a.eid and b.templateid=a.templateid and b.status='100'"
				+ " where a.rn=1 "
				+ " )"
				+ " ");*/
		
		
		//【ID1031100】【货郎3.0】商城商品设置新增选择商品读不出来-报错  by jinzma 20230207
		sqlbuf.append(" "
				+ " with goodstemplate as ("
				+ " select b.* from dcp_goodstemplate a"
				+ " inner join dcp_goodstemplate_goods b on a.eid=b.eid and a.templateid=b.templateid and b.status='100'"
				+ " where a.eid='"+eId+"' and a.templateid=F_DCP_GET_GOODSTEMPLATE('"+eId+"','"+shopId+"')"
				+ " ) ");
		
		sqlbuf.append(" "
				+ " ,bom as ("
				+ " select a.pluno,a.mulqty,a.unit,c.material_pluno,c.material_unit,c.material_qty,c.qty,c.loss_rate,c.isbuckle,c.isreplace,c.sortid from ("
				+ " select a.bomno,a.pluno,a.mulqty,a.unit,row_number() over (partition by a.pluno order by effdate desc) as rn from dcp_bom a"
				+ " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"'"
				+ " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and status='100' and bomtype = '0'"
				+ " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))"
				+ " )a"
				+ " inner join dcp_bom_material c on a.bomno=c.bomno and c.eid='"+eId+"' and trunc(c.material_bdate)<=trunc(sysdate) and trunc(material_edate)>=trunc(sysdate)"
				+ " inner join dcp_goods_unit d on d.eid=c.eid and d.pluno=a.pluno and d.ounit=a.unit  and d.prod_unit_use='Y' "
				+ " where a.rn=1"
				+ " )"
				+ " ");
		
		//【ID1026974】【菲尔雪3.0】公司环境，门店开启了多仓，设置了加工模板，商品自动完工入库，收货仓库和扣料仓库不正确。
		// 要和2.0逻辑城市花园逻辑一致  by jinzma 20220704
		sqlbuf.append(" "
				+ " ,materialwarehouse as ("
				+ " select a.pluno,"
				+ " max(b.materialwarehouse) keep(dense_rank last order by b.tran_time) materialwarehouse"
				+ " from dcp_ptemplate_detail a"
				+ " inner join dcp_ptemplate_shop b on a.eid=b.eid and a.ptemplateno=b.ptemplateno"
				+ " and a.doc_type=b.doc_type and a.status='100' and b.status='100'"
				+ " where a.eid='"+eId+"' and a.doc_type='2' and b.shopid='"+shopId+"'"
				+ " and b.goodswarehouse is not null and b.materialwarehouse is not null"
				+ " group by a.pluno"
				+ " )"
				+ " ");
		
		sqlbuf.append(" select a1.*,bom.material_pluno,bom.sortid,bom.unit as punit,bom.material_unit as material_punit,"
				+ " bom.isbuckle,bom.qty as finalprodbaseqty,bom.material_qty as rawmaterialbaseqty,"
				+ " nvl(u1.udlength,2) as baseunitlength,"
				+ " nvl(u2.udlength,2) as material_baseunitlength,"
				+ " nvl(u3.udlength,2) as punitlength,"
				+ " nvl(u4.udlength,2) as material_punitlength,"
				+ " unit.unitratio,"
				+ " g1.baseunit,g1.isbatch,"
				+ " munit.unitratio as material_unitratio,"
				+ " g2.baseunit as material_baseunit,g2.isbatch as material_isbatch,"
				+ " case"
				+ " when bom.qty is null then 0"
				+ " when bom.qty=0 then 0"
				+ " when unit.unitratio is null then 0"
				+ " when unit.unitratio=0 then 0"
				+ " else round(bom.material_qty*(1+ nvl(bom.loss_rate,0)/100)/bom.qty*nvl(munit.unitratio,0)/unit.unitratio*(a1.refbaseqty*-1),nvl(u2.udlength,2)) end as material_baseqty,"
				+ " case"
				+ " when bom.qty is null then 0"
				+ " when bom.qty=0 then 0"
				+ " when unit.unitratio is null then 0"
				+ " when unit.unitratio=0 then 0"
				+ " else round(bom.material_qty*(1+ nvl(bom.loss_rate,0)/100)/bom.qty/unit.unitratio*(a1.refbaseqty*-1),nvl(u4.udlength,2)) end as material_pqty,"
				+ " case "
				+ " when unit.unitratio is null then 0"
				+ " when unit.unitratio=0 then 0"
				+ " else round(a1.refbaseqty*-1/unit.unitratio,nvl(u3.udlength,2)) end as pqty,"
				+ " case "
				+ " when unit.unitratio is null then 0"
				+ " when unit.unitratio=0 then 0"
				+ " else round(a1.refbaseqty*-1,nvl(u1.udlength,2) ) end as baseqty,"
				+ " m.materialwarehouse"
				+ " from ("
				
				/*+ " select * from ("
				+ " select a.eid,a.pluno,a.featureno,a.batchno,max(proddate) as proddate,sum(a.baseqty) as refbaseqty,a.Warehouse from"
				+ " ("
				+ " select a.eid,a.pluno,trim(a.featureno) as featureno,trim(batchno) as batchno,proddate,a.qty as baseqty,Warehouse from DCP_Stock_Day a"
				+ " where a.eId='"+eId+"' and a.organizationno='" + shopId +"'"
				+ " union all"
				+ " select a.eid,a.pluno,trim(a.featureno) as featureno,trim(batchno) as batchno,proddate,a.baseqty*a.stocktype as baseqty,Warehouse from DCP_STOCK_DETAIL a"
				+ " where a.eId='"+eId+"' and a.organizationno='" + shopId+"'"
				+ " and billtype in ('00','01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','30','31','32','33','34','35','36','37','38','39','40','41','42')"
				+ " ) a"
				+ " inner join goodstemplate on goodstemplate.pluno=a.pluno and goodstemplate.is_auto_subtract='Y'"
				+ " group by a.eid,a.pluno,a.featureno,a.batchno,a.Warehouse"
				+ " ) where refbaseqty<0 "*/
				
				//【ID1032115】【霸王】倒扣料SQL极度复杂，占服务器资源过高，怀疑有严重冗余数据和效率问题  by jinzma 20230324
				+ " select a.eid,a.pluno,trim(a.featureno) as featureno,N'' as batchno,N'' as proddate,"
				+ " a.qty as refbaseqty,a.warehouse"
				+ " from dcp_stock a"
				+ " inner join goodstemplate on goodstemplate.pluno=a.pluno and goodstemplate.is_auto_subtract='Y'"
				+ " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.warehouse='"+warehouseId+"' "
				+ " and a.qty<0 "
				+ " ) a1"
				+ " inner join bom on bom.pluno=a1.pluno"
				+ " inner join dcp_goods g1 on g1.eid = a1.eid and g1.pluno=a1.pluno"
				+ " inner join dcp_goods g2 on g2.eid = a1.eid and g2.pluno=bom.material_pluno"
				+ " inner join dcp_goods_unit unit on unit.eid=a1.eid and  a1.pluno=unit.pluno and bom.unit=unit.ounit and unit.unit=g1.baseunit"
				+ " inner join dcp_goods_unit munit on munit.eid=a1.eid and bom.material_pluno=munit.pluno and bom.material_unit=munit.ounit and munit.unit=g2.baseunit "
				+ " left join dcp_unit u1 on u1.eid=a1.eid and u1.unit=g1.baseunit"
				+ " left join dcp_unit u2 on u2.eid=a1.eid and u2.unit=g2.baseunit"
				+ " left join dcp_unit u3 on u3.eid=a1.eid and u3.unit=bom.unit"
				+ " left join dcp_unit u4 on u4.eid=a1.eid and u4.unit=bom.material_unit"
				+ " left join materialwarehouse m on a1.pluno=m.pluno"
				+ " order by a1.pluno,a1.featureno,a1.batchno,bom.sortid"
				+ " ");
		
		String sql = sqlbuf.toString();
		return sql;
	}
	
	
	
}
