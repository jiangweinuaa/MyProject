package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.time.LocalDateTime;

/**
 * @description:
 * @author: xingdong
 * @time: 2021/1/21 下午5:32
 */
public class DCP_GoodsExtUpdateReq extends JsonBasicReq {

    private levelElm request;

    public levelElm getRequest() {
        return request;
    }

    public void setRequest(levelElm request) {
        this.request = request;
    }

    public class levelElm {
        /**
         * 商品编码,不可修改
         */
        private String pluNo;

        /**
         * 商品简介
         */
        private String description;

        public String getPluNo() {
            return pluNo;
        }

        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}
