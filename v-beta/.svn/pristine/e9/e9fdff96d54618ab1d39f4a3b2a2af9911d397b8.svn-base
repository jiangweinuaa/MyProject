package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_BomTreeQueryRes  extends JsonRes {

    private List<DCP_BomTreeQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{
        private String bomNo;
        private String bomType;
        private String pluNo;
        private String pluName;
        private String spec;
        private String unit;
        private String unitName;
        private String status;
        private List<MaterialList> materialList;

    }

    @Data
    public class MaterialList{

        private String materialPluNo;
        private String materialPluName;
        private String materialQty;
        private String qty;
        private String materialUnit;
        private String materialUnitName;
        private String sortId;
        private String bomNo;
        private String status;

        private List<MaterialList> children;

    }
}
