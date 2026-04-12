package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FlavorDeleteReq;
import com.dsc.spos.json.cust.req.DCP_FlavorDeleteReq.flavor;
import com.dsc.spos.json.cust.res.DCP_FlavorDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_FlavorDelete extends SPosAdvanceService<DCP_FlavorDeleteReq,DCP_FlavorDeleteRes>
{

	@Override
	protected void processDUID(DCP_FlavorDeleteReq req, DCP_FlavorDeleteRes res) throws Exception 
	{
		String eId = req.geteId();
		//清缓存
		String posUrl = PosPub.getPOS_INNER_URL(eId);
		String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'" +
				" AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
		List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
		String apiUserCode = "";
		String apiUserKey = "";
		if (result != null && result.size() == 2) {
			for (Map<String, Object> map : result) {
				if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
					apiUserCode = map.get("ITEMVALUE").toString();
				} else {
					apiUserKey = map.get("ITEMVALUE").toString();
				}
			}
		}
		PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,eId);


		for (flavor par : req.getRequest().getFlavorIdList())
		{
			String flavorId = par.getFlavorId();
			//
			DelBean db1 = new DelBean("DCP_FLAVOR");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("FLAVORID", new DataValue(flavorId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			//
			db1 = new DelBean("DCP_FLAVOR_LANG");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("FLAVORID", new DataValue(flavorId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			db1 = new DelBean("DCP_FLAVOR_CATEGORY");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("FLAVORID", new DataValue(flavorId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			db1 = new DelBean("DCP_FLAVOR_GOODS");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("FLAVORID", new DataValue(flavorId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
		}




		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FlavorDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FlavorDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FlavorDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FlavorDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		if(req.getRequest().getFlavorIdList()==null)
		{
			isFail = true;
			errMsg.append("编号列表不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		for (flavor par : req.getRequest().getFlavorIdList())
		{
			String flavorId = par.getFlavorId();

			if(Check.Null(flavorId))
			{
				errMsg.append("口味编号不能为空值 ");
				isFail = true;
			}
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FlavorDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorDeleteReq>() {};
	}

	@Override
	protected DCP_FlavorDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_FlavorDeleteRes();
	}




}
