package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_AttributionValueUpdateReq;
import com.dsc.spos.json.cust.req.DCP_AttributionValueUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AttributionValueUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_AttributionValueUpdate  extends SPosAdvanceService<DCP_AttributionValueUpdateReq,DCP_AttributionValueUpdateRes> {

	@Override
	protected void processDUID(DCP_AttributionValueUpdateReq req, DCP_AttributionValueUpdateRes res) throws Exception {

		String sql = null;
		String eId = req.geteId();
		String attrValueId = req.getRequest().getAttrValueId();
		String status = req.getRequest().getStatus();
		String sortId = req.getRequest().getSortId();
		int sortId_i = 0;
		try 
		{
			sortId_i = Integer.parseInt(sortId);
			
		} catch (Exception e) {
		// TODO: handle exception
	
		}
		String attrId = req.getRequest().getAttrId();
		
		
		
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		DelBean db2 = new DelBean("DCP_ATTRIBUTION_VALUE_LANG");
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db2.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
		db2.addCondition("ATTRVALUEID", new DataValue(attrValueId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));
		
		
		
		List<level1Elm> getLangDatas = req.getRequest().getAttrValueName_lang();
		
		
		
		if(getLangDatas != null && !getLangDatas.isEmpty()){
			for(level1Elm oneLv1: getLangDatas){
								
				String[] columnsName = {
			  		"ATTRID","ATTRVALUEID","LANG_TYPE","ATTRVALUENAME","EID","LASTMODITIME"
				};
				
				String langType = oneLv1.getLangType();
				String attrValueName = oneLv1.getName();
			
				DataValue[] insValueDetail = new DataValue[] 
						{		
							new DataValue(attrId, Types.VARCHAR),
							new DataValue(attrValueId, Types.VARCHAR),
							new DataValue(langType, Types.VARCHAR),
							new DataValue(attrValueName, Types.VARCHAR),									
							new DataValue(eId, Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE)
						};
				InsBean ib2 = new InsBean("DCP_ATTRIBUTION_VALUE_LANG", columnsName);
				ib2.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ib2));				
			}
		}
		
		
		
		UptBean ub1 = null;	
		ub1 = new UptBean("DCP_ATTRIBUTION_VALUE");
		//add Value
		
		if (sortId!=null&&sortId.length()>0) 
		{
			ub1.addUpdateValue("SORTID", new DataValue(sortId_i, Types.VARCHAR));	
		}
		
		ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		//ub1.addUpdateValue("ATTRID", new DataValue(attrId, Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		//condition
		ub1.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
		ub1.addCondition("ATTRVALUEID", new DataValue(attrValueId, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(ub1));
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
	
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_AttributionValueUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AttributionValueUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AttributionValueUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AttributionValueUpdateReq req) throws Exception {

		// TODO Auto-generated method stub

		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");

	    if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
	    String attrValueId = req.getRequest().getAttrValueId();
	    String attrId = req.getRequest().getAttrId();
	        
	    if(Check.Null(attrValueId)){
		   	errMsg.append("规格编码不能为空值 ");
		   	isFail = true;
		
	    }
	    if(Check.Null(attrId)){
		   	errMsg.append("属性编码不能为空值 ");
		   	isFail = true;
		
	    }
	    List<level1Elm> datas = req.getRequest().getAttrValueName_lang();
	    if(datas == null ||datas.isEmpty())
	    {
	    	errMsg.append("多语言资料不能为空 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    	
	    }
	    
	   	for (level1Elm oneData : datas) 
			{
	    		String langType = oneData.getLangType();
	    		
	    		if(Check.Null(langType)){
	    		   	errMsg.append("多语言类型不能为空值 ");
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
	protected TypeToken<DCP_AttributionValueUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttributionValueUpdateReq>(){};
	}

	@Override
	protected DCP_AttributionValueUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttributionValueUpdateRes() ;
	}

	
}
