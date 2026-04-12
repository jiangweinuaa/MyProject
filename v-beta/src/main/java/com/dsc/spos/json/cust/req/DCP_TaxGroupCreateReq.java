package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/02/24
 */
@Getter
@Setter
public class DCP_TaxGroupCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "税分类名称")
        private String taxGroupName;
        @JSONFieldRequired(display = "关联品类/商品")
        private List<GoodsList> goodsList;
        private String memo;
        @JSONFieldRequired(display = "税别编码")
        private String taxCode;
        @JSONFieldRequired(display = "税分类编码")
        private String taxGroupNo;
        @JSONFieldRequired(display = "状态：-1未启用100已启用0已禁用")
        private String status;
    }

    @Getter
    @Setter
    public class GoodsList {
        @JSONFieldRequired(display = "属性编码")
        private String attrId;
        @JSONFieldRequired(display = "属性类型")
        private String attrType;
        @JSONFieldRequired(display = "状态：-1未启用100已启用0已禁用")
        private String status;
    }

}