package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_StockOutEntryQuery
 * 服务说明：退货录入查询
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryQueryRes extends JsonRes {
    private level1Elm datas;
    
    public level1Elm getDatas() {
        return datas;
    }
    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }
    
    public class level1Elm {
        private List<level2Elm> entrylist;
        
        public List<level2Elm> getEntrylist() {
            return entrylist;
        }
        public void setEntrylist(List<level2Elm> entrylist) {
            this.entrylist = entrylist;
        }
    }
    
    @Data
    public class level2Elm {
        private String stockOutEntryNo;
        private String bDate;
        private String memo;
        private String status;
        private String deliveryNo;
        private String bsNo;
        private String bsName;
        private String warehouse;
        private String warehouseName;
        private String totCqty;
        private String totPqty;
        private String totAmt;
        private String totDistriAmt;
        private String createBy;
        private String createByName;
        private String createDate;
        private String modifyBy;
        private String modifyByName;
        private String modifyDate;
        private String confirmBy;
        private String confirmByName;
        private String confirmDate;
    }
    
}
