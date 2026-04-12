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
import com.dsc.spos.json.cust.req.DCP_StuffGoodsDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetDeleteReq.Plu;
import com.dsc.spos.json.cust.req.DCP_StuffGoodsDeleteReq.levelStuffGoods;
import com.dsc.spos.json.cust.res.DCP_StuffGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_StuffGoodsDelete extends SPosAdvanceService<DCP_StuffGoodsDeleteReq,DCP_StuffGoodsDeleteRes>
{

	@Override
	protected void processDUID(DCP_StuffGoodsDeleteReq req, DCP_StuffGoodsDeleteRes res) throws Exception 
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
		
		
		List<levelStuffGoods> PluList=req.getRequest().getPluList();
		
		for (levelStuffGoods plu : PluList) 
		{
			
			DelBean db1 = new DelBean("DCP_STUFF_GOODS");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("STUFFID", new DataValue(plu.getStuffId(),Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(plu.getPluNo(),Types.VARCHAR));
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
	protected List<InsBean> prepareInsertData(DCP_StuffGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StuffGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StuffGoodsDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StuffGoodsDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		
		
		
		List<levelStuffGoods> PluList=req.getRequest().getPluList();
	
		if(PluList==null || PluList.size()==0)
		{
			errMsg.append("商品列表不能为空值， ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		for (levelStuffGoods plu : PluList) 
		{
			if(Check.Null(plu.getStuffId()))
			{
				errMsg.append("加料商品编码不能为空值 ，");
				isFail = true;
			}
			if(Check.Null(plu.getPluNo()))
			{
				errMsg.append("商品编码不能为空值 ，");
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
	protected TypeToken<DCP_StuffGoodsDeleteReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StuffGoodsDeleteReq>() {};
	}

	@Override
	protected DCP_StuffGoodsDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_StuffGoodsDeleteRes();
	}	




}
