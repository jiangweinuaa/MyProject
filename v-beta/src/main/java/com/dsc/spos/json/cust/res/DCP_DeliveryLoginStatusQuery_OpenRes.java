package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_DeliveryLoginStatusQuery_Open
 *    說明：查询配送员登录状态
 * 服务说明 查询配送员登录状态
 * @author wangzyc
 * @since  2021/5/19
 */
@Data
public class DCP_DeliveryLoginStatusQuery_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private String opNo;         // 账号
        private String opName;       // 用户名
        private String ipone;        // 手机号

        private List<level2Elm> shopList; // 所属门店
        private String viewAbleDay; // 单据可查看天数 若为空则不限制
        private String crmService;  // 会员服务地址
        private String shopId;      // 最近一次登录门店
    }

    @Data
    public class level2Elm{
        private String shopId;     // 门店编号
        private String shopName;   // 门店名称
    }
}
