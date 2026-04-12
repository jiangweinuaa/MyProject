package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_HorseLampDeleteReq;
import com.dsc.spos.json.cust.req.DCP_ScanOrderBaseSetDeleteReq;
import com.dsc.spos.json.cust.res.DCP_HorseLampDeleteRes;
import com.dsc.spos.json.cust.res.DCP_ScanOrderBaseSetDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_ScanOrderBaseSetDelete extends SPosAdvanceService<DCP_ScanOrderBaseSetDeleteReq,DCP_ScanOrderBaseSetDeleteRes>
{

	@Override
	protected void processDUID(DCP_ScanOrderBaseSetDeleteReq req, DCP_ScanOrderBaseSetDeleteRes res) throws Exception 
	{
		String eid = req.geteId();
		String ruleno = req.getRequest().getRuleNo();

		DelBean db1 = new DelBean("DCP_SCANORDER_BASESET");
		db1.addCondition("EID", new DataValue(eid, Types.VARCHAR));
		db1.addCondition("RULENO", new DataValue(ruleno, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

		DelBean db2 = new DelBean("DCP_SCANORDER_BASESET_RANGE");
		db2.addCondition("EID", new DataValue(eid, Types.VARCHAR));
		db2.addCondition("RULENO", new DataValue(ruleno, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));

        DelBean db3 = new DelBean("DCP_SCANORDER_BASESET_PAYTYPE");
        db3.addCondition("EID", new DataValue(eid, Types.VARCHAR));
        db3.addCondition("RULENO", new DataValue(ruleno, Types.VARCHAR));
        this.addProcessData(new DataProcessBean(db3));
		this.doExecuteDataToDB();
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ScanOrderBaseSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ScanOrderBaseSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ScanOrderBaseSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ScanOrderBaseSetDeleteReq req) throws Exception 
	{

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (Check.Null(req.getRequest().getRuleNo())) 
		{
			errMsg.append("规则编号不可为空值, ");
			isFail = true;
		}

		if (isFail) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ScanOrderBaseSetDeleteReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ScanOrderBaseSetDeleteReq>(){};
	}

	@Override
	protected DCP_ScanOrderBaseSetDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_ScanOrderBaseSetDeleteRes();
	}


}
