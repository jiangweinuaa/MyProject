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
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateUpdateReq;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateUpdateReq.Range;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateUpdateReq.TemplateLang;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateUpdate extends SPosAdvanceService<DCP_SupPriceTemplateUpdateReq,DCP_SupPriceTemplateUpdateRes>
{

	@Override
	protected void processDUID(DCP_SupPriceTemplateUpdateReq req, DCP_SupPriceTemplateUpdateRes res) throws Exception
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
		if (getData==null || getData.isEmpty()) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此模板不存在！");
			return;
		}

		if (templateName_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("模板多语言必须有值！");
			return;
		}

		//删除之前的,再插入
		DelBean db1 = new DelBean("DCP_SUPPRICETEMPLATE_LANG");
		db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
		db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
		this.addProcessData(new DataProcessBean(db1));	

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
			//删除之前的,再插入
			db1 = new DelBean("DCP_SUPPRICETEMPLATE_RANGE");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));	

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

		UptBean ub1 = new UptBean("DCP_SUPPRICETEMPLATE");
		ub1.addUpdateValue("SUPPLIERTYPE", new DataValue(supplierType,Types.INTEGER));
		ub1.addUpdateValue("SUPPLIERID", new DataValue(supplierId,Types.VARCHAR));
		ub1.addUpdateValue("RECEIVERTYPE", new DataValue(receiverType,Types.INTEGER));
		ub1.addUpdateValue("RECEIVERIDRANGE", new DataValue(receiverIdRange,Types.INTEGER));
		ub1.addUpdateValue("MEMO", new DataValue(req.getRequest().getMemo(),Types.VARCHAR));
		ub1.addUpdateValue("STATUS", new DataValue(status,Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPID", new DataValue(req.getOpNO(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODIOPNAME", new DataValue(req.getOpName(),Types.VARCHAR));
		ub1.addUpdateValue("LASTMODITIME", new DataValue(lastmoditime,Types.DATE));


		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("TEMPLATEID", new DataValue(templateId, Types.VARCHAR));
		this.addProcessData(new DataProcessBean(ub1));
		//
		this.doExecuteDataToDB();

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		return;	

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_SupPriceTemplateUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_SupPriceTemplateUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_SupPriceTemplateUpdateReq req) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_SupPriceTemplateUpdateReq req) throws Exception
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
	protected TypeToken<DCP_SupPriceTemplateUpdateReq> getRequestType()
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SupPriceTemplateUpdateReq>(){};
	}

	@Override
	protected DCP_SupPriceTemplateUpdateRes getResponseType()
	{
		// TODO Auto-generated method stub
		return new DCP_SupPriceTemplateUpdateRes();
	}


}
