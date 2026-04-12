package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_DinnerAreaDeleteReq;
import com.dsc.spos.json.cust.res.DCP_DinnerAreaDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_DinnerAreaDelete extends SPosAdvanceService<DCP_DinnerAreaDeleteReq,DCP_DinnerAreaDeleteRes>
{

	@Override
	protected void processDUID(DCP_DinnerAreaDeleteReq req, DCP_DinnerAreaDeleteRes res) throws Exception 
	{

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
        PosPub.clearTableBaseInfoCache(posUrl, apiUserCode, apiUserKey,req.geteId(),req.getShopId());

		String sql = this.getDINNERGROUPNO_SQL(req);			

		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此桌台区域下有桌台记录，请先删除桌台信息！");		
		}
		else
		{
			//DCP_DINNER_AREA
			DelBean db1 = new DelBean("DCP_DINNER_AREA");
			db1.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			db1.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
			db1.addCondition("DINNERGROUP", new DataValue(req.getRequest().getDinnerGroup(), Types.VARCHAR));

			this.addProcessData(new DataProcessBean(db1)); // 		

			//删除该行之后， 该行之后优先级全部减一
			UptBean ub2 = new UptBean("DCP_DINNER_AREA");			
			ub2.addCondition("PRIORITY",new DataValue( req.getRequest().getPriority(), Types.VARCHAR,DataExpression.GreaterEQ));
			ub2.addCondition("ORGANIZATIONNO", new DataValue(req.getShopId(), Types.VARCHAR));
			ub2.addCondition("EID", new DataValue(req.geteId(), Types.VARCHAR));
			
			ub2.addUpdateValue("PRIORITY",new DataValue(1,Types.INTEGER,DataExpression.SubSelf)); 
			this.addProcessData(new DataProcessBean(ub2));			

			this.doExecuteDataToDB();		

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");		
		}



	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DinnerAreaDeleteReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DinnerAreaDeleteReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DinnerAreaDeleteReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DinnerAreaDeleteReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		String DINNERGROUP =req.getRequest().getDinnerGroup();

		if (Check.Null(DINNERGROUP)) 
		{
			errMsg.append("桌台区域编码不可为空值, ");
			isFail = true;
		} 

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DinnerAreaDeleteReq> getRequestType() 
	{
		return new TypeToken<DCP_DinnerAreaDeleteReq>(){};
	}

	@Override
	protected DCP_DinnerAreaDeleteRes getResponseType() 
	{
		return new DCP_DinnerAreaDeleteRes();
	}


	protected String getDINNERGROUPNO_SQL(DCP_DinnerAreaDeleteReq req)
	{		
		String sql = null;

		StringBuffer sqlbuf = new StringBuffer("");

		sqlbuf.append("SELECT * FROM DCP_DINNERTABLE WHERE EID='"+req.geteId()+"' AND ORGANIZATIONNO='"+req.getShopId()+"' AND DINNERGROUP='"+req.getRequest().getDinnerGroup()+"' ");

		sql = sqlbuf.toString();

		return sql;	
	}
	
	

}
