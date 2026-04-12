package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MachShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_MachShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.OrderUtil;
import com.google.gson.reflect.TypeToken;

public class DCP_MachShopQuery extends SPosBasicService<DCP_MachShopQueryReq, DCP_MachShopQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_MachShopQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_MachShopQueryReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MachShopQueryReq> (){};
	}

	@Override
	protected DCP_MachShopQueryRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_MachShopQueryRes();
	}

	@Override
	protected DCP_MachShopQueryRes processJson(DCP_MachShopQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		DCP_MachShopQueryRes res = this.getResponse();
		DCP_MachShopQueryRes.responseDatas datas = res.new responseDatas();
		
		String eId = req.getRequest().geteId();
		String shippingShopNo = req.getRequest().getShippingShopNo();
		String langType = req.getLangType();
		if (langType == null || langType.isEmpty())
		{
			langType = "zh_CN";
		}
		//先查询自己门店 是否支持生产。
		String sql = " select A.ORGANIZATIONNO,A.CITY,A.LATITUDE,A.LONGITUDE,A.BELFIRM,AL.ORG_NAME,CL.ORG_NAME BELFIRMNAME,C.ISPRODUCTION from DCP_org A "
				+ " left join DCP_org_LANG AL on A.EID=AL.EID and A.ORGANIZATIONNO=AL.ORGANIZATIONNO and AL.Lang_Type='"+langType+"'"
				+ "  left join DCP_ORG_ORDERSET  C on  A.EID=C.EID and  A.ORGANIZATIONNO=C.ORGANIZATIONNO "
				+ "  left join DCP_org_LANG CL on A.EID=CL.EID and A.BELFIRM=CL.ORGANIZATIONNO and CL.Lang_Type='zh_CN'"
				+ " where A.EID='"+eId+"' and A.ORGANIZATIONNO='"+shippingShopNo+"' ";
		
		List<Map<String, Object>> getShippingShopInfo = this.doQueryData(sql, null);
		if(getShippingShopInfo!=null&&getShippingShopInfo.isEmpty()==false)
		{
			double longitude_cur = 0;
			double latitude_cur = 0;
			try
			{
				String LONGITUDE_cur = getShippingShopInfo.get(0).get("LONGITUDE").toString();
				String LATITUDE_cur = getShippingShopInfo.get(0).get("LATITUDE").toString();
				longitude_cur = Double.parseDouble(LONGITUDE_cur);
				latitude_cur = Double.parseDouble(LATITUDE_cur);									
			} 
			catch (Exception e)
			{
				// TODO: handle exception
			}
			String ISPRODUCTION = getShippingShopInfo.get(0).get("ISPRODUCTION").toString();
			//如果自己门店支持生产，也要加进去，后面的查询sql已经排除了，所以不会重复
			DCP_MachShopQueryRes.lever1Elm curBelfirmModel = res.new lever1Elm();
			curBelfirmModel.setBelFirmNo(getShippingShopInfo.get(0).get("BELFIRM").toString());
			curBelfirmModel.setBelFirmName(getShippingShopInfo.get(0).get("BELFIRMNAME").toString());
			curBelfirmModel.setMachShopList(new ArrayList<DCP_MachShopQueryRes.lever2Elm>());
			DCP_MachShopQueryRes.lever2Elm curOrgModel = res.new lever2Elm();
			curOrgModel.setMachShopNo(getShippingShopInfo.get(0).get("ORGANIZATIONNO").toString());
			curOrgModel.setMachShopName(getShippingShopInfo.get(0).get("ORG_NAME").toString());
			curOrgModel.setCityNo(getShippingShopInfo.get(0).get("CITY").toString());
			curOrgModel.setCityName(getShippingShopInfo.get(0).get("CITY").toString());
			curOrgModel.setDistance("0.1");//自己对自己门店距离，距离0表示异常的
			curBelfirmModel.getMachShopList().add(curOrgModel);
			
			
			List<DCP_MachShopQueryRes.lever1Elm>  addList = new ArrayList<DCP_MachShopQueryRes.lever1Elm>();
			//查询排除当前配送门店自己是生产的门店	
			sql = "";
			sql = this.getQuerySql(req);
			
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if(getQData!=null&&getQData.isEmpty()==false)
			{
				//公司别
				Map<String, Boolean> condition_belfirm = new HashMap<String, Boolean>(); //查詢條件
				condition_belfirm.put("BELFIRM", true);		
				//调用过滤函数
				List<Map<String, Object>> belfirmDatas=MapDistinct.getMap(getQData, condition_belfirm);
				
				
				//门店
				Map<String, Boolean> condition_org = new HashMap<String, Boolean>(); //查詢條件
				condition_org.put("ORGANIZATIONNO", true);		
				//调用过滤函数
				List<Map<String, Object>> orgDatas=MapDistinct.getMap(getQData, condition_org);
				for (Map<String, Object> map : belfirmDatas)
				{
					DCP_MachShopQueryRes.lever1Elm oneLv1 = res.new lever1Elm();
					oneLv1.setMachShopList(new ArrayList<DCP_MachShopQueryRes.lever2Elm>());
					String belFirmNo = map.get("BELFIRM").toString();
					String belFirmName = map.get("BELFIRMNAME").toString();
					oneLv1.setBelFirmNo(belFirmNo);
					oneLv1.setBelFirmName(belFirmName);
					
					for (Map<String, Object> mapItem : orgDatas)
					{
						String belFirmNo_detail = map.get("BELFIRM").toString();
						if(belFirmNo_detail!=null&&belFirmNo_detail.isEmpty()==false&&belFirmNo_detail.equals(belFirmNo))
						{
							DCP_MachShopQueryRes.lever2Elm oneLv2 = res.new lever2Elm();
							oneLv2.setMachShopNo(mapItem.get("ORGANIZATIONNO").toString());
							oneLv2.setMachShopName(mapItem.get("ORG_NAME").toString());
							oneLv2.setCityNo(mapItem.get("CITY").toString());
							oneLv2.setCityName(mapItem.get("CITY").toString());
							
							double distance = 0;
							if(latitude_cur==0||longitude_cur==0)
							{
								distance = 0;
							}
							else
							{
								double longitude = 0;
								double latitude = 0;
								try
								{
									String LONGITUDE = mapItem.get("LONGITUDE").toString();
									String LATITUDE = mapItem.get("LATITUDE").toString();
									longitude = Double.parseDouble(LONGITUDE);
									latitude = Double.parseDouble(LATITUDE);									
								} 
								catch (Exception e)
								{
									// TODO: handle exception
								}
								
								if(latitude==0||longitude==0)
								{
									distance = 0;
								}
								else
								{
									distance = OrderUtil.getDistance(latitude_cur, longitude_cur, latitude, longitude);
								}
							}
							oneLv2.setDistance(distance+"");
							oneLv1.getMachShopList().add(oneLv2);
						}
						
					}
					
					addList.add(oneLv1);
					
				}
				
				
			}
			
			if(ISPRODUCTION.equals("Y"))
			{
				if(addList.size()>0)
				{
					boolean isAdd = false;
					
					for (DCP_MachShopQueryRes.lever1Elm item : addList)
					{
						if(item.getBelFirmNo().equals(curBelfirmModel.getBelFirmNo()))
						{
							isAdd = true;
							item.getMachShopList().add(curOrgModel);
							break;
						}
					}
					
					if(!isAdd)
					{
						addList.add(curBelfirmModel);
					}
					
				}
				else
				{
					addList.add(curBelfirmModel);
				}
			}
			
			if(addList.size()>0)
			{
				datas.setBelFirmList(addList);
			}
			
			
		}
		else
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "配送机构:"+shippingShopNo+"不存在门店信息表");
		}
		res.setDatas(datas);
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_MachShopQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		
		String eId = req.getRequest().geteId();
		String shippingShopNo = req.getRequest().getShippingShopNo();
		String langType = req.getLangType();
		if (langType == null || langType.isEmpty())
		{
			langType = "zh_CN";
		}
		String sql = "";
		
		StringBuilder strBuffer = new StringBuilder("");
		
		strBuffer.append("  select A.ORGANIZATIONNO,AL.ORG_NAME,C.CITY,C.LATITUDE,C.LONGITUDE,C.BELFIRM,CL.ORG_NAME BELFIRMNAME from ");
		strBuffer.append(" (");
		strBuffer.append(" select distinct A.ORGANIZATIONNO,A.EID from DCP_ORG_ORDERSET  A  where A.EID='"+eId+"' and A.ISPRODUCTION='Y'  and A.Radiatetype='0' and A.ORGANIZATIONNO<>'"+shippingShopNo+"' ");
		strBuffer.append(" union all ");
		strBuffer.append(" select distinct A.ORGANIZATIONNO,A.EID from DCP_ORG_ORDERSET  A ");
		strBuffer.append(" inner join  DCP_ORG_ORDERSET_RADIATEORG B on A.EID=B.EID AND A.ORGANIZATIONNO=B.ORGANIZATIONNO ");
		strBuffer.append(" where A.EID='"+eId+"' and A.ISPRODUCTION='Y'  and A.Radiatetype='1' AND B.RADIATESHIPPINGSHOP='"+shippingShopNo+"' and A.ORGANIZATIONNO<>'"+shippingShopNo+"' ");
		strBuffer.append("  ) A");
		strBuffer.append(" left join DCP_org_LANG AL on A.EID=AL.EID and A.ORGANIZATIONNO=AL.ORGANIZATIONNO and AL.Lang_Type='"+langType+"'");
		strBuffer.append(" left join DCP_org  C on  A.EID=C.EID and  A.ORGANIZATIONNO=C.ORGANIZATIONNO ");
		strBuffer.append(" left join DCP_org_LANG CL on C.EID=CL.EID and C.BELFIRM=CL.ORGANIZATIONNO and CL.Lang_Type='"+langType+"'");
		sql = strBuffer.toString();
		return sql;
	}

}
