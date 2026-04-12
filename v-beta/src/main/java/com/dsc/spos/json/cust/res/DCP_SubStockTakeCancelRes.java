package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

/*
 * 服务函数：DCP_SubStockTakeCancel
 * 服务说明：盘点子任务初盘导入撤销
 * @author jinzma
 * @since  2021-03-11
 */
public class DCP_SubStockTakeCancelRes extends JsonRes {
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
