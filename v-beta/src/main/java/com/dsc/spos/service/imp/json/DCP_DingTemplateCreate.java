package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DingTemplateCreateReq;
import com.dsc.spos.json.cust.res.DCP_DingTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DingTemplateCreateDCP
 * 服务说明： 钉钉审批模板新增
 * @author jinzma
 * @since  2019-10-31
 */
public class DCP_DingTemplateCreate extends SPosAdvanceService<DCP_DingTemplateCreateReq,DCP_DingTemplateCreateRes> {

	@Override
	protected void processDUID(DCP_DingTemplateCreateReq req, DCP_DingTemplateCreateRes res) throws Exception {
		// TODO 自动生成的方法存根

		String eId = req.geteId();
		String templateNO =req.getRequest().getTemplateNo();
		String templateName = req.getRequest().getTemplateName();
		String status = req.getRequest().getStatus();

		try
		{
			if (checkExist(req) == false)
			{
				String[] columns = { "EID", "TEMPLATENO","TEMPLATENAME","STATUS" };

				DataValue[] insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(templateNO, Types.VARCHAR), 
						new DataValue(templateName, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
				};
				InsBean ib = new InsBean("DCP_DING_TEMPLATE", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 

			}
			else
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板编号已存在！");		
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
	protected List<InsBean> prepareInsertData(DCP_DingTemplateCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DingTemplateCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DingTemplateCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DingTemplateCreateReq req) throws Exception {
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
	protected TypeToken<DCP_DingTemplateCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DingTemplateCreateReq>(){};
	}

	@Override
	protected DCP_DingTemplateCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DingTemplateCreateRes();
	}

	private boolean checkExist(DCP_DingTemplateCreateReq req)  throws Exception {
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
