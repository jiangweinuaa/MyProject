package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECCommerceCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECCommerceCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 电商平台信息新增 
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECCommerceCreate extends SPosAdvanceService<DCP_OrderECCommerceCreateReq, DCP_OrderECCommerceCreateRes> {

	@Override
	protected void processDUID(DCP_OrderECCommerceCreateReq req, DCP_OrderECCommerceCreateRes res) throws Exception {
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
			
			
			String momo_EntpId = req.getMomo_EntpId();
			String momo_EntpCode = req.getMomo_EntpCode();
			String momo_Pwd = req.getMomo_Pwd();
					
			String memberGet = req.getMemberGet();
			String orderShop = req.getOrderShop();
			//String orderShopName = req.getOrderShopName();
			String orderWarehouse = req.getOrderWarehouse();
			//String orderWarehouseName = req.getOrderWarehouseName();
			String currencyNo = req.getCurrencyNo();
			String customerNo = req.getCustomerNO();
			String canInvoice = req.getCanInvoice();
			String status = req.getStatus();
			String OTHER_NO=req.getPlatformID();
			if(OTHER_NO==null||OTHER_NO.isEmpty())
			{
				OTHER_NO=ecPlatformNo;
			}
			String ENABLE_DISPATCHING=req.getIsneedPro();
			
			String ecStockRatio = req.getEcStockRatio();
			if (Check.Null(ecStockRatio)) 
			{
				ecStockRatio="0";
			}
			
			
			sql = this.isRepeat(eId, ecPlatformNo, OTHER_NO);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if (getQData.isEmpty()) {
				String[] columns1 = {
						"EID",
						"ECPLATFORMNO",
						"ECPLATFORMNAME",
						"ECPLATFORMURL",
						"ECPLATFORMHOTLINE",
						"API_URL",
						"API_KEY",
						"API_SECRET",
						"PARTNER_ID",
						"STORE_ID",
						"PARTNER_KEY",
						"APPKEY",
						"TOKEN",
						"SALTKEY",
						"SHOPSN",
						"LTSECRETKEY",
						"LTLICENSEKEY",
						"STATUS",
						"MEMBERGET",
						"SHOPID",
						"WAREHOUSE",
						"CURRENCYNO",
						"ECCUSTOMERNO",
						"ISINVOICE",
						"MOMO_ENTPID",
						"MOMO_ENTPCODE",
						"MOMO_PWD",
						"OTHER_NO",
						"ENABLE_DISPATCHING",
						"LTSHOPURL",
						"ECPLATFORMRATIO"
							};
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]
						{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(ecPlatformNo, Types.VARCHAR),
							new DataValue(ecPlatformName, Types.VARCHAR),
							new DataValue(ecPlatformUrl, Types.VARCHAR),
							new DataValue(ecPlatformHotline, Types.VARCHAR),
							new DataValue(apiUrl, Types.VARCHAR),
							new DataValue(yh_Apikey, Types.VARCHAR),
							new DataValue(yh_Apisecret, Types.VARCHAR),						
							new DataValue(xp_PartnerId, Types.VARCHAR),							
							new DataValue(xp_ShopId, Types.VARCHAR),							
							new DataValue(xp_PartnerKey, Types.VARCHAR),	
							new DataValue(jy_Apikey, Types.VARCHAR),
							new DataValue(jy_Token, Types.VARCHAR),
							new DataValue(jy_Saltkey, Types.VARCHAR),
							new DataValue(jy_Shopsn, Types.VARCHAR),
							
							new DataValue(lt_Secretkey, Types.VARCHAR),
							new DataValue(lt_Licensekey, Types.VARCHAR),
							new DataValue(status, Types.VARCHAR),
							
							new DataValue(memberGet, Types.VARCHAR),
							new DataValue(orderShop, Types.VARCHAR),
							new DataValue(orderWarehouse, Types.VARCHAR),
							new DataValue(currencyNo, Types.VARCHAR),
							new DataValue(customerNo, Types.VARCHAR),
							new DataValue(canInvoice, Types.VARCHAR),
							new DataValue(momo_EntpId, Types.VARCHAR),
							new DataValue(momo_EntpCode, Types.VARCHAR),
							new DataValue(momo_Pwd, Types.VARCHAR),
							new DataValue(OTHER_NO, Types.VARCHAR),
							new DataValue(ENABLE_DISPATCHING, Types.VARCHAR),
							new DataValue(lt_ShopUrl, Types.VARCHAR),
							new DataValue(ecStockRatio, Types.VARCHAR)
						};
				InsBean ib1 = new InsBean("OC_ECOMMERCE", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增单头
				this.doExecuteDataToDB();
				if (res.isSuccess()) 
				{
					res.setServiceStatus("000");
					res.setServiceDescription("服务执行成功");						
				} 
				
			}else{
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("编码为" +ecPlatformNo+", 平台标识 "+ OTHER_NO +" 的平台信息 已存在，请勿重复添加");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");	
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECCommerceCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECCommerceCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECCommerceCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECCommerceCreateReq req) throws Exception {
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
	protected TypeToken<DCP_OrderECCommerceCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECCommerceCreateReq>(){};
	}

	@Override
	protected DCP_OrderECCommerceCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECCommerceCreateRes();
	}
	
	/**
	 * 验证是否已存在
	 * @param eId
	 * @param ecPlatformNo
	 * @return
	 */
	private String isRepeat(String eId, String ecPlatformNo, String otherNo ){
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		// 2020-03-16 按照武小凤要求，other_NO变成了主键  ， ecPlatformNo 不是主键
		sqlbuf.append("select ecPlatformNo from OC_ECOMMERCE where EID = '"+eId+"' "
//				+ " and ecPlatformNo = '"+ecPlatformNo+ "' "
				+ " and other_No = '"+otherNo+"' "
						);
		sql = sqlbuf.toString();
		return sql;
		
	}
	

}
