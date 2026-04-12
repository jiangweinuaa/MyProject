package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

@Data
public class DCP_BatchingDocCreateRes extends JsonBasicRes {

    private Datas datas;

    @Data
    public class Datas{
        private String batchNo;
    }
}
