package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_InterSettSettingQueryRes extends JsonRes {

    private List<DCP_InterSettSettingQueryRes.levelElm> datas;

    @Data
    public class levelElm{
        private String status;
        private String processNo;
        private String businessType;
        private String supplyObject;
        private String supplyObjectName;
        private String demandObject;
        private String demandObjectName;
        private String priceType;
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

    }

}
