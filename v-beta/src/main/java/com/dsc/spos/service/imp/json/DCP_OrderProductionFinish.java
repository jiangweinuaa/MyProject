package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderProductionFinish_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderProductionFinish_OpenReq.OrderList;
import com.dsc.spos.json.cust.res.DCP_OrderProductionFinish_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderProductionFinish_OpenRes.levRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import microsoft.exchange.webservices.data.core.IFileAttachmentContentHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 订单生产完工入库DCP_OrderProductionFinish
 *
 * @author 规格地址   <a href="http://183.233.190.204:10004/project/148/interface/api/3206">...</a>
 *
 */
public class DCP_OrderProductionFinish extends SPosAdvanceService<DCP_OrderProductionFinish_OpenReq, DCP_OrderProductionFinish_OpenRes> {

	@Override
	protected void processDUID(DCP_OrderProductionFinish_OpenReq req, DCP_OrderProductionFinish_OpenRes res) throws Exception {
		levRes resDatas = res.new levRes();
		resDatas.setErrorOrderList(new ArrayList<>());
		res.setDatas(resDatas);

		try {
			String eId = req.getRequest().geteId();
			String companyId = req.getBELFIRM();
			String shopId = req.getRequest().getShopId();
			String opNo = req.getRequest().getOpNo();
			String opName = req.getRequest().getOpName();
			String opType = req.getRequest().getOpType();
			List<OrderList> orderList = req.getRequest().getOrderList();
			List<String> sucessOrderList = new ArrayList<>();//执行成功的 单号列表
			List<Map<String, Object>> failOrderList = new ArrayList<>();//失败的列表,以及错误描述
			boolean isAllSucess = true;
			String logStrStart;

			MyCommon MC = new MyCommon();

			//循环单号，有一个失败break,后面不执行了
			for (OrderList orderNoList : orderList)
			{

				String orderNo = orderNoList.getOrderNo();
				logStrStart="循环操作【生成完成】单号orderNo="+orderNo+",";
				try
				{
					String orderSql = " select a.*,  "
							+ " b.item, b.pluNo , b.pluName , b.PluBarcode, b.featureNo , b.featureName , b.goodsUrl ,b.specName , b.attrName , "
							+ " b.sUnit , b.sUnitName, b.warehouse  , b.warehouseName , "
							//+ " b.qty , "
							//+ " (case when (a.machshop<>a.shop) then b.qty-b.shopqty else b.qty end) qty ,"
							+ " (case when d.GOODSTYPE='1' then 0  when (a.machshop<>a.shop) then b.qty-b.shopqty else b.qty end ) qty ,"
							+ " b.oldPrice , b.oldAmt , b.price ,b.disc, b.Amt ,"
							+ " C.unit AS baseUnit , C.Unitratio ,d.GOODSTYPE  "
							+ " from DCP_Order a"
							+ " left join DCP_ORDER_DETAIL B  on a.eId = b.eId and a.orderNO = b.orderNO "
							+ " LEFT JOIN Dcp_Goods_Unit C ON B.eId = C.eId AND B.pluNo = C.pluNO AND B.Sunit = C.oUnit AND C.Prod_Unit_Use = 'Y' "
							+ " LEFT JOIN Dcp_Goods D ON B.eId = D.eId AND B.pluNo = D.pluNO "
							+ " where a.eid = '"+eId+"'  "
							+ " and a.OrderNo='"+orderNo+"' ";
					HelpTools.writelog_waimai(logStrStart+"查询订单sql="+orderSql);
					List<Map<String, Object>> orderDatas = this.doQueryData(orderSql, null);
					if(orderDatas==null||orderDatas.isEmpty())
					{
						HelpTools.writelog_waimai(logStrStart+"没有查询到该订单数据！");
						isAllSucess = false;
						Map<String, Object> errorMap = new HashMap<>();
						errorMap.put("orderNo", orderNo);
						errorMap.put("errorDesc", "没有查询到该订单数据！");
						failOrderList.add(errorMap);
						break;
					}

					Map<String, Object> map = orderDatas.get(0);
					String status = map.get("STATUS").toString();// 订单状态
					String productStatus = map.get("PRODUCTSTATUS").toString();//生产状态
					String loadDocType = map.get("LOADDOCTYPE").toString();
					String channelId = map.get("CHANNELID").toString();
					String machShop = map.get("MACHSHOP").toString();
					//String mainWarehouse = req.getIn_cost_warehouse(); //默认出货成本仓

					HelpTools.writelog_waimai(logStrStart+"数据库中 订单状态status="+status+",生产状态productStatus="+productStatus+",生产机构machShop="+machShop+",当前传入机构shopId="+shopId);
					boolean canProductFinishFlag = true;
					StringBuffer errMsg = new StringBuffer();
					//订单状态  status ==1 订单开立 或  status == 2 已接单，生产状态为 4 生产接单
					if(("1".equals(status)||"2".equals(status)||"4".equals(status)) && "4".equals(productStatus))
					{

					}
					else
					{
						canProductFinishFlag = false;
						errMsg.append("订单开立或已接单，且生产状态=4（生产接单）的单据才可操作生成完成， ");
					}

					//当前机构为订单生产机构
					if(shopId.equals(machShop))
					{

					}
					else
					{
						canProductFinishFlag = false;
						errMsg.append("当前机构非订单生产机构，不能进行生产完成， ");
					}
					//获取公司别，取进货价
                    String sql_belfirm = " select belfirm from dcp_org where eid='" + eId + "' and organizationno='" + shopId + "' ";
                    List<Map<String, Object>> getQDataBelfirm = this.doQueryData(sql_belfirm, null);
                    companyId = getQDataBelfirm.get(0).get("BELFIRM").toString();

					//【ID1031628】黛慕 订单中心订单完工入库需优先关联生产模板，根据生产模板的仓库进行扣料 by jinzma 20230901
					String machShopWarehouse_in_def = "";//查询生产门店的默认收货仓      原变量：  machShopWarehouse_in
					String machShopWarehouse_out_def = "";//查询生产门店的默认出货仓     原变量：  machShopWarehouse_out
					// 查询配送门店的默认收货仓
					String warehouseSql = " select IN_COST_WAREHOUSE,OUT_COST_WAREHOUSE from DCP_ORG where eId = '"+eId+"' and organizationNo = '"+machShop+"'";
					HelpTools.writelog_waimai(logStrStart+"查询生产机构默认收货/出货仓库sql="+warehouseSql);
					List<Map<String, Object>> warehouseDatas = this.doQueryData(warehouseSql, null);

					if(warehouseDatas != null && !warehouseDatas.isEmpty()){
						machShopWarehouse_in_def = warehouseDatas.get(0).get("IN_COST_WAREHOUSE").toString(); //门店默认收货成本仓
						machShopWarehouse_out_def = warehouseDatas.get(0).get("OUT_COST_WAREHOUSE").toString();//门店默认出货成本仓

						HelpTools.writelog_waimai(logStrStart+"查询生产机构默认收货仓库IN_COST_WAREHOUSE="+machShopWarehouse_in_def+",出货仓库OUT_COST_WAREHOUSE="+machShopWarehouse_out_def);
					}

					if(Check.Null(machShopWarehouse_in_def) || Check.Null(machShopWarehouse_out_def)) {
						canProductFinishFlag = false;
						errMsg.append("生产机构对应的默认收货/出货成本仓为空， ");
					}

					if(!canProductFinishFlag)
					{
						HelpTools.writelog_waimai(logStrStart+errMsg.toString());
						isAllSucess = false;
						Map<String, Object> errorMap = new HashMap<>();
						errorMap.put("orderNo", orderNo);
						errorMap.put("errorDesc", errMsg.toString());
						failOrderList.add(errorMap);
						break;
					}
					/*****************过滤不需要生产的商品**************************/
					boolean allGoodsNoProduct = true;//订单上所有商品都不用生产
					List<String> pluBomArrList = new ArrayList<>();//查询bom的pluNo列表
					for (Map<String, Object> detailMap : orderDatas)
					{
						String oItem = detailMap.get("ITEM").toString(); // 订单明细商品的 item 编号。 也对应给到 DCP_PSTOCKIN_DETAIL 的oItem
						String pluNo = detailMap.get("PLUNO").toString(); // 订单商品
						String orderQty = detailMap.get("QTY").toString();
						String goodsType = detailMap.getOrDefault("GOODSTYPE","").toString();//1-不需要生产，2或者空-需要生产
						BigDecimal orderQty_machShop = new BigDecimal(orderQty);
						if(orderQty_machShop.compareTo(BigDecimal.ZERO)<=0)
						{
							HelpTools.writelog_waimai(logStrStart+"需要过滤商品，商品属性不需要生产或需要生产数量qty-shopqty="+orderQty+",过滤的商品项次item="+oItem+",商品pluNo="+pluNo);
							continue;
						}
						if (pluNo==null||pluNo.trim().isEmpty())
						{
							HelpTools.writelog_waimai(logStrStart+"需要过滤商品，商品pluno为空,过滤的商品项次item="+oItem+",商品pluNo="+pluNo);
							continue;
						}
						if (pluBomArrList.contains(pluNo))
						{
							continue;
						}
						allGoodsNoProduct = false;
						pluBomArrList.add(pluNo);
					}

					//*********************查询pluno对应的bom****************************/
					// 这里过滤订单上的所有商品，  然后查询这些商品的BOM 资料
					Map<String, Boolean> condition2 = new HashMap<>(); //查詢條件
					condition2.put("PLUNO", true);
					//调用过滤函数
					List<Map<String, Object>> getPluDatas = MapDistinct.getMap(orderDatas, condition2);


					//String[] pluArr = new String[getPluDatas.size()] ;
					//int i=0;

					//获取成品 和 原料的 进货价 和 零售价 商品mapList
					List<Map<String, Object>> pluList = new ArrayList<>();
					for (Map<String, Object> pluMap : getPluDatas)
					{
						String pluNo = "";
						String sUnit = pluMap.get("SUNIT").toString(); // 订单上商品的销售单位
						if(!Check.Null(pluMap.get("PLUNO").toString())){
							pluNo = pluMap.get("PLUNO").toString();

							Map<String, Object> pluMap2 = new HashMap<>();
							pluMap2.put("PLUNO", pluNo);//// 订单商品
							pluMap2.put("PUNIT", sUnit); //订单上的销售单位
                            pluMap2.put("BASEUNIT",pluMap.get("BASEUNIT").toString());
                            pluMap2.put("UNITRATIO",pluMap.get("UNITRATIO").toString());
							pluList.add(pluMap2);
						}
						//pluArr[i] = pluNo;
						//i++;
					}

					String pluStr = ""; //MC.arrayToString(pluArr);
					if (pluBomArrList!=null&&!pluBomArrList.isEmpty())
					{
						StringBuffer str2 = new StringBuffer("");
						for (String ss1 : pluBomArrList)
						{
							str2.append("'" + ss1 + "'"+ ",");
						}
						if (str2.length()>0)
						{
							str2.deleteCharAt(str2.length()-1);
						}
						pluStr = str2.toString();
					}

					List<Map<String, Object>> getBomDatas = new ArrayList<>();
					//需要生产的，一定要有bom，不需要生产的可以不用bom
					if (!pluStr.trim().isEmpty())
					{
						//region 获取商品BOM信息
						String bomSql = ""
								+ " select distinct a.pluno ,A.eid,  A.Bomno , A.bomType , A.Restrictshop , A.Pluno , A.UNIT , A.MULQTY , A.EFFDATE , "
								+ " C.material_PluNo AS materialPluNo , C.material_Unit AS materialUnit , c.material_Qty AS materialQty , C.isBuckle ,"
								+ " C.Qty , C.isreplace , c.sortId ,  to_char(B.SHOPID ) AS shopId , d.unitRatio ,"
								+ " E.BOM_UNIT , E.Baseunit , E.sunit,f.udlength "
								+ " from dcp_bom a "
								+ " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopId+"' "
								+ " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno "
								+ " and trunc(c.material_bdate)<=trunc(sysdate) and trunc(c.material_edate)>=trunc(sysdate) "
								//【ID1029991】【大拇指-3.0】完工入库红冲单中的单位和写入到库存流水表中的单位不一致  by jinzma 20221129
								//+ " inner join dcp_goods_unit d on a.eid=d.eid and a.pluno=d.pluno and a.unit=d.ounit and d.prod_unit_use='Y' "
								+ " inner join dcp_goods_unit d on a.eid=d.eid and c.material_pluno=d.pluno and c.material_unit=d.ounit and d.prod_unit_use='Y'"

								+ " INNER JOIN Dcp_Goods e ON C.eId = E.EID AND C.MATERIAL_Pluno = E.Pluno AND E.Status = '100' "
								+ " left  join dcp_unit f on c.eid=f.eid and c.material_unit=f.unit "
								+ " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and a.bomtype = '0'  "
								+ " AND A.pluNo IN ("+pluStr+")"
								+ " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))";

						//endregion
						HelpTools.writelog_waimai(logStrStart+"查询需要生产的商品bom信息sql="+bomSql);
						getBomDatas = this.doQueryData(bomSql, null);

						if(getBomDatas == null || getBomDatas.isEmpty())
						{
							HelpTools.writelog_waimai(logStrStart+"查询需要生产的商品bom信息为空！");
							isAllSucess = false;
							Map<String, Object> errorMap = new HashMap<>();
							errorMap.put("orderNo", orderNo);
							errorMap.put("errorDesc", "查询需要生产的商品bom信息为空！");
							failOrderList.add(errorMap);
							break;
						}


						for (Map<String,Object> bomMap : getBomDatas) {
							String materialPluNo = bomMap.get("MATERIALPLUNO").toString();
							String materialUnit = bomMap.get("MATERIALUNIT").toString(); //原料用料单位

							if(!Check.Null(materialPluNo)){
								Map<String, Object> pluMap = new HashMap<>();
								pluMap.put("PLUNO", materialPluNo);
								pluMap.put("PUNIT", materialUnit); //原料用料单位
                                pluMap.put("BASEUNIT",bomMap.get("BASEUNIT").toString());
                                pluMap.put("UNITRATIO",bomMap.get("UNITRATIO").toString());
								pluList.add(pluMap);
							}
						}
					}




					List<Map<String, Object>> getPluPrice = MC.getSalePrice_distriPrice(dao,eId,companyId, shopId,pluList,companyId);


					//*****************************开始组装sql*************************************/
					/* 以下步骤 :
					 * 1. 查询订单商品的子料（BOM）信息
					 * 2. 生成完工入库单，DCP_PSTOCKIN, DCP_PSTOCKIN_DETAIL , DCP_PSTOCKIN_MATERIAL
					 * 3. 完工入库单 主料写入库流水， 子料写出库流水
					 * 4. 更新订单生产状态
					 * 5. 写订单日志
					 */

					String createBy = req.getOpNO();
					Calendar cal = Calendar.getInstance();//获得当前时间
					SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
					String createDate = dfDate.format(cal.getTime());
					String createTime = dfTime.format(cal.getTime());



					String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);



					String pStockInNO = getPStockInNO(req);
					boolean isNeedStockSync = false;

					//****************  DCP_PSTOCKIN *******************
					String[] columns1 = {
							"SHOPID", "ORGANIZATIONNO","BDATE","PSTOCKIN_ID","CREATEBY", "CREATE_DATE", "CREATE_TIME","TOT_PQTY",
							"TOT_AMT", "TOT_CQTY", "EID","PSTOCKINNO", "MEMO", "STATUS", "PROCESS_STATUS","OFNO","PTEMPLATENO",
							"ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","OTYPE","WAREHOUSE","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
							"SUBMITBY","SUBMIT_DATE","SUBMIT_TIME","DOC_TYPE","TOT_DISTRIAMT","ACCOUNT_CHATUSERID",
							"UPDATE_TIME","TRAN_TIME"
					};
					//**************** DCP_PSTOCKIN_DETAIL ********************
					String[] columnsName = {
							"PSTOCKINNO", "SHOPID", "item", "oItem", "pluNO","punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
							"price", "amt", "EID", "organizationNO","task_qty", "scrap_qty", "mul_Qty", "bsNO",
							"memo", "gDate", "gTime", "WAREHOUSE",
							"BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE","FEATURENO"
					};
					//**************** DCP_PSTOCKIN_MATERIAL *******************
					String[] matColumnsName = {
							"MITEM", "ITEM", "WAREHOUSE",
							"PLUNO","PUNIT",
							"PQTY","PRICE","AMT",
							"FINALPRODBASEQTY", "RAWMATERIALBASEQTY",
							"EID","ORGANIZATIONNO","PSTOCKINNO",
							"SHOPID","MPLUNO","BASEUNIT",
							"BASEQTY","UNIT_RATIO",
							"BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE","ISBUCKLE","FEATURENO"
					};

					// 写原料流水表
					int materialItem = 1;
					int detailItem = 1;
					// 下面这仨字段需要统计，从单身中各项数量 金额统计
					BigDecimal tot_pqty = new BigDecimal(0); //合格总数
					BigDecimal tot_amt = new BigDecimal(0); //零售价总金额

					String stockType = "1";     //成品库存方向  1 入   -1出
					String stockDocType = "08";  //成品库存单据类型，完工入库
					String matStockType = "-1";     //原料库存方向  1入  -1出
					String matStockDocType = "11";  //原料库存单据类型:   11加工原料倒扣

					BigDecimal totDistAmt = new BigDecimal(0); // 进货价总金额
					Boolean isNeedProduction = false;//当前生产门店可能没有生产具体的商品，无须入库




					//【ID1031628】黛慕 订单中心订单完工入库需优先关联生产模板，根据生产模板的仓库进行扣料 by jinzma 20230831
					String weekOfDay = this.getWeekDay();
					String day = this.getDay();
					String doubleDay = "1";    //单日
					if(Integer.parseInt(day) % 2==0) {
						doubleDay = "2";       //双日
					}
					String MultiWarehouse = PosPub.getPARA_SMS(dao,eId,shopId,"MultiWarehouse"); //是否支持多仓
					if (Check.Null(MultiWarehouse)){
						MultiWarehouse="N";
					}


					for (Map<String, Object> detailMap : orderDatas)
					{
						String oItem = detailMap.get("ITEM").toString(); // 订单明细商品的 item 编号。 也对应给到 DCP_PSTOCKIN_DETAIL 的oItem
						String pluNo = detailMap.get("PLUNO").toString(); // 订单商品

						String pluName = detailMap.get("PLUNAME").toString();
						String pluBarCode = detailMap.get("PLUBARCODE").toString();
						String featureNo = " ";

						if(!Check.Null( detailMap.get("FEATURENO").toString())){
							featureNo = detailMap.get("FEATURENO").toString();
						}

						String featureName = detailMap.get("FEATURENAME").toString();
						String goodsUrl = detailMap.get("GOODSURL").toString();
						String specName = detailMap.get("SPECNAME").toString();
						String attrName = detailMap.get("ATTRNAME").toString();
						String sUnit = detailMap.get("SUNIT").toString();
						//String warehouse = detailMap.get("WAREHOUSE").toString();
						String detailBaseUnit = detailMap.get("BASEUNIT").toString();
						String detailUnitRatio = detailMap.get("UNITRATIO").toString();

						String orderQty = detailMap.get("QTY").toString();
						String goodsType = detailMap.getOrDefault("GOODSTYPE","").toString();//1-不需要生产，2或者空-需要生产
						//判断下 可能村长qty=0
						BigDecimal orderQty_machShop = new BigDecimal(orderQty);
						if(orderQty_machShop.compareTo(BigDecimal.ZERO)<=0)
						{
							HelpTools.writelog_waimai(logStrStart+"需要过滤商品，商品属性，qty-shopqty="+orderQty+",过滤的商品项次item="+oItem+",商品pluNo="+pluNo);
							continue;
						}

						String price = detailMap.get("PRICE").toString();
						String amt = detailMap.get("AMT").toString();

						// 成品基准单位对应数量
						String mainPluNoBaseQtyStr = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, detailBaseUnit, orderQty+"");

						String detailMulQty = "0";

						//【ID1031628】黛慕 订单中心订单完工入库需优先关联生产模板，根据生产模板的仓库进行扣料 by jinzma 20230831
						String warehouse_in = machShopWarehouse_in_def;       //取组织表默认仓库
						String warehouse_out = machShopWarehouse_out_def;     //取组织表默认仓库

						if (MultiWarehouse.equals("Y")){
							String sql = " select c.goodswarehouse,c.materialwarehouse from dcp_ptemplate a"
									+ " inner join dcp_ptemplate_detail b on a.eid=b.eid and a.ptemplateno=b.ptemplateno and a.doc_type=b.doc_type and b.pluno='"+pluNo+"'"
									+ " inner join dcp_ptemplate_shop c on a.eid=c.eid and a.ptemplateno=c.ptemplateno and a.doc_type=c.doc_type and c.shopid='"+shopId+"'"
									+ " where a.eid='"+eId+"' and a.doc_type='2'"
									+ " and ("
									+ " a.time_type='0'"
									+ " or (a.time_type='1' and a.time_value like '%"+doubleDay+"%')"
									+ " or (a.time_type='2' and a.time_value like '%"+weekOfDay+"%')"
									+ " or (a.time_type='3' and ';'||a.time_value||';' like '%%;"+ Integer.valueOf(day) +";%%')"
									+ " or (a.time_type='3' and a.time_value like '%%"+day+"%%')"
									+ " )"
									+ " order by a.tran_time desc";
							List<Map<String, Object>> getQData = this.doQueryData(sql, null);
							if (getQData != null && !getQData.isEmpty()){
								if (!Check.Null(getQData.get(0).get("GOODSWAREHOUSE").toString())){
									warehouse_in = getQData.get(0).get("GOODSWAREHOUSE").toString();
								}
								if (!Check.Null(getQData.get(0).get("MATERIALWAREHOUSE").toString())){
									warehouse_out = getQData.get(0).get("MATERIALWAREHOUSE").toString();
								}
							}
						}



						//【ID1029948】【大拇指3.0】订单转完工入库包含了没有配方的商品  by jinzma
						//是否存在BOM资料
						boolean existBom = false;

						/**
						 *
						 * 原料表  DCP_PstockIn_Material
						 */
						//region 插入原料表信息 以及原料库存流水 DCP_PstockIn_Material ，
						for (Map<String,Object> bomMap : getBomDatas) {
							if(pluNo.equals(bomMap.get("PLUNO"))){
								existBom = true;

								detailMulQty = bomMap.get("MULQTY").toString(); //成品倍量， 完工入库明细表 DCP_PStockIn_Detail 上需要记录
								String detailUnit = bomMap.get("UNIT").toString(); // 成品BOM用料单位， 用于计算成品录入单位到BOM用料单位的 数量
								String materialPluNo = bomMap.get("MATERIALPLUNO").toString();
								String materialUnit = bomMap.get("MATERIALUNIT").toString(); //原料BOM用料单位
								String materialBaseUnit = bomMap.get("BASEUNIT").toString(); //原料基础单位
								String materialUnitRatio = bomMap.get("UNITRATIO").toString(); //原料单位换算率
								String isBuckle = bomMap.get("ISBUCKLE").toString(); // 是否扣料件
								String materialUnit_UdLength = bomMap.get("UDLENGTH").toString(); //原料单位长度
								if (!PosPub.isNumeric(materialUnit_UdLength)){
									materialUnit_UdLength="6";
								}

								Map<String, Object> condiV = new HashMap<>();
								condiV.put("PLUNO",materialPluNo);
								condiV.put("PUNIT",materialUnit); //原料用料单位
								List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);

								String materialPrice = "0";
								String materialDistriPrice = "0";
								if(priceList!=null && !priceList.isEmpty())
								{
									materialPrice=priceList.get(0).get("PRICE").toString(); //原料用料单位materialUnit对应零售价
									materialDistriPrice=priceList.get(0).get("DISTRIPRICE").toString();//原料单位进货价
								}

								// 开始计算原料对应数量
								String mainPLuNoUnit = bomMap.get("UNIT").toString();

								//mainPluNoBaseQty 表示订单商品单位 对应 成品单位用量

								String finalProdBaseQtyStr = bomMap.get("QTY").toString(); //成品基础用量
								String rawMaterialBaseQtyStr = bomMap.get("MATERIALQTY").toString();  //原料基础用量

								// 成品BOM用料单位对应数量
								String mainPluBomUnitQtyStr = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, detailUnit, orderQty+"");

								BigDecimal mainPluBomUnitQty = new BigDecimal(mainPluBomUnitQtyStr);
								BigDecimal finalProdBaseQty = new BigDecimal(finalProdBaseQtyStr);
								BigDecimal rawMaterialBaseQty = new BigDecimal(rawMaterialBaseQtyStr);

								//【ID1027938】【菲尔雪3.0】门店订单中心，点生产完成的生成的完工入库单的逻辑不对 by jinzma 20220811
								//materialQty = mainPluBomUnitQty.multiply(finalProdBaseQty).divide(rawMaterialBaseQty,Integer.parseInt(materialUnit_UdLength),RoundingMode.HALF_UP);
								//【ID1030740】【希悦3.0】订单转完工入库扣料不对
								BigDecimal materialQty = mainPluBomUnitQty.multiply(rawMaterialBaseQty).divide(finalProdBaseQty,Integer.parseInt(materialUnit_UdLength),RoundingMode.HALF_UP);
								// 数量 和 金额 保留位数， 应该根据单位长度保留
								// materialQty = materialQty.setScale(2, BigDecimal.ROUND_UP);
								// BigDecimal materialAmt = materialQty.multiply(new BigDecimal(materialPrice)).setScale(2, BigDecimal.ROUND_UP);
								BigDecimal materialAmt = materialQty.multiply(new BigDecimal(materialPrice)); // 零售价对应金额应该也要保留位数
								BigDecimal materialDistAmt = materialQty.multiply(new BigDecimal(materialDistriPrice)); // 进货价对应金额应该也要保留位数

								String materialWarehouse = warehouse_out ; //原料仓库，取门店默认出货仓库

								String materialBaseQty = PosPub.getUnitConvert(dao, eId, materialPluNo, materialUnit, materialBaseUnit, materialQty+"");
								String batchNo = ""; //bomMap.get("").toString(); //批号

								int insColCt2 = 0;

								DataValue[] matColumnsVal = new DataValue[matColumnsName.length];
								for (int j = 0; j < matColumnsVal.length; j++) {
									String matKeyVal = null;

									switch (j) {
										case 0:
											matKeyVal = detailItem + ""; //字段值来源是 订单商品项次。 暂不考虑套餐商品（套餐商品不能转完工，没有BOM信息）
											break;
										case 1:
											//matKeyVal = mat.getMaterial_item();
											matKeyVal = materialItem+"";
											break;
										case 2:
											matKeyVal = materialWarehouse;
											break;
										case 3:
											matKeyVal = materialPluNo;//原料商品编码
											break;
										case 4:
											matKeyVal = materialUnit;// 原料用料单位
											break;
										case 5:
											matKeyVal = materialQty + "";// 原料数量
											break;
										case 6:
											matKeyVal = materialPrice;
											if(Check.Null(matKeyVal))
											{
												matKeyVal="0";
											}
											break;
										case 7:
											matKeyVal = materialAmt + ""; //零售价对应金额
											break;
										case 8:
											matKeyVal = finalProdBaseQtyStr ; ///MATERIAL_FINALPRODBASEQTY
											break;
										case 9:
											matKeyVal = rawMaterialBaseQtyStr;    ///MATERIAL_RAWMATERIALBASEQTY
											break;
										case 10:
											matKeyVal = eId;
											break;
										case 11:
											matKeyVal = shopId;
											break;
										case 12:
											matKeyVal = pStockInNO; //完工入库单号
											break;
										case 13:
											matKeyVal = shopId;
											break;
										case 14:
											matKeyVal = pluNo; //MpluNO 主商品PLUNO,非原料PLUNO
											break;
										case 15:
											matKeyVal = materialBaseUnit;//基准单位
											break;
										case 16:
											matKeyVal = materialBaseQty; //基准单位对应数量
											break;
										case 17:
											matKeyVal = materialUnitRatio; //原料单位换算率
											break;
										case 18:
											matKeyVal = batchNo; //批号
											break;
										case 19:
											matKeyVal = "";  // 生产日期 Prod_Date
											break;
										case 20:
											matKeyVal = materialDistriPrice ; //原料进货价
											if (Check.Null(matKeyVal))
												matKeyVal = "0";
											break;
										case 21:
											matKeyVal = materialDistAmt + "";
											if (Check.Null(matKeyVal))
												matKeyVal="0";
											break;
										case 22:
											matKeyVal = accountDate;
											break;
										case 23:
											matKeyVal = isBuckle; //是否扣料件
											if (Check.Null(matKeyVal)||!matKeyVal.equals("N"))
											{
												matKeyVal="Y";
											}
											break;
										case 24:
											matKeyVal = " ";//原料特征码 featureNo
											if (Check.Null(matKeyVal))
												matKeyVal=" ";
											break;
										default:
											break;
									}

									if (matKeyVal != null) {
										insColCt2++;
										if (j == 6 || j == 7){
											matColumnsVal[j] = new DataValue(matKeyVal, Types.FLOAT);
										}else{
											matColumnsVal[j] = new DataValue(matKeyVal, Types.VARCHAR);
										}
									}
									else {
										matColumnsVal[j] = null;
									}
								}
								String[] columns3  = new String[insColCt2];
								DataValue[] insValue3 = new DataValue[insColCt2];
								// 依照傳入參數組譯要insert的欄位與數值；
								insColCt2 = 0;

								for (int k=0;k<matColumnsVal.length;k++){
									if(matColumnsVal[k] != null){
										columns3[insColCt2] = matColumnsName[k];
										insValue3[insColCt2] = matColumnsVal[k];
										insColCt2 ++;
										if (insColCt2 >= insValue3.length)
											break;
									}
								}
								InsBean ib3 = new InsBean("DCP_PSTOCKIN_MATERIAL", columns3);
								ib3.addValues(insValue3);
								this.addProcessData(new DataProcessBean(ib3));

								//region 写原料库存流水
								String procedure="SP_DCP_StockChange";
								Map<Integer,Object> inputParameter = new HashMap<>();
								inputParameter.put(1,eId);                                      //--企业ID
								inputParameter.put(2,shopId);                                   //--组织
								inputParameter.put(3,matStockDocType);                             //--单据类型
								inputParameter.put(4,pStockInNO);	                            //--单据号
								inputParameter.put(5,materialItem);                               //--单据行号
								inputParameter.put(6,matStockType);                                //--异动方向 1=加库存 -1=减库存
								inputParameter.put(7,createDate);                                    //--营业日期 yyyy-MM-dd
								inputParameter.put(8,materialPluNo);                                    //--品号
								inputParameter.put(9," ");                                //--特征码
								inputParameter.put(10,materialWarehouse);                           //--仓库
								inputParameter.put(11,"");                                      //--批号
								inputParameter.put(12,materialUnit);                                   //--交易单位
								inputParameter.put(13,materialQty);                                //--交易数量
								inputParameter.put(14,materialBaseUnit);                          //--基准单位
								inputParameter.put(15,materialBaseQty);                     //--基准数量
								inputParameter.put(16,materialUnitRatio);                         //--换算比例
								inputParameter.put(17,materialPrice);                             //--零售价
								inputParameter.put(18,materialAmt);                                     //--零售金额
								inputParameter.put(19,materialDistriPrice);                       //--进货价
								inputParameter.put(20,materialDistAmt);                         //--进货金额
								inputParameter.put(21,accountDate);                             //--入账日期 yyyy-MM-dd
								inputParameter.put(22,"");                                      //--批号的生产日期 PROD_DATE yyyy-MM-dd
								inputParameter.put(23,createDate);                                   //--单据日期
								inputParameter.put(24,"");                                      //--异动原因
								inputParameter.put(25,"订单转完工，原料出库");                  //--异动描述
								inputParameter.put(26,createBy);                                //--操作员

								ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
								this.addProcessData(new DataProcessBean(pdb));

								//endregion
								isNeedStockSync = true;
								materialItem++;

							}

						}
						//endregion

						//【ID1029948】【大拇指3.0】订单转完工入库包含了没有配方的商品  by jinzma 20221129
						if (!existBom){
							continue;
						}

						//region 插入明细表 DCP_PStockIn_Detail
						Map<String, Object> condiV = new HashMap<>();
						condiV.put("PLUNO",pluNo); //订单上的商品编码
						condiV.put("PUNIT",sUnit); //订单上的商品单位
						List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);

