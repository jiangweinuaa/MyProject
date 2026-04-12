package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_RouteCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private DCP_RouteCreateReq.levelRequest request;

    @Data
    public class levelRequest{

        @JSONFieldRequired
        private String routeNo;
        @JSONFieldRequired
        String routeName;
        @JSONFieldRequired
        String status;
        String memo;
        public List<Detail> detail;
    }

    @Data
    public class Detail{
        @JSONFieldRequired
        private String sorting;
        @JSONFieldRequired
        private String routeType;
        @JSONFieldRequired
        private String code;
        @JSONFieldRequired
        private String name;
        private String address;
        @JSONFieldRequired
        private String status;

    }

}
