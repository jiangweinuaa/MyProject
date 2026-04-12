package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateGoodsDeleteReq;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateGoodsDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateGoodsDelete extends SPosAdvanceService<DCP_SupPriceTemplateGoodsDeleteReq,DCP_SupPriceTemplateGoodsDeleteRes> {
	
	@Override
	protected void processDUID(DCP_SupPriceTemplateGoodsDeleteReq req, DCP_SupPriceTemplateGoodsDeleteRes res) throws Exception {
		String eId=req.geteId();
		String templateId = req.getRequest().getTemplateId();
		String isAllgoods = req.getRequest().getIsAllGoods();
		
		if (isAllgoods.equals("Y")==false) {
			List<DCP_SupPriceTemplateGoodsDeleteReq.TemplatePrice> pluList= req.getRequest().getPluList();
			
			for (DCP_SupPriceTemplateGoodsDeleteReq.TemplatePrice templatePrice : pluList) {
				DelBean db1 = new DelBean("DCP_SUPPRICETEMPLATE_PRICE");
				db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
				db1.addCondition("ITEM", new DataValue(templatePrice.getItem(),Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
			}
			
		} else {
			DelBean db1 = new DelBean("DCP_SUPPRICETEMPLATE_PRICE");
			db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
			db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
		}
		
		this.doExecuteDataToDB();
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
	}
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_SupPriceTemplateGoodsDeleteReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_SupPriceTemplateGoodsDeleteReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_SupPriceTemplateGoodsDeleteReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_SupPriceTemplateGoodsDeleteReq req) throws Exception
	{
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		String templateId = req.getRequest().getTemplateId();
		String isAllgoods = req.getRequest().getIsAllGoods();
		
		if (Check.Null(templateId)) {
			errMsg.append("模板编码不能为空值 ");
			isFail = true;
		}
		
		if (Check.Null(isAllgoods)) {
			errMsg.append("是否删除全部商品不能为空值 ");
			isFail = true;
		}
		
		if (isAllgoods.equals("Y")==false) {
			List<DCP_SupPriceTemplateGoodsDeleteReq.TemplatePrice> pluList= req.getRequest().getPluList();
			if (pluList==null || pluList.size()==0) {
				errMsg.append("商品列表不能为空值 ");
				isFail = true;
			}
			
			for (DCP_SupPriceTemplateGoodsDeleteReq.TemplatePrice templatePrice : pluList) {
				if (Check.Null(templatePrice.getItem())) {
					errMsg.append("商品项次不能为空值 ");
					isFail = true;
				}
			}
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return isFail;
	}
	
	@Override
	protected TypeToken<DCP_SupPriceTemplateGoodsDeleteReq> getRequestType() {
		return new TypeToken<DCP_SupPriceTemplateGoodsDeleteReq>() {};
	}
	
	@Override
	protected DCP_SupPriceTemplateGoodsDeleteRes getResponseType() {
		return new DCP_SupPriceTemplateGoodsDeleteRes();
	}
	
	
	
}
