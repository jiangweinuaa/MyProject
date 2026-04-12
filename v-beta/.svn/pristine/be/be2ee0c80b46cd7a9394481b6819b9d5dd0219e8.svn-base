package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_StockOutEntryUpdate
 * 服务说明：退货录入修改
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryUpdateRes extends JsonBasicRes {
    private level1Elm datas;
    
    public level1Elm getDatas() {
        return datas;
    }
    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }
    
    @Data
    public class level1Elm{
        private List<level2Elm> pluList;
    }
    @Data
    public class level2Elm{
        private String pluNo;
        private String pluName;
    }
}
