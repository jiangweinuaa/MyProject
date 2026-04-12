package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/10
 */
@Getter
@Setter
public class DCP_CustGroupCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {

        private String memo;
        @JSONFieldRequired(display = "客户范围列表")
        private List<GroupList> groupList;
        @JSONFieldRequired(display = "客户组名称")
        private String custGroupName;
        @JSONFieldRequired(display = "状态")
        private String status;
    }

    @Getter
    @Setter
    public class GroupList {
        @JSONFieldRequired(display = "组别")
        private String item;
        @JSONFieldRequired(display = "客户属性编号")
        private String attrId;
        @JSONFieldRequired(display = "客户属性类型")
        private String attrType;
        @JSONFieldRequired(display = "状态码")
        private String status;
    }

}