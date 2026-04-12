package com.dsc.spos.service.imp.json;

import java.math.RoundingMode;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderCreateCancle_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderSaleCancle_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderCreateCancle_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderSaleCancle_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderCreateCancle_Open extends SPosAdvanceService<DCP_OrderCreateCancle_OpenReq, DCP_OrderCreateCancle_OpenRes>
{

	@Override
	protected void processDUID(DCP_OrderCreateCancle_OpenReq req, DCP_OrderCreateCancle_OpenRes res) throws Exception {
		// TODO Auto-generated method stub
		//需要删除原单的信息
		String sql="select * from OC_order where EID='"+req.getRequest().geteId()+"' and orderno='"+req.getRequest().getOrderNo()+"' and load_doctype='"+req.getRequest().getLoad_docType()+"' ";
		List<Map<String, Object>> listdateList=this.doQueryData(sql, null);
		if(listdateList!=null&&!listdateList.isEmpty())
		{
			//直接把订单更新成一个别的状态
			//更新单头
			UptBean ub1 = null;	
			ub1 = new UptBean("OC_ORDER");
			
			//撤销单
			ub1.addUpdateValue("STATUS", new DataValue("N", Types.VARCHAR));

			//condition
			ub1.addCondition("EID", new DataValue(req.getRequest().geteId(), Types.VARCHAR));
			ub1.addCondition("orderno", new DataValue(req.getRequest().getOrderNo(), Types.VARCHAR));
			ub1.addCondition("load_doctype", new DataValue(req.getRequest().getLoad_docType(), Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();
		}
		else
		{
			res.setSuccess(false);
			res.setServiceDescription("订单不存在请重新确认！");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderCreateCancle_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderCreateCancle_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderCreateCancle_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderCreateCancle_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderCreateCancle_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderCreateCancle_OpenReq>() {};
	}

	@Override
	protected DCP_OrderCreateCancle_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderCreateCancle_OpenRes();
	}

}
