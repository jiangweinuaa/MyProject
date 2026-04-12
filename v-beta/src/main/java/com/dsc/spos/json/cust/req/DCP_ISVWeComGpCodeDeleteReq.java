package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;
import java.util.List;

/**
 * 服务函数：DCP_ISVWeComGpCodeDelete
 * 服务说明：社群活码删除
 * @author jinzma
 * @since  2024-02-28
 */
@Data
public class DCP_ISVWeComGpCodeDeleteReq extends JsonBasicReq {
    private Request request;
    @Data
    public class Request{
        private List<GpCode> gpCodeList;
    }
    @Data
    public class GpCode{
        private String gpCodeId;
    }
}
