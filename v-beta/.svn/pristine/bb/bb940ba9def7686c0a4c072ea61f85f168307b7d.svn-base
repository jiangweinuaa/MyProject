package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class ISV_WMClientRegisterReq extends JsonBasicReq {
    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String custNo;
        private String custName;
        private String mainURL;
        private String memo;
        private List<regType> regLoadDocTypeList;
        private String opNo;
        private String opName;

        public String getCustNo() {
            return custNo;
        }

        public void setCustNo(String custNo) {
            this.custNo = custNo;
        }

        public String getCustName() {
            return custName;
        }

        public void setCustName(String custName) {
            this.custName = custName;
        }

        public String getMainURL() {
            return mainURL;
        }

        public void setMainURL(String mainURL) {
            this.mainURL = mainURL;
        }

        public List<regType> getRegLoadDocTypeList() {
            return regLoadDocTypeList;
        }

        public void setRegLoadDocTypeList(List<regType> regLoadDocTypeList) {
            this.regLoadDocTypeList = regLoadDocTypeList;
        }

        public String getOpNo() {
            return opNo;
        }

        public void setOpNo(String opNo) {
            this.opNo = opNo;
        }

        public String getOpName() {
            return opName;
        }

        public void setOpName(String opName) {
            this.opName = opName;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }
    }
    public class regType {
        private String loadDocType;
        private String loadDocTypeName;
        private String isRegister;

        public String getLoadDocType() {
            return loadDocType;
        }

        public void setLoadDocType(String loadDocType) {
            this.loadDocType = loadDocType;
        }

        public String getLoadDocTypeName() {
            return loadDocTypeName;
        }

        public void setLoadDocTypeName(String loadDocTypeName) {
            this.loadDocTypeName = loadDocTypeName;
        }

        public String getIsRegister() {
            return isRegister;
        }

        public void setIsRegister(String isRegister) {
            this.isRegister = isRegister;
        }
    }
}
