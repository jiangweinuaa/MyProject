package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

@Data
public class DCP_AccountingSubjectCreateReq extends JsonBasicReq
{
    private levelRequest request;

    @Data
    public class levelRequest
    {
        private  String subjectId;
        private  String subjectName;
        private  String direction;
        private  String auxiliaryType;
        private  String memo;
    }

}
