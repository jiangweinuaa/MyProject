package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DCP_BatchingDocDeleteReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_BatchingDocDeleteReq.levelRequest request;


    @Getter
    @Setter
    public class levelRequest {
        @JSONFieldRequired
        private List<Datas> datas;
    }

    @Data
    public class Datas{
        @JSONFieldRequired
        private String batchNo;
    }

}
