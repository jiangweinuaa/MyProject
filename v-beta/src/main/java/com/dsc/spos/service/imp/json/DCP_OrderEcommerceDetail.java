package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderEcommerceDetailReq;
import com.dsc.spos.json.cust.res.DCP_OrderEcommerceDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.google.gson.reflect.TypeToken;

public class DCP_OrderEcommerceDetail extends SPosBasicService<DCP_OrderEcommerceDetailReq,DCP_OrderEcommerceDetailRes>
{

	@Override
	protected boolean isVerifyFail(DCP_OrderEcommerceDetailReq req) throws Exception
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
	protected TypeToken<DCP_OrderEcommerceDetailReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderEcommerceDetailReq>(){};
	}

	@Override
	protected DCP_OrderEcommerceDetailRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_OrderEcommerceDetailRes();
	}

	@Override
	protected DCP_OrderEcommerceDetailRes processJson(DCP_OrderEcommerceDetailReq req) throws Exception
	{	
		DCP_OrderEcommerceDetailRes res =this.getResponse();		

		String sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData=this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_OrderEcommerceDetailRes.level1Elm>());
		if (getQData!=null && getQData.isEmpty()==false)
		{
			for (Map<String, Object> map : getQData)
			{
                DCP_OrderEcommerceDetailRes.level1Elm lv1 = res.new level1Elm();
                lv1.setReviewShopList(new ArrayList<>());
                lv1.setLoadDocType(map.get("LOADDOCTYPE").toString());
                lv1.setApiKey(map.get("APIKEY").toString());
                lv1.setApiSecret(map.get("APISECRET").toString());
                lv1.setPublicKey(map.get("PUBLICKEY").toString());
                //有赞做个兼容，妈的气死人，现有不用非要单独搞一个字段
                if (orderLoadDocType.YOUZAN.equals(lv1.getLoadDocType())) {
                    lv1.setApiSecret(map.get("PUBLICKEY").toString());
                }
                lv1.setApiSign(map.get("APISIGN").toString());
                lv1.setApiUrl(map.get("APIURL").toString());
                lv1.setBrandId(map.get("BRANDID").toString());
                lv1.setChannelId(map.get("CHANNELID").toString());
                lv1.setCurrencyNo(map.get("CURRENCYNO").toString());
                lv1.setCustomerName(map.get("CUSTOMERNAME").toString());
                lv1.setCustomerNo(map.get("ECCUSTOMERNO").toString());
                lv1.setEcPlatformHotline(map.get("ECPLATFORMHOTLINE").toString());
                lv1.setEcPlatformUrl(map.get("ECPLATFORMURL").toString());
                lv1.setIsJbp(map.get("ISJBP").toString());
                lv1.setIsOrderLockStock(map.get("ISORDERLOCKSTOCK").toString());
                lv1.setIsProDispatch(map.get("ISPRODISPATCH").toString());
                lv1.setIsReview(map.get("ISREVIEW").toString());
                lv1.setIsTest(map.get("ISTEST").toString());
                lv1.setLastModiOpId(map.get("LASTMODIOPID").toString());
                lv1.setLastModiOpName(map.get("LASTMODIOPNAME").toString());
                lv1.setLastModiTime(map.get("LASTMODITIME").toString());
                lv1.setShippingShopName(map.get("SHIPPINGSHOPNAME").toString());
                lv1.setShippingShopNo(map.get("SHIPPINGSHOPNO").toString());
                lv1.setStatus(map.get("STATUS").toString());
                lv1.setStoreId(map.get("STOREID").toString());
                lv1.setToken(map.get("TOKEN").toString());
                lv1.setWarehouse(map.get("WAREHOUSE").toString());
                lv1.setWarehouseName(map.get("WAREHOUSENAME").toString());
                lv1.setOrderGoodsDeliver(map.get("ORDERGOODSDELIVER").toString());
                lv1.setSupportInvoice(map.get("SUPPORTINVOICE").toString());
                lv1.setExpireTime(map.getOrDefault("EXPIRETIME", "").toString());

                String partnermember = map.get("PARTNERMEMBER").toString();
                if (Check.Null(partnermember)) {
                    partnermember = "digiwin";
                }
                lv1.setPartnerMember(partnermember);
                lv1.setPartnerApiKey(map.get("PARTNERAPIKEY").toString());
                lv1.setPartnerApiUrl(map.get("PARTNERAPIURL").toString());
                lv1.setPartnerApiSecret(map.get("PARTNERAPISECRET").toString());
                lv1.setPartnerApiSign(map.get("PARTNERAPISIGN").toString());
                lv1.setCardSite(map.get("CARDSITE").toString());
                //订单审核类型(1或者空-支持审核所有订单；2-仅支持审核配送订单)
                String reviewType = map.getOrDefault("REVIEWTYPE","1").toString();
                if (Check.Null(reviewType)) {
                    reviewType = "1";
                }
                lv1.setReviewType(reviewType);
                String isReviewToShop = map.getOrDefault("ISREVIEWTOSHOP","N").toString();
                if (Check.Null(isReviewToShop)) {
                    isReviewToShop = "N";
                }
                lv1.setIsReviewToShop(isReviewToShop);
                //这里可以不管Y/N 都查询下，
                List<Map<String,Object>> getReviewShopList = this.getReviewShopList(req.geteId(),lv1.getLoadDocType(),lv1.getChannelId());
                if (getReviewShopList!=null&&!getReviewShopList.isEmpty())
                {
                    for (Map<String,Object> par : getReviewShopList)
                    {
                        DCP_OrderEcommerceDetailRes.levelReviewShop lv2 = res.new levelReviewShop();
                        lv2.setShopNo(par.get("SHOPNO").toString());
                        lv2.setShopName(par.get("SHOPNAME").toString());
                        lv1.getReviewShopList().add(lv2);
                    }
                }


                res.getDatas().add(lv1);
                lv1 = null;
            }
		}


		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderEcommerceDetailReq req) throws Exception
	{
		String eId = req.geteId();
		String channelId = req.getRequest().getChannelId();
		String loadDocType = req.getRequest().getLoadDocType();
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("select a.*,b.org_name as SHIPPINGSHOPNAME,c.warehouse_name as WAREHOUSENAME,d.customer_name as CUSTOMERNAME "
				+ "from DCP_ECOMMERCE a "
				+ "left join dcp_org_lang b on a.eid=b.eid and a.shippingshopno=b.organizationno and b.lang_type='zh_CN' "
				+ "left join dcp_warehouse_lang c on a.eid=c.eid and a.shippingshopno=c.organizationno and a.warehouse=c.warehouse and c.lang_type='zh_CN' "
				+ "left join dcp_customer_lang d on a.eid=d.eid and a.eccustomerno=d.customerno and d.lang_type='zh_CN' "
				+ "where a.eid='"+eId+"' ");

		if (channelId != null && channelId.length()>0)
		{
			sqlbuf.append("and a.CHANNELID='"+channelId+"' ");
		}
		
		if (loadDocType != null && loadDocType.length()>0)
		{
			sqlbuf.append("and a.LOADDOCTYPE='"+loadDocType+"' ");
		}

		sql = sqlbuf.toString();
		return sql;
	}


	private List<Map<String,Object>> getReviewShopList(String eId,String loadDocType,String channelId) throws Exception
    {
        List<Map<String,Object>> mapList = new ArrayList<>();
        try
        {
            String sql = "select * from DCP_CHANNELREVIEW_SHOP where EID='"+eId+"' and LOADDOCTYPE='"+loadDocType+"' and CHANNELID='"+channelId+"'";
            mapList = this.doQueryData(sql,null);
        }
        catch (Exception e)
        {

        }
        return mapList;
    }

}
