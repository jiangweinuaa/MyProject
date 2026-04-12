package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PStockOutCreateReq;
import com.dsc.spos.json.cust.res.DCP_PStockOutCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.dsc.spos.dao.DataValue.DataExpression.UpdateSelf;

public class DCP_PStockOutCreate extends SPosAdvanceService<DCP_PStockOutCreateReq, DCP_PStockOutCreateRes> {

    @Override
    protected void processDUID(DCP_PStockOutCreateReq req, DCP_PStockOutCreateRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String memo = req.getRequest().getMemo();
        String status = req.getRequest().getStatus();
        String process_Status = "N";
        String pTemplateNO = req.getRequest().getPTemplateNo();
        String bDate = req.getRequest().getBDate();
        String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
        String warehouse = req.getRequest().getWarehouse();
        //2018-08-07 添加DOC_TYPE
        String doc_Type = req.getRequest().getDocType();
        String opNo = req.getOpNO();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());
        ///完工入库，单据日期新增如果前端没有给值，后端取系统日期  BY JZMA 20200427
        if (Check.Null(bDate)) {
            bDate = sDate;
        }
        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

        String pStockInID = req.getRequest().getPStockInID();
        String ofNO = req.getRequest().getOfNo();
        String oType = req.getRequest().getOType();
        String completeDate = sDate;
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totCqty = req.getRequest().getTotCqty();
        String totDistriAmt = req.getRequest().getTotDistriAmt();

