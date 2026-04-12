package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FlavorUpdateReq;
import com.dsc.spos.json.cust.req.DCP_FlavorUpdateReq.levelFlavor;
import com.dsc.spos.json.cust.res.DCP_FlavorUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_FlavorUpdate extends SPosAdvanceService<DCP_FlavorUpdateReq,DCP_FlavorUpdateRes>
{

	@Override
	protected void processDUID(DCP_FlavorUpdateReq req, DCP_FlavorUpdateRes res) throws Exception 
	{

		String eId=req.geteId();
        //清缓存
        String posUrl = PosPub.getPOS_INNER_URL(req.geteId());
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

		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		String flavorId = req.getRequest().getFlavorId();		
		String groupId=req.getRequest().getGroupId();
		String sortId=req.getRequest().getSortId();
		List<levelFlavor> flavorName_Lang=req.getRequest().getFlavorName_lang();

		if (flavorName_Lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("口味名称多语言必须有值！");
			return;
		}		

		String sql = "select FLAVORID from DCP_FLAVOR where eid='"+eId+"' and FLAVORID='"+flavorId+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData==null || getData.isEmpty()) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此口味编码不存在！");
			return;
		}

		DelBean db1 = new DelBean("DCP_FLAVOR_LANG");
		db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		db1.addCondition("FLAVORID", new DataValue(flavorId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));

		String[] columns_DCP_FLAVOR_LANG = 
			{ 
					"EID","FLAVORID","LANG_TYPE","FLAVORNAME","GROUPID","LASTMODITIME"
			};
		for (levelFlavor group : flavorName_Lang) 
		{

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(flavorId, Types.VARCHAR),
					new DataValue(group.getLangType(), Types.VARCHAR),
					new DataValue(group.getName(), Types.VARCHAR),
					new DataValue(groupId, Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_FLAVOR_LANG", columns_DCP_FLAVOR_LANG);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}	

		//
		UptBean ub1 = new UptBean("DCP_FLAVOR");
		ub1.addUpdateValue("GROUPID", new DataValue(groupId,Types.VARCHAR));
		ub1.addUpdateValue("SORTID", new DataValue(sortId,Types.INTEGER));
		ub1.addUpdateValue("MEMO", new DataValue("",Types.VARCHAR));
		ub1.addUpdateValue("STATUS", new DataValue(100,Types.INTEGER));
		ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));

		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("FLAVORID", new DataValue(flavorId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));


		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FlavorUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FlavorUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FlavorUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FlavorUpdateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String flavorId = req.getRequest().getFlavorId();		
		String groupId=req.getRequest().getGroupId();
		String sortId=req.getRequest().getSortId();
		List<levelFlavor> flavorName_Lang=req.getRequest().getFlavorName_lang();


		if(Check.Null(groupId))
		{
			errMsg.append("分组编码不能为空值 ");
			isFail = true;
		}

		if(Check.Null(flavorId))
		{
			errMsg.append("口味编码不能为空值 ");
			isFail = true;
		}

		if(flavorName_Lang==null)
		{
			errMsg.append("口味名称多语言不能为空值 ");
			isFail = true;
		}

		//给默认值0
		if (PosPub.isNumeric(sortId)==false) 
		{
			req.getRequest().setSortId("0");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FlavorUpdateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorUpdateReq>() {};
	}

	@Override
	protected DCP_FlavorUpdateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_FlavorUpdateRes();
	}



}
