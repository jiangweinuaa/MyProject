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
import com.dsc.spos.json.cust.req.DCP_AttributionUpdateReq;
import com.dsc.spos.json.cust.req.DCP_AttributionUpdateReq.attrGroup;
import com.dsc.spos.json.cust.req.DCP_AttributionUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AttributionUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_AttributionUpdate  extends SPosAdvanceService<DCP_AttributionUpdateReq,DCP_AttributionUpdateRes> {

	@Override
	protected void processDUID(DCP_AttributionUpdateReq req, DCP_AttributionUpdateRes res) throws Exception {

		String sql = null;
		String eId = req.geteId();
		String attrId = req.getRequest().getAttrId();
		String status = req.getRequest().getStatus();
		String sortId = req.getRequest().getSortId();
		int sortId_i = 0;
		try 
		{
			sortId_i = Integer.parseInt(sortId);
			
		} catch (Exception e) {
		// TODO: handle exception
	
		}
		String productParam = req.getRequest().getProductParam();
		String multiSpec = req.getRequest().getMultiSpec();
		
		
		String memo = req.getRequest().getMemo();
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		DelBean db2 = new DelBean("DCP_ATTRIBUTION_LANG");
		db2.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));
		
		DelBean db3 = new DelBean("DCP_ATTRGROUP_DETAIL");
		db3.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
		db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db3));
		
		List<level1Elm> getLangDatas = req.getRequest().getAttrName_lang();
		List<attrGroup> attrGroupList = req.getRequest().getAttrGroup();
		
		
		if(getLangDatas != null && !getLangDatas.isEmpty()){
			for(level1Elm oneLv1: getLangDatas){
								
				String[] columnsName = {
				  		"ATTRID","LANG_TYPE","ATTRNAME","EID","LASTMODITIME"
					};
				
				String langType = oneLv1.getLangType();
				String attrName = oneLv1.getName();
			
				DataValue[] insValueDetail = new DataValue[] 
						{
							new DataValue(attrId, Types.VARCHAR),
							new DataValue(langType, Types.VARCHAR),
							new DataValue(attrName, Types.VARCHAR),									
							new DataValue(eId, Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE)
						};
				InsBean ib2 = new InsBean("DCP_ATTRIBUTION_LANG", columnsName);
				ib2.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ib2));				
			}
		}
		
		if(attrGroupList!=null&&!attrGroupList.isEmpty())
		{

			for(attrGroup oneLv1: attrGroupList){
								
				String[] columnsName = {
				  		"ATTRID","ATTRGROUPID","SORTID","EID","LASTMODITIME"
					};
				
				String attrGroupId = oneLv1.getAttrGroupId();
				String attrGroup_sortId = oneLv1.getSortId();
				int attrGroup_sortId_i = 0;
				try 
				{
					attrGroup_sortId_i = Integer.parseInt(attrGroup_sortId);
					
				} catch (Exception e) {
				// TODO: handle exception
			
				}
			
				DataValue[] insValueDetail = new DataValue[] 
						{
							new DataValue(attrId, Types.VARCHAR),
							new DataValue(attrGroupId, Types.VARCHAR),
							new DataValue(attrGroup_sortId_i, Types.VARCHAR),									
							new DataValue(eId, Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE)
						};
				InsBean ib2 = new InsBean("DCP_ATTRGROUP_DETAIL", columnsName);
				ib2.addValues(insValueDetail);
				this.addProcessData(new DataProcessBean(ib2));	
				
			}
		
			
		}
		
		UptBean ub1 = null;	
		ub1 = new UptBean("DCP_ATTRIBUTION");
		//add Value
		
		if (sortId!=null&&sortId.length()>0) 
		{
			ub1.addUpdateValue("SORTID", new DataValue(sortId_i, Types.VARCHAR));	
		}
		if(memo!=null)
		{
			ub1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		}
		
		
		ub1.addUpdateValue("PRODUCTPARAM", new DataValue(productParam, Types.VARCHAR));		
		ub1.addUpdateValue("MULTISPEC", new DataValue(multiSpec, Types.VARCHAR));
		ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
		//condition
		ub1.addCondition("ATTRID", new DataValue(attrId, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(ub1));
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
	
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_AttributionUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AttributionUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AttributionUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AttributionUpdateReq req) throws Exception {

		// TODO Auto-generated method stub

		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");

	    if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
	    String attrId = req.getRequest().getAttrId();
	    String productParam = req.getRequest().getProductParam();
	    String multiSpec = req.getRequest().getMultiSpec();
	    if(Check.Null(attrId)){
		   	errMsg.append("属性编码不能为空值 ");
		   	isFail = true;
		
	    }
	    if(Check.Null(productParam)){
		   	errMsg.append("是否用于产品productParam不能为空值 ");
		   	isFail = true;
		
	    }
	    if(Check.Null(multiSpec)){
		   	errMsg.append("是否用于多规格multiSpec不能为空值 ");
		   	isFail = true;
		
	    }
	    List<level1Elm> datas = req.getRequest().getAttrName_lang();
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
	   	
	   	List<attrGroup> groupList = req.getRequest().getAttrGroup();
	   	if(groupList!=null&&groupList.isEmpty()==false)
	   	{
	   		for (attrGroup oneDataGroup : groupList) 
	   		{
	   			if(Check.Null(oneDataGroup.getAttrGroupId()))
	   			{
	   				errMsg.append("属性分组编码不能为空值 ");
    		   	isFail = true;
	   				
	   			}
			
		
	   		}
	   	}
	  
	   	
	    
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_AttributionUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttributionUpdateReq>(){};
	}

	@Override
	protected DCP_AttributionUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttributionUpdateRes() ;
	}

	
}
