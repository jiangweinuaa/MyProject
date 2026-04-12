package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class MES_StockDetailCreateReq extends JsonBasicReq
{

    private levelRequest request;

    @Data
    public class levelRequest
    {
        private List<level1Elm> mesStockDetail;

    }

    @Data
    public class level1Elm
    {
        private String eId;//
        private String organizationNo;//
        private String billType;//
        private String erpBillNo;//
        private String erpItem;//
        private String erpSeq;//
        private String direct;//
        private String bDate;//
        private String accountDate;//
        private String pluNo;//
        private String featureNo;//
        private String batchNo;//
        private String prodDate;//
        private String warehouse;//
        private String location;//
        private String baseUnit;//
        private String baseQty;//
        private String price;//
        private String amt;//
        private String userId;//

    }


}
