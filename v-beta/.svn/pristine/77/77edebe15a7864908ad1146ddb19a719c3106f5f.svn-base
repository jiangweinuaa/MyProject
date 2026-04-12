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

public class DCP_EInvoiceTempCreate extends SPosAdvanceService<DCP_EInvoiceTempCreateReq, DCP_EInvoiceTempCreateRes> {

	@Override
	protected void processDUID(DCP_EInvoiceTempCreateReq req, DCP_EInvoiceTempCreateRes res) throws Exception {
	// TODO Auto-generated method stub
		
		String eId = req.geteId();
		String templateNo = req.getRequest().getTemplateNo();
		String createBy = req.getOpNO();
		String create_date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String create_time = new SimpleDateFormat("hhmmss").format(new Date());
		String status = req.getRequest().getStatus();

		
		try 
		{
			if(checkTemplateNO(req))
			{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板编号("+templateNo+")已经存在！");
			}
			
			
			String[] columns = {"EID","PTEMPLATENO","PTEMPLATE_NAME","SHOPTYPE","INVOICETYPE","TAXPAYERID",
					"TAXPAYERID_EC","INVOICEPOSURL","INVOICESKIPURL","INVOICERESQUERSSYS","INVOICESL","INVOICEENCRYPTKEY","INVOICEENCRYPTKEY_EC",
					"INVOICETAXCLASS","INVOICETAXCLASS_EC","INVOICEPROJECTNAME","INVOICEMERCHANTNAME","INVOICESKR","INVOICEKPR","INVOICEFHR",
					"INVOICECOMPANYNO","INVOICEUSERNAME","INVOICEPASSWORD","INVOICEMERCHANTCODE","INVOICEQRCODETIMELIMIT",
					"TAXURL","TAXSHOPNO","PROJECTNAMEFORCARDCOUPON","TAXCLASSFORCARDCOUPON","INVOICEMERCHANTPHONE","INVOICEMERCHANTBANK","INVOICEMERCHANTACCOUNT",
					"CREATEBY","CREATE_DATE","CREATE_TIME","MODIFYBY","MODIFY_DATE","MODIFY_TIME","STATUS"};
				
				DataValue[] insValue = new DataValue[] {
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateNo, Types.VARCHAR),
						new DataValue(req.getRequest().getTemplateName(), Types.VARCHAR),
						new DataValue(req.getRequest().getShopType(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceType(), Types.VARCHAR),
						new DataValue(req.getRequest().getTaxpayerID(), Types.VARCHAR),
						new DataValue(req.getRequest().getTaxpayerID_EC(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoicePosUrl(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceSkipUrl(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceResquersSys(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceSL(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceEncryptKey(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceEncryptKey_EC(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceTaxClass(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceTaxClass_EC(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceProjectName(), Types.VARCHAR),				
						new DataValue(req.getRequest().getInvoiceMerchantName(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceSKR(), Types.VARCHAR),			
						new DataValue(req.getRequest().getInvoiceKPR(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceFHR(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceCompanyNo(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceUserName(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoicePassWord(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceMerchantCode(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceQrCodeTimeLimit(), Types.VARCHAR),
						new DataValue(req.getRequest().getTaxURL(), Types.VARCHAR),
						new DataValue(req.getRequest().getTaxShopNo(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceProjectNameForCardCoupon(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceTaxClassForCardCoupon(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceMerchantPhone(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceMerchantBank(), Types.VARCHAR),
						new DataValue(req.getRequest().getInvoiceMerchantAccount(), Types.VARCHAR),
						new DataValue(createBy, Types.VARCHAR),
						new DataValue(create_date, Types.VARCHAR),
						new DataValue(create_time, Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue("", Types.VARCHAR),
						new DataValue(status, Types.VARCHAR)
						};
				
				InsBean ib1 = new InsBean("DCP_einvoiceset", columns);	
				ib1.addValues(insValue);
				this.addProcessData(new DataProcessBean(ib1));
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
