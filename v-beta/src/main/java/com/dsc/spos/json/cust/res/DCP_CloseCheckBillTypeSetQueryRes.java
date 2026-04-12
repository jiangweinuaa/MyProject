package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import java.util.List;

/**
 * @apiNote 闭店检查单据类型设置查询
 * @since 2021-05-18
 * @author jinzma
 */
public class DCP_CloseCheckBillTypeSetQueryRes extends JsonRes {
  private List<level1Elm> datas;

    public List<level1Elm> getDatas() {
        return datas;
    }

    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }

    public class level1Elm{
        private String billType;
        private String billTypeName;
        private String status;
        private String restrictShop;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
        private List<level2Elm> shopList;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRestrictShop() {
            return restrictShop;
        }

        public void setRestrictShop(String restrictShop) {
            this.restrictShop = restrictShop;
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

        public String getLastModiTime() {
            return lastModiTime;
        }

        public void setLastModiTime(String lastModiTime) {
            this.lastModiTime = lastModiTime;
        }

        public List<level2Elm> getShopList() {
            return shopList;
        }

        public void setShopList(List<level2Elm> shopList) {
            this.shopList = shopList;
        }
    }
    public class level2Elm{
        private String shopId;
        private String shopName;

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }
    }

}
