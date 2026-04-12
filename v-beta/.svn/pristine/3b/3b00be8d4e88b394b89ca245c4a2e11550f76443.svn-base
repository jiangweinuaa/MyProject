package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_TicketStyleDeleteReq;
import com.dsc.spos.json.cust.res.DCP_TicketStyleDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_TicketStyleDelete 
 * 服务说明：企业小票删除
 * @author wangzyc
 * @since 2020-12-3
 */
public class DCP_TicketStyleDelete extends SPosAdvanceService<DCP_TicketStyleDeleteReq, DCP_TicketStyleDeleteRes> {

	@Override
	protected void processDUID(DCP_TicketStyleDeleteReq req, DCP_TicketStyleDeleteRes res) throws Exception {
		// TODO 自动生成的方法存根
		String eId = req.geteId();
		String styleId = req.getRequest().getStyleId();
		try {
			if(checkExist(req)){
				// DCP_TICKETSTYLE
				DelBean db1 = new DelBean("DCP_TICKETSTYLE");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("STYLEID", new DataValue(styleId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				this.doExecuteDataToDB();

				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");
			}
			else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败：要删除的企业小票不存在，请重新操作!");	
			}
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TicketStyleDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TicketStyleDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TicketStyleDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TicketStyleDeleteReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if (Check.Null(req.getRequest().getStyleId())) {
			errMsg.append("小票样式模板不可为空值,");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TicketStyleDeleteReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TicketStyleDeleteReq>() {
		};
	}

	@Override
	protected DCP_TicketStyleDeleteRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TicketStyleDeleteRes();
	}

	/**
	 * 判断要删除的企业小票样式是否存在
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private boolean checkExist(DCP_TicketStyleDeleteReq req) throws Exception {
		String sql = null;
		boolean exist = false;

		sql = "SELECT * FROM DCP_TICKETSTYLE WHERE eid = '" + req.geteId() + "' AND STYLEID = '"
				+ req.getRequest().getStyleId() + "'";

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			exist = true;
		}

		return exist;
	}

}
