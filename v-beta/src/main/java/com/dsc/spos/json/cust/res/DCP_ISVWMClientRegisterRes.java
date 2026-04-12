package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_ISVWMClientRegisterRes extends JsonBasicRes {
    private responseDatas datas;

    public responseDatas getDatas() {
        return datas;
    }

    public void setDatas(responseDatas datas) {
        this.datas = datas;
    }

    public class responseDatas {
        private String clientNo;

        public String getClientNo() {
            return clientNo;
        }

        public void setClientNo(String clientNo) {
            this.clientNo = clientNo;
        }
    }

}
