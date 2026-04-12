package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_StockOutEntryDetail
 * 服务说明：退货录入明细查询
 * @author jinzma
 * @since  2023-03-27
 */
public class DCP_StockOutEntryDetailRes extends JsonRes {
    private level1Elm datas;
    
    public level1Elm getDatas() {
        return datas;
    }
    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }
    
    @Data
    public class level1Elm{
        private String stockOutEntryNo;
        private List<level2Elm> pluList;
    }
    @Data
    public class level2Elm{
        private String item;
        private String pluNo;
        private String pluName;
        private String punit;
        private String punitName;
        private String prodDate;
        private String pqty;
        private String price;
        private String distriPrice;
        private String amt;
        private String distriAmt;
        private String memo;
        private String baseQty;
        private String unitRatio;
        private String baseUnit;
        private String featureNo;
        private String featureName;
        private String batchNo;
        private String isBatch;
        private String punitUdLength;
        private String baseUnitUdLength;
        private String stockManageType;
        private String stockQty;
        private String baseUnitName;
        private String bsNo;
        private String bsName;
        private String receiptOrg;
        private String receiptOrgName;
    }
}
