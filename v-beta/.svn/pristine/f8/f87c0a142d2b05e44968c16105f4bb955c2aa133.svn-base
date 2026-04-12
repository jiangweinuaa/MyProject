package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_LangLabelDeleteReq;
import com.dsc.spos.json.cust.res.DCP_LangLabelDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

/**
 * 批量删除多语言标签
 * @author yuanyy 2019-01-17 
 *
 */
public class DCP_LangLabelDelete extends SPosAdvanceService<DCP_LangLabelDeleteReq, DCP_LangLabelDeleteRes> {

	@Override
	protected void processDUID(DCP_LangLabelDeleteReq req, DCP_LangLabelDeleteRes res) throws Exception {
		// TODO Auto-generated method stub

		String[] labelIDList = req.getRequest().getLabelID();
		try 
		{
			for (int i =0;i<labelIDList.length;i++) 
			{
				DelBean db1 = new DelBean("PLATFORM_LANG_LABEL");
				db1.addCondition("LABELID", new DataValue(labelIDList[i],Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
			}
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
	protected List<InsBean> prepareInsertData(DCP_LangLabelDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_LangLabelDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_LangLabelDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_LangLabelDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		//必传值不为空
		String[] labelID = req.getRequest().getLabelID();

		if(labelID == null || labelID.length == 0){
			errMsg.append("多语言标签编码不可为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400 , errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_LangLabelDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_LangLabelDeleteReq>(){};
	}

	@Override
	protected DCP_LangLabelDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_LangLabelDeleteRes();
	}

}
