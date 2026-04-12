package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DCP_PriceAdjustPlansStateUpdateRes extends JsonBasicRes {

    private String totalRecords;

    private String successRecords;

}
