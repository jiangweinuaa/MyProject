package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutRefundProcessReq;
import com.dsc.spos.json.cust.res.DCP_StockOutRefundProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.dsc.spos.utils.ding.DCP_DingCallBack;
import com.google.gson.reflect.TypeToken;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

public class DCP_StockOutRefundProcess extends SPosAdvanceService<DCP_StockOutRefundProcessReq, DCP_StockOutRefundProcessRes>
{


    @Override
    protected void processDUID(DCP_StockOutRefundProcessReq req, DCP_StockOutRefundProcessRes res) throws Exception
    {
        try {
            DCP_StockOutRefundProcessReq.levelElm request = req.getRequest();
            String shopId = "";
            String shopName = req.getShopName();
            String organizationNO = "";
            String eId = "";
            String stockOutNO = request.getStockOutNo();
            String stockInNO = "";
            String docType = request.getDocType();
            String ofNO = request.getOfNo();
            String status = request.getStatus();
            String inv_cost_warehouse = "";
            String langType="";
            String opNo = "";
            String sDate =  new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());

            String sourceMenu="";
            //钉钉JOB调用
            String oEId=request.getoEId();
            String oShopId=request.getoShopId();
            String o_createBy=request.getO_createBy();
            String o_inv_cost_warehouse=request.getO_inv_cost_warehouse();
            String o_langType=request.getO_langType();

            //钉钉JOB调入
            if (!Check.Null(oEId)) {
                shopId = oShopId;
                organizationNO = oShopId;
                eId = oEId;
                inv_cost_warehouse = o_inv_cost_warehouse;
                langType=o_langType;
                opNo=o_createBy;
            } else {
                shopId = req.getShopId();
                organizationNO = req.getOrganizationNO();
                eId = req.geteId();
                inv_cost_warehouse = req.getInv_cost_warehouse();
                langType=req.getLangType();
                opNo = req.getOpNO();
            }

            String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

            //获取启用在途参数 Enable_InTransit
            String Enable_InTransit= PosPub.getPARA_SMS(dao, eId,"", "Enable_InTransit");
            if (Check.Null(Enable_InTransit)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"获取在途仓参数:Enable_InTransit失败");
            }

