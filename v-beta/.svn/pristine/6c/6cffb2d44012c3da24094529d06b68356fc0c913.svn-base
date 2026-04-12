package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_RestrictGroupQueryRes extends JsonRes {
    private List<DCP_RestrictGroupQueryRes.level1Elm> datas;

    @Data
    public class level1Elm {
        private String groupNo;
        private String groupName;
        private String groupType;
        private String memo;
        private String status;
        private String totBizCqty;
        private String creatorID;
        private String creatorName;
        private String creatorDeptID;
        private String creatorDeptName;
        private String create_datetime;
        private String lastmodifyID;
        private String lastmodifyName;
        private String lastmodify_datetime;

        private List<DeptList> deptList;
        private List<EmpList> empList;
        private List<BizpartnerList> bizpartnerList;
        private List<Goodslist> goodslist;
        private List<Lang_list> lang_list;
        private List<Org_list> org_list;
        private List<WarehouseList> warehouseList;
    }

    @Data
    public class DeptList{
        private String deptNo;
        private String deptName;
        private String status;
    }

    @Data
    public class EmpList{

        private String empNo;
        private String empName;
        private String status;
    }

    @Data
    public class BizpartnerList{
        private String bizpartnerNo;
        private String bizpartnerName;
        private String status;
    }

    @Data
    public class Goodslist{
        private String attriType;
        private String atrriValue;
        private String status;
    }

    @Data
    public class Lang_list{
        private String lang_type;
        private String desc;
    }

    @Data
    public class Org_list{
        private String orgNo;
        private String orgName;
        private String status;
    }

    @Data
    public class WarehouseList{
        private String organizationNo;
        private String organizationName;
        private String warehouse;
        private String warehouseName;
        private String status;
    }

}
