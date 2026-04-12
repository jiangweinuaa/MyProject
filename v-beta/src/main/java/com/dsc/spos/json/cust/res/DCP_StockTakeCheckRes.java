package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
/**
 * @apiNote 盘点新增检查
 * @author jinzma
 * @since  2021-07-13
 */
public class DCP_StockTakeCheckRes extends JsonRes {
    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }
    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private String memo;

        public String getMemo() {
            return memo;
        }
        public void setMemo(String memo) {
            this.memo = memo;
        }
    }
}
