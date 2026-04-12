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
import com.dsc.spos.json.cust.req.DCP_AttributionCreateReq;
import com.dsc.spos.json.cust.req.DCP_AttributionCreateReq.attrGroup;
import com.dsc.spos.json.cust.req.DCP_AttributionCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AttributionCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_AttributionCreate extends SPosAdvanceService<DCP_AttributionCreateReq,DCP_AttributionCreateRes> {

	@Override
	protected void processDUID(DCP_AttributionCreateReq req, DCP_AttributionCreateRes res) throws Exception {
	// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		
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
		
		List<level1Elm> getLangDatas = req.getRequest().getAttrName_lang();
		List<attrGroup> attrGroupList = req.getRequest().getAttrGroup();
		
		sql = this.isRepeat(attrId, eId);
		List<Map<String , Object>> datas = this.doQueryData(sql, null);
		if(datas.isEmpty()){
			if(getLangDatas != null && !getLangDatas.isEmpty()){
				for(level1Elm oneLv1: getLangDatas){
									
					String[] columnsName = {
					  		"ATTRID","LANG_TYPE","ATTRNAME","EID","LASTMODITIME"
						};
					
					String langType = oneLv1.getLangType();
					String attrName = oneLv1.getName();
								
					sql = this.isRepeatLang(attrId, eId, langType);
					List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
					
					if(detailDatas.isEmpty()){
						
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
				
					}else{
						res.setSuccess(false);
						res.setServiceStatus("200");
						res.setServiceDescription("服务执行失败: 属性编码为  "+attrId+" , 多语言类型为 "+langType+" 的信息已存在");	
						return;
					}
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
								
					sql = this.isRepeatGroupDetail(attrId, eId, attrGroupId);
					List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
					
					if(detailDatas.isEmpty()){
						
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
				
					}else{
						res.setSuccess(false);
						res.setServiceStatus("200");
						res.setServiceDescription("服务执行失败: 属性编码为  "+attrId+" , 属性分组为 "+attrGroupId+" 的信息已存在");	
						return;
					}
				}
			
				
			}
			

			String[] columns1 = { "ATTRID","PRODUCTPARAM","MULTISPEC","SORTID","STATUS","EID","MEMO","CREATETIME" };
			DataValue[] insValue1 = null;
			
			insValue1 = new DataValue[]{
					new DataValue(attrId, Types.VARCHAR),
					new DataValue(productParam, Types.VARCHAR),
					new DataValue(multiSpec, Types.VARCHAR),
					new DataValue(sortId_i, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(eId, Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)
				};
			
			InsBean ib1 = new InsBean("DCP_ATTRIBUTION", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); 
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			
		}else{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败: 属性编码为  "+attrId+"  的信息已存在");	
			return;
		}
		
	
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_AttributionCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AttributionCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AttributionCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AttributionCreateReq req) throws Exception {

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
	protected TypeToken<DCP_AttributionCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttributionCreateReq>(){};
	}

	@Override
	protected DCP_AttributionCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttributionCreateRes();
	}

	private String isRepeat(String attrId, String eId){
		String sql = null;
		sql = "SELECT * FROM DCP_ATTRIBUTION WHERE "
				+ " ATTRID = '"+attrId+"' "
				+ " and EID = '"+eId+"'" ;
		return sql;
	}
	
	private String isRepeatLang(String attrId, String eId, String langType ){
		String sql = null;
		sql = "SELECT * FROM DCP_ATTRIBUTION_LANG WHERE "
				+ " ATTRID = '"+attrId+"' "
				+ " and EID = '"+eId+"' "
				+ " and lang_Type = '"+langType+"'" ;
		return sql;
	}
	
	private String isRepeatGroupDetail(String attrId, String eId, String attrGroupId){
		String sql = null;
		sql = "SELECT * FROM DCP_ATTRGROUP_DETAIL WHERE "
				+ " ATTRID = '"+attrId+"' "
				+ " and EID = '"+eId+"' "
				+ " and ATTRGROUPID = '"+attrGroupId+"'" ;
		return sql;
	}
	
}
