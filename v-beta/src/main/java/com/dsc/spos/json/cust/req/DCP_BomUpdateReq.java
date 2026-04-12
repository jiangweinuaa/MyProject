package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BomUpdateReq extends JsonBasicReq
{

    private DCP_BomUpdateReq.LevelRequest request;


    @Data
    public class LevelRequest{
        @JSONFieldRequired(display = "配方编号")
        private String bomNo;
        @JSONFieldRequired(display = "配方类型")
        private String bomType;
        @JSONFieldRequired(display = "主件编码")
        private String pluNo;
        private String pluName;
        @JSONFieldRequired(display = "主件单位")
        private String unit;
        private String mulQty;
        @JSONFieldRequired(display = "生效日期")
        private String effDate;
        @JSONFieldRequired(display = "适用组织")
        private String restrictShop;
        @JSONFieldRequired(display = "状态")
        private String status;
        private String batchQty;
        private String versionNum;
        private String remainType;
        private String containType;
        private String processStatus;
        private String prodType;

        private String fixedLossQty;
        private String isProcessEnable;
        private String inWGroupNo;

        @JSONFieldRequired
        private List<DCP_BomUpdateReq.MaterialList> materialList;

        private List<DCP_BomUpdateReq.RangeList> rangeList;
        private String memo;
        private String minQty;
        private String oddValue;
        private String productExceed;
        private String procRate;
        private String dispType;
        private String semiwoType;
        private String semiwoDeptType;
        private String fixPreDays;
        private String sdlaborTime;
        private String sdmachineTime;
        private String standardHours;

        private String isCoByProduct;

        private List<DCP_BomUpdateReq.CoByList> coByList;
    }

    @Data
    public class CoByList{
        private String productType;
        private String pluNo;
        private String unit;
        private String costRate;
    }


    @Data
    public class MaterialList{
        @JSONFieldRequired(display = "子件编码")
        private String materialPluNo;
        @JSONFieldRequired(display = "主件底数")
        private String qty;
        @JSONFieldRequired(display = "子件单位")
        private String materialUnit;
        @JSONFieldRequired(display = "子件数量")
        private String materialQty;
        @JSONFieldRequired(display = "损耗率")
        private String lossRate;
        @JSONFieldRequired(display = "是否扣料")
        private String isBuckle;
        @JSONFieldRequired(display = "是否可替代")
        private String isReplace;
        @JSONFieldRequired(display = "原料生效日期")
        private String materialBDate;

        private String materialEDate;
        @JSONFieldRequired(display = "显示顺序")
        private String sortId;
        @JSONFieldRequired(display = "成本分摊比例")
        private String costRate;
        private String isPick;
        private String isBatch;
        private String pWGroupNo;
        private String isMix;
        private String mixGroup;
        private String kWGroupNo;
        private List<DCP_BomUpdateReq.ReplaceList> replaceList;
    }

    @Data
    public class RangeList{
        private String shopId;
        private String organizationNo;

    }

    @Data
    public class ReplaceList{
        private String priority;
        private String materialQty;
        private String materialUnit;
        private String replacePluNo;
        private String replaceQty;
        private String replaceUnit;
        private String replaceBDate;
        private String replaceEDate;
    }
    @Data
    public class BomItem{
        String pluno;
        String prodType;
    }


}
