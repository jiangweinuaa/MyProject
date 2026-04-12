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
import com.dsc.spos.json.cust.req.DCP_StuffCategoryDeleteReq;
import com.dsc.spos.json.cust.req.DCP_StuffCategoryDeleteReq.levelStuffCategory;
import com.dsc.spos.json.cust.res.DCP_StuffCategoryDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_StuffCategoryDelete extends SPosAdvanceService<DCP_StuffCategoryDeleteReq,DCP_StuffCategoryDeleteRes>
{

	@Override
	protected void processDUID(DCP_StuffCategoryDeleteReq req, DCP_StuffCategoryDeleteRes res) throws Exception 
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
		List<levelStuffCategory> PluList=req.getRequest().getCategoryList();
	
		for (levelStuffCategory plu : PluList) 
		{
			String stuffId = plu.getStuffId();
			DelBean db1 = new DelBean("DCP_STUFF_CATEGORY");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("STUFFID", new DataValue(stuffId,Types.VARCHAR));
			db1.addCondition("CATEGORYID", new DataValue(plu.getCategory(),Types.VARCHAR));
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
	protected List<InsBean> prepareInsertData(DCP_StuffCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StuffCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StuffCategoryDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StuffCategoryDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		
		
		List<levelStuffCategory> PluList=req.getRequest().getCategoryList();
	
		if(PluList==null || PluList.size()==0)
		{
			errMsg.append("商品分类列表不能为空值， ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for (levelStuffCategory plu : PluList) 
		{
			String stuffId = plu.getStuffId();
			if(Check.Null(stuffId))
			{
				errMsg.append("加料商品编码不能为空值 ，");
				isFail = true;
			}
			if(Check.Null(plu.getCategory()))
			{
				errMsg.append("分类编码不能为空值 ，");
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
	protected TypeToken<DCP_StuffCategoryDeleteReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StuffCategoryDeleteReq>() {};
	}

	@Override
	protected DCP_StuffCategoryDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_StuffCategoryDeleteRes();
	}	




}
