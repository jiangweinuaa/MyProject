package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DinnerTimeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DinnerTimeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 餐段新增
 * @author yuanyy 2019-09-18
 *
 */
public class DCP_DinnerTimeDelete extends SPosAdvanceService<DCP_DinnerTimeDeleteReq, DCP_DinnerTimeDeleteRes> {

	@Override
	protected void processDUID(DCP_DinnerTimeDeleteReq req, DCP_DinnerTimeDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		try {
			String dtNo = req.getRequest().getDtNo();
			String workNo = req.getRequest().getWorkNo();
			
			DelBean db1 = new DelBean("DCP_DINNERTIME");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			db1.addCondition("DTNO", new DataValue(dtNo, Types.VARCHAR));
			db1.addCondition("WORKNO", new DataValue(workNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			this.doExecuteDataToDB();	
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败:" + e.getMessage());	
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DinnerTimeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DinnerTimeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DinnerTimeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DinnerTimeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String dtNo = req.getRequest().getDtNo();

		if (Check.Null(dtNo)) 
		{
			errCt++;
			errMsg.append("餐段编号不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}


		return isFail;
	}

	@Override
	protected TypeToken<DCP_DinnerTimeDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DinnerTimeDeleteReq>(){};
	}

	@Override
	protected DCP_DinnerTimeDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DinnerTimeDeleteRes();
	}

}
