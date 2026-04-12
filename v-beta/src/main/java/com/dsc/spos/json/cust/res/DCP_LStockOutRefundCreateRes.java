package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

public class DCP_LStockOutRefundCreateRes extends JsonRes
{

    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm {
        private String lStockOutNo;

        public String getlStockOutNo() {
            return lStockOutNo;
        }

        public void setlStockOutNo(String lStockOutNo) {
            this.lStockOutNo = lStockOutNo;
        }
    }




}
