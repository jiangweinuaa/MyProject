package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xml.resolver.apps.resolver;

import com.dsc.spos.json.cust.req.DCP_WarningMsgTemplateQueryReq;
import com.dsc.spos.json.cust.req.DCP_WarningMsgTemplateQueryReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_WarningMsgTemplateQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningMsgTemplateQuery extends SPosBasicService<DCP_WarningMsgTemplateQueryReq, DCP_WarningMsgTemplateQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_WarningMsgTemplateQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_WarningMsgTemplateQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningMsgTemplateQueryReq>(){};
	}

	@Override
	protected DCP_WarningMsgTemplateQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningMsgTemplateQueryRes();
	}

	@Override
	protected DCP_WarningMsgTemplateQueryRes processJson(DCP_WarningMsgTemplateQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			DCP_WarningMsgTemplateQueryRes res = this.getResponse();
			res.setDatas(res.new level1Elm());
			String sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);		
			
			if (getQData != null && getQData.isEmpty() == false) 
			{							
				Map<String, Object> oneData = getQData.get(0);
				DCP_WarningMsgTemplateQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String msgMiddle = oneData.get("MSGMIDDLE").toString();
				if(msgMiddle!=null)
				{
					msgMiddle = msgMiddle.replace(" ", "\n");
				}
				
				oneLv1.setTemplateType(oneData.get("TEMPLATETYPE").toString());
				oneLv1.setTemplateTypeTitle(oneData.get("TEMPLATETITLE").toString());
				oneLv1.setMsgBegin(oneData.get("MSGBEGIN").toString());
				oneLv1.setMsgMiddle(msgMiddle);
				oneLv1.setMsgEnd(oneData.get("MSGEND").toString());
				oneLv1.setLinkUrl(oneData.get("LINKURL").toString());
				oneLv1.setBatchNo(oneData.get("BATCHNO").toString());
				
				
			  res.setDatas(oneLv1);			
				
			}
			
			return res;					
	
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());			
		}
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_WarningMsgTemplateQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		

		String templateType = requestModel.getTemplateType();
		if(templateType!=null&&templateType.isEmpty()==false)
		{
			templateType = templateType.toLowerCase();
		}

		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");
	
		sqlbuf.append("select * from  dcp_warning_msgtemplate");			
		sqlbuf.append(" where eid='"+eId+"' and lower（templateType)='"+templateType+"'");
		//sqlbuf.append("");
		sql = sqlbuf.toString();
		return sql;
	}

}
