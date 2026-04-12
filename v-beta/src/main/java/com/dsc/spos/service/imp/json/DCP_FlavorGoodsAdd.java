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
import com.dsc.spos.json.cust.req.DCP_FlavorGoodsAddReq;
import com.dsc.spos.json.cust.req.DCP_FlavorGoodsAddReq.levelFlavorGoods;
import com.dsc.spos.json.cust.res.DCP_FlavorGoodsAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_FlavorGoodsAdd extends SPosAdvanceService<DCP_FlavorGoodsAddReq,DCP_FlavorGoodsAddRes>
{

	@Override
	protected void processDUID(DCP_FlavorGoodsAddReq req, DCP_FlavorGoodsAddRes res) throws Exception 
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
		List<levelFlavorGoods> PluList=req.getRequest().getPluList();

		//
//		DelBean db1 = new DelBean("DCP_FLAVOR_GOODS");
//		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
//		db1.addCondition("FLAVORID", new DataValue(flavorId,Types.VARCHAR));
//		this.addProcessData(new DataProcessBean(db1));


		String[] columns_DCP_FLAVOR_CATEGORY = 
			{ 
					"EID","FLAVORID","PLUNO","GROUPID","LASTMODITIME"
			};
		for (levelFlavorGoods plu : PluList) 
		{

			DataValue[] insValue1 = null;
			
			DelBean db1 = new DelBean("DCP_FLAVOR_GOODS");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("FLAVORID", new DataValue(flavorId,Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(plu.getPluNo(),Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));  //  先删除 后新增

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(flavorId, Types.VARCHAR),
					new DataValue(plu.getPluNo(), Types.VARCHAR),					
					new DataValue(groupId, Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_FLAVOR_GOODS", columns_DCP_FLAVOR_CATEGORY);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}	

		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_FlavorGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FlavorGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FlavorGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FlavorGoodsAddReq req) throws Exception 
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
		List<levelFlavorGoods> PluList=req.getRequest().getPluList();


		if(Check.Null(flavorId))
		{
			errMsg.append("口味编码不能为空值 ");
			isFail = true;
		}

		if(PluList==null || PluList.size()==0)
		{
			errMsg.append("商品列表不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_FlavorGoodsAddReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorGoodsAddReq>() {};
	}

	@Override
	protected DCP_FlavorGoodsAddRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_FlavorGoodsAddRes();
	}	




}
