package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsStyleDeleteReq;
import com.dsc.spos.json.cust.res.DCP_GoodsStyleDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsStyleDelete extends SPosAdvanceService<DCP_GoodsStyleDeleteReq, DCP_GoodsStyleDeleteRes> {

	@Override
	protected void processDUID(DCP_GoodsStyleDeleteReq req, DCP_GoodsStyleDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String styleNO = req.getRequest().getStyleNo();
			String eId = req.geteId();

			DelBean db1 = new DelBean("DCP_STYLE");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("STYLENO", new DataValue(styleNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			DelBean db2 = new DelBean("DCP_STYLE_DETAIL");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("STYLENO", new DataValue(styleNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));


			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsStyleDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsStyleDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsStyleDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsStyleDeleteReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String styleNO = req.getRequest().getStyleNo();

		if(Check.Null(styleNO)){
			errMsg.append("款式编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsStyleDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsStyleDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsStyleDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsStyleDeleteRes();
	}

}
