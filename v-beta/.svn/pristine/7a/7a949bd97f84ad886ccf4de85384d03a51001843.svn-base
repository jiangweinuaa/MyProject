package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_MultiSpecGoodsDeleteReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_MultiSpecGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

import eleme.openapi.sdk.api.entity.product.OBackCategory;

public class DCP_MultiSpecGoodsDelete extends SPosAdvanceService<DCP_MultiSpecGoodsDeleteReq,DCP_MultiSpecGoodsDeleteRes>{

	@Override
	protected void processDUID(DCP_MultiSpecGoodsDeleteReq req, DCP_MultiSpecGoodsDeleteRes res) throws Exception 
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
		try 
		{


			String sql = "";
			for (level1Elm par : req.getRequest().getMasterPluNoList())
			{
				String masterPluNo =par.getMasterPluNo();
				sql = "";
				sql = "select status from DCP_MSPECGOODS where status='-1' and eid='"+eId+"' and MASTERPLUNO='"+masterPluNo+"' ";
				List<Map<String, Object>> getData = this.doQueryData(sql, null);
				if(getData==null||getData.isEmpty())
				{
					continue;
				}



				DelBean db1 = new DelBean("DCP_MSPECGOODS");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("MASTERPLUNO", new DataValue(masterPluNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

				DelBean db2 = new DelBean("DCP_MSPECGOODS_LANG");
				db2.addCondition("MASTERPLUNO", new DataValue(masterPluNo, Types.VARCHAR));
				db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db2));

				DelBean db3 = new DelBean("DCP_MSPECGOODS_ATTR");
				db3.addCondition("MASTERPLUNO", new DataValue(masterPluNo, Types.VARCHAR));
				db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db3));

				DelBean db4 = new DelBean("DCP_MSPECGOODS_ATTR_VALUE");
				db4.addCondition("MASTERPLUNO", new DataValue(masterPluNo, Types.VARCHAR));
				db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db4));

				DelBean db5 = new DelBean("DCP_MSPECGOODS_SUBGOODS");
				db5.addCondition("MASTERPLUNO", new DataValue(masterPluNo, Types.VARCHAR));
				db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db5));

				db5 = new DelBean("DCP_MSPECGOODS_SUBGOODS_LANG");
				db5.addCondition("MASTERPLUNO", new DataValue(masterPluNo, Types.VARCHAR));
				db5.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db5));

			}

			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");	

		} 
		catch (Exception e) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("服务执行异常:"+e.getMessage());	

		}


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_MultiSpecGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_MultiSpecGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_MultiSpecGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_MultiSpecGoodsDeleteReq req) throws Exception {

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (req.getRequest().getMasterPluNoList()==null) 
		{
			errMsg.append("编码不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}


		for (level1Elm par : req.getRequest().getMasterPluNoList())
		{
			String attrGroupId = par.getMasterPluNo();   
			if(Check.Null(attrGroupId)){
				errMsg.append("编码不能为空值 ");
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
	protected TypeToken<DCP_MultiSpecGoodsDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MultiSpecGoodsDeleteReq>(){};
	}

	@Override
	protected DCP_MultiSpecGoodsDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MultiSpecGoodsDeleteRes() ;
	}

}
