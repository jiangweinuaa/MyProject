package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ServiceChargeShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_ServiceChargeShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务费适用门店查询
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_ServiceChargeShopQuery extends SPosBasicService<DCP_ServiceChargeShopQueryReq, DCP_ServiceChargeShopQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ServiceChargeShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if (Check.Null(req.getRequest().getServiceChargeNo())) 
		{
			errCt++;
			errMsg.append("服务费编码不可为空值, ");
			isFail = true;
		} 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ServiceChargeShopQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ServiceChargeShopQueryReq>(){};
	}

	@Override
	protected DCP_ServiceChargeShopQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ServiceChargeShopQueryRes();
	}

	@Override
	protected DCP_ServiceChargeShopQueryRes processJson(DCP_ServiceChargeShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;
		DCP_ServiceChargeShopQueryRes res = null;
		res = this.getResponse();

		try {
			//未选取门店
			sql = this.getQuerySql(req);			

			String[] conditionValues1 = { }; //查詢條件

			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
			
			res.setDatas(new ArrayList<DCP_ServiceChargeShopQueryRes.level1Elm>());
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_ServiceChargeShopQueryRes.level1Elm lev1= res.new level1Elm();
					lev1.setShopId(oneData.get("SHOPID").toString());
					lev1.setShopName(oneData.get("SHOPNAME").toString());
					res.getDatas().add(lev1);
				}
			}
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		} catch (Exception e) {
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ServiceChargeShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;
		
		String serviceChargeNO=req.getRequest().getServiceChargeNo();
		String langType = req.getLangType();
		String eId = req.geteId();
		
		if(serviceChargeNO==null) serviceChargeNO="";
		StringBuffer sqlbuf=new StringBuffer("");
		
		sqlbuf.append("SELECT SHOPID , shopName   FROM ( "
				+ " SELECT a.EID , a.serviceChargeNo ,  a.SHOPID AS SHOPID , b.org_name AS shopName  "
				+ "FROM DCP_SERVICECHARGE_shop a LEFT JOIN DCP_ORG_lang b "
				+ " ON a.EID = b.EID AND a.SHOPID = b.organizationNO  AND b.lang_Type = '"+langType+"'"
				+ " WHERE a.EID = '"+eId+"' "
				+ " and a.serviceChargeNO = '"+serviceChargeNO+"' " 
				+ " ) order by SHOPID ");
		sql=sqlbuf.toString();
		return sql;
	}
	

}
