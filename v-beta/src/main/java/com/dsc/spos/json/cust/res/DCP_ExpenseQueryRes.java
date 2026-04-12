package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_ExpenseQueryRes extends JsonRes {

    private List<Datas> datas;

    @Data
    public class Datas {
        private String bdate;
        private String doc_Type;
        private String corp;
        private String corpName;
        private String supplierNo;
        private String supplierName;
        private String tot_Amt;
        private String bfeeNo;
        private String status;
    }
}
