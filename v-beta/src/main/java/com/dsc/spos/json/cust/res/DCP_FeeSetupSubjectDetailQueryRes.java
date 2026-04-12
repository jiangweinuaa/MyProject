package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_FeeSetupSubjectDetailQueryRes extends JsonRes {

    private List<Request> request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String accountId;
        private String account;
        private String coaRefID;
        private String status;

        private List<SetupList> setupList;

        private String createBy;
        private String createByName;
        private String create_Date;
        private String create_Time;
        private String modifyBy;

        private String modifyByName;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirmByName;
        private String confirm_Date;
        private String confirm_Time;
        private String cancelBy;
        private String cancelByName;
        private String cancel_Date;
        private String cancel_Time;
    }

    @NoArgsConstructor
    @Data
    public class SetupList {
        private String item;
        private String accountID;
        private String coaRefID;
        private String fee;
        private String feeName;
        private String feeNature;
        private String accSubject;
        private String accSubjectName;
        private String revSubject;
        private String revSubjectName;
        private String advSubject;
        private String advSubjectName;
    }
}

