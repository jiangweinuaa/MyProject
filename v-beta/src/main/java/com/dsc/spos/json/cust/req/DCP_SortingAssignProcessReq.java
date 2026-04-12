package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.analysis.function.Add;

import java.util.List;

@Getter
@Setter
public class DCP_SortingAssignProcessReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {

        @JSONFieldRequired
        private String billNo;
        @JSONFieldRequired
        private String opType;

        private List<AddList> addList;


    }
    @Getter
    @Setter
    public class AddList{
        private String billNo;
        private String item;
    }

}
