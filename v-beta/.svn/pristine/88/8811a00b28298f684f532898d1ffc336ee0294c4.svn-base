package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_COAUpdateReq extends JsonBasicReq {

    private DCP_COAUpdateReq.levelRequest request;

    @Data
    public class levelRequest {
        private String accType;
        private String coaRefID;
        private String subjectId;
        private String subjectName;
        private String auxiliaryType;
        private String memo;
        private String subjectCat;
        private String upSubjectId;
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
        private String freeChars1TypeId;
        private String freeChars1CtrlMode;
        private String isFreeChars2;
        private String freeChars2TypeId;
        private String freeChars2CtrlMode;
        private String isFreeChars3;
        private String freeChars3TypeId;
        private String freeChars3CtrlMode;
        private String status;
        private String firstSubjectId;
        private List<DCP_COAUpdateReq.AccountList> accountList;
    }

    @Data
    public class AccountList{
        private String accountId;
        private String accountName;
    }

}
