package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

public class DCP_OrgAccountQueryRes extends JsonRes {

	@Getter
	@Setter
    private level1Elm datas;

	@Getter
	@Setter
    public class level1Elm {
        private List<account> accountList;
    }

	@Getter
	@Setter
    public class account {
		private String shopID;
		private String shopName;
		private String account;
        private String bankNo;
		private String status;
    }


}
