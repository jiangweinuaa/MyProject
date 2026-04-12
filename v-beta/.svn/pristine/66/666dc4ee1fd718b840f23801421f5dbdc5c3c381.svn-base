package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

public class DCP_EstClearGoodsUpdateReq extends JsonBasicReq {
	// 0估清,1反估清
	private String docType;

	private List<level1Elm> datas;

	public class level1Elm {
		private String pluno;

		public String getPluno() {
			return pluno;
		}

		public void setPluno(String pluno) {
			this.pluno = pluno;
		}
	}

	public List<level1Elm> getDatas() {
		return datas;
	}

	public void setDatas(List<level1Elm> datas) {
		this.datas = datas;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

}
