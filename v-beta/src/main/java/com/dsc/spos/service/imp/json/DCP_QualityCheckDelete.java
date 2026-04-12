package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.JsonBasicRes;
import com.dsc.spos.json.cust.req.DCP_QualityCheckDeleteReq;
 
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * 结算日条件 DCP_SupLicenseApply
 * 
 * @date 2024-09-21
 * @author 01029
 */
public class DCP_QualityCheckDelete extends SPosAdvanceService<DCP_QualityCheckDeleteReq, JsonBasicRes> {

	/**
	 * 判断 信息时候已存在或重复
	 */
	private String isRepeat(String... key) {
		String sql = null;
		sql = " SELECT * FROM DCP_QUALITYCHECK WHERE EID='%s'   AND QCBILLNO='%s' ";
		sql = String.format(sql, key);
		return sql;
	}

	@Override
	protected void processDUID(DCP_QualityCheckDeleteReq req, JsonBasicRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			String billNo = null;
			String sql = null;

			List<DCP_QualityCheckDeleteReq.Detail1> detailLists = req.getRequest().getDeleteList();
			StringBuffer errMsg = new StringBuffer("");
			if (!SUtil.EmptyList(detailLists)) {
				for (DCP_QualityCheckDeleteReq.Detail1 par : detailLists) {
					billNo = par.getQcBillNo();
					sql = this.isRepeat(eId, billNo);
					List<Map<String, Object>> mDatasQry = this.doQueryData(sql, null);
					if (SUtil.EmptyList(mDatasQry)) {
						String status = (String) mDatasQry.get(0).get("STATUS");
						if (!"0".equals(status)) {
							errMsg.append("单号:" + billNo + "状态不是 检验中 不可更新！");
							continue;
						}
					}
					DelBean db1 = new DelBean("DCP_QUALITYCHECK");

					
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("QCBILLNO", new DataValue(billNo, Types.VARCHAR));

					this.addProcessData(new DataProcessBean(db1));
					this.doExecuteDataToDB();

					res.setSuccess(true);
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_QualityCheckDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_QualityCheckDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_QualityCheckDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_QualityCheckDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("request不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_QualityCheckDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_QualityCheckDeleteReq>() {
		};
	}

	@Override
	protected JsonBasicRes getResponseType() {
		// TODO Auto-generated method stub
		return new JsonBasicRes();
	}

}
