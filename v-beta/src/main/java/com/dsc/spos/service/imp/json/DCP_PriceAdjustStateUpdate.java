package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PriceAdjustStateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PriceAdjustStateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.date.DatePeriod;
import com.dsc.spos.utils.date.DatePeriodSplitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DCP_PriceAdjustStateUpdate extends SPosAdvanceService<DCP_PriceAdjustStateUpdateReq, DCP_PriceAdjustStateUpdateRes> {

    //            枚举: confirm：审核,cancel：作废
    private static final String TYPE_CONFIRM = "confirm";
    private static final String TYPE_CANCEL = "cancel";


    @Override
    protected void processDUID(DCP_PriceAdjustStateUpdateReq req, DCP_PriceAdjustStateUpdateRes res) throws Exception {

        try {

            String eid = req.geteId();
            String billNO = req.getRequest().getBillNo();

            String query = " SELECT a.BTYPE,a.EFFECTIVEDATE,a.INVALIDDATE," +
                    " a.UPDATE_DEFPURPRICE,a.SUPPLIER, a.TEMPLATENO, a.STATUS," +
                    " b.BILLNO,b.ITEM, b.PLUNO,b.PLUBARCODE,b.EID,b.PRICEUNIT,b.PURPRICETYPE, " +
                    " b.PURPRICE,b.PRICE,b.MINPRICE,a.UPDATE_DEFPURPRICE FROM DCP_PRICEADJUST a " +
                    " LEFT JOIN DCP_PRICEADJUST_DETAIL b ON a.EID=b.EID AND a.BILLNO=b.BILLNO  " +
                    " WHERE a.EID='%s' AND a.BILLNO='%s' ";
            query = String.format(query, eid, billNO);
            List<Map<String, Object>> data = this.doQueryData(query, null);
            Map<String, Object> bill = data.get(0);

            if (!StringUtils.equals("0", bill.get("STATUS").toString())) {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("单据状态非【0-新建】不可更改！");
                return;
            }

            String templateNo = bill.get("TEMPLATENO").toString();

            String lastModifyTime = DateFormatUtils.getNowDateTime();

            String oprType = req.getRequest().getOprType();
            String bType = bill.get("BTYPE").toString();
            ColumnDataValue condition = new ColumnDataValue();
            condition.add("EID", DataValues.newString(req.geteId()));
            condition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));

//            0-新建 1-已确认 2-已作废
            ColumnDataValue dcpPriceAdjust = new ColumnDataValue();
            if (StringUtils.equals(TYPE_CONFIRM, oprType)) {
                dcpPriceAdjust.add("CONFIRMBY", DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjust.add("CONFIRMTIME", DataValues.newDate(lastModifyTime));

                dcpPriceAdjust.add("STATUS", DataValues.newInteger(1));

                //审核按单据详情抛转成任务计划
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PRICEADJUSTPLAN", condition)));//删除任务单
                int i = 1;
                for (Map<String, Object> billDetail : data) {
                    ColumnDataValue dcpPriceAdjustPlan = new ColumnDataValue();
                    dcpPriceAdjustPlan.add("EID", DataValues.newString(billDetail.get("EID")));
                    dcpPriceAdjustPlan.add("ADTYPE", DataValues.newInteger(billDetail.get("BTYPE")));
                    dcpPriceAdjustPlan.add("TEMPLATENO", DataValues.newString(billDetail.get("TEMPLATENO")));
                    dcpPriceAdjustPlan.add("BILLNO", DataValues.newString(billDetail.get("BILLNO")));
                    dcpPriceAdjustPlan.add("ITEM", DataValues.newInteger(i++));
                    dcpPriceAdjustPlan.add("PLUBARCODE", DataValues.newString(billDetail.get("PLUBARCODE")));
                    dcpPriceAdjustPlan.add("PLUNO", DataValues.newString(billDetail.get("PLUNO")));
                    dcpPriceAdjustPlan.add("PRICEUNIT", DataValues.newString(billDetail.get("PRICEUNIT")));
                    dcpPriceAdjustPlan.add("PURPRICETYPE", DataValues.newString(billDetail.get("PURPRICETYPE")));

                    dcpPriceAdjustPlan.add("PURPRICE", DataValues.newString(billDetail.get("PURPRICE")));
                    dcpPriceAdjustPlan.add("PRICE", DataValues.newString(billDetail.get("PRICE")));
                    dcpPriceAdjustPlan.add("UPDATE_DEFPURPRICE", DataValues.newString(billDetail.get("UPDATE_DEFPURPRICE")));
                    dcpPriceAdjustPlan.add("UPDATE_DEFPRICE", DataValues.newString(billDetail.get("ISUPDATESDPRICE")));
                    dcpPriceAdjustPlan.add("SUPPLIER", DataValues.newString(billDetail.get("SUPPLIER")));

                    dcpPriceAdjustPlan.add("BEGINDATE", DataValues.newDate(billDetail.get("EFFECTIVEDATE")));
                    dcpPriceAdjustPlan.add("ENDDATE", DataValues.newDate(billDetail.get("INVALIDDATE")));

                    dcpPriceAdjustPlan.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
                    dcpPriceAdjustPlan.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
                    dcpPriceAdjustPlan.add("CONFIRMBY", DataValues.newString(req.getEmployeeNo()));
                    dcpPriceAdjustPlan.add("CONFIRMTIME", DataValues.newDate(lastModifyTime));

                    if ("2".equals(bType)) {
                        dcpPriceAdjustPlan.add("STATUS", DataValues.newInteger(4));
                    } else {
                        dcpPriceAdjustPlan.add("STATUS", DataValues.newInteger(0));
                    }

                    dcpPriceAdjustPlan.add("MEMO", DataValues.newString("由价格调整审核抛转生成"));

                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PRICEADJUSTPLAN", dcpPriceAdjustPlan)));

                    //如果是2的话直接生成调价不用等调价任务执行
                    if ("2".equals(bType)) {
                        //查询当前已经存在数据
                        List<Map<String, Object>> existed = this.doQueryData(
                                "SELECT * FROM DCP_SALEPRICETEMPLATE_PRICE WHERE EID='" + eid + "'" + " AND TEMPLATEID='" + templateNo + "'"
                                , null);

                        String maxItemSql = "select MAX(ITEM) MAXITEM from dcp_salepricetemplate_price where eid ='" + eid + "' and templateid='" + templateNo + "' ";
                        List<Map<String, Object>> getDataMaxItem = this.doQueryData(maxItemSql, null);

                        long maxItem = 0;
                        if (getDataMaxItem == null || getDataMaxItem.isEmpty()) {
                            maxItem = 0;
                        } else {
                            String sMax = getDataMaxItem.get(0).get("MAXITEM").toString();
                            if (!PosPub.isNumeric(sMax)) {
                                maxItem = 0;
                            } else {
                                maxItem = Long.parseLong(sMax);
                            }
                        }

                        String pluNo = billDetail.get("PLUNO").toString();
                        String beginDate = billDetail.get("EFFECTIVEDATE").toString();
                        String endDate = billDetail.get("INVALIDDATE").toString();

                        List<DatePeriod> periods = getDatePeriod(req.geteId(), existed, pluNo, beginDate, endDate);
                        for (DatePeriod period : periods) {
                            ColumnDataValue dcp_salePriceTemplate_price = new ColumnDataValue();
                            ColumnDataValue conditionPrice = new ColumnDataValue();
                            if (period.getId() <= 0) {
                                maxItem++;

                                dcp_salePriceTemplate_price.add("EID", DataValues.newString(eid));
                                dcp_salePriceTemplate_price.add("TEMPLATEID", DataValues.newString(templateNo));
                                dcp_salePriceTemplate_price.add("ITEM", DataValues.newInteger(maxItem));
                                dcp_salePriceTemplate_price.add("PLUNO", DataValues.newString(pluNo));
                                dcp_salePriceTemplate_price.add("UNIT", DataValues.newString(billDetail.get("PRICEUNIT")));
                                dcp_salePriceTemplate_price.add("ISDISCOUNT", DataValues.newString("Y"));
                                dcp_salePriceTemplate_price.add("ISPROM", DataValues.newString("Y"));
                                dcp_salePriceTemplate_price.add("BEGINDATE", DataValues.newDate(DateFormatUtils.format(period.getStartDate())));
                                dcp_salePriceTemplate_price.add("ENDDATE", DataValues.newDate(DateFormatUtils.format(period.getEndDate())));

                                dcp_salePriceTemplate_price.add("STATUS", DataValues.newInteger(100));

                                String price = billDetail.get("PRICE").toString();
                                String minPrice = billDetail.get("MINPRICE").toString();
                                if (!PosPub.isNumericType(price)) {
                                    price = "0";
                                }
                                if (period.getId() < 0) { //小于0时需要查询原单据

                                    Map<String, Object> cond1 = new HashMap<>();
                                    cond1.put("ITEM", period.getId()*-1);
                                    cond1.put("TEMPLATEID", templateNo);
                                    cond1.put("PLUNO", pluNo);

                                    List<Map<String, Object>> oData = MapDistinct.getWhereMap(existed, cond1, false);
                                    price = oData.get(0).get("PRICE").toString();
                                    minPrice = oData.get(0).get("MINPRICE").toString();
                                }

                                dcp_salePriceTemplate_price.add("PRICE", DataValues.newDecimal(price));
                                dcp_salePriceTemplate_price.add("MINPRICE", DataValues.newDecimal(minPrice));

                                dcp_salePriceTemplate_price.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
                                dcp_salePriceTemplate_price.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                                dcp_salePriceTemplate_price.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
                                dcp_salePriceTemplate_price.add("CREATETIME", DataValues.newDate(lastModifyTime));
                                dcp_salePriceTemplate_price.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                                dcp_salePriceTemplate_price.add("LASTMODITIME", DataValues.newDate(lastModifyTime));
                                dcp_salePriceTemplate_price.add("CONFIRMTIME", DataValues.newDate(lastModifyTime));
                                dcp_salePriceTemplate_price.add("OFNO", DataValues.newString(billDetail.get("BILLNO")));
                                dcp_salePriceTemplate_price.add("OITEM", DataValues.newString(billDetail.get("ITEM")));

                                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_SALEPRICETEMPLATE_PRICE", dcp_salePriceTemplate_price)));

                            } else {
                                conditionPrice.add("EID", DataValues.newString(eid));
                                conditionPrice.add("TEMPLATEID", DataValues.newString(templateNo));
                                conditionPrice.add("PLUNO", DataValues.newString(pluNo));
                                conditionPrice.add("ITEM", DataValues.newInteger(period.getId()));

                                dcp_salePriceTemplate_price.add("BEGINDATE", DataValues.newDate(DateFormatUtils.format(period.getStartDate())));
                                dcp_salePriceTemplate_price.add("ENDDATE", DataValues.newDate(DateFormatUtils.format(period.getEndDate())));
                                dcp_salePriceTemplate_price.add("LASTMODITIME", DataValues.newDate(lastModifyTime));
                                dcp_salePriceTemplate_price.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                                dcp_salePriceTemplate_price.add("CONFIRMTIME", DataValues.newDate(lastModifyTime));
                                if (period.getStartDate().after(period.getEndDate())) {
                                    dcp_salePriceTemplate_price.add("STATUS", DataValues.newInteger(0));
                                }

                                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SALEPRICETEMPLATE_PRICE", conditionPrice, dcp_salePriceTemplate_price)));

                            }
                        }
                    }

                }

            } else if (StringUtils.equals(TYPE_CANCEL, oprType)) {
                dcpPriceAdjust.add("CANCELBY", DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjust.add("CANCELTIME", DataValues.newDate(lastModifyTime));

                dcpPriceAdjust.add("STATUS", DataValues.newInteger(2));
            }

            dcpPriceAdjust.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
            dcpPriceAdjust.add("LASTMODITIME", DataValues.newDate(lastModifyTime));

            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PRICEADJUST", condition, dcpPriceAdjust)));

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
    protected List<InsBean> prepareInsertData(DCP_PriceAdjustStateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PriceAdjustStateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PriceAdjustStateUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PriceAdjustStateUpdateReq req) throws Exception {
        StringBuilder errMsg = new StringBuilder("");
        if (req.getRequest() == null) {
            errMsg.append("request不能为空值 ");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (StringUtils.isEmpty(req.getRequest().getBillNo())) {
            errMsg.append("单号不可为空");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        if (StringUtils.isEmpty(req.getRequest().getOprType())) {
            errMsg.append("操作类型不可为空");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        req.getRequest().setOprType(req.getRequest().getOprType().toLowerCase()); //小写转换
        return false;
    }

    @Override
    protected TypeToken<DCP_PriceAdjustStateUpdateReq> getRequestType() {
        return new TypeToken<DCP_PriceAdjustStateUpdateReq>() {

        };
    }

    @Override
    protected DCP_PriceAdjustStateUpdateRes getResponseType() {
        return new DCP_PriceAdjustStateUpdateRes();
    }

    private List<DatePeriod> getDatePeriod(String eid,
                                           List<Map<String, Object>> existed, String pluNo, String beginDate, String endDate) {
        List<DatePeriod> periods = Lists.newArrayList();
        if (null != existed && !existed.isEmpty()) {
            Map<String, Object> condition = Maps.newHashMap();
            condition.put("EID", eid);
            condition.put("PLUNO", pluNo);

            List<Map<String, Object>> existPrice = MapDistinct.getWhereMap(existed, condition, true);

            if (null != existPrice && !existPrice.isEmpty()) {
                for (Map<String, Object> oneData : existPrice) {
                    periods.add(new DatePeriod(
                            Integer.parseInt(oneData.get("ITEM").toString()),
                            DateFormatUtils.parseDate(oneData.get("BEGINDATE").toString()),
                            DateFormatUtils.parseDate(oneData.get("ENDDATE").toString())
                    ));
                }
                return DatePeriodSplitter.splitIntoContinuousPeriods(periods, DateFormatUtils.parseDate(beginDate), DateFormatUtils.parseDate(endDate));

            }
        }

        //相关客户没有数据时直接返回
        periods.add(new DatePeriod(
                0,
                DateFormatUtils.parseDate(beginDate),
                DateFormatUtils.parseDate(endDate)

        ));

        return periods;

    }

}
