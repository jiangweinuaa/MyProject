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
import com.dsc.spos.json.cust.req.DCP_OrderEcommerceUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderEcommerceUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderEcommerceUpdate extends SPosAdvanceService<DCP_OrderEcommerceUpdateReq,DCP_OrderEcommerceUpdateRes>
{

	@Override
	protected void processDUID(DCP_OrderEcommerceUpdateReq req, DCP_OrderEcommerceUpdateRes res) throws Exception
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();

		String channelId = req.getRequest().getChannelId();	
		String loadDocType = req.getRequest().getLoadDocType();	
		String expireTime = req.getRequest().getExpireTime()==null?"":req.getRequest().getExpireTime();
		String publicKey = req.getRequest().getPublicKey()==null?"":req.getRequest().getPublicKey();
		if (orderLoadDocType.YOUZAN.equals(loadDocType))
        {
        	publicKey = req.getRequest().getApiSecret();//兼容下有赞
        }
		// //订单审核类型(1或者空-支持审核所有订单；2-仅支持审核配送订单)
		String reviewType = req.getRequest().getReviewType();
		if (reviewType==null||reviewType.trim().isEmpty())
        {
            reviewType = "1";
        }

        // 订单审核是否到门店 Y：是；N：否
        String isReviewToShop = req.getRequest().getIsReviewToShop();
        if (!"Y".equals(isReviewToShop))
        {
            isReviewToShop = "N";
        }

		String sql = "select CHANNELID from DCP_ECOMMERCE where eid='"+eId+"' and CHANNELID='"+channelId+"' and LOADDOCTYPE='"+loadDocType+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData==null || getData.isEmpty()) 
		{
			String[] columns_DCP_ECOMMERCE = 
				{ 
						"EID","CHANNELID","LOADDOCTYPE","ECPLATFORMURL","ECPLATFORMHOTLINE","APIURL","APIKEY",
						"APISECRET","APISIGN","TOKEN","BRANDID","SHIPPINGSHOPNO","WAREHOUSE",
						"STOREID","CURRENCYNO","ECCUSTOMERNO","ISREVIEW","ISPRODISPATCH",
						"ISORDERLOCKSTOCK","ISTEST","ISJBP","STATUS",
						"LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","ORDERGOODSDELIVER",
						"SUPPORTINVOICE","EXPIRETIME","PUBLICKEY","PARTNERAPIURL","PARTNERAPIKEY","PARTNERAPISECRET","PARTNERAPISIGN","PARTNERMEMBER","REVIEWTYPE","ISREVIEWTOSHOP"
				};		

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(channelId, Types.VARCHAR),
					new DataValue(loadDocType, Types.VARCHAR),
					new DataValue(req.getRequest().getEcPlatformUrl(), Types.VARCHAR),
					new DataValue(req.getRequest().getEcPlatformHotline(), Types.VARCHAR),
					new DataValue(req.getRequest().getApiUrl(), Types.VARCHAR),
					new DataValue(req.getRequest().getApiKey(), Types.VARCHAR),
					new DataValue(req.getRequest().getApiSecret(), Types.VARCHAR),
					new DataValue(req.getRequest().getApiSign(), Types.VARCHAR),
					new DataValue(req.getRequest().getToken(), Types.VARCHAR),
					new DataValue(req.getRequest().getBrandId(), Types.VARCHAR),
					new DataValue(req.getRequest().getShippingShopNo(), Types.VARCHAR),
					new DataValue(req.getRequest().getWarehouse(), Types.VARCHAR),
					new DataValue(req.getRequest().getStoreId(), Types.VARCHAR),
					new DataValue(req.getRequest().getCurrencyNo(), Types.INTEGER),
					new DataValue(req.getRequest().getCustomerNo(), Types.VARCHAR),
					new DataValue(req.getRequest().getIsReview(), Types.VARCHAR),
					new DataValue(req.getRequest().getIsProDispatch(), Types.VARCHAR),
					new DataValue(req.getRequest().getIsOrderLockStock(), Types.VARCHAR),
					new DataValue(req.getRequest().getIsTest(), Types.VARCHAR),
					new DataValue(req.getRequest().getIsJbp(), Types.VARCHAR),
					new DataValue(req.getRequest().getStatus(), Types.VARCHAR),
					new DataValue(req.getOpNO(), Types.VARCHAR),	
					new DataValue(req.getOpName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE),
					new DataValue(req.getRequest().getOrderGoodsDeliver(), Types.VARCHAR),
					new DataValue(req.getRequest().getSupportInvoice(), Types.VARCHAR),
					new DataValue(expireTime, Types.VARCHAR),
                    new DataValue(publicKey, Types.VARCHAR),
					new DataValue(req.getRequest().getPartnerApiUrl(), Types.VARCHAR),
					new DataValue(req.getRequest().getPartnerApiKey(), Types.VARCHAR),
					new DataValue(req.getRequest().getPartnerApiSecret(), Types.VARCHAR),
					new DataValue(req.getRequest().getPartnerApiSign(), Types.VARCHAR),
					new DataValue(req.getRequest().getPartnerMember(), Types.VARCHAR),
                    new DataValue(reviewType, Types.VARCHAR),
                    new DataValue(isReviewToShop, Types.VARCHAR)
			};

			InsBean ib1 = new InsBean("DCP_ECOMMERCE", columns_DCP_ECOMMERCE);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增

            if ("Y".equals(isReviewToShop))
            {
                if (req.getRequest().getReviewShopList()!=null&&!req.getRequest().getReviewShopList().isEmpty())
                {
                    DelBean del1 = new DelBean("DCP_CHANNELREVIEW_SHOP");
                    del1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
                    del1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(del1));
                    String[] columns2 =
                            {
                                    "EID","CHANNELID","LOADDOCTYPE","SHOPNO","SHOPNAME","LASTMODIFYID","LASTMODIFYTIME",
                            };
                    for (DCP_OrderEcommerceUpdateReq.levelReviewShop par : req.getRequest().getReviewShopList())
                    {
                        if (par.getShopNo()==null||par.getShopNo().isEmpty())
                        {
                            continue;
                        }
                        DataValue[] insValue2 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(channelId, Types.VARCHAR),
                                new DataValue(loadDocType, Types.VARCHAR),
                                new DataValue(par.getShopNo(), Types.VARCHAR),
                                new DataValue(par.getShopName(), Types.VARCHAR),
                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE),
                        };

                        InsBean ib2 = new InsBean("DCP_CHANNELREVIEW_SHOP", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2)); // 新增
                    }
                }

            }
		}
		else {
            UptBean ub1 = new UptBean("DCP_ECOMMERCE");
            ub1.addUpdateValue("ECPLATFORMURL", new DataValue(req.getRequest().getEcPlatformUrl(), Types.VARCHAR));
            ub1.addUpdateValue("ECPLATFORMHOTLINE", new DataValue(req.getRequest().getEcPlatformHotline(), Types.VARCHAR));
            ub1.addUpdateValue("APIURL", new DataValue(req.getRequest().getApiUrl(), Types.VARCHAR));
            ub1.addUpdateValue("APIKEY", new DataValue(req.getRequest().getApiKey(), Types.VARCHAR));
            ub1.addUpdateValue("APISECRET", new DataValue(req.getRequest().getApiSecret(), Types.VARCHAR));
            if (orderLoadDocType.YOUZAN.equals(loadDocType)) {
                ub1.addUpdateValue("PUBLICKEY", new DataValue(publicKey, Types.VARCHAR));
            }
            ub1.addUpdateValue("APISIGN", new DataValue(req.getRequest().getApiSign(), Types.VARCHAR));
            ub1.addUpdateValue("TOKEN", new DataValue(req.getRequest().getToken(), Types.VARCHAR));
            ub1.addUpdateValue("BRANDID", new DataValue(req.getRequest().getBrandId(), Types.VARCHAR));
            ub1.addUpdateValue("SHIPPINGSHOPNO", new DataValue(req.getRequest().getShippingShopNo(), Types.VARCHAR));
            ub1.addUpdateValue("WAREHOUSE", new DataValue(req.getRequest().getWarehouse(), Types.VARCHAR));
            ub1.addUpdateValue("STOREID", new DataValue(req.getRequest().getStoreId(), Types.VARCHAR));
            ub1.addUpdateValue("CURRENCYNO", new DataValue(req.getRequest().getCurrencyNo(), Types.VARCHAR));
            ub1.addUpdateValue("ECCUSTOMERNO", new DataValue(req.getRequest().getCustomerNo(), Types.VARCHAR));
            ub1.addUpdateValue("ISREVIEW", new DataValue(req.getRequest().getIsReview(), Types.VARCHAR));
            ub1.addUpdateValue("ISPRODISPATCH", new DataValue(req.getRequest().getIsProDispatch(), Types.VARCHAR));
            ub1.addUpdateValue("ISORDERLOCKSTOCK", new DataValue(req.getRequest().getIsOrderLockStock(), Types.VARCHAR));
            ub1.addUpdateValue("ISTEST", new DataValue(req.getRequest().getIsTest(), Types.VARCHAR));
            ub1.addUpdateValue("ISJBP", new DataValue(req.getRequest().getIsJbp(), Types.VARCHAR));
            ub1.addUpdateValue("STATUS", new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(), Types.VARCHAR));
            ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime, Types.DATE));
            ub1.addUpdateValue("ORDERGOODSDELIVER", new DataValue(req.getRequest().getOrderGoodsDeliver(), Types.VARCHAR));
            ub1.addUpdateValue("SUPPORTINVOICE", new DataValue(req.getRequest().getSupportInvoice(), Types.VARCHAR));
            if (req.getRequest().getExpireTime() != null) {
                ub1.addUpdateValue("EXPIRETIME", new DataValue(req.getRequest().getExpireTime(), Types.VARCHAR));
            }

            // 20220606 企迈对接 新增字段
            ub1.addUpdateValue("PARTNERAPIURL", new DataValue(req.getRequest().getPartnerApiUrl(), Types.VARCHAR));
            ub1.addUpdateValue("PARTNERAPIKEY", new DataValue(req.getRequest().getPartnerApiKey(), Types.VARCHAR));
            ub1.addUpdateValue("PARTNERAPISECRET", new DataValue(req.getRequest().getPartnerApiSecret(), Types.VARCHAR));
            ub1.addUpdateValue("PARTNERAPISIGN", new DataValue(req.getRequest().getPartnerApiSign(), Types.VARCHAR));
            ub1.addUpdateValue("PARTNERMEMBER", new DataValue(Check.Null(req.getRequest().getPartnerMember()) ? "digiwin" : req.getRequest().getPartnerMember(), Types.VARCHAR));

            ub1.addUpdateValue("CARDSITE", new DataValue(req.getRequest().getCardSite(), Types.VARCHAR));
            if (req.getRequest().getReviewType()!=null)
            {
                ub1.addUpdateValue("REVIEWTYPE", new DataValue(reviewType, Types.VARCHAR));
            }
            if (req.getRequest().getIsReviewToShop()!=null)
            {
                ub1.addUpdateValue("ISREVIEWTOSHOP", new DataValue(isReviewToShop, Types.VARCHAR));
            }

            ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
            ub1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
            ub1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
            this.addProcessData(new DataProcessBean(ub1));


            if ("Y".equals(isReviewToShop))
            {
                if (req.getRequest().getReviewShopList()!=null&&!req.getRequest().getReviewShopList().isEmpty())
                {
                    //先删
                    DelBean del1 = new DelBean("DCP_CHANNELREVIEW_SHOP");
                    del1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
                    del1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
                    del1.addCondition("LOADDOCTYPE", new DataValue(loadDocType, Types.VARCHAR));
                    this.addProcessData(new DataProcessBean(del1));

                    String[] columns2 =
                            {
                                    "EID","CHANNELID","LOADDOCTYPE","SHOPNO","SHOPNAME","LASTMODIFYID","LASTMODIFYTIME",
                            };
                    for (DCP_OrderEcommerceUpdateReq.levelReviewShop par : req.getRequest().getReviewShopList())
                    {
                        if (par.getShopNo()==null||par.getShopNo().isEmpty())
                        {
                            continue;
                        }
                        DataValue[] insValue2 = new DataValue[]{
                                new DataValue(eId, Types.VARCHAR),
                                new DataValue(channelId, Types.VARCHAR),
                                new DataValue(loadDocType, Types.VARCHAR),
                                new DataValue(par.getShopNo(), Types.VARCHAR),
                                new DataValue(par.getShopName(), Types.VARCHAR),
                                new DataValue(req.getOpNO(), Types.VARCHAR),
                                new DataValue(lastmoditime, Types.DATE),
                        };

                        InsBean ib2 = new InsBean("DCP_CHANNELREVIEW_SHOP", columns2);
                        ib2.addValues(insValue2);
                        this.addProcessData(new DataProcessBean(ib2)); // 新增
                    }
                }

            }
        }

		//
		this.doExecuteDataToDB();


		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;


	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderEcommerceUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderEcommerceUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderEcommerceUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderEcommerceUpdateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		if (Check.Null(req.getRequest().getChannelId()))
		{
			isFail = true;
			errMsg.append("渠道编码不能为空 ");
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderEcommerceUpdateReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderEcommerceUpdateReq>(){};
	}

	@Override
	protected DCP_OrderEcommerceUpdateRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderEcommerceUpdateRes();
	}


}
