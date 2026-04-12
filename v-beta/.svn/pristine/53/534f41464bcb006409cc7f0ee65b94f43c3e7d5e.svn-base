package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DCP_DepartmentAlterReq extends JsonBasicReq {

    @Getter
    @Setter
    private levelRequest request;


    @Getter
    @Setter
    public class levelRequest {

        private String oprType;
        private String deptNo;
        private String upperDept;
        private String orgNo;
        private String manager;
        private String status;
        private String responsibilityCenterType;
        private String respCenter;
        private String isProductGroup;
        private List<DepartLang> dept_lang;
        private List<DeptFNameLang> deptFname_lang;

    }

    @Getter
    @Setter
    public class DepartLang {
        private String langType;
        private String name;
    }

    @Getter
    @Setter
    public class DeptFNameLang {
        private String langType;
        private String name;
    }

}
