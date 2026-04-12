package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MiniChargeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_MiniChargeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 低消信息删除
 * @author yuanyy 2019-03-01 
 *
 */
public class DCP_MiniChargeDelete extends SPosAdvanceService<DCP_MiniChargeDeleteReq, DCP_MiniChargeDeleteRes> {

	@Override
	protected void processDUID(DCP_MiniChargeDeleteReq req, DCP_MiniChargeDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String eId = req.geteId(); 
			String miniChargeNO = req.getRequest().getMiniChargeNo();

			DelBean db1 = new DelBean("DCP_MINICHARGE");
			db1.addCondition("MINICHARGENO", new DataValue(miniChargeNO, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			DelBean db2 = new DelBean("DCP_MINICHARGE_DETAIL");
			db2.addCondition("MINICHARGENO", new DataValue(miniChargeNO, Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MiniChargeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MiniChargeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MiniChargeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MiniChargeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String miniChargeNO = req.getRequest().getMiniChargeNo();

		if (Check.Null(miniChargeNO)  ) 
		{
			errMsg.append("编号不能为空！ ");
			isFail = true;
		}	
		return isFail;
	}

	@Override
	protected TypeToken<DCP_MiniChargeDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MiniChargeDeleteReq> (){};
	}

	@Override
	protected DCP_MiniChargeDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MiniChargeDeleteRes();
	}

}
