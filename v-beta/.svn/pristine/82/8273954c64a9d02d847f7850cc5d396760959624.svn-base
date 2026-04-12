package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_MiniChargeShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_MiniChargeShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 查询低消适用门店
 * @author yuanyy 2019-03-01
 *
 */
public class DCP_MiniChargeShopQuery extends SPosBasicService<DCP_MiniChargeShopQueryReq, DCP_MiniChargeShopQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_MiniChargeShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if (Check.Null(req.getRequest().getMiniChargeNo())) 
		{
			errCt++;
			errMsg.append("编码不可为空值, ");
			isFail = true;
		} 
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_MiniChargeShopQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_MiniChargeShopQueryReq>(){};
	}

	@Override
	protected DCP_MiniChargeShopQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_MiniChargeShopQueryRes();
	}

	@Override
	protected DCP_MiniChargeShopQueryRes processJson(DCP_MiniChargeShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String sql=null;
		DCP_MiniChargeShopQueryRes res = null;
		res = this.getResponse();
		try {
			//未选取门店
			sql = this.getQuerySql(req);			

			String[] conditionValues1 = { }; //查詢條件

			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
			
			res.setDatas(new ArrayList<DCP_MiniChargeShopQueryRes.level1Elm>());
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_MiniChargeShopQueryRes.level1Elm lev1=  res.new level1Elm();
					lev1.setShopId(oneData.get("SHOPID").toString());
					lev1.setShopName(oneData.get("SHOPNAME").toString());
					res.getDatas().add(lev1);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());	
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		
	}
	
	@Override
	protected String getQuerySql(DCP_MiniChargeShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql=null;
		String miniChargeNO=req.getRequest().getMiniChargeNo();
		String eId = req.geteId();
		String langType= req.getLangType();
		
		if(miniChargeNO==null) miniChargeNO="";
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append("SELECT SHOPID , shopName   FROM ( "
				+ " SELECT a.EID , a.miniChargeNo ,  a.SHOPID AS SHOPID , b.org_name AS shopName  "
				+ "FROM DCP_MINICHARGE_shop a LEFT JOIN DCP_ORG_lang b "
				+ " ON a.EID = b.EID AND a.SHOPID = b.organizationNO  AND b.lang_Type = '"+langType+"'"
				+ " WHERE a.EID = '"+eId+"'  "
				+ " and a.miniChargeNO = '"+miniChargeNO+"' " 
				+ " ) order by SHOPID ");
		sql=sqlbuf.toString();
		
		return sql;
	}
	
	
}
