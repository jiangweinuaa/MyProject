package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import com.dsc.spos.json.cust.req.DCP_POrderOTUsageReq;
import com.dsc.spos.json.cust.req.DCP_POrderOTUsageReq.*;
import com.dsc.spos.json.cust.res.DCP_POrderOTUsageRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DCP_POrderOTUsage extends SPosBasicService<DCP_POrderOTUsageReq, DCP_POrderOTUsageRes> {
	Logger logger = LogManager.getLogger(DCP_POrderOTUsage.class);
	@Override
	protected boolean isVerifyFail(DCP_POrderOTUsageReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		levelElm request = req.getRequest();
		List<level1Elm> datas = request.getDatas();
		
		if (Check.Null(request.getBeginDate())) {
			errMsg.append("开始日期不可为空值, ");
			isFail = true;
		}
		if (Check.Null(request.getEndDate())) {
			errMsg.append("结束日期不可为空值, ");
			isFail = true;
		}
		if (!PosPub.isNumericType(request.getAvgsaleAMT())) {
			errMsg.append("平均营业额不可为空值或非数值, ");
			isFail = true;
		}
		if (!PosPub.isNumericType(request.getSaleAMT())) {
			errMsg.append("营业额预估不可为空值或非数值, ");
			isFail = true;
		}
		if (!PosPub.isNumericType(request.getModifRatio())) {
			errMsg.append("调整系数不可为空值或非数值, ");
			isFail = true;
		}
		if (Check.Null(request.getCal_type())) {
			errMsg.append("计算方式不可为空值, ");
			isFail = true;
		}
		if (Check.Null(request.getMateral_type())) {
			errMsg.append("用料计算方式不可为空值, ");
			isFail = true;
		}
		if (datas==null){
			errMsg.append("datas明细不可为空值, ");
			isFail = true;
		}else{
			for (level1Elm par:datas){
				if (Check.Null(par.getPluNo())) {
					errMsg.append("商品编号不可为空值, ");
					isFail = true;
				}
				if (Check.Null(par.getFeatureNo())) {
					errMsg.append("特征码不可为空值, ");
					isFail = true;
				}
				if (Check.Null(par.getPunit())) {
					errMsg.append("录入单位不可为空值, ");
					isFail = true;
				}
				
				if (isFail){
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
				}
			}
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_POrderOTUsageReq> getRequestType() {
		return new TypeToken<DCP_POrderOTUsageReq>() {};
	}
	
	@Override
	protected DCP_POrderOTUsageRes getResponseType() {
		return new DCP_POrderOTUsageRes();
	}
	
	@Override
	protected DCP_POrderOTUsageRes processJson(DCP_POrderOTUsageReq req) throws Exception {
		DCP_POrderOTUsageRes res = this.getResponse();
		try {
			// 1、查找所传商品的库存 2、查找这几天的完工入库的量 3、查找单位换算率  // 传过来的平均营业额
			res.setDatas(new ArrayList<>());
			//【ID1032657】【3.0】要货申请 千元用量功能问题   by jinzma 20230424
			String eId = req.geteId();
			String shopId = req.getShopId();
			String beginDate = req.getRequest().getBeginDate();        //开始日期
			String endDate = req.getRequest().getEndDate();            //结束日期
			String avgSaleAmt = req.getRequest().getAvgsaleAMT();      //范围内平均营业额
			String saleAmt = req.getRequest().getSaleAMT();            //预估营业额
			String modifRatio = req.getRequest().getModifRatio();      //调整系数
			String cal_type = req.getRequest().getCal_type();          //计算公式   1.预估量=计算量-库存量-未到货量  2.预估量=计算量
			String materal_type = req.getRequest().getMateral_type();  //用料计算方式   1.销售量BOM推算 2.盘点损耗
			String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			//计算开始和结束日期之间的差异天数
			Date date1 = new SimpleDateFormat("yyyyMMdd").parse(beginDate);
			Date date2 = new SimpleDateFormat("yyyyMMdd").parse(endDate);
			int difdate = PosPub.differentDaysByMillisecond(date1, date2) + 1;
			
			//计算期间营业额
			BigDecimal avgSaleAmt_b = new BigDecimal(avgSaleAmt);                  //范围内平均营业额
			BigDecimal saleAmt_b = new BigDecimal(saleAmt);  //营业额预估
			
			//rRate 营业额计算比例   if 范围内平均营业额 == 0 则 rRate==0 else rRate==预估营业额/平均营业额*调整系数
			BigDecimal rRate_b = new BigDecimal("0");
			if (avgSaleAmt_b.compareTo(BigDecimal.ZERO) > 0) {
				rRate_b = saleAmt_b.divide(avgSaleAmt_b,6, RoundingMode.HALF_UP).multiply(new BigDecimal(modifRatio));
			}
			
			//商品和单位存入临时表
			MyCommon mc = new MyCommon();
			List<level1Elm> datas = req.getRequest().getDatas();
			Map<String,String> map = new HashMap<>();
			String sJoinPluNo = "";
			String sJoinPunit = "";
			for(level1Elm par :datas) {
				///要货单商品导入 商品编号要小写处理，有客户商品编号带字母
				sJoinPluNo += par.getPluNo().toLowerCase()+",";
				sJoinPunit += par.getPunit()+",";
			}
			map.put("PLUNO", sJoinPluNo);
			map.put("PUNIT", sJoinPunit);
			String withPlu = mc.getFormatSourceMultiColWith(map);
			
			String sql;
			List<Map<String, Object>> getQData = new ArrayList<>();
			
			if (materal_type.equals("1")) {
				sql = " select a.pluno,a.ounit as punit,a.unit as baseunit,a.unitratio from dcp_goods_unit a"
						+ " inner join ("+withPlu+")b on a.pluno=b.pluno and a.ounit=b.punit"
						+ " where a.eid='"+eId+"' ";
				List<Map<String, Object>> getQUnitRatioData = this.doQueryData(sql, null);
				
				
				sql = " select a.pluno,b1.unit as baseunit,a.material_pluno,b2.unit as material_baseunit,"
						+ " round(a.material_qty*b2.unitratio/b1.unitratio,6) as material_baseqty "
						+ " from ("
						+ " select row_number() over (partition by a.pluno order by effdate desc) as rn,"
						+ " a.eid,a.pluno,a.unit,c.material_pluno,c.material_unit,"
						+ " c.material_qty*(1+c.loss_rate*0.01)/c.qty as material_qty"
						+ " from dcp_bom a"
						+ " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"'"
						+ " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno and c.material_bdate<=sysdate and c.material_edate>=sysdate"
						+ " inner join ("+withPlu+")d on c.material_pluno=d.pluno"
						+ " where a.eId='"+eId+"' and a.effdate<=sysdate and a.status='100' and a.bomtype = '0' "
						+ " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))"
						+ " )a"
						+ " inner join dcp_goods_unit b1 on a.eid=b1.eid and a.pluno=b1.pluno and a.unit=b1.ounit"
						+ " inner join dcp_goods_unit b2 on a.eid=b2.eid and a.material_pluno=b2.pluno and a.material_unit=b2.ounit"
						+ " where a.rn=1 ";
				//先用原料匹配BOM表查对应的成品数
				List<Map<String, Object>> getQMaterial1Data = this.doQueryData(sql, null);
				//放入第一次和第二次查询BOM对应的成品和前端传入的商品
				String sJoinAllPluNo = sJoinPluNo;
				String sJoinAllPunit = sJoinPunit;
				if (!CollectionUtils.isEmpty(getQMaterial1Data)){
					
					//成品放入原料再查一次，支持二级原料（半成品）
					String sJoinPlu = "";
					
					for (Map<String, Object> getOneMaterial1:getQMaterial1Data){
						sJoinPlu += getOneMaterial1.get("PLUNO").toString()+",";
						sJoinAllPluNo += getOneMaterial1.get("PLUNO").toString()+",";
						sJoinAllPunit += getOneMaterial1.get("BASEUNIT").toString()+",";
					}
					Map<String,String> pluMap = new HashMap<>(); //第一次查询出来的成品
					pluMap.put("PLUNO", sJoinPlu);
					
					sql = " select a.pluno,b1.unit as baseunit,a.material_pluno,b2.unit as material_baseunit,"
							+ " round(a.material_qty*b2.unitratio/b1.unitratio,6) as material_baseqty "
							+ " from ("
							+ " select row_number() over (partition by a.pluno order by effdate desc) as rn,"
							+ " a.eid,a.pluno,a.unit,c.material_pluno,c.material_unit,"
							+ " c.material_qty*(1+c.loss_rate*0.01)/c.qty as material_qty"
							+ " from dcp_bom a"
							+ " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"'"
							+ " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno "
							+ " inner join ("+mc.getFormatSourceMultiColWith(pluMap)+")d on c.material_pluno=d.pluno"
							+ " where a.eId='"+eId+"' and a.effdate<=sysdate and a.status='100' and a.bomtype = '0' "
							+ " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null)) "
							+ " and c.material_bdate<=sysdate and c.material_edate>=sysdate "
							+ " )a"
							+ " inner join dcp_goods_unit b1 on a.eid=b1.eid and a.pluno=b1.pluno and a.unit=b1.ounit"
							+ " inner join dcp_goods_unit b2 on a.eid=b2.eid and a.material_pluno=b2.pluno and a.material_unit=b2.ounit"
							+ " where a.rn=1 ";
					
					//成品放入原料再查一次，支持二级原料（半成品）
					List<Map<String, Object>> getQMaterial2Data = this.doQueryData(sql, null);
					if (!CollectionUtils.isEmpty(getQMaterial2Data)){
						for (Map<String, Object> oneMaterial2 : getQMaterial2Data){
							String pluNo = oneMaterial2.get("PLUNO").toString();
							String baseUnit = oneMaterial2.get("BASEUNIT").toString();
							String materialPluNo = oneMaterial2.get("MATERIAL_PLUNO").toString();
							String materialBaseQty = oneMaterial2.get("MATERIAL_BASEQTY").toString();
							
							//放入第一次和第二次查询BOM对应的成品和前端传入的商品
							sJoinAllPluNo += pluNo.toLowerCase()+",";
							sJoinAllPunit += baseUnit.toLowerCase()+",";
							
							//此处一定能找到的
							List<Map<String, Object>> getQMaterial1 = getQMaterial1Data.stream().filter(p->p.get("PLUNO").toString().equals(materialPluNo)).collect(Collectors.toList());
							for (Map<String, Object> oneMaterial1 : getQMaterial1){
								/*  把查到的原料和成品再塞回去   by jinzma 20230427
								  举例： 八宝饭 --> 八宝和米饭    八宝--> 红枣和糖
								  第一次用原料红枣查到了成品八宝
								  第二次用八宝查到了成品八宝饭
								  塞回去以后变成
								  八宝   --> 红枣和糖  (原本就有的)
								  八宝饭 --> 红枣和糖  (新塞回去的)
								  八宝和八宝饭都去库存流水里面查，有可能八宝作为半成品也是单独售卖的
								 */
								
								Map<String, Object> map1 = new HashMap<>();
								map1.put("PLUNO",pluNo);
								map1.put("BASEUNIT",baseUnit);
								map1.put("MATERIAL_PLUNO",oneMaterial1.get("MATERIAL_PLUNO").toString());
								map1.put("MATERIAL_BASEUNIT",oneMaterial1.get("MATERIAL_BASEUNIT").toString());
								map1.put("MATERIAL_BASEQTY",new BigDecimal(materialBaseQty).multiply(new BigDecimal(oneMaterial1.get("MATERIAL_BASEQTY").toString())).setScale(2,RoundingMode.HALF_UP));
								
								getQMaterial1Data.add(map1);
							}
						}
					}
				}
				
				Map<String,String> allPluMap = new HashMap<>(); //第一次和第二次查询BOM对应的成品和前端传入的商品
				allPluMap.put("PLUNO", sJoinAllPluNo);
				allPluMap.put("PUNIT", sJoinAllPunit);
				
				sql = " select a.pluno,a.featureno,"
						+ " round(sum((a.sale_qty-a.adjust_qty)/c.unitratio)"+"*"+rRate_b.toPlainString()+"/"+difdate+",6) as sqty,"
						+ " round(sum(a.sale_qty-a.adjust_qty)"+"*"+rRate_b.toPlainString()+"/"+difdate+",6) as sbaseqty"
						+ " from dcp_stock_day_static a"
						+ " inner join ("+mc.getFormatSourceMultiColWith(allPluMap)+")b on a.pluno=b.pluno"
						+ " inner join dcp_goods_unit c on a.eid=c.eid and a.pluno=c.pluno and b.punit=c.ounit "
						+ " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.partition_date>='"+beginDate+"' and a.partition_date<='"+endDate+"'"
						+ " group by a.pluno,a.featureno";
				List<Map<String, Object>> getAllQData = this.doQueryData(sql, null);
				if (!CollectionUtils.isEmpty(getAllQData)){
					
					/*  把查到的成品销售数据SQTY * 对应的原料数 再塞回到 getQData  by jinzma 20230427
						举例： 八宝饭 售卖100份
						八宝饭1份 --> 10个红枣和10克糖
						红枣 1000个
						糖   1000克
					*/
					//  getAllQData 对应的所有成品都是要货单位
					for (Map<String, Object> getOneAllData:getAllQData){
						String pluNo = getOneAllData.get("PLUNO").toString();
						String featureNo = getOneAllData.get("FEATURENO").toString();
						String sQty = getOneAllData.get("SQTY").toString();
						if (Check.Null(sQty)){
							sQty = "0";
						}
						String sBaseQty = getOneAllData.get("SBASEQTY").toString();
						if (Check.Null(sBaseQty)){
							sBaseQty = "0";
						}
						//查BOMLIST里面是否存在这个成品
						List<Map<String, Object>> getQMaterial1 = getQMaterial1Data.stream().filter(p->p.get("PLUNO").toString().equals(pluNo)).collect(Collectors.toList());
						if (!CollectionUtils.isEmpty(getQMaterial1)){
							//原料LIST匹配到成品后，取出对应的原料塞入getQData （判断getQData是否已存在，sQty_b要转换成要货单位）
							for (Map<String, Object> getOneMaterial1 : getQMaterial1){
								String materialPluNo = getOneMaterial1.get("MATERIAL_PLUNO").toString();
								String materialBaseQty = getOneMaterial1.get("MATERIAL_BASEQTY").toString();
								String baseUnit = getOneMaterial1.get("BASEUNIT").toString();
								//此处取基准单位算出来的sqty，因为原料LIST里全部用的基准单位
								BigDecimal sBaseQty_b = new BigDecimal(sBaseQty);
								
								//库存档实际销售数量（要货单位） * 原料档
								sBaseQty_b = sBaseQty_b.multiply(new BigDecimal(materialBaseQty)).setScale(2,RoundingMode.HALF_UP);
								
								//原料单位从基准单位转换成要货单位，取单位转换率计算
								BigDecimal sQty_b = new BigDecimal(sQty);
								//查原料对应的单位转换率
								List<Map<String, Object>> getUnitRatio = getQUnitRatioData.stream().filter(p->p.get("PLUNO").toString().equals(materialPluNo)).collect(Collectors.toList());
								if (!CollectionUtils.isEmpty(getUnitRatio)){
									BigDecimal unitRatio_b = new BigDecimal(getUnitRatio.get(0).get("UNITRATIO").toString());
									sQty_b = sBaseQty_b.divide(unitRatio_b,2,RoundingMode.HALF_UP);
								}else{
									//原料对应的转换率不存在时，需要报错
									List<level1Elm> checkUnitRatio = datas.stream().filter(p->p.getPluNo().equals(materialPluNo)).collect(Collectors.toList());
									if (!CollectionUtils.isEmpty(checkUnitRatio)){
										throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "原料:"+materialPluNo+" 找不到对应的单位转换率");
									}
								}
								
								//判断getData是否已存在这个商品，例如枣子既可以作为原材料又可以作为成品售卖，可能重复添加
								List<Map<String, Object>> getData = getQData.stream().filter(p->p.get("PLUNO").toString().equals(materialPluNo) && p.get("FEATURENO").toString().equals(" ")).collect(Collectors.toList());
								
								if (!CollectionUtils.isEmpty(getData)){
									sQty_b = sQty_b.add(new BigDecimal(getData.get(0).get("SQTY").toString()));
									getData.get(0).put("SQTY",sQty_b.toPlainString());
								}else {
									Map<String, Object> map1 =new HashMap<>();
									map1.put("PLUNO", materialPluNo);
									map1.put("FEATURENO", " ");  //原料不支持特征码，此处全部默认为空
									map1.put("SQTY", sQty_b);
									getQData.add(map1);
								}
							}
						}
						
						//不管是否是原料商品，都要添加到getQData里面， 举例： 客户同时要货八宝饭和枣子
						List<Map<String, Object>> getData = getQData.stream().filter(p->p.get("PLUNO").toString().equals(pluNo) && p.get("FEATURENO").toString().equals(featureNo)).collect(Collectors.toList());
						//判断getData是否已存在这个商品，例如枣子既可以作为原材料又可以作为成品售卖，可能重复添加
						if (!CollectionUtils.isEmpty(getData)){
							BigDecimal sQty_b = new BigDecimal(sQty);
							sQty_b = sQty_b.add(new BigDecimal(getData.get(0).get("SQTY").toString()));
							getData.get(0).put("SQTY",sQty_b.toPlainString());
						}else {
							Map<String, Object> map1 =new HashMap<>();
							map1.put("PLUNO", pluNo);
							map1.put("FEATURENO", featureNo);
							map1.put("SQTY", sQty);
							getQData.add(map1);
						}
						
					}
				}
				
			}else{
				sql = " select a.pluno,a.featureno,round(sum((a.sale_qty-a.adjust_qty)/c.unitratio)"+"*"+rRate_b.toPlainString()+"/"+difdate+",6) as sqty"
						+ " from dcp_stock_day_static a"
						+ " inner join ("+withPlu+")b on a.pluno=b.pluno"
						+ " inner join dcp_goods_unit c on a.eid=c.eid and a.pluno=c.pluno and b.punit=c.ounit "
						+ " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' and a.partition_date>='"+beginDate+"' and a.partition_date<='"+endDate+"'"
						+ " group by a.pluno,a.featureno";
				getQData = this.doQueryData(sql, null);
			}
			
			
			
			//计算未到货量
			List<Map<String, Object>> getUqtyQData = new ArrayList<>();
			if (cal_type.equals("1")) {
				sql = " select b.pluno,b.featureno,sum(b.pqty-nvl(b.stockin_qty,0)) as uqty from dcp_porder a"
						+ " inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno"
						+ " inner join (" + withPlu + ") c on b.pluno=c.pluno and b.punit=c.punit"
						+ " where a.eid='" + eId + "' and a.shopid='" + shopId + "' and a.rdate >='" + sDate + "' and a.status='2'"
						+ " group by b.pluno,b.featureno";
				getUqtyQData = this.doQueryData(sql, null);
			}
			
			//计算库存量
			sql = " select a.pluno,a.featureno,sum(a.qty) as baseqty,sum(round(a.qty/c.unitratio,6)) as pqty from dcp_stock a"
					+ " inner join ("+withPlu+") b on a.pluno=b.pluno"
					+ " inner join dcp_goods_unit c on a.eid=c.eid and a.pluno=c.pluno and b.punit=c.ounit "
					+ " where a.eid='"+eId+"' and a.organizationno='"+shopId+"'"
					+ " group by a.pluno,a.featureno";
			List<Map<String, Object>> getRefQData = this.doQueryData(sql, null);
			
			
			for (level1Elm par : datas) {
				DCP_POrderOTUsageRes.level1Elm oneLv1 = res.new level1Elm();
				String pluNo = par.getPluNo();
				String featureNo = par.getFeatureNo();
				String punit = par.getPunit();
				String refBaseQty = "0";            //库存量(基准单位)
				String refPqty = "0";               //库存量(要货单位)
				String propQty = "0";               //预估量(要货单位计算)
				String kQty = "0";                  //千元量(要货单位计算)     原本是基准单位
				String kAdjQty = "0";               //千元调整量 (要货单位计算) 原本是基准单位
				String propAdjQty = "0";            //预估调整
				
				
				//获取库存
				List<Map<String, Object>> refQData = getRefQData.stream().filter(p->p.get("PLUNO").toString().equals(pluNo) && p.get("FEATURENO").toString().equals(featureNo)).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(refQData)){
					refBaseQty = refQData.get(0).get("BASEQTY").toString();
					refPqty = refQData.get(0).get("PQTY").toString();
					//【ID1033390】【大万】丰荣店要货选测试门店成品模板点千元用量会报错，体育路店不报错
					if (Check.Null(refBaseQty)){
						refBaseQty = "0";
					}
					if (Check.Null(refPqty)){
						refPqty = "0";
					}
					
				}
				
				
				//用料计算
				List<Map<String, Object>> oneData = getQData.stream().filter(p->p.get("PLUNO").toString().equals(pluNo) && p.get("FEATURENO").toString().equals(featureNo)).collect(Collectors.toList());
				if (!CollectionUtils.isEmpty(oneData)){
					String sqty = oneData.get(0).get("SQTY").toString();  // 实际用量*营业额计算比例/调整系数
					if (Check.Null(sqty)){
						sqty = "0";
					}
					BigDecimal sqty_b = new BigDecimal(sqty);
					sqty_b = sqty_b.setScale(2,RoundingMode.HALF_UP);
					propQty = sqty_b.toPlainString();               //预估量(要货单位计算)
					
					/*  以下是龙海给的3.0计算方式   20230506
					我总结下来：逻辑应该是这样的
					千元量，应该是（销售量/营业额）*1000  ；
					计算量应该是等于 （预估营业额/1000）*千元用量 ；
					最后的预估量 = 计算量-库存量-未到货量；*/
					//kQty =  sqty_b.divide(saleAmt_b.multiply(new BigDecimal("1000")),2,RoundingMode.HALF_UP).toPlainString() ; //千元量 =  计算量/营业额预估/1000
					
					kQty = sqty_b.divide(saleAmt_b,6,RoundingMode.HALF_UP).multiply(new BigDecimal("1000")).toPlainString();
					kAdjQty = kQty;               //千元调整量 (要货单位计算) = 千元量(要货单位计算)
					propAdjQty = propQty;         //预估调整 = 预估量(要货单位计算)
					
					//计算公式   1.预估量=计算量-库存量-未到货量  2.预估量=计算量
					if (cal_type.equals("1")) {
						BigDecimal propQty_b = new BigDecimal(propQty);
						BigDecimal refPqty_b = new BigDecimal(refPqty);
						BigDecimal uqty_b = new BigDecimal("0");
						
						//获取未到货量
						List<Map<String, Object>> uqtyQData = getUqtyQData.stream().filter(p->p.get("PLUNO").toString().equals(pluNo) && p.get("FEATURENO").toString().equals(featureNo)).collect(Collectors.toList());
						if (!CollectionUtils.isEmpty(uqtyQData)){
							uqty_b = new BigDecimal(uqtyQData.get(0).get("UQTY").toString());
						}
						propQty = propQty_b.subtract(refPqty_b).subtract(uqty_b).toPlainString();
					}
				}
				
				oneLv1.setPluNo(pluNo);
				oneLv1.setFeatureNo(featureNo);
				oneLv1.setRefBaseQty(refBaseQty);
				oneLv1.setRefPqty(refPqty);
				oneLv1.setPropQty(propQty);
				oneLv1.setKQty(kQty);
				oneLv1.setKAdjQty(kAdjQty);
				oneLv1.setPropAdjQty(propAdjQty);
				
				res.getDatas().add(oneLv1);
			}
			
			return res;
			
		}catch (Exception e){
			
			try {
				StringWriter errors = new StringWriter();
				PrintWriter pw = new PrintWriter(errors);
				e.printStackTrace(pw);
				
				pw.flush();
				pw.close();
				
				errors.flush();
				errors.close();
				
				logger.error("服务名：DCP_POrderOTUsage 异常： " + errors  + " ");
				
			}catch (Exception ignored) {
			
			}
			
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
		}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_POrderOTUsageReq req) throws Exception {
		return null;
	}
	
	
}
