package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class ISV_WMClientQueryRes extends JsonRes {
    private responseDatas datas;

    public responseDatas getDatas() {
        return datas;
    }

    public void setDatas(responseDatas datas) {
        this.datas = datas;
    }

    public class responseDatas
    {
        private String clientNo;
        private String custNo;
        private String custName;
        private String mainURL;
        private List<regType> regLoadDocTypeList;
        private String createTime;
        private String createOpId;
        private String createOpName;
        private String lastModiTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String memo;
        private String status;

        public String getClientNo() {
            return clientNo;
        }

        public void setClientNo(String clientNo) {
            this.clientNo = clientNo;
        }

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

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getCreateOpId() {
            return createOpId;
        }

        public void setCreateOpId(String createOpId) {
            this.createOpId = createOpId;
        }

        public String getCreateOpName() {
            return createOpName;
        }

        public void setCreateOpName(String createOpName) {
            this.createOpName = createOpName;
        }

        public String getLastModiTime() {
            return lastModiTime;
        }

        public void setLastModiTime(String lastModiTime) {
            this.lastModiTime = lastModiTime;
        }

        public String getLastModiOpId() {
            return lastModiOpId;
        }

        public void setLastModiOpId(String lastModiOpId) {
            this.lastModiOpId = lastModiOpId;
        }

        public String getLastModiOpName() {
            return lastModiOpName;
        }

        public void setLastModiOpName(String lastModiOpName) {
            this.lastModiOpName = lastModiOpName;
        }

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
    public class regType
    {
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
