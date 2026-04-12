package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DCP_SortingTaskDetailRes extends JsonRes {

    private List<Data> datas;

    @Getter
    @Setter
    public class Data {
        private String taskNo;
        private String warehouse;
        private String warehouseName;
        private String requireType;
        private String requireNo;
        private String requireName;
        private String totCqty;
        private String totRQty;
        private String totQty;
        private String totAQty;
        private String totDQty;


        List<Detail> detail;

    }

    @Getter
    @Setter
    public class Detail{

        private String item;
        private String ofNo;
        private String oItem;
        private String pluNo;
        private String pluName;
        private String spec;
        private String featureNo;
        private String featureName;
        private String category;
        private String categoryName;
        private String unit;
        private String unitName;
        private String rQty;
        private String qty;
        private String aQty;
        private String diffQty;
        private String orderNo;

    }


}
