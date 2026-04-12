package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class MES_BatchCreateRes extends JsonBasicRes {

    private Data datas;

    @Getter
    @Setter
    public class Data{
        private String btachNo;

    }

}
