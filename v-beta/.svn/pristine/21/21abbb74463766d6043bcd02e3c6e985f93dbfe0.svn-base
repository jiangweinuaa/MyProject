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
import com.dsc.spos.json.cust.req.DCP_PaymentShopCreateReq;
import com.dsc.spos.json.cust.req.DCP_PaymentShopCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PaymentShopCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PaymentShopCreate extends SPosAdvanceService<DCP_PaymentShopCreateReq, DCP_PaymentShopCreateRes> 
{

	@Override
	protected void processDUID(DCP_PaymentShopCreateReq req, DCP_PaymentShopCreateRes res) throws Exception
	{

		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());
		
		//先删除，后插入
		DelBean db1 = new DelBean("DCP_PAYFUNCNOINFO_SHOP");
		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db1.addCondition("PAYCODEPOS", new DataValue(req.getPayCodePOS(), Types.VARCHAR));

		this.addProcessData(new DataProcessBean(db1)); // 


		//DCP_PAYFUNCNOINFO_SHOP
		String[] columnsModular = 
			{
					"EID",
					"FUNCNO",
					"PAYCODE",
					"SHOPID",
					"UPDATE_TIME",
					"PAYCODEPOS"
			};


		List<level1Elm> jsonDatas=req.getDatas();

		if(jsonDatas!=null)
		{
			for (level1Elm par : jsonDatas) 
			{			

				DataValue[] insValue1 = null;

				insValue1 = new DataValue[] 
						{ 
								new DataValue(req.geteId(), Types.VARCHAR),
								new DataValue(req.getFuncNO(), Types.VARCHAR), 
								new DataValue(req.getPayCode(),Types.VARCHAR), 
								new DataValue(par.getShopId(), Types.VARCHAR),
								new DataValue(mySysTime, Types.VARCHAR),
								new DataValue(req.getPayCodePOS(), Types.VARCHAR),
						};

				InsBean ib1 = new InsBean("DCP_PAYFUNCNOINFO_SHOP", columnsModular);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			}
		}

		//更新单头
		UptBean ub1 = new UptBean("DCP_PAYFUNCNOINFO");		
		ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
		ub1.addCondition("PAYCODEPOS",new DataValue(req.getPayCodePOS(), Types.VARCHAR));
		
		ub1.addUpdateValue("SHOPTYPE",new DataValue(req.getShopType(), Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));	
		
		this.doExecuteDataToDB();		

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	
				
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PaymentShopCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PaymentShopCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PaymentShopCreateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PaymentShopCreateReq req) throws Exception 
	{
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String FUNCNO =req.getFuncNO();

		if (Check.Null(FUNCNO)) 
		{
			errMsg.append("POS支付编码不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(req.getShopType())) 
		{
			errMsg.append("适用门店类型不可为空值, ");
			isFail = true;
		} 

		List<level1Elm> jsonDatas=req.getDatas();

		for (level1Elm par : jsonDatas) 
		{			
			if (Check.Null(par.getShopId())) 
			{
				errMsg.append("门店编码不可为空值, ");
				isFail = true;
			}			
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
		
	}

	@Override
	protected TypeToken<DCP_PaymentShopCreateReq> getRequestType()
	{
		return new TypeToken<DCP_PaymentShopCreateReq>(){};
	}

	@Override
	protected DCP_PaymentShopCreateRes getResponseType() 
	{
		return new DCP_PaymentShopCreateRes();
	}
	
	
}
