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
import com.dsc.spos.json.cust.req.DCP_AttributionValueCreateReq;
import com.dsc.spos.json.cust.req.DCP_AttributionValueCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AttributionValueCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_AttributionValueCreate extends SPosAdvanceService<DCP_AttributionValueCreateReq,DCP_AttributionValueCreateRes> {

	@Override
	protected void processDUID(DCP_AttributionValueCreateReq req, DCP_AttributionValueCreateRes res) throws Exception {
	// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		
		String sql = null;
		String eId = req.geteId();
		String attrValueId = req.getRequest().getAttrValueId();
		String status = req.getRequest().getStatus();
		String sortId = req.getRequest().getSortId();
		String attrId = req.getRequest().getAttrId();
		int sortId_i = 0;
		try 
		{
			sortId_i = Integer.parseInt(sortId);
			
		} catch (Exception e) {
		// TODO: handle exception
	
		}
		
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		List<level1Elm> getLangDatas = req.getRequest().getAttrValueName_lang();
		
		
		sql = this.isRepeat(attrId,attrValueId, eId);
		List<Map<String , Object>> datas = this.doQueryData(sql, null);
		if(datas.isEmpty()){
			if(getLangDatas != null && !getLangDatas.isEmpty()){
				for(level1Elm oneLv1: getLangDatas){
									
					String[] columnsName = {
					  		"ATTRID","ATTRVALUEID","LANG_TYPE","ATTRVALUENAME","EID","LASTMODITIME"
						};
					
					String langType = oneLv1.getLangType();
					String attrValueName = oneLv1.getName();
								
					sql = this.isRepeatLang(attrId,attrValueId, eId, langType);
					List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
					
					if(detailDatas.isEmpty()){
						
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
				
					}else{
						res.setSuccess(false);
						res.setServiceStatus("200");
						res.setServiceDescription("服务执行失败: 规格编码为  "+attrValueId+" , 多语言类型为 "+langType+" 的信息已存在");	
						return;
					}
				}
			}
			
			
			

			String[] columns1 = { "ATTRID","ATTRVALUEID","SORTID","STATUS","EID","CREATETIME" };
			DataValue[] insValue1 = null;
			
			insValue1 = new DataValue[]{
					new DataValue(attrId, Types.VARCHAR),
					new DataValue(attrValueId, Types.VARCHAR),
					new DataValue(sortId_i, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(eId, Types.VARCHAR),
					
					new DataValue(lastmoditime, Types.DATE)
				};
			
			InsBean ib1 = new InsBean("DCP_ATTRIBUTION_VALUE", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); 
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			
		}else{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败: 规格编码为  "+attrValueId+"  的信息已存在");	
			return;
		}
		
	
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_AttributionValueCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AttributionValueCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AttributionValueCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AttributionValueCreateReq req) throws Exception {

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
	protected TypeToken<DCP_AttributionValueCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttributionValueCreateReq>(){};
	}

	@Override
	protected DCP_AttributionValueCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttributionValueCreateRes();
	}

	private String isRepeat(String attrId,String attrValueId, String eId){
		String sql = null;
		sql = "SELECT * FROM DCP_ATTRIBUTION_VALUE WHERE "		
				+ " ATTRVALUEID = '"+attrValueId+"' "
				+ " and ATTRID = '"+attrId+"' "
				+ " and EID = '"+eId+"'" ;
		return sql;
	}
	
	private String isRepeatLang(String attrId,String attrValueId, String eId, String langType ){
		String sql = null;
		sql = "SELECT * FROM DCP_ATTRIBUTION_VALUE_LANG WHERE "			
				+ " ATTRVALUEID = '"+attrValueId+"' "
				+ " and ATTRID = '"+attrId+"' "
				+ " and EID = '"+eId+"' "
				+ " and lang_Type = '"+langType+"'" ;
		return sql;
	}
	

	
}
