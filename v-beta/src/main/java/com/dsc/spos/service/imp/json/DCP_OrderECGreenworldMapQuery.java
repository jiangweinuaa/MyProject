package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECGETSHOPQueryReq;
import com.dsc.spos.json.cust.req.DCP_OrderECGreenworldMapQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECGETSHOPQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderECGreenworldMapQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.logistics.GreenWorld;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderECGreenworldMapQuery extends SPosBasicService<DCP_OrderECGreenworldMapQueryReq,DCP_OrderECGreenworldMapQueryRes>
{
	@Override
	protected boolean isVerifyFail(DCP_OrderECGreenworldMapQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		String shoptype = req.getShopType();
		String extraData = req.getExtraData();

		if(Check.Null(shoptype))
		{
			errCt++;
			errMsg.append("超商门店类型不可为空值, ");
			isFail = true;
		}
		
		if(Check.Null(extraData))
		{
			errCt++;
			errMsg.append("约定资料不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECGreenworldMapQueryReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECGreenworldMapQueryReq>(){};
	}

	@Override
	protected DCP_OrderECGreenworldMapQueryRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_OrderECGreenworldMapQueryRes();
	}

	@Override
	protected DCP_OrderECGreenworldMapQueryRes processJson(DCP_OrderECGreenworldMapQueryReq req) throws Exception 
	{
		DCP_OrderECGreenworldMapQueryRes res = null;
		res = this.getResponse();

		//1：全家  2:萊爾富 3：7-ELEVEN 
		String shoptype = req.getShopType();
		String extraData = req.getExtraData();

		String canCollection = req.getCanCollection()==null?"N":req.getCanCollection();
		String serverReplyUrl = req.getServerReplyUrl()==null?"":req.getServerReplyUrl();
		String device = req.getDevice()==null?"0":req.getDevice();

		String sql = "";
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getLogisticsData=this.doQueryData(sql, null);
		if(getLogisticsData!=null&&!getLogisticsData.isEmpty())
		{			
			String apiUrl=getLogisticsData.get(0).get("API_URL").toString();//
			String cvs_Mode=getLogisticsData.get(0).get("CVS_MODE").toString();
			String greenworld_MerchantId=getLogisticsData.get(0).get("GREENWORLD_MERCHANTID").toString();
			String greenworld_HashKey=getLogisticsData.get(0).get("GREENWORLD_HASHKEY").toString();
			String greenworld_HashIv=getLogisticsData.get(0).get("GREENWORLD_HASHIV").toString();										
			String greenworld_Mapwebsite=getLogisticsData.get(0).get("GREENWORLD_MAPWEBSITE").toString();
		
			String greenworld_MerchantID_B2C=getLogisticsData.get(0).get("GREENWORLD_MERCHANTID_BTOC").toString();
			String greenworld_Hashkey_B2C=getLogisticsData.get(0).get("GREENWORLD_HASHKEY_BTOC").toString();
			String greenworld_Hashiv_B2C=getLogisticsData.get(0).get("GREENWORLD_HASHIV_BTOC").toString();

			GreenWorld greenworld=new GreenWorld();

			String htmlcode="";

			//---C2C--- FAMIC2C：全家店到店 UNIMARTC2C：7-ELEVEN 超商交貨便 HILIFEC2C:萊爾富店到店 
			//---B2C--- FAMI：全家 UNIMART：7-ELEVEN 超商 HILIFE：萊爾富 
			String logisticsSubType="";
			
			
			//后台绿界物流服务地址
			if (serverReplyUrl.equals("")) 
			{
				serverReplyUrl=greenworld_Mapwebsite;
			}			

			if (cvs_Mode.equals("1")) //1：C2C店到店模式 2：B2C大物流中心模式
			{
				if (shoptype.equals("1")) 
				{
					logisticsSubType="FAMIC2C";
				}
				else if (shoptype.equals("2")) 
				{
					logisticsSubType="HILIFEC2C";
				}
				else if (shoptype.equals("3")) 
				{
					logisticsSubType="UNIMARTC2C";
				}
				
				htmlcode=greenworld.ExpressMapInfo(apiUrl, greenworld_MerchantId, greenworld_HashKey, greenworld_HashIv, logisticsSubType, canCollection, serverReplyUrl, extraData, device);
			}
			else 
			{
				if (shoptype.equals("1")) 
				{
					logisticsSubType="FAMI";
				}
				else if (shoptype.equals("2")) 
				{
					logisticsSubType="HILIFE";
				}
				else if (shoptype.equals("3")) 
				{
					logisticsSubType="UNIMART";
				}
				
				htmlcode=greenworld.ExpressMapInfo(apiUrl, greenworld_MerchantID_B2C, greenworld_Hashkey_B2C, greenworld_Hashiv_B2C, logisticsSubType, canCollection, serverReplyUrl, extraData, device);
			}

			if (htmlcode.equals("")) 
			{
				res.setHtmlcode(htmlcode);
				res.setSuccess(false);
				res.setServiceDescription("服务执行失败!");
			}
			else 
			{
				res.setHtmlcode(htmlcode);
				res.setSuccess(true);
				res.setServiceDescription("服务执行成功!");
			}
			
			greenworld=null;
		}
		else 
		{
			res.setHtmlcode("");
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败!");
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderECGreenworldMapQueryReq req) throws Exception 
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();

		sqlbuf.append(" select * from OC_logistics where lgplatformno='greenworld' and status='100' "
				+ "  and EID = '"+eId+"'");


		sql = sqlbuf.toString();
		return sql;
	}



}
