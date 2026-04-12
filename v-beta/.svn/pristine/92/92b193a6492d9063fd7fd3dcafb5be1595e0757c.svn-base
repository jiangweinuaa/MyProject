package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_HeadOrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_HeadOrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_HeadOrderQuery extends SPosBasicService<DCP_HeadOrderQueryReq,DCP_HeadOrderQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_HeadOrderQueryReq req) throws Exception
	{
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		DCP_HeadOrderQueryReq.levelRequest request=req.getRequest();
		if(request==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}else{
			String headOrderNo=request.getHeadOrderNo();
			if(headOrderNo==null||headOrderNo.length()<1){
				isFail = true;
				errMsg.append("主单号不能为空 ");
			}
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_HeadOrderQueryReq> getRequestType()
	{
		return new TypeToken<DCP_HeadOrderQueryReq>(){};
	}

	@Override
	protected DCP_HeadOrderQueryRes getResponseType()
	{
		return new DCP_HeadOrderQueryRes();
	}

	@Override
	protected DCP_HeadOrderQueryRes processJson(DCP_HeadOrderQueryReq req) throws Exception
	{
		DCP_HeadOrderQueryRes res=this.getResponse();

		String sqlOrder=getQuerySql(req);		
		List<Map<String, Object>> getOrder = this.doQueryData(sqlOrder, null);
		
		if(getOrder==null||getOrder.size()<1){
			getOrder = this.doQueryData("SELECT A.*,A.BILLNO AS ORDERNO FROM CRM_ORDER A WHERE A.EID='"+req.geteId()+"' "
				+ " AND A.HEADORDERNO='"+req.getRequest().getHeadOrderNo()+"' AND A.HEADORDERNO <> A.BILLNO", null);
		}

		res.setDatas(res.new level1Elm());
		res.getDatas().setOrderList(new ArrayList<DCP_HeadOrderQueryRes.Order>());

		if (getOrder!=null && getOrder.isEmpty()==false)
		{
			for (Map<String, Object> map : getOrder)
			{
				DCP_HeadOrderQueryRes.Order order=res.new Order();
				String orderNo= map.get("ORDERNO")==null?"":map.get("ORDERNO").toString();
				String headOrderNo= map.get("HEADORDERNO")==null?"":map.get("HEADORDERNO").toString();
				order.seteId(req.geteId());
				order.setHeadOrderNo(headOrderNo);
				order.setOrderNo(orderNo);
				res.getDatas().getOrderList().add(order);
			}			
		}


		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_HeadOrderQueryReq req) throws Exception
	{
		String sql = null;
		StringBuffer sqlbuf=new StringBuffer("");
		String eId=req.geteId();
		sqlbuf.append(" SELECT A.* FROM DCP_ORDER A WHERE A.EID='"+eId+"' "
				+ " AND A.HEADORDERNO='"+req.getRequest().getHeadOrderNo()+"' AND A.BILLTYPE='1' ");
		sql = sqlbuf.toString();
		return sql;		
	}




}
