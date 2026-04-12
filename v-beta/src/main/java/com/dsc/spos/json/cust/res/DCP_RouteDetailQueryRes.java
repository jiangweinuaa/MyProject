package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_RouteDetailQueryRes extends JsonRes {
    private List<DCP_RouteDetailQueryRes.level1Elm> datas;

    @Data
    public class level1Elm{
        private String routeNo;
        private String routeName;
        private String memo;
        private String status;
        private String createOpId;
        private String createOpName;
        private String createDeptId;
        private String createDeptName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
        private List<Detail> detail;
    }

    @Data
    public class Detail{
        private String sorting;
        private String routeType;
        private String code;
        private String name;
        private String address;
        private String status;
    }

}
