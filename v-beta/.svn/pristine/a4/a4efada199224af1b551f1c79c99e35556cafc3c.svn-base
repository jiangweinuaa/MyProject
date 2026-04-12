package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderTransferErpSetCreateReq;
import com.dsc.spos.json.cust.req.DCP_OrderTransferErpSetCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_OrderTransferErpSetCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_OrderTransferErpSetCreate
 * 服务说明：订单上传ERP白名单新增
 * @author jinzma 
 * @since  2020-12-03
 */
public class DCP_OrderTransferErpSetCreate extends SPosAdvanceService<DCP_OrderTransferErpSetCreateReq,DCP_OrderTransferErpSetCreateRes>{

	@Override
	protected void processDUID(DCP_OrderTransferErpSetCreateReq req, DCP_OrderTransferErpSetCreateRes res)
			throws Exception {
		// TODO 自动生成的方法存根
		String eId=req.geteId();
		String createBy = req.getOpNO();
		String createByName = req.getOpName();
		String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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

				//新增资料
				String[] columns = {
						"EID","SHOP","CREATEOPID","CREATEOPNAME","CREATETIME"
				};
				DataValue[]	insValue = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(shop, Types.VARCHAR), 
						new DataValue(createBy, Types.VARCHAR), 
						new DataValue(createByName, Types.VARCHAR), 
						new DataValue(createTime, Types.DATE), 
				};
				InsBean ib = new InsBean("DCP_ORDERTRANSFERERPSET", columns);
				ib.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib)); 
			}

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
	protected List<InsBean> prepareInsertData(DCP_OrderTransferErpSetCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderTransferErpSetCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderTransferErpSetCreateReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderTransferErpSetCreateReq req) throws Exception {
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
	protected TypeToken<DCP_OrderTransferErpSetCreateReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_OrderTransferErpSetCreateReq>(){};
	}

	@Override
	protected DCP_OrderTransferErpSetCreateRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_OrderTransferErpSetCreateRes();
	}

}
