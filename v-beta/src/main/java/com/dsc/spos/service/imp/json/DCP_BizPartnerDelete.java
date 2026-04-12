package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_BizPartnerDeleteReq;
 
import com.dsc.spos.json.cust.res.DCP_BizPartnerRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 结算日条件 DCP_BizPartner
 * 
 * @date 2024-09-21
 * @author 01029
 */
public class DCP_BizPartnerDelete extends SPosAdvanceService<DCP_BizPartnerDeleteReq, DCP_BizPartnerRes> {

	@Override
	protected void processDUID(DCP_BizPartnerDeleteReq req, DCP_BizPartnerRes res) throws Exception {
		// TODO Auto-generated method stub
		try {

			String eId = req.geteId();
			String bizPartnerNo = req.getRequest().getBizPartnerNo();
			isExist(req);
			DelBean db1 = new DelBean("DCP_BizPartner");
			DelBean db2 = new DelBean("DCP_BIZPARTNER_ORG");
			DelBean db3 = new DelBean("DCP_BIZPARTNER_CONTACT");
			DelBean db4 = new DelBean("DCP_BIZPARTNER_BILL");
			DelBean db5 = new DelBean("DCP_SUPPLIER_LICENSE");
			//DelBean db6 = new DelBean("DCP_BIZPARTNER_CONTACT");
			DelBean db7 = new DelBean("DCP_TAGTYPE_DETAIL");
			DelBean db8 = new DelBean("DCP_CUSTOMER_SHOP");
//			DelBean db9 = new DelBean("DCP_CUSTOMER_RANGE");

			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));

			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));

			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));

			db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db4.addCondition("BIZPARTNERNO", new DataValue(bizPartnerNo, Types.VARCHAR));

			db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db5.addCondition("SUPPLIER", new DataValue(bizPartnerNo, Types.VARCHAR));

			db7.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db7.addCondition("ID", new DataValue(bizPartnerNo, Types.VARCHAR));

			db8.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db8.addCondition("CUSTOMERNO", new DataValue(bizPartnerNo, Types.VARCHAR));

//			db9.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//			db9.addCondition("CUSTOMERNO", new DataValue(bizPartnerNo, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(db1));
			this.addProcessData(new DataProcessBean(db2));
			this.addProcessData(new DataProcessBean(db3));
			this.addProcessData(new DataProcessBean(db4));
			this.addProcessData(new DataProcessBean(db5));
			this.addProcessData(new DataProcessBean(db7));
			this.addProcessData(new DataProcessBean(db8));
//			this.addProcessData(new DataProcessBean(db9));
			this.doExecuteDataToDB();

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_BizPartnerDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_BizPartnerDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_BizPartnerDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_BizPartnerDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("request不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String iCode = req.getRequest().getBizPartnerNo();

		if (Check.Null(iCode)) {
			errMsg.append("对象编号不能为空值  ");
			isFail = true;
		}
 
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}
	
	private boolean isExist(DCP_BizPartnerDeleteReq req) throws Exception {
		boolean isFail = false;
		String iCode = req.getRequest().getBizPartnerNo();
		StringBuffer errMsg = new StringBuffer("");
		String sql = " SELECT * FROM DCP_BIZPARTNER WHERE EID='%s'   AND BIZPARTNERNO='%s'  ";
		sql = String.format(sql, req.geteId(), iCode);
		List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
		if (mDatas != null && mDatas.size() > 0)
			for (Map<String, Object> oneData1 : mDatas) {
				if (!"-1".equals(oneData1.get("STATUS").toString())) {
					errMsg.append("状态非【-1.未启用】不可删除！");
					isFail = true;
				}

			}
		else {
			errMsg.append("当前对象编号不存在 。");
			isFail = true;
		}
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
    }

	@Override
	protected TypeToken<DCP_BizPartnerDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_BizPartnerDeleteReq>() {
		};
	}

	@Override
	protected DCP_BizPartnerRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_BizPartnerRes();
	}

}
