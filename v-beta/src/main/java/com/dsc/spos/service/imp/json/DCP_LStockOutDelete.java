package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LStockOutDeleteReq;
import com.dsc.spos.json.cust.res.DCP_LStockOutDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_LStockOutDelete extends SPosAdvanceService<DCP_LStockOutDeleteReq, DCP_LStockOutDeleteRes>
{
	@Override
	protected boolean isVerifyFail(DCP_LStockOutDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String lStockOutNO = req.getRequest().getlStockOutNo();
		if (Check.Null(lStockOutNO)) {
			isFail = true;
			errMsg.append("报损单单号不可为空值, ");
		}  

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected void processDUID(DCP_LStockOutDeleteReq req, DCP_LStockOutDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String shopId = req.getShopId();
		String eId = req.geteId();;
		String lStockOutNO = req.getRequest().getlStockOutNo();
		try 
		{
			String sql ="select status from DCP_lstockout "
					+ "where EID='"+eId+"' and shopid='"+shopId+"' and lstockoutno='"+lStockOutNO+"' and status='0' ";
			List<Map<String, Object>> getQDataDetail = this.doQueryData(sql, null);		
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)	
			{
				//DCP_LSTOCKOUT
				DelBean db1 = new DelBean("DCP_LSTOCKOUT");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("lStockOutNO", new DataValue(lStockOutNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				//DCP_LSTOCKOUT_DETAIL
				DelBean db2 = new DelBean("DCP_LSTOCKOUT_DETAIL");
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db2.addCondition("lStockOutNO", new DataValue(lStockOutNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));

				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");

			}
			else 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "单据不存在或已确认！");
			}
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_LStockOutDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_LStockOutDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_LStockOutDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TypeToken<DCP_LStockOutDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_LStockOutDeleteReq>(){};
	}

	@Override
	protected DCP_LStockOutDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_LStockOutDeleteRes();
	}

}
