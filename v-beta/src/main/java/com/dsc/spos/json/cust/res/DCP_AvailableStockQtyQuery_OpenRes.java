package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.cust.JsonRes;
import com.dsc.spos.json.cust.res.DCP_ValidGoodsQuery_OpenRes.level2Barcode;
import com.dsc.spos.json.cust.res.DCP_ValidGoodsQuery_OpenRes.level2Feature;

public class DCP_AvailableStockQtyQuery_OpenRes extends JsonRes
{
	private levelDatas datas;
	
	public levelDatas getDatas()
	{
		return datas;
	}

	public void setDatas(levelDatas datas)
	{
		this.datas = datas;
	}
	public class levelDatas
	{
		private List<level1Elm> pluList;

		public List<level1Elm> getPluList()
		{
			return pluList;
		}

		public void setPluList(List<level1Elm> pluList)
		{
			this.pluList = pluList;
		}
		
	}
	
	public class level1Elm
	{
		private String organizationNo;
		private String pluNo;
		private String pluName;
		private String baseUnit;
		private String baseUnitName;
		private String featureNo;
		private String warehouse;
		private String availableQty;		
		private List<level2Barcode> pluBarcodeList;
		public String getOrganizationNo()
		{
			return organizationNo;
		}
		public void setOrganizationNo(String organizationNo)
		{
			this.organizationNo = organizationNo;
		}
		public String getPluNo()
		{
			return pluNo;
		}
		public void setPluNo(String pluNo)
		{
			this.pluNo = pluNo;
		}
		public String getPluName()
		{
			return pluName;
		}
		public void setPluName(String pluName)
		{
			this.pluName = pluName;
		}
		public String getBaseUnit()
		{
			return baseUnit;
		}
		public void setBaseUnit(String baseUnit)
		{
			this.baseUnit = baseUnit;
		}		
		public String getBaseUnitName()
		{
			return baseUnitName;
		}
		public void setBaseUnitName(String baseUnitName)
		{
			this.baseUnitName = baseUnitName;
		}
		public String getFeatureNo()
		{
			return featureNo;
		}
		public void setFeatureNo(String featureNo)
		{
			this.featureNo = featureNo;
		}
		public String getWarehouse()
		{
			return warehouse;
		}
		public void setWarehouse(String warehouse)
		{
			this.warehouse = warehouse;
		}
		public String getAvailableQty()
		{
			return availableQty;
		}
		public void setAvailableQty(String availableQty)
		{
			this.availableQty = availableQty;
		}
		public List<level2Barcode> getPluBarcodeList()
		{
			return pluBarcodeList;
		}
		public void setPluBarcodeList(List<level2Barcode> pluBarcodeList)
		{
			this.pluBarcodeList = pluBarcodeList;
		}
		
	}
	
	public class level2Barcode
	{
		private String pluBarcode;
		private String featureNo;
		private String featureName;
		private String unit;
		private String unitName;
		private String availableQty;
		public String getPluBarcode()
		{
			return pluBarcode;
		}
		public void setPluBarcode(String pluBarcode)
		{
			this.pluBarcode = pluBarcode;
		}
		public String getFeatureNo()
		{
			return featureNo;
		}
		public void setFeatureNo(String featureNo)
		{
			this.featureNo = featureNo;
		}
		public String getFeatureName()
		{
			return featureName;
		}
		public void setFeatureName(String featureName)
		{
			this.featureName = featureName;
		}
		public String getUnit()
		{
			return unit;
		}
		public void setUnit(String unit)
		{
			this.unit = unit;
		}
		public String getUnitName()
		{
			return unitName;
		}
		public void setUnitName(String unitName)
		{
			this.unitName = unitName;
		}
		public String getAvailableQty()
		{
			return availableQty;
		}
		public void setAvailableQty(String availableQty)
		{
			this.availableQty = availableQty;
		}
		
	}

}
