package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BsNoSetSubjectDetailQueryRes extends JsonRes {

    private List<DCP_BsNoSetSubjectDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String accountId;
        private String accountName;
        private String status;
        private String coaRefID;

        private List<DCP_BsNoSetSubjectDetailQueryRes.SetupList> setupList;

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

    @Data
    public class SetupList{
        private String accountId;
        private String accountName;
        private String bsNo;
        private String bsName;
        private String coaRefId;
        private String manuExpSubject;
        private String manuExpSubjectName;

        private String salesExpSubject;
        private String salesExpSubjectName;

        private String mgmtExpSubject;
        private String mgmtExpSubjectName;

        private String rdExpSubject;
        private String rdExpSubjectName;
        private String memo;

    }
}
