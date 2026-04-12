package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/03/26
 */
@Getter
@Setter
public class DCP_WoProdRepStatCreateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "法人组织")
        private String corp;
        @JSONFieldRequired(display = "日期")
        private String wDate;
        @JSONFieldRequired(display = "来源类型")
        private String woType;
        @JSONFieldRequired(display = "单据编号")
        private String porderNo;
    }



}