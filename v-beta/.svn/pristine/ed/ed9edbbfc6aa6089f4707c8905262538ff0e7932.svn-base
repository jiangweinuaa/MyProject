package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_StockTakeToMStockOutReq extends JsonBasicReq {

    @JSONFieldRequired
    private RequestLevel request;

    @NoArgsConstructor
    @Data
    public  class RequestLevel {
        @JSONFieldRequired
        private String stockTakeNo;
        private String accountDate;
        @JSONFieldRequired
        private List<DetailListLevel> detailList;

    }

    @NoArgsConstructor
    @Data
    public  class DetailListLevel {
        @JSONFieldRequired
        private String oOType;
        @JSONFieldRequired
        private String oOfNo;
        @JSONFieldRequired
        private String oOItem;
        @JSONFieldRequired
        private String pluNo;
        @JSONFieldRequired
        private String pUnit;
        @JSONFieldRequired
        private String prodQty;
        @JSONFieldRequired
        private String bomNo;
        @JSONFieldRequired
        private String versionNum;

        private List<DetailMaterialLevel> detailMaterialLevel;
    }

    @Data
    public class DetailMaterialLevel{

        private String ooType;
        private String oofNo;
        private String ooItem;

        private String unit;//主件单位
        private String batchQty;//配方基数
        private String materialPluNo;
        private String materialUnit;
        private String materialQty;//原料数量 耗用量
        private String prodMaterialQty;//生产数量 转换为配方单位的数量
        private String qty;//底数
        private String sItem;
        private String pItem;
        private String item;

        private String zItem;
        private String processNo;

    }



}
