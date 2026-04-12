package com.dsc.spos.service.imp.json;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest;
import com.dingtalk.api.request.OapiProcessinstanceCreateRequest.ProcessInstanceApproverVo;
import com.dingtalk.api.response.OapiProcessinstanceCreateResponse;
import com.dsc.spos.dao.*;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockOutProcessReq;
import com.dsc.spos.json.cust.req.DCP_StockOutProcessReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataProcessRes;
import com.dsc.spos.json.cust.res.DCP_StockOutProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.batchLocation.BatchLocationPlu;
import com.dsc.spos.utils.batchLocation.BatchLocationStockAlloc;
import com.dsc.spos.utils.batchLocation.WarehouseLocationPlu;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.dsc.spos.utils.ding.DCP_DingCallBack;
import com.dsc.spos.utils.transitStock.TransitStockAdjust;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服務函數：StockOutProcess
 * 說明：出货单处理
 * 服务说明：出货单处理
 *
 * @author panjing
 * @since 2016-09-20
 */
public class DCP_StockOutProcess extends SPosAdvanceService<DCP_StockOutProcessReq, DCP_StockOutProcessRes> {
    @Override
    protected boolean isVerifyFail(DCP_StockOutProcessReq req) throws Exception {

        return false;
    }

    @Override
    protected TypeToken<DCP_StockOutProcessReq> getRequestType() {
        return new TypeToken<DCP_StockOutProcessReq>() {
        };
    }

    @Override
    protected DCP_StockOutProcessRes getResponseType() {
        return new DCP_StockOutProcessRes();
    }

    @Override
    protected void processDUID(DCP_StockOutProcessReq req, DCP_StockOutProcessRes res) throws Exception {
        //try {
        levelElm request = req.getRequest();
        String shopId = "";
        String shopName = req.getShopName();
        String organizationNO = "";
        String eId = "";
        String stockOutNO = request.getStockOutNo();
        String stockInNO = "";
        String docType = request.getDocType();
        String ofNO = request.getOfNo();
        String status = request.getStatus();
        String sourceMenu="";
        String inv_cost_warehouse = "";
        String langType = "";
        String opNo = "";
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());
        String nowDateTime = DateFormatUtils.getNowDateTime();

        //钉钉JOB调用
        String oEId = request.getOEId();
        String oShopId = request.getOShopId();
        String o_createBy = request.getO_createBy();
        String o_inv_cost_warehouse = request.getO_inv_cost_warehouse();
        String o_langType = request.getO_langType();

        //钉钉JOB调入
        if (!Check.Null(oEId)) {
            shopId = oShopId;
            organizationNO = oShopId;
            eId = oEId;
            inv_cost_warehouse = o_inv_cost_warehouse;
            langType = o_langType;
            opNo = o_createBy;
        } else {
            shopId = req.getShopId();
            organizationNO = req.getOrganizationNO();
            eId = req.geteId();
            inv_cost_warehouse = req.getInv_cost_warehouse();
            langType = req.getLangType();
            opNo = req.getEmployeeNo();
        }

        //自动审批组织别切换
        if (StringUtils.isNotEmpty(request.getOrgNo())){
            organizationNO = request.getOrgNo();
        }

        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

        //获取启用在途参数 Enable_InTransit
        String Enable_InTransit = PosPub.getPARA_SMS(dao, eId, "", "Enable_InTransit");
        if (Check.Null(Enable_InTransit)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取在途仓参数:Enable_InTransit失败");
        }

        // IF 参数是否启用批号==N ，库存流水的批号和日期字段不给值 BY JZMA 20191101
        String isBatchPara = PosPub.getPARA_SMS(dao, eId, "", "Is_BatchNO");
        if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")) {
            isBatchPara = "N";
        }

        if (Enable_InTransit.equals("Y")) {
            if (Check.Null(inv_cost_warehouse)) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "参数已启用在途,门店:" + req.getShopId() + "  " + req.getShopName() + "在途仓不可为空");
            }
        }

        UptBean ub1 = null;

        String sql1;
        String sql2;
        String sql3;

        String stockOutSql = "select * from DCP_STOCKOUT "
