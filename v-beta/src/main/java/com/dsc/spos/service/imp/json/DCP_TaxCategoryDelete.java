package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TaxCategoryDeleteReq;
import com.dsc.spos.json.cust.req.DCP_TaxCategoryDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_TaxCategoryDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TaxCategoryDelete extends SPosAdvanceService<DCP_TaxCategoryDeleteReq, DCP_TaxCategoryDeleteRes> {

	@Override
	protected void processDUID(DCP_TaxCategoryDeleteReq req, DCP_TaxCategoryDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
	
		
		String eId = req.geteId();
		for (level1Elm par : req.getRequest().getTaxCodeList())
		{
			String taxCode = par.getTaxCode();
            String taxArea = par.getTaxArea();
            DelBean db1 = new DelBean("DCP_TAXCATEGORY");
			db1.addCondition("TAXCODE", new DataValue(taxCode,Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db1.addCondition("TAXAREA", new DataValue(taxArea, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_TAXCATEGORY_LANG");
			db2.addCondition("TAXCODE", new DataValue(taxCode,Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            db2.addCondition("TAXAREA", new DataValue(taxArea, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
		}
		this.doExecuteDataToDB();
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TaxCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TaxCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TaxCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TaxCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }

		for (level1Elm par : req.getRequest().getTaxCodeList())
		{
			String taxCode = par.getTaxCode();
            String taxArea = par.getTaxArea();

            if (Check.Null(taxCode))
			{
				errMsg.append("税别编码不可为空值, ");
				isFail = true;
			}
            if (Check.Null(taxArea))
            {
                errMsg.append("税区不可为空值, ");
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
	protected TypeToken<DCP_TaxCategoryDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TaxCategoryDeleteReq>(){};
	}

	@Override
	protected DCP_TaxCategoryDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TaxCategoryDeleteRes();
	}

}
