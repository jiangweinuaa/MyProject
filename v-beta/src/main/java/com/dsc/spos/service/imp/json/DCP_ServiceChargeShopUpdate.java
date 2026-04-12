package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_ServiceChargeShopUpdateReq;
import com.dsc.spos.json.cust.req.DCP_ServiceChargeShopUpdateReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_ServiceChargeShopUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务费修改
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_ServiceChargeShopUpdate extends SPosAdvanceService<DCP_ServiceChargeShopUpdateReq, DCP_ServiceChargeShopUpdateRes> {

	@Override
	protected void processDUID(DCP_ServiceChargeShopUpdateReq req, DCP_ServiceChargeShopUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String eId = req.geteId(); 
			String serviceChargeNO = req.getRequest().getServiceChargeNo();
			List<DCP_ServiceChargeShopUpdateReq.level1Elm> jsonData=req.getRequest().getDatas();
			//先删除原来单身
			DelBean db1 = new DelBean("DCP_SERVICECHARGE_SHOP");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("SERVICECHARGENO", new DataValue(serviceChargeNO, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));			

			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_SERVICECHARGE");
			SimpleDateFormat dfStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String udTime = dfStamp.format(new Date());		

			ub1.addUpdateValue("UPDATE_TIME", new DataValue(udTime ,Types.VARCHAR));
			ub1.addCondition("SERVICECHARGENO", new DataValue(serviceChargeNO, Types.VARCHAR));
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			for (DCP_ServiceChargeShopUpdateReq.level1Elm level1Elm : jsonData) 
			{
				//新增SQL
				int insColCt = 0;
				String[] columnsModularDetail ={"EID","SERVICECHARGENO","SHOPID"};
				DataValue[] columnsVal = new DataValue[columnsModularDetail.length];
				for (int i = 0; i < columnsVal.length; i++)
				{
					String keyVal = null;
					switch (i) 
					{
					case 0:
						keyVal=eId;
						break;
					case 1:
						keyVal=serviceChargeNO;
						break;
					case 2:
						keyVal=level1Elm.getShopId();
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

				String[] columns2 = new String[insColCt];
				DataValue[] insValue2 = new DataValue[insColCt];

				insColCt = 0;

				for (int i = 0; i < columnsVal.length; i++) 
				{
					if (columnsVal[i] != null) 
					{
						columns2[insColCt] = columnsModularDetail[i];
						insValue2[insColCt] = columnsVal[i];
						insColCt++;
						if (insColCt >= insValue2.length)
							break;
					}
				}

				InsBean ib2 = new InsBean("DCP_SERVICECHARGE_SHOP", columns2);
				ib2.addValues(insValue2);
				this.addProcessData(new DataProcessBean(ib2));	
			}

			this.doExecuteDataToDB();	

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		}
		catch(Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
		}
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_ServiceChargeShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_ServiceChargeShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_ServiceChargeShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_ServiceChargeShopUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		List<level1Elm> datas = req.getRequest().getDatas();
		String serviceChargeNO = req.getRequest().getServiceChargeNo();

		if (Check.Null(serviceChargeNO)) 
		{
			errMsg.append("服务费编码不能为空值 ");
			isFail = true;
		}
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for(level1Elm par : datas){

			if (Check.Null(par.getShopId())) 
			{
				errMsg.append("门店编号不能为空值 ");
				isFail = true;
			}
			if (isFail){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_ServiceChargeShopUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ServiceChargeShopUpdateReq>(){};
	}

	@Override
	protected DCP_ServiceChargeShopUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ServiceChargeShopUpdateRes();
	}


}
