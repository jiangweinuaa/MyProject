package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_POrderCustomerQueryRes extends JsonRes {
    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private List<level2Elm> dataList;

        public List<level2Elm> getDataList() {
            return dataList;
        }

        public void setDataList(List<level2Elm> dataList) {
            this.dataList = dataList;
        }
    }
    public class level2Elm{
        private String customerNo;
        private String customerName;

        public String getCustomerNo() {
            return customerNo;
        }

        public void setCustomerNo(String customerNo) {
            this.customerNo = customerNo;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
    }
}
