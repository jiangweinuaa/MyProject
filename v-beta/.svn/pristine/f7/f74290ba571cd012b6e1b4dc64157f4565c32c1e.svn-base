package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ApSetupSubjectDetailQueryRes extends JsonRes
{
    private DCP_ApSetupSubjectDetailQueryRes.level1Elm datas;

    @Data
    public class level1Elm {
        private String accountId;
        private String status;
        private String setupType;
        private String coaRefID;
        private String accountName;

        private List<SetupTypeList> setupList;

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
    public class SetupTypeList{
        private String accountId;
        private String accountName;
        private String setupType;
        private String setupItem;
        private String setupDiscrip;
        private String subjectId;
        private String subjectName;
        private String subjectDBCR;
        private String subjectSumType;
        private String discSubject;
        private String discSubjectName;
        private String memo;

    }

}

