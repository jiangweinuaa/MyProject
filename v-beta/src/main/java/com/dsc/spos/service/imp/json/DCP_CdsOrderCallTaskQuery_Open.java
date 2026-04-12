package com.dsc.spos.service.imp.json;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.json.cust.req.DCP_CdsOrderCallTaskQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_CdsOrderCallTaskQuery_OpenRes;
import com.dsc.spos.redis.RedisPosPub;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import org.eclipse.jetty.util.ajax.JSON;

import java.util.ArrayList;
import java.util.Map;

/**
 * @description: CDS语音播报任务查询
 * @author: wangzyc
 * @create: 2022-05-24
 */
public class DCP_CdsOrderCallTaskQuery_Open extends SPosBasicService<DCP_CdsOrderCallTaskQuery_OpenReq, DCP_CdsOrderCallTaskQuery_OpenRes> {
    @Override
    protected boolean isVerifyFail(DCP_CdsOrderCallTaskQuery_OpenReq req) throws Exception {
        boolean isFail = false;
        StringBuffer errMsg = new StringBuffer("");
        DCP_CdsOrderCallTaskQuery_OpenReq.level1Elm request = req.getRequest();
        if (request == null) {
            errMsg.append("request不能为空 ");
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        if (Check.Null(request.getShopId())) {
            errMsg.append("门店编号不能为空,");
            isFail = true;
        }

        if (isFail) {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
        }

        return isFail;
    }

    @Override
    protected TypeToken<DCP_CdsOrderCallTaskQuery_OpenReq> getRequestType() {
        return new TypeToken<DCP_CdsOrderCallTaskQuery_OpenReq>(){};
    }

    @Override
    protected DCP_CdsOrderCallTaskQuery_OpenRes getResponseType() {
        return new DCP_CdsOrderCallTaskQuery_OpenRes();
    }

    @Override
    protected DCP_CdsOrderCallTaskQuery_OpenRes processJson(DCP_CdsOrderCallTaskQuery_OpenReq req) throws Exception {
        DCP_CdsOrderCallTaskQuery_OpenRes res = this.getResponseType();
        try {
            String eId = req.getRequest().getEId();
            String shopId = req.getRequest().getShopId();

            String key="cdsCallTask" + ":" +eId +":" + shopId;
            RedisPosPub Rpp = new RedisPosPub();
            Map<String, String> allHashMap = Rpp.getALLHashMap(key);

            DCP_CdsOrderCallTaskQuery_OpenRes.level1Elm level1Elm = res.new level1Elm();
            res.setDatas(level1Elm);
            level1Elm.setTaskList(new ArrayList<>());
            for (Map.Entry<String, String> entry : allHashMap.entrySet()) {
                DCP_CdsOrderCallTaskQuery_OpenRes.level2Elm level2Elm = JSONObject.parseObject(JSONObject.toJSONString(JSON.parse(entry.getValue())), DCP_CdsOrderCallTaskQuery_OpenRes.level2Elm.class);
                level1Elm.getTaskList().add(level2Elm);
            }
            res.setSuccess(true);
            res.setServiceStatus("000");
            res.setServiceDescription("服务执行成功");
        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行失败!" + e.getMessage());
        }

        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_CdsOrderCallTaskQuery_OpenReq req) throws Exception {
        return null;
    }
}
