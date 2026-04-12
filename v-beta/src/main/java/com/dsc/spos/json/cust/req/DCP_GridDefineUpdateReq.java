package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JsonBasicReq;

/**
 * 表单自定义修改
 * @author yuanyy 2019-02-16
 *
 */
public class DCP_GridDefineUpdateReq extends JsonBasicReq {
	
	private level1Elm request;
	public class level1Elm
	{
		private String modularNo;
		private String status;
		
		private List<level2Elm> grids;

		public String getModularNo() {
			return modularNo;
		}

		public void setModularNo(String modularNo) {
			this.modularNo = modularNo;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<level2Elm> getGrids() {
			return grids;
		}

		public void setGrids(List<level2Elm> grids) {
			this.grids = grids;
		}
		
	}
	
	public class level2Elm{
		private String gridNo;
		private List<level3Elm> fields;
		public String getGridNo() {
			return gridNo;
		}
		public void setGridNo(String gridNo) {
			this.gridNo = gridNo;
		}
		public List<level3Elm> getFields() {
			return fields;
		}
		public void setFields(List<level3Elm> fields) {
			this.fields = fields;
		}
		
	}
	
	public class level3Elm{
		
		private String fieldName;
		private String isShow;
		private String isMove;
		private String priority;
		
		public String getFieldName() {
			return fieldName;
		}
		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}
		public String getIsShow() {
			return isShow;
		}
		public void setIsShow(String isShow) {
			this.isShow = isShow;
		}
		public String getIsMove() {
			return isMove;
		}
		public void setIsMove(String isMove) {
			this.isMove = isMove;
		}
		public String getPriority() {
			return priority;
		}
		public void setPriority(String priority) {
			this.priority = priority;
		}
		
	}
	public level1Elm getRequest() {
		return request;
	}
	public void setRequest(level1Elm request) {
		this.request = request;
	}
	
}
