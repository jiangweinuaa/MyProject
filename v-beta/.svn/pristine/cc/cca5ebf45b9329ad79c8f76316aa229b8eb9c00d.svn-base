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
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateCreateReq;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateCreateReq.levelRange;
import com.dsc.spos.json.cust.req.DCP_GoodsTemplateCreateReq.levelTemplate;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsTemplateCreate extends SPosAdvanceService<DCP_GoodsTemplateCreateReq,DCP_GoodsTemplateCreateRes>
{

	@Override
	protected void processDUID(DCP_GoodsTemplateCreateReq req, DCP_GoodsTemplateCreateRes res) throws Exception 
	{
		String lastmoditime =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String eId=req.geteId();
		String templateId = req.getRequest().getTemplateId();		
		String templateType=req.getRequest().getTemplateType();

        String employeeNo = req.getEmployeeNo();
        String employeeName = req.getEmployeeName();
        String departmentNo = req.getDepartmentNo();

        String status=req.getRequest().getStatus();
		String memo=req.getRequest().getMemo();
		List<levelRange> rangeList=req.getRequest().getRangeList();

		List<levelTemplate> templateName_lang=req.getRequest().getTemplateName_lang();
		String rangeType ="2";//适用范围：1-公司 2-门店
		if (templateType.equals("COMPANY"))
		{
			rangeType ="1";
		}
		else if (templateType.equals("SHOP")) 
		{
			rangeType ="2";
		}
		else if (templateType.equals("CHANNEL")) 
		{
			rangeType ="3";
		}
		else 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "模板适用对象templateType枚举值未定义！");
		}

		String sql = "select templateid from dcp_goodstemplate where eid='"+eId+"' and templateid='"+templateId+"' ";
		List<Map<String , Object>> getData=this.doQueryData(sql, null);
		if (getData!=null && getData.isEmpty()==false) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("此模板编码已经存在！");
			return;
		}


		//控制通用模板只能有1个
		if (templateType.equals("COMMON")) 
		{			
			sql = "select templateid from dcp_goodstemplate where eid='"+eId+"' and TEMPLATETYPE='"+templateType+"' ";
			getData=this.doQueryData(sql, null);
			if (getData!=null && getData.isEmpty()==false) 
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("通用模板只能有1个，已经存在！");
				return;
			}
		}		

		if (templateName_lang.size()==0) 
		{
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription("模板多语言必须有值！");
			return;
		}

		String[] columns_DCP_GOODSTEMPLATE_LANG = 
			{ 
					"EID","TEMPLATEID","LANG_TYPE","TEMPLATENAME","LASTMODITIME"
			};
		for (levelTemplate template : templateName_lang) 
		{

			DataValue[] insValue1 = null;

			insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(templateId, Types.VARCHAR),
					new DataValue(template.getLangType(), Types.VARCHAR),
					new DataValue(template.getName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE)				
			};

			InsBean ib1 = new InsBean("DCP_GOODSTEMPLATE_LANG", columns_DCP_GOODSTEMPLATE_LANG);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1)); // 新增
		}		

		if (rangeList!=null && rangeList.size()>0) 
		{
			//
			String[] columns_DCP_GOODSTEMPLATE_RANGE = 
				{ 
						"EID","TEMPLATEID","RANGETYPE","ID","NAME",
						"LASTMODITIME"
				};
			for (levelRange range : rangeList) 
			{
				//String rangeType ="2";//适用范围：1-公司 2-门店
			 	 

				DataValue[] insValue1 = null;

				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(rangeType, Types.VARCHAR),
						new DataValue(range.getId(), Types.VARCHAR),
						new DataValue(range.getName(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)				
				};

				InsBean ib1 = new InsBean("DCP_GOODSTEMPLATE_RANGE", columns_DCP_GOODSTEMPLATE_RANGE);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增			
			}
			
		}


		//
		String[] columns_DCP_GOODSTEMPLATE = 
			{ 
					"EID","TEMPLATEID","TEMPLATETYPE","MEMO","STATUS","CREATEOPID",
					"CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME","CREATEDEPTID"
			};
		DataValue[] insValue1 = null;

		insValue1 = new DataValue[]{
				new DataValue(eId, Types.VARCHAR),
				new DataValue(templateId, Types.VARCHAR),
				new DataValue(templateType, Types.VARCHAR),						
				new DataValue(memo, Types.VARCHAR),
				new DataValue(status, Types.VARCHAR),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
				new DataValue(req.getOpNO(), Types.VARCHAR),
				new DataValue(req.getOpName(), Types.VARCHAR),
				new DataValue(lastmoditime, Types.DATE),
                new DataValue(departmentNo, Types.VARCHAR),
		};

		InsBean ib1 = new InsBean("DCP_GOODSTEMPLATE", columns_DCP_GOODSTEMPLATE);
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
	protected List<InsBean> prepareInsertData(DCP_GoodsTemplateCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_GoodsTemplateCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_GoodsTemplateCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateCreateReq req) throws Exception 
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String templateId = req.getRequest().getTemplateId();		
		String templateType=req.getRequest().getTemplateType();
		
		
		String status=req.getRequest().getStatus();

		List<levelTemplate> templateName_lang=req.getRequest().getTemplateName_lang();
		List<levelRange> rangeList=req.getRequest().getRangeList();

		if(Check.Null(templateId))
		{
			errMsg.append("模板编码不能为空值 ");
			isFail = true;
		}
		if(Check.Null(templateType))
		{
			errMsg.append("模板类型不能为空值 ");
			isFail = true;
		}
			
		if(Check.Null(status))
		{
			errMsg.append("状态不能为空值 ");
			isFail = true;
		}
		if(templateName_lang==null)
		{
			errMsg.append("模板多语言不能为空值 ");
			isFail = true;
		}
		
		if(rangeList==null || rangeList.size()==0)
		{
			errMsg.append("适用范围不能为空值 ");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsTemplateCreateReq> getRequestType() 
	{		
		return new TypeToken<DCP_GoodsTemplateCreateReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateCreateRes getResponseType() 
	{
		return new DCP_GoodsTemplateCreateRes();
	}	



}
