package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PayClassUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PayClassUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PayClassUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PayClassUpdate extends SPosAdvanceService<DCP_PayClassUpdateReq, DCP_PayClassUpdateRes> {

	@Override
	protected void processDUID(DCP_PayClassUpdateReq req, DCP_PayClassUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId= req.geteId();
		//必传字段
	
		String classNo = req.getRequest().getClassNo();		
		String status = req.getRequest().getStatus();
		List<DCP_PayClassUpdateReq.level1Elm> className_lang = req.getRequest().getClassName_lang();	
			
		String memo = req.getRequest().getMemo();
		String sorId = req.getRequest().getSortId();
		
		String lastmoditime = null;//req.getRequest().getLastmoditime();
		if(lastmoditime==null||lastmoditime.isEmpty())
		{
			lastmoditime =	new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		}
		
		DelBean db1 = new DelBean("DCP_PAYCLASS_LANG");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(db1));
				
		UptBean up1 = new UptBean("DCP_PAYCLASS"); 
		up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		up1.addCondition("CLASSNO", new DataValue(classNo, Types.VARCHAR));
		
		up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		if(memo!=null)
		{
			up1.addUpdateValue("MEMO", new DataValue(memo, Types.VARCHAR));
		}
		if(sorId!=null)
		{
			up1.addUpdateValue("SORTID", new DataValue(sorId, Types.VARCHAR));
		}
		
		this.addProcessData(new DataProcessBean(up1));
		
		String[] columns_class_lang =
			{
					"EID",					
					"CLASSNO",
					"LANG_TYPE" ,
					"CLASSNAME",					
				  "LASTMODITIME"
		
			};
		
		for (level1Elm lang : className_lang) 
		{		
			String langType = lang.getLangType();
			String className = lang.getName();					
			DataValue[] insValue1 = null;			
			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),					
					new DataValue(classNo, Types.VARCHAR),
					new DataValue(langType, Types.VARCHAR),
					new DataValue(className, Types.VARCHAR),					
					new DataValue(lastmoditime, Types.DATE)
				};
			
			InsBean ib1 = new InsBean("DCP_PAYCLASS_LANG", columns_class_lang);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); 
	
		}
			
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PayClassUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PayClassUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PayClassUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PayClassUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	
	boolean isFail = false;
  StringBuffer errMsg = new StringBuffer("");

  if(req.getRequest()==null)
  {
  	errMsg.append("requset不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }
  String classNo = req.getRequest().getClassNo();
  String status = req.getRequest().getStatus();
      
  if(Check.Null(classNo)){
   	errMsg.append("编码不能为空值， ");
   	isFail = true;

  }
  if(Check.Null(status)){
   	errMsg.append("状态不能为空值， ");
   	isFail = true;

  }
  List<level1Elm> datas = req.getRequest().getClassName_lang();
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
	protected TypeToken<DCP_PayClassUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_PayClassUpdateReq>(){};
	}

	@Override
	protected DCP_PayClassUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_PayClassUpdateRes();
	}

}
