package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FlavorGroupDeleteReq;
import com.dsc.spos.json.cust.req.DCP_FlavorGroupDeleteReq.group;
import com.dsc.spos.json.cust.res.DCP_FlavorGroupDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_FlavorGroupDelete extends SPosAdvanceService<DCP_FlavorGroupDeleteReq,DCP_FlavorGroupDeleteRes>
{

	@Override
	protected void processDUID(DCP_FlavorGroupDeleteReq req, DCP_FlavorGroupDeleteRes res) throws Exception 
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

		
		
		for (group par : req.getRequest().getGroupIdList())
		{
			String groupId = par.getGroupId();
			//
			DelBean db1 = new DelBean("DCP_FLAVORGROUP");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("GROUPID", new DataValue(groupId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			//
			db1 = new DelBean("DCP_FLAVORGROUP_LANG");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("GROUPID", new DataValue(groupId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			db1 = new DelBean("DCP_FLAVOR");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("GROUPID", new DataValue(groupId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			db1 = new DelBean("DCP_FLAVOR_LANG");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("GROUPID", new DataValue(groupId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			db1 = new DelBean("DCP_FLAVOR_CATEGORY");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("GROUPID", new DataValue(groupId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			db1 = new DelBean("DCP_FLAVOR_GOODS");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("GROUPID", new DataValue(groupId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

		}
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FlavorGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FlavorGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FlavorGroupDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FlavorGroupDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<group> groupList = req.getRequest().getGroupIdList();
		if(groupList==null)
		{
			isFail = true;
			errMsg.append("编号列表不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for (group par : groupList)
		{
			String groupId = par.getGroupId();

			if(Check.Null(groupId))
			{
				errMsg.append("分组编号不能为空值， ");
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
	protected TypeToken<DCP_FlavorGroupDeleteReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorGroupDeleteReq>() {};
	}

	@Override
	protected DCP_FlavorGroupDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_FlavorGroupDeleteRes();
	}	

}
