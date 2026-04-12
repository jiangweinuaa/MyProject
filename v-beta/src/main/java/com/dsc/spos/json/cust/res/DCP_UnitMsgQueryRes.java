package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

/**
 * 单位信息查询 2018-09-20
 * @author yuanyy
 *
 */

@Getter
@Setter
public class DCP_UnitMsgQueryRes extends JsonRes {
	
	private List<level1Elm> datas;

  @Getter
  @Setter
	public class level1Elm{
		
		private String unit;
		private String unitName;
		private String udLength;
		private String unitType;
		private String roundType;
		private String status;
		private String creatorID;
		private String creatorName;
		private String creatorDeptID;
		private String creatorDeptName;
		private String create_datetime;
		private String lastmodifyID;
		private String lastmodifyName;
		private String lastmodify_datetime;

		private List<level2Elm> unitName_lang;


		
	}
	
	@Getter
  @Setter
	public class level2Elm {			
		private String name;
		private String langType;

	}
	
}
