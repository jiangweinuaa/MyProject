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
import com.dsc.spos.json.cust.req.DCP_OrderECPrintCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECPrintCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 出货单列印
 * @author yuanyy 2019-03-19
 *
 */
public class DCP_OrderECPrintCreate extends SPosAdvanceService<DCP_OrderECPrintCreateReq, DCP_OrderECPrintCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECPrintCreateReq req, DCP_OrderECPrintCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		
		//處理訂單以配送門店為歸屬門店
		
		String list[] = req.getShipmentNo();
		for (int i = 0; i < list.length; i++) {
			list[i] = list[i].replaceAll(list[i],"'" + list[i] + "'");
		}
		String shipmentNo = StringUtils.join(list,",");

		UptBean ub1 = new UptBean("OC_SHIPMENT");	
		//条件
		ub1.addCondition("EID",new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("SHIPMENTNO",new DataValue(shipmentNo, Types.VARCHAR,DataExpression.IN));
		//值
		ub1.addUpdateValue("PRINTCOUNT", new DataValue(1, Types.VARCHAR,DataExpression.UpdateSelf));
		
		this.addProcessData(new DataProcessBean(ub1));
		this.doExecuteDataToDB();
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECPrintCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECPrintCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECPrintCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECPrintCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String shipmentNoList[] = req.getShipmentNo();
		
		if (shipmentNoList.length == 0) 
		{
			errCt++;
			errMsg.append("货运单号不可为空值  ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECPrintCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECPrintCreateReq>(){};
	}

	@Override
	protected DCP_OrderECPrintCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECPrintCreateRes();
	}
	
}
