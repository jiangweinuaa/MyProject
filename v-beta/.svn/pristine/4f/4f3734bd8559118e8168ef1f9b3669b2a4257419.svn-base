package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import java.util.List;

/**
 * 服务名称：DCP_DisplayBaseDelete
 * 服务说明：客显基础资料删除
 * @author jinzma
 * @since  2022-04-26
 */
public class DCP_DisplayBaseDeleteReq extends JsonBasicReq {
    private levelElm request;
    
    public levelElm getRequest() {
        return request;
    }
    public void setRequest(levelElm request) {
        this.request = request;
    }
    
    public class levelElm {
        private List<level1Elm> templateList;
        
        public List<level1Elm> getTemplateList() {
            return templateList;
        }
        public void setTemplateList(List<level1Elm> templateList) {
            this.templateList = templateList;
        }
    }
    public class level1Elm {
        private String templateNo;
        
        public String getTemplateNo() {
            return templateNo;
        }
        public void setTemplateNo(String templateNo) {
            this.templateNo = templateNo;
        }
    }
}
