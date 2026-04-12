package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_BatchStatisticsRes extends JsonBasicRes {


    private List<DatasLevel> datas;

    @NoArgsConstructor
    @Data
    public  class DatasLevel {
        private String status;
        private String batchCount;
    }
}
