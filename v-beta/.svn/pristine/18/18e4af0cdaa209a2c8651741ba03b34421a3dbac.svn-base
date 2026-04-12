package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECDCUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECDCUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 大物流信息新增
 * @author yuanyy 2019-03-27
 *
 */
public class DCP_OrderECDCUpdate extends SPosAdvanceService<DCP_OrderECDCUpdateReq , DCP_OrderECDCUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderECDCUpdateReq req, DCP_OrderECDCUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			String dcNo = req.getDcNo();
			String dcName = req.getDcName();
			String lgplatformNo = req.getLgPlatformNo();
			String lgplatformName = req.getLgPlatformName();
			String dcContactman = req.getDcContactman();
			String dcPhone = req.getDcPhone();
			String dcAddress = req.getDcAddress();
			String status = req.getStatus();

			UptBean ub1 = null;	
			ub1 = new UptBean("OC_LOGISTICS_DC");
			//add Value
			ub1.addUpdateValue("DCNAME", new DataValue(dcName, Types.VARCHAR));
			ub1.addUpdateValue("LGPLATFORMNO", new DataValue(lgplatformNo, Types.VARCHAR));
			ub1.addUpdateValue("LGPLATFORMNAME", new DataValue(lgplatformName, Types.VARCHAR));
			ub1.addUpdateValue("DCCONTACTMAN", new DataValue(dcContactman, Types.VARCHAR));
			ub1.addUpdateValue("DCPHONE", new DataValue(dcPhone, Types.VARCHAR));
			ub1.addUpdateValue("DCADDRESS", new DataValue(dcAddress, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));

			//condition
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("DCNO", new DataValue(dcNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");

		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECDCUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECDCUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECDCUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECDCUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String dcNo = req.getDcNo();

		if (Check.Null(dcNo)) 
		{
			errMsg.append("大物流中心编码不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECDCUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECDCUpdateReq>(){};
	}

	@Override
	protected DCP_OrderECDCUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECDCUpdateRes();
	}

}
