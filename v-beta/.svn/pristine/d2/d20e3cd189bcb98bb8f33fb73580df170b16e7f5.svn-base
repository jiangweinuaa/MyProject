package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Null;

import com.dsc.spos.json.cust.req.DCP_EInvoiceTempQueryReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceTempQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_EInvoiceTempQuery extends SPosBasicService<DCP_EInvoiceTempQueryReq,DCP_EInvoiceTempQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_EInvoiceTempQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return false;
	}

	@Override
	protected TypeToken<DCP_EInvoiceTempQueryReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_EInvoiceTempQueryReq>(){};
	}

	@Override
	protected DCP_EInvoiceTempQueryRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_EInvoiceTempQueryRes();
	}

	@Override
	protected DCP_EInvoiceTempQueryRes processJson(DCP_EInvoiceTempQueryReq req) throws Exception {
	// TODO Auto-generated method stub
		
		DCP_EInvoiceTempQueryRes res = null;
		res = this.getResponse();
		String eId = req.geteId();
		String keyTxt = req.getRequest().getKeyTxt();
		String docType = req.getRequest().getInvoiceType();
		
		String sql = " select * from DCP_EINVOICESET  where EID='"+eId+"'";
		if(keyTxt!=null&&keyTxt.trim().isEmpty()==false )
		{
			sql +=" and (PTEMPLATENO like '%%"+keyTxt+"%%' OR PTEMPLATE_NAME like '%%"+keyTxt+"%%')";
		}
		
		if(docType!=null&&docType.trim().isEmpty()==false )
		{
			sql +=" and INVOICETYPE='"+docType+"'";
		}
		
		sql +=" order by ptemplateno";
		
		try 
		{
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_EInvoiceTempQueryRes.level1Elm>());

			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				for (Map<String, Object> map : getQDataDetail) 
				{
					DCP_EInvoiceTempQueryRes.level1Elm oneLv1 = res.new level1Elm();
					String templateNO = map.get("PTEMPLATENO").toString();
					String templateName = map.get("PTEMPLATE_NAME").toString();
					String shopType = map.get("SHOPTYPE").toString();
					String invoiceType = map.get("INVOICETYPE").toString();
					String taxpayerID = map.get("TAXPAYERID").toString();
					String taxpayerID_EC = map.get("TAXPAYERID_EC").toString();
					String invoicePosUrl = map.get("INVOICEPOSURL").toString();
					String invoiceSkipUrl = map.get("INVOICESKIPURL").toString();
					String invoiceResquersSys = map.get("INVOICERESQUERSSYS").toString();
					String invoiceSL = map.get("INVOICESL").toString();
					String invoiceEncryptKey = map.get("INVOICEENCRYPTKEY").toString();
					String invoiceEncryptKey_EC = map.get("INVOICEENCRYPTKEY_EC").toString();
					String invoiceTaxClass = map.get("INVOICETAXCLASS").toString();
					String invoiceTaxClass_EC = map.get("INVOICETAXCLASS_EC").toString();
					String invoiceProjectName = map.get("INVOICEPROJECTNAME").toString();
					String invoiceMerchantName = map.get("INVOICEMERCHANTNAME").toString();
					String invoiceSKR = map.get("INVOICESKR").toString();
					String invoiceKPR = map.get("INVOICEKPR").toString();
					String invoiceFHR = map.get("INVOICEFHR").toString();
					String invoiceCompanyNo = map.get("INVOICECOMPANYNO").toString();
					String invoiceUserName = map.get("INVOICEUSERNAME").toString();
					String invoicePassWord = map.get("INVOICEPASSWORD").toString();
					String invoiceMerchantCode = map.get("INVOICEMERCHANTCODE").toString();
					String invoiceQrCodeTimeLimit = map.get("INVOICEQRCODETIMELIMIT").toString();
					String status = map.get("STATUS").toString();
					
					String taxURL = map.get("TAXURL").toString();
					String taxShopNo = map.get("TAXSHOPNO").toString();
					String invoiceMerchantPhone = map.get("INVOICEMERCHANTPHONE").toString();
					String invoiceMerchantBank = map.get("INVOICEMERCHANTBANK").toString();
					String invoiceMerchantAccount = map.get("INVOICEMERCHANTACCOUNT").toString();
					String invoiceProjectNameForCardCoupon = map.get("PROJECTNAMEFORCARDCOUPON").toString();
					String invoiceTaxClassForCardCoupon = map.get("TAXCLASSFORCARDCOUPON").toString();
					
					
					oneLv1.setTemplateNo(templateNO);
					oneLv1.setTemplateName(templateName);
					oneLv1.setShopType(shopType);
					oneLv1.setInvoiceType(invoiceType);
					oneLv1.setTaxpayerID(taxpayerID);
					oneLv1.setTaxpayerID_EC(taxpayerID_EC);
					oneLv1.setInvoicePosUrl(invoicePosUrl);
					oneLv1.setInvoiceSkipUrl(invoiceSkipUrl);
					oneLv1.setInvoiceResquersSys(invoiceResquersSys);
					oneLv1.setInvoiceSL(invoiceSL);
					oneLv1.setInvoiceEncryptKey(invoiceEncryptKey);
					oneLv1.setInvoiceEncryptKey_EC(invoiceEncryptKey_EC);
					oneLv1.setInvoiceTaxClass(invoiceTaxClass);
					oneLv1.setInvoiceTaxClass_EC(invoiceTaxClass_EC);
					oneLv1.setInvoiceProjectName(invoiceProjectName);
					oneLv1.setInvoiceMerchantName(invoiceMerchantName);
					oneLv1.setInvoiceSKR(invoiceSKR);
					oneLv1.setInvoiceKPR(invoiceKPR);
					oneLv1.setInvoiceFHR(invoiceFHR);
					oneLv1.setInvoiceCompanyNo(invoiceCompanyNo);
					oneLv1.setInvoiceUserName(invoiceUserName);
					oneLv1.setInvoicePassWord(invoicePassWord);											
					oneLv1.setInvoiceMerchantCode(invoiceMerchantCode);
					oneLv1.setInvoiceQrCodeTimeLimit(invoiceQrCodeTimeLimit);
					oneLv1.setStatus(status);
					oneLv1.setTaxURL(taxURL);
					oneLv1.setTaxShopNo(taxShopNo);
					oneLv1.setInvoiceMerchantPhone(invoiceMerchantPhone);
					oneLv1.setInvoiceMerchantBank(invoiceMerchantBank);
					oneLv1.setInvoiceMerchantAccount(invoiceMerchantAccount);
					oneLv1.setInvoiceProjectNameForCardCoupon(invoiceProjectNameForCardCoupon);
					oneLv1.setInvoiceTaxClassForCardCoupon(invoiceTaxClassForCardCoupon);
					
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
		// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
	
		}
	
	return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_EInvoiceTempQueryReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

}
