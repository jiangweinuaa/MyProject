package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SeriesDeleteReq;
import com.dsc.spos.json.cust.req.DCP_SeriesDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_SeriesDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 系列编码删除服务 2018-09-27
 * @author yuanyy
 *
 */
public class DCP_SeriesDelete extends SPosAdvanceService<DCP_SeriesDeleteReq, DCP_SeriesDeleteRes> {

	@Override
	protected void processDUID(DCP_SeriesDeleteReq req, DCP_SeriesDeleteRes res) throws Exception {
		// TODO Auto-generated method stub		
		try 
		{
			String eId = req.geteId();
			for (level1Elm par : req.getRequest().getSeriesNoList())
			{
				String seriesNO = par.getSeriesNo();
				
				DelBean db1 = new DelBean("DCP_SERIES");
				db1.addCondition("SERIESNO", new DataValue(seriesNO, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				DelBean db2 = new DelBean("DCP_SERIES_LANG");
				db2.addCondition("SERIESNO", new DataValue(seriesNO, Types.VARCHAR));
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));
				
			}
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常:"+e.getMessage());

		}
		
	
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SeriesDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SeriesDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SeriesDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  
		if(req.getRequest()==null)
    {
    	errMsg.append("requset不能为空值 ");
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }
		for (level1Elm par : req.getRequest().getSeriesNoList())
		{
			String seriesNO = par.getSeriesNo();
			if (Check.Null(seriesNO)) {
				isFail = true;
				errCt++;
				errMsg.append("系列编码不可为空值, ");
			}  
			
		}
		

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SeriesDeleteReq> getRequestType() {
		// TODO Auto-generated method stub0
		return new TypeToken<DCP_SeriesDeleteReq>(){};
	}

	@Override
	protected DCP_SeriesDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SeriesDeleteRes();
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SeriesDeleteReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}
	
}
