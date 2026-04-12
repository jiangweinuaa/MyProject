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
import com.dsc.spos.json.cust.req.DCP_PGoodsShopCreateReq;
import com.dsc.spos.json.cust.req.DCP_PGoodsShopCreateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PGoodsShopCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsShopCreate extends SPosAdvanceService<DCP_PGoodsShopCreateReq, DCP_PGoodsShopCreateRes>
{

	@Override
	protected void processDUID(DCP_PGoodsShopCreateReq req, DCP_PGoodsShopCreateRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		//先删除，后插入
		DelBean db1 = new DelBean("DCP_PGOODSCLASS_SHOP");
		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(req.getPluNO(), Types.VARCHAR));		

		this.addProcessData(new DataProcessBean(db1)); // 


		//DCP_PGOODSCLASS_SHOP
		String[] columnsModular = 
			{
					"EID",
					"PLUNO",
					"ORGANIZATIONNO",
					"UPDATE_TIME"
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
								new DataValue(req.getPluNO(), Types.VARCHAR), 
								new DataValue(par.getShopId(), Types.VARCHAR),
								new DataValue(mySysTime, Types.VARCHAR)				
						};

				InsBean ib1 = new InsBean("DCP_PGOODSCLASS_SHOP", columnsModular);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

			}
		}

		this.doExecuteDataToDB();		

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PGoodsShopCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PGoodsShopCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PGoodsShopCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PGoodsShopCreateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String PluNO =req.getPluNO();

		if (Check.Null(PluNO)) 
		{
			errMsg.append("套餐商品编码不可为空值, ");
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
	protected TypeToken<DCP_PGoodsShopCreateReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsShopCreateReq>(){};
	}

	@Override
	protected DCP_PGoodsShopCreateRes getResponseType() 
	{
		return new DCP_PGoodsShopCreateRes();
	}


}
