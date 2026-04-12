package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DepartmentUpdateReq;
import com.dsc.spos.json.cust.req.DCP_OrgUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DepartmentUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_DepartmentUpdate extends SPosAdvanceService<DCP_DepartmentUpdateReq, DCP_DepartmentUpdateRes> 
{

	@Override
	protected void processDUID(DCP_DepartmentUpdateReq req, DCP_DepartmentUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		//开始组del语句
		if(req.getRequest().getDepartNo()!=null&&req.getRequest().getUpDepartNo()!=null)
	  {
	  	if(req.getRequest().getDepartNo().equals(req.getRequest().getUpDepartNo()))
	  	{
	  		res.setSuccess(false);
	  		res.setServiceDescription("上级部门不能选择本部门！");
	  		return;
	  	}
	  }
		
		DelBean del=new DelBean("DCP_DEPARTMENT");
		del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		del.addCondition("DEPARTNO", new DataValue(req.getRequest().getDepartNo(), Types.VARCHAR));
		this.pData.add(new DataProcessBean(del));
		
		String[] colname={"EID","DEPARTNO","DEPARTNAME","UPDEPARTNO","MEMO","STATUS","CREATEBY",
				"CREATE_DTIME","MODIFYBY","MODIFY_DTIME"};
		DataValue[] insValue={new DataValue(req.geteId(), Types.VARCHAR)
				,new DataValue(req.getRequest().getDepartNo(), Types.VARCHAR)
				,new DataValue(req.getRequest().getDepartName(), Types.VARCHAR)
				,new DataValue(req.getRequest().getUpDepartNo(), Types.VARCHAR)
				,new DataValue(req.getRequest().getMemo(), Types.VARCHAR)
				,new DataValue(req.getRequest().getStatus(), Types.VARCHAR)
				,new DataValue(req.getOpNO(), Types.VARCHAR)
				,new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR)
				,new DataValue(req.getOpNO(), Types.VARCHAR)
				,new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR)};
		InsBean ins=new InsBean("DCP_DEPARTMENT", colname);
		ins.addValues(insValue);
		this.pData.add(new DataProcessBean(ins));
		
		if(req.getRequest().getDepart_Lang()!=null&&!req.getRequest().getDepart_Lang().isEmpty())
		{
			del=new DelBean("DCP_DEPARTMENT_LANG");
			del.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			del.addCondition("DEPARTNO", new DataValue(req.getRequest().getDepartNo(), Types.VARCHAR));
			this.pData.add(new DataProcessBean(del));
			for (DCP_DepartmentUpdateReq.level2Elm departlang : req.getRequest().getDepart_Lang()) 
			{
				String[] colnamelev={"EID","Lang_Type","DEPARTNO","DEPARTNAME","STATUS"};
				DataValue[] insValuelev={
						new DataValue(req.geteId(), Types.VARCHAR)
						,new DataValue(departlang.getLang_Type(), Types.VARCHAR)
						,new DataValue(req.getRequest().getDepartNo(), Types.VARCHAR)
						,new DataValue(departlang.getDepartName(), Types.VARCHAR)
						,new DataValue("100", Types.VARCHAR)
						}; 
			  ins=new InsBean("DCP_DEPARTMENT_LANG", colnamelev);
				ins.addValues(insValuelev);
				this.pData.add(new DataProcessBean(ins));
			}
			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DepartmentUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DepartmentUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DepartmentUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DepartmentUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
		  	errMsg.append("request不能为空 ");
		  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if (isFail)
	    {
		   throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		  
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DepartmentUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_DepartmentUpdateReq>(){};
	}

	@Override
	protected DCP_DepartmentUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_DepartmentUpdateRes();
	}

}
