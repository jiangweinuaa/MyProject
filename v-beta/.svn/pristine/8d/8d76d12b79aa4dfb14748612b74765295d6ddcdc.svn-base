package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PayClassSubjectDetailQueryRes extends JsonRes {

    private List<Request> request;

    @NoArgsConstructor
    @Data
    public class Request {
        private String accountId;
        private String account;
        private String coaRefID;
        private String status;
        private List<ClassList> classList;

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
    public class ClassList {
        private String item;
        private String classNo;
        private String className;
        private String debitSubject;
        private String debitSubjectName;
        private String paySubject;
        private String paySubjectName;
        private String revSubject;
        private String revSubjectName;
        private String advSubject;
        private String advSubjectName;
    }
}

