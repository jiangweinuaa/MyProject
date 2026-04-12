package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/05/09
 */
@Getter
@Setter
public class DCP_ProcessReportUpdateReq extends JsonBasicReq {

    @JSONFieldRequired
    private Request request;

    @Getter
    @Setter
    public class Request {
        @JSONFieldRequired(display = "产出物类型  1.虚拟物 2.产成品")
        private String artifactType;
        private String batchNo;
        @JSONFieldRequired(display = "任务单号")
        private String batchTaskNo;

        private String artifactNo;
        @JSONFieldRequired(display = "合计产出物数量")
        private String totPPQty;

        private String scrapQty;

        @JSONFieldRequired(display = "主件编号")
        private String pluNo;
        @JSONFieldRequired(display = "产出物单位")
        private String pPUnit;
        @JSONFieldRequired(display = "工序项次")
        private String pItem;

        @JSONFieldRequired(display = "工序编号")
        private String processNo;
        @JSONFieldRequired(display = "合格数量")
        private String passQty;
        @JSONFieldRequired(display = "单据日期")
        private String bDate;
        private String machine;
        @JSONFieldRequired(display = "产出物名称")
        private String artifactName;
        @JSONFieldRequired(display = "岗位")
        private String station;
        @JSONFieldRequired(display = "产出物数量")
        private String pPQty;
        @JSONFieldRequired(display = "报工单号")
        private String reportNo;
        @JSONFieldRequired(display = "状态 0新建 1确定 2草稿")
        private String status;

        @JSONFieldRequired(display = "")
        private List<Users> users;
        @JSONFieldRequired(display = "")
        private List<Datas> datas;
        private List<ScrapList> scrapList;
    }


    @Getter
    @Setter
    public class ScrapList {

        private String reason;
        private String scrapQty;
    }

    @Getter
    @Setter
    public class Datas {
        @JSONFieldRequired(display = "子工序项次")
        private String sItem;
        private String eReportTime;
        private String pReportTime;

        private List<MaterialList> materialList;
    }


    @Getter
    @Setter
    public class MaterialList {
        @JSONFieldRequired(display = "标准用量")
        private String standardQty;
        @JSONFieldRequired(display = "子件数量")
        private String materialQty;
        @JSONFieldRequired(display = "子件项次")
        private String zItem;
        @JSONFieldRequired(display = "子件类型 0.原料 1.虚拟物")
        private String materialType;
        private String materialPluNo;
        @JSONFieldRequired(display = "是否扣料")
        private String isBuckle;
        @JSONFieldRequired(display = "扣料仓库")
        private String kWarehouse;
        @JSONFieldRequired(display = "子件单位")
        private String materialUnit;
        @JSONFieldRequired(display = "子件名称")
        private String materialPluName;

        private List<BatchList> batchList;
    }


    @Getter
    @Setter
    public class BatchList {
        private String batchNo;
        @JSONFieldRequired(display = "子件数量")
        private String materialQty;
        private String location;
    }

    @Getter
    @Setter
    public class Users {
        @JSONFieldRequired(display = "")
        private String opNo;
    }

}