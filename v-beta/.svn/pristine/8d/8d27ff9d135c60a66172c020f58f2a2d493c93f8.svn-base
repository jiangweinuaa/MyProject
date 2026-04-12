package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_CreditQueryRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public  class level1Elm {

        private String credit; //信用额度
        private String used; //已使用额度
        private String rest; //剩余额度
        private String recharge; //充值额度

        // 新增专款额度查询及要货单是否超额 判断
        private List<level2Elm> specialList; // 专款额度
        private String isPorder; // 是否超出剩余赊销额度，0-否，1-是
        private List<level3Elm> pOrderList; // 超出额度信息

        private String pBeforeOrderAMT; //未上传给ERP的要货单金额
        private String creditContrl ;// 赊销额度管控方式，0.不管控  1:超额警告 2.超额禁止交易
    }

    @Data
    public  class level2Elm {

        private String specialNo; // 专款规则编号
        private String special; // 专款额度
        private String rules; // 规则说明
    }

    @Data
    public  class level3Elm {

        private String creditType; // 额度类型，0.普通 1.专款
        private String specialNo; // 专款规则编号
    }


}
