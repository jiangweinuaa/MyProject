package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SquadUpdateReq;
import com.dsc.spos.json.cust.res.DCP_SquadUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SquadUpdate extends SPosAdvanceService<DCP_SquadUpdateReq,DCP_SquadUpdateRes> {

	@Override
	protected void processDUID(DCP_SquadUpdateReq req, DCP_SquadUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		 
	    String shopId = req.getRequest().getShopId();
	    String offOPNO = req.getRequest().getOffOPNo();
	    String machine = req.getRequest().getMachine();
	    String squadNo = req.getRequest().getSquadNo();
	    String bDate = req.getRequest().getbDate();
		String eId = req.geteId();
		
		UptBean ub1 = null;	
		ub1 = new UptBean("DCP_SQUAD");
		//add Value
		ub1.addUpdateValue("ENDSQUAD", new DataValue("Y", Types.VARCHAR));
		//condition
		ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		ub1.addCondition("OPNO", new DataValue(offOPNO, Types.VARCHAR));
		ub1.addCondition("MACHINE", new DataValue(machine, Types.VARCHAR));
		ub1.addCondition("SQUADNO", new DataValue(squadNo, Types.VARCHAR));
		ub1.addCondition("BDATE", new DataValue(bDate, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
		this.addProcessData(new DataProcessBean(ub1));
		this.doExecuteDataToDB();
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SquadUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SquadUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SquadUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SquadUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    
	    String shopId = req.getRequest().getShopId();
	    String offOPNO = req.getRequest().getOffOPNo();
	    String machine = req.getRequest().getMachine();
	    String squadNo = req.getRequest().getSquadNo();
	    String bDate = req.getRequest().getbDate();
	    
	    if(Check.Null(shopId)){
		   	errMsg.append("门店编号不能为空值 ");
		   	isFail = true;
		}
	    if(Check.Null(offOPNO)){
		   	errMsg.append("用户编号不能为空值 ");
		   	isFail = true;
		}
	    if(Check.Null(machine)){
		   	errMsg.append("机台编号不能为空值 ");
		   	isFail = true;
		}
	    if(Check.Null(squadNo)){
		   	errMsg.append("班次编号不能为空值 ");
		   	isFail = true;
		}
	    if(Check.Null(bDate)){
		   	errMsg.append("营业日期不能为空值 ");
		   	isFail = true;
		}
	    
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SquadUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SquadUpdateReq>(){};
	}

	@Override
	protected DCP_SquadUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SquadUpdateRes();
	}
		
}
