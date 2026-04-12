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
import com.dsc.spos.json.cust.req.DCP_GoodsSetGoodpriceShopCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetGoodpriceUpdateReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSetGoodpriceShopCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetGoodpriceShopCreate extends SPosAdvanceService<DCP_GoodsSetGoodpriceShopCreateReq, DCP_GoodsSetGoodpriceShopCreateRes>
{

	@Override
	protected void processDUID(DCP_GoodsSetGoodpriceShopCreateReq req, DCP_GoodsSetGoodpriceShopCreateRes res)
			throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		//
		res.setSuccess(false);
		res.setServiceStatus("100");
		res.setServiceDescription("服务执行失败！");			

		List<DCP_GoodsSetGoodpriceShopCreateReq.level1Elm> shopdatas =req.getDatas();

		//DCP_GOODS_PRICE_ORG
		String[] columnsModular2 = 
			{
					"EID",
					"ORGANIZATIONNO",
					"PLUNO",
					"UNIT",
					"TRADE_TYPE",
					"TRADE_OBJECT",
					"PRICE",
					"STATUS",
					"UPDATE_TIME"
			};

		//删除，DCP_GOODS_PRICE_ORG
		DelBean db1 = new DelBean("DCP_GOODS_PRICE_ORG");
		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(req.getPluNO(), Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1)); // 
		
		for(DCP_GoodsSetGoodpriceShopCreateReq.level1Elm par: shopdatas)
		{
			//插入
			DataValue[] insValue2 = null;
			insValue2 = new DataValue[] 
					{ 
							new DataValue(req.geteId(), Types.VARCHAR),
							new DataValue(par.getShopId(), Types.VARCHAR),
							new DataValue(req.getPluNO(), Types.VARCHAR), 
							new DataValue(req.getUnitNO(), Types.VARCHAR),
							new DataValue("0", Types.VARCHAR),
							new DataValue(req.getSupplierNO(), Types.VARCHAR),
							new DataValue(par.getShopGoodPrice(), Types.FLOAT),
							new DataValue("100", Types.VARCHAR),
							new DataValue(mySysTime, Types.VARCHAR)				
					};

			InsBean ib2 = new InsBean("DCP_GOODS_PRICE_ORG", columnsModular2);
			ib2.addValues(insValue2);
			this.addProcessData(new DataProcessBean(ib2)); // 新增單頭

		}


		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsSetGoodpriceShopCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetGoodpriceShopCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetGoodpriceShopCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetGoodpriceShopCreateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");


		String pluno =req.getPluNO();
		String SupplierNO= req.getSupplierNO();

		List<DCP_GoodsSetGoodpriceShopCreateReq.level1Elm> shopdatas =req.getDatas();

		if (Check.Null(pluno)) 
		{
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
		} 


		if (Check.Null(SupplierNO)) 
		{
			errMsg.append("供货组织编码不可为空值, ");
			isFail = true;
		} 

		if (shopdatas==null) 
		{
			errMsg.append("门店编码不可为空值, ");
			isFail = true;
		} 
		else
		{			
			for(DCP_GoodsSetGoodpriceShopCreateReq.level1Elm par: shopdatas)
			{
				if (Check.Null(par.getShopId())) 
				{
					errMsg.append("适用门店编码不可为空值, ");
					isFail = true;
				} 

				if (PosPub.isNumericType(par.getShopGoodPrice())==false) 
				{
					errMsg.append("特殊供货价必须是数字类型, ");
					isFail = true;
				} 

			}
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsSetGoodpriceShopCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_GoodsSetGoodpriceShopCreateReq>(){};
	}

	@Override
	protected DCP_GoodsSetGoodpriceShopCreateRes getResponseType() 
	{
		return new DCP_GoodsSetGoodpriceShopCreateRes();
	}


}
