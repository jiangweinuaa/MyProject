package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_POrderNonArrivalReq;
import com.dsc.spos.json.cust.req.DCP_POrderNonArrivalReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_POrderNonArrivalRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 要货未到货  POrderNonArrival 
 * @author yuanyy
 *
 */
public class DCP_POrderNonArrival extends SPosBasicService<DCP_POrderNonArrivalReq, DCP_POrderNonArrivalRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_POrderNonArrivalReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		List<level1Elm> datas = req.getRequest().getDatas();
		if(datas.isEmpty()){
			errMsg.append ( "要货商品列表不能为空");
			isFail = true;
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}
	
	@Override
	protected TypeToken<DCP_POrderNonArrivalReq> getRequestType() {
		return new TypeToken<DCP_POrderNonArrivalReq>(){};
	}
	
	@Override
	protected DCP_POrderNonArrivalRes getResponseType() {
		return new DCP_POrderNonArrivalRes();
	}
	
	@Override
	protected DCP_POrderNonArrivalRes processJson(DCP_POrderNonArrivalReq req) throws Exception {
		DCP_POrderNonArrivalRes res = this.getResponse();
		
		try {
			String eId = req.geteId();
			String shopId = req.getShopId();
			List<level1Elm> datas = req.getRequest().getDatas();
			String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
			String sql;
			
			//计算未到货量
			StringBuffer sJoinPluNo = new StringBuffer();
			StringBuffer sJoinFeatureNo = new StringBuffer();
			for (level1Elm par : datas) {
				String featureNo = par.getFeatureNo();
				if (Check.Null(featureNo)){
					featureNo = " ";
				}
				sJoinPluNo.append(par.getPluNo() + ",");
				sJoinFeatureNo.append(featureNo + ",");
			}
			Map<String, String> map = new HashMap<>();
			map.put("PLUNO", sJoinPluNo.toString());
			map.put("FEATURENO", sJoinFeatureNo.toString());
			
			MyCommon cm = new MyCommon();
			String withPlu = cm.getFormatSourceMultiColWith(map);
			
			//【ID1035651】【潮品-3.0】要货申请：未到货量显示不准确。 by jinzma 20230825
			/* 目前逻辑
			    1：我们现在的计算逻辑是：需求日期大于等于当天做单的系统日期，计算出来要货单数量-入库数量 得到在途数量；
               修改后的逻辑：
               1：我们计算的逻辑是：需求日期大于等于（当天日期-LeadTimeDemand）日期，计算出来要货单数量-入库数量 得到在途数量；
			*/
			String LeadTimeDemand = PosPub.getPARA_SMS(dao,eId,shopId, "LeadTimeDemand"); // 要货在途前置天数；
			int LeadTimeDemand_i = 0;
			if (PosPub.isNumeric(LeadTimeDemand)){
				LeadTimeDemand_i = Integer.parseInt(LeadTimeDemand);
			}
			
			//要货数量统计
			sql = " select b.pluno,b.featureno,sum(b.baseqty) as baseqty from dcp_porder a"
					+ " inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno"
					+ " inner join ("+withPlu+")c on b.pluno=c.pluno and b.featureno=c.featureno"
					+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.rdate>='"+PosPub.GetStringDate(sDate, -LeadTimeDemand_i)+"' and a.status='2'"
					+ " group by b.pluno,b.featureno";
			List<Map<String, Object>> getQPorder = this.doQueryData(sql, null);
			
			//收货数量统计
			sql = " select b.pluno,b.featureno,sum(b.baseqty) as baseqty from dcp_stockin a"
					+ " inner join dcp_stockin_detail b on a.eid=b.eid and a.shopid=b.shopid and a.stockinno=b.stockinno"
					+ " inner join ("+withPlu+")c on b.pluno=c.pluno and b.featureno=c.featureno"
					+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.doc_type='0' and a.bdate>='"+sDate+"'"
					+ " group by b.pluno,b.featureno";
			List<Map<String, Object>> getQStockIn = this.doQueryData(sql, null);
			
			//计算库存
			sql = " select a.pluno,a.featureno,sum(a.qty) as baseqty from dcp_stock a"
					+ " inner join ("+withPlu+")b on a.pluno=b.pluno and a.featureno=b.featureno"
					+ " inner join dcp_warehouse c on a.eid=c.eid and a.organizationno=c.organizationno and a.warehouse=c.warehouse and c.warehouse_type<>'3'"
					+ " where a.eid='"+eId+"' and a.organizationno='"+shopId+"' "
					+ " group by a.pluno,a.featureno";
			List<Map<String, Object>> getStock = this.doQueryData(sql, null);
			
			//昨日要货量
			sql = " select b.pluno,b.featureno,sum(b.baseqty) as baseqty from dcp_porder a"
					+ " inner join dcp_porder_detail b on a.eid = b.eid and a.shopid = b.shopid and a.porderno = b.porderno"
					+ " inner join ("+withPlu+")c on b.pluno=c.pluno and b.featureno=c.featureno"
					+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status='2' and a.bdate='"+PosPub.GetStringDate(sDate,-1)+"'"
					+ " group by b.pluno,b.featureno";
			List<Map<String, Object>> QPorderYesterday = this.doQueryData(sql, null);
			
			//昨日销售量
			//是否分区标记 0:未分区 1:已分区
			/*if (PosPub.tablePartition_dcp_sale.equals("1")){
				sql = " select b.pluno,b.featureno,"
						+ " sum(case when a.type='1' or a.type='2' or a.type='4' then -b.baseqty else b.baseqty end ) as baseqty from dcp_sale a"
						+ " inner join dcp_sale_detail b on a.eId=b.eId and a.shopId=b.shopId and a.saleno=b.saleno"
						+ " inner join (" + withPlu + ")c on b.pluno=c.pluno and b.featureno=c.featureno"
						+ " where a.eId='" + eId + "' and a.shopId='" + shopId + "' and a.partition_date='" + PosPub.GetStringDate(sDate, -1) + "'"
						+ " group by b.pluno,b.featureno";
			}else {
				sql = " select b.pluno,b.featureno,"
						+ " sum(case when a.type='1' or a.type='2' or a.type='4' then -b.baseqty else b.baseqty end ) as baseqty from dcp_sale a"
						+ " inner join dcp_sale_detail b on a.eId=b.eId and a.shopId=b.shopId and a.saleno=b.saleno"
						+ " inner join (" + withPlu + ")c on b.pluno=c.pluno and b.featureno=c.featureno"
						+ " where a.eId='" + eId + "' and a.shopId='" + shopId + "' and a.bdate='" + PosPub.GetStringDate(sDate, -1) + "'"
						+ " group by b.pluno,b.featureno";
			}*/
			//【ID1035594】【嘉华3.0】B29148门店选501要货模板报错 error executing work  by jinzma 20230822
			sql = " select a.pluno,a.featureno,a.sale_qty as baseqty from dcp_stock_day a"
					+ " inner join (" + withPlu + ")b on a.pluno=b.pluno and a.featureno=b.featureno"
					+ " where a.eId='" + eId + "' and a.organizationno='" + shopId + "' and a.edate='" + PosPub.GetStringDate(sDate, -1) + "'"
					+ " ";
			List<Map<String, Object>> QSaleYesterday = this.doQueryData(sql, null);
			
			
			//商品单位转换率
			sql = " select a.pluno,a.ounit,a.unit,a.unitratio from dcp_goods_unit a"
					+ " inner join (" + withPlu + ")b on a.pluno=b.pluno"
					+ " where a.eid='"+eId+"'";
			List<Map<String, Object>> getUnitRatio = this.doQueryData(sql, null);
			
			//【ID1036519】【霸王3.3.0.6】客户想要根据要货模板管控当天商品的要货最大数量--服务端
			List<Map<String, Object>> getPOrderTemplateQty = new ArrayList<>();
			String templateNo = req.getRequest().getTemplateNo();  //要货模板编号
			if (!Check.Null(templateNo)){
				String POrder_Temp_Check = PosPub.getPARA_SMS(dao,eId,shopId,"POrder_Temp_Check");//要货模板最大数量管控
				if ("Y".equals(POrder_Temp_Check)){
					sql = " select b.pluno,b.featureno,sum(b.baseqty) as baseqty from dcp_porder a"
							+ " inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno"
							+ " inner join ("+withPlu+")c on b.pluno=c.pluno and b.featureno=c.featureno"
							+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.status>0 and a.status<>'5' "
							+ " and a.ptemplateno='"+templateNo+"' and a.confirm_date='"+sDate+"'"
							+ " group by b.pluno,b.featureno";
					getPOrderTemplateQty = this.doQueryData(sql, null);
				}
			}
			
			
			
			
			res.setDatas(new ArrayList<>());
			for (level1Elm par : datas) {
				DCP_POrderNonArrivalRes.level1Elm oneLv1 = res.new level1Elm();
				String pluNo = par.getPluNo();
				String featureNo;
				if (Check.Null(par.getFeatureNo())){
					featureNo = " ";
				}else{
					featureNo = par.getFeatureNo();
				}
				String punit = par.getPunit();
				int punitUdLength = 2;
				if (PosPub.isNumeric(par.getPunitUdLength())) {
					punitUdLength = Integer.parseInt(par.getPunitUdLength());
				}
				
				oneLv1.setPluNo(pluNo);
				oneLv1.setFeatureNo(par.getFeatureNo());
				
				/*【ID1033709】【潮品3.0】门店要货量管控：当前补货申请量+当前库存+配送在途+要货在途量不可超过设定库存上限值-移动门店 by jinzma 20230607
				要货数 = 要货单需求日期大于等于今天且已经确认的数量合计
				收货数 = 收货单单据日期等于今天的数量合计
				未到货 = 要货单 - 收货单 */
				{
					BigDecimal porder_b = new BigDecimal("0");   //要货数量
					BigDecimal stockIn_b = new BigDecimal("0");  //到货数量
					
					List<Map<String, Object>> porder = getQPorder.stream().filter(
							s -> s.get("PLUNO").toString().equals(pluNo) && s.get("FEATURENO").toString().equals(featureNo)
					).collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(porder)) {
						if (!Check.Null(porder.get(0).get("BASEQTY").toString())) {
							porder_b = new BigDecimal(porder.get(0).get("BASEQTY").toString());
						}
					}
					
					List<Map<String, Object>> stockIn = getQStockIn.stream().filter(
							s -> s.get("PLUNO").toString().equals(pluNo) && s.get("FEATURENO").toString().equals(featureNo)
					).collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(stockIn)) {
						if (!Check.Null(stockIn.get(0).get("BASEQTY").toString())) {
							stockIn_b = new BigDecimal(stockIn.get(0).get("BASEQTY").toString());
						}
					}
					
					//未到货 = 要货单 - 收货单
					BigDecimal refUAQty_b = porder_b.subtract(stockIn_b);
					if (refUAQty_b.compareTo(BigDecimal.ZERO) < 0) {
						refUAQty_b = new BigDecimal(0);
					}
					oneLv1.setRefUAQty(refUAQty_b.toPlainString());  //未到货量
				}
				
				
				//获取要货单位转换率
				BigDecimal unitRatio_b;
				{
					List<Map<String, Object>> unitRatio = getUnitRatio.stream().filter(
							s -> s.get("PLUNO").toString().equals(pluNo) && s.get("OUNIT").toString().equals(punit)
					).collect(Collectors.toList());
					
					if (!CollectionUtils.isEmpty(unitRatio)) {
						unitRatio_b = new BigDecimal(unitRatio.get(0).get("UNITRATIO").toString());
					}else{
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品" + pluNo + " 单位" + punit + " 对应的单位转换率未设置");
					}
				}
				
				//获取库存数量
				BigDecimal refBaseQty_b = new BigDecimal(0);     //库存数量
				BigDecimal reqBaseQty_b = new BigDecimal(0);     //要货单位库存数量
				{
					List<Map<String, Object>> stock = getStock.stream().filter(
							s -> s.get("PLUNO").toString().equals(pluNo) && s.get("FEATURENO").toString().equals(featureNo)
					).collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(stock)) {
						if (!Check.Null(stock.get(0).get("BASEQTY").toString())) {
							refBaseQty_b = new BigDecimal(stock.get(0).get("BASEQTY").toString());
							reqBaseQty_b = refBaseQty_b.divide(unitRatio_b, punitUdLength, RoundingMode.HALF_UP);
						}
					}
				}
				
				//昨日要货量
				BigDecimal preDayQty_b = new BigDecimal(0);
				{
					List<Map<String, Object>> porderYesterday = QPorderYesterday.stream().filter(
							s -> s.get("PLUNO").toString().equals(pluNo) && s.get("FEATURENO").toString().equals(featureNo)
					).collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(porderYesterday)) {
						if (!Check.Null(porderYesterday.get(0).get("BASEQTY").toString())) {
							BigDecimal baseQty = new BigDecimal(porderYesterday.get(0).get("BASEQTY").toString());
							preDayQty_b = baseQty.divide(unitRatio_b, punitUdLength, RoundingMode.HALF_UP);
						}
					}
				}
				
				//昨日销量（已转换为要货单位）
				BigDecimal preDaySaleQty_b = new BigDecimal(0);
				{
					List<Map<String, Object>> saleYesterday = QSaleYesterday.stream().filter(
							s -> s.get("PLUNO").toString().equals(pluNo) && s.get("FEATURENO").toString().equals(featureNo)
					).collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(saleYesterday)) {
						if (!Check.Null(saleYesterday.get(0).get("BASEQTY").toString())) {
							BigDecimal baseQty = new BigDecimal(saleYesterday.get(0).get("BASEQTY").toString());
							preDaySaleQty_b = baseQty.divide(unitRatio_b, punitUdLength, RoundingMode.HALF_UP);
						}
					}
				}
				
				//【ID1036519】【霸王3.3.0.6】客户想要根据要货模板管控当天商品的要货最大数量--服务端  by jinzma 20231020
				BigDecimal acDemandQty_b = new BigDecimal(0);
				{
					List<Map<String, Object>> acDemandQty = getPOrderTemplateQty.stream().filter(
							s -> s.get("PLUNO").toString().equals(pluNo) && s.get("FEATURENO").toString().equals(featureNo)
					).collect(Collectors.toList());
					if (!CollectionUtils.isEmpty(acDemandQty)) {
						acDemandQty_b = new BigDecimal(acDemandQty.get(0).get("BASEQTY").toString());
					}
				}
				
				oneLv1.setRefBaseQty(refBaseQty_b.toPlainString());
				oneLv1.setReqBaseQty(reqBaseQty_b.toPlainString());
				oneLv1.setPreDayQty(preDayQty_b.toPlainString());
				oneLv1.setPreDaySaleQty(preDaySaleQty_b.toPlainString());
				oneLv1.setAcDemandQty(acDemandQty_b.toPlainString());
				
				res.getDatas().add(oneLv1);
			}
			
			return res;
			
		}catch (Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_POrderNonArrivalReq req) throws Exception {
		return null;
	}
	
}
