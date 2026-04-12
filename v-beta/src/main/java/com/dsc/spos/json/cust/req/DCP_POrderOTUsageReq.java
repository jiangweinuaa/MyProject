package com.dsc.spos.json.cust.req;

import java.util.List;
import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

public class DCP_POrderOTUsageReq extends JsonBasicReq {
    //【ID1026725】【河北建投/鲜鲜坊】1001门店做要货申请使用要货模板主食类，点击千元用量报错 by jinzma 20220621
    //增加request
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    @Data
    public class levelElm{
        private String beginDate;
        private String endDate;
        private String avgsaleAMT;
        private String saleAMT;        // 营业额千元用量，传输入的值；    盘点千元用量， 传另一个服务返回的totAmt;
        private String modifRatio;
        private String cal_type;
        private String materal_type;
        private List<level1Elm> datas;
    }
    
    @Data
    public class level1Elm {
        private String pluNo;
        private String featureNo;
        private String punit;
    }
    
}
