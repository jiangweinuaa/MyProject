package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_StockTaskBatchCreateRes extends JsonBasicRes {

    private Datas datas;
    @Data
    public class StockTaskList{
        private String stockTaskNo;
    }

    @Data
    public class Datas{
        private String totalRecords;
        private List<StockTaskList> stockTaskList;
    }
}
