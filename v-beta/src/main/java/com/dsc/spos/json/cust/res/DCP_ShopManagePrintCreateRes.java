package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_ShopManagePrintCreateRes extends JsonBasicRes {

    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm
    {
        private  String batchKey;

        public String getBatchKey() {
            return batchKey;
        }

        public void setBatchKey(String batchKey) {
            this.batchKey = batchKey;
        }
    }
}
