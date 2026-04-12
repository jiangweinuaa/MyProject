package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_MoDetailQueryRes extends JsonRes {
    private List<DCP_MoDetailQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{

        private String bDate;
        private String pDate;
        private String moNo;
        private String pGroupNo;
        private String pGroupName;
        private String loadDocNo;
        private String memo;
        private String status;
        private String createBy;
        private String createByName;
        private String createTime;
        private String modifyBy;
        private String modifyByName;
        private String modifyTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmTime;
        private String departId;
        private String departName;
        private String oType;
        private String ofNo;
        private String sourceMoNo;
        private String prodType;
        private List<Datas> datas;

    }

    @Data
    public class Datas{

        private String item;
        private String oItem;
        private String sourceMoItem;
        private String pluNo;
        private String pluName;
        private String featureNo;
        private String featureName;
        private String pUnit;
        private String pUName;
        private String pQty;
        private String beginDate;
        private String endDate;
        private String bomNo;
        private String versionNum;
        private String pickStatus;
        private String dispatchQty;
        private String dispatchStatus;
        private String mulQty;
        private String minQty;
    }
}
