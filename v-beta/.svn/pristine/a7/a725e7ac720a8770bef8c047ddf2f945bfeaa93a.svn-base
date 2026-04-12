package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsBrandDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsBrandDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_GoodsBrandDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 删除商品品牌 2018-10-19
 * @author yuanyy
 *
 */
public class DCP_GoodsBrandDelete extends SPosAdvanceService<DCP_GoodsBrandDeleteReq, DCP_GoodsBrandDeleteRes> {

	@Override
	protected void processDUID(DCP_GoodsBrandDeleteReq req, DCP_GoodsBrandDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		String eId = req.geteId();
		for (level1Elm par : req.getRequest().getBrandNoList())
		{
		
			String brandNO = par.getBrandNo();
			DelBean db1 = new DelBean("DCP_BRAND");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("BRANDNO", new DataValue(brandNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_BRAND_LANG");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("BRANDNO", new DataValue(brandNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
		}
		
		
		this.doExecuteDataToDB();
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");	
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsBrandDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsBrandDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsBrandDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		List<DelBean> data = new ArrayList<DelBean>();
		
	
		
		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsBrandDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    
	    if(req.getRequest()==null)
	    {
	    	errMsg.append("requset不能为空值 ");
	    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	    }
	    for (level1Elm par : req.getRequest().getBrandNoList())
			{
	    	String brandNO = par.getBrandNo();
		    
		    if(Check.Null(brandNO)){
			   	errMsg.append("编码不能为空值 ");
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
	protected TypeToken<DCP_GoodsBrandDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsBrandDeleteReq>(){};
	}

	@Override
	protected DCP_GoodsBrandDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsBrandDeleteRes();
	}
	
}
