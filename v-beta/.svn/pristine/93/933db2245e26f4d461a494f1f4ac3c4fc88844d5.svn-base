package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_AppQueryReq;
import com.dsc.spos.json.cust.res.DCP_AppQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_AppQuery extends SPosBasicService<DCP_AppQueryReq,DCP_AppQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_AppQueryReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_AppQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_AppQueryReq>() {};
	}

	@Override
	protected DCP_AppQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_AppQueryRes();
	}

	@Override
	protected DCP_AppQueryRes processJson(DCP_AppQueryReq req) throws Exception 
	{
		DCP_AppQueryRes res=this.getResponse();
		
		String sql = this.getQuerySql(req);				
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_AppQueryRes.level1Elm>());

		if(getQData!=null && getQData.isEmpty()==false)
		{		
			for (Map<String, Object> map : getQData) 
			{				
				DCP_AppQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setAppName(map.get("APPNAME").toString());
				lv1.setAppNo(map.get("APPNO").toString());
				lv1.setIsOnline(map.get("ISONLINE").toString());
				lv1.setIsThird(map.get("ISTHIRD").toString());
				lv1.setDockFunction(map.get("DOCKFUNCTION").toString());

				res.getDatas().add(lv1);
				lv1=null;
			}

		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");	
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_AppQueryReq req) throws Exception 
	{
		String eId = req.geteId();
		String sql = "";
		
		String isOnline=req.getRequest().getIsOnline();
		String isThird=req.getRequest().getIsThird();
		String status=req.getRequest().getStatus();
		
		String scurdate=new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		
		StringBuffer sqlBuff = new StringBuffer("select * from ("
				+ " select a.APPNO,a.APPNAME,a.isthird,a.isonline,a.DOCKFUNCTION from PLATFORM_APP a "
//				+ " inner join dcp_regedistmodular b on a.rfuncno=b.rfuncno "
//				+ " inner join ("
//				+ " SELECT DISTINCT productType  FROM Platform_CregisterDetail WHERE bdate <= '"+scurdate+"' AND eDate >= '"+scurdate+"' "
//				+ " ) c on A.rfuncno = c.productType "
//				+ " where 1=1 "); 
        		+ " left join dcp_regedistmodular b on a.rfuncno=b.rfuncno "
				+ " left join ("
				+ " SELECT DISTINCT productType  FROM Platform_CregisterDetail WHERE bdate <= '"+scurdate+"' AND eDate >= '"+scurdate+"' "
				+ " ) c on a.rfuncno = c.productType ");
		sqlBuff.append(" WHERE ( a.RFUNCNO in ('46','47')  "
				+ " or   (b.rfuncno is not null and c.productType is not null  ))");


		
		if (isOnline != null && isOnline.length()>0)
		{
			sqlBuff.append(" and a.isonline='" + isOnline + "' ");
		}
		if(isThird!=null && isThird.length()>0)
		{
			sqlBuff.append(" and a.isthird='" + isThird + "' ");
			
		}
		if(status!=null && status.length()>0)
		{
			sqlBuff.append(" and a.status='" + status + "' ");
			
		}
		sqlBuff.append(" order by a.SORTID ");

		sqlBuff.append(" )" );
		sql = sqlBuff.toString();
		return sql;
	}




}
