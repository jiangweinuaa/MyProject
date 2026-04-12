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
import com.dsc.spos.json.cust.req.DCP_AttrGroupCreateReq;
import com.dsc.spos.json.cust.req.DCP_AttrGroupCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_AttrGroupCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_AttrGroupCreate extends SPosAdvanceService<DCP_AttrGroupCreateReq,DCP_AttrGroupCreateRes> {

	@Override
	protected void processDUID(DCP_AttrGroupCreateReq req, DCP_AttrGroupCreateRes res) throws Exception {
	// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		
		String sql = null;
		String eId = req.geteId();
		String attrGroupId = req.getRequest().getAttrGroupId();
		String status = req.getRequest().getStatus();
		String sortId = req.getRequest().getSortId();
		int sortId_i = 0;
		try 
		{
			sortId_i = Integer.parseInt(sortId);
			
		} catch (Exception e) {
		// TODO: handle exception
	
		}
		
		String memo = req.getRequest().getMemo();
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		List<level1Elm> getLangDatas = req.getRequest().getAttrGroupName_lang();
		
		sql = this.isRepeat(attrGroupId, eId);
		List<Map<String , Object>> datas = this.doQueryData(sql, null);
		if(datas.isEmpty()){
			if(getLangDatas != null && !getLangDatas.isEmpty()){
				for(level1Elm oneLv1: getLangDatas){
									
					String[] columnsName = {
					  		"ATTRGROUPID","LANG_TYPE","ATTRGROUPNAME","EID","LASTMODITIME"
						};
					
					String langType = oneLv1.getLangType();
					String attrGroupName = oneLv1.getName();
								
					sql = this.isRepeatLang(attrGroupId, eId, langType);
					List<Map<String, Object>> detailDatas = this.doQueryData(sql, null);
					
					if(detailDatas.isEmpty()){
						
						DataValue[] insValueDetail = new DataValue[] 
								{
									new DataValue(attrGroupId, Types.VARCHAR),
									new DataValue(langType, Types.VARCHAR),
									new DataValue(attrGroupName, Types.VARCHAR),									
									new DataValue(eId, Types.VARCHAR),
									new DataValue(lastmoditime, Types.DATE)
								};
						InsBean ib2 = new InsBean("DCP_ATTRGROUP_LANG", columnsName);
						ib2.addValues(insValueDetail);
						this.addProcessData(new DataProcessBean(ib2));	
				
					}else{
						res.setSuccess(false);
						res.setServiceStatus("200");
						res.setServiceDescription("服务执行失败: 属性分组编码为  "+attrGroupId+" , 多语言类型为 "+langType+" 的信息已存在");	
						return;
					}
				}
			}
			

			String[] columns1 = { "ATTRGROUPID","SORTID","STATUS","EID","MEMO","CREATETIME" };
			DataValue[] insValue1 = null;
			
			insValue1 = new DataValue[]{
					new DataValue(attrGroupId, Types.VARCHAR),
					new DataValue(sortId_i, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(eId, Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)
				};
			
			InsBean ib1 = new InsBean("DCP_ATTRGROUP", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增品牌
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	
			
		}else{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败: 品牌编码为  "+attrGroupId+"  的信息已存在");	
			return;
		}
		
	
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_AttrGroupCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_AttrGroupCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_AttrGroupCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_AttrGroupCreateReq req) throws Exception {

		// TODO Auto-generated method stub

		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");

	    if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
	    String attrGroupId = req.getRequest().getAttrGroupId();
	        
	    if(Check.Null(attrGroupId)){
		   	errMsg.append("属性分组编码不能为空值 ");
		   	isFail = true;
		
	    }
	    List<level1Elm> datas = req.getRequest().getAttrGroupName_lang();
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
	protected TypeToken<DCP_AttrGroupCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_AttrGroupCreateReq>(){};
	}

	@Override
	protected DCP_AttrGroupCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_AttrGroupCreateRes();
	}

	private String isRepeat(String attrGroupId, String eId){
		String sql = null;
		sql = "SELECT * FROM DCP_ATTRGROUP WHERE "
				+ " ATTRGROUPID = '"+attrGroupId+"' "
				+ " and EID = '"+eId+"'" ;
		return sql;
	}
	
	private String isRepeatLang(String attrGroupId, String eId, String langType ){
		String sql = null;
		sql = "SELECT * FROM DCP_ATTRGROUP_LANG WHERE "
				+ " ATTRGROUPID = '"+attrGroupId+"' "
				+ " and EID = '"+eId+"' "
				+ " and lang_Type = '"+langType+"'" ;
		return sql;
	}
}
