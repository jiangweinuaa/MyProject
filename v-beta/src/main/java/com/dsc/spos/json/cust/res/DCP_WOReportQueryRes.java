package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_WOReportQueryRes extends JsonRes {

    private List<DatasDTO> datas;

    @NoArgsConstructor
    @Data
    public  class DatasDTO {
        private String reportNo;
        private String bDate;
        private String accountDate;
        private String memo;
        private String status;
        private String createBy;
        private String createByName;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyTime;
        private String accountBy;
        private String accountByName;
        private String accountTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelTime;
        private String processStatus;
        private String processErpNo;
        private String processErpOrg;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
    }
}
