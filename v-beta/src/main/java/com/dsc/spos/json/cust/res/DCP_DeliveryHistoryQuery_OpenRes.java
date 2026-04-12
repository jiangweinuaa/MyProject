package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DeliveryHistoryQuery_OpenRes extends JsonBasicRes {

    private level1Elm datas;

    @Data
    public class level1Elm{
        private String deliveryStatus;
        private String logisticCode;
        private String shipperCode;

        private List<level2Elm> traces;

    }

    @Data
    public class level2Elm{
        private String deliveryAction;
        private String deliveryAcceptStation;
        private String delieveryAcceptTime;
        private String deliveryLocation;
    }
}
