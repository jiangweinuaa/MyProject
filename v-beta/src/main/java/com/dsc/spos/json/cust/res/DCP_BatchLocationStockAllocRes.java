package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author YapiGenerator自动生成
 * @date 2025/05/12
 */
@Getter
@Setter
public class DCP_BatchLocationStockAllocRes extends JsonRes {

    private List<Datas> datas;

    @Getter
    @Setter
    public class Datas {
        private String item;
        private String pUnit;
        private String pluNo;
        private List<BatchList> batchList;
    }

    @Getter
    @Setter
    public class BatchList {
        private String batchNo;
        private String validDate;
        private String prodDate;
        private String qty;
        private String allocQty;
        private String location;
    }


}