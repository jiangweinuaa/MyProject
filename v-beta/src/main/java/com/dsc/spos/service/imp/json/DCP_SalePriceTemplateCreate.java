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
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateCreateReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateCreateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.google.gson.reflect.TypeToken;

public class DCP_SalePriceTemplateCreate extends SPosAdvanceService<DCP_SalePriceTemplateCreateReq,DCP_SalePriceTemplateCreateRes> {
	
	@Override
	protected void processDUID(DCP_SalePriceTemplateCreateReq req, DCP_SalePriceTemplateCreateRes res) throws Exception {
		
		try {
			String eId = req.geteId();
			String lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//			String templateId = req.getRequest().getTemplateId();
			String templateId = getProcessTaskNO(req.geteId(),req.getShopId(),"SJMB");

			String templateType = req.getRequest().getTemplateType();
			String restrictchannel = req.getRequest().getRestrictChannel();
			String channelId = req.getRequest().getChannelId();
			String status = req.getRequest().getStatus();
			String memo = req.getRequest().getMemo();
			List<DCP_SalePriceTemplateCreateReq.RangeList> rangeList = req.getRequest().getRangeList();
			List<DCP_SalePriceTemplateCreateReq.TemplateName_Lang> templateName_lang = req.getRequest().getTemplateName_lang();
			
			
			String selfBuiltShopId = req.getRequest().getSelfBuiltShopId(); //自建门店
			String selfBuiltShopName = rangeList.get(0).getName();
//			if (!Check.Null(selfBuiltShopId)){
//				templateId = "shop"+selfBuiltShopId+"0001";
//				templateType = "SHOP";
//
//				DCP_SalePriceTemplateCreateReq.RangeList lr = req.new RangeList();
//				lr.setId(selfBuiltShopId);
//				lr.setName(selfBuiltShopName);
//				rangeList.clear();
//				rangeList.add(lr);
//
//				lastmoditime = "2199-12-31 00:00:00";
//				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				//Calendar cal = Calendar.getInstance();
//				//cal.setTime(sdf.parse("2199-12-31 00:00:00"));
//				//lastmoditime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sdf.parse("2199-12-31 00:00:00"));
//
//			}else{
//				selfBuiltShopId="";
//				//因为自建门店的模板编号是写死的，为了防止用户输入的模板编号和门店自建模板编号重复，这里需要拦截
//				if (templateId.startsWith("shop")){
//					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "非自建门店的模板编号不能用shop作为前缀");
//				}
//			}
			
			String sql = "select templateid from dcp_salepricetemplate "
					+ " where eid='" + eId + "' and (templateid='"+templateId+"' or selfbuiltshopid='"+selfBuiltShopId+"') ";
			List<Map<String, Object>> getData = this.doQueryData(sql, null);
			if (getData != null && getData.isEmpty() == false) {
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("此模板编码或自建门店已存在！");
				return;
			}
			
			
			if (templateName_lang.size() == 0) {
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("模板多语言必须有值！");
				return;
			}
			
			String[] columns_DCP_SALEPRICETEMPLATE_LANG = {
					"EID", "TEMPLATEID", "LANG_TYPE", "TEMPLATENAME", "LASTMODITIME"
			};
			
			for (DCP_SalePriceTemplateCreateReq.TemplateName_Lang template : templateName_lang) {
				
				DataValue[] insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(templateId, Types.VARCHAR),
						new DataValue(template.getLangType(), Types.VARCHAR),
						new DataValue(template.getName(), Types.VARCHAR),
						new DataValue(lastmoditime, Types.DATE)
				};
				
				InsBean ib1 = new InsBean("DCP_SALEPRICETEMPLATE_LANG", columns_DCP_SALEPRICETEMPLATE_LANG);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增
			}
			
			
			if (rangeList != null && rangeList.size() > 0) {
				String[] columns_DCP_SALEPRICETEMPLATE_RANGE = {
						"EID", "TEMPLATEID", "RANGETYPE", "ID", "NAME",
						"LASTMODITIME"
				};
				for (DCP_SalePriceTemplateCreateReq.RangeList range : rangeList) {
					
					DataValue[] insValue1 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(templateId, Types.VARCHAR),
							new DataValue(templateType.equals("COMPANY") ? 1 : 2, Types.INTEGER),
							new DataValue(range.getId(), Types.VARCHAR),
							new DataValue(range.getName(), Types.VARCHAR),
							new DataValue(lastmoditime, Types.DATE)
					};
					
					InsBean ib1 = new InsBean("DCP_SALEPRICETEMPLATE_RANGE", columns_DCP_SALEPRICETEMPLATE_RANGE);
					ib1.addValues(insValue1);
					this.addProcessData(new DataProcessBean(ib1)); // 新增
				}
				
			}
			
			String[] columns_DCP_SALEPRICETEMPLATE = {
					"EID","TEMPLATEID","TEMPLATETYPE",
					"RESTRICTCHANNEL","CHANNELID","MEMO","STATUS",
					"CREATEOPID","CREATEOPNAME","CREATETIME",
					"LASTMODIOPID", "LASTMODIOPNAME", "LASTMODITIME",
					"SELFBUILTSHOPID"
			};
			
			DataValue[] insValue1 = new DataValue[]{
					new DataValue(eId, Types.VARCHAR),
					new DataValue(templateId, Types.VARCHAR),
					new DataValue(templateType, Types.VARCHAR),
					new DataValue(restrictchannel, Types.VARCHAR),
					new DataValue(channelId, Types.VARCHAR),
					new DataValue(memo, Types.VARCHAR),
					new DataValue(status, Types.VARCHAR),
					new DataValue(req.getEmployeeNo(), Types.VARCHAR),
					new DataValue(req.getEmployeeName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE),
					new DataValue(req.getEmployeeNo(), Types.VARCHAR),
					new DataValue(req.getEmployeeName(), Types.VARCHAR),
					new DataValue(lastmoditime, Types.DATE),
					new DataValue(selfBuiltShopId, Types.VARCHAR),
			};
			
			InsBean ib1 = new InsBean("DCP_SALEPRICETEMPLATE", columns_DCP_SALEPRICETEMPLATE);
			ib1.addValues(insValue1);
			this.addProcessData(new DataProcessBean(ib1));
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功！");
			
		}catch(Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_SalePriceTemplateCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_SalePriceTemplateCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_SalePriceTemplateCreateReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_SalePriceTemplateCreateReq req) throws Exception {
		boolean isFail = false;
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_SalePriceTemplateCreateReq> getRequestType() {
		return new TypeToken<DCP_SalePriceTemplateCreateReq>(){};
	}
	
	@Override
	protected DCP_SalePriceTemplateCreateRes getResponseType() {
		return new DCP_SalePriceTemplateCreateRes();
	}
	
	
	
}
