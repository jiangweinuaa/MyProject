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
import com.dsc.spos.json.cust.req.DCP_CustomerTagGroupUpdateReq;
import com.dsc.spos.json.cust.req.DCP_CustomerTagGroupUpdateReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_CustomerTagGroupUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 客户标签
 * @author yuanyy
 *
 */
public class DCP_CustomerTagGroupDelete extends SPosAdvanceService<DCP_CustomerTagGroupUpdateReq, DCP_CustomerTagGroupUpdateRes> {

	@Override
	protected void processDUID(DCP_CustomerTagGroupUpdateReq req, DCP_CustomerTagGroupUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		try {
			String tagGroupNo = req.getRequest().getTagGroupNo();
			
			DelBean db1 = new DelBean("DCP_CUSTOMERTAGGROUP_DETAIL");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("TAGGROUPNO", new DataValue(tagGroupNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_CUSTOMERTAGGROUP");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("TAGGROUPNO", new DataValue(tagGroupNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			this.doExecuteDataToDB();
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceDescription("服务执行失败！");
			res.setSuccess(false);
			res.setServiceStatus("200");
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_CustomerTagGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_CustomerTagGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_CustomerTagGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_CustomerTagGroupUpdateReq req) throws Exception {
		// TODO Auto-generated method stub

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		String tagGroupNo = req.getRequest().getTagGroupNo();

		if (Check.Null(tagGroupNo)) {
			errCt++;
			errMsg.append("标签组编码不可为空值, ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_CustomerTagGroupUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_CustomerTagGroupUpdateReq>(){};
	}

	@Override
	protected DCP_CustomerTagGroupUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_CustomerTagGroupUpdateRes();
	}
		
	
}
