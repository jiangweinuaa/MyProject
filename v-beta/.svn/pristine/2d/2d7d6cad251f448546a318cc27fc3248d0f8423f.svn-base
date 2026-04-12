package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * @description: CDS叫号任务更新
 * @author: wangzyc
 * @create: 2022-05-24
 */
@Data
public class DCP_CdsOrderCallTaskQUpdate_OpenReq extends JsonBasicReq {
    private level1Elm request;

    @Data
    public class level1Elm{
        private String shopId;
        private String eId;
        private String handleType; // 0完成叫号，不传默认叫号完成
        private List<level2Elm> taskList;
    }

    @Data
    public class level2Elm{
        private String trNo;
        private String taskID;
        private String appName;
        private String billNo;
    }
}
