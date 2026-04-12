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
import com.dsc.spos.json.cust.req.DCP_PinPeiGoodsCheckReq;
import com.dsc.spos.json.cust.req.DCP_PinPeiGoodsCheckReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PinPeiGoodsCheckRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_PinPeiGoodsCheck
 * 服务说明：拼胚商品启用/禁用
 * @author jinzma 
 * @since  2020-07-13
 */
public class DCP_PinPeiGoodsCheck extends SPosAdvanceService<DCP_PinPeiGoodsCheckReq,DCP_PinPeiGoodsCheckRes>{

	@Override
	protected void processDUID(DCP_PinPeiGoodsCheckReq req, DCP_PinPeiGoodsCheckRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String modifyBy = req.getOpNO();
		String modifyByName = req.getOpName();
		String modifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String status = req.getRequest().getStatus();
		if (!status.equals("0"))    ///100启用,0禁用
			status="100";

		try 
		{
			List<level1Elm> pluList = req.getRequest().getPluList();
			for (level1Elm par:pluList)
			{
				String pluNo = par.getPluNo();
				
				UptBean ub = new UptBean("DCP_PINPEI_GOODS");
				ub.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				ub.addUpdateValue("LASTMODIOPID", new DataValue(modifyBy, Types.VARCHAR));
				ub.addUpdateValue("LASTMODIOPNAME", new DataValue(modifyByName, Types.VARCHAR));
				ub.addUpdateValue("LASTMODITIME", new DataValue(modifyTime, Types.DATE));

				// condition
				ub.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));

				this.addProcessData(new DataProcessBean(ub));
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
	protected List<InsBean> prepareInsertData(DCP_PinPeiGoodsCheckReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PinPeiGoodsCheckReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PinPeiGoodsCheckReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PinPeiGoodsCheckReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		List<level1Elm> pluList = req.getRequest().getPluList();
		String status = req.getRequest().getStatus(); 

		if(pluList==null || pluList.isEmpty())
		{
			errMsg.append("商品列表不能为空, ");
			isFail = true;
		}
		if(Check.Null(status))
		{
			errMsg.append("商品状态不能为空, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PinPeiGoodsCheckReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_PinPeiGoodsCheckReq>(){};
	}

	@Override
	protected DCP_PinPeiGoodsCheckRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_PinPeiGoodsCheckRes();
	}


}
