package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProductGroupGoodsQueryRes extends JsonRes {

    private List<DCP_ProductGroupGoodsQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String pGroupNo;
        private String pGroupName;
        private String memo;
        private String departId;
        private String departName;
        private String status;
        private String creatorID;
        private String creatorName;
        private String createTime;
        private String lastModifyID;
        private String lastModifyName;
        private String lastModifyTime;

        private List<Datas> datas;
    }

    @Data
    public class Datas{
        private String pluNo;
        private String pluName;
        private String spec;
    }
}
