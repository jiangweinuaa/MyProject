package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dsc.spos.json.cust.req.DCP_EInvoicePre_OpenReq;
import com.dsc.spos.json.cust.res.DCP_EInvoicePre_OpenRes;
import com.dsc.spos.model.EInvoiceRequest;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.invoice.InvoiceService;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 开票申请试算
 * @author: wangzyc
 * @create: 2022-03-10
 */
public class DCP_EInvoicePre_Open extends SPosBasicService<DCP_EInvoicePre_OpenReq, DCP_EInvoicePre_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_EInvoicePre_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_EInvoicePre_OpenReq.level1Elm request = req.getRequest();
        String token = req.getToken();
        if(Check.Null(token)){
            if (Check.Null(request.getTemplateid())) {
                errMsg.append("发票参数模板id不可为空值, ");
                isFail = true;
            }
        }

        if (Check.Null(request.getBillType())) {
            errMsg.append("业务单据类型不可为空值, ");
            isFail = true;
        }
        if (Check.Null(request.getIsCheckLimitation())) {
            errMsg.append("是否校验发票限额不可为空值, ");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_EInvoicePre_OpenReq> getRequestType() {
        return new TypeToken<DCP_EInvoicePre_OpenReq>(){};
    }

    @Override
    protected DCP_EInvoicePre_OpenRes getResponseType() {
        return new DCP_EInvoicePre_OpenRes();
    }

    @Override
    protected DCP_EInvoicePre_OpenRes processJson(DCP_EInvoicePre_OpenReq req) throws Exception {
        DCP_EInvoicePre_OpenRes res = this.getResponseType();
        DCP_EInvoicePre_OpenReq.level1Elm  request = req.getRequest();



        try {
            /**
             * 拼装发票查询参数
             */
            EInvoiceRequest eInvoiceRequest = new EInvoiceRequest();
            eInvoiceRequest.setOperater("queryInfoByOrderList");
            eInvoiceRequest.setBilltype(request.getBillType());
            eInvoiceRequest.setEid(req.geteId());
            eInvoiceRequest.setOrdernolist(new ArrayList<>());
            for (DCP_EInvoicePre_OpenReq.level2Elm level2Elm : request.getOrdernoList()) {
                EInvoiceRequest.level1Elm lv1 = new EInvoiceRequest.level1Elm();
                lv1.setOrderno(level2Elm.getOrderno());
                lv1.setShopid(level2Elm.getShopid());
                eInvoiceRequest.getOrdernolist().add(lv1);
            }
            eInvoiceRequest.setTemplateid(request.getTemplateid());
            eInvoiceRequest.setQuerytype("2");
            eInvoiceRequest.setProjectId(request.getProjectId());
            String langType = req.getLangType();
            if(Check.Null(langType)){
                langType = "zh_CN";
            }
            eInvoiceRequest.setLangType(langType);
            eInvoiceRequest.setIschecklimitation(request.getIsCheckLimitation());

            String requestJson = JSONArray.toJSONString(eInvoiceRequest);

            InvoiceService invoiceService = new InvoiceService();
            String responeJson = invoiceService.invoiceQueryInfoByOrder(this.dao, requestJson, "RUIHONG");
            DCP_EInvoicePre_OpenRes.level1Elm level1Elm = res.new level1Elm();

            if (!Check.Null(responeJson)) {
                Map<String,String> hashMap = JSON.parseObject(responeJson, HashMap.class);
                String errorMessage =  hashMap.getOrDefault("errorMessage","");
                if(!Check.Null(errorMessage)){
                    res.setSuccess(false);
                    res.setServiceStatus("100");
                    res.setServiceDescription(errorMessage);
                    return res;
                }
                level1Elm.setAmt(hashMap.get("amt"));
                level1Elm.setTaxAmt(hashMap.get("taxAmt"));
                level1Elm.setExTaxAmt(hashMap.get("exTaxAmt"));
                level1Elm.setLimitation(hashMap.get("limitation"));
                if(!Check.Null(req.getToken())){
                    level1Elm.setProjectId(hashMap.get("projectId"));
                    level1Elm.setProjectName(hashMap.get("projectName"));
                    level1Elm.setTaxRate(hashMap.get("taxRate"));
                    level1Elm.setTaxCode(hashMap.get("tacCode"));
                }
                res.setDatas(level1Elm);
            }
            res.setDatas(level1Elm);
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
        }
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_EInvoicePre_OpenReq req) throws Exception {
        return null;
    }
}
