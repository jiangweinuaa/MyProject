package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_PackChargeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_PackChargeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import cn.hutool.core.collection.CollectionUtil;

import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DCP_PackChargeDelete extends SPosAdvanceService<DCP_PackChargeDeleteReq, DCP_PackChargeDeleteRes> {
    @Override
    protected void processDUID(DCP_PackChargeDeleteReq req, DCP_PackChargeDeleteRes res) throws Exception {
    	String eId = req.geteId();
    	String packPluNo = req.getRequest().getPackPluNo();
    	String sql = "SELECT STATUS FROM DCP_PACKCHARGE where EID = ? and PACKPLUNO = ?";
    	String[] conditionValues = {eId, packPluNo};
    	List<Map<String, Object>> list = this.doQueryData(sql, conditionValues);
    	if(CollectionUtil.isEmpty(list) || list.get(0).get("STATUS").toString().equals("100")) {
    		throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "请先禁用");
    	}
    	
    	DelBean db1 = new DelBean("DCP_PACKCHARGE");
    	db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
    	db1.addCondition("PACKPLUNO", new DataValue(packPluNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));
		
		DelBean db2 = new DelBean("DCP_PACKCHARGE_GOODS");
		db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db2.addCondition("PACKPLUNO", new DataValue(packPluNo, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db2));
		
		this.doExecuteDataToDB();		
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
    }

    @Override
    protected List<InsBean> prepareInsertData(DCP_PackChargeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<UptBean> prepareUpdateData(DCP_PackChargeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected List<DelBean> prepareDeleteData(DCP_PackChargeDeleteReq req) throws Exception {
        return null;
    }

    @Override
    protected boolean isVerifyFail(DCP_PackChargeDeleteReq req) throws Exception {
    	boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		String packPluNo = req.getRequest().getPackPluNo();
		if (Check.Null(packPluNo)) {
			errMsg.append("打包商品编码packPluNo不能为空值， ");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
    }

    @Override
    protected TypeToken<DCP_PackChargeDeleteReq> getRequestType() {
        return new TypeToken<DCP_PackChargeDeleteReq>() {};
    }

    @Override
    protected DCP_PackChargeDeleteRes getResponseType() {
        return new DCP_PackChargeDeleteRes();
    }
}
