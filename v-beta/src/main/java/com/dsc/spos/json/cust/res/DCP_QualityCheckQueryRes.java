package com.dsc.spos.json.cust.res;

import com.alibaba.fastjson.annotation.JSONField;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Data
public class DCP_QualityCheckQueryRes extends JsonRes {






	@Getter
    @Setter
    private List<Datas> datas;


	@NoArgsConstructor
	@Data
	public class Datas {
		private String status;
		private String sourceBillNo;
		private String qcBillNo;
		private String oItem;
		private String oItem2;
		private String pluNo;
		private String pluName;
		private String featureNo;
		private String featureName;
		private String batchNo;
		private String prodDate;
		private String expDate;
		private String testUnit;
		private String testUnitName;
		private String deliverQty;
		private String passQty;
		private String rejectQty;
		private String result;
		private String memo;
		private String inspector;
		private String inspectorName;
		private String inspectDate;
		private String inspectTime;
		private String confirmDateTime;
		private String ownOpId;
		private String ownOpName;
		private String ownDeptId;
		private String ownDeptName;
		private String createOpId;
		private String createOpName;
		private String createDeptId;
		private String createDeptName;
		private String createTime;
		private String lastModiOpId;
		private String lastModiOpName;
		private String lastModiTime;
		private String confirmBy;
		private String confirmByName;
		private String confirmTime;
		private String cancelBy;
		private String cancelByName;
		private String cancelTime;
	}

}
