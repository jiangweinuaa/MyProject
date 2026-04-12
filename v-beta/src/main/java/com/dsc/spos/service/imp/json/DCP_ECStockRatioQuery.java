package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_ECStockRatioQueryReq;
import com.dsc.spos.json.cust.res.DCP_ECStockRatioQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_ECStockRatioGet
 * 服务说明：电商平台库存比例查询
 * @author jinzma 
 * @since  2020-02-16
 */
public class DCP_ECStockRatioQuery extends SPosBasicService<DCP_ECStockRatioQueryReq,DCP_ECStockRatioQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_ECStockRatioQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		return false;
	}

	@Override
	protected TypeToken<DCP_ECStockRatioQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return  new TypeToken<DCP_ECStockRatioQueryReq>(){};
	}

	@Override
	protected DCP_ECStockRatioQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_ECStockRatioQueryRes();
	}

	@Override
	protected DCP_ECStockRatioQueryRes processJson(DCP_ECStockRatioQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;			
		DCP_ECStockRatioQueryRes res = this.getResponse();
		BigDecimal ecStockRatio_B = new BigDecimal("0");
		try
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_ECStockRatioQueryRes.level1Elm>());
			for (Map<String, Object> oneData : getQData) 
			{
				DCP_ECStockRatioQueryRes.level1Elm oneLv1 = res.new level1Elm(); 
				
				String ecPlatformNo = oneData.get("ECPLATFORMNO").toString();
				String ecPlatformName = oneData.get("ECPLATFORMNAME").toString();
				String ecStockRatio = oneData.get("ECPLATFORMRATIO").toString();
				if (Check.Null(ecStockRatio)) ecStockRatio="0";
				
				oneLv1.setEcPlatformNo(ecPlatformNo);
				oneLv1.setEcPlatformName(ecPlatformName);
				oneLv1.setEcStockRatio(ecStockRatio);
				res.getDatas().add(oneLv1);
				
				ecStockRatio_B=ecStockRatio_B.add(new BigDecimal(ecStockRatio));	
			}
			//库存比例必须等于100
			if (ecStockRatio_B.compareTo(new BigDecimal("100"))!=0)
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "库存比例设置不等于100,请重新设置 ");		
			}
			
			return res;		
			
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_ECStockRatioQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;	
		StringBuffer sqlbuf=new StringBuffer("");
		String eId = req.geteId();
		sqlbuf.append(" select ECPLATFORMNO,ECPLATFORMNAME,ECPLATFORMRATIO from OC_ECOMMERCE "
			+ " where EID='"+eId+"' and status='100' AND ECPLATFORMNO<>'4' ");

		sql = sqlbuf.toString();
		return sql;
		
	}

}