            // IF 参数是否启用批号==N ，库存流水的批号和日期字段不给值 BY JZMA 20191101
            String isBatchPara = PosPub.getPARA_SMS(dao, eId, "", "Is_BatchNO");
            if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")){
                isBatchPara="N";
            }

            if(Enable_InTransit.equals("Y")){
                if(Check.Null(inv_cost_warehouse)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"参数已启用在途,门店:"+req.getShopId()+ "  "+req.getShopName() +"在途仓不可为空"  );
                }
            }

            UptBean ub1 = null;

            String sql1;
            String sql2;
            String sql3;


            //移仓说明
            if (docType.equals("4")) {

                String[] columns1 = {
                        "SHOPID", "organizationNO", "createBy", "create_date", "create_time",
                        "tot_pqty", "tot_amt", "tot_cqty", "doc_type", "status", "process_status",
                        "EID", "stockInNO", "BDATE", "WAREHOUSE", "TRANSFER_WAREHOUSE", "LOAD_DOCNO","MEMO",
                        "AccountBy","Account_Date","Account_Time","SubmitBy","Submit_Date","Submit_Time",
                        "ConfirmBy","Confirm_Date","Confirm_Time","TOT_DISTRIAMT",
                        "CREATE_CHATUSERID","ACCOUNT_CHATUSERID","UPDATE_TIME","TRAN_TIME"
                };

                DataValue[] insValue1 = null;

                stockInNO = this.getStockInNO(req);
                String bDate = "";
                String warehouse = "";
                String transferWarehouse = "";
                String MEMO = "";
                String totPqty="";
                String totAmt="";
                String totDistriAmt="";
                String totCqty ="";

                ///检查入库单是否已经审核，审核就退出
                String exisstockin="select * from DCP_STOCKIN where EID='"+eId+"' and SHOPID='"+shopId+"'  and load_docno='"+stockOutNO+"'  and status='2'  and doc_type='4'  ";
                List<Map<String, Object>> stockinlist=this.doQueryData(exisstockin, null);
                if(stockinlist !=null && stockinlist.isEmpty()==false)
                {
                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行成功");
                    return;
                }


                sql2 = this.getQuerySql2(req);   // 查出库单
                String[] conditionValues2 = { shopId, organizationNO, eId, stockOutNO };
                List<Map<String, Object>> getQData2 = this.doQueryData(sql2, conditionValues2);
                if (getQData2 != null && getQData2.isEmpty() == false) {
                    bDate = getQData2.get(0).get("BDATE").toString();
                    warehouse = getQData2.get(0).get("WAREHOUSE").toString();
                    transferWarehouse = getQData2.get(0).get("TRANSFER_WAREHOUSE").toString();
                    MEMO= getQData2.get(0).get("MEMO").toString();

                    totPqty = getQData2.get(0).get("TOTPQTY").toString();
                    totAmt = getQData2.get(0).get("TOTAMT").toString();
                    totDistriAmt = getQData2.get(0).get("TOT_DISTRIAMT").toString();
                    totCqty = getQData2.get(0).get("TOTCQTY").toString();
                }

                sql3 = this.getQuerySql3(req);  //查出库单明细
                String[] conditionValues3 = { shopId, organizationNO, eId, stockOutNO };
                List<Map<String, Object>> getQData3 = this.doQueryData(sql3, conditionValues3);

                if (getQData3 != null && getQData3.isEmpty() == false) {
                    for (Map<String, Object> oneData3 : getQData3) {
                        int insColCt = 0;
                        String[] columnsName = { "STOCKINNO", "SHOPID", "EID", "organizationNO", "item",
                                "OType", "OFNO", "OItem", "PLUNO", "Punit", "PQTY", "RECEIVING_QTY",
                                "BASEUNIT", "BASEQTY", "unit_Ratio", "Price", "AMT","WAREHOUSE","PLU_MEMO",
                                "BATCH_NO","PROD_DATE","DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO"
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
                                    keyVal = eId;
                                    break;
                                case 3:
                                    keyVal = organizationNO;
                                    break;
                                case 4:
                                    keyVal = oneData3.get("ITEM").toString();
                                    break;
                                case 5:
                                    keyVal = "";
                                    break;
                                case 6:
                                    keyVal = oneData3.get("STOCKOUTNO").toString();
                                    break;
                                case 7:
                                    String oItem = oneData3.get("ITEM").toString();
                                    if (oItem.length() == 0) {
                                        oItem = "0";
                                    }
                                    keyVal = oItem;
                                    break;
                                case 8:
                                    keyVal = oneData3.get("PLUNO").toString();
                                    break;
                                case 9:
                                    keyVal = oneData3.get("PUNIT").toString();
                                    break;
                                case 10:
                                    keyVal = oneData3.get("PQTY").toString();
                                    break;
                                case 11:
                                    keyVal = oneData3.get("PQTY").toString();
                                    break;
                                case 12:
                                    keyVal = oneData3.get("BASEUNIT").toString();
                                    break;
                                case 13:
                                    keyVal = oneData3.get("BASEQTY").toString();
                                    break;
                                case 14:
                                    keyVal = oneData3.get("UNITRATIO").toString();
                                    break;
                                case 15:
                                    keyVal = oneData3.get("PRICE").toString();
                                    break;
                                case 16:
                                    keyVal = oneData3.get("AMT").toString();
                                    break;
                                case 17:
                                    keyVal= transferWarehouse;
                                    break;
                                case 18:
                                    keyVal= oneData3.get("PLUMEMO").toString();
                                    break;
                                case 19:
                                    keyVal = oneData3.get("BATCH_NO").toString();
                                    break;
                                case 20:
                                    keyVal =oneData3.get("PROD_DATE").toString();
                                    break;
                                case 21:
                                    if(oneData3.get("DISTRIPRICE")==null || Check.Null(oneData3.get("DISTRIPRICE").toString()) ){
                                        keyVal = "0";
                                    }
                                    else {
                                        keyVal=oneData3.get("DISTRIPRICE").toString();
                                    }
                                    break;
                                case 22:
                                    if(oneData3.get("DISTRIAMT")==null || Check.Null(oneData3.get("DISTRIAMT").toString()) ){
                                        keyVal = "0";
                                    }
                                    else {
                                        keyVal=oneData3.get("DISTRIAMT").toString();
                                    }
                                    break;
                                case 23:
                                    keyVal = bDate;
                                    break;
                                case 24:
                                    keyVal = oneData3.get("FEATURENO").toString();
                                    if (Check.Null(keyVal))
                                        keyVal=" ";
                                    break;
                                default:
                                    break;
                            }

                            if (keyVal != null) {
                                insColCt++;
                                if (i == 4 || i == 7) {
                                    columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                } else if (i == 10 || i == 11 || i == 13 || i == 14 || i == 15 || i == 16 || i == 21) {
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

                        insColCt = 0;

                        for (int i = 0; i < columnsVal.length; i++) {
                            if (columnsVal[i] != null) {
                                columns2[insColCt] = columnsName[i];
                                insValue2[insColCt] = columnsVal[i];
                                insColCt++;
                                if (insColCt >= insValue2.length)
                                    break;
                            }
                        }

                        InsBean ib2 = new InsBean("DCP_STOCKIN_DETAIL", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2));
                    }
                }

                insValue1 = new DataValue[] {
                        new DataValue(shopId, Types.VARCHAR),
                        new DataValue(organizationNO, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue(totPqty, Types.VARCHAR),
                        new DataValue(totAmt, Types.VARCHAR),
                        new DataValue(totCqty, Types.VARCHAR),
                        new DataValue("4", Types.VARCHAR),
                        new DataValue("2", Types.VARCHAR),
                        new DataValue("Y", Types.VARCHAR),
                        new DataValue(eId, Types.VARCHAR),
                        new DataValue(stockInNO, Types.VARCHAR),
                        new DataValue(bDate, Types.VARCHAR),
                        new DataValue(transferWarehouse, Types.VARCHAR),  //调出单的调入仓库=调入单的仓库
                        new DataValue(warehouse, Types.VARCHAR),          //调出单的调出仓库=调入单的调入仓库
                        new DataValue(request.getStockOutNo(), Types.VARCHAR),
                        new DataValue(MEMO, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(accountDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue(opNo, Types.VARCHAR),
                        new DataValue(sDate, Types.VARCHAR),
                        new DataValue(sTime, Types.VARCHAR),
                        new DataValue(totDistriAmt, Types.VARCHAR),
                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                        new DataValue(req.getChatUserId(), Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                };

                InsBean ib1 = new InsBean("DCP_STOCKIN", columns1);
                ib1.addValues(insValue1);
                this.addProcessData(new DataProcessBean(ib1));
            }

            switch (status) {
                case "2": // 确认
                    String isCreatSql="select * from DCP_STOCKOUT "
                            + " where EID='"+eId+"' and SHOPID='"+shopId+"'  and STOCKOUTNO='"+stockOutNO+"'  "
                            + " and status<>'0' and status<>'-1' ";
                    List<Map<String, Object>> stakelist=this.doQueryData(isCreatSql, null);
                    if(stakelist !=null && stakelist.isEmpty()==false) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"门店:"+shopId+ " 单号 :"+stockOutNO +" 该单已经确认！"  );
                    }
                    Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                    if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                    }
                    String isCreatSql1="select * from DCP_STOCKOUT "
                            + " where EID='"+eId+"' and SHOPID='"+shopId+"'  and STOCKOUTNO='"+stockOutNO+"'  "
                            + "  ";
                    List<Map<String, Object>> stakelist1=this.doQueryData(isCreatSql1, null);

                    sourceMenu=stakelist1.get(0).get("SOURCEMENU").toString();

                    //调拨出库时传1   换季退货时传0    次品退货时传2   其它出库传3 移仓出库传4
                    if (docType.equals("1")){
                        sql1 =" SELECT *  FROM DCP_RECEIVING  WHERE EID=? AND TRANSFER_SHOP=? AND Load_DOCNO=? ";
                        String[] conditionValues1 = { eId, shopId, stockOutNO };
                        List<Map<String, Object>> getQData1 = this.doQueryData(sql1, conditionValues1);
                        if (getQData1 == null || getQData1.isEmpty()) {
                            sql2 = this.getQuerySql2(req);
                            String[] conditionValues2 = { shopId, organizationNO, eId, stockOutNO};
                            List<Map<String, Object>> getQData2 = this.doQueryData(sql2, conditionValues2);
                            if (getQData2 != null && getQData2.isEmpty() == false) {
                                String transferShop ;
                                String transfer_warehouse;
                                if(docType.equals("1")) {
                                    transferShop=getQData2.get(0).get("TRANSFERSHOP").toString();
                                } else {
                                    transferShop=shopId;
                                }
                                transfer_warehouse=getQData2.get(0).get("TRANSFER_WAREHOUSE").toString();
                                String ReceivingNO = getReceivingNO(req, transferShop);

                                String[] columns1 = {
                                        "TRANSFER_SHOP","RECEIVINGNO","SHOPID","BDATE","MEMO","DOC_TYPE",
                                        "STATUS","CREATEBY","CREATE_DATE","CREATE_TIME","MODIFYBY","MODIFY_DATE",
                                        "MODIFY_TIME","CANCELBY","CANCEL_DATE","CANCEL_TIME","TOT_PQTY","TOT_AMT",
                                        "TOT_CQTY","LOAD_DOCTYPE","LOAD_DOCNO","ORGANIZATIONNO","EID",
                                        "WAREHOUSE","TOT_DISTRIAMT","RECEIPTDATE","UPDATE_TIME","TRAN_TIME"
                                };


                                String receivingBdate = PosPub.getAccountDate_SMS(dao, eId, transferShop);

                                //【ID1023848】【詹记】门店盘点单需要增加业务单据检查。根据参数设置检查哪些单据。类似于3.0闭店检查作业。
                                String Transfer_Day = PosPub.getPARA_SMS(dao,eId,shopId,"Transfer_Day");
                                if (!PosPub.isNumeric(Transfer_Day)){
                                    Transfer_Day = "7";
                                }
                                String receiptDate = PosPub.GetStringDate(sDate,Integer.parseInt(Transfer_Day));


                                DataValue[] insValue1 = new DataValue[] {
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(ReceivingNO, Types.VARCHAR),
                                        new DataValue(transferShop, Types.VARCHAR),
                                        new DataValue(receivingBdate, Types.VARCHAR),
                                        new DataValue(getQData2.get(0).get("MEMO"), Types.VARCHAR),
                                        new DataValue(getQData2.get(0).get("DOCTYPE"), Types.VARCHAR), //0-配送收货通知  1-调拨收货通知 2-统采配送通知
                                        new DataValue("6", Types.VARCHAR),//STATUS  6-待收货  7-已完成  8-已作废
                                        new DataValue(opNo, Types.VARCHAR),
                                        new DataValue(sDate, Types.VARCHAR),
                                        new DataValue(sTime, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR), // ModifyBy
                                        new DataValue("", Types.VARCHAR), // Modify_Date
                                        new DataValue("", Types.VARCHAR), // Modify_Time
                                        new DataValue("", Types.VARCHAR), // CancelBy
                                        new DataValue("", Types.VARCHAR), // Cancel_Date
                                        new DataValue("", Types.VARCHAR), // Cancel_Time
                                        new DataValue(getQData2.get(0).get("TOTPQTY"), Types.VARCHAR),
                                        new DataValue(getQData2.get(0).get("TOTAMT"), Types.VARCHAR),
                                        new DataValue(getQData2.get(0).get("TOTCQTY"), Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(getQData2.get(0).get("STOCKOUTNO"), Types.VARCHAR),
                                        new DataValue(transferShop, Types.VARCHAR),
                                        new DataValue(getQData2.get(0).get("EID"), Types.VARCHAR),
                                        new DataValue(getQData2.get(0).get("TRANSFER_WAREHOUSE"), Types.VARCHAR),
                                        new DataValue(getQData2.get(0).get("TOT_DISTRIAMT"), Types.VARCHAR),
                                        new DataValue(receiptDate,Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),
                                        new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR),

                                };

                                //收货通知单主表
                                InsBean ib1 = new InsBean("DCP_RECEIVING", columns1);
                                ib1.addValues(insValue1);
                                this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

                                sql3 = this.getQuerySql3(req);
                                String[] conditionValues3 = { shopId, organizationNO, eId, stockOutNO };
                                List<Map<String, Object>> getQData3 = this.doQueryData(sql3, conditionValues3);

                                if (getQData3 != null && !getQData3.isEmpty()) {
                                    for (Map<String, Object> oneData3 : getQData3) {
                                        int insColCt = 0;
                                        String[] columnsName = { "ReceivingNO", "organizationNO", "EID", "SHOPID", "item",
                                                "OType", "OFNO", "OItem", "PLUNO", "Punit", "PQTY", "BASEUNIT",
                                                "BASEQTY", "unit_Ratio", "Price", "AMT","WAREHOUSE","PLU_MEMO","BATCH_NO","PROD_DATE",
                                                "DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO" };

                                        DataValue[] columnsVal = new DataValue[columnsName.length];

                                        for (int i = 0; i < columnsVal.length; i++) {
                                            String keyVal = null;
                                            switch (i) {
                                                case 0:
                                                    keyVal = ReceivingNO;
                                                    break;
                                                case 1:
                                                    keyVal = transferShop;
                                                    break;
                                                case 2:
                                                    keyVal = eId;
                                                    break;
                                                case 3:
                                                    keyVal = transferShop;
                                                    break;
                                                case 4:
                                                    keyVal = oneData3.get("ITEM").toString(); // item
                                                    break;
                                                case 5:
                                                    keyVal = "1"; // OType
                                                    break;
                                                case 6:
                                                    keyVal =  getQData2.get(0).get("STOCKOUTNO").toString(); // OFNO
                                                    break;
                                                case 7:
                                                    String oItem = oneData3.get("ITEM").toString(); // OItem
                                                    if (oItem.length() == 0) {
                                                        oItem = "0";
                                                    }
                                                    keyVal = oItem; // oItem
                                                    break;
                                                case 8:
                                                    keyVal = oneData3.get("PLUNO").toString(); // PLUNO
                                                    break;
                                                case 9:
                                                    keyVal = oneData3.get("PUNIT").toString(); // Punit
                                                    break;
                                                case 10:
                                                    keyVal = oneData3.get("PQTY").toString(); // PQTY
                                                    break;
                                                case 11:
                                                    keyVal = oneData3.get("BASEUNIT").toString(); // WUNIT
                                                    break;
                                                case 12:
                                                    keyVal = oneData3.get("BASEQTY").toString(); // WQTY
                                                    break;
                                                case 13:
                                                    keyVal = oneData3.get("UNITRATIO").toString(); // unit_Ratio
                                                    break;
                                                case 14:
                                                    keyVal = oneData3.get("PRICE").toString(); // Price
                                                    break;
                                                case 15:
                                                    keyVal = oneData3.get("AMT").toString(); // AMT
                                                    break;
                                                case 16:
                                                    keyVal = transfer_warehouse; // 移入仓库/调入仓库
                                                    break;
                                                case 17:
                                                    keyVal = oneData3.get("PLUMEMO").toString();
                                                    break;
                                                case 18:
                                                    keyVal = oneData3.get("BATCH_NO").toString();
                                                    break;
                                                case 19:
                                                    keyVal =oneData3.get("PROD_DATE").toString();
                                                    break;
                                                case 20:
                                                    if(oneData3.get("DISTRIPRICE")==null || Check.Null(oneData3.get("DISTRIPRICE").toString()) ){
                                                        keyVal = "0";
                                                    }
                                                    else {
                                                        keyVal=oneData3.get("DISTRIPRICE").toString();
                                                    }
                                                    break;
                                                case 21:
                                                    keyVal =oneData3.get("DISTRIAMT").toString();
                                                    break;
                                                case 22:
                                                    keyVal = receivingBdate;
                                                    break;
                                                case 23:
                                                    keyVal = oneData3.get("FEATURENO").toString();
                                                    if (Check.Null(keyVal))
                                                        keyVal=" ";
                                                    break;
                                                default:
                                                    break;
                                            }

                                            if (keyVal != null) {
                                                insColCt++;
                                                if (i == 4 || i == 7) {
                                                    columnsVal[i] = new DataValue(keyVal, Types.INTEGER);
                                                } else if (i == 10 || i == 12 || i == 13 || i == 14 || i == 15 || i == 20) {
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
                                                columns2[insColCt] = columnsName[i];
                                                insValue2[insColCt] = columnsVal[i];
                                                insColCt++;
                                                if (insColCt >= insValue2.length)
                                                    break;
                                            }
                                        }

                                        //收货通知单子表
                                        InsBean ib2 = new InsBean("DCP_RECEIVING_DETAIL", columns2);
                                        ib2.addValues(insValue2);
                                        this.addProcessData(new DataProcessBean(ib2));
                                    }
                                }
                            }
                            // 注释下面提交代码， 因底下的库存流水有判断在途仓是否一致，这里直接提交会有问题   BY JZMA 20190606
                            //this.doExecuteDataToDB();

                            // 只有在上述两个插入动作成功以后，才会执行下面的更新操作
                            ub1 = new UptBean("DCP_StockOut");
                            ub1.addUpdateValue("Status", new DataValue("2", Types.VARCHAR));
                            ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                            ub1.addUpdateValue("ConfirmBy", new DataValue(opNo, Types.VARCHAR));
                            ub1.addUpdateValue("Confirm_Date", new DataValue(sDate, Types.VARCHAR));
                            ub1.addUpdateValue("Confirm_Time", new DataValue(sTime, Types.VARCHAR));
                            ub1.addUpdateValue("accountBy", new DataValue(opNo, Types.VARCHAR));
                            ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                            ub1.addUpdateValue("account_Time", new DataValue(sTime, Types.VARCHAR));
                            ub1.addUpdateValue("SUBMITBY", new DataValue(opNo, Types.VARCHAR));
                            ub1.addUpdateValue("SUBMIT_DATE", new DataValue(sDate, Types.VARCHAR));
                            ub1.addUpdateValue("SUBMIT_TIME", new DataValue(sTime, Types.VARCHAR));
                            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                            // condition
                            ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                            this.addProcessData(new DataProcessBean(ub1));
                            //以下数据库提交注释 BY JZMA 2019-7-23
                            //this.doExecuteDataToDB();
                        } else {
                            // throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"该出库单已做过确认");

                            ub1 = new UptBean("DCP_StockOut");
                            ub1.addUpdateValue("Status", new DataValue("2", Types.VARCHAR));
                            ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                            ub1.addUpdateValue("ConfirmBy", new DataValue(opNo, Types.VARCHAR));
                            ub1.addUpdateValue("Confirm_Date", new DataValue(sDate, Types.VARCHAR));
                            ub1.addUpdateValue("Confirm_Time", new DataValue(sTime, Types.VARCHAR));
                            ub1.addUpdateValue("accountBy", new DataValue(opNo, Types.VARCHAR));
                            ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                            ub1.addUpdateValue("account_Time", new DataValue(sTime, Types.VARCHAR));
                            ub1.addUpdateValue("SUBMITBY", new DataValue(opNo, Types.VARCHAR));
                            ub1.addUpdateValue("SUBMIT_DATE", new DataValue(sDate, Types.VARCHAR));
                            ub1.addUpdateValue("SUBMIT_TIME", new DataValue(sTime, Types.VARCHAR));
                            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                            // condition
                            ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                            this.addProcessData(new DataProcessBean(ub1));
                        }
                    } else {
                        //如果是退货出库并且ofno有值的需要去判断一下原单的数量等是否满足，并且需要更新原单的已退货数量
                        if (docType.equals("0") && ofNO!=null && !ofNO.isEmpty()) {
                            String sqlstockin=" select * from DCP_STOCKIN_DETAIL A,DCP_STOCKOUT_DETAIL B "
                                    + " where A.EID='"+eId+"' and A.SHOPID='"+shopId+"' and A.STOCKINNO='"+ofNO+"' and A.EID=B.EID and A.SHOPID=B.SHOPID  "
                                    + " and B.STOCKOUTNO='"+stockOutNO+"' and A.item=B.oitem  and A.baseqty<(nvl(A.RETWQTY,0)+B.baseqty)  ";

                            List<Map<String, Object>> stokindetail=this.doQueryData(sqlstockin, null);
                            if (stokindetail != null && !stokindetail.isEmpty()) {
                                res.setSuccess(false);
                                res.setServiceDescription("商品:"+stokindetail.get(0).get("PLUNO").toString()+"大于可退货数量");
                                return;
                            }
                            sql3 = this.getQuerySql3(req);
                            String[] conditionValues3 = { shopId, organizationNO, eId, stockOutNO };
                            List<Map<String, Object>> getQData3 = this.doQueryData(sql3, conditionValues3);
                            if (getQData3 != null && getQData3.isEmpty() == false) {
                                for (Map<String, Object> oneData3 : getQData3) {
                                    //这里要更新原单的RETWQTY
                                    UptBean upstockin=new UptBean("DCP_STOCKIN_DETAIL");
                                    upstockin.addUpdateValue("RETWQTY", new DataValue(Float.parseFloat(oneData3.get("BASEQTY").toString()), Types.FLOAT, DataValue.DataExpression.UpdateSelf));
                                    // condition
                                    upstockin.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                    upstockin.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    upstockin.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                    upstockin.addCondition("STOCKINNO", new DataValue(ofNO, Types.VARCHAR));
                                    upstockin.addCondition("OITEM", new DataValue(Integer.parseInt(oneData3.get("OITEM").toString()), Types.INTEGER));
                                    this.addProcessData(new DataProcessBean(upstockin));
                                }
                            }
                        }

                        ub1 = new UptBean("DCP_StockOut");
                        if (docType.equals("4")) {
                            ub1.addUpdateValue("Status", new DataValue("3", Types.VARCHAR));
                        } else {
                            ub1.addUpdateValue("Status", new DataValue("2", Types.VARCHAR));
                        }

                        // add value
                        ub1.addUpdateValue("ACCOUNT_CHATUSERID", new DataValue(req.getChatUserId(), Types.VARCHAR));
                        ub1.addUpdateValue("ConfirmBy", new DataValue(opNo, Types.VARCHAR));
                        ub1.addUpdateValue("Confirm_Date", new DataValue(sDate, Types.VARCHAR));
                        ub1.addUpdateValue("Confirm_Time", new DataValue(sTime, Types.VARCHAR));
                        ub1.addUpdateValue("accountBy", new DataValue(opNo, Types.VARCHAR));
                        ub1.addUpdateValue("account_Date", new DataValue(accountDate, Types.VARCHAR));
                        ub1.addUpdateValue("account_Time", new DataValue(sTime, Types.VARCHAR));
                        ub1.addUpdateValue("SUBMITBY", new DataValue(opNo, Types.VARCHAR));
                        ub1.addUpdateValue("SUBMIT_DATE", new DataValue(sDate, Types.VARCHAR));
                        ub1.addUpdateValue("SUBMIT_TIME", new DataValue(sTime, Types.VARCHAR));
                        ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                        ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                        // condition
                        ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                        ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub1));
                    }

                    String sql = this.getDCP_StockOut_Sql(req,eId,shopId,stockOutNO,langType);
                    List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
                    String dingStatus = getQDataDetail.get(0).get("STATUS").toString(); //判断数据库里的status，if ==-1 ，已经审批中，直接返回
                    //判断服务发起调用方，状态==-1且是云中台门店管理发起，直接返回审核中
                    if (Check.Null(oEId) && dingStatus.equals("-1")) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批中，请重新查询 ");
                    }

                    //其他出库单钉钉审批判断，启用钉钉审批  BY JZMA 20191218
                    if (docType.equals("3") && status.equals("2") && dingStatus.equals("0")) {
                        //判断是否启用钉钉审批
                        String sql_ding=" select b.def_userid,b.def_deptid,c.approvedbyid,c.approvedbydeptid,a.templateno,d.chsmsg,d.chtmsg from DCP_DING_FUNC a "
                                + " inner join DCP_DING_FUNC_SHOP b on a.EID=b.EID and a.funcno=b.funcno and a.status='100' "
                                + " inner join DCP_DING_FUNC_SHOP_APPROVEDBY c on a.EID=c.EID and a.funcno=c.funcno and b.SHOPID=c.SHOPID and c.status='100' "
                                + " left join DCP_MODULAR d on a.EID=d.EID and a.funcno=d.modularno "
                                + " where a.EID='"+eId+"' and a.funcno='120109'  and c.SHOPID='"+shopId+"'  ";

                        List<Map<String, Object>> getQData = this.doQueryData(sql_ding, null);
                        if(getQData !=null && getQData.isEmpty()==false) {
                            String funcName= getQData.get(0).get("CHSMSG").toString();
                            if (req.getLangType().equals("zh_TW")) funcName= getQData.get(0).get("CHTMSG").toString();

                            //模板编号检查
                            String processCode = getQData.get(0).get("TEMPLATENO").toString();
                            if (Check.Null(processCode)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"钉钉审批模板编号未维护  ");
                            }

                            //审批人获取(多审批人时只需一人同意)
                            List<String> approvedbyIds = new ArrayList<>();
                            int approvedbyCount =0 ;
                            for (Map<String, Object> oneData : getQData) {
                                approvedbyIds.add(oneData.get("APPROVEDBYID").toString());
                                approvedbyCount++;
                            }
                            if (approvedbyCount==0) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"钉钉审批人未维护  ");
                            }

                            //申请人获取
                            String originatorUserId ="";
                            String originatorUserName = "";
                            long deptId = 0;
                            sql ="select userid,USERNAME,deptid from DCP_DING_USERSET where EID='"+eId+"' and opno='"+req.getOpNO()+"'  " ;
                            List<Map<String, Object>> getUserId = this.doQueryData(sql, null);
                            if(getUserId !=null && getUserId.isEmpty()==false) {
                                originatorUserId = getUserId.get(0).get("USERID").toString();
                                originatorUserName = getUserId.get(0).get("USERNAME").toString();
                                deptId =Long.parseLong(getUserId.get(0).get("DEPTID").toString());

                            } else {
                                originatorUserId = getQData.get(0).get("DEF_USERID").toString();
                                sql ="select userid,USERNAME,deptid from DCP_DING_USERSET where EID='"+eId+"' and userid='"+originatorUserId+"'  " ;
                                List<Map<String, Object>> getUserName = this.doQueryData(sql, null);
                                if(getUserName !=null && getUserName.isEmpty()==false) {
                                    originatorUserName = getUserName.get(0).get("USERNAME").toString();
                                }else{
                                    originatorUserName=req.getOpName();
                                }

                                String tempDEPTID=getQData.get(0).get("DEF_DEPTID").toString();
                                if (PosPub.isNumeric(tempDEPTID)) {
                                    deptId = Long.parseLong(getQData.get(0).get("DEF_DEPTID").toString());
                                }

                            }
                            if (Check.Null(originatorUserId) || deptId==0L ) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"钉钉申请人ID未维护  ");
                            }


                            String bDate = getQDataDetail.get(0).get("BDATE").toString();
                            bDate= bDate.substring(0,4)+"-"+bDate.substring(4,6)+"-"+bDate.substring(6,8);
                            String reasonName = getQDataDetail.get(0).get("REASON_NAME").toString();
                            String memo = getQDataDetail.get(0).get("MEMO").toString();

                            // 审批流表单参数，设置各表单项值
                            List<OapiProcessinstanceCreateRequest.FormComponentValueVo> formComponentValues = new ArrayList<OapiProcessinstanceCreateRequest.FormComponentValueVo>();
                            // 单行输入框
                            OapiProcessinstanceCreateRequest.FormComponentValueVo vo1 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            vo1.setName("门店");
                            vo1.setValue(shopName);
                            formComponentValues.add(vo1);

                            OapiProcessinstanceCreateRequest.FormComponentValueVo vo2 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            vo2.setName("单据类型");
                            vo2.setValue("其他出库单");
                            formComponentValues.add(vo2);

                            OapiProcessinstanceCreateRequest.FormComponentValueVo vo3 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            vo3.setName("单据编号");
                            vo3.setValue(stockOutNO);
                            formComponentValues.add(vo3);

                            OapiProcessinstanceCreateRequest.FormComponentValueVo vo4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            vo4.setName("单据日期");
                            vo4.setValue(bDate);
                            formComponentValues.add(vo4);

                            String originatorUser = req.getOpName()+"("+req.getOpNO()+")";
                            if (!Check.Null(originatorUser)) {
                                OapiProcessinstanceCreateRequest.FormComponentValueVo vo5 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                                vo5.setName("申请人");
                                vo5.setValue(originatorUser);
                                formComponentValues.add(vo5);
                            }

                            if (!Check.Null(reasonName)) {
                                OapiProcessinstanceCreateRequest.FormComponentValueVo vo6 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                                vo6.setName("申请原因");
                                vo6.setValue(reasonName);
                                formComponentValues.add(vo6);
                            }

                            if (!Check.Null(memo)) {
                                OapiProcessinstanceCreateRequest.FormComponentValueVo vo7 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                                vo7.setName("备注说明");
                                vo7.setValue(memo);
                                formComponentValues.add(vo7);
                            }

                            List <Object> listvo = new ArrayList<>();
                            for (Map<String, Object> oneData : getQDataDetail) {
                                String pluName = oneData.get("PLU_NAME").toString();
                                String featureName = oneData.get("FEATURENAME").toString();
                                pluName = pluName+featureName;  //特征码
                                String pQty  = oneData.get("PQTY").toString();
                                String unitName = oneData.get("UNIT_NAME").toString();
                                String price = oneData.get("PRICE").toString();
                                String amt = oneData.get("AMT").toString();

                                // 明细-单行输入框
                                if (Check.Null(pluName)) pluName=" ";
                                OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName1 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                                ItemName1.setName("品名");
                                ItemName1.setValue(pluName);

                                OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName2 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                                ItemName2.setName("数量");
                                ItemName2.setValue(pQty);

                                if (Check.Null(unitName)) unitName=" ";
                                OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName3 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                                ItemName3.setName("单位");
                                ItemName3.setValue(unitName);

                                if (Check.Null(price)) price="0";
                                OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                                ItemName4.setName("售价");
                                ItemName4.setValue(price);

                                if (Check.Null(amt)) amt="0";
                                OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName5 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                                ItemName5.setName("金额");
                                ItemName5.setValue(amt);
                                listvo.add(Arrays.asList(ItemName1,ItemName2,ItemName3,ItemName4,ItemName5));
                            }

                            OapiProcessinstanceCreateRequest.FormComponentValueVo vo = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            vo.setName("商品明细");
                            vo.setValue(JSON.toJSONString(listvo));
                            formComponentValues.add(vo);


                            //审批人多人或单人赋值
                            OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo approversV2 = new OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo();
                            List<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo> approversV2s = new ArrayList<OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo>();
                            approversV2.setUserIds(approvedbyIds);
                            if (approvedbyCount==1) {
                                approversV2.setTaskActionType("NONE");
                            } else {
                                approversV2.setTaskActionType("OR");
                            }
                            approversV2s.add(approversV2);

                            //获取钉钉平台token
                            DCP_DingCallBack dingCallBack = new DCP_DingCallBack();
                            Map<String,String> accessTokenMap = dingCallBack.getDingAccessToken(dao, eId, false);
                            String accessToken = accessTokenMap.getOrDefault("token", "");
                            if (Check.Null(accessToken)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, accessTokenMap.getOrDefault("errmsg", ""));
                            }

                            //钉钉审批接口赋值
                            DefaultDingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/processinstance/create");
                            OapiProcessinstanceCreateRequest dingDingRequest = new OapiProcessinstanceCreateRequest();
                            dingDingRequest.setProcessCode(processCode);
                            //long agentId = 216476129L;
                            //request.setAgentId(agentId);
                            dingDingRequest.setFormComponentValues(formComponentValues);
                            dingDingRequest.setApproversV2(approversV2s);
                            dingDingRequest.setOriginatorUserId(originatorUserId);
                            dingDingRequest.setDeptId(deptId);
                            OapiProcessinstanceCreateResponse response = client.execute(dingDingRequest, accessToken);
                            Long errcode = response.getErrcode();
                            //accessToken异常时，强制刷新并重新调用
                            if (errcode==88L||errcode==40014L ) {
                                //获取钉钉平台token
                                accessTokenMap =  dingCallBack.getDingAccessToken(dao, eId, true);
                                accessToken = accessTokenMap.getOrDefault("token", "");
                                if (Check.Null(accessToken)) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, accessTokenMap.getOrDefault("errmsg", ""));
                                }
                                response = client.execute(dingDingRequest,accessToken);
                            }

                            errcode = response.getErrcode();
                            String errmsg = response.getErrmsg();
                            String processId = response.getProcessInstanceId();

                            if (errcode!=0L ) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批提交失败，错误代码："+errcode+" 错误原因："+errmsg);
                            } else {
                                //更新其他出库单单据状态 == -1，钉钉审批
                                UptBean ub = new UptBean("DCP_STOCKOUT");
                                ub.addUpdateValue("STATUS", new DataValue("-1", Types.VARCHAR));
                                ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                                ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                                ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                ub.addCondition("ORGANIZATIONNO", new DataValue(organizationNO, Types.VARCHAR));
                                ub.addCondition("STOCKOUTNO", new DataValue(stockOutNO, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(ub));


                                //新增钉钉审批单身
                                for (Map<String, Object> oneData : getQDataDetail) {
                                    String[] columns_detail = {
                                            "EID", "PROCESSID","COMPANYID","SHOPID","BILLNO",
                                            "ITEM","PLUNO","PLUNAME","UNITNAME","QTY","PRICE","AMT","DISCAMT","FEATURENAME"
                                    };
                                    String item =oneData.get("ITEM").toString();
                                    String pluNo=oneData.get("PLUNO").toString();
                                    String pluName=oneData.get("PLU_NAME").toString();
                                    String unitName=oneData.get("UNIT_NAME").toString();
                                    String qty=oneData.get("PQTY").toString();
                                    String price=oneData.get("PRICE").toString();
                                    String amt=oneData.get("AMT").toString();
                                    String discAmt="0";
                                    String featureName=oneData.get("FEATURENAME").toString();

                                    DataValue[] insValue = new DataValue[]{
                                            new DataValue(eId, Types.VARCHAR),
                                            new DataValue(processId, Types.VARCHAR),
                                            new DataValue("", Types.VARCHAR),
                                            new DataValue(shopId, Types.VARCHAR),
                                            new DataValue(stockOutNO, Types.VARCHAR),
                                            new DataValue(item, Types.VARCHAR),
                                            new DataValue(pluNo, Types.VARCHAR),
                                            new DataValue(pluName, Types.VARCHAR),
                                            new DataValue(unitName, Types.VARCHAR),
                                            new DataValue(qty, Types.VARCHAR),
                                            new DataValue(price, Types.VARCHAR),
                                            new DataValue(amt, Types.VARCHAR),
                                            new DataValue(discAmt, Types.VARCHAR),
                                            new DataValue(featureName, Types.VARCHAR)
                                    };
                                    InsBean ib_detail = new InsBean("DCP_DING_APPROVE_DETAIL", columns_detail);
                                    ib_detail.addValues(insValue);
                                    this.addProcessData(new DataProcessBean(ib_detail));
                                }

                                //获取当前时间
                                SimpleDateFormat timeSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String time =  timeSDF.format(new Date());

                                //新增钉钉审批单头
                                String[] columns = {
                                        "EID","PROCESSID","COMPANYID","SHOPID","BILLNO","BILLTYPE","FUNCNO","FUNCNAME",
                                        "CREATETIME","CREATEOPID","CREATEOPNAME","APPLYREASON","REJECTREASON",
                                        "CHECKOPID","CHECKOPNAME","CHECKTIME",
                                        "STATUS","PROCESS_STATUS"};
                                DataValue[] insValue = new DataValue[]{
                                        new DataValue(eId, Types.VARCHAR),
                                        new DataValue(processId, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue(shopId, Types.VARCHAR),
                                        new DataValue(stockOutNO, Types.VARCHAR),
                                        new DataValue("DCP", Types.VARCHAR),
                                        new DataValue("120109", Types.VARCHAR),
                                        new DataValue(funcName, Types.VARCHAR),
                                        new DataValue(time, Types.DATE),
                                        new DataValue(originatorUserId, Types.VARCHAR),     //钉钉用户ID
                                        new DataValue(originatorUserName, Types.VARCHAR),   //钉钉用户名称
                                        new DataValue(reasonName, Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("", Types.VARCHAR),
                                        new DataValue("0", Types.VARCHAR),
                                        new DataValue("N", Types.VARCHAR),
                                };
                                InsBean ib = new InsBean("DCP_DING_APPROVE", columns);
                                ib.addValues(insValue);
                                this.addProcessData(new DataProcessBean(ib));

                                this.doExecuteDataToDB();

                                res.setSuccess(true);
                                res.setServiceStatus("000");
                                res.setServiceDescription("服务执行成功");
                                return;
                            }
                        }
                    }


                    // 新增调拨扣库存方式	  1.调出门店发货后扣库存       2.调入门店收货后扣库存
                    String Transfer_Stock= PosPub.getPARA_SMS(dao, eId, shopId, "Transfer_Stock");

                    if (Enable_InTransit.equals("Y")|| Check.Null(Transfer_Stock) || Transfer_Stock.equals("1")
                            || (Transfer_Stock.equals("2") && !docType.equals("1"))) {
                        List<Map<String, Object>> getQData_checkStockDetail = null;
                        if(getQData_checkStockDetail == null || getQData_checkStockDetail.isEmpty()) {
                            if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
                                for (Map<String, Object> oneData : getQDataDetail) {
                                    //String stockDocType = "";
                                    /*switch (docType) {
                                        case "0":
                                        case "2":
                                            stockDocType = "03";//退货出库
                                            break;
                                        case "1":
                                            stockDocType = "04";//调拨出库
                                            break;
                                        case "3":
                                            stockDocType = "15";//其他出库
                                            break;
                                        case "4":
                                            stockDocType = "18";//移仓出库
                                            break;
                                    }*/

                                    String stockWarehouse=oneData.get("WAREHOUSE").toString();
                                    if (inv_cost_warehouse.equals(stockWarehouse) && Enable_InTransit.equals("Y") ) {
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400,"出货仓库 和 在途成本仓库 一致("+stockWarehouse+"),不能出库！");
                                    }
                                    //判断仓库不能为空或空格  BY JZMA 20191118
                                    if (Check.Null(stockWarehouse) || Check.Null(stockWarehouse.trim())
                                            || stockWarehouse.trim().isEmpty()) {
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");
                                    }

                                    String featureNo = oneData.get("FEATURENO").toString();  ///  特征码为空给空格
                                    if (Check.Null(featureNo))
                                        featureNo=" ";

                                    BcReq bcReq=new BcReq();
                                    bcReq.setServiceType("StockOutRefundProcess");
                                    bcReq.setDocType(docType);
                                    bcReq.setBillType("15");

                                    BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                                    String bType = bcMap.getBType();
                                    String costCode = bcMap.getCostCode();
                                    if(Check.Null(bType)||Check.Null(costCode)){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                                    }

                                    String procedure="SP_DCP_STOCKCHANGE_VX";
                                    Map<Integer,Object> inputParameterOut = new HashMap<Integer, Object>();
                                    inputParameterOut.put(1,eId);                                      //--企业ID
                                    inputParameterOut.put(2,null);
                                    inputParameterOut.put(3,organizationNO);                           //--组织
                                    //【ID1038173】【嘉华3.0】版本3311试吃出库单，1、红冲单单据类型记录的退货出库，2、单号QTCK2023122500001，
                                    // 和红冲单QTCK2023122500002，进销存明细表中是反的， by jinzma 20231228
                                    //inputParameterOut.put(3,stockDocType);                             //--单据类型
                                    inputParameterOut.put(4, bType);
                                    inputParameterOut.put(5, costCode);
                                    inputParameterOut.put(6,"15");                                     //--单据类型
                                    inputParameterOut.put(7,oneData.get("STOCKOUTNO").toString());	   //--单据号
                                    inputParameterOut.put(8,oneData.get("ITEM").toString());           //--单据行号
                                    inputParameterOut.put(9,"0");
                                    inputParameterOut.put(10,"-1");                                     //--异动方向 1=加库存 -1=减库存
                                    inputParameterOut.put(11,oneData.get("BDATE").toString());          //--营业日期 yyyy-MM-dd
                                    inputParameterOut.put(12,oneData.get("PLUNO").toString());          //--品号
                                    inputParameterOut.put(13,featureNo);                                //--特征码
                                    inputParameterOut.put(14,stockWarehouse);                          //--仓库
                                    inputParameterOut.put(15,oneData.get("BATCH_NO").toString());      //--批号
                                    inputParameterOut.put(16,oneData.get("LOCATION").toString());
                                    inputParameterOut.put(17,oneData.get("PUNIT").toString());         //--交易单位
                                    inputParameterOut.put(18,oneData.get("PQTY").toString());          //--交易数量
                                    inputParameterOut.put(19,oneData.get("BASEUNIT").toString());      //--基准单位
                                    inputParameterOut.put(20,oneData.get("BASEQTY").toString());       //--基准数量
                                    inputParameterOut.put(21,oneData.get("UNIT_RATIO").toString());    //--换算比例
                                    inputParameterOut.put(22,oneData.get("PRICE").toString());         //--零售价
                                    inputParameterOut.put(23,oneData.get("AMT").toString());           //--零售金额
                                    inputParameterOut.put(24,oneData.get("DISTRIPRICE").toString());   //--进货价
                                    inputParameterOut.put(25,oneData.get("DISTRIAMT").toString());     //--进货金额
                                    inputParameterOut.put(26,accountDate);                             //--入账日期 yyyy-MM-dd
                                    inputParameterOut.put(27,oneData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                                    inputParameterOut.put(28,oneData.get("BDATE").toString());         //--单据日期
                                    inputParameterOut.put(29,"");                                      //--异动原因
                                    inputParameterOut.put(30,"");                                      //--异动描述
                                    inputParameterOut.put(31,opNo);                                    //--操作员

                                    ProcedureBean pdbOut = new ProcedureBean(procedure, inputParameterOut);
                                    this.addProcessData(new DataProcessBean(pdbOut));

                                    //增加在库数 //如果启用在途
                                    if(Enable_InTransit.equals("Y") || docType.equals("4")){
                                        String stockDocType="";
                                        if(docType.equals("4")) {
                                            stockDocType = "19";
                                        }else {
                                            stockDocType = "12";
                                        }
                                        String stockDocNo="";
                                        if(docType.equals("4")) {
                                            stockDocNo = stockInNO; //DOCNO调整为入库单的单号
                                        } else {
                                            stockDocNo = oneData.get("STOCKOUTNO").toString();
                                        }
                                        stockWarehouse="";
                                        if(docType.equals("4")) {
                                            stockWarehouse = oneData.get("TRANSFER_WAREHOUSE").toString();//拨入仓库
                                        }else {
                                            stockWarehouse = inv_cost_warehouse;
                                        }
                                        //判断仓库不能为空或空格  BY JZMA 20191118
                                        if (Check.Null(stockWarehouse) || Check.Null(stockWarehouse.trim())
                                                || stockWarehouse.trim().isEmpty()) {
                                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");
                                        }

                                        Map<Integer,Object> inputParameterIn = new HashMap<Integer, Object>();
                                        inputParameterIn.put(1,eId);                                       //--企业ID
                                        inputParameterIn.put(2,organizationNO);                            //--组织
                                        inputParameterIn.put(3,stockDocType);                              //--单据类型
                                        inputParameterIn.put(4,stockDocNo);	                               //--单据号
                                        inputParameterIn.put(5,oneData.get("ITEM").toString());            //--单据行号
                                        inputParameterIn.put(6,"1");                                       //--异动方向 1=加库存 -1=减库存
                                        inputParameterIn.put(7,oneData.get("BDATE").toString());           //--营业日期 yyyy-MM-dd
                                        inputParameterIn.put(8,oneData.get("PLUNO").toString());           //--品号
                                        inputParameterIn.put(9,featureNo);       //--特征码
                                        inputParameterIn.put(10,stockWarehouse);                           //--仓库
                                        inputParameterIn.put(11,oneData.get("BATCH_NO").toString());       //--批号
                                        inputParameterIn.put(12,oneData.get("PUNIT").toString());          //--交易单位
                                        inputParameterIn.put(13,oneData.get("PQTY").toString());           //--交易数量
                                        inputParameterIn.put(14,oneData.get("BASEUNIT").toString());       //--基准单位
                                        inputParameterIn.put(15,oneData.get("BASEQTY").toString());        //--基准数量
                                        inputParameterIn.put(16,oneData.get("UNIT_RATIO").toString());     //--换算比例
                                        inputParameterIn.put(17,oneData.get("PRICE").toString());          //--零售价
                                        inputParameterIn.put(18,oneData.get("AMT").toString());            //--零售金额
                                        inputParameterIn.put(19,oneData.get("DISTRIPRICE").toString());    //--进货价
                                        inputParameterIn.put(20,oneData.get("DISTRIAMT").toString());      //--进货金额
                                        inputParameterIn.put(21,accountDate);                              //--入账日期 yyyy-MM-dd
                                        inputParameterIn.put(22,oneData.get("PROD_DATE").toString());      //--批号的生产日期 yyyy-MM-dd
                                        inputParameterIn.put(23,oneData.get("BDATE").toString());          //--单据日期
                                        inputParameterIn.put(24,"");                                       //--异动原因
                                        inputParameterIn.put(25,"");                                       //--异动描述
                                        inputParameterIn.put(26,opNo);                                     //--操作员

                                        ProcedureBean pdbIn = new ProcedureBean(procedure, inputParameterIn);
                                        this.addProcessData(new DataProcessBean(pdbIn));

                                    }
                                }
                            }
                        }
                    }
                    break;
                case "0": // 作废
                    break;
                case "6":

                    String stockOriginNo="";
                    String outSql="select * from DCP_STOCKOUT "
                            + " where EID='"+eId+"' and SHOPID='"+shopId+"'  and STOCKOUTNO='"+stockOutNO+"'  "
                            + " ";
                    List<Map<String, Object>> stockList=this.doQueryData(outSql, null);
                    if(CollUtil.isNotEmpty(stockList)){
                        stockOriginNo=stockList.get(0).get("STOCKOUTNO_ORIGIN").toString();
                    }

                    //增加作废单据处理逻辑：（等同于删除）
                    //1.清空字段值：原出库单红冲单号STOCKOUTNO_REFUND
                    //2.更新单据状态，作废人、作废时间，修改人，修改时间
                    ub1 = new UptBean("DCP_StockOut");
                    ub1.addUpdateValue("Status", new DataValue("6", Types.VARCHAR));
                    //ub1.addUpdateValue("STOCKOUTNO_REFUND", new DataValue("", Types.VARCHAR));
                    ub1.addUpdateValue("CancelBy", new DataValue(opNo, Types.VARCHAR));
                    ub1.addUpdateValue("Cancel_Date", new DataValue(sDate, Types.VARCHAR));
                    ub1.addUpdateValue("Cancel_Time", new DataValue(sTime, Types.VARCHAR));
                    ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
                    ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                    // condition
                    ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));

                    //更新原单的退单号
                    UptBean ub_DCP_STOCKOUT = new UptBean("DCP_STOCKOUT");
                    ub_DCP_STOCKOUT.addUpdateValue("STOCKOUTNO_REFUND", new DataValue("", Types.VARCHAR));
                    // condition
                    ub_DCP_STOCKOUT.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    ub_DCP_STOCKOUT.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                    ub_DCP_STOCKOUT.addCondition("STOCKOUTNO", new DataValue(stockOriginNo, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(ub_DCP_STOCKOUT));


                    break;
                default:
                    break;
            }



            this.doExecuteDataToDB();


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            //***********调用库存同步给三方，这是个异步，不会影响效能*****************
            try
            {
                WebHookService.stockSync(eId, shopId, stockOutNO);
            }
            catch (Exception e)
            {

            }

        } catch (Exception e) {
            //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());

            //【ID1032555】【乐沙儿3.3.0.3】在门店管理出库单据点确定时，存在负库存时的提示返回error executing work，
            // 需要能够返回SP_DCP_StockChange返回的报错，提示门店  by jinzma 20230526
            String description=e.getMessage();
            try {
                StringWriter errors = new StringWriter();
                PrintWriter pw=new PrintWriter(errors);
                e.printStackTrace(pw);

                pw.flush();
                pw.close();

                errors.flush();
                errors.close();

                description = errors.toString();

                if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
                    description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
                }

            } catch (Exception ignored) {

            }

            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutRefundProcessReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutRefundProcessReq req) throws Exception
    {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutRefundProcessReq req) throws Exception
    {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_StockOutRefundProcessReq req) throws Exception
    {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        DCP_StockOutRefundProcessReq.levelElm request = req.getRequest();

        String stockOutNO = request.getStockOutNo();
        String docType = request.getDocType();
        String status = request.getStatus();

        if (Check.Null(stockOutNO)) {
            errMsg.append("单据编号不可为空值, ");
            isFail = true;
        }

        if (Check.Null(docType)) {
            errMsg.append("单据类型不可为空值, ");
            isFail = true;
        }

        if (Check.Null(status)) {
            errMsg.append("状态不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_StockOutRefundProcessReq> getRequestType()
    {
        return new TypeToken<DCP_StockOutRefundProcessReq>(){};
    }

    @Override
    protected DCP_StockOutRefundProcessRes getResponseType()
    {
        return new DCP_StockOutRefundProcessRes();
    }


    private String getQuerySql2(DCP_StockOutRefundProcessReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(
                "SELECT TRANSFERSHOP,MEMO,DOCType,CreateBy,CreateDate,Createtime,TOTPQTY,TOTAMT,TOTCQTY,"
                        + "STOCKOUTNO,OrganizationNO,EID,TRANSFER_WAREHOUSE,WAREHOUSE,BDATE,TOT_DISTRIAMT "
                        + " from (  "
                        + " SELECT TRANSFER_SHOP as TRANSFERSHOP, MEMO, DOC_Type as DOCType, CreateBy, Create_Date as CreateDate, "
                        + " Create_time as Createtime, TOT_PQTY as TOTPQTY, TOT_AMT as TOTAMT, TOT_CQTY as TOTCQTY, STOCKOUTNO, "
                        + " OrganizationNO, EID, TRANSFER_WAREHOUSE, WAREHOUSE, BDATE,TOT_DISTRIAMT FROM DCP_STOCKOUT A "
                        + " WHERE A.SHOPID = ? AND A.OrganizationNO = ? AND A.EID = ? AND A.stockOutNO = ? "
        );
        sqlbuf.append(" ) TBL ");
        return sqlbuf.toString();
    }

    private String getQuerySql3(DCP_StockOutRefundProcessReq req) throws Exception {
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append(
                "SELECT ITEM,OITEM,PLUNO,PUNIT,PQTY,BASEUNIT,BASEQTY,UNITRATIO,PRICE,AMT,WAREHOUSE,STOCKOUTNO,"
                        + " pluMemo,BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT,featureno "
                        + " from ("
                        + " SELECT ITEM,OITEM,PLUNO,PUNIT,PQTY,BASEUNIT,BASEQTY,UNIT_RATIO as UNITRATIO,PRICE,AMT,WAREHOUSE,"
                        + " STOCKOUTNO,PLU_MEMO as pluMemo,BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT,a.featureno "
                        + " FROM DCP_STOCKOUT_DETAIL A "
                        + " WHERE A.SHOPID = ? AND A.OrganizationNO = ? AND A.EID = ? AND A.stockOutNO = ? "
        );
        sqlbuf.append(" ) TBL ");
        return sqlbuf.toString();
    }

    private String getReceivingNO(DCP_StockOutRefundProcessReq req, String transferShop) throws Exception {

        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
         */
        StringBuffer sqlbuf = new StringBuffer();
        String ReceivingNO = null;
        String eId = req.geteId();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, transferShop);

        sqlbuf.append(""
                + " select max(ReceivingNO) as ReceivingNO from DCP_RECEIVING"
                + " WHERE organizationNO = '"+transferShop +"'  "
                + " AND EID='"+eId +"' AND SHOPID='"+transferShop +"' "
                + " and ReceivingNO like 'SHTZ" + bDate + "%%' ");

        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), null);

        if (getQData != null && !getQData.isEmpty()) {
            ReceivingNO = getQData.get(0).get("RECEIVINGNO").toString();
            if (ReceivingNO != null && ReceivingNO.length() > 0) {
                long i;
                ReceivingNO = ReceivingNO.substring(4);
                i = Long.parseLong(ReceivingNO) + 1;
                ReceivingNO = i + "";
                ReceivingNO = "SHTZ" + ReceivingNO;
            } else {
                ReceivingNO = "SHTZ" + bDate + "00001";
            }
        } else {
            ReceivingNO = "SHTZ" + bDate + "00001";
        }

        return ReceivingNO;
    }

    private String getDCP_StockOut_Sql(DCP_StockOutRefundProcessReq req,String eId,String shopNo,String stockOutNo,String langType) throws Exception {
        String sql = " select a.stockoutno,a.bdate,doc_type,a.otype,a.ofno,delivery_no,memo,createby,create_date,create_time,accountby,"
                + " account_date,account_time,item,oitem,b.pluno,b.featureno,punit,pqty,baseunit,unit_ratio,baseqty,price,amt,batch_no,"
                + " prod_date,distriprice,transfer_shop,load_doctype,load_docno,b.warehouse,transfer_warehouse,c.reason_name,d.plu_name,"
                + " e.uname,a.status,fn.featurename,B.distriAMT,b.MES_LOCATION as location from dcp_stockout a "
                + " inner join dcp_stockout_detail b on a.stockoutno=b.stockoutno and a.eid=b.eid and a.organizationno=b.organizationno "
                + " and a.bdate=b.bdate"
                + " left join dcp_reason_lang c on a.eid=c.eid and a.bsno=c.bsno and c.bstype='4' and c.lang_type='"+langType+"' "
                + " left join dcp_goods_lang d on a.eid=d.eid and b.pluno=d.pluno and d.lang_type='"+langType+"' "
                + " left join dcp_unit_lang e on a.eid = e.eid and b.punit=e.unit and e.lang_type='"+langType+"' "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno  and fn.lang_type='"+langType+"'"
                + " where a.eid='"+eId+"' and a.organizationno='"+shopNo+"' and a.stockoutno='"+stockOutNo+"' "
                + " " ;

        return sql;
    }

    private String getStockInNO(DCP_StockOutRefundProcessReq req) throws Exception {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果doctype=0 则固定编码PSSH  如果doctype=1 则固定码为DBSH
         */
        DCP_StockOutRefundProcessReq.levelElm request=req.getRequest();
        String stockInNo = null;
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String docType = request.getDocType();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String[] conditionValues = { organizationNO, eId, shopId, docType }; // 查询要货单号
        StringBuffer sqlbuf = new StringBuffer();

        if (docType.equals("4") ) {
            stockInNo = "YCSH" + bDate;
        }
        sqlbuf.append(" "
                + " select max(stockInNO) as stockInNO from DCP_STOCKIN"
                + " where OrganizationNO = ? and EID = ? and SHOPID = ? "
                + " and stockInNO like '%%" + stockInNo + "%%' and doc_Type=?"
        );
        List<Map<String, Object>> getQData = this.doQueryData(sqlbuf.toString(), conditionValues);
        if (getQData != null && !getQData.isEmpty()) {
            stockInNo = getQData.get(0).get("STOCKINNO").toString();
            if (stockInNo != null && stockInNo.length() > 0) {
                long i;
                stockInNo = stockInNo.substring(4);
                i = Long.parseLong(stockInNo) + 1;
                stockInNo = i + "";
                if (docType.equals("4")){
                    stockInNo = "YCSH" + stockInNo;
                }
            } else {
                if (docType.equals("4")){
                    stockInNo = "YCSH" + bDate + "00001";
                }
            }
        } else {
            if (docType.equals("4")){
                stockInNo = "YCSH" + bDate + "00001";
            }
        }
        return stockInNo;
    }


}
