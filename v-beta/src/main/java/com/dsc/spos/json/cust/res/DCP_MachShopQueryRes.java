package com.dsc.spos.json.cust.res;

import java.util.List;

import com.dsc.spos.json.JsonBasicRes;

public class DCP_MachShopQueryRes extends JsonBasicRes 
{

	private responseDatas datas;

	public responseDatas getDatas()
	{
		return datas;
	}

	public void setDatas(responseDatas datas)
	{
		this.datas = datas;
	}

	public class responseDatas
	{
		private List<lever1Elm> belFirmList;

		public List<lever1Elm> getBelFirmList()
		{
			return belFirmList;
		}

		public void setBelFirmList(List<lever1Elm> belFirmList)
		{
			this.belFirmList = belFirmList;
		}

	}
	
	public class lever1Elm
	{
		private String belFirmNo;
		private String belFirmName;
		private List<lever2Elm> machShopList;
		public String getBelFirmNo()
		{
			return belFirmNo;
		}
		public void setBelFirmNo(String belFirmNo)
		{
			this.belFirmNo = belFirmNo;
		}
		public String getBelFirmName()
		{
			return belFirmName;
		}
		public void setBelFirmName(String belFirmName)
		{
			this.belFirmName = belFirmName;
		}
		public List<lever2Elm> getMachShopList()
		{
			return machShopList;
		}
		public void setMachShopList(List<lever2Elm> machShopList)
		{
			this.machShopList = machShopList;
		}
		

	}
	
	public class lever2Elm
	{
		private String machShopNo;
		private String machShopName;
		private String cityNo;
		private String cityName;
		private String distance;
		public String getMachShopNo()
		{
			return machShopNo;
		}
		public void setMachShopNo(String machShopNo)
		{
			this.machShopNo = machShopNo;
		}
		public String getMachShopName()
		{
			return machShopName;
		}
		public void setMachShopName(String machShopName)
		{
			this.machShopName = machShopName;
		}
		public String getCityNo()
		{
			return cityNo;
		}
		public void setCityNo(String cityNo)
		{
			this.cityNo = cityNo;
		}
		public String getCityName()
		{
			return cityName;
		}
		public void setCityName(String cityName)
		{
			this.cityName = cityName;
		}
		public String getDistance()
		{
			return distance;
		}
		public void setDistance(String distance)
		{
			this.distance = distance;
		}
		
	}
}
