package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.req.DCP_COACreateReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_COADetailQueryRes extends JsonRes {

    private List<DCP_COADetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String coaRefID;
        private String accType;
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
        private String drCashChgCodeName;
        private String crCashChgCodeName;
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
        private List<AccountList> accountList;

        private String createBy;
        private String createByName;
        private String createDate;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyDate;
        private String modifyTime;
    }
    @Data
    public class AccountList{
        private String accountId;
        private String accountName;
    }
}
