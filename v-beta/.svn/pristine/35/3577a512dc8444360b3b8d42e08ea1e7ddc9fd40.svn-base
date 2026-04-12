package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import java.util.List;

/**
 * @apiNote 闭店检查单据类型设置适用类型修改
 * @since 2021-05-18
 * @author jinzma
 */
public class DCP_CloseCheckBillTypeSetUpdateReq extends JsonBasicReq {

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm{
        private String billType;
        private String status;
        private String restrictShop;
        private List<level1Elm> shopList;

        public String getBillType() {
            return billType;
        }

        public void setBillType(String billType) {
            this.billType = billType;
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

        public List<level1Elm> getShopList() {
            return shopList;
        }

        public void setShopList(List<level1Elm> shopList) {
            this.shopList = shopList;
        }
    }
    public class level1Elm{
        private String shopId;

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }
    }
}
