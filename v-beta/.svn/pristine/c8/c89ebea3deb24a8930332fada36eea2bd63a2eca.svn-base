package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_WMSPGoodsCreateReq extends JsonBasicReq {

    private levelRequest request;

    public levelRequest getRequest() {
        return request;
    }

    public void setRequest(levelRequest request) {
        this.request = request;
    }

    public class levelRequest
    {
        private String id;
        private String name;
        private String status;
        private List<levelSpec> specDatas;
        private List<levelAttr> attrDatas;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<levelSpec> getSpecDatas() {
            return specDatas;
        }

        public void setSpecDatas(List<levelSpec> specDatas) {
            this.specDatas = specDatas;
        }

        public List<levelAttr> getAttrDatas() {
            return attrDatas;
        }

        public void setAttrDatas(List<levelAttr> attrDatas) {
            this.attrDatas = attrDatas;
        }
    }

    public class levelSpec
    {
        private String specId;
        private String specName;

        public String getSpecId() {
            return specId;
        }

        public void setSpecId(String specId) {
            this.specId = specId;
        }

        public String getSpecName() {
            return specName;
        }

        public void setSpecName(String specName) {
            this.specName = specName;
        }
    }

    public class levelAttr
    {
        private String attrName;
        private String attrValue;
        private String attrValue_elm;
        private String attrValue_mt;
        private String pluBarcode;

        public String getAttrName() {
            return attrName;
        }

        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }

        public String getAttrValue() {
            return attrValue;
        }

        public void setAttrValue(String attrValue) {
            this.attrValue = attrValue;
        }

        public String getAttrValue_elm() {
            return attrValue_elm;
        }

        public void setAttrValue_elm(String attrValue_elm) {
            this.attrValue_elm = attrValue_elm;
        }

        public String getAttrValue_mt() {
            return attrValue_mt;
        }

        public void setAttrValue_mt(String attrValue_mt) {
            this.attrValue_mt = attrValue_mt;
        }

        public String getPluBarcode() {
            return pluBarcode;
        }

        public void setPluBarcode(String pluBarcode) {
            this.pluBarcode = pluBarcode;
        }
    }
}
