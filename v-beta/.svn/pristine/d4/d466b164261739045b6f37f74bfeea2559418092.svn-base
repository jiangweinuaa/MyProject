package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_SortingTaskQueryRes extends JsonRes {

    private List<Data> datas;

    @Getter
    @Setter
    public class Data {
        private String billNo;
        private String bDate;
        private String warehouse;
        private String warehouseName;
        private String status;

        List<TaskList> taskList;

    }

    @Getter
    @Setter
    public class TaskList {

        private String taskNo;
        @Deprecated
        private String requireType;
        @Deprecated
        private String requireNo;
        @Deprecated
        private String requireName;
        private String rDate;
        private String status;
        private String isStockOut;
        private String warehouse;
        private String warehouseName;

        private String totRQty;
        private String totAQty;
        private String totDQty;


    }


}
