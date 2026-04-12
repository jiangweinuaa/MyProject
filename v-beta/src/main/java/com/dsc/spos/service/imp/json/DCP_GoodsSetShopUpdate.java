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
import com.dsc.spos.json.cust.req.DCP_GoodsSetShopUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetShopUpdate extends SPosAdvanceService<DCP_GoodsSetShopUpdateReq, DCP_GoodsSetShopUpdateRes>
{
	@Override
	protected void processDUID(DCP_GoodsSetShopUpdateReq req, DCP_GoodsSetShopUpdateRes res) throws Exception 
	{
		
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		//
		res.setSuccess(false);
		res.setServiceStatus("100");
		res.setServiceDescription("服务执行失败！");			
		
		//更新单头
		UptBean ub1 = new UptBean("DCP_GOODS_SHOP");			
		ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
		ub1.addCondition("ORGANIZATIONNO", new DataValue(req.getSELSHOP(), Types.VARCHAR));
		ub1.addCondition("PLUNO", new DataValue(req.getPLUNO(), Types.VARCHAR));

		ub1.addUpdateValue("FSOD",new DataValue(req.getFSOD(), Types.VARCHAR)); 
		ub1.addUpdateValue("FPSO",new DataValue(req.getFPSO(), Types.VARCHAR)); 
		ub1.addUpdateValue("FPSP",new DataValue(req.getFPSP(), Types.VARCHAR)); 
		ub1.addUpdateValue("FSBA",new DataValue(req.getFSBA(), Types.VARCHAR)); 
		ub1.addUpdateValue("FSAL",new DataValue(req.getFSAL(), Types.VARCHAR)); 
		ub1.addUpdateValue("COUNTERNO",new DataValue(req.getCOUNTERNO(), Types.VARCHAR)); 
		ub1.addUpdateValue("WARNING_QTY",new DataValue(req.getWARNING_QTY(), Types.FLOAT)); 
		ub1.addUpdateValue("MIN_QTY",new DataValue(req.getMIN_QTY(), Types.FLOAT)); 
		ub1.addUpdateValue("MUL_QTY",new DataValue(req.getMUL_QTY(), Types.FLOAT)); 
		ub1.addUpdateValue("MAX_QTY",new DataValue(req.getMAX_QTY(), Types.FLOAT)); 
		ub1.addUpdateValue("TAXCODE",new DataValue(req.getTAXCODE(), Types.VARCHAR)); 
		ub1.addUpdateValue("SAFE_QTY",new DataValue(req.getSAFE_QTY(), Types.FLOAT)); 
		ub1.addUpdateValue("STYPE",new DataValue(req.getSTYPE(), Types.VARCHAR)); 
		ub1.addUpdateValue("SUPPLIER",new DataValue(req.getSUPPLIER(), Types.VARCHAR)); 
		ub1.addUpdateValue("IS_AUTO_SUBTRACT",new DataValue(req.getIS_AUTO_SUBTRACT(), Types.VARCHAR)); 
		ub1.addUpdateValue("STATUS",new DataValue(req.getStatus(), Types.VARCHAR)); 
		ub1.addUpdateValue("UPDATE_TIME",new DataValue(mySysTime, Types.VARCHAR)); 

		this.addProcessData(new DataProcessBean(ub1));			


		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsSetShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetShopUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetShopUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String pluno =req.getPLUNO();
		String selshop =req.getSELSHOP();
		

		if (Check.Null(pluno)) 
		{
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(selshop)) 
		{
			errMsg.append("门店编码不可为空值, ");
			isFail = true;
		} 
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
		
	}

	@Override
	protected TypeToken<DCP_GoodsSetShopUpdateReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetShopUpdateReq>(){};
	}

	@Override
	protected DCP_GoodsSetShopUpdateRes getResponseType() 
	{
		return new DCP_GoodsSetShopUpdateRes();
	}
	
	
	
}
