package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_InterSettSettingCreateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_InterSettSettingCreateReq.LevelRequest request;

    @Data
    public class LevelRequest{

        private String status;
        private String processNo;
        @JSONFieldRequired
        private String businessType;
        @JSONFieldRequired
        private String supplyObject;
        @JSONFieldRequired
        private String demandObject;
        @JSONFieldRequired
        private String priceType;
        @JSONFieldRequired
        private String relationship;
        private String memo;
        private String createBy;
        private String create_Date;
        private String create_Time;
        private String modifyBy;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirm_Date;
        private String confirm_Time;
        private String cancelBy;
        private String cancel_Date;
        private String cancel_Time;
        private List<ObjectList> objectList;
    }

    @Data
    public class ObjectList{
        private String item;
        private String object;
        private String priceType;
        private String priceRatio;
        private String warehouse;
        private String status;
        private String supplyObject;
        private String demandObject;
    }



}
