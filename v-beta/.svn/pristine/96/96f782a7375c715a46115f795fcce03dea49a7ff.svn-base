package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutRefundCreateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockOutRefundCreateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DCP_StockOutRefundCreate extends SPosAdvanceService<DCP_StockOutRefundCreateReq, DCP_StockOutRefundCreateRes>
{
    
    @Override
    protected void processDUID(DCP_StockOutRefundCreateReq req, DCP_StockOutRefundCreateRes res) throws Exception
    {
        try {
            DCP_StockOutRefundCreateReq.levelElm request = req.getRequest();
            if (checkGuid(req) == false){
                String shopId = req.getShopId();
                String organizationNO = req.getOrganizationNO();
                String eId = req.geteId();
                String bDate = request.getbDate();
                String memo = request.getMemo();
                String status = request.getStatus();
                String docType = request.getDocType();
                String oType = request.getoType();
                String ofNO = request.getOfNo();
                String receiptOrg = request.getReceiptOrg();
                String createBy = req.getOpNO();
                Calendar cal = Calendar.getInstance();//获得当前时间
                SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
                String createDate = df.format(cal.getTime());
                df=new SimpleDateFormat("HHmmss");
                String createTime = df.format(cal.getTime());
                String loadDocType = request.getLoadDocType();
                String loadDocNO = request.getLoadDocNo();
                String stockOutNO = getStockOutNONew(req);
                String totPqty = request.getTotPqty();
                String totAmt =request.getTotAmt();
                String totDistriAmt = request.getTotDistriAmt();
                String totCqty = request.getTotCqty();
                String sourceMenu = request.getSourceMenu();  //	0其他出库单  1试吃出库单  2赠送出库单

                String employeeId = request.getEmployeeId();
                String deaprtId = request.getDeaprtId();

                //2019-12-19 增加模板编码
                String pTemplateNo = "";
                pTemplateNo = request.getpTemplateNo();

                String deliveryNO = request.getDeliveryNo();
                String transferShop = request.getTransferShop();
                String stockOutID = request.getStockOutID();

                String bsNO = Check.Null(request.getBsNo())?"":request.getBsNo();

                String warehouse = request.getWarehouse();
                String transferWarehouse = request.getTransferWarehouse();

                String corp="";
                String receiptCorp="";
                String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ";
                String orgSql2="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+request.getReceiptOrg()+"' ";
                List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
                if(CollUtil.isNotEmpty(orgList1)){
                    corp = orgList1.get(0).get("CORP").toString();
                }
                List<Map<String, Object>> orgList2 = this.doQueryData(orgSql2, null);
                if(CollUtil.isNotEmpty(orgList2)){
                    receiptCorp = orgList2.get(0).get("CORP").toString();
                }

                String[] columns1 = {
                        "SHOPID", "OrganizationNO","EID","stockOutNO","BDate", "MEMO",  "Status",
                        "DOC_TYPE","oType","ofNO","CreateBy", "Create_Date", "Create_time","TOT_PQTY",
                        "TOT_AMT", "TOT_CQTY", "LOAD_DOCTYPE", "LOAD_DOCNO","DELIVERY_NO","transfer_shop",
                        "stockOut_ID","bsNO","receipt_org","WAREHOUSE","TRANSFER_WAREHOUSE","TOT_DISTRIAMT",
                        "PTEMPLATENO","SOURCEMENU","CREATE_CHATUSERID","UPDATE_TIME","TRAN_TIME",
                        "STOCKOUTNO_ORIGIN","EMPLOYEEID","DEPARTID","CORP","RECEIPTCORP"
                };

                DataValue[] insValue1 = null;
                //新增單身 (多筆)
                List<DCP_StockOutRefundCreateReq.level1Elm> jsonDatas = request.getDatas();
                for (DCP_StockOutRefundCreateReq.level1Elm par : jsonDatas) {
                    int insColCt = 0;
                    String[] columnsName = {
                            "STOCKOUTNO", "SHOPID", "item", "oItem", "pluNO",
                            "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio", "PLU_BARCODE",
                            "price", "amt", "EID", "organizationNO", "WAREHOUSE","BSNO"
                            ,"PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO","STOCKQTY"
                            ,"ITEM_ORIGIN","PQTY_ORIGIN","MES_LOCATION","EXPDATE"
                    };

                    DataValue[] columnsVal = new DataValue[columnsName.length];
                    for (int i = 0; i < columnsVal.length; i++) {
                        String keyVal = null;
                        switch (i) {
                            case 0:
                                keyVal = stockOutNO;
                                break;
                            case 1:
                                keyVal = shopId;
                                break;
                            case 2:
                                keyVal = par.getItem(); //item
                                break;
                            case 3:
                                String oItem = par.getoItem();
                                if (oItem.length() == 0){
                                    oItem="0";
                                }
                                keyVal = oItem; //oItem
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
                                keyVal = par.getPluBarcode();    //pluBarcode
                                break;
                            case 11:
                                keyVal = par.getPrice();    //price
                                if(par.getPrice()==null || par.getPrice().isEmpty()){
                                    keyVal = "0";
                                }
                                break;
                            case 12:
                                keyVal = par.getAmt();    //amt
                                break;
                            case 13:
                                keyVal = eId;
                                break;
                            case 14:
                                keyVal = organizationNO;
                                break;
                            case 15:
                                keyVal = request.getWarehouse();
                                break;
                            case 16:
                                if(!Check.Null(docType) && docType.equals("3") &&
                                        !Check.Null(sourceMenu) &&
                                        (sourceMenu.equals("0") ||
                                                sourceMenu.equals("1") ||
                                                sourceMenu.equals("2"))  )
                                {
                                    keyVal = bsNO;
                                }else
                                {
                                    keyVal = par.getBsNo();
                                }
                                break;
                            case 17:
                                if(par.getPluMemo() == null){
                                    keyVal = "";
                                }
                                else{
                                    keyVal = par.getPluMemo();
                                }
                                break;
                            case 18:
                                keyVal = par.getBatchNo();
                                break;
                            case 19:
                                keyVal = par.getProdDate();
                                break;
                            case 20:
                                if(par.getDistriPrice()==null || Check.Null(par.getDistriPrice()) ){
                                    keyVal = "0";
                                }
                                else {
                                    keyVal=par.getDistriPrice();
                                }
                                break;
                            case 21:
                                keyVal = par.getDistriAmt();
                                if (Check.Null(keyVal))
                                    keyVal="0";
                                break;
                            case 22:
                                keyVal = bDate;
                                break;
                            case 23:
                                keyVal = par.getFeatureNo();
                                if (Check.Null(keyVal))
                                    keyVal = " ";
                                break;
                            case 24:
                                keyVal = par.getStockqty();
                                if (PosPub.isNumericTypeMinus(keyVal)==false)
                                    keyVal = "999999";
                                break;
                            case 25:
                                keyVal = par.getItem_origin();
                                break;
                            case 26:
                                keyVal = par.getPqty_origin();
                                break;
                            case 27:
                                keyVal = par.getLocation();
                                if(Check.Null(keyVal)){
                                    keyVal=" ";
                                }
                                break;
                            case 28:
                                keyVal = par.getExpDate();
                                break;
                            default:
                                break;
                        }

                        if (keyVal != null) {
                            insColCt++;
                            if (i == 2 || i == 3 ){
                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                            }else if (i == 6 || i == 8 || i == 9 || i == 11 || i == 12 || i==20|| i==24 ){
                                columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                            }else{
                                columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                            }

                        } else {
                            columnsVal[i] = null;
                        }
                    }
                    String[]    columns2  = new String[insColCt];
                    DataValue[] insValue2 = new DataValue[insColCt];
                    // 依照傳入參數組譯要insert的欄位與數值；
                    insColCt = 0;

                    for (int i=0;i<columnsVal.length;i++){
                        if (columnsVal[i] != null){
                            columns2[insColCt] = columnsName[i];
                            insValue2[insColCt] = columnsVal[i];
                            insColCt ++;
                            if (insColCt >= insValue2.length) break;
                        }
                    }

                    InsBean ib2 = new InsBean("DCP_STOCKOUT_DETAIL", columns2);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));


                    //更新原单的明细已红冲数量
                    UptBean ub_DCP_STOCKOUT_DETAIL = new UptBean("DCP_STOCKOUT_DETAIL");
                    ub_DCP_STOCKOUT_DETAIL.addUpdateValue("PQTY_REFUND", new DataValue(par.getPqty(), Types.VARCHAR));
                    // condition
                    ub_DCP_STOCKOUT_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_STOCKOUT_DETAIL.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub_DCP_STOCKOUT_DETAIL.addCondition("STOCKOUTNO", new DataValue(req.getRequest().getStockOutNo_origin(), Types.VARCHAR));
                    ub_DCP_STOCKOUT_DETAIL.addCondition("ITEM", new DataValue(par.getItem_origin(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub_DCP_STOCKOUT_DETAIL));

                }
                //收货组织
                if(transferShop==null ||transferShop.trim().length()==0){
                    transferShop=receiptOrg;
                }
                // STOCKOUT查询的时候用transferShop关联组织表，如果不赋值会查询不出资料 BY JZMA 20200107
                if (Check.Null(transferShop) && docType.equals("3")) {
                    transferShop = shopId;
                }

                //【ID1032371】//[饰一派3.0]门店1008，3月28日做退货出库单（THCK2023032800001），收货组织选的是SITE-08义乌之源，报文是1 by jinzma 20230406
                if (docType.equals("0")){
                    transferShop = receiptOrg;
                }

                insValue1 = new DataValue[]{
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(organizationNO, Types.VARCHAR),
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(stockOutNO, Types.VARCHAR),
                        new DataValue(bDate, Types.VARCHAR),
                        new DataValue(memo, Types.VARCHAR),
                        new DataValue(status, Types.VARCHAR),
                        new DataValue(docType, Types.VARCHAR),
                        new DataValue(oType, Types.VARCHAR),
                        new DataValue(ofNO, Types.VARCHAR),
                        new DataValue(createBy, Types.VARCHAR),
                        new DataValue(createDate, Types.VARCHAR),
                        new DataValue(createTime, Types.VARCHAR),
                        new DataValue(totPqty, Types.VARCHAR),
                        new DataValue(totAmt, Types.VARCHAR),
                        new DataValue(totCqty, Types.VARCHAR),
                        new DataValue(loadDocType, Types.VARCHAR),
                        new DataValue(loadDocNO, Types.VARCHAR),
                        new DataValue(deliveryNO, Types.VARCHAR),
                        new DataValue(transferShop, Types.VARCHAR),
                        new DataValue(stockOutID, Types.VARCHAR),
                        new DataValue(bsNO, Types.VARCHAR),
                        new DataValue(receiptOrg, Types.VARCHAR),
                        new DataValue(warehouse, Types.VARCHAR),
                        new DataValue(transferWarehouse, Types.VARCHAR),
                        new DataValue(totDistriAmt, Types.VARCHAR),
                        new DataValue(pTemplateNo, Types.VARCHAR),
                        new DataValue(sourceMenu, Types.VARCHAR),
                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        new DataValue(req.getRequest().getStockOutNo_origin(), Types.VARCHAR),
                        new DataValue(employeeId, Types.VARCHAR),
                        new DataValue(deaprtId, Types.VARCHAR),
                        new DataValue(corp, Types.VARCHAR),
                        new DataValue(receiptCorp, Types.VARCHAR),
                };

                InsBean ib1 = new InsBean("DCP_STOCKOUT", columns1);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭


                //更新原单的退单号
                UptBean ub_DCP_STOCKOUT = new UptBean("DCP_STOCKOUT");
                ub_DCP_STOCKOUT.addUpdateValue("STOCKOUTNO_REFUND", new DataValue(stockOutNO, Types.VARCHAR));
                // condition
                ub_DCP_STOCKOUT.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub_DCP_STOCKOUT.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub_DCP_STOCKOUT.addCondition("STOCKOUTNO", new DataValue(req.getRequest().getStockOutNo_origin(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub_DCP_STOCKOUT));



                this.doExecuteDataToDB();
                res.setStockoutNo(stockOutNO);

                if(!corp.equals(receiptCorp)){
                    //单据类型(0退货出库 1调拨出库 3其他出库 4移仓出库 5配送出库)
                    //配货出库
                    if("5".equals(docType)) {

                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockOutNO);
                        request1.setSupplyOrgNo(req.getOrganizationNO());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10003.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockOutRefundCreateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getRequest().getReceiptOrg());
                            detail.setSourceBillNo(req.getRequest().getOfNo());
                            detail.setSourceItem(par.getoItem());
                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);
                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }

                    if("0".equals(docType)){

                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockOutNO);
                        request1.setSupplyOrgNo(req.getRequest().getReceiptOrg());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType_10003.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockOutRefundCreateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getOrganizationNO());
                            detail.setSourceBillNo(req.getRequest().getOfNo());
                            detail.setSourceItem(par.getoItem());
                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice(par.getDistriPrice());
                            detail.setReceiveAmt(par.getDistriAmt());
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);
                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }

                    if("1".equals(docType)){
                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        //DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockOutNO);
                        request1.setSupplyOrgNo("");
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10005.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockOutRefundCreateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getOrganizationNO());
                            detail.setSourceBillNo(req.getRequest().getOfNo());
                            detail.setSourceItem(par.getoItem());
                            detail.setItem(String.valueOf(par.getItem()));
                            detail.setPluNo(par.getPluNo());
                            detail.setFeatureNo(par.getFeatureNo());
                            detail.setPUnit(par.getPunit());
                            detail.setPQty(String.valueOf(par.getPqty()));
                            detail.setReceivePrice("");
                            detail.setReceiveAmt("");
                            detail.setSupplyPrice("");
                            detail.setSupplyAmt("");
                            request1.getDetail().add(detail);

                            if(!req.getOrganizationNO().equals(req.getRequest().getReceiptOrg())){
                                detail = inReq.new Detail();

                                detail.setReceiveOrgNo(req.getRequest().getReceiptOrg());
                                detail.setSourceBillNo(req.getRequest().getOfNo());
                                detail.setSourceItem(par.getoItem());
                                detail.setItem(String.valueOf(par.getItem()));
                                detail.setPluNo(par.getPluNo());
                                detail.setFeatureNo(par.getFeatureNo());
                                detail.setPUnit(par.getPunit());
                                detail.setPQty(String.valueOf(par.getPqty()));
                                detail.setReceivePrice("");
                                detail.setReceiveAmt("");
                                detail.setSupplyPrice("");
                                detail.setSupplyAmt("");
                                request1.getDetail().add(detail);
                            }

                        }
                        inReq.setRequest(request1);
                        ParseJson pj = new ParseJson();
                        String jsontemp = pj.beanToJson(inReq);

                        DispatchService ds = DispatchService.getInstance();
                        String resXml = ds.callService(jsontemp, StaticInfo.dao);
                        DCP_InterSettleDataGenerateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_InterSettleDataGenerateRes>() {
                        });
                        if (resserver.isSuccess() == false) {
                            res.setSuccess(true);
                            res.setServiceStatus("000");
                            res.setServiceDescription("内部结算失败！");
                            return;
                            //throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "内部结算失败！");
                        }
                    }
                }

                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据已存在，请重新确认！ ");
            }

        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutRefundCreateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockOutRefundCreateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockOutRefundCreateReq.levelElm request = req.getRequest();
        List<DCP_StockOutRefundCreateReq.level1Elm> jsonDatas = request.getDatas();

        //必传值不为空
        String bDate = request.getbDate();
        String status = request.getStatus();
        String docType = request.getDocType();
        String warehouse = request.getWarehouse();
        String transferWarehouse = request.getTransferWarehouse();

        //必传值可以为空
        String memo = request.getMemo();
        String oType = request.getoType();
        String ofNO = request.getOfNo();
        String loadDocType = request.getLoadDocType();
        String loadDocNO = request.getLoadDocNo();
        String deliveryNO = request.getDeliveryNo();
        String transferShop = request.getTransferShop();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt=request.getTotDistriAmt();
        String totCqty = request.getTotCqty();

        if (docType.equals("1")){
            if (Check.Null(transferShop)) {
                errMsg.append("调入门店不能为空值！");
                isFail = true;
            }
        }
        if (docType.equals("1")||docType.equals("4")) {
            if (transferWarehouse == null) {
                errMsg.append("调入仓库不可为空值, ");
                isFail = true;
            }
        }
        if (Check.Null(bDate)) {
            errMsg.append("营业日期不可为空值, ");
            isFail = true;
        }
        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }
        if (Check.Null(docType)) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }
        if (warehouse == null) {
            errMsg.append("拨出仓库不可为空值, ");
            isFail = true;
        }

        if (memo == null) {
            errMsg.append("备注不可为空值, ");
            isFail = true;
        }
        if (loadDocType == null) {
            errMsg.append("转入来源单据类型不可为空值, ");
        }
        if (loadDocNO == null) {
            errMsg.append("转入来源单据单号不可为空值, ");
        }
        if (deliveryNO == null) {
            errMsg.append("物流单号不可为空值, ");
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
        if (Check.Null(request.getStockOutNo_origin())) {
            errMsg.append("stockOutNo_origin不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_StockOutRefundCreateReq.level1Elm par : jsonDatas) {
            //必传值不为空		
            String item = par.getItem();
            String pluNo = par.getPluNo();
            String punit = par.getPunit();
            String pqty = par.getPqty();
            String baseUnit = par.getBaseUnit();
            String baseQty = par.getBaseQty();
            String warehouseD = par.getWarehouse();
            String unitRatio = par.getUnitRatio();

            //必传值可以为空
            String oItem = par.getoItem();
            String price = par.getPrice();
            String amt = par.getAmt();

            if (Check.Null(item)) {
                errMsg.append("商品"+pluNo+"项次不可为空值, ");
                isFail = true;
            }
            if (oItem == null) {
                errMsg.append("商品"+pluNo+"来源项次不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pluNo)) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }
            if (Check.Null(punit)) {
                errMsg.append("商品"+pluNo+"商品单位不可为空值, ");
                isFail = true;
            }
            if (Check.Null(pqty)) {
                errMsg.append("商品"+pluNo+"商品数量不可为空值, ");
                isFail = true;
            }
            if (price == null) {
                errMsg.append("商品"+pluNo+"单价不可为空值, ");
                isFail = true;
            }
            if (amt == null) {
                errMsg.append("商品"+pluNo+"金额不可为空值, ");
                isFail = true;
            }
            if (warehouseD == null) {
                errMsg.append("商品"+pluNo+"拨出仓库不可为空值, ");
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
            if (Check.Null(par.getItem_origin())) 
            {
                errMsg.append("item_origin不可为空值, ");
                isFail = true;
            }
            if (Check.Null(par.getPqty_origin()))
            {
                errMsg.append("pqty_origin不可为空值, ");
                isFail = true;
            }

            if (isFail)
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockOutRefundCreateReq> getRequestType()
    {
        return new TypeToken<DCP_StockOutRefundCreateReq>(){};
    }

    @Override
    protected DCP_StockOutRefundCreateRes getResponseType()
    {
        return new DCP_StockOutRefundCreateRes();
    }

    @Override
    protected String getQuerySql(DCP_StockOutRefundCreateReq req) throws Exception {
        String sql = null;
        DCP_StockOutRefundCreateReq.levelElm request = req.getRequest();
        StringBuffer sqlbuf = new StringBuffer();
        String bDate = PosPub.getAccountDate_SMS(dao, req.geteId(), req.getShopId());
        String stockOutNO = "";
        String docType = request.getDocType();
        if (docType.equals("1")) {
            stockOutNO = "DBCK" + bDate;//matter.format(dt);
        }
        if (docType.equals("0") || docType.equals("2") ) {
            stockOutNO = "THCK" + bDate;//matter.format(dt);
        }
        if (docType.equals("3")) {
            stockOutNO = "QTCK" + bDate;
        }
        if (docType.equals("4")) {
            stockOutNO = "YCCK" + bDate;
        }
        sqlbuf.append("" + "select stockOutNO  from ( " + "select max(stockOutNO) as  stockOutNO "
                              + "  from DCP_STOCKOUT " + " where OrganizationNO = ? " + " and EID = ? " + " and SHOPID = ? "
                              + " and stockOutNO like '%%" + stockOutNO + "%%' "); // 假資料
        sqlbuf.append(" ) TBL ");

        if (sqlbuf.length() > 0)
            sql = sqlbuf.toString();

        return sql;
    }

    private String getStockOutNO(DCP_StockOutRefundCreateReq req) throws Exception {
        DCP_StockOutRefundCreateReq.levelElm request = req.getRequest();
        String sql = null;
        String stockOutNO = null;
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String docType = request.getDocType();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String[] conditionValues = { organizationNO, eId, shopId }; // 查询要货单号
        sql = this.getQuerySql(req);
        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

        if (getQData != null && getQData.isEmpty() == false) {

            stockOutNO = (String) getQData.get(0).get("STOCKOUTNO");

            if (stockOutNO != null && stockOutNO.length() > 0) {
                long i;
                stockOutNO = stockOutNO.substring(4);
                i = Long.parseLong(stockOutNO) + 1;
                stockOutNO = i + "";
                if (docType.equals("1")) {
                    stockOutNO = "DBCK" + stockOutNO;
                }
                if (docType.equals("0") || docType.equals("2")) {
                    stockOutNO = "THCK" + stockOutNO;
                }
                if (docType.equals("3")) {
                    stockOutNO = "QTCK" + stockOutNO;
                }
                if (docType.equals("4")) {
                    stockOutNO = "YCCK" + stockOutNO;
                }
            } else {
                if (docType.equals("1")) {
                    stockOutNO = "DBCK" + bDate + "00001";
                }
                if (docType.equals("0") || docType.equals("2")) {
                    stockOutNO = "THCK" + bDate + "00001";
                }
                if (docType.equals("3")) {
                    stockOutNO = "QTCK" + bDate + "00001";
                }
                if (docType.equals("4")) {
                    stockOutNO = "YCCK" + bDate + "00001";
                }
            }
        } else {
            if (docType.equals("1")) {
                stockOutNO = "DBCK" + bDate + "00001";
            }
            if (docType.equals("0") || docType.equals("2") ) {
                stockOutNO = "THCK" + bDate + "00001";
            }
            if (docType.equals("3")) {
                stockOutNO = "QTCK" + bDate + "00001";
            }
            if (docType.equals("4")) {
                stockOutNO = "YCCK" + bDate + "00001";
            }
        }

        return stockOutNO;
    }

    private String getStockOutNONew(DCP_StockOutRefundCreateReq req) throws Exception {
        DCP_StockOutRefundCreateReq.levelElm request = req.getRequest();
        String stockOutNO = null;
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String docType = request.getDocType();

        if (docType.equals("1")) {
            stockOutNO = this.getOrderNO(req,"DBCK");
        }
        if (docType.equals("0") || docType.equals("2")) {
            stockOutNO = this.getOrderNO(req,"THCK");
        }
        if (docType.equals("3")) {
            stockOutNO = this.getOrderNO(req,"QTCK");
        }
        if (docType.equals("4")) {
            stockOutNO = this.getOrderNO(req,"YCCK");
        }

        return stockOutNO;
    }

    private boolean checkGuid(DCP_StockOutRefundCreateReq req) throws Exception {
        DCP_StockOutRefundCreateReq.levelElm request = req.getRequest();
        String guid = request.getStockOutID();
        boolean existGuid;
        String	sql = "select StockOut_ID "
                + " from DCP_StockOut "
                + " where StockOut_ID = '"+guid+"' ";

        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData != null && getQData.isEmpty() == false) {
            existGuid = true;
        } else {
            existGuid =  false;
        }

        return existGuid;
    }
}
