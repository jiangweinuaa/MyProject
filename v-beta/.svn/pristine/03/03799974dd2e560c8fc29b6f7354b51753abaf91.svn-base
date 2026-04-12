package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProdTemplateQueryRes extends JsonRes {

    private List<DCP_ProdTemplateQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String templateId;
        private String templateName;
        private String status;
        private String restrictOrg;
        private String memo;
        private String createOpId;
        private String createOpName;
        private String createDeptId;
        private String createDeptName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
    }
}