						String detailPrice = "0"; // 成品零售价， 重新取一遍，这个字段先保留，和销售单上的零售价不一样，
						String detailDistriPrice = "0";//成品进货价
						if(priceList!=null && !priceList.isEmpty())
						{
							detailPrice=priceList.get(0).get("PRICE").toString(); //成品零售价
							detailDistriPrice=priceList.get(0).get("DISTRIPRICE").toString();//成品进货价
						}
						BigDecimal detailDistriAmt = new BigDecimal(orderQty).multiply(new BigDecimal( detailDistriPrice));

						totDistAmt = detailDistriAmt.add( totDistAmt ) ;
						tot_pqty = new BigDecimal(orderQty).add( tot_pqty ) ;
						tot_amt = new BigDecimal(amt).add(tot_amt);

						DataValue[] insValue1 = new DataValue[]{
								new DataValue(pStockInNO, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(detailItem, Types.VARCHAR),
								new DataValue(oItem, Types.VARCHAR), // 来源项次，  订单上的商品所属项次
								new DataValue(pluNo, Types.VARCHAR), //
								new DataValue(sUnit, Types.VARCHAR),
								new DataValue(orderQty, Types.VARCHAR),
								new DataValue(detailBaseUnit, Types.VARCHAR),
								new DataValue(mainPluNoBaseQtyStr, Types.VARCHAR),
								new DataValue(detailUnitRatio, Types.VARCHAR),
								new DataValue(price, Types.VARCHAR),
								new DataValue(amt, Types.VARCHAR),
								new DataValue(eId, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR),
								new DataValue("0", Types.VARCHAR), //task_Qty //任务数， 订单完工入库定死写 0 ，
								new DataValue("0", Types.VARCHAR),// scrap_Qty 报损数， 写 0 ，没有报损

								new DataValue(detailMulQty, Types.VARCHAR),// mul_qty 倍量, 从BOM资料中获取到
								new DataValue("", Types.VARCHAR), // bsno 报损原因。
								new DataValue("", Types.VARCHAR), // memo 备注
								new DataValue("", Types.VARCHAR), //gDate 蛋糕需求日期
								new DataValue("", Types.VARCHAR), //gTime 蛋糕需求时间
								new DataValue(warehouse_in, Types.VARCHAR),
								new DataValue("", Types.VARCHAR), // batch_no 批号

								new DataValue("", Types.VARCHAR), // prod_date 生产日期
								new DataValue(detailDistriPrice, Types.VARCHAR), // distriPrice成品进货价
								new DataValue(detailDistriAmt, Types.VARCHAR), // DISTRIAMT 成品进货价合计
								new DataValue(accountDate, Types.VARCHAR), // 入账日期, 默认给当天
								new DataValue(featureNo, Types.VARCHAR) // featureNo 特征码

						};
						InsBean ib1 = new InsBean("DCP_PSTOCKIN_DETAIL", columnsName);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

						//endregion

						//region 写成品库存流水
						String procedure="SP_DCP_StockChange";
						Map<Integer,Object> inputParameter = new HashMap<>();
						inputParameter.put(1,eId);                                      //--企业ID
						inputParameter.put(2,shopId);                                   //--组织
						inputParameter.put(3,stockDocType);                             //--单据类型
						inputParameter.put(4,pStockInNO);	                            //--单据号
						inputParameter.put(5,detailItem);                               //--单据行号
						inputParameter.put(6,stockType);                                //--异动方向 1=加库存 -1=减库存
						inputParameter.put(7,createDate);                                    //--营业日期 yyyy-MM-dd
						inputParameter.put(8,pluNo);                                    //--品号
						inputParameter.put(9,featureNo);                                //--特征码
						inputParameter.put(10,warehouse_in);                               //--仓库
						inputParameter.put(11,"");                                      //--批号
						inputParameter.put(12,sUnit);                                   //--交易单位
						inputParameter.put(13,orderQty);                                //--交易数量
						inputParameter.put(14,detailBaseUnit);                          //--基准单位
						inputParameter.put(15,mainPluNoBaseQtyStr);                     //--基准数量
						inputParameter.put(16,detailUnitRatio);                         //--换算比例
						inputParameter.put(17,detailPrice);                             //--零售价
						inputParameter.put(18,amt);                                     //--零售金额
						inputParameter.put(19,detailDistriPrice);                       //--进货价
						inputParameter.put(20,detailDistriAmt);                         //--进货金额
						inputParameter.put(21,accountDate);                             //--入账日期 yyyy-MM-dd
						inputParameter.put(22,"");                                      //--批号的生产日期 PROD_DATE yyyy-MM-dd
						inputParameter.put(23,createDate);                                   //--单据日期
						inputParameter.put(24,"");                                      //--异动原因
						inputParameter.put(25,"订单转完工，成品入库");                  //--异动描述
						inputParameter.put(26,createBy);                                //--操作员

						ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
						this.addProcessData(new DataProcessBean(pdb));
						detailItem ++;
						//endregion
						isNeedStockSync =true;

						isNeedProduction = true;
					}




