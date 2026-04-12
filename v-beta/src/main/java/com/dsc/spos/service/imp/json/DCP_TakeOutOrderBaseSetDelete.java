package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TakeOutOrderBaseSetDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 外卖基础设置删除
 * @author yuanyy
 *
 */
public class DCP_TakeOutOrderBaseSetDelete extends SPosAdvanceService<DCP_TakeOutOrderBaseSetDeleteReq, DCP_TakeOutOrderBaseSetDeleteRes> {

	@Override
	protected void processDUID(DCP_TakeOutOrderBaseSetDeleteReq req, DCP_TakeOutOrderBaseSetDeleteRes res)
			throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String baseSetNo = req.getRequest().getBaseSetNo();
			
			DelBean db1 = new DelBean("DCP_TAKEOUT_BASESET_FREIGHT");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_TAKEOUT_BASESET_DETAIL");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			DelBean db3 = new DelBean("DCP_TAKEOUT_BASESET");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));
			
			DelBean db4 = new DelBean("DCP_TAKEOUT_BASESET_ORDERTIME");
			db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db4.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db4));

            DelBean db5 = new DelBean("DCP_TAKEOUT_BASESET_PAYTYPE");
            db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db5.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(db5));
			
			
			this.doExecuteDataToDB();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setServiceDescription("服务执行失败！");
			res.setSuccess(false);
			res.setServiceStatus("200");
			
		}

		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TakeOutOrderBaseSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TakeOutOrderBaseSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TakeOutOrderBaseSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TakeOutOrderBaseSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String baseSetNo = req.getRequest().getBaseSetNo();
		
		if (Check.Null(baseSetNo)) 
		{
			errMsg.append("编号不可为空值");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TakeOutOrderBaseSetDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TakeOutOrderBaseSetDeleteReq>(){};
	}

	@Override
	protected DCP_TakeOutOrderBaseSetDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TakeOutOrderBaseSetDeleteRes();
	}
	
}
