package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import lombok.Data;

/**
 * 服务函数：DCP_GoodsBomQuery
 * 服务说明：商品BOM查询
 * @author jinzma
 * @since 2020-08-14
 */

@Data
public class DCP_GoodsBomQueryRes  extends JsonRes {

	private List<level1Elm> datas;


    @Data
	public class level1Elm{
		private String bomNo;
		private String bomType;
		private String pluNo;
		private String unit;
		private String mulQty;
        private String versionNum;
        private String prodType;
        private String inWarehouse;
		private String inWarehouseName;
        private String isLocation;
        private String dispType;
		private List<level2Elm> materialList;

	}

    @Data
	public class level2Elm{
		private String material_pluNo;
		private String material_pluName;
		private String material_finalProdBaseQty;
		private String material_unit;
		private String material_unitName;
		private String material_unitUdLength;
		private String material_rawMaterialBaseQty;
		private String lossRate;
        private String costRate;
		private String isBuckle;
		private String isReplace;
		private String sortId;
		private String material_baseUnit;
		private String material_baseUnitName;
		private String material_unitRatio;
		private String material_price;
		private String material_distriPrice;
		private String material_isBatch;
		private String material_listImage;
		private String material_baseUnitUdLength;
        private String material_stockQty;
        private String material_spec;
        private String kWarehouse;
		private String kWarehouseName;
        private String kIsLocation;
		

	}

}
