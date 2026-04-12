package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

@Data
public class DCP_ReturnApplyCreateRes extends JsonBasicRes {
    private String billNo;
}
