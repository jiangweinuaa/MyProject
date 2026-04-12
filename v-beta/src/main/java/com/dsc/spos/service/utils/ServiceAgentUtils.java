package com.dsc.spos.service.utils;

import com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.google.gson.reflect.TypeToken;

/**
 * 内部方法代理类
 */
public class ServiceAgentUtils<REQ extends JsonBasicReq, RES extends JsonBasicRes> {

    public RES agentService(REQ req, TypeToken<RES> typeToken) throws Exception {
        ParseJson pj = new ParseJson();
        String jsonReq = pj.beanToJson(req);

        DispatchService ds = DispatchService.getInstance();

        String jsonRes = ds.callService(jsonReq, StaticInfo.dao);
        return pj.jsonToBean(jsonRes, typeToken);
    }

    public boolean agentServiceSuccess(REQ req, TypeToken<RES> typeToken) throws Exception {
        RES res = agentService(req, typeToken);
        return res.isSuccess();
    }

}
