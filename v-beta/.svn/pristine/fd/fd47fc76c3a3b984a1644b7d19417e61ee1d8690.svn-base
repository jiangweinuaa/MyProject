package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_OrderECPickupPrintCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECPickupPrintCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 电商订单拣货打印
 * @author yuanyy 2019-04-23
 *
 */
public class DCP_OrderECPickupPrintCreate extends SPosAdvanceService<DCP_OrderECPickupPrintCreateReq, DCP_OrderECPickupPrintCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECPickupPrintCreateReq req, DCP_OrderECPickupPrintCreateRes res)
			throws Exception {
		// TODO Auto-generated method stub
		
		String eId = req.geteId();
		
		//處理訂單以配送門店為歸屬門店
		
		String list[] = req.getEcOrderNo();
		for (int i = 0; i < list.length; i++) {
			list[i] = list[i].replaceAll(list[i],"'" + list[i] + "'");
		}
		String ecOrderNo = StringUtils.join(list,",");

		UptBean ub1 = new UptBean("OC_ORDER");	
		//条件
		ub1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("ORDERNO",new DataValue(ecOrderNo, Types.VARCHAR,DataExpression.IN));
		//值
		ub1.addUpdateValue("PICKUPDOCPRINT", new DataValue("Y", Types.VARCHAR));
		
		this.addProcessData(new DataProcessBean(ub1));
		this.doExecuteDataToDB();
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECPickupPrintCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECPickupPrintCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECPickupPrintCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECPickupPrintCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECPickupPrintCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECPickupPrintCreateReq>(){};
	}

	@Override
	protected DCP_OrderECPickupPrintCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECPickupPrintCreateRes();
	}
	
}
