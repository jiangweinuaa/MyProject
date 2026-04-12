package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BomBulkUpdateReq extends JsonBasicReq
{

    @JSONFieldRequired
    private DCP_BomBulkUpdateReq.LevelRequest request;

    @Data
    public class LevelRequest{

        @JSONFieldRequired
        private String operation;
        @JSONFieldRequired
        private String materialPluNo;
        private String materialUnit;
        private String newMaterialPluNo;
        private String newMaterialUnit;
        private String oldRawQty;
        private String newRawQty;
        @JSONFieldRequired
        private String updBomProcess;
        private String updMaterialUnit;
        private String updByRaw;
        private String updByMaterialQty;
        private String materialQty;
        private String updQty;
        private String qty;
        private String updLoseRate;
        private String loseRate;
        private String updBDate;
        private String materialBDate;
        private String updEDate;
        private String materialEDate;
        private String updIsPick;
        private String isPick;
        private String updIsBatch;
        private String isBatch;
        private String updPWGroupNo;
        private String pWGroupNo;
        private String updIsBuckle;
        private String isBuckle;
        private String updKPGroupNo;
        private String kWGroupNo;
        @JSONFieldRequired
        private List<BomList> bomList;

    }

    @Data
    public class BomList{
        @JSONFieldRequired
        private String bomNo;
    }

}
