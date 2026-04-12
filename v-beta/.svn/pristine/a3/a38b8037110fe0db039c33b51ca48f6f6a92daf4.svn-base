package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PriceAdjustUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PriceAdjustUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DCP_PriceAdjustUpdate extends SPosAdvanceService<DCP_PriceAdjustUpdateReq, DCP_PriceAdjustUpdateRes> {
    @Override
    protected void processDUID(DCP_PriceAdjustUpdateReq req, DCP_PriceAdjustUpdateRes res) throws Exception {
        try {
            String query = " SELECT * FROM DCP_PRICEADJUST WHERE EID='%s' AND BILLNO='%s' ";
            query = String.format(query,req.geteId(), req.getRequest().getBillNo());
            List<Map<String, Object>> data = this.doQueryData(query, null);
            if (null == data || data.isEmpty()) {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("该单号不存在！" + req.getRequest().getBillNo());
            } else {
                Map<String, Object> bill = data.get(0);

                if (!StringUtils.equals("0",bill.get("STATUS").toString())) {
                    res.setSuccess(false);
                    res.setServiceStatus("200");
                    res.setServiceDescription("单据状态非【0-新建】不可更改！");
                    return;
                }

                ColumnDataValue condition = new ColumnDataValue();

                condition.add("EID", DataValues.newString(req.geteId()));
                condition.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));

                ColumnDataValue dcpPriceAdjust = new ColumnDataValue();

                String lastModifyTime = DateFormatUtils.getNowDateTime();


                dcpPriceAdjust.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
                dcpPriceAdjust.add("BTYPE", DataValues.newString(req.getRequest().getBType()));
                dcpPriceAdjust.add("BDATE", DataValues.newDate(req.getRequest().getBdate()));
                dcpPriceAdjust.add("SUPPLIER", DataValues.newString(req.getRequest().getSupplier()));
                dcpPriceAdjust.add("TEMPLATENO", DataValues.newString(req.getRequest().getTemplateNo()));
                dcpPriceAdjust.add("EFFECTIVEDATE", DataValues.newDate(req.getRequest().getEffectiveDate()));
                dcpPriceAdjust.add("UPDATE_DEFPURPRICE", DataValues.newString(req.getRequest().getIsUpdate_DefpurPrice()));
                dcpPriceAdjust.add("EMPLOYEEID", DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjust.add("DEPARTID", DataValues.newString(req.getDepartmentNo()));
                dcpPriceAdjust.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
                dcpPriceAdjust.add("OWNOPID", DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjust.add("OWNDEPTID", DataValues.newString(req.getDepartmentNo()));

                dcpPriceAdjust.add("ISUPDATESDPRICE", DataValues.newString(req.getRequest().getIsUpdateSdPrice()));
                dcpPriceAdjust.add("INVALIDDATE", DataValues.newDate(req.getRequest().getInvalidDate()));


                dcpPriceAdjust.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjust.add("LASTMODITIME", DataValues.newDate(lastModifyTime));

                this.addProcessData(new DataProcessBean(DataBeans.getUptBean("DCP_PRICEADJUST", condition, dcpPriceAdjust)));

                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PRICEADJUST_DETAIL", condition)));
                this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_PRICEADJUST_PRICE", condition)));
                for (DCP_PriceAdjustUpdateReq.PluList plu : req.getRequest().getPluList()) {
                    ColumnDataValue dcpPriceAdjustDetail = new ColumnDataValue();

                    dcpPriceAdjustDetail.add("EID",DataValues.newString(req.geteId()));
                    dcpPriceAdjustDetail.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
                    dcpPriceAdjustDetail.add("ITEM", DataValues.newString(plu.getItem()));
                    dcpPriceAdjustDetail.add("PLUNO", DataValues.newString(plu.getPluno()));
                    dcpPriceAdjustDetail.add("PRICEUNIT", DataValues.newString(plu.getPriceUnit()));
                    dcpPriceAdjustDetail.add("PURPRICETYPE", DataValues.newString(plu.getNpurPriceType()));
                    dcpPriceAdjustDetail.add("PURPRICE", DataValues.newDecimal(plu.getNpurPrice()));
                    dcpPriceAdjustDetail.add("OPURPRICETYPE", DataValues.newString(plu.getOpurPriceType()));
                    dcpPriceAdjustDetail.add("OPURPRICE", DataValues.newDecimal(plu.getOpurPrice()));
                    dcpPriceAdjustDetail.add("PLUBARCODE", DataValues.newString(plu.getPluBarcode()));

                    dcpPriceAdjustDetail.add("PRICE", DataValues.newDecimal(plu.getPrice()));
                    dcpPriceAdjustDetail.add("OPRICE", DataValues.newDecimal(plu.getOPrice()));
                    dcpPriceAdjustDetail.add("MINPRICE", DataValues.newDecimal(plu.getMinPrice()));
                    dcpPriceAdjustDetail.add("SDPRICE", DataValues.newDecimal(plu.getSdPrice()));
                    dcpPriceAdjustDetail.add("ISDISCOUNT", DataValues.newString(plu.getIsDiscount()));
                    dcpPriceAdjustDetail.add("ISPROM", DataValues.newString(plu.getIsProm()));

                    if ("2".equals(plu.getNpurPriceType())) {

                        for (DCP_PriceAdjustUpdateReq.NpriceList price : plu.getNpriceList()) {

                            ColumnDataValue dcpPriceAdjustPrice = new ColumnDataValue();

                            dcpPriceAdjustPrice.add("EID",DataValues.newString(req.geteId()));
                            dcpPriceAdjustPrice.add("BILLNO", DataValues.newString(req.getRequest().getBillNo()));
                            dcpPriceAdjustPrice.add("ITEM", DataValues.newString(plu.getItem()));
                            dcpPriceAdjustPrice.add("ITEM2", DataValues.newString(price.getItem2()));

                            dcpPriceAdjustPrice.add("BEGINQTY", DataValues.newDecimal(price.getNbeginQty()));
                            dcpPriceAdjustPrice.add("ENDQTY", DataValues.newDecimal(price.getNendQty()));
                            dcpPriceAdjustPrice.add("PURPRICE", DataValues.newDecimal(price.getNpurPrice()));

                            for (DCP_PriceAdjustUpdateReq.OpriceList oPrice : plu.getOpriceList()) {
                                if (StringUtils.equals(oPrice.getItem2(), price.getItem2())) {
                                    dcpPriceAdjustPrice.add("OBEGINQTY", DataValues.newDecimal(oPrice.getObeginQty()));
                                    dcpPriceAdjustPrice.add("OENDQTY", DataValues.newDecimal(oPrice.getOendQty()));
                                    dcpPriceAdjustPrice.add("OPURPRICE", DataValues.newDecimal(oPrice.getOpurPrice()));
                                    break;
                                }
                            }
                            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PRICEADJUST_PRICE",  dcpPriceAdjustPrice)));
                        }


                    }
                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PRICEADJUST_DETAIL",  dcpPriceAdjustDetail)));

                }
                this.doExecuteDataToDB();
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            }

        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }

    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PriceAdjustUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PriceAdjustUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PriceAdjustUpdateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PriceAdjustUpdateReq req) throws Exception {
        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder("");

        if (DateFormatUtils.lessNowDate(req.getRequest().getEffectiveDate())) {
            errMsg.append("价格生效日不可小于当前日期！");
            isFail = true;
        }

        List<DCP_PriceAdjustUpdateReq.PluList> list = req.getRequest().getPluList();
        Set<DCP_PriceAdjustUpdateReq.PluList> set = list.stream().filter(i -> list.stream().filter(i::equals).count() > 1).collect(Collectors.toSet());
        if (!set.isEmpty()) {
            errMsg.append("商品").append(Arrays.toString(set.toArray())).append("重复录入，请重新确认！");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_PriceAdjustUpdateReq.PluList plu : req.getRequest().getPluList()) {
            if ("2".equals(plu.getNpurPriceType())) {
                if (null == plu.getNpriceList() || plu.getNpriceList().isEmpty()) {
                    errMsg.append(plu.getPluno()).append("请维护分量价格明细！");
                    isFail = true;
                }
            }

        }


        if (isFail) {
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return false;
    }

    @Override
    protected TypeToken<DCP_PriceAdjustUpdateReq> getRequestType() {
        return new TypeToken<DCP_PriceAdjustUpdateReq>() {

        };
    }

    @Override
    protected DCP_PriceAdjustUpdateRes getResponseType() {
        return new DCP_PriceAdjustUpdateRes();
    }
}
