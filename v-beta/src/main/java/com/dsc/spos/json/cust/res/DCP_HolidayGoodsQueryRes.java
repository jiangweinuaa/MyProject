package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;


public class DCP_HolidayGoodsQueryRes extends JsonRes {

    private List<level1Elm> datas;

    public List<level1Elm> getDatas() {
        return datas;
    }

    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }

    public class level1Elm
    {
        private String companyId;
        private String employeeId;
        private String employeeName;
        private String billNo;
        private String billDate;
        private String title;
        private String beginDate;
        private String endDate;
        private String remark;
        private String status;
        private String goodsSync;
        private String canGoodsSync;
        private String redisUpdateSuccess;
        private List<level2Elm> goodsList;

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public String getBillNo() {
            return billNo;
        }

        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }

        public String getBillDate() {
            return billDate;
        }

        public void setBillDate(String billDate) {
            this.billDate = billDate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getGoodsSync() {
            return goodsSync;
        }

        public void setGoodsSync(String goodsSync) {
            this.goodsSync = goodsSync;
        }

        public String getCanGoodsSync() {
            return canGoodsSync;
        }

        public void setCanGoodsSync(String canGoodsSync) {
            this.canGoodsSync = canGoodsSync;
        }

        public String getRedisUpdateSuccess() {
            return redisUpdateSuccess;
        }

        public void setRedisUpdateSuccess(String redisUpdateSuccess) {
            this.redisUpdateSuccess = redisUpdateSuccess;
        }

        public List<level2Elm> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<level2Elm> goodsList) {
            this.goodsList = goodsList;
        }
    }

    public class level2Elm {

        private String pluNo;
        private String pluName;
        private int sortId;

        public String getPluNo() {
            return pluNo;
        }

        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }

        public String getPluName() {
            return pluName;
        }

        public void setPluName(String pluName) {
            this.pluName = pluName;
        }

        public int getSortId() {
            return sortId;
        }

        public void setSortId(int sortId) {
            this.sortId = sortId;
        }
    }
}
