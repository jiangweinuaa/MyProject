package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_StockOutNoticeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_StockOutNoticeRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.ColumnDataValue;
import com.dsc.spos.utils.DataBeans;
import com.dsc.spos.utils.SUtil;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DCP_StockOutNoticeDelete
 * 
 * @date 2024-09-21
 * @author 01029
 */
public class DCP_StockOutNoticeDelete extends SPosAdvanceService<DCP_StockOutNoticeDeleteReq, DCP_StockOutNoticeRes> {

	/**
	 * 判断 信息时候已存在或重复
	 */
	private String isRepeat(String... key) {
		String sql = null;
		sql = " SELECT * FROM DCP_STOCKOUTNOTICE WHERE EID='%s'   AND BILLNO='%s' ";
		sql = String.format(sql, key);
		return sql;
	}

	@Override
	protected void processDUID(DCP_StockOutNoticeDeleteReq req, DCP_StockOutNoticeRes res) throws Exception {
		// TODO Auto-generated method stub
		//try {
			String eId = req.geteId();
			String billNo = req.getRequest().getBillNo();
			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String sql = null;
			sql = this.isRepeat(eId, billNo);

			List<Map<String, Object>> mDatas = this.doQueryData(sql, null);
			if (!SUtil.EmptyList(mDatas)) {
				if (!"0".equals(mDatas.get(0).get("STATUS").toString())) {
					res.setSuccess(false);
					res.setServiceStatus("200");
					res.setServiceDescription("服务执行失败: 单据状态非【0-新建】不可删除 ");
					return;
				}

				DelBean db1 = new DelBean("DCP_STOCKOUTNOTICE");
				DelBean db2 = new DelBean("DCP_STOCKOUTNOTICE_DETAIL");

				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));

				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db2.addCondition("BILLNO", new DataValue(billNo, Types.VARCHAR));

				this.addProcessData(new DataProcessBean(db1));
				this.addProcessData(new DataProcessBean(db2));


				//删除内部交易
				ColumnDataValue condition1 = new ColumnDataValue();
				condition1.add("EID", req.geteId());
				condition1.add("BILLNO", req.getRequest().getBillNo());
				this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_DETAIL", condition1)));
				this.addProcessData(new DataProcessBean(DataBeans.getDelBean("DCP_INTERSETTLE_ROUTE", condition1)));


				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}

		//} catch (Exception e) {
			// TODO: handle exception
		//	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		//}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockOutNoticeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockOutNoticeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockOutNoticeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StockOutNoticeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (req.getRequest() == null) {
			errMsg.append("request不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String iCode = req.getRequest().getBillNo();

		if (Check.Null(iCode)) {
			errMsg.append("单据编号不能为空值 ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockOutNoticeDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockOutNoticeDeleteReq>() {
		};
	}

	@Override
	protected DCP_StockOutNoticeRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockOutNoticeRes();
	}

}
