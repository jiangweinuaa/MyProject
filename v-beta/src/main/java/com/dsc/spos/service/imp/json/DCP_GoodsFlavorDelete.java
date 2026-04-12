package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_GoodsFlavorDeleteReq;
import com.dsc.spos.json.cust.res.DCP_GoodsFlavorDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsFlavorDelete extends SPosAdvanceService<DCP_GoodsFlavorDeleteReq, DCP_GoodsFlavorDeleteRes> {

	@Override
	protected void processDUID(DCP_GoodsFlavorDeleteReq req, DCP_GoodsFlavorDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String flavorNO = req.getRequest().getFlavorNo();
			String eId = req.geteId();
			DelBean db1 = new DelBean("DCP_FLAVOR");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("FLAVORNO", new DataValue(flavorNO, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(db1));  

			UptBean ub1 = new UptBean("DCP_FLAVOR");			
			ub1.addCondition("PRIORITY",new DataValue( req.getRequest().getPriority(), Types.VARCHAR,DataExpression.GreaterEQ));
			ub1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));

			ub1.addUpdateValue("PRIORITY",new DataValue(1,Types.INTEGER,DataExpression.SubSelf)); 
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();		

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");	
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsFlavorDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsFlavorDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsFlavorDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsFlavorDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String flavorNO = req.getRequest().getFlavorNo();

		if(Check.Null(flavorNO)){
			errMsg.append("口味编码不能为空值 ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsFlavorDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsFlavorDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsFlavorDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsFlavorDeleteRes();
	}

}