        String isMaterialReplace=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "MaterialReplace");
        String isBatchNo=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");
        if(isMaterialReplace==null||isMaterialReplace.isEmpty())
        {
            isMaterialReplace="N";
        }

        if (!checkGuid(req)) {
            String pStockInNO = this.getOrderNO(req,"ZHCJ");
            String[] columns1 = {
                    "SHOPID", "ORGANIZATIONNO","BDATE","PSTOCKIN_ID","CREATEBY", "CREATE_DATE", "CREATE_TIME","TOT_PQTY",
                    "TOT_AMT", "TOT_CQTY", "EID","PSTOCKINNO", "MEMO", "STATUS", "PROCESS_STATUS","OFNO","PTEMPLATENO",
                    "ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","OTYPE","WAREHOUSE","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                    "SUBMITBY","SUBMIT_DATE","SUBMIT_TIME","DOC_TYPE","TOT_DISTRIAMT","CREATE_CHATUSERID",
                    "UPDATE_TIME","TRAN_TIME","PROCESSPLANNO","TASK0NO","DTNO","EMPLOYEEID","DEPARTID","CREATEDEPTID"};

            List<DCP_PStockOutCreateReq.level1Elm> datas = req.getRequest().getDatas();

            int mItem = 1;//原料项次自增长，不取前端

            List<String> detailWarehouseList = new ArrayList();
            List<String> detailPluNoList=new ArrayList<>();
            //新增单身（多笔）
            for (DCP_PStockOutCreateReq.level1Elm par : datas) {
                //【ID1023852】【詹记】门店分批出数作业调整
                String pluNo = par.getPluNo();
                String featureNo = par.getFeatureNo();
                if (Check.Null(featureNo)) {
                    featureNo = " ";
                }

                if(Check.Null(par.getBatchNo())){
                    String batchNo = PosPub.getBatchNo(dao, eId, shopId, "", par.getPluNo(), par.getFeatureNo(), par.getProdDate(),"", req.getOpNO(), req.getOpName(), "");
                    par.setBatchNo(batchNo);
                }

                BigDecimal scrap_b = new BigDecimal("0");
                BigDecimal pqty_b = new BigDecimal("0");

                int insColCt = 0;
                String[] columnsName = {
                        "PSTOCKINNO","SHOPID","ITEM","OITEM","PLUNO","PUNIT","PQTY","BASEUNIT","BASEQTY","UNIT_RATIO",
                        "PRICE","AMT","EID","ORGANIZATIONNO","TASK_QTY","SCRAP_QTY","MUL_QTY","BSNO",
                        "MEMO","GDATE","GTIME","WAREHOUSE",
                        "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE","FEATURENO","LOCATION","EXPDATE","PRODTYPE","BOMNO","VERSIONNUM"
                };
                DataValue[] columnsVal = new DataValue[columnsName.length];
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
                            keyVal = par.getItem(); //item
                            break;
                        case 3:
                            keyVal = par.getOItem();//oItem
                            break;
                        case 4:
                            keyVal = pluNo; //pluNO
                            break;
                        case 5:
                            keyVal = par.getPunit(); //punit
                            break;
                        case 6:
                            keyVal = par.getPqty(); //pqty
                            pqty_b = new BigDecimal(keyVal);
                            break;
                        case 7:
                            keyVal = par.getBaseUnit();     //wunit
                            break;
                        case 8:
                            keyVal = par.getBaseQty();   //wqty
                            break;
                        case 9:
                            keyVal = par.getUnitRatio();     //unitRatio
                            break;
                        case 10:
                            keyVal = par.getPrice();    //price
                            if(Check.Null(keyVal))
                                keyVal = "0";
                            break;
                        case 11:
                            keyVal = par.getAmt();    //amt
                            break;
                        case 12:
                            keyVal = eId;
                            break;
                        case 13:
                            keyVal = organizationNO;
                            break;
                        case 14:
                            keyVal = par.getTaskQty();	//taskQty
                            break;
                        case 15:
                            keyVal = par.getScrapQty();	//scrapQty
                            if (Check.Null(keyVal)) {
                                keyVal = "0";
                            }
                            scrap_b = new BigDecimal(keyVal);
                            break;
                        case 16:
                            keyVal = par.getMulQty();
                            break;
                        case 17:
                            keyVal = par.getBsNo();
                            break;
                        case 18:
                            keyVal = par.getMemo();
                            break;
                        case 19:
                            keyVal = par.getGDate();
                            break;
                        case 20:
                            keyVal = par.getGTime();
                            break;
                        case 21:
                            keyVal = warehouse;
                            break;
                        case 22:
                            keyVal = par.getBatchNo();
                            break;
                        case 23:
                            keyVal = par.getProdDate();
                            break;
                        case 24:
                            keyVal=par.getDistriPrice();
                            if(Check.Null(keyVal))
                                keyVal = "0";
                            break;
                        case 25:
                            keyVal = par.getDistriAmt();
                            if (Check.Null(keyVal))
                                keyVal="0";
                            break;
                        case 26:
                            keyVal = accountDate;
                            break;
                        case 27:
                            keyVal = featureNo ;
                            break;

                        case 28:
                            keyVal = par.getLocation() ;
                            if(Check.Null(keyVal)){
                                keyVal = " ";
                            }
                            break;

                        case 29:
                            keyVal = par.getExpDate() ;
                            break;

                        case 30:
                            keyVal = par.getProdType() ;
                            break;
                        case 31:
                            keyVal = par.getBomNo() ;
                            break;
                        case 32:
                            keyVal = par.getVersionNum() ;
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
                        columns2[insColCt] = columnsName[i];
                        insValue2[insColCt] = columnsVal[i];
                        insColCt ++;
                        if (insColCt >= insValue2.length)
                            break;
                    }
                }

                InsBean ib2 = new InsBean("DCP_PSTOCKIN_DETAIL", columns2);
                ib2.addValues(insValue2);
                this.addProcessData(new DataProcessBean(ib2));

                if(status.equals("2") && !Check.Null(ofNO)){
                    UptBean ub2 = new UptBean("DCP_PROCESSTASKSUM_DETAIL");
                    //add Value
                    ub2.addUpdateValue("STOCKIN_QTY", new DataValue(pqty_b.toPlainString(), Types.VARCHAR,UpdateSelf));
                    ub2.addUpdateValue("SCRAP_QTY", new DataValue(scrap_b.toPlainString(), Types.VARCHAR,UpdateSelf));
                    //condition
                    ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub2.addCondition("PROCESSTASKSUMNO", new DataValue(ofNO, Types.VARCHAR));
                    ub2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                    ub2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub2));
                }
                if(Check.Null(par.getVersionNum())){
                    par.setVersionNum("1");
                }
                //查一下bom
                String bomSql="select  a.material_pluno,a.costrate from DCP_BOM_MATERIAL a " +
                        " inner join dcp_bom b on a.eid=b.eid and a.bomno=b.bomno " +
                        " where b.bomno='"+par.getBomNo()+"' and b.versionnum='"+par.getVersionNum()+"' " +
                        " union all" +
                        " select  a.material_pluno,a.costrate from DCP_BOM_MATERIAL_V a" +
                        " inner join dcp_bom_v b on a.eid=b.eid and a.bomno=b.bomno " +
                        " where b.bomno='"+par.getBomNo()+"' and b.versionnum='"+par.getVersionNum()+"' " ;
                List<Map<String, Object>> bomMList = this.doQueryData(bomSql, null);



                //新增原料单身（多笔）
                List<DCP_PStockOutCreateReq.level2Elm> material = par.getMaterial();
                for (DCP_PStockOutCreateReq.level2Elm mat : material) {
                    if(Check.Null(mat.getCostRate())){
                        List<Map<String, Object>> filterRows1 = bomMList.stream().filter(x -> x.get("MATERIAL_PLUNO").toString().equals(mat.getMaterial_pluNo())).collect(Collectors.toList());
                        if(filterRows1.size()>0){
                            mat.setCostRate(String.valueOf(filterRows1.get(0).get("COSTRATE")));
                        }
                    }
                    int insColCt2 = 0;
                    String[] matColumnsName = {
                            "MITEM", "ITEM", "WAREHOUSE",
                            "PLUNO","PUNIT",
                            "PQTY","PRICE","AMT",
                            "FINALPRODBASEQTY", "RAWMATERIALBASEQTY",
                            "EID","ORGANIZATIONNO","PSTOCKINNO",
                            "SHOPID","MPLUNO","BASEUNIT",
                            "BASEQTY","UNIT_RATIO",
                            "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE","ISBUCKLE","FEATURENO","LOCATION","EXPDATE","COSTRATE"
                    };
                    DataValue[] matColumnsVal = new DataValue[matColumnsName.length];
                    for (int j = 0; j < matColumnsVal.length; j++) {
                        String matKeyVal = null;
                        switch (j) {
                            case 0:
                                matKeyVal = par.getItem();
                                break;
                            case 1:
                                //matKeyVal = mat.getMaterial_item();
                                matKeyVal = String.valueOf(mItem) ;
                                break;
                            case 2:
                                matKeyVal =materialWarehouse;
                                break;
                            case 3:
                                matKeyVal = mat.getMaterial_pluNo();
                                break;
                            case 4:
                                matKeyVal = mat.getMaterial_punit();
                                break;
                            case 5:
                                matKeyVal = mat.getMaterial_pqty();
                                break;
                            case 6:
                                matKeyVal = mat.getMaterial_price();
                                if(Check.Null(matKeyVal)) {
                                    matKeyVal="0";
                                }
                                break;
                            case 7:
                                matKeyVal = mat.getMaterial_amt();
                                break;
                            case 8:
                                matKeyVal = mat.getMaterial_finalProdBaseQty();   ///MATERIAL_FINALPRODBASEQTY
                                break;
                            case 9:
                                matKeyVal = mat.getMaterial_rawMaterialBaseQty();    ///MATERIAL_RAWMATERIALBASEQTY
                                break;
                            case 10:
                                matKeyVal = eId;
                                break;
                            case 11:
                                matKeyVal = organizationNO;
                                break;
                            case 12:
                                matKeyVal = pStockInNO;
                                break;
                            case 13:
                                matKeyVal = shopId;
                                break;
                            case 14:
                                matKeyVal = par.getPluNo(); //MpluNO 主商品PLUNO,非原料PLUNO
                                break;
                            case 15:
                                matKeyVal = mat.getMaterial_baseUnit();
                                break;
                            case 16:
                                matKeyVal = mat.getMaterial_baseQty();
                                break;
                            case 17:
                                matKeyVal = mat.getMaterial_unitRatio();
                                break;
                            case 18:
                                matKeyVal = mat.getMaterial_batchNo();
                                break;
                            case 19:
                                matKeyVal = mat.getMaterial_prodDate();
                                break;
                            case 20:
                                matKeyVal=mat.getMaterial_distriPrice();
                                if (Check.Null(matKeyVal))
                                    matKeyVal = "0";
                                break;
                            case 21:
                                matKeyVal = mat.getMaterial_distriAmt();
                                if (Check.Null(matKeyVal))
                                    matKeyVal="0";
                                break;
                            case 22:
                                matKeyVal = accountDate;
                                break;
                            case 23:
                                matKeyVal = mat.getIsBuckle();
                                if (Check.Null(matKeyVal)||!matKeyVal.equals("N")) {
                                    matKeyVal="Y";
                                }
                                break;
                            case 24:
                                matKeyVal = mat.getMaterial_featureNo();
                                if (Check.Null(matKeyVal))
                                    matKeyVal=" ";
                                break;

                            case 25:
                                matKeyVal = mat.getMaterial_location();
                                if (Check.Null(matKeyVal))
                                    matKeyVal=" ";
                                break;

                            case 26:
                                matKeyVal = mat.getMaterial_expDate();
                                break;
                            case 27:
                                matKeyVal = mat.getCostRate();
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
                    if (status.equals("2") && isMaterialReplace.equals("Y"))//启用替代并且是分批出数
                    {
                        continue;
                    }
                    this.addProcessData(new DataProcessBean(ib3));

                    mItem++;



                }
                //新增原料单身到此为止
                if(!detailWarehouseList.contains(par.getWarehouse())){
                    detailWarehouseList.add(par.getWarehouse());
                }
                if(!detailPluNoList.contains(par.getPluNo())){
                    detailPluNoList.add(par.getPluNo());
                }
            }

            //【ID1031835】 //【3.0】门店管理--库存管理 相关问题 by jinzma 20230323
            String confirmBy ="";
            String confirmDate ="";
            String confirmTime ="";
            if(status.equals("2")) {
                confirmBy = opNo;
                confirmDate = sDate;
                confirmTime = sTime;
            }

            //取当前时间的所属批次号,
            //第一批：8点-9点 ，第二批：9点-10点。
            //如果是刚好9点，取第二批
            String dtno="";
            String sql_DCP_DINNERTIME="select * from DCP_DINNERTIME dt1 " +
                    "where dt1.eid='"+req.geteId()+"' " +
                    "and dt1.shopid='"+req.getOrganizationNO()+"' " +
                    "and dt1.begin_time<='"+sTime+"' " +
                    "and dt1.end_time>'"+sTime+"' ";
            List<Map<String, Object>> getQData_DINNERTIME = this.doQueryData(sql_DCP_DINNERTIME, null);
            if (getQData_DINNERTIME !=null && getQData_DINNERTIME.size()>0)
            {
                dtno=getQData_DINNERTIME.get(0).get("DTNO").toString();
            }


            DataValue[] insValue1 = new DataValue[]{
                    new DataValue(shopId, Types.VARCHAR),
                    new DataValue(organizationNO, Types.VARCHAR),
                    new DataValue(bDate, Types.VARCHAR),
                    new DataValue(pStockInID, Types.VARCHAR),
                    new DataValue(opNo, Types.VARCHAR),
                    new DataValue(sDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(totPqty, Types.VARCHAR),
                    new DataValue(totAmt, Types.VARCHAR),
                    new DataValue(totCqty, Types.VARCHAR),
                    new DataValue(eId, Types.VARCHAR),
                    new DataValue(pStockInNO, Types.VARCHAR),
                    new DataValue(memo, Types.VARCHAR),
                    new DataValue(status, Types.VARCHAR),
                    new DataValue(process_Status, Types.VARCHAR),
                    new DataValue(ofNO, Types.VARCHAR),
                    new DataValue(pTemplateNO, Types.VARCHAR),
                    new DataValue(confirmBy, Types.VARCHAR),
                    new DataValue(accountDate, Types.VARCHAR),
                    new DataValue(sTime, Types.VARCHAR),
                    new DataValue(oType, Types.VARCHAR),
                    new DataValue(warehouse, Types.VARCHAR),
                    new DataValue(confirmBy, Types.VARCHAR),
                    new DataValue(confirmDate, Types.VARCHAR),
                    new DataValue(confirmTime, Types.VARCHAR),
                    new DataValue(confirmBy, Types.VARCHAR),
                    new DataValue(confirmDate, Types.VARCHAR),
                    new DataValue(confirmTime, Types.VARCHAR),
                    new DataValue(doc_Type, Types.VARCHAR),
                    new DataValue(totDistriAmt, Types.VARCHAR),
                    new DataValue(req.getChatUserId(), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                    new DataValue(req.getRequest().getProcessPlanNo(), Types.VARCHAR),
                    new DataValue(req.getRequest().getTask0No(), Types.VARCHAR),
                    new DataValue(dtno, Types.VARCHAR),
                    new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR),
                    new DataValue(req.getRequest().getDepartId(), Types.VARCHAR),
                    new DataValue(req.getDepartmentNo(), Types.VARCHAR),
            };
            InsBean ib1 = new InsBean("DCP_PSTOCKIN", columns1);
            ib1.addValues(insValue1);
            this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

            boolean isNeedStockSync = false;//是否同步到三方

            if(doc_Type.equals("2")){

                StringBuffer sJoinWarehouse=new StringBuffer("");
                for (String thisWarehouse : detailWarehouseList){
                    sJoinWarehouse.append(thisWarehouse+",");
                }
                Map<String, String> mapWarehouse=new HashMap<String, String>();
                mapWarehouse.put("WAREHOUSE", sJoinWarehouse.toString());
                MyCommon cm=new MyCommon();
                String withasSql_mono1=cm.getFormatSourceMultiColWith(mapWarehouse);

                if (withasSql_mono1.equals(""))
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
                }

                String warehouseSql="with p as ("+withasSql_mono1+") "+
                        " select a.ISLOCATION,a.warehouse " +
                        " from DCP_WAREHOUSE a " +
                        " inner join p on p.warehouse=a.warehouse "+
                        " where a.eid='"+req.geteId()+"' and a.organizationno='"+req.getOrganizationNO()+"' "
                        ;
                List<Map<String, Object>> getWarehouseData=this.doQueryData(warehouseSql, null);


                StringBuffer sJoinPluNo=new StringBuffer("");
                for (String thisPluno : detailPluNoList){
                    sJoinPluNo.append(thisPluno+",");
                }
                Map<String, String> mapPluNo=new HashMap<String, String>();
                mapPluNo.put("PLUNO", sJoinPluNo.toString());

                String withasSql_mono2=cm.getFormatSourceMultiColWith(mapPluNo);

                if (withasSql_mono1.equals(""))
                {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "入参转换成临时表with语句的方法处理失败！");
                }

                String pluNoSql="with p as ("+withasSql_mono2+") "+
                        " select a.ISBATCH,a.PLUNO,a.BATCHSORTTYPE " +
                        " from DCP_GOODS a " +
                        " inner join p on p.pluno=a.pluno "+
                        " where a.eid='"+req.geteId()+"'  "
                        ;
                List<Map<String, Object>> getPluData=this.doQueryData(pluNoSql, null);

                //MES_BATCH_STOCK_DETAIL
                String stockSql="with p as ("+withasSql_mono2+") "+
                        " select a.*,to_char(a.proddate,'yyyyMMdd') proddates,to_char(a.validdate,'yyyyMMdd') validdates " +
                        " from MES_BATCH_STOCK_DETAIL a " +
                        " inner join p on p.pluno=a.pluno "+
                        " where a.eid='"+req.geteId()+"' and a.QTY>0  "
                        ;
                List<Map<String, Object>> getStockData=this.doQueryData(stockSql, null);

                List<DCP_PStockOutCreateReq.StockInfo> stockInfos = getStockData.stream().map(x -> {
                    DCP_PStockOutCreateReq.StockInfo stockInfo = req.new StockInfo();
                    stockInfo.setPluNo(x.get("PLUNO").toString());
                    stockInfo.setFeatureNo(x.get("FEATURENO").toString());
                    stockInfo.setWarehouse(x.get("WAREHOUSE").toString());
                    stockInfo.setBatchNo(x.get("BATCHNO").toString());
                    stockInfo.setLocation(x.get("LOCATION").toString());
                    stockInfo.setBaseUnit(x.get("BASEUNIT").toString());
                    stockInfo.setQty(x.get("QTY").toString());
                    stockInfo.setLockQty(x.get("LOCKQTY").toString());
                    stockInfo.setProdDate(x.get("PRODDATES").toString());
                    stockInfo.setValidDate(x.get("VALIDDATES").toString());
                    return stockInfo;
                }).collect(Collectors.toList());

                int batchItem=0;
                for (DCP_PStockOutCreateReq.level1Elm par : datas){
                    List<DCP_PStockOutCreateReq.level3Elm> batchList = par.getBatchList();

                    BigDecimal pQty = new BigDecimal(par.getPqty());

                    if(CollUtil.isNotEmpty(batchList)){
                        for (DCP_PStockOutCreateReq.level3Elm oneMb : batchList){
                            batchItem++;
                            ColumnDataValue mbColumns=new ColumnDataValue();
                            mbColumns.add("EID", DataValues.newString(eId));
                            mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                            mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                            mbColumns.add("ITEM",DataValues.newString(batchItem));
                            mbColumns.add("OITEM",DataValues.newString(par.getItem()));
                            mbColumns.add("PLUNO",DataValues.newString(par.getPluNo()));
                            mbColumns.add("FEATURENO",DataValues.newString(par.getFeatureNo()));
                            mbColumns.add("PUNIT",DataValues.newString(oneMb.getPUnit()));
                            mbColumns.add("PQTY",DataValues.newString(oneMb.getPQty()));
                            mbColumns.add("BASEUNIT",DataValues.newString(oneMb.getBaseUnit()));
                            mbColumns.add("BASEQTY",DataValues.newString(oneMb.getBaseQty()));
                            mbColumns.add("UNIT_RATIO",DataValues.newString(oneMb.getUnitRatio()));

                            mbColumns.add("WAREHOUSE",DataValues.newString(par.getWarehouse()));
                            mbColumns.add("LOCATION",DataValues.newString(Check.Null(oneMb.getLocation())?" ":oneMb.getLocation()));
                            mbColumns.add("BATCHNO",DataValues.newString(Check.Null(oneMb.getBatchNo())?" ":oneMb.getBatchNo()));
                            mbColumns.add("PRODDATE",DataValues.newString(oneMb.getProdDate()));
                            mbColumns.add("EXPDATE",DataValues.newString(oneMb.getExpDate()));


                            String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                            DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                            ibmb.addValues(mbDataValues);
                            this.addProcessData(new DataProcessBean(ibmb));

                        }

                        continue;
                    }

                    List<Map<String, Object>> singlePluInfos = getPluData.stream().filter(x -> x.get("PLUNO").toString().equals(par.getPluNo())).collect(Collectors.toList());
                    if(CollUtil.isNotEmpty(singlePluInfos)){
                        String isBatch = singlePluInfos.get(0).get("ISBATCH").toString();
                        String batchSortType = singlePluInfos.get(0).get("BATCHSORTTYPE").toString();
                        if("Y".equals(isBatch)&&"Y".equals(isBatchNo)){
                            List<DCP_PStockOutCreateReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(par.getPluNo()) && x.getWarehouse().equals(par.getWarehouse())).collect(Collectors.toList());
                            if("1".equals(batchSortType)){
                                singlePluStock.sort(Comparator.comparing(x -> x.getProdDate()));
                            }
                            if("2".equals(batchSortType)){
                                singlePluStock.sort(Comparator.comparing(x -> x.getValidDate()));
                            }


                            for(DCP_PStockOutCreateReq.StockInfo oneStock : singlePluStock){
                                batchItem++;
                                BigDecimal stockQty= new BigDecimal(oneStock.getQty());
                                if(pQty.compareTo(BigDecimal.ZERO)<=0){
                                    continue;//分完了
                                }
                                BigDecimal fpQty=new BigDecimal("0");
                                if(stockQty.compareTo(pQty)>=0){
                                    fpQty=pQty;
                                }else{
                                    fpQty=stockQty;
                                }
                                if(fpQty.compareTo(BigDecimal.ZERO)<=0){
                                    continue;
                                }

                                pQty=pQty.subtract(fpQty);

                                Integer pUnitUdlength = PosPub.getUnitUDLength(dao, eId, par.getPunit());
                                BigDecimal thisPQty = fpQty.divide(new BigDecimal(par.getUnitRatio()), pUnitUdlength);
                                //存表
                                ColumnDataValue mbColumns=new ColumnDataValue();
                                mbColumns.add("EID", DataValues.newString(eId));
                                mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                mbColumns.add("ITEM",DataValues.newString(batchItem+""));
                                mbColumns.add("OITEM",DataValues.newString(par.getItem()));
                                mbColumns.add("PLUNO",DataValues.newString(par.getPluNo()));
                                mbColumns.add("FEATURENO",DataValues.newString(par.getFeatureNo()));
                                mbColumns.add("PUNIT",DataValues.newString(par.getPunit()));
                                mbColumns.add("PQTY",DataValues.newString(thisPQty));
                                mbColumns.add("BASEUNIT",DataValues.newString(par.getBaseUnit()));
                                mbColumns.add("BASEQTY",DataValues.newString(fpQty.toString()));
                                mbColumns.add("UNIT_RATIO",DataValues.newString(par.getUnitRatio()));

                                mbColumns.add("WAREHOUSE",DataValues.newString(oneStock.getWarehouse()));
                                mbColumns.add("LOCATION",DataValues.newString(oneStock.getLocation()));
                                mbColumns.add("BATCHNO",DataValues.newString(oneStock.getBatchNo()));
                                mbColumns.add("PRODDATE",DataValues.newString(oneStock.getProdDate()));
                                mbColumns.add("EXPDATE",DataValues.newString(oneStock.getValidDate()));


                                String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                ibmb.addValues(mbDataValues);
                                this.addProcessData(new DataProcessBean(ibmb));

                                //stockInfos 扣减数量
                                for (DCP_PStockOutCreateReq.StockInfo oldInfo :stockInfos){
                                    if(oldInfo.getPluNo().equals(oneStock.getPluNo())&&oldInfo.getWarehouse().equals(oneStock.getWarehouse())&&
                                            oldInfo.getBatchNo().equals(oneStock.getBatchNo())&&oldInfo.getLocation().equals(oneStock.getLocation())){
                                        oldInfo.setQty(String.valueOf(new BigDecimal(oldInfo.getQty()).subtract(fpQty)));
                                    }
                                }
                            }


                        }else{
                            //仓库
                            List<Map<String, Object>> warehouseInfos = getWarehouseData.stream().filter(x -> x.get("WAREHOUSE").toString().equals(par.getWarehouse())).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(warehouseInfos)){
                                String islocation = warehouseInfos.get(0).get("ISLOCATION").toString();
                                if("N".equals(islocation)){
                                    batchItem++;
                                    ColumnDataValue mbColumns=new ColumnDataValue();
                                    mbColumns.add("EID", DataValues.newString(eId));
                                    mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                    mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                    mbColumns.add("ITEM",DataValues.newString(batchItem));
                                    mbColumns.add("OITEM",DataValues.newString(par.getItem()));
                                    mbColumns.add("PLUNO",DataValues.newString(par.getPluNo()));
                                    mbColumns.add("FEATURENO",DataValues.newString(par.getFeatureNo()));
                                    mbColumns.add("PUNIT",DataValues.newString(par.getPunit()));
                                    mbColumns.add("PQTY",DataValues.newString(par.getPqty()));
                                    mbColumns.add("BASEUNIT",DataValues.newString(par.getBaseUnit()));
                                    mbColumns.add("BASEQTY",DataValues.newString(par.getBaseQty()));
                                    mbColumns.add("UNIT_RATIO",DataValues.newString(par.getUnitRatio()));

                                    mbColumns.add("WAREHOUSE",DataValues.newString(par.getWarehouse()));
                                    mbColumns.add("LOCATION",DataValues.newString(Check.Null(par.getLocation())?" ":par.getLocation()));
                                    mbColumns.add("BATCHNO",DataValues.newString(Check.Null(par.getBatchNo())?" ":par.getBatchNo()));
                                    mbColumns.add("PRODDATE",DataValues.newString(par.getProdDate()));
                                    mbColumns.add("EXPDATE",DataValues.newString(par.getExpDate()));


                                    String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                    DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                    InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                    ibmb.addValues(mbDataValues);
                                    this.addProcessData(new DataProcessBean(ibmb));

                                }
                                else {
                                    List<DCP_PStockOutCreateReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(par.getPluNo()) && x.getWarehouse().equals(par.getWarehouse())).sorted(Comparator.comparing(x -> x.getLocation())).collect(Collectors.toList());
                                    for(DCP_PStockOutCreateReq.StockInfo oneStock : singlePluStock){
                                        batchItem++;
                                        BigDecimal stockQty= new BigDecimal(oneStock.getQty());
                                        if(pQty.compareTo(BigDecimal.ZERO)<=0){
                                            continue;//分完了
                                        }
                                        BigDecimal fpQty=new BigDecimal("0");
                                        if(stockQty.compareTo(pQty)>=0){
                                            fpQty=pQty;
                                        }else{
                                            fpQty=stockQty;
                                        }
                                        if(fpQty.compareTo(BigDecimal.ZERO)<=0){
                                            continue;
                                        }

                                        pQty=pQty.subtract(fpQty);

                                        Integer pUnitUdlength = PosPub.getUnitUDLength(dao, eId, par.getPunit());
                                        BigDecimal thisPQty = fpQty.divide(new BigDecimal(par.getUnitRatio()), pUnitUdlength);

                                        //存表
                                        ColumnDataValue mbColumns=new ColumnDataValue();
                                        mbColumns.add("EID", DataValues.newString(eId));
                                        mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                        mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                        mbColumns.add("ITEM",DataValues.newString(batchItem+""));
                                        mbColumns.add("OITEM",DataValues.newString(par.getItem()));
                                        mbColumns.add("PLUNO",DataValues.newString(par.getPluNo()));
                                        mbColumns.add("FEATURENO",DataValues.newString(par.getFeatureNo()));
                                        mbColumns.add("PUNIT",DataValues.newString(par.getPunit()));
                                        mbColumns.add("PQTY",DataValues.newString(thisPQty));
                                        mbColumns.add("BASEUNIT",DataValues.newString(par.getBaseUnit()));
                                        mbColumns.add("BASEQTY",DataValues.newString(fpQty.toString()));
                                        mbColumns.add("UNIT_RATIO",DataValues.newString(par.getUnitRatio()));

                                        mbColumns.add("WAREHOUSE",DataValues.newString(oneStock.getWarehouse()));
                                        mbColumns.add("LOCATION",DataValues.newString(oneStock.getLocation()));
                                        mbColumns.add("BATCHNO",DataValues.newString(oneStock.getBatchNo()));
                                        mbColumns.add("PRODDATE",DataValues.newString(oneStock.getProdDate()));
                                        mbColumns.add("EXPDATE",DataValues.newString(oneStock.getValidDate()));


                                        String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                        DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                        InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                        ibmb.addValues(mbDataValues);
                                        this.addProcessData(new DataProcessBean(ibmb));

                                        //stockInfos 扣减数量
                                        for (DCP_PStockOutCreateReq.StockInfo oldInfo :stockInfos){
                                            if(oldInfo.getPluNo().equals(oneStock.getPluNo())&&oldInfo.getWarehouse().equals(oneStock.getWarehouse())&&
                                                    oldInfo.getBatchNo().equals(oneStock.getBatchNo())&&oldInfo.getLocation().equals(oneStock.getLocation())){
                                                oldInfo.setQty(String.valueOf(new BigDecimal(oldInfo.getQty()).subtract(fpQty)));
                                            }
                                        }
                                    }

                                }
                            }
                        }

                    }


                }
            }

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            //把单号返回
            res.setPStockInNo(pStockInNO);


        }
        else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PStockOutCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PStockOutCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PStockOutCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PStockOutCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        //必传值不为空
        String status = req.getRequest().getStatus();
        String pStockInID = req.getRequest().getPStockInID();
        String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
        String warehouse = req.getRequest().getWarehouse();
        String docType = req.getRequest().getDocType();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totDistriAmt=req.getRequest().getTotDistriAmt();
        String totCqty = req.getRequest().getTotCqty();
        String bDate = req.getRequest().getBDate();
        List<DCP_PStockOutCreateReq.level1Elm> datas = req.getRequest().getDatas();

        if(Check.Null(docType)){
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }
        if(Check.Null(status)){
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if(Check.Null(pStockInID)){
            errMsg.append("完工入库单guid不可为空值, ");
            isFail = true;
        }
        if(Check.Null(warehouse)||warehouse.equals(" ")){
            errMsg.append("成品仓库不可为空值或空格, ");
            isFail = true;
        }
        if(Check.Null(materialWarehouse)||materialWarehouse.equals(" ")){
            errMsg.append("原料仓库不可为空值或空格, ");
            isFail = true;
        }
        if (Check.Null(totPqty)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totAmt)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totDistriAmt)) {
            errMsg.append("合计进货金额可为空值, ");
            isFail = true;
        }
        if (Check.Null(totCqty)) {
            errMsg.append("合计品种数量不可为空值, ");
            isFail = true;
        }

        //【ID1021919】【嘉华3.0】完工入库BDATE传值有问题  by jinzma 20211108
        if (Check.Null(bDate)){
            errMsg.append("单据日期不可为空值, ");
            isFail = true;
        }else{
            if (bDate.length() != 8){
                errMsg.append("单据日期格式错误, ");
                isFail = true;
            }
        }
        //【ID1033376】【大拇指3.0】组合拆解单问题（只有单头没有单身） by jinzma 20230518
        if(datas == null || datas.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "成品单身不能为空!");
        }else{
            int distriAmtLength=2;
            int amtLength=2;
            if(PosPub.isNumeric(PosPub.getPARA_SMS(dao, req.geteId(), req.getOrganizationNO(), "distriAmtLength")))
            {
                distriAmtLength=Integer.parseInt(PosPub.getPARA_SMS(dao, req.geteId(), req.getOrganizationNO(), "distriAmtLength"));
            }
            if(PosPub.isNumeric(PosPub.getPARA_SMS(dao,req.geteId(), req.getOrganizationNO(), "amtLength")))
            {
                amtLength=Integer.parseInt(PosPub.getPARA_SMS(dao,req.geteId(), req.getOrganizationNO(), "amtLength"));
            }
            for(DCP_PStockOutCreateReq.level1Elm par:datas) {
                String pluNo = par.getPluNo();
                String baseUnit =par.getBaseUnit();
                String baseQty = par.getBaseQty();
                String unitRatio = par.getUnitRatio();
                String price=par.getPrice();
                String distriPrice=par.getDistriPrice();
                String amt=par.getAmt();
                String distriAmt=par.getDistriAmt();
                BigDecimal bdcPQty=new BigDecimal("0");
                BigDecimal bdcScrapQty=new BigDecimal("0");
                BigDecimal bdcPrice=new BigDecimal("0");
                BigDecimal bdcAmt=new BigDecimal("0");
                BigDecimal bdcDistriPrice=new BigDecimal("0");
                BigDecimal bdcDistriAmt=new BigDecimal("0");
                if(par.getPrice()!=null && !par.getPrice().toString().isEmpty())
                {
                    bdcPrice=new BigDecimal(par.getPrice());
                }
                if(par.getAmt()!=null && !par.getAmt().toString().isEmpty())
                {
                    bdcAmt=new BigDecimal(par.getAmt());
                }
                if(par.getDistriPrice()!=null && !par.getDistriPrice().toString().isEmpty())
                {
                    bdcDistriPrice=new BigDecimal(par.getDistriPrice());
                }
                if(par.getDistriAmt()!=null && !par.getDistriAmt().toString().isEmpty())
                {
                    bdcDistriAmt=new BigDecimal(par.getDistriAmt());
                }
                if(par.getPqty()!=null && !par.getPqty().toString().isEmpty())
                {
                    bdcPQty=new BigDecimal(par.getPqty());
                }
                if(par.getScrapQty()!=null && !par.getScrapQty().toString().isEmpty())
                {
                    bdcScrapQty=new BigDecimal(par.getScrapQty());
                }
                if(!BigDecimalUtils.equal(bdcScrapQty.add(bdcPQty).multiply(bdcPrice).setScale(amtLength, java.math.RoundingMode.HALF_UP).doubleValue(), bdcAmt.doubleValue()))
                {
                    errMsg.append("商品:" + par.getPluNo()
                            +"零售价:"+bdcPrice.toString()+"*数量:"+bdcScrapQty.add(bdcPQty).toString()+"不等于金额:"+bdcAmt.toString()+", ");
                }
                if(!BigDecimalUtils.equal( bdcScrapQty.add(bdcPQty).multiply(bdcDistriPrice).setScale(distriAmtLength, java.math.RoundingMode.HALF_UP).doubleValue(), bdcDistriAmt.doubleValue()))
                {
                    errMsg.append("商品:" + par.getPluNo()
                            +"进货价:"+bdcDistriPrice.toString()+"*数量:"+bdcScrapQty.add(bdcPQty).toString()+"不等于金额:"+bdcDistriAmt.toString());
                }
                List<DCP_PStockOutCreateReq.level2Elm> material = par.getMaterial();

                if (Check.Null(par.getItem())) {
                    errMsg.append("项次不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getOItem())) {
                    errMsg.append("来源项次不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPluNo())) {
                    errMsg.append("商品编码不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPunit())) {
                    errMsg.append("录入单位不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getPqty())) {
                    errMsg.append("录入数量不可为空值, ");
                    isFail = true;
                }
                if (baseUnit == null) {
                    errMsg.append("商品"+pluNo+"基本单位不可为空值, ");
                    isFail = true;
                }
                if (baseQty == null) {
                    errMsg.append("商品"+pluNo+"基本数量不可为空值, ");
                    isFail = true;
                }
                if (unitRatio == null) {
                    errMsg.append("商品"+pluNo+"单位转换率不可为空值, ");
                    isFail = true;
                }
                if (price== null) {
                    errMsg.append("商品"+pluNo+"零售价不可为空值, ");
                    isFail = true;
                }
                if (distriPrice== null) {
                    errMsg.append("商品"+pluNo+"进货价不可为空值, ");
                    isFail = true;
                }
                if (amt== null) {
                    errMsg.append("商品"+pluNo+"金额不可为空值, ");
                    isFail = true;
                }
                if (distriAmt== null) {
                    errMsg.append("商品"+pluNo+"进货金额不可为空值, ");
                    isFail = true;
                }
                if (material==null) {
                    errMsg.append("商品" + pluNo + "原料单身不可为空,");
                    isFail = true;
                }
                if (isFail) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }

                for (DCP_PStockOutCreateReq.level2Elm materialPar : material) {
                    //String mItem = materialPar.getMItem();
                    String material_item = materialPar.getMaterial_item();
                    String material_pluNo = materialPar.getMaterial_pluNo();
                    String material_punit = materialPar.getMaterial_punit();
                    String material_pqty = materialPar.getMaterial_pqty();
                    String material_baseUnit = materialPar.getMaterial_baseUnit();
                    String material_baseQty = materialPar.getMaterial_baseQty();
                    String material_unitRatio = materialPar.getMaterial_unitRatio();
                    String material_price = materialPar.getMaterial_price();
                    String material_distriPrice = materialPar.getMaterial_distriPrice();
                    String material_amt = materialPar.getMaterial_amt();
                    String material_distriAmt = materialPar.getMaterial_distriAmt();
                    String material_finalProdBaseQty = materialPar.getMaterial_finalProdBaseQty();
                    String material_rawMaterialBaseQty = materialPar.getMaterial_rawMaterialBaseQty();
                    //if (mItem == null) {
                    //    errMsg.append("原料" + material_pluNo + "主项次不可为空值, ");
                    //    isFail = true;
                    //}
                    if (material_item == null) {
                        errMsg.append("原料" + material_pluNo + "项次不可为空值, ");
                        isFail = true;
                    }
                    if (material_pluNo == null) {
                        errMsg.append("原料商品编码不可为空值, ");
                        isFail = true;
                    }
                    if (material_punit == null) {
                        errMsg.append("原料" + material_pluNo + "单位不可为空值, ");
                        isFail = true;
                    }
                    if (material_pqty == null) {
                        errMsg.append("原料" + material_pluNo + "数量不可为空值, ");
                        isFail = true;
                    }
                    if (material_baseUnit == null) {
                        errMsg.append("原料" + material_pluNo + "基准单位不可为空值, ");
                        isFail = true;
                    }
                    if (material_baseQty == null) {
                        errMsg.append("原料" + material_pluNo + "基准单位数量不可为空值, ");
                        isFail = true;
                    }
                    if (material_unitRatio == null) {
                        errMsg.append("原料" + material_pluNo + "单位转换率不可为空值, ");
                        isFail = true;
                    }
                    if (material_price == null) {
                        errMsg.append("原料" + material_pluNo + "零售价不可为空值, ");
                        isFail = true;
                    }
                    if (material_distriPrice == null) {
                        errMsg.append("原料" + material_pluNo + "进货价不可为空值, ");
                        isFail = true;
                    }
                    if (material_amt == null) {
                        errMsg.append("原料" + material_pluNo + "金额不可为空值, ");
                        isFail = true;
                    }
                    if (material_distriAmt == null) {
                        errMsg.append("原料" + material_pluNo + "进货金额不可为空值, ");
                        isFail = true;
                    }
                    if (material_finalProdBaseQty == null) {
                        errMsg.append("原料" + material_pluNo + "成品基础量不可为空值, ");
                        isFail = true;
                    }
                    if (material_rawMaterialBaseQty == null) {
                        errMsg.append("原料" + material_pluNo + "原料基础用量不可为空值, ");
                        isFail = true;
                    }


                    if (isFail) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }
                List<DCP_PStockOutCreateReq.level3Elm> materialBatchList = par.getBatchList();

                if(CollUtil.isNotEmpty(materialBatchList)){
                    for (DCP_PStockOutCreateReq.level3Elm materialBatch : materialBatchList) {
                        if(Check.Null(materialBatch.getItem())){
                            isFail=true;
                            errMsg.append("原料" + par.getPluNo() + "批号列表项次不可为空值, ");
                        }
                        //if(Check.Null(materialBatch.getLocation())){
                        //    isFail=true;
                        //    errMsg.append("原料" + par.getPluNo() + "批号列表库位编号不可为空值, ");
                        //}
                        //if(Check.Null(materialBatch.getBatchNo())){
                        //    isFail=true;
                        //    errMsg.append("原料" + par.getPluNo() + "批号列表批号不可为空值, ");
                        //}
                        //if(Check.Null(materialBatch.getProdDate())){
                        //    isFail=true;
                        //    errMsg.append("原料" + par.getPluNo() + "批号列表生产日期不可为空值, ");
                        //}
                        //if(Check.Null(materialBatch.getExpDate())){
                        //    isFail=true;
                        //    errMsg.append("原料" + par.getPluNo() + "批号列表有效日期不可为空值, ");
                        //}
                        if(Check.Null(materialBatch.getPUnit())){
                            isFail=true;
                            errMsg.append("原料" + par.getPluNo() + "批号列表原料单位不可为空值, ");
                        }
                        if(Check.Null(materialBatch.getPQty())){
                            isFail=true;
                            errMsg.append("原料" + par.getPluNo() + "批号列表原料数量不可为空值, ");
                        }
                        if(Check.Null(materialBatch.getBaseUnit())){
                            isFail=true;
                            errMsg.append("原料" + par.getPluNo() + "批号列表原料基础单位不可为空值, ");
                        }
                        if(Check.Null(materialBatch.getBaseQty())){
                            isFail=true;
                            errMsg.append("原料" + par.getPluNo() + "批号列表原料基础数量不可为空值, ");
                        }
                        if(Check.Null(materialBatch.getUnitRatio())){
                            isFail=true;
                            errMsg.append("原料" + par.getPluNo() + "批号列表单位与基准单位的换算率不可为空值, ");
                        }

                    }
                }

            }
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_PStockOutCreateReq> getRequestType() {
        return new TypeToken<DCP_PStockOutCreateReq>(){};
    }

    @Override
    protected DCP_PStockOutCreateRes getResponseType() {
        return new DCP_PStockOutCreateRes();
    }

    @Override
    protected String getQuerySql(DCP_PStockOutCreateReq req) throws Exception {
        return null ;
    }

    private boolean checkGuid(DCP_PStockOutCreateReq req) throws Exception {
        String guid = req.getRequest().getPStockInID();
        boolean existGuid=false;
        String sql= "select pstockin_id from dcp_pstockin where pstockin_id = '"+guid+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        if (getQData != null && !getQData.isEmpty()) {
            existGuid = true;
        }
        return existGuid;
    }


}

