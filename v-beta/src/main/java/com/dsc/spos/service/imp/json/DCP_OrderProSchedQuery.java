package com.dsc.spos.service.imp.json;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.json.cust.req.DCP_OrderProSchedQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderProSchedQueryRes;
import com.dsc.spos.json.utils.ParseJson;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.OrderUtil;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.GaoDeGeoModel;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderProSchedQuery extends SPosBasicService<DCP_OrderProSchedQueryReq, DCP_OrderProSchedQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderProSchedQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OrderProSchedQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderProSchedQueryReq>(){};
	}

	@Override
	protected DCP_OrderProSchedQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderProSchedQueryRes();
	}

	@Override
	protected DCP_OrderProSchedQueryRes processJson(DCP_OrderProSchedQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		DCP_OrderProSchedQueryRes res=new DCP_OrderProSchedQueryRes();
		
		if(req.getOptype().equals("2"))
		{
			//批量调度的，直接查询所有门店
			//加入所属公司的判断
				
			String dsql="select * from (  select a.*,b.Org_Name,'0' DISTANCE from DCP_ORG a left join DCP_ORG_Lang b on a.EID=b.EID and a.OrganizationNo=b.OrganizationNo and b.Lang_Type='"+req.getLangType()+"' "
			    + "  where a.EID='"+req.geteId()+"' and (A.BELFIRM='"+req.getShopId()+"'  ) )"
				
					+ " order by DISTANCE,ORGANIZATIONNO ";
			List<Map<String, Object>> dsqldate=this.doQueryData(dsql, null);
			{
				res.setDatas(new ArrayList<DCP_OrderProSchedQueryRes.level1Elm>());
				for (Map<String, Object> map : dsqldate) 
				{
					DCP_OrderProSchedQueryRes.level1Elm lv1=res.new level1Elm();
					String shoptemp=map.get("ORGANIZATIONNO").toString();
					lv1.setoEId(map.get("EID").toString());
					lv1.setoShopId(map.get("ORGANIZATIONNO").toString());
					lv1.setO_shopName(map.get("ORG_NAME").toString());
					res.getDatas().add(lv1)	;
					lv1 = null;
				}
			}
			return res;
			
		}
		
		String orderno=req.getDatas().get(0).getOrderNO();
		String doctype=req.getDatas().get(0).getDocType();
		if(doctype==null||doctype.isEmpty())
		{
			doctype="6";
		}
		
		String langtype="zh_CN";
		List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
		if(lstProd!=null&&!lstProd.isEmpty())
		{
			langtype=lstProd.get(0).getHostLang().getValue();
		}
		
		String sql="select * from OC_ORDER where EID='"+req.geteId()+"'  and ORDERNO='"+orderno+"' and LOAD_DOCTYPE='"+doctype+"'  ";
		List<Map<String, Object>> listsqldate=this.doQueryData(sql, null);
		if(listsqldate!=null&&!listsqldate.isEmpty())
		{
			String ADDRESS=listsqldate.get(0).get("ADDRESS").toString();
			String LATITUDE=listsqldate.get(0).get("LATITUDE").toString();
			String LONGITUDE=listsqldate.get(0).get("LONGITUDE").toString();
			String SDATE=listsqldate.get(0).get("SDATE").toString();
			String bfirm=listsqldate.get(0).get("BELFIRM").toString();
			
			String[] listjinwei=new String[2];
			if(LATITUDE.isEmpty()||LONGITUDE.isEmpty()||LATITUDE.equals("0")||LONGITUDE.equals("0"))
			{
				ADDRESS=URLEncoder.encode(ADDRESS, "utf-8" ); 
				
				String url="";
				try
				{
					//经纬度为空的时候先调用一下高德的地图取一下经纬度
				  //调用高德地图查找经纬度
				  url="https://restapi.amap.com/v3/geocode/geo?address="+ADDRESS+"&output=JSON&key=575f128b5da35246777d2c5c24f9b68b";
					String responseStr= OrderUtil.Sendcom("",url,"GET");
					//解析返回的内容
					ParseJson pj = new ParseJson();
					GaoDeGeoModel curreginfo=pj.jsonToBean(responseStr, new TypeToken<GaoDeGeoModel>(){});
					pj=null;
					
					//String sstatus= curreginfo.getStatus();
					if(curreginfo.getStatus().equals("1"))
					{
					//默认是第一条位置的经纬度
						String jinwei=curreginfo.getGeocodes().get(0).getLocation();
					  listjinwei=jinwei.split(",");
					}
				}
				catch(Exception ex)
				{
				//报错写日志
  				HelpTools.writelog_fileName("地图调用失败:"+url+ex.getMessage(), "MAPCALL");
				}
			}
			else
			{
				listjinwei[0]=LONGITUDE;
				listjinwei[1]=LATITUDE;
			}
			
		String dsql="select * from (  select a.*,b.Org_Name, F_CRM_GetDistance("+listjinwei[1]+","+listjinwei[0] +",a.LATITUDE,a.LONGITUDE) DISTANCE from DCP_ORG a left join DCP_ORG_Lang b on a.EID=b.EID and a.OrganizationNo=b.OrganizationNo and b.Lang_Type='"+req.getLangType()+"'  where a.EID='"+req.geteId()+"' and a.Org_Form='2'" 
					+ "  and (A.BELFIRM='"+req.getShopId()+"'  )   )"
				+ " order by DISTANCE,ORGANIZATIONNO ";
			if(langtype.equals("zh_TW"))
			{
				dsql="select * from (  select a.*,b.Org_Name, F_CRM_GetDistance("+listjinwei[1]+","+listjinwei[0] +",a.LATITUDE,a.LONGITUDE) DISTANCE from DCP_ORG a left join DCP_ORG_Lang b on a.EID=b.EID and a.OrganizationNo=b.OrganizationNo and b.Lang_Type='"+req.getLangType()+"'  where a.EID='"+req.geteId()+"' and a.Org_Form='2'" 
						+ "  and (A.BELFIRM='"+req.getShopId()+"'  )   )"
					+ " order by DISTANCE,ORGANIZATIONNO ";
			}
			
			List<Map<String, Object>> dsqldate=this.doQueryData(dsql, null);
			String MACHSHOP="";
			if(dsqldate!=null&&!dsqldate.isEmpty())
			{
				MACHSHOP=dsqldate.get(0).get("ORGANIZATIONNO").toString();
				String eId=dsqldate.get(0).get("EID").toString();
			}
			res.setDatas(new ArrayList<DCP_OrderProSchedQueryRes.level1Elm>());
			
		  //查找门店的任务量
			String sqlorder="select SHOPID,count(*) procount from OC_ORDER where sdate='' and status!='11' and status!='12' and EID='"+req.geteId()+"' group by SHOPID ";
			List<Map<String, Object>> listsqlorder=this.doQueryData(sqlorder, null);
			
			//查找是否集中点
			String sqlOrderSet="select * from DCP_SHOP_ORDERSet where EID='"+req.geteId()+"' and OrdersetType='3'   ";
			List<Map<String, Object>> listsqlOrderSet=this.doQueryData(sqlOrderSet, null);
			
			//查找订单商品的库存量
			String sqlstock="select EID,ORGANIZATIONNO,sum(qty) qty from ( "
				+ "select a.EID,a.ORGANIZATIONNO,a.qty from OC_Order_Detail a left join DCP_stock_day b on a.EID=b.EID and a.pluno=b.pluno and A.SHOPID=b.organizationno    "
				+ " where a.EID='"+req.geteId()+"' and b.EID='"+req.geteId()+"'  and  a.orderno='"+orderno+"' "
				+ " union all  select a.EID,a.ORGANIZATIONNO,(case when stock_type='0' then wqty else -wqty end) as wqty  from OC_Order_Detail a left join DCP_stock_detail b on a.EID=b.EID and a.pluno=b.pluno and A.SHOPID=b.organizationno  "
				+ " where a.EID='"+req.geteId()+"' and b.EID='"+req.geteId()+"'  and  a.orderno='"+orderno+"' "
				+ " ) group by EID,ORGANIZATIONNO " ;
			List<Map<String, Object>> listsqlstock=this.doQueryData(sqlstock, null);
			
			for (Map<String, Object> map : dsqldate) 
			{
				DCP_OrderProSchedQueryRes.level1Elm lv1=res.new level1Elm();
				String shoptemp=map.get("ORGANIZATIONNO").toString();
				if(listsqlorder!=null&&!listsqlorder.isEmpty())
				{
					for (Map<String, Object> map2 : listsqlorder) 
					{	
						if(map2.get("SHOPID").toString().equals(shoptemp))
						{
							lv1.setTaskCount(map2.get("PROCOUNT").toString());
						}
		      }
				}
				if(listsqlOrderSet!=null&&!listsqlOrderSet.isEmpty())
				{
					for (Map<String, Object> map2 : listsqlOrderSet) 
					{	
						if(map2.get("SHOPID").toString().equals(shoptemp))
						{
							lv1.setIsPoints("Y");
						}
		      }
				}
				
				if(listsqlstock!=null&&!listsqlstock.isEmpty())
				{
					for (Map<String, Object> map2 : listsqlstock) 
					{	
						if(map2.get("ORGANIZATIONNO").toString().equals(shoptemp))
						{
							lv1.setStockCount(map2.get("QTY").toString());
						}
		      }
				}
				String DISTANCE=map.get("DISTANCE").toString();
				if(DISTANCE!=null&&!DISTANCE.isEmpty())
				{
					DISTANCE= PosPub.GetdoubleScale(Double.parseDouble(DISTANCE),2);
				}
				
				lv1.setDistance(DISTANCE);
				lv1.setoEId(map.get("EID").toString());
				lv1.setoShopId(map.get("ORGANIZATIONNO").toString());
				lv1.setO_shopName(map.get("ORG_NAME").toString());
				res.getDatas().add(lv1)	;
				lv1 = null;
	    }
			
			
			
		}
		else
		{
			res.setSuccess(false);
			res.setServiceDescription("查询订单失败！");
		}
		
	return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_OrderProSchedQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
