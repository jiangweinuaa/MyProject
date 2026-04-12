package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

/**
 * 服務函數：ProcessTaskDelete
 *    說明：加工任务删除
 * 服务说明：加工任务删除
 * @author luoln 
 * @since  2017-03-31
 */
public class DCP_ProcessTaskDeleteReq extends JsonBasicReq {

	private levelElm request;

	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}

    @Data
	public class levelElm{
		private List<ProcessTaskList> processTaskList;
    }

    @Data
    public class ProcessTaskList{
        private String processTaskNo;
    }


}
