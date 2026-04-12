package com.dsc.spos.json.cust.res;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

import java.util.List;
@Data
public class DCP_ConcModQueryRes extends JsonRes
{
	private List<level1Elm> datas;
	@Data
	public class level1Elm
	{
		private String rFuncNo;
		private String rFuncName;
	}
}

