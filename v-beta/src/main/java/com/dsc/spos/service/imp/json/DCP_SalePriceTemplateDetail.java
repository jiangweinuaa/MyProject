package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SalePriceTemplateDetailReq;
import com.dsc.spos.json.cust.res.DCP_SalePriceTemplateDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_SalePriceTemplateDetail extends SPosBasicService<DCP_SalePriceTemplateDetailReq,DCP_SalePriceTemplateDetailRes>
{

	@Override
	protected boolean isVerifyFail(DCP_SalePriceTemplateDetailReq req) throws Exception 
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
	protected TypeToken<DCP_SalePriceTemplateDetailReq> getRequestType() 
	{
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SalePriceTemplateDetailReq>() {};
	}

	@Override
	protected DCP_SalePriceTemplateDetailRes getResponseType() 
	{
		// TODO Auto-generated method stub
		return new DCP_SalePriceTemplateDetailRes();
	}

	@Override
	protected DCP_SalePriceTemplateDetailRes processJson(DCP_SalePriceTemplateDetailReq req) throws Exception 
	{
		DCP_SalePriceTemplateDetailRes res=this.getResponse();

		String eId=req.geteId();
		String templateId = req.getRequest().getTemplateId();

		String sql=getQuerySql(req);
		List<Map<String , Object>> getData=this.doQueryData(sql, null);

		res.setDatas(new ArrayList<DCP_SalePriceTemplateDetailRes.level1Elm>());

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
			condV.put("ITEM", true);	
			List<Map<String, Object>> template_GoodsList= MapDistinct.getMap(getData, condV);	

			condV.clear();
			condV.put("TEMPLATEID", true);	
			condV.put("RANGETYPE", true);
			condV.put("ID", true);
			List<Map<String, Object>> template_RangeList= MapDistinct.getMap(getData, condV);	


			//
			DCP_SalePriceTemplateDetailRes.level1Elm lv1=res.new level1Elm();
			lv1.setMemo(templateList.get(0).get("MEMO").toString());
			lv1.setRestrictAppType(templateList.get(0).get("RESTRICTAPPTYPE").toString());
			lv1.setRestrictChannel(templateList.get(0).get("RESTRICTCHANNEL").toString());

			lv1.setRestrictShop(templateList.get(0).get("RESTRICTSHOP").toString());
			lv1.setRestrictCardType(templateList.get(0).get("RESTRICTCARDTYPE").toString());
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
			lv1.setTemplateName_lang(new ArrayList<DCP_SalePriceTemplateDetailRes.levelTemplate>());
			for (Map<String, Object> map : template_LangList) 
			{
				DCP_SalePriceTemplateDetailRes.levelTemplate Template_lang=res.new levelTemplate();
				Template_lang.setLangType(map.get("LANG_TYPE").toString());
				Template_lang.setName(map.get("TEMPLATENAME").toString());
				lv1.getTemplateName_lang().add(Template_lang);
				Template_lang=null;
			}


			//
			lv1.setPriceList(new ArrayList<DCP_SalePriceTemplateDetailRes.levelGoods>());
			for (Map<String, Object> map : template_GoodsList) 
			{
				DCP_SalePriceTemplateDetailRes.levelGoods goods=res.new levelGoods();

				//				
				String beginDate=map.get("BEGINDATE").toString();
				String endDate=map.get("ENDDATE").toString();		

				//格式化
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
				beginDate=simpleDateFormat.format(simpleDateFormat.parse(beginDate));
				endDate=simpleDateFormat.format(simpleDateFormat.parse(endDate));			

				goods.setBeginDate(beginDate);
				goods.setEndDate(endDate);
				goods.setFeatureName(map.get("FEATURENAME").toString());
				goods.setFeatureNo(map.get("FEATURENO").toString());
				goods.setIsDiscount(map.get("ISDISCOUNT").toString());
				goods.setIsProm(map.get("ISPROM").toString());
				goods.setItem(map.get("ITEM").toString());
				goods.setMinPrice(map.get("MINPRICE").toString());
				goods.setPluName(map.get("PLU_NAME").toString());
				goods.setPluNo(map.get("PLUNO").toString());
				goods.setPrice(map.get("PRICE").toString());
				goods.setStatus(map.get("STATUS").toString());
				goods.setUnit(map.get("UNIT").toString());
				goods.setUnitName(map.get("UNAME").toString());

				lv1.getPriceList().add(goods);
				goods=null;				
			}


			//
			lv1.setRangeList(new ArrayList<DCP_SalePriceTemplateDetailRes.levelRange>());
			for (Map<String, Object> map : template_RangeList) 
			{
				if (map.get("RANGETYPE").toString().trim().equals("")==false) 
				{
					DCP_SalePriceTemplateDetailRes.levelRange Template_range=res.new levelRange();
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
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_SalePriceTemplateDetailReq req) throws Exception 
	{
		String eId = req.geteId();
		String langtype = req.getLangType();
		String templateId = req.getRequest().getTemplateId();
		String sql = null;

		StringBuffer sqlbuf=new StringBuffer("select a.*,b.*,c.*,d.*,e.plu_name,f.featurename,g.uname from DCP_SALEPRICETEMPLATE a  "
				+ "left join DCP_SALEPRICETEMPLATE_LANG b on a.eid=b.eid and a.templateid=b.templateid and b.lang_type='"+langtype+"' "
				+ "left join DCP_SALEPRICETEMPLATE_PRICE c on a.eid=c.eid and a.templateid=c.templateid  "
				+ "left join DCP_SALEPRICETEMPLATE_RANGE d on a.eid=d.eid and a.templateid=d.templateid "
				+ "left join DCP_GOODS_LANG e on c.eid=e.eid and c.pluno=e.pluno and e.lang_type='"+langtype+"' "
				+ "left join Dcp_Goods_Feature_Lang f on c.eid=f.eid and c.pluno=f.pluno and c.featureno=f.featureno and f.lang_type='"+langtype+"' "
				+ "left join dcp_unit_lang g on c.eid=g.eid and c.unit=g.unit and g.lang_type='"+langtype+"' "
				+ "where a.eid='"+eId+"' and a.templateid='"+templateId+"' ");

		sql = sqlbuf.toString();
		return sql;
	}



}
