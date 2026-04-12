package com.dsc.spos.service.imp.json;

import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TouchMenuShopUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TouchMenuShopUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_TouchMenuShopUpdateRes;

public class DCP_TouchMenuShopUpdate extends SPosAdvanceService<DCP_TouchMenuShopUpdateReq,DCP_TouchMenuShopUpdateRes >{

	@Override
	protected void processDUID(DCP_TouchMenuShopUpdateReq req, DCP_TouchMenuShopUpdateRes res) throws Exception {
	// TODO 自动生成的方法存根
		StringBuffer errMsg = new StringBuffer("");
		String eId = req.geteId();
		String menuNO = req.getMenuNO();
		DataValue[] insValue = null;
		try 
		{
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {eId,menuNO}; //查詢條件
			List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);
			if(getQData!=null && !getQData.isEmpty())
			{
				//删除原有单身
				DelBean db1 = new DelBean("DCP_TOUCHMENU_SHOP");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("MENUNO", new DataValue(menuNO, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				List<DCP_TouchMenuShopUpdateReq.level1Elm> jsonDatas = req.getDatas();
				for (level1Elm par : jsonDatas) {
					String[] columns = {
							"EID", "MENUNO","SHOPID","STATUS" };
					String shopId=par.getShopId();

					insValue = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(menuNO, Types.VARCHAR), 
							new DataValue(shopId, Types.VARCHAR),
							new DataValue("100", Types.VARCHAR),								
					};
					InsBean ib = new InsBean("DCP_TOUCHMENU_SHOP", columns);
					ib.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib)); //			
				}
		
				this.doExecuteDataToDB();		
			}
			else
			{
				errMsg.append("菜单编号不存在，请重新输入！");
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
			
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TouchMenuShopUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TouchMenuShopUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TouchMenuShopUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TouchMenuShopUpdateReq req) throws Exception {
	// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (Check.Null(req.getMenuNO())) 
		{
			errMsg.append("菜单编号不可为空值, ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_TouchMenuShopUpdateReq> getRequestType() {
	// TODO 自动生成的方法存根
	return new TypeToken<DCP_TouchMenuShopUpdateReq>(){};
	}

	@Override
	protected DCP_TouchMenuShopUpdateRes getResponseType() {
	// TODO 自动生成的方法存根
		return new DCP_TouchMenuShopUpdateRes();
	}

	@Override
	protected String getQuerySql(DCP_TouchMenuShopUpdateReq req) throws Exception {
		String sql = null;
		sql= " select *  from DCP_TOUCHMENU  where EID= ? and MENUNO = ?  ";
		return sql;
	}
	
}