//                + " where EID='" + eId + "' and SHOPID='" + shopId + "'  and STOCKOUTNO='" + stockOutNO + "'  "
                + " where EID='" + eId + "' and ORGANIZATIONNO='" + organizationNO + "'  and STOCKOUTNO='" + stockOutNO + "'  "
                + "  ";
        List<Map<String, Object>> stockOutList = this.doQueryData(stockOutSql, null);
        if (CollUtil.isEmpty(stockOutList)) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在，单据编号:" + stockOutNO);
        }
        sourceMenu=stockOutList.get(0).get("SOURCEMENU").toString();
        List<WarehouseLocationPlu> allocList = Collections.emptyList();

        String receiptOrg = stockOutList.get(0).get("RECEIPT_ORG").toString();
        String docOrg = stockOutList.get(0).get("ORGANIZATIONNO").toString();
        String stockOutStatus = stockOutList.get(0).get("STATUS").toString();
        String isTranInConfirm = stockOutList.get(0).getOrDefault("ISTRANINCONFIRM", "N").toString();
        String outEmployeeId = stockOutList.get(0).get("EMPLOYEEID").toString();
        String outDepartId = stockOutList.get(0).get("DEPARTID").toString();
        String ooType = stockOutList.get(0).get("OOTYPE").toString();
        String oType = stockOutList.get(0).get("OTYPE").toString();
        String stockoutno_origin = stockOutList.get(0).get("STOCKOUTNO_ORIGIN").toString();
        String ofNo = stockOutList.get(0).get("OFNO").toString();
        String oofNo = stockOutList.get(0).get("OOFNO").toString();
        docType = stockOutList.get(0).get("DOC_TYPE").toString();
        String createReceivingNo = "";
        switch (status) {
            case "2": // 确认
                String isCreatSql = "select * from DCP_STOCKOUT "
                        + " where EID='" + eId + "' and ORGANIZATIONNO='" + organizationNO + "'  and STOCKOUTNO='" + stockOutNO + "'  "
                        + " and status<>'0' and status<>'-1' ";
                List<Map<String, Object>> stakelist = this.doQueryData(isCreatSql, null);
                if (stakelist != null && !stakelist.isEmpty()) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, " 单号 :" + stockOutNO + " 该单已经确认！");
                }

                Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
                if("N".equals( stockChangeVerifyMsg.get("check").toString())){
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
                }

                String sql = this.getDCP_StockOut_Sql(req, eId, organizationNO, stockOutNO, langType);
                List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);



                //调拨出库时传1   换季退货时传0    次品退货时传2   其它出库传3 移仓出库传4  5-配货出库 退货出库是0
                switch (docType) {
                    case "1":
                        sql1 = " SELECT *  FROM DCP_RECEIVING  WHERE EID=? AND TRANSFER_SHOP=? AND Load_DOCNO=? ";
                        String[] conditionValues1 = {eId, shopId, stockOutNO};
                        List<Map<String, Object>> getQData1 = this.doQueryData(sql1, conditionValues1);
                        if (getQData1 == null || getQData1.isEmpty()) {
                            sql2 = this.getQuerySql2(req);
                            String[] conditionValues2 = { organizationNO, eId, stockOutNO};
                            List<Map<String, Object>> getQData2 = this.doQueryData(sql2, conditionValues2);
                            if (getQData2 != null && !getQData2.isEmpty()) {
                                String transferShop;
                                String transfer_warehouse;
                                if (docType.equals("1")) {
                                    transferShop = getQData2.get(0).get("TRANSFERSHOP").toString();
                                } else {
                                    transferShop = shopId;
                                }
                                transfer_warehouse = getQData2.get(0).get("TRANSFER_WAREHOUSE").toString();

                                /*  由在途写入
                                if (!StringUtils.equals(receiptOrg, organizationNO)) {

                                    String ReceivingNO = this.getOrderNO(req,
                                            getQData2.get(0).get("ORGANIZATIONNO").toString(),
                                            "DBTZ");// getReceivingNO(req, getQData2.get(0).get("ORGANIZATIONNO").toString());

                                    String receivingBdate = PosPub.getAccountDate_SMS(dao, eId, transferShop);
                                    String Transfer_Day = PosPub.getPARA_SMS(dao, eId, shopId, "Transfer_Day");
                                    if (!PosPub.isNumeric(Transfer_Day)) {
                                        Transfer_Day = "7";
                                    }
                                    String receiptDate = PosPub.GetStringDate(sDate, Integer.parseInt(Transfer_Day));

                                    ColumnDataValue dcp_receiving = new ColumnDataValue();

                                    dcp_receiving.add("TRANSFER_SHOP", DataValues.newString(shopId));
                                    dcp_receiving.add("RECEIVINGNO", DataValues.newString(ReceivingNO));
                                    dcp_receiving.add("SHOPID", DataValues.newString(getQData2.get(0).get("ORGANIZATIONNO")));
                                    dcp_receiving.add("BDATE", DataValues.newString(receivingBdate));
                                    dcp_receiving.add("MEMO", DataValues.newString(getQData2.get(0).get("MEMOMEMO")));
                                    dcp_receiving.add("DOC_TYPE", DataValues.newString(getQData2.get(0).get("DOCTYPE")));
                                    dcp_receiving.add("STATUS", DataValues.newString("6"));
                                    dcp_receiving.add("CREATEBY", DataValues.newString(opNo));
                                    dcp_receiving.add("CREATE_DATE", DataValues.newString(sDate));
                                    dcp_receiving.add("CREATE_TIME", DataValues.newString(sTime));
                                    dcp_receiving.add("MODIFYBY", DataValues.newString(""));
                                    dcp_receiving.add("MODIFY_DATE", DataValues.newString(""));
                                    dcp_receiving.add("MODIFY_TIME", DataValues.newString(""));
                                    dcp_receiving.add("CANCELBY", DataValues.newString(""));
                                    dcp_receiving.add("CANCEL_DATE", DataValues.newString(""));
                                    dcp_receiving.add("CANCEL_TIME", DataValues.newString(""));
                                    dcp_receiving.add("TOT_PQTY", DataValues.newString(getQData2.get(0).get("TOTPQTY")));
                                    dcp_receiving.add("TOT_AMT", DataValues.newString(getQData2.get(0).get("TOTAMT")));
                                    dcp_receiving.add("TOT_CQTY", DataValues.newString(getQData2.get(0).get("TOTCQTY")));
                                    dcp_receiving.add("LOAD_DOCTYPE", DataValues.newString(""));
                                    dcp_receiving.add("LOAD_DOCNO", DataValues.newString(getQData2.get(0).get("STOCKOUTNO")));
                                    dcp_receiving.add("ORGANIZATIONNO", DataValues.newString(getQData2.get(0).get("ORGANIZATIONNO")));
                                    dcp_receiving.add("EID", DataValues.newString(getQData2.get(0).get("EID")));
                                    dcp_receiving.add("WAREHOUSE", DataValues.newString(getQData2.get(0).get("TRANSFER_WAREHOUSE")));
                                    dcp_receiving.add("TOT_DISTRIAMT", DataValues.newString(getQData2.get(0).get("TOT_DISTRIAMT")));
                                    dcp_receiving.add("RECEIPTDATE", DataValues.newString(receiptDate));
                                    dcp_receiving.add("UPDATE_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
                                    dcp_receiving.add("TRAN_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
                                    dcp_receiving.add("DELIVERYBY", DataValues.newString(getQData2.get(0).get("DELIVERYBY")));
                                    dcp_receiving.add("OTYPE", DataValues.newString("1"));
                                    dcp_receiving.add("OFNO", DataValues.newString(stockOutNO));
                                    dcp_receiving.add("RECEIPTORGNO", DataValues.newString(getQData2.get(0).get("RECEIPT_ORG")));
                                    dcp_receiving.add("EMPLOYEEID", DataValues.newString(getQData2.get(0).get("EMPLOYEEID")));
                                    dcp_receiving.add("DEPARTID", DataValues.newString(getQData2.get(0).get("DEPARTID")));
                                    dcp_receiving.add("INVWAREHOUSE", DataValues.newString(getQData2.get(0).get("INVWAREHOUSE")));
                                    //收货通知单主表
                                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_RECEIVING", dcp_receiving))); // 新增單頭


                                    sql3 = this.getQuerySql3(req);
                                    String[] conditionValues3 = {shopId, organizationNO, eId, stockOutNO};
                                    List<Map<String, Object>> getQData3 = this.doQueryData(sql3, conditionValues3);

                                    if (getQData3 != null && !getQData3.isEmpty()) {
                                        for (Map<String, Object> oneData3 : getQData3) {
                                            int insColCt = 0;

                                            ColumnDataValue dcp_receiving_detail = new ColumnDataValue();

                                            dcp_receiving_detail.add("ReceivingNO", DataValues.newString(ReceivingNO));
                                            dcp_receiving_detail.add("EID", DataValues.newString(eId));
                                            dcp_receiving_detail.add("SHOPID", DataValues.newString(shopId));
                                            dcp_receiving_detail.add("organizationNO", DataValues.newString(getQData2.get(0).get("ORGANIZATIONNO")));
                                            dcp_receiving_detail.add("OFNO", DataValues.newString(getQData2.get(0).get("STOCKOUTNO")));
                                            dcp_receiving_detail.add("item", DataValues.newString(oneData3.get("ITEM")));
                                            dcp_receiving_detail.add("OItem", DataValues.newString(oneData3.get("ITEM")));
                                            dcp_receiving_detail.add("OType", DataValues.newString("1"));
                                            dcp_receiving_detail.add("PLUNO", DataValues.newString(oneData3.get("PLUNO")));
                                            dcp_receiving_detail.add("Punit", DataValues.newString(oneData3.get("PUNIT")));
                                            dcp_receiving_detail.add("PQTY", DataValues.newString(oneData3.get("PQTY")));
                                            dcp_receiving_detail.add("BASEUNIT", DataValues.newString(oneData3.get("BASEUNIT")));
                                            dcp_receiving_detail.add("BASEQTY", DataValues.newString(oneData3.get("BASEQTY")));
                                            dcp_receiving_detail.add("unit_Ratio", DataValues.newString(oneData3.get("UNITRATIO")));
                                            dcp_receiving_detail.add("Price", DataValues.newString(oneData3.get("PRICE")));
                                            dcp_receiving_detail.add("AMT", DataValues.newString(oneData3.get("AMT")));
                                            dcp_receiving_detail.add("WAREHOUSE", DataValues.newString(transfer_warehouse));
                                            dcp_receiving_detail.add("PLU_MEMO", DataValues.newString(oneData3.get("PLU_MEMO")));
                                            dcp_receiving_detail.add("BATCH_NO", DataValues.newString(oneData3.get("BATCH_NO")));
                                            dcp_receiving_detail.add("PROD_DATE", DataValues.newString(oneData3.get("PROD_DATE")));
                                            dcp_receiving_detail.add("DISTRIPRICE", DataValues.newString(oneData3.get("DISTRIPRICE")));
                                            dcp_receiving_detail.add("DISTRIAMT", DataValues.newString(oneData3.get("DISTRIAMT")));
                                            dcp_receiving_detail.add("BDATE", DataValues.newString(oneData3.get("BDATE")));
                                            dcp_receiving_detail.add("FEATURENO", DataValues.newString(oneData3.get("FEATURENO")));
                                            dcp_receiving_detail.add("EXPDATE", DataValues.newString(oneData3.get("EXPDATE")));
                                            dcp_receiving_detail.add("BSNO", DataValues.newString(oneData3.get("BSNO")));

                                            //收货通知单子表
                                            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_RECEIVING_DETAIL", dcp_receiving_detail)));
                                        }
                                    }
                                }

                            */

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
//                            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
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
//                            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                            ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                            this.addProcessData(new DataProcessBean(ub1));
                        }
                        break;
                    case "4":
                    case "5": {
                        //配货出库
                        //● 调整库存异动：已有逻辑，取出批号逻辑回写逻辑同DCP_SSTOCKOUT_PROCESS

                        //预分配 批号库位  DCP_STOCKOUT_BATCH
                        String stockDocType = "41";
                        if ("4".equals(docType)) {
                            stockDocType = "18";
                        }
                        String sBatchSql = "select a.* ,NVL(uc1.UNIT_RATIO,1) UNIT_RATIO,d.price,d.DISTRIPRICE from DCP_STOCKOUT_BATCH a " +
                                " LEFT JOIN DCP_UNITCONVERT uc1 on uc1.eid=a.eid and uc1.OUNIT=a.PUNIT AND uc1.UNIT=a.BASEUNIT " +
                                " left join DCP_STOCKOUT_DETAIL d on d.eid=a.eid and d.organizationno=a.organizationno and d.stockoutno=a.stockoutno and d.item=a.item2 " +
                                " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' " +
                                " and a.stockoutno='" + stockOutNO + "' ";
                        List<Map<String, Object>> sBatchList = this.doQueryData(sBatchSql, null);

                        List<BatchLocationPlu> batchLocationPlus = new ArrayList<>();

                        DelBean db3 = new DelBean("DCP_STOCKOUT_BATCH");
                        db3.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                        db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//                        db3.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        db3.addCondition("STOCKOUTNO", new DataValue(stockOutNO, Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db3));

                        for (Map<String, Object> singleBatch : sBatchList) {
                            BatchLocationPlu onePlu = new BatchLocationPlu();
                            onePlu.setId(Integer.parseInt(singleBatch.get("ITEM").toString()));
                            onePlu.setWarehouse(singleBatch.get("WAREHOUSE").toString());
                            onePlu.setPQty(singleBatch.get("PQTY").toString());
                            onePlu.setPUnit(singleBatch.get("PUNIT").toString());
                            onePlu.setPluNo(singleBatch.get("PLUNO").toString());
                            onePlu.setFeatureNo(singleBatch.get("FEATURENO").toString());

                            onePlu.setBatchNo(singleBatch.get("BATCHNO").toString());
                            onePlu.setLocation(singleBatch.get("LOCATION").toString());

                            batchLocationPlus.add(onePlu);
                        }

                         allocList = BatchLocationStockAlloc.batchLocationStockAlloc(batchLocationPlus);

                        //日期格式要修改一下
                        for (WarehouseLocationPlu oneAlloc : allocList) {
                            if (Check.NotNull(oneAlloc.getProdDate())) {
                                oneAlloc.setProdDate(new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oneAlloc.getProdDate())));
                            }
                            if (Check.NotNull(oneAlloc.getValidDate())) {
                                oneAlloc.setValidDate(new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(oneAlloc.getValidDate())));
                            }
                        }


                        //根据allocList 扣库存
                        int batchItem = 0;
                        for (Map<String, Object> row : sBatchList) {
                            String thisItem = row.get("ITEM").toString();
                            String thisItem2 = row.get("ITEM2").toString();
                            String thisPluNo = row.get("PLUNO").toString();
                            String thisFeatureNo = row.get("FEATURENO").toString();
                            String thisWarehouse = row.get("WAREHOUSE").toString();
                            String thisLocation = row.get("LOCATION").toString();
                            String thisBatchNo = row.get("BATCHNO").toString();
                            String thisProdDate = row.get("PRODDATE").toString();
                            String thisExpDate = row.get("EXPDATE").toString();
                            String thisPUnit = row.get("PUNIT").toString();
                            String thisPQty = row.get("PQTY").toString();
                            String thisBaseUnit = row.get("BASEUNIT").toString();
                            String thisBaseQty = row.get("BASEQTY").toString();
                            String thisUnitRatio = row.get("UNIT_RATIO").toString();
                            double amt = BigDecimalUtils.mul(Double.parseDouble(row.get("PRICE").toString()), Double.parseDouble(row.get("PQTY").toString()), 2);
                            double distriAmt = BigDecimalUtils.mul(Double.parseDouble(row.get("DISTRIPRICE").toString()), Double.parseDouble(row.get("PQTY").toString()), 2);
                            
                            List<WarehouseLocationPlu> filterAlloc = allocList.stream().filter(x -> String.valueOf(x.getId()).equals(thisItem)).collect(Collectors.toList());
//                            if (!filterAlloc.isEmpty()) {
                            if (!filterAlloc.isEmpty() && !"7".equals(oType)) { //增加判断来源，如果来源为差异单则不再重新分配
                                for (WarehouseLocationPlu onePlu : filterAlloc) {
                                    batchItem++;

                                    onePlu.setNewId(batchItem);//回写新的序号

                                    Map<String, Object> baseMap = PosPub.getBaseQty(this.dao, req.geteId(), onePlu.getPluNo(), onePlu.getPUnit(), onePlu.getAllocQty());

                                    String baseQty = baseMap.get("baseQty").toString();
                                    String ratio = baseMap.get("unitRatio").toString();

                                    ColumnDataValue batchColumns = new ColumnDataValue();
                                    batchColumns.add("EID", DataValues.newString(eId));
                                    batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                    batchColumns.add("SHOPID", DataValues.newString(shopId));
                                    batchColumns.add("STOCKOUTNO", DataValues.newString(stockOutNO));
                                    batchColumns.add("ITEM", DataValues.newString(batchItem));
                                    batchColumns.add("ITEM2", DataValues.newString(thisItem2));
                                    batchColumns.add("PLUNO", DataValues.newString(onePlu.getPluNo()));
                                    batchColumns.add("FEATURENO", DataValues.newString(thisFeatureNo));
                                    batchColumns.add("WAREHOUSE", DataValues.newString(onePlu.getWarehouse()));
                                    batchColumns.add("LOCATION", DataValues.newString(onePlu.getLocation()));
                                    batchColumns.add("BATCHNO", DataValues.newString(onePlu.getBatchNo()));
                                    batchColumns.add("PRODDATE", DataValues.newString(onePlu.getProdDate()));
                                    batchColumns.add("EXPDATE", DataValues.newString(onePlu.getValidDate()));
                                    batchColumns.add("PUNIT", DataValues.newString(onePlu.getPUnit()));
                                    batchColumns.add("PQTY", DataValues.newString(onePlu.getAllocQty()));
                                    batchColumns.add("BASEUNIT", DataValues.newString(thisBaseUnit));
                                    batchColumns.add("BASEQTY", DataValues.newString(baseQty));
                                    batchColumns.add("UNITRATIO", DataValues.newString(ratio));

                                    String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                                    DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                                    InsBean ibb = new InsBean("DCP_STOCKOUT_BATCH", batchColumnNames);
                                    ibb.addValues(batchDataValues);
                                    this.addProcessData(new DataProcessBean(ibb));

                                    BcReq bcReq=new BcReq();
                                    bcReq.setServiceType("StockOutProcess");
                                    bcReq.setDocType(docType);
                                    bcReq.setBillType(stockDocType);

                                    BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                                    String bType = bcMap.getBType();
                                    String costCode = bcMap.getCostCode();
                                    if(Check.Null(bType)||Check.Null(costCode)){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                                    }

                                    String procedure = "SP_DCP_STOCKCHANGE_VX";
                                    Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                    inputParameter.put(1, eId);                                            //--企业ID
                                    inputParameter.put(2, null);
                                    inputParameter.put(3, organizationNO);                                         //--组织
                                    inputParameter.put(4, bType);
                                    inputParameter.put(5, costCode);
                                    inputParameter.put(6, stockDocType);                                //--单据类型
                                    inputParameter.put(7, stockOutNO);                                        //--单据号
                                    inputParameter.put(8, thisItem2);
                                    inputParameter.put(9, "0");
                                    inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                    inputParameter.put(11, stockOutList.get(0).get("BDATE").toString());             //--营业日期 yyyy-MM-dd
                                    inputParameter.put(12, thisPluNo);         //--品号
                                    inputParameter.put(13, thisFeatureNo);                                   //--特征码
                                    inputParameter.put(14, thisWarehouse);    //--仓库
                                    inputParameter.put(15, onePlu.getBatchNo());     //批号
                                    inputParameter.put(16, onePlu.getLocation());     //--location
                                    inputParameter.put(17, onePlu.getPUnit());        //--交易单位
                                    inputParameter.put(18, onePlu.getAllocQty());         //--交易数量
                                    inputParameter.put(19, thisBaseUnit);     //--基准单位
                                    inputParameter.put(20, baseQty);      //--基准数量
                                    inputParameter.put(21, ratio);   //--换算比例
                                    inputParameter.put(22, row.get("PRICE").toString());        //--零售价
                                    inputParameter.put(23, amt);          //--零售金额
                                    inputParameter.put(24, row.get("DISTRIPRICE").toString());  //--进货价
                                    inputParameter.put(25, distriAmt);    //--进货金额
                                    inputParameter.put(26, accountDate);                                   //--入账日期 yyyy-MM-dd
                                    inputParameter.put(27, onePlu.getProdDate());    //--批号的生产日期 yyyy-MM-dd
                                    inputParameter.put(28, stockOutList.get(0).get("BDATE").toString());            //--单据日期
                                    inputParameter.put(29, "配货出库扣库存");             //--异动原因
                                    inputParameter.put(30, "");             //--异动描述
                                    inputParameter.put(31, req.getOpNO());          //--操作员

                                    ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                    this.addProcessData(new DataProcessBean(pdb));

                                }
                            }
                            else {
                                batchItem++;

                                ColumnDataValue batchColumns = new ColumnDataValue();
                                batchColumns.add("EID", DataValues.newString(eId));
                                batchColumns.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
                                batchColumns.add("SHOPID", DataValues.newString(shopId));
                                batchColumns.add("STOCKOUTNO", DataValues.newString(stockOutNO));
                                batchColumns.add("ITEM", DataValues.newString(batchItem));
                                batchColumns.add("ITEM2", DataValues.newString(thisItem2));
                                batchColumns.add("PLUNO", DataValues.newString(thisPluNo));
                                batchColumns.add("FEATURENO", DataValues.newString(thisFeatureNo));
                                batchColumns.add("WAREHOUSE", DataValues.newString(thisWarehouse));
                                batchColumns.add("LOCATION", DataValues.newString(thisLocation));
                                batchColumns.add("BATCHNO", DataValues.newString(thisBatchNo));
                                batchColumns.add("PRODDATE", DataValues.newString(thisProdDate));
                                batchColumns.add("EXPDATE", DataValues.newString(thisExpDate));
                                batchColumns.add("PUNIT", DataValues.newString(thisPUnit));
                                batchColumns.add("PQTY", DataValues.newString(thisPQty));
                                batchColumns.add("BASEUNIT", DataValues.newString(thisBaseUnit));
                                batchColumns.add("BASEQTY", DataValues.newString(thisBaseQty));
                                batchColumns.add("UNITRATIO", DataValues.newString(thisUnitRatio));

                                String[] batchColumnNames = batchColumns.getColumns().toArray(new String[0]);
                                DataValue[] batchDataValues = batchColumns.getDataValues().toArray(new DataValue[0]);
                                InsBean ibb = new InsBean("DCP_STOCKOUT_BATCH", batchColumnNames);
                                ibb.addValues(batchDataValues);
                                this.addProcessData(new DataProcessBean(ibb));

                                BcReq bcReq=new BcReq();
                                bcReq.setServiceType("StockOutProcess");
                                bcReq.setDocType(docType);
                                bcReq.setBillType(stockDocType);

                                BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                                String bType = bcMap.getBType();
                                String costCode = bcMap.getCostCode();
                                if(Check.Null(bType)||Check.Null(costCode)){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                                }

                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                                inputParameter.put(1, eId);                                            //--企业ID
                                inputParameter.put(2, null);
                                inputParameter.put(3, organizationNO);                                         //--组织
                                inputParameter.put(4, bType);
                                inputParameter.put(5, costCode);
                                inputParameter.put(6, stockDocType);                                //--单据类型
                                inputParameter.put(7, stockOutNO);                                        //--单据号
                                inputParameter.put(8, thisItem2);
                                inputParameter.put(9, "0");
                                inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                                inputParameter.put(11, stockOutList.get(0).get("BDATE").toString());             //--营业日期 yyyy-MM-dd
                                inputParameter.put(12, thisPluNo);         //--品号
                                inputParameter.put(13, thisFeatureNo);                                   //--特征码
                                inputParameter.put(14, thisWarehouse);    //--仓库
                                inputParameter.put(15, thisBatchNo);     //批号
                                inputParameter.put(16, thisLocation);     //--location
                                inputParameter.put(17, thisPUnit);        //--交易单位
                                inputParameter.put(18, thisPQty);         //--交易数量
                                inputParameter.put(19, thisBaseUnit);     //--基准单位
                                inputParameter.put(20, thisBaseQty);      //--基准数量
                                inputParameter.put(21, thisUnitRatio);   //--换算比例
                                inputParameter.put(22, row.get("PRICE").toString());        //--零售价
                                inputParameter.put(23, amt);          //--零售金额
                                inputParameter.put(24, row.get("DISTRIPRICE").toString());  //--进货价
                                inputParameter.put(25, distriAmt);    //--进货金额
                                inputParameter.put(26, accountDate);                                   //--入账日期 yyyy-MM-dd
                                inputParameter.put(27, thisProdDate);    //--批号的生产日期 yyyy-MM-dd
                                inputParameter.put(28, stockOutList.get(0).get("BDATE").toString());            //--单据日期
                                inputParameter.put(29, "配货出库扣库存");             //--异动原因
                                inputParameter.put(30, "");             //--异动描述
                                inputParameter.put(31, req.getOpNO());          //--操作员

                                ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                                this.addProcessData(new DataProcessBean(pdb));

                            }
                        }

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
//                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub1));

                        break;
                    }
                    case "0": {
                        //退配出库 ->生成退配收货通知单 -> 退配入库

                        String detailSb = "select a.eid,a.organizationno,a.RECEIPT_ORG AS RECEIPTORG,a.RECEIPTDATE,a.WAREHOUSE,b.PLU_BARCODE as PLUBARCODE,b.pluno,b.featureno,b.punit," +
                                " b.price,b.DISTRIPRICE,b.warehouse as detailwarehouse,b.baseunit,b.UNIT_RATIO,b.PLU_MEMO,b.BATCH_NO AS BATCHNO,b.PROD_DATE AS PRODDATE,b.PACKINGNO,nvl(c.pqty,0) as ypqty ,b.pqty,b.item,b.expdate,  " +
                                " d.IN_COST_WAREHOUSE,d.INV_COST_WAREHOUSE,a.INVWAREHOUSE,b.bsno  " +
                                " from dcp_stockout a " +
                                " left join DCP_STOCKOUT_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.stockoutno=b.stockoutno " +
                                " left join DCP_PORDER_DETAIL c on c.eid=a.eid and c.organizationno=a.organizationno and b.oofno=c.porderno and b.ooitem=c.item and b.ootype='3'" +
                                " left join dcp_org d on d.eid=a.eid and d.organizationno=a.receipt_org " +
                                " where a.stockoutno='" + stockOutNO + "' and a.eid='" + eId + "' and a.organizationno='" + organizationNO + "'";
                        List<Map<String, Object>> stockoutDetails = this.doQueryData(detailSb, null);
                        if (stockoutDetails == null || stockoutDetails.isEmpty()) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "该出库单不存在");
                        }
                        Map<String, Object> stockOutInfo = stockoutDetails.get(0);

                        String warehouse = stockOutInfo.get("WAREHOUSE").toString();
                        String receiptdate = stockOutInfo.get("RECEIPTDATE").toString();
                        String isBatchNoForReceipt = PosPub.getPARA_SMS(dao, req.geteId(), receiptOrg, "Is_BatchNO");
                        String invCostWarehouse = stockOutInfo.get("INVWAREHOUSE").toString();// stockOutInfo.get("INV_COST_WAREHOUSE").toString();

                        String inCostWarehouse = stockOutInfo.get("IN_COST_WAREHOUSE").toString();

                        Calendar cal = Calendar.getInstance();//获得当前时间

                        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                        String createDate = df.format(cal.getTime());
                        df = new SimpleDateFormat("HHmmss");
                        String createTime = df.format(cal.getTime());
                        String orderNO = this.getOrderNO(req, "SHTZ");
                        BigDecimal totCqty = BigDecimal.ZERO;
                        BigDecimal totPqty = BigDecimal.ZERO;
                        BigDecimal totDistriAmt = BigDecimal.ZERO;
                        BigDecimal totAmt = BigDecimal.ZERO;

                        List<Map> collect = stockoutDetails.stream().map(x -> {
                            Map map = new HashMap();
                            map.put("pluno", x.get("PLUNO").toString());
                            map.put("featureno", x.get("FEATURENO").toString());
                            return map;
                        }).distinct().collect(Collectors.toList());
                        totCqty = new BigDecimal(collect.size());


                        //明细进行分组
                        List<Map<String, String>> detailGroup = new ArrayList<Map<String, String>>();
                        int detailType = 1;
                        if (isBatchPara.equals("N")) {
                            detailGroup = stockoutDetails.stream().map(x -> {
                                Map<String, String> map = new HashMap();
                                map.put("item", x.get("ITEM").toString());
                                return map;
                            }).distinct().collect(Collectors.toList());
                            detailType = 1;
                        } else {
                            if (isBatchNoForReceipt.equals("N")) {
                                detailGroup = stockoutDetails.stream().map(x -> {
                                    Map<String, String> map = new HashMap();
                                    map.put("pluno", x.get("PLUNO").toString());
                                    map.put("featureno", x.get("FEATURENO").toString());
                                    map.put("punit", x.get("PUNIT").toString());
                                    return map;
                                }).distinct().collect(Collectors.toList());
                                detailType = 2;
                            } else {
                                detailGroup = stockoutDetails.stream().map(x -> {
                                    Map<String, String> map = new HashMap();
                                    map.put("pluno", x.get("PLUNO").toString());
                                    map.put("featureno", x.get("FEATURENO").toString());
                                    map.put("punit", x.get("PUNIT").toString());
                                    map.put("batchno", x.get("BATCHNO").toString());
                                    return map;
                                }).distinct().collect(Collectors.toList());
                                detailType = 3;
                            }
                        }

                        /* 由在途库存写入
                        int detailItem = 0;
                        for (Map<String, String> detail : detailGroup) {

                            List<Map<String, Object>> filterItems = new ArrayList<>();
                            if (detailType == 1) {
                                String item = detail.get("item");
                                filterItems = stockoutDetails.stream().filter(x -> x.get("ITEM").toString().equals(item)).collect(Collectors.toList());
                            } else if (detailType == 2) {
                                String pluno = detail.get("pluno");
                                String featureno = detail.get("featureno");
                                String punit = detail.get("punit");
                                filterItems = stockoutDetails.stream().filter(x -> x.get("PLUNO").toString().equals(pluno) &&
                                        x.get("FEATURENO").toString().equals(featureno) && x.get("PUNIT").toString().equals(punit)).collect(Collectors.toList());

                            } else {
                                String pluno = detail.get("pluno");
                                String featureno = detail.get("featureno");
                                String punit = detail.get("punit");
                                String batchno = detail.get("batchno");
                                filterItems = stockoutDetails.stream().filter(x -> x.get("PLUNO").toString().equals(pluno) &&
                                        x.get("FEATURENO").toString().equals(featureno) && x.get("PUNIT").toString().equals(punit)
                                        && x.get("BATCHNO").toString().equals(batchno)).collect(Collectors.toList());

                            }

                            Map<String, Object> singleMap = filterItems.get(0);

                            String plubarcode = singleMap.get("PLUBARCODE").toString();
                            String pluno = singleMap.get("PLUNO").toString();
                            String bsno = singleMap.get("BSNO").toString();
                            String featureno = singleMap.get("FEATURENO").toString();
                            String punit = singleMap.get("PUNIT").toString();
                            String price = singleMap.get("PRICE").toString();
                            String distriprice = singleMap.get("DISTRIPRICE").toString();
                            String detailwarehouse = singleMap.get("DETAILWAREHOUSE").toString();
                            String baseunit = singleMap.get("BASEUNIT").toString();
                            String unit_ratio = singleMap.get("UNIT_RATIO").toString();
                            String plu_memo = singleMap.get("PLU_MEMO").toString();
                            String proddate = isBatchNoForReceipt.equals("N") ? "" : singleMap.get("PRODDATE").toString();
                            String expDate = isBatchNoForReceipt.equals("N") ? "" : singleMap.get("EXPDATE").toString();
                            String batchno = isBatchNoForReceipt.equals("N") ? "" : singleMap.get("BATCHNO").toString();
                            String packingno = singleMap.get("PACKINGNO").toString();
                            BigDecimal pqty = new BigDecimal(0);
                            BigDecimal ypqty = new BigDecimal(0);
                            for (Map<String, Object> item : filterItems) {
                                BigDecimal pqty1 = new BigDecimal(item.get("PQTY").toString());
                                pqty = pqty.add(pqty1);
                                ypqty = ypqty.add(new BigDecimal(item.get("YPQTY").toString()));
                            }
                            totPqty = totPqty.add(pqty);
                            BigDecimal amt = new BigDecimal(price).multiply(pqty);
                            BigDecimal distriAmt = new BigDecimal(distriprice).multiply(pqty);
                            totAmt = totAmt.add(amt);
                            totDistriAmt = totDistriAmt.add(distriAmt);
                            String oitem = "";
                            if (filterItems.size() == 1) {
                                oitem = filterItems.get(0).get("ITEM").toString();
                            }
                            BigDecimal baseqty = pqty.multiply(new BigDecimal(unit_ratio));

                            detailItem++;
                            ColumnDataValue detailColumns = new ColumnDataValue();
                            detailColumns.add("EID", DataValues.newString(eId));
                            detailColumns.add("ORGANIZATIONNO", DataValues.newString(docOrg));
                            detailColumns.add("SHOPID", DataValues.newString(docOrg));
                            detailColumns.add("RECEIVINGNO", DataValues.newString(orderNO));
                            detailColumns.add("BDATE", DataValues.newString(createDate));
                            detailColumns.add("ITEM", DataValues.newString(String.valueOf(detailItem)));
                            detailColumns.add("PLU_BARCODE", DataValues.newString(plubarcode));
                            detailColumns.add("PLUNO", DataValues.newString(pluno));
                            detailColumns.add("FEATURENO", DataValues.newString(featureno));
                            detailColumns.add("PUNIT", DataValues.newString(punit));
                            detailColumns.add("PQTY", DataValues.newString(pqty.toString()));
                            detailColumns.add("STOCKIN_QTY", DataValues.newString("0"));
                            detailColumns.add("POQTY", DataValues.newString(ypqty));
                            detailColumns.add("PRICE", DataValues.newString(price));
                            detailColumns.add("AMT", DataValues.newString(amt.toString()));
                            detailColumns.add("DISTRIPRICE", DataValues.newString(distriprice));
                            detailColumns.add("DISTRIAMT", DataValues.newString(distriAmt.toString()));
                            detailColumns.add("PROC_RATE", DataValues.newString("0"));
                            detailColumns.add("WAREHOUSE", DataValues.newString(inCostWarehouse));
                            detailColumns.add("BASEQTY", DataValues.newString(baseqty.toString()));
                            detailColumns.add("BASEUNIT", DataValues.newString(baseunit));
                            detailColumns.add("UNIT_RATIO", DataValues.newString(unit_ratio));
                            detailColumns.add("OTYPE", DataValues.newString("0"));
                            detailColumns.add("OFNO", DataValues.newString(stockOutNO));
                            detailColumns.add("OITEM", DataValues.newString(oitem));
                            detailColumns.add("PLU_MEMO", DataValues.newString(plu_memo));
                            detailColumns.add("BATCH_NO", DataValues.newString(batchno));
                            detailColumns.add("PROD_DATE", DataValues.newString(proddate));
                            detailColumns.add("PACKINGNO", DataValues.newString(packingno));
                            detailColumns.add("STATUS", DataValues.newString("0"));
                            detailColumns.add("EXPDATE", DataValues.newString(expDate));
                            detailColumns.add("BSNO", DataValues.newString(bsno));


                            String[] detailColumnNames = detailColumns.getColumns().toArray(new String[0]);
                            DataValue[] detailDataValues = detailColumns.getDataValues().toArray(new DataValue[0]);
                            InsBean ib1 = new InsBean("DCP_RECEIVING_DETAIL", detailColumnNames);
                            ib1.addValues(detailDataValues);
                            this.addProcessData(new DataProcessBean(ib1));

                        }

                        ColumnDataValue mainColumns = new ColumnDataValue();
                        mainColumns.add("EID", DataValues.newString(eId));
                        mainColumns.add("SHOPID", DataValues.newString(docOrg));
                        mainColumns.add("ORGANIZATIONNO", DataValues.newString(docOrg));
                        mainColumns.add("BDATE", DataValues.newString(createDate));
                        mainColumns.add("RECEIVINGNO", DataValues.newString(orderNO));
                        mainColumns.add("DOC_TYPE", DataValues.newString("5"));//DOC_TYPE	5-退配收货

                        mainColumns.add("STATUS", DataValues.newString("6"));
                        mainColumns.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
                        mainColumns.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getPlainDate(nowDateTime)));
                        mainColumns.add("CREATE_TIME", DataValues.newString(StringUtils.substring(DateFormatUtils.getPlainDatetime(nowDateTime), 9)));
                        mainColumns.add("RECEIPTDATE", DataValues.newString(receiptdate));
                        mainColumns.add("TOT_CQTY", DataValues.newString(totCqty));
                        mainColumns.add("TOT_PQTY", DataValues.newString(totPqty));
                        mainColumns.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmt));
                        mainColumns.add("TOT_AMT", DataValues.newString(totAmt));
                        mainColumns.add("WAREHOUSE", DataValues.newString(inCostWarehouse));
                        mainColumns.add("TRANSFER_SHOP", DataValues.newString(organizationNO));
                        mainColumns.add("TRANSFER_WAREHOUSE", DataValues.newString(warehouse));
                        mainColumns.add("OTYPE", DataValues.newString("0"));// 0.退配出库
                        mainColumns.add("DEPARTID", DataValues.newString(outDepartId));
                        mainColumns.add("EMPLOYEEID", DataValues.newString(outEmployeeId));
                        mainColumns.add("OFNO", DataValues.newString(stockOutNO));
                        mainColumns.add("LOAD_DOCNO", DataValues.newString(stockOutNO));
                        mainColumns.add("RECEIPTORGNO", DataValues.newString(receiptOrg));
                        mainColumns.add("STATUS", DataValues.newString("6"));
                        mainColumns.add("INVWAREHOUSE", DataValues.newString(invCostWarehouse));
                        String[] mainColumnNames = mainColumns.getColumns().toArray(new String[0]);
                        DataValue[] mainDataValues = mainColumns.getDataValues().toArray(new DataValue[0]);
                        InsBean ib1 = new InsBean("DCP_RECEIVING", mainColumnNames);
                        ib1.addValues(mainDataValues);
                        this.addProcessData(new DataProcessBean(ib1));
                        */

                        for (Map<String, Object> oneData : getQDataDetail) {

                            String featureNo = oneData.get("FEATURENO").toString();  ///  特征码为空给空格
                            if (Check.Null(featureNo))
                                featureNo = " ";

                            String stockWarehouse = oneData.get("WAREHOUSE").toString();
                            if (inv_cost_warehouse.equals(stockWarehouse) && Enable_InTransit.equals("Y")) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "出货仓库 和 在途成本仓库 一致(" + stockWarehouse + "),不能出库！");
                            }
                            //判断仓库不能为空或空格  BY JZMA 20191118
                            if (Check.Null(stockWarehouse) || Check.Null(stockWarehouse.trim())
                                    || stockWarehouse.trim().isEmpty()) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");
                            }

                            BcReq bcReq=new BcReq();
                            bcReq.setServiceType("StockOutProcess");
                            bcReq.setDocType(docType);
                            bcReq.setBillType("03");

                            BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                            String bType = bcMap.getBType();
                            String costCode = bcMap.getCostCode();
                            if(Check.Null(bType)||Check.Null(costCode)){
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                            }


                            String procedure = "SP_DCP_STOCKCHANGE_VX";
                            Map<Integer, Object> inputParameter = new HashMap<Integer, Object>();
                            inputParameter.put(1, eId);                                            //--企业ID
                            inputParameter.put(2, null);
                            inputParameter.put(3, organizationNO);                                         //--组织
                            inputParameter.put(4, bType);
                            inputParameter.put(5, costCode);
                            inputParameter.put(6, "03");                                //--单据类型
                            inputParameter.put(7, stockOutNO);                                        //--单据号
                            inputParameter.put(8, oneData.get("ITEM").toString());
                            inputParameter.put(9, "0");
                            inputParameter.put(10, "-1");                                   //--异动方向 1=加库存 -1=减库存
                            inputParameter.put(11, oneData.get("BDATE").toString());             //--营业日期 yyyy-MM-dd
                            inputParameter.put(12, oneData.get("PLUNO").toString());         //--品号
                            inputParameter.put(13, featureNo);                                   //--特征码
                            inputParameter.put(14, stockWarehouse);    //--仓库
                            inputParameter.put(15, oneData.get("BATCH_NO").toString());     //批号
                            inputParameter.put(16, oneData.get("MES_LOCATION").toString());     //--location
                            inputParameter.put(17, oneData.get("PUNIT").toString());        //--交易单位
                            inputParameter.put(18, oneData.get("PQTY").toString());         //--交易数量
                            inputParameter.put(19, oneData.get("BASEUNIT").toString());     //--基准单位
                            inputParameter.put(20, oneData.get("BASEQTY").toString());      //--基准数量
                            inputParameter.put(21, oneData.get("UNIT_RATIO").toString());   //--换算比例
                            inputParameter.put(22, oneData.get("PRICE").toString());        //--零售价
                            inputParameter.put(23, oneData.get("AMT").toString());          //--零售金额
                            inputParameter.put(24, oneData.get("DISTRIPRICE").toString());  //--进货价
                            inputParameter.put(25, oneData.get("DISTRIAMT").toString());    //--进货金额
                            inputParameter.put(26, accountDate);                                   //--入账日期 yyyy-MM-dd
                            inputParameter.put(27, oneData.get("PROD_DATE").toString());    //--批号的生产日期 yyyy-MM-dd
                            inputParameter.put(28, oneData.get("BDATE").toString());            //--单据日期
                            inputParameter.put(29, "退配出库扣库存");             //--异动原因
                            inputParameter.put(30, "");             //--异动描述
                            inputParameter.put(31, req.getOpNO());          //--操作员

                            ProcedureBean pdb = new ProcedureBean(procedure, inputParameter);
                            this.addProcessData(new DataProcessBean(pdb));
                        }


                        //如果是退货出库并且ofno有值的需要去判断一下原单的数量等是否满足，并且需要更新原单的已退货数量
                        if (docType.equals("0") && ofNO != null && !ofNO.isEmpty()) {
                            String sqlstockin = " select * from DCP_STOCKIN_DETAIL A,DCP_STOCKOUT_DETAIL B "
                                    + " where A.EID='" + eId + "' and  A.STOCKINNO='" + ofNO + "' and A.EID=B.EID and A.SHOPID=B.SHOPID  "
                                    + " and B.STOCKOUTNO='" + stockOutNO + "' and A.item=B.oitem  and A.baseqty<(nvl(A.RETWQTY,0)+B.baseqty)  ";

                            List<Map<String, Object>> stokindetail = this.doQueryData(sqlstockin, null);
                            if (stokindetail != null && !stokindetail.isEmpty()) {
                                res.setSuccess(false);
                                res.setServiceDescription("商品:" + stokindetail.get(0).get("PLUNO").toString() + "大于可退货数量");
                                return;
                            }
                            sql3 = this.getQuerySql3(req);
                            String[] conditionValues3 = {organizationNO, eId, stockOutNO};
                            List<Map<String, Object>> getQData3 = this.doQueryData(sql3, conditionValues3);
                            if (getQData3 != null && !getQData3.isEmpty()) {
                                for (Map<String, Object> oneData3 : getQData3) {
                                    //这里要更新原单的RETWQTY
                                    UptBean upstockin = new UptBean("DCP_STOCKIN_DETAIL");
                                    upstockin.addUpdateValue("RETWQTY", new DataValue(Float.parseFloat(oneData3.get("BASEQTY").toString()), Types.FLOAT, DataExpression.UpdateSelf));
                                    // condition
                                    upstockin.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                    upstockin.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//                                    upstockin.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                                    upstockin.addCondition("STOCKINNO", new DataValue(ofNO, Types.VARCHAR));
                                    upstockin.addCondition("ITEM", new DataValue(Integer.parseInt(oneData3.get("OITEM").toString()), Types.INTEGER));
                                    this.addProcessData(new DataProcessBean(upstockin));
                                }
                            }
                        }

                        ub1 = new UptBean("DCP_StockOut");
                        ub1.addUpdateValue("Status", new DataValue("2", Types.VARCHAR));


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
//                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub1));
                        break;
                    }
                    default:

                        ub1 = new UptBean("DCP_StockOut");

                        ub1.addUpdateValue("Status", new DataValue("2", Types.VARCHAR));


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
//                        ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                        ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                        this.addProcessData(new DataProcessBean(ub1));
                        break;
                }

                String dingStatus = getQDataDetail.get(0).get("STATUS").toString(); //判断数据库里的status，if ==-1 ，已经审批中，直接返回
                //判断服务发起调用方，状态==-1且是云中台门店管理发起，直接返回审核中
                if (Check.Null(oEId) && dingStatus.equals("-1")) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批中，请重新查询 ");
                }

                //其他出库单钉钉审批判断，启用钉钉审批  BY JZMA 20191218
                if (docType.equals("3") && status.equals("2") && dingStatus.equals("0")) {
                    //判断是否启用钉钉审批
                    String sql_ding = " select b.def_userid,b.def_deptid,c.approvedbyid,c.approvedbydeptid,a.templateno,d.chsmsg,d.chtmsg from DCP_DING_FUNC a "
                            + " inner join DCP_DING_FUNC_SHOP b on a.EID=b.EID and a.funcno=b.funcno and a.status='100' "
                            + " inner join DCP_DING_FUNC_SHOP_APPROVEDBY c on a.EID=c.EID and a.funcno=c.funcno and b.SHOPID=c.SHOPID and c.status='100' "
                            + " left join DCP_MODULAR d on a.EID=d.EID and a.funcno=d.modularno "
                            + " where a.EID='" + eId + "' and a.funcno='120109'  and c.SHOPID='" + shopId + "'  ";

                    List<Map<String, Object>> getQData = this.doQueryData(sql_ding, null);
                    if (getQData != null && !getQData.isEmpty()) {
                        String funcName = getQData.get(0).get("CHSMSG").toString();
                        if (req.getLangType().equals("zh_TW")) funcName = getQData.get(0).get("CHTMSG").toString();

                        //模板编号检查
                        String processCode = getQData.get(0).get("TEMPLATENO").toString();
                        if (Check.Null(processCode)) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批模板编号未维护  ");
                        }

                        //审批人获取(多审批人时只需一人同意)
                        List<String> approvedbyIds = new ArrayList<>();
                        int approvedbyCount = 0;
                        for (Map<String, Object> oneData : getQData) {
                            approvedbyIds.add(oneData.get("APPROVEDBYID").toString());
                            approvedbyCount++;
                        }
                        if (approvedbyCount == 0) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批人未维护  ");
                        }

                        //申请人获取
                        String originatorUserId = "";
                        String originatorUserName = "";
                        long deptId = 0;
                        sql = "select userid,USERNAME,deptid from DCP_DING_USERSET where EID='" + eId + "' and opno='" + req.getOpNO() + "'  ";
                        List<Map<String, Object>> getUserId = this.doQueryData(sql, null);
                        if (getUserId != null && !getUserId.isEmpty()) {
                            originatorUserId = getUserId.get(0).get("USERID").toString();
                            originatorUserName = getUserId.get(0).get("USERNAME").toString();
                            deptId = Long.parseLong(getUserId.get(0).get("DEPTID").toString());

                        } else {
                            originatorUserId = getQData.get(0).get("DEF_USERID").toString();
                            sql = "select userid,USERNAME,deptid from DCP_DING_USERSET where EID='" + eId + "' and userid='" + originatorUserId + "'  ";
                            List<Map<String, Object>> getUserName = this.doQueryData(sql, null);
                            if (getUserName != null && !getUserName.isEmpty()) {
                                originatorUserName = getUserName.get(0).get("USERNAME").toString();
                            } else {
                                originatorUserName = req.getOpName();
                            }

                            String tempDEPTID = getQData.get(0).get("DEF_DEPTID").toString();
                            if (PosPub.isNumeric(tempDEPTID)) {
                                deptId = Long.parseLong(getQData.get(0).get("DEF_DEPTID").toString());
                            }

                        }
                        if (Check.Null(originatorUserId) || deptId == 0L) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉申请人ID未维护  ");
                        }


                        String bDate = getQDataDetail.get(0).get("BDATE").toString();
                        bDate = bDate.substring(0, 4) + "-" + bDate.substring(4, 6) + "-" + bDate.substring(6, 8);
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

                        String originatorUser = req.getOpName() + "(" + req.getOpNO() + ")";
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

                        List<Object> listvo = new ArrayList<>();
                        for (Map<String, Object> oneData : getQDataDetail) {
                            String pluName = oneData.get("PLU_NAME").toString();
                            String featureName = oneData.get("FEATURENAME").toString();
                            pluName = pluName + featureName;  //特征码
                            String pQty = oneData.get("PQTY").toString();
                            String unitName = oneData.get("UNIT_NAME").toString();
                            String price = oneData.get("PRICE").toString();
                            String amt = oneData.get("AMT").toString();

                            // 明细-单行输入框
                            if (Check.Null(pluName)) pluName = " ";
                            OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName1 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            ItemName1.setName("品名");
                            ItemName1.setValue(pluName);

                            OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName2 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            ItemName2.setName("数量");
                            ItemName2.setValue(pQty);

                            if (Check.Null(unitName)) unitName = " ";
                            OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName3 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            ItemName3.setName("单位");
                            ItemName3.setValue(unitName);

                            if (Check.Null(price)) price = "0";
                            OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName4 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            ItemName4.setName("售价");
                            ItemName4.setValue(price);

                            if (Check.Null(amt)) amt = "0";
                            OapiProcessinstanceCreateRequest.FormComponentValueVo ItemName5 = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                            ItemName5.setName("金额");
                            ItemName5.setValue(amt);
                            listvo.add(Arrays.asList(ItemName1, ItemName2, ItemName3, ItemName4, ItemName5));
                        }

                        OapiProcessinstanceCreateRequest.FormComponentValueVo vo = new OapiProcessinstanceCreateRequest.FormComponentValueVo();
                        vo.setName("商品明细");
                        vo.setValue(JSON.toJSONString(listvo));
                        formComponentValues.add(vo);


                        //审批人多人或单人赋值
                        ProcessInstanceApproverVo approversV2 = new ProcessInstanceApproverVo();
                        List<ProcessInstanceApproverVo> approversV2s = new ArrayList<ProcessInstanceApproverVo>();
                        approversV2.setUserIds(approvedbyIds);
                        if (approvedbyCount == 1) {
                            approversV2.setTaskActionType("NONE");
                        } else {
                            approversV2.setTaskActionType("OR");
                        }
                        approversV2s.add(approversV2);

                        //获取钉钉平台token
                        DCP_DingCallBack dingCallBack = new DCP_DingCallBack();
                        Map<String, String> accessTokenMap = dingCallBack.getDingAccessToken(dao, eId, false);
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
                        if (errcode == 88L || errcode == 40014L) {
                            //获取钉钉平台token
                            accessTokenMap = dingCallBack.getDingAccessToken(dao, eId, true);
                            accessToken = accessTokenMap.getOrDefault("token", "");
                            if (Check.Null(accessToken)) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, accessTokenMap.getOrDefault("errmsg", ""));
                            }
                            response = client.execute(dingDingRequest, accessToken);
                        }

                        errcode = response.getErrcode();
                        String errmsg = response.getErrmsg();
                        String processId = response.getProcessInstanceId();

                        if (errcode != 0L) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "钉钉审批提交失败，错误代码：" + errcode + " 错误原因：" + errmsg);
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
                                        "EID", "PROCESSID", "COMPANYID", "SHOPID", "BILLNO",
                                        "ITEM", "PLUNO", "PLUNAME", "UNITNAME", "QTY", "PRICE", "AMT", "DISCAMT", "FEATURENAME"
                                };
                                String item = oneData.get("ITEM").toString();
                                String pluNo = oneData.get("PLUNO").toString();
                                String pluName = oneData.get("PLU_NAME").toString();
                                String unitName = oneData.get("UNIT_NAME").toString();
                                String qty = oneData.get("PQTY").toString();
                                String price = oneData.get("PRICE").toString();
                                String amt = oneData.get("AMT").toString();
                                String discAmt = "0";
                                String featureName = oneData.get("FEATURENAME").toString();

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
                            String time = timeSDF.format(new Date());

                            //新增钉钉审批单头
                            String[] columns = {
                                    "EID", "PROCESSID", "COMPANYID", "SHOPID", "BILLNO", "BILLTYPE", "FUNCNO", "FUNCNAME",
                                    "CREATETIME", "CREATEOPID", "CREATEOPNAME", "APPLYREASON", "REJECTREASON",
                                    "CHECKOPID", "CHECKOPNAME", "CHECKTIME",
                                    "STATUS", "PROCESS_STATUS"};
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
                String Transfer_Stock = PosPub.getPARA_SMS(dao, eId, shopId, "Transfer_Stock");

                //移仓说明 原有逻辑 一阶段
                if (docType.equals("4") && "N".equals(isTranInConfirm)) {

                    ///检查入库单是否已经审核，审核就退出
                    String exisstockin = "select * from DCP_STOCKIN where EID='" + eId + "' and SHOPID='" + shopId + "'  and load_docno='" + stockOutNO + "'  and status='2'  and doc_type='4'  ";
                    List<Map<String, Object>> stockinlist = this.doQueryData(exisstockin, null);
                    if (stockinlist != null && !stockinlist.isEmpty()) {
                        res.setSuccess(true);
                        res.setServiceStatus("000");
                        res.setServiceDescription("服务执行成功");
                        return;
                    }

                    //                    stockInNO =  this.getStockInNO(req);
                    stockInNO = getOrderNO(req,"YCSH");
                    String bDate = "";
                    String warehouse = "";
                    String transferWarehouse = "";
                    String MEMO = "";
                    String totPqty = "";
                    String totAmt = "";
                    String totDistriAmt = "";
                    String totCqty = "";

                    sql2 = this.getQuerySql2(req);   // 查出库单
                    String[] conditionValues2 = {organizationNO, eId, stockOutNO};
                    List<Map<String, Object>> getQData2 = this.doQueryData(sql2, conditionValues2);
                    if (getQData2 != null && !getQData2.isEmpty()) {
                        bDate = getQData2.get(0).get("BDATE").toString();
                        warehouse = getQData2.get(0).get("WAREHOUSE").toString();
                        transferWarehouse = getQData2.get(0).get("TRANSFER_WAREHOUSE").toString();
                        MEMO = getQData2.get(0).get("MEMO").toString();

                        totPqty = getQData2.get(0).get("TOTPQTY").toString();
                        totAmt = getQData2.get(0).get("TOTAMT").toString();
                        totDistriAmt = getQData2.get(0).get("TOT_DISTRIAMT").toString();
                        totCqty = getQData2.get(0).get("TOTCQTY").toString();
                    }

                    String warehouseSql = "select * from dcp_warehouse a where a.organizationno='" + organizationNO + "'  and a.eid='" + eId + "' and a.warehouse='" + transferWarehouse + "'";
                    List<Map<String, Object>> warehouselist = this.doQueryData(warehouseSql, null);
                    String islocation = warehouselist.get(0).get("ISLOCATION").toString();

                    String tlSql = " select * from DCP_STOCKOUT_DETAIL_LOCATION a where a.eid='" + eId + "' and a.stockoutno='" + stockOutNO + "' and a.organizationno='" + organizationNO + "'";
                    List<Map<String, Object>> tllist = this.doQueryData(tlSql, null);

                    sql3 = this.getQuerySql3(req);  //查出库单明细
                    String[] conditionValues3 = {organizationNO, eId, stockOutNO};
                    List<Map<String, Object>> getQData3 = this.doQueryData(sql3, conditionValues3);

                    if ("Y".equals(islocation)) {
                        for (Map<String, Object> oneData3 : getQData3) {
                            String detailItem = oneData3.get("ITEM").toString();
                            List<Map<String, Object>> filterList = tllist.stream().filter(x -> x.get("OITEM").toString().equals(detailItem)).collect(Collectors.toList());
                            if (filterList.size() <= 0) {
                                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, detailItem + ":出库单明细没有对应的移入库位明细！");
                            }
                        }
                    }

                    if (getQData3 != null && !getQData3.isEmpty()) {
                        if ("Y".equals(islocation)) {
                            int inItem=0;
                            for (Map<String, Object> oneData3 : getQData3) {
                                String detailItem = oneData3.get("ITEM2").toString();
                                BigDecimal price = new BigDecimal(oneData3.get("PRICE").toString());
                                BigDecimal distriPrice = new BigDecimal(oneData3.get("DISTRIPRICE").toString());

                                List<Map<String, Object>> filterList = tllist.stream().filter(x -> x.get("OITEM").toString().equals(detailItem)).collect(Collectors.toList());
                                for (Map<String, Object> fData : filterList) {
                                    inItem++;
                                    BigDecimal fPqty = new BigDecimal(fData.get("PQTY").toString());
                                    BigDecimal amt = fPqty.multiply(price);
                                    BigDecimal distriAmt = fPqty.multiply(distriPrice);

                                    ColumnDataValue dcp_stockin_detail = new ColumnDataValue();
                                    dcp_stockin_detail.add("STOCKINNO", DataValues.newString(stockInNO));
                                    dcp_stockin_detail.add("SHOPID", DataValues.newString(shopId));
                                    dcp_stockin_detail.add("EID", DataValues.newString(eId));
                                    dcp_stockin_detail.add("organizationNO", DataValues.newString(organizationNO));
                                    dcp_stockin_detail.add("item", DataValues.newInteger(inItem));
                                    dcp_stockin_detail.add("OType", DataValues.newString(""));
                                    dcp_stockin_detail.add("OFNO", DataValues.newString(oneData3.get("STOCKOUTNO").toString()));
                                    dcp_stockin_detail.add("OItem", DataValues.newInteger(oneData3.get("ITEM").toString()));
                                    dcp_stockin_detail.add("PLUNO", DataValues.newString(oneData3.get("PLUNO").toString()));
                                    dcp_stockin_detail.add("Punit", DataValues.newString(oneData3.get("PUNIT").toString()));
                                    dcp_stockin_detail.add("PQTY", DataValues.newDecimal(fData.get("PQTY").toString()));
                                    dcp_stockin_detail.add("RECEIVING_QTY", DataValues.newDecimal(fData.get("PQTY").toString()));
                                    dcp_stockin_detail.add("BASEUNIT", DataValues.newString(oneData3.get("BASEUNIT").toString()));
                                    dcp_stockin_detail.add("BASEQTY", DataValues.newDecimal(fData.get("BASEQTY").toString()));
                                    dcp_stockin_detail.add("unit_Ratio", DataValues.newDecimal(oneData3.get("UNITRATIO").toString()));
                                    dcp_stockin_detail.add("Price", DataValues.newDecimal(oneData3.get("PRICE").toString()));
                                    dcp_stockin_detail.add("AMT", DataValues.newDecimal(amt.toString()));
                                    dcp_stockin_detail.add("WAREHOUSE", DataValues.newString(transferWarehouse));
                                    dcp_stockin_detail.add("PLU_MEMO", DataValues.newString(oneData3.get("PLUMEMO").toString()));
                                    dcp_stockin_detail.add("BATCH_NO", DataValues.newString(oneData3.get("BATCH_NO").toString()));
                                    dcp_stockin_detail.add("PROD_DATE", DataValues.newString(oneData3.get("PROD_DATE").toString()));
                                    dcp_stockin_detail.add("DISTRIPRICE", DataValues.newDecimal(oneData3.get("DISTRIPRICE").toString()));
                                    dcp_stockin_detail.add("DISTRIAMT", DataValues.newDecimal(distriAmt.toString()));
                                    dcp_stockin_detail.add("BDATE", DataValues.newString(bDate));
                                    dcp_stockin_detail.add("FEATURENO", DataValues.newString(oneData3.get("FEATURENO").toString()));
                                    dcp_stockin_detail.add("MES_LOCATION", DataValues.newString(fData.get("LOCATION").toString()));

                                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKIN_DETAIL", dcp_stockin_detail)));

                                    //这两笔库存异动是移仓收货的
                                    BcReq bcReq=new BcReq();
                                    bcReq.setServiceType("StockInProcess");
                                    bcReq.setDocType("4");
                                    bcReq.setBillType("19");
                                    bcReq.setSyneryDiff(false);

                                    BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                                    String bType = bcMap.getBType();
                                    String costCode = bcMap.getCostCode();
                                    if(Check.Null(bType)||Check.Null(costCode)){
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                                    }

                                    Map<Integer, Object> inputParameterIn = new HashMap<Integer, Object>();
                                    inputParameterIn.put(1, eId);                                       //--企业ID
                                    inputParameterIn.put(2, null);
                                    inputParameterIn.put(3, organizationNO);                            //--组织
                                    inputParameterIn.put(4, bType);
                                    inputParameterIn.put(5, costCode);
                                    inputParameterIn.put(6, "19");                              //--单据类型
                                    inputParameterIn.put(7, stockInNO);                                   //--单据号
                                    inputParameterIn.put(8, oneData3.get("ITEM").toString());            //--单据行号
                                    inputParameterIn.put(9, "0");
                                    inputParameterIn.put(10, "1");                                       //--异动方向 1=加库存 -1=减库存
                                    inputParameterIn.put(11, bDate);           //--营业日期 yyyy-MM-dd
                                    inputParameterIn.put(12, oneData3.get("PLUNO").toString());           //--品号
                                    inputParameterIn.put(13, oneData3.get("FEATURENO").toString());       //--特征码
                                    inputParameterIn.put(14, transferWarehouse);                           //--仓库
                                    inputParameterIn.put(15, oneData3.get("BATCH_NO").toString());       //--批号
                                    inputParameterIn.put(16, fData.get("LOCATION").toString());       //--批号
                                    inputParameterIn.put(17, oneData3.get("PUNIT").toString());          //--交易单位
                                    inputParameterIn.put(18, fData.get("PQTY").toString());           //--交易数量
                                    inputParameterIn.put(19, oneData3.get("BASEUNIT").toString());       //--基准单位
                                    inputParameterIn.put(20, fData.get("BASEQTY").toString());        //--基准数量
                                    inputParameterIn.put(21, oneData3.get("UNITRATIO").toString());     //--换算比例
                                    inputParameterIn.put(22, oneData3.get("PRICE").toString());          //--零售价
                                    inputParameterIn.put(23, amt.toString());            //--零售金额
                                    inputParameterIn.put(24, oneData3.get("DISTRIPRICE").toString());    //--进货价
                                    inputParameterIn.put(25, distriAmt.toString());      //--进货金额
                                    inputParameterIn.put(26, accountDate);                              //--入账日期 yyyy-MM-dd
                                    inputParameterIn.put(27, oneData3.get("PROD_DATE").toString());      //--批号的生产日期 yyyy-MM-dd
                                    inputParameterIn.put(28, bDate);          //--单据日期
                                    inputParameterIn.put(29, "");                                       //--异动原因
                                    inputParameterIn.put(30, "");                                       //--异动描述
                                    inputParameterIn.put(31, opNo);                                     //--操作员

                                    ProcedureBean pdbIn = new ProcedureBean("SP_DCP_STOCKCHANGE_VX", inputParameterIn);
                                    this.addProcessData(new DataProcessBean(pdbIn));

                                }

                            }

                        }
                        else {
                            for (Map<String, Object> oneData3 : getQData3) {

                                ColumnDataValue dcp_stockin_detail = new ColumnDataValue();
                                dcp_stockin_detail.add("STOCKINNO", DataValues.newString(stockInNO));
                                dcp_stockin_detail.add("SHOPID", DataValues.newString(shopId));
                                dcp_stockin_detail.add("EID", DataValues.newString(eId));
                                dcp_stockin_detail.add("organizationNO", DataValues.newString(organizationNO));
                                dcp_stockin_detail.add("item", DataValues.newInteger(oneData3.get("ITEM").toString()));
                                dcp_stockin_detail.add("OType", DataValues.newString(""));
                                dcp_stockin_detail.add("OFNO", DataValues.newString(oneData3.get("STOCKOUTNO").toString()));
                                dcp_stockin_detail.add("OItem", DataValues.newInteger(oneData3.get("ITEM").toString()));
                                dcp_stockin_detail.add("PLUNO", DataValues.newString(oneData3.get("PLUNO").toString()));
                                dcp_stockin_detail.add("Punit", DataValues.newString(oneData3.get("PUNIT").toString()));
                                dcp_stockin_detail.add("PQTY", DataValues.newDecimal(oneData3.get("PQTY").toString()));
                                dcp_stockin_detail.add("RECEIVING_QTY", DataValues.newDecimal(oneData3.get("PQTY").toString()));
                                dcp_stockin_detail.add("BASEUNIT", DataValues.newString(oneData3.get("BASEUNIT").toString()));
                                dcp_stockin_detail.add("BASEQTY", DataValues.newDecimal(oneData3.get("BASEQTY").toString()));
                                dcp_stockin_detail.add("unit_Ratio", DataValues.newDecimal(oneData3.get("UNITRATIO").toString()));
                                dcp_stockin_detail.add("Price", DataValues.newDecimal(oneData3.get("PRICE").toString()));
                                dcp_stockin_detail.add("AMT", DataValues.newDecimal(oneData3.get("AMT").toString()));
                                dcp_stockin_detail.add("WAREHOUSE", DataValues.newString(transferWarehouse));
                                dcp_stockin_detail.add("PLU_MEMO", DataValues.newString(oneData3.get("PLUMEMO").toString()));
                                dcp_stockin_detail.add("BATCH_NO", DataValues.newString(oneData3.get("BATCH_NO").toString()));
                                dcp_stockin_detail.add("PROD_DATE", DataValues.newString(oneData3.get("PROD_DATE").toString()));
                                dcp_stockin_detail.add("DISTRIPRICE", DataValues.newDecimal(oneData3.get("DISTRIPRICE").toString()));
                                dcp_stockin_detail.add("DISTRIAMT", DataValues.newDecimal(oneData3.get("DISTRIAMT").toString()));
                                dcp_stockin_detail.add("BDATE", DataValues.newString(bDate));
                                //                                dcp_stockin_detail.add("LOCATION",DataValues.newString(fData.get("LOCATION").toString()));
                                dcp_stockin_detail.add("FEATURENO", DataValues.newString(oneData3.get("FEATURENO").toString()));

                                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKIN_DETAIL", dcp_stockin_detail)));

                                BcReq bcReq=new BcReq();
                                bcReq.setServiceType("StockInProcess");
                                bcReq.setDocType("4");
                                bcReq.setBillType("19");
                                bcReq.setSyneryDiff(false);

                                BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                                String bType = bcMap.getBType();
                                String costCode = bcMap.getCostCode();
                                if(Check.Null(bType)||Check.Null(costCode)){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                                }

                                Map<Integer, Object> inputParameterIn = new HashMap<Integer, Object>();
                                inputParameterIn.put(1, eId);                                       //--企业ID
                                inputParameterIn.put(2, null);
                                inputParameterIn.put(3, organizationNO);                            //--组织
                                inputParameterIn.put(4, bType);
                                inputParameterIn.put(5, costCode);
                                inputParameterIn.put(6, "19");                              //--单据类型
                                inputParameterIn.put(7, stockInNO);                                   //--单据号
                                inputParameterIn.put(8, oneData3.get("ITEM").toString());            //--单据行号
                                inputParameterIn.put(9, "0");
                                inputParameterIn.put(10, "1");                                       //--异动方向 1=加库存 -1=减库存
                                inputParameterIn.put(11, bDate);           //--营业日期 yyyy-MM-dd
                                inputParameterIn.put(12, oneData3.get("PLUNO").toString());           //--品号
                                inputParameterIn.put(13, oneData3.get("FEATURENO").toString());       //--特征码
                                inputParameterIn.put(14, transferWarehouse);                           //--仓库
                                inputParameterIn.put(15, oneData3.get("BATCH_NO").toString());       //--批号
                                inputParameterIn.put(16, " ");       //--批号
                                inputParameterIn.put(17, oneData3.get("PUNIT").toString());          //--交易单位
                                inputParameterIn.put(18, oneData3.get("PQTY").toString());           //--交易数量
                                inputParameterIn.put(19, oneData3.get("BASEUNIT").toString());       //--基准单位
                                inputParameterIn.put(20, oneData3.get("BASEQTY").toString());        //--基准数量
                                inputParameterIn.put(21, oneData3.get("UNITRATIO").toString());     //--换算比例
                                inputParameterIn.put(22, oneData3.get("PRICE").toString());          //--零售价
                                inputParameterIn.put(23, oneData3.get("AMT").toString());            //--零售金额
                                inputParameterIn.put(24, oneData3.get("DISTRIPRICE").toString());    //--进货价
                                inputParameterIn.put(25, oneData3.get("DISTRIAMT").toString());      //--进货金额
                                inputParameterIn.put(26, accountDate);                              //--入账日期 yyyy-MM-dd
                                inputParameterIn.put(27, oneData3.get("PROD_DATE").toString());      //--批号的生产日期 yyyy-MM-dd
                                inputParameterIn.put(28, bDate);          //--单据日期
                                inputParameterIn.put(29, "");                                       //--异动原因
                                inputParameterIn.put(30, "");                                       //--异动描述
                                inputParameterIn.put(31, opNo);                                     //--操作员

                                ProcedureBean pdbIn = new ProcedureBean("SP_DCP_STOCKCHANGE_VX", inputParameterIn);
                                this.addProcessData(new DataProcessBean(pdbIn));
                            }
                        }
                    }

                    ColumnDataValue dcp_stockin = new ColumnDataValue();

                    dcp_stockin.add("SHOPID", DataValues.newString(shopId));
                    dcp_stockin.add("organizationNO", DataValues.newString(organizationNO));
                    dcp_stockin.add("createBy", DataValues.newString(opNo));
                    dcp_stockin.add("create_date", DataValues.newString(sDate));
                    dcp_stockin.add("create_time", DataValues.newString(sTime));
                    dcp_stockin.add("tot_pqty", DataValues.newString(totPqty));
                    dcp_stockin.add("tot_amt", DataValues.newString(totAmt));
                    dcp_stockin.add("tot_cqty", DataValues.newString(totCqty));
                    dcp_stockin.add("doc_type", DataValues.newString("4"));
                    dcp_stockin.add("status", DataValues.newString("2"));
                    dcp_stockin.add("process_status", DataValues.newString("Y"));
                    dcp_stockin.add("EID", DataValues.newString(eId));
                    dcp_stockin.add("stockInNO", DataValues.newString(stockInNO));
                    dcp_stockin.add("BDATE", DataValues.newString(bDate));
                    dcp_stockin.add("WAREHOUSE", DataValues.newString(transferWarehouse));
                    dcp_stockin.add("TRANSFER_WAREHOUSE", DataValues.newString(warehouse));
                    dcp_stockin.add("LOAD_DOCTYPE", DataValues.newString(docType));
                    dcp_stockin.add("LOAD_DOCNO", DataValues.newString(request.getStockOutNo()));
                    dcp_stockin.add("MEMO", DataValues.newString(MEMO));
                    dcp_stockin.add("AccountBy", DataValues.newString(opNo));
                    dcp_stockin.add("Account_Date", DataValues.newString(accountDate));
                    dcp_stockin.add("Account_Time", DataValues.newString(sTime));
                    dcp_stockin.add("SubmitBy", DataValues.newString(opNo));
                    dcp_stockin.add("Submit_Date", DataValues.newString(sDate));
                    dcp_stockin.add("Submit_Time", DataValues.newString(sTime));
                    dcp_stockin.add("ConfirmBy", DataValues.newString(opNo));
                    dcp_stockin.add("Confirm_Date", DataValues.newString(sDate));
                    dcp_stockin.add("Confirm_Time", DataValues.newString(sTime));
                    dcp_stockin.add("TOT_DISTRIAMT", DataValues.newString(totDistriAmt));
                    dcp_stockin.add("CREATE_CHATUSERID", DataValues.newString(req.getChatUserId()));
                    dcp_stockin.add("ACCOUNT_CHATUSERID", DataValues.newString(req.getChatUserId()));
                    dcp_stockin.add("UPDATE_TIME", DataValues.newString(DateFormatUtils.getTimestamp()));
                    dcp_stockin.add("TRAN_TIME", DataValues.newString(DateFormatUtils.getTimestamp()));
                    dcp_stockin.add("EMPLOYEEID", DataValues.newString(outEmployeeId));
                    dcp_stockin.add("DEPARTID", DataValues.newString(outDepartId));
                    dcp_stockin.add("OOTYPE", DataValues.newString(docType));
                    dcp_stockin.add("OOFNO", DataValues.newString(request.getStockOutNo()));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_STOCKIN", dcp_stockin)));
                }


                //下面包含库存异动 配货出库排除  doctype=5 放到上面单独处理
                if ((Enable_InTransit.equals("Y") || Check.Null(Transfer_Stock) || Transfer_Stock.equals("1")
                        || (Transfer_Stock.equals("2") && !docType.equals("1"))) && !docType.equals("5") && !docType.equals("0") && !docType.equals("4") && !"Y".equals(isTranInConfirm)) {
                    List<Map<String, Object>> getQData_checkStockDetail = null;
                    if (getQData_checkStockDetail == null || getQData_checkStockDetail.isEmpty()) {
                        if (getQDataDetail != null && !getQDataDetail.isEmpty()) {
                            for (Map<String, Object> oneData : getQDataDetail) {
                                String stockDocType = "";
                                switch (docType) {
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
                                }

                                String stockWarehouse = oneData.get("WAREHOUSE").toString();
                                if (inv_cost_warehouse.equals(stockWarehouse) && Enable_InTransit.equals("Y")) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "出货仓库 和 在途成本仓库 一致(" + stockWarehouse + "),不能出库！");
                                }
                                //判断仓库不能为空或空格  BY JZMA 20191118
                                if (Check.Null(stockWarehouse) || Check.Null(stockWarehouse.trim())
                                        || stockWarehouse.trim().isEmpty()) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");
                                }

                                String featureNo = oneData.get("FEATURENO").toString();  ///  特征码为空给空格
                                if (Check.Null(featureNo))
                                    featureNo = " ";


                                BcReq bcReq=new BcReq();
                                bcReq.setServiceType("StockOutProcess");
                                bcReq.setDocType(docType);
                                bcReq.setBillType(stockDocType);

                                BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                                String bType = bcMap.getBType();
                                String costCode = bcMap.getCostCode();
                                if(Check.Null(bType)||Check.Null(costCode)){
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                                }

//                                String procedure = "SP_DCP_StockChange";
                                String procedure = "SP_DCP_STOCKCHANGE_VX";
                                Map<Integer, Object> inputParameterOut = new HashMap<Integer, Object>();
                                inputParameterOut.put(1, eId);                                      //--企业ID
                                inputParameterOut.put(2, null);
                                inputParameterOut.put(3, organizationNO);                           //--组织
                                inputParameterOut.put(4, bType);
                                inputParameterOut.put(5, costCode);
                                inputParameterOut.put(6, stockDocType);                             //--单据类型
                                inputParameterOut.put(7, oneData.get("STOCKOUTNO").toString());       //--单据号
                                inputParameterOut.put(8, oneData.get("ITEM").toString());           //--单据行号
                                inputParameterOut.put(9, "0");
                                inputParameterOut.put(10, "-1");                                     //--异动方向 1=加库存 -1=减库存
                                inputParameterOut.put(11, oneData.get("BDATE").toString());          //--营业日期 yyyy-MM-dd
                                inputParameterOut.put(12, oneData.get("PLUNO").toString());          //--品号
                                inputParameterOut.put(13, featureNo);                                //--特征码
                                inputParameterOut.put(14, stockWarehouse);                          //--仓库
                                inputParameterOut.put(15, oneData.get("BATCH_NO").toString());      //--批号
                                inputParameterOut.put(16, oneData.get("MES_LOCATION").toString());     //--location
                                inputParameterOut.put(17, oneData.get("PUNIT").toString());         //--交易单位
                                inputParameterOut.put(18, oneData.get("PQTY").toString());          //--交易数量
                                inputParameterOut.put(19, oneData.get("BASEUNIT").toString());      //--基准单位
                                inputParameterOut.put(20, oneData.get("BASEQTY").toString());       //--基准数量
                                inputParameterOut.put(21, oneData.get("UNIT_RATIO").toString());    //--换算比例
                                inputParameterOut.put(22, oneData.get("PRICE").toString());         //--零售价
                                inputParameterOut.put(23, oneData.get("AMT").toString());           //--零售金额
                                inputParameterOut.put(24, oneData.get("DISTRIPRICE").toString());   //--进货价
                                inputParameterOut.put(25, oneData.get("DISTRIAMT").toString());     //--进货金额
                                inputParameterOut.put(26, accountDate);                             //--入账日期 yyyy-MM-dd
                                inputParameterOut.put(27, oneData.get("PROD_DATE").toString());     //--批号的生产日期 yyyy-MM-dd
                                inputParameterOut.put(28, oneData.get("BDATE").toString());         //--单据日期
                                inputParameterOut.put(29, "");                                      //--异动原因
                                inputParameterOut.put(30, "");                                      //--异动描述
                                inputParameterOut.put(31, opNo);                                    //--操作员

                                ProcedureBean pdbOut = new ProcedureBean(procedure, inputParameterOut);
                                this.addProcessData(new DataProcessBean(pdbOut));

                                //增加在库数 //如果启用在途
                                if (Enable_InTransit.equals("Y") || docType.equals("4")) {
                                    stockDocType = "";
                                    if (docType.equals("4")) {
                                        stockDocType = "19";
                                    } else {
                                        stockDocType = "12";
                                    }
                                    String stockDocNo = "";
                                    if (docType.equals("4")) {
                                        stockDocNo = stockInNO; //DOCNO调整为入库单的单号
                                    } else {
                                        stockDocNo = oneData.get("STOCKOUTNO").toString();
                                    }
                                    stockWarehouse = "";
                                    if (docType.equals("4")) {
                                        stockWarehouse = oneData.get("TRANSFER_WAREHOUSE").toString();//拨入仓库
                                    } else {
                                        stockWarehouse = inv_cost_warehouse;
                                    }
                                    //判断仓库不能为空或空格  BY JZMA 20191118
                                    if (Check.Null(stockWarehouse) || Check.Null(stockWarehouse.trim())
                                            || stockWarehouse.trim().isEmpty()) {
                                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");
                                    }

                                    Map<Integer, Object> inputParameterIn = new HashMap<Integer, Object>();
                                    inputParameterIn.put(1, eId);                                       //--企业ID
                                    inputParameterIn.put(2, organizationNO);                            //--组织
                                    inputParameterIn.put(3, stockDocType);                              //--单据类型
                                    inputParameterIn.put(4, stockDocNo);                                   //--单据号
                                    inputParameterIn.put(5, oneData.get("ITEM").toString());            //--单据行号
                                    inputParameterIn.put(6, "1");                                       //--异动方向 1=加库存 -1=减库存
                                    inputParameterIn.put(7, oneData.get("BDATE").toString());           //--营业日期 yyyy-MM-dd
                                    inputParameterIn.put(8, oneData.get("PLUNO").toString());           //--品号
                                    inputParameterIn.put(9, featureNo);       //--特征码
                                    inputParameterIn.put(10, stockWarehouse);                           //--仓库
                                    inputParameterIn.put(11, oneData.get("BATCH_NO").toString());       //--批号
                                    inputParameterIn.put(12, oneData.get("MES_LOCATION").toString());       //--批号
                                    inputParameterIn.put(13, oneData.get("PUNIT").toString());          //--交易单位
                                    inputParameterIn.put(14, oneData.get("PQTY").toString());           //--交易数量
                                    inputParameterIn.put(15, oneData.get("BASEUNIT").toString());       //--基准单位
                                    inputParameterIn.put(16, oneData.get("BASEQTY").toString());        //--基准数量
                                    inputParameterIn.put(17, oneData.get("UNIT_RATIO").toString());     //--换算比例
                                    inputParameterIn.put(18, oneData.get("PRICE").toString());          //--零售价
                                    inputParameterIn.put(19, oneData.get("AMT").toString());            //--零售金额
                                    inputParameterIn.put(20, oneData.get("DISTRIPRICE").toString());    //--进货价
                                    inputParameterIn.put(21, oneData.get("DISTRIAMT").toString());      //--进货金额
                                    inputParameterIn.put(22, accountDate);                              //--入账日期 yyyy-MM-dd
                                    inputParameterIn.put(23, oneData.get("PROD_DATE").toString());      //--批号的生产日期 yyyy-MM-dd
                                    inputParameterIn.put(24, oneData.get("BDATE").toString());          //--单据日期
                                    inputParameterIn.put(25, "");                                       //--异动原因
                                    inputParameterIn.put(26, "");                                       //--异动描述
                                    inputParameterIn.put(27, opNo);                                     //--操作员

                                    ProcedureBean pdbIn = new ProcedureBean(procedure, inputParameterIn);
                                    this.addProcessData(new DataProcessBean(pdbIn));

                                }
                            }
                        }
                    }
                }


                //出库单确认：来源单号类型为出货通知单（包括3配货通知 4退配通知 5调拨通知 6移仓通知），确认后回写单据状态、明细的已出货量以及明细状态
                //1-已出货量>=通知出货量，明细状态=2-出货结束；
                //2-明细状态全部为2-出货结束，单据状态=2-出货结束；否则更新至1-待出货（剩下未出完的可以继续出货或者结束出货）
                //服务：/DCP_StockOutProcess
                if (oType.equals("3") || oType.equals("4") || oType.equals("5") || oType.equals("6")) {
                    //先把单身存在的ofno弄出来
                    String detailSql = "select distinct a.ofno from dcp_stockout_detail a where a.eid='" + eId + "'" +
                            " and a.organizationno='" + organizationNO + "' " +
                            " and a.stockoutno='" + stockOutNO + "'";
                    List<Map<String, Object>> ofList = this.doQueryData(detailSql, null);
                    for (Map<String, Object> ofData : ofList) {
                        String ofNoD = ofData.get("OFNO").toString();

                        String upSql = "select nvl(b.stockoutno,'') as stockoutno, a.billno,a.item,nvl(a.STOCKOUTQTY,0) as STOCKOUTQTY," +
                                " nvl(c.pqty,0) as pqty,nvl(a.pqty,0) as npqty,a.status,b.status as stockoutstatus " +
                                " from DCP_STOCKOUTNOTICE_DETAIL a " +
                                " left join dcp_stockout_detail c on a.eid=c.eid  and a.item=c.oitem and a.billno=c.ofno " +
                                " left join dcp_stockout b on a.eid=b.eid and c.organizationno=b.organizationno and c.stockoutno=b.stockoutno " +
                                " where a.eid='" + eId + "'  and a.billno='" + ofNoD + "' ";
                        List<Map<String, Object>> getUpDetail = this.doQueryData(upSql, null);

                        String thisStockOutSql = "select a.oitem,a.ofno,sum(a.pqty) pqty from dcp_stockout_detail a where a.stockoutno='" + stockOutNO + "'" +
                                " and a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' " +
                                " group by a.ofno,a.oitem ";
                        List<Map<String, Object>> thisStockDList = this.doQueryData(thisStockOutSql, null);

                        List detailStatusList = new ArrayList();
                        //getUpDetail 这边会重复 没关系  数量重新
                        for (Map<String, Object> oneData : getUpDetail) {
                            String billNo = oneData.get("BILLNO").toString();
                            String nStatus = oneData.get("STATUS").toString();
                            String item = oneData.get("ITEM").toString();
                            String checkStatus = oneData.get("STOCKOUTSTATUS").toString();
                            String nowStockoutNo = oneData.get("STOCKOUTNO").toString();
                            BigDecimal stockoutQty = new BigDecimal(oneData.get("STOCKOUTQTY").toString());
                            BigDecimal pQty = new BigDecimal(oneData.get("PQTY").toString());
                            BigDecimal npQty = new BigDecimal(oneData.get("NPQTY").toString());
                            String detailStatus = "";
                            if (Check.Null(billNo) || Check.Null(item)) {
                                continue;
                            }
                            if ("2".equals(checkStatus)) {
                                continue;
                            }
                            //分段的时候同一条明细有两条出库数据  状态都是4
                            //需要排除掉已经审核过的出库单
                            if (!nowStockoutNo.equals(stockOutNO)) {
                                //该条不是这次出库单的
                                if (!detailStatusList.contains(nStatus)) {
                                    detailStatusList.add(nStatus);
                                }
                                continue;
                            }

                            List<Map<String, Object>> collect = thisStockDList.stream().filter(x -> x.get("OFNO").toString().equals(billNo) && x.get("OITEM").toString().equals(item)).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                pQty = new BigDecimal(collect.get(0).get("PQTY").toString());
                            }

                            stockoutQty = stockoutQty.add(pQty);
                            if (stockoutQty.compareTo(npQty) < 0) {
                                detailStatus = "1";
                            } else {
                                detailStatus = "2";
                            }
                            if (!detailStatusList.contains(detailStatus)) {
                                detailStatusList.add(detailStatus);
                            }

                            UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE_DETAIL");
                            upNotice.addUpdateValue("STATUS", new DataValue(Float.parseFloat(detailStatus), Types.VARCHAR));
                            upNotice.addUpdateValue("STOCKOUTQTY", new DataValue(stockoutQty, Types.VARCHAR));
                            // condition
                            //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                            upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                            upNotice.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));
                            upNotice.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
                            this.addProcessData(new DataProcessBean(upNotice));


                        }

                        if (detailStatusList.size() > 1) {

                            // 存在行状态=【4-出货中】/【3-已派工】，则单据状态=【4-出货中】
                            if (detailStatusList.contains("3") || detailStatusList.contains("4")) {

                                UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                upNotice.addUpdateValue("STATUS", new DataValue("4", Types.VARCHAR));
                                // condition
                                //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                upNotice.addCondition("BILLNO", new DataValue(ofNoD, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(upNotice));
                            } else {

                                UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                upNotice.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
                                // condition
                                //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                upNotice.addCondition("BILLNO", new DataValue(ofNoD, Types.VARCHAR));
                                this.addProcessData(new DataProcessBean(upNotice));
                            }

                        } else {
                            if (detailStatusList.size() == 1) {
                                if (detailStatusList.get(0).equals("1")) {
                                    UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                    upNotice.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));
                                    // condition
                                    //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                    upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    upNotice.addCondition("BILLNO", new DataValue(ofNoD, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(upNotice));
                                } else if (detailStatusList.get(0).equals("2")) {
                                    UptBean upNotice = new UptBean("DCP_STOCKOUTNOTICE");
                                    upNotice.addUpdateValue("STATUS", new DataValue("2", Types.VARCHAR));
                                    // condition
                                    //upNotice.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                                    upNotice.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                                    upNotice.addCondition("BILLNO", new DataValue(ofNoD, Types.VARCHAR));
                                    this.addProcessData(new DataProcessBean(upNotice));
                                }
                            }
                        }

                    }

                }

                break;
            case "0": // 作废
                break;
            case "6":

