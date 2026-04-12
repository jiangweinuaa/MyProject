package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_NoticeDeleteReq;
import com.dsc.spos.json.cust.res.DCP_NoticeDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_NoticeDelete extends SPosAdvanceService<DCP_NoticeDeleteReq,DCP_NoticeDeleteRes>
{
	@Override
	protected void processDUID(DCP_NoticeDeleteReq req, DCP_NoticeDeleteRes res) throws Exception 
	{	

		try 
		{
			String eId = req.geteId();;
			String NoticeID = req.getRequest().getNoticeID();

			DelBean db1 = new DelBean("DCP_NOTICE_FILE");
			db1.addCondition("NOTICE_ID", new DataValue(NoticeID, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			DelBean db2 = new DelBean("DCP_NOTICE");
			db2.addCondition("NOTICE_ID", new DataValue(NoticeID, Types.VARCHAR));
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));

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
	protected List<InsBean> prepareInsertData(DCP_NoticeDeleteReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_NoticeDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_NoticeDeleteReq req) throws Exception 
	{
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_NoticeDeleteReq req) throws Exception 
	{		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		String NoticeID = req.getRequest().getNoticeID();

		if (Check.Null(NoticeID)) 
		{
			isFail = true;
			errCt++;
			errMsg.append("公告ID不可为空值, ");
		}  

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_NoticeDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_NoticeDeleteReq>(){};
	}

	@Override
	protected DCP_NoticeDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_NoticeDeleteRes();
	}
}
