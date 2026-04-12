package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_GoodsSetCreateRes extends JsonBasicRes {
    private level1Elm datas;
    
    public level1Elm getDatas() {
        return datas;
    }
    public void setDatas(level1Elm datas) {
        this.datas = datas;
    }
    
    public class level1Elm{
        private String pluNo;
        
        public String getPluNo() {
            return pluNo;
        }
        public void setPluNo(String pluNo) {
            this.pluNo = pluNo;
        }
    }
    
    
}