					if (isNeedProduction)
					{
						//region 写完工入库主表 DCP_PSTOCKIN
						String pStockIn_Id = "";
						DataValue[]	insValue1 = new DataValue[]{
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(createDate, Types.VARCHAR),
								new DataValue(pStockIn_Id, Types.VARCHAR),
								new DataValue(opNo, Types.VARCHAR),
								new DataValue(createDate, Types.VARCHAR),
								new DataValue(createTime, Types.VARCHAR),
								new DataValue(tot_pqty, Types.VARCHAR),
								new DataValue(tot_amt, Types.VARCHAR),
								new DataValue(detailItem-1, Types.VARCHAR),
								new DataValue(eId, Types.VARCHAR),
								new DataValue(pStockInNO, Types.VARCHAR),
								new DataValue("订单转完工入库", Types.VARCHAR),//memo 备注
								new DataValue("2", Types.VARCHAR),//status 完工入库单状态
								new DataValue("N", Types.VARCHAR), //process_status 上传状态
								new DataValue(orderNo, Types.VARCHAR), //ofNo 来源单号
								new DataValue("", Types.VARCHAR), //PTEMPLATENO 模板编号
								new DataValue(opNo, Types.VARCHAR), //ACCOUNTBY
								new DataValue(accountDate, Types.VARCHAR),
								new DataValue(createTime, Types.VARCHAR),
								new DataValue("3", Types.VARCHAR), // oType 来源类型
								new DataValue(machShopWarehouse_in_def, Types.VARCHAR),
								new DataValue(opNo, Types.VARCHAR),
								new DataValue(createDate, Types.VARCHAR),
								new DataValue(createTime, Types.VARCHAR),
								new DataValue(opNo, Types.VARCHAR),
								new DataValue(createDate, Types.VARCHAR),
								new DataValue(createTime, Types.VARCHAR),
								new DataValue("0", Types.VARCHAR), // docType 单据类型 0：完工入库  1：组合   2：拆解   3：转换合并  4：转换拆解
								new DataValue(totDistAmt, Types.VARCHAR), //进货价合计
								new DataValue(req.getChatUserId(), Types.VARCHAR),
								new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
								new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
						};
						InsBean ib1 = new InsBean("DCP_PSTOCKIN", columns1);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

						//endregion

					}
					else
					{
						//无须产生调拨单的话，还得更新下生产状态，防止之前有sql没有执行，清空下
						this.pData.clear();
					}


