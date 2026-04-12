package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PStockInCreateReq;
import com.dsc.spos.json.cust.req.DCP_PStockInCreateReq.level1Elm;
import com.dsc.spos.json.cust.req.DCP_PStockInCreateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_PStockInCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.google.gson.reflect.TypeToken;
import static com.dsc.spos.dao.DataValue.DataExpression.UpdateSelf;

public class DCP_PStockInCreate extends SPosAdvanceService<DCP_PStockInCreateReq, DCP_PStockInCreateRes>{

    @Override
    protected void processDUID(DCP_PStockInCreateReq req, DCP_PStockInCreateRes res) throws Exception {
        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String memo = req.getRequest().getMemo();
        String status = req.getRequest().getStatus();
        String process_Status = "N";
        String pTemplateNO = req.getRequest().getpTemplateNo();
        String bDate = req.getRequest().getbDate();
        String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
        String warehouse = req.getRequest().getWarehouse();
        //2018-08-07 添加DOC_TYPE
        String doc_Type = req.getRequest().getDocType();
        String opNo = req.getEmployeeNo();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());
        ///完工入库，单据日期新增如果前端没有给值，后端取系统日期  BY JZMA 20200427
        if (Check.Null(bDate)) {
            bDate = sDate;
        }
        String accountDate =PosPub.getAccountDate_SMS(dao, eId, shopId);

        String pStockInID = req.getRequest().getpStockInID();
        String ofNO = req.getRequest().getOfNo();
        String oType = req.getRequest().getoType();
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


