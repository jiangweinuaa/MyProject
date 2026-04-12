package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_KdsBaseSetQuery_OpenReq;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.json.cust.res.DCP_KdsBaseSetQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @description: KDS基础设置查询
 * @author: wangzyc
 * @create: 2021-09-18
 */
public class DCP_KdsBaseSetQuery_Open extends SPosBasicService<DCP_KdsBaseSetQuery_OpenReq, DCP_KdsBaseSetQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_KdsBaseSetQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_KdsBaseSetQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (Check.Null(request.getMachineId())) {
            errMsg.append("机台编号不能为空");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_KdsBaseSetQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_KdsBaseSetQuery_OpenReq>() {
        };
    }

    @Override
    protected DCP_KdsBaseSetQuery_OpenRes getResponseType() {
        return new DCP_KdsBaseSetQuery_OpenRes();
    }

    @Override
    protected DCP_KdsBaseSetQuery_OpenRes processJson(DCP_KdsBaseSetQuery_OpenReq req) throws Exception {
        DCP_KdsBaseSetQuery_OpenRes res = this.getResponseType();
        res.setDatas(res.new level1Elm());
        try {
            String sql = "SELECT * FROM DCP_KDSBASICSET WHERE eid = ? AND SHOPID =? AND MACHINEID = ?";
            String[] parms = {req.geteId(), req.getRequest().getShopId(), req.getRequest().getMachineId()};
            List<Map<String, Object>> data = this.doQueryData(sql, parms);
            if (!CollectionUtils.isEmpty(data)) {
                String overTime = data.get(0).get("OVERTIME").toString();
                String miniSendMsg = data.get(0).get("MINISENDMSG").toString();
                res.getDatas().setOverTime(overTime);
                res.getDatas().setMiniSendMsg(miniSendMsg);
            }
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
    protected String getQuerySql(DCP_KdsBaseSetQuery_OpenReq req) throws Exception {
        return null;
    }
}
