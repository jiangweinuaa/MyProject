package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_PurReceiveStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PurReceiveStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.bc.MesBatchInfo;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_PurReceiveStatusUpdate extends SPosAdvanceService<DCP_PurReceiveStatusUpdateReq, DCP_PurReceiveStatusUpdateRes> {

    private static final String OPTYPE_CONFIRM = "confirm";
    private static final String OPTYPE_UNCONFIRM = "unconfirm";
    private static final String OPTYPE_CANCEL = "cancel";

    @Override
    protected void processDUID(DCP_PurReceiveStatusUpdateReq req, DCP_PurReceiveStatusUpdateRes res) throws Exception {


        ColumnDataValue condition = new ColumnDataValue();
        ColumnDataValue dcp_purreceive = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));

        String querySql = String.format(" SELECT * FROM DCP_PURRECEIVE WHERE EID='%s' AND BILLNO='%s' ", req.geteId(), req.getRequest().getBillNo());

        List<MesBatchInfo> mesBatchInfoList = new ArrayList<>();
        List<Map<String, Object>> queryData = this.doQueryData(querySql, null);
        if (queryData != null && !queryData.isEmpty()) {
            Map<String, Object> oneData = queryData.get(0);
            if (OPTYPE_CANCEL.equals(req.getRequest().getOpType())) {

                if (!"0".equals(oneData.get("STATUS").toString())) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可作废");
                }
                dcp_purreceive.add("STATUS", DataValues.newInteger(3));
                dcp_purreceive.add("CANCELBY", DataValues.newString(req.getOpNO()));
                dcp_purreceive.add("CANCELTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                //回滚收获通知单状态为6
                ColumnDataValue receivingCondition = new ColumnDataValue();
                ColumnDataValue dcp_receiving = new ColumnDataValue();

                receivingCondition.add("EID", DataValues.newString(req.geteId()));
                receivingCondition.add("RECEIVINGNO", DataValues.newString(oneData.get("RECEIVINGNO")));

                dcp_receiving.add("STATUS", DataValues.newInteger(6));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING", receivingCondition, dcp_receiving)));

            }
            else if (OPTYPE_CONFIRM.equals(req.getRequest().getOpType())) {
                if (!"0".equals(oneData.get("STATUS").toString())) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可审核");
                }

//                Set<String> receivingNoSet = Sets.newHashSet();
                querySql = String.format(" SELECT a.*," +
                        " b.PURORDERNO OOrderNO,b.RECEIVEQTY,b.BOOKQTY,c.RECEIVINGNO RORDERNO,c.STOCKIN_QTY," +
                        " c.PQTY RPQTY,d.SUPPLIER,c.PASSQTY AS PASSQTYYET " +
                        " FROM DCP_PURRECEIVE_DETAIL a " +
                        " inner join DCP_PURRECEIVE  d on a.eid=d.eid and a.ORGANIZATIONNO=d.ORGANIZATIONNO and a.BILLNO=d.BILLNO "+
                        " LEFT JOIN DCP_PURORDER_DELIVERY b ON a.EID=b.EID and a.PURORDERNO=b.PURORDERNO and a.POITEM=b.ITEM and a.POITEM2=b.ITEM2 " +
                        " LEFT JOIN DCP_RECEIVING_DETAIL c on a.EID=c.EID and a.RECEIVINGNO=c.RECEIVINGNO and a.RECEIVINGITEM=c.ITEM  " +
                        " WHERE a.EID='%s' AND a.BILLNO='%s' ", req.geteId(), req.getRequest().getBillNo());
                queryData = doQueryData(querySql, null);

                if (queryData != null && !queryData.isEmpty()) {
                    for (Map<String, Object> data : queryData) {

                        //增加批号集合
                        MesBatchInfo mesBatchInfo = new MesBatchInfo();
                        mesBatchInfo.setEId(req.geteId());
                        mesBatchInfo.setShopId(req.getShopId());
                        mesBatchInfo.setAddBatchNo(data.get("BATCHNO").toString());
                        mesBatchInfo.setFeatureNo(data.get("FEATURENO").toString());
                        mesBatchInfo.setBillType("");
                        mesBatchInfo.setOpNo(req.getOpNO());
                        mesBatchInfo.setOpName(req.getOpName());
                        mesBatchInfo.setBillNo(req.getRequest().getBillNo());
                        mesBatchInfo.setProductDate(data.get("PRODDATE").toString());
                        mesBatchInfo.setLoseDate(data.get("EXPDATE").toString());
                        mesBatchInfo.setPluNo(data.get("PLUNO").toString());
                        mesBatchInfo.setSupplierType("1");
                        mesBatchInfo.setSupplierId(data.get("SUPPLIER").toString());
                        mesBatchInfoList.add(mesBatchInfo);

                        //按照【来源采购单+采购项次+采购项序】汇总行【收货量-PQTY】，更新采购交期明细对应DCP_PURORDER_DELIVERY.RECEIVEQTY【已收货量】=原字段值+本次更新值（合计收货量）;
                        //if ("0".equals(data.get("QCSTATUS").toString())) { //无需检验
                            if (null != data.get("OORDERNO") && StringUtils.isNotEmpty(data.get("OORDERNO").toString())) {

                                //数量需要汇总更新
                                String purorderNo = data.get("PURORDERNO")==null?"":data.get("PURORDERNO").toString();
                                String poItem = data.get("POITEM")==null?"":data.get("POITEM").toString();
                                String poitem2 = data.get("POITEM2")==null?"":data.get("POITEM2").toString();
                                if(Check.NotNull(purorderNo)&&Check.NotNull(poItem)&&Check.NotNull(poitem2)){
                                    double sum = queryData.stream().filter(x -> purorderNo.equals(x.get("PURORDERNO").toString())
                                                    && poItem.equals(x.get("POITEM").toString())
                                                    && poitem2.equals(x.get("POITEM2").toString())).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                            .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();


                                    ColumnDataValue dcp_purorder_delivery = new ColumnDataValue();
                                    ColumnDataValue deliveryCondition = new ColumnDataValue();
                                    deliveryCondition.add("EID", DataValues.newString(req.geteId()));
                                    deliveryCondition.add("PURORDERNO", DataValues.newString(data.get("PURORDERNO")));
                                    deliveryCondition.add("ITEM", DataValues.newString(data.get("POITEM")));
                                    deliveryCondition.add("ITEM2", DataValues.newString(data.get("POITEM2")));

                                    double receiveQty = Double.parseDouble(data.get("RECEIVEQTY").toString()) +sum;
                                    //double bookQty = Double.parseDouble(data.get("BOOKQTY").toString()) + Double.parseDouble(data.get("PQTY").toString());

                                    dcp_purorder_delivery.add("RECEIVEQTY", DataValues.newDecimal(receiveQty));
                                    //dcp_purorder_delivery.add("BOOKQTY", DataValues.newDecimal(bookQty));

                                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER_DELIVERY", deliveryCondition, dcp_purorder_delivery)));
                                }

                            }
                            if (null != data.get("RORDERNO") && StringUtils.isNotEmpty(data.get("RORDERNO").toString())) {

                                String rOrderNo = data.get("RORDERNO").toString();
                                String rOrderItem = data.get("RECEIVINGITEM")==null?"":data.get("RECEIVINGITEM").toString();

                                if(Check.NotNull(rOrderNo)&&Check.NotNull(rOrderItem)){
                                    double sum = queryData.stream().filter(x -> rOrderNo.equals(x.get("RORDERNO").toString())
                                                    && rOrderItem.equals(x.get("RECEIVINGITEM").toString())
                                            ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                            .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();

                                    double passSum = queryData.stream().filter(x -> rOrderNo.equals(x.get("RORDERNO").toString())
                                                    && rOrderItem.equals(x.get("RECEIVINGITEM").toString())
                                            &&"0".equals(x.get("QCSTATUS").toString())
                                            ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                            .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();

                                    ColumnDataValue receivingCondition = new ColumnDataValue();
                                    ColumnDataValue dcp_receiving_detail = new ColumnDataValue();

//                            receivingNoSet.add(data.get("RECEIVINGNO").toString());

                                    receivingCondition.add("EID", DataValues.newString(req.geteId()));
                                    receivingCondition.add("RECEIVINGNO", DataValues.newString(data.get("RORDERNO")));
                                    receivingCondition.add("ITEM", DataValues.newString(data.get("RECEIVINGITEM")));

                                    double already = Double.parseDouble(data.get("RPQTY").toString());//通知单的业务数量
                                    double stockInQty = Double.parseDouble(Check.Null(data.get("STOCKIN_QTY").toString())?"0":data.get("STOCKIN_QTY").toString())
                                            + sum;//加完以后的收货数量

                                    //合格量需要有无需质检更新
                                    double passQty = Double.parseDouble(Check.Null(data.get("PASSQTYYET").toString())?"0":data.get("PASSQTYYET").toString())
                                            + passSum;//加完以后的收货合格量
                                    dcp_receiving_detail.add("STOCKIN_QTY", DataValues.newDecimal(stockInQty));
                                    dcp_receiving_detail.add("PASSQTY", DataValues.newDecimal(passQty));


//                            2.判断收货通知单明细【已收货量-STOCKIN_QTY】>=【通知收货量-PQTY】，则更新行状态=【100-收货完成】；否则更新行状态至【0-待收货】
                                    if (stockInQty >= already) {
                                        dcp_receiving_detail.add("STATUS", DataValues.newDecimal(100));
                                    } else {
                                        dcp_receiving_detail.add("STATUS", DataValues.newDecimal(0));
                                    }

                                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING_DETAIL", receivingCondition, dcp_receiving_detail)));


                                }


                            }
                        if ("0".equals(data.get("QCSTATUS").toString())) {
                            ColumnDataValue dcp_purreceive_detail = new ColumnDataValue();
                            ColumnDataValue condition1 = new ColumnDataValue();

                            condition1.add("EID", DataValues.newString(req.geteId()));
                            condition1.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
                            condition1.add("ITEM", DataValues.newString(data.get("ITEM")));

                            dcp_purreceive_detail.add("PASSQTY", DataValues.newString(data.get("PQTY")));

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURRECEIVE_DETAIL", condition1, dcp_purreceive_detail)));
                        }

                            //生成质检档
                         if ("1".equals(data.get("QCSTATUS").toString())) {
                            ColumnDataValue dcp_qualitycheck = new ColumnDataValue();

                            dcp_qualitycheck.add("EID", DataValues.newString(req.geteId()));
                            dcp_qualitycheck.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                            dcp_qualitycheck.add("BDATE", DataValues.newString(DateFormatUtils.getNowPlainDate()));
                            dcp_qualitycheck.add("QCBILLNO", DataValues.newString(getOrderNO(req, "SHQC")));
                            dcp_qualitycheck.add("QCTYPE", DataValues.newString("1"));
                            dcp_qualitycheck.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
                            dcp_qualitycheck.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
                            dcp_qualitycheck.add("SOURCEBILLNO", DataValues.newString(data.get("BILLNO")));
                            dcp_qualitycheck.add("OITEM", DataValues.newString(data.get("ITEM")));
                            dcp_qualitycheck.add("OQTY", DataValues.newString(data.get("PQTY")));
                            dcp_qualitycheck.add("OUNIT", DataValues.newString(data.get("PUNIT")));
                            dcp_qualitycheck.add("SUPPLIER", DataValues.newString(data.get("SUPPLIER")));
                            dcp_qualitycheck.add("PLUNO", DataValues.newString(data.get("PLUNO")));
                            dcp_qualitycheck.add("FEATURENO", DataValues.newString(data.get("FEATURENO")));
                            dcp_qualitycheck.add("BATCHNO", DataValues.newString(data.get("BATCHNO")));
                            dcp_qualitycheck.add("PROD_DATE", DataValues.newString(data.get("PRODDATE")));
                            dcp_qualitycheck.add("EXP_DATE", DataValues.newString(data.get("EXPDATE")));
                            dcp_qualitycheck.add("TESTQTY", DataValues.newString(data.get("PQTY")));
                            dcp_qualitycheck.add("PASSQTY", DataValues.newString(data.get("PQTY")));
                            dcp_qualitycheck.add("REJECTQTY", DataValues.newString(0));
                            dcp_qualitycheck.add("DELIVERQTY", DataValues.newString(data.get("PQTY")));
                            dcp_qualitycheck.add("TESTUNIT", DataValues.newString(data.get("PUNIT")));
                            dcp_qualitycheck.add("STATUS", DataValues.newInteger(0));
                            dcp_qualitycheck.add("RESULT", DataValues.newInteger(0));

                            dcp_qualitycheck.add("CREATEOPID", DataValues.newString(req.getOpNO()));
                            dcp_qualitycheck.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                            dcp_qualitycheck.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDate()));

                            addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_QUALITYCHECK", dcp_qualitycheck)));


                        }
                    }
                }
                dcp_purreceive.add("STATUS", DataValues.newInteger(1));
                dcp_purreceive.add("CONFIRMBY", DataValues.newString(req.getOpNO()));
                dcp_purreceive.add("CONFIRMTIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));

//                3.最后收货通知单所有行STATUS，若全部等于【100-收货完成】，更新单据状态DCP_RECEIVING.STATUS=【7-收货结束】；否则更新单据状态=【6-待收货】

            }
            else if (OPTYPE_UNCONFIRM.equals(req.getRequest().getOpType())) {
                if (!"1".equals(oneData.get("STATUS").toString())) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可反审核");
                }
                querySql = " SELECT * FROM dcp_qualitycheck WHERE SOURCEBILLNO='" + req.getRequest().getBillNo() + "' and status=1";
                queryData = doQueryData(querySql, null);
                if (null != queryData && !queryData.isEmpty()) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "收货单已存在质检单，无法删除，请联系相关部门确认！");
                }
                querySql = " SELECT b.* FROM DCP_SSTOCKIN a  " +
                        " LEFT JOIN DCP_SSTOCKIN_DETAIL b on a.eid=b.eid and a.SSTOCKINNO=b.SSTOCKINNO and a.SHOPID=b.SHOPID and a.ORGANIZATIONNO=b.ORGANIZATIONNO  " +
                        " WHERE  b.ofno='" + req.getRequest().getBillNo() + "' and a.STATUS<>'3'";//a.STOCKINTYPE='2' and
                queryData = doQueryData(querySql, null);
                if (null != queryData && !queryData.isEmpty()) {
                    throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "收货单已入库，无法删除，请检查！");
                }
                querySql = String.format(" SELECT a.*,b.PURORDERNO OOrderNO,b.RECEIVEQTY,b.BOOKQTY,c.RECEIVINGNO RORDERNO,c.STOCKIN_QTY,c.PQTY RPQTY,c.PASSQTY AS PASSQTYYET" +
                        " FROM DCP_PURRECEIVE_DETAIL a " +
                        " LEFT JOIN DCP_PURORDER_DELIVERY b ON a.EID=b.EID and a.PURORDERNO=b.PURORDERNO and a.POITEM=b.ITEM and a.POITEM2=b.ITEM2  " +
                        " LEFT JOIN DCP_RECEIVING_DETAIL c on a.EID=c.EID and a.RECEIVINGNO=c.RECEIVINGNO and a.RECEIVINGITEM=c.ITEM  " +
                        " WHERE a.EID='%s' AND BILLNO='%s' ", req.geteId(), req.getRequest().getBillNo());
                queryData = doQueryData(querySql, null);
                if (queryData != null && !queryData.isEmpty()) {
                    for (Map<String, Object> data : queryData) {
                        //按照【来源采购单+采购项次+采购项序】汇总行【收货量-PQTY】，更新采购交期明细对应DCP_PURORDER_DELIVERY.RECEIVEQTY【已收货量】=原字段值+本次更新值（合计收货量）;
                        //if ("0".equals(data.get("QCSTATUS").toString())) {
                            if (null != data.get("OORDERNO") && StringUtils.isNotEmpty(data.get("OORDERNO").toString())) {
                                //数量需要汇总更新
                                String purorderNo = data.get("PURORDERNO")==null?"":data.get("PURORDERNO").toString();
                                String poItem = data.get("POITEM")==null?"":data.get("POITEM").toString();
                                String poitem2 = data.get("POITEM2")==null?"":data.get("POITEM2").toString();
                                if(Check.NotNull(purorderNo)&&Check.NotNull(poItem)&&Check.NotNull(poitem2)){
                                    double sum = queryData.stream().filter(x -> purorderNo.equals(x.get("PURORDERNO").toString())
                                                    && poItem.equals(x.get("POITEM").toString())
                                                    && poitem2.equals(x.get("POITEM2").toString())).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                            .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();


                                    ColumnDataValue dcp_purorder_delivery = new ColumnDataValue();
                                    ColumnDataValue deliveryCondition = new ColumnDataValue();
                                    deliveryCondition.add("EID", DataValues.newString(req.geteId()));
                                    deliveryCondition.add("PURORDERNO", DataValues.newString(data.get("PURORDERNO")));
                                    deliveryCondition.add("ITEM", DataValues.newString(data.get("POITEM")));
                                    deliveryCondition.add("ITEM2", DataValues.newString(data.get("POITEM2")));

                                    double receiveQty = Double.parseDouble(data.get("RECEIVEQTY").toString()) -sum;
                                    //double bookQty = Double.parseDouble(data.get("BOOKQTY").toString()) + Double.parseDouble(data.get("PQTY").toString());

                                    dcp_purorder_delivery.add("RECEIVEQTY", DataValues.newDecimal(receiveQty));
                                    //dcp_purorder_delivery.add("BOOKQTY", DataValues.newDecimal(bookQty));

                                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURORDER_DELIVERY", deliveryCondition, dcp_purorder_delivery)));
                                }

                            }
                            if (null != data.get("RORDERNO") && StringUtils.isNotEmpty(data.get("RORDERNO").toString())) {

                                String rOrderNo = data.get("RORDERNO").toString();
                                String rOrderItem = data.get("RECEIVINGITEM")==null?"":data.get("RECEIVINGITEM").toString();

                                if(Check.NotNull(rOrderNo)&&Check.NotNull(rOrderItem)){
                                    double sum = queryData.stream().filter(x -> rOrderNo.equals(x.get("RORDERNO").toString())
                                                    && rOrderItem.equals(x.get("RECEIVINGITEM").toString())
                                            ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                            .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();

                                    double passSum = queryData.stream().filter(x -> rOrderNo.equals(x.get("RORDERNO").toString())
                                                    && rOrderItem.equals(x.get("RECEIVINGITEM").toString())
                                                    &&"0".equals(x.get("QCSTATUS").toString())
                                            ).map(y -> new BigDecimal(y.get("PQTY").toString()))
                                            .collect(Collectors.summarizingDouble(BigDecimal::doubleValue)).getSum();

                                    ColumnDataValue receivingCondition = new ColumnDataValue();
                                    ColumnDataValue dcp_receiving_detail = new ColumnDataValue();

//                            receivingNoSet.add(data.get("RECEIVINGNO").toString());

                                    receivingCondition.add("EID", DataValues.newString(req.geteId()));
                                    receivingCondition.add("RECEIVINGNO", DataValues.newString(data.get("RORDERNO")));
                                    receivingCondition.add("ITEM", DataValues.newString(data.get("RECEIVINGITEM")));

                                    double already = Double.parseDouble(data.get("RPQTY").toString());//通知单的业务数量
                                    double stockInQty = Double.parseDouble(Check.Null(data.get("STOCKIN_QTY").toString())?"0":data.get("STOCKIN_QTY").toString())
                                            - sum;//加完以后的收货数量

                                    //合格量需要有无需质检更新
                                    double passQty = Double.parseDouble(Check.Null(data.get("PASSQTYYET").toString())?"0":data.get("PASSQTYYET").toString())
                                            - passSum;//加完以后的收货合格量
                                    dcp_receiving_detail.add("STOCKIN_QTY", DataValues.newDecimal(stockInQty));
                                    dcp_receiving_detail.add("PASSQTY", DataValues.newDecimal(passQty));


//                            2.判断收货通知单明细【已收货量-STOCKIN_QTY】>=【通知收货量-PQTY】，则更新行状态=【100-收货完成】；否则更新行状态至【0-待收货】
                                    if (stockInQty >= already) {
                                        dcp_receiving_detail.add("STATUS", DataValues.newDecimal(100));
                                    } else {
                                        dcp_receiving_detail.add("STATUS", DataValues.newDecimal(0));
                                    }

                                    this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_RECEIVING_DETAIL", receivingCondition, dcp_receiving_detail)));


                                }

                            }
                        //}

                        //更新允收量为0
                        ColumnDataValue dcp_purreceive_detail = new ColumnDataValue();
                        ColumnDataValue condition2 = new ColumnDataValue();

                        condition2.add("EID", DataValues.newString(req.geteId()));
                        condition2.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
                        condition2.add("ITEM", DataValues.newString(data.get("ITEM")));

                        dcp_purreceive_detail.add("PASSQTY", DataValues.newString(0));

                        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURRECEIVE_DETAIL", condition2, dcp_purreceive_detail)));


                        dcp_purreceive.add("STATUS", DataValues.newInteger(0));
                        dcp_purreceive.add("CONFIRMBY", DataValues.newString(""));

                        //删除质检档
                        ColumnDataValue condition1 = new ColumnDataValue();
                        condition1.add("EID", DataValues.newString(req.geteId()));
                        condition1.add("SOURCEBILLNO", DataValues.newString(req.getRequest().getBillNo()));

                        this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_QUALITYCHECK", condition1)));

                        //删除批次
                        DelBean db1 = new DelBean("MES_BATCH");
                        db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                        db1.addCondition("BILLNO", new DataValue(req.getRequest().getBillNo(), Types.VARCHAR));
                        this.addProcessData(new DataProcessBean(db1));

                    }
                }
            }
            dcp_purreceive.add("LASTMODIOPID", DataValues.newString(req.getOpNO()));
            dcp_purreceive.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PURRECEIVE", condition, dcp_purreceive)));

        }
        else {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到对应单据！");
        }

        this.doExecuteDataToDB();

        this.pData.clear();//清空数据

        if(mesBatchInfoList.size()>0){
            PosPub.insertIntoMesBatchList(dao, mesBatchInfoList);
        }

        if (OPTYPE_CONFIRM.equals(req.getRequest().getOpType())) {
            //更改收货通知单所有行STATUS
            String exeSql = " UPDATE DCP_RECEIVING oh " +
                    " SET oh.status = ( " +
                    "    SELECT " +
                    "        CASE " +
                    "            WHEN COUNT(*) = 0 THEN 6   " +
                    "            WHEN MIN(ol.status) = 100 AND MAX(ol.status) = 100 THEN 7  " +
                    "            ELSE 6   " +
                    "            END " +
                    "    FROM DCP_RECEIVING_DETAIL ol " +
                    "    WHERE ol.EID=oh.EID and ol.RECEIVINGNO = oh.RECEIVINGNO  " +
                    " ) " +
                    " WHERE EXISTS (   " +
                    "    SELECT 1 " +
                    "    FROM DCP_PURRECEIVE_DETAIL oo " +
                    "    LEFT JOIN DCP_RECEIVING_DETAIL ol on oo.EID=ol.EID and oo.RECEIVINGNO=ol.RECEIVINGNO and oo.RECEIVINGITEM=ol.ITEM  " +
                    "    WHERE ol.EID=oh.EID and ol.RECEIVINGNO = oh.RECEIVINGNO AND oo.BILLNO='" + req.getRequest().getBillNo() + "'" +
                    " )  ";
            ExecBean execBean = DataBeans.getExecBean(
                    exeSql
            );
            addProcessData(new DataProcessBean(execBean));
        }
        else if (OPTYPE_UNCONFIRM.equals(req.getRequest().getOpType())) {
            //更改收货通知单所有行STATUS
            String exeSql = " UPDATE DCP_RECEIVING oh " +
                    " SET oh.status = ( " +
                    "    SELECT " +
                    "        CASE " +
                    "            WHEN COUNT(*) = 0 THEN 6   " +
                    "            WHEN MIN(ol.status) = 100 AND MAX(ol.status) = 100 THEN 7  " +
                    "            ELSE 1   " +
                    "            END " +
                    "    FROM DCP_RECEIVING_DETAIL ol " +
                    "    WHERE ol.EID=oh.EID and ol.RECEIVINGNO = oh.RECEIVINGNO  " +
                    " ) " +
                    " WHERE EXISTS (   " +
                    "    SELECT 1 " +
                    "    FROM DCP_PURRECEIVE_DETAIL oo " +
                    "    LEFT JOIN DCP_RECEIVING_DETAIL ol on oo.EID=ol.EID and oo.RECEIVINGNO=ol.RECEIVINGNO and oo.RECEIVINGITEM=ol.ITEM  " +
                    "    WHERE ol.EID=oh.EID and ol.RECEIVINGNO = oh.RECEIVINGNO AND oo.BILLNO='" + req.getRequest().getBillNo() + "'" +
                    " )  ";
            ExecBean execBean = DataBeans.getExecBean(
                    exeSql
            );
            addProcessData(new DataProcessBean(execBean));
        }


        this.doExecuteDataToDB();


        res.setSuccess(Boolean.TRUE);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurReceiveStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurReceiveStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurReceiveStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_PurReceiveStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurReceiveStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_PurReceiveStatusUpdateReq>() {

        };
    }

    @Override
    protected DCP_PurReceiveStatusUpdateRes getResponseType() {
        return new DCP_PurReceiveStatusUpdateRes();
    }
}
