package com.dsc.spos.json.cust.req;

import com.dsc.spos.json.JsonBasicReq;
import lombok.Data;

import java.util.List;

@Data
public class DCP_TouchMenuDeleteReq  extends JsonBasicReq{

	private level1Elm request;

	@Data
	public class level1Elm{
	    private List<level2Elm> menuList;
    }

    @Data
    public class level2Elm{
	    private String menuNo;          // 菜单编码
    }
	
}
