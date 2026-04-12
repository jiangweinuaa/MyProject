package com.dsc.spos.service.imp.json;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FuncUpdateReq;
import com.dsc.spos.json.cust.req.DCP_FuncUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_FuncUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;
public class DCP_FuncUpdate extends SPosAdvanceService<DCP_FuncUpdateReq,DCP_FuncUpdateRes> {

	@Override
	protected void processDUID(DCP_FuncUpdateReq req, DCP_FuncUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		level1Elm request = req.getRequest();
		String funcNo = request.getFuncNo();
		String funcType = request.getFuncType();
		String approve = request.getApprove();
		String eId=req.geteId();
		try 
		{
			if (checkExist(req) == true) //update
			{
				UptBean ub = new UptBean("DCP_DINGFUNC");
				ub.addUpdateValue("APPROVE", new DataValue(approve, Types.VARCHAR));
				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("FUNCNO", new DataValue(funcNo, Types.VARCHAR));
				ub.addCondition("FUNCTYPE", new DataValue(funcType, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub));
			}
			else  //insert 
			{
				String[] columns = {"EID","FUNCNO","FUNCTYPE","APPROVE" };
				DataValue[] insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(funcNo, Types.VARCHAR), 
						new DataValue(funcType, Types.VARCHAR),
						new DataValue(approve, Types.VARCHAR),								
				};
				InsBean ib = new InsBean("DCP_DINGFUNC", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib));			
			}

			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FuncUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FuncUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FuncUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FuncUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		level1Elm request = req.getRequest();

		if (request!=null )
		{
			if (Check.Null(request.getFuncNo())) 
			{
				errMsg.append("功能编号不可为空值, ");
				isFail = true;
			}
			if (Check.Null(request.getFuncType()) ) 
			{
				errMsg.append("功能类型不可为空值, ");
				isFail = true;
			}
			else 
			{
				if (!(request.getFuncType().equals("pos")||request.getFuncType().equals("dcp"))) 
				{
					errMsg.append("功能类型给值错误, ");
					isFail = true;
				}
			}
			if (Check.Null(request.getApprove()) ) 
			{
				errMsg.append("钉钉审批不可为空值, ");
				isFail = true;
			}
			else
			{
				if (!PosPub.isNumeric(request.getApprove()))
				{
					errMsg.append("钉钉审批必须为数值, ");
					isFail = true;
				}
			}
		}
		else
		{
			errMsg.append("request不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_FuncUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_FuncUpdateReq>(){};
	}

	@Override
	protected DCP_FuncUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_FuncUpdateRes();
	}

	private boolean checkExist(DCP_FuncUpdateReq req)  throws Exception {
		String sql = null;
		boolean exist = false;
		level1Elm request = req.getRequest();
		String eId=req.geteId();
		String funcNo = request.getFuncNo();
		String funcType = request.getFuncType();

		sql = " select EID from dcp_dingfunc where  EID='"+eId+"' and  FUNCNO='"+funcNo+"' and FUNCTYPE='"+funcType+"' ";

		List<Map<String, Object>> getQData = this.doQueryData(sql,null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

}
