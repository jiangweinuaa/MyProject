package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SStockInECSReq;
import com.dsc.spos.json.cust.req.DCP_SStockInECSReq.levelElm;
import com.dsc.spos.json.cust.res.DCP_SStockInECSRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服務函數：SStockInECSDCP
 * 服务说明：自采通知单结案
 * @author JINZMA 
 * @since  2019-07-03
 */
public class DCP_SStockInECS  extends SPosAdvanceService<DCP_SStockInECSReq,DCP_SStockInECSRes> {

	@Override
	protected void processDUID(DCP_SStockInECSReq req, DCP_SStockInECSRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();		
		String shopId = req.getShopId();
		levelElm request = req.getRequest();
		String receivingNO=request.getOfNo();

		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String confirmDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String confirmTime = df.format(cal.getTime());

		try
		{
			//更新单头
			UptBean ub = new UptBean("DCP_RECEIVING");

			//add Value
			ub.addUpdateValue("CONFIRMBY", new DataValue(req.getOpNO(), Types.VARCHAR));
			ub.addUpdateValue("CONFIRM_DATE", new DataValue(confirmDate, Types.VARCHAR));
			ub.addUpdateValue("CONFIRM_TIME", new DataValue(confirmTime, Types.VARCHAR));
			ub.addUpdateValue("STATUS", new DataValue("7", Types.VARCHAR));
			ub.addUpdateValue("PROCESS_STATUS", new DataValue("N", Types.VARCHAR));
            ub.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));
            ub.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()), Types.VARCHAR));




			//condition
			ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub.addCondition("ORGANIZATIONNO", new DataValue(shopId, Types.VARCHAR));
			ub.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));		
			ub.addCondition("RECEIVINGNO", new DataValue(receivingNO, Types.VARCHAR));	
			ub.addCondition("STATUS", new DataValue("6", Types.VARCHAR));

			this.addProcessData(new DataProcessBean(ub));
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SStockInECSReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SStockInECSReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SStockInECSReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SStockInECSReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		levelElm request = req.getRequest();
		//必传值不为空
		String ofNO = request.getOfNo();
		if(Check.Null(ofNO)){
			errMsg.append("来源单号不可为空值, ");
			isFail = true;
		}

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    

		return isFail;
	}

	@Override
	protected TypeToken<DCP_SStockInECSReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_SStockInECSReq>(){};
	}

	@Override
	protected DCP_SStockInECSRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_SStockInECSRes();
	}


}
