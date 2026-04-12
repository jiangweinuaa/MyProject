package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_EnterpriseChatSetQueryReq;
import com.dsc.spos.json.cust.res.DCP_EnterpriseChatSetQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * 服務函數：DCP_EnterpriseChatSetQuery
 * 服务说明：企业微信基础设置查询
 *
 * @author wangzyc
 * @since 2020-12-28
 */
public class DCP_EnterpriseChatSetQuery extends SPosBasicService<DCP_EnterpriseChatSetQueryReq, DCP_EnterpriseChatSetQueryRes> {
    @Override
    protected boolean isVerifyFail(DCP_EnterpriseChatSetQueryReq req) throws Exception {
        return false;
    }

    @Override
    protected TypeToken<DCP_EnterpriseChatSetQueryReq> getRequestType() {
        return new TypeToken<DCP_EnterpriseChatSetQueryReq>(){};
    }

    @Override
    protected DCP_EnterpriseChatSetQueryRes getResponseType() {
        return new DCP_EnterpriseChatSetQueryRes();
    }

    @Override
    protected DCP_EnterpriseChatSetQueryRes processJson(DCP_EnterpriseChatSetQueryReq req) throws Exception {
        DCP_EnterpriseChatSetQueryRes res = null;
        res = this.getResponseType();
        String sql = null;

        try {
            sql = this.getQuerySql(req);
            List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
            res.setDatas(res.new level1Elm());
            if(getQDataDetail.isEmpty() ==false && getQDataDetail.size()>0){

                for (Map<String, Object> oneData : getQDataDetail) {
                    DCP_EnterpriseChatSetQueryRes.level1Elm level1Elm = res.new level1Elm();
                    String corpid = oneData.get("CORPID").toString();
                    String agentid = oneData.get("AGENTID").toString();
                    String secret = oneData.get("SECRET").toString();

                    level1Elm.setCorpId(corpid);
                    level1Elm.setAgentId(agentid);
                    level1Elm.setSecret(secret);
                    res.setDatas(level1Elm);
                    level1Elm = null;
                }
            }

        } catch (Exception e) {
            res.setSuccess(false);
            res.setServiceStatus("200");
            res.setServiceDescription("服务执行异常！" +e.getMessage());
        }

        res.setSuccess(true);
        res.setServiceStatus("000");
        res.setServiceDescription("服务执行成功");
        return res;
    }

    @Override
    protected void processRow(Map<String, Object> row) throws Exception {

    }

    @Override
    protected String getQuerySql(DCP_EnterpriseChatSetQueryReq req) throws Exception {
        String sql = null;
        StringBuffer sqlbuf = new StringBuffer();
        sqlbuf.append("select CORPID,AGENTID,SECRET FROM DCP_ENTERPRISECHATSET where EID = '"+req.geteId()+"'");
        sql = sqlbuf.toString();
        return sql;
    }
}
