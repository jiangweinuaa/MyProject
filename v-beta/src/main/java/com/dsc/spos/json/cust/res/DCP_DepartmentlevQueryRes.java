package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DCP_DepartmentlevQueryRes extends JsonRes {
    private List<level1Elm> datas;

    @Getter
    @Setter
    public class level1Elm {
        private String departNo;
        private String departName;
        private String memo;
        private String status;
        private String upDepartNo;
        private String upDepartName;
        private String orgNo;
        private String orgName;
        private String manager;
        private String managerName;
        private int staffCnt;
        private String belCorp;
        private String belCorpName;
        private String responsibilityCenterType;
        private String respCenter;
        private String isProductGroup;

        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;

        private List<level1Elm> children;

        private List<DepartLang> depart_list;
        private List<DepartFLang> deptFname_list;

    }

    @Getter
    @Setter
    public static class DepartLang {
        private String langType;
        private String name;
    }


    @Getter
    @Setter
    public static class DepartFLang {
        private String langType;
        private String name;
    }


}
