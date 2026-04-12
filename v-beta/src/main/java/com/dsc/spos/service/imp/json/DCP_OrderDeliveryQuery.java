package com.dsc.spos.service.imp.json;

import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderDeliveryQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderDeliveryQueryRes;
import com.dsc.spos.json.cust.res.DCP_OrderDeliveryQueryRes.responseDatas;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.HelpTools;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderDeliveryQuery extends SPosBasicService<DCP_OrderDeliveryQueryReq,DCP_OrderDeliveryQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderDeliveryQueryReq req) throws Exception
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

		if (Check.Null(req.getRequest().geteId())) 
		{
			errCt++;
			errMsg.append("企业编号eId不可为空值, ");
			isFail = true;
		} 
		
		/*if (Check.Null(req.getRequest().getShopNo())) 
		{
			errCt++;
			errMsg.append("当前门店shopNo不可为空值, ");
			isFail = true;
		} */
		
		if (Check.Null(req.getRequest().getOrderNo())) 
		{
			errCt++;
			errMsg.append("订单号orderNo不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderDeliveryQueryReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderDeliveryQueryReq> (){};
	}

	@Override
	protected DCP_OrderDeliveryQueryRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderDeliveryQueryRes();
	}

	@Override
	protected DCP_OrderDeliveryQueryRes processJson(DCP_OrderDeliveryQueryReq req) throws Exception
	{
		
		DCP_OrderDeliveryQueryRes res = this.getResponse();
		// TODO Auto-generated method stub
		String eId_req = req.getRequest().geteId();
		String orderNo = req.getRequest().getOrderNo();
		responseDatas datas = res.new responseDatas();
		
		String sql = "select * from dcp_order where eid='"+eId_req+"' and orderNo='"+orderNo+"'";
		List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
		if(getDatas==null||getDatas.isEmpty())
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422, "该订单不存在！");
		}
		String deliveryType = getDatas.get(0).getOrDefault("DELIVERYTYPE", "").toString();
		String deliveryNo = getDatas.get(0).getOrDefault("DELIVERYNO", "").toString();
		String deliveryStatus = getDatas.get(0).getOrDefault("DELIVERYSTATUS", "").toString();
		String shortAddress = getDatas.get(0).getOrDefault("SHORTADDRESS", "").toString();
		
		String deliveryTypeName = "";
		if(!deliveryType.isEmpty())
		{
			deliveryTypeName = HelpTools.getDeliveryTypeName(deliveryType);
		}
				
		datas.setDeliveryNo(deliveryNo);
		datas.setDeliveryType(deliveryType);
		datas.setDeliveryTypeName(deliveryTypeName);
		datas.setShortAddress(shortAddress);
		res.setDatas(datas);
		
		return res;
	
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderDeliveryQueryReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}
	}
