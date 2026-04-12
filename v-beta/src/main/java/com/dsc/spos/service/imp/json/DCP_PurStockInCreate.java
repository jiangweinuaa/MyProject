package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PriceAdjustCreateReq;
import com.dsc.spos.json.cust.req.DCP_PurOrderCreateReq;
import com.dsc.spos.json.cust.req.DCP_PurStockInCreateReq;
import com.dsc.spos.json.cust.res.DCP_PurStockInCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 接口已作废
 *
 */
@Deprecated
public class DCP_PurStockInCreate extends SPosAdvanceService<DCP_PurStockInCreateReq, DCP_PurStockInCreateRes> {

    private static final String FIX_CHAR_BILL_TYPE_1 = "CGSH";
    private static final String FIX_CHAR_BILL_TYPE_2 = "CGRK";
    private static final String FIX_CHAR_BILL_TYPE_3 = "CGRK";
    private static final String FIX_CHAR_BILL_TYPE_4 = "ZCRK";
    private static final String FIX_CHAR_BILL_TYPE_5 = "CTCK";

    private static final String[] FIX_CHAR_ARRAY = {"", FIX_CHAR_BILL_TYPE_1, FIX_CHAR_BILL_TYPE_2, FIX_CHAR_BILL_TYPE_3, FIX_CHAR_BILL_TYPE_4, FIX_CHAR_BILL_TYPE_5};

