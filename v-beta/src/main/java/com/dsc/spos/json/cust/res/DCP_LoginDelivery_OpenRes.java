package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：DCP_LoginDelivery_Open
 *    說明：配送员登录
 * 服务说明：配送员登录
 * @author wangzyc
 * @since  2021/4/23
 */
@Data
public class DCP_LoginDelivery_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public  class level1Elm{
        private String opNo;    // 账号
        private String opName;  // 用户名
        private String ipone;   // 手机号
        private List<level2Elm> shopList;   // 所属门店
        private String viewAbleDay; // 单据可查看天数  若为空则不限制
        private String dcpService; // 单中台服务地址
        private String crmService; // 会员服务地址
        private String posService; // POS服务地址
    }

    @Data
    public class level2Elm{
        private String shopId;   // 门店编号
        private String shopName;  // 门店名称
        private String smsAlerts;  // 默认发短信提醒Y/N
    }


}
