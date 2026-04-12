package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_HqDishControlQueryRes extends JsonRes
{

    private List<level1Elm>  datas;

    @Data
    public class level1Elm
    {
        private String id;
        private String name;
        private String goodsType;
        private String unSide;
        private String unCook;
        private String unCall;
    }

}
