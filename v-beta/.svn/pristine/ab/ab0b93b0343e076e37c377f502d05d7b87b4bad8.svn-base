package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

/**
 * 服务名称：DCP_SupplierDelete
 * 服务说明：供应商删除
 * @author jinzma
 * @since  2022-03-15
 */
public class DCP_SupplierDeleteReq extends JsonBasicReq {
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    public class levelElm{
        private List<level1Elm> supplierList;
        
        public List<level1Elm> getSupplierList() {
            return supplierList;
        }
        public void setSupplierList(List<level1Elm> supplierList) {
            this.supplierList = supplierList;
        }
    }
    public class level1Elm{
        private String supplier;
        
        public String getSupplier() {
            return supplier;
        }
        public void setSupplier(String supplier) {
            this.supplier = supplier;
        }
    }
}
