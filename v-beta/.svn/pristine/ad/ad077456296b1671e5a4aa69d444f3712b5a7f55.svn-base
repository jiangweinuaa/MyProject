package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_PickGroupDetailQueryRes extends JsonBasicRes {

    private List<Datas> datas;

    @Getter
    @Setter
    public class Datas{

        private String pickGroupNo;
        private String pickGroupName;
        private String warehouse;
        private String warehouseName;
        private String wareRegionNo;
        private String wareRegionName;
        private String pickType;
        private String rangeType;
        private String objectRange;
        private String status;
        private String memo;
        private String createOpId;
        private String createOpName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;

        private List<RangeList> rangeList;
        private List<ObjectList> objectList;

    }

    @Getter
    @Setter
    public class RangeList{
        private String type;
        private String code;
        private String name;
        private String sortId;
    }

    @Getter
    @Setter
    public class ObjectList{
        private String type;
        private String code;
        private String name;
        private String sortId;
    }


}
