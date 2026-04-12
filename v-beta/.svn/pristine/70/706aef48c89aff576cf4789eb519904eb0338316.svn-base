package com.dsc.spos.service.imp.json;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_TouchMenuShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_TouchMenuShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_TouchMenuShopQuery  extends SPosBasicService<DCP_TouchMenuShopQueryReq,DCP_TouchMenuShopQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_TouchMenuShopQueryReq req) throws Exception {
	// TODO 自动生成的方法存根
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String menuNO = req.getMenuNO();

		if(Check.Null(menuNO)){
			errMsg.append("菜单编号不能为空值 ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_TouchMenuShopQueryReq> getRequestType() {
	// TODO 自动生成的方法存根
	return new TypeToken<DCP_TouchMenuShopQueryReq>(){};
	}

	@Override
	protected DCP_TouchMenuShopQueryRes getResponseType() {
	// TODO 自动生成的方法存根
	return new DCP_TouchMenuShopQueryRes();
	}

	@Override
	protected DCP_TouchMenuShopQueryRes processJson(DCP_TouchMenuShopQueryReq req) throws Exception {
	// TODO 自动生成的方法存根
			String sql=null;			
			//查詢資料
			DCP_TouchMenuShopQueryRes res = null;
			res = this.getResponse();
			try
			{
				//单头查询
				sql=this.getQuerySql(req);	
				List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
				if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
				{
					res.setDatas(new ArrayList<DCP_TouchMenuShopQueryRes.level1Elm>());
					for (Map<String, Object> oneData : getQDataDetail) 
					{
						DCP_TouchMenuShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
					 
						String shopId = oneData.get("SHOPID").toString();
						String shopName = oneData.get("ORG_NAME").toString();

						//设置响应
						oneLv1.setShopId(shopId);
						oneLv1.setShopName(shopName);
						res.getDatas().add(oneLv1);
					}					
				}
			}
			catch (Exception e) 
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
			}

			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
			return res;
			
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO 自动生成的方法存根
	
	}

	@Override
	protected String getQuerySql(DCP_TouchMenuShopQueryReq req) throws Exception {
	// TODO 自动生成的方法存根
		String sql=null;			
		String eId = req.geteId();
		String langType = req.getLangType();
		String menuNO=req.getMenuNO();

		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( 
			  	" select MENUNO,SHOPID,ORG_NAME from DCP_TOUCHMENU_SHOP a "
				+ " inner join DCP_ORG_lang b on a.EID=b.EID and a.SHOPID=b.organizationno "
				+ " and b.status='100' and b.lang_type='"+ langType +"' "
				+ " WHERE a.status='100' and a.menuno='"+ menuNO +"' and a.EID ='"+ eId +"'  " ) ;

		sql = sqlbuf.toString();
		return sql;
	}

}
