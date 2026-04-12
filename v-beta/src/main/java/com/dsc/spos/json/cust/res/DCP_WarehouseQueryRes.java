package com.dsc.spos.json.cust.res;

import java.util.List;
import com.dsc.spos.json.cust.JsonRes;
import lombok.Getter;
import lombok.Setter;

/**
 * 服務函數：WarehouseGet
 *    說明：仓库查询
 * 服务说明： 仓库查询
 * @author luoln
 * @since  2017-11-30
 */
@Setter
@Getter
public class DCP_WarehouseQueryRes extends JsonRes {

	private List<level1Elm> datas;

    @Getter
	@Setter
	public class level1Elm{
		private String warehouse;
		private String warehouseName;
		private String isCost;
        private String organizationNo;
        private String organizationName;
        private String deductionRate;
        private String wareHouseType;
        private String stockManageType;
        private String status;
        private String isLocation;
        private String isCheckStock;
    }

}
