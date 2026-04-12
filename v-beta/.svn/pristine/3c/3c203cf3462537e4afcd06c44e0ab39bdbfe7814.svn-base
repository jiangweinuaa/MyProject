package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_ToDoListQueryNewReq;
import com.dsc.spos.json.cust.res.DCP_ToDoListQueryNewRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.DateFormatUtils;
import com.dsc.spos.utils.StringUtils;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DCP_ToDoListQueryNew extends SPosBasicService<DCP_ToDoListQueryNewReq, DCP_ToDoListQueryNewRes> {
    @Override
    protected boolean isVerifyFail(DCP_ToDoListQueryNewReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_ToDoListQueryNewReq> getRequestType() {
        return new TypeToken<DCP_ToDoListQueryNewReq>() {
        };
    }

    @Override
    protected DCP_ToDoListQueryNewRes getResponseType() {
        return new DCP_ToDoListQueryNewRes();
    }

    @Override
    protected DCP_ToDoListQueryNewRes processJson(DCP_ToDoListQueryNewReq req) throws Exception {

        DCP_ToDoListQueryNewRes res = this.getResponseType();

        List<Map<String, Object>> getDatas = this.doQueryData(getQuerySql(req), null);

        res.setDatas(new ArrayList<>());
        String beginPlainDate = DateFormatUtils.getPlainDate(DateFormatUtils.addDay(DateFormatUtils.getNowDate(), -7));
        String endPlainDate = DateFormatUtils.getNowPlainDate();


        if (CollectionUtils.isNotEmpty(getDatas)) {
            for (Map<String, Object> data : getDatas) {
                DCP_ToDoListQueryNewRes.Data oneData = res.new Data();
                res.getDatas().add(oneData);

                String proName = data.get("PRONAME").toString();

                oneData.setProName(proName);
                oneData.setQty(data.get("QTY").toString());
                oneData.setStatus(data.get("STATUS").toString());

                DCP_ToDoListQueryNewRes.Extend extend = res.new Extend();
                oneData.setExtend(extend);

                ProName name = ProName.getByName(proName);

                if (null != name) {
                    switch (name) {
                        case ReceivingDetail:
                            extend.setStatus("6");
                            extend.setDocType(new ArrayList<>());
                            extend.getDocType().add("3");
                            extend.setDateType("receiptDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case PurchaseReceiveIn:
                            extend.setStatus("6");
                            extend.setDocType(new ArrayList<>());
                            extend.getDocType().add("4");
                            extend.setDateType("receiptDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case PurStockIn:
                            extend.setStatus("1");
                            extend.setDateType("bDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setSearchScope("1");
                            break;
                        case delivery_receive:
                            extend.setStatus("6");
                            extend.setDocType(new ArrayList<>());
                            extend.getDocType().add("0");
                            extend.setDateType("receiptDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case BackInfoStash:
                            extend.setStatus("6");
                            extend.setDocType(new ArrayList<>());
                            extend.getDocType().add("5");
                            extend.setDateType("receiptDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case stock_in:
                            extend.setStatus("6");
                            extend.setDocType(new ArrayList<>());
                            extend.getDocType().add("1");
                            extend.setDateType("receiptDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case MoveIntoStorage:
                            extend.setStatus("6");
                            extend.setDocType(new ArrayList<>());
                            extend.getDocType().add("7");
                            extend.setDateType("receiptDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case returnStorageIn:
                            extend.setStatus("6");
                            extend.setDocType(new ArrayList<>());
                            extend.getDocType().add("6");
                            extend.setDateType("receiptDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case PurStockOut:
                            extend.setStatus("1");
                            extend.setBillType("1");
                            extend.setDateType("deliveryDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case SalesDelivery:
                            extend.setStatus("1");
                            extend.setBillType("2");
                            extend.setDateType("deliveryDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case Distribution:
                            extend.setStatus("1");
                            extend.setBillType("3");
                            extend.setDateType("deliveryDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case back_out:
                            extend.setStatus("1");
                            extend.setBillType("4");
                            extend.setDateType("deliveryDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case stock_out:
                            extend.setStatus("1");
                            extend.setBillType("5");
                            extend.setDateType("deliveryDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case RemoveWarehouse:
                            extend.setStatus("1");
                            extend.setBillType("6");
                            extend.setDateType("deliveryDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setGetType("1");
                            break;
                        case RequestReview:
                            extend.setStatus("2");
                            extend.setDateType("rDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            extend.setSupplierType("FACTOERY");
                            extend.setQueryType("1");
                            break;
                        case DemandToPO:
                            extend.setIsCheckRestrictGroup("Y");
                            extend.setDateType("rDate");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            break;
                        case TransApplyPro:
                            extend.setStatus("1");
                            extend.setDateType("bDate");
                            extend.setGetType("1");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            break;
                        case ReturnApplyPro:
                            extend.setReturnType("1");
                            extend.setApproveStatus("0");
                            extend.setDateType("bDate");
                            extend.setGetType("1");
                            setExtendDate(extend, data, true, beginPlainDate, endPlainDate);
                            break;
                    }

                }

            }
        }

        res.setSuccess(Boolean.TRUE);

        return res;
    }

    protected void setExtendDate(DCP_ToDoListQueryNewRes.Extend extend, Map<String, Object> data, boolean showDate, String beginDate, String endDate) {
        extend.setBeginDate(data.get("MINDATE").toString());
        extend.setEndDate(data.get("MAXDATE").toString());
        if (showDate && StringUtils.isEmpty(extend.getBeginDate())) {
            extend.setBeginDate(beginDate);
        }
        if (showDate && StringUtils.isEmpty(extend.getEndDate())) {
            extend.setEndDate(endDate);
        }
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    protected String getCountSql(ProName proName, String tableName, String dateField, String status, String condition) throws Exception {

        String mWhere = " WHERE 1=1 ";

        if (StringUtils.isNotEmpty(status)
                && proName != ProName.DemandToPO) {
            mWhere += " AND a.status='" + status + "'";
        }

        mWhere += condition;

        StringBuilder querySql = new StringBuilder();

        querySql.append(" SELECT ")
                .append(" '").append(proName.getName()).append("' as PRONAME,Count(*) as QTY,")
                .append(" '").append(status).append("' AS STATUS ");

        if (StringUtils.isNotEmpty(dateField)) {
            querySql.append(" ,MAX(a.").append(dateField).append(") MAXDATE ")
                    .append(" ,MIN(a.").append(dateField).append(") MINDATE");
        } else {
            querySql.append(" ,cast('' as nvarchar2(8)) MAXDATE ")
                    .append(" ,cast('' as nvarchar2(8)) MINDATE");
        }
        querySql.append(" FROM ").append(tableName).append(" a ")
                .append(mWhere);

        return querySql.toString();
    }


    @Override
    protected String getQuerySql(DCP_ToDoListQueryNewReq req) throws Exception {

        String beginPlainDate = DateFormatUtils.getPlainDate(DateFormatUtils.addDay(DateFormatUtils.getNowDate(), -7));
        String endPlainDate = DateFormatUtils.getNowPlainDate();

        String orgNo = req.getOrganizationNO();

        StringBuilder query = new StringBuilder();
        StringBuilder querySql = new StringBuilder();

        String condition = "";
        String tableName = "";
        String status = "";
        String dateField = "";
        for (ProName value : ProName.values()) {
            switch (value) {
                case ReceivingDetail:
                    condition = " AND a.RECEIPTORGNO='" + orgNo + "' AND a.DOC_TYPE='3' and a.RECEIPTDATE>='" + beginPlainDate + "' AND a.RECEIPTDATE<='" + endPlainDate + "'";
                    tableName = "DCP_RECEIVING";
                    status = "6";
                    dateField = "RECEIPTDATE";
                    break;
                case PurchaseReceiveIn:
                    condition = " AND a.RECEIPTORGNO='" + orgNo + "' and a.DOC_TYPE='4' and a.RECEIPTDATE>='" + beginPlainDate + "' AND a.RECEIPTDATE<='" + endPlainDate + "'";
                    tableName = "DCP_RECEIVING";
                    status = "6";
                    dateField = "RECEIPTDATE";
                    break;
                case PurStockIn:
                    condition = " AND a.ORGANIZATIONNO='" + orgNo + "' and NOT EXISTS ( SELECT 1 FROM DCP_RECEIVING_DETAIL b WHERE a.EID=b.EID and a.BILLNO =b.OFNO ) " +
                            "AND EXISTS ( SELECT c.EID,c.BILLNO FROM DCP_PURRECEIVE_DETAIL c " +
                            "                                   WHERE a.EID=c.EID and a.BILLNO=c.BILLNO " +
                            "                                   GROUP BY c.EID,c.BILLNO " +
                            "                                   HAVING SUM(c.PASSQTY-c.STOCKINQTY)>0  )";
                    tableName = "DCP_PURRECEIVE";
                    status = "1";
                    dateField = "BDATE";
                    break;
                case delivery_receive:
                    condition = " AND a.RECEIPTORGNO='" + orgNo + "' AND a.DOC_TYPE='0' and a.RECEIPTDATE>='" + beginPlainDate + "' AND a.RECEIPTDATE<='" + endPlainDate + "'";
                    tableName = "DCP_RECEIVING";
                    status = "6";
                    dateField = "RECEIPTDATE";
                    break;
                case BackInfoStash:
                    condition = " AND a.RECEIPTORGNO='" + orgNo + "' AND a.DOC_TYPE='5' and a.RECEIPTDATE>='" + beginPlainDate + "' AND a.RECEIPTDATE<='" + endPlainDate + "'";
                    tableName = "DCP_RECEIVING";
                    status = "6";
                    dateField = "RECEIPTDATE";
                    break;
                case stock_in:
                    condition = " AND a.RECEIPTORGNO='" + orgNo + "' AND a.DOC_TYPE='1' and a.RECEIPTDATE>='" + beginPlainDate + "' AND a.RECEIPTDATE<='" + endPlainDate + "'";
                    tableName = "DCP_RECEIVING";
                    status = "6";
                    dateField = "RECEIPTDATE";
                    break;
                case MoveIntoStorage:
                    condition = " AND a.RECEIPTORGNO='" + orgNo + "' AND a.DOC_TYPE='7' and a.RECEIPTDATE>='" + beginPlainDate + "' AND a.RECEIPTDATE<='" + endPlainDate + "'";
                    tableName = "DCP_RECEIVING";
                    status = "6";
                    dateField = "RECEIPTDATE";
                    break;
                case returnStorageIn:
                    condition = " AND a.RECEIPTORGNO='" + orgNo + "' AND a.DOC_TYPE='6' and a.RECEIPTDATE>='" + beginPlainDate + "' AND a.RECEIPTDATE<='" + endPlainDate + "'";
                    tableName = "DCP_RECEIVING";
                    status = "6";
                    dateField = "RECEIPTDATE";
                    break;
                case PurStockOut:
                    condition = " AND a.DELIVERORGNO='" + orgNo + "' AND a.billType='1' and a.deliveryDate>='" + beginPlainDate + "' AND a.deliveryDate<='" + endPlainDate + "'";
                    tableName = "DCP_STOCKOUTNOTICE";
                    status = "1";
                    dateField = "DELIVERYDATE";
                    break;
                case SalesDelivery:
                    condition = " AND a.DELIVERORGNO='" + orgNo + "' AND a.billType='2' and a.deliveryDate>='" + beginPlainDate + "' AND a.deliveryDate<='" + endPlainDate + "'";
                    tableName = "DCP_STOCKOUTNOTICE";
                    status = "1";
                    dateField = "DELIVERYDATE";
                    break;
                case Distribution:
                    condition = " AND a.DELIVERORGNO='" + orgNo + "' AND a.billType='3' and a.deliveryDate>='" + beginPlainDate + "' AND a.deliveryDate<='" + endPlainDate + "'";
                    tableName = "DCP_STOCKOUTNOTICE";
                    status = "1";
                    dateField = "DELIVERYDATE";
                    break;
                case back_out:
                    condition = " AND a.DELIVERORGNO='" + orgNo + "' AND a.billType='4' and a.deliveryDate>='" + beginPlainDate + "' AND a.deliveryDate<='" + endPlainDate + "'";
                    tableName = "DCP_STOCKOUTNOTICE";
                    status = "1";
                    dateField = "DELIVERYDATE";
                    break;
                case stock_out:
                    condition = " AND a.DELIVERORGNO='" + orgNo + "' AND a.billType='5' and a.deliveryDate>='" + beginPlainDate + "' AND a.deliveryDate<='" + endPlainDate + "'";
                    tableName = "DCP_STOCKOUTNOTICE";
                    status = "1";
                    dateField = "DELIVERYDATE";
                    break;
                case RemoveWarehouse:
                    condition = " AND a.DELIVERORGNO='" + orgNo + "' AND a.billType='6' and a.deliveryDate>='" + beginPlainDate + "' AND a.deliveryDate<='" + endPlainDate + "'";
                    tableName = "DCP_STOCKOUTNOTICE";
                    status = "1";
                    dateField = "DELIVERYDATE";
                    break;
                case RequestReview:
                    condition = " AND a.ORGANIZATIONNO='" + orgNo + "' AND a.SUPPLIERTYPE='FACTORY' ";
                    tableName = "DCP_PORDER";
                    status = "2";
                    dateField = "RDATE";
                    break;
                case DemandToPO:
                    condition = " AND a.SUPPLIER='" + orgNo + "' AND a.SUPPLIERTYPE='SUPPLIER' " +
                            " AND EXISTS ( SELECT 1 FROM DCP_PORDER_DETAIL b " +
                            "  left join DCP_PORDER c on b.SHOPID=c.SHOPID and b.PORDERNO=c.PORDERNO and b.EID=c.EID and b.ORGANIZATIONNO=c.ORGANIZATIONNO   " +
                            "  WHERE NVL(b.DETAIL_STATUS,0)=0 and b.eid=a.eid and b.porderno=a.orderno and b.item=a.item AND c.STATUS='6') ";
                    tableName = "DCP_DEMAND";
                    status = "6";
                    dateField = "RDATE";
                    break;
                case TransApplyPro:
                    condition = " AND a.APPROVEORGNO='" + orgNo + "' ";
                    tableName = "DCP_TRANSAPPLY";
                    status = "1";
                    dateField = "BDATE";
                    break;
                case ReturnApplyPro:
                    condition = " AND EXISTS ( SELECT 1 FROM DCP_RETURNAPPLY_detail b WHERE b.APPROVEORGNO='" + orgNo + "' " +
                            " AND b.APPROVESTATUS='0' and a.EID=b.EID and a.BILLNO=b.BILLNO   )";
                    tableName = "DCP_RETURNAPPLY";
                    status = "1";
                    dateField = "BDATE";
                    break;

            }

            if (query.length() > 0) {
                query.append(" UNION ALL ");
                query.append(getCountSql(value, tableName, dateField, status, condition));
            } else {
                query.append(getCountSql(value, tableName, dateField, status, condition));
            }

        }
        querySql.append(" select * from ( ").append(query)
                .append(") a");


        return querySql.toString();

//        query.append("select proName,qty,status from (")
//                //采购收货-ReceivingDetail
//                .append(getCountSql("ReceivingDetail", "DCP_RECEIVING", "6", receivingDetailCondition))
//                //采购收货入库PurchaseReceiveIn
//                .append(" UNION ALL ")
//                .append(getCountSql("PurchaseReceiveIn", "DCP_RECEIVING", "6", purchaseReceiveInCondition))
//                //采购入库-PurStockIn
//                .append(" UNION ALL ")
//                .append(getCountSql("PurStockIn", "DCP_PURRECEIVE", "1", purStockInCondition))
//                //配货入库delivery_receive
//                .append(" UNION ALL ")
//                .append(getCountSql("delivery_receive", "DCP_RECEIVING", "6", deliveryCondition))
//                //退配入库BackInfoStash
//                .append(" UNION ALL ")
//                .append(getCountSql("BackInfoStash", "DCP_RECEIVING", "6", backInfoCondition))
//                //调拨入库stock_in
//                .append(" UNION ALL ")
//                .append(getCountSql("stock_in", "DCP_RECEIVING", "6", stockInCondition))
//                //移仓入库MoveIntoStorage
//                .append(" UNION ALL ")
//                .append(getCountSql("MoveIntoStorage", "DCP_RECEIVING", "6", moveIntoStorageCondition))
//
//                //销退入库returnStorageIn
//                .append(" UNION ALL ")
//                .append(getCountSql("returnStorageIn", "DCP_RECEIVING", "6", returnStorageInCondition))
//                //采退出库-PurStockOut
//                .append(" UNION ALL ")
//                .append(getCountSql("PurStockOut", "DCP_STOCKOUTNOTICE", "", purStockOutCondition))
//                //销货出库-SalesDelivery
//                .append(" UNION ALL ")
//                .append(getCountSql("SalesDelivery", "DCP_STOCKOUTNOTICE", "", salesDeliveryCondition))
//                //配货出库-Distribution
//                .append(" UNION ALL ")
//                .append(getCountSql("Distribution", "DCP_STOCKOUTNOTICE", "", distributionCondition))
//                //退配出库-back_out
//                .append(" UNION ALL ")
//                .append(getCountSql("back_out", "DCP_STOCKOUTNOTICE", "", backOutCondition))
//                //调拨出库-stock_out
//                .append(" UNION ALL ")
//                .append(getCountSql("stock_out", "DCP_STOCKOUTNOTICE", "", stockOutCondition))
//                //移仓出库-RemoveWarehouse
//                .append(" UNION ALL ")
//                .append(getCountSql("RemoveWarehouse", "DCP_STOCKOUTNOTICE", "", removeWarehouseCondition))
//
//                //要货督审-RequestReview
//                .append(" UNION ALL ")
//                .append(getCountSql("RequestReview", "DCP_PORDER", "2", requestReviewCondition))
//                //要货转采-DemandToPO
//                .append(" UNION ALL ")
//                .append(getCountSql("DemandToPO", "DCP_DEMAND", "6", demandToPOCondition))
//                //调拨审批-TransApplyPro
//                .append(" UNION ALL ")
//                .append(getCountSql("TransApplyPro", "DCP_TRANSAPPLY", "1", transApplyProCondition))
//                //退货审批-ReturnApplyPro
//                .append(" UNION ALL ")
//                .append(getCountSql("ReturnApplyPro", "DCP_RETURNAPPLY", "1", returnApplyProCondition))
//                .append(") a");
    }

    @Getter
    public enum ProName {

        ReceivingDetail("ReceivingDetail"),
        PurchaseReceiveIn("PurchaseReceiveIn"),
        PurStockIn("PurStockIn"),
        delivery_receive("delivery_receive"),
        BackInfoStash("BackInfoStash"),
        stock_in("stock_in"),
        MoveIntoStorage("MoveIntoStorage"),
        returnStorageIn("returnStorageIn"),
        PurStockOut("PurStockOut"),
        SalesDelivery("SalesDelivery"),
        Distribution("Distribution"),
        back_out("back_out"),
        stock_out("stock_out"),
        RemoveWarehouse("RemoveWarehouse"),
        RequestReview("RequestReview"),
        DemandToPO("DemandToPO"),
        TransApplyPro("TransApplyPro"),
        ReturnApplyPro("ReturnApplyPro");

        private final String name;


        ProName(String name) {
            this.name = name;
        }

        public static ProName getByName(String name) {
            for (ProName value : values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return null;
        }

    }


}
