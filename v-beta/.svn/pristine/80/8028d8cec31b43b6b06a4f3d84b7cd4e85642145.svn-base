package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;

/**
 * 服务函数：DCP_EnterpriseChatSetQuery
 * 服务说明：企业微信基础设置查询
 * @author wangzyc
 * @since  2020-12-28
 */
public class DCP_EnterpriseChatSetQueryRes extends JsonBasicRes {

    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private String corpId; // 企业Id
        private String agentId; // 应用Id
        private String secret; // 密钥

        public String getCorpId() {
            return corpId;
        }

        public void setCorpId(String corpId) {
            this.corpId = corpId;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
