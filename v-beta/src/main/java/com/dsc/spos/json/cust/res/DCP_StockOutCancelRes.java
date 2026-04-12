package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

/**
 * @apiNote 调拨出库撤销
 * @since 2021-04-14
 * @author jinzma
 */
public class DCP_StockOutCancelRes extends JsonBasicRes {
    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }
    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{

    }
}
