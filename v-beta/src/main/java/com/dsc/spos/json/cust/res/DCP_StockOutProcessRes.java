package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Getter;
import lombok.Setter;

/**
 * StockOutUpdate 專用的 response json
 * @author panjing 
 * @since  2016-09-22
 */
@Getter
@Setter
public class DCP_StockOutProcessRes extends JsonBasicRes {

    private FailureData failuredatas;

    @Getter
    @Setter
    public class FailureData{

        private String seq;
        private String stockoutNo;
        private String stockoutItem;
        private String descdesc;

    }


  
}
