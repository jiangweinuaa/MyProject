package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_InterSettleDataProcessReq;
import com.dsc.spos.json.cust.req.DCP_SStockInProcessReq;
import com.dsc.spos.json.cust.req.DCP_SStockInProcessReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_InterSettleDataProcessRes;
import com.dsc.spos.json.cust.res.DCP_SStockInProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.utils.ServiceAgentUtils;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.bc.BcReq;
import com.dsc.spos.utils.bc.BcRes;
import com.dsc.spos.utils.bc.MesBatchInfo;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 服務函數：SStockInProcess
 * 說明：自采入库处理
 * 服务说明：自采入库处理
 *
 * @author jinzma
 * @since 2018-11-21
 */
public class DCP_SStockInProcess extends SPosAdvanceService<DCP_SStockInProcessReq, DCP_SStockInProcessRes> {

    @Override
    protected boolean isVerifyFail(DCP_SStockInProcessReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer();
        levelElm request = req.getRequest();

        if (Check.Null(request.getSStockInNo())) {
            errMsg.append("自采入库单号不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected void processDUID(DCP_SStockInProcessReq req, DCP_SStockInProcessRes res) throws Exception {

        String eId = req.geteId();
        String shopId = req.getShopId();
        String organizationNo = req.getOrganizationNO();
        String opNo = req.getOpNO();
        String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String sTime = new SimpleDateFormat("HHmmss").format(new Date());
        String accountDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

        levelElm request = req.getRequest();
        String sStockInNO = request.getSStockInNo();
        String status = request.getStatus();

        String corp = "";
        String bizCorp = "";
        String supplier = "";
        String bizIcCostWarehouse = "";


        if (Check.NotNull(req.getRequest().getAccountDate())) {
            accountDate = req.getRequest().getAccountDate();
        }

        //查单头和单身
        String sql = this.GetDCP_SSTOCKIN_SQL(req, sStockInNO);
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);
        if (getQData == null || getQData.isEmpty()) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "未查询到可操作单据");
        } else {
            if (!"0".equals(getQData.get(0).get("STATUS").toString())) {
                String errorMsg = "";
                if ("3".equals(status)) { //作废
                    errorMsg = "不可作废！";
                }
                if ("1".equals(status)) { //作废
                    errorMsg = "不可审核！";
                }
                if (StringUtils.isNotEmpty(errorMsg)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非0" + errorMsg);
                }

            }
            if (!"1".equals(getQData.get(0).get("STATUS").toString())) {
                String errorMsg = "";
                if ("0".equals(status)) { //作废
                    errorMsg = "不可反审！";
                }
                if (StringUtils.isNotEmpty(errorMsg)) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据状态非0" + errorMsg);
                }

            }

