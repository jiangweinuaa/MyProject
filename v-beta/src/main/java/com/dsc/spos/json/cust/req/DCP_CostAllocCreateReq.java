package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/19
 */
@Getter
@Setter
public class DCP_CostAllocCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "账套编号")
        private String accountID;
        @JSONFieldRequired(display = "期别")
        private double period;
        @JSONFieldRequired(display = "年度")
        private double year;
        @JSONFieldRequired(display = "成本中心")
        private String costCenter;
        @JSONFieldRequired(display = "分摊类型")
        private String allocType;
        @JSONFieldRequired(display = "")
        private List<AllocList> allocList;
        @JSONFieldRequired(display = "账套")
        private String account;
        @JSONFieldRequired(display = "")
        private String status;
    }

    @Getter
    @Setter
    public class AllocList {
        private String deptNature;
        @JSONFieldRequired(display = "分摊来源")
        private String allocSource;
        @JSONFieldRequired(display = "项次")
        private String item;
        private String organizationID;
        @JSONFieldRequired(display = "状态")
        private String staus;
        @JSONFieldRequired(display = "金额")
        private String amt;
        @JSONFieldRequired(display = "部门名称")
        private String dept;
        private String org_Name;
        @JSONFieldRequired(display = "部门编号")
        private String deptNo;
        @JSONFieldRequired(display = "分摊公式")
        private String allocFormula;
        private String allocWeight;
    }

}