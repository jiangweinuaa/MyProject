package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_FinancialAuditQueryReq extends JsonBasicReq
{

    private level1Elm request;

    @Data
    public class  level1Elm
    {
        private String beginBDate;//起始营业日期YYYY-MM-DD
        private String endBDate;//截止营业日期YYYY-MM-DD
        private String beginAuditDate;//起始稽核日期YYYY-MM-DD
        private String endAuditDate;//截止稽核日期YYYY-MM-DD
        private String shopGroupNo;//门店分组编码
        private String[] shopList;//门店id，未选门店传[]
        private String auditStatus;//稽核状态：Y-已稽核 N-未稽核 空-全部
        private String auditOpNo;//稽核人编码
        private String exportStatus;//导出状态：Y-已导出 N-未导出 空-全部

    }

}
