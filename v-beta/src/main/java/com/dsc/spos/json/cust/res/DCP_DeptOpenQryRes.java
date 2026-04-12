package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_DeptOpenQryRes extends JsonRes {

    private List<DCP_DeptOpenQryRes.level1Elm> datas;

    @Data
    public class level1Elm
    {
        private String status;
        private String deptNo;
        private String sName;
        private String fullName;
        private String isProductGroup;

        private String corp;
        private String corpName;

    }
}