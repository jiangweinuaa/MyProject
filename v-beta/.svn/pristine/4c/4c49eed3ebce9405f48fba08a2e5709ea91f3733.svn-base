package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_CategorySubjectDetailQueryRes extends JsonRes {

    private List<DCP_CategorySubjectDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String accountId;
        private String accountName;
        private String status;
        private String coaRefId;

        private List<DCP_CategorySubjectDetailQueryRes.SetupList> setupList;

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
        private String category;
        private String categoryName;
        private String coaRefId;
        private String revSubject;
        private String revSubjectName;
        private String costSubject;
        private String costSubjectName;
        private String invSubject;
        private String invSubjectName;
        private String discSubject;
        private String discSubjectName;
        private String memo;

    }
}
