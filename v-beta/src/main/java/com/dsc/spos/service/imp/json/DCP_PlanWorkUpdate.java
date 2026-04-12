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
import com.dsc.spos.json.cust.req.DCP_PlanWorkUpdateReq;
import com.dsc.spos.json.cust.res.DCP_PlanWorkUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 修改班次信息 2018-10-10
 * @author yuanyy
 *
 */
public class DCP_PlanWorkUpdate extends SPosAdvanceService<DCP_PlanWorkUpdateReq, DCP_PlanWorkUpdateRes> {

	@Override
	protected void processDUID(DCP_PlanWorkUpdateReq req, DCP_PlanWorkUpdateRes res) throws Exception {
		// TODO Auto-generated method stub

		try 
		{
			String workNO = req.getRequest().getWorkNo();
			String workName = req.getRequest().getWorkName();
			String bTime = req.getRequest().getbTime();
			String eTime = req.getRequest().geteTime();
			String status = req.getRequest().getStatus();
			String eId = req.geteId();

			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String updateTime = df.format(cal.getTime());

			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_PLANWORK");
			//add Value
			ub1.addUpdateValue("WORKNAME", new DataValue(workName, Types.VARCHAR));
			ub1.addUpdateValue("BTIME", new DataValue(bTime, Types.VARCHAR));
			ub1.addUpdateValue("ETIME", new DataValue(eTime, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			ub1.addUpdateValue("UPDATE_TIME", new DataValue(updateTime, Types.VARCHAR));
			//condition
			ub1.addCondition("WORKNO", new DataValue(workNO, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PlanWorkUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PlanWorkUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PlanWorkUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PlanWorkUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		//必传值不为空
		String workNO = req.getRequest().getWorkNo();
		if(Check.Null(workNO))
		{
			errCt++;
			errMsg.append("班次编码不能为空值！！ ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400 , errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_PlanWorkUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PlanWorkUpdateReq>(){};
	}

	@Override
	protected DCP_PlanWorkUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PlanWorkUpdateRes();
	}

}
