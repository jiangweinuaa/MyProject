package com.dsc.spos.scheduler.job;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.date.DatePeriod;
import com.dsc.spos.utils.date.DatePeriodSplitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class AutoAdjustPrice extends InitJob {
    Logger logger = LogManager.getLogger(AutoAdjustPrice.class.getName());
    static boolean bRun = false;//标记此服务是否正在执行中

    public String doExe() {

        String sReturnInfo = "";
        if (bRun) {
            logger.info("\r\n*********采购价格调整AutoAdjustPrice正在执行中,本次调用取消:************\r\n");
            sReturnInfo = "采购价格调整AutoAdjustPrice正在执行中！";
            return sReturnInfo;
        }
        bRun = true;

        try {
            String sDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String sTime = new SimpleDateFormat("HHmmss").format(new Date());

            String getTimeSql = "select * from job_quartz_detail where job_name = 'AutoAdjustPrice'  and STATUS = '100' ";
            List<Map<String, Object>> getTimeDatas = this.doQueryData(getTimeSql, null);
            if (getTimeDatas != null && !getTimeDatas.isEmpty()) {
                boolean isTime = false;
                for (Map<String, Object> map : getTimeDatas) {
                    String beginTime = map.get("BEGIN_TIME").toString();
                    String endTime = map.get("END_TIME").toString();

                    if (sTime.compareTo(beginTime) >= 0 && sTime.compareTo(endTime) < 0) {
                        isTime = true;
                        break;
                    }
                }
                if (!isTime) {
                    return sReturnInfo;
                }
            }

            //查询所有调价计划
            String sql = " select a.* from DCP_PRICEADJUSTPLAN a "
                    + " where (a.status='0' or a.status='1' ) and a.begindate>=to_date('" + sDate + "','yyyyMMdd') and a.ENDDATE<=to_date('" + sDate + "','yyyyMMdd')"
                    + " order by a.eid,a.billno,a.item";

            List<Map<String, Object>> getHead = this.doQueryData(sql, null);

            List<DataProcessBean> data = new ArrayList<>();

            for (Map<String, Object> map : getHead) {
                ColumnDataValue condition = new ColumnDataValue();
                ColumnDataValue dataValue = new ColumnDataValue();

                if ("0".equals(map.get("STATUS"))) {
                    condition.add("EID", DataValues.newString(map.get("EID")));
                    condition.add("BILLNO", DataValues.newString(map.get("BILLNO")));
                    condition.add("ITEM", DataValues.newString(map.get("ITEM")));

                    dataValue.add("STATUS", DataValues.newInteger(1));
                    dataValue.add("LASTEXEDATE", DataValues.newDate(DateFormatUtils.getNowDateTime()));

                    UptBean uptBean = DataBeans.getUptBean("DCP_PRICEADJUSTPLAN", condition, dataValue);
                    data.add(new DataProcessBean(uptBean));
                }
            }

            if (!data.isEmpty()) {
                StaticInfo.dao.useTransactionProcessData(data); //执行到数据库
            }

            //执行价格变更
            for (Map<String, Object> map : getHead) {
                data.clear(); //清空当次执行

                String eid = map.get("EID").toString();
                String templateNo = map.get("TEMPLATENO").toString();

                ColumnDataValue condition = new ColumnDataValue();
                ColumnDataValue dataValue = new ColumnDataValue();

                condition.add("EID", DataValues.newString(map.get("EID")));
                condition.add("BILLNO", DataValues.newString(map.get("BILLNO")));
                condition.add("ITEM", DataValues.newString(map.get("ITEM")));

                dataValue.add("STATUS", DataValues.newInteger(2));

                UptBean uptBean = DataBeans.getUptBean("DCP_PRICEADJUSTPLAN", condition, dataValue);
                data.add(new DataProcessBean(uptBean));

                ColumnDataValue priceCondition = new ColumnDataValue();
                ColumnDataValue priceData = new ColumnDataValue();
                ColumnDataValue goodsCondition = new ColumnDataValue();
                ColumnDataValue dcpGoods = new ColumnDataValue();

                if ("1".equals(map.get("ADTYPE"))) {
                    priceCondition.add("EID", DataValues.newString(map.get("EID")));
                    priceCondition.add("PURTEMPLATENO", DataValues.newString(map.get("TEMPLATENO")));
                    priceCondition.add("PLUNO", DataValues.newString(map.get("PLUNO")));

                    priceData.add("PURPRICE", DataValues.newDecimal(map.get("PRICE")));

                    data.add(new DataProcessBean(DataBeans.getUptBean("DCP_PURCHASETEMPLATE_PRICE", priceCondition, priceData)));


                    //更新默认采购价
                    if ("Y".equals(map.get("UPDATE_DEFPURPRICE").toString())) {

                        goodsCondition.add("EID", DataValues.newString(map.get("EID")));
                        goodsCondition.add("PLUNO", DataValues.newString(map.get("PLUNO")));

                        dcpGoods.add("PURPRICE",DataValues.newDecimal(map.get("PRICE")));

                        data.add(new DataProcessBean(DataBeans.getUptBean("DCP_GOODS",goodsCondition,dcpGoods)));

                    }


                } else {

                    String maxItemSql = "select MAX(ITEM) MAXITEM from dcp_salepricetemplate_price where eid ='" + eid + "' and templateid='" + templateNo + "' ";
                    List<Map<String, Object>> getDataMaxItem = this.doQueryData(maxItemSql, null);
                    //【ID1026291】【荷家3.0】供货价模板添加商品报错如图 by jinzma 20220531
                    //int maxItem=0;
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

                    //查询当前已经存在数据
                    List<Map<String, Object>> existed = this.doQueryData(
                            "SELECT * FROM DCP_SALEPRICETEMPLATE_PRICE WHERE EID='" + eid + "'" + " AND TEMPLATEID='" + templateNo + "'"
                            , null);

                    String pluNo = map.get("PLUNO").toString();
                    String beginDate = map.get("BEGINDATE").toString();
                    String endDate = map.get("ENDDATE").toString();

                    List<DatePeriod> periods = getDatePeriod(eid, existed, pluNo, beginDate, endDate);

                    for (DatePeriod period : periods) {
                        ColumnDataValue dcp_salePriceTemplate_price = new ColumnDataValue();
                        ColumnDataValue conditionPrice = new ColumnDataValue();
                        if (period.getId() == 0) {
                            maxItem++;

                            dcp_salePriceTemplate_price.add("EID", DataValues.newString(eid));
                            dcp_salePriceTemplate_price.add("TEMPLATEID", DataValues.newString(templateNo));
                            dcp_salePriceTemplate_price.add("ITEM", DataValues.newInteger(maxItem));
                            dcp_salePriceTemplate_price.add("PLUNO", DataValues.newString(map.get("PLUNO")));
                            dcp_salePriceTemplate_price.add("UNIT", DataValues.newString(map.get("PRICEUNIT")));
                            dcp_salePriceTemplate_price.add("ISDISCOUNT", DataValues.newString("Y"));
                            dcp_salePriceTemplate_price.add("ISPROM", DataValues.newString("Y"));
                            dcp_salePriceTemplate_price.add("BEGINDATE", DataValues.newDate(DateFormatUtils.format(period.getStartDate())));
                            dcp_salePriceTemplate_price.add("ENDDATE", DataValues.newDate(DateFormatUtils.format(period.getEndDate())));

                            dcp_salePriceTemplate_price.add("STATUS", DataValues.newInteger(100));
                            String price = map.get("PRICE").toString();
                            if (!PosPub.isNumericType(price)) {
                                price = "0";
                            }
                            dcp_salePriceTemplate_price.add("PRICE", DataValues.newDecimal(price));

                            dcp_salePriceTemplate_price.add("EMPLOYEEID", DataValues.newString(map.get("EMPLOYEEID")));
                            dcp_salePriceTemplate_price.add("CREATEOPID", DataValues.newString(map.get("CONFIRMBY")));
                            dcp_salePriceTemplate_price.add("DEPARTID", DataValues.newString(map.get("DEPARTID")));
                            dcp_salePriceTemplate_price.add("CREATETIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                            dcp_salePriceTemplate_price.add("OFNO", DataValues.newString(map.get("BILLNO")));
                            dcp_salePriceTemplate_price.add("OITEM", DataValues.newString(map.get("ITEM")));

                            data.add(new DataProcessBean(DataBeans.getInsBean("DCP_SALEPRICETEMPLATE_PRICE", dcp_salePriceTemplate_price)));

                        } else {
                            conditionPrice.add("EID", DataValues.newString(eid));
                            conditionPrice.add("TEMPLATEID", DataValues.newString(templateNo));
                            conditionPrice.add("PLUNO", DataValues.newString(map.get("PLUNO")));
                            conditionPrice.add("ITEM", DataValues.newInteger(period.getId()));
                            dcp_salePriceTemplate_price.add("BEGINDATE", DataValues.newDate(DateFormatUtils.format(period.getStartDate())));
                            dcp_salePriceTemplate_price.add("ENDDATE", DataValues.newDate(DateFormatUtils.format(period.getEndDate())));
                            dcp_salePriceTemplate_price.add("LASTMODITIME", DataValues.newDate(DateFormatUtils.getNowDateTime()));
                            dcp_salePriceTemplate_price.add("LASTMODIOPID", DataValues.newString(map.get("CONFIRMBY")));
                            dcp_salePriceTemplate_price.add("CONFIRMTIME", DataValues.newString(map.get("CONFIRMBY")));
                            if (period.getStartDate().after(period.getEndDate())) {
                                dcp_salePriceTemplate_price.add("STATUS", DataValues.newInteger(0));
                            }

                            data.add(new DataProcessBean(DataBeans.getUptBean("DCP_SALEPRICETEMPLATE_PRICE", conditionPrice, dcp_salePriceTemplate_price)));

                        }
                    }
                }

                condition = new ColumnDataValue();

                condition.add("EID", DataValues.newString(map.get("EID")));
                condition.add("BILLNO", DataValues.newString(map.get("BILLNO")));
                condition.add("ITEM", DataValues.newString(map.get("ITEM")));

                dataValue.add("STATUS", DataValues.newInteger(2));

                data.add(new DataProcessBean(DataBeans.getUptBean("DCP_PRICEADJUSTPLAN", condition, dataValue)));

                try {
                    StaticInfo.dao.useTransactionProcessData(data); //执行到数据库
                } catch (Exception e) {
                    logger.error("\r\n******采购价格调整计划" + map.get("BILLNO") + map.get("ITEM") + "执行异常" + e.getMessage() + "\r\n******\r\n");
                }

            }

        } catch (Exception e) {
            logger.error("\r\n******采购价格调整AutoAdjustPrice执行异常" + e.getMessage() + "\r\n******\r\n");
            sReturnInfo = "执行异常:" + e.getMessage();
        } finally {
            bRun = false;
            logger.info("\r\n*********采购价格调整AutoAdjustPrice定时调用End:************\r\n");
        }

        return sReturnInfo;
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
