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
import com.dsc.spos.json.cust.req.DCP_PosFuncUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PosFuncUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PosFuncUpdate extends SPosAdvanceService<DCP_PosFuncUpdateReq, DCP_PosFuncUpdateRes>
{

	@Override
	protected void processDUID(DCP_PosFuncUpdateReq req, DCP_PosFuncUpdateRes res) throws Exception
	{
		// TODO Auto-generated method stub
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();
		
		for ( DCP_PosFuncUpdateReq.posFunc par : req.getRequest().getDatas())
		{
			String funcNo = par.getFuncNo();
			String funcName = par.getFuncName();
			String shortcutKey = par.getShortcutKey();
			String icon = par.getIcon();
			String qss = par.getQss();
			String sortId = par.getSortId();
			
			UptBean up1 = new UptBean("DCP_POSFUNC");
			up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			up1.addCondition("FUNCNO", new DataValue(funcNo,Types.VARCHAR));
			
			up1.addUpdateValue("FUNCNAME", new DataValue(funcName, Types.VARCHAR));
			up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			if(shortcutKey!=null)
			{
				up1.addUpdateValue("SHORTCUTKEY", new DataValue(shortcutKey, Types.VARCHAR));
			}
			
			if(icon!=null)
			{
				up1.addUpdateValue("ICON", new DataValue(icon, Types.VARCHAR));
			}
			
			if(qss!=null)
			{
				up1.addUpdateValue("QSS", new DataValue(qss, Types.VARCHAR));
			}
			
			if(sortId!=null&&sortId.trim().isEmpty()==false)
			{
				up1.addUpdateValue("SORTID", new DataValue(sortId, Types.VARCHAR));	
			}
					
			this.addProcessData(new DataProcessBean(up1));
		}
		
		
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PosFuncUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PosFuncUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PosFuncUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PosFuncUpdateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	isFail = true;
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
		if(req.getRequest().getDatas()==null)
	    {
	    	errMsg.append("修改资料列表不能为空值 ");
	    	isFail = true;
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
		
		for ( DCP_PosFuncUpdateReq.posFunc par : req.getRequest().getDatas())
		{
			
			String funcNo = par.getFuncNo();
			String funcName = par.getFuncName();
			
			if(Check.Null(funcName))
			{
				errMsg.append("funcName功能名称不能为空值, ");
				isFail = true;
			}
			
			if(Check.Null(funcNo))
			{
				errMsg.append("funcNo功能编码不能为空值, ");
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
	protected TypeToken<DCP_PosFuncUpdateReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PosFuncUpdateReq>(){};
	}

	@Override
	protected DCP_PosFuncUpdateRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_PosFuncUpdateRes();
	}

}
