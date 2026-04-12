package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataGenerateReq;
import com.dsc.spos.json.cust.req.DCP_StockInRefundCreateReq;
import com.dsc.spos.json.cust.req.DCP_StockInRefundUpdateReq;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataGenerateRes;
import com.dsc.spos.json.cust.res.DCP_StockInRefundUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DCP_StockInRefundUpdate extends SPosAdvanceService<DCP_StockInRefundUpdateReq, DCP_StockInRefundUpdateRes>
{


    @Override
    protected void processDUID(DCP_StockInRefundUpdateReq req, DCP_StockInRefundUpdateRes res) throws Exception
    {
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        DCP_StockInRefundUpdateReq.levelElm request = req.getRequest();
        String bDate = request.getBDate();
        String modifyBy = req.getOpNO();
        Calendar cal = Calendar.getInstance();//获得当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String modifyDate = df.format(cal.getTime());
        df = new SimpleDateFormat("HHmmss");
        String modifyTime = df.format(cal.getTime());
        String memo = request.getMemo();
        String stockInNO = request.getStockInNo();
        String status = request.getStatus();
        String oType = request.getOType();
        String ofNO = request.getOfNo();
        String bsNO = request.getBsNo();
        String warehouse = request.getWarehouse();
        String docType = request.getDocType();

        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt=request.getTotDistriAmt();
        String totCqty = request.getTotCqty();
        String corp=req.getRequest().getCorp();//收货法人
        String deliveryCorp=req.getRequest().getDeliveryCorp();//发货法人

        if(Check.Null(corp)){
            String orgSql1="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+req.getOrganizationNO()+"' ";
            List<Map<String, Object>> orgList1 = this.doQueryData(orgSql1, null);
            if(CollUtil.isNotEmpty(orgList1)){
                corp = orgList1.get(0).get("CORP").toString();
            }
            req.getRequest().setCorp(corp);
        }

        if(Check.Null(deliveryCorp)){
            String orgSql2="select * from dcp_org where eid='"+req.geteId()+"' and organizationno='"+request.getTransferShop()+"' ";
            List<Map<String, Object>> orgList2 = this.doQueryData(orgSql2, null);
            if(CollUtil.isNotEmpty(orgList2)){
                deliveryCorp = orgList2.get(0).get("CORP").toString();
            }
            req.getRequest().setDeliveryCorp(deliveryCorp);
        }



        //增加判断，单据已经审核就不能修改，单据不存在也不能修改
        try
        {
            if (checkExist(req))
            {
                //删除原有单身DCP_stockin_detail
                DelBean db1 = new DelBean("DCP_STOCKIN_DETAIL");
                db1.addCondition("stockInNO", new DataValue(stockInNO, Types.VARCHAR));
                db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));


                //新增新的单身（多条记录）
                List<DCP_StockInRefundUpdateReq.level1Elm> datas = request.getDatas();
                for (DCP_StockInRefundUpdateReq.level1Elm par : datas) {
                    int insColCt = 0;
                    String[] columnsName = {
                            "STOCKINNO", "SHOPID", "item", "oItem", "pluNO",
                            "punit", "pqty", "BASEUNIT", "BASEQTY", "unit_Ratio",
                            "price", "amt", "EID", "organizationNO",
                            "PLU_BARCODE","RECEIVING_QTY", "POQTY","WAREHOUSE",
                            "PLU_MEMO","BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO",
                            "PACKINGNO", "OTYPE", "OFNO", "OOTYPE", "OOFNO", "OOITEM","ITEM_ORIGIN","PQTY_ORIGIN","MES_LOCATION","EXPDATE"
                    };
                    DataValue[] columnsVal = new DataValue[columnsName.length];

                    for (int i = 0; i < columnsVal.length; i++) {
                        String keyVal = null;
                        switch (i) {
                            case 0:
                                keyVal = stockInNO;
                                break;
                            case 1:
                                keyVal = shopId;
                                break;
                            case 2:
                                keyVal = par.getItem(); //item
                                break;
                            case 3:
                                keyVal = par.getOItem(); //oItem
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
                                keyVal = par.getBaseQty();     //wqty
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
                                keyVal = par.getPluBarcode();      //pluBarcode
                                break;
                            case 15:
                                keyVal = par.getReceivingQty();    //receivingqty
                                break;
                            case 16:
                                keyVal = par.getPoQty();//poQty
                                break;
                            case 17:
                                keyVal = par.getWarehouse();
                                break;
                            case 18:
                                if(par.getPluMemo()==null){
                                    keyVal = "";
                                }
                                else{
                                    keyVal = par.getPluMemo();
                                }
                                break;
                            case 19:
                                keyVal = par.getBatchNo();
                                break;
                            case 20:
                                keyVal = par.getProdDate();
                                break;
                            case 21:
                                if(par.getDistriPrice()==null || Check.Null(par.getDistriPrice()) ){
                                    keyVal = "0";
                                }
                                else {
                                    keyVal=par.getDistriPrice();
                                }
                                break;
                            case 22:
                                keyVal = par.getDistriAmt(); //
                                if (Check.Null(keyVal))
                                    keyVal="0";
                                break;
                            case 23:
                                keyVal = bDate;
                                break;
                            case 24:
                                keyVal = par.getFeatureNo();
                                if (Check.Null(keyVal))
                                    keyVal=" ";
                                break;
                            case 25:
                                keyVal = par.getPackingNo();
                                break;
                            case 26:
                                keyVal = par.getOType();   //"OTYPE"
                                break;
                            case 27:
                                keyVal = par.getOfNo();    //"OFNO"
                                break;
                            case 28:
                                keyVal = par.getOoType();  //"OOTYPE"
                                break;
                            case 29:
                                keyVal = par.getOofNo();  //"OOFNO"
                                break;
                            case 30:
                                keyVal =par.getOoItem();  //"OOITEM"
                                break;
                            case 31:
                                keyVal = par.getItem_origin();
                                break;
                            case 32:
                                keyVal = par.getPqty_origin();
                                break;

                            case 33:
                                keyVal = par.getLocation();
                                if(Check.Null(keyVal)){
                                    keyVal=" ";
                                }
                                break;

                            case 34:
                                keyVal = par.getExpDate();
                                break;
                            default:
                                break;
                        }

                        if (keyVal != null)
                        {
                            insColCt++;
                            if (i == 2 || i == 3){
                                columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                            }else if (i == 6 || i == 8 || i == 9 || i == 10 || i == 11 || i == 15 || i == 16 || i == 21){
                                columnsVal[i] = new DataValue(keyVal, Types.FLOAT);
                            }else{
                                columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
                            }
                        }
                        else
                        {
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
                    InsBean ib2 = new InsBean("DCP_STOCKIN_DETAIL", columns2);
                    ib2.addValues(insValue2);
                    this.addProcessData(new DataProcessBean(ib2));//增加单身

                    //更新原单的明细已红冲数量
                    UptBean ub_DCP_STOCKIN_DETAIL = new UptBean("DCP_STOCKIN_DETAIL");
                    ub_DCP_STOCKIN_DETAIL.addUpdateValue("PQTY_REFUND", new DataValue(par.getPqty(), Types.VARCHAR));
                    // condition
                    ub_DCP_STOCKIN_DETAIL.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_STOCKIN_DETAIL.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub_DCP_STOCKIN_DETAIL.addCondition("STOCKINNO", new DataValue(req.getRequest().getStockInNo_origin(), Types.VARCHAR));
                    ub_DCP_STOCKIN_DETAIL.addCondition("ITEM", new DataValue(par.getItem_origin(), Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub_DCP_STOCKIN_DETAIL));


                    // 更新关联通知单单身 by jinzma 20210422 【ID1017036】【3.0货郎】按商品行收货（DCP_StockInUpdate（收货入库单修改））
                    if (docType.equals("0") && !Check.Null(ofNO)) {
                        UptBean ub = new UptBean("DCP_RECEIVING_DETAIL");
                        // add value
                        ub.addUpdateValue("STOCKIN_QTY", new DataValue(par.getPqty(), Types.VARCHAR));
                        ub.addUpdateValue("STATUS", new DataValue("100", Types.VARCHAR));
                        // condition
                        ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                        ub.addCondition("RECEIVINGNO", new DataValue(ofNO, Types.VARCHAR));
                        ub.addCondition("ITEM", new DataValue(par.getOItem(), Types.VARCHAR));
                        ub.addCondition("PLUNO", new DataValue(par.getPluNo(), Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(ub));
                    }
                }

                UptBean ub1 = new UptBean("DCP_STOCKIN");
                //add Value
                ub1.addUpdateValue("MODIFY_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                ub1.addUpdateValue("bDate", new DataValue(bDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate, Types.VARCHAR));
                ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime, Types.VARCHAR));
                ub1.addUpdateValue("TOT_PQTY", new DataValue(totPqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_AMT", new DataValue(totAmt, Types.VARCHAR));
                ub1.addUpdateValue("TOT_CQTY", new DataValue(totCqty, Types.VARCHAR));
                ub1.addUpdateValue("TOT_DISTRIAMT", new DataValue(totDistriAmt, Types.VARCHAR));
                ub1.addUpdateValue("memo", new DataValue(memo, Types.VARCHAR));
                ub1.addUpdateValue("status", new DataValue(status, Types.VARCHAR));
                ub1.addUpdateValue("oType", new DataValue(oType, Types.VARCHAR));
                ub1.addUpdateValue("ofNO", new DataValue(ofNO, Types.VARCHAR));
                ub1.addUpdateValue("bsNO", new DataValue(bsNO, Types.VARCHAR));
                ub1.addUpdateValue("warehouse", new DataValue(warehouse, Types.VARCHAR));
                ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                ub1.addUpdateValue("EMPLOYEEID", new DataValue(req.getRequest().getEmployeeId(), Types.VARCHAR));
                ub1.addUpdateValue("DEPARTID", new DataValue(req.getRequest().getDepartId(), Types.VARCHAR));

                ub1.addUpdateValue("CORP", new DataValue(corp, Types.VARCHAR));
                ub1.addUpdateValue("DELIVERYCORP", new DataValue(deliveryCorp, Types.VARCHAR));


                //condition
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("stockInNO", new DataValue(stockInNO, Types.VARCHAR));
                ub1.addCondition("organizationNO", new DataValue(organizationNO, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(ub1));

                this.doExecuteDataToDB();

                if(!req.getRequest().getCorp().equals(req.getRequest().getDeliveryCorp())){
                    if("0".equals(req.getRequest().getDocType())){
                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockInNO);
                        request1.setSupplyOrgNo(req.getRequest().getTransferShop());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10004.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockInRefundUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getOrganizationNO());
                            detail.setSourceBillNo(par.getOofNo());
                            detail.setSourceItem(par.getOoItem());
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

                    if("5".equals(req.getRequest().getDocType())){
                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockInNO);
                        request1.setSupplyOrgNo(req.getOrganizationNO());
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType_10004.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockInRefundUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getRequest().getTransferShop());
                            detail.setSourceBillNo(par.getOofNo());
                            detail.setSourceItem(par.getOoItem());
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

                    if("1".equals(req.getRequest().getDocType())){
                        DCP_InterSettleDataGenerateReq inReq = new DCP_InterSettleDataGenerateReq();
                        inReq.setServiceId("DCP_InterSettleDataGenerate");
                        inReq.setToken(req.getToken());
                        DCP_InterSettleDataGenerateReq.Request request1 = inReq.new Request();
                        request1.setOrganizationNo(req.getOrganizationNO());
                        request1.setBillNo(stockInNO);
                        request1.setSupplyOrgNo(null);
                        request1.setReturnSupplyPrice("Y");
                        request1.setBillType(DCP_InterSettleDataGenerate.BillType.BillType10006.getType());
                        request1.setDetail(new ArrayList<>());
                        for (DCP_StockInRefundUpdateReq.level1Elm par : req.getRequest().getDatas()) {
                            DCP_InterSettleDataGenerateReq.Detail detail = inReq.new Detail();
                            detail.setReceiveOrgNo(req.getRequest().getTransferShop());
                            detail.setSourceBillNo(par.getOofNo());
                            detail.setSourceItem(par.getOoItem());
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

                            if(!req.getOrganizationNO().equals(req.getRequest().getTransferShop())){
                                detail = inReq.new Detail();
                                detail.setReceiveOrgNo(req.getOrganizationNO());
                                detail.setSourceBillNo(par.getOofNo());
                                detail.setSourceItem(par.getOoItem());
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

            }
            else
            {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已提交，请重新输入！");
            }
        }
        catch (Exception e)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockInRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockInRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockInRefundUpdateReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockInRefundUpdateReq req) throws Exception
    {
        boolean isFail = false;

        StringBuffer errMsg = new StringBuffer();
        DCP_StockInRefundUpdateReq.levelElm request = req.getRequest();
        List<DCP_StockInRefundUpdateReq.level1Elm> jsonDatas = request.getDatas();

        if(jsonDatas==null || jsonDatas.size()==0) {
            errMsg.append("明细记录不可为空值, ");
            isFail = true;
        }

        //必传值不为空
        String bDate = request.getBDate();
        String status = request.getStatus();
        String docType = request.getDocType();
        String oType = request.getOType();
        String ofNO = request.getOfNo();
        String warehouse = request.getWarehouse();
        String totPqty = request.getTotPqty();
        String totAmt = request.getTotAmt();
        String totDistriAmt=request.getTotDistriAmt();
        String totCqty = request.getTotCqty();

        //必传值可以为空
        String memo = request.getMemo();
        String loadDocType = request.getLoadDocType();
        String loadDocNO = request.getLoadDocNo();

        if (Check.Null(totPqty)) {
            errMsg.append("合计录入数量不可为空值, ");
            isFail = true;
        }
        if (Check.Null(totAmt)) {
            errMsg.append("合计录入金额不可为空值, ");
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

        //其他入库单不需要判断来源单信息
        if(!docType.equals("3")) {
            if (Check.Null(oType)) {
                errMsg.append("来源单据类型不可为空值, ");
                isFail = true;
            }
            if (Check.Null(ofNO)) {
                errMsg.append("来源单据单号不可为空值, ");
                isFail = true;
            }
        }

        if (Check.Null(warehouse)) {
            errMsg.append("仓库不可为空值, ");
            isFail = true;
        }

        if (memo == null) {
            errMsg.append("备注不可为空值, ");
            isFail = true;
        }

        if (loadDocType == null) {
            errMsg.append("转入来源单据类型不可为空值, ");
            isFail = true;
        }

        if (loadDocNO == null) {
            errMsg.append("转入来源单据单号不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getStockInNo_origin()))
        {
            errMsg.append("stockInNo_origin不可为空值, ");
            isFail = true;
        }
        if (isFail){
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_StockInRefundUpdateReq.level1Elm par : jsonDatas) {
            //必传值不为空
            String item = par.getItem();
            String oItem = par.getOItem();
            String pluNo = par.getPluNo();
            String punit = par.getPunit();
            String pqty = par.getPqty();
            String warehouseD = par.getWarehouse();
            String baseUnit =par.getBaseUnit();
            String baseQty = par.getBaseQty();
            String unitRatio = par.getUnitRatio();

            //必传值可以为空
            String price = par.getPrice();
            String amt = par.getAmt();

            if (Check.Null(item)) {
                errMsg.append("项次不可为空值, ");
                isFail = true;
            }

            if (Check.Null(oItem)) {
                errMsg.append("来源项次不可为空值, ");
                isFail = true;
            }

            if (Check.Null(pluNo)) {
                errMsg.append("商品编码不可为空值, ");
                isFail = true;
            }

            if (Check.Null(punit)) {
                errMsg.append("商品"+pluNo+"单位不可为空值, ");
                isFail = true;
            }

            if (Check.Null(pqty)) {
                errMsg.append("商品"+pluNo+"数量不可为空值, ");
                isFail = true;
            }

            if (Check.Null(warehouseD)) {
                errMsg.append("商品"+pluNo+"仓库不可为空值, ");
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

            if (isFail){
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
            }

        }
        return false;
    }

    @Override
    protected TypeToken<DCP_StockInRefundUpdateReq> getRequestType()
    {
        return new TypeToken<DCP_StockInRefundUpdateReq>(){};
    }

    @Override
    protected DCP_StockInRefundUpdateRes getResponseType()
    {
        return new DCP_StockInRefundUpdateRes();
    }


    private boolean checkExist(DCP_StockInRefundUpdateReq req)  throws Exception  {
        boolean exist = false;
        String eId = req.geteId();
        String shopId = req.getShopId();
        DCP_StockInRefundUpdateReq.levelElm request = req.getRequest();
        String stockInNO =request.getStockInNo();
        String[] conditionValues = { eId,shopId,stockInNO };
        String sql = " select * from DCP_StockIn  where EID=?  and SHOPID=? and StockinNo=? and  Status='0'  " ;
        List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
        if (getQData != null && !getQData.isEmpty()) {
            exist = true;
        }
        return exist;
    }
}
