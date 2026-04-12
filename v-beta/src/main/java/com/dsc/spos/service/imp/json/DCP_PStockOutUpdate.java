package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PStockOutUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PStockOutUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DCP_PStockOutUpdate extends SPosAdvanceService<DCP_PStockOutUpdateReq, DCP_PStockOutUpdateRes> {

    @Override
    protected void processDUID(DCP_PStockOutUpdateReq req, DCP_PStockOutUpdateRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String bDate = req.getRequest().getBDate();
        String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
        String warehouse = req.getRequest().getWarehouse();
        String modifyBy = req.getOpNO();
        String modifyDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String modifyTime = new SimpleDateFormat("HHmmss").format(new Date());
        String memo = req.getRequest().getMemo();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totCqty = req.getRequest().getTotCqty();
        String totDistriAmt = req.getRequest().getTotDistriAmt();
        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

        ///完工入库，单据日期新增如果前端没有给值，后端取系统日期  BY JZMA 20200427
        if (Check.Null(bDate)) {
            bDate = modifyDate;
        }
        String pStockInNO = req.getRequest().getPStockInNo();
        String isMaterialReplace=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "MaterialReplace");
        String isBatchNo=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "Is_BatchNO");
        if(isMaterialReplace==null||isMaterialReplace.isEmpty())
        {
            isMaterialReplace="N";
        }

        //2018-08-10新增docType
        String docType = req.getRequest().getDocType();
        String pTemplateNO = req.getRequest().getPTemplateNo();
        //try {
            if (checkGuid(req)) {
                List<String> detailWarehouseList = new ArrayList();
                List<String> detailPluNoList=new ArrayList<>();

                //删除原有单身
                DelBean db1 = new DelBean("DCP_PSTOCKIN_DETAIL");
                db1.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                //2018-08-10新增以下代码，同理，删除成品明细表中的数据，然后再新增该数据，不做具体的修改操作，完成修改的目的。
                DelBean db2 = new DelBean("DCP_PSTOCKIN_MATERIAL");
                db2.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db2.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db2));

                //DCP_PSTOCKOUT_BATCH
                DelBean db3 = new DelBean("DCP_PSTOCKOUT_BATCH");
                db3.addCondition("pStockInNO", new DataValue(pStockInNO, Types.VARCHAR));
                db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db3.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db3));

                //新增新的单身（多条记录）
                int mItem = 1;//原料项次自增长，不取前端
                List<DCP_PStockOutUpdateReq.level1Elm> datas = req.getRequest().getDatas();
                for (DCP_PStockOutUpdateReq.level1Elm par : datas) {
                    int insColCt = 0;
                    String[] columnsName = {
                            "PSTOCKINNO","SHOPID","ITEM","OITEM","PLUNO",
                            "PUNIT","PQTY","BASEUNIT","BASEQTY","UNIT_RATIO",
                            "PRICE","AMT","EID","ORGANIZATIONNO",
                            "TASK_QTY","SCRAP_QTY","MUL_QTY","BSNO","WAREHOUSE",
                            "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT",
                            "ACCOUNT_DATE","FEATURENO","MEMO","LOCATION","EXPDATE","PRODTYPE","BOMNO","VERSIONNUM"
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
                                keyVal = par.getOItem();
                                if (!PosPub.isNumeric(keyVal)) {
                                    keyVal="0";
                                }
                                break;
                            case 4:
                                keyVal = par.getPluNo(); //pluNO
                                break;
                            case 5:
                                keyVal = par.getPunit(); //punit
                                break;
                            case 6:
                                keyVal = par.getPqty(); //pqty
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
                                keyVal = par.getTaskQty(); //taskQty
                                if (!PosPub.isNumeric(keyVal)) {
                                    keyVal="0";
                                }
                                break;
                            case 15:
                                keyVal = par.getScrapQty();	//scrapQty
                                if (!PosPub.isNumericType(keyVal)) {
                                    keyVal="0";
                                }
                                break;
                            case 16:
                                keyVal = par.getMulQty(); //mulQty
                                if (!PosPub.isNumeric(keyVal)) {
                                    keyVal="0";
                                }
                                break;
                            case 17:
                                keyVal = par.getBsNo();
                                break;
                            case 18:
                                keyVal = warehouse;
                                break;
                            case 19:
                                keyVal = par.getBatchNo();
                                break;
                            case 20:
                                keyVal = par.getProdDate();
                                break;
                            case 21:
                                keyVal=par.getDistriPrice();
                                if(Check.Null(keyVal))
                                    keyVal = "0";
                                break;
                            case 22:
                                keyVal = par.getDistriAmt();
                                if (Check.Null(keyVal))
                                    keyVal="0";
                                break;
                            case 23:
                                keyVal = accountDate;
                                break;
                            case 24:
                                keyVal = par.getFeatureNo();
                                if (Check.Null(keyVal))
                                    keyVal = " ";
                                break;
                            case 25:
                                keyVal = par.getMemo();
                                break;

                            case 26:
                                keyVal = par.getLocation();
                                if(Check.Null(keyVal)){
                                    keyVal=" ";
                                }
                                break;

                            case 27:
                                keyVal = par.getExpDate();
                                break;
                            case 28:
                                keyVal = par.getProdType();
                                break;
                            case 29:
                                keyVal = par.getBomNo();
                                break;
                            case 30:
                                keyVal = par.getVersionNum();
                                break;
                            default:
                                break;
                        }
                        if (keyVal != null) {
                            insColCt++;
                            if (i == 2 || i == 3){
                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                            }else if (i == 6 || i == 8 || i == 9 || i == 10 || i == 11 || i == 14 || i == 15 || i == 16){
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

                    insColCt = 0;
                    for (int i = 0; i < columnsVal.length; i++){
                        if (columnsVal[i] != null){
                            columns2[insColCt] = columnsName[i];
                            insValue2[insColCt] = columnsVal[i];
                            insColCt ++;
                            if (insColCt >= insValue2.length) break;
                        }
                    }

                    InsBean ib2 = new InsBean("DCP_PSTOCKIN_DETAIL", columns2);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));//增加单身

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

                    //新增原料明细（多笔）//加上一个管控，若没有成品明细，直接break;
                    List<DCP_PStockOutUpdateReq.level2Elm> material = par.getMaterial();

                    for (DCP_PStockOutUpdateReq.level2Elm mat : material) {
                        if(Check.Null(mat.getCostRate())){
                            List<Map<String, Object>> filterRows1 = bomMList.stream().filter(x -> x.get("MATERIAL_PLUNO").toString().equals(mat.getMaterial_pluNo())).collect(Collectors.toList());
                            if(filterRows1.size()>0){
                                mat.setCostRate(String.valueOf(filterRows1.get(0).get("COSTRATE")));
                            }
                        }
                        int insColCt2 = 0;
                        String[] matColumnsName = {
                                "MITEM","ITEM","WAREHOUSE",
                                "PLUNO","PUNIT",
                                "PQTY","PRICE","AMT",
                                "FINALPRODBASEQTY", "RAWMATERIALBASEQTY",
                                "EID","ORGANIZATIONNO","PSTOCKINNO",
                                "SHOPID","MPLUNO","BASEUNIT",
                                "BASEQTY","UNIT_RATIO",
                                "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE",
                                "ISBUCKLE","FEATURENO","LOCATION","EXPDATE","COSTRATE"
                        };
                        DataValue[] matColumnsVal = new DataValue[matColumnsName.length];
                        for (int j = 0; j < matColumnsVal.length; j++) {
                            String matKeyVal = null;
                            switch (j) {
                                case 0:
                                    matKeyVal = par.getItem();
                                    break;
                                case 1:
                                    matKeyVal = String.valueOf(mItem) ;
                                    break;
                                case 2:
                                    matKeyVal = materialWarehouse;
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
                                    break;
                                case 7:
                                    matKeyVal = mat.getMaterial_amt();
                                    break;
                                case 8:
                                    matKeyVal = mat.getMaterial_finalProdBaseQty();
                                    break;
                                case 9:
                                    matKeyVal = mat.getMaterial_rawMaterialBaseQty();
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
                                    matKeyVal = par.getPluNo();
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
                                        matKeyVal="0";
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
                                    String isBuckle = mat.getIsBuckle();
                                    if (Check.Null(isBuckle)||!isBuckle.equals("N")) {
                                        isBuckle="Y";
                                    }
                                    matKeyVal = isBuckle;
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
                                if (j == 5 || j == 6){
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

                //取当前时间的所属批次号,
                //第一批：8点-9点 ，第二批：9点-10点。
                //如果是刚好9点，取第二批
                String dtno="";
                String sql_DCP_DINNERTIME="select * from DCP_DINNERTIME dt1 " +
                        "where dt1.eid='"+req.geteId()+"' " +
                        "and dt1.shopid='"+req.getOrganizationNO()+"' " +
                        "and dt1.begin_time<='"+modifyTime+"' " +
                        "and dt1.end_time>'"+modifyTime+"' ";
                List<Map<String, Object>> getQData_DINNERTIME = this.doQueryData(sql_DCP_DINNERTIME, null);
                if (getQData_DINNERTIME !=null && getQData_DINNERTIME.size()>0)
                {
                    dtno=getQData_DINNERTIME.get(0).get("DTNO").toString();
                }

                //更新单头
                UptBean ub1 = new UptBean("DCP_PSTOCKIN");
                //add Value
                ub1.addUpdateValue("BDATE", new DataValue(bDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
                ub1.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
                ub1.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
                ub1.addUpdateValue("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
                ub1.addUpdateValue("ACCOUNT_DATE", new DataValue(accountDate, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                //2018-08-10  更新单头部分加上docType;
                ub1.addUpdateValue("DOC_TYPE", new DataValue(docType, Types.VARCHAR));
                //2019-07-31  更新单头部分加上pTemplateNO BY JZMA 前端允许变更模板;
                ub1.addUpdateValue("pTemplateNO", new DataValue(pTemplateNO, Types.VARCHAR));
                ub1.addUpdateValue("PROCESSPLANNO", new DataValue(req.getRequest().getProcessPlanNo(), Types.VARCHAR));
                ub1.addUpdateValue("TASK0NO", new DataValue(req.getRequest().getTask0No(), Types.VARCHAR));
                ub1.addUpdateValue("DTNO", new DataValue(dtno, Types.VARCHAR));

                ub1.addUpdateValue("EMPLOYEEID", new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR));
                ub1.addUpdateValue("DEPARTID", new DataValue(req.getRequest().getDepartId(), Types.VARCHAR));



                //condition
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("PSTOCKINNO", new DataValue(pStockInNO, Types.VARCHAR));
                ub1.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));


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

                List<DCP_PStockOutUpdateReq.StockInfo> stockInfos = getStockData.stream().map(x -> {
                    DCP_PStockOutUpdateReq.StockInfo stockInfo = req.new StockInfo();
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
                for (DCP_PStockOutUpdateReq.level1Elm par : datas){
                    List<DCP_PStockOutUpdateReq.level3Elm> batchList = par.getBatchList();

                    BigDecimal pQty = new BigDecimal(par.getPqty());

                    if(CollUtil.isNotEmpty(batchList)){
                        for (DCP_PStockOutUpdateReq.level3Elm oneMb : batchList){
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
                            List<DCP_PStockOutUpdateReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(par.getPluNo()) && x.getWarehouse().equals(par.getWarehouse())).collect(Collectors.toList());
                            if("1".equals(batchSortType)){
                                singlePluStock.sort(Comparator.comparing(x -> x.getProdDate()));
                            }
                            if("2".equals(batchSortType)){
                                singlePluStock.sort(Comparator.comparing(x -> x.getValidDate()));
                            }


                            for(DCP_PStockOutUpdateReq.StockInfo oneStock : singlePluStock){
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
                                for (DCP_PStockOutUpdateReq.StockInfo oldInfo :stockInfos){
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
                                    List<DCP_PStockOutUpdateReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(par.getPluNo()) && x.getWarehouse().equals(par.getWarehouse())).sorted(Comparator.comparing(x -> x.getLocation())).collect(Collectors.toList());
                                    for(DCP_PStockOutUpdateReq.StockInfo oneStock : singlePluStock){
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
                                        for (DCP_PStockOutUpdateReq.StockInfo oldInfo :stockInfos){
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


                this.doExecuteDataToDB();

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            } else {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
            }
        //} catch(Exception e) {
        //    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E500, e.getMessage());
        //}
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PStockOutUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PStockOutUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PStockOutUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PStockOutUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        //必传值不为空
        String warehouse = req.getRequest().getWarehouse();
        String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totDistriAmt=req.getRequest().getTotDistriAmt();
        String totCqty = req.getRequest().getTotCqty();
        String pStockInNo = req.getRequest().getPStockInNo();
        String bDate = req.getRequest().getBDate();
        List<DCP_PStockOutUpdateReq.level1Elm> datas = req.getRequest().getDatas();

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
        if (Check.Null(pStockInNo)) {
            errMsg.append("单号不可为空值, ");
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
        }else {
            for (DCP_PStockOutUpdateReq.level1Elm par : datas) {
                String pluNo = par.getPluNo();
                String baseUnit = par.getBaseUnit();
                String baseQty = par.getBaseQty();
                String unitRatio = par.getUnitRatio();
                String price = par.getPrice();
                String distriPrice = par.getDistriPrice();
                String amt = par.getAmt();
                String distriAmt = par.getDistriAmt();
                List<DCP_PStockOutUpdateReq.level2Elm> material = par.getMaterial();

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
                if (baseUnit == null) {
                    errMsg.append("商品" + pluNo + "基本单位不可为空值, ");
                    isFail = true;
                }
                if (baseQty == null) {
                    errMsg.append("商品" + pluNo + "基本数量不可为空值, ");
                    isFail = true;
                }
                if (unitRatio == null) {
                    errMsg.append("商品" + pluNo + "单位转换率不可为空值, ");
                    isFail = true;
                }
                if (price == null) {
                    errMsg.append("商品" + pluNo + "零售价不可为空值, ");
                    isFail = true;
                }
                if (distriPrice == null) {
                    errMsg.append("商品" + pluNo + "进货价不可为空值, ");
                    isFail = true;
                }
                if (amt == null) {
                    errMsg.append("商品" + pluNo + "金额不可为空值, ");
                    isFail = true;
                }
                if (distriAmt == null) {
                    errMsg.append("商品" + pluNo + "进货金额不可为空值, ");
                    isFail = true;
                }
                if (material==null) {
                    errMsg.append("商品" + pluNo + "原料单身不可为空,");
                    isFail = true;
                }

                if (isFail) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }

                for (DCP_PStockOutUpdateReq.level2Elm materialPar : material) {
                    String mItem = materialPar.getMItem();
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

                    if (mItem == null) {
                        errMsg.append("原料" + material_pluNo + "主项次不可为空值, ");
                        isFail = true;
                    }
                    if (material_item == null) {
                        errMsg.append("原料" + material_pluNo + "项次不可为空值, ");
                        isFail = true;
                    }
                    if (material_pluNo == null) {
                        errMsg.append("原料编码不可为空值, ");
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

            }
        }

        if (isFail){
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_PStockOutUpdateReq> getRequestType() {
        return new TypeToken<DCP_PStockOutUpdateReq>(){};
    }

    @Override
    protected DCP_PStockOutUpdateRes getResponseType() {
        return new DCP_PStockOutUpdateRes();
    }

    private boolean checkGuid(DCP_PStockOutUpdateReq req) throws Exception {
        String eId= req.geteId();
        String shopId=req.getShopId();
        String pStockInNO = req.getRequest().getPStockInNo();
        boolean existGuid=true;
        String sql = "select pstockinno,account_date from DCP_pStockIn "
                + " where EID='"+eId+"' and SHOPID='"+shopId+"' and pstockinno='"+pStockInNO+"' and status='0'   " ;
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData == null || getQData.isEmpty()) {
            existGuid = false;
        }
        return existGuid;
    }
}

