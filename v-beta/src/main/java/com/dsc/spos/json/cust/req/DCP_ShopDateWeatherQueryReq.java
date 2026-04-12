package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ShopDateWeatherQueryReq extends JsonBasicReq
{
    private levelRequest request;

    @Data
    public class levelRequest
    {
        private List<level2Elm> dataList;

    }

    @Data
    public class level2Elm
    {
        private String sDate;//日期（格式yyyymmdd）
    }


}
