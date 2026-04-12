package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DingTemplateUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DingTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DingTemplateUpdateDCP
 * 服务说明： 钉钉审批模板修改
 * @author jinzma
 * @since  2019-10-31
 */
public class DCP_DingTemplateUpdate extends SPosAdvanceService<DCP_DingTemplateUpdateReq,DCP_DingTemplateUpdateRes>{

	@Override
	protected void processDUID(DCP_DingTemplateUpdateReq req, DCP_DingTemplateUpdateRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String templateNO =req.getRequest().getTemplateNo();
		String templateName = req.getRequest().getTemplateName();
		String status = req.getRequest().getStatus();

		try 
		{
			if (checkExist(req))
			{	
				UptBean ub = new UptBean("DCP_DING_TEMPLATE");
				ub.addUpdateValue("TEMPLATENAME", new DataValue(templateName, Types.VARCHAR));
				ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));

				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("TEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
				
				this.addProcessData(new DataProcessBean(ub));
			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板编号不存在，请重新输入！");
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
	protected List<InsBean> prepareInsertData(DCP_DingTemplateUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DingTemplateUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DingTemplateUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DingTemplateUpdateReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (Check.Null(req.getRequest().getTemplateNo())) 
		{
			errMsg.append("模板编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getTemplateName())) 
		{
			errMsg.append("模板名称不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DingTemplateUpdateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingTemplateUpdateReq>(){};
	}

	@Override
	protected DCP_DingTemplateUpdateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingTemplateUpdateRes();
	}

	private boolean checkExist(DCP_DingTemplateUpdateReq req)  throws Exception {
		String sql = null;
		boolean exist = false;
		String eId = req.geteId();
		String templateNO =req.getRequest().getTemplateNo();

		sql = " select * from DCP_DING_TEMPLATE  where EID='"+eId+"' and TEMPLATENO='"+templateNO+"' " ;
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}
		return exist;
	}

}
