package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderGoodsStatusUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderGoodsStatusUpdate_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import org.apache.cxf.common.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服務函數：DCP_OrderGoodsStatusUpdate_Open
 *   說明：订单商品状态修改
 * 服务说明：订单商品状态修改
 * @author wangzyc
 * @since  2021-4-20
 */
public class DCP_OrderGoodsStatusUpdate_Open extends SPosAdvanceService<DCP_OrderGoodsStatusUpdate_OpenReq, DCP_OrderGoodsStatusUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_OrderGoodsStatusUpdate_OpenReq req, DCP_OrderGoodsStatusUpdate_OpenRes res) throws Exception {
        DCP_OrderGoodsStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        String goodsStatus = request.getGoodsStatus();
        String opNo = request.getOpNo();
        String shopNo = request.getShopNo();
        String stallId = request.getStallId();
        String eId = req.geteId();
        List<DCP_OrderGoodsStatusUpdate_OpenReq.level2Elm> goodsList = request.getGoodsList();

        res.setDatas(new ArrayList<DCP_OrderGoodsStatusUpdate_OpenRes.level1Elm>());

        // 员工有传值 则判断查询员工是否激活 否则不判断
        if(!Check.Null(opNo)){
        try {
            String sql = "select * from PLATFORM_STAFFS where eid = '"+eId+"' and ORGANIZATIONNO = '"+shopNo+"' and OPNO = '"+opNo+"' and  STATUS ='100'";
            List<Map<String, Object>> getActivation = this.doQueryData(sql, null);
            if(!CollectionUtils.isEmpty(getActivation)){
                String activation = getActivation.get(0).get("ACTIVATION").toString();
                if(activation.equals("N")||Check.Null(activation)){
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("当前员工未激活，请激活后重新操作");
                    return ;
                }
            }else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("当前员工不存在，请重新操作");
                return ;
            }
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败:"+e.getMessage());
            return ;
        }
        }

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
        String createDate = dfDate.format(cal.getTime());
        String createTime = dfTime.format(cal.getTime());
        String createBy = req.getApiUser().getUserCode();

        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopNo);
        Date parse = new SimpleDateFormat("yyyyMMdd").parse(bDate);
        bDate = new SimpleDateFormat("yyyy-MM-dd").format(parse);

        String yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());


        MyCommon MC = new MyCommon();

        try {
            if(!CollectionUtils.isEmpty(goodsList)){

                String[] columnsPstockin = {
                        "SHOPID", "ORGANIZATIONNO","BDATE","PSTOCKIN_ID","CREATEBY", "CREATE_DATE", "CREATE_TIME","TOT_PQTY",
                        "TOT_AMT", "TOT_CQTY", "EID","PSTOCKINNO", "MEMO", "STATUS", "PROCESS_STATUS","OFNO","PTEMPLATENO",
                        "ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","OTYPE","WAREHOUSE","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                        "SUBMITBY","SUBMIT_DATE","SUBMIT_TIME","DOC_TYPE","TOT_DISTRIAMT","ACCOUNT_CHATUSERID",
                        "UPDATE_TIME","TRAN_TIME"
                };

                String[] columnsPstockinDetail = {
                        "PSTOCKINNO", "SHOPID", "item", "oItem", "pluNO","punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
                        "price", "amt", "EID", "organizationNO","task_qty", "scrap_qty", "mul_Qty", "bsNO",
                        "memo", "gDate", "gTime", "WAREHOUSE",
                        "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE","FEATURENO","OFNO"
                };

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

                if(goodsStatus.equals("2")){
                    boolean isUpdateProductStatus = false;
                    // 查询门店的物流状态类型 判断接单配送设置档的【同城配送类型】或【全国配送类型】=1.自配送，则回写物流状态为0.已下单；
                   String  sql = "SELECT * FROM DCP_ORG_ORDERTAKESET WHERE EID = '"+eId+"' AND ORGANIZATIONNO = '"+shopNo+"' ";
                    List<Map<String, Object>> getEliveryType = this.doQueryData(sql, null);

                    // 状态为2 时 判断对应单据下商品是否都制作完成 制作完成则更改订单状态
                    for (DCP_OrderGoodsStatusUpdate_OpenReq.level2Elm level2Elm : goodsList) {
                        String pluNo = level2Elm.getPluNo();
                        String oItem = level2Elm.getOItem();
                        String pqty = level2Elm.getQty();
                        String item = level2Elm.getItem();
                        String orderNo = level2Elm.getOrderNo();
                        //更新单身 DCP_PROCESSTASK_DETAIL
                        UptBean ub = null;
                        ub = new UptBean("DCP_PROCESSTASK_DETAIL");
                        ub.addUpdateValue("GOODSSTATUS", new DataValue(goodsStatus, Types.VARCHAR));
                        if(!Check.Null(opNo)){
                            ub.addUpdateValue("OPNO", new DataValue(opNo, Types.VARCHAR));
                        }
                        ub.addUpdateValue("COMPLETETIME", new DataValue(yyyyMMddHHmmssSSS, Types.VARCHAR));
                        ub.addUpdateValue("STALLID", new DataValue(stallId, Types.VARCHAR));

                        ub.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
                        ub.addCondition("OITEM", new DataValue(oItem, Types.VARCHAR));
                        ub.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                        ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        ub.addCondition("PQTY", new DataValue(pqty, Types.VARCHAR));
                        ub.addCondition("SHOPID", new DataValue(shopNo, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub));

                        // *************************************************** 生成完工入库  Begin*****************************************************
                         sql  = "SELECT * FROM DCP_PROCESSTASK_DETAIL a " +
                                " LEFT JOIN DCP_PROCESSTASK b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO" +
                                " WHERE a.SHOPID = '"+shopNo+"' AND a.OFNO = '"+level2Elm.getOrderNo()+"' AND a.EID = '"+eId+"'  AND a.PLUNO = '"+pluNo+"' " +
                                " and a.ITEM = '"+item+"'";
                        List<Map<String, Object>> getPlunoDetail = this.doQueryData(sql, null);

                        String punit = getPlunoDetail.get(0).get("PUNIT").toString();
                        String baseUnit = getPlunoDetail.get(0).get("BASEUNIT").toString();
                        String baseQty = getPlunoDetail.get(0).get("BASEQTY").toString();
                        String unit_ratio = getPlunoDetail.get(0).get("UNIT_RATIO").toString();
                        String price = getPlunoDetail.get(0).get("PRICE").toString();
                        String amt = getPlunoDetail.get(0).get("AMT").toString();
                        String mul_qty = getPlunoDetail.get(0).get("MUL_QTY").toString();
                        String warehouse = getPlunoDetail.get(0).get("WAREHOUSE").toString();
                        String featureno = getPlunoDetail.get(0).get("FEATURENO").toString();
                        String distriPrice = getPlunoDetail.get(0).get("DISTRIPRICE").toString();
                        String distriAmt = getPlunoDetail.get(0).get("DISTRIAMT").toString();

                        String pStockInNO = getPStockInNO(req);
                        int insColCt = 0;
                        DataValue[] columnsVal = new DataValue[columnsPstockinDetail.length];
                        for (int i = 0; i < columnsVal.length; i++) {
                            String keyVal = null;
                            switch (i) {
                                case 0:
                                    keyVal = pStockInNO;
                                    break;
                                case 1:
                                    keyVal = shopNo;
                                    break;
                                case 2:
                                    keyVal = "1"; //item 只有一种商品 所以为1
                                    break;
                                case 3:
                                    keyVal = oItem;//oItem
                                    break;
                                case 4:
                                    keyVal = pluNo; //pluNO
                                    break;
                                case 5:
                                    keyVal = punit; //punit
                                    break;
                                case 6:
                                    keyVal = pqty; //pqty
                                    break;
                                case 7:
                                    keyVal = baseUnit;     //wunit
                                    break;
                                case 8:
                                    keyVal = baseQty;   //wqty
                                    break;
                                case 9:
                                    keyVal = unit_ratio;     //unitRatio
                                    break;
                                case 10:
                                    keyVal = price;    //price
                                    if(Check.Null(keyVal))
                                        keyVal = "0";
                                    break;
                                case 11:
                                    keyVal = amt;    //amt
                                    break;
                                case 12:
                                    keyVal = eId;
                                    break;
                                case 13:
                                    keyVal = shopNo;
                                    break;
                                case 14:
                                    keyVal = "0";	//taskQty
                                    break;
                                case 15:
                                    keyVal = "";	//scrapQty
                                    if (Check.Null(keyVal))
                                        keyVal="0";
                                    break;
                                case 16:
                                    keyVal = mul_qty;
                                    break;
                                case 17:
                                    keyVal = ""; // bsNo
                                    break;
                                case 18:
                                    keyVal = "";
                                    break;
                                case 19:
                                    keyVal = ""; // GDate
                                    break;
                                case 20:
                                    keyVal = ""; // gTime
                                    break;
                                case 21:
                                    keyVal = warehouse;
                                    break;
                                case 22:
                                    keyVal = ""; // BATCH_NO
                                    break;
                                case 23:
                                    keyVal = ""; // PROD_DATE
                                    break;
                                case 24:
                                    keyVal= distriPrice;
                                    break;
                                case 25:
                                    keyVal = distriAmt;
                                    break;
                                case 26:
                                    keyVal = createDate;
                                    break;
                                case 27:
                                    keyVal = featureno;
                                    if (Check.Null(keyVal))
                                        keyVal=" ";
                                    break;
                                case 28:
                                    keyVal = orderNo;
                                    break;
                                default:
                                    break;
                            }

                            if (keyVal != null) {
                                insColCt++;
                                if (i == 2 || i == 3){
                                    columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                }else if (i == 6 || i == 8 || i == 9 || i == 10 || i == 11 || i == 14 || i == 15 || i == 16 || i == 24){
                                    columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                                }else{
                                    columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                                }
                            }
                            else {
                                columnsVal[i] = null;
                            }
                        }

                        String[] columns2  = new String[insColCt];
                        DataValue[] insValue2 = new DataValue[insColCt];
                        // 依照傳入參數組譯要insert的欄位與數值；
                        insColCt = 0;
                        for (int i=0;i<columnsVal.length;i++){
                            if (columnsVal[i] != null){
                                columns2[insColCt] = columnsPstockinDetail[i];
                                insValue2[insColCt] = columnsVal[i];
                                insColCt ++;
                                if (insColCt >= insValue2.length)
                                    break;
                            }
                        }
                        InsBean ib2 = new InsBean("DCP_PSTOCKIN_DETAIL", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));

                        //region 写成品库存流水
                        String procedure="SP_DCP_StockChange";
                        Map<Integer,Object> inputParameter = new HashMap<Integer, Object>();
                        inputParameter.put(1,eId);                                      //--企业ID
                        inputParameter.put(2,shopNo);                                   //--组织
                        inputParameter.put(3,"08");                                     //--单据类型
                        inputParameter.put(4,pStockInNO);	                            //--单据号
                        inputParameter.put(5,1);                                      //--单据行号 只有一种商品
                        inputParameter.put(6,"1");                                      //--异动方向 1=加库存 -1=减库存
                        inputParameter.put(7,bDate);                                    //--营业日期 yyyy-MM-dd
                        inputParameter.put(8,pluNo);                                    //--品号
                        inputParameter.put(9,featureno);                                //--特征码
                        inputParameter.put(10,warehouse);                               //--仓库
                        inputParameter.put(11,"");                                      //--批号
                        inputParameter.put(12,punit);                                   //--交易单位
                        inputParameter.put(13,pqty);                                //--交易数量
                        inputParameter.put(14,baseUnit);                          //--基准单位
                        inputParameter.put(15,baseQty);                     //--基准数量
                        inputParameter.put(16,unit_ratio);                         //--换算比例
                        inputParameter.put(17,price);                             //--零售价
                        inputParameter.put(18,amt);                                     //--零售金额
                        inputParameter.put(19,distriPrice);                       //--进货价
                        inputParameter.put(20,distriAmt);                         //--进货金额
                        inputParameter.put(21,createDate);                             //--入账日期 yyyy-MM-dd
                        inputParameter.put(22,"");                                      //--批号的生产日期 PROD_DATE yyyy-MM-dd
                        inputParameter.put(23,bDate);                                   //--单据日期
                        inputParameter.put(24,"");                                      //--异动原因
                        inputParameter.put(25,"订单转完工，成品入库");                  //--异动描述
                        inputParameter.put(26,createBy);                                //--操作员

                        ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                        this.addProcessData(new DataProcessBean(pdb));

                        //新增原料单身（多笔）

                        //region 获取商品BOM信息
                        String bomSql = ""
                                + " select distinct a.pluno ,A.eid,  A.Bomno , A.bomType , A.Restrictshop , A.Pluno , A.UNIT , A.MULQTY , A.EFFDATE , "
                                + " C.material_PluNo AS materialPluNo , C.material_Unit AS materialUnit , c.material_Qty AS materialQty , C.isBuckle ,"
                                + " C.Qty , C.isreplace , c.sortId ,  to_char(B.SHOPID ) AS shopId , d.unitRatio ,"
                                + " E.BOM_UNIT , E.Baseunit , E.sunit "
                                + " from dcp_bom a "
                                + " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+shopNo+"' "
                                + " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno "
                                + " and trunc(c.material_bdate)<=trunc(sysdate) and trunc(material_edate)>=trunc(sysdate) "
                                //【ID1029991】【大拇指-3.0】完工入库红冲单中的单位和写入到库存流水表中的单位不一致  by jinzma 20221129
                                //+ " inner join dcp_goods_unit d on a.eid=d.eid and a.pluno=d.pluno and a.unit=d.ounit and d.prod_unit_use='Y' "
                                + " inner join dcp_goods_unit d on a.eid=d.eid and c.material_pluno=d.pluno and c.material_unit=d.ounit and d.prod_unit_use='Y'"
                                
                                + " INNER JOIN Dcp_Goods e ON C.eId = E.EID AND C.MATERIAL_Pluno = E.Pluno AND E.Status = '100' "
                                + " where a.eId='"+eId+"' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and a.bomtype = '0'  "
                                + " AND A.pluNo = '"+pluNo+"'"
                                + " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))";

                        List<Map<String, Object>> getBomDatas = this.doQueryData(bomSql, null);
                        List<Map<String, Object>> pluList  = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(getBomDatas))

                            for (Map<String,Object> bomMap : getBomDatas) {
                                String materialPluNo = bomMap.get("MATERIALPLUNO").toString();
                                String materialUnit = bomMap.get("MATERIALUNIT").toString(); //原料用料单位

                                if(!Check.Null(materialPluNo)){
                                    Map<String, Object> pluMap = new HashMap<>();
                                    pluMap.put("PLUNO", materialPluNo);
                                    pluMap.put("PUNIT", materialUnit); //原料用料单位
                                    pluList.add(pluMap);
                                }
                            }
                        List<Map<String, Object>> getPluPrice = MC.getSalePrice_distriPrice(dao,eId,eId, shopNo,pluList,eId);
                            String detailMulQty = "0";
                            int materialItem = 1;
                            /**
                             *
                             * 原料表  DCP_PstockIn_Material
                             */
                            //region 插入原料表信息 以及原料库存流水 DCP_PstockIn_Material ，
                            for (Map<String,Object> bomMap : getBomDatas) {
                                if(pluNo.equals(bomMap.get("PLUNO"))){
                                    {

                                        detailMulQty = bomMap.get("MULQTY").toString(); //成品倍量， 完工入库明细表 DCP_PStockIn_Detail 上需要记录
                                        String detailUnit = bomMap.get("UNIT").toString(); // 成品BOM用料单位， 用于计算成品录入单位到BOM用料单位的 数量
                                        String materialPluNo = bomMap.get("MATERIALPLUNO").toString();
                                        String materialUnit = bomMap.get("MATERIALUNIT").toString(); //原料BOM用料单位
                                        String materialBaseUnit = bomMap.get("BASEUNIT").toString(); //原料基础单位
                                        String materialUnitRatio = bomMap.get("UNITRATIO").toString(); //原料单位换算率
                                        String isBuckle = bomMap.get("ISBUCKLE").toString(); // 是否扣料件

                                        Map<String, Object> condiV = new HashMap<String, Object>();
                                        condiV.put("PLUNO",materialPluNo);
                                        condiV.put("PUNIT",materialUnit); //原料用料单位
                                        List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);

                                        String materialPrice = "0";
                                        String materialDistriPrice = "0";
                                        if(priceList!=null && priceList.size()>0 )
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
                                        String mainPluBomUnitQtyStr = PosPub.getUnitConvert(dao, eId, pluNo, punit, detailUnit, pqty+"");

                                        BigDecimal mainPluBomUnitQty = new BigDecimal(mainPluBomUnitQtyStr);
                                        BigDecimal finalProdBaseQty = new BigDecimal(finalProdBaseQtyStr);
                                        BigDecimal rawMaterialBaseQty = new BigDecimal(rawMaterialBaseQtyStr);

                                        BigDecimal materialQty = new BigDecimal("0");
                                        materialQty = mainPluBomUnitQty.multiply(finalProdBaseQty).multiply(rawMaterialBaseQty);

                                        // 数量 和 金额 保留位数， 应该根据单位长度保留
                                        // materialQty = materialQty.setScale(2, BigDecimal.ROUND_UP);
                                        // BigDecimal materialAmt = materialQty.multiply(new BigDecimal(materialPrice)).setScale(2, BigDecimal.ROUND_UP);
                                        BigDecimal materialAmt = materialQty.multiply(new BigDecimal(materialPrice)); // 零售价对应金额应该也要保留位数
                                        BigDecimal materialDistAmt = materialQty.multiply(new BigDecimal(materialDistriPrice)); // 进货价对应金额应该也要保留位数

                                        String materialWarehouse = warehouse ; //原料仓库，取门店默认出货仓库

                                        String materialBaseQty = PosPub.getUnitConvert(dao, eId, materialPluNo, materialUnit, materialBaseUnit, materialQty+"");
                                        String batchNo = ""; //bomMap.get("").toString(); //批号

                                        int insColCt2 = 0;

                                        DataValue[] matColumnsVal = new DataValue[matColumnsName.length];
                                        for (int j = 0; j < matColumnsVal.length; j++) {
                                            String matKeyVal = null;

                                            switch (j) {
                                                case 0:
                                                    matKeyVal = item + ""; //字段值来源是 订单商品项次。 暂不考虑套餐商品（套餐商品不能转完工，没有BOM信息）
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
                                                    matKeyVal = shopNo;
                                                    break;
                                                case 12:
                                                    matKeyVal = pStockInNO; //完工入库单号
                                                    break;
                                                case 13:
                                                    matKeyVal = shopNo;
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
                                                    matKeyVal = createDate;
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
                                        String procedure2="SP_DCP_StockChange";
                                        Map<Integer,Object> inputParameter2 = new HashMap<Integer, Object>();
                                        inputParameter2.put(1,eId);                                      //--企业ID
                                        inputParameter2.put(2,shopNo);                                   //--组织
                                        inputParameter2.put(3,"11");                             //--单据类型
                                        inputParameter2.put(4,pStockInNO);	                            //--单据号
                                        inputParameter2.put(5,materialItem);                               //--单据行号
                                        inputParameter2.put(6,"-1");                                //--异动方向 1=加库存 -1=减库存
                                        inputParameter2.put(7,bDate);                                    //--营业日期 yyyy-MM-dd
                                        inputParameter2.put(8,materialPluNo);                                    //--品号
                                        inputParameter2.put(9," ");                                //--特征码
                                        inputParameter2.put(10,warehouse);                           //--仓库
                                        inputParameter2.put(11,"");                                      //--批号
                                        inputParameter2.put(12,materialUnit);                                   //--交易单位
                                        inputParameter2.put(13,materialQty);                                //--交易数量
                                        inputParameter2.put(14,materialBaseUnit);                          //--基准单位
                                        inputParameter2.put(15,materialBaseQty);                     //--基准数量
                                        inputParameter2.put(16,materialUnitRatio);                         //--换算比例
                                        inputParameter2.put(17,materialPrice);                             //--零售价
                                        inputParameter2.put(18,materialAmt);                                     //--零售金额
                                        inputParameter2.put(19,materialDistriPrice);                       //--进货价
                                        inputParameter2.put(20,materialDistAmt);                         //--进货金额
                                        inputParameter2.put(21,createDate);                             //--入账日期 yyyy-MM-dd
                                        inputParameter2.put(22,"");                                      //--批号的生产日期 PROD_DATE yyyy-MM-dd
                                        inputParameter2.put(23,createDate);                                   //--单据日期
                                        inputParameter2.put(24,"");                                      //--异动原因
                                        inputParameter2.put(25,"订单转完工，原料出库");                  //--异动描述
                                        inputParameter2.put(26,createBy);                                //--操作员

                                        ProcedureBean pdb2 = new ProcedureBean(procedure2, inputParameter2);
                                        this.addProcessData(new DataProcessBean(pdb2));
                                        //endregion
                                        materialItem++;

                                    }
                                }
                            }

                        //region 写完工入库主表 DCP_PSTOCKIN
                        String pStockIn_Id = "";
                        DataValue[] insValue1 = null;
                        insValue1 = new DataValue[]{
                                new DataValue(shopNo, Types.VARCHAR),
                                new DataValue(shopNo, Types.VARCHAR),
                                new DataValue(createDate, Types.VARCHAR),
                                new DataValue(pStockIn_Id, Types.VARCHAR),
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(createDate, Types.VARCHAR),
                                new DataValue(createTime, Types.VARCHAR),
                                new DataValue(pqty, Types.VARCHAR),
                                new DataValue(amt, Types.VARCHAR),
                                new DataValue("1", Types.VARCHAR),
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(pStockInNO, Types.VARCHAR),

                                new DataValue("订单转完工入库", Types.VARCHAR),//memo 备注
                                new DataValue("2", Types.VARCHAR),//status 完工入库单状态
                                new DataValue("N", Types.VARCHAR), //process_status 上传状态
                                new DataValue(orderNo, Types.VARCHAR), //ofNo 来源单号
                                new DataValue("", Types.VARCHAR), //PTEMPLATENO 模板编号
                                new DataValue(opNo, Types.VARCHAR), //ACCOUNTBY
                                new DataValue(createDate, Types.VARCHAR),
                                new DataValue(createTime, Types.VARCHAR),
                                new DataValue("3", Types.VARCHAR), // oType 来源类型
                                new DataValue(warehouse, Types.VARCHAR),
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(createDate, Types.VARCHAR),
                                new DataValue(createTime, Types.VARCHAR),
                                new DataValue(opNo, Types.VARCHAR),
                                new DataValue(createDate, Types.VARCHAR),
                                new DataValue(createTime, Types.VARCHAR),
                                new DataValue("0", Types.VARCHAR), // docType 单据类型 0：完工入库  1：组合   2：拆解   3：转换合并  4：转换拆解
                                new DataValue(distriAmt, Types.VARCHAR), //进货价合计
                                new DataValue(req.getChatUserId(), Types.VARCHAR),
								new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
								new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        };
                        InsBean ib1 = new InsBean("DCP_PSTOCKIN", columnsPstockin);
                        ib1.addValues(insValue1);
                        this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                        // *************************************************** 生成完工入库  End*****************************************************
                        this.doExecuteDataToDB();

                        // 如果 订单下所有商品 状态都为已完成 则修改order/生产 中的订单生产状态为 6
                        sql = this.getGoodsStatus(req,level2Elm.getOrderNo());
                        List<Map<String, Object>> datas = this.doQueryData(sql, null);
                        if(CollectionUtils.isEmpty(datas)){

                            // 修改 生产订单状态为 6
                            UptBean ub3 = null;
                            ub3 = new UptBean("DCP_PROCESSTASK");
                            ub3.addUpdateValue("PRODUCTSTATUS", new DataValue("6", Types.VARCHAR));
                            ub3.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                            ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub3.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
                            ub3.addCondition("SHOPID", new DataValue(shopNo, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub3));

                            // 查判断接单配送设置档的【同城配送类型】或【全国配送类型】=1.自配送，则回写物流状态为0.已下单；
                            Boolean isUpdateEliveryStatus = false;
                            if(!CollectionUtils.isEmpty(getEliveryType)){
                                for (Map<String, Object> eliveryType : getEliveryType) {
                                    String citydeliverytype = eliveryType.get("CITYDELIVERYTYPE").toString();
                                    String nationaldeliverytype = eliveryType.get("NATIONALDELIVERYTYPE").toString();
                                    if(citydeliverytype.equals("1") || nationaldeliverytype.equals("1")){
                                        isUpdateEliveryStatus = true;
                                        break;
                                    }
                                }
                            }

//                            sql = "select * from DCP_PROCESSTASK where PROCESSTASKNO = '"+level2Elm.getOrderNo()+"' ";
//                            List<Map<String, Object>> getOrderNo = this.doQueryData(sql, null);
//                            String ofno = getOrderNo.get(0).get("OFNO").toString();
                            // 如果该单据下 所有商品都已制作完成 则更改生产状态为 6 完工入库
                            UptBean ub2 = null;
                            ub2 = new UptBean("DCP_ORDER");
                            ub2.addUpdateValue("PRODUCTSTATUS", new DataValue("6", Types.VARCHAR));
                            ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                            ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                            if(isUpdateEliveryStatus){
                                // 更改物流状态
                                ub2.addUpdateValue("DELIVERYSTATUS", new DataValue("0", Types.VARCHAR));
                            }
                            ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(ub2));

                            // 修改状态后 返回Order 订单的 商品详情 提供于小票打印
                            sql = "SELECT a.Shop,a.SHOPNAME,b.ITEM, b.PLUNO, b.PLUNAME,e.PLU_NAME, c.SPEC, b.FEATURENAME,b.QTY , d.MEMOTYPE, d.MEMONAME, d.MEMO " +
                                    " FROM DCP_ORDER a" +
                                    " LEFT JOIN DCP_ORDER_DETAIL b ON a.EID = b.EID AND a.ORDERNO = b.ORDERNO " +
                                    " LEFT JOIN DCP_GOODS_UNIT_LANG c ON a.EID = c.EID AND b.PLUNO = c.PLUNO AND b.SUNIT = c.OUNIT AND c.LANG_TYPE = '"+req.getLangType()+"' " +
                                    " LEFT JOIN DCP_ORDER_DETAIL_MEMO d ON a.EID = d.EID AND a.ORDERNO = d.ORDERNO AND b.ITEM = d.OITEM  " +
                                    " LEFT JOIN DCP_GOODS_LANG e ON a.EID = e.EID AND b.PLUNO = e.PLUNO AND e.LANG_TYPE = '"+req.getLangType()+"'" +
                                    " WHERE a.EID = '"+eId+"' AND a.ORDERNO = '"+orderNo+"'";
                            List<Map<String, Object>> getGoodsList = this.doQueryData(sql, null);
                            if(!CollectionUtils.isEmpty(getGoodsList)){
                                DCP_OrderGoodsStatusUpdate_OpenRes.level1Elm lv1 = res.new level1Elm();
                                lv1.setOrderNo(orderNo);
                                lv1.setShopNo(getGoodsList.get(0).get("SHOP").toString());
                                lv1.setShopName(getGoodsList.get(0).get("SHOPNAME").toString());

                                // 过滤
                                Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
                                condition.put("PLUNO", true);
                                condition.put("SHOP", true);
                                condition.put("ITEM", true);
                                // 调用过滤函数
                                List<Map<String, Object>> getMemos = MapDistinct.getMap(getGoodsList, condition);
                                lv1.setGoodsList(new ArrayList<DCP_OrderGoodsStatusUpdate_OpenRes.level2Elm>());
                                for (Map<String, Object> getgood : getMemos) {
                                    DCP_OrderGoodsStatusUpdate_OpenRes.level2Elm lv2 = res.new level2Elm();
                                    String item1 = getgood.get("ITEM").toString();
                                    String pluno = getgood.get("PLUNO").toString();

                                    String pluName = getgood.get("PLUNAME").toString();
                                    String plu_name = getgood.get("PLU_NAME").toString();

                                    String spec = getgood.get("SPEC").toString();
                                    String featureName = getgood.get("FEATURENAME").toString();
                                    String qty = getgood.get("QTY").toString();

                                    lv2.setMessages(new ArrayList<DCP_OrderGoodsStatusUpdate_OpenRes.level3Elm>());
                                    for (Map<String, Object> getMemo : getGoodsList) {
                                        String oItemMemo = getMemo.get("ITEM").toString();
                                        String oPlunoMemo = getMemo.get("PLUNO").toString();
                                        // 过滤不属于此商品的 备注
                                        if(oItemMemo.equals(item1)&&pluno.equals(oPlunoMemo)){
                                            DCP_OrderGoodsStatusUpdate_OpenRes.level3Elm lv3 = res.new level3Elm();
                                            String memoType = getMemo.get("MEMOTYPE").toString();
                                            String memoName = getMemo.get("MEMONAME").toString();
                                            String memo = getMemo.get("MEMO").toString();
                                            if (Check.Null(memoType) && Check.Null(memoName) && Check.Null(memo)) {
                                                continue;
                                            }
                                            lv3.setMessage(memo);
                                            lv3.setMsgType(Check.Null(memoType) == true ? "text" : memoType);
                                            lv3.setMsgName(memoName);
                                            lv2.getMessages().add(lv3);
                                        }
                                    }

                                    lv2.setItem(item1);
                                    lv2.setPluNo(pluno);

                                    // Add 2021/6/4 增加商品名称逻辑  如果生产订单明细中商品名称 不存在 则商品名称从DCP_GOODS_LANG 中获取
                                    if(Check.Null(pluName)){
                                        lv2.setPluName(plu_name);
                                    }else{
                                        lv2.setPluName(pluName);
                                    }
                                    lv2.setSpecName(spec);
                                    lv2.setFeatureName(featureName);
                                    lv2.setQty(qty);
                                    lv1.getGoodsList().add(lv2);
                                }
                                res.getDatas().add(lv1);
                            }

                        }
                    }
                }else {
                    for (DCP_OrderGoodsStatusUpdate_OpenReq.level2Elm level2Elm : goodsList) {
                        String pluNo = level2Elm.getPluNo();
                        String oItem = level2Elm.getOItem();
                        String item = level2Elm.getItem();
                        String pqty = level2Elm.getQty();
                        String orderNo = level2Elm.getOrderNo();

                        UptBean ub = null;
                        ub = new UptBean("DCP_PROCESSTASK_DETAIL");
                        ub.addUpdateValue("GOODSSTATUS", new DataValue(goodsStatus, Types.VARCHAR));
                        if(!Check.Null(opNo)){
                            ub.addUpdateValue("OPNO", new DataValue(opNo, Types.VARCHAR));
                        }
                        if(goodsStatus.equals("1")){
                            ub.addUpdateValue("MAKETIME", new DataValue(yyyyMMddHHmmssSSS, Types.VARCHAR));
                        }
                        ub.addUpdateValue("STALLID", new DataValue(stallId, Types.VARCHAR));

                        ub.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
                        ub.addCondition("OITEM", new DataValue(oItem, Types.VARCHAR));
                        ub.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                        ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                        ub.addCondition("PQTY", new DataValue(pqty, Types.VARCHAR));
                        ub.addCondition("SHOPID", new DataValue(shopNo, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub));

                        // 若goodsStatus由2改为1，则判断订单生产状态是否为6.完工入库，若是则将订单生产状态改回4.生产接单
                        if(goodsStatus.equals("1")){
                           String sql = "select * from DCP_PROCESSTASK where OFNO = '"+level2Elm.getOrderNo()+"' ";
                            List<Map<String, Object>> getProdStatus = this.doQueryData(sql, null);
                            String productstatus = getProdStatus.get(0).get("PRODUCTSTATUS").toString();
                            if(productstatus.equals("6")){
//                                String ofno = getProdStatus.get(0).get("OFNO").toString();
                                UptBean ub2 = null;
                                ub2 = new UptBean("DCP_ORDER");
                                ub2.addUpdateValue("PRODUCTSTATUS", new DataValue("4", Types.VARCHAR));
                                ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub2));

                                // 修改 生产订单状态为 4
                                UptBean ub3 = null;
                                ub3 = new UptBean("DCP_PROCESSTASK");
                                ub3.addUpdateValue("PRODUCTSTATUS", new DataValue("4", Types.VARCHAR));
                                ub3.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                                ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub3.addCondition("OFNO", new DataValue(orderNo, Types.VARCHAR));
                                ub3.addCondition("SHOPID", new DataValue(shopNo, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub3));
                            }
                        }
                    }
                }
            }
            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败:"+e.getMessage());
        }

        this.doExecuteDataToDB();
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_OrderGoodsStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_OrderGoodsStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_OrderGoodsStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_OrderGoodsStatusUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
        if(req.getRequest()==null)
        {
            isFail = true;
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        List<DCP_OrderGoodsStatusUpdate_OpenReq.level2Elm> goodsList = req.getRequest().getGoodsList();
        if(goodsList==null||goodsList.isEmpty())
        {
            isFail = true;
            errMsg.append("商品列表不能为空, ");
        }
        if(Check.Null(req.getRequest().getShopNo()))
        {
            isFail = true;
            errMsg.append("下订门店不能为空,");
        }
        if(Check.Null(req.getRequest().getStallId()))
        {
            isFail = true;
            errMsg.append("档口编号不能为空 ");
        }

        if (isFail)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_OrderGoodsStatusUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_OrderGoodsStatusUpdate_OpenReq>(){};
    }

    @Override
    protected DCP_OrderGoodsStatusUpdate_OpenRes getResponseType() {
        return new DCP_OrderGoodsStatusUpdate_OpenRes();
    }

    /**
     * 查询 订单下商品是否制作完成
     * @param req
     * @return
     */
    private String getGoodsStatus(DCP_OrderGoodsStatusUpdate_OpenReq req,String orderNo){
        DCP_OrderGoodsStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        String sql = "";
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT * FROM DCP_PROCESSTASK_DETAIL a " +
                " WHERE a.EID = '"+req.geteId()+"' AND a.SHOPID = '"+request.getShopNo()+"' AND a.OFNO = '"+orderNo+"' AND a.GOODSSTATUS !='2'");
        sql = sqlbuf.toString();
        return sql;
    }



    private String getPStockInNO(DCP_OrderGoodsStatusUpdate_OpenReq req) throws Exception  {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String pStockInNO = null;
        String shopId = req.getRequest().getShopNo();
        String eId = req.geteId();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        StringBuffer sqlbuf = new StringBuffer("");
        String docType = "0"; //0-完工入库  1-组合单   2-拆解单  3-转换合并  4-转换拆解
        //新增服务时  docType是1的时候 单号开头字母换成ZHRK；2的时候换成CJCK
        if(docType.equals("0"))
        {
            pStockInNO = "WGRK" + bDate;
        }
        else if(docType.equals("1"))
        {
            pStockInNO = "ZHRK" + bDate;
        }
        else if(docType.equals("2")){
            pStockInNO = "CJCK" + bDate;
        }
        // 2019-05-29 若docType==3， 转换合并单，
        else if(docType.equals("3")){
            pStockInNO = "ZHHB" + bDate;
        }
        // 2019-08-19 若docType==4， 转换拆解单据，
        else if(docType.equals("4")){
            pStockInNO = "ZHCJ" + bDate;
        }
        sqlbuf.append("select PSTOCKINNO  from (select max(PSTOCKINNO) as PSTOCKINNO "
                + " from DCP_PSTOCKIN where EID = '"+eId+"' and SHOPID = '"+shopId+"' "
                + " and PSTOCKINNO like '%%" + pStockInNO + "%%' "); // 假資料
        sqlbuf.append(" ) TBL ");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && getQData.isEmpty() == false) {
            pStockInNO = (String) getQData.get(0).get("PSTOCKINNO");
            if (pStockInNO != null && pStockInNO.length() > 0) {
                long i;
                pStockInNO = pStockInNO.substring(4, pStockInNO.length());
                i = Long.parseLong(pStockInNO) + 1;
                pStockInNO = i + "";

                if(docType.equals("0")){
                    pStockInNO = "WGRK" + pStockInNO; //完工入库0
                }else if (docType.equals("1")){
                    pStockInNO = "ZHRK" + pStockInNO; //组合入库1
                }else if (docType.equals("2")){
                    pStockInNO = "CJCK" + pStockInNO; //拆解出库2
                }else if (docType.equals("3")){
                    pStockInNO = "ZHHB" + pStockInNO; //转换合并3
                }
                else if (docType.equals("4")){
                    pStockInNO = "ZHCJ" + pStockInNO;
                }
            }
            else {
                if(docType.equals("0")){
                    pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
                }else if (docType.equals("1")){
                    pStockInNO = "ZHRK" + bDate + "00001"; //组合入库1
                }else if (docType.equals("2")){
                    pStockInNO = "CJCK" + bDate + "00001"; //拆解出库2
                }else if (docType.equals("3")){
                    pStockInNO = "ZHHB" + bDate + "00001"; //拆解出库2
                }
                // 2019-08-19 若docType==4， 转换拆解单据，
                else if(docType.equals("4")){
                    pStockInNO = "ZHCJ" + bDate + "00001";
                }
            }
        }
        else
        {
            if(docType.equals("0")){
                pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
            }else if (docType.equals("1")){
                pStockInNO = "ZHRK" + bDate + "00001"; //组合入库1
            }else if (docType.equals("2")){
                pStockInNO = "CJCK" + bDate + "00001"; //拆解出库2
            }else if (docType.equals("3")){
                pStockInNO = "ZHHB" + bDate + "00001"; //转换合并3
            }
            // 2019-08-19 若docType==4， 转换拆解单据，
            else if(docType.equals("4")){
                pStockInNO = "ZHCJ" + bDate + "00001"; //转换拆解4
            }
        }

        return pStockInNO;
    }
}
