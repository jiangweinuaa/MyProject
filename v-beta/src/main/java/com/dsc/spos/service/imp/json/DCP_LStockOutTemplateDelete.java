package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LStockOutTemplateDeleteReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutTemplateDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 删除 报损模板 2019-01-08
 * @author yuanyy	
 *
 */
public class DCP_LStockOutTemplateDelete extends SPosAdvanceService<DCP_LStockOutTemplateDeleteReq, DCP_LStockOutTemplateDeleteRes> {

	@Override
	protected void processDUID(DCP_LStockOutTemplateDeleteReq req, DCP_LStockOutTemplateDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String templateNO = req.getRequest().getTemplateNo();
		try 
		{
			//DCP_PTEMPLATE
			DelBean db1 = new DelBean("DCP_PTEMPLATE");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			db1.addCondition("DOC_TYPE", new DataValue("4", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			//DCP_PTEMPLATE_DETAIL
			DelBean db2 = new DelBean("DCP_PTEMPLATE_DETAIL");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			db2.addCondition("DOC_TYPE", new DataValue("4", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			//DCP_PTEMPLATE_SHOP
			DelBean db3 = new DelBean("DCP_PTEMPLATE_SHOP");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("PTEMPLATENO", new DataValue(templateNO, Types.VARCHAR));
			db3.addCondition("DOC_TYPE", new DataValue("4", Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));

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
	protected List<InsBean> prepareInsertData(DCP_LStockOutTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_LStockOutTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_LStockOutTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_LStockOutTemplateDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		int errCt = 0;
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
	protected TypeToken<DCP_LStockOutTemplateDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_LStockOutTemplateDeleteReq>(){};
	}

	@Override
	protected DCP_LStockOutTemplateDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_LStockOutTemplateDeleteRes();
	}
	
}