            shopId = getQData.get(0).get("SHOPID").toString();
            organizationNo = getQData.get(0).get("ORGANIZATIONNO").toString();
            corp = getQData.get(0).get("CORP").toString();
            bizCorp = getQData.get(0).get("BIZCORP").toString();
            supplier = getQData.get(0).get("SUPPLIER").toString();
            bizIcCostWarehouse = getQData.get(0).get("BIZICCOSTWAREHOUSE").toString();
        }
        String stockInType = String.valueOf(getQData.get(0).get("STOCKINTYPE"));

        List<Map<String, Object>> interSettleDataList = new ArrayList<>();
        if (!corp.equals(bizCorp)) {
            String interSql = "select * from DCP_INTERSETTLE_DETAIL a" +
                    " where a.eid='" + eId + "' and a.organizationno='" + organizationNo + "' and a.billno='" + sStockInNO + "' ";
            interSettleDataList = this.doQueryData(interSql, null);
        }

        ColumnDataValue condition = new ColumnDataValue();

        condition.add("EID", DataValues.newString(eId));
        condition.add("SHOPID", DataValues.newString(shopId));
        condition.add("SSTOCKINNO", DataValues.newString(sStockInNO));
        condition.add("ORGANIZATIONNO", DataValues.newString(organizationNo));


        //没有审核 直接过账 status 传的2   原来1是审核
        if ("3".equals(status)) { //作废
            ColumnDataValue dcp_sstockin = new ColumnDataValue();

            dcp_sstockin.add("STATUS", DataValues.newString("3"));
            dcp_sstockin.add("MODIFYBY", DataValues.newString(opNo));
            dcp_sstockin.add("MODIFY_DATE", DataValues.newString(sDate));
            dcp_sstockin.add("MODIFY_TIME", DataValues.newString(sTime));
            dcp_sstockin.add("UPDATE_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
            dcp_sstockin.add("CANCELBY", new DataValue(opNo, Types.VARCHAR));
            dcp_sstockin.add("CANCEL_DATE", new DataValue(sDate, Types.VARCHAR));
            dcp_sstockin.add("CANCEL_TIME", new DataValue(sTime, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SSTOCKIN", condition, dcp_sstockin)));

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }
        else if ("2".equals(status)) { //审核

            ColumnDataValue dcp_sstockin = new ColumnDataValue();

            dcp_sstockin.add("STATUS", DataValues.newString("1"));
            dcp_sstockin.add("MODIFYBY", DataValues.newString(opNo));
            dcp_sstockin.add("MODIFY_DATE", DataValues.newString(sDate));
            dcp_sstockin.add("MODIFY_TIME", DataValues.newString(sTime));
            dcp_sstockin.add("UPDATE_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
            dcp_sstockin.add("CONFIRMBY", new DataValue(opNo, Types.VARCHAR));
            dcp_sstockin.add("CONFIRM_DATE", new DataValue(sDate, Types.VARCHAR));
            dcp_sstockin.add("CONFIRM_TIME", new DataValue(sTime, Types.VARCHAR));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SSTOCKIN", condition, dcp_sstockin)));

            // String stockInType = String.valueOf(getQData.get(0).get("STOCKINTYPE"));
//          if ("2".equals(stockInType)) { //采购收货入库、
            //查询收货通知单

//          sql = getUpdateDCP_RECEIVING_DETAILSQL(req);
//          ExecBean eb = new ExecBean(sql);
//          this.addProcessData(new DataProcessBean(eb));
//
//
//          sql = getUpdateDCP_PURORDER_DELIVERYSQL(req);
//          ExecBean eb1 = new ExecBean(sql);
//          this.addProcessData(new DataProcessBean(eb1));

            List<Map<String, Object>> receivingDetailData = doQueryData(getQueryDCP_ReceivingDetailSql(req), null);
            List<Map<String, Object>> purOrderDeliveryData = doQueryData(getQueryDCP_PurOrder_DeliverySql(req), null);

            for (Map<String, Object> oneData : getQData) {
                //更新收货通知单身的已收数量和状态
                double qty = Double.parseDouble(oneData.get("PQTY").toString());
                if ("2".equals(stockInType)) {
                    for (Map<String, Object> receivingData : receivingDetailData) {

                        String receivingNo = receivingData.get("RECEIVINGNO").toString();
                        String item = receivingData.get("ITEM").toString();
                        if (Check.NotNull(receivingNo) && Check.NotNull(item)) {
                            double sum = getQData.stream().filter(x -> receivingNo.equals(x.get("RECEIVINGNO").toString())
                                            && item.equals(x.get("RECEIVINGITEM").toString())
                                    ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                    .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();


                            //qty  是单笔的  得汇总

                            double stockInQty = Double.parseDouble(receivingData.get("STOCKIN_QTY").toString());
                            double receivingQty = Double.parseDouble(receivingData.get("PQTY").toString());
                            ColumnDataValue dcp_receiving_detail = new ColumnDataValue();
                            ColumnDataValue receivingCondition = new ColumnDataValue();

                            receivingCondition.add("EID", DataValues.newString(eId));
                            receivingCondition.add("RECEIVINGNO", DataValues.newString(receivingData.get("RECEIVINGNO").toString()));
                            receivingCondition.add("ITEM", DataValues.newInteger(receivingData.get("ITEM")));


                            dcp_receiving_detail.add("STOCKIN_QTY", DataValues.newString(stockInQty + sum));
                            if (stockInQty + sum >= receivingQty) {
                                dcp_receiving_detail.add("STATUS", DataValues.newString(100));
                                receivingData.put("STATUS", 100);//修改查询出的信息值
                            }

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_receiving_detail", receivingCondition, dcp_receiving_detail)));

                        }


                    }
                }
                if ("2".equals(stockInType)) {
                    //更新采购交期明细对应的已收货量
                    for (Map<String, Object> deliveryData : purOrderDeliveryData) {
                        String purorderNo = deliveryData.get("PURORDERNO").toString().toString();
                        String deliveryItem = deliveryData.get("ITEM").toString();
                        String deliveryItem2 = deliveryData.get("ITEM2").toString();
                        String receivingNo = deliveryData.get("RECEIVINGNO").toString();
                        String receivingItem = deliveryData.get("RECEIVINGITEM").toString();

                        if (Check.NotNull(purorderNo) && Check.NotNull(deliveryItem)
                                && Check.NotNull(deliveryItem2) && Check.NotNull(receivingNo) && Check.NotNull(receivingItem)
                        ) {
                            double sum = getQData.stream().filter(x -> receivingNo.equals(x.get("RECEIVINGNO").toString())
                                            && receivingItem.equals(x.get("RECEIVINGITEM").toString())
                                    ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                    .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();
                            double receiveQty = Double.parseDouble(deliveryData.get("RECEIVEQTY").toString());
                            ColumnDataValue dcp_purOrder_delivery = new ColumnDataValue();
                            ColumnDataValue deliveryCondition = new ColumnDataValue();

                            deliveryCondition.add("EID", DataValues.newString(eId));
                            deliveryCondition.add("PURORDERNO", DataValues.newString(deliveryData.get("PURORDERNO").toString()));
                            deliveryCondition.add("ITEM", DataValues.newString(deliveryData.get("ITEM").toString()));
                            deliveryCondition.add("ITEM2", DataValues.newString(deliveryData.get("ITEM2").toString()));

                            dcp_purOrder_delivery.add("RECEIVEQTY", DataValues.newDecimal(sum + receiveQty));

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER_DELIVERY", deliveryCondition, dcp_purOrder_delivery)));
                        }

                    }
                }
            }

            String nowStatus = "100";
            String receivingNo = "";
            for (Map<String, Object> receivingData : receivingDetailData) {
                if (!nowStatus.equals(receivingData.get("STATUS").toString())) {
                    nowStatus = receivingData.get("STATUS").toString();
                    break;
                }
            }

            if (receivingDetailData.size() > 0) {
                receivingNo = receivingDetailData.get(0).get("RECEIVINGNO").toString();
                //一笔入库单只有一笔收货单的情况才行
                if ("100".equals(nowStatus)) { //这个没有用被更改则表示全部为100
                    ColumnDataValue dcp_receiving = new ColumnDataValue();
                    ColumnDataValue receivingCondition = new ColumnDataValue();

                    receivingCondition.add("EID", DataValues.newString(eId));
                    receivingCondition.add("RECEIVINGNO", DataValues.newString(receivingNo));

                    dcp_receiving.add("STATUS", DataValues.newString(7));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", receivingCondition, dcp_receiving)));
                } else {
                    if ("2".equals(stockInType)) {
                        //一段式采购收货入库 新增会把通知单状态改成收货中  这时候状态没全部结束要把状态改回去
                        ColumnDataValue dcp_receiving = new ColumnDataValue();
                        ColumnDataValue receivingCondition = new ColumnDataValue();

                        receivingCondition.add("EID", DataValues.newString(eId));
                        receivingCondition.add("RECEIVINGNO", DataValues.newString(receivingNo));

                        dcp_receiving.add("STATUS", DataValues.newString(6));

                        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", receivingCondition, dcp_receiving)));

                    }
                }


            }

//       }

            //this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        }
        else if ("0".equals(status)) {
            //取消审核
//                （针对stockInType=2.采购收货入库和4.销退入库单据可执行）增加【取消审核】处理逻辑，status增加入参枚举“0新建”，仅状态STATUS=“1待过账”可执行
//                处理逻辑：
//                1.回滚收货通知单【已收货量】：
//                DCP_SSTOCKIN_DETAIL.OTYPE=2.收货通知，更新「收货通知单」：DCP_RECEIVING_DETAIL.STOCKINQTY=STOCKINQTY-本次收货量；(按DCP_SSTOCKIN_DETAIL.OFNO+OITEM汇总数量后回写）
//                DCP_RECEIVING_DETAIL.OTYPE=2.采购订单,更新「采购订单」DCP_PURORDER_DELIVERY.RECEIVEQTY=RECEIVEQTY-本次收货量；（按DCP_RECEIVING_DETAIL.OFNO+OITEM关联）
//
//                2.回滚更新收货通知【状态】
//                更新行状态DCP_RECEIVING_DETAIL.STATUS=“0待收货”
//                更新单据状态DCP_RECEIVING.STATUS=“1收货中”
//
//                3.更新资料异动字段：【审核人】、【审核日期】、【审核时间】清空值，更新【修改人】、【修改日期】、【修改时间】

            ColumnDataValue dcp_sstockin = new ColumnDataValue();

            dcp_sstockin.add("STATUS", DataValues.newString("0"));
            dcp_sstockin.add("MODIFYBY", DataValues.newString(opNo));
            dcp_sstockin.add("MODIFY_DATE", DataValues.newString(sDate));
            dcp_sstockin.add("MODIFY_TIME", DataValues.newString(sTime));
            dcp_sstockin.add("UPDATE_TIME", DataValues.newString(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime())));
            dcp_sstockin.add("CONFIRMBY", DataValues.newString(""));
            dcp_sstockin.add("CONFIRM_DATE", DataValues.newString(""));
            dcp_sstockin.add("CONFIRM_TIME", DataValues.newString(""));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SSTOCKIN", condition, dcp_sstockin)));


            List<Map<String, Object>> receivingDetailData = doQueryData(getQueryDCP_ReceivingDetailSql(req), null);
            List<Map<String, Object>> purOrderDeliveryData = doQueryData(getQueryDCP_PurOrder_DeliverySql(req), null);

            for (Map<String, Object> oneData : getQData) {
                //更新收货通知单身的已收数量和状态
                double qty = Double.parseDouble(oneData.get("PQTY").toString());

                if ("2".equals(stockInType)) {
                    for (Map<String, Object> receivingData : receivingDetailData) {
                        String receivingNo = receivingData.get("RECEIVINGNO").toString();
                        String item = receivingData.get("ITEM").toString();
                        if (Check.NotNull(receivingNo) && Check.NotNull(item)) {
                            double sum = getQData.stream().filter(x -> receivingNo.equals(x.get("RECEIVINGNO").toString())
                                            && item.equals(x.get("RECEIVINGITEM").toString())
                                    ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                    .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();


                            //qty  是单笔的  得汇总

                            double stockInQty = Double.parseDouble(receivingData.get("STOCKIN_QTY").toString());
                            double receivingQty = Double.parseDouble(receivingData.get("PQTY").toString());
                            ColumnDataValue dcp_receiving_detail = new ColumnDataValue();
                            ColumnDataValue receivingCondition = new ColumnDataValue();

                            receivingCondition.add("EID", DataValues.newString(eId));
                            receivingCondition.add("RECEIVINGNO", DataValues.newString(receivingData.get("RECEIVINGNO").toString()));
                            receivingCondition.add("ITEM", DataValues.newInteger(receivingData.get("ITEM")));


                            dcp_receiving_detail.add("STOCKIN_QTY", DataValues.newString(stockInQty - sum));
                            if (stockInQty - sum >= receivingQty) {
                                dcp_receiving_detail.add("STATUS", DataValues.newString(100));
                                receivingData.put("STATUS", 100);//修改查询出的信息值
                            }

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_receiving_detail", receivingCondition, dcp_receiving_detail)));

                        }

                    }
                }

                //更新采购交期明细对应的已收货量 没收货单才更新 一段式
                if ("2".equals(stockInType)) {
                    for (Map<String, Object> deliveryData : purOrderDeliveryData) {

                        String purorderNo = deliveryData.get("PURORDERNO").toString().toString();
                        String deliveryItem = deliveryData.get("ITEM").toString();
                        String deliveryItem2 = deliveryData.get("ITEM2").toString();
                        String receivingNo = deliveryData.get("RECEIVINGNO").toString();
                        String receivingItem = deliveryData.get("RECEIVINGITEM").toString();

                        if (Check.NotNull(purorderNo) && Check.NotNull(deliveryItem)
                                && Check.NotNull(deliveryItem2) && Check.NotNull(receivingNo) && Check.NotNull(receivingItem)
                        ) {
                            double sum = getQData.stream().filter(x -> receivingNo.equals(x.get("RECEIVINGNO").toString())
                                            && receivingItem.equals(x.get("RECEIVINGITEM").toString())
                                    ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                    .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();
                            double receiveQty = Double.parseDouble(deliveryData.get("RECEIVEQTY").toString());
                            ColumnDataValue dcp_purOrder_delivery = new ColumnDataValue();
                            ColumnDataValue deliveryCondition = new ColumnDataValue();

                            deliveryCondition.add("EID", DataValues.newString(eId));
                            deliveryCondition.add("PURORDERNO", DataValues.newString(deliveryData.get("PURORDERNO").toString()));
                            deliveryCondition.add("ITEM", DataValues.newString(deliveryData.get("ITEM").toString()));
                            deliveryCondition.add("ITEM2", DataValues.newString(deliveryData.get("ITEM2").toString()));

                            dcp_purOrder_delivery.add("RECEIVEQTY", DataValues.newDecimal(sum + receiveQty));

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER_DELIVERY", deliveryCondition, dcp_purOrder_delivery)));
                        }

                    }
                }
            }

            String nowStatus = "0";
            for (Map<String, Object> receivingData : receivingDetailData) {
                if (!nowStatus.equals(receivingData.get("STATUS").toString())) {
                    nowStatus = receivingData.get("STATUS").toString();
                    break;
                }
            }
            //一笔入库单只有一笔收货单的情况才行
            if ("0".equals(nowStatus)) { //这个没有用被更改则表示全部为100
                ColumnDataValue dcp_receiving = new ColumnDataValue();
                ColumnDataValue receivingCondition = new ColumnDataValue();

                receivingCondition.add("EID", DataValues.newString(eId));
                receivingCondition.add("RECEIVINGNO", DataValues.newString(receivingDetailData.get(0).get("RECEIVINGNO")));

                dcp_receiving.add("STATUS", DataValues.newString(1));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", receivingCondition, dcp_receiving)));
            }
            //else{
            //    if("2".equals(stockInType)){
            //一段式采购收货入库 新增会把通知单状态改成收货中  这时候状态没全部结束要把状态改回去  改成收货中
            //      ColumnDataValue dcp_receiving = new ColumnDataValue();
            //      ColumnDataValue receivingCondition = new ColumnDataValue();

            //      receivingCondition.add("EID", DataValues.newString(eId));
            //      receivingCondition.add("RECEIVINGNO", DataValues.newString(receivingDetailData.get(0).get("RECEIVINGNO")));

            //     dcp_receiving.add("STATUS", DataValues.newString(1));

            //    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", receivingCondition, dcp_receiving)));
            // }
            //}

            this.doExecuteDataToDB();

            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        }


        //stockInType=2.采购收货入库，且单据状态=0.新建时，执行过账处理(目标状态=2已过帐），连续完成【审核】、【过账】两步处理逻辑；
        if ("2".equals(stockInType) && "1".equals(status)) {
            status = "2";//将状态改成2  去执行过账
        }
        //过账
        if ("2".equals(status)) {//过账

            //过帐前检查DCP_TRANSACTION 税


            Map<String, Object> stockChangeVerifyMsg = PosPub.getStockChangeVerifyMsg(dao, req.geteId(), req.getOrganizationNO(), accountDate);
            if ("N".equals(stockChangeVerifyMsg.get("check").toString())) {
                throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, stockChangeVerifyMsg.get("errorMsg").toString());
            }

            //更新单据状态=“2.已过帐”
            //查库存流水避免重复提交
            sql = "select * from DCP_stock_detail where EID='" + req.geteId() + "'"
                    + " and billno='" + sStockInNO + "' and organizationno='" + req.getOrganizationNO() + "'";
            List<Map<String, Object>> getDCP_SockDetail = this.doQueryData(sql, null);
            if (getDCP_SockDetail != null && !getDCP_SockDetail.isEmpty()) {
                throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "不可重复扣账！");
            }

            //更新单头
            UptBean ub1 = new UptBean("DCP_SSTOCKIN");
            //add Value
            ub1.addUpdateValue("status", new DataValue("2", Types.VARCHAR));
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
            ub1.addUpdateValue("MODIFYBY", new DataValue(opNo, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_DATE", new DataValue(sDate, Types.VARCHAR));
            ub1.addUpdateValue("MODIFY_TIME", new DataValue(sTime, Types.VARCHAR));
            ub1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("ACCOUNTDATETIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

            //condition
            ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("sStockInNO", new DataValue(sStockInNO, Types.VARCHAR));
            ub1.addCondition("organizationNO", new DataValue(shopId, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));

            //String stockInType = String.valueOf(getQData.get(0).get("STOCKINTYPE"));
            //1 是有purreceiving   2 是receiving 直接入库
            if ("1".equals(stockInType)) { //采购入库  二段式

                List<Map<String, Object>> purReceiveDetailData = doQueryData(getQueryDcp_PurReceive_detailSql(req), null);
                List<Map<String, Object>> purOrderDeliveryData = doQueryData(getQueryDCP_PurOrder_Delivery2Sql(req), null);
                for (Map<String, Object> oneData : getQData) {
                    //更新来源收货单收货明细【已入库量】
                    double qty = Double.parseDouble(oneData.get("PQTY").toString());
                    Boolean isStockinFull = true;
                    String purreceiveNo = "";//一张收货单 一个入库
                    for (Map<String, Object> purReceivingData : purReceiveDetailData) {

                        purreceiveNo = purReceivingData.get("BILLNO").toString();
                        double stockInQty = Double.parseDouble(purReceivingData.get("STOCKINQTY").toString());
                        double passQty = Double.parseDouble(purReceivingData.get("PASSQTY").toString());
                        double receiveQty = Double.parseDouble(purReceivingData.get("PQTY").toString());
                        String qcStatus = purReceivingData.get("QCSTATUS").toString();
                        String purReceiveItem = purReceivingData.get("ITEM").toString();

                        String finalPurreceiveNo = purreceiveNo;

                        List<Map<String, Object>> filterRows = getQData.stream().filter(x -> finalPurreceiveNo.equals(x.get("OFNO").toString())
                                && purReceiveItem.equals(x.get("OITEM").toString())).collect(Collectors.toList());
                        if (filterRows.size() > 0) {
                            double sum = filterRows.stream().map(y -> new BigDecimal(y.get("PQTY").toString()))
                                    .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();

                            ColumnDataValue dcp_purReceive_detail = new ColumnDataValue();
                            ColumnDataValue purReceivingCondition = new ColumnDataValue();

                            purReceivingCondition.add("EID", DataValues.newString(eId));
                            purReceivingCondition.add("BILLNO", DataValues.newString(purreceiveNo));
                            purReceivingCondition.add("ITEM", DataValues.newString(purReceiveItem));

                            double nowStockInQty = stockInQty + sum;

                            dcp_purReceive_detail.add("STOCKINQTY", DataValues.newString(nowStockInQty));

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURRECEIVE_DETAIL", purReceivingCondition, dcp_purReceive_detail)));

                            //合格量>0的时候，入库量=合格量视为[已完成】;
                            // 合格量=0时，qcStatus=2检验完成视为[已完成】，否则视为[未完成】
                            if (passQty > 0 && passQty > nowStockInQty) {
                                isStockinFull = false;
                            }


                        } else {
                            //不在入库单的
                            if (passQty == 0) {
                                if (!qcStatus.equals("2")) {
                                    isStockinFull = false;
                                }

                            }
                        }

                    }
                    if (isStockinFull && Check.NotNull(purreceiveNo)) {
                        ColumnDataValue dcp_purReceive = new ColumnDataValue();
                        ColumnDataValue purReceivingCondition = new ColumnDataValue();

                        purReceivingCondition.add("EID", DataValues.newString(eId));
                        purReceivingCondition.add("BILLNO", DataValues.newString(purreceiveNo));

                        dcp_purReceive.add("STATUS", DataValues.newString("2"));

                        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURRECEIVE", purReceivingCondition, dcp_purReceive)));

                    }

                    //更新采购交期明细对应的已收货量
                    for (Map<String, Object> deliveryData : purOrderDeliveryData) {

                        //收货单的单据号
                        String receivingNo = deliveryData.get("RECEIVINGNO").toString();
                        String receivingItem = deliveryData.get("RECEIVINGITEM").toString();
                        String purOrderNo = deliveryData.get("PURORDERNO").toString();
                        String purOrderItem = deliveryData.get("ITEM").toString();
                        String purOrderItem2 = deliveryData.get("ITEM2").toString();


                        if (Check.NotNull(purOrderNo) && Check.NotNull(purOrderItem)
                                && Check.NotNull(purOrderItem2) && Check.NotNull(receivingNo) && Check.NotNull(receivingItem)
                        ) {
                            double sum = getQData.stream().filter(x -> receivingNo.equals(x.get("RECEIVINGNO").toString())
                                            && receivingItem.equals(x.get("RECEIVINGITEM").toString())
                                    ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                    .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();
                            //这个receivieQty  一段式不是最新的值 要加上sum 前面会更新 影响不到这边
                            double receiveQty = Double.parseDouble(deliveryData.get("RECEIVEQTY").toString());
                            double stockInQty = Double.parseDouble(deliveryData.get("STOCKINQTY").toString());
                            ColumnDataValue dcp_purOrder_delivery = new ColumnDataValue();
                            ColumnDataValue deliveryCondition = new ColumnDataValue();

                            deliveryCondition.add("EID", DataValues.newString(eId));
                            deliveryCondition.add("PURORDERNO", DataValues.newString(purOrderNo));
                            deliveryCondition.add("ITEM", DataValues.newString(purOrderItem));
                            deliveryCondition.add("ITEM2", DataValues.newString(purOrderItem2));

                            dcp_purOrder_delivery.add("STOCKINQTY", DataValues.newDecimal(sum + stockInQty));

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER_DELIVERY", deliveryCondition, dcp_purOrder_delivery)));
//                                DCP_PURORDER_DELIVERY按项次item汇总【采购量】<=【已入库量】，更新DCP_PURORDER_DETAIL.CLOSE_STATUS="1.已结束"；
//                                整单所有行CLOSE_STATUS=“1.已结束”，更新单据状态码DCP_PURORDER.STATUS="2.已结案"

                            if (sum + stockInQty > receiveQty) {
                                ColumnDataValue dcp_purOrder_detail = new ColumnDataValue();
                                ColumnDataValue detailCondition = new ColumnDataValue();

                                detailCondition.add("EID", DataValues.newString(eId));
                                detailCondition.add("PURORDERNO", DataValues.newString(deliveryData.get("PURORDERNO").toString()));
                                detailCondition.add("ITEM", DataValues.newString(deliveryData.get("ITEM2").toString()));

                                dcp_purOrder_detail.add("CLOSE_STATUS", DataValues.newInteger(1));

                                deliveryData.put("CLOSE_STATUS", 1);
                                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER_DETAIL", detailCondition, dcp_purOrder_detail)));

                            }
                        }

                    }

                }

                String nowCloseStatus = "1";
                for (Map<String, Object> deliveryData : purOrderDeliveryData) {
                    if (!nowCloseStatus.equals(deliveryData.get("CLOSE_STATUS").toString())) {
                        nowCloseStatus = deliveryData.get("CLOSE_STATUS").toString();
                        break;
                    }
                }
                //一笔入库单只有一笔收货单的情况才行
                if ("1".equals(nowCloseStatus) && purOrderDeliveryData.size() > 0) { //这个没有用被更改则表示全部为100
                    ColumnDataValue dcp_purOrder = new ColumnDataValue();
                    ColumnDataValue purOrderCondition = new ColumnDataValue();

                    purOrderCondition.add("EID", DataValues.newString(eId));
                    purOrderCondition.add("PURORDERNO", DataValues.newString(purOrderDeliveryData.get(0).get("PURORDERNO")));

                    dcp_purOrder.add("STATUS", DataValues.newString(2));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_purOrder", purOrderCondition, dcp_purOrder)));
                }


            }
            else if ("2".equals(stockInType)) {
                // 一段式没有收货单
                // ● 更新来源采购订单【已入库量】,字段：DCP_PURORDER_DELIVERY.STOCKINQTY
                //● 更新来源采购订单【结束码】、【单据状态】,字段：DCP_PURORDER_DETAIL.CLOSE_STATUS,DCP_PURORDER.STATUS,
                List<Map<String, Object>> purOrderDeliveryData = doQueryData(getQueryDCP_PurOrder_DeliverySql(req), null);
                for (Map<String, Object> oneData : getQData) {

                    //double qty = Double.parseDouble(oneData.get("PQTY").toString());
                    for (Map<String, Object> deliveryData : purOrderDeliveryData) {

                        //收货单的单据号
                        String receivingNo = deliveryData.get("RECEIVINGNO").toString();
                        String receivingItem = deliveryData.get("RECEIVINGITEM").toString();
                        String purOrderNo = deliveryData.get("PURORDERNO").toString();
                        String purOrderItem = deliveryData.get("ITEM").toString();
                        String purOrderItem2 = deliveryData.get("ITEM2").toString();


                        if (Check.NotNull(purOrderNo) && Check.NotNull(purOrderItem)
                                && Check.NotNull(purOrderItem2) && Check.NotNull(receivingNo) && Check.NotNull(receivingItem)
                        ) {
                            double sum = getQData.stream().filter(x -> receivingNo.equals(x.get("RECEIVINGNO").toString())
                                            && receivingItem.equals(x.get("RECEIVINGITEM").toString())
                                    ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                    .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();
                            double receiveQty = Double.parseDouble(deliveryData.get("RECEIVEQTY").toString());
                            double stockInQty = Double.parseDouble(deliveryData.get("STOCKINQTY").toString());
                            ColumnDataValue dcp_purOrder_delivery = new ColumnDataValue();
                            ColumnDataValue deliveryCondition = new ColumnDataValue();

                            deliveryCondition.add("EID", DataValues.newString(eId));
                            deliveryCondition.add("PURORDERNO", DataValues.newString(deliveryData.get("PURORDERNO").toString()));
                            deliveryCondition.add("ITEM", DataValues.newString(deliveryData.get("ITEM").toString()));
                            deliveryCondition.add("ITEM2", DataValues.newString(deliveryData.get("ITEM2").toString()));

                            double nowStockinQty = sum + stockInQty;

                            dcp_purOrder_delivery.add("STOCKINQTY", DataValues.newDecimal(nowStockinQty));
                            //dcp_purOrder_delivery.add("RECEIVEQTY",DataValues.newDecimal(nowStockinQty));

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER_DELIVERY", deliveryCondition, dcp_purOrder_delivery)));
//                                DCP_PURORDER_DELIVERY按项次item汇总【采购量】<=【已入库量】，更新DCP_PURORDER_DETAIL.CLOSE_STATUS="1.已结束"；
//                                整单所有行CLOSE_STATUS=“1.已结束”，更新单据状态码DCP_PURORDER.STATUS="2.已结案"

                            if (sum + stockInQty > receiveQty + sum) {
                                ColumnDataValue dcp_purOrder_detail = new ColumnDataValue();
                                ColumnDataValue detailCondition = new ColumnDataValue();

                                detailCondition.add("EID", DataValues.newString(eId));
                                detailCondition.add("PURORDERNO", DataValues.newString(deliveryData.get("PURORDERNO").toString()));
                                detailCondition.add("ITEM", DataValues.newString(deliveryData.get("ITEM2").toString()));

                                dcp_purOrder_detail.add("CLOSE_STATUS", DataValues.newInteger(1));

                                deliveryData.put("CLOSE_STATUS", 1);
                                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER_DETAIL", detailCondition, dcp_purOrder_detail)));

                            }
                        }


                    }

                }

                String nowCloseStatus = "1";
                for (Map<String, Object> deliveryData : purOrderDeliveryData) {
                    if (!nowCloseStatus.equals(deliveryData.get("CLOSE_STATUS").toString())) {
                        nowCloseStatus = deliveryData.get("CLOSE_STATUS").toString();
                        break;
                    }
                }
                //一笔入库单只有一笔收货单的情况才行
                if ("1".equals(nowCloseStatus)) { //这个没有用被更改则表示全部为100
                    ColumnDataValue dcp_purOrder = new ColumnDataValue();
                    ColumnDataValue purOrderCondition = new ColumnDataValue();

                    purOrderCondition.add("EID", DataValues.newString(eId));
                    purOrderCondition.add("PURORDERNO", DataValues.newString(purOrderDeliveryData.get(0).get("PURORDERNO")));

                    dcp_purOrder.add("STATUS", DataValues.newString(2));

                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER", purOrderCondition, dcp_purOrder)));
                }

            }

            // IF 参数是否启用批号==N ，库存流水的批号和日期字段不给值 BY JZMA 20191024
            String isBatchPara = PosPub.getPARA_SMS(dao, eId, req.getShopId(), "Is_BatchNO");
            if (Check.Null(isBatchPara) || !isBatchPara.equals("Y")) {
                isBatchPara = "N";
            }

            String isBatchParaBiz = PosPub.getPARA_SMS(dao, eId, bizCorp, "Is_BatchNO");
            if (Check.Null(isBatchParaBiz) || !isBatchParaBiz.equals("Y")) {
                isBatchParaBiz = "N";
            }


            String bizPartnerNo = "";
            String bType = "";
            String bizType = "";
            String setDirection = "1";
            String settleBType = "";
            List<MesBatchInfo> mesBatchInfoList = new ArrayList<>();
            for (Map<String, Object> oneData : getQData) {
                //增加进货允收管控
                String prodDate = oneData.get("PROD_DATE").toString();
                String batchNO = StringUtils.toString(oneData.get("BATCH_NO"), " ");
                String stockInAllowType = "0"; //oneData.get("STOCKINALLOWTYPE").toString();
                String shelfLife = oneData.get("SHELFLIFE").toString();
                String stockInValidDay = oneData.get("STOCKINVALIDDAY").toString();
                String isBatch = oneData.get("ISBATCH").toString();  //商品属性

                //增加批号集合
                MesBatchInfo mesBatchInfo = new MesBatchInfo();
                mesBatchInfo.setEId(eId);
                mesBatchInfo.setShopId(shopId);
                mesBatchInfo.setAddBatchNo(batchNO);
                mesBatchInfo.setFeatureNo(oneData.get("FEATURENO").toString());
                mesBatchInfo.setBillType("05");
                mesBatchInfo.setOpNo(req.getOpNO());
                mesBatchInfo.setOpName(req.getOpName());
                mesBatchInfo.setBillNo(sStockInNO);
                mesBatchInfo.setProductDate(prodDate);
                mesBatchInfo.setLoseDate(oneData.get("EXP_DATE").toString());
                mesBatchInfo.setPluNo(oneData.get("PLUNO").toString());
                mesBatchInfo.setSupplierType("1");
                mesBatchInfo.setSupplierId(supplier);
                mesBatchInfoList.add(mesBatchInfo);

                //Y.启用批号不检查库存量  T.启用批号且检查库存量     生产日期或批号必须填写
                if (!Check.Null(isBatch) && (isBatch.equals("Y") || isBatch.equals("T")) && isBatchPara.equals("Y")) {
                    if (Check.Null(prodDate) || Check.Null(batchNO)) {
                        throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编号:" + oneData.get("PLUNO").toString() + " 的生产日期或批号为空！");
                    }
                }

                //进货允收管制方式： 1.依进货效期管控     //必须开启批号管理  BY JZMA 20191112
                if (isBatchPara.equals("Y")) {
                    if (!Check.Null(stockInAllowType) && stockInAllowType.equals("1")) {
                        if (PosPub.isNumeric(shelfLife) && PosPub.isNumeric(stockInValidDay)) {
                            if (Integer.parseInt(stockInValidDay) > 0) {
                                String shelfLifeDate = PosPub.GetStringDate(prodDate, Integer.parseInt(shelfLife));
                                String stockInDate = PosPub.GetStringDate(prodDate, Integer.parseInt(stockInValidDay));
                                if (PosPub.compare_date(sDate, shelfLifeDate) > 0) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编号:" + oneData.get("PLUNO").toString() + " 已超过保质期，不能收货 ");
                                }
                                if (PosPub.compare_date(sDate, stockInDate) > 0) {
                                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编号:" + oneData.get("PLUNO").toString() + " 已超过收货允收日期，不能收货 ");
                                }
                            }
                        }
                    }
                }

                String warehouse = oneData.get("WAREHOUSE").toString();
                //判断仓库不能为空或空格  BY JZMA 20191118
                if (Check.Null(warehouse) || Check.Null(warehouse.trim()) || warehouse.trim().isEmpty()) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "仓库不能为空或空格");
                }


                BcReq bcReq = new BcReq();
                bcReq.setServiceType("SStockInProcess");
                bcReq.setStockInOutType(stockInType);
                bcReq.setSyneryDiff(!corp.equals(bizCorp));
                BcRes bcMap = PosPub.getBTypeAndCostCode(bcReq);
                if (Check.Null(bcMap.getBType()) || Check.Null(bcMap.getCostCode())) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取不到业务类型及成本码！");
                }
                //销售没进货价

                String distriPrice = "";
                String distriAMT = "";
                String taxAmt = "";
                String preTaxAmt = "";
                String taxCode = "";
                String taxRate = "";

                //只有采购才有搞税 costcode=S
                if (!"4".equals(stockInType)) {
                    if (corp.equals(bizCorp)) {
                        //1.同法人采购：
                        //进货价=采购价DCP_SSTOCKIN_DETAIL.purPrice,
                        //进货金额（含税）=采购金额DCP_SSTOCKIN_DETAIL.purAmt,
                        //未税金额=未税金额DCP_SSTOCKIN_DETAIL.preTaxAmt,
                        //税额 = 税额DCP_SSTOCKIN_DETAIL.taxAmt,
                        //税别 = 税别DCP_SSTOCKIN_DETAIL.taxCode,
                        //税率 = 税率DCP_SSTOCKIN_DETAIL.taxRate;
                        //
                        distriPrice = oneData.get("PURPRICE").toString();
                        distriAMT = oneData.get("PURAMT").toString();
                        taxAmt = oneData.get("TAXAMT").toString();
                        taxCode = oneData.get("TAXCODE").toString();
                        taxRate = oneData.get("TAXRATE").toString();
                        preTaxAmt = oneData.get("PRETAXAMT").toString();
                    } else {
                        List<Map<String, Object>> interFilterRows = interSettleDataList.stream().filter(x -> x.get("ITEM").toString().equals(oneData.get("ITEM").toString())).collect(Collectors.toList());
                        if (interFilterRows.size() > 0) {


                            //2.跨法人采购：
                            //
                            //进货价 = 内部采购进货价DCP_INTERSETTLE_DETAIL.RECEIVEPRICE,
                            //进货金额（含税）=内部采购进货金额DCP_SSTOCKIN_DETAIL.RECEIVEAMT,
                            //未税金额 = 内部采购未税金额DCP_INTERSETTLE_DETAIL.PRETAXAMT,
                            //税额 = 内部采购未税金额DCP_INTERSETTLE_DETAIL.TAXAMT,
                            //税别编码 = 内部采购税别编码DCP_INTERSETTLE_DETAIL.TAXCODE,
                            //税率%= 内部采购税率DCP_INTERSETTLE_DETAIL.TAXRATE;


                            distriPrice = interFilterRows.get(0).get("RECEIVEPRICE").toString();
                            distriAMT = interFilterRows.get(0).get("RECEIVEAMT").toString();
                            taxAmt = interFilterRows.get(0).get("TAXAMT").toString();
                            taxCode = interFilterRows.get(0).get("TAXCODE").toString();
                            taxRate = interFilterRows.get(0).get("TAXRATE").toString();
                            preTaxAmt = interFilterRows.get(0).get("PRETAXAMT").toString();
                        }
                    }


                    if (Check.Null(distriAMT) || Check.Null(taxAmt) || Check.Null(preTaxAmt)) {
                        if (corp.equals(bizCorp)) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "供应商结算金额校验失败!税额、金额、未税金额不可为空！");
                        } else {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "内部结算金额校验失败!税额、金额、未税金额不可为空！");
                        }

                    }
                    int i = new BigDecimal(preTaxAmt).add(new BigDecimal(taxAmt)).compareTo(new BigDecimal(distriAMT));
                    if (i != 0) {

                        if (corp.equals(bizCorp)) {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "供应商结算金额校验失败！供应商结算的【未税金额】与【税额】≠【含税金额】");
                        } else {
                            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "内部结算金额校验失败！供应商结算的【未税金额】与【税额】≠【含税金额】");
                        }

                        // throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "金额、未税金额、税额不一致！");
                    }
                }

                List<Object> inputParameter = Lists.newArrayList();
                inputParameter.add(eId);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                inputParameter.add(null);
                inputParameter.add(shopId);                                          //P_OrgID		IN	VARCHAR2,	--组织
                inputParameter.add(bcMap.getBType());
                inputParameter.add(bcMap.getCostCode());
                inputParameter.add("05");                                                                   //P_BillType	IN	VARCHAR2,	--单据类型
                inputParameter.add(sStockInNO);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                inputParameter.add(oneData.get("ITEM"));                                                   //P_Item		IN	INTEGER,	--单据行号
                inputParameter.add("0");
                inputParameter.add(1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                inputParameter.add(oneData.get("BDATE"));                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                inputParameter.add(oneData.get("PLUNO"));                                          //P_PluNo		IN	VARCHAR2,	--品号
                inputParameter.add(StringUtils.toString(oneData.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                inputParameter.add(warehouse);                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                inputParameter.add(StringUtils.toString(oneData.get("BATCH_NO"), " "));        //P_BATCHNO	IN	VARCHAR2,	--批号
                inputParameter.add(StringUtils.toString(oneData.get("LOCATION"), " "));       //P_LOCATION IN VARCHAR2,	--库位
                inputParameter.add(oneData.get("PUNIT"));                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                inputParameter.add(oneData.get("PQTY"));                                            //P_Qty		IN	NUMBER,		--交易数量
                inputParameter.add(oneData.get("BASEUNIT"));                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                inputParameter.add(oneData.get("BASEQTY"));                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                inputParameter.add(oneData.get("UNIT_RATIO"));                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                inputParameter.add(oneData.get("PRICE"));                                                   //P_Price		IN	NUMBER,		--零售价
                inputParameter.add(oneData.get("AMT"));                                                     //P_Amt		IN	NUMBER,		--零售金额
                inputParameter.add(distriPrice);                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                inputParameter.add(distriAMT);                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额

                inputParameter.add(preTaxAmt);
                inputParameter.add(taxAmt);
                inputParameter.add(taxCode);
                inputParameter.add(taxRate);

                inputParameter.add(accountDate);               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                inputParameter.add(DateFormatUtils.getDate(oneData.get("PROD_DATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                inputParameter.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                inputParameter.add("DCP_SStockInProcess生成");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                inputParameter.add("");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                inputParameter.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                this.addProcessData(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V35", inputParameter)));

                //不同法人采购收货加一个业务法人的 入库---- 采购收货入库

                if (!corp.equals(bizCorp)) {

                    String bizBatchNo = "";
                    if ("Y".equals(isBatchParaBiz)) {
                        if (Check.NotNull(oneData.get("BATCH_NO").toString())) {
                            bizBatchNo = oneData.get("BATCH_NO").toString();
                        } else {
                            bizBatchNo = "DEFAULTBATCH";
                        }
                    }

                    List<Object> inputParameter2 = Lists.newArrayList();
                    inputParameter2.add(eId);                                                     //P_EID		IN	VARCHAR2,	--企业ID
                    inputParameter2.add(null);
                    inputParameter2.add(bizCorp);                                          //P_OrgID		IN	VARCHAR2,	--组织
                    if (!"4".equals(stockInType)) {
                        inputParameter2.add("05");//BTYPE
                        inputParameter2.add("S");//COSTCODE
                        inputParameter2.add("05");//BILLTYPE
                    } else {
                        //销退
                        inputParameter2.add("21");//BTYPE
                        inputParameter2.add("U");//COSTCODE
                        inputParameter2.add("21");//BILLTYPE
                    }
                    inputParameter2.add(sStockInNO);                                                //P_BillNo	IN	VARCHAR2,	--单据号
                    inputParameter2.add(oneData.get("ITEM"));                                                   //P_Item		IN	INTEGER,	--单据行号
                    inputParameter2.add("0");
                    inputParameter2.add(1);                                                                     //P_Direct	IN	INTEGER,	--异动方向 1=加库存 -1=减库存
                    inputParameter2.add(oneData.get("BDATE"));                                                   //P_BDate		IN	VARCHAR2,	--营业日期 yyyy-MM-dd
                    inputParameter2.add(oneData.get("PLUNO"));                                          //P_PluNo		IN	VARCHAR2,	--品号
                    inputParameter2.add(StringUtils.toString(oneData.get("FEATURENO"), " "));          //P_FeatureNo	IN	VARCHAR2,	--特征码
                    inputParameter2.add(bizIcCostWarehouse);                                              //P_WAREHOUSE	IN	VARCHAR2,	--仓库
                    inputParameter2.add(bizBatchNo);        //P_BATCHNO	IN	VARCHAR2,	--批号
                    inputParameter2.add(" ");       //P_LOCATION IN VARCHAR2,	--库位
                    inputParameter2.add(oneData.get("PUNIT"));                                           //P_SUnit		IN	VARCHAR2,	--交易单位
                    inputParameter2.add(oneData.get("PQTY"));                                            //P_Qty		IN	NUMBER,		--交易数量
                    inputParameter2.add(oneData.get("BASEUNIT"));                                                //P_BASEUNIT	IN	VARCHAR2,	--基准单位
                    inputParameter2.add(oneData.get("BASEQTY"));                                                 //P_BASEQTY	IN	NUMBER,		--基准数量
                    inputParameter2.add(oneData.get("UNIT_RATIO"));                                              //P_UNITRATIO	IN	NUMBER,		--换算比例
                    inputParameter2.add(oneData.get("PRICE"));                                                   //P_Price		IN	NUMBER,		--零售价
                    inputParameter2.add(oneData.get("AMT"));                                                     //P_Amt		IN	NUMBER,		--零售金额

                    if (!"4".equals(stockInType)) {
                        inputParameter2.add(oneData.get("PURPRICE").toString());                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                        inputParameter2.add(oneData.get("PURAMT").toString());                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额

                        inputParameter2.add(oneData.get("PRETAXAMT").toString());
                        inputParameter2.add(oneData.get("TAXAMT").toString());
                        inputParameter2.add(oneData.get("TAXCODE").toString());
                        inputParameter2.add(oneData.get("TAXRATE").toString());

                    } else {
                        inputParameter2.add("0");                                             //P_DISTRIPRICE	IN	NUMBER,		--进货价
                        inputParameter2.add("0");                                               //P_DISTRIAMT	IN	NUMBER,		--进货金额

                        inputParameter2.add("0");
                        inputParameter2.add("0");
                        inputParameter2.add("");
                        inputParameter2.add("0");

                    }
                    inputParameter2.add(accountDate);               //P_ACCOUNTDATE	IN	VARCHAR2,	--入账日期 yyyy-MM-dd
                    inputParameter2.add(DateFormatUtils.getDate(oneData.get("PROD_DATE").toString()));            //P_PRODDATE	IN	VARCHAR2,	--批号的生产日期 yyyy-MM-dd
                    inputParameter2.add(DateFormatUtils.getDate(oneData.get("BDATE").toString()));               //P_BillDate	IN	VARCHAR2,	--单据日期 yyyy-MM-dd
                    inputParameter2.add("DCP_SStockInProcess生成");                                           //P_ReasonID	IN 	VARCHAR2,	--异动原因
                    inputParameter2.add("");                                                                     //P_Description	IN 	VARCHAR2,	--异动描述
                    inputParameter2.add(opNo);                                                    //P_UserID	IN	VARCHAR2	--操作员

                    this.addProcessData(new DataProcessBean(DataBeans.getProcedureBean("SP_DCP_STOCKCHANGE_V35", inputParameter2)));

                }


                if (stockInType.equals("4")) {//销退入库
                    bizPartnerNo = getQData.get(0).get("CUSTOMER").toString();
                    bType = "1";
                    bizType = "2";
                    setDirection = "-1";
                    settleBType = "5";

                } else {
                    bizPartnerNo = getQData.get(0).get("SUPPLIER").toString();
                    bType = "1";
                    bizType = "1";
                    setDirection = "1";
                    settleBType = "1";
                }
            }

            String transferInfoSql = getTransferInfoSql(req);
            List<Map<String, Object>> transferData = this.doQueryData(transferInfoSql, null);
            String bDate = getQData.get(0).get("BDATE").toString();
            String accountDateS = bDate;// transferData.get(0).get("ACCOUNT_DATE").toString();
            String payDate = "";
            String month = "";
            String year = "";
            if (Check.NotNull(accountDateS)) {
                if (accountDateS.length() == 8) {
                    year = accountDateS.substring(0, 4);
                    month = accountDateS.substring(4, 6);
                }
                int pseasons = Integer.parseInt(transferData.get(0).get("PSEASONS").toString());
                int pmonths = Integer.parseInt(transferData.get(0).get("PMONTHS").toString());
                int pdays = Integer.parseInt(transferData.get(0).get("PDAYS").toString());

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                LocalDate parseDate = LocalDate.parse(accountDateS, formatter);
                parseDate = parseDate.plusMonths(pseasons * 3);
                parseDate = parseDate.plusMonths(pmonths);
                parseDate = parseDate.plusDays(pdays);
                payDate = parseDate.format(formatter);

            }

            String billSql = "select to_char(a.BILLDATE,'yyyyMMdd') as billDate from DCP_BIZPARTNER_BILL a where a.eid='" + req.geteId() + "'" +
                    " and a.BIZPARTNERNO='" + bizPartnerNo + "' and " +
                    " to_char(a.BDATE,'yyyyMMdd')<='" + accountDateS + "'" +
                    " and to_char(a.EDATE,'yyyyMMdd')>='" + accountDateS + "' ";
            List<Map<String, Object>> billData = this.doQueryData(billSql, null);
            String billDate = sDate;
            if (billData.size() > 0) {
                billDate = billData.get(0).get("BILLDATE").toString();
            }
            for (Map<String, Object> oneData : transferData) {

                //增加检查税率是否相等
                BigDecimal transferTaxAmt = new BigDecimal(oneData.get("TAXAMT").toString());
                BigDecimal trasnferAmt = new BigDecimal(oneData.get("AMT").toString());
                BigDecimal transferPreTaxAmt = new BigDecimal(oneData.get("PRETAXAMT").toString());

                //未税金额+税额<>含税金额，不允许过账！
                if (transferPreTaxAmt.add(transferTaxAmt).compareTo(trasnferAmt) != 0) {
                    throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "供应商结算金额校验失败！供应商结算的【未税金额】与【税额】≠【含税金额】");
                }

                ColumnDataValue setColumns = new ColumnDataValue();
                setColumns.add("EID", eId, Types.VARCHAR);
                setColumns.add("ORGANIZATIONNO", req.getOrganizationNO(), Types.VARCHAR);
                setColumns.add("BDATE", oneData.get("BDATE").toString(), Types.VARCHAR);
                setColumns.add("BTYPE", settleBType, Types.VARCHAR);//单据类型	BILLTYPE	2.采购退货	1.采购入库,2.采购退货,3.费用单,4.销货单,5.销退单,6.供应商往来调整,7.大客往来调整
                setColumns.add("BILLNO", oneData.get("BILLNO").toString(), Types.VARCHAR);
                setColumns.add("ITEM", oneData.get("ITEM").toString(), Types.VARCHAR);
                setColumns.add("BIZTYPE", bizType, Types.VARCHAR);//1.供应商
                setColumns.add("BIZPARTNERNO", bizPartnerNo, Types.VARCHAR);
                setColumns.add("PAYORGNO", oneData.get("PAYORGNO").toString(), Types.VARCHAR);
                setColumns.add("BILLDATENO", oneData.get("BILLDATENO").toString(), Types.VARCHAR);
                setColumns.add("PAYDATENO", oneData.get("PAYDATENO").toString(), Types.VARCHAR);
                setColumns.add("INVOICECODE", oneData.get("INVOICECODE").toString(), Types.VARCHAR);
                setColumns.add("CURRENCY", oneData.get("CURRENCY").toString(), Types.VARCHAR);
                setColumns.add("BILLDATE", billDate, Types.VARCHAR);
                setColumns.add("PAYDATE", payDate, Types.VARCHAR);
                setColumns.add("MONTH", month, Types.VARCHAR);
                setColumns.add("YEAR", year, Types.VARCHAR);
                setColumns.add("TAXCODE", oneData.get("TAXCODE").toString(), Types.VARCHAR);
                setColumns.add("TAXRATE", oneData.get("TAXRATE").toString(), Types.VARCHAR);
                setColumns.add("DIRECTION", setDirection, Types.VARCHAR);//1:正向 -1:负向
                setColumns.add("PRETAXAMT", oneData.get("PRETAXAMT").toString(), Types.VARCHAR);
                setColumns.add("BILLAMT", oneData.get("AMT").toString(), Types.VARCHAR);
                setColumns.add("TAXAMT", oneData.get("TAXAMT").toString(), Types.VARCHAR);
                setColumns.add("UNSETTLEAMT", oneData.get("AMT").toString(), Types.VARCHAR);
                setColumns.add("SETTLEAMT", "0", Types.VARCHAR);
                setColumns.add("APQTY", "0", Types.VARCHAR);
                setColumns.add("PAIDAMT", "0", Types.VARCHAR);
                setColumns.add("BILLQTY", oneData.get("PQTY").toString(), Types.VARCHAR);
                setColumns.add("BILLPRICE", oneData.get("PRICE").toString(), Types.VARCHAR);
                setColumns.add("PRICEUNIT", oneData.get("PUNIT").toString(), Types.VARCHAR);
                setColumns.add("DEPARTID", oneData.get("DEPARTID").toString(), Types.VARCHAR);
                setColumns.add("CATEGORY", oneData.get("CATEGORY").toString(), Types.VARCHAR);
                setColumns.add("PLUNO", oneData.get("PLUNO").toString(), Types.VARCHAR);
                setColumns.add("FEATURENO", oneData.get("FEATURENO").toString(), Types.VARCHAR);
                setColumns.add("STATUS", "0", Types.VARCHAR);//0-未对账 1-对账中 2-对账完成 3-部分付款 4-付款完成
                setColumns.add("UNPAIDAMT", oneData.get("AMT").toString(), Types.VARCHAR);
                setColumns.add("UNAPAMT", oneData.get("AMT").toString(), Types.VARCHAR);
                setColumns.add("APAMT", "0", Types.VARCHAR);
                String[] setColumnNames = setColumns.getColumns().toArray(new String[0]);
                DataValue[] setDataValues = setColumns.getDataValues().toArray(new DataValue[0]);
                InsBean ib1 = new InsBean("DCP_SETTLEDATA", setColumnNames);
                ib1.addValues(setDataValues);
                this.addProcessData(new DataProcessBean(ib1));
            }

            if (mesBatchInfoList.size() > 0) {
                PosPub.insertIntoMesBatchList(dao, mesBatchInfoList);
            }

            this.doExecuteDataToDB();


            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
            //***********调用库存同步给三方，这是个异步，不会影响效能*****************
            try {
                WebHookService.stockSync(eId, shopId, sStockInNO);
            } catch (Exception e) {

            }
        }

        DCP_InterSettleDataProcessReq isdpr = new DCP_InterSettleDataProcessReq();
        isdpr.setServiceId("DCP_InterSettleDataProcess");
        if ("2".equals(req.getRequest().getStatus())) {
            isdpr.setToken(req.getToken());
            isdpr.setRequest(isdpr.new Request());
            isdpr.getRequest().setBillNo(req.getRequest().getSStockInNo());
            isdpr.getRequest().setOprType("confirm");
        }
        else {
            isdpr.setToken(req.getToken());
            isdpr.setRequest(isdpr.new Request());
            isdpr.getRequest().setBillNo(req.getRequest().getSStockInNo());
            isdpr.getRequest().setOprType("unConfirm");
        }
        ServiceAgentUtils<DCP_InterSettleDataProcessReq, DCP_InterSettleDataProcessRes> serviceAgent = new ServiceAgentUtils<>();
        if (!serviceAgent.agentServiceSuccess(isdpr, new TypeToken<DCP_InterSettleDataProcessRes>() {
        })) {
            res.setServiceDescription("调用内部交易结算数据审核/反审核失败");
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SStockInProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SStockInProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SStockInProcessReq req) throws Exception {
        return null;
    }

    @Override
    protected TypeToken<DCP_SStockInProcessReq> getRequestType() {
        return new TypeToken<DCP_SStockInProcessReq>() {
        };
    }

    @Override
    protected DCP_SStockInProcessRes getResponseType() {
        return new DCP_SStockInProcessRes();
    }


    private String getQueryDcp_PurReceive_detailSql(DCP_SStockInProcessReq req) throws Exception {
        String eId = req.geteId();
        String sStockInNO = req.getRequest().getSStockInNo();
        String sql = " SELECT a.EID,a.BILLNO,a.ITEM,a.STOCKINQTY,a.PASSQTY,a.PQTY,a.QCSTATUS   " +
                " FROM DCP_PURRECEIVE_DETAIL a " +
                " WHERE exists ( SELECT 1 FROM " +
                "   DCP_sstockin_detail b WHERE a.BILLNO=b.OFNO and b.EID='" + eId + "' and b.sstockinno='" + sStockInNO + "'" +
                "  ) ";

        return sql;
    }


    private String getQueryDCP_ReceivingDetailSql(DCP_SStockInProcessReq req) throws Exception {
        //一笔入库单只有一笔通知单
        String eId = req.geteId();
        String sStockInNO = req.getRequest().getSStockInNo();
        String sql = " SELECT a.EID,a.RECEIVINGNO,a.ITEM,a.ORGANIZATIONNO,a.SHOPID,NVL(a.STOCKIN_QTY,0) STOCKIN_QTY,NVL(PQTY,0) PQTY, a.STATUS " +
                " FROM DCP_receiving_detail a " +
                " WHERE exists ( SELECT 1 FROM " +
                "   DCP_sstockin_detail b WHERE a.RECEIVINGNO=b.RECEIVINGNO and b.EID='" + eId + "' and b.sstockinno='" + sStockInNO + "'" +
                "  ) ";

        return sql;
    }

    private String getQueryDCP_PurOrder_Delivery2Sql(DCP_SStockInProcessReq req) throws Exception {
//       ● 更新来源采购订单【已入库量】，字段：DCP_PURORDER_DELIVERY.STOCKINQTY
//        目标表：DCP_PURORDER_DELIVERY
//        关联条件：
//        收货单：收货单号BILLNO=入库单来源单号OFNO，收货单项次ITEM=入库单来源项次OITEM
//        采购单：采购单PURORDERNO=收货单号PURORDERNO，采购单项次ITEM=收货单采购项次POITEM，采购单项序ITEM2=收货单采购项次POITEM2

        //一笔入库单只有一笔通知单
        String eId = req.geteId();
        String sStockInNO = req.getRequest().getSStockInNo();

        String sql = " SELECT NVL(a.RECEIVEQTY,0) RECEIVEQTY,a.PURORDERNO,NVL(a.STOCKINQTY,0) STOCKINQTY, " +
                " a.ITEM,a.ITEM2,b.RECEIVINGNO,b.ITEM RECEIVINGITEM,d.CLOSE_STATUS " +
                " FROM DCP_PURORDER_DELIVERY a " +
                " INNER JOIN DCP_receiving_detail b on a.EID=b.EID and a.ITEM=b.OITEM and a.ITEM2=b.OITEM2 and a.PURORDERNO=b.OFNO " +
                //" INNER JOIN DCP_PURRECEIVE_DETAIL b on a.EID=b.EID and a.ITEM=b.POITEM and a.ITEM2=b.POITEM2 and a.PURORDERNO=b.PURORDERNO " +
                " LEFT JOIN DCP_PURORDER_DETAIL d on a.eid=d.eid and a.PURORDERNO=d.PURORDERNO and a.ITEM2=d.ITEM " +
                " WHERE exists ( SELECT 1 FROM " +
                "   DCP_sstockin_detail c WHERE b.RECEIVINGNO=c.receivingno and b.ITEM=c.receivingitem and c.EID='" + eId + "' and c.sstockinno='" + sStockInNO + "'" +
                "  ) ";

        return sql;
    }

    private String getQueryDCP_PurOrder_DeliverySql(DCP_SStockInProcessReq req) throws Exception {
        //、入库类型=“2.采购收货入库”时，按照通知单【来源采购单+采购项次+采购项序】汇总行【收货量-PQTY】，
        // 更新采购交期明细对应DCP_PURORDER_DELIVERY.RECEIVEQTY【已收货量】=原字段值+本次更新值（合计收货量）；


        //一笔入库单只有一笔通知单
        String eId = req.geteId();
        String sStockInNO = req.getRequest().getSStockInNo();

        String sql = " SELECT a.RECEIVEQTY,a.PURORDERNO,a.ITEM,a.ITEM2,b.RECEIVINGNO,b.ITEM RECEIVINGITEM,A.STOCKINQTY " +
                " ,d.CLOSE_STATUS" +
                " FROM DCP_PURORDER_DELIVERY a " +
                " INNER JOIN DCP_receiving_detail b on a.EID=b.EID and a.ITEM=b.OITEM and a.ITEM2=b.OITEM2 and a.PURORDERNO=b.OFNO " +
                " LEFT JOIN DCP_PURORDER_DETAIL d on a.eid=d.eid and a.PURORDERNO=d.PURORDERNO and a.ITEM2=d.ITEM " +
                " WHERE exists ( SELECT 1 FROM " +
                "   DCP_sstockin_detail c WHERE b.RECEIVINGNO=c.RECEIVINGNO and b.ITEM=c.RECEIVINGITEM and c.EID='" + eId + "' and c.sstockinno='" + sStockInNO + "'" +
                "  ) ";

        return sql;
    }


//    private String getUpdateDCP_PURORDER_DETAILSQL(DCP_SStockInProcessReq req) throws Exception {
//        String eId = req.geteId();
//        String sStockInNO = req.getRequest().getSStockInNo();
//        String sql =
//                "       SELECT c.EID,c.POITEM,c.POITEM2,c.PURORDERNO ,NVL(SUM(b.pqty), 0) pqty " +
//                        "        FROM DCP_sstockin_detail b " +
//                        "        INNER JOIN DCP_PURRECEIVE_DETAIL c ON b.EID=c.EID and b.OFNO=c.BILLNO and b.OITEM=c.ITEM   " +
//                        "        WHERE b.EID='" + eId + "' and b.sstockinno='" + sStockInNO + "' " +
//                        "   GROUP BY c.EID,c.POITEM,c.POITEM2,c.PURORDERNO ";
//
//        sql = " UPDATE DCP_PURORDER_DETAIL a SET " +
//                " a.STATUS= CASE WHEN c " +
//                "  ( " +
//                "   SELECT PQTY FROM (" + sql + ") doc " +
//                "    WHERE a.EID = DOC.EID " +
//                "      AND a.ITEM2 = DOC.POITEM " +
//                "      AND a.PURORDERNO = DOC.PURORDERNO " +
//                " ) " +
//                " WHERE EXISTS (  SELECT 1 FROM (" + sql + ") doc" +
//                "    WHERE a.EID = DOC.EID " +
//                "      AND a.ITEM2 = DOC.POITEM " +
//                "      AND a.PURORDERNO = DOC.PURORDERNO " +
//                ") ";
//
//
//        return sql;
//    }

//    private String getUpdateDCP_PURORDER_DELIVERYSQL2(DCP_SStockInProcessReq req) throws Exception {
////        收货单：收货单号BILLNO=入库单来源单号OFNO，收货单项次ITEM=入库单来源项次OITEM
////        采购单：采购单PURORDERNO=收货单号PURORDERNO，采购单项次ITEM=收货单采购项次POITEM，采购单项序ITEM2=收货单采购项次POITEM2
////● 更新来源采购订单【结束码】、【单据状态】,字段：DCP_PURORDER_DETAIL.CLOSE_STATUS, DCP_PURORDER.STATUS
//
//        String eId = req.geteId();
//        String sStockInNO = req.getRequest().getSStockInNo();
//
//        String sql =
//                "   SELECT c.EID,c.POITEM,c.POITEM2,c.PURORDERNO ,NVL(SUM(b.pqty), 0) pqty " +
//                        "        FROM DCP_sstockin_detail b " +
//                        "        INNER JOIN DCP_PURRECEIVE_DETAIL c ON b.EID=c.EID and b.OFNO=c.BILLNO and b.OITEM=c.ITEM   " +
//                        "        WHERE b.EID='" + eId + "' and b.sstockinno='" + sStockInNO + "' " +
//                        "   GROUP BY c.EID,c.POITEM,c.POITEM2,c.PURORDERNO ";
//
//        sql = " UPDATE DCP_PURORDER_DELIVERY a SET " +
//                " RECEIVEQTY = NVL(a.RECEIVEQTY, 0) +  " +
//                "  ( " +
//                "   SELECT PQTY FROM (" + sql + ") doc " +
//                "    WHERE a.EID = DOC.EID " +
//                "      AND a.ITEM2 = DOC.POITEM2 " +
//                "      AND a.ITEM2 = DOC.POITEM " +
//                "      AND a.PURORDERNO = DOC.PURORDERNO " +
//                " ) " +
//                " WHERE EXISTS ( SELECT 1 FROM (" + sql + ") doc" +
//                "    WHERE a.EID = DOC.EID " +
//                "      AND a.ITEM2 = DOC.POITEM2 " +
//                "      AND a.ITEM2 = DOC.POITEM " +
//                "      AND a.PURORDERNO = DOC.PURORDERNO " +
//                ") ";
//
//        return sql;
//
//    }


    protected String GetDCP_SSTOCKIN_SQL(DCP_SStockInProcessReq req, String sStockInNO) throws Exception {

        String sql = " select a.SSTOCKINNO,a.SHOPID,a.EID,a.ORGANIZATIONNO,a.STOCKINTYPE,b.ITEM,b.RECEIVINGNO,b.RECEIVINGITEM,b.OFNO,b.OITEM,b.SSTOCKINNO, "
                + " b.OTYPE,b.ORIGINNO,b.ORIGINITEM,b.OOTYPE,b.OOFNO,b.OOITEM,a.BDATE,b.PLUNO, "
                + " b.PUNIT,b.PQTY,b.PRICE,b.BASEQTY,b.UNIT_RATIO,b.BASEUNIT,b.DISTRIPRICE,b.AMT, "
                + " b.DISTRIAMT,b.PROD_DATE,b.FEATURENO,b.BATCH_NO,c.ISBATCH,c.SHELFLIFE,c.STOCKINVALIDDAY, "
                + " b.WAREHOUSE,b.LOCATION,a.STATUS,a.customer,a.supplier,a.corp,a.bizcorp,B.PURPRICE,b.PURAMT,b.exp_date,b.ISGIFT,b.TAXCODE,b.TAXAMT,b.TAXRATE,b.PRETAXAMT,a.supplier," +
                " d.IC_COST_WAREHOUSE as bizIcCostWarehouse  "
                + " FROM DCP_SSTOCKIN a "
                + " INNER JOIN DCP_SSTOCKIN_DETAIL b ON a.EID=b.EID and a.SSTOCKINNO=b.SSTOCKINNO and a.SHOPID=b.SHOPID and a.ORGANIZATIONNO=b.ORGANIZATIONNO and a.SSTOCKINNO=b.SSTOCKINNO "
                + " LEFT JOIN DCP_GOODS c ON b.EID=c.EID and b.PLUNO=c.PLUNO  " +
                " LEFT JOIN DCP_ORG d on d.eid=a.eid and d.organizationno=a.bizcorp "
                + " where a.EID='" + req.geteId() + "'  "
                + " and a.SSTOCKINNO='" + sStockInNO + "' ";

        return sql;
    }


    private String getTransferInfoSql(DCP_SStockInProcessReq req) {
        String sql = "select distinct a.eid,a.organizationno,a.bdate,b.billno,b.item,a.supplier,a.customer,a.payorgno,a.billdateno,a.paydateno,a.invoicecode,a.currency," +
                " b.taxcode,b.taxrate,b.pretaxamt,b.amt,b.taxamt,b.pqty,b.price,b.punit,a.departid,c.category,b.pluno,b.featureno,a.ACCOUNT_DATE," +
                " nvl(d.PSEASONS,0) as PSEASONS,nvl(d.PMONTHS,0) as pmonths,nvl(d.pdays,0) as pdays  " +
                " from DCP_SSTOCKIN a " +
                " left join DCP_TRANSACTION b on a.eid=b.eid and a.organizationno=b.organizationno and a.sstockinno=b.billno " +
                " left join DCP_SSTOCKIN_detail c on a.eid=c.eid and a.organizationno=c.organizationno and a.sstockinno=c.sstockinno and b.pluno=c.pluno and b.featureno=c.featureno  " +
                " left join dcp_paydate d on a.eid=d.eid and a.paydateno=d.paydateno " +
                " where a.EID='" + req.geteId() + "' " +
                " and a.SHOPID='" + req.getShopId() + "'" +
                " and a.organizationno='" + req.getOrganizationNO() + "'  " +
                " and a.SSTOCKINNO='" + req.getRequest().getSStockInNo() + "' ";

        return sql;
    }


}

