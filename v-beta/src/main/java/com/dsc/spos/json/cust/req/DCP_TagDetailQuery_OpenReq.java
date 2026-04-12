package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

/**
 * 服務函數：DCP_TagDetailQuery_Open
 *   說明：生产标签商品查询
 * 服务说明：生产标签商品查询
 * @author wangzyc
 * @since  2021-5-10
 */
@Data
public class DCP_TagDetailQuery_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String machShopNo;      // 生产机构编码
        private String stallId;         // 档口编号
    }
}
