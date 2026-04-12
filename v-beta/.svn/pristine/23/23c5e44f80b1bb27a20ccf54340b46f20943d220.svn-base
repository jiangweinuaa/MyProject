package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DeliveryPackageCreateReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryPackageCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 货运包裹新增
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_DeliveryPackageDelete extends SPosAdvanceService<DCP_DeliveryPackageCreateReq, DCP_DeliveryPackageCreateRes> {

	@Override
	protected void processDUID(DCP_DeliveryPackageCreateReq req, DCP_DeliveryPackageCreateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		
		this.doDelete(req);	
		
		this.doExecuteDataToDB();
		
		if(res.isSuccess())
		{
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DeliveryPackageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DeliveryPackageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DeliveryPackageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		List<DelBean> data = new ArrayList<DelBean>();
		String eId = req.geteId();
		String packageNO = req.getRequest().getPackageNo().toString();
		DelBean db1 = new DelBean("DCP_SHIPPACKAGESET");
		db1.addCondition("PACKAGENO", new DataValue(packageNO, Types.VARCHAR));
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		data.add(db1);	
		return data;
		
	}

	@Override
	protected boolean isVerifyFail(DCP_DeliveryPackageCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
	    if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
	    String packageNO = req.getRequest().getPackageNo();
	    if(Check.Null(packageNO))
    	{
	      errCt++;
	      errMsg.append("包裹编号不可为空值, ");
	      isFail = true;
	    }
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DeliveryPackageCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DeliveryPackageCreateReq>(){};
	}

	@Override
	protected DCP_DeliveryPackageCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DeliveryPackageCreateRes();
	}
	
	
	
	
	
}
