package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsImageDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsImageDeleteReq.levelGoods;
import com.dsc.spos.json.cust.res.DCP_GoodsImageDeleteRes;
import com.dsc.spos.model.Plu_POS_GoodsPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsImageDelete extends SPosAdvanceService<DCP_GoodsImageDeleteReq,DCP_GoodsImageDeleteRes>
{

	@Override
	protected void processDUID(DCP_GoodsImageDeleteReq req, DCP_GoodsImageDeleteRes res) throws Exception 
	{
		String eId = req.geteId();
		String apptype = req.getRequest().getAppType();
		List<levelGoods> pluList= req.getRequest().getPluList();


        //同步缓存
        List<Plu_POS_GoodsPriceRedisUpdate> v_pluList=new ArrayList<>();


		for (levelGoods plu : pluList) 
		{
			DelBean db1 = new DelBean("DCP_GOODSIMAGE_DETAILIMAGE");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("APPTYPE", new DataValue(apptype,Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(plu.getPluNo(),Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));	

			db1 = new DelBean("DCP_GOODSIMAGE_PRODIMAGE");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("APPTYPE", new DataValue(apptype,Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(plu.getPluNo(),Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));	

			db1 = new DelBean("DCP_GOODSIMAGE_SPECIMAGE");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("APPTYPE", new DataValue(apptype,Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(plu.getPluNo(),Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));	

			db1 = new DelBean("DCP_GOODSIMAGE_SYMBOL");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("APPTYPE", new DataValue(apptype,Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(plu.getPluNo(),Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));	

			db1 = new DelBean("DCP_GOODSIMAGE");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("APPTYPE", new DataValue(apptype,Types.VARCHAR));
			db1.addCondition("PLUNO", new DataValue(plu.getPluNo(),Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));


            //同步缓存
            Plu_POS_GoodsPriceRedisUpdate plu_pos_goodsPriceRedisUpdate=new Plu_POS_GoodsPriceRedisUpdate();
            plu_pos_goodsPriceRedisUpdate.setPluNo(plu.getPluNo());
            v_pluList.add(plu_pos_goodsPriceRedisUpdate);
		}

		//
		this.doExecuteDataToDB();


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

        //
        PosPub.POS_GoodsPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,v_pluList);


		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsImageDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsImageDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsImageDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsImageDeleteReq req) throws Exception 
	{	

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String apptype = req.getRequest().getAppType();
		List<levelGoods> pluList= req.getRequest().getPluList();

		if(Check.Null(apptype))
		{
			errMsg.append("应用类型不能为空值 ");
			isFail = true;
		}

		if (pluList==null || pluList.size()==0) 
		{
			errMsg.append("商品图片列表不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsImageDeleteReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsImageDeleteReq>() {};
	}

	@Override
	protected DCP_GoodsImageDeleteRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsImageDeleteRes();
	}


}
