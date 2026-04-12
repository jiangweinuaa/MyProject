package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ServiceChargeUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ServiceChargeUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ServiceChargeUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务费修改
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_ServiceChargeUpdate extends SPosAdvanceService<DCP_ServiceChargeUpdateReq, DCP_ServiceChargeUpdateRes> {

	@Override
	protected void processDUID(DCP_ServiceChargeUpdateReq req, DCP_ServiceChargeUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId(); 
			String serviceChargeNO = req.getRequest().getServiceChargeNo();
			String scType = req.getRequest().getScType();

			String limitShop = req.getRequest().getLimitShop();
			String opNO = req.getOpNO();
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dfTime = new SimpleDateFormat("HHmmss");
			String modifyDate = dfDate.format(cal.getTime());
			String modifyTime = dfTime.format(cal.getTime());
			String status = req.getRequest().getStatus();

			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_SERVICECHARGE");
			ub1.addUpdateValue("SCTYPE", new DataValue(scType, Types.INTEGER));
			ub1.addUpdateValue("LIMIT_SHOP", new DataValue(limitShop, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
			ub1.addUpdateValue("MODIFYBY", new DataValue(opNO,Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_DATE", new DataValue(modifyDate,Types.VARCHAR));
			ub1.addUpdateValue("MODIFY_TIME", new DataValue(modifyTime,Types.VARCHAR));

			SimpleDateFormat dfStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String udTime = dfStamp.format(new Date());		

			ub1.addUpdateValue("UPDATE_TIME", new DataValue(udTime,Types.VARCHAR));

			ub1.addCondition("SERVICECHARGENO", new DataValue(serviceChargeNO, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			DelBean db1 = new DelBean("DCP_SERVICECHARGE_DETAIL");
			db1.addCondition("SERVICECHARGENO", new DataValue(serviceChargeNO, Types.VARCHAR));
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));		

			List<level1Elm> datas = req.getRequest().getDatas();
			//			if(datas.isEmpty())
			//			{
			//				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "服务费详细信息为空!!");
			//			}
			for (level1Elm par : datas) {
				int insColCt = 0;  
				String[] columnsName = {
						"EID","SERVICECHARGENO","SCTYPE","SPNO","SCRATE"
				};

				DataValue[] columnsVal = new DataValue[columnsName.length];
				for (int i = 0; i < columnsVal.length; i++) { 
					String keyVal = null;
					switch (i) { 
					case 0:
						keyVal = eId;
						break;
					case 1:
						keyVal = serviceChargeNO;
						break;
					case 2:
						keyVal = scType;
						break;
					case 3:
						keyVal = par.getSpNo().toString();
						break;
					case 4:
						keyVal = par.getScrate();
						break;
					default:
						break;
					}
					if (keyVal != null) 
					{
						insColCt++;
						columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
					} 
					else 
					{
						columnsVal[i] = null;
					}

				}
				String[] columns2  = new String[insColCt];
				DataValue[] insValue2 = new DataValue[insColCt];
				// 依照傳入參數組譯要insert的欄位與數值；
				insColCt = 0;

				for (int i=0;i<columnsVal.length;i++){
					if (columnsVal[i] != null){
						columns2[insColCt] = columnsName[i];
						insValue2[insColCt] = columnsVal[i];
						insColCt ++;
						if (insColCt >= insValue2.length) 
							break;
					}
				}
				InsBean ib2 = new InsBean("DCP_SERVICECHARGE_DETAIL", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2));
			}

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ServiceChargeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ServiceChargeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ServiceChargeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ServiceChargeUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		List<level1Elm> datas = req.getRequest().getDatas();
		String serviceChargeNO = req.getRequest().getServiceChargeNo();
		String scType = req.getRequest().getScType();

		if (Check.Null(serviceChargeNO)) 
		{
			errMsg.append("服务费编码不能为空值 ");
			isFail = true;
		}

		if (Check.Null(scType)) 
		{
			errMsg.append("种类不能为空值 ");
			isFail = true;
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for(level1Elm par : datas){

			if (Check.Null(par.getSpNo())) 
			{
				errMsg.append("编号不能为空值 ");
				isFail = true;
			}
			if (Check.Null(par.getScrate())) 
			{
				errMsg.append("费率不能为空值 ");
				isFail = true;
			}
			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ServiceChargeUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ServiceChargeUpdateReq>(){};
	}

	@Override
	protected DCP_ServiceChargeUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ServiceChargeUpdateRes();
	}

}
