package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

import java.util.List;

public class DCP_ShopManagePrintQuery_OpenRes extends JsonBasicRes {

    private level1Elm datas;

    public level1Elm getDatas() {
        return datas;
    }

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm
    {
        private List<level2Elm> printList;

        public List<level2Elm> getPrintList() {
            return printList;
        }

        public void setPrintList(List<level2Elm> printList) {
            this.printList = printList;
        }
    }

    public class level2Elm
    {
        private String batchKey;
        private String printBillType;
        private String billNo;
        private String printData;

        public String getBatchKey() {
            return batchKey;
        }

        public void setBatchKey(String batchKey) {
            this.batchKey = batchKey;
        }

        public String getPrintBillType() {
            return printBillType;
        }

        public void setPrintBillType(String printBillType) {
            this.printBillType = printBillType;
        }

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        public String getPrintData() {
            return printData;
        }

        public void setPrintData(String printData) {
            this.printData = printData;
        }
    }
}
