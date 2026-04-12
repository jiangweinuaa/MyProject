package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

@Data
public class DCP_WOReportCreateRes extends JsonBasicRes {

    public Datas datas;

    @Data
    public class Datas{
        private String reportNo;
    }
}
