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
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsOnlineDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsOnlineDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsOnlineDelete extends SPosAdvanceService<DCP_GoodsOnlineDeleteReq, DCP_GoodsOnlineDeleteRes> {

	@Override
	protected void processDUID(DCP_GoodsOnlineDeleteReq req, DCP_GoodsOnlineDeleteRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{
			String lastmoditime = null;//req.getRequset().getLastmoditime();
			if(lastmoditime==null||lastmoditime.isEmpty())
			{
				lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			}
			
			String eId= req.geteId();		
			String curLangType = req.getLangType();
			if(curLangType==null||curLangType.isEmpty())
			{
				curLangType = "zh_CN";
			}
			String classType = "ONLINE";
			String sql = "";
			for (level1Elm par : req.getRequest().getPluList())
			{
				
				String pluNo = par.getPluNo();	
//				sql = "";
//				sql = "select status from DCP_GOODS_ONLINE "
//					+ "where status='-1' and eid='"+eId+"' and PLUNO='"+pluNo+"' ";
//				List<Map<String, Object>> getData = this.doQueryData(sql, null);
//				if(getData==null||getData.isEmpty())
//				{
//					continue;
//				}
				
				UptBean ub1 = new UptBean("DCP_GOODS_ONLINE");			
				ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
				ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));

				//按玲霞的要求，删除固定修改状态为90 ，  数据不删除
				ub1.addUpdateValue("STATUS",new DataValue("90", Types.VARCHAR));  
				
				ub1.addUpdateValue("LASTMODIOPID",new DataValue(req.getOpNO(), Types.VARCHAR)); 
				ub1.addUpdateValue("LASTMODIOPNAME",new DataValue(req.getOpName(), Types.VARCHAR));
				ub1.addUpdateValue("LASTMODITIME",new DataValue(lastmoditime, Types.DATE)); 
			
				this.addProcessData(new DataProcessBean(ub1));
				
//				DelBean db1 = new DelBean("DCP_GOODS_ONLINE");
//				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));				
//				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//				this.addProcessData(new DataProcessBean(db1));
//				
//				db1 = new DelBean("DCP_GOODS_ONLINE_LANG");
//				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
//				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//				this.addProcessData(new DataProcessBean(db1)); // 
//			
//				//先删除原来的
//				db1 = new DelBean("DCP_CLASS_GOODS");
//				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
//				db1.addCondition("CLASSTYPE", new DataValue(classType, Types.VARCHAR));
//				
//				this.addProcessData(new DataProcessBean(db1));
//				
//			//先删除原来的
//				db1 = new DelBean("DCP_GOODS_ONLINE_REFCLASS");
//				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
//				this.addProcessData(new DataProcessBean(db1));
//				
//			//先删除原来的
//				db1 = new DelBean("DCP_GOODS_ONLINE_INTRO");
//				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
//				this.addProcessData(new DataProcessBean(db1));
//
//				//先删除原来的
//				db1 = new DelBean("DCP_GOODS_ONLINE_INTRO_LANG");
//				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
//				db1.addCondition("LANG_TYPE", new DataValue(curLangType, Types.VARCHAR));
//				this.addProcessData(new DataProcessBean(db1));
//
//				//先删除原来的
//				db1 = new DelBean("DCP_GOODS_ONLINE_MSGKIND");
//				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
//				this.addProcessData(new DataProcessBean(db1));		
				
			
			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("服务执行异常:"+e.getMessage());
		}
		
		
		
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsOnlineDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsOnlineDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsOnlineDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsOnlineDeleteReq req) throws Exception {
	// TODO Auto-generated method stub

	boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("request不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  
  if (req.getRequest().getPluList()==null) 
  {
  	errMsg.append("编码不能为空 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	}
  
  
  for (level1Elm par : req.getRequest().getPluList())
	{ 	
  	String pluNo = par.getPluNo();    
    if(Check.Null(pluNo)){
     	errMsg.append("编码不能为空值 ，");
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
	protected TypeToken<DCP_GoodsOnlineDeleteReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsOnlineDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsOnlineDeleteRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsOnlineDeleteRes();
	}

}
