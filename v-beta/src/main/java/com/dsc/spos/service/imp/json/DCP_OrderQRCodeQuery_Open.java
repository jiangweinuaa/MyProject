package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderQRCodeQuery_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderQRCodeQuery_OpenReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderQRCodeQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderQRCodeQuery_Open extends SPosBasicService<DCP_OrderQRCodeQuery_OpenReq,DCP_OrderQRCodeQuery_OpenRes>
{

	private String orderQRCodeLogFileName = "OrderQRCodeQuery";
	@Override
	protected boolean isVerifyFail(DCP_OrderQRCodeQuery_OpenReq req) throws Exception
	{
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if (Check.Null(req.getRequest().getOrgType())) 
		{
			errCt++;
			errMsg.append("组织类型orgType不可为空值, ");
			isFail = true;
		} 	
		if (Check.Null(req.getRequest().getShopNo())) 
		{
			errCt++;
			errMsg.append("当前组织编码shopNo不可为空值, ");
			isFail = true;
		} 
		
		List<level1Elm> orderList = req.getRequest().getOrderList();
		if(orderList==null||orderList.isEmpty())
		{
			errCt++;
			errMsg.append("订单信息orderList不可为空值, ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for (level1Elm level1Elm : orderList)
		{
			if (Check.Null(level1Elm.getOrderNo()))
			{
				errCt++;
				errMsg.append("订单号orderNo不可为空值, ");
				isFail = true;
			} 
			if (Check.Null(level1Elm.getLoadDocType())) 
			{
				errCt++;
				errMsg.append("来源渠道loadDocType不可为空值, ");
				isFail = true;
			} 
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderQRCodeQuery_OpenReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderQRCodeQuery_OpenReq>(){};
	}

	@Override
	protected DCP_OrderQRCodeQuery_OpenRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderQRCodeQuery_OpenRes();
	}

	@Override
	protected DCP_OrderQRCodeQuery_OpenRes processJson(DCP_OrderQRCodeQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		DCP_OrderQRCodeQuery_OpenRes res = this.getResponse();
		res.setDatas(new ArrayList<DCP_OrderQRCodeQuery_OpenRes.level1Elm>());
		
		String eId = req.geteId();
		String shopNo = req.getRequest().getShopNo();
		String orgType = req.getRequest().getOrgType();
		String machineNo = req.getRequest().getMachineNo();
		String squadNo = req.getRequest().getSquadNo();
		String workNo = req.getRequest().getWorkNo();
		String opNo = req.getRequest().getOpNo();
		String opName = req.getRequest().getOpName();
		String channelId = "";
		if(req.getApiUser()!=null)
		{
			channelId = req.getApiUser().getChannelId();
		}
		String pageId = "OrderScan";
		//1：根据 eId 和 shopNo 查询 PAY_WEIXIN 和 PAY_WEIXIN_SHOP 表，  获取appId 。
		List<Map<String, Object>> appIdList = this.getAppId(eId, shopNo);
		if(appIdList==null||appIdList.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "请检查门店编码:"+shopNo+"的微信移动支付配置！");
		}
		String appid = appIdList.get(0).getOrDefault("APPID", "").toString();
		if(appid==null||appid.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "APPID不可为空,请检查表PAY_WEIXIN的ID为"+appIdList.get(0).getOrDefault("ID", "").toString()+"这条记录");
		}
		//2： 查询 参数DoMainName ， 得到 IP 地址
		
		String  ipAdress =   PosPub.getPARA_SMS(this.dao, eId, "", "DoMainName");
		
		if(ipAdress==null||ipAdress.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取IP地址或域名为空，请检查参数DoMainName配置！");
		}
		
		
		
		String payType = "#P1";
		List<Map<String, Object>> payMentList = this.getPayMentInfo(eId, payType);
		if(payMentList==null||payMentList.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "获取payType:"+payType+"对应的支付配置信息为空！");
		}
		
		String payCode = payMentList.get(0).getOrDefault("PAYCODE", "").toString();
		String payCodeErp = payMentList.get(0).getOrDefault("PAYCODEERP", "").toString();
		String payName = payMentList.get(0).getOrDefault("PAYNAME", "").toString();
		String funcNo = payMentList.get(0).getOrDefault("FUNCNO", "").toString();
	
		String url_start = "https://IP/member/qr/OrderScan.html?";
		url_start = url_start.replace("IP", ipAdress);
		
		Map<String, String> paraMap = new HashMap<String, String>();
		//paraMap.put("appid", appid);
		paraMap.put("pageId", pageId);
		paraMap.put("eId", eId);
		paraMap.put("shopNo", shopNo);
		paraMap.put("orgType", orgType);
		if(machineNo!=null&&machineNo.isEmpty()==false)
		{
			paraMap.put("machineNo", machineNo);
		}
		if(squadNo!=null&&squadNo.isEmpty()==false)
		{
			paraMap.put("squadNo", squadNo);
		}
		if(workNo!=null&&workNo.isEmpty()==false)
		{
			paraMap.put("workNo", workNo);
		}
		if(opNo!=null&&opNo.isEmpty()==false)
		{
			paraMap.put("opNo", opNo);
		}
		if(opName!=null&&opName.isEmpty()==false)
		{
			paraMap.put("opName", opName);
		}
		paraMap.put("payCode", payCode);
		paraMap.put("payCodeErp", payCodeErp);
		paraMap.put("payName", payName);
		paraMap.put("funcNo", funcNo);
		paraMap.put("payType", payType);
		String paraSetString = "";
		paraSetString="appid="+appid;
		String joinChar = "&";
		for(Map.Entry<String, String> entry : paraMap.entrySet())
		{
		    String mapKey = entry.getKey();
		    String mapValue = entry.getValue();
		    if(paraSetString.isEmpty())
		    {
		    	 paraSetString +=mapKey+"="+mapValue;
		    }
		    else
		    {
		    	 paraSetString +=joinChar+mapKey+"="+mapValue;
		    }
		    
		}
		
		url_start = url_start+paraSetString;
		HelpTools.writelog_fileName("【DCP_OrderQRCodeQuery_Open接口】查询返回url前缀="+url_start,orderQRCodeLogFileName);
		List<level1Elm> orderList = req.getRequest().getOrderList();
		for (level1Elm par : orderList)
		{
			String orderNo = par.getOrderNo();
			String loadDocType = par.getLoadDocType();
			String qrCode = url_start+joinChar+"orderNo="+orderNo;
			
			//3： 查询 渠道信息， 获取渠道 支付有效时长 expireTime 
			List<Map<String, Object>> expireTimeList = this.getExpireTime(eId, loadDocType, channelId);
			if(expireTimeList!=null&&expireTimeList.isEmpty()==false)
			{
				String expireTime = expireTimeList.get(0).getOrDefault("EXPIRETIME", "").toString();
				if(expireTime.isEmpty()==false)
				{
					qrCode = qrCode+joinChar+"expireTime="+expireTime;
				}						
				
			}		
			HelpTools.writelog_fileName("【DCP_OrderQRCodeQuery_Open接口】【转码前】查询返回url="+qrCode,orderQRCodeLogFileName);
			qrCode = java.net.URLEncoder.encode(qrCode,"utf-8");//后面返回解码
			DCP_OrderQRCodeQuery_OpenRes.level1Elm oneLv1 = res.new level1Elm();
			oneLv1.setOrderNo(orderNo);
			oneLv1.setQrCode(qrCode);
			HelpTools.writelog_fileName("【DCP_OrderQRCodeQuery_Open接口】【转码后】查询返回url="+qrCode,orderQRCodeLogFileName);
			
			res.getDatas().add(oneLv1);
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderQRCodeQuery_OpenReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<Map<String, Object>> getAppId (String eId,String shopId) throws Exception
	{
		String sql = " select B.* from PAY_WEIXIN_SHOP A inner join PAY_WEIXIN B on A.EID=B.EID and A.ID=B.ID"
				+ " where A.Eid='"+eId+"' and A.Shopid='"+shopId+"' ";
		HelpTools.writelog_fileName("【DCP_OrderQRCodeQuery_Open接口】查询appId对应的sql="+sql,orderQRCodeLogFileName);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		return getQData;
	}

	
	private List<Map<String, Object>> getExpireTime (String eId,String loadDocType,String channelId) throws Exception
	{
		String sql = " select * from dcp_ecommerce "
				+ " where Eid='"+eId+"' and LOADDOCTYPE='"+loadDocType+"' and CHANNELID='"+channelId+"' ";
		HelpTools.writelog_fileName("【DCP_OrderQRCodeQuery_Open接口】查询支付时长expireTime对应的sql="+sql,orderQRCodeLogFileName);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		return getQData;
	}
	
	private List<Map<String, Object>> getPayMentInfo(String eId,String payType) throws Exception
	{
		String sql = " SELECT A.PAYCODE,A.PAYTYPE,A.FUNCNO,B.PAYCODEERP,B.PAYNAME FROM dcp_payType a INNER JOIN dcp_payment B ON A.eID = B.eId AND a.Paycode = B.payCode "
				+ " where A.Eid='"+eId+"' and A.Paytype='"+payType+"' AND A.status = 100 AND B.status = '100' ";
		HelpTools.writelog_fileName("【DCP_OrderQRCodeQuery_Open接口】查询支付信息payMent对应的sql="+sql,orderQRCodeLogFileName);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		return getQData;
	}
}
