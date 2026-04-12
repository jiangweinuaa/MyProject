package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_HqDishControlDeleteReq extends JsonBasicReq
{

    private level1Elm request;

    @Data
    public class level1Elm
    {
        private List<level2Elm> itemList;
    }

    @Data
    public class level2Elm
    {
        private String id;
        private String goodsType;
    }


}
