package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FeeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_FeeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 2018-09-27 费用项目删除
 * @author yuanyy
 *
 */
public class DCP_FeeDelete extends SPosAdvanceService<DCP_FeeDeleteReq, DCP_FeeDeleteRes> {

	@Override
	protected void processDUID(DCP_FeeDeleteReq req, DCP_FeeDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String fee = req.getRequest().getFee();
			String eId = req.geteId();

			DelBean db1 = new DelBean("DCP_FEE_LANG");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("FEE", new DataValue(fee, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			DelBean db2 = new DelBean("DCP_FEE");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("FEE", new DataValue(fee, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));

			this.doExecuteDataToDB();			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		}
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FeeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FeeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FeeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FeeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//必传值不为空
		String fee = req.getRequest().getFee();

		if(Check.Null(fee)){
			errMsg.append("费用编码不能为空值 ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FeeDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FeeDeleteReq>(){};
	}

	@Override
	protected DCP_FeeDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_FeeDeleteRes();
	}


}
