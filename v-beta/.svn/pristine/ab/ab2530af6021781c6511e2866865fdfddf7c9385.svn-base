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
import com.dsc.spos.json.cust.req.DCP_StuffGoodsAddReq;
import com.dsc.spos.json.cust.req.DCP_StuffGoodsAddReq.levelStuffGoods;
import com.dsc.spos.json.cust.res.DCP_StuffGoodsAddRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_StuffGoodsAdd extends SPosAdvanceService<DCP_StuffGoodsAddReq,DCP_StuffGoodsAddRes>
{

	@Override
	protected void processDUID(DCP_StuffGoodsAddReq req, DCP_StuffGoodsAddRes res) throws Exception 
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
		
		String stuffId = req.getRequest().getStuffId();
		List<levelStuffGoods> PluList=req.getRequest().getPluList();

		String[] columns_DCP_STUFF_GOODS = 
			{ 
					"EID","STUFFID","PLUNO","LASTMODITIME"
			};
		
		for (levelStuffGoods plu : PluList) 
		{
			DelBean db1 = new DelBean("DCP_STUFF_GOODS");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("STUFFID", new DataValue(stuffId,Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(plu.getPluNo(),Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(stuffId, Types.VARCHAR),
					new DataValue(plu.getPluNo(), Types.VARCHAR),										
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_STUFF_GOODS", columns_DCP_STUFF_GOODS);
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
	protected List<InsBean> prepareInsertData(DCP_StuffGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StuffGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StuffGoodsAddReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StuffGoodsAddReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		
		String stuffId = req.getRequest().getStuffId();
		if(Check.Null(stuffId))
		{
			errMsg.append("加料商品编码不能为空值 ，");
			isFail = true;
		}
		List<levelStuffGoods> PluList=req.getRequest().getPluList();
	
		if(PluList==null || PluList.size()==0)
		{
			errMsg.append("商品列表不能为空值， ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_StuffGoodsAddReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StuffGoodsAddReq>() {};
	}

	@Override
	protected DCP_StuffGoodsAddRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_StuffGoodsAddRes();
	}	




}
