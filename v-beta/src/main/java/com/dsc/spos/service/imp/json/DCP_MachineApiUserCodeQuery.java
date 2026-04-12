package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MachineApiUserCodeQueryReq;
import com.dsc.spos.json.cust.res.DCP_MachineApiUserCodeQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_MachineApiUserCodeQuery extends SPosBasicService<DCP_MachineApiUserCodeQueryReq,DCP_MachineApiUserCodeQueryRes>
{

	@Override
	protected boolean isVerifyFail(DCP_MachineApiUserCodeQueryReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().getShopId())) 
		{

			errMsg.append("门店编号不可为空值, ");
			isFail = true;
		}			

		if (Check.Null(req.getRequest().getAppType())) 
		{

			errMsg.append("应用类型不可为空值, ");
			isFail = true;
		}			

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_MachineApiUserCodeQueryReq> getRequestType()
	{
		return new TypeToken<DCP_MachineApiUserCodeQueryReq>(){};
	}

	@Override
	protected DCP_MachineApiUserCodeQueryRes getResponseType()
	{
		return new DCP_MachineApiUserCodeQueryRes();
	}

	@Override
	protected DCP_MachineApiUserCodeQueryRes processJson(DCP_MachineApiUserCodeQueryReq req) throws Exception
	{
		DCP_MachineApiUserCodeQueryRes res=this.getResponse();

		//单头查询
		String sql=this.getQuerySql(req);	
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_MachineApiUserCodeQueryRes.level1Elm>());
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			for (Map<String, Object> oneData : getQDataDetail) 
			{
				DCP_MachineApiUserCodeQueryRes.level1Elm lv1=res.new level1Elm();
				lv1.setAppName(oneData.get("APPNAME").toString());
				lv1.setAppType(oneData.get("APPTYPE").toString());
				lv1.setApiUserCode(oneData.get("APIUSERCODE").toString());
				lv1.setChannelName(oneData.get("CHANNELNAME").toString());
				lv1.setCompanyName(oneData.get("ORG_NAME").toString());

				res.getDatas().add(lv1);
				lv1=null;				
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
	protected String getQuerySql(DCP_MachineApiUserCodeQueryReq req) throws Exception
	{
		String sql=null;		

		String shopId= req.getRequest().getShopId();
		String appType=req.getRequest().getAppType();


		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select a.*,b.belfirm,f.org_name,d.appname,c.channelid,e.channelname  from PLATFORM_MACHINE a "
				+ "inner join DCP_ORG b on a.eid=b.eid and a.shopid=b.organizationno  "
				+ "left join CRM_APIUSER c on a.eid=c.eid and b.belfirm=c.companyid and a.apptype=c.apptype "
				+ "left join PLATFORM_APP d on a.apptype=d.appno "
				+ "left join CRM_CHANNEL e on a.eid=e.eid and c.channelid=e.channelid and a.apptype=e.APPNO "
				+ "left join DCP_ORG_LANG f on b.eid=f.eid and b.belfirm=f.organizationno and f.lang_type='"+req.getLangType()+"' "
				+ "where a.eid='"+req.geteId()+"' ");
		
		if (shopId!=null && shopId.length()>0)
		{
			sqlbuf.append("and a.shopid='"+shopId+"' ");
		}

		if (appType!=null && appType.length()>0)
		{
			sqlbuf.append("and a.APPTYPE='"+appType+"' ");
		}	

		sql = sqlbuf.toString();
		return sql;
	}

}
