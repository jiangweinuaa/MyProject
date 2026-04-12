package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_DishStatusUpdate_OpenReq;
import com.dsc.spos.json.cust.res.DCP_DishStatusUpdate_OpenRes;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: KDS菜品状态变更
 * @author: wangzyc
 * @create: 2021-09-29
 */
public class DCP_DishStatusUpdate_Open extends SPosAdvanceService<DCP_DishStatusUpdate_OpenReq, DCP_DishStatusUpdate_OpenRes> {
    @Override
    protected void processDUID(DCP_DishStatusUpdate_OpenReq req, DCP_DishStatusUpdate_OpenRes res) throws Exception {
        DCP_DishStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        String shopId = request.getShopId();
        String eId = req.geteId();
        String machineId = request.getMachineId();
        String terminalType = request.getTerminalType();
        String goodsStatus = request.getGoodsStatus(); // 原状态
        String userId = request.getUserId();
        String updateStatus = request.getUpdateStatus(); // 变更状态
        List<DCP_DishStatusUpdate_OpenReq.level2Elm> goodsList = request.getGoodsList();
        String cookId = request.getCookId();


        // 撤销：点击撤销后，该菜品返回至未配菜列表，已制作菜品不能撤销，需制作端撤销后方可在配菜端撤销。传菜端传菜完成后，配菜端和制作端均不可撤销。
        // 若表中为2，要变更0时，报错提示：需制作端撤销后方可在配菜端撤销！
        // 若表中为3，要变更时，报错提示：配菜端与制作端均不可撤销！
        if (goodsStatus.equals("2") && updateStatus.equals("0")) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "需制作端撤销后方可在配菜端撤销！");
        } else if (goodsStatus.equals("3") && !Check.Null(updateStatus)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配菜端与制作端均不可撤销！");
        }

        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
        String createDate = dfDate.format(cal.getTime());
        String createTime = dfTime.format(cal.getTime());
        String createBy = req.getApiUser().getUserCode();

        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        Date parse = new SimpleDateFormat("yyyyMMdd").parse(bDate);
        bDate = new SimpleDateFormat("yyyy-MM-dd").format(parse);

        String yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());


        MyCommon MC = new MyCommon();

        try
        {
            // 两种逻辑
            // 一种商品维度的商品状态修改
            // 一种订单维度的商品状态修改 且变更加工任务中的商品状态
            // 变更1就记为配餐完成时间；2是制作完成时间；3是传菜取餐时间
            if (!CollectionUtils.isEmpty(goodsList))
            {
                StringBuffer sqlbuf = new StringBuffer("");

                String[] columnsPstockin = {
                        "SHOPID", "ORGANIZATIONNO", "BDATE", "PSTOCKIN_ID", "CREATEBY", "CREATE_DATE", "CREATE_TIME", "TOT_PQTY",
                        "TOT_AMT", "TOT_CQTY", "EID", "PSTOCKINNO", "MEMO", "STATUS", "PROCESS_STATUS", "OFNO", "PTEMPLATENO",
                        "ACCOUNTBY", "ACCOUNT_DATE", "ACCOUNT_TIME", "OTYPE", "WAREHOUSE", "CONFIRMBY", "CONFIRM_DATE", "CONFIRM_TIME",
                        "SUBMITBY", "SUBMIT_DATE", "SUBMIT_TIME", "DOC_TYPE", "TOT_DISTRIAMT", "ACCOUNT_CHATUSERID", "MACHINEID", "TERMINALTYPE",
                        "USERID","UPDATE_TIME","TRAN_TIME"
                };

                String[] columnsPstockinDetail = {
                        "PSTOCKINNO", "SHOPID", "item", "oItem", "pluNO", "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
                        "price", "amt", "EID", "organizationNO", "task_qty", "scrap_qty", "mul_Qty", "bsNO",
                        "memo", "gDate", "gTime", "WAREHOUSE",
                        "BATCH_NO", "PROD_DATE", "DISTRIPRICE", "DISTRIAMT", "ACCOUNT_DATE", "FEATURENO", "OFNO"
                };

                String[] matColumnsName = {
                        "MITEM", "ITEM", "WAREHOUSE",
                        "PLUNO", "PUNIT",
                        "PQTY", "PRICE", "AMT",
                        "FINALPRODBASEQTY", "RAWMATERIALBASEQTY",
                        "EID", "ORGANIZATIONNO", "PSTOCKINNO",
                        "SHOPID", "MPLUNO", "BASEUNIT",
                        "BASEQTY", "UNIT_RATIO",
                        "BATCH_NO", "PROD_DATE", "DISTRIPRICE", "DISTRIAMT", "ACCOUNT_DATE", "ISBUCKLE", "FEATURENO"
                };


                String[] columns_Processtask_Detail = {
                        "EID", "SHOPID", "ORGANIZATIONNO", "PROCESSTASKNO", "ITEM", "MUL_QTY", "PQTY",  "PUNIT",
                        "BASEQTY", "PLUNO", "PLUNAME", "PRICE", "BASEUNIT", "UNIT_RATIO", "AMT", "DISTRIPRICE", "DISTRIAMT", "BDATE", "FEATURENO",
                        "GOODSSTATUS", "FINALCATEGORY", "PLUBARCODE", "AVAILQTY"
                };


                // 写单头
                String[] columns_Processtask = {
                        "SHOPID", "PROCESSTASKNO", "EID", "ORGANIZATIONNO", "CREATE_TIME", "CREATE_DATE", "CREATEBY",
                        "STATUS", "TOT_CQTY", "PROCESS_STATUS", "BDATE", "TOT_PQTY", "MEMO", "UPDATE_TIME", "WAREHOUSE",
                        "MATERIALWAREHOUSE", "OTYPE", "CREATEDATETIME", "TOT_AMT", "TOT_DISTRIAMT"
                };


                if (updateStatus.equals("2"))
                {
                    // 加工任务中的仓库有可能是空的 如果为空取门店的默认出货仓
                    sqlbuf.setLength(0);
                    sqlbuf.append("select OUT_COST_WAREHOUSE from DCP_ORG where eid = '"+eId+"' and ORGANIZATIONNO = '"+shopId+"'");
                    List<Map<String, Object>> getOut_cost_warehouse = this.doQueryData(sqlbuf.toString(), null);
                    String out_cost_warehouse = "";
                    if(!CollectionUtils.isEmpty(getOut_cost_warehouse)){
                        out_cost_warehouse =  getOut_cost_warehouse.get(0).get("OUT_COST_WAREHOUSE").toString();
                    }

                    //向上取整后剩余商品转预支单的item
                    int v_beforeItem=0;
                    String v_BeforeProcessTaskNO="";
                    BigDecimal v_TotRemainQty=new BigDecimal("0");

                    // 状态为3 时 判断对应单据下商品是否都制作完成 制作完成则更改订单状态
                    for (DCP_DishStatusUpdate_OpenReq.level2Elm lv2 : goodsList)
                    {
                        //加工任务单号
                        String processTaskNo = lv2.getProcessTaskNo();
                        //来源单号，如果是预制菜就是空了
                        String billNo = lv2.getBillNo();
                        String pluNo = lv2.getPluNo();
                        String oitem = lv2.getOItem();
                        String[] itemList = lv2.getItemList();
                        String pluBarCode = lv2.getPluBarCode();

                        //这个数量是向上取整的，比如=4 其实总的数量是商品加起来是3.3
                        //多余的0.7要自动做预制菜完成动作
                        String qty = lv2.getQty();

                        List<Map<String, Object>> getPlunoDetails = null;
                        sqlbuf.setLength(0);
                        if (Check.Null(oitem))
                        {
                            //预制菜来的，没原单号，查出多单，做数量判断
                            //比如入参数量4份，其中1单有4分满足了，直接跳过，后面那单就不要更新
                            if (Check.Null(billNo))
                            {
                                // 查询出 加工任务中的商品信息 DCP_PROCESSTASK_DETAIL
                                String itemSQLIn = PosPub.getArrayStrSQLIn(itemList);
                                sqlbuf.append("SELECT * FROM DCP_PROCESSTASK_DETAIL a " +
                                                      " left join DCP_PROCESSTASK b  ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO " +
                                                      "WHERE a.EID = '" + eId + "' AND a.SHOPID = '" + shopId + "' and a.PROCESSTASKNO='"+processTaskNo+"' and nvl(a.ofno,'@#$')='@#$' and a.item IN (" + itemSQLIn + ") and a.pluno = '" + pluNo + "' and a.goodsstatus<>'2' ");
                                getPlunoDetails = this.doQueryData(sqlbuf.toString(), null);
                            }
                            else
                            {
                                // 查询出 加工任务中的商品信息 DCP_PROCESSTASK_DETAIL
                                String itemSQLIn = PosPub.getArrayStrSQLIn(itemList);
                                sqlbuf.append("SELECT * FROM DCP_PROCESSTASK_DETAIL a " +
                                                      " left join DCP_PROCESSTASK b  ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO " +
                                                      "WHERE a.EID = '" + eId + "' AND a.SHOPID = '" + shopId + "' and a.PROCESSTASKNO='"+processTaskNo+"' and a.OFNO = '" + billNo + "' AND a.item IN (" + itemSQLIn + ") and a.pluno = '" + pluNo + "' and a.goodsstatus<>'2' ");
                                getPlunoDetails = this.doQueryData(sqlbuf.toString(), null);
                            }

                        }
                        else if (!Check.Null(oitem))
                        {
                            // 查询来源项次 根据来源项次查询出对应的商品
                            sqlbuf.append("SELECT * FROM DCP_PROCESSTASK_DETAIL a" +
                                                  " left join DCP_PROCESSTASK b  ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO " +
                                                  "WHERE a.EID = '" + eId + "' AND a.SHOPID = '" + shopId + "' AND a.OFNO = '" + billNo + "' and a.OITEM = '" + oitem + "' and a.pluno = '" + pluNo + "' ");
                            getPlunoDetails = this.doQueryData(sqlbuf.toString(), null);
                        }

                        //入参总数量
                        //这个数量是向上取整的，比如=4 其实总的数量是商品加起来是3.3
                        //多余的0.7要自动做预制菜完成动作
                        BigDecimal bdm_Totqty=new BigDecimal(qty);

                        //实际数量累计
                        BigDecimal bdm_TotRealQty=new BigDecimal("0");

                        if (!CollectionUtils.isEmpty(getPlunoDetails))
                        {
                            for (Map<String, Object> getPlunoDetail : getPlunoDetails)
                            {
                                // *************************************************** 生成完工入库  Begin*****************************************************
                                //                                sql  = "SELECT * FROM DCP_PROCESSTASK_DETAIL a " +
                                //                                        " LEFT JOIN DCP_PROCESSTASK b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO" +
                                //                                        " WHERE a.SHOPID = '"+shopNo+"' AND a.OFNO = '"+level2Elm.getOrderNo()+"' AND a.EID = '"+eId+"'  AND a.PLUNO = '"+pluNo+"' " +
                                //                                        " and a.ITEM = '"+item+"'";
                                //                                List<Map<String, Object>> getPlunoDetail = this.doQueryData(sql, null);

                                String pqty = getPlunoDetail.get("PQTY").toString();

                                if (Check.Null(billNo))
                                {
                                    //更新数量达到入参数量就不扣了
                                    if (bdm_Totqty.compareTo(new BigDecimal(pqty))<0)
                                    {
                                        continue;
                                    }
                                }

                                String processtaskno = getPlunoDetail.get("PROCESSTASKNO").toString();
                                String punit = getPlunoDetail.get("PUNIT").toString();
                                String baseUnit = getPlunoDetail.get("BASEUNIT").toString();
                                String baseQty = getPlunoDetail.get("BASEQTY").toString();
                                String unit_ratio = getPlunoDetail.get("UNIT_RATIO").toString();
                                String price = getPlunoDetail.get("PRICE").toString();
                                String amt = getPlunoDetail.get("AMT").toString();
                                String mul_qty = getPlunoDetail.get("MUL_QTY").toString();
                                String warehouse = getPlunoDetail.get("WAREHOUSE").toString();
                                if(Check.Null(warehouse)){
                                    warehouse = out_cost_warehouse;
                                }
                                String featureno = getPlunoDetail.get("FEATURENO").toString();
                                String distriPrice = getPlunoDetail.get("DISTRIPRICE").toString();
                                String distriAmt = getPlunoDetail.get("DISTRIAMT").toString();
                                String oItem = getPlunoDetail.get("OITEM").toString();
                                String item = getPlunoDetail.get("ITEM").toString();
                                String otype = getPlunoDetail.get("OTYPE").toString();

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
                                            keyVal = shopId;
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
                                            if (Check.Null(keyVal))
                                                keyVal = "0";
                                            break;
                                        case 11:
                                            keyVal = amt;    //amt
                                            break;
                                        case 12:
                                            keyVal = eId;
                                            break;
                                        case 13:
                                            keyVal = shopId;
                                            break;
                                        case 14:
                                            keyVal = "0";    //taskQty
                                            break;
                                        case 15:
                                            keyVal = "";    //scrapQty
                                            if (Check.Null(keyVal))
                                                keyVal = "0";
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
                                            keyVal = distriPrice;
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
                                                keyVal = " ";
                                            break;
                                        case 28:
                                            keyVal = billNo;
                                            break;
                                        default:
                                            break;
                                    }

                                    if (keyVal != null) {
                                        insColCt++;
                                        if (i == 2 || i == 3) {
                                            columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                        } else if (i == 6 || i == 8 || i == 9 || i == 10 || i == 11 || i == 14 || i == 15 || i == 16 || i == 24) {
                                            columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                                        } else {
                                            columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                                        }
                                    } else {
                                        columnsVal[i] = null;
                                    }
                                }

                                String[] columns2 = new String[insColCt];
                                DataValue[] insValue2 = new DataValue[insColCt];
                                // 依照傳入參數組譯要insert的欄位與數值；
                                insColCt = 0;
                                for (int i = 0; i < columnsVal.length; i++) {
                                    if (columnsVal[i] != null) {
                                        columns2[insColCt] = columnsPstockinDetail[i];
                                        insValue2[insColCt] = columnsVal[i];
                                        insColCt++;
                                        if (insColCt >= insValue2.length)
                                            break;
                                    }
                                }
                                InsBean ib2 = new InsBean("DCP_PSTOCKIN_DETAIL", columns2);
                                ib2.addValues(insValue2);
                                this.addProcessData(new DataProcessBean(ib2));

                                //region 写成品库存流水
                                String procedure = "SP_DCP_StockChange";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                      //--企业ID
                                inputParameter.put(2, shopId);                                   //--组织
                                inputParameter.put(3, "08");                                     //--单据类型
                                inputParameter.put(4, pStockInNO);                                //--单据号
                                inputParameter.put(5, 1);                                      //--单据行号 只有一种商品
                                inputParameter.put(6, "1");                                      //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(7, bDate);                                    //--营业日期 yyyy-MM-dd
                                inputParameter.put(8, pluNo);                                    //--品号
                                inputParameter.put(9, featureno);                                //--特征码
                                inputParameter.put(10, warehouse);                               //--仓库
                                inputParameter.put(11, "");                                      //--批号
                                inputParameter.put(12, punit);                                   //--交易单位
                                inputParameter.put(13, pqty);                                //--交易数量
                                inputParameter.put(14, baseUnit);                          //--基准单位
                                inputParameter.put(15, baseQty);                     //--基准数量
                                inputParameter.put(16, unit_ratio);                         //--换算比例
                                inputParameter.put(17, price);                             //--零售价
                                inputParameter.put(18, amt);                                     //--零售金额
                                inputParameter.put(19, distriPrice);                       //--进货价
                                inputParameter.put(20, distriAmt);                         //--进货金额
                                inputParameter.put(21, createDate);                             //--入账日期 yyyy-MM-dd
                                inputParameter.put(22, "");                                      //--批号的生产日期 PROD_DATE yyyy-MM-dd
                                inputParameter.put(23, bDate);                                   //--单据日期
                                inputParameter.put(24, "");                                      //--异动原因
                                inputParameter.put(25, "订单转完工，成品入库");                  //--异动描述
                                inputParameter.put(26, createBy);                                //--操作员

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
                                        + " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='" + shopId + "' "
                                        + " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno "
                                        + " and trunc(c.material_bdate)<=trunc(sysdate) and trunc(c.material_edate)>=trunc(sysdate) "
                                        //【ID1029991】【大拇指-3.0】完工入库红冲单中的单位和写入到库存流水表中的单位不一致  by jinzma 20221129
                                        //+ " inner join dcp_goods_unit d on a.eid=d.eid and a.pluno=d.pluno and a.unit=d.ounit and d.prod_unit_use='Y' "
                                        + " inner join dcp_goods_unit d on a.eid=d.eid and c.material_pluno=d.pluno and c.material_unit=d.ounit and d.prod_unit_use='Y'"

                                        + " INNER JOIN Dcp_Goods e ON C.eId = E.EID AND C.MATERIAL_Pluno = E.Pluno AND E.Status = '100' "
                                        + " where a.eId='" + eId + "' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and a.bomtype = '0'  "
                                        + " AND A.pluNo = '" + pluNo + "'"
                                        + " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))";

                                List<Map<String, Object>> getBomDatas = this.doQueryData(bomSql, null);
                                List<Map<String, Object>> pluList = new ArrayList<>();
                                if (!org.apache.cxf.common.util.CollectionUtils.isEmpty(getBomDatas))

                                    for (Map<String, Object> bomMap : getBomDatas) {
                                        String materialPluNo = bomMap.get("MATERIALPLUNO").toString();
                                        String materialUnit = bomMap.get("MATERIALUNIT").toString(); //原料用料单位

                                        if (!Check.Null(materialPluNo)) {
                                            Map<String, Object> pluMap = new HashMap<>();
                                            pluMap.put("PLUNO", materialPluNo);
                                            pluMap.put("PUNIT", materialUnit); //原料用料单位
                                            pluList.add(pluMap);
                                        }
                                    }
                                List<Map<String, Object>> getPluPrice = MC.getSalePrice_distriPrice(dao,eId,eId,shopId,pluList,eId);
                                String detailMulQty = "0";
                                int materialItem = 1;
                                /**
                                 *
                                 * 原料表  DCP_PstockIn_Material
                                 */
                                //region 插入原料表信息 以及原料库存流水 DCP_PstockIn_Material ，
                                for (Map<String, Object> bomMap : getBomDatas) {
                                    if (pluNo.equals(bomMap.get("PLUNO"))) {
                                        {

                                            detailMulQty = bomMap.get("MULQTY").toString(); //成品倍量， 完工入库明细表 DCP_PStockIn_Detail 上需要记录
                                            String detailUnit = bomMap.get("UNIT").toString(); // 成品BOM用料单位， 用于计算成品录入单位到BOM用料单位的 数量
                                            String materialPluNo = bomMap.get("MATERIALPLUNO").toString();
                                            String materialUnit = bomMap.get("MATERIALUNIT").toString(); //原料BOM用料单位
                                            String materialBaseUnit = bomMap.get("BASEUNIT").toString(); //原料基础单位
                                            String materialUnitRatio = bomMap.get("UNITRATIO").toString(); //原料单位换算率
                                            String isBuckle = bomMap.get("ISBUCKLE").toString(); // 是否扣料件

                                            Map<String, Object> condiV = new HashMap<String, Object>();
                                            condiV.put("PLUNO", materialPluNo);
                                            condiV.put("PUNIT", materialUnit); //原料用料单位
                                            List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPluPrice, condiV, false);

                                            String materialPrice = "0";
                                            String materialDistriPrice = "0";
                                            if (priceList != null && priceList.size() > 0) {
                                                materialPrice = priceList.get(0).get("PRICE").toString(); //原料用料单位materialUnit对应零售价
                                                materialDistriPrice = priceList.get(0).get("DISTRIPRICE").toString();//原料单位进货价
                                            }

                                            // 开始计算原料对应数量
                                            String mainPLuNoUnit = bomMap.get("UNIT").toString();

                                            //mainPluNoBaseQty 表示订单商品单位 对应 成品单位用量

                                            String finalProdBaseQtyStr = bomMap.get("QTY").toString(); //成品基础用量
                                            String rawMaterialBaseQtyStr = bomMap.get("MATERIALQTY").toString();  //原料基础用量

                                            // 成品BOM用料单位对应数量
                                            String mainPluBomUnitQtyStr = PosPub.getUnitConvert(dao, eId, pluNo, punit, detailUnit, pqty + "");

                                            BigDecimal mainPluBomUnitQty = new BigDecimal(mainPluBomUnitQtyStr);
                                            BigDecimal finalProdBaseQty = new BigDecimal(finalProdBaseQtyStr);
                                            BigDecimal rawMaterialBaseQty = new BigDecimal(rawMaterialBaseQtyStr);

                                            BigDecimal materialQty = new BigDecimal("0");
                                            //materialQty = mainPluBomUnitQty.multiply(finalProdBaseQty).multiply(rawMaterialBaseQty);
                                            materialQty = mainPluBomUnitQty.multiply(rawMaterialBaseQty).divide(finalProdBaseQty,6, RoundingMode.HALF_UP);

                                            // 数量 和 金额 保留位数， 应该根据单位长度保留
                                            // materialQty = materialQty.setScale(2, BigDecimal.ROUND_UP);
                                            // BigDecimal materialAmt = materialQty.multiply(new BigDecimal(materialPrice)).setScale(2, BigDecimal.ROUND_UP);
                                            BigDecimal materialAmt = materialQty.multiply(new BigDecimal(materialPrice)); // 零售价对应金额应该也要保留位数
                                            BigDecimal materialDistAmt = materialQty.multiply(new BigDecimal(materialDistriPrice)); // 进货价对应金额应该也要保留位数

                                            String materialWarehouse = warehouse; //原料仓库，取门店默认出货仓库

                                            String materialBaseQty = PosPub.getUnitConvert(dao, eId, materialPluNo, materialUnit, materialBaseUnit, materialQty + "");
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
                                                        matKeyVal = materialItem + "";
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
                                                        if (Check.Null(matKeyVal)) {
                                                            matKeyVal = "0";
                                                        }
                                                        break;
                                                    case 7:
                                                        matKeyVal = materialAmt + ""; //零售价对应金额
                                                        break;
                                                    case 8:
                                                        matKeyVal = finalProdBaseQtyStr; ///MATERIAL_FINALPRODBASEQTY
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
                                                        matKeyVal = materialDistriPrice; //原料进货价
                                                        if (Check.Null(matKeyVal))
                                                            matKeyVal = "0";
                                                        break;
                                                    case 21:
                                                        matKeyVal = materialDistAmt + "";
                                                        if (Check.Null(matKeyVal))
                                                            matKeyVal = "0";
                                                        break;
                                                    case 22:
                                                        matKeyVal = createDate;
                                                        break;
                                                    case 23:
                                                        matKeyVal = isBuckle; //是否扣料件
                                                        if (Check.Null(matKeyVal) || !matKeyVal.equals("N")) {
                                                            matKeyVal = "Y";
                                                        }
                                                        break;
                                                    case 24:
                                                        matKeyVal = " ";//原料特征码 featureNo
                                                        if (Check.Null(matKeyVal))
                                                            matKeyVal = " ";
                                                        break;
                                                    default:
                                                        break;
                                                }

                                                if (matKeyVal != null) {
                                                    insColCt2++;
                                                    if (j == 6 || j == 7) {
                                                        matColumnsVal[j] = new DataValue(matKeyVal, Types.FLOAT);
                                                    } else {
                                                        matColumnsVal[j] = new DataValue(matKeyVal, Types.VARCHAR);
                                                    }
                                                } else {
                                                    matColumnsVal[j] = null;
                                                }
                                            }
                                            String[] columns3 = new String[insColCt2];
                                            DataValue[] insValue3 = new DataValue[insColCt2];
                                            // 依照傳入參數組譯要insert的欄位與數值；
                                            insColCt2 = 0;

                                            for (int k = 0; k < matColumnsVal.length; k++) {
                                                if (matColumnsVal[k] != null) {
                                                    columns3[insColCt2] = matColumnsName[k];
                                                    insValue3[insColCt2] = matColumnsVal[k];
                                                    insColCt2++;
                                                    if (insColCt2 >= insValue3.length)
                                                        break;
                                                }
                                            }
                                            InsBean ib3 = new InsBean("DCP_PSTOCKIN_MATERIAL", columns3);
                                            ib3.addValues(insValue3);
                                            this.addProcessData(new DataProcessBean(ib3));

                                            //region 写原料库存流水
                                            String procedure2 = "SP_DCP_StockChange";
                                            Map<Integer, Object> inputParameter2 = new HashMap<Integer, Object>();
                                            inputParameter2.put(1, eId);                                      //--企业ID
                                            inputParameter2.put(2, shopId);                                   //--组织
                                            inputParameter2.put(3, "11");                             //--单据类型
                                            inputParameter2.put(4, pStockInNO);                                //--单据号
                                            inputParameter2.put(5, materialItem);                               //--单据行号
                                            inputParameter2.put(6, "-1");                                //--异动方向 1=加库存 -1=减库存
                                            inputParameter2.put(7, bDate);                                    //--营业日期 yyyy-MM-dd
                                            inputParameter2.put(8, materialPluNo);                                    //--品号
                                            inputParameter2.put(9, " ");                                //--特征码
                                            inputParameter2.put(10, warehouse);                           //--仓库
                                            inputParameter2.put(11, "");                                      //--批号
                                            inputParameter2.put(12, materialUnit);                                   //--交易单位
                                            inputParameter2.put(13, materialQty);                                //--交易数量
                                            inputParameter2.put(14, materialBaseUnit);                          //--基准单位
                                            inputParameter2.put(15, materialBaseQty);                     //--基准数量
                                            inputParameter2.put(16, materialUnitRatio);                         //--换算比例
                                            inputParameter2.put(17, materialPrice);                             //--零售价
                                            inputParameter2.put(18, materialAmt);                                     //--零售金额
                                            inputParameter2.put(19, materialDistriPrice);                       //--进货价
                                            inputParameter2.put(20, materialDistAmt);                         //--进货金额
                                            inputParameter2.put(21, createDate);                             //--入账日期 yyyy-MM-dd
                                            inputParameter2.put(22, "");                                      //--批号的生产日期 PROD_DATE yyyy-MM-dd
                                            inputParameter2.put(23, createDate);                                   //--单据日期
                                            inputParameter2.put(24, "");                                      //--异动原因
                                            inputParameter2.put(25, "订单转完工，原料出库");                  //--异动描述
                                            inputParameter2.put(26, createBy);                                //--操作员

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
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(pStockIn_Id, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
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
                                        new DataValue(billNo, Types.VARCHAR), //ofNo 来源单号
                                        new DataValue("", Types.VARCHAR), //PTEMPLATENO 模板编号
                                        new DataValue(userId, Types.VARCHAR), //ACCOUNTBY
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(createTime, Types.VARCHAR),
                                        new DataValue("3", Types.VARCHAR), // oType 来源类型
                                        new DataValue(warehouse, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(createTime, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(createTime, Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR), // docType 单据类型 0：完工入库  1：组合   2：拆解   3：转换合并  4：转换拆解
                                        new DataValue(distriAmt, Types.VARCHAR), //进货价合计
                                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                                        new DataValue(machineId, Types.VARCHAR),
                                        new DataValue(terminalType, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                };
                                InsBean ib1 = new InsBean("DCP_PSTOCKIN", columnsPstockin);
                                ib1.addValues(insValue1);
                                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                                // *************************************************** 生成完工入库  End*****************************************************
                                this.doExecuteDataToDB();

                                // *************************************************** 变更加工任务单  Begin*****************************************************
                                //更新单身 DCP_PROCESSTASK_DETAIL
                                UptBean ub = null;
                                ub = new UptBean("DCP_PROCESSTASK_DETAIL");
                                ub.addUpdateValue("GOODSSTATUS", new DataValue(updateStatus, Types.VARCHAR));
                                ub.addUpdateValue("OPNO", new DataValue(userId, Types.VARCHAR));
                                ub.addUpdateValue("MAKETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                                if (!Check.Null(oitem))
                                {
                                    ub.addCondition("OFNO", new DataValue(billNo, Types.VARCHAR));
                                    ub.addCondition("OITEM", new DataValue(oItem, Types.VARCHAR));
                                }
                                if (!Check.Null(item)) {
                                    ub.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                                }
                                if (!Check.Null(processtaskno))
                                {
                                    ub.addCondition("PROCESSTASKNO", new DataValue(processtaskno, Types.VARCHAR));
                                }
                                ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                                ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub));

                                // 如果 订单下所有商品 状态都为已完成 则修改order/生产 中的订单生产状态为 6
                                sqlbuf.setLength(0);
                                sqlbuf = this.getGoodsStatus(req, billNo);
                                List<Map<String, Object>> getBillNoDetail = this.doQueryData(sqlbuf.toString(), null);
                                if (CollectionUtils.isEmpty(getBillNoDetail)) {
                                    // 修改 生产订单状态为 6
                                    UptBean ub3 = null;
                                    ub3 = new UptBean("DCP_PROCESSTASK");
                                    ub3.addUpdateValue("PRODUCTSTATUS", new DataValue("6", Types.VARCHAR));
                                    ub3.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                                    ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    if (!Check.Null(oitem))
                                    {
                                        ub3.addCondition("OFNO", new DataValue(billNo, Types.VARCHAR));
                                    }
                                    ub3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                    if (!Check.Null(processtaskno))
                                    {
                                        ub3.addCondition("PROCESSTASKNO", new DataValue(processtaskno, Types.VARCHAR));
                                    }
                                    this.addProcessData(new DataProcessBean(ub3));

                                    if (otype.equals("ORDER"))
                                    {
                                        // 如果该单据下 所有商品都已制作完成 则更改生产状态为 6 完工入库
                                        UptBean ub2 = null;
                                        ub2 = new UptBean("DCP_ORDER");
                                        ub2.addUpdateValue("PRODUCTSTATUS", new DataValue("6", Types.VARCHAR));
                                        ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                        ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                        ub2.addCondition("ORDERNO", new DataValue(billNo, Types.VARCHAR));
                                        this.addProcessData(new DataProcessBean(ub2));
                                    }
                                }

                                //制作完成后，机器人状态改成空闲
                                UptBean ub_kdscookset=new UptBean("DCP_KDSCOOKSET");
                                ub_kdscookset.addUpdateValue("COOKSTATUS", new DataValue("0", Types.VARCHAR));
                                ub_kdscookset.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub_kdscookset.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                ub_kdscookset.addCondition("COOKID", new DataValue(cookId, Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(ub_kdscookset));


                                this.doExecuteDataToDB();
                                // *************************************************** 变更加工任务单  End*****************************************************

                                // *************************************************** 变更临时任务表  Begin*****************************************************
                                if (checkGoodsStatus(req, oItem, billNo, updateStatus)) {
                                    // 查询下临时表中的该类商品是否都制作完成
                                    UptBean ubPRODUCT_DETAIL = null;
                                    ubPRODUCT_DETAIL = new UptBean("DCP_PRODUCT_DETAIL");
                                    ubPRODUCT_DETAIL.addUpdateValue("GOODSSTATUS", new DataValue(updateStatus, Types.VARCHAR));
                                    ubPRODUCT_DETAIL.addUpdateValue("MAKETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                                    ubPRODUCT_DETAIL.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                                    ubPRODUCT_DETAIL.addCondition("OITEM", new DataValue(oItem, Types.VARCHAR));
                                    ubPRODUCT_DETAIL.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(ubPRODUCT_DETAIL));
                                    this.doExecuteDataToDB();
                                    if (checkGoodsStatus2(req, billNo, updateStatus)) {
                                        // 如果临时表都为统一状态则 变更临时表单头状态
                                        UptBean ub4 = null;
                                        ub4 = new UptBean("DCP_PRODUCT_SALE");
                                        ub4.addUpdateValue("PRODUCTSTATUS", new DataValue(updateStatus, Types.VARCHAR));
                                        ub4.addUpdateValue("MAKETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));

                                        ub4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                        ub4.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                                        ub4.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                        this.addProcessData(new DataProcessBean(ub4));
                                    }
                                }
                                this.doExecuteDataToDB();
                                // *************************************************** 变更临时任务表  End*****************************************************

                                if (Check.Null(billNo))
                                {
                                    //减
                                    bdm_Totqty=bdm_Totqty.subtract(new BigDecimal(pqty));
                                }

                                //累计实际的商品数量
                                bdm_TotRealQty=bdm_TotRealQty.add(new BigDecimal(pqty));

                            }


                            //向上取整后,多余的数量0.7要自动做预制菜完成动作
                            //剩余数量计算
                            BigDecimal bdm_TotRemainQty=new BigDecimal(qty).subtract(bdm_TotRealQty).setScale(2,BigDecimal.ROUND_HALF_UP);

                            //累加总的
                            v_TotRemainQty=v_TotRemainQty.add(bdm_TotRemainQty);

                            if (bdm_TotRemainQty.compareTo(BigDecimal.ZERO)>0)
                            {
                                if (v_BeforeProcessTaskNO.equals(""))
                                {
                                    v_BeforeProcessTaskNO=this.getProcessTaskNO(req);
                                }
                                v_beforeItem+=1;

                                DataValue[] insValueDetail = new DataValue[]
                                        {
                                                new DataValue(eId, Types.VARCHAR),
                                                new DataValue(shopId, Types.VARCHAR),
                                                new DataValue(shopId, Types.VARCHAR),
                                                new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                                new DataValue(v_beforeItem, Types.VARCHAR),
                                                new DataValue(0, Types.VARCHAR), // 倍量 默认0
                                                new DataValue(bdm_TotRemainQty, Types.VARCHAR), // 数量 以单份维度存储
                                                new DataValue(getPlunoDetails.get(0).get("PUNIT").toString(), Types.VARCHAR),
                                                new DataValue(getPlunoDetails.get(0).get("BASEQTY").toString(), Types.VARCHAR),
                                                new DataValue(pluNo, Types.VARCHAR),
                                                new DataValue(getPlunoDetails.get(0).get("PLUNAME").toString(), Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(getPlunoDetails.get(0).get("BASEUNIT").toString(), Types.VARCHAR),
                                                new DataValue(getPlunoDetails.get(0).get("UNIT_RATIO").toString(), Types.VARCHAR), // 单位转换率
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue("0", Types.VARCHAR),
                                                new DataValue(bDate.replace("-",""), Types.VARCHAR),
                                                new DataValue("", Types.VARCHAR),
                                                new DataValue("3", Types.VARCHAR), // goodsStatus 菜品状态
                                                new DataValue(getPlunoDetails.get(0).get("FINALCATEGORY").toString(), Types.VARCHAR), // 末级分类
                                                new DataValue(pluBarCode, Types.VARCHAR), // 条码
                                                new DataValue(bdm_TotRemainQty.toPlainString(), Types.VARCHAR) // 剩余可用数量
                                        };
                                InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
                                ib1.addValues(insValueDetail);
                                this.addProcessData(new DataProcessBean(ib1));



                                //完工入库处理，复制上面的
                                String pqty = getPlunoDetails.get(0).get("PQTY").toString();
                                String punit = getPlunoDetails.get(0).get("PUNIT").toString();
                                String baseUnit = getPlunoDetails.get(0).get("BASEUNIT").toString();
                                String baseQty = getPlunoDetails.get(0).get("BASEQTY").toString();
                                String unit_ratio = getPlunoDetails.get(0).get("UNIT_RATIO").toString();
                                String price = getPlunoDetails.get(0).get("PRICE").toString();
                                String amt = getPlunoDetails.get(0).get("AMT").toString();
                                String mul_qty = getPlunoDetails.get(0).get("MUL_QTY").toString();
                                String warehouse = getPlunoDetails.get(0).get("WAREHOUSE").toString();
                                if(Check.Null(warehouse)){
                                    warehouse = out_cost_warehouse;
                                }
                                String featureno = getPlunoDetails.get(0).get("FEATURENO").toString();
                                String distriPrice = getPlunoDetails.get(0).get("DISTRIPRICE").toString();
                                String distriAmt = getPlunoDetails.get(0).get("DISTRIAMT").toString();
                                String oItem = getPlunoDetails.get(0).get("OITEM").toString();
                                String item = getPlunoDetails.get(0).get("ITEM").toString();
                                String otype = getPlunoDetails.get(0).get("OTYPE").toString();

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
                                            keyVal = shopId;
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
                                            keyVal = bdm_TotRemainQty.toPlainString(); //pqty
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
                                            if (Check.Null(keyVal))
                                                keyVal = "0";
                                            break;
                                        case 11:
                                            keyVal = amt;    //amt
                                            break;
                                        case 12:
                                            keyVal = eId;
                                            break;
                                        case 13:
                                            keyVal = shopId;
                                            break;
                                        case 14:
                                            keyVal = "0";    //taskQty
                                            break;
                                        case 15:
                                            keyVal = "";    //scrapQty
                                            if (Check.Null(keyVal))
                                                keyVal = "0";
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
                                            keyVal = distriPrice;
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
                                                keyVal = " ";
                                            break;
                                        case 28:
                                            keyVal = billNo;
                                            break;
                                        default:
                                            break;
                                    }

                                    if (keyVal != null) {
                                        insColCt++;
                                        if (i == 2 || i == 3) {
                                            columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                        } else if (i == 6 || i == 8 || i == 9 || i == 10 || i == 11 || i == 14 || i == 15 || i == 16 || i == 24) {
                                            columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                                        } else {
                                            columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                                        }
                                    } else {
                                        columnsVal[i] = null;
                                    }
                                }

                                String[] columns2 = new String[insColCt];
                                DataValue[] insValue2 = new DataValue[insColCt];
                                // 依照傳入參數組譯要insert的欄位與數值；
                                insColCt = 0;
                                for (int i = 0; i < columnsVal.length; i++) {
                                    if (columnsVal[i] != null) {
                                        columns2[insColCt] = columnsPstockinDetail[i];
                                        insValue2[insColCt] = columnsVal[i];
                                        insColCt++;
                                        if (insColCt >= insValue2.length)
                                            break;
                                    }
                                }
                                InsBean ib2 = new InsBean("DCP_PSTOCKIN_DETAIL", columns2);
                                ib2.addValues(insValue2);
                                this.addProcessData(new DataProcessBean(ib2));

                                //region 写成品库存流水
                                String procedure = "SP_DCP_StockChange";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                      //--企业ID
                                inputParameter.put(2, shopId);                                   //--组织
                                inputParameter.put(3, "08");                                     //--单据类型
                                inputParameter.put(4, pStockInNO);                                //--单据号
                                inputParameter.put(5, 1);                                      //--单据行号 只有一种商品
                                inputParameter.put(6, "1");                                      //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(7, bDate);                                    //--营业日期 yyyy-MM-dd
                                inputParameter.put(8, pluNo);                                    //--品号
                                inputParameter.put(9, featureno);                                //--特征码
                                inputParameter.put(10, warehouse);                               //--仓库
                                inputParameter.put(11, "");                                      //--批号
                                inputParameter.put(12, punit);                                   //--交易单位
                                inputParameter.put(13, pqty);                                //--交易数量
                                inputParameter.put(14, baseUnit);                          //--基准单位
                                inputParameter.put(15, baseQty);                     //--基准数量
                                inputParameter.put(16, unit_ratio);                         //--换算比例
                                inputParameter.put(17, price);                             //--零售价
                                inputParameter.put(18, amt);                                     //--零售金额
                                inputParameter.put(19, distriPrice);                       //--进货价
                                inputParameter.put(20, distriAmt);                         //--进货金额
                                inputParameter.put(21, createDate);                             //--入账日期 yyyy-MM-dd
                                inputParameter.put(22, "");                                      //--批号的生产日期 PROD_DATE yyyy-MM-dd
                                inputParameter.put(23, bDate);                                   //--单据日期
                                inputParameter.put(24, "");                                      //--异动原因
                                inputParameter.put(25, "订单转完工，成品入库");                  //--异动描述
                                inputParameter.put(26, createBy);                                //--操作员

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
                                        + " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='" + shopId + "' "
                                        + " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno "
                                        + " and trunc(c.material_bdate)<=trunc(sysdate) and trunc(c.material_edate)>=trunc(sysdate) "
                                        //【ID1029991】【大拇指-3.0】完工入库红冲单中的单位和写入到库存流水表中的单位不一致  by jinzma 20221129
                                        //+ " inner join dcp_goods_unit d on a.eid=d.eid and a.pluno=d.pluno and a.unit=d.ounit and d.prod_unit_use='Y' "
                                        + " inner join dcp_goods_unit d on a.eid=d.eid and c.material_pluno=d.pluno and c.material_unit=d.ounit and d.prod_unit_use='Y'"

                                        + " INNER JOIN Dcp_Goods e ON C.eId = E.EID AND C.MATERIAL_Pluno = E.Pluno AND E.Status = '100' "
                                        + " where a.eId='" + eId + "' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and a.bomtype = '0'  "
                                        + " AND A.pluNo = '" + pluNo + "'"
                                        + " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))";

                                List<Map<String, Object>> getBomDatas = this.doQueryData(bomSql, null);
                                List<Map<String, Object>> pluList = new ArrayList<>();
                                if (!org.apache.cxf.common.util.CollectionUtils.isEmpty(getBomDatas))

                                    for (Map<String, Object> bomMap : getBomDatas) {
                                        String materialPluNo = bomMap.get("MATERIALPLUNO").toString();
                                        String materialUnit = bomMap.get("MATERIALUNIT").toString(); //原料用料单位

                                        if (!Check.Null(materialPluNo)) {
                                            Map<String, Object> pluMap = new HashMap<>();
                                            pluMap.put("PLUNO", materialPluNo);
                                            pluMap.put("PUNIT", materialUnit); //原料用料单位
                                            pluList.add(pluMap);
                                        }
                                    }
                                List<Map<String, Object>> getPluPrice = MC.getSalePrice_distriPrice(dao, eId, eId, shopId, pluList, eId);
                                String detailMulQty = "0";
                                int materialItem = 1;
                                /**
                                 *
                                 * 原料表  DCP_PstockIn_Material
                                 */
                                //region 插入原料表信息 以及原料库存流水 DCP_PstockIn_Material ，
                                for (Map<String, Object> bomMap : getBomDatas) {
                                    if (pluNo.equals(bomMap.get("PLUNO"))) {
                                        {

                                            detailMulQty = bomMap.get("MULQTY").toString(); //成品倍量， 完工入库明细表 DCP_PStockIn_Detail 上需要记录
                                            String detailUnit = bomMap.get("UNIT").toString(); // 成品BOM用料单位， 用于计算成品录入单位到BOM用料单位的 数量
                                            String materialPluNo = bomMap.get("MATERIALPLUNO").toString();
                                            String materialUnit = bomMap.get("MATERIALUNIT").toString(); //原料BOM用料单位
                                            String materialBaseUnit = bomMap.get("BASEUNIT").toString(); //原料基础单位
                                            String materialUnitRatio = bomMap.get("UNITRATIO").toString(); //原料单位换算率
                                            String isBuckle = bomMap.get("ISBUCKLE").toString(); // 是否扣料件

                                            Map<String, Object> condiV = new HashMap<String, Object>();
                                            condiV.put("PLUNO", materialPluNo);
                                            condiV.put("PUNIT", materialUnit); //原料用料单位
                                            List<Map<String, Object>> priceList = MapDistinct.getWhereMap(getPluPrice, condiV, false);

                                            String materialPrice = "0";
                                            String materialDistriPrice = "0";
                                            if (priceList != null && priceList.size() > 0) {
                                                materialPrice = priceList.get(0).get("PRICE").toString(); //原料用料单位materialUnit对应零售价
                                                materialDistriPrice = priceList.get(0).get("DISTRIPRICE").toString();//原料单位进货价
                                            }

                                            // 开始计算原料对应数量
                                            String mainPLuNoUnit = bomMap.get("UNIT").toString();

                                            //mainPluNoBaseQty 表示订单商品单位 对应 成品单位用量

                                            String finalProdBaseQtyStr = bomMap.get("QTY").toString(); //成品基础用量
                                            String rawMaterialBaseQtyStr = bomMap.get("MATERIALQTY").toString();  //原料基础用量

                                            // 成品BOM用料单位对应数量
                                            String mainPluBomUnitQtyStr = PosPub.getUnitConvert(dao, eId, pluNo, punit, detailUnit, pqty + "");

                                            BigDecimal mainPluBomUnitQty = new BigDecimal(mainPluBomUnitQtyStr);
                                            BigDecimal finalProdBaseQty = new BigDecimal(finalProdBaseQtyStr);
                                            BigDecimal rawMaterialBaseQty = new BigDecimal(rawMaterialBaseQtyStr);

                                            BigDecimal materialQty = new BigDecimal("0");
                                            //materialQty = mainPluBomUnitQty.multiply(finalProdBaseQty).multiply(rawMaterialBaseQty);
                                            materialQty = mainPluBomUnitQty.multiply(rawMaterialBaseQty).divide(finalProdBaseQty,6, RoundingMode.HALF_UP);

                                            // 数量 和 金额 保留位数， 应该根据单位长度保留
                                            // materialQty = materialQty.setScale(2, BigDecimal.ROUND_UP);
                                            // BigDecimal materialAmt = materialQty.multiply(new BigDecimal(materialPrice)).setScale(2, BigDecimal.ROUND_UP);
                                            BigDecimal materialAmt = materialQty.multiply(new BigDecimal(materialPrice)); // 零售价对应金额应该也要保留位数
                                            BigDecimal materialDistAmt = materialQty.multiply(new BigDecimal(materialDistriPrice)); // 进货价对应金额应该也要保留位数

                                            String materialWarehouse = warehouse; //原料仓库，取门店默认出货仓库

                                            String materialBaseQty = PosPub.getUnitConvert(dao, eId, materialPluNo, materialUnit, materialBaseUnit, materialQty + "");
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
                                                        matKeyVal = materialItem + "";
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
                                                        if (Check.Null(matKeyVal)) {
                                                            matKeyVal = "0";
                                                        }
                                                        break;
                                                    case 7:
                                                        matKeyVal = materialAmt + ""; //零售价对应金额
                                                        break;
                                                    case 8:
                                                        matKeyVal = finalProdBaseQtyStr; ///MATERIAL_FINALPRODBASEQTY
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
                                                        matKeyVal = materialDistriPrice; //原料进货价
                                                        if (Check.Null(matKeyVal))
                                                            matKeyVal = "0";
                                                        break;
                                                    case 21:
                                                        matKeyVal = materialDistAmt + "";
                                                        if (Check.Null(matKeyVal))
                                                            matKeyVal = "0";
                                                        break;
                                                    case 22:
                                                        matKeyVal = createDate;
                                                        break;
                                                    case 23:
                                                        matKeyVal = isBuckle; //是否扣料件
                                                        if (Check.Null(matKeyVal) || !matKeyVal.equals("N")) {
                                                            matKeyVal = "Y";
                                                        }
                                                        break;
                                                    case 24:
                                                        matKeyVal = " ";//原料特征码 featureNo
                                                        if (Check.Null(matKeyVal))
                                                            matKeyVal = " ";
                                                        break;
                                                    default:
                                                        break;
                                                }

                                                if (matKeyVal != null) {
                                                    insColCt2++;
                                                    if (j == 6 || j == 7) {
                                                        matColumnsVal[j] = new DataValue(matKeyVal, Types.FLOAT);
                                                    } else {
                                                        matColumnsVal[j] = new DataValue(matKeyVal, Types.VARCHAR);
                                                    }
                                                } else {
                                                    matColumnsVal[j] = null;
                                                }
                                            }
                                            String[] columns3 = new String[insColCt2];
                                            DataValue[] insValue3 = new DataValue[insColCt2];
                                            // 依照傳入參數組譯要insert的欄位與數值；
                                            insColCt2 = 0;

                                            for (int k = 0; k < matColumnsVal.length; k++) {
                                                if (matColumnsVal[k] != null) {
                                                    columns3[insColCt2] = matColumnsName[k];
                                                    insValue3[insColCt2] = matColumnsVal[k];
                                                    insColCt2++;
                                                    if (insColCt2 >= insValue3.length)
                                                        break;
                                                }
                                            }
                                            InsBean ib3 = new InsBean("DCP_PSTOCKIN_MATERIAL", columns3);
                                            ib3.addValues(insValue3);
                                            this.addProcessData(new DataProcessBean(ib3));

                                            //region 写原料库存流水
                                            String procedure2 = "SP_DCP_StockChange";
                                            Map<Integer, Object> inputParameter2 = new HashMap<Integer, Object>();
                                            inputParameter2.put(1, eId);                                      //--企业ID
                                            inputParameter2.put(2, shopId);                                   //--组织
                                            inputParameter2.put(3, "11");                             //--单据类型
                                            inputParameter2.put(4, pStockInNO);                                //--单据号
                                            inputParameter2.put(5, materialItem);                               //--单据行号
                                            inputParameter2.put(6, "-1");                                //--异动方向 1=加库存 -1=减库存
                                            inputParameter2.put(7, bDate);                                    //--营业日期 yyyy-MM-dd
                                            inputParameter2.put(8, materialPluNo);                                    //--品号
                                            inputParameter2.put(9, " ");                                //--特征码
                                            inputParameter2.put(10, warehouse);                           //--仓库
                                            inputParameter2.put(11, "");                                      //--批号
                                            inputParameter2.put(12, materialUnit);                                   //--交易单位
                                            inputParameter2.put(13, materialQty);                                //--交易数量
                                            inputParameter2.put(14, materialBaseUnit);                          //--基准单位
                                            inputParameter2.put(15, materialBaseQty);                     //--基准数量
                                            inputParameter2.put(16, materialUnitRatio);                         //--换算比例
                                            inputParameter2.put(17, materialPrice);                             //--零售价
                                            inputParameter2.put(18, materialAmt);                                     //--零售金额
                                            inputParameter2.put(19, materialDistriPrice);                       //--进货价
                                            inputParameter2.put(20, materialDistAmt);                         //--进货金额
                                            inputParameter2.put(21, createDate);                             //--入账日期 yyyy-MM-dd
                                            inputParameter2.put(22, "");                                      //--批号的生产日期 PROD_DATE yyyy-MM-dd
                                            inputParameter2.put(23, createDate);                                   //--单据日期
                                            inputParameter2.put(24, "");                                      //--异动原因
                                            inputParameter2.put(25, "订单转完工，原料出库");                  //--异动描述
                                            inputParameter2.put(26, createBy);                                //--操作员

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
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(pStockIn_Id, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(createTime, Types.VARCHAR),
                                        new DataValue(bdm_TotRemainQty, Types.VARCHAR),
                                        new DataValue(amt, Types.VARCHAR),
                                        new DataValue("1", Types.VARCHAR),
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(pStockInNO, Types.VARCHAR),

                                        new DataValue("订单转完工入库-" +v_BeforeProcessTaskNO, Types.VARCHAR),//memo 备注
                                        new DataValue("2", Types.VARCHAR),//status 完工入库单状态
                                        new DataValue("N", Types.VARCHAR), //process_status 上传状态
                                        new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR), //ofNo 来源单号
                                        new DataValue("", Types.VARCHAR), //PTEMPLATENO 模板编号
                                        new DataValue(userId, Types.VARCHAR), //ACCOUNTBY
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(createTime, Types.VARCHAR),
                                        new DataValue("3", Types.VARCHAR), // oType 来源类型
                                        new DataValue(warehouse, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(createTime, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(createTime, Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR), // docType 单据类型 0：完工入库  1：组合   2：拆解   3：转换合并  4：转换拆解
                                        new DataValue(distriAmt, Types.VARCHAR), //进货价合计
                                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                                        new DataValue(machineId, Types.VARCHAR),
                                        new DataValue(terminalType, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                };
                                InsBean ib1_PSTOCKIN = new InsBean("DCP_PSTOCKIN", columnsPstockin);
                                ib1_PSTOCKIN.addValues(insValue1);
                                this.addProcessData(new DataProcessBean(ib1_PSTOCKIN)); // 新增單頭

                            }

                        }

                    }

                    //说明有明细，再添加单头
                    if (v_beforeItem >0)
                    {
                        DataValue[] insValue = new DataValue[]
                                {
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(v_BeforeProcessTaskNO, Types.VARCHAR),
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(createTime, Types.VARCHAR),
                                        new DataValue(createDate, Types.VARCHAR),
                                        new DataValue(userId, Types.VARCHAR),
                                        new DataValue("6", Types.VARCHAR), // status 默认6
                                        new DataValue(v_beforeItem, Types.VARCHAR),
                                        new DataValue("N", Types.VARCHAR),
                                        new DataValue(bDate.replace("-",""), Types.VARCHAR),
                                        new DataValue(v_TotRemainQty, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR),
                                        new DataValue(out_cost_warehouse, Types.VARCHAR),
                                        new DataValue(out_cost_warehouse, Types.VARCHAR), // MATERIALWAREHOUSE 原料仓库 取默认出货仓库
                                        new DataValue("BEFORE", Types.VARCHAR), // 单据类型 此处为预制单 Before
                                        new DataValue(createDate+createTime, Types.VARCHAR), // 生产日期
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                };

                        InsBean ib2 = new InsBean("DCP_PROCESSTASK", columns_Processtask);
                        ib2.addValues(insValue);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                    this.doExecuteDataToDB();


                }
                else
                {
                    // 变更1就记为配餐完成时间；2是制作完成时间；3是传菜取餐时间
                    for (DCP_DishStatusUpdate_OpenReq.level2Elm lv2 : goodsList)
                    {
                        //加工任务单号
                        String processTaskNo = lv2.getProcessTaskNo();
                        //来源单号，如果是预制菜就是空了
                        String billNo = lv2.getBillNo();
                        String pluNo = lv2.getPluNo();
                        String oitem = lv2.getOItem();
                        String[] itemList = lv2.getItemList();
                        String pluBarCode = lv2.getPluBarCode();
                        String qty = lv2.getQty();

                        List<Map<String, Object>> getPlunoDetails = null;
                        sqlbuf.setLength(0);
                        if (Check.Null(oitem))
                        {
                            if (Check.Null(billNo))
                            {
                                // 查询出 加工任务中的商品信息 DCP_PROCESSTASK_DETAIL
                                String itemSQLIn = PosPub.getArrayStrSQLIn(itemList);
                                sqlbuf.append("SELECT * FROM DCP_PROCESSTASK_DETAIL a " +
                                                      " left join DCP_PROCESSTASK b  ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO " +
                                                      "WHERE a.EID = '" + eId + "' AND a.SHOPID = '" + shopId + "' and a.PROCESSTASKNO='"+processTaskNo+"' and nvl(a.ofno,'@#$')='@#$' and a.item IN (" + itemSQLIn + ") and a.pluno = '" + pluNo + "' ");

                            }
                            else
                            {
                                // 查询出 加工任务中的商品信息 DCP_PROCESSTASK_DETAIL
                                String itemSQLIn = PosPub.getArrayStrSQLIn(itemList);
                                sqlbuf.append("SELECT * FROM DCP_PROCESSTASK_DETAIL a " +
                                                      " left join DCP_PROCESSTASK b  ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO " +
                                                      "WHERE a.EID = '" + eId + "' AND a.SHOPID = '" + shopId + "' and a.PROCESSTASKNO='"+processTaskNo+"' and a.ofno='"+billNo+"' and a.item IN (" + itemSQLIn + ") and a.pluno = '" + pluNo + "' ");

                            }
                            getPlunoDetails = this.doQueryData(sqlbuf.toString(), null);
                        }
                        else if (!Check.Null(oitem))
                        {
                            // 查询来源项次 根据来源项次查询出对应的商品
                            sqlbuf.append("SELECT * FROM DCP_PROCESSTASK_DETAIL a" +
                                                  " left join DCP_PROCESSTASK b  ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.PROCESSTASKNO = b.PROCESSTASKNO AND a.ORGANIZATIONNO = b.ORGANIZATIONNO " +
                                                  "WHERE a.EID = '" + eId + "' AND a.SHOPID = '" + shopId + "' AND a.OFNO = '" + billNo + "' and a.OITEM = '" + oitem + "' and a.pluno = '" + pluNo + "' ");
                            getPlunoDetails = this.doQueryData(sqlbuf.toString(), null);
                        }
                        for (Map<String, Object> getPlunoDetail : getPlunoDetails)
                        {
                            String oItem = getPlunoDetail.get("OITEM").toString();
                            String item = getPlunoDetail.get("ITEM").toString();
                            String otype = getPlunoDetail.get("OTYPE").toString();

                            // *************************************************** 变更加工任务单  Begin*****************************************************
                            //更新单身 DCP_PROCESSTASK_DETAIL
                            UptBean ub = null;
                            ub = new UptBean("DCP_PROCESSTASK_DETAIL");
                            ub.addUpdateValue("GOODSSTATUS", new DataValue(updateStatus, Types.VARCHAR));
                            ub.addUpdateValue("OPNO", new DataValue(userId, Types.VARCHAR));
                            if (!Check.Null(oitem))
                            {
                                ub.addCondition("OITEM", new DataValue(oItem, Types.VARCHAR));
                            }

                            if (!Check.Null(billNo))
                            {
                                ub.addCondition("OFNO", new DataValue(billNo, Types.VARCHAR));
                            }
                            if (updateStatus.equals("1")) {
                                ub.addUpdateValue("ASSORTEDTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                            } else if (updateStatus.equals("3")) {
                                ub.addUpdateValue("COMPLETETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                            }
                            ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                            ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub.addCondition("ITEM", new DataValue(item, Types.VARCHAR));

                            if (!Check.Null(processTaskNo))
                            {
                                ub.addCondition("PROCESSTASKNO", new DataValue(processTaskNo, Types.VARCHAR));
                            }

                            this.addProcessData(new DataProcessBean(ub));

                            if (goodsStatus.equals("2") && updateStatus.equals("1") && otype.equals("ORDER"))
                            {
                                String sql = "select * from DCP_PROCESSTASK where OFNO = '" + billNo + "' ";
                                List<Map<String, Object>> getProdStatus = this.doQueryData(sql, null);
                                String productstatus = getProdStatus.get(0).get("PRODUCTSTATUS").toString();
                                if (productstatus.equals("6"))
                                {
                                    UptBean ub2 = null;
                                    ub2 = new UptBean("DCP_ORDER");
                                    ub2.addUpdateValue("PRODUCTSTATUS", new DataValue("4", Types.VARCHAR));
                                    ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                    ub2.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
                                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    ub2.addCondition("ORDERNO", new DataValue(billNo, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(ub2));

                                    // 修改 生产订单状态为 4
                                    UptBean ub3 = null;
                                    ub3 = new UptBean("DCP_PROCESSTASK");
                                    ub3.addUpdateValue("PRODUCTSTATUS", new DataValue("4", Types.VARCHAR));
                                    ub3.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));

                                    if (!Check.Null(oitem))
                                    {
                                        ub3.addCondition("OFNO", new DataValue(billNo, Types.VARCHAR));
                                    }
                                    if (!Check.Null(billNo))
                                    {
                                        ub3.addCondition("OFNO", new DataValue(billNo, Types.VARCHAR));
                                    }
                                    ub3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                    ub3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    if (!Check.Null(processTaskNo))
                                    {
                                        ub3.addCondition("PROCESSTASKNO", new DataValue(processTaskNo, Types.VARCHAR));
                                    }

                                    this.addProcessData(new DataProcessBean(ub3));
                                }
                            }
                            // *************************************************** 变更加工任务单  End*****************************************************
                            this.doExecuteDataToDB();


                            // *************************************************** 变更临时任务表  Begin*****************************************************
                            //查DCP_PROCESSTASK_DETAIL表商品状态
                            if (checkGoodsStatus(req, oItem, billNo, updateStatus))
                            {
                                // 查询下临时表中的该类商品是否都制作完成
                                UptBean ubPRODUCT_DETAIL = null;
                                ubPRODUCT_DETAIL = new UptBean("DCP_PRODUCT_DETAIL");
                                ubPRODUCT_DETAIL.addUpdateValue("GOODSSTATUS", new DataValue(updateStatus, Types.VARCHAR));
                                if (updateStatus.equals("1"))
                                {
                                    ubPRODUCT_DETAIL.addUpdateValue("ASSORTEDTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                                }
                                else if (updateStatus.equals("3"))
                                {
                                    ubPRODUCT_DETAIL.addUpdateValue("COMPLETETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                                }
                                ubPRODUCT_DETAIL.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                                ubPRODUCT_DETAIL.addCondition("OITEM", new DataValue(oItem, Types.VARCHAR));
                                ubPRODUCT_DETAIL.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                ubPRODUCT_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));

                                this.addProcessData(new DataProcessBean(ubPRODUCT_DETAIL));
                                this.doExecuteDataToDB();

                                //查DCP_PRODUCT_DETAIL表商品状态
                                if (checkGoodsStatus2(req, billNo, updateStatus))
                                {
                                    // 如果临时表都为统一状态则 变更临时表单头状态
                                    UptBean ub4 = null;
                                    ub4 = new UptBean("DCP_PRODUCT_SALE");
                                    ub4.addUpdateValue("PRODUCTSTATUS", new DataValue(updateStatus, Types.VARCHAR));
                                    if (updateStatus.equals("1"))
                                    {
                                        ub4.addUpdateValue("ASSORTEDTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                                    }
                                    else if (updateStatus.equals("3"))
                                    {
                                        ub4.addUpdateValue("PICKUPTIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), Types.VARCHAR));
                                    }
                                    ub4.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                                    ub4.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                    ub4.addCondition("EID", new DataValue(eId, Types.VARCHAR));

                                    this.addProcessData(new DataProcessBean(ub4));
                                }
                            }
                            this.doExecuteDataToDB();
                            // *************************************************** 变更临时任务表  End*****************************************************


                        }



                    }
                }

            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        catch (Exception e)
        {
            StringBuffer sb_errors=new StringBuffer();

            try
            {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                String[] p_error=errors.toString().split("\n");
                for (String sp : p_error)
                {
                    if (sp.contains("com.dsc") || sp.contains("Caused by"))
                    {
                        sb_errors.append(sp+"\n");
                    }
                }

                pw=null;
                errors=null;
            }
            catch (IOException e1)
            {
                sb_errors.append(e1.getMessage());
            }

            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败:"+sb_errors.toString());
            sb_errors.setLength(0);
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_DishStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_DishStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_DishStatusUpdate_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_DishStatusUpdate_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_DishStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }
        if (Check.Null(request.getTerminalType())) {
            errMsg.append("机台类型不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getUserId())) {
            errMsg.append("用户编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getGoodsStatus())) {
            errMsg.append("制作状态不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getUpdateStatus())) {
            errMsg.append("变更状态不能为空,");
            isFail = true;
        }

        if (CollectionUtils.isEmpty(request.getGoodsList())) {
            errMsg.append("商品列表不能为空");
            isFail = true;
        }
        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_DishStatusUpdate_OpenReq> getRequestType() {
        return new TypeToken<DCP_DishStatusUpdate_OpenReq>() {
        };
    }

    @Override
    protected DCP_DishStatusUpdate_OpenRes getResponseType() {
        return new DCP_DishStatusUpdate_OpenRes();
    }

    protected String getOrderDetails(DCP_DishStatusUpdate_OpenReq req) {
        return "";
    }

    private String getPStockInNO(DCP_DishStatusUpdate_OpenReq req) throws Exception {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String pStockInNO = null;
        String shopId = req.getRequest().getShopId();
        String eId = req.geteId();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        StringBuffer sqlbuf = new StringBuffer("");
        String docType = "0"; //0-完工入库  1-组合单   2-拆解单  3-转换合并  4-转换拆解
        //新增服务时  docType是1的时候 单号开头字母换成ZHRK；2的时候换成CJCK
        if (docType.equals("0")) {
            pStockInNO = "WGRK" + bDate;
        } else if (docType.equals("1")) {
            pStockInNO = "ZHRK" + bDate;
        } else if (docType.equals("2")) {
            pStockInNO = "CJCK" + bDate;
        }
        // 2019-05-29 若docType==3， 转换合并单，
        else if (docType.equals("3")) {
            pStockInNO = "ZHHB" + bDate;
        }
        // 2019-08-19 若docType==4， 转换拆解单据，
        else if (docType.equals("4")) {
            pStockInNO = "ZHCJ" + bDate;
        }
        sqlbuf.append("select PSTOCKINNO  from (select max(PSTOCKINNO) as PSTOCKINNO "
                              + " from DCP_PSTOCKIN where EID = '" + eId + "' and SHOPID = '" + shopId + "' "
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

                if (docType.equals("0")) {
                    pStockInNO = "WGRK" + pStockInNO; //完工入库0
                } else if (docType.equals("1")) {
                    pStockInNO = "ZHRK" + pStockInNO; //组合入库1
                } else if (docType.equals("2")) {
                    pStockInNO = "CJCK" + pStockInNO; //拆解出库2
                } else if (docType.equals("3")) {
                    pStockInNO = "ZHHB" + pStockInNO; //转换合并3
                } else if (docType.equals("4")) {
                    pStockInNO = "ZHCJ" + pStockInNO;
                }
            } else {
                if (docType.equals("0")) {
                    pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
                } else if (docType.equals("1")) {
                    pStockInNO = "ZHRK" + bDate + "00001"; //组合入库1
                } else if (docType.equals("2")) {
                    pStockInNO = "CJCK" + bDate + "00001"; //拆解出库2
                } else if (docType.equals("3")) {
                    pStockInNO = "ZHHB" + bDate + "00001"; //拆解出库2
                }
                // 2019-08-19 若docType==4， 转换拆解单据，
                else if (docType.equals("4")) {
                    pStockInNO = "ZHCJ" + bDate + "00001";
                }
            }
        } else {
            if (docType.equals("0")) {
                pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
            } else if (docType.equals("1")) {
                pStockInNO = "ZHRK" + bDate + "00001"; //组合入库1
            } else if (docType.equals("2")) {
                pStockInNO = "CJCK" + bDate + "00001"; //拆解出库2
            } else if (docType.equals("3")) {
                pStockInNO = "ZHHB" + bDate + "00001"; //转换合并3
            }
            // 2019-08-19 若docType==4， 转换拆解单据，
            else if (docType.equals("4")) {
                pStockInNO = "ZHCJ" + bDate + "00001"; //转换拆解4
            }
        }

        return pStockInNO;
    }

    /**
     * 查询 订单下商品是否制作完成
     *
     * @param req
     * @return
     */
    private StringBuffer getGoodsStatus(DCP_DishStatusUpdate_OpenReq req, String billNo) {
        DCP_DishStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        StringBuffer sqlbuf = new StringBuffer("");
        sqlbuf.append(" SELECT * FROM DCP_PROCESSTASK_DETAIL a " +
                              " WHERE a.EID = '" + req.geteId() + "' AND a.SHOPID = '" + request.getShopId() + "' AND a.OFNO = '" + billNo + "' AND a.GOODSSTATUS !='2'");
        return sqlbuf;
    }

    /**
     * 查询下该类商品是否都制作完成 制作完成则修改临时表状态
     *
     * @param req
     * @param oItem
     * @return
     */
    private boolean checkGoodsStatus(DCP_DishStatusUpdate_OpenReq req, String oItem, String billNo, String goodsStatus) throws Exception
    {
        StringBuffer sqlbuf = new StringBuffer("");
        int i = Integer.parseInt(goodsStatus);
        boolean flag = true;
        DCP_DishStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        sqlbuf.append("SELECT * FROM DCP_PROCESSTASK_DETAIL WHERE EID = '" + req.geteId() + "' AND SHOPID = '" + request.getShopId() + "' AND OFNO = '" + billNo + "' " +
                              " AND oitem = '" + oItem + "'");

        List<Map<String, Object>> getPlunoDetail = this.doQueryData(sqlbuf.toString(), null);
        if (!CollectionUtils.isEmpty(getPlunoDetail))
        {
            for (Map<String, Object> map : getPlunoDetail)
            {
                String goodsStatusStr = map.get("GOODSSTATUS").toString();
                int goodsStatusInt = Integer.parseInt(goodsStatusStr);
                if (goodsStatusInt < i)
                {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     * 判断临时表中的商品 是否都为同一状态 如都为同一状态 则变更临时表单头
     *
     * @param req
     * @param billNo
     * @param goodsStatus
     * @return
     */
    private boolean checkGoodsStatus2(DCP_DishStatusUpdate_OpenReq req, String billNo, String goodsStatus) throws Exception
    {
        boolean flag = true;
        StringBuffer sqlbuf = new StringBuffer("");
        int i = Integer.parseInt(goodsStatus);
        DCP_DishStatusUpdate_OpenReq.level1Elm request = req.getRequest();
        sqlbuf.append("SELECT * FROM DCP_PRODUCT_DETAIL WHERE eid = '" + req.geteId() + "' AND SHOPID = '" + request.getShopId() + "' AND BILLNO = '" + billNo + "' ");
        List<Map<String, Object>> datas = this.doQueryData(sqlbuf.toString(), null);
        if (!CollectionUtils.isEmpty(datas)) {
            for (Map<String, Object> data : datas) {
                String goodsstatus = data.get("GOODSSTATUS").toString();
                if (Integer.parseInt(goodsstatus) < i) {
                    flag = false;
                }
            }
        }
        return flag;
    }


    private String getProcessTaskNO(DCP_DishStatusUpdate_OpenReq req) throws Exception  {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String sql = null;
        String processTaskNO = null;
        String shopId = req.getRequest().getShopId();
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','JGRW') PROCESSTASKNO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            processTaskNO = (String) getQData.get(0).get("PROCESSTASKNO");
        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return processTaskNO;
    }


}
