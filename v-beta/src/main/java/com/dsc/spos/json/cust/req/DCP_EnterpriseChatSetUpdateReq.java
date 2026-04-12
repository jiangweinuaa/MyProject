package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 服务函数：DCP_EnterpriseChatSetUpdate
 * 服务说明：企业微信基础设置更新
 * @author wangzyc
 * @since  2020-12-28
 */
public class DCP_EnterpriseChatSetUpdateReq extends JsonBasicReq {

    private level1Elm request;

    public level1Elm getRequest() {
        return request;
    }

    public void setRequest(level1Elm request) {
        this.request = request;
    }

    public class level1Elm{
        private String corpId; // 企业ID
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
