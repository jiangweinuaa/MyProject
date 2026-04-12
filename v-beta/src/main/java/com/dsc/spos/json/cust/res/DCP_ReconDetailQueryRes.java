package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ReconDetailQueryRes extends JsonRes {

    private List<DCP_ReconDetailQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String organizationNo;
        private String organizationName;
        private String bdate;
        private String billNo;
        private String corp;
        private String corpName;
        private String dataType;
        private String bizPartnerNo;
        private String bizPartnerName;
        private String year;
        private String month;
        private String estReceExpDay;
        private String currReconAmt;
        private String currReconTaxAmt;
        private String currReconPretaxAmt;
        private String paidReceAmt;
        private String notPaidReceAmt;
        private String memo;
        private List<DCP_ReconDetailQueryRes.Detail> reconList;

        private String status;
        private String createBy;
        private String createByName;
        private String create_Date;
        private String create_Time;
        private String modifyBy;
        private String modifyByName;
        private String modify_Date;
        private String modify_Time;
        private String confirmBy;
        private String confirmByName;
        private String confirm_Date;
        private String confirm_Time;
        private String cancelBy;
        private String cancelByName;
        private String cancel_Date;
        private String cancel_Time;

        private String startDate;
        private String endDate;
        private String payDateNo;
        private String payDateDesc;
        private String currency;
        private String currencyName;

    }


    @Data
    public class Detail{
        private String organizationNo;
        private String organizationName;
        private String corp;
        private String corpName;
        private String billNo;
        private String item;
        private String sourceType;
        private String sourceNo;
        private String sourceNoSeq;
        private String rDate;
        private String fee;
        private String feeName;
        private String pluNo;
        private String pluName;
        private String currency;
        private String currencyName;
        private String taxRate;
        private String direction;
        private String billQty;
        private String reconQty;
        private String billPrice;
        private String preTaxAmt;
        private String amt;
        private String reconAmt;
        private String unPaidAmt;
        private String currReconAmt;
        private String departId;
        private String departName;
        private String cateGory;
        private String cateGoryName;
        private String taxCode;
        private String taxName;
    }
}
