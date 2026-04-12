package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;

import java.util.List;

public class DCP_WMSPGoodsDetailRes extends JsonRes {

    private level1Elm datas;

    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }

    public level1Elm getDatas() {
        return datas;
    }

    public  class level1Elm
    {
        private String id;
        private String name;
        private String status; //-1未启用,100已启用,0已禁用
        private String createtime;
        private String createopid;
        private String createopname;
        private String lastmoditime;
        private String lastmodiopid;
        private String lastmodiopname;
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

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getCreateopid() {
            return createopid;
        }

        public void setCreateopid(String createopid) {
            this.createopid = createopid;
        }

        public String getCreateopname() {
            return createopname;
        }

        public void setCreateopname(String createopname) {
            this.createopname = createopname;
        }

        public String getLastmoditime() {
            return lastmoditime;
        }

        public void setLastmoditime(String lastmoditime) {
            this.lastmoditime = lastmoditime;
        }

        public String getLastmodiopid() {
            return lastmodiopid;
        }

        public void setLastmodiopid(String lastmodiopid) {
            this.lastmodiopid = lastmodiopid;
        }

        public String getLastmodiopname() {
            return lastmodiopname;
        }

        public void setLastmodiopname(String lastmodiopname) {
            this.lastmodiopname = lastmodiopname;
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
