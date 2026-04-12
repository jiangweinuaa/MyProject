package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ParaDefineDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ParaDefineDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ParaDefineDelete extends SPosAdvanceService<DCP_ParaDefineDeleteReq, DCP_ParaDefineDeleteRes> {

	@Override
	protected void processDUID(DCP_ParaDefineDeleteReq req, DCP_ParaDefineDeleteRes res) throws Exception 
	{
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String item = req.getRequest().getItem();
		try 
		{ 
			String sqlBaseSetTempItem=getBaseSetTempItem_SQL(req);				
			String[] conditionValues_checkNO = null ; //查詢條件
			List<Map<String, Object>> getQData_checkNO = this.doQueryData(sqlBaseSetTempItem,conditionValues_checkNO);
			if(getQData_checkNO !=null && !getQData_checkNO.isEmpty())
			{	
				
				//PLATFORM_BASESETTEMP
				DelBean db1 = new DelBean("PLATFORM_BASESETTEMP");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1)); // 删除		

				//PLATFORM_BASESETTEMP_LANG
				DelBean db2 = new DelBean("PLATFORM_BASESETTEMP_LANG");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2)); // 删除		
				
//				//PLATFORM_BASESET
//				DelBean db3 = new DelBean("PLATFORM_BASESET");
//				db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//				db3.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
//				this.addProcessData(new DataProcessBean(db3)); // 删除		
				
				//PLATFORM_BASESETTEMP_PARA
				DelBean db4 = new DelBean("PLATFORM_BASESETTEMP_PARA");
				db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db4.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db4)); // 删除		
				
				//PLATFORM_BASESETTEMP_PARA_LANG
				DelBean db5 = new DelBean("PLATFORM_BASESETTEMP_PARA_LANG");
				db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db5.addCondition("ITEM", new DataValue(item, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db5)); // 删除		
				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else
			{
				res.setSuccess(false);
				res.setServiceDescription("参数编码不存在！");
			}
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ParaDefineDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ParaDefineDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ParaDefineDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null ;
	}

	@Override
	protected boolean isVerifyFail(DCP_ParaDefineDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；	
		if (Check.Null(req.getRequest().getItem())) 
		{
			errCt++;
			errMsg.append("参数编码不可为空值!");
			isFail = true;
		} 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;	
	}

	@Override
	protected TypeToken<DCP_ParaDefineDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_ParaDefineDeleteReq>(){};
	}

	@Override
	protected DCP_ParaDefineDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ParaDefineDeleteRes();
	}

	protected String getBaseSetTempItem_SQL(DCP_ParaDefineDeleteReq req)
	{
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId=req.geteId();
		String ITEM=req.getRequest().getItem();
		sqlbuf.append("select ITEM from Platform_BaseSetTemp "
				+ "WHERE EID='"+eId +"'  "
				+ "AND UPPER(ITEM)='"+ITEM.toUpperCase() +"' ");			
		sql = sqlbuf.toString(); 	
		return sql;	
	}

}
