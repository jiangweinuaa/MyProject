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
import com.dsc.spos.json.cust.req.DCP_FlavorCreateReq;
import com.dsc.spos.json.cust.req.DCP_FlavorCreateReq.levelFlavor;
import com.dsc.spos.json.cust.res.DCP_FlavorCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_FlavorCreate extends SPosAdvanceService<DCP_FlavorCreateReq,DCP_FlavorCreateRes>
{

	@Override
	protected void processDUID(DCP_FlavorCreateReq req, DCP_FlavorCreateRes res) throws Exception 
	{
		String eId=req.geteId();
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
		if (getData!=null && getData.isEmpty()==false) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此口味编码已经存在！");
			return;
		}

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
		String[] columns_DCP_FLAVOR = 
			{ 
					"EID","FLAVORID","GROUPID","SORTID",
					"MEMO","STATUS","CREATEOPID",
					"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
			};
		DataValue[] insValue1 = null;

		insValue1 = new DataValue[]{
				new DataValue(eId, Types.VARCHAR),
				new DataValue(flavorId, Types.VARCHAR),
				new DataValue(groupId, Types.VARCHAR),
				new DataValue(sortId, Types.INTEGER),				
				new DataValue("", Types.VARCHAR),
				new DataValue(100, Types.INTEGER),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE)				
		};

		InsBean ib1 = new InsBean("DCP_FLAVOR", columns_DCP_FLAVOR);
		ib1.addValues(insValue1);
		this.addProcessData(new DataProcessBean(ib1)); // 新增

		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FlavorCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FlavorCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FlavorCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FlavorCreateReq req) throws Exception 
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
	protected TypeToken<DCP_FlavorCreateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorCreateReq>() {};
	}

	@Override
	protected DCP_FlavorCreateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_FlavorCreateRes();
	}




}
