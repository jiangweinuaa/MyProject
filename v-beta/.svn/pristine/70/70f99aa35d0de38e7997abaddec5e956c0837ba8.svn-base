package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PackChargeEnableReq;
import com.dsc.spos.json.cust.res.DCP_PackChargeEnableRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_PackChargeEnable extends SPosAdvanceService<DCP_PackChargeEnableReq, DCP_PackChargeEnableRes> {
    @Override
    protected void processDUID(DCP_PackChargeEnableReq req, DCP_PackChargeEnableRes res) throws Exception {
    	String status = "100";//状态：-1未启用100已启用 0已禁用
		
		if(req.getRequest().getOprType().equals("1"))//操作类型：1-启用2-禁用
		{
			status = "100";
		}
		else
		{
			status = "0";
		}
		
		UptBean up1 = new UptBean("DCP_PACKCHARGE");
		up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
		up1.addCondition("PACKPLUNO", new DataValue(req.getRequest().getPackPluNo(), Types.VARCHAR));					
		up1.addUpdateValue("STATUS", new DataValue(status, Types.NUMERIC));
		this.addProcessData(new DataProcessBean(up1));
		this.doExecuteDataToDB();		
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PackChargeEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PackChargeEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PackChargeEnableReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PackChargeEnableReq req) throws Exception {
    	boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String packPluNo = req.getRequest().getPackPluNo();
		String oprType = req.getRequest().getOprType();
		if (Check.Null(packPluNo)) {
			errMsg.append("打包商品编码packPluNo不能为空值， ");
			isFail = true;
		}
		if (Check.Null(oprType)) {
			errMsg.append("操作类型oprType不能为空值， ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
    }

    @Override
    protected TypeToken<DCP_PackChargeEnableReq> getRequestType() {
        return new TypeToken<DCP_PackChargeEnableReq>() {};
    }

    @Override
    protected DCP_PackChargeEnableRes getResponseType() {
        return new DCP_PackChargeEnableRes();
    }
}
