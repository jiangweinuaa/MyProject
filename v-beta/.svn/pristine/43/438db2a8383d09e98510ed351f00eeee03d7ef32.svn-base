package com.dsc.spos.service.imp.json;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_GoodsSetDeleteReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetDeleteReq.Plu;
import com.dsc.spos.json.cust.res.DCP_GoodsSetDeleteRes;
import com.dsc.spos.model.Plu_POS_GoodsPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetDelete extends SPosAdvanceService<DCP_GoodsSetDeleteReq,DCP_GoodsSetDeleteRes> {
	
	@Override
	protected void processDUID(DCP_GoodsSetDeleteReq req, DCP_GoodsSetDeleteRes res) throws Exception {
		
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
		PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,req.geteId());


        //同步缓存
        List<Plu_POS_GoodsPriceRedisUpdate> pluList=new ArrayList<>();

		try
		{
			for (Plu par : req.getRequest().getPluNoList())
			{

                //同步缓存
                Plu_POS_GoodsPriceRedisUpdate plu_pos_goodsPriceRedisUpdate=new Plu_POS_GoodsPriceRedisUpdate();
                plu_pos_goodsPriceRedisUpdate.setPluNo(par.getPluNo());
                pluList.add(plu_pos_goodsPriceRedisUpdate);

				
				String pluNo = par.getPluNo();
				//DCP_GOODS
				DelBean db1 = new DelBean("DCP_GOODS");
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				
				this.addProcessData(new DataProcessBean(db1)); // 
				
				//先删除原来的
				db1 = new DelBean("DCP_GOODS_LANG");
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//先删除原来的
				db1 = new DelBean("DCP_GOODS_UNIT");
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//先删除原来的
				db1 = new DelBean("DCP_GOODS_BARCODE");
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//先删除原来的
				db1 = new DelBean("DCP_GOODS_FEATURE");
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//先删除原来的
				db1 = new DelBean("DCP_GOODS_FEATURE_LANG");
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//先删除原来的
				db1 = new DelBean("DCP_GOODS_ATTR");
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//先删除原来的
				db1 = new DelBean("DCP_GOODS_ATTR_VALUE");
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//先删除原来的          谁干了新增忘记干删除，也搞不懂这个表是干嘛的  by jinzma 20220429
				db1 = new DelBean("DCP_GOODS_EXT");
				db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));

                //DCP_GOODSTEMPLATE_GOODS-商品模板
                //DCP_PTEMPLATE_DETAIL-要货模板
                //DCP_TAGTYPE_DETAIL-商品标签

                db1 = new DelBean("DCP_GOODSTEMPLATE_GOODS");
                db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                db1 = new DelBean("DCP_PTEMPLATE_DETAIL");
                db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));

                db1 = new DelBean("DCP_TAGTYPE_DETAIL");
                db1.addCondition("ID", new DataValue(pluNo, Types.VARCHAR));
                db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db1));
			}


			//
            PosPub.POS_GoodsPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,pluList);


            this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
			
		}
		catch (Exception e)
		{
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常："+e.getMessage());
			
		}
		
		
		
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_GoodsSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_GoodsSetDeleteReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<Plu> pluList = req.getRequest().getPluNoList();
		
		if (pluList==null || pluList.isEmpty())
		{
			errMsg.append("商品编码不可为空值, ");
			isFail = true;
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		for(Plu par : pluList)
		{
			if(Check.Null(par.getPluNo()))
			{
				errMsg.append("商品编码不可为空值, ");
				isFail = true;
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
			}
		}
		
		
		
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
		
	}
	
	@Override
	protected TypeToken<DCP_GoodsSetDeleteReq> getRequestType()
	{
		return new TypeToken<DCP_GoodsSetDeleteReq>(){};
	}
	
	@Override
	protected DCP_GoodsSetDeleteRes getResponseType()
	{
		return new DCP_GoodsSetDeleteRes();
	}
	
	
}
