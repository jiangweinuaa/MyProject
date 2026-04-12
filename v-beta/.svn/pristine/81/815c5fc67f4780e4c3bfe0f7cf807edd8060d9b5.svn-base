package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DinnerTimeUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DinnerTimeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 餐段新增
 * @author yuanyy 2019-09-18
 *
 */
public class DCP_DinnerTimeUpdate extends SPosAdvanceService<DCP_DinnerTimeUpdateReq, DCP_DinnerTimeUpdateRes> {

	@Override
	protected void processDUID(DCP_DinnerTimeUpdateReq req, DCP_DinnerTimeUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String shopId = req.getShopId();
		
		try {
			String dtNo = req.getRequest().getDtNo();
			String dtName = req.getRequest().getDtName();
			String beginTime = req.getRequest().getBeginTime();
			String endTime = req.getRequest().getEndTime();
			String status = req.getRequest().getStatus();
			String createBy = req.getOpNO();
			String workNo = req.getRequest().getWorkNo();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createDateTime = dfDate.format(cal.getTime());
			
			String priority = req.getRequest().getPriority()== null?"0":req.getRequest().getPriority().toString();
			
			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_DINNERTIME");
			//add Value
			ub1.addUpdateValue("DTNAME", new DataValue(dtName, Types.VARCHAR));
			ub1.addUpdateValue("BEGIN_TIME", new DataValue(beginTime, Types.VARCHAR));
			ub1.addUpdateValue("END_TIME", new DataValue(endTime, Types.VARCHAR));
			ub1.addUpdateValue("CREATEBY", new DataValue(createBy, Types.VARCHAR));
			ub1.addUpdateValue("CREATE_DATETIME", new DataValue(createDateTime, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			
			if(req.getRequest().getPriority() != null){
				ub1.addUpdateValue("PRIORITY", new DataValue(priority, Types.VARCHAR));
			}
			
			//condition
			ub1.addCondition("DTNO", new DataValue(dtNo, Types.VARCHAR));
			ub1.addCondition("WORKNO", new DataValue(workNo, Types.VARCHAR));
			ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			this.addProcessData(new DataProcessBean(ub1));
			this.doExecuteDataToDB();				
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败:" + e.getMessage());	
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DinnerTimeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DinnerTimeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DinnerTimeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DinnerTimeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String dtNo = req.getRequest().getDtNo();
		String dtName = req.getRequest().getDtName();
		String beginTime = req.getRequest().getBeginTime();
		String endTime = req.getRequest().getEndTime();

		if (Check.Null(dtNo)) 
		{
			errCt++;
			errMsg.append("餐段编号不可为空值, ");
			isFail = true;
		}
		if (Check.Null(dtName)) 
		{
			errCt++;
			errMsg.append("餐段名称不可为空值, ");
			isFail = true;
		} 

		if (Check.Null(beginTime)) 
		{
			errCt++;
			errMsg.append("开始时间不可为空值, ");
			isFail = true;
		} 
		if (Check.Null(endTime)) 
		{
			errCt++;
			errMsg.append("结束时间不可为空值, ");
			isFail = true;
		} 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DinnerTimeUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DinnerTimeUpdateReq>(){};
	}

	@Override
	protected DCP_DinnerTimeUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DinnerTimeUpdateRes();
	}

}
