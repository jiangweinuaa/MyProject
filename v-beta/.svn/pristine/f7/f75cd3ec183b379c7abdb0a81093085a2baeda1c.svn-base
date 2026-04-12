package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DCP_WarehouseRangeQueryRes extends JsonBasicRes {
    private List<DCP_WarehouseRangeQueryRes.level1Elm> datas;

    @Getter
    @Setter
    public class level1Elm {
        private String organizationNo;
        private String organizationName;
        private String warehouseNo;
        private String warehouseName;
        private List<RangeList> rangeList;
    }

    @Data
    public class RangeList{
        private String type;
        private String code;
        private String name;
        private String status;
        private String createOpId;
        private String createOpName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
    }
}
