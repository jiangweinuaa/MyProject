package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_QuotationDeleteReq;
import com.dsc.spos.json.cust.res.DCP_QuotationDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 报价单
 * @author yuanyy
 *
 */
public class DCP_QuotationDelete extends SPosAdvanceService<DCP_QuotationDeleteReq, DCP_QuotationDeleteRes> {

	@Override
	protected void processDUID(DCP_QuotationDeleteReq req, DCP_QuotationDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			String shopId = req.getRequest().getShopId();
			String quotationRecordNo = req.getRequest().getQuotationRecordNo();
			
			DelBean db1 = new DelBean("DCP_QUOTATIONRECORD_DETAIL");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("QUOTATIONRECORDNO", new DataValue(quotationRecordNo, Types.VARCHAR));
			
			if(!Check.Null(shopId)){
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			}
			
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_QUOTATIONRECORD");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("QUOTATIONRECORDNO", new DataValue(quotationRecordNo, Types.VARCHAR));
			if(!Check.Null(shopId)){
				db2.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			}
			
			this.addProcessData(new DataProcessBean(db2));
			
			this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败！");
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_QuotationDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_QuotationDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_QuotationDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_QuotationDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		String quotationRecordNo = req.getRequest().getQuotationRecordNo();

		if (Check.Null(quotationRecordNo)) {
			errCt++;
			errMsg.append("报价单号不可为空值, ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_QuotationDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_QuotationDeleteReq>(){};
	}

	@Override
	protected DCP_QuotationDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_QuotationDeleteRes();
	}
	
}
