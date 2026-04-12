package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_LocationDeleteReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_LocationDeleteReq.LevelRequest request;

    @Data
    public class LevelRequest{
        @JSONFieldRequired
        private String orgNo;
        @JSONFieldRequired
        private String wareHouse;

        @JSONFieldRequired
        private List<LocationList> locationList;
    }

    @Data
    public class LocationList{
        @JSONFieldRequired
        private String location;
    }

}
