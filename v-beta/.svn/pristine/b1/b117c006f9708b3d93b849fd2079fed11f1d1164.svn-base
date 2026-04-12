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
import com.dsc.spos.json.cust.req.DCP_GoodsSetEnableReq;
import com.dsc.spos.json.cust.req.DCP_GoodsSetEnableReq.Plu;
import com.dsc.spos.json.cust.res.DCP_GoodsSetEnableRes;
import com.dsc.spos.model.Plu_POS_GoodsPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsSetEnable extends SPosAdvanceService<DCP_GoodsSetEnableReq,DCP_GoodsSetEnableRes> {

	@Override
	protected void processDUID(DCP_GoodsSetEnableReq req, DCP_GoodsSetEnableRes res) throws Exception {
	// TODO Auto-generated method stub
		try 
		{

            //同步缓存
            List<Plu_POS_GoodsPriceRedisUpdate> pluList=new ArrayList<>();
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


			String status = "100";//状态：-1未启用100已启用 0已禁用
			
			if(req.getRequest().getOprType().equals("1"))//操作类型：1-启用2-禁用
			{
				status = "100";
			}
			else
			{
				status = "0";
			}
			
			for (Plu par : req.getRequest().getPluNoList()) 
			{
                //同步缓存
                Plu_POS_GoodsPriceRedisUpdate plu_pos_goodsPriceRedisUpdate=new Plu_POS_GoodsPriceRedisUpdate();
                plu_pos_goodsPriceRedisUpdate.setPluNo(par.getPluNo());
                pluList.add(plu_pos_goodsPriceRedisUpdate);

				String pluNo = par.getPluNo();
				UptBean up1 = new UptBean("DCP_GOODS");
				up1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
				up1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
				if(status.equals("0"))//只能禁用，已启用的。
				{
					up1.addCondition("STATUS", new DataValue("100", Types.VARCHAR));
				}	
				
				up1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
                up1.addUpdateValue("REDISUPDATESUCCESS", new DataValue("N", Types.VARCHAR));

				this.addProcessData(new DataProcessBean(up1));
			}


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
	protected List<InsBean> prepareInsertData(DCP_GoodsSetEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsSetEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsSetEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsSetEnableReq req) throws Exception {
	// TODO Auto-generated method stub
	boolean isFail = false;
	StringBuffer errMsg = new StringBuffer("");
	
	if(req.getRequest()==null)
  {
  	errMsg.append("requset不能为空值 ");
  	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
  }

	if(Check.Null(req.getRequest().getOprType()))
	{
		errMsg.append("操作类型不可为空值, ");
		isFail = true;
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
	protected TypeToken<DCP_GoodsSetEnableReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_GoodsSetEnableReq>(){};
	}

	@Override
	protected DCP_GoodsSetEnableRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_GoodsSetEnableRes();
	}

}
