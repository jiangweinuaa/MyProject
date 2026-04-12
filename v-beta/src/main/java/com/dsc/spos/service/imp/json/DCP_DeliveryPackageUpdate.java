package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DeliveryPackageUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DeliveryPackageUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 货运包裹新增
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_DeliveryPackageUpdate extends SPosAdvanceService<DCP_DeliveryPackageUpdateReq, DCP_DeliveryPackageUpdateRes> {

	@Override
	protected void processDUID(DCP_DeliveryPackageUpdateReq req, DCP_DeliveryPackageUpdateRes res) throws Exception 
	{
		// TODO Auto-generated method stub
		String sql = null;
		try 
		{
			String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String eId = req.geteId();
			String packageNo = req.getRequest().getPackageNo();
			String packageName = req.getRequest().getPackageName();
			String measureNo = req.getRequest().getMeasureNo();
			String measureName = req.getRequest().getMeasureName();
			String temperateNo = req.getRequest().getTemperateNo();
			String temperateName = req.getRequest().getTemperateName();
			String packageFee = req.getRequest().getPackageFee();
			String status = req.getRequest().getStatus();
			
			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_SHIPPACKAGESET");
			//Value
			ub1.addUpdateValue("PACKAGENAME", new DataValue(packageName, Types.VARCHAR));
			ub1.addUpdateValue("MEASURENO", new DataValue(measureNo, Types.VARCHAR));
			ub1.addUpdateValue("MEASURENAME", new DataValue(measureName, Types.VARCHAR));
			ub1.addUpdateValue("TEMPERATENO", new DataValue(temperateNo, Types.VARCHAR));
			ub1.addUpdateValue("TEMPERATENAME", new DataValue(temperateName, Types.VARCHAR));
			ub1.addUpdateValue("PACKAGEFEE", new DataValue(packageFee, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));			
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			// condition	
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("PACKAGENO", new DataValue(packageNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();	
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DeliveryPackageUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DeliveryPackageUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DeliveryPackageUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DeliveryPackageUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String packageNO = req.getRequest().getPackageNo();
		String packageName = req.getRequest().getPackageName();
		if(Check.Null(packageNO))
		{
			errCt++;
			errMsg.append("包裹编号不可为空值, ");
			isFail = true;
		}
		if(Check.Null(packageName))
		{
			errCt++;
			errMsg.append("包裹编号不可为空值, ");
			isFail = true;
		}
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DeliveryPackageUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DeliveryPackageUpdateReq>(){};
	}

	@Override
	protected DCP_DeliveryPackageUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DeliveryPackageUpdateRes();
	}
	
	
}
