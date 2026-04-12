package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_PickGroupUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request{

        @JSONFieldRequired
        private String pickGroupNo;
        @JSONFieldRequired
        private String pickGroupName;
        @JSONFieldRequired
        private String warehouse;
        private String wareRegionNo;
        @JSONFieldRequired
        private String pickType;
        @JSONFieldRequired
        private String rangeType;
        @JSONFieldRequired
        private String objectRange;
        @JSONFieldRequired
        private String status;
        @JSONFieldRequired
        private List<RangeList> rangeList;
        private List<ObjectList> objectList;

    }

    @Getter
    @Setter
    public class RangeList{
        @JSONFieldRequired
        private String type;
        @JSONFieldRequired
        private String code;
    }

    @Getter
    @Setter
    public class ObjectList{
        @JSONFieldRequired
        private String type;
        @JSONFieldRequired
        private String code;
        @JSONFieldRequired
        private String sortId;
    }

}
