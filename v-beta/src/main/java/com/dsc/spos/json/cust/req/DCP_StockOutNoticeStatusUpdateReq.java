package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author 01029
 */
@NoArgsConstructor
@Data
public class DCP_StockOutNoticeStatusUpdateReq extends JsonBasicReq {


    @JSONFieldRequired
    private Request request;

    @NoArgsConstructor
    @Data
    public class Request {
        @JSONFieldRequired
        private String opType;
        private String billType;
        @JSONFieldRequired
        private String billNo;
        private List<Detail> detail;
    }

    @NoArgsConstructor
    @Data
    public class Detail {
        private String item;
    }

}
