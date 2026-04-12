package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PowerUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PowerUpdateReq.levelFunction;
import com.dsc.spos.json.cust.req.DCP_PowerUpdateReq.levelModular;
import com.dsc.spos.json.cust.res.DCP_PowerUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PowerUpdate extends SPosAdvanceService<DCP_PowerUpdateReq,DCP_PowerUpdateRes> {

	@Override
	protected void processDUID(DCP_PowerUpdateReq req, DCP_PowerUpdateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String opGroup=req.getRequest().getOpGroup();
		List<DCP_PowerUpdateReq.levelModular> listModular=req.getRequest().getModularPower();
		List<DCP_PowerUpdateReq.levelFunction> listFunction=req.getRequest().getFunctionPower();

		//region 先删除ModularNO 1-2级模块
		DelBean db1 = new DelBean("PLATFORM_BILLPOWER");
		db1.addCondition("OPGROUP", new DataValue(opGroup, Types.VARCHAR));
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));				
		//endregion

		//region 再增加1-2模块
		for(levelModular itemModular : listModular)
		{
			//再增加
			String powerType=itemModular.getPowerType();
			String modular=itemModular.getModularNo();
			int i_powerType=1;
			if(powerType==null||powerType.length()==0)
			{
				i_powerType=1;
			}
			else
			{
				i_powerType=Integer.parseInt(powerType);
			}
			String[] columns1 = {"OPGROUP","MODULARNO","POWERTYPE","STATUS","EID"};
			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(opGroup, Types.VARCHAR),
					new DataValue(modular, Types.VARCHAR),
					new DataValue(i_powerType, Types.INTEGER),		
					new DataValue("100", Types.VARCHAR),
					new DataValue(eId, Types.VARCHAR),								
			};

			InsBean ib1 = new InsBean("PLATFORM_BILLPOWER", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));
		}
		
		//region 先删除PLATFORM_POWER  BY JZMA 2018/4/9
		DelBean db2 = new DelBean("PLATFORM_POWER");
		db2.addCondition("OPGROUP", new DataValue(opGroup, Types.VARCHAR));
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));
		//endregion

		//region 再增加functionNO
		for(levelFunction itemFunction : listFunction)
		{
			String powerType=itemFunction.getPowerType();
			String functionno=itemFunction.getFunctionNo();
			int i_powerType=1;
			if(powerType==null||powerType.length()==0)
			{
				i_powerType=1;
			}
			else
			{
				i_powerType=Integer.parseInt(powerType);
			}
			String[] columns1 = {"OPGROUP","FUNCNO","POWERTYPE","STATUS","EID"};
			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(opGroup, Types.VARCHAR),
					new DataValue(functionno, Types.VARCHAR),
					new DataValue(i_powerType, Types.INTEGER),		
					new DataValue("100", Types.VARCHAR),
					new DataValue(eId, Types.VARCHAR),
			};

			InsBean ib1 = new InsBean("PLATFORM_POWER", columns1);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));			
		}
		//endregion

		//不需要判断节点modularPower、functionPower有没有数据，没有数据 就是删除权限
		this.doExecuteDataToDB();			
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PowerUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PowerUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PowerUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PowerUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if (Check.Null(req.getRequest().getOpGroup())) 
		{
			errCt++;
			errMsg.append("角色不可为空值, ");
			isFail = true;
		} 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PowerUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PowerUpdateReq>(){};
	}

	@Override
	protected DCP_PowerUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PowerUpdateRes();
	}
}
