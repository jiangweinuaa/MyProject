package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.json.cust.req.DCP_OrderShopStatusQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderShopStatusQueryRes;
import com.dsc.spos.scheduler.job.StaticInfo;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.WMELMShopService;
import com.dsc.spos.waimai.WMJBPShopService;
import com.dsc.spos.waimai.WMMTShopService;
import com.dsc.spos.waimai.jddj.HelpJDDJHttpUtil;
import com.dsc.spos.waimai.jddj.OStoreInfo;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderShopStatusQuery extends SPosBasicService<DCP_OrderShopStatusQueryReq,DCP_OrderShopStatusQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderShopStatusQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_OrderShopStatusQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_OrderShopStatusQueryReq>(){};
	}

	@Override
	protected DCP_OrderShopStatusQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_OrderShopStatusQueryRes();
	}

	@Override
	protected DCP_OrderShopStatusQueryRes processJson(DCP_OrderShopStatusQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		//查询一下外卖平台的门店状态
		DCP_OrderShopStatusQueryRes res=new DCP_OrderShopStatusQueryRes();
		
		StringBuilder errorMessage=new StringBuilder();
		//如果官网的,先查询是否有官网的门店，如果没有手动加一条进去
		{
			//插入官网的门店
			//如果官网的,先查询是否有官网的门店，如果没有手动加一条进去
			String shopgw="select * from OC_ECOMMERCE where  EID='"+req.getoEId()+"' and ECPLATFORMNO='6' ";
			List<Map<String, Object>> listshopgw=this.doQueryData(shopgw, null);
			if(listshopgw!=null&&!listshopgw.isEmpty())
			{
				try
				{
					//调用官网的地址获取开关店状态
					//从味多美官网上查询信息，然后保存到数据库,直接从这里取
					String method="";
					method="salesDeliver";
					JSONObject reqJsonObject=new JSONObject();
					reqJsonObject.put("cmd", "wdmwaimai_get_md_status");
					reqJsonObject.put("channel", "mall");
					reqJsonObject.put("erp_code", req.getoShopId());
					
					String resbody=HttpSend.SendWuXiang(method, reqJsonObject.toString(), "http://www.wdmcake.cn/api/erp-wdmwaimai_get_md_status.html");
					JSONObject resJsonObject=new JSONObject(resbody);
					String code= resJsonObject.getString("code");
					String message= resJsonObject.getString("msg");
					String memoStr = message;
					if(code.equals("0"))
					{
						String is_online= resJsonObject.getJSONArray("md").getJSONObject(0).getInt("is_online")+"";
						if(is_online.equals("1"))
						{
							res.setGwStatus("1");
						}
						else
						{
							res.setGwStatus("2");
						}
					}
					else
					{
						res.setGwStatus("2");
					}
				}
				catch(Exception ex)
				{
					//京东报错
					res.setGwStatus("2");
				}
				
			}
		}
		
		String shopsql="select * from OC_MAPPINGSHOP where SHOPID='"+req.getoShopId()+"' and EID='"+req.getoEId()+"' ";
		List<Map<String, Object>> listshop=this.doQueryData(shopsql, null);
		if(listshop!=null&&!listshop.isEmpty())
		{
			for (Map<String, Object> map : listshop) 
			{
				String ORDERSHOPNO=map.get("ORDERSHOPNO").toString(); 
				String loadDocType = map.get("LOAD_DOCTYPE").toString();
				String ISSALETYPE = map.get("ISSALETYPE").toString();
				
				if(map.get("LOAD_DOCTYPE").equals("1"))
				{
					
					Map<String, Object> mapAppkey = PosPub.getWaimaiAppConfigByShopNO_New(this.dao, req.getoEId(), req.getoShopId(), loadDocType,"");
					Boolean isGoNewFunction = false;//是否走新的接口
					String elmAPPKey = "";
					String elmAPPSecret = "";
					String elmAPPName = "";			
					boolean elmIsSandbox = false;
					if (mapAppkey != null)
					{
						elmAPPKey = mapAppkey.get("APPKEY").toString();
						elmAPPSecret = mapAppkey.get("APPSECRET").toString();
						elmAPPName = mapAppkey.get("APPNAME").toString();
						String	elmIsTest = mapAppkey.get("ISTEST").toString();					
						if (elmIsTest != null && elmIsTest.equals("Y"))
						{
							elmIsSandbox = true;
						}
						isGoNewFunction = true;
					}
					int status=2;
					if(isGoNewFunction)
					{
						status = WMELMShopService.getShopStatus(elmIsSandbox,elmAPPKey,elmAPPSecret,elmAPPName,Long.parseLong(ORDERSHOPNO),errorMessage);
					}
					else
					{
						status = WMELMShopService.getShopStatus(Long.parseLong(ORDERSHOPNO),errorMessage);
					}
					
					
					if(status==1)
					{
						res.setElmStatus("1");
					}
					else
					{
						res.setElmStatus("2");
					}
				}
				if(map.get("LOAD_DOCTYPE").equals("2"))
				{
					int status= 0 ;
					if (StaticInfo.waimaiMTIsJBP != null && StaticInfo.waimaiMTIsJBP.equals("Y"))//聚宝盆
					{
						status= WMJBPShopService.getShopStatus(req.getoEId(),req.getoShopId(),errorMessage);						
					}
					else
					{
						status = WMMTShopService.getShopStatus(ORDERSHOPNO, errorMessage);					
					}
					
					if(status==1)
					{
						res.setJbpStatus("1");
					}
					else
					{
						res.setJbpStatus("2");
					}
				}
				//京东好之后可以直接启用
				if(map.get("LOAD_DOCTYPE").equals("3"))
				{
					try
					{
						//京东门店状态
						OStoreInfo osi= HelpJDDJHttpUtil.getStoreInfoByStationNo(ORDERSHOPNO,errorMessage);
						if(osi!=null&&osi.getCloseStatus()==0)
						{
							res.setJdStatus("1");
						}
						else
						{
							res.setJdStatus("2");
						}
					}
					catch(Exception ex)
					{
						//京东报错
					}
				}
			
		  }
		}
		else
		{
			res.setSuccess(false);
			res.setServiceDescription("当前门店不存平台映射关系");
			return res;
		}
		
		
	return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_OrderShopStatusQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
