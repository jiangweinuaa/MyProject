package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

/**
 * @apiNote 商品收货
 * @since 2021-04-23
 * @author jinzma
 */
public class DCP_GoodsStockInProcessRes extends JsonBasicRes {

    private levelElm datas;

    public levelElm getDatas() {
        return datas;
    }

    public void setDatas(levelElm datas) {
        this.datas = datas;
    }

    public class levelElm{
    }

}