    @Override
    protected void processDUID(DCP_PurStockInCreateReq req, DCP_PurStockInCreateRes res) throws Exception {

        try {

            double totPQty = 0;
            double totPurAmt = 0;
            double totPreTaxAmt = 0;
            double totTaxAmt = 0;

            List<DCP_PurStockInCreateReq.Detail> datalist = req.getRequest().getDataList().stream().distinct().collect(Collectors.toList());
            double totCQty = datalist.size();

            for (DCP_PurStockInCreateReq.Detail detail : datalist) {
                double pQty = 0;

                if (null != detail.getMulitiLotsList() && !detail.getMulitiLotsList().isEmpty()) {
                    for (DCP_PurStockInCreateReq.MulitiLosts lots : detail.getMulitiLotsList()) {
                        pQty = pQty + Double.parseDouble(lots.getPQty());
                    }
                    if (pQty != Double.parseDouble(detail.getPQty())) {
                        throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400,
                                "项次" + detail.getItem() + "商品" + detail.getPluNo() + "入库量与分批入库量之和不等，请检查！"
                        );
                    }
                }

                pQty = Double.parseDouble(StringUtils.toString(detail.getPQty(),"0"));
                totPQty = totPQty + pQty;
                totPurAmt = totPurAmt + Double.parseDouble(StringUtils.toString(detail.getPurAmt(),"0"));
                totPreTaxAmt = totPreTaxAmt + Double.parseDouble(StringUtils.toString(detail.getPreTaxAmt(),"0"));
                totTaxAmt = totTaxAmt + Double.parseDouble(StringUtils.toString(detail.getTaxAmt(),"0"));
            }

            int billType = Integer.parseInt(req.getRequest().getBillType());
            String ticketNo = getOrderNO(req,FIX_CHAR_ARRAY[billType]);
            String lastmoditime = DateFormatUtils.getNowDateTime();

            ColumnDataValue dcp_purStockIn = new ColumnDataValue();

            dcp_purStockIn.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
            dcp_purStockIn.add("EID", DataValues.newString(req.geteId()));
            dcp_purStockIn.add("BDATE", DataValues.newDate(req.getRequest().getBDate()));
            dcp_purStockIn.add("ACCOUNT_DATE", DataValues.newDate(null));
            dcp_purStockIn.add("PSTOCKINNO", DataValues.newString(ticketNo));
            dcp_purStockIn.add("BILLTYPE", DataValues.newString(req.getRequest().getBillType()));

            dcp_purStockIn.add("SOURCEBILLNO", DataValues.newString(req.getRequest().getSourceBillNo()));
            dcp_purStockIn.add("SUPPLIER", DataValues.newString(req.getRequest().getSupplierNo()));

            dcp_purStockIn.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
            dcp_purStockIn.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
            dcp_purStockIn.add("PURORGNO", DataValues.newString(req.getRequest().getPurOrgNo()));

            dcp_purStockIn.add("TOT_CQTY", DataValues.newDecimal(totCQty));
            dcp_purStockIn.add("TOT_PQTY", DataValues.newDecimal(totPQty));
            dcp_purStockIn.add("TOT_PURAMT", DataValues.newDecimal(totPurAmt));
            dcp_purStockIn.add("TOT_PRETAXAMT", DataValues.newDecimal(totPreTaxAmt));
            dcp_purStockIn.add("TOT_TAXAMT", DataValues.newDecimal(totTaxAmt));

            dcp_purStockIn.add("PAYTYPE", DataValues.newString(req.getRequest().getPayType()));
            dcp_purStockIn.add("PAYORGNO", DataValues.newString(req.getRequest().getPayOrgNo()));
            dcp_purStockIn.add("BILLDATENO", DataValues.newString(req.getRequest().getBillDateNo()));

            dcp_purStockIn.add("PAYDATENO", DataValues.newString(req.getRequest().getPayDateNo()));
            dcp_purStockIn.add("INVOICECODE", DataValues.newString(req.getRequest().getInvoiceCode()));
            dcp_purStockIn.add("CURRENCY", DataValues.newString(req.getRequest().getCurrency()));
            dcp_purStockIn.add("DELIVERYFEE", DataValues.newString(req.getRequest().getDeliveryFee()));

            dcp_purStockIn.add("STATUS", DataValues.newString("0"));
            dcp_purStockIn.add("MEMO", DataValues.newString(req.getRequest().getMemo()));

            dcp_purStockIn.add("OWNOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_purStockIn.add("OWNDEPTID", DataValues.newString(req.getDepartmentNo()));
            dcp_purStockIn.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_purStockIn.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
            dcp_purStockIn.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcp_purStockIn.add("CREATETIME", DataValues.newDate(lastmoditime));
            dcp_purStockIn.add("LASTMODITIME", DataValues.newDate(lastmoditime));

            InsBean ib1 = DataBeans.getInsBean("DCP_PURSTOCKIN", dcp_purStockIn);
            this.addProcessData(new DataProcessBean(ib1));

            for (DCP_PurStockInCreateReq.Detail detail : req.getRequest().getDataList()) {
                ColumnDataValue dcp_purStockIn_detail = new ColumnDataValue();

                dcp_purStockIn_detail.add("EID", DataValues.newString(req.geteId()));
                dcp_purStockIn_detail.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
                dcp_purStockIn_detail.add("PSTOCKINNO", DataValues.newString(ticketNo));
                dcp_purStockIn_detail.add("ITEM", DataValues.newInteger(detail.getItem()));
                dcp_purStockIn_detail.add("RECEIVINGNO", DataValues.newString(req.getRequest().getReceivingNo()));
                dcp_purStockIn_detail.add("RECEIVINGITEM", DataValues.newString(detail.getOItem()));
                dcp_purStockIn_detail.add("PURORDERNO", DataValues.newString(detail.getPurOrderNo()));
                dcp_purStockIn_detail.add("POITEM", DataValues.newInteger(detail.getPoItem()));
                dcp_purStockIn_detail.add("POITEM2", DataValues.newInteger(detail.getPoItem2()));
                dcp_purStockIn_detail.add("OITEM", DataValues.newInteger(detail.getOItem()));
                dcp_purStockIn_detail.add("PLUNO", DataValues.newString(detail.getPluNo()));
                dcp_purStockIn_detail.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
//                dcp_purStockIn_detail.add("CATEGORY",DataValues.newString(detail.get()));
                dcp_purStockIn_detail.add("ISFREE", DataValues.newString(detail.getIsFree()));
                dcp_purStockIn_detail.add("WAREHOUSE", DataValues.newString(detail.getWareHouse()));
                dcp_purStockIn_detail.add("PUNIT", DataValues.newString(detail.getPUnit()));
                dcp_purStockIn_detail.add("PQTY", DataValues.newDecimal(detail.getPQty()));

                dcp_purStockIn_detail.add("TAXCODE", DataValues.newString(detail.getTaxCode()));
                dcp_purStockIn_detail.add("TAXRATE", DataValues.newDecimal(detail.getTaxRate()));
                dcp_purStockIn_detail.add("INCLTAX", DataValues.newString(detail.getInclTax()));
                dcp_purStockIn_detail.add("PURPRICE", DataValues.newDecimal(detail.getPurPrice()));
                dcp_purStockIn_detail.add("PURAMT", DataValues.newDecimal(detail.getPurAmt()));

                dcp_purStockIn_detail.add("PRETAXAMT", DataValues.newDecimal(detail.getPreTaxAmt()));
                dcp_purStockIn_detail.add("TAXAMT", DataValues.newDecimal(detail.getTaxAmt()));
                dcp_purStockIn_detail.add("IS_QUALITYCHECK", DataValues.newString(detail.getIsQc()));

                dcp_purStockIn_detail.add("MEMO", DataValues.newString(detail.getMemo()));

//                dcp_purStockIn_detail.add("PURTEMPLATENO", DataValues.newString(req.getRequest()));


                if (null == detail.getMulitiLotsList() || detail.getMulitiLotsList().isEmpty()) {
                    ColumnDataValue dcp_purStockIn_lots = new ColumnDataValue();


                } else {
                    if (detail.getMulitiLotsList().size() == 1) {
                        DCP_PurStockInCreateReq.MulitiLosts lots = detail.getMulitiLotsList().get(0);
                        dcp_purStockIn_detail.add("LOCATION", DataValues.newString(lots.getLocation()));
                        dcp_purStockIn_detail.add("BATCHNO", DataValues.newString(lots.getBatchNo()));
                        dcp_purStockIn_detail.add("PROD_DATE", DataValues.newDate(lots.getProdDate()));
                        dcp_purStockIn_detail.add("EXP_DATE", DataValues.newDate(lots.getExpDate()));
                    }

                    for (DCP_PurStockInCreateReq.MulitiLosts lots : detail.getMulitiLotsList()) {
                        ColumnDataValue dcp_purStockIn_lots = new ColumnDataValue();

                        dcp_purStockIn_lots.add("EID", DataValues.newString(req.geteId()));
                        dcp_purStockIn_lots.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
                        dcp_purStockIn_lots.add("PSTOCKINNO", DataValues.newString(ticketNo));
                        dcp_purStockIn_lots.add("ITEM", DataValues.newInteger(detail.getItem()));
                        dcp_purStockIn_lots.add("ITEM2", DataValues.newInteger(lots.getItem2()));
                        dcp_purStockIn_lots.add("PLUNO", DataValues.newString(detail.getPluNo()));
                        dcp_purStockIn_lots.add("FEATURENO", DataValues.newString(detail.getFeatureNo()));
                        dcp_purStockIn_lots.add("WAREHOUSE", DataValues.newString(detail.getWareHouse()));
                        dcp_purStockIn_lots.add("LOCATION", DataValues.newString(lots.getLocation()));
                        dcp_purStockIn_lots.add("BATCHNO", DataValues.newString(lots.getBatchNo()));
                        dcp_purStockIn_lots.add("PROD_DATE", DataValues.newDate(lots.getProdDate()));
                        dcp_purStockIn_lots.add("EXP_DATE", DataValues.newDate(lots.getExpDate()));
                        dcp_purStockIn_lots.add("PUNIT", DataValues.newString(detail.getPUnit()));
                        dcp_purStockIn_lots.add("PQTY", DataValues.newDecimal(lots.getPQty()));

                        InsBean ib3 = DataBeans.getInsBean("DCP_PURSTOCKIN_LOTS", dcp_purStockIn_lots);
                        this.addProcessData(new DataProcessBean(ib3));
                    }

                }

                InsBean ib2 = DataBeans.getInsBean("DCP_PURSTOCKIN_DETAIL", dcp_purStockIn_detail);
                this.addProcessData(new DataProcessBean(ib2));


            }



            this.doExecuteDataToDB();
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");

        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PurStockInCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PurStockInCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PurStockInCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PurStockInCreateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_PurStockInCreateReq> getRequestType() {
        return new TypeToken<DCP_PurStockInCreateReq>() {

        };
    }

    @Override
    protected DCP_PurStockInCreateRes getResponseType() {
        return new DCP_PurStockInCreateRes();
    }
}
