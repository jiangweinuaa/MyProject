package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import java.util.List;

/**
 * 服务名称：DCP_DisplayBaseUpdate
 * 服务说明：客显基础资料修改
 * @author jinzma
 * @since  2022-04-25
 */
public class DCP_DisplayBaseUpdateReq extends JsonBasicReq {
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    public class levelElm {
        private String templateNo;
        private String templateName;
        private String shopType;
        private String status;
        private String logoId;
        private String welcomeWords;
        private String backgroundColor;
        private String welcomeWordColor;
        private List<level1Elm> shopList;
    
        public String getTemplateNo() {
            return templateNo;
        }
        public void setTemplateNo(String templateNo) {
            this.templateNo = templateNo;
        }
        public String getTemplateName() {
            return templateName;
        }
        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }
        public String getShopType() {
            return shopType;
        }
        public void setShopType(String shopType) {
            this.shopType = shopType;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getLogoId() {
            return logoId;
        }
        public void setLogoId(String logoId) {
            this.logoId = logoId;
        }
        public String getWelcomeWords() {
            return welcomeWords;
        }
        public void setWelcomeWords(String welcomeWords) {
            this.welcomeWords = welcomeWords;
        }
        public String getBackgroundColor() {
            return backgroundColor;
        }
        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }
        public String getWelcomeWordColor() {
            return welcomeWordColor;
        }
        public void setWelcomeWordColor(String welcomeWordColor) {
            this.welcomeWordColor = welcomeWordColor;
        }
        public List<level1Elm> getShopList() {
            return shopList;
        }
        public void setShopList(List<level1Elm> shopList) {
            this.shopList = shopList;
        }
    }
    public class level1Elm {
        private String shopId;
    
        public String getShopId() {
            return shopId;
        }
        public void setShopId(String shopId) {
            this.shopId = shopId;
        }
    }
}
