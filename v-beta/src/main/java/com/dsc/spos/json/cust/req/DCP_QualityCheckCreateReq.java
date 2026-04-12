package com.dsc.spos.json.cust.req;

import java.util.List;

import com.dsc.spos.json.JSONFieldRequired;
import com.dsc.spos.json.JsonBasicReq;
 
import lombok.Getter;
import lombok.Setter;

/**
 * 质量检验单新增(批量)
 * 
 * @date 2024-10-19
 * @author 01029
 */
@Getter
@Setter
public class DCP_QualityCheckCreateReq extends JsonBasicReq {
	@JSONFieldRequired
	private levelRequest request;

	@Getter
	@Setter
	
	public class levelRequest {
		@JSONFieldRequired
		private List<Detail1> dataList;
 

	}

	@Getter
	@Setter
	public class Detail1 {
		@JSONFieldRequired (display = "检验类型")
		private String qcType;
		@JSONFieldRequired (display = "单据日期")
		private String bDate;
		@JSONFieldRequired (display = "开单人员")
		private String employeeID;
		@JSONFieldRequired (display = "开单部门")
		private String departID;
		@JSONFieldRequired (display = "检验日期  ")
		private String inspectDate;
		@JSONFieldRequired (display = "检验时间 ")
		private String inspectTime;
		@JSONFieldRequired (display = "来源单号")
		private String sourceBillNo;
		@JSONFieldRequired (display = "来源项次")
		private String oItem;
		@JSONFieldRequired (display = "来源项序")
		private String oItem2;
		@JSONFieldRequired (display = "品号")
		private String pluNo;
		@JSONFieldRequired (display = "特征码")
		private String featureNo;
		@JSONFieldRequired (display = "来源单位")
		private String oUnit;
		@JSONFieldRequired (display = "来源数量")
		private String oQty;
		@JSONFieldRequired (display = "供应商编号")
		private String supplier;
		@JSONFieldRequired (display = "批号")
		private String batchNo;
		@JSONFieldRequired (display = "生产日期")
		private String prodDate;
		@JSONFieldRequired (display = "有效日期")
		private String expDate;



	}
 

}