					// ********************** 更新订单生产状态 **************************88
					//region 更新订单生产状态
					UptBean up1;
					up1 = new UptBean("DCP_ORDER");
					up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
					up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));

					//更新updatetime
					up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
					up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
					// 订单状态 STATUS 改为 6 生产完成
					//up1.addUpdateValue("STATUS", new DataValue("6",Types.VARCHAR)); 
					// 生产状态 PRODUCTSTATUS 改为 6 ，生产完成
					up1.addUpdateValue("PRODUCTSTATUS", new DataValue("6",Types.VARCHAR));
					this.addProcessData(new DataProcessBean(up1));
					//endregion

					this.doExecuteDataToDB();
					sucessOrderList.add(orderNo);

					//***********调用库存同步给三方，这是个异步，不会影响效能*****************
					if (isNeedStockSync)
					{
						try
						{
							WebHookService.stockSync(eId,shopId,pStockInNO);
						}
						catch (Exception ignored)
						{

						}
					}


					//写日志
					//region 写订单日志
					List<orderStatusLog> orderStatusLogList = new ArrayList<>();
					orderStatusLog onelv1 = new orderStatusLog();
					onelv1.setLoadDocType(loadDocType);
					onelv1.setChannelId(channelId);
					onelv1.setLoadDocBillType(map.get("LOADDOCBILLTYPE").toString());
					onelv1.setLoadDocOrderNo(map.get("LOADDOCORDERNO").toString());
					onelv1.seteId(eId);

					onelv1.setOpName(opName);
					onelv1.setOpNo(opNo);
					onelv1.setShopNo(shopId);
					onelv1.setOrderNo(orderNo);
					onelv1.setMachShopNo(map.get("LOADDOCORDERNO").toString());
					onelv1.setShippingShopNo(map.get("SHIPPINGSHOP").toString());
					String statusType = "4";//已接单
					String updateStaus = "6"; // 生产完成			
					onelv1.setStatusType(statusType);
					onelv1.setStatus(updateStaus);
					StringBuilder statusTypeNameObj = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
					String statusTypeName = statusTypeNameObj.toString();
					onelv1.setStatusTypeName(statusTypeName);
					onelv1.setStatusName(statusName);

					String memo = "";
					memo += statusName;
					if (!isNeedProduction)
					{
						memo +="<br>无须产生完工入库单(生产门店需要生产的商品数量都是0)";
					}
					onelv1.setMemo(memo);

					onelv1.setDisplay("1");

					String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv1.setUpdate_time(updateDatetime);

					orderStatusLogList.add(onelv1);

					StringBuilder errorStatusLogMessage = new StringBuilder();
					boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
					if (nRet) {
						HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
					} else {
						HelpTools.writelog_waimai(
								"【写表DCP_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
					}
					//endregion

				}
				catch (Exception e)
				{
					//【ID1037325】【3.0菲尔雪】中台订单中心，生产完成报错  by jinzma 20240105
					String description=e.getMessage();
					try {
						StringWriter errors = new StringWriter();
						PrintWriter pw=new PrintWriter(errors);
						e.printStackTrace(pw);

						pw.flush();
						pw.close();

						errors.flush();
						errors.close();

						description = errors.toString();

						if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
							description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
						}

					} catch (Exception ignored) {
					}

					//只要有一个失败，就不在执行下面的
					isAllSucess = false;
					Map<String, Object> errorMap = new HashMap<>();
					errorMap.put("orderNo", orderNo);
					errorMap.put("errorDesc", description);
					failOrderList.add(errorMap);
					break;
				}

			}

			if(isAllSucess)
			{
				res.setSuccess(true);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行成功");
			}
			else
			{
				StringBuffer errorDescBuffer = new StringBuffer();
				//异常得单号先提示
				for (Map<String, Object> map : failOrderList)
				{
					String orderNo = map.getOrDefault("orderNo", "").toString();
					String errorDesc = map.getOrDefault("errorDesc", "").toString();
					errorDescBuffer.append("单号:"+orderNo+",异常:"+errorDesc);
				}

				//已经成功得单号
				if(sucessOrderList!=null&& !sucessOrderList.isEmpty())
				{
					errorDescBuffer.append("<br>单号:");
					for (String sucessOrderNo : sucessOrderList)
					{
						errorDescBuffer.append(sucessOrderNo+",");
					}
					errorDescBuffer.append("生产完成成功！");
				}


				//找下没有执行的
				List<String> noProcessOrderList = new ArrayList<>();
				for (OrderList orderNoList : orderList)
				{
					String orderNo = orderNoList.getOrderNo();
					boolean isFind = false;
					for (Map<String, Object> map : failOrderList)
					{
						String orderNo1 = map.getOrDefault("orderNo", "").toString();
						if(orderNo.equals(orderNo1))
						{
							isFind = true;
							break;
						}
					}

					if(isFind)
					{
						continue;
					}

					for (String sucessOrderNo : sucessOrderList)
					{

						if(orderNo.equals(sucessOrderNo))
						{
							isFind = true;
							break;
						}
					}

					if(isFind)
					{
						continue;
					}
					noProcessOrderList.add(orderNo);

				}


				//已经成功得单号
				if(noProcessOrderList!=null&& !noProcessOrderList.isEmpty())
				{
					errorDescBuffer.append("<br>单号:");
					for (String noProcessOrderNo : noProcessOrderList)
					{
						errorDescBuffer.append(noProcessOrderNo+",");
					}
					errorDescBuffer.append("请重新操作！");
				}



				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败！<br>"+errorDescBuffer.toString());

			}


		}
		catch (Exception e)
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常");
		}



	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderProductionFinish_OpenReq req) throws Exception {
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderProductionFinish_OpenReq req) throws Exception {
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderProductionFinish_OpenReq req) throws Exception {
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderProductionFinish_OpenReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		List<OrderList> orderList = req.getRequest().getOrderList();
		if(orderList==null||orderList.isEmpty())
		{
			isFail = true;
			errMsg.append("orderList不能为空 ");
		}
		if(Check.Null(req.getRequest().getShopId()))
		{
			isFail = true;
			errMsg.append("shopId不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return false;

	}

	@Override
	protected TypeToken<DCP_OrderProductionFinish_OpenReq> getRequestType() {
		return new TypeToken<DCP_OrderProductionFinish_OpenReq>(){};
	}

	@Override
	protected DCP_OrderProductionFinish_OpenRes getResponseType() {
		return new DCP_OrderProductionFinish_OpenRes();
	}


	private String getPStockInNO(DCP_OrderProductionFinish_OpenReq req) throws Exception  {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
		String shopId = req.getRequest().getShopId();
		String eId = req.getRequest().geteId();
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		StringBuffer sqlbuf = new StringBuffer();
		String docType = "0"; //0-完工入库  1-组合单   2-拆解单  3-转换合并  4-转换拆解
		//新增服务时  docType是1的时候 单号开头字母换成ZHRK；2的时候换成CJCK
		String pStockInNO = "WGRK" + bDate;
		sqlbuf.append("select PSTOCKINNO  from (select max(PSTOCKINNO) as PSTOCKINNO "
				+ " from DCP_PSTOCKIN where EID = '"+eId+"' and SHOPID = '"+shopId+"' "
				+ " and PSTOCKINNO like '%%" + pStockInNO + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
		if (getQData != null && !getQData.isEmpty()) {
			pStockInNO = (String) getQData.get(0).get("PSTOCKINNO");
			if (pStockInNO != null && !pStockInNO.isEmpty()) {
				long i;
				pStockInNO = pStockInNO.substring(4);
				i = Long.parseLong(pStockInNO) + 1;
				pStockInNO = i + "";
				pStockInNO = "WGRK" + pStockInNO; //完工入库0
			}
			else {
				pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
			}
		}
		else
		{
			pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
		}

		return pStockInNO;
	}


	private String getWeekDay() throws Exception{
		String weekOfDay="";
		String sql = "select to_char(sysdate,'D') as week from dual";
		List<Map<String, Object>> getWeekDay = this.doQueryData(sql, null);
		if (getWeekDay != null && !getWeekDay.isEmpty())
		{
			Map<String, Object> strWeekDay = getWeekDay.get(0);
			String weekDay = strWeekDay.get("WEEK").toString();
			switch (weekDay) {
				case "1":
					weekOfDay="周日";
					break;
				case "2":
					weekOfDay="周一";
					break;
				case "3":
					weekOfDay="周二";
					break;
				case "4":
					weekOfDay="周三";
					break;
				case "5":
					weekOfDay="周四";
					break;
				case "6":
					weekOfDay="周五";
					break;
				case "7":
					weekOfDay="周六";
					break;
				default:
					break;
			}
		}
		return weekOfDay;
	}

	private String getDay() throws Exception{
		String day="";
		String sql = "select to_char(sysdate,'dd') as day from dual";
		List<Map<String, Object>> getDay = this.doQueryData(sql, null);
		if (getDay != null && !getDay.isEmpty())
		{
			day=getDay.get(0).get("DAY").toString();
		}
		return day;
	}


}
