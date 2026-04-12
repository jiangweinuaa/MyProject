package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

/**
 * StockTakeCreateDCPRes 
 * @author JZMA
 * @since  2018-11-21
 */

public class DCP_StockTakeCreateRes extends JsonBasicRes {
    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }
    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private String stockTakeNo;

        public String getStockTakeNo() {
            return stockTakeNo;
        }
        public void setStockTakeNo(String stockTakeNo) {
            this.stockTakeNo = stockTakeNo;
        }
    }
}