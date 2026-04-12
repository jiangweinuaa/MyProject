package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProductInDetailQueryRes extends JsonRes {

    private List<DCP_ProductInDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String bDate;
        private String productInNo;
        private String pGroupNo;
        private String pGroupName;
        private String createBy;
        private String createByName;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmTime;
        private String processStatus;
        private String processErpNo;
        private String processErpOrg;
        private String sourceReportNo;
        private String returnNo;
        private String returnStatus;
        private String departId;
        private String departName;

        private List<Detail> datas;
    }

    @Data
    public class ScrapList{
        private String reason;
        private String scrapQty;
    }

    @Data
    public class Detail{
        private String item;
        private String pluNo;
        private String pluName;
        private String spec;
        private String pUnit;
        private String pUName;
        private String pQty;
        private String batchNo;
        private String warehouse;
        private String warehouseName;
        private String baseUnit;
        private String baseUName;
        private String baseQty;
        private String sourceNo;
        private String scrapQty;
        private String featureNo;
        private String featureName;
        private List<ScrapList> scrapList;
    }
}
