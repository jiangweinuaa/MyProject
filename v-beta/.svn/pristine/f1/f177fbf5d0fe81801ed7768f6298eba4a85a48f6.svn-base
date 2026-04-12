package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ReasonMsgDeleteReq;
import com.dsc.spos.json.cust.res.DCP_ReasonMsgDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 原因码信息删除 2018-09-19
 * @author yuanyy
 *
 */
public class DCP_ReasonMsgDelete extends SPosAdvanceService<DCP_ReasonMsgDeleteReq, DCP_ReasonMsgDeleteRes> {

	@Override
	protected boolean isVerifyFail(DCP_ReasonMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//必传值不为空
		String BSNO = req.getRequest().getBsNo();
		String BSType = req.getRequest().getBsType();


		if(Check.Null(BSNO)){
			errMsg.append("原因码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(BSType)){
			errMsg.append("原因类型不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}
	/**
	 * 查询多语言信息
	 */
	@Override
	protected String getQuerySql(DCP_ReasonMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "select BSNO, Lang_Type from DCP_REASON_LANG "
				+ " where BSNO = '"+req.getRequest().getBsNo()+"' and BSTYPE = '"+req.getRequest().getBsType()+"' "
				+ " and EID = '"+req.geteId()+"'";
		return sql;
	}

	@Override
	protected void processDUID(DCP_ReasonMsgDeleteReq req, DCP_ReasonMsgDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		try 
		{
			String BSNO = req.getRequest().getBsNo();
			String BSType = req.getRequest().getBsType();
			String eId = req.geteId();

			sql = this.getQuerySql(req);	
			String[] condCountValues = { }; //查詢條件
			List<Map<String, Object>> getReasonLangDatas = this.doQueryData(sql, condCountValues);
			if(getReasonLangDatas != null)
			{
				for (Map<String, Object> oneData : getReasonLangDatas) 
				{
					String lBSNO = oneData.get("BSNO").toString();
					String langType = oneData.get("LANG_TYPE").toString();

					DelBean db2 = new DelBean("DCP_REASON_LANG");
					db2.addCondition("BSNO", new DataValue(lBSNO, Types.VARCHAR));
					db2.addCondition("LANG_TYPE", new DataValue(langType,Types.VARCHAR));
					db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					this.addProcessData(new DataProcessBean(db2));
				}
			}

			DelBean db1 = new DelBean("DCP_REASON");
			db1.addCondition("BSNO", new DataValue(BSNO, Types.VARCHAR));
			db1.addCondition("BSType", new DataValue(BSType,Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			this.doExecuteDataToDB();	
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}		

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ReasonMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ReasonMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ReasonMsgDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected TypeToken<DCP_ReasonMsgDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ReasonMsgDeleteReq>(){};
	}
	@Override
	protected DCP_ReasonMsgDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ReasonMsgDeleteRes();
	}

}
