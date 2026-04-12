package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_GoodsTemplateDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsTemplateDetail extends SPosBasicService<DCP_GoodsTemplateDetailReq,DCP_GoodsTemplateDetailRes>
{

	@Override
	protected boolean isVerifyFail(DCP_GoodsTemplateDetailReq req) throws Exception 
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
		if (Check.Null(templateId) ) 
		{
			errMsg.append("模板编码不可为空值, ");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_GoodsTemplateDetailReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsTemplateDetailReq>() {};
	}

	@Override
	protected DCP_GoodsTemplateDetailRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_GoodsTemplateDetailRes();
	}

	@Override
	protected DCP_GoodsTemplateDetailRes processJson(DCP_GoodsTemplateDetailReq req) throws Exception 
	{
		DCP_GoodsTemplateDetailRes res=this.getResponse();

		String eId=req.geteId();
		String templateId = req.getRequest().getTemplateId();

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_GoodsTemplateDetailRes.level1Elm>());

		if (getData!=null && getData.isEmpty()==false) 
		{

			Map<String, Boolean> condV = new HashMap<String, Boolean>(); //查詢條件
			condV.put("TEMPLATEID", true);			
			List<Map<String, Object>> templateList= MapDistinct.getMap(getData, condV);		

			condV.clear();
			condV.put("TEMPLATEID", true);		
			condV.put("LANG_TYPE", true);			
			List<Map<String, Object>> template_LangList= MapDistinct.getMap(getData, condV);	

			condV.clear();
			condV.put("TEMPLATEID", true);			
			condV.put("PLUNO", true);			
			List<Map<String, Object>> template_GoodsList= MapDistinct.getMap(getData, condV);	

			condV.clear();
			condV.put("TEMPLATEID", true);	
			condV.put("RANGETYPE", true);
			condV.put("ID", true);
			List<Map<String, Object>> template_RangeList= MapDistinct.getMap(getData, condV);	


			//
			DCP_GoodsTemplateDetailRes.level1Elm lv1=res.new level1Elm();
			lv1.setMemo(templateList.get(0).get("MEMO").toString());
			lv1.setRestrictAppType(templateList.get(0).get("RESTRICTAPPTYPE").toString());
			lv1.setRestrictChannel(templateList.get(0).get("RESTRICTCHANNEL").toString());
			
			lv1.setRestrictShop(templateList.get(0).get("RESTRICTSHOP").toString());
			lv1.setStatus(templateList.get(0).get("STATUS").toString());
			lv1.setTemplateId(templateList.get(0).get("TEMPLATEID").toString());
			lv1.setTemplateType(templateList.get(0).get("TEMPLATETYPE").toString());
			lv1.setCreateopid(templateList.get(0).get("CREATEOPID").toString());
			lv1.setCreateopname(templateList.get(0).get("CREATEOPNAME").toString());			
			lv1.setLastmodiname(templateList.get(0).get("LASTMODIOPNAME").toString());
			lv1.setLastmodiopid(templateList.get(0).get("LASTMODIOPID").toString());

			lv1.setLastmoditime(templateList.get(0).get("LASTMODITIME").toString());
			lv1.setCreatetime(templateList.get(0).get("CREATETIME").toString());

			//
			lv1.setTemplateName_lang(new ArrayList<DCP_GoodsTemplateDetailRes.levelTemplate>());
			for (Map<String, Object> map : template_LangList) 
			{
				DCP_GoodsTemplateDetailRes.levelTemplate Template_lang=res.new levelTemplate();
				Template_lang.setLangType(map.get("LANG_TYPE").toString());
				Template_lang.setName(map.get("TEMPLATENAME").toString());
				lv1.getTemplateName_lang().add(Template_lang);
				Template_lang=null;
			}


			//
			lv1.setGoodsList(new ArrayList<DCP_GoodsTemplateDetailRes.levelGoods>());
			for (Map<String, Object> map : template_GoodsList) 
			{
				DCP_GoodsTemplateDetailRes.levelGoods goods=res.new levelGoods();
				goods.setCanEstimate(map.get("CANESTIMATE").toString());
				goods.setCanFree(map.get("CANFREE").toString());
				goods.setCanOrder(map.get("CANORDER").toString());
				goods.setCanPurchase(map.get("CANPURCHASE").toString());
				goods.setCanRequire(map.get("CANREQUIRE").toString());
				goods.setCanRequireBack(map.get("CANREQUIREBACK").toString());
				goods.setCanReturn(map.get("CANRETURN").toString());
				goods.setCanSale(map.get("CANSALE").toString());
				goods.setClearType(map.get("CLEARTYPE").toString());
				goods.setIsAutoSubtract(map.get("IS_AUTO_SUBTRACT").toString());
				goods.setMaxQty(map.get("MAXQTY").toString());
				goods.setMinQty(map.get("MINQTY").toString());
				goods.setMultiQty(map.get("MULQTY").toString());
				goods.setPluNo(map.get("PLUNO").toString());
				goods.setSafeQty(map.get("SAFEQTY").toString());
				goods.setStatus(map.get("STATUS").toString());
				goods.setSupplierId(map.get("SUPPLIERID").toString());
				goods.setSupplierType(map.get("SUPPLIERTYPE").toString());
				goods.setTaxCode(map.get("TAXCODE").toString());
				goods.setWarningQty(map.get("WARNINGQTY").toString());

				lv1.getGoodsList().add(goods);
				goods=null;				
			}


			//
			lv1.setRangeList(new ArrayList<DCP_GoodsTemplateDetailRes.levelRange>());
			for (Map<String, Object> map : template_RangeList) 
			{
				if (map.get("RANGETYPE").toString().trim().equals("")==false) 
				{
					DCP_GoodsTemplateDetailRes.levelRange Template_range=res.new levelRange();
					Template_range.setId(map.get("ID").toString());
					Template_range.setName(map.get("NAME").toString());
					Template_range.setRangeType(map.get("RANGETYPE").toString());
					lv1.getRangeList().add(Template_range);
					Template_range=null;
				}
				
			}


			//
			res.getDatas().add(lv1);
			lv1=null;

		}

		res.setSuccess(true);
		res.setServiceStatus("000");
		res.setServiceDescription("服务执行成功！");
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception 
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_GoodsTemplateDetailReq req) throws Exception 
	{
		String eId = req.geteId();
		String templateId = req.getRequest().getTemplateId();
		String sql = null;

		StringBuffer sqlbuf=new StringBuffer("select * from dcp_goodstemplate a "
				+ "left join dcp_goodstemplate_lang b on a.eid=b.eid and a.templateid=b.templateid "
				+ "left join dcp_goodstemplate_goods c on a.eid=c.eid and a.templateid=c.templateid "
				+ "left join dcp_goodstemplate_range d on a.eid=d.eid and a.templateid=d.templateid "
				+ "where a.eid='"+eId+"' and a.templateid='"+templateId+"' ");

		sql = sqlbuf.toString();
		return sql;
	}



}
