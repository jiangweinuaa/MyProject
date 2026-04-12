package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderProductionTransfer_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderProductionTransfer_OpenReq.OrderList;
import com.dsc.spos.json.cust.res.DCP_OrderProductionTransfer_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderProductionTransfer_OpenRes.ErrorOrderList;
import com.dsc.spos.json.cust.res.DCP_OrderProductionTransfer_OpenRes.levRes;
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
import java.util.Calendar;
public class DCP_OrderProductionTransfer_Open extends SPosAdvanceService<DCP_OrderProductionTransfer_OpenReq, DCP_OrderProductionTransfer_OpenRes> {
    
    @Override
    protected void processDUID(DCP_OrderProductionTransfer_OpenReq req,DCP_OrderProductionTransfer_OpenRes res) throws Exception {
        levRes resDatas = res.new levRes();
        resDatas.setErrorOrderList(new ArrayList<ErrorOrderList>());
        res.setDatas(resDatas);
        
        try {
            String eId = req.getRequest().geteId();
            
            
            String shopId = req.getRequest().getShopId();
            String opNo = req.getRequest().getOpNo();
            String opName = req.getRequest().getOpName();
            List<OrderList> orderList = req.getRequest().getOrderList();
            List<String> sucessOrderList = new ArrayList<String>();//执行成功的 单号列表
            List<Map<String, Object>> failOrderList = new ArrayList<Map<String, Object>>();//失败的列表,以及错误描述
            boolean isAllSucess = true;
            String logStrStart = "";
            
            
            //【ID1033494】 【三味奇】订单中心点调拨生成的调拨单没有进货价和零售价格  by jinzma 20230524
            String companyId = req.getBELFIRM();
            {
                String sql = " select belfirm from dcp_org where eid='" + eId + "' and organizationno='" + shopId + "' ";
                List<Map<String, Object>> getQData = this.doQueryData(sql, null);
                companyId = getQData.get(0).get("BELFIRM").toString();
            }
            
            MyCommon MC = new MyCommon();
            //循环单号，有一个失败break,后面不执行了
            for (OrderList orderNoList : orderList)
            {
                logStrStart = "";
                
                String orderNo = orderNoList.getOrderNo();
                logStrStart="循环操作【订单调拨】单号orderNo="+orderNo+",";
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
                        Map<String, Object> errorMap = new HashMap<String, Object>();
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
                    String machShop = map.get("MACHSHOP").toString(); //生产门店
                    String shippingShop = map.get("SHIPPINGSHOP").toString();//配送门店
                    //String warehouse = map.get("WAREHOUSE").toString(); //仓库， 仓库取自订单明细表， 正常情况下每笔订单仓库是一致的
                    String deliveryNo = map.get("DELIVERYNO").toString(); //物流单号
                    String shippingWarehouse = "";//查询配送门店的默认收货仓
                    String delId = map.getOrDefault("DELID", "").toString();
                    String delName = map.getOrDefault("DELNAME", "").toString();
                    String delTelephone = map.getOrDefault("DELTELEPHONE", "").toString();
                    String packerId = map.getOrDefault("PACKERID", "").toString();
                    String packerName = map.getOrDefault("PACKERNAME", "").toString();
                    String packerTelephone = map.getOrDefault("PACKERTELEPHONE", "").toString();
                    
                    HelpTools.writelog_waimai(logStrStart+"数据库中 订单状态status="+status+",生产状态productStatus="+productStatus+",生产机构machShop="+machShop+",配送机构shippingshop="+shippingShop+",当前传入机构shopId="+shopId);
                    boolean canProductFinishFlag = true;
                    StringBuffer errMsg = new StringBuffer();
                    boolean isNeedStockSync = false;
                    
                    if(shippingShop==null||shippingShop.isEmpty())
                    {
                        canProductFinishFlag = false;
                        errMsg.append("配送机构为空， ");
                    }
                    else
                    {
                        // 查询配送门店的默认收货仓
                        String warehouseSql = " select IN_COST_WAREHOUSE from DCP_ORG where eId = '"+eId+"' and organizationNo = '"+shippingShop+"'";
                        HelpTools.writelog_waimai(logStrStart+"查询配送机构默认收货仓库sql="+warehouseSql);
                        List<Map<String, Object>> warehouseDatas = this.doQueryData(warehouseSql, null);
                        
                        if(warehouseDatas != null && warehouseDatas.size() > 0 ){
                            shippingWarehouse = warehouseDatas.get(0).get("IN_COST_WAREHOUSE").toString(); //门店默认收货成本仓
                        }
                        
                        if(shippingWarehouse==null||shippingWarehouse.isEmpty())
                        {
                            canProductFinishFlag = false;
                            errMsg.append("配送机构对应的默认收货成本仓为空， ");
                        }
                        
                    }
                    
                    //订单状态  status ==1 订单开立 或  status == 2 已接单，生产状态productStatus = 6 完工入库
                    if(("1".equals(status)||"2".equals(status)) && "6".equals(productStatus))
                    {
                    
                    }
                    else
                    {
                        canProductFinishFlag = false;
                        errMsg.append("订单开立或已接单，且生产状态=6（生产完成）的单据才可操作调拨， ");
                    }
                    
                    
                    //当前机构为订单生产机构
                    if(shopId.equals(machShop))
                    {
                        if(machShop.equals(shippingShop))
                        {
                            canProductFinishFlag = false;
                            errMsg.append("生产机构与配送机构一致，不能进行调拨， ");
                        }
                        
                    }
                    else
                    {
                        canProductFinishFlag = false;
                        errMsg.append("当前机构非订单生产机构，不能进行调拨， ");
                    }
                    
                    //String machShopWarehouse_in = "";//查询生产门店的默认收货仓
                    String machShopWarehouse_out = "";//查询生产门店的默认出货仓
                    
                    String warehouseSql = " select IN_COST_WAREHOUSE,OUT_COST_WAREHOUSE from DCP_ORG where eId = '"+eId+"' and organizationNo = '"+machShop+"'";
                    HelpTools.writelog_waimai(logStrStart+"查询生产机构默认收货/出货仓库sql="+warehouseSql);
                    List<Map<String, Object>> warehouseDatas = this.doQueryData(warehouseSql, null);
                    
                    if(warehouseDatas != null && warehouseDatas.size() > 0 ){
                        machShopWarehouse_out = warehouseDatas.get(0).get("OUT_COST_WAREHOUSE").toString();//门店默认出货成本仓
                        String machShopWarehouse_in = warehouseDatas.get(0).get("IN_COST_WAREHOUSE").toString(); //门店默认收货成本仓
                        HelpTools.writelog_waimai(logStrStart+"查询生产机构默认出货仓库OUT_COST_WAREHOUSE="+machShopWarehouse_out);
                    }
                    
                    if(machShopWarehouse_out==null||machShopWarehouse_out.isEmpty())
                    {
                        canProductFinishFlag = false;
                        errMsg.append("生产机构对应的默认出货成本仓为空， ");
                    }
                    
                    
                    if(!canProductFinishFlag)
                    {
                        HelpTools.writelog_waimai(logStrStart+ errMsg);
                        isAllSucess = false;
                        Map<String, Object> errorMap = new HashMap<String, Object>();
                        errorMap.put("orderNo", orderNo);
                        errorMap.put("errorDesc", errMsg.toString());
                        failOrderList.add(errorMap);
                        break;
                    }
                    
                    
                    
                    // 获取成品 和 原料的 进货价 和 零售价
                    List<Map<String, Object>> pluList = new ArrayList<>();
                    for (Map<String, Object> detailMap : orderDatas) {
                        String pluNo = detailMap.get("PLUNO").toString(); // 订单商品
                        String sUnit = detailMap.get("SUNIT").toString(); // 订单上商品的销售单位
                        if(!Check.Null(pluNo)){
                            Map<String, Object> pluMap = new HashMap<>();
                            pluMap.put("PLUNO", pluNo);
                            pluMap.put("PUNIT", sUnit); //订单上的销售单位
                            //【ID1033494】【三味奇】订单中心点调拨生成的调拨单没有进货价和零售价格 by jinzma 20230525
                            pluMap.put("BASEUNIT",detailMap.get("BASEUNIT").toString());
                            pluMap.put("UNITRATIO",detailMap.get("UNITRATIO").toString());
                            pluList.add(pluMap);
                        }
                    }
                    
                    //获取商品零售价和进货价
                    List<Map<String, Object>> getPluPrice = MC.getSalePrice_distriPrice(dao,eId,companyId, shopId,pluList,companyId);
                    
                    String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId); // 营业日期 yyyyMMdd
                    String createBy = req.getOpNO();
                    String createDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
                    String createTime = new SimpleDateFormat("HHmmss").format(new Date());
                    
                    String stockOutNo = getStockOutNo(eId,machShop);
                    String receivingNo = getReceivingNO(eId,shippingShop);
                    
                    BigDecimal tot_pqty = new BigDecimal("0");//总数量， 取订单商品总数量
                    BigDecimal tot_amt = new BigDecimal("0"); // 零售价总金额 ， 不能取订单上的总金额， 应该根据商品单价和数量计算
                    BigDecimal tot_distriAmt = new BigDecimal("0"); //进货价总金额
                    
                    // 调拨出库单主表 DCP_STOCKOUT
                    String[] stockOut_columns = {
                            "SHOPID", "ORGANIZATIONNO","EID","STOCKOUTNO","BDATE", "MEMO",  "STATUS",
                            "DOC_TYPE","OTYPE","OFNO","CREATEBY", "CREATE_DATE", "CREATE_TIME","TOT_PQTY",
                            "TOT_AMT", "TOT_CQTY", "LOAD_DOCTYPE", "LOAD_DOCNO","DELIVERY_NO","TRANSFER_SHOP",
                            "STOCKOUT_ID","BSNO","RECEIPT_ORG","WAREHOUSE","TRANSFER_WAREHOUSE","TOT_DISTRIAMT",
                            "PTEMPLATENO","SOURCEMENU","CREATE_CHATUSERID","ACCOUNT_CHATUSERID",
                            "ACCOUNT_DATE","ACCOUNT_TIME","UPDATE_TIME","TRAN_TIME","DELIVERYBY"
                    };
                    
                    // 调拨出库单子表 DCP_STOCKOUT_DETAIL
                    String[] stockOutDetail_columns = {
                            "STOCKOUTNO", "SHOPID", "ITEM", "OITEM", "PLUNO",
                            "PUNIT", "PQTY", "BASEUNIT", "BASEQTY", "UNIT_RATIO", "PLU_BARCODE",
                            "PRICE", "AMT", "EID", "ORGANIZATIONNO", "WAREHOUSE","BSNO"
                            ,"PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };
                    
                    // 收货通知单主表 DCP_RECEIVING
                    String[] receiving_columns = {
                            "TRANSFER_SHOP","RECEIVINGNO","SHOPID","BDATE","MEMO","DOC_TYPE","STATUS",
                            "CREATEBY","CREATE_DATE","CREATE_TIME","MODIFYBY","MODIFY_DATE","MODIFY_TIME",
                            "CANCELBY","CANCEL_DATE","CANCEL_TIME",
                            "TOT_PQTY","TOT_AMT","TOT_CQTY","LOAD_DOCTYPE","LOAD_DOCNO","ORGANIZATIONNO",
                            "EID","WAREHOUSE","TOT_DISTRIAMT","RECEIPTDATE",
                            "UPDATE_TIME","TRAN_TIME","DELIVERYBY"
                    };
                    
                    // 收货通知单主表 DCP_RECEIVING_DETAIL
                    String[] receivingDetail_columns = {
                            "RECEIVINGNO", "ORGANIZATIONNO", "EID", "SHOPID", "ITEM",
                            "OTYPE", "OFNO", "OITEM", "PLUNO", "PUNIT", "PQTY", "BASEUNIT",
                            "BASEQTY", "UNIT_RATIO", "PRICE", "AMT","WAREHOUSE","PLU_MEMO","BATCH_NO","PROD_DATE",
                            "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
                    };
                    
                    
                    int item = 1;
                    boolean isNeedProductionTransfer = false;//当前生产门店可能没有生产具体的商品，无须调拨
                    for (Map<String, Object> orderMap : orderDatas)
                    {
                        String oItem = orderMap.get("ITEM").toString();
                        String pluNo = orderMap.get("PLUNO").toString();
                        String pUnit = orderMap.get("SUNIT").toString();
                        String pQty = orderMap.get("QTY").toString();
                        
                        //判断下 可能村长qty=0
                        BigDecimal orderQty_machShop = new BigDecimal(pQty);
                        if(orderQty_machShop.compareTo(BigDecimal.ZERO)<=0)
                        {
                            HelpTools.writelog_waimai(logStrStart+"需要过滤商品，商品属性不需要生产或需要生产数量qty-shopqty="+pQty+",过滤的商品项次item="+oItem+",商品pluNo="+pluNo);
                            continue;
                        }
                        isNeedProductionTransfer = true;
                        String baseUnit = orderMap.get("BASEUNIT").toString();
                        // 成品基准单位对应数量
                        String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, pUnit, baseUnit, pQty );
                        
                        String unitRatio = orderMap.get("UNITRATIO").toString();
                        String pluBarcode = orderMap.get("PLUBARCODE").toString(); // 商品条码，订单上有
                        String featureNo = " ";
                        if(!Check.Null(orderMap.get("FEATURENO").toString())){
                            featureNo = orderMap.get("FEATURENO").toString();
                        }
                        
                        Map<String, Object> condiV = new HashMap<String, Object>();
                        condiV.put("PLUNO",pluNo);
                        condiV.put("PUNIT",pUnit); //原料用料单位
                        List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);
                        
                        //String price = "0";
                        String price = orderMap.get("PRICE").toString();  //默认取订单里面的单价  by jinzma 20230525
                        String distriPrice = "0";
                        if(priceList!=null && priceList.size()>0 )
                        {
                            price=priceList.get(0).get("PRICE").toString(); //零售价
                            distriPrice=priceList.get(0).get("DISTRIPRICE").toString();//进货价
                        }
                        
                        BigDecimal amt = new BigDecimal("0");
                        amt = new BigDecimal(pQty).multiply(new BigDecimal(price));
                        
                        BigDecimal distriAmt = new BigDecimal("0");
                        distriAmt = new BigDecimal(pQty).multiply(new BigDecimal(distriPrice));
                        
                        tot_pqty = new BigDecimal(pQty).add(tot_pqty);
                        tot_amt =  amt.add(tot_amt);
                        tot_distriAmt = distriAmt.add(tot_distriAmt);
						
						/*
						  // 调拨出库单子表 DCP_STOCKOUT_DETAIL
						 String[] stockOutDetail_columns = {
						 "STOCKOUTNO", "SHOPID", "item", "oItem", "pluNO",
						 "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio", "PLU_BARCODE",
						 "price", "amt", "EID", "organizationNO", "WAREHOUSE","BSNO"
						 ,"PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT", "FEATURENO"
						 };
						 */
                        DataValue[] insValue1 = null;
                        insValue1 = new DataValue[]{
                                new DataValue(stockOutNo, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(item, Types.VARCHAR),
                                new DataValue(oItem, Types.VARCHAR), // 来源项次，  订单上的商品所属项次
                                new DataValue(pluNo, Types.VARCHAR), //
                                new DataValue(pUnit, Types.VARCHAR),
                                new DataValue(pQty, Types.VARCHAR),
                                new DataValue(baseUnit, Types.VARCHAR),
                                new DataValue(baseQty, Types.VARCHAR),
                                new DataValue(unitRatio, Types.VARCHAR),
                                new DataValue(pluBarcode, Types.VARCHAR),
                                new DataValue(price, Types.VARCHAR),
                                new DataValue(amt, Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(machShopWarehouse_out, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),// bsno
                                new DataValue("", Types.VARCHAR),// pluMemo
                                new DataValue("", Types.VARCHAR),// batch_no
                                new DataValue("", Types.VARCHAR),// PROD_DATE
                                new DataValue(distriPrice, Types.VARCHAR), // DISTRIPRICE 进货价
                                new DataValue(distriAmt, Types.VARCHAR), // DISTRIAMT 进货价总金额
                                new DataValue(bDate, Types.VARCHAR), // bDate 单据日期
                                new DataValue(featureNo, Types.VARCHAR) // featureNo 特征码
                            
                        };
                        InsBean ib1 = new InsBean("DCP_STOCKOUT_DETAIL", stockOutDetail_columns);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                        
                        /*
                         * 写库存流水， 商品调拨出库
                         */
                        //region 商品调拨出库库存流水
                        String procedure="SP_DCP_StockChange";
                        Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1,eId);							//--企业ID
                        inputParameter.put(2,shopId);						//--组织
                        inputParameter.put(3,"04");							//--单据类型
                        inputParameter.put(4,stockOutNo);					//--单据号
                        inputParameter.put(5,item);							//--单据行号
                        inputParameter.put(6,"-1");							//--异动方向 1=加库存 -1=减库存
                        inputParameter.put(7,bDate);						//--营业日期 yyyy-MM-dd
                        inputParameter.put(8,pluNo);						//--品号
                        inputParameter.put(9,featureNo);					//--特征码
                        inputParameter.put(10,machShopWarehouse_out);					//--仓库
                        inputParameter.put(11,"");							//--批号
                        inputParameter.put(12,pUnit);						//--交易单位
                        inputParameter.put(13,pQty);						//--交易数量
                        inputParameter.put(14,baseUnit);					//--基准单位
                        inputParameter.put(15,baseQty);                     //--基准数量
                        inputParameter.put(16,unitRatio);					//--换算比例
                        inputParameter.put(17,price);						//--零售价
                        inputParameter.put(18,amt);							//--零售金额
                        inputParameter.put(19,distriPrice);					//--进货价
                        inputParameter.put(20,distriAmt);					//--进货金额
                        inputParameter.put(21,bDate);			    		//--入账日期 yyyy-MM-dd
                        inputParameter.put(22,"");							//--批号的生产日期 PROD_DATE yyyy-MM-dd
                        inputParameter.put(23,bDate);						//--单据日期
                        inputParameter.put(24,"");							//--异动原因
                        inputParameter.put(25,"订单调拨出库");					//--异动描述
                        inputParameter.put(26,createBy);					//--操作员
                        
                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));
                        isNeedStockSync = true;
                        DataValue[] receiving_insValue = null;
                        receiving_insValue = new DataValue[]{
                                new DataValue(receivingNo, Types.VARCHAR),
                                new DataValue(shippingShop, Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shippingShop, Types.VARCHAR),
                                new DataValue(item, Types.VARCHAR),
                                
                                new DataValue("1", Types.VARCHAR), // oType , 0-配送收货通知  1-调拨收货通知 2-统采配送通知
                                new DataValue(stockOutNo, Types.VARCHAR),  // 来源单号， 即调拨出库单单号
                                new DataValue(item, Types.VARCHAR),  // 来源项次，调拨出库单上的item
                                new DataValue(pluNo, Types.VARCHAR),  //
                                new DataValue(pUnit, Types.VARCHAR),
                                new DataValue(pQty, Types.VARCHAR),
                                new DataValue(baseUnit, Types.VARCHAR),
                                new DataValue(baseQty, Types.VARCHAR),
                                new DataValue(unitRatio, Types.VARCHAR),
                                new DataValue(price, Types.VARCHAR),
                                new DataValue(amt, Types.VARCHAR),
                                
                                new DataValue(shippingWarehouse, Types.VARCHAR), // 收货门店的默认收货成本仓
                                new DataValue("", Types.VARCHAR),// pluMemo
                                new DataValue("", Types.VARCHAR),// batch_no
                                new DataValue("", Types.VARCHAR),// PROD_DATE
                                new DataValue(distriPrice, Types.VARCHAR), // DISTRIPRICE 进货价
                                new DataValue(distriAmt, Types.VARCHAR), // DISTRIAMT 进货价总金额
                                new DataValue(bDate, Types.VARCHAR), // bDate 单据日期
                                new DataValue(featureNo, Types.VARCHAR) // featureNo 特征码
                            
                        };
                        InsBean ib2 = new InsBean("DCP_RECEIVING_DETAIL", receivingDetail_columns);
                        ib2.addValues(receiving_insValue);
                        this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
                        
                        item = item + 1;
                    }
                    
