package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsCategoryDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsCategoryDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsCategoryDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsCategoryDelete extends SPosAdvanceService<DCP_GoodsCategoryDeleteReq, DCP_GoodsCategoryDeleteRes> {

	@Override
	protected void processDUID(DCP_GoodsCategoryDeleteReq req, DCP_GoodsCategoryDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try 
		{
			String eId = req.geteId();
			for (level1Elm par : req.getRequest().getCategoryList()) 
			{
				String category =par.getCategory();				
				DelBean db1 = new DelBean("DCP_CATEGORY");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));
				//db1.addCondition("CATEGORYTYPE", new DataValue(categoryType, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				DelBean db3 = new DelBean("DCP_CATEGORY_LANG");
				db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db3.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));
				//db1.addCondition("CATEGORYTYPE", new DataValue(categoryType, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db3));				
				
				DelBean db4 = new DelBean("DCP_CATEGORY_IMAGE");
				db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db4.addCondition("CATEGORY", new DataValue(category, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db4));
			}
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		}
		catch (Exception e) 
		{
		// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常:"+e.getMessage());	
	
		}
			
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsCategoryDeleteReq req) throws Exception {
		
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsCategoryDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		List<level1Elm> stausList = req.getRequest().getCategoryList();

		if (stausList == null || stausList.isEmpty())
		{
			errMsg.append("分类不可为空, ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for (level1Elm par : stausList)
		{
			if (Check.Null(par.getCategory()))
			{
				errMsg.append("分类编码不可为空值, ");
				isFail = true;
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsCategoryDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsCategoryDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsCategoryDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsCategoryDeleteRes();
	}

}
