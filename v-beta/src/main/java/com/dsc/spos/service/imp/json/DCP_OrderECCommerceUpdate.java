package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECCommerceUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECCommerceUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 电商平台信息修改 
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECCommerceUpdate extends SPosAdvanceService<DCP_OrderECCommerceUpdateReq, DCP_OrderECCommerceUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderECCommerceUpdateReq req, DCP_OrderECCommerceUpdateRes res) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = null;
		try {
			String eId = req.geteId();
			String ecPlatformNo = req.getEcPlatformNo();
			String ecPlatformName = req.getEcPlatformName();
			String ecPlatformUrl = req.getEcPlatformUrl();
			String ecPlatformHotline = req.getEcPlatformHotline();
			String apiUrl = req.getApiUrl();
			String yh_Apikey = req.getYh_Apikey();
			String yh_Apisecret = req.getYh_Apisecret();
			String xp_PartnerId = req.getXp_PartnerId();
			String xp_ShopId = req.getXp_ShopId();
			String xp_PartnerKey = req.getXp_PartnerKey();
			String jy_Apikey = req.getJy_Apikey();
			String jy_Token = req.getJy_Token();
			String jy_Saltkey = req.getJy_Saltkey();
			String jy_Shopsn = req.getJy_Shopsn();
			
			String lt_Secretkey = req.getLt_Secretkey();
			String lt_Licensekey = req.getLt_Licensekey();
			String lt_ShopUrl = req.getLt_ShopUrl();
			String status = req.getStatus();
			
			String memberGet = req.getMemberGet();
			String orderShop = req.getOrderShop();
			//String orderShopName = req.getOrderShopName();
			String orderWarehouse = req.getOrderWarehouse();
			//String orderWarehouseName = req.getOrderWarehouseName();
			String currencyNo = req.getCurrencyNo();
			String customerNo = req.getCustomerNO();
			String canInvoice = req.getCanInvoice();
			
			String momo_EntpId = req.getMomo_EntpId();
			String momo_EntpCode = req.getMomo_EntpCode();
			String momo_Pwd = req.getMomo_Pwd();
			String ecStockRatio = req.getEcStockRatio();
			if (Check.Null(ecStockRatio)) 
			{
				ecStockRatio="0";
			}
			
			UptBean ub1 = null;	
			ub1 = new UptBean("OC_ECOMMERCE");
			//Value
			ub1.addUpdateValue("ECPLATFORMNAME", new DataValue(ecPlatformName, Types.VARCHAR));
			ub1.addUpdateValue("ECPLATFORMURL", new DataValue(ecPlatformUrl, Types.VARCHAR));
			ub1.addUpdateValue("ECPLATFORMHOTLINE", new DataValue(ecPlatformHotline, Types.VARCHAR));
			ub1.addUpdateValue("API_URL", new DataValue(apiUrl, Types.VARCHAR));
			ub1.addUpdateValue("API_KEY", new DataValue(yh_Apikey, Types.VARCHAR));
			ub1.addUpdateValue("API_SECRET", new DataValue(yh_Apisecret, Types.VARCHAR));
			ub1.addUpdateValue("PARTNER_ID", new DataValue(xp_PartnerId, Types.VARCHAR));
			ub1.addUpdateValue("STORE_ID", new DataValue(xp_ShopId, Types.VARCHAR));				
			ub1.addUpdateValue("PARTNER_KEY", new DataValue(xp_PartnerKey, Types.VARCHAR));
			ub1.addUpdateValue("APPKEY", new DataValue(jy_Apikey, Types.VARCHAR));
			ub1.addUpdateValue("TOKEN", new DataValue(jy_Token, Types.VARCHAR));
			ub1.addUpdateValue("SALTKEY", new DataValue(jy_Saltkey, Types.VARCHAR));
			ub1.addUpdateValue("SHOPSN", new DataValue(jy_Shopsn, Types.VARCHAR));
			
			ub1.addUpdateValue("LTSECRETKEY", new DataValue(lt_Secretkey, Types.VARCHAR));
			ub1.addUpdateValue("LTLICENSEKEY", new DataValue(lt_Licensekey, Types.VARCHAR));	
			ub1.addUpdateValue("LTSHOPURL", new DataValue(lt_ShopUrl, Types.VARCHAR));	
			ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			
			ub1.addUpdateValue("MEMBERGET", new DataValue(memberGet, Types.VARCHAR));
			ub1.addUpdateValue("SHOPID", new DataValue(orderShop, Types.VARCHAR));
			ub1.addUpdateValue("WAREHOUSE", new DataValue(orderWarehouse, Types.VARCHAR));	
			ub1.addUpdateValue("CURRENCYNO", new DataValue(currencyNo, Types.VARCHAR));
			ub1.addUpdateValue("ECCUSTOMERNO", new DataValue(customerNo, Types.VARCHAR));
			ub1.addUpdateValue("ISINVOICE", new DataValue(canInvoice, Types.VARCHAR));
			
			ub1.addUpdateValue("MOMO_ENTPID", new DataValue(momo_EntpId, Types.VARCHAR));
			ub1.addUpdateValue("MOMO_ENTPCODE", new DataValue(momo_EntpCode, Types.VARCHAR));
			ub1.addUpdateValue("MOMO_PWD", new DataValue(momo_Pwd, Types.VARCHAR));
			ub1.addUpdateValue("ENABLE_DISPATCHING", new DataValue(req.getIsneedPro(), Types.VARCHAR));
			ub1.addUpdateValue("ECPLATFORMRATIO", new DataValue(ecStockRatio, Types.VARCHAR));
			ub1.addUpdateValue("OTHER_NO", new DataValue(req.getPlatformID(), Types.VARCHAR));
			
			// condition	
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
//			ub1.addCondition("ECPLATFORMNO", new DataValue(ecPlatformNo, Types.VARCHAR));
			// 2020-03-16 按照武小凤要求，other_NO变成了主键， 以other_NO 为更新条件 （ 没考虑到页面允许改 platformId ）
			ub1.addCondition("OTHER_NO", new DataValue(req.getPlatformID(), Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(ub1));
			
			this.doExecuteDataToDB();	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECCommerceUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECCommerceUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECCommerceUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECCommerceUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		String ecPlatformNo = req.getEcPlatformNo();
		String ecPlatformName = req.getEcPlatformName();
		String ecStockRatio = req.getEcStockRatio();
		
		if (Check.Null(ecPlatformNo)) 
		{
			errCt++;
			errMsg.append("电商平台代号不可为空值  ");
			isFail = true;
		}
		if (Check.Null(ecPlatformName)) 
		{
			errCt++;
			errMsg.append("电商平台名称不可为空值  ");
			isFail = true;
		}
		
		if (!Check.Null(ecStockRatio)) 
		{
			if (!PosPub.isNumeric(ecStockRatio))
			{
				errCt++;
				errMsg.append("库存比例必须为整数  ");
				isFail = true;
			}
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECCommerceUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECCommerceUpdateReq>(){};
	}

	@Override
	protected DCP_OrderECCommerceUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECCommerceUpdateRes();
	}
	

}
