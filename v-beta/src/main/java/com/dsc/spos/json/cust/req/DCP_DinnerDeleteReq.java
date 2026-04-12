package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;

import java.util.List;

public class DCP_DinnerDeleteReq extends JsonBasicReq
{
	private levelElm request;
	public levelElm getRequest() {
		return request;
	}
	public void setRequest(levelElm request) {
		this.request = request;
	}
	public class levelElm{


		private List<String> dinnerNo;

        public List<String> getDinnerNo() {
            return dinnerNo;
        }

        public void setDinnerNo(List<String> dinnerNo) {
            this.dinnerNo = dinnerNo;
        }
    }

}
