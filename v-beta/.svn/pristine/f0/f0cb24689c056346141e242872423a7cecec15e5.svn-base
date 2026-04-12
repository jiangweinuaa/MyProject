package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_DualPlayShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_DualPlayShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;


/**
 * 服務函數：DualPlayGetDCP
 *   說明：双屏播放查询DCP
 * 服务说明：双屏播放查询DCP
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DualPlayShopQuery extends SPosBasicService<DCP_DualPlayShopQueryReq,DCP_DualPlayShopQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_DualPlayShopQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String dualPlayID = req.getRequest().getDualPlayID();

		if(Check.Null(dualPlayID)){
			errMsg.append("双屏播放ID不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;

	}

	@Override
	protected TypeToken<DCP_DualPlayShopQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DualPlayShopQueryReq>(){};
	}

	@Override
	protected DCP_DualPlayShopQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DualPlayShopQueryRes();
	}

	@Override
	protected DCP_DualPlayShopQueryRes processJson(DCP_DualPlayShopQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_DualPlayShopQueryRes res = null;
		res = this.getResponse();

		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(new ArrayList<DCP_DualPlayShopQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_DualPlayShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
				 
					String shopId = oneData.get("SHOPID").toString();
					String shopName = oneData.get("ORG_NAME").toString();

					//设置响应
					oneLv1.setShopId(shopId);
					oneLv1.setShopName(shopName);
					res.getDatas().add(oneLv1);
					oneLv1 = null;
				}
			}

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");

		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_DualPlayShopQueryReq req) throws Exception {
		String sql=null;			
		String eId = req.geteId();
		String langType = req.getLangType();
		String dualplayId = req.getRequest().getDualPlayID();

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( 
				" select a.SHOPID,b.org_name from DCP_DUALPLAY_SHOP a inner join DCP_ORG_lang b  "
		+ " on a.EID=b.EID and a.SHOPID=b.organizationno "
		+ " and b.lang_type='"+ langType +"' "
    + " where a.EID='"+ eId +"' and a.status='100' and dualplayid = '"+dualplayId+"' order by a.SHOPID" ) ;

		sql = sqlbuf.toString();
		return sql;

	}

}