//传入status=6.已作废：【功能：作废】
//作废前检查：单据状态<>"0.新建"不可作废
//作废单据更新：
//1.更新单据状态=6.已作废，作废人、作废日期、作废时间
//2.根据【来源单号+来源项次】关联查询更新来源通知单明细行状态为[1-待出货]
                if (!stockOutStatus.equals("0")) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态<>" + stockOutStatus + "不可作废");
                }
                ub1 = new UptBean("DCP_StockOut");

                // add value
                ub1.addUpdateValue("STATUS", new DataValue("6", Types.VARCHAR));
                ub1.addUpdateValue("CANCELBY", new DataValue(opNo, Types.VARCHAR));
                ub1.addUpdateValue("CANCEL_Date", new DataValue(sDate, Types.VARCHAR));
                ub1.addUpdateValue("CANCEL_Time", new DataValue(sTime, Types.VARCHAR));
                ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

                // condition
                ub1.addCondition("OrganizationNO", new DataValue(organizationNO, Types.VARCHAR));
                ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
                ub1.addCondition("StockOutNO", new DataValue(stockOutNO, Types.VARCHAR));

                this.addProcessData(new DataProcessBean(ub1));

                String stockUnionSql = "select distinct b.eid,b.organizationno,b.billno,b.item from dcp_stockout_detail a " +
                        " left join DCP_STOCKOUTNOTICE_DETAIL b  on a.oitem=b.item and a.ofno=b.billno and a.eid=b.eid and a.organizationno=b.organizationno " +
                        " where a.eid='" + eId + "' and a.organizationno='" + organizationNO + "' and a.stockoutNo='" + stockOutNO + "' ";
                List<Map<String, Object>> list = this.doQueryData(stockUnionSql, null);
                for (Map<String, Object> oneData : list) {
                    String eid = oneData.get("EID").toString();
                    String organizationno = oneData.get("ORGANIZATIONNO").toString();
                    String billno = oneData.get("BILLNO").toString();
                    String item = oneData.get("ITEM").toString();

                    if (Check.Null(billno) || Check.Null(item)) {
                        continue;
                    }

                    ub1 = new UptBean("DCP_STOCKOUTNOTICE_DETAIL");

                    // add value
                    ub1.addUpdateValue("STATUS", new DataValue("1", Types.VARCHAR));//1.待出货

                    // condition
                    ub1.addCondition("OrganizationNO", new DataValue(organizationno, Types.VARCHAR));
                    ub1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
                    ub1.addCondition("BILLNO", new DataValue(billno, Types.VARCHAR));
                    ub1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));

                    this.addProcessData(new DataProcessBean(ub1));
                }


                break;
            default:
                break;
        }

        //审核的时候需要写在途，写到这里
        String receivingNo = "";
        if (!"7".equals(oType) && StringUtils.isEmpty(stockoutno_origin)){  //红冲单 和 差异生成的单子不写Receiving
            if ("0".equals(docType)) {
                receivingNo = getOrderNO(req, "SHTZ");
            } else if ("1".equals(docType)) {
                receivingNo = getOrderNO(req, "DBTZ");
            } else if ("4".equals(docType)) {
                if ("Y".equals(isTranInConfirm)) {
                    receivingNo = getOrderNO(req, "DBTZ");
                    createReceivingNo = receivingNo;
                } else {
                    receivingNo = "";
                }
            } else if ("5".equals(docType)) {
                receivingNo = getOrderNO(req, "SHTZ");
            } else {
                receivingNo = getOrderNO(req, "");
            }
        }


        //在途仓库存异动
        List<DataProcessBean> newDatabeans = new ArrayList<>();
        switch (docType) {
            case "0":
            case "1":
            case "4":
            case "5":
                newDatabeans = new TransitStockAdjust().stockOutWarehouse(eId,
                        stockOutNO,
                        shopId,
                        organizationNO,
                        opNo,
                        req.getDepartmentNo(),
                        receivingNo,
                        allocList
                        );
        }
        if (!newDatabeans.isEmpty()) {
            for (DataProcessBean bean : newDatabeans) {
                addProcessData(bean);
            }
        }

        this.doExecuteDataToDB();

        /* 无需审核，生成的单子是审核状态，审核中目前无其它逻辑
        if (!Check.Null(createReceivingNo)) {
            ParseJson pj = new ParseJson();
            DCP_ReceivingStatusUpdateReq receivingReq = new DCP_ReceivingStatusUpdateReq();
            receivingReq.setServiceId("DCP_ReceivingStatusUpdate");
            receivingReq.setToken(req.getToken());
            DCP_ReceivingStatusUpdateReq.Request requestR = receivingReq.new Request();
            requestR.setOpType("confirm");
            requestR.setReceivingNo(createReceivingNo);
            requestR.setDocType("3");
            receivingReq.setRequest(requestR);

            String jsontemp = pj.beanToJson(receivingReq);

            DispatchService ds = DispatchService.getInstance();
            String resXml = ds.callService(jsontemp, StaticInfo.dao);
            DCP_ReceivingStatusUpdateRes resserver = pj.jsonToBean(resXml, new TypeToken<DCP_ReceivingStatusUpdateRes>() {
            });
            if (!resserver.isSuccess()) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "通知单审核失败！");
            }
        }
        **/
        if("2".equals(status)){
            DCP_InterSettleDataProcessReq isdpr = new DCP_InterSettleDataProcessReq();
            isdpr.setServiceId("DCP_InterSettleDataProcess");
            isdpr.setToken(req.getToken());
            isdpr.setRequest(isdpr.new Request());
            isdpr.getRequest().setBillNo(req.getRequest().getStockOutNo());
            isdpr.getRequest().setOprType("confirm");
            ServiceAgentUtils<DCP_InterSettleDataProcessReq, DCP_InterSettleDataProcessRes> serviceAgent = new ServiceAgentUtils<>();
            if (!serviceAgent.agentServiceSuccess(isdpr, new TypeToken<DCP_InterSettleDataProcessRes>() {
            })) {
                res.setServiceDescription("调用内部交易结算数据审核/反审核失败");
            }
        }


        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        //***********调用库存同步给三方，这是个异步，不会影响效能*****************
        try {
            //WebHookService.stockSync(eId,shopId,stockOutNO);
        } catch (Exception e) {

        }

        //}
        //catch (Exception e) {
        //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());

        //【ID1032555】【乐沙儿3.3.0.3】在门店管理出库单据点确定时，存在负库存时的提示返回error executing work，
        // 需要能够返回SP_DCP_StockChange返回的报错，提示门店  by jinzma 20230526
        //String description=e.getMessage();
        //try {
        //	StringWriter errors = new StringWriter();
        //	PrintWriter pw=new PrintWriter(errors);
        //	e.printStackTrace(pw);

        //	pw.flush();
        //	pw.close();

        //	errors.flush();
        //	errors.close();

        //	description = errors.toString();

        //	if (description.indexOf("品号")>0 && description.indexOf("库存出库")>0) {
        //		description = description.substring(description.indexOf("品号"), description.indexOf("库存出库") + 4);
        //	}

        //} catch (Exception ignored) {

        //}

        //throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, description);
        //}
    }

    protected String addDcpReceivingToData(DCP_StockOutProcessReq req, List<Map<String, Object>> stockOutList, String docType) throws Exception {

        Map<String, Object> sMap = stockOutList.get(0);
        String nowDateTime = DateFormatUtils.getNowDateTime();
        String eId = sMap.get("EID").toString();
        String organizationNO = sMap.get("ORGANIZATIONNO").toString();
        String stockOutNO = req.getRequest().getStockOutNo();

        String receiptDate = sMap.get("RECEIPTDATE").toString();
        String totCqty = sMap.get("TOT_CQTY").toString();
        String totPqty = sMap.get("TOT_PQTY").toString();
        String totAmt = sMap.get("TOT_AMT").toString();
        String totDistriAmt = sMap.get("TOT_DISTRIAMT").toString();
        String transferWarehouse = sMap.get("TRANSFER_WAREHOUSE").toString();
        String rDate = sMap.get("RDATE").toString();
        String bDate = sMap.get("BDATE").toString();
        String warehouse = sMap.get("WAREHOUSE").toString();
        String invWarehouse = sMap.get("INVWAREHOUSE").toString();
        String memo = sMap.get("MEMO").toString();
        String employeeId = sMap.get("EMPLOYEEID").toString();
        String departId = sMap.get("DEPARTID").toString();

        //生成通知单 再审核
        String batchSql = "select b.PLU_BARCODE,b.pluno,b.featureno,b.punit,a.pqty pqty,a.baseqty,b.price,b.distriprice,b.warehouse," +
                " b.baseunit,b.unit_ratio as unitratio,b.PLU_MEMO,a.batchno,b.bdate,a.PRODDATE,a.EXPDATE, nvl(c.pqty,0) as poqty,b.bsno  " +
                " ,a.ITEM,a.ITEM2 " +
                " from DCP_STOCKOUT_BATCH a " +
                " inner join DCP_STOCKOUT_DETAIL b on a.eid=b.eid and a.organizationno=b.organizationno and a.stockoutno=b.stockoutno  AND a.item2=b.item " +
                " left join DCP_TRANSAPPLY_DETAIL c on a.eid=c.eid and b.oofno=c.billno and b.ooitem=c.item " +
                " where a.eid='" + eId + "' " +
                " and a.organizationno='" + organizationNO + "' " +
                " and a.stockoutno='" + stockOutNO + "' ";
        List<Map<String, Object>> batchList = doQueryData(batchSql, null);
        List<Map<String, String>> filterRows = batchList.stream().map(x -> {
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("pluNo", x.get("PLUNO").toString());
            hashMap.put("featureNo", x.get("FEATURENO").toString());
            hashMap.put("pUnit", x.get("PUNIT").toString());
            hashMap.put("batchNo", x.get("BATCHNO").toString());
            return hashMap;
        }).distinct().collect(Collectors.toList());

        String receivingNo = this.getOrderNO(req, "YCSH");

        int detailItem = 0;

        ColumnDataValue dcp_receiving = new ColumnDataValue();
        //枚举0-配送收货 1-调拨收货 2-统采直供 3-采购收货 4-采购入库 5-退配收货 6-销退收货 7移仓收货
        dcp_receiving.add("EID", DataValues.newString(eId));
        dcp_receiving.add("ORGANIZATIONNO", DataValues.newString(organizationNO));
        dcp_receiving.add("SHOPID", DataValues.newString(organizationNO));
        dcp_receiving.add("RECEIVINGNO", DataValues.newString(receivingNo));
        dcp_receiving.add("BDATE", DataValues.newString(DateFormatUtils.getPlainDate(nowDateTime)));
        dcp_receiving.add("DOC_TYPE", DataValues.newString("7"));
        dcp_receiving.add("STATUS", DataValues.newInteger(0));
        dcp_receiving.add("RECEIPTDATE", DataValues.newString(DateFormatUtils.getPlainDate(receiptDate)));
        dcp_receiving.add("CREATEBY", DataValues.newString(req.getEmployeeNo()));
        dcp_receiving.add("CREATE_DATE", DataValues.newString(DateFormatUtils.getPlainDate(nowDateTime)));
        dcp_receiving.add("CREATE_TIME", DataValues.newString(StringUtils.substring(DateFormatUtils.getPlainDatetime(nowDateTime), 9)));
        dcp_receiving.add("MEMO", DataValues.newString(memo));
        dcp_receiving.add("WAREHOUSE", DataValues.newString(transferWarehouse));
        dcp_receiving.add("INVWAREHOUSE", DataValues.newString(invWarehouse));
        dcp_receiving.add("TRANSFER_WAREHOUSE", DataValues.newString(warehouse));
        dcp_receiving.add("TRANSFER_SHOP", DataValues.newString(organizationNO));
        dcp_receiving.add("PARTITION_DATE", DataValues.newString(bDate));
        dcp_receiving.add("RDATE", DataValues.newString(rDate));
        dcp_receiving.add("MODIFY_DATE", DataValues.newString(DateFormatUtils.getPlainDate(nowDateTime)));
        dcp_receiving.add("MODIFY_TIME", DataValues.newString(StringUtils.substring(DateFormatUtils.getPlainDatetime(nowDateTime), 9)));
        dcp_receiving.add("RECEIPTORGNO", DataValues.newString(organizationNO));
        dcp_receiving.add("OWNOPID", DataValues.newString(employeeId));
        dcp_receiving.add("OWNDEPTID", DataValues.newString(departId));
        dcp_receiving.add("EMPLOYEEID", DataValues.newString(employeeId));
        dcp_receiving.add("DEPARTID", DataValues.newString(departId));
        dcp_receiving.add("OTYPE", DataValues.newString("4"));
        dcp_receiving.add("OFNO", DataValues.newString(stockOutNO));
        dcp_receiving.add("LOAD_DOCNO", DataValues.newString(stockOutNO));
        dcp_receiving.add("TOT_PQTY", DataValues.newDecimal(totPqty));
        dcp_receiving.add("TOT_AMT", DataValues.newDecimal(totAmt));
        dcp_receiving.add("TOT_CQTY", DataValues.newDecimal(totCqty));
        dcp_receiving.add("TOT_DISTRIAMT", DataValues.newDecimal(totDistriAmt));

        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_receiving", dcp_receiving)));

        int receivingItem = 0;
        for (Map<String, String> map : filterRows) {
            String pluNo = map.get("pluNo");
            String featureNo = map.get("featureNo");
            String pUnit = map.get("pUnit");
            String batchNo = map.get("batchNo");
            receivingItem++;
            List<Map<String, Object>> filterBatch = batchList.stream().filter(x -> x.get("PLUNO").toString().equals(pluNo)
                    && x.get("FEATURENO").toString().equals(featureNo) &&
                    x.get("PUNIT").toString().equals(pUnit) && x.get("BATCHNO").toString().equals(batchNo)).collect(Collectors.toList());

            BigDecimal sumPqty = new BigDecimal(0);
            BigDecimal sumBaseQty = new BigDecimal(0);
            for (Map<String, Object> batch : filterBatch) {
                sumPqty = sumPqty.add(new BigDecimal(batch.get("PQTY").toString()));
                sumBaseQty = sumBaseQty.add(new BigDecimal(batch.get("BASEQTY").toString()));

            }
            Map<String, Object> singleBatch = filterBatch.get(0);
            String pluBarcode = singleBatch.get("PLU_BARCODE").toString();
            String bsno = singleBatch.get("BSNO").toString();
            String poQty = singleBatch.get("POQTY").toString();
            BigDecimal price = new BigDecimal(singleBatch.get("PRICE").toString());
            BigDecimal distriPrice = new BigDecimal(singleBatch.get("DISTRIPRICE").toString());
            BigDecimal amt = price.multiply(sumPqty).setScale(2, RoundingMode.HALF_UP);
            BigDecimal distriAmt = distriPrice.multiply(sumPqty).setScale(2, RoundingMode.HALF_UP);
            String detailWarehouse = singleBatch.get("WAREHOUSE").toString();
            String baseUnit = singleBatch.get("BASEUNIT").toString();
            String plu_memo = singleBatch.get("PLU_MEMO").toString();
            String unitRatio = singleBatch.get("UNITRATIO").toString();
            String item = singleBatch.get("ITEM").toString();
            String item2 = singleBatch.get("ITEM2").toString();
            String prodDate = singleBatch.get("PRODDATE").toString();
            String expDate = singleBatch.get("EXPDATE").toString();


            ColumnDataValue dcp_receiving_detail = new ColumnDataValue();
            dcp_receiving_detail.add("EID", DataValues.newString(eId));
            dcp_receiving_detail.add("RECEIVINGNO", DataValues.newString(receivingNo));
            dcp_receiving_detail.add("SHOPID", DataValues.newString(organizationNO));
            dcp_receiving_detail.add("ORGANIZATIONNO", DataValues.newString(organizationNO));

            dcp_receiving_detail.add("ITEM", DataValues.newInteger(receivingItem));

            dcp_receiving_detail.add("OTYPE", DataValues.newString("4"));
            dcp_receiving_detail.add("OFNO", DataValues.newString(stockOutNO));
            dcp_receiving_detail.add("OITEM", DataValues.newInteger(item));
            dcp_receiving_detail.add("OITEM2", DataValues.newInteger(item2));
            dcp_receiving_detail.add("PLUNO", DataValues.newString(pluNo));
            dcp_receiving_detail.add("PUNIT", DataValues.newString(pUnit));
            dcp_receiving_detail.add("PQTY", DataValues.newDecimal(sumPqty));
            dcp_receiving_detail.add("STOCKIN_QTY", DataValues.newDecimal(0));
            dcp_receiving_detail.add("BASEUNIT", DataValues.newString(baseUnit));
            dcp_receiving_detail.add("BASEQTY", DataValues.newDecimal(sumBaseQty));

            dcp_receiving_detail.add("UNIT_RATIO", DataValues.newDecimal(unitRatio));
            dcp_receiving_detail.add("PRICE", DataValues.newString(price));
            dcp_receiving_detail.add("AMT", DataValues.newString(amt));

            dcp_receiving_detail.add("POQTY", DataValues.newString(poQty));
            dcp_receiving_detail.add("PROC_RATE", DataValues.newDecimal(0));
            dcp_receiving_detail.add("WAREHOUSE", DataValues.newString(detailWarehouse));
            dcp_receiving_detail.add("PLU_MEMO", DataValues.newString(plu_memo));
            dcp_receiving_detail.add("BATCH_NO", DataValues.newString(batchNo));
            dcp_receiving_detail.add("PROD_DATE", DataValues.newString(prodDate));
            dcp_receiving_detail.add("EXPDATE", DataValues.newString(expDate));

            dcp_receiving_detail.add("DISTRIPRICE", DataValues.newDecimal(distriPrice));
            dcp_receiving_detail.add("DISTRIAMT", DataValues.newDecimal(distriAmt));
            dcp_receiving_detail.add("BDATE", DataValues.newString(bDate));
            dcp_receiving_detail.add("FEATURENO", DataValues.newString(featureNo));
            dcp_receiving_detail.add("PACKINGNO", DataValues.newString(""));
            dcp_receiving_detail.add("STATUS", DataValues.newInteger(0));

            dcp_receiving_detail.add("PLU_BARCODE", DataValues.newString(pluBarcode));
            dcp_receiving_detail.add("BSNO", DataValues.newString(bsno));


            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_receiving_detail", dcp_receiving_detail)));

        }

        return receivingNo;
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_StockOutProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_StockOutProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_StockOutProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected String getQuerySql(DCP_StockOutProcessReq req) throws Exception {
        return null;
    }


    private String getQuerySql2(DCP_StockOutProcessReq req) throws Exception {
        String sqlbuf = "SELECT TRANSFERSHOP,MEMO,DOCType,CreateBy,CreateDate,Createtime,TOTPQTY,TOTAMT,TOTCQTY,"
                + "STOCKOUTNO,OrganizationNO,EID,TRANSFER_WAREHOUSE,WAREHOUSE,BDATE,TOT_DISTRIAMT,DELIVERYBY,receipt_org,employeeid,departid,invwarehouse "
                + " from (  "
                + " SELECT TRANSFER_SHOP as TRANSFERSHOP, MEMO, DOC_Type as DOCType, CreateBy, Create_Date as CreateDate, "
                + " Create_time as Createtime, TOT_PQTY as TOTPQTY, TOT_AMT as TOTAMT, TOT_CQTY as TOTCQTY, STOCKOUTNO, "
                + " OrganizationNO, EID, TRANSFER_WAREHOUSE, WAREHOUSE, BDATE,TOT_DISTRIAMT,DELIVERYBY,receipt_org,employeeid,departid,invwarehouse FROM DCP_STOCKOUT A "
                + " WHERE  A.OrganizationNO = ? AND A.EID = ? AND A.stockOutNO = ? " +
                " ) TBL ";
        return sqlbuf;
    }

    private String getQuerySql3(DCP_StockOutProcessReq req) throws Exception {
        String sqlbuf = "SELECT ITEM,OITEM,ITEM2,PLUNO,PUNIT,PQTY,BASEUNIT,BASEQTY,UNITRATIO,PRICE,AMT,WAREHOUSE,STOCKOUTNO,"
                + " pluMemo,BATCH_NO,PROD_DATE,DISTRIPRICE,DISTRIAMT,featureno,expdate,bsno "
                + " from ("
                + " SELECT a.PRICE,a.AMT,a.OITEM,b.*,"
                + " a.PLU_MEMO as pluMemo,b.BATCHNO BATCH_NO,b.PRODDATE PROD_DATE,a.DISTRIPRICE,a.DISTRIAMT,a.bsno "
                + " FROM DCP_STOCKOUT_DETAIL A "
                + " INNER JOIN DCP_STOCKOUT_BATCH B on a.eid=b.eid and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SHOPID=b.SHOPID and a.STOCKOUTNO=b.STOCKOUTNO and a.ITEM=b.ITEM2 "
                + " WHERE A.OrganizationNO = ? AND A.EID = ? AND A.stockOutNO = ? " +
                " ) TBL ";
        return sqlbuf;
    }


    private String getDCP_StockOut_Sql(DCP_StockOutProcessReq req, String eId, String shopNo, String stockOutNo, String langType) throws Exception {
        String sql = " select  a.stockoutno," +
                "       a.bdate," +
                "       doc_type," +
                "       b.otype," +
                "       b.ofno," +
                "       delivery_no," +
                "       memo," +
                "       createby," +
                "       create_date," +
                "       create_time," +
                "       accountby," +
                "       account_date," +
                "       account_time," +
                "       ob.item," +
                "       oitem," +
                "       b.pluno," +
                "       b.featureno," +
                "       ob.punit," +
                "       ob.pqty," +
                "       ob.baseunit," +
                "       unit_ratio," +
                "       ob.baseqty," +
                "       b.price," +
                "       amt," +
                "       batch_no," +
                "       b.prod_date," +
                "       b.distriprice," +
                "       transfer_shop," +
                "       load_doctype," +
                "       load_docno," +
                "       b.warehouse," +
                "       transfer_warehouse," +
                "       c.reason_name," +
                "       d.plu_name," +
                "       e.uname," +
                "       a.status," +
                "       fn.featurename," +
                "       B.distriAMT," +
                "       b.MES_LOCATION "
                + "  from dcp_stockout a "
                + " inner join dcp_stockout_detail b on a.stockoutno=b.stockoutno and a.eid=b.eid and a.organizationno=b.organizationno "
                + " inner join dcp_stockout_batch ob on b.stockoutno=ob.stockoutno and ob.eid=b.eid and ob.organizationno=b.organizationno and ob.ITEM2=b.ITEM "
                + " left join dcp_reason_lang c on a.eid=c.eid and a.bsno=c.bsno and c.bstype='4' and c.lang_type='" + langType + "' "
                + " left join dcp_goods_lang d on a.eid=d.eid and b.pluno=d.pluno and d.lang_type='" + langType + "' "
                + " left join dcp_unit_lang e on a.eid = e.eid and b.punit=e.unit and e.lang_type='" + langType + "' "
                + " left join DCP_GOODS_FEATURE_LANG fn on a.eid=fn.eid and b.pluno=fn.pluno and b.featureno=fn.featureno  and fn.lang_type='" + langType + "'"
                + " where a.eid='" + eId + "' and a.organizationno='" + shopNo + "' and a.stockoutno='" + stockOutNo + "' "
                + " ";

        return sql;
    }

    private String getStockInNO(DCP_StockOutProcessReq req) throws Exception {
        /*
         * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
         * 注意固定编码：如果doctype=0 则固定编码PSSH  如果doctype=1 则固定码为DBSH
         */
        levelElm request = req.getRequest();
        String stockInNo = null;
        String shopId = req.getShopId();
        String organizationNO = req.getOrganizationNO();
        String eId = req.geteId();
        String docType = request.getDocType();
        String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
        String[] conditionValues = {organizationNO, eId, shopId, docType}; // 查询要货单号
        StringBuffer sqlbuf = new StringBuffer();

        if (docType.equals("4")) {
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
                if (docType.equals("4")) {
                    stockInNo = "YCSH" + stockInNo;
                }
            } else {
                if (docType.equals("4")) {
                    stockInNo = "YCSH" + bDate + "00001";
                }
            }
        } else {
            if (docType.equals("4")) {
                stockInNo = "YCSH" + bDate + "00001";
            }
        }
        return stockInNo;
    }

}
