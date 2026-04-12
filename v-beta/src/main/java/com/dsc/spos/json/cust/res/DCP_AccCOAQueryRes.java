package com.dsc.spos.json.cust.res;

import com.alibaba.fastjson.annotation.JSONField;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_AccCOAQueryRes extends JsonRes {

    private List<Request> datas;

    @NoArgsConstructor
    @Data
    public class Request {
        private String coaRefID;

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

        private List<AccountList> accountList;
    }

    @NoArgsConstructor
    @Data
    public class AccountList {
        private String accountId;
        private String accountName;

        private List<CoaList> coaList;
    }

    @NoArgsConstructor
    @Data
    public class CoaList {
        private String accountId;
        private String accountName;
        private String subjectId;
        private String subjectName;
        private String auxiliaryType;
        private String memo;
        private String coaRefID;
        private String subjectCat;
        private String upSubjectCat;
        private String levelID;
        private String subjectProperty;
        private String subjectType;
        private String direction;
        private String isDirection;
        private String expType;
        private String finAnalSource;
        private String isCashSubject;
        private String isEnableDptMng;
        private String isEnableTradObjMng;
        private String isEnableProdCatMng;
        private String isEnableManMng;
        private String isMultiCurMng;
        private String isSubsysSubject;
        private String drCashChgCode;
        private String crCashChgCode;
        private String isFreeChars1;
        @JSONField(name = "freeChars1-TypeID")
        private String freeChars1TypeID;
        @JSONField(name = "freeChars1-CtrlMode")
        private String freeChars1CtrlMode;
        private String isFreeChars2;
        @JSONField(name = "freeChars2-TypeID")
        private String freeChars2TypeID;
        @JSONField(name = "freeChars2-CtrlMode")
        private String freeChars2CtrlMode;
        private String isFreeChars3;
        @JSONField(name = "freeChars3-TypeID")
        private String freeChars3TypeID;
        @JSONField(name = "freeChars3-CtrlMode")
        private String freeChars3CtrlMode;
        private String status;
    }
}

