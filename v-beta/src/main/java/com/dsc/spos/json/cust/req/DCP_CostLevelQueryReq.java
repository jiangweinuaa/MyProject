package com.dsc.spos.json.cust.req;

import  com.dsc.spos.json.JsonBasicReq;
import com.dsc.spos.json.JSONFieldRequired;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

/**
 *
 * @author YapiGenerator自动生成
 * @date 2025/03/03
 */
@Getter
@Setter
public class DCP_CostLevelQueryReq extends JsonBasicReq{

   private Request request;

    @Getter
    @Setter
    public class Request{
          private String costGroupingId;
          private String status;
          private String keyTxt;
    }

}