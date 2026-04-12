package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_DeliveryManUpdate
 *    說明：配送员修改
 * 服务说明：配送员修改
 * @author wangzyc
 * @since  2021/4/23
 */
@Data
public class DCP_DeliveryManUpdateReq extends JsonBasicReq {

    private level1Elm request;

    @Data
    public class level1Elm{
        private String opNo;    // 配送员编号
        private String opName;  // 配送员名称
        private String phone;   // 手机号码
        private String viewAbleDay;    // 单据可查看天数
        private List<level2Elm> orgList;    // 所属组织
        private String status;      // 状态
    }

    @Data
    public class level2Elm{
        private String org; // 组织编码
        private String orgName; // 组织名称
    }
}