        //try {
            if (!checkGuid(req)) {
                String docType = req.getRequest().getDocType();
                String preFix="";
                switch (docType) {
                    case "0":
                        preFix = "WGRK";
                        break;
                    case "1":
                        preFix = "ZHRK" ;
                        break;
                    case "2":
                        preFix = "CJCK" ;
                        break;
                    // 2019-05-29 若docType==3， 转换合并单，
                    case "3":
                        preFix = "ZHHB" ;
                        break;
                    // 2019-08-19 若docType==4， 转换拆解单据，
                    case "4":
                        preFix = "ZHCJ" ;
                        break;
                }

                String pStockInNO = this.getOrderNO(req,preFix); //getPStockInNO(req);
                String[] columns1 = {
                        "SHOPID", "ORGANIZATIONNO","BDATE","PSTOCKIN_ID","CREATEBY", "CREATE_DATE", "CREATE_TIME","TOT_PQTY",
                        "TOT_AMT", "TOT_CQTY", "EID","PSTOCKINNO", "MEMO", "STATUS", "PROCESS_STATUS","OFNO","PTEMPLATENO",
                        "ACCOUNTBY","ACCOUNT_DATE","ACCOUNT_TIME","OTYPE","WAREHOUSE","CONFIRMBY","CONFIRM_DATE","CONFIRM_TIME",
                        "SUBMITBY","SUBMIT_DATE","SUBMIT_TIME","DOC_TYPE","TOT_DISTRIAMT","CREATE_CHATUSERID",
                        "UPDATE_TIME","TRAN_TIME","PROCESSPLANNO","TASK0NO","DTNO","EMPLOYEEID","DEPARTID","CREATEDEPTID","PRODTYPE","OOTYPE","OOFNO"};

                List<level1Elm> datas = req.getRequest().getDatas();

                int mItem = 1;//原料项次自增长，不取前端

                List<String> materialWarehouseList = new ArrayList();
                List<String> materialPluNoList=new ArrayList<>();
                //新增单身（多笔）
                for (level1Elm par : datas) {
                    //【ID1023852】【詹记】门店分批出数作业调整
                    String pluNo = par.getPluNo();
                    String featureNo = par.getFeatureNo();
                    if (Check.Null(featureNo)) {
                        featureNo = " ";
                    }

                    if(Check.Null(par.getBatchNo())){
                        String batchNo = PosPub.getBatchNo(dao, eId, shopId, "", par.getPluNo(), par.getFeatureNo(), par.getProdDate(),"", req.getEmployeeNo(), req.getEmployeeName(), "");
                        par.setBatchNo(batchNo);
                    }

                    BigDecimal scrap_b = new BigDecimal("0");
                    BigDecimal pqty_b = new BigDecimal("0");

                    int insColCt = 0;
                    String[] columnsName = {
                            "PSTOCKINNO","SHOPID","ITEM","OITEM","PLUNO","PUNIT","PQTY","BASEUNIT","BASEQTY","UNIT_RATIO",
                            "PRICE","AMT","EID","ORGANIZATIONNO","TASK_QTY","SCRAP_QTY","MUL_QTY","BSNO",
                            "MEMO","GDATE","GTIME","WAREHOUSE",
                            "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","ACCOUNT_DATE","FEATURENO","LOCATION","EXPDATE","DISPTYPE","OOITEM","MINQTY","PRODTYPE","BOMNO","VERSIONNUM"
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
                                keyVal = par.getoItem();//oItem
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
                                keyVal = par.getgDate();
                                break;
                            case 20:
                                keyVal = par.getgTime();
                                break;
                            case 21:
                                keyVal =Check.Null(par.getWarehouse())? warehouse:par.getWarehouse();
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
                                keyVal = par.getDispType() ;
                                break;
                            case 31:
                                keyVal = par.getoOItem() ;
                                break;
                            case 32:
                                keyVal = par.getMinQty() ;
                                break;

                            case 33:
                                keyVal = par.getProdType() ;
                                if(Check.Null(keyVal)){
                                    keyVal=req.getRequest().getProdType();
                                }
                                break;

                            case 34:
                                keyVal = par.getBomNo() ;
                                break;
                            case 35:
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
                    List<level2Elm> material = par.getMaterial();
                    for (level2Elm mat : material) {

                        if(Check.Null(mat.getCostRate())){
                            List<Map<String, Object>> filterRows1 = bomMList.stream().filter(x -> x.get("MATERIAL_PLUNO").toString().equals(mat.getMaterial_pluNo())).collect(Collectors.toList());
                            if(filterRows1.size()>0){
                                mat.setCostRate(String.valueOf(filterRows1.get(0).get("COSTRATE")));
                            }
                        }

                        mat.setMaterial_item(String.valueOf(mItem));
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
                                    matKeyVal =mat.getMaterial_warehouse();
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

                        if(!materialWarehouseList.contains(mat.getMaterial_warehouse())){
                            materialWarehouseList.add(mat.getMaterial_warehouse());
                        }
                        if(!materialPluNoList.contains(mat.getMaterial_pluNo())){
                            materialPluNoList.add(mat.getMaterial_pluNo());
                        }

                    }
                    //新增原料单身到此为止
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
                        new DataValue(req.getRequest().getProdType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getoOType(), Types.VARCHAR),
                        new DataValue(req.getRequest().getoOfNo(), Types.VARCHAR),
                };
                InsBean ib1 = new InsBean("DCP_PSTOCKIN", columns1);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                boolean isNeedStockSync = false;//是否同步到三方
                ////分批出数创建即确认,类型==完工入库单 &&"0".equals(doc_Type) 不确定加不加
                if(status.equals("2")) {//新增是否会给2？

                    //if(!Check.Null(ofNO)) {
                        
                        /*
                        UptBean ub2 = new UptBean("DCP_PROCESSTASK");
                        //add Value
                        ub2.addUpdateValue("status", new DataValue("7", Types.VARCHAR));
                        ub2.addUpdateValue("Complete_Date", new DataValue(completeDate, Types.VARCHAR));
                        ub2.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                        //condition
                        ub2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub2.addCondition("PROCESSTASKNO", new DataValue(ofNO, Types.VARCHAR));
                        ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub2));
                        */

                    //}

                    //1.加入库存流水账信息
                    int item = 1;
                    mItem = 1;  //原料项次初始化
                    List<level1Elm> getDetail = req.getRequest().getDatas();
                    for (level1Elm oneData : getDetail) {
                        //报废不写库存流水   BY JZMA 20190802        有报废时录入数一定是零，报废单独一条记录
                        BigDecimal pqty_b = new BigDecimal("0"); //商品录入数
                        if (!Check.Null(oneData.getPqty()))
                            pqty_b =new BigDecimal(oneData.getPqty());

                        if(pqty_b.compareTo(new BigDecimal(0)) != 0 ) {
                            String procedure="SP_DCP_StockChange";
                            String featureNo = oneData.getFeatureNo();  ///特征码为空给空格
                            if (Check.Null(featureNo))
                                featureNo=" ";

                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("PStockInProcess");
                            bcReq.setDocType(req.getRequest().getDocType());
                            bcReq.setBillType("08");
                            //bcReq.setProdType(oneData.getpr);//组合作业没有  todo
                            bcReq.setDirection("1");
                            BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                            String bType = bcMap.getBType();
                            String costCode = bcMap.getCostCode();
                            if(Check.Null(bType)||Check.Null(costCode)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                            }

                            Map<Integer,Object> inputParameter = new HashMap<>();

                            inputParameter.put(1,eId);                         //--企业ID
                            inputParameter.put(2,shopId);                      //--组织
                            inputParameter.put(3,"08");                        //--单据类型
                            inputParameter.put(4,pStockInNO);	                 //--单据号
                            inputParameter.put(5,String.valueOf(item));        //--单据行号
                            inputParameter.put(6,"1");                         //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(7,bDate);                       //--营业日期 yyyy-MM-dd
                            inputParameter.put(8,oneData.getPluNo());          //--品号
                            inputParameter.put(9,featureNo);                   //--特征码
                            inputParameter.put(10,warehouse);                  //--仓库
                            inputParameter.put(11,oneData.getBatchNo());       //--批号
                            inputParameter.put(12,oneData.getPunit());         //--交易单位
                            inputParameter.put(13,oneData.getPqty());          //--交易数量
                            inputParameter.put(14,oneData.getBaseUnit());      //--基准单位
                            inputParameter.put(15,oneData.getBaseQty());       //--基准数量
                            inputParameter.put(16,oneData.getUnitRatio());     //--换算比例
                            inputParameter.put(17,oneData.getPrice());         //--零售价
                            inputParameter.put(18,oneData.getAmt());           //--零售金额
                            inputParameter.put(19,oneData.getDistriPrice());   //--进货价
                            inputParameter.put(20,oneData.getDistriAmt());     //--进货金额
                            inputParameter.put(21,accountDate);                //--入账日期 yyyy-MM-dd
                            inputParameter.put(22,oneData.getProdDate());      //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(23,bDate);                      //--单据日期
                            inputParameter.put(24,"");                         //--异动原因
                            inputParameter.put(25,oneData.getMemo());          //--异动描述
                            inputParameter.put(26,opNo);                   //--操作员

                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));
                            isNeedStockSync = true;//是否同步到三方
                            item++;
                        }

                        //******** 加上原料的库存流水  **********
                        List<level2Elm> getQDataMaterial = oneData.getMaterial();
                        for(level2Elm oneDataMaterial : getQDataMaterial) {
                            //非扣料件直接跳过
                            if(oneDataMaterial.getIsBuckle() ==null || oneDataMaterial.getIsBuckle().equals("Y")) {
                                String matFeatureNo = oneDataMaterial.getMaterial_featureNo();   ///特征码为空给空格
                                if (Check.Null(matFeatureNo))
                                    matFeatureNo=" ";
                                String procedure="SP_DCP_StockChange";
                                Map<Integer,Object> inputParameter = new HashMap<>();
                                inputParameter.put(1,eId);                                        //--企业ID
                                inputParameter.put(2,shopId);                                     //--组织
                                inputParameter.put(3,"11");                                       //--单据类型
                                inputParameter.put(4,pStockInNO);	                                //--单据号
                                inputParameter.put(5,String.valueOf(mItem));                      //--单据行号
                                inputParameter.put(6,"-1");                                       //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(7,bDate);                                      //--营业日期 yyyy-MM-dd
                                inputParameter.put(8,oneDataMaterial.getMaterial_pluNo());        //--品号
                                inputParameter.put(9,matFeatureNo);                               //--特征码
                                inputParameter.put(10,materialWarehouse);                         //--仓库
                                inputParameter.put(11,oneDataMaterial.getMaterial_batchNo());     //--批号
                                inputParameter.put(12,oneDataMaterial.getMaterial_punit());       //--交易单位
                                inputParameter.put(13,oneDataMaterial.getMaterial_pqty());        //--交易数量
                                inputParameter.put(14,oneDataMaterial.getMaterial_baseUnit());    //--基准单位
                                inputParameter.put(15,oneDataMaterial.getMaterial_baseQty());     //--基准数量
                                inputParameter.put(16,oneDataMaterial.getMaterial_unitRatio());   //--换算比例
                                inputParameter.put(17,oneDataMaterial.getMaterial_price());       //--零售价
                                inputParameter.put(18,oneDataMaterial.getMaterial_amt());         //--零售金额
                                inputParameter.put(19,oneDataMaterial.getMaterial_distriPrice()); //--进货价
                                inputParameter.put(20,oneDataMaterial.getMaterial_distriAmt());   //--进货金额
                                inputParameter.put(21,accountDate);                               //--入账日期 yyyy-MM-dd
                                inputParameter.put(22,oneDataMaterial.getMaterial_prodDate());    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(23,bDate);                                     //--单据日期
                                inputParameter.put(24,"");                                        //--异动原因
                                inputParameter.put(25,"");                                        //--异动描述
                                inputParameter.put(26,opNo);                                  //--操作员
                                if (status.equals("2") && isMaterialReplace.equals("Y"))//启用替代并且是分批出数
                                {
                                    continue;
                                }
                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));
                                isNeedStockSync = true;//是否同步到三方
                                mItem++;
                            }
                        }
                    }
                    if (status.equals("2") && isMaterialReplace.equals("Y"))//启用替代并且是分批出数,重新产生原料
                    {
                        List<Map<String, Object>> pstockinMaterial =new ArrayList<Map<String,Object>>();
                        pstockinMaterial=this.materialReplace(req);
                        if(pstockinMaterial!=null && pstockinMaterial.isEmpty()==false)
                        {
                            for (Map<String, Object> oneData : pstockinMaterial)
                            {
                                int insColCt2 = 0;
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
                                DataValue[] matColumnsVal = new DataValue[matColumnsName.length];
                                for (int j = 0; j < matColumnsVal.length; j++) {
                                    String matKeyVal = null;
                                    switch (j) {
                                        case 0:
                                            matKeyVal=oneData.get("MITEM").toString();
                                            break;
                                        case 1:
                                            matKeyVal   = oneData.get("ITEM").toString();
                                            break;
                                        case 2:
                                            matKeyVal   = oneData.get("WAREHOUSE").toString();
                                            break;
                                        case 3:
                                            matKeyVal   =oneData.get("materialPluNo").toString();
                                            break;
                                        case 4:
                                            matKeyVal = oneData.get("materialPunit").toString();
                                            break;
                                        case 5:
                                            matKeyVal = oneData.get("PQTY").toString();
                                            break;
                                        case 6:
                                            matKeyVal = oneData.get("PRICE").toString();
                                            if(Check.Null(matKeyVal))
                                            {
                                                matKeyVal="0";
                                            }
                                            break;
                                        case 7:
                                            matKeyVal = oneData.get("AMT").toString();
                                            break;
                                        case 8:
                                            //									matKeyVal = mat.getBaseQty();
                                            matKeyVal = oneData.get("BASE_QTY").toString();
                                            break;
                                        case 9:
                                            matKeyVal = oneData.get("MATERIAL_BASE_QTY").toString();
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
                                            matKeyVal = oneData.get("pluNo").toString();
                                            break;
                                        case 15:
                                            matKeyVal = oneData.get("WUNIT").toString();
                                            break;
                                        case 16:
                                            matKeyVal = oneData.get("WQTY").toString();
                                            break;
                                        case 17:
                                            matKeyVal = oneData.get("UNIT_RATIO").toString();
                                            break;
                                        case 18:
                                            matKeyVal = oneData.get("BATCH_NO").toString();
                                            break;
                                        case 19:
                                            matKeyVal = oneData.get("PROD_DATE").toString();
                                            break;
                                        case 20:
                                            matKeyVal = oneData.get("DISTRIPRICE").toString();
                                            break;
                                        case 21:
                                            matKeyVal =  oneData.get("DISTRIAMT").toString();
                                            if (Check.Null(matKeyVal)) matKeyVal="0";
                                            break;
                                        case 22:
                                            matKeyVal = accountDate;
                                            break;
                                        case 23:
                                            String isBuckle = oneData.get("ISBUCKLE").toString();
                                            if (Check.Null(isBuckle)||!isBuckle.equals("N"))
                                            {
                                                isBuckle="Y";
                                            }
                                            matKeyVal = isBuckle;
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
                                //原料库存流水
                                if(oneData.get("ISBUCKLE") ==null && oneData.get("ISBUCKLE").equals("Y")) {
                                    String matFeatureNo = oneData.getOrDefault("MATERIAL_FEATURENO", " ").toString();  ///特征码为空给空格
                                    if (Check.Null(matFeatureNo))
                                        matFeatureNo=" ";
                                    String procedure="SP_DCP_StockChange";
                                    Map<Integer,Object> inputParameter = new HashMap<>();
                                    inputParameter.put(1,eId);                                        //--企业ID
                                    inputParameter.put(2,shopId);                                     //--组织
                                    inputParameter.put(3,"11");                                       //--单据类型
                                    inputParameter.put(4,pStockInNO);	                                //--单据号
                                    inputParameter.put(5,String.valueOf(oneData.get("ITEM").toString()));                      //--单据行号
                                    inputParameter.put(6,"-1");                                       //--异动方向 1=加库存 -1=减库存
                                    inputParameter.put(7,bDate);                                      //--营业日期 yyyy-MM-dd
                                    inputParameter.put(8,oneData.get("materialPluNo").toString());        //--品号
                                    inputParameter.put(9,matFeatureNo);                               //--特征码
                                    inputParameter.put(10,materialWarehouse);                         //--仓库
                                    //inputParameter.put(11,oneDataMaterial.getMaterial_batchNo());     //--批号
                                    inputParameter.put(11,oneData.get("BATCH_NO").toString());
                                    inputParameter.put(12,oneData.get("materialPunit").toString());       //--交易单位
                                    inputParameter.put(13,oneData.get("PQTY").toString());            //--交易数量
                                    //inputParameter.put(14,oneDataMaterial.getMaterial_baseUnit());    //--基准单位
                                    inputParameter.put(14,oneData.get("WUNIT").toString());
                                    //inputParameter.put(15,oneDataMaterial.getMaterial_baseQty());     //--基准数量
                                    inputParameter.put(15,oneData.get("WQTY").toString());
                                    //inputParameter.put(16,oneDataMaterial.getMaterial_unitRatio());   //--换算比例
                                    inputParameter.put(16,oneData.get("UNIT_RATIO").toString());
                                    //inputParameter.put(17,oneDataMaterial.getMaterial_price());       //--零售价
                                    inputParameter.put(17,oneData.get("PRICE").toString());
                                    //inputParameter.put(18,oneDataMaterial.getMaterial_amt());         //--零售金额
                                    inputParameter.put(18,oneData.get("AMT").toString());
                                    //inputParameter.put(19,oneDataMaterial.getMaterial_distriPrice()); //--进货价
                                    inputParameter.put(19,oneData.get("DISTRIPRICE").toString());
                                    //inputParameter.put(20,oneDataMaterial.getMaterial_distriAmt());   //--进货金额
                                    inputParameter.put(20,oneData.get("DISTRIAMT").toString());
                                    inputParameter.put(21,accountDate);                               //--入账日期 yyyy-MM-dd
                                    //inputParameter.put(22,oneDataMaterial.getMaterial_prodDate());    //--批号的生产日期 yyyy-MM-dd
                                    inputParameter.put(22,oneData.get("PROD_DATE").toString());
                                    inputParameter.put(23,bDate);                                     //--单据日期
                                    inputParameter.put(24,oneData.get("BSNO").toString());                                        //--异动原因
                                    inputParameter.put(25,"");                                        //--异动描述
                                    inputParameter.put(26,opNo);                                  //--操作员
                                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                    this.addProcessData(new DataProcessBean(pdb));
                                    isNeedStockSync = true;//是否同步到三方
                                    mItem++;
                                }

                            }
                        }
                    }
                }

                if(doc_Type.equals("1")||doc_Type.equals("0")){

                    StringBuffer sJoinWarehouse=new StringBuffer("");
                    for (String thisWarehouse : materialWarehouseList){
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
                    for (String thisPluno : materialPluNoList){
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
                            " select a.*,to_char(a.PRODDATE,'yyyyMMdd') as proddatestr,to_char(a.VALIDDATE,'yyyyMMdd') as VALIDDATESTR " +
                            " from MES_BATCH_STOCK_DETAIL a " +
                            " inner join p on p.pluno=a.pluno "+
                            " where a.eid='"+req.geteId()+"' and a.qty>0 "
                            ;
                    List<Map<String, Object>> getStockData=this.doQueryData(stockSql, null);

                    List<DCP_PStockInCreateReq.StockInfo> stockInfos = getStockData.stream().map(x -> {
                        DCP_PStockInCreateReq.StockInfo stockInfo = req.new StockInfo();
                        stockInfo.setPluNo(x.get("PLUNO").toString());
                        stockInfo.setFeatureNo(x.get("FEATURENO").toString());
                        stockInfo.setWarehouse(x.get("WAREHOUSE").toString());
                        stockInfo.setBatchNo(x.get("BATCHNO").toString());
                        stockInfo.setLocation(x.get("LOCATION").toString());
                        stockInfo.setBaseUnit(x.get("BASEUNIT").toString());
                        stockInfo.setQty(x.get("QTY").toString());
                        stockInfo.setLockQty(x.get("LOCKQTY").toString());
                        stockInfo.setProdDate(x.get("PRODDATESTR").toString());
                        stockInfo.setValidDate(x.get("VALIDDATESTR").toString());
                        return stockInfo;
                    }).collect(Collectors.toList());

                    int batchItem=0;

                    for (level1Elm par : datas){
                        List<level2Elm> materials = par.getMaterial();
                        for (level2Elm oneDataMaterial : materials) {
                            String material_item = oneDataMaterial.getMaterial_item();
                            BigDecimal material_pqty = new BigDecimal(oneDataMaterial.getMaterial_pqty());
                            String material_warehouse = oneDataMaterial.getMaterial_warehouse();
                            String material_pluNo = oneDataMaterial.getMaterial_pluNo();
                            List<DCP_PStockInCreateReq.level3Elm> materialBatchList = oneDataMaterial.getMaterialBatchList();
                            if(CollUtil.isNotEmpty(materialBatchList)){
                                for (DCP_PStockInCreateReq.level3Elm oneMb : materialBatchList){
                                    ColumnDataValue mbColumns=new ColumnDataValue();
                                    mbColumns.add("EID", DataValues.newString(eId));
                                    mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                    mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                    mbColumns.add("ITEM",DataValues.newString(batchItem++));
                                    mbColumns.add("OITEM",DataValues.newString(material_item));
                                    mbColumns.add("PLUNO",DataValues.newString(oneDataMaterial.getMaterial_pluNo()));
                                    mbColumns.add("FEATURENO",DataValues.newString(oneDataMaterial.getMaterial_featureNo()));
                                    mbColumns.add("PUNIT",DataValues.newString(oneMb.getPUnit()));
                                    mbColumns.add("PQTY",DataValues.newString(oneMb.getPQty()));
                                    mbColumns.add("BASEUNIT",DataValues.newString(oneMb.getBaseUnit()));
                                    mbColumns.add("BASEQTY",DataValues.newString(oneMb.getBaseQty()));
                                    mbColumns.add("UNIT_RATIO",DataValues.newString(oneMb.getUnitRatio()));

                                    mbColumns.add("WAREHOUSE",DataValues.newString(oneDataMaterial.getMaterial_warehouse()));
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

                            List<Map<String, Object>> singlePluInfos = getPluData.stream().filter(x -> x.get("PLUNO").toString().equals(oneDataMaterial.getMaterial_pluNo())).collect(Collectors.toList());
                            if(CollUtil.isNotEmpty(singlePluInfos)){
                                String isBatch = singlePluInfos.get(0).get("ISBATCH").toString();
                                String batchSortType = singlePluInfos.get(0).get("BATCHSORTTYPE").toString();
                                if("Y".equals(isBatch)&&"Y".equals(isBatchNo)){
                                    List<DCP_PStockInCreateReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(material_pluNo) && x.getWarehouse().equals(material_warehouse)).collect(Collectors.toList());
                                    if("1".equals(batchSortType)){
                                        singlePluStock.sort(Comparator.comparing(x -> x.getProdDate()));
                                    }
                                    if("2".equals(batchSortType)){
                                        singlePluStock.sort(Comparator.comparing(x -> x.getValidDate()));
                                    }

                                    //int detailItem=0;
                                    for(DCP_PStockInCreateReq.StockInfo oneStock : singlePluStock){
                                        //detailItem++;
                                        BigDecimal stockQty= new BigDecimal(oneStock.getQty());
                                        if(material_pqty.compareTo(BigDecimal.ZERO)<=0){
                                            continue;//分完了
                                        }
                                        BigDecimal fpQty=new BigDecimal("0");
                                        if(stockQty.compareTo(material_pqty)>=0){
                                            fpQty=material_pqty;
                                        }else{
                                            fpQty=stockQty;
                                        }
                                        if(fpQty.compareTo(BigDecimal.ZERO)<=0){
                                            continue;
                                        }

                                        material_pqty=material_pqty.subtract(fpQty);

                                        Integer pUnitUdlength = PosPub.getUnitUDLength(dao, eId, oneDataMaterial.getMaterial_punit());
                                        BigDecimal thisPQty = fpQty.divide(new BigDecimal(oneDataMaterial.getMaterial_unitRatio()), pUnitUdlength);

                                        //存表
                                        ColumnDataValue mbColumns=new ColumnDataValue();
                                        mbColumns.add("EID", DataValues.newString(eId));
                                        mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                        mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                        mbColumns.add("ITEM",DataValues.newString(batchItem++));
                                        mbColumns.add("OITEM",DataValues.newString(material_item));
                                        mbColumns.add("PLUNO",DataValues.newString(oneDataMaterial.getMaterial_pluNo()));
                                        mbColumns.add("FEATURENO",DataValues.newString(oneDataMaterial.getMaterial_featureNo()));
                                        mbColumns.add("PUNIT",DataValues.newString(oneDataMaterial.getMaterial_punit()));
                                        mbColumns.add("PQTY",DataValues.newString(thisPQty));
                                        mbColumns.add("BASEUNIT",DataValues.newString(oneDataMaterial.getMaterial_baseUnit()));
                                        mbColumns.add("BASEQTY",DataValues.newString(fpQty.toString()));
                                        mbColumns.add("UNIT_RATIO",DataValues.newString(oneDataMaterial.getMaterial_unitRatio()));

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
                                        for (DCP_PStockInCreateReq.StockInfo oldInfo :stockInfos){
                                            if(oldInfo.getPluNo().equals(oneStock.getPluNo())&&oldInfo.getWarehouse().equals(oneStock.getWarehouse())&&
                                                    oldInfo.getBatchNo().equals(oneStock.getBatchNo())&&oldInfo.getLocation().equals(oneStock.getLocation())){
                                                oldInfo.setQty(String.valueOf(new BigDecimal(oldInfo.getQty()).subtract(fpQty)));
                                            }
                                        }
                                    }


                                }
                                else{
                                    //仓库
                                    List<Map<String, Object>> warehouseInfos = getWarehouseData.stream().filter(x -> x.get("WAREHOUSE").toString().equals(oneDataMaterial.getMaterial_warehouse())).collect(Collectors.toList());
                                    if(CollUtil.isNotEmpty(warehouseInfos)){
                                        String islocation = warehouseInfos.get(0).get("ISLOCATION").toString();
                                        if("N".equals(islocation)){
                                            ColumnDataValue mbColumns=new ColumnDataValue();
                                            mbColumns.add("EID", DataValues.newString(eId));
                                            mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                            mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                            mbColumns.add("ITEM",DataValues.newString(batchItem++));
                                            mbColumns.add("OITEM",DataValues.newString(material_item));
                                            mbColumns.add("PLUNO",DataValues.newString(oneDataMaterial.getMaterial_pluNo()));
                                            mbColumns.add("FEATURENO",DataValues.newString(oneDataMaterial.getMaterial_featureNo()));
                                            mbColumns.add("PUNIT",DataValues.newString(oneDataMaterial.getMaterial_punit()));
                                            mbColumns.add("PQTY",DataValues.newString(oneDataMaterial.getMaterial_pqty()));
                                            mbColumns.add("BASEUNIT",DataValues.newString(oneDataMaterial.getMaterial_baseUnit()));
                                            mbColumns.add("BASEQTY",DataValues.newString(oneDataMaterial.getMaterial_baseQty()));
                                            mbColumns.add("UNIT_RATIO",DataValues.newString(oneDataMaterial.getMaterial_unitRatio()));

                                            mbColumns.add("WAREHOUSE",DataValues.newString(oneDataMaterial.getMaterial_warehouse()));
                                            mbColumns.add("LOCATION",DataValues.newString(Check.Null(oneDataMaterial.getMaterial_location())?" ":oneDataMaterial.getMaterial_location()));
                                            mbColumns.add("BATCHNO",DataValues.newString(Check.Null(oneDataMaterial.getMaterial_batchNo())?" ":oneDataMaterial.getMaterial_batchNo()));
                                            mbColumns.add("PRODDATE",DataValues.newString(oneDataMaterial.getMaterial_prodDate()));
                                            mbColumns.add("EXPDATE",DataValues.newString(oneDataMaterial.getMaterial_expDate()));


                                            String[] mbColumnNames = mbColumns.getColumns().toArray(new String[0]);
                                            DataValue[] mbDataValues = mbColumns.getDataValues().toArray(new DataValue[0]);
                                            InsBean ibmb=new InsBean("DCP_PSTOCKOUT_BATCH",mbColumnNames);
                                            ibmb.addValues(mbDataValues);
                                            this.addProcessData(new DataProcessBean(ibmb));

                                        }
                                        else {
                                            //int detailItem=0;
                                            List<DCP_PStockInCreateReq.StockInfo> singlePluStock = stockInfos.stream().filter(x -> x.getPluNo().equals(material_pluNo) && x.getWarehouse().equals(material_warehouse)).sorted(Comparator.comparing(x -> x.getLocation())).collect(Collectors.toList());
                                            for(DCP_PStockInCreateReq.StockInfo oneStock : singlePluStock){
                                                //detailItem++;
                                                BigDecimal stockQty= new BigDecimal(oneStock.getQty());
                                                if(material_pqty.compareTo(BigDecimal.ZERO)<=0){
                                                    continue;//分完了
                                                }
                                                BigDecimal fpQty=new BigDecimal("0");
                                                if(stockQty.compareTo(material_pqty)>=0){
                                                    fpQty=material_pqty;
                                                }else{
                                                    fpQty=stockQty;
                                                }

                                                if(fpQty.compareTo(BigDecimal.ZERO)<=0){
                                                    continue;
                                                }
                                                material_pqty=material_pqty.subtract(fpQty);

                                                Integer pUnitUdlength = PosPub.getUnitUDLength(dao, eId, oneDataMaterial.getMaterial_punit());
                                                BigDecimal thisPQty = fpQty.divide(new BigDecimal(oneDataMaterial.getMaterial_unitRatio()), pUnitUdlength);

                                                //存表
                                                ColumnDataValue mbColumns=new ColumnDataValue();
                                                mbColumns.add("EID", DataValues.newString(eId));
                                                mbColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                                mbColumns.add("PSTOCKINNO", DataValues.newString(pStockInNO));
                                                mbColumns.add("ITEM",DataValues.newString(batchItem++));
                                                mbColumns.add("OITEM",DataValues.newString(material_item));
                                                mbColumns.add("PLUNO",DataValues.newString(oneDataMaterial.getMaterial_pluNo()));
                                                mbColumns.add("FEATURENO",DataValues.newString(oneDataMaterial.getMaterial_featureNo()));
                                                mbColumns.add("PUNIT",DataValues.newString(oneDataMaterial.getMaterial_punit()));
                                                mbColumns.add("PQTY",DataValues.newString(thisPQty));
                                                mbColumns.add("BASEUNIT",DataValues.newString(oneDataMaterial.getMaterial_baseUnit()));
                                                mbColumns.add("BASEQTY",DataValues.newString(fpQty.toString()));
                                                mbColumns.add("UNIT_RATIO",DataValues.newString(oneDataMaterial.getMaterial_unitRatio()));

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
                                                for (DCP_PStockInCreateReq.StockInfo oldInfo :stockInfos){
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
                }

                this.doExecuteDataToDB();

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
                //把单号返回
                res.setPStockInNo(pStockInNO);

                //***********调用库存同步给三方，这是个异步，不会影响效能*****************
                try
                {
                    if(isNeedStockSync)
                    {
                        WebHookService.stockSync(eId,shopId,pStockInNO);
                    }

                }
                catch (Exception ignored)
                {

                }

            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
            }

       // } catch(Exception e) {
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
       // }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PStockInCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PStockInCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PStockInCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PStockInCreateReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        //必传值不为空
        String status = req.getRequest().getStatus();
        String pStockInID = req.getRequest().getpStockInID();
        String materialWarehouse = req.getRequest().getMaterialWarehouseNo();
        String warehouse = req.getRequest().getWarehouse();
        String docType = req.getRequest().getDocType();
        String totPqty = req.getRequest().getTotPqty();
        String totAmt = req.getRequest().getTotAmt();
        String totDistriAmt=req.getRequest().getTotDistriAmt();
        String totCqty = req.getRequest().getTotCqty();
        String bDate = req.getRequest().getbDate();
        List<level1Elm> datas = req.getRequest().getDatas();

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
        //if(Check.Null(warehouse)||warehouse.equals(" ")){
        //    errMsg.append("成品仓库不可为空值或空格, ");
        //    isFail = true;
        //}
        //if(Check.Null(materialWarehouse)||materialWarehouse.equals(" ")){
        //    errMsg.append("原料仓库不可为空值或空格, ");
        //    isFail = true;
        //}
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
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "成品单身不能为空!");
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
            for(level1Elm par:datas) {
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
                List<level2Elm> material = par.getMaterial();

                if (Check.Null(par.getItem())) {
                    errMsg.append("项次不可为空值, ");
                    isFail = true;
                }
                if (Check.Null(par.getoItem())) {
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
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                }

                for (level2Elm materialPar : material) {
                    String mItem = materialPar.getmItem();
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
                    List<DCP_PStockInCreateReq.level3Elm> materialBatchList = materialPar.getMaterialBatchList();
                    if (mItem == null) {
                        errMsg.append("原料" + material_pluNo + "主项次不可为空值, ");
                        isFail = true;
                    }
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

                    if(CollUtil.isNotEmpty(materialBatchList)){
                        for (DCP_PStockInCreateReq.level3Elm materialBatch : materialBatchList) {
                            if(Check.Null(materialBatch.getItem())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表项次不可为空值, ");
                            }
                            if(Check.Null(materialBatch.getOItem())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表原料项次不可为空值, ");
                            }
                            if(Check.Null(materialBatch.getLocation())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表库位编号不可为空值, ");
                            }
                            if(Check.Null(materialBatch.getBatchNo())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表批号不可为空值, ");
                            }
                            //if(Check.Null(materialBatch.getProdDate())){
                            //    isFail=true;
                            //    errMsg.append("原料" + material_pluNo + "批号列表生产日期不可为空值, ");
                            //}
                            //if(Check.Null(materialBatch.getExpDate())){
                            //    isFail=true;
                            //    errMsg.append("原料" + material_pluNo + "批号列表有效日期不可为空值, ");
                            //}
                            if(Check.Null(materialBatch.getPUnit())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表原料单位不可为空值, ");
                            }
                            if(Check.Null(materialBatch.getPQty())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表原料数量不可为空值, ");
                            }
                            if(Check.Null(materialBatch.getBaseUnit())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表原料基础单位不可为空值, ");
                            }
                            if(Check.Null(materialBatch.getBaseQty())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表原料基础数量不可为空值, ");
                            }
                            if(Check.Null(materialBatch.getUnitRatio())){
                                isFail=true;
                                errMsg.append("原料" + material_pluNo + "批号列表单位与基准单位的换算率不可为空值, ");
                            }

                        }
                    }

                    if (isFail) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
                    }
                }

            }
        }

        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return false;
    }

    @Override
    protected TypeToken<DCP_PStockInCreateReq> getRequestType() {
        return new TypeToken<DCP_PStockInCreateReq>(){};
    }

    @Override
    protected DCP_PStockInCreateRes getResponseType() {
        return new DCP_PStockInCreateRes();
    }

    @Override
    protected String getQuerySql(DCP_PStockInCreateReq req) throws Exception {
        return null ;
    }

    private String getPStockInNO(DCP_PStockInCreateReq req) throws Exception  {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        String pStockInNO = null;
        String shopId = req.getShopId();
        String eId = req.geteId();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        StringBuffer sqlbuf = new StringBuffer();
        String docType = req.getRequest().getDocType(); //0-完工入库  1-组合单   2-拆解单  3-转换合并  4-转换拆解
        //新增服务时  docType是1的时候 单号开头字母换成ZHRK；2的时候换成CJCK
        switch (docType) {
            case "0":
                pStockInNO = "WGRK" + bDate;
                break;
            case "1":
                pStockInNO = "ZHRK" + bDate;
                break;
            case "2":
                pStockInNO = "CJCK" + bDate;
                break;
            // 2019-05-29 若docType==3， 转换合并单，
            case "3":
                pStockInNO = "ZHHB" + bDate;
                break;
            // 2019-08-19 若docType==4， 转换拆解单据，
            case "4":
                pStockInNO = "ZHCJ" + bDate;
                break;
        }
        sqlbuf.append(""
                              + " select max(pstockinno) as pstockinno "
                              + " from dcp_pstockin where eid = '"+eId+"' and shopid = '"+shopId+"' "
                              + " and pstockinno like '%%" + pStockInNO + "%%' "); // 假資料

        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);
        if (getQData != null && !getQData.isEmpty()) {
            pStockInNO = getQData.get(0).get("PSTOCKINNO").toString();
            if (pStockInNO != null && pStockInNO.length() > 0) {
                long i;
                pStockInNO = pStockInNO.substring(4);
                i = Long.parseLong(pStockInNO) + 1;
                pStockInNO = i + "";

                switch (docType) {
                    case "0":
                        pStockInNO = "WGRK" + pStockInNO; //完工入库0
                        break;
                    case "1":
                        pStockInNO = "ZHRK" + pStockInNO; //组合入库1
                        break;
                    case "2":
                        pStockInNO = "CJCK" + pStockInNO; //拆解出库2
                        break;
                    case "3":
                        pStockInNO = "ZHHB" + pStockInNO; //转换合并3
                        break;
                    // 2019-08-19 若docType==4， 转换拆解单据，
                    case "4":
                        pStockInNO = "ZHCJ" + pStockInNO;
                        break;
                }
            }
            else {
                switch (docType) {
                    case "0":
                        pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
                        break;
                    case "1":
                        pStockInNO = "ZHRK" + bDate + "00001"; //组合入库1
                        break;
                    case "2":
                        pStockInNO = "CJCK" + bDate + "00001"; //拆解出库2
                        break;
                    case "3":
                        pStockInNO = "ZHHB" + bDate + "00001"; //拆解出库2
                        break;
                    // 2019-08-19 若docType==4， 转换拆解单据，
                    case "4":
                        pStockInNO = "ZHCJ" + bDate + "00001";
                        break;
                }
            }
        } else {
            switch (docType) {
                case "0":
                    pStockInNO = "WGRK" + bDate + "00001"; //完工入库0
                    break;
                case "1":
                    pStockInNO = "ZHRK" + bDate + "00001"; //组合入库1
                    break;
                case "2":
                    pStockInNO = "CJCK" + bDate + "00001"; //拆解出库2
                    break;
                case "3":
                    pStockInNO = "ZHHB" + bDate + "00001"; //转换合并3
                    break;
                // 2019-08-19 若docType==4， 转换拆解单据，
                case "4":
                    pStockInNO = "ZHCJ" + bDate + "00001"; //转换拆解4
                    break;
            }
        }

        return pStockInNO;
    }

    private boolean checkGuid(DCP_PStockInCreateReq req) throws Exception {
        String guid = req.getRequest().getpStockInID();
        boolean existGuid=false;
        String sql= "select pstockin_id from dcp_pstockin where pstockin_id = '"+guid+"' ";
        List<Map<String, Object>> getQData = this.doQueryData(sql,null);
        if (getQData != null && !getQData.isEmpty()) {
            existGuid = true;
        }
        return existGuid;
    }

    private List<Map<String, Object>>  materialReplace(DCP_PStockInCreateReq req) throws Exception
    {
        List<Map<String, Object>> pstockinMaterial =new ArrayList<Map<String,Object>>();
        //List<Map<String, Object>> stocklist =new ArrayList<Map<String,Object>>();
        List<level1Elm> datas = req.getRequest().getDatas();

        for (level1Elm par : datas)
        {
            List<level2Elm> material = par.getMaterial();
            for (level2Elm mat : material)
            {
                Map<String, Object> mappstockinMaterial=new HashMap<String, Object>();
                mappstockinMaterial.put("pluNo", par.getPluNo());
                mappstockinMaterial.put("materialPluNo",mat.getMaterial_pluNo());
                mappstockinMaterial.put("materialPunit", mat.getMaterial_punit());
                mappstockinMaterial.put("PQTY", mat.getMaterial_pqty());
                List<Map<String, Object>> getQData = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),mat.getMaterial_pluNo(),mat.getMaterial_punit());
                String materialwUnit=null;
                BigDecimal materialunitRatio=new BigDecimal(0);
                if (getQData != null && getQData.isEmpty() == false)
                {
                    materialunitRatio = (BigDecimal) getQData.get(0).get("UNIT_RATIO");
                    materialwUnit = (String) getQData.get(0).get("WUNIT");
                    if (materialunitRatio.compareTo(BigDecimal.valueOf(0)) == 0)
                    {
                        //;
                    }
                    else
                    {
                        //;
                    }
                }
                else
                {
                    //;
                }
                mappstockinMaterial.put("UNIT_RATIO",materialunitRatio);
                mappstockinMaterial.put("WUNIT",materialwUnit);
                mappstockinMaterial.put("WQTY",String.valueOf(BigDecimal.valueOf(Float.parseFloat(mat.getMaterial_pqty())).multiply(materialunitRatio)));
                mappstockinMaterial.put("MITEM", par.getItem().toString());
                mappstockinMaterial.put("WAREHOUSE",mat.getMaterial_warehouse());
                mappstockinMaterial.put("BDATE", req.getRequest().getbDate());
                mappstockinMaterial.put("OTYPE", req.getRequest().getoType());
                mappstockinMaterial.put("OFNO", req.getRequest().getOfNo());
                mappstockinMaterial.put("MEMO", req.getRequest().getMemo());
                mappstockinMaterial.put("OITEM", "");
                mappstockinMaterial.put("LOAD_DOCTYPE", "");
                mappstockinMaterial.put("LOAD_DOCNO", "");
                mappstockinMaterial.put("BSNO", "");
                mappstockinMaterial.put("SCRAP_QTY", "");
                String batch_no=mat.getMaterial_batchNo();
                if(Check.Null(batch_no))
                {
                    batch_no=" ";
                }
                mappstockinMaterial.put("BATCH_NO",batch_no);
                if(Check.Null(mat.getMaterial_prodDate()))
                {
                    mappstockinMaterial.put("PROD_DATE","");
                }else
                {
                    mappstockinMaterial.put("PROD_DATE",mat.getMaterial_prodDate());
                }
                mappstockinMaterial.put("ISBUCKLE",mat.getIsBuckle());
                //mappstockinMaterial.put("BASE_QTY",mat.getBaseQty());
                mappstockinMaterial.put("MATERIAL_BASE_QTY",mat.getMaterial_baseQty());

                pstockinMaterial.add(mappstockinMaterial);
            }
        }
        //	PosPub.GoodsMaterialReplace(dao, req.getCompanyNo(), req.getShop(), pstockinMaterial, "PluNo", "Punit", "Pqty", "Wunit", "UnitRatio", "MaterialPluNo", "MaterialPunit", "MaterialPqty", "MaterialWunit", "MaterialWarehouse", "defMaterialWarehouse");

        PosPub.MaterialReplace(dao,req.geteId(),req.getBELFIRM(), req.getShopId(),pstockinMaterial,
                               "pluNo","materialPunit","materialPluNo","WUNIT","WAREHOUSE");


        return pstockinMaterial;
    }

}
