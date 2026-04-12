package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_EInvoiceTempShopUpdateReq;
import com.dsc.spos.json.cust.req.DCP_EInvoiceTempShopUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_EInvoiceTempShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_EInvoiceTempShopUpdate extends SPosAdvanceService<DCP_EInvoiceTempShopUpdateReq, DCP_EInvoiceTempShopUpdateRes> {

	@Override
	protected void processDUID(DCP_EInvoiceTempShopUpdateReq req, DCP_EInvoiceTempShopUpdateRes res) throws Exception {
	// TODO Auto-generated method stub
		String eId = req.geteId();
		String templateNo = req.getRequest().getTemplateNo();
		
		try 
		{
			DelBean db1 = new DelBean("DCP_einvoiceset_shop");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("PTEMPLATENO", new DataValue(templateNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			if(req.getRequest().getDatas()!=null&&req.getRequest().getDatas().size()>0)
			{
				List<level1Elm> getDataDetail = req.getRequest().getDatas();
				String[] columns = {"EID","PTEMPLATENO","SHOPID","STATUS"};
				for (level1Elm oneData : getDataDetail) 
				{
					if(oneData.getShopId()==null)
					{
						continue;
					}
					
					DataValue[] insValue = new DataValue[]
							{
									new DataValue(eId, Types.VARCHAR),
									new DataValue(templateNo, Types.VARCHAR),
									new DataValue(oneData.getShopId(), Types.VARCHAR),
									new DataValue("100", Types.VARCHAR)
							};
					
					InsBean ib1 = new InsBean("DCP_einvoiceset_shop", columns);
					ib1.addValues(insValue);
					this.addProcessData(new DataProcessBean(ib1));	
				}
				
			}

			
			this.doExecuteDataToDB();	
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
	protected List<InsBean> prepareInsertData(DCP_EInvoiceTempShopUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_EInvoiceTempShopUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_EInvoiceTempShopUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_EInvoiceTempShopUpdateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuilder errMsg = new StringBuilder();
		
		if(Check.Null(req.getRequest().getTemplateNo()))
		{
			isFail = true;
			errMsg.append("模板编号不能为空！");
		}
		
		if(isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
	return isFail;
	
	}

	@Override
	protected TypeToken<DCP_EInvoiceTempShopUpdateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_EInvoiceTempShopUpdateReq>(){};
	}

	@Override
	protected DCP_EInvoiceTempShopUpdateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_EInvoiceTempShopUpdateRes();
	}

}
