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
import com.dsc.spos.json.cust.req.DCP_PGoodsDetailUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PGoodsDetailUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_PGoodsDetailUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PGoodsDetailUpdate extends SPosAdvanceService<DCP_PGoodsDetailUpdateReq, DCP_PGoodsDetailUpdateRes>
{

	@Override
	protected void processDUID(DCP_PGoodsDetailUpdateReq req, DCP_PGoodsDetailUpdateRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String mySysTime = df.format(cal.getTime());

		//先删除，后插入
		DelBean db1 = new DelBean("DCP_PGOODSCLASS_DETAIL");
		db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		db1.addCondition("PLUNO", new DataValue(req.getPluNO(), Types.VARCHAR));		
		db1.addCondition("PCLASSNO", new DataValue(req.getPclassNO(), Types.VARCHAR));		

		this.addProcessData(new DataProcessBean(db1)); // 


		//DCP_PGOODSCLASS_DETAIL
		String[] columnsModular = 
			{
					"EID",
					"PLUNO",
					"PCLASSNO",
					"DPLUNO",
					"DUNIT",
					"INVOWAY",
					"QTY",
					"ISSEL",
					"EXTRAAMT",
					"STATUS",
					"UPDATE_TIME",
					"PRIORITY"
			};


		List<level1Elm> jsonDatas=req.getDatas();

		if(jsonDatas!=null)
		{
			for (level1Elm par : jsonDatas) 
			{			

				int priority_i = 0;
				try
				{
					priority_i = Integer.parseInt(par.getPriority());		
				} 
				catch (Exception e) 
				{
				// TODO: handle exception
					priority_i = 0;
				}
				
				DataValue[] insValue1 = null;

				insValue1 = new DataValue[] 
						{ 
								new DataValue(req.geteId(), Types.VARCHAR),
								new DataValue(req.getPluNO(), Types.VARCHAR), 
								new DataValue(req.getPclassNO(), Types.VARCHAR),
								new DataValue(par.getDetaiPluNO(), Types.VARCHAR),
								new DataValue(par.getDetailUnit(), Types.VARCHAR),
								new DataValue(par.getInvoWay(), Types.VARCHAR),
								new DataValue(par.getQty(), Types.VARCHAR),
								new DataValue(par.getIsSel(), Types.VARCHAR),
								new DataValue(par.getExtraAmt(), Types.VARCHAR),
								new DataValue("100", Types.VARCHAR),
								new DataValue(mySysTime, Types.VARCHAR),
								new DataValue(priority_i, Types.INTEGER)
						};

				InsBean ib1 = new InsBean("DCP_PGOODSCLASS_DETAIL", columnsModular);
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
	protected List<InsBean> prepareInsertData(DCP_PGoodsDetailUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PGoodsDetailUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PGoodsDetailUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PGoodsDetailUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		String PluNO =req.getPluNO();
		String PclassNO =req.getPclassNO();
		
		if (Check.Null(PluNO)) 
		{
			errMsg.append("套餐商品编码不可为空值, ");
			isFail = true;
		}
		
		if (Check.Null(PclassNO)) 
		{
			errMsg.append("套餐类别编码不可为空值, ");
			isFail = true;
		} 	
		
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_PGoodsDetailUpdateReq> getRequestType() 
	{
		return new TypeToken<DCP_PGoodsDetailUpdateReq>(){};
	}

	@Override
	protected DCP_PGoodsDetailUpdateRes getResponseType() 
	{
		return new DCP_PGoodsDetailUpdateRes();
	}

}
