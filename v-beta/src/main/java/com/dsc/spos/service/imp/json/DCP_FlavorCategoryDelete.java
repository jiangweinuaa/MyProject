package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_FlavorCategoryDeleteReq;
import com.dsc.spos.json.cust.req.DCP_FlavorCategoryDeleteReq.levelCategory;
import com.dsc.spos.json.cust.res.DCP_FlavorCategoryDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;


public class DCP_FlavorCategoryDelete extends SPosAdvanceService<DCP_FlavorCategoryDeleteReq,DCP_FlavorCategoryDeleteRes>
{

	@Override
	protected void processDUID(DCP_FlavorCategoryDeleteReq req, DCP_FlavorCategoryDeleteRes res) throws Exception 
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
		
			
		List<levelCategory> categoryList=req.getRequest().getCategoryList();		
		for (levelCategory category : categoryList) 
		{
			//
			DelBean db1 = new DelBean("DCP_FLAVOR_CATEGORY");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("FLAVORID", new DataValue(category.getFlavorId(),Types.VARCHAR));
			db1.addCondition("CATEGORY", new DataValue(category.getCategory(),Types.VARCHAR));
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
	protected List<InsBean> prepareInsertData(DCP_FlavorCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_FlavorCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_FlavorCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_FlavorCategoryDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

				
		List<levelCategory> categoryList=req.getRequest().getCategoryList();

	
		if(categoryList==null || categoryList.size()==0)
		{
			errMsg.append("商品分类列表不能为空值 ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for (levelCategory category : categoryList) 
		{
			if(Check.Null(category.getFlavorId()))
			{
				errMsg.append("口味编码不能为空值 ");
				isFail = true;
			}

			if(Check.Null(category.getCategory()))
			{
				errMsg.append("分类编码不能为空值 ");
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
	protected TypeToken<DCP_FlavorCategoryDeleteReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlavorCategoryDeleteReq>() {};
	}

	@Override
	protected DCP_FlavorCategoryDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_FlavorCategoryDeleteRes();
	}




}
