package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_ToDoListQueryNewRes extends JsonBasicRes {

    private List<Data> datas;

    @Getter
    @Setter
    public class Data {
        private String proName;
        private String qty;
        private String status;
        private Extend extend;
    }

    @Getter
    @Setter
    public class Extend {

        private String status;
        private List<String> docType;
        private String dateType;
        private String billType;
        private String beginDate;
        private String endDate;
        private String getType;
        private String searchScope;
        private String supplierType;
        private String queryType;
        private String isCheckRestrictGroup;
        private String returnType;
        private String approveStatus;

    }


}
