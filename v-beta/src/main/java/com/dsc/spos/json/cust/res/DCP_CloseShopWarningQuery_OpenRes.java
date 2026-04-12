package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import java.util.List;

/**
 * @apiNote POS闭店提醒通知
 * @since 2021-05-21
 * @author jinzma
 */
public class DCP_CloseShopWarningQuery_OpenRes extends JsonRes {
    private level1Elm datas;
    public level1Elm getDatas() {
        return datas;
    }
    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private List<level2ElmBill> billDatas;
        public List<level2ElmBill> getBillDatas() {
            return billDatas;
        }
        public void setBillDatas(List<level2ElmBill> billDatas) {
            this.billDatas = billDatas;
        }
    }

    public class level2ElmBill{
        private String billType;
        private String billTypeName;
        private String billNo;
        private String memo;

        public String getBillType() {
            return billType;
        }
        public void setBillType(String billType) {
            this.billType = billType;
        }
        public String getBillTypeName() {
            return billTypeName;
        }
        public void setBillTypeName(String billTypeName) {
            this.billTypeName = billTypeName;
        }
        public String getBillNo() {
            return billNo;
        }
        public void setBillNo(String billNo) {
            this.billNo = billNo;
        }
        public String getMemo() {
            return memo;
        }
        public void setMemo(String memo) {
            this.memo = memo;
        }
    }


}
