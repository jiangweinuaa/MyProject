package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_DeliveryManDelete
 * 說明：配送员删除
 * 服务说明：配送员删除
 *
 * @author wangzyc
 * @since 2021/4/23
 */
@Data
public class DCP_DeliveryManDeleteReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm {
        private List<String> opNo;  // 配送员编号
    }

}
