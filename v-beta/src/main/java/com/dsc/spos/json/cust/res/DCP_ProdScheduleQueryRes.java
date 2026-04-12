package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ProdScheduleQueryRes extends JsonRes {

    private List<DCP_ProdScheduleQueryRes.Level1Elm> datas;

    @Data
    public class Level1Elm {
        private String bDate;
        private String billNo;
        private String beginDate;
        private String endDate;
        private String semiWOGenType;
        private String employeeId;
        private String employeeName;
        private String departId;
        private String departName;
        private String totCqty;
        private String totPqty;
        private String totWOQty;
        private String memo;
        private String status;
        private String createBy;
        private String createByName;
        private String createTime;
        private String createDeptId;
        private String createDeptName;
        private String modifyBy;
        private String modifyByName;
        private String modifyTime;
        private String confirmBy;
        private String confirmByName;
        private String confirmTime;
        private String cancelBy;
        private String cancelByName;
        private String cancelTime;
        private String closeBy;
        private String closeByName;
        private String closeTime;

    }
}
