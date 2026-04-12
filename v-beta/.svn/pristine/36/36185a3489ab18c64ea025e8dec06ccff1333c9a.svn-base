package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DCP_BatchingTaskMaterialRes extends JsonRes {

    private List<DCP_BatchingTaskMaterialRes.Datas> datas;

    @Getter
    @Setter
    public class Datas {
        private String batchTaskNo;
        private String pluNo;
        private String pluName;
        private String pQty;
        private String pUnit;
        private String pUName;
        private String pItem;
        private String processNo;
        private String processName;
        private String sItem;
        private String zItem;
        private String materialPluNo;
        private String materialPluName;
        private String materialPUnit;
        private String materialPUName;
        private String pWarehouse;
        private String kWarehouse;
        private String copies;
        private String batchCopies;
        private String isBuckle;
        private List<DetailList> detailList;

    }

    @Data
    public class DetailList{
        private String benCopies;
        private String materialPQty;
    }
}
