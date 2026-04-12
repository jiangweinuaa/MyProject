package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_DeliverySettingUpdateReq;
import com.dsc.spos.json.cust.res.DCP_DeliverySettingUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 货运厂商修改
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_DeliverySettingUpdate extends SPosAdvanceService<DCP_DeliverySettingUpdateReq, DCP_DeliverySettingUpdateRes> 
{
	@Override
	protected void processDUID(DCP_DeliverySettingUpdateReq req, DCP_DeliverySettingUpdateRes res) throws Exception 
	{
		String sql = null;
		try 
		{
			String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String eId = req.geteId();
			String deliveryType = req.getRequest().getDeliveryType().toString();
			String appId = req.getRequest().getAppId().toString();
			String status = req.getRequest().getStatus();
			String apiUrl = req.getRequest().getApiUrl();
			String apiUrlThree = req.getRequest().getApiUrlThree();
			String apiUrlTwo = req.getRequest().getApiUrlTwo();
			String appSecret = req.getRequest().getAppSecret();
			String appSignKey = req.getRequest().getAppSignKey();
			String deliveryMode = req.getRequest().getDeliveryMode();
			String printMode = req.getRequest().getPrintMode();
			String iv = req.getRequest().getIv();
			String shopCode = req.getRequest().getShopCode();
			String v = req.getRequest().getV();
            String shopType = req.getRequest().getShopType();//1-全部门店2-指定门店，
            String appName = req.getRequest().getAppName();
            String ytoType = req.getRequest().getYtoType();
            String productType = req.getRequest().getProductType();
            String queryType = req.getRequest().getQueryType();
            String shipperType = req.getRequest().getShipperType();
            String shipMode = req.getRequest().getShipMode();
            String instantConfigType = req.getRequest().getInstantConfigType();
            String levelCategory = req.getRequest().getLevelCategory();
            String delayTimeSpan = req.getRequest().getDelayTimeSpan();
            if (delayTimeSpan!=null&&!delayTimeSpan.trim().isEmpty())
            {
                int delayTimeSpan_i = 60;
                try {
                    delayTimeSpan_i = Integer.parseInt(delayTimeSpan);
                }
                catch (Exception e)
                {

                }
                delayTimeSpan = delayTimeSpan_i+"";
            }

            String isTest = req.getRequest().getIsTest();
            if (isTest!=null)
            {
                if (!"Y".equals(isTest))
                {
                    isTest = "N";
                }
            }

			String isDelay = req.getRequest().getIsDelay();
			if (isDelay!=null)
			{
				if (!"Y".equals(isDelay))
				{
					isDelay = "N";
				}
			}


			UptBean ub1 = null;	
			ub1 = new UptBean("DCP_OUTSALESET");
			//Value
			ub1.addUpdateValue("APPSECRET", new DataValue(appSecret, Types.VARCHAR));
			ub1.addUpdateValue("APPSIGNKEY", new DataValue(appSignKey, Types.VARCHAR));
			ub1.addUpdateValue("SHOPCODE", new DataValue(shopCode, Types.VARCHAR));
			ub1.addUpdateValue("APIURL", new DataValue(apiUrl, Types.VARCHAR));
			ub1.addUpdateValue("APIURLTWO", new DataValue(apiUrlTwo, Types.VARCHAR));
			ub1.addUpdateValue("APIURLTHREE", new DataValue(apiUrlThree, Types.VARCHAR));
			ub1.addUpdateValue("IV", new DataValue(iv, Types.VARCHAR));
			ub1.addUpdateValue("V", new DataValue(v, Types.VARCHAR));				
			ub1.addUpdateValue("DELIVERYMODE", new DataValue(deliveryMode, Types.VARCHAR));
			ub1.addUpdateValue("PRINTMODE", new DataValue(printMode, Types.VARCHAR));
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
			ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));	
			ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
			if (shopType!=null)
            {
                ub1.addUpdateValue("SHOPTYPE", new DataValue(shopType, Types.VARCHAR));
            }
            if (appName!=null)
            {
                ub1.addUpdateValue("APPNAME", new DataValue(appName, Types.VARCHAR));
            }
            if (ytoType!=null)
            {
                ub1.addUpdateValue("YTOTYPE", new DataValue(ytoType, Types.VARCHAR));
            }
            if (productType!=null)
            {
                ub1.addUpdateValue("PRODUCTTYPE", new DataValue(productType, Types.VARCHAR));
            }
            if (queryType!=null)
            {
                ub1.addUpdateValue("QUERYTYPE", new DataValue(queryType, Types.VARCHAR));
            }
            if (shipperType!=null)
            {
                ub1.addUpdateValue("SHIPPERTYPE", new DataValue(shipperType, Types.VARCHAR));
            }
            if (shipMode!=null)
            {
                ub1.addUpdateValue("SHIPMODE", new DataValue(shipMode, Types.VARCHAR));
            }
            if (instantConfigType!=null)
            {
                ub1.addUpdateValue("INSTANTCONFIGTYPE", new DataValue(instantConfigType, Types.VARCHAR));
            }
            if (levelCategory!=null)
            {
                ub1.addUpdateValue("LEVELCATEGORY", new DataValue(levelCategory, Types.VARCHAR));
            }
            if (isTest!=null)
            {
                ub1.addUpdateValue("ISTEST", new DataValue(isTest, Types.VARCHAR));
            }
            if (delayTimeSpan!=null&&!delayTimeSpan.trim().isEmpty())
            {
                ub1.addUpdateValue("DELAYTIME_SPAN", new DataValue(delayTimeSpan, Types.VARCHAR));
            }
			if (isDelay!=null)
			{
				ub1.addUpdateValue("ISDELAY", new DataValue(isDelay, Types.VARCHAR));
			}

			// condition	
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("DELIVERYTYPE", new DataValue(deliveryType, Types.VARCHAR));
			ub1.addCondition("APPID", new DataValue(appId, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub1));

			if (shopType!=null&&"1".equals(shopType))
            {
                //改成适用于全部门店
                DelBean db_shop = new DelBean("DCP_OUTSALESET_SHOP");
                db_shop.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                db_shop.addCondition("DELIVERYTYPE", new DataValue(deliveryType, Types.VARCHAR));
                db_shop.addCondition("APPID", new DataValue(appId, Types.VARCHAR));
                this.addProcessData(new DataProcessBean(db_shop));
            }

			this.doExecuteDataToDB();	

		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_DeliverySettingUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DeliverySettingUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DeliverySettingUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DeliverySettingUpdateReq req) throws Exception 
	{
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		if(req.getRequest()==null)
		{
			errMsg.append("requset不能为空值 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		String deliveryType = req.getRequest().getDeliveryType().toString();
		String appId = req.getRequest().getAppId().toString();
		String status = req.getRequest().getStatus();
        String shipMode = req.getRequest().getShipMode();

		if (Check.Null(deliveryType)) 
		{
			errCt++;
			errMsg.append("物流类型不可为空值  ");
			isFail = true;
		}
		if (Check.Null(appId)) 
		{
			errCt++;
			errMsg.append("物流id不可为空值  ");
			isFail = true;
		}
		if (Check.Null(status)) 
		{
			errCt++;
			errMsg.append("状态不可为空值  ");
			isFail = true;
		}
        if ("KDN".equals(deliveryType))
        {
            if (Check.Null(shipMode))
            {
                errCt++;
                errMsg.append("快递鸟物流的配送类型shipMode不能为空值，");
                isFail = true;
            }
            else
            {
                if ("1".equals(shipMode))
                {
                    if (Check.Null(req.getRequest().getProductType()))
                    {
                        errCt++;
                        errMsg.append("快递鸟同城物流，物品类型productType不可为空值，");
                        isFail = true;
                    }
                    if (Check.Null(req.getRequest().getInstantConfigType()))
                    {
                        errCt++;
                        errMsg.append("快递鸟同城物流，即时配类型instantConfigType不可为空值，");
                        isFail = true;
                    }
                    if (Check.Null(req.getRequest().getLevelCategory()))
                    {
                        errCt++;
                        errMsg.append("快递鸟同城物流，三级类目编码levelCategory不可为空值，");
                        isFail = true;
                    }
                }
            }
        }
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_DeliverySettingUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DeliverySettingUpdateReq>(){};
	}

	@Override
	protected DCP_DeliverySettingUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DeliverySettingUpdateRes();
	}

    /**
     * 验证是否已存在
     * @param eId
     * @param deliverytype
     * @param appId
     * @return
     */
	private String isRepeat(String eId, String deliverytype,String appId)
	{
		String sql = "select DELIVERYTYPE from DCP_OUTSALESET where EID = '"+eId+"' "
				+ " and DELIVERYTYPE = '"+deliverytype+ "' and APPID='"+appId+"' ";
		return sql;

	}

}
