package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class MES_MoCreateReq extends JsonBasicReq
{

    private levelRequest request;

    @Data
    public class levelRequest
    {
        private String eId;//企业id
        private String organizationNo;//
        private String loadDocNo;//
        private String pGroupNo;//
        private String bDate;//
        private String pDate;//
        private String creatByNo;//
        private String creatByName;//
        private String status;//
        private String memo;//

        private List<level1> detailDatas;
    }

    @Data
    public class level1
    {
        private int item;//
        private String pluNo;//
        private String pUnit;//
        private String pQty;//
        private String beginDate;//
        private String endDate;//
    }

}
