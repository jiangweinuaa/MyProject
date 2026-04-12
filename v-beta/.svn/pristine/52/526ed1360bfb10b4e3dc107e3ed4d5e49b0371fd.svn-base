package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateDeleteReq;
import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateDeleteReq.levelTemplate;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateDeleteRes;
import com.dsc.spos.model.Template_POS_GoodsChannelPriceRedisUpdate;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

public class DCP_SalePriceTemplateDelete extends SPosAdvanceService<DCP_SalePriceTemplateDeleteReq,DCP_SalePriceTemplateDeleteRes> {
	
	@Override
	protected void processDUID(DCP_SalePriceTemplateDeleteReq req, DCP_SalePriceTemplateDeleteRes res) throws Exception {


        //同步缓存
        List<Template_POS_GoodsChannelPriceRedisUpdate> templateList=new ArrayList<>();

		String eId=req.geteId();
		//清缓存
		String posUrl = PosPub.getPOS_INNER_URL(eId);
		String apiUserSql = "SELECT ITEM,ITEMVALUE FROM PLATFORM_BASESETTEMP WHERE EID = '" + req.geteId() + "'"
				+ " AND ( ITEM = 'ApiUserCode' OR ITEM = 'ApiUserKey' )";
		List<Map<String, Object>> result = this.doQueryData(apiUserSql, null);
		String apiUserCode = "";
		String apiUserKey = "";
		if (result != null && result.size() == 2) {
			for (Map<String, Object> map : result) {
				if (map.get("ITEM") != null && map.get("ITEM").toString().equals("ApiUserCode")) {
					apiUserCode = map.get("ITEMVALUE").toString();
				} else {
					apiUserKey = map.get("ITEMVALUE").toString();
				}
			}
		}
		
		List<levelTemplate> templatList = req.getRequest().getTemplateList();
		for (levelTemplate levelTemplate : templatList) {

            //
            Template_POS_GoodsChannelPriceRedisUpdate pos_goodsChannelPriceRedisUpdate=new Template_POS_GoodsChannelPriceRedisUpdate();
            pos_goodsChannelPriceRedisUpdate.setTemplateId(levelTemplate.getTemplateId());
            templateList.add(pos_goodsChannelPriceRedisUpdate);

			String templateId=levelTemplate.getTemplateId();
			//只删除 状态：-1未启用
			String sqlStatus="select status from DCP_SALEPRICETEMPLATE where eid='"+eId+"' and TEMPLATEID='"+templateId+"' and status=-1 ";
			List<Map<String , Object>> getData=this.doQueryData(sqlStatus, null);
			
			if (getData!=null && !getData.isEmpty()) {
				DelBean db1 = new DelBean("DCP_SALEPRICETEMPLATE");
				db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				db1 = new DelBean("DCP_SALEPRICETEMPLATE_LANG");
				db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				db1 = new DelBean("DCP_SALEPRICETEMPLATE_PRICE");
				db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				db1 = new DelBean("DCP_SALEPRICETEMPLATE_RANGE");
				db1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
				db1.addCondition("TEMPLATEID", new DataValue(templateId,Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
			}else{
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E422,"模板:"+templateId+" 状态必须是未启用才可以删除");
			}
		}
		

		this.doExecuteDataToDB();
		
		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");


        PosPub.clearGoodsCache(posUrl, apiUserCode, apiUserKey,eId);


        PosPub.POS_GoodsChannelPriceRedisUpdate_Cache(posUrl, apiUserCode, apiUserKey,templateList);



    }
	
	@Override
	protected List<InsBean> prepareInsertData(DCP_SalePriceTemplateDeleteReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<UptBean> prepareUpdateData(DCP_SalePriceTemplateDeleteReq req) throws Exception {
		return null;
	}
	
	@Override
	protected List<DelBean> prepareDeleteData(DCP_SalePriceTemplateDeleteReq req) throws Exception {
		return null;
	}
	
	@Override
	protected boolean isVerifyFail(DCP_SalePriceTemplateDeleteReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null) {
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		List<levelTemplate> templatList = req.getRequest().getTemplateList();
		
		if (templatList==null || templatList.size()==0) {
			errMsg.append("模板对象不能为空值 ");
			isFail = true;
		}else {
			for (levelTemplate levelTemplate : templatList) {
				if (Check.Null(levelTemplate.getTemplateId())) {
					errMsg.append("模板编码不能为空值 ");
					isFail = true;
				}
			}
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_SalePriceTemplateDeleteReq> getRequestType() {
		return new TypeToken<DCP_SalePriceTemplateDeleteReq>() {};
	}
	
	@Override
	protected DCP_SalePriceTemplateDeleteRes getResponseType() {
		return new DCP_SalePriceTemplateDeleteRes();
	}
	
}