                    //生产门店没有生产商品数量，无须产生调拨单
                    if (isNeedProductionTransfer) {
                        // 开始写调拨出库单主表 DCP_STOCKOUT、  收货通知单主表 DCP_RECEIVING
                        DataValue[] insValue1 = new DataValue[]{
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(shopId, Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(stockOutNo, Types.VARCHAR),
                                new DataValue(bDate, Types.VARCHAR),
                                new DataValue("订单调拨出库单，订单号:"+orderNo, Types.VARCHAR),//memo
                                new DataValue("2", Types.VARCHAR), //status
                                new DataValue("1", Types.VARCHAR), // docType 0-换季退货  1-调拨出库  2-次品退货 3-其他出库 4-移仓出库
                                new DataValue("", Types.VARCHAR), //oType
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(createBy, Types.VARCHAR),
                                new DataValue(createDate, Types.VARCHAR),
                                new DataValue(createTime, Types.VARCHAR),
                                new DataValue(tot_pqty.toString(), Types.VARCHAR),
                                new DataValue(tot_amt.toString(), Types.VARCHAR),
                                new DataValue(item-1, Types.VARCHAR),  //品种数在循环最后，多加了
                                new DataValue("1", Types.VARCHAR), //调拨出库单上记录的 loadDocType ， 1：订单
                                new DataValue(orderNo, Types.VARCHAR),
                                new DataValue(deliveryNo, Types.VARCHAR), //物流单号
                                new DataValue(shippingShop, Types.VARCHAR), //调入门店 transferShop
                                new DataValue("", Types.VARCHAR), //stockOutID
                                new DataValue("", Types.VARCHAR), //bsno 报损原因
                                new DataValue(companyId, Types.VARCHAR), //收货组织 receiptOrg
                                new DataValue(machShopWarehouse_out, Types.VARCHAR), //出货仓库
                                new DataValue(shippingWarehouse, Types.VARCHAR),//调入仓库   transferWarehouse
                                new DataValue(tot_distriAmt.toString(), Types.VARCHAR), //进货价总金额
                                new DataValue("", Types.VARCHAR), //ptemplateNo 模板编号
                                new DataValue("", Types.VARCHAR), // sourceMenu 来源菜单
                                new DataValue(req.getChatUserId(), Types.VARCHAR),
                                new DataValue(req.getChatUserId(), Types.VARCHAR),
                                new DataValue(bDate, Types.VARCHAR),
                                new DataValue(createTime, Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，
                                // 之前易成用的是（易成用的要货发货单功能）--服务端  by jinzma 20231218
                                new DataValue(map.get("DELID").toString(), Types.VARCHAR),
                            
                        };
                        
                        InsBean ib1 = new InsBean("DCP_STOCKOUT", stockOut_columns);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
                        
                        String receivingBdate = PosPub.getAccountDate_SMS(dao, eId, shippingShop);
                        
                        
                        //【ID1023848】【詹记】门店盘点单需要增加业务单据检查。根据参数设置检查哪些单据。类似于3.0闭店检查作业。
                        String Transfer_Day = PosPub.getPARA_SMS(dao,eId,shopId,"Transfer_Day");
                        if (!PosPub.isNumeric(Transfer_Day)){
                            Transfer_Day = "7";
                        }
                        String receiptDate = PosPub.GetStringDate(createDate,Integer.parseInt(Transfer_Day));
                        
                        
                        DataValue[] insValue2 = new DataValue[] {
                                new DataValue(shopId, Types.VARCHAR), //调出门店
                                new DataValue(receivingNo, Types.VARCHAR),
                                new DataValue(shippingShop, Types.VARCHAR), //shop
                                new DataValue(receivingBdate, Types.VARCHAR),
                                new DataValue("订单调拨收货通知,订单号:"+orderNo, Types.VARCHAR), //memo
                                new DataValue("1", Types.VARCHAR), //doc_type  0-配送收货通知  1-调拨收货通知 2-统采配送通知
                                new DataValue("6", Types.VARCHAR),//STATUS  6-待收货  7-已完成  8-已作废
                                new DataValue(createBy, Types.VARCHAR),
                                new DataValue(createDate, Types.VARCHAR),
                                new DataValue(createTime, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR), // ModifyBy
                                new DataValue("", Types.VARCHAR), // Modify_Date
                                new DataValue("", Types.VARCHAR), // Modify_Time
                                new DataValue("", Types.VARCHAR), // CancelBy
                                new DataValue("", Types.VARCHAR), // Cancel_Date
                                new DataValue("", Types.VARCHAR), // Cancel_Time
                                new DataValue(tot_pqty, Types.VARCHAR),
                                new DataValue(tot_amt, Types.VARCHAR),
                                new DataValue(item, Types.VARCHAR),
                                new DataValue("", Types.VARCHAR),
                                new DataValue(stockOutNo, Types.VARCHAR),
                                new DataValue(shippingShop, Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(shippingWarehouse, Types.VARCHAR),
                                new DataValue(tot_distriAmt, Types.VARCHAR),
                                new DataValue(receiptDate, Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                //【ID1037888】[红房子3.0]订单和调拨单记录配送员，配送员电话，统计出来后用于计算配送人员工资，
                                // 之前易成用的是（易成用的要货发货单功能）--服务端  by jinzma 20231218
                                new DataValue(map.get("DELID").toString(), Types.VARCHAR),
                        };
                        
                        //收货通知单主表
                        InsBean ib2 = new InsBean("DCP_RECEIVING", receiving_columns);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2)); // 新增單頭
                        
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
                    StringBuffer logmemo = new StringBuffer("");
                    if (req.getRequest().getPackerId()!=null)
                    {
                        if(req.getRequest().getPackerId().equals(packerId)==false)
                        {
                            up1.addUpdateValue("PACKERID", new DataValue(req.getRequest().getPackerId(), Types.VARCHAR));

                            logmemo.append( "<br>打包人ID：" + packerId + "-->" + req.getRequest().getPackerId());
                        }
                    }
                    if (req.getRequest().getPackerName() != null)
                    {
                        if(req.getRequest().getPackerName().equals(packerName)==false)
                        {
                            up1.addUpdateValue("PACKERNAME", new DataValue(req.getRequest().getPackerName(), Types.VARCHAR));
                            logmemo .append( "<br>打包人：" + packerName + "-->" + req.getRequest().getPackerName());
                        }

                    }
                    if (req.getRequest().getPackerTelephone() != null)
                    {
                        if(req.getRequest().getPackerTelephone().equals(packerTelephone)==false)
                        {
                            up1.addUpdateValue("PACKERTELEPHONE", new DataValue(req.getRequest().getPackerTelephone(), Types.VARCHAR));
                            logmemo .append( "<br>打包人电话：" + packerTelephone + "-->" + req.getRequest().getPackerTelephone());
                        }

                    }

                    if (req.getRequest().getDelId() != null)
                    {
                        if(req.getRequest().getDelId().equals(delId)==false)
                        {
                            up1.addUpdateValue("DELID", new DataValue(req.getRequest().getDelId(), Types.VARCHAR));
                            logmemo .append( "<br>配送人ID：" + delId + "-->" + req.getRequest().getDelId());
                        }

                    }
                    if (req.getRequest().getDelName() != null)
                    {
                        if(req.getRequest().getDelName().equals(delName)==false)
                        {
                            up1.addUpdateValue("DELNAME", new DataValue(req.getRequest().getDelName(), Types.VARCHAR));
                            logmemo .append( "<br>配送人：" + delName + "-->" + req.getRequest().getDelName());
                        }

                    }
                    if (req.getRequest().getDelTelephone() != null)
                    {
                        if(req.getRequest().getDelTelephone().equals(delTelephone)==false)
                        {
                            up1.addUpdateValue("DELTELEPHONE", new DataValue(req.getRequest().getDelTelephone(), Types.VARCHAR));
                            logmemo .append( "<br>配送人电话：" + delTelephone + "-->" + req.getRequest().getDelTelephone());
                        }

                    }
                    
                    //更新updatetime
                    up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                    // 订单状态 STATUS 改为 7(门店调拨)
                    //up1.addUpdateValue("STATUS", new DataValue("7",Types.VARCHAR));
                    // 生产状态PRODUCTSTATUS 改为7 (门店调拨) ， 和订单状态不一样
                    up1.addUpdateValue("PRODUCTSTATUS", new DataValue("7",Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(up1));
                    //endregion
                    
                    this.doExecuteDataToDB();
                    sucessOrderList.add(orderNo);
                    
                    //***********调用库存同步给三方，这是个异步，不会影响效能*****************
                    if (isNeedStockSync)
                    {
                        try
                        {
                            WebHookService.stockSync(eId,shopId,stockOutNo);
                        }
                        catch (Exception e)
                        {
                        
                        }
                    }
                    
                    //region 写订单日志
                    List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
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
                    String updateStaus = "7"; // 门店调拨
                    onelv1.setStatusType(statusType);
                    onelv1.setStatus(updateStaus);
                    StringBuilder statusTypeNameObj = new StringBuilder();
                    String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
                    String statusTypeName = statusTypeNameObj.toString();
                    onelv1.setStatusTypeName(statusTypeName);
                    onelv1.setStatusName(statusName);
                    
                    String memo = "";
                    memo += statusName;
                    if (!isNeedProductionTransfer)
                    {
                        memo +="<br>无须产生调拨单(生产门店需要生产的商品数量都是0)";
                    }
                    memo +=logmemo;
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
                    //只要有一个失败，就不在执行下面的
                    isAllSucess = false;
                    Map<String, Object> errorMap = new HashMap<String, Object>();
                    errorMap.put("orderNo", orderNo);
                    errorMap.put("errorDesc", e.getMessage());
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
                if(sucessOrderList!=null&&sucessOrderList.isEmpty()==false)
                {
                    errorDescBuffer.append("<br>单号:");
                    for (String sucessOrderNo : sucessOrderList)
                    {
                        errorDescBuffer.append(sucessOrderNo+",");
                    }
                    errorDescBuffer.append("调拨成功！");
                }
                
                
                //找下没有执行的
                List<String> noProcessOrderList = new ArrayList<String>();
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
                if(noProcessOrderList!=null&&noProcessOrderList.isEmpty()==false)
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
                res.setServiceDescription("服务执行失败！<br>"+ errorDescBuffer);
                
            }
            
            
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500,e.getMessage());
        }
        
        
        
    }
    
    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderProductionTransfer_OpenReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderProductionTransfer_OpenReq req) throws Exception {
        return null;
    }
    
    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderProductionTransfer_OpenReq req) throws Exception {
        return null;
    }
    
    @Override
    protected boolean isVerifyFail(DCP_OrderProductionTransfer_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        
        if(req.getRequest()==null) {
            isFail = true;
            errMsg.append("request不能为空 ");
        }else {
            List<OrderList> orderList = req.getRequest().getOrderList();
            if (orderList == null || orderList.isEmpty()) {
                isFail = true;
                errMsg.append("orderList不能为空 ");
            }
            if (Check.Null(req.getRequest().getShopId())) {
                isFail = true;
                errMsg.append("shopId不能为空 ");
            }
        }
        
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        
        return isFail;
    }
    
    @Override
    protected TypeToken<DCP_OrderProductionTransfer_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderProductionTransfer_OpenReq>(){};
    }
    
    @Override
    protected DCP_OrderProductionTransfer_OpenRes getResponseType() {
        return new DCP_OrderProductionTransfer_OpenRes();
    }
    
    private String getStockOutNo(String eId,String shopId) throws Exception{
        StringBuffer sqlbuf = new StringBuffer();
        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String stockOutNo = "DBCK" + accountDate;
        sqlbuf.append(""
                + " select max(stockoutno) as stockoutno "
                + " from dcp_stockout"
                + " where organizationno = '"+shopId+"' and eid = '"+eId+"' and shopid = '"+shopId+"'"
                + " and stockoutno like '%%" + stockOutNo + "%%' ");
        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
        if (getQData != null && !getQData.isEmpty()) {
            stockOutNo = getQData.get(0).get("STOCKOUTNO").toString();
            if (stockOutNo != null && stockOutNo.length() > 0) {
                long i;
                stockOutNo = stockOutNo.substring(4);
                i = Long.parseLong(stockOutNo) + 1;
                stockOutNo = i + "";
                stockOutNo = "DBCK" + stockOutNo;
            } else {
                stockOutNo = "DBCK" + accountDate + "00001";
            }
        } else {
            stockOutNo = "DBCK" + accountDate + "00001";
        }
        
        return stockOutNo;
    }
    
    private String getReceivingNO(String eId, String transferShop) throws Exception {
        String accountDate = PosPub.getAccountDate_SMS(dao,eId,transferShop);
        String receivingNo = "SHTZ" + accountDate;
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(""
                + " select max(receivingno) as receivingno from dcp_receiving"
                + " where organizationno = '"+transferShop+"' and eid='"+eId+"' and shopid = '"+transferShop+"' "
                + " and receivingno like '%%" + receivingNo + "%%' ");
        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
        if (getQData != null && !getQData.isEmpty()){
            receivingNo = getQData.get(0).get("RECEIVINGNO").toString();
            if (receivingNo != null && receivingNo.length() > 0) {
                long i;
                receivingNo = receivingNo.substring(4);
                i = Long.parseLong(receivingNo) + 1;
                receivingNo = i + "";
                receivingNo = "SHTZ" + receivingNo;
            } else {
                receivingNo = "SHTZ" + accountDate + "00001";
            }
        } else {
            receivingNo = "SHTZ" + accountDate + "00001";
        }
        
        return receivingNo;
    }
    
    
}
