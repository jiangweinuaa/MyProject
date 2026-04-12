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
import com.dsc.spos.json.cust.req.DCP_DeliverySettingCreateReq;
import com.dsc.spos.json.cust.res.DCP_DeliverySettingCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 货运厂商新增
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_DeliverySettingCreate extends SPosAdvanceService<DCP_DeliverySettingCreateReq, DCP_DeliverySettingCreateRes> 
{
	@Override
	protected void processDUID(DCP_DeliverySettingCreateReq req, DCP_DeliverySettingCreateRes res) throws Exception 
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
			if (shopType==null)
            {
                shopType = "1";
            }
			if (!"2".equals(shopType))
            {
                shopType = "1";//默认1
            }
			String appName = req.getRequest().getAppName();
			String ytoType = req.getRequest().getYtoType();
			if (!"Y".equals(ytoType))
            {
                ytoType = "N";
            }
            String productType = req.getRequest().getProductType();
            if (productType==null)
            {
                productType = "";
            }

            String queryType = req.getRequest().getQueryType();
            if (!"8001".equals(queryType))
            {
                queryType = "8002";
            }

            String shipperType = req.getRequest().getShipperType();
            if (shipperType==null)
            {
                shipperType = "5";
            }

            String shipMode = req.getRequest().getShipMode();
            if (shipMode==null)
            {
                shipMode = "0";//KDN物流产品类型 0全国快递，1同城快递
            }
            String instantConfigType = req.getRequest().getInstantConfigType();

            String levelCategory = req.getRequest().getLevelCategory();

            String delayTimeSpan = req.getRequest().getDelayTimeSpan();
            if (delayTimeSpan==null)
            {
                delayTimeSpan = "60";
            }
            int delayTimeSpan_i = 60;
            try {
                delayTimeSpan_i = Integer.parseInt(delayTimeSpan);
            }
            catch (Exception e)
            {

            }

            String isTest = req.getRequest().getIsTest();
            if (!"Y".equals(isTest))
            {
                isTest = "N";
            }

			String isDelay = req.getRequest().getIsDelay();
			if (!"Y".equals(isDelay))
			{
				isDelay = "N";
			}

			sql = this.isRepeat(eId,deliveryType,appId);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData.isEmpty()) 
			{
				String[] columns1 = {
						"EID" ,
						"DELIVERYTYPE",
						"APPID",
						"APPSECRET",
						"APPSIGNKEY",
						"SHOPCODE",
						"APIURL",
                        "APPNAME",
						"APIURLTWO",
						"APIURLTHREE",
						"IV",
						"V",
						"DELIVERYMODE",
						"PRINTMODE",
						"STATUS",
						"CREATEOPID",
						"CREATEOPNAME",
						"CREATETIME",
						"LASTMODIOPID",
						"LASTMODIOPNAME",
						"LASTMODITIME",
                        "SHOPTYPE",
                        "YTOTYPE",
                        "PRODUCTTYPE",
                        "QUERYTYPE",
                        "SHIPPERTYPE",
                        "SHIPMODE",
                        "INSTANTCONFIGTYPE",
                        "LEVELCATEGORY",
                        "ISTEST",
                        "DELAYTIME_SPAN",
						"ISDELAY"
				};
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]
						{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(deliveryType, Types.VARCHAR),
								new DataValue(appId, Types.VARCHAR),
								new DataValue(appSecret, Types.VARCHAR),
								new DataValue(appSignKey, Types.VARCHAR),
								new DataValue(shopCode, Types.VARCHAR),
								new DataValue(apiUrl, Types.VARCHAR),
                                new DataValue(appName, Types.VARCHAR),
								new DataValue(apiUrlTwo, Types.VARCHAR),							
								new DataValue(apiUrlThree, Types.VARCHAR),							
								new DataValue(iv, Types.VARCHAR),	
								new DataValue(v, Types.VARCHAR),
								new DataValue(deliveryMode, Types.VARCHAR),
								new DataValue(printMode, Types.VARCHAR),
								new DataValue(status, Types.VARCHAR),
								new DataValue(req.getOpNO(), Types.VARCHAR),
								new DataValue(req.getOpName(), Types.VARCHAR),
								new DataValue(lastmoditime, Types.DATE),
								new DataValue(req.getOpNO(), Types.VARCHAR),
								new DataValue(req.getOpName(), Types.VARCHAR),
								new DataValue(lastmoditime, Types.DATE),
                                new DataValue(shopType, Types.VARCHAR),
                                new DataValue(ytoType, Types.VARCHAR),
                                new DataValue(productType, Types.VARCHAR),
                                new DataValue(queryType, Types.VARCHAR),
                                new DataValue(shipperType, Types.VARCHAR),
                                new DataValue(shipMode, Types.VARCHAR),
                                new DataValue(instantConfigType, Types.VARCHAR),
                                new DataValue(levelCategory, Types.VARCHAR),
                                new DataValue(isTest, Types.VARCHAR),
                                new DataValue(delayTimeSpan_i, Types.VARCHAR),
								new DataValue(isDelay, Types.VARCHAR)
						};
				InsBean ib1 = new InsBean("DCP_OUTSALESET", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增单头
				this.doExecuteDataToDB();


                res.setSuccess(true);
                res.setServiceStatus("000");
                res.setServiceDescription("服务执行成功");

			}
			else
			{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("物流类型：" +deliveryType+"，物流id："+appId+" 的信息 已存在，请勿重复添加");
			}
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
	protected List<InsBean> prepareInsertData(DCP_DeliverySettingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_DeliverySettingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_DeliverySettingCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_DeliverySettingCreateReq req) throws Exception 
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
			errMsg.append("物流类型不可为空值，");
			isFail = true;
		}
		if (Check.Null(appId)) 
		{
			errCt++;
			errMsg.append("物流id不可为空值，");
			isFail = true;
		}
		if (Check.Null(status)) 
		{
			errCt++;
			errMsg.append("状态不可为空值，");
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
	protected TypeToken<DCP_DeliverySettingCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_DeliverySettingCreateReq>(){};
	}

	@Override
	protected DCP_DeliverySettingCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_DeliverySettingCreateRes();
	}

	/**
	 * 验证是否已存在
	 * @param eId
	 * @param lgPlatformNo
	 * @return
	 */
	private String isRepeat(String eId, String deliverytype,String appId){
		String sql = "select DELIVERYTYPE from DCP_OUTSALESET where EID = '"+eId+"' "
				+ " and DELIVERYTYPE = '"+deliverytype+ "' and APPID='"+appId+"' ";
		return sql;

	}

}
