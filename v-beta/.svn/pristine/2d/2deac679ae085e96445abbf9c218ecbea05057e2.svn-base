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
import com.dsc.spos.json.cust.req.DCP_NoticeProcessReq;
import com.dsc.spos.json.cust.res.DCP_NoticeProcessRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_NoticeProcess extends SPosAdvanceService<DCP_NoticeProcessReq,DCP_NoticeProcessRes>
{
	@Override
	protected void processDUID(DCP_NoticeProcessReq req, DCP_NoticeProcessRes res) throws Exception 
	{
		Calendar cal = Calendar.getInstance();//获得当前时间
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		String ISSUE_DATE = df.format(cal.getTime());
		df=new SimpleDateFormat("HHmmss");
		String ISSUE_TIME = df.format(cal.getTime());

		if(req.getRequest().getStatus().equals("1"))
		{
			//更新
			UptBean ub1 = new UptBean("DCP_NOTICE");			
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("NOTICE_ID",new DataValue(req.getRequest().getNoticeID(), Types.VARCHAR));

			ub1.addUpdateValue("STATUS",new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
			ub1.addUpdateValue("ISSUEBY",new DataValue(req.getOpNO(), Types.VARCHAR));
			ub1.addUpdateValue("ISSUE_DATE",new DataValue(ISSUE_DATE, Types.VARCHAR));
			ub1.addUpdateValue("ISSUE_TIME",new DataValue(ISSUE_TIME, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

		}
		else if(req.getRequest().getStatus().equals("2"))
		{
			//更新
			UptBean ub1 = new UptBean("DCP_NOTICE");			
			ub1.addCondition("EID",new DataValue(req.geteId(), Types.VARCHAR));
			ub1.addCondition("NOTICE_ID",new DataValue(req.getRequest().getNoticeID(), Types.VARCHAR));

			ub1.addUpdateValue("STATUS",new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
			ub1.addUpdateValue("CANCELBY",new DataValue(req.getOpNO(), Types.VARCHAR));
			ub1.addUpdateValue("CANCEL_DATE",new DataValue(ISSUE_DATE, Types.VARCHAR));
			ub1.addUpdateValue("CANCEL_TIME",new DataValue(ISSUE_TIME, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

		}
		else 
		{

			String[] columnsNOTICE_READBY ={"EID","NOTICE_ID","OPNO"};
			DataValue[] insValue1 = null;

			insValue1 = new DataValue[] 
					{ 							
							new DataValue(req.geteId(), Types.VARCHAR),
							new DataValue(req.getRequest().getNoticeID(), Types.VARCHAR),
							new DataValue(req.getOpNO(), Types.VARCHAR),							
					};

			InsBean ib1 = new InsBean("DCP_NOTICE_READBY", columnsNOTICE_READBY);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

		}	

		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_NoticeProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_NoticeProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_NoticeProcessReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_NoticeProcessReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		String NoticeID = req.getRequest().getNoticeID();
		String 	Status=	req.getRequest().getStatus();
		if (Check.Null(NoticeID)) 
		{
			isFail = true;
			errCt++;
			errMsg.append("公告ID不可为空值, ");
		}  

		if (Check.Null(Status)) 
		{
			isFail = true;
			errCt++;
			errMsg.append("状态不可为空值, ");
		}  


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_NoticeProcessReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_NoticeProcessReq>(){};
	}

	@Override
	protected DCP_NoticeProcessRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_NoticeProcessRes();
	}

}
