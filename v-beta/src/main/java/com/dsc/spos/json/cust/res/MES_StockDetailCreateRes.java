package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;

import java.util.List;

@Data
public class MES_StockDetailCreateRes extends JsonBasicRes
{
    private String doc_no; //门店管理对应的单号
    private String org_no; //门店管理对应的门店

    private List<level1Elm> errorList;

    @Data
    public class level1Elm
    {
        private String eId;//
        private String organizationNo;//
        private String billType;
        private String erpBillNo;
        private String erpItem;
        private String erpSeq;
        private String direct;
    }


}
