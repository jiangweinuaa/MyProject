package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_EInvoiceApply_OpenReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceApply_OpenRes;
import com.dsc.spos.json.cust.res.DCP_EInvoicePre_OpenRes;
import com.dsc.spos.model.EInvoiceRequest;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.invoice.InvoiceService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 开票申请
 * @author: wangzyc
 * @create: 2022-03-11
 */
public class DCP_EInvoiceApply_Open extends SPosAdvanceService<DCP_EInvoiceApply_OpenReq, DCP_EInvoiceApply_OpenRes> {
    @Override
    protected void processDUID(DCP_EInvoiceApply_OpenReq req, DCP_EInvoiceApply_OpenRes res) throws Exception {
        DCP_EInvoiceApply_OpenReq.level1Elm request = req.getRequest();

        try {
            EInvoiceRequest eInvoiceRequest = new EInvoiceRequest();
            eInvoiceRequest.setOperater("queryInfoByOrderList");
            eInvoiceRequest.setBilltype(request.getBillType());
            eInvoiceRequest.setEid(req.geteId());
            eInvoiceRequest.setOrdernolist(new ArrayList<>());
            for (DCP_EInvoiceApply_OpenReq.level2Elm level2Elm : request.getOrdernoList()) {
                EInvoiceRequest.level1Elm lv1 = new EInvoiceRequest.level1Elm();
                lv1.setOrderno(level2Elm.getOrderno());
                lv1.setShopid(level2Elm.getShopid());
                eInvoiceRequest.getOrdernolist().add(lv1);
            }
            eInvoiceRequest.setTemplateid(request.getTemplateid());
            eInvoiceRequest.setQuerytype("2");
            eInvoiceRequest.setProjectId(request.getProjectId());
            eInvoiceRequest.setIschecklimitation("Y");
            eInvoiceRequest.setApply("Y");
            String langType = req.getLangType();
            if(Check.Null(langType)){
                langType = "zh_CN";
            }
            eInvoiceRequest.setLangType(langType);

            // 发票信息
            eInvoiceRequest.setInvoiceKind(request.getInvoiceKind());
            eInvoiceRequest.setBuyerName(request.getBuyerName());
            eInvoiceRequest.setBuyerTaxNum(request.getBuyerTaxNum());
            eInvoiceRequest.setProjectName(request.getProjectName());
            eInvoiceRequest.setBuyerAddress(request.getBuyerAddress());
            eInvoiceRequest.setBuyerTel(request.getBuyerTel());
            eInvoiceRequest.setBuyerBank(request.getBuyerBank());
            eInvoiceRequest.setBuyerPhone(request.getBuyerPhone());
            eInvoiceRequest.setBuyerAccount(request.getBuyerAccount());
            eInvoiceRequest.setBuyerEmaill(request.getBuyerEmail());
            eInvoiceRequest.setMemberId(request.getMemberId());
            eInvoiceRequest.setMemberName(request.getMemberName());
            eInvoiceRequest.setOpenId(request.getOpenId());


            String requestJson = JSONArray.toJSONString(eInvoiceRequest);

            InvoiceService invoiceService = new InvoiceService();
            String responeJson = invoiceService.invoiceQueryInfoByOrder(this.dao, requestJson, "RUIHONG");
            DCP_EInvoiceApply_OpenRes.level1Elm level1Elm = res.new level1Elm();

            if (!Check.Null(responeJson)) {
                Map<String, String> hashMap = JSON.parseObject(responeJson, HashMap.class);
                String errorMessage = hashMap.getOrDefault("errorMessage", "");
                String code = hashMap.getOrDefault("code", "");
                if (!Check.Null(errorMessage)) {
                    res.setSuccess(false);
                    res.setServiceStatus("100");
                    res.setServiceDescription(errorMessage);
                } else if ("501".equals(code) || "0".equals(code)) {
                    level1Elm.setAmt(hashMap.get("amt"));
                    level1Elm.setTaxAmt(hashMap.get("taxAmt"));
                    level1Elm.setExTaxAmt(hashMap.get("exTaxAmt"));
                    level1Elm.setInvoiceBillNo(hashMap.get("invoiceBillNo"));
                    level1Elm.setPlatformType(hashMap.get("platformType"));
                    res.setDatas(level1Elm);
                    res.setSuccess(true);
                    res.setServiceStatus("000");
                    res.setServiceDescription("服务执行成功");
                } else {

                    res.setSuccess(false);
                    res.setServiceStatus("100");
                    res.setServiceDescription("申请开票失败：" + hashMap.get("message"));
                    res.setDatas(level1Elm);
                }
            }
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_EInvoiceApply_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_EInvoiceApply_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_EInvoiceApply_OpenReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_EInvoiceApply_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_EInvoiceApply_OpenReq.level1Elm request = req.getRequest();

        if (Check.Null(request.getTemplateid())) {
            errMsg.append("发票参数模板id不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getBillType())) {
            errMsg.append("业务单据类型不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getBuyerName())) {
            errMsg.append("购方名称/抬头不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_EInvoiceApply_OpenReq> getRequestType() {
        return new TypeToken<DCP_EInvoiceApply_OpenReq>() {
        };
    }

    @Override
    protected DCP_EInvoiceApply_OpenRes getResponseType() {
        return new DCP_EInvoiceApply_OpenRes();
    }
}
