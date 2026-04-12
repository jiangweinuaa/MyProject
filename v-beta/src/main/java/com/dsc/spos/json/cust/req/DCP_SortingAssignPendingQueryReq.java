package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_SortingAssignPendingQueryReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {

        private List<ObjectList> objectList;

        private List<String> routeNo;
        @JSONFieldRequired
        private List<String> pTemplateNo;
        private String warehouse;
        @JSONFieldRequired
        private String dateType;
        @JSONFieldRequired
        private String beginDate;
        @JSONFieldRequired
        private String endDate;
        private String keyTxt;


    }

    @Getter
    @Setter
    public class ObjectList {
        private String objectType;
        private List<String> objectId;
    }
}
