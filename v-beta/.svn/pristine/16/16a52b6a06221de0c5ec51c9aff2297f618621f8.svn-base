package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/05/12
 */
@Getter
@Setter
public class DCP_BatchLocationStockAllocReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "")
        private List<Datas> datas;
    }

    @Getter
    @Setter
    public class Datas {
        @JSONFieldRequired(display = "出库项次")
        private String item;
        @JSONFieldRequired(display = "需求数量")
        private String pQty;
        @JSONFieldRequired(display = "业务单位")
        private String pUnit;
        @JSONFieldRequired(display = "商品编号")
        private String pluNo;
        @JSONFieldRequired(display = "出库仓库")
        private String warehouse;
    }

}