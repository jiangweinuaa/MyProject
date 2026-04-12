package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_UpdateStockToThirdPlatform_OpenReq;
import com.dsc.spos.json.cust.res.DCP_UpdateStockToThirdPlatform_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.service.webhook.WebHookService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class DCP_UpdateStockToThirdPlatform_Open extends SPosBasicService<DCP_UpdateStockToThirdPlatform_OpenReq, DCP_UpdateStockToThirdPlatform_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_UpdateStockToThirdPlatform_OpenReq req) throws Exception {
        boolean isFail = false;
        if(req.getRequest()==null)
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "request节点不能为空！");
        }
        StringBuffer errMsg = new StringBuffer("");
        String eId = req.geteId();
        String eId_para = req.getRequest().geteId();
        if (Check.Null(eId_para))
        {
            eId_para = eId;
            req.getRequest().seteId(eId_para);
        }

        if (Check.Null(eId_para)) {
            errMsg.append("eId不能为空值,");
            isFail = true;
        }

        if (Check.Null(req.getRequest().getShopId())) {
            errMsg.append("shopId不能为空值,");
            isFail = true;
        }
        if (Check.Null(req.getRequest().getBillNo())) {
            errMsg.append("billNo不能为空值,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }
        return isFail;
    }

    @Override
    protected TypeToken<DCP_UpdateStockToThirdPlatform_OpenReq> getRequestType() {
        return new TypeToken<DCP_UpdateStockToThirdPlatform_OpenReq>(){};
    }

    @Override
    protected DCP_UpdateStockToThirdPlatform_OpenRes getResponseType() {
        return new DCP_UpdateStockToThirdPlatform_OpenRes();
    }

    @Override
    protected DCP_UpdateStockToThirdPlatform_OpenRes processJson(DCP_UpdateStockToThirdPlatform_OpenReq req) throws Exception {
        DCP_UpdateStockToThirdPlatform_OpenRes res = this.getResponse();
        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功！");
        String logStartStr = "【DCP内部调用】";
        String logFileName = "stockSync";
        if (req.getApiUser()!=null&&req.getApiUser().getAppType()!=null)
        {
            logStartStr ="【渠道类型"+req.getApiUser().getAppType()+"调用】";
            logFileName = req.getApiUser().getAppType().toLowerCase()+"-stockSync";
        }

        HelpTools.writelog_fileName(logStartStr+",门店shopId:"+req.getRequest().getShopId()+",异动单号billNo:"+req.getRequest().getBillNo(),logFileName);
        WebHookService.stockSync(req.getRequest().geteId(),req.getRequest().getShopId(),req.getRequest().getBillNo());

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_UpdateStockToThirdPlatform_OpenReq req) throws Exception {
        return null;
    }
}
