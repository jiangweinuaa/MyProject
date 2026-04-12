package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_BatchDetailReq extends JsonBasicReq {

    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        private List<String> category;
        private List<String> pluNo;
        private String dateType;
        private String beginDate;
        private String endDate;
        private String supplierType;
        private List<String> supplierId;
        private List<String> status;
        private String keyTxt;
    }
}
