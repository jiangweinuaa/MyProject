package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_STakeTemplateDetailQueryRes  extends JsonRes {

    private List<DCP_STakeTemplateDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String templateNo;
        private String templateName;
        private String taskWay;
        private String isBtake;
        private String timeType;
        private String timeValue;
        private String status;
        private String stockTakeCheck;
        private String shopType;//1-全部门店、2-指定门店
        private String isAdjustStock;
        private String rangeWay;
        private String isShowZStock;
        private String createDate;
        private String createTime;
        private String createBy;
        private String createByName;
        private String createDeptId;
        private String createDeptName;
        private String modifyDate;
        private String modifyTime;
        private String modifyBy;
        private String modifyByName;

        private List<DCP_STakeTemplateDetailQueryRes.Detail> detail;
        private List<DCP_STakeTemplateDetailQueryRes.OrgList> orgList;

    }

    @Data
    public class Detail{
        private String item;
        private String pluNo;
        private String pluName;
        private String category;
        private String categoryName;
        private String pUnit;
        private String pUnitName;
        private String status;
    }

    @Data
    public class OrgList{
        private String organizationNo;
        private String organizationName;
        private String warehouse;
        private String warehouseName;
        private String status;
    }

}
