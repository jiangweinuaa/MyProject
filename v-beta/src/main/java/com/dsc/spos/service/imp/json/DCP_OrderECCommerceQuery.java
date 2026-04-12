package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECCommerceQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECCommerceQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 电商平台查询
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECCommerceQuery extends SPosBasicService<DCP_OrderECCommerceQueryReq, DCP_OrderECCommerceQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECCommerceQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECCommerceQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECCommerceQueryReq>(){};
	}

	@Override
	protected DCP_OrderECCommerceQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECCommerceQueryRes();
	}

	@Override
	protected DCP_OrderECCommerceQueryRes processJson(DCP_OrderECCommerceQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECCommerceQueryRes res = this.getResponse();
		String sql = null;
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			sql = this.getQuerySql(req);
			String[] conditionValues = {}; //查詢條件
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues);
			
			String num = getQDataDetail.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				res.setDatas(new ArrayList<DCP_OrderECCommerceQueryRes.level1Elm>());
				for (Map<String, Object> oneData : getQDataDetail) 
				{
					DCP_OrderECCommerceQueryRes.level1Elm lev1 = res.new level1Elm();
					String ecPlatformNo = oneData.get("ECPLATFORMNO").toString();
					String ecPlatformName = oneData.get("ECPLATFORMNAME").toString();
					
					lev1.setEcPlatformNo(ecPlatformNo);
					lev1.setEcPlatformName(ecPlatformName);
					
					if(!Check.Null(req.getEcPlatformNo())){ //订单导入导出那里需要查电商平台， 只需要电商平台 编码和名称。 如果查询全部就表示是导入导出那里查询

						String ecPlatformUrl = oneData.get("ECPLATFORMURL").toString();
						String ecPlatformHotline = oneData.get("ECPLATFORMHOTLINE").toString();
						
						String apiUrl = oneData.get("APIURL").toString();
						String yh_Apikey = oneData.get("YH_APIKEY").toString();
						String yh_Apisecret = oneData.get("YH_APISECRET").toString();
						String xp_PartnerId = oneData.get("XP_PARTNERID").toString();
						String xp_ShopId = oneData.get("XP_SHOPID").toString();
						String xp_PartnerKey = oneData.get("XP_PARTNERKEY").toString();
						String jy_Apikey = oneData.get("JY_APIKEY").toString();
						String jy_Token = oneData.get("JY_TOKEN").toString();
						String jy_Saltkey = oneData.get("JY_SALTKEY").toString();
						String jy_Shopsn = oneData.get("JY_SHOPSN").toString();
						
						String lt_Secretkey = oneData.get("LT_SECRETKEY").toString();
						String lt_Licensekey = oneData.get("LT_LICENSEKEY").toString();
						String lt_ShopUrl = oneData.get("LT_SHOPURL").toString();
						String status = oneData.get("STATUS").toString();
						
						String memberGet = oneData.get("MEMBERGET").toString();
						String orderShop = oneData.get("ORDERSHOP").toString();
						String orderShopName = oneData.get("ORDERSHOPNAME").toString(); 
						String orderWarehouse = oneData.get("ORDERWAREHOUSE").toString(); 
						String orderWarehouseName = oneData.get("ORDERWAREHOUSENAME").toString();
						String currencyNo = oneData.get("CURRENCYNO").toString();
						String customerNo = oneData.get("CUSTOMERNO").toString();
						String customerName = oneData.get("CUSTOMERNAME").toString();
						String canInvoice = oneData.get("CANINVOICE").toString();
						
						String momo_EntpId = oneData.get("MOMO_ENTPID").toString();
						String momo_EntpCode = oneData.get("MOMO_ENTPCODE").toString();
						String momo_Pwd = oneData.get("MOMO_PWD").toString();
						String ecStockRatio = oneData.get("ECPLATFORMRATIO").toString(); 
						if (Check.Null(ecStockRatio)) ecStockRatio="0";
						
						
						lev1.setEcPlatformUrl(ecPlatformUrl);
						lev1.setEcPlatformHotline(ecPlatformHotline);
						lev1.setApiUrl(apiUrl);
						lev1.setYh_Apikey(yh_Apikey);
						lev1.setYh_Apisecret(yh_Apisecret);
						lev1.setXp_PartnerId(xp_PartnerId);
						lev1.setXp_ShopId(xp_ShopId);
						lev1.setXp_PartnerKey(xp_PartnerKey);
						lev1.setJy_Apikey(jy_Apikey);
						lev1.setJy_Token(jy_Token);
						lev1.setJy_Saltkey(jy_Saltkey);
						lev1.setJy_Shopsn(jy_Shopsn);
						
						lev1.setLt_Secretkey(lt_Secretkey);
						lev1.setLt_Licensekey(lt_Licensekey);
						lev1.setLt_ShopUrl(lt_ShopUrl);
						
						lev1.setStatus(status);
						lev1.setMemberGet(memberGet);
						lev1.setOrderShop(orderShop);
						lev1.setOrderShopName(orderShopName);
						lev1.setOrderWarehouse(orderWarehouse);
						lev1.setOrderWarehouseName(orderWarehouseName);
						lev1.setCurrencyNo(currencyNo);
						lev1.setCustomerNO(customerNo);
						lev1.setCustomerName(customerName);
						lev1.setCanInvoice(canInvoice);
						
						lev1.setMomo_EntpId(momo_EntpId);
						lev1.setMomo_EntpCode(momo_EntpCode);
						lev1.setMomo_Pwd(momo_Pwd);
						lev1.setPlatformID(oneData.get("OTHER_NO").toString());
						lev1.setIsneedPro(oneData.get("ENABLE_DISPATCHING").toString());
						lev1.setEcStockRatio(ecStockRatio);
					}
					
					res.getDatas().add(lev1);
					
				}
				
			}
		} catch (Exception e) {

		}
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderECCommerceQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");
		String eId = req.geteId();
		String keyTxt = req.getKeyTxt();
		// 2019-12-13 增加 ecPlatformNo 参数
		String ecPlatformNo = req.getEcPlatformNo();
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		String langType = req.getLangType();
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		sqlbuf.append("SELECT " );
		if(Check.Null(ecPlatformNo)){
			sqlbuf.append( " DISTINCT  ecplatformNo , ecplatformName ,  NUM , rn   " );
		}else{
			sqlbuf.append( " * ");
		}
				
		sqlbuf.append(" FROM ("
			+ " SELECT  COUNT(DISTINCT ECPLATFORMNO ) OVER() NUM ,dense_rank() over(ORDER BY ECPLATFORMNO) rn ,"
			+ " ECPLATFORMNO ,ECPLATFORMNAME, ECPLATFORMURL , ECPLATFORMHOTLINE, "
			+ " API_URL AS  ApiUrl , API_KEY  AS yh_ApiKey  ,API_SECRET  AS yh_Apisecret , "
			+ " PARTNER_ID AS  xp_PartnerId , STORE_ID AS xp_ShopId ,PARTNER_KEY  AS xp_PartnerKey ,"
			+ " APPKEY AS  jy_Apikey ,LTSECRETKEY  AS  lt_Secretkey,LTLICENSEKEY  AS  lt_Licensekey , ltshopurl as lt_shopurl ,a.status ,"
			+ " a.SHOPID as orderShop, b.org_name as orderShopName , "
			+ " a.warehouse as orderWarehouse , c.warehouse_Name as orderWarehouseName ,"
			+ " a.currencyNo , a.memberGet, "
			+ " a.token as jy_Token , a.saltkey as jy_saltKey , a.Shopsn as jy_Shopsn  ,"
			+ " a.eccustomerNo as customerNo , d.customer_name as customerName ,"
			+ " a.ISINVOICE as canInvoice ,"
			+ " a.momo_EntpId , a.momo_EntpCode , a.momo_PWD ,a.OTHER_NO,a.ENABLE_DISPATCHING,a.ECPLATFORMRATIO  "
			+ " FROM OC_ECOMMERCE a "
			+ " LEFT JOIN DCP_ORG_LANG b on a.EID = b.EID and a.SHOPID = b.organizationNO and b.lang_Type = '"+langType+"'  "
			+ " LEFT JOIN DCP_WAREHOUSE_LANG c on a.EID = c.EID and a.SHOPID = c.organizationNO "
			+ " and a.warehouse = c.warehouse and c.lang_Type = '"+langType+"'  "
			+ " LEFT JOIN DCP_CUSTOMER_LANG d on a.EID = d.EID and a.ecCustomerNo = d.Customer "
			+ " and d.lang_type = '"+langType+"' "
			
			+ " WHERE a.EID = '"+eId+"' " );
		
		if (ecPlatformNo != null && ecPlatformNo.length()!=0) { 	
			sqlbuf.append( " AND  a.ECPLATFORMNO = '"+ecPlatformNo+"'  ");
		}
		
		if (keyTxt != null && keyTxt.length()!=0) { 	
			sqlbuf.append( " AND  a.ECPLATFORMNO LIKE '%%"+keyTxt+"%%' or  a.ECPLATFORMNAME like '%%"+keyTxt+"%%'  ");
		}
		sqlbuf.append( " ORDER BY  ECPLATFORMNO  ) "
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by ECPLATFORMNO ");
		
		sql = sqlbuf.toString();
		return sql;
	}

	
}
