package com.dsc.spos.service.imp.json;

import java.net.URLEncoder;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_BatchGoordinateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_BatchGoordinateUpdateRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.OrderUtil;
import com.dsc.spos.waimai.GaoDeGeoModel;
import com.google.gson.reflect.TypeToken;

public class DCP_BatchGoordinateUpdate extends SPosAdvanceService<DCP_BatchGoordinateUpdateReq,DCP_BatchGoordinateUpdateRes>
{
	@Override
	protected void processDUID(DCP_BatchGoordinateUpdateReq req, DCP_BatchGoordinateUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		if(req.getType().equals("0"))
		{
			String sql="select * from DCP_ORG  where status='100' and (LATITUDE is null  or LONGITUDE is null) and ADDRESS is not null ";
			List<Map<String, Object>> listshop=this.doQueryData(sql, null);
			for (Map<String, Object> map : listshop) 
			{
				String ADDRESS=map.get("ADDRESS").toString();
				ADDRESS=URLEncoder.encode(ADDRESS, "utf-8" ); 
				//调用高德地图查找经纬度
				String url="https://restapi.amap.com/v3/geocode/geo?address="+ADDRESS+"&output=JSON&key=66b4b07702ff29ecc1ee9fbbf6980675";
				String responseStr= OrderUtil.Sendcom("",url,"GET");
				//解析返回的内容
				try
				{
					ParseJson pj = new ParseJson();
					GaoDeGeoModel curreginfo=pj.jsonToBean(responseStr, new TypeToken<GaoDeGeoModel>(){});
					pj=null;
					
					if(curreginfo.getStatus().equals("1"))
					{
						//默认是第一条位置的经纬度
						String jinwei=curreginfo.getGeocodes().get(0).getLocation();
						String[] listjinwei=jinwei.split(",");
						//更新经纬度
						UptBean ub1 = null;	
						ub1 = new UptBean("DCP_Org");
					  //add Value
						ub1.addUpdateValue("LONGITUDE", new DataValue(listjinwei[0], Types.VARCHAR));
						ub1.addUpdateValue("LATITUDE", new DataValue(listjinwei[1], Types.VARCHAR));
						//顺便更新下城市等信息
						ub1.addUpdateValue("PROVINCE", new DataValue(curreginfo.getGeocodes().get(0).getProvince(), Types.VARCHAR));
						ub1.addUpdateValue("CITY", new DataValue(curreginfo.getGeocodes().get(0).getCity(), Types.VARCHAR));
						ub1.addUpdateValue("COUNTY", new DataValue(curreginfo.getGeocodes().get(0).getDistrict(), Types.VARCHAR));
						//ub1.addUpdateValue("STREET", new DataValue(curreginfo.getGeocodes().get(0).getTownship(), Types.VARCHAR));
					//condition
						ub1.addCondition("EID", new DataValue(map.get("EID").toString(), Types.VARCHAR));
						ub1.addCondition("organizationNO", new DataValue(map.get("ORGANIZATIONNO").toString(), Types.VARCHAR));		
						this.addProcessData(new DataProcessBean(ub1));
					}
				}
				catch(Exception ex)
				{
					
				}
				
		  }
			
		}
		else
		{
			//更新订单上面的经纬度
			String orderno="";
			if(req.getOrderNO()!=null&&!req.getOrderNO().isEmpty())
			{
				orderno=" and ORDERNO='"+req.getOrderNO()+"' ";
			}
			String sql="select * from OC_ORDER where (LATITUDE is null  or LONGITUDE is null) and ADDRESS is not null and SHIPTYPE='2' "+orderno;
			List<Map<String, Object>> listshop=this.doQueryData(sql, null);
			for (Map<String, Object> map : listshop) 
			{
				String ADDRESS=map.get("ADDRESS").toString();
				//调用高德地图查找经纬度
				String url="https://restapi.amap.com/v3/geocode/geo?address="+ADDRESS+"&output=JSON&key=66b4b07702ff29ecc1ee9fbbf6980675";
				String responseStr= OrderUtil.Sendcom("",url,"GET");
				//解析返回的内容
				ParseJson pj = new ParseJson();
				GaoDeGeoModel curreginfo=pj.jsonToBean(responseStr, new TypeToken<GaoDeGeoModel>(){});
				pj=null;
				
				if(curreginfo.getStatus().equals("0"))
				{
					//默认是第一条位置的经纬度
					String jinwei=curreginfo.getGeocodes().get(0).getLocation();
					String[] listjinwei=jinwei.split(",");
					//更新经纬度
					UptBean ub1 = null;	
					ub1 = new UptBean("OC_ORDER");
				  //add Value
					ub1.addUpdateValue("LONGITUDE", new DataValue(listjinwei[0], Types.VARCHAR));
					ub1.addUpdateValue("LATITUDE", new DataValue(listjinwei[1], Types.VARCHAR));

				//condition
					ub1.addCondition("EID", new DataValue(map.get("EID").toString(), Types.VARCHAR));
					ub1.addCondition("organizationNO", new DataValue(map.get("ORGANIZATIONNO").toString(), Types.VARCHAR));
					ub1.addCondition("ORDERNO", new DataValue(map.get("ORDERNO").toString(), Types.VARCHAR));	
					this.addProcessData(new DataProcessBean(ub1));
				}
				
			}
			
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BatchGoordinateUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BatchGoordinateUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BatchGoordinateUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BatchGoordinateUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_BatchGoordinateUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_BatchGoordinateUpdateReq>(){};
	}

	@Override
	protected DCP_BatchGoordinateUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_BatchGoordinateUpdateRes();
	}

}


