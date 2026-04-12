package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ElementUpdateReq;
import com.dsc.spos.json.cust.res.DCP_ElementUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 影响因素新增
 * @author yuanyy 
 *
 */
public class DCP_ElementUpdate extends SPosAdvanceService<DCP_ElementUpdateReq, DCP_ElementUpdateRes> {

	@Override
	protected void processDUID(DCP_ElementUpdateReq req, DCP_ElementUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			String eType = req.getRequest().getE_Type();
			String eNo = req.getRequest().geteNo();
			String eName = req.getRequest().geteName();
			String eRatio = req.getRequest().geteRatio();
			String status = req.getRequest().getStatus();
			
			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_ELEMENT");
			//add Value
			ub1.addUpdateValue("E_NAME", new DataValue(eName, Types.VARCHAR));
			ub1.addUpdateValue("E_RATIO", new DataValue(eRatio, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			//condition
			ub1.addCondition("E_TYPE", new DataValue(eType, Types.VARCHAR));
			ub1.addCondition("E_NO", new DataValue(eNo, Types.VARCHAR));	
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("SHOPID", new DataValue(req.getShopId(), Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
				
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ElementUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ElementUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ElementUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ElementUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    
	    //必传值不为空
	    String eType = req.getRequest().getE_Type();
		
	    if(Check.Null(eType)){
		   	errMsg.append("因素类型不能为空值 ");
		   	isFail = true;
		}
	    if(Check.Null(req.getRequest().geteNo())){
		   	errMsg.append("因素编码不能为空值 ");
		   	isFail = true;
		}
	    if(Check.Null(req.getRequest().geteRatio())){
		   	errMsg.append("影响比例不能为空值 ");
		   	isFail = true;
		}
	    
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ElementUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ElementUpdateReq>(){};
	}

	@Override
	protected DCP_ElementUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ElementUpdateRes();
	}
	

}
