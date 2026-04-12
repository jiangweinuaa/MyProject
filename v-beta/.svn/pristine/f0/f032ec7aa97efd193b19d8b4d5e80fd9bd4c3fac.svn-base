package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DCP_ReconDetailQueryReq extends JsonBasicReq {


    @JSONFieldRequired
    private DCP_ReconDetailQueryReq.Request request;


    @Getter
    @Setter
    public class Request {

        @JSONFieldRequired
        private String dataType;
        @JSONFieldRequired
        private String reconNo;


    }

}
