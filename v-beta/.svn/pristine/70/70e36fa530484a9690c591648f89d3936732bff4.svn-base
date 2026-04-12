package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_POrderTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_POrderTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_POrderTemplateDelete extends SPosAdvanceService<DCP_POrderTemplateDeleteReq, DCP_POrderTemplateDeleteRes>
{

	@Override
	protected void processDUID(DCP_POrderTemplateDeleteReq req, DCP_POrderTemplateDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String templateNO = req.getRequest().getTemplateNo();
		try 
		{
			//
			DelBean db1 = new DelBean("DCP_PTEMPLATE");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			db1.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			//
			DelBean db2 = new DelBean("DCP_PTEMPLATE_DETAIL");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			db2.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			//
			DelBean db3 = new DelBean("DCP_PTEMPLATE_SHOP");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			db3.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));

            //大客户对应模板
            DelBean db4 = new DelBean("DCP_TEMCUSTOMER");
            db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db4.addCondition("TEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db4));

            DelBean db5 = new DelBean("DCP_PTEMPLATE_EMPLOYEE");
			db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db5.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			db5.addCondition("DOC_TYPE", new DataValue("0", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db5));
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
	protected List<InsBean> prepareInsertData(DCP_POrderTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_POrderTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_POrderTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_POrderTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String templateNO = req.getRequest().getTemplateNo();
		if (Check.Null(templateNO)) {
			isFail = true;
			errCt++;
			errMsg.append("模板编号不可为空值, ");
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_POrderTemplateDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_POrderTemplateDeleteReq>(){};
	}

	@Override
	protected DCP_POrderTemplateDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_POrderTemplateDeleteRes();
	}

}
