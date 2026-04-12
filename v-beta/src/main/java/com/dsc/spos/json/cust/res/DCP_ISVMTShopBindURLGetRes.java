package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_ISVMTShopBindURLGetRes extends JsonRes {
    private responseDatas datas;

    public responseDatas getDatas() {
        return datas;
    }

    public void setDatas(responseDatas datas) {
        this.datas = datas;
    }

    public class responseDatas
    {
        private String url; //物流类型 4-达达 24-圆通

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
