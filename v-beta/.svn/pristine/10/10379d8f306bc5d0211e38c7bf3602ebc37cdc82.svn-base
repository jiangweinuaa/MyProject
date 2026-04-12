package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_DeliverySettingShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_DeliverySettingShopQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服務函數：PTemplateGet
 *   說明：要货模板查询
 * 服务说明：要货模板查询
 * @author Jinzma 
 * @since  2017-03-09
 */
public class DCP_DeliverySettingShopQuery extends SPosBasicService<DCP_DeliverySettingShopQueryReq,DCP_DeliverySettingShopQueryRes>  {

	@Override
	protected boolean isVerifyFail(DCP_DeliverySettingShopQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根

		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
		}

		if (Check.Null(req.getRequest().getAppId()))
		{
			errCt++;
			errMsg.append("物流ID不可为空值, ");
			isFail = true;
		}

		if (Check.Null(req.getRequest().getDeliveryType()))
		{
			errCt++;
			errMsg.append("物流类型不可为空值, ");
			isFail = true;
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_DeliverySettingShopQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_DeliverySettingShopQueryReq>(){};
	}

	@Override
	protected DCP_DeliverySettingShopQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_DeliverySettingShopQueryRes();
	}

	@Override
	protected DCP_DeliverySettingShopQueryRes processJson(DCP_DeliverySettingShopQueryReq req) throws Exception 
	{
		// TODO 自动生成的方法存根
		String sql=null;			
		//查詢資料
		DCP_DeliverySettingShopQueryRes res = null;
		res = this.getResponse();	
		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_DeliverySettingShopQueryRes.level1Elm>());
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{

				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_DeliverySettingShopQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String shopId = oneData.get("SHOPID").toString();
					String shopName = oneData.get("ORG_NAME").toString();

					//设置响应
					oneLv1.setShopId(shopId);
					oneLv1.setShopName(shopName);

					res.getDatas().add(oneLv1);
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
	protected String getQuerySql(DCP_DeliverySettingShopQueryReq req) throws Exception {

		String sql=null;			
		String eId = req.geteId();
		String appId = req.getRequest().getAppId();
		String deliveryType = req.getRequest().getDeliveryType();
		String langType= req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");

		sqlbuf.append(  " "
				+	" select a.*,b.org_name "
				+ " from  DCP_OUTSALESET_SHOP a "
				+ " left join DCP_ORG_lang b on b.EID=a.EID and b.organizationno=a.SHOPID and b.status='100'  and b.lang_type='"+langType +"'  "
				+ "	where a.EID='"+eId +"' and a.APPID='"+appId +"' "
				+ " and a.DELIVERYTYPE='"+deliveryType +"'  order by a.SHOPID  ");

		sql = sqlbuf.toString();
		return sql;
	}

}


