package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Data
public class DCP_PendingPOrderQueryRes extends JsonRes {


    private List<DatasLevel> datas;

    @NoArgsConstructor
    @Data
    public  class DatasLevel {
        private String pOrderNo;
        private String bDate;
        private String rDate;
        private String organizationNo;
        private String organizationName;
        private String pTemplateNo;
        private String pTemplateName;
        private String isUrgent;
        private String isAdd;
        private Integer preDays;
        private Integer totCqty;
        private BigDecimal totPqty;
        private BigDecimal totDistriAmt;
        private BigDecimal totAmt;
    }
}
