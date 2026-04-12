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
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateCreateReq.Range;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateCreateReq.TemplateLang;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateCreate extends SPosAdvanceService<DCP_SupPriceTemplateCreateReq,DCP_SupPriceTemplateCreateRes>
{

	@Override
	protected void processDUID(DCP_SupPriceTemplateCreateReq req, DCP_SupPriceTemplateCreateRes res) throws Exception
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();

		//
		String templateId = req.getRequest().getTemplateId();		
		List<TemplateLang> templateName_lang=req.getRequest().getTemplateName_lang();
		String supplierType = req.getRequest().getSupplierType();	
		String supplierId = req.getRequest().getSupplierId();	
		String receiverType = req.getRequest().getReceiverType();	
		String receiverIdRange = req.getRequest().getReceiverIdRange();	
		String status = req.getRequest().getStatus();	
		List<Range> rangeList=req.getRequest().getRangeList();

		if(status==null||status.isEmpty())
		{
			status = "100";
		}

		String sql = "select TEMPLATEID from DCP_SUPPRICETEMPLATE where eid='"+eId+"' and TEMPLATEID='"+templateId+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData!=null && getData.isEmpty()==false) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此模板已经存在！");
			return;
		}

		if (templateName_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("模板多语言必须有值！");
			return;
		}

		String[] columns_DCP_SUPPRICETEMPLATE_LANG = 
			{ 
					"EID","TEMPLATEID","LANG_TYPE","TEMPLATENAME","LASTMODITIME"
			};
		for (TemplateLang lang : templateName_lang) 
		{

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(templateId, Types.VARCHAR),
					new DataValue(lang.getLangType(), Types.VARCHAR),
					new DataValue(lang.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_SUPPRICETEMPLATE_LANG", columns_DCP_SUPPRICETEMPLATE_LANG);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}	


		if (rangeList!=null)
		{
			String[] columns_DCP_SUPPRICETEMPLATE_RANGE = 
				{ 
						"EID","TEMPLATEID","ID","NAME","LASTMODITIME"
				};
			for (Range rg : rangeList) 
			{

				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(rg.getId(), Types.VARCHAR),
						new DataValue(rg.getName(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_SUPPRICETEMPLATE_RANGE", columns_DCP_SUPPRICETEMPLATE_RANGE);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增
			}
		}

		//
		String[] columns_DCP_SUPPRICETEMPLATE = 
			{ 
					"EID","TEMPLATEID","SUPPLIERTYPE","SUPPLIERID",
					"RECEIVERTYPE","RECEIVERIDRANGE","MEMO","STATUS","CREATEOPID",
					"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"
			};
		DataValue[] insValue1 = null;

		insValue1 = new DataValue[]{
				new DataValue(eId, Types.VARCHAR),
				new DataValue(templateId, Types.VARCHAR),
				new DataValue(supplierType, Types.INTEGER),
				new DataValue(supplierId, Types.VARCHAR),
				new DataValue(receiverType, Types.INTEGER),
				new DataValue(receiverIdRange, Types.INTEGER),
				new DataValue(req.getRequest().getMemo(), Types.VARCHAR),
				new DataValue(status, Types.INTEGER),				
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE)				
		};

		InsBean ib1 = new InsBean("DCP_SUPPRICETEMPLATE", columns_DCP_SUPPRICETEMPLATE);
		ib1.addValues(insValue1);
		this.addProcessData(new DataProcessBean(ib1)); // 新增

		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SupPriceTemplateCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SupPriceTemplateCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SupPriceTemplateCreateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SupPriceTemplateCreateReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		//
		String templateId = req.getRequest().getTemplateId();		
		List<TemplateLang> templateName_lang=req.getRequest().getTemplateName_lang();
		String supplierType = req.getRequest().getSupplierType();	
		String supplierId = req.getRequest().getSupplierId();	
		String receiverType = req.getRequest().getReceiverType();	
		String receiverIdRange = req.getRequest().getReceiverIdRange();	
		String status = req.getRequest().getStatus();			

		if(Check.Null(templateId))
		{
			errMsg.append("模板编码不能为空值 ");
			isFail = true;
		}

		if(templateName_lang==null)
		{
			errMsg.append("模板多语言不能为空值 ");
			isFail = true;
		}

		if(Check.Null(supplierType))
		{
			errMsg.append("供货机构类型不能为空值 ");
			isFail = true;
		}

		if(Check.Null(supplierId))
		{
			errMsg.append("供货机构编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(receiverType))
		{
			errMsg.append("收货机构类型不能为空值 ");
			isFail = true;
		}
		if(Check.Null(receiverIdRange))
		{
			errMsg.append("收货机构范围不能为空值 ");
			isFail = true;
		}
		if(Check.Null(status))
		{
			errMsg.append("状态不能为空值 ");
			isFail = true;
		}


		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;	
	}

	@Override
	protected TypeToken<DCP_SupPriceTemplateCreateReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SupPriceTemplateCreateReq>(){};
	}

	@Override
	protected DCP_SupPriceTemplateCreateRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_SupPriceTemplateCreateRes();
	}



}
