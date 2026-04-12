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
import com.dsc.spos.json.cust.req.DCP_EInvoiceTempCreateReq;
import com.dsc.spos.json.cust.res.DCP_EInvoiceTempCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_EInvoiceTempUpdate extends SPosAdvanceService<DCP_EInvoiceTempCreateReq, DCP_EInvoiceTempCreateRes> {

	@Override
	protected void processDUID(DCP_EInvoiceTempCreateReq req, DCP_EInvoiceTempCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String templateNo = req.getRequest().getTemplateNo();
		String modifyBy = req.getOpNO();
		String modify_date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String modify_time = new SimpleDateFormat("hhmmss").format(new Date());
		String update_time = new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
		
		try 
		{
			
			UptBean up1 = new UptBean("DCP_einvoiceset");
			
			up1.addCondition("EID", new DataValue(eId, Types.VARCHAR));		
			up1.addCondition("PTEMPLATENO", new DataValue(templateNo, Types.VARCHAR));		
			
			up1.addUpdateValue("PTEMPLATE_NAME", new DataValue(req.getRequest().getTemplateName(), Types.VARCHAR));
			up1.addUpdateValue("SHOPTYPE", new DataValue(req.getRequest().getShopType(), Types.VARCHAR));
			up1.addUpdateValue("INVOICETYPE", new DataValue(req.getRequest().getInvoiceType(), Types.VARCHAR));
			up1.addUpdateValue("TAXPAYERID", new DataValue(req.getRequest().getTaxpayerID(), Types.VARCHAR));
			up1.addUpdateValue("TAXPAYERID_EC", new DataValue(req.getRequest().getTaxpayerID_EC(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEPOSURL", new DataValue(req.getRequest().getInvoicePosUrl(), Types.VARCHAR));
			up1.addUpdateValue("INVOICESKIPURL", new DataValue(req.getRequest().getInvoiceSkipUrl(), Types.VARCHAR));
			up1.addUpdateValue("INVOICERESQUERSSYS", new DataValue(req.getRequest().getInvoiceResquersSys(), Types.VARCHAR));
			up1.addUpdateValue("INVOICESL", new DataValue(req.getRequest().getInvoiceSL(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEENCRYPTKEY", new DataValue(req.getRequest().getInvoiceEncryptKey(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEENCRYPTKEY_EC", new DataValue(req.getRequest().getInvoiceEncryptKey_EC(), Types.VARCHAR));
			up1.addUpdateValue("INVOICETAXCLASS", new DataValue(req.getRequest().getInvoiceTaxClass(), Types.VARCHAR));
			up1.addUpdateValue("INVOICETAXCLASS_EC", new DataValue(req.getRequest().getInvoiceTaxClass_EC(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEPROJECTNAME", new DataValue(req.getRequest().getInvoiceProjectName(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEMERCHANTNAME", new DataValue(req.getRequest().getInvoiceMerchantName(), Types.VARCHAR));
			up1.addUpdateValue("INVOICESKR", new DataValue(req.getRequest().getInvoiceSKR(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEKPR", new DataValue(req.getRequest().getInvoiceKPR(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEFHR", new DataValue(req.getRequest().getInvoiceFHR(), Types.VARCHAR));
			up1.addUpdateValue("INVOICECOMPANYNO", new DataValue(req.getRequest().getInvoiceCompanyNo(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEUSERNAME", new DataValue(req.getRequest().getInvoiceUserName(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEPASSWORD", new DataValue(req.getRequest().getInvoicePassWord(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEMERCHANTCODE", new DataValue(req.getRequest().getInvoiceMerchantCode(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEQRCODETIMELIMIT", new DataValue(req.getRequest().getInvoiceQrCodeTimeLimit(), Types.VARCHAR));
			up1.addUpdateValue("MODIFYBY", new DataValue(modifyBy, Types.VARCHAR));
			up1.addUpdateValue("MODIFY_DATE", new DataValue(modify_date, Types.VARCHAR));
			up1.addUpdateValue("MODIFY_TIME", new DataValue(modify_time, Types.VARCHAR));
			up1.addUpdateValue("STATUS", new DataValue(req.getRequest().getStatus(), Types.VARCHAR));
			up1.addUpdateValue("UPDATE_TIME", new DataValue(update_time, Types.VARCHAR));
			
			up1.addUpdateValue("TAXURL", new DataValue(req.getRequest().getTaxURL(), Types.VARCHAR));
			up1.addUpdateValue("TAXSHOPNO", new DataValue(req.getRequest().getTaxShopNo(), Types.VARCHAR));
			up1.addUpdateValue("PROJECTNAMEFORCARDCOUPON", new DataValue(req.getRequest().getInvoiceProjectNameForCardCoupon(), Types.VARCHAR));
			up1.addUpdateValue("TAXCLASSFORCARDCOUPON", new DataValue(req.getRequest().getInvoiceTaxClassForCardCoupon(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEMERCHANTPHONE", new DataValue(req.getRequest().getInvoiceMerchantPhone(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEMERCHANTBANK", new DataValue(req.getRequest().getInvoiceMerchantBank(), Types.VARCHAR));
			up1.addUpdateValue("INVOICEMERCHANTACCOUNT", new DataValue(req.getRequest().getInvoiceMerchantAccount(), Types.VARCHAR));
			
			this.addProcessData(new DataProcessBean(up1));
			this.doExecuteDataToDB();
			
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());
	
		}
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功");
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_EInvoiceTempCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_EInvoiceTempCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_EInvoiceTempCreateReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_EInvoiceTempCreateReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(Check.Null(req.getRequest().getTemplateNo()))
		{
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		}
		
		if(Check.Null(req.getRequest().getTemplateName()))
		{
			errMsg.append("模板名称不可为空值, ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().getInvoiceType()))
		{
			errMsg.append("电子发票类型不可为空值, ");
			isFail = true;
		}
		
		if(Check.Null(req.getRequest().getShopType()))
		{
			errMsg.append("生效门店类型不可为空值, ");
			isFail = true;
		}
		if(Check.Null(req.getRequest().getInvoiceSL()))
		{
			errMsg.append("发票税率不可为空值, ");
			isFail = true;
		}
		
		
		
		if(isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
	 return isFail;
	}

	@Override
	protected TypeToken<DCP_EInvoiceTempCreateReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_EInvoiceTempCreateReq>(){};
	}

	@Override
	protected DCP_EInvoiceTempCreateRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_EInvoiceTempCreateRes();
	}
	
	private boolean checkTemplateNO(DCP_EInvoiceTempCreateReq req) throws Exception {
		String sql = null;
		String templateNo = req.getRequest().getTemplateNo();
		boolean existGuid = false;
		
		sql = "select *  from DCP_EINVOICESET  where PTEMPLATENO ='"+templateNo+"'";
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		if (getQData != null && getQData.isEmpty() == false) {
			existGuid = true;
		} else {
			existGuid =  false;
		}
		return existGuid;
	}

}
