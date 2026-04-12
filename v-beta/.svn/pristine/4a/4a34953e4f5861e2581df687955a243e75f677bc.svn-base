package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LangLabelUpdateReq;
import com.dsc.spos.json.cust.res.DCP_LangLabelUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 多语言标签新增 
 * @author yuanyy 2019-01-17 
 *
 */
public class DCP_LangLabelUpdate extends SPosAdvanceService<DCP_LangLabelUpdateReq, DCP_LangLabelUpdateRes> {

	@Override
	protected void processDUID(DCP_LangLabelUpdateReq req, DCP_LangLabelUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{	
			String labelID = req.getRequest().getLabelID();
			String label_cn = req.getRequest().getLabel_cn();
			String label_tw = req.getRequest().getLabel_tw();
			String label_en = req.getRequest().getLabel_en();

			String opNO = req.getOpNO();
			Calendar cal = Calendar.getInstance();// 获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String lastModifyTime = df.format(cal.getTime());

			UptBean ub1 = null;	
			ub1 = new UptBean("PLATFORM_LANG_LABEL");
			//add Value
			ub1.addUpdateValue("LABEL_CN", new DataValue(label_cn, Types.VARCHAR));
			ub1.addUpdateValue("LABEL_TW", new DataValue(label_tw, Types.VARCHAR));
			ub1.addUpdateValue("LABEL_EN", new DataValue(label_en, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(opNO, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastModifyTime, Types.VARCHAR));
			//condition
			ub1.addCondition("LABELID", new DataValue(labelID, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_LangLabelUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_LangLabelUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_LangLabelUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_LangLabelUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String labelID = req.getRequest().getLabelID();
		String label_cn = req.getRequest().getLabel_cn();
		String label_tw = req.getRequest().getLabel_tw();
		String label_en = req.getRequest().getLabel_en();

		if (Check.Null(labelID)) 
		{
			errMsg.append("多语言标签编码不能为空值 ");
			isFail = true;
		}

		if( Check.Null(label_cn) && Check.Null(label_tw) && Check.Null(label_en)){
			errMsg.append("请至少添加一条多语言信息");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_LangLabelUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_LangLabelUpdateReq>(){};
	}

	@Override
	protected DCP_LangLabelUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_LangLabelUpdateRes();
	}

}
