package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DeliverySettingDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DeliverySettingDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 货运厂商新增
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_DeliverySettingDelete extends SPosAdvanceService<DCP_DeliverySettingDeleteReq, DCP_DeliverySettingDeleteRes> 
{

	@Override
	protected void processDUID(DCP_DeliverySettingDeleteReq req, DCP_DeliverySettingDeleteRes res) throws Exception 
	{
		this.doDelete(req);	

		this.doExecuteDataToDB();

		if(res.isSuccess())
		{
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");			
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DeliverySettingDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DeliverySettingDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DeliverySettingDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		List<DelBean> data = new ArrayList<DelBean>();
		String eId = req.geteId();
		String deliveryType = req.getRequest().getDeliveryType().toString();
		String appId = req.getRequest().getAppId().toString();
		DelBean db1 = new DelBean("DCP_OUTSALESET");
		db1.addCondition("DELIVERYTYPE", new DataValue(deliveryType, Types.VARCHAR));
		db1.addCondition("APPID", new DataValue(appId, Types.VARCHAR));
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		data.add(db1);

        //DCP_OUTSALESET_SHOP
        DelBean db2 = new DelBean("DCP_OUTSALESET_SHOP");
        db2.addCondition("DELIVERYTYPE", new DataValue(deliveryType, Types.VARCHAR));
        db2.addCondition("APPID", new DataValue(appId, Types.VARCHAR));
        db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
        data.add(db2);

		return data;
	}

	@Override
	protected boolean isVerifyFail(DCP_DeliverySettingDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
		String deliveryType = req.getRequest().getDeliveryType().toString();
		String appId = req.getRequest().getAppId().toString();

		if (Check.Null(deliveryType)) 
		{
			errCt++;
			errMsg.append("物流类型不可为空值  ");
			isFail = true;
		}

		if (Check.Null(appId)) 
		{
			errCt++;
			errMsg.append("物流id不可为空值  ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DeliverySettingDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DeliverySettingDeleteReq>(){};
	}

	@Override
	protected DCP_DeliverySettingDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DeliverySettingDeleteRes();
	}


}
