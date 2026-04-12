package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_CustomerPriceAdjustStatusUpdateReq;
import com.dsc.spos.json.cust.res.DCP_CustomerPriceAdjustStatusUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.date.DatePeriod;
import com.dsc.spos.utils.date.DatePeriodSplitter;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DCP_CustomerPriceAdjustStatusUpdate extends SPosAdvanceService<DCP_CustomerPriceAdjustStatusUpdateReq, DCP_CustomerPriceAdjustStatusUpdateRes> {


    @Override
    protected void processDUID(DCP_CustomerPriceAdjustStatusUpdateReq req, DCP_CustomerPriceAdjustStatusUpdateRes res) throws Exception {

        List<Map<String, Object>> data = doQueryData(String.format(" SELECT * FROM DCP_CUSTPRICEADJUST WHERE EID='%s' AND BILLNO='%s'  ", req.geteId(), req.getRequest().getBillNo()), null);

        if (null == data || data.isEmpty()) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "未查询到对应的单据" + req.getRequest().getBillNo());
        }
        if (!"0".equals(data.get(0).get("STATUS").toString())) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, "当前单据状态不可操作:" + data.get(0).get("STATUS"));
        }

        ColumnDataValue condition = new ColumnDataValue();
        condition.add("EID", DataValues.newString(req.geteId()));
        condition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));

        ColumnDataValue dcp_custPriceAdjust = new ColumnDataValue();

        String lastModiTime = DateFormatUtils.getNowDateTime();
        if (Constant.OPR_TYPE_CANCEL.equals(req.getRequest().getOpType())) {
            dcp_custPriceAdjust.add("STATUS", DataValues.newInteger(2));
            dcp_custPriceAdjust.add("CANCELBY", DataValues.newString(req.getEmployeeNo()));
            dcp_custPriceAdjust.add("CANCELTIME", DataValues.newDate(lastModiTime));
            dcp_custPriceAdjust.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
            dcp_custPriceAdjust.add("MODIFYTIME", DataValues.newDate(lastModiTime));
        } else if (Constant.OPR_TYPE_CONFIRM.equals(req.getRequest().getOpType())) {
            dcp_custPriceAdjust.add("STATUS", DataValues.newInteger(1));
            dcp_custPriceAdjust.add("CONFIRMBY", DataValues.newString(req.getEmployeeNo()));
            dcp_custPriceAdjust.add("CONFIRMTIME", DataValues.newDate(lastModiTime));
            dcp_custPriceAdjust.add("MODIFYBY", DataValues.newString(req.getEmployeeNo()));
            dcp_custPriceAdjust.add("MODIFYTIME", DataValues.newDate(lastModiTime));

            List<Map<String, Object>> customerPrice = this.doQueryData(getQueryCustomerSQL(req), null);
            List<Map<String, Object>> adjust = this.doQueryData(getDetailSql(req), null);

            long maxItem = 0;
            for (Map<String, Object> map : adjust) {

                String customerNo = map.get("CUSTOMERNO").toString();
                String customerType = map.get("CUSTOMERTYPE").toString();
                String pluNo = map.get("PLUNO").toString();
                String unit = map.get("UNIT").toString();

                String beginDate = map.get("BEGINDATE").toString();
                String endDate = map.get("ENDDATE").toString();

                String sMax = map.get("MAXITEM").toString();
                if (!PosPub.isNumeric(sMax)) {
                    maxItem = 0;
                } else {
                    maxItem = Long.parseLong(sMax);
                }

                List<DatePeriod> periods = getDatePeriod(req, customerPrice, customerType, customerNo, pluNo, unit, beginDate, endDate);
                for (DatePeriod period : periods) {
                    ColumnDataValue dcp_customer_price = new ColumnDataValue();
                    //这里需要在循环里重新获取
                    String price = map.get("PRICE").toString();

                    if (period.getId() <= 0) {
                        maxItem++;

                        if (period.getId() < 0) {  //小于0时则表示为原来数据修改，单也需要新增一笔
//                            Map<String, Object> cond1 = new HashMap<>();

//                            cond1.put("EID", DataValues.newString(req.geteId()));
//                            cond1.put("CUSTOMERTYPE", DataValues.newString(customerType));
//                            cond1.put("CUSTOMERNO", DataValues.newString(customerNo));
//                            cond1.put("PLUNO", DataValues.newString(pluNo));
//                            cond1.put("UNIT", DataValues.newString(unit));
//                            cond1.put("ITEM", DataValues.newString(period.getId() * -1));


                            List<Map<String, Object>> oData = customerPrice.stream().filter(
                                            x -> StringUtils.equals(x.get("CUSTOMERTYPE").toString(), customerType) &&
                                                    StringUtils.equals(x.get("CUSTOMERNO").toString(), customerNo) &&
                                                    StringUtils.equals(x.get("PLUNO").toString(), pluNo) &&
                                                    StringUtils.equals(x.get("UNIT").toString(), unit) &&
                                                    StringUtils.equals(x.get("ITEM").toString(), String.valueOf(period.getId() * -1))
                                    )
                                    .collect(Collectors.toList());
//                            MapDistinct.getWhereMap(customerPrice, cond1, false);
                            if (CollectionUtils.isNotEmpty(oData)) {
                                price = oData.get(0).get("PRICE").toString();
                            }

                        }

                        dcp_customer_price.add("EID", DataValues.newString(req.geteId()));
                        dcp_customer_price.add("CUSTOMERNO", DataValues.newString(customerNo));
                        dcp_customer_price.add("ITEM", DataValues.newInteger(maxItem));
                        dcp_customer_price.add("CUSTOMERTYPE", DataValues.newString(customerType));
                        dcp_customer_price.add("PLUNO", DataValues.newString(pluNo));
                        dcp_customer_price.add("UNIT", DataValues.newString(unit));
                        dcp_customer_price.add("FEATURENO", DataValues.newString(" "));
                        dcp_customer_price.add("PRICE", DataValues.newDecimal(price));
                        dcp_customer_price.add("ISDISCOUNT", DataValues.newString("Y"));
                        dcp_customer_price.add("ISPROM", DataValues.newString("Y"));
                        dcp_customer_price.add("BEGINDATE", DataValues.newString(DateFormatUtils.format(period.getStartDate(), DateFormatUtils.PLAIN_DATE_FORMAT)));
                        dcp_customer_price.add("ENDDATE", DataValues.newString(DateFormatUtils.format(period.getEndDate(), DateFormatUtils.PLAIN_DATE_FORMAT)));
                        dcp_customer_price.add("STATUS", DataValues.newInteger(100));
                        dcp_customer_price.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                        dcp_customer_price.add("OFNO", DataValues.newString(req.getRequest().getBillNo()));
                        dcp_customer_price.add("OITEM", DataValues.newInteger(map.get("ITEM")));
                        dcp_customer_price.add("ORGANIZATIONNO", DataValues.newString(map.get("ORGANIZATIONNO")));
                        dcp_customer_price.add("EMPLOYEEID", DataValues.newString(map.get("EMPLOYEEID")));
                        dcp_customer_price.add("DEPARTID", DataValues.newString(map.get("DEPARTID")));

                        this.addProcessData(new DataProcessBean(DataBeans.getInsBean("dcp_customer_price", dcp_customer_price)));

                    } else {

                        ColumnDataValue conditionPrice = new ColumnDataValue();

                        conditionPrice.add("EID", DataValues.newString(req.geteId()));
                        conditionPrice.add("CUSTOMERTYPE", DataValues.newString(customerType));
                        conditionPrice.add("CUSTOMERNO", DataValues.newString(customerNo));
                        conditionPrice.add("PLUNO", DataValues.newString(pluNo));
                        conditionPrice.add("UNIT", DataValues.newString(unit));
                        conditionPrice.add("ITEM", DataValues.newString(period.getId()));

                        dcp_customer_price.add("BEGINDATE", DataValues.newString(DateFormatUtils.format(period.getStartDate(), DateFormatUtils.PLAIN_DATE_FORMAT)));
                        dcp_customer_price.add("ENDDATE", DataValues.newString(DateFormatUtils.format(period.getEndDate(), DateFormatUtils.PLAIN_DATE_FORMAT)));

                        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_customer_price", conditionPrice, dcp_customer_price)));

                    }

                }

            }


        }

        this.addProcessData(new DataProcessBean(DataBeans.getUptBean("dcp_custPriceAdjust", condition, dcp_custPriceAdjust)));
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
    }

    //查询当前已存在的客户定价
    private String getQueryCustomerSQL(DCP_CustomerPriceAdjustStatusUpdateReq req) {
        String sb = " SELECT a.CUSTOMERTYPE,a.CUSTOMERNO,a.ITEM,a.BEGINDATE,a.ENDDATE,a.PLUNO,a.UNIT,a.PRICE " +
                " FROM DCP_CUSTOMER_PRICE a " +

                " LEFT JOIN DCP_CUSTPRICEADJUST_RANGE c on a.EID=c.EID and a.CUSTOMERNO=c.CUSTOMERNO and a.CUSTOMERTYPE=c.CUSTOMERTYPE " +
                " WHERE c.EID='" + req.geteId() + "' and c.BILLNO='" + req.getRequest().getBillNo() + "'" +
                " ORDER BY a.CUSTOMERTYPE,a.CUSTOMERNO,a.PLUNO ";

        return sb;
    }

    //查询本次客户定价调整
    private String getDetailSql(DCP_CustomerPriceAdjustStatusUpdateReq req) {

        String sb = " SELECT a.ORGANIZATIONNO,a.EMPLOYEEID,a.DEPARTID,c.CUSTOMERTYPE,c.CUSTOMERNO,a.BEGINDATE,b.ITEM,a.ENDDATE,b.PLUNO,b.UNIT,b.PRICE,d.MAXITEM " +
                " FROM DCP_CUSTPRICEADJUST a " +
                " LEFT JOIN DCP_CUSTPRICEADJUST_DETAIL b on a.EID=b.EID and a.BILLNO=b.BILLNO and a.ORGANIZATIONNO=b.ORGANIZATIONNO " +
                " LEFT JOIN DCP_CUSTPRICEADJUST_RANGE c on b.EID=c.EID and b.BILLNO=c.BILLNO and b.ORGANIZATIONNO=c.ORGANIZATIONNO " +
                " LEFT JOIN ( " +
                "           SELECT EID,CUSTOMERTYPE,CUSTOMERNO,MAX(ITEM) MAXITEM FROM DCP_CUSTOMER_PRICE" +
                "           GROUP BY EID,CUSTOMERTYPE,CUSTOMERNO   " +
                "           ) d on d.eid=c.eid and c.CUSTOMERTYPE=d.CUSTOMERTYPE and c.CUSTOMERNO=d.CUSTOMERNO " +
                " WHERE a.EID='" + req.geteId() + "' and a.BILLNO='" + req.getRequest().getBillNo() + "'" +
                " ORDER BY c.CUSTOMERTYPE,c.CUSTOMERNO ";

        return sb;
    }


    @Override
    protected List<InsBean> prepareInsertData(DCP_CustomerPriceAdjustStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_CustomerPriceAdjustStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_CustomerPriceAdjustStatusUpdateReq req) throws Exception {
        return Collections.emptyList();
    }

    @Override
    protected boolean isVerifyFail(DCP_CustomerPriceAdjustStatusUpdateReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_CustomerPriceAdjustStatusUpdateReq> getRequestType() {
        return new TypeToken<DCP_CustomerPriceAdjustStatusUpdateReq>() {
        };
    }

    @Override
    protected DCP_CustomerPriceAdjustStatusUpdateRes getResponseType() {
        return new DCP_CustomerPriceAdjustStatusUpdateRes();
    }


    private List<DatePeriod> getDatePeriod(DCP_CustomerPriceAdjustStatusUpdateReq req,
                                           List<Map<String, Object>> existed,
                                           String customerType, String customerNo,
                                           String pluNo, String unit,
                                           String beginDate, String endDate) {
        List<DatePeriod> periods = Lists.newArrayList();
        if (null != existed && !existed.isEmpty()) {
//            Map<String, Object> condition = Maps.newHashMap();
//            condition.put("CUSTOMERNO", customerNo);
//            condition.put("CUSTOMERTYPE", customerType);
//            condition.put("PLUNO", pluNo);
//            condition.put("UNIT", unit);
            List<Map<String, Object>> customerPrice =
                    existed.stream().filter(oneData ->
                            oneData.get("CUSTOMERNO").equals(customerNo)
                                    && oneData.get("CUSTOMERTYPE").equals(customerType)
                                    && oneData.get("PLUNO").equals(pluNo)
                                    && oneData.get("UNIT").equals(unit)
                    ).collect(Collectors.toList());
//                    MapDistinct.getWhereMap(existed, condition, true);

            if (!customerPrice.isEmpty()) {
                for (Map<String, Object> oneData : customerPrice) {
                    periods.add(new DatePeriod(
                            Integer.parseInt(oneData.get("ITEM").toString()),
                            DateFormatUtils.parseDate(oneData.get("BEGINDATE").toString()),
                            DateFormatUtils.parseDate(oneData.get("ENDDATE").toString())
                    ));
                }
                return DatePeriodSplitter.splitIntoContinuousPeriods(periods, DateFormatUtils.parseDate(beginDate), DateFormatUtils.parseDate(endDate));

            }

        }
        //相关客户没有数据时直接返回一笔新增的
        periods.add(new DatePeriod(
                0,
                DateFormatUtils.parseDate(beginDate),
                DateFormatUtils.parseDate(endDate)));

        return periods;
    }


}
