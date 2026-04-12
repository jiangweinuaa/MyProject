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
import com.dsc.spos.json.cust.req.DCP_StuffUpdateReq;
import com.dsc.spos.json.cust.res.DCP_StuffUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_StuffUpdate extends SPosAdvanceService<DCP_StuffUpdateReq,DCP_StuffUpdateRes>
{

	@Override
	protected void processDUID(DCP_StuffUpdateReq req, DCP_StuffUpdateRes res) throws Exception 
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
        PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,req.geteId());
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		
		String stuffId = req.getRequest().getStuffId();
	    String sortId = req.getRequest().getSortId();
	

		UptBean up1 = new UptBean("DCP_STUFF");
		up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		up1.addCondition("STUFFID", new DataValue(stuffId,Types.VARCHAR));
		
		up1.addUpdateValue("SORTID", new DataValue(sortId,Types.VARCHAR));
		up1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));
		
		this.addProcessData(new DataProcessBean(up1)); // 新增
		
		
		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StuffUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StuffUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StuffUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StuffUpdateReq req) throws Exception 
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
	    String sortId = req.getRequest().getSortId();
		if(Check.Null(stuffId))
		{
			isFail = true;
			errMsg.append("加料商品编码stuffId不能为空 ");
		}
		if(Check.Null(sortId))
		{
			isFail = true;
			errMsg.append("行号sortId不能为空 ");
		}
		

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_StuffUpdateReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StuffUpdateReq>() {};
	}

	@Override
	protected DCP_StuffUpdateRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_StuffUpdateRes();
	}	




}
