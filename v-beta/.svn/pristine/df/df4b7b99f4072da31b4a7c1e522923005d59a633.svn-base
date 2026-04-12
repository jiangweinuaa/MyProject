package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutRefundCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockOutRefundUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockOutRefundUpdateRes;
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

public class DCP_StockOutRefundUpdate extends SPosAdvanceService<DCP_StockOutRefundUpdateReq, DCP_StockOutRefundUpdateRes>
{


    @Override
    protected void processDUID(DCP_StockOutRefundUpdateReq req, DCP_StockOutRefundUpdateRes res) throws Exception
    {
        try {
            DCP_StockOutRefundUpdateReq.levelElm request = req.getRequest();
            String shopId = req.getShopId();
            String organizationNO = req.getOrganizationNO();
            String eId = req.geteId();
            String bDate = request.getbDate();
            String memo = request.getMemo();
            String docType = request.getDocType();
            String oType = request.getoType();
            String ofNO = request.getOfNo();
            String modifyBy = req.getOpNO();
            Calendar cal = Calendar.getInstance();//获得当前时间
            SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
            String modifyDate = df.format(cal.getTime());
            df=new SimpleDateFormat("HHmmss");
            String modifyTime = df.format(cal.getTime());
            String loadDocType = request.getLoadDocType();
            String loadDocNO = request.getLoadDocNo();
            String deliveryNO = request.getDeliveryNo();
            String transferShop = request.getTransferShop();
            String receiptOrg = request.getReceiptOrg();
            String stockOutNO =  request.getStockOutNo();
            String bsNO = Check.Null(request.getBsNo())?"":request.getBsNo();
            String warehouse = request.getWarehouse();
            String transferWarehouse = request.getTransferWarehouse();
            String totPqty = request.getTotPqty();
            String totAmt =request.getTotAmt();
            String totDistriAmt = request.getTotDistriAmt();
            String totCqty = request.getTotCqty();
            String sourceMenu = request.getSourceMenu();
            String employeeId = request.getEmployeeId();
            String departId = request.getDeaprtId();


            //2019-12-19 增加模板编码
            String pTemplateNo = "";
            pTemplateNo = request.getpTemplateNo();


            //校验此收货单是否存在
            String sql = "select stockOutNO,Status,BDATE "
                    + " from DCP_STOCKOUT "
                    + " where OrganizationNO = '"+organizationNO+"' "
                    + " and EID = '"+eId+"' "
                    + " and SHOPID = '"+shopId+"' "
                    + " and stockOutNO = '"+stockOutNO+"' ";
            List<Map<String, Object>> getQData = this.doQueryData(sql, null);

            String keyDate=bDate;
            if (getQData != null && getQData.isEmpty() == false) {
                String status=getQData.get(0).get("STATUS").toString();
                if (status.equals("-1")) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批中，不能修改 ");
                }

                if (status.equals("0")) {

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

                    keyDate=getQData.get(0).get("BDATE").toString();

                    //删除原来单身
                    DelBean db1 = new DelBean("DCP_STOCKOUT_DETAIL");
                    db1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    db1.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(db1));

                    //新增單身 (多筆)
                    List<DCP_StockOutRefundUpdateReq.level1Elm> jsonDatas = request.getDatas();
                    for (DCP_StockOutRefundUpdateReq.level1Elm par : jsonDatas) {

                        int insColCt = 0;
                        String[] columnsName = {
                                "STOCKOUTNO","SHOPID","item","oItem","pluNO",
                                "punit", "pqty","BASEUNIT", "BASEQTY", "unit_Ratio", "PLU_BARCODE",
                                "price", "amt", "EID", "organizationNO", "WAREHOUSE",
                                "BSNO","PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT",
                                "BDATE","FEATURENO","STOCKQTY","ITEM_ORIGIN","PQTY_ORIGIN","MES_LOCATION","EXPDATE"
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
                                    if (oItem.equals("") || oItem.length() == 0 ){
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
                                    keyVal = par.getUnitRatio();  //unitRatio
                                    break;
                                case 10:
                                    keyVal = par.getPluBarcode();    //pluBarcode
                                    break;
                                case 11:
                                    keyVal = par.getPrice();    //price
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
                                    keyVal = par.getWarehouse();
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
                                    if( par.getPluMemo() == null){
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
                                    keyVal = par.getDistriPrice();
                                    if (Check.Null(keyVal)) keyVal="0";
                                    break;
                                case 21:
                                    keyVal = par.getDistriAmt();
                                    if (Check.Null(keyVal)) keyVal="0";
                                    break;
                                case 22:
                                    keyVal = bDate;
                                    break;
                                case 23:
                                    keyVal = par.getFeatureNo();
                                    if (Check.Null(keyVal))
                                        keyVal=" ";
                                    break;
                                case 24:
                                    keyVal = par.getStockqty();
                                    if (PosPub.isNumericTypeMinus(keyVal)==false)
                                        keyVal="999999";
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
                                }else if (i == 6 || i == 8 || i == 9 || i == 11 || i == 12 || i == 20|| i == 24){
                                    columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                                }else{
                                    columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                                }
                            } else {
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
                    if(transferShop==null || transferShop.trim().length()==0 ) {
                        transferShop=receiptOrg;
                    }

                    //【ID1031972】【罗森尼娜3.0】中台退货出库单，ERP接过来之后退货组织变了  BY JINZMA 20230318 这帮ERP就是饭桶
                    if (docType.equals("0")){
                        transferShop = receiptOrg;
                    }


                    //更新单头
                    UptBean ub1 = new UptBean("DCP_STOCKOUT");
                    ub1.addUpdateValue("BDate", new DataValue(bDate, Types.VARCHAR));
                    if(!Check.Null(memo)) {
                        ub1.addUpdateValue("memo", new DataValue(Check.Null(memo), Types.VARCHAR));
                    }
                    if(!Check.Null(docType)) {
                        ub1.addUpdateValue("DOC_TYPE", new DataValue(docType, Types.VARCHAR));
                    }
                    if(!Check.Null(receiptOrg)) {
                        ub1.addUpdateValue("RECEIPT_ORG", new DataValue(receiptOrg, Types.VARCHAR));
                    }
                    ub1.addUpdateValue("oType", new DataValue(oType, Types.VARCHAR));
                    ub1.addUpdateValue("ofNO", new DataValue(ofNO, Types.VARCHAR));
                    if(!Check.Null(loadDocType)) {
                        ub1.addUpdateValue("load_DocType", new DataValue(loadDocType, Types.VARCHAR));
                    }
                    if(!Check.Null(loadDocNO)) {
                        ub1.addUpdateValue("load_DocNO", new DataValue(loadDocNO, Types.VARCHAR));
                    }
                    if(!Check.Null(deliveryNO)) {
                        ub1.addUpdateValue("delivery_NO", new DataValue(deliveryNO, Types.VARCHAR));
                    }
                    if(!Check.Null(transferShop)) {
                        ub1.addUpdateValue("transfer_Shop", new DataValue(transferShop, Types.VARCHAR));
                    }
                    ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                    ub1.addUpdateValue("modifyBy", new DataValue(modifyBy, Types.VARCHAR));
                    ub1.addUpdateValue("modify_Date", new DataValue(modifyDate, Types.VARCHAR));
                    ub1.addUpdateValue("modify_Time", new DataValue(modifyTime, Types.VARCHAR));
                    ub1.addUpdateValue("tot_Pqty", new DataValue(totPqty, Types.VARCHAR));
                    ub1.addUpdateValue("tot_Amt", new DataValue(totAmt, Types.VARCHAR));
                    ub1.addUpdateValue("tot_Cqty", new DataValue(totCqty, Types.VARCHAR));
                    ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("PTEMPLATENO", new DataValue(pTemplateNo, Types.VARCHAR));
                    ub1.addUpdateValue("memo", new DataValue(request.getMemo(), Types.VARCHAR));
                    ub1.addUpdateValue("EMPLOYEEID", new DataValue(employeeId, Types.VARCHAR));
                    ub1.addUpdateValue("DEPARTID", new DataValue(departId, Types.VARCHAR));
                    ub1.addUpdateValue("CORP", new DataValue(corp, Types.VARCHAR));
                    ub1.addUpdateValue("RECEIPTCORP", new DataValue(receiptCorp, Types.VARCHAR));

                    if(!Check.Null(bsNO)) {
                        ub1.addUpdateValue("bsNO", new DataValue(bsNO, Types.VARCHAR));
                    }
                    if(!Check.Null(warehouse)) {
                        ub1.addUpdateValue("warehouse", new DataValue(warehouse, Types.VARCHAR));
                    }
                    if(!Check.Null(transferWarehouse)) {
                        ub1.addUpdateValue("transfer_Warehouse", new DataValue(transferWarehouse, Types.VARCHAR));
                    }
                    if(!Check.Null(bsNO)) {
                        ub1.addUpdateValue("bsNO", new DataValue(bsNO, Types.VARCHAR));
                    }
                    if(!Check.Null(sourceMenu)){
                        ub1.addUpdateValue("SOURCEMENU", new DataValue(sourceMenu, Types.VARCHAR));
                    }
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    // condition
                    ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("stockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));

                    this.doExecuteDataToDB();

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
                            for (DCP_StockOutRefundUpdateReq.level1Elm par : req.getRequest().getDatas()) {
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
                            for (DCP_StockOutRefundUpdateReq.level1Elm par : req.getRequest().getDatas()) {
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
                            for (DCP_StockOutRefundUpdateReq.level1Elm par : req.getRequest().getDatas()) {
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
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "此单已确认，不允许修改！");
                }

            } else {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，请重新输入！");
            }
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockOutRefundUpdateReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockOutRefundUpdateReq.levelElm request = req.getRequest();
        List<DCP_StockOutRefundUpdateReq.level1Elm> jsonDatas = request.getDatas();

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

        if (oType == null) {
            errMsg.append("来源单据类型不可为空值, ");
            isFail = true;
        }
        if (ofNO == null) {
            errMsg.append("来源单据单号不可为空值, ");
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
            errMsg.append("原单号不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_StockOutRefundUpdateReq.level1Elm par : jsonDatas) {
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
    protected TypeToken<DCP_StockOutRefundUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_StockOutRefundUpdateReq>(){};
    }

    @Override
    protected DCP_StockOutRefundUpdateRes getResponseType()
    {
        return new DCP_StockOutRefundUpdateRes();
    }
}
