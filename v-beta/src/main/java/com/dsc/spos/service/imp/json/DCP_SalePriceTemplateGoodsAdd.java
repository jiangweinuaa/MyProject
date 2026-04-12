package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateGoodsAddReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateGoodsAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.*;
import com.dsc.spos.utils.date.DatePeriod;
import com.dsc.spos.utils.date.DatePeriodSplitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DCP_SalePriceTemplateGoodsAdd extends SPosAdvanceService<DCP_SalePriceTemplateGoodsAddReq, DCP_SalePriceTemplateGoodsAddRes> {

    @Override
    protected void processDUID(DCP_SalePriceTemplateGoodsAddReq req, DCP_SalePriceTemplateGoodsAddRes res) throws Exception {

        String eId = req.geteId();
        String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String templateId = req.getRequest().getTemplateId();

        ///处理单价和金额小数位数  BY JZMA
        String priceLength = PosPub.getPARA_SMS(dao, eId, "", "priceLength");
        if (Check.Null(priceLength) || !PosPub.isNumeric(priceLength)) {
            priceLength = "2";
        }
        int priceLengthInt = Integer.parseInt(priceLength);

        String maxItemSql = "select MAX(ITEM) MAXITEM from dcp_salepricetemplate_price where eid ='" + eId + "' and templateid='" + templateId + "' ";
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
                "SELECT * FROM DCP_SALEPRICETEMPLATE_PRICE WHERE EID='" + req.geteId() + "'" + " AND TEMPLATEID='" + req.getRequest().getTemplateId() + "'"
                , null);


        for (DCP_SalePriceTemplateGoodsAddReq.PluList template : req.getRequest().getPluList()) {
            //CATEGORY-商品分类；GOODS-商品；
            String sqlCategory;
            if (template.getType().equals("CATEGORY")) {
                //关联商品表将商品分类下属所有商品都添加进来
                sqlCategory = "select * from dcp_goods where category ='" + template.getId() + "' ";
            } else {
                //关联商品表将商品分类下属所有商品都添加进来
                sqlCategory = "select * from dcp_goods where pluno ='" + template.getId() + "' ";
            }

            List<Map<String, Object>> getData = this.doQueryData(sqlCategory, null);
            if (getData != null && !getData.isEmpty()) {
                for (Map<String, Object> map : getData) {

                    String pluNo = map.get("PLUNO").toString();
                    String beginDate = DateFormatUtils.getDateTime(template.getBeginDate());
                    String endDate = DateFormatUtils.getDateTime(template.getEndDate());

                    List<DatePeriod> periods = getDatePeriod(req, existed, pluNo, beginDate, endDate);

                    for (DatePeriod period : periods) {
                        ColumnDataValue dcp_salePriceTemplate_price = new ColumnDataValue();
                        ColumnDataValue condition = new ColumnDataValue();
                        if (period.getId() == 0) {
                            maxItem++;

                            dcp_salePriceTemplate_price.add("EID", DataValues.newString(req.geteId()));
                            dcp_salePriceTemplate_price.add("TEMPLATEID", DataValues.newString(req.getRequest().getTemplateId()));
                            dcp_salePriceTemplate_price.add("ITEM", DataValues.newInteger(maxItem));
                            dcp_salePriceTemplate_price.add("PLUNO", DataValues.newString(map.get("PLUNO")));
                            dcp_salePriceTemplate_price.add("UNIT", DataValues.newString(map.get("SUNIT")));
                            dcp_salePriceTemplate_price.add("ISDISCOUNT", DataValues.newString(template.getIsDiscount()));
                            dcp_salePriceTemplate_price.add("ISPROM", DataValues.newString(template.getIsProm()));
                            dcp_salePriceTemplate_price.add("BEGINDATE", DataValues.newDate(template.getBeginDate()));
                            dcp_salePriceTemplate_price.add("ENDDATE", DataValues.newDate(template.getEndDate()));

                            dcp_salePriceTemplate_price.add("STATUS", DataValues.newInteger(100));
                            String price = map.get("PRICE").toString();
                            if (!PosPub.isNumericType(price)) {
                                price = "0";
                            }
                            double minprice = new BigDecimal(price).multiply(new BigDecimal(template.getPriceDiscRate())).divide(new BigDecimal(100), priceLengthInt, RoundingMode.HALF_UP).doubleValue();
                            dcp_salePriceTemplate_price.add("PRICE", DataValues.newDecimal(minprice));
                            dcp_salePriceTemplate_price.add("MINPRICE", DataValues.newDecimal(new BigDecimal(price).multiply(new BigDecimal(template.getMinPriceDiscRate())).divide(new BigDecimal(100), priceLengthInt, RoundingMode.HALF_UP).doubleValue()));
                            dcp_salePriceTemplate_price.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
                            dcp_salePriceTemplate_price.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                            dcp_salePriceTemplate_price.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
                            dcp_salePriceTemplate_price.add("CREATETIME", DataValues.newDate(lastmoditime));

                            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_SALEPRICETEMPLATE_PRICE", dcp_salePriceTemplate_price)));

                        } else {
                            condition.add("EID", DataValues.newString(req.geteId()));
                            condition.add("TEMPLATEID", DataValues.newString(req.getRequest().getTemplateId()));
                            condition.add("PLUNO", DataValues.newString(map.get("PLUNO")));
                            condition.add("ITEM", DataValues.newInteger(period.getId()));

                            dcp_salePriceTemplate_price.add("BEGINDATE", DataValues.newDate(DateFormatUtils.format(period.getStartDate())));
                            dcp_salePriceTemplate_price.add("ENDDATE", DataValues.newDate(DateFormatUtils.format(period.getEndDate())));
                            dcp_salePriceTemplate_price.add("LASTMODITIME", DataValues.newDate(lastmoditime));
                            dcp_salePriceTemplate_price.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                            if (period.getStartDate().after(period.getEndDate())) {
                                dcp_salePriceTemplate_price.add("STATUS", DataValues.newInteger(0));
                            }

                            this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_SALEPRICETEMPLATE_PRICE", condition, dcp_salePriceTemplate_price)));

                        }

                    }

                }

            }
        }

        //
        this.doExecuteDataToDB();

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_SalePriceTemplateGoodsAddReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_SalePriceTemplateGoodsAddReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_SalePriceTemplateGoodsAddReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_SalePriceTemplateGoodsAddReq req) throws Exception {
        boolean isFail = false;

        return isFail;
    }

    @Override
    protected TypeToken<DCP_SalePriceTemplateGoodsAddReq> getRequestType() {
        return new TypeToken<DCP_SalePriceTemplateGoodsAddReq>() {
        };
    }

    @Override
    protected DCP_SalePriceTemplateGoodsAddRes getResponseType() {
        return new DCP_SalePriceTemplateGoodsAddRes();
    }

    private List<DatePeriod> getDatePeriod(DCP_SalePriceTemplateGoodsAddReq req,
                                           List<Map<String, Object>> existed, String pluNo, String beginDate, String endDate) {
        List<DatePeriod> periods = Lists.newArrayList();
        if (null != existed && !existed.isEmpty()) {
            Map<String, Object> condition = Maps.newHashMap();
            condition.put("EID", req.geteId());
            condition.put("PLUNO", pluNo);
            List<Map<String, Object>> existedPrice = MapDistinct.getWhereMap(existed, condition, true);

            if (null != existedPrice && !existedPrice.isEmpty()) {
                for (Map<String, Object> oneData : existedPrice) {
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
