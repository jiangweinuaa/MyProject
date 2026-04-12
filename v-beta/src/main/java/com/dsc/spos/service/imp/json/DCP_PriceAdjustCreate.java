package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PriceAdjustCreateReq;
import com.dsc.spos.json.cust.res.DCP_PriceAdjustCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.utils.*;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DCP_PriceAdjustCreate extends SPosAdvanceService<DCP_PriceAdjustCreateReq, DCP_PriceAdjustCreateRes> {
    @Override
    protected void processDUID(DCP_PriceAdjustCreateReq req, DCP_PriceAdjustCreateRes res) throws Exception {
        try {
            String query = " SELECT * FROM DCP_PRICEADJUST WHERE BILL_ID='%s' ";
            query = String.format(query, req.getRequest().getBillno_ID());
            List<Map<String, Object>> data = this.doQueryData(query, null);
            if (null == data || data.isEmpty()) {
                String ticketNo = "";
                //                    1-采购价 2-零售价
                if ("1".equals(req.getRequest().getBType())){
                    ticketNo = getOrderNO(req,"JJTZ");//getTicketNo("DCP_PRICEADJUST", "BILLNO", 4, "JJTZ", req.geteId(), req.getRequest().getBdate(), null);
                }else {
                    ticketNo = getOrderNO(req,"SJTZ");
                }

                ColumnDataValue dcpPriceAdjust = new ColumnDataValue();

                String lastModifyTime = DateFormatUtils.getNowDateTime();

                dcpPriceAdjust.add("EID", DataValues.newString(req.geteId()));
                if (StringUtils.isEmpty(req.getRequest().getOrgNo())){
                    dcpPriceAdjust.add("ORGANIZATIONNO", DataValues.newString(req.getOrganizationNO()));
                }else {
                    dcpPriceAdjust.add("ORGANIZATIONNO", DataValues.newString(req.getRequest().getOrgNo()));
                }

                dcpPriceAdjust.add("BTYPE", DataValues.newString(req.getRequest().getBType()));
                dcpPriceAdjust.add("BILLNO", DataValues.newString(ticketNo));
                dcpPriceAdjust.add("BILL_ID", DataValues.newString(req.getRequest().getBillno_ID()));
                dcpPriceAdjust.add("BDATE", DataValues.newDate(req.getRequest().getBdate()));
                dcpPriceAdjust.add("SUPPLIER", DataValues.newString(req.getRequest().getSupplier()));
                dcpPriceAdjust.add("TEMPLATENO", DataValues.newString(req.getRequest().getTemplateNo()));
                dcpPriceAdjust.add("EFFECTIVEDATE", DataValues.newDate(req.getRequest().getEffectiveDate()));
                dcpPriceAdjust.add("UPDATE_DEFPURPRICE", DataValues.newString(req.getRequest().getIsUpdate_DefpurPrice()));
                dcpPriceAdjust.add("EMPLOYEEID", DataValues.newString(req.getRequest().getEmployeeID()));
                dcpPriceAdjust.add("DEPARTID", DataValues.newString(req.getRequest().getDepartID()));
                dcpPriceAdjust.add("MEMO", DataValues.newString(req.getRequest().getMemo()));
                dcpPriceAdjust.add("STATUS", DataValues.newInteger(0));
                dcpPriceAdjust.add("OWNOPID", DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjust.add("OWNDEPTID", DataValues.newString(req.getDepartmentNo()));

                dcpPriceAdjust.add("CREATEOPID", DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjust.add("CREATEDEPTID", DataValues.newString(req.getDepartmentNo()));
                dcpPriceAdjust.add("CREATETIME", DataValues.newDate(lastModifyTime));
                dcpPriceAdjust.add("LASTMODIOPID", DataValues.newString(req.getEmployeeNo()));
                dcpPriceAdjust.add("LASTMODITIME", DataValues.newDate(lastModifyTime));

                if ("2".equals(req.getRequest().getBType())){
                    dcpPriceAdjust.add("ISUPDATESDPRICE", DataValues.newString(req.getRequest().getIsUpdateSdPrice()));
                    dcpPriceAdjust.add("INVALIDDATE", DataValues.newDate(req.getRequest().getInvalidDate()));
                }

                this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PRICEADJUST", dcpPriceAdjust)));

                for (DCP_PriceAdjustCreateReq.PluList plu : req.getRequest().getPluList()) {
                    ColumnDataValue dcpPriceAdjustDetail = new ColumnDataValue();

                    dcpPriceAdjustDetail.add("EID", DataValues.newString(req.geteId()));
                    dcpPriceAdjustDetail.add("BILLNO", DataValues.newString(ticketNo));
                    dcpPriceAdjustDetail.add("ITEM", DataValues.newString(plu.getItem()));
//            dcpPriceAdjustDetail.add("PLUBARCODE", DataValues.newString(plu.getPluno()));
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
                        for (DCP_PriceAdjustCreateReq.NpriceList price : plu.getNpriceList()) {
                            ColumnDataValue dcpPriceAdjustPrice = new ColumnDataValue();

                            dcpPriceAdjustPrice.add("EID", DataValues.newString(req.geteId()));
                            dcpPriceAdjustPrice.add("BILLNO", DataValues.newString(ticketNo));
                            dcpPriceAdjustPrice.add("ITEM", DataValues.newString(plu.getItem()));
                            dcpPriceAdjustPrice.add("ITEM2", DataValues.newString(price.getItem2()));

                            dcpPriceAdjustPrice.add("BEGINQTY", DataValues.newDecimal(price.getNbeginQty()));
                            dcpPriceAdjustPrice.add("ENDQTY", DataValues.newDecimal(price.getNendQty()));
                            dcpPriceAdjustPrice.add("PURPRICE", DataValues.newDecimal(price.getNpurPrice()));


                            for (DCP_PriceAdjustCreateReq.OpriceList oPrice : plu.getOpriceList()) {
                                if (StringUtils.equals(oPrice.getItem2(), price.getItem2())) {
                                    dcpPriceAdjustPrice.add("OBEGINQTY", DataValues.newDecimal(oPrice.getObeginQty()));
                                    dcpPriceAdjustPrice.add("OENDQTY", DataValues.newDecimal(oPrice.getOendQty()));
                                    dcpPriceAdjustPrice.add("OPURPRICE", DataValues.newDecimal(oPrice.getOpurPrice()));
                                    break;
                                }
                            }
                            this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PRICEADJUST_PRICE", dcpPriceAdjustPrice)));
                        }
                    }
                    this.addProcessData(new DataProcessBean(DataBeans.getInsBean("DCP_PRICEADJUST_DETAIL", dcpPriceAdjustDetail)));

                }
                this.doExecuteDataToDB();
                res.setBillNo(ticketNo);
                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");
            } else {
                res.setSuccess(false);
                res.setServiceStatus("200");
                res.setServiceDescription("该单号已存在！" + req.getRequest().getBillno_ID());
            }

        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常:" + e.getMessage());
        }


    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PriceAdjustCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PriceAdjustCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PriceAdjustCreateReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PriceAdjustCreateReq req) throws Exception {

//        1-数据完整性：
//        调价明细列表不可为空！
//        检查调价明细中字段【新计价方式】值为“2-分量计价”，则分量价格明细不可传空，提示维护分量价格明细！
//        2-字段必录检查：必录字段不可为空！
//        3-商品行检查：
//        商品判断是否存在【采购模板】明细中，不存在则保存失败并提示“商品xxx不存在供应商采购模板中，请重新确认！”；
//        商品判断在【采购模板】明细的状态，非【100-启用】状态则保存失败并提示“商品xxx不存在供应商采购模板中未启用，请重新确认！”
//        商品判断是否存在重复，若存在整单保存失败提示“商品xxx重复录入，请重新确认！”
//        4-日期检查：字段【价格生效日】不可小于当前系统日期

        boolean isFail = false;
        StringBuilder errMsg = new StringBuilder();

        if (DateFormatUtils.lessNowDate(req.getRequest().getEffectiveDate())) {
            errMsg.append("价格生效日不可小于当前日期！");
            isFail = true;
        }

        List<DCP_PriceAdjustCreateReq.PluList> list = req.getRequest().getPluList();
        Set<DCP_PriceAdjustCreateReq.PluList> set = list.stream().filter(i -> list.stream().filter(i::equals).count() > 1).collect(Collectors.toSet());
        if (!set.isEmpty()) {
            errMsg.append("商品").append(Arrays.toString(set.toArray())).append("重复录入，请重新确认！");
            throw new DispatchService.SPosCodeException(DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        for (DCP_PriceAdjustCreateReq.PluList plu : req.getRequest().getPluList()) {
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
    protected TypeToken<DCP_PriceAdjustCreateReq> getRequestType() {
        return new TypeToken<DCP_PriceAdjustCreateReq>() {

        };
    }

    @Override
    protected DCP_PriceAdjustCreateRes getResponseType() {
        return new DCP_PriceAdjustCreateRes();
    }
}
