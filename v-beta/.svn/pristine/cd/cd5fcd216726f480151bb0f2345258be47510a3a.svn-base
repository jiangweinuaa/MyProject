package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_PurOrderTrackGetRes extends JsonRes {

    private List<DCP_PurOrderTrackGetRes.level1Elm> datas;

    @Data
    public class level1Elm{

        private String receiveStatus;
        private String arrivalDate;
        private String purOrderNo;
        private String receivingNO;
        private String receiveOrgno;
        private String supplier;
        private String supplierName;
        private String totcQty;
        private String totpQty;
        private String totrQty;
        private String totsQty;
        private String tot_non_pQty;
        private String tot_non_rQty;
        private String tot_non_sQty;
        private String totPurQty;
        private String rate;
        private String expireDate;

        private List<DCP_PurOrderTrackGetRes.Detail> detail;

    }

    @Data
    public class Detail{

        private String item;
        private String item2;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String pluBarCode;
        private String purUnit;
        private String purUnitName;
        private String purQty;
        private String noticeQty;
        private String rQty;
        private String sQty;
        private String non_noticeQty;
        private String non_rQty;
        private String non_sQty;



    }
}
