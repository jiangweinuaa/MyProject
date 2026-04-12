package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_RestrictGroupAlterReq extends JsonBasicReq {
    @JSONFieldRequired
    private DCP_RestrictGroupAlterReq.level1Elm request;

    @Data
    public class level1Elm {
        private String oprType;
        private String groupType;
        private String groupNo;
        private String status;
        private String memo;

        private List<Name_lang> name_lang;
        private List<DeptList> deptList;
        private List<EmpList> empList;
        private List<BizpartnerList> bizpartnerList;
        private List<GoodsList> goodsList;
        private List<OrgList> orgList;
        private List<WarehouseList> warehouseList;

    }

    @Data
    public class Name_lang {
        private String langType;
        private String name;
    }

    @Data
    public class DeptList{
        private String deptNo;
        private String status;
    }

    @Data
    public class EmpList{
        private String empNo;
        private String status;
    }

    @Data
    public class BizpartnerList{
        private String bizpartnerNo;
        private String status;
    }

    @Data
    public class GoodsList{
        private String attriType;
        private String attrValue;
        private String status;
    }

    @Data
    public class OrgList{
        private String orgNo;
        private String status;
    }

    @Data
    public class WarehouseList{
        @JSONFieldRequired
        private String organizationNo;
        @JSONFieldRequired
        private String warehouse;
        @JSONFieldRequired
        private String status;
    }
}
