package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderTransferErpSetDeleteReq;
import com.dsc.spos.json.cust.req.DCP_OrderTransferErpSetDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderTransferErpSetDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_OrderTransferErpSetDelete
 * 服务说明：订单上传ERP白名单删除
 * @author jinzma 
 * @since  2020-12-03
 */
public class DCP_OrderTransferErpSetDelete extends SPosAdvanceService<DCP_OrderTransferErpSetDeleteReq,DCP_OrderTransferErpSetDeleteRes>{

	@Override
	protected void processDUID(DCP_OrderTransferErpSetDeleteReq req, DCP_OrderTransferErpSetDeleteRes res)
			throws Exception {
		// TODO 自动生成的方法存根
		String eId=req.geteId();
		try 
		{
			List<level1Elm> orgList = req.getRequest().getOrgList();
			for (level1Elm par:orgList)
			{
				String shop=par.getShop();
				//删除资料
				DelBean db = new DelBean("DCP_ORDERTRANSFERERPSET");
				db.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db.addCondition("SHOP", new DataValue(shop, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db));
			}

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
	protected List<InsBean> prepareInsertData(DCP_OrderTransferErpSetDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderTransferErpSetDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderTransferErpSetDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderTransferErpSetDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> orgList = req.getRequest().getOrgList();
		if(orgList==null || orgList.isEmpty())
		{
			errMsg.append("组织列表不能为空, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderTransferErpSetDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderTransferErpSetDeleteReq>(){} ;
	}

	@Override
	protected DCP_OrderTransferErpSetDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderTransferErpSetDeleteRes();
	}

}
