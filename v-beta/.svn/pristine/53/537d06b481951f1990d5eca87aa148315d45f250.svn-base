package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_COAOpenQueryRes extends JsonRes {

    private List<DCP_COAOpenQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {

        private String subjectType;
        private List<SubjectList> subjectList;

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
    public class SubjectList{

        private String subjectId;
        private String subjectName;
        private String subjectCat;
    }

}
