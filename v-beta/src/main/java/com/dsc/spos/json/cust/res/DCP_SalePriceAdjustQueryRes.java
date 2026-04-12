package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import java.util.List;

/**
 * 服务名称：DCP_SalePriceAdjustQuery
 * 服务说明：自建门店调价查询(零售价)
 * @author jinzma
 * @since  2022-02-23
 */
public class DCP_SalePriceAdjustQueryRes extends JsonRes {
    private List<level1Elm> datas;
    private String templateId;
    
    public String getTemplateId() {
        return templateId;
    }
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    public List<level1Elm> getDatas() {
        return datas;
    }
    public void setDatas(List<level1Elm> datas) {
        this.datas = datas;
    }
    
    public class level1Elm{
        private String salePriceAdjustNo;
        private String bDate;
        private String type;
        private String status;
        private String createOpId;
        private String createOpName;
        private String createTime;
        private String lastModiOpId;
        private String lastModiOpName;
        private String lastModiTime;
        
        public String getSalePriceAdjustNo() {
            return salePriceAdjustNo;
        }
        public void setSalePriceAdjustNo(String salePriceAdjustNo) {
            this.salePriceAdjustNo = salePriceAdjustNo;
        }
        public String getbDate() {
            return bDate;
        }
        public void setbDate(String bDate) {
            this.bDate = bDate;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String getCreateOpId() {
            return createOpId;
        }
        public void setCreateOpId(String createOpId) {
            this.createOpId = createOpId;
        }
        public String getCreateOpName() {
            return createOpName;
        }
        public void setCreateOpName(String createOpName) {
            this.createOpName = createOpName;
        }
        public String getCreateTime() {
            return createTime;
        }
        public void setCreateTime(String createTime) {
            this.createTime = createTime;
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
    }
    
}
