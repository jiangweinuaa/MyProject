package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

/**
 * @description: CDS语音播报任务查询
 * @author: wangzyc
 * @create: 2022-05-24
 */
@Data
public class DCP_CdsOrderCallTaskQuery_OpenRes extends JsonBasicRes {
    private level1Elm datas;

    @Data
    public class level1Elm{
        private List<level2Elm> taskList;
    }

    @Data
    public static class level2Elm{
        /**
         * 取餐号
         */
        private String trNo;
        /**
         * 叫号任务流水号
         */
        private String taskID;
        /**
         * 创建叫号时间
         */
        private String callTime;

        /**
         * 应用类型
         */
        private String appType;

        private String appName;

        /**
         * 单号
         */
        private String billNo;
    }
}
