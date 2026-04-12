package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ElementDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ElementDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 影响因素新增
 * @author yuanyy 
 *
 */
public class DCP_ElementDelete extends SPosAdvanceService<DCP_ElementDeleteReq, DCP_ElementDeleteRes> {

	@Override
	protected void processDUID(DCP_ElementDeleteReq req, DCP_ElementDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			String eType = req.getRequest().getE_Type();
			String eNo = req.getRequest().geteNo();

			DelBean del1 = new DelBean("DCP_ELEMENT");
			//condition
			del1.addCondition("E_TYPE", new DataValue(eType, Types.VARCHAR));
			del1.addCondition("E_NO", new DataValue(eNo, Types.VARCHAR));	
			del1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			del1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(del1));
			
			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ElementDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ElementDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ElementDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ElementDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//必传值不为空
		String eType = req.getRequest().getE_Type();

		if(Check.Null(eType)){
			errMsg.append("因素类型不能为空值 ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().geteNo())){
			errMsg.append("因素编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ElementDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ElementDeleteReq>(){};
	}

	@Override
	protected DCP_ElementDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ElementDeleteRes();
	}


}
