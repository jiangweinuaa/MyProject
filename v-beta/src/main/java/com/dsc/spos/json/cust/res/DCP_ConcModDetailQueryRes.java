package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;

@Data
public class DCP_ConcModDetailQueryRes extends JsonRes
{
	private List<level1Elm> datas;
	@Data
	public class level1Elm
	{
		private String rFuncNo;
		private String rfuncName;
		private String machineCode;
		private String token;
		private String opNo;
		private String opName;
		private String lastModiTime;
	}
}

