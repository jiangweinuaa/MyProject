package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_SupPriceTemplateGoodsQueryReq;
import com.dsc.spos.json.cust.res.DCP_SupPriceTemplateGoodsQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_SupPriceTemplateGoodsQuery extends SPosBasicService<DCP_SupPriceTemplateGoodsQueryReq,DCP_SupPriceTemplateGoodsQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_SupPriceTemplateGoodsQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if(req.getRequest()==null) {
			isFail = true;
			errMsg.append("request不能为空 ");
		}else {
			if (Check.Null(req.getRequest().getTemplateId())) {
				errMsg.append("模板编码不能为空值 ");
				isFail = true;
			}
			if (Check.Null(req.getRequest().getSortType())) {
				errMsg.append("显示顺序不能为空值 ");
				isFail = true;
			}
			//ID1024837】【潮品3.0】零售价模板04006底下商品没有显示，数据表DCP_SALEPRICETEMPLATE_PRICE中有
			String effectStatus = req.getRequest().getEffectStatus();
			if (!Check.Null(effectStatus) && !effectStatus.equals("0")){
				if (Check.Null(req.getRequest().getEffectDate())) {
					isFail = true;
					errMsg.append("价格查询日期不能为空 ");
				}
			}
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_SupPriceTemplateGoodsQueryReq> getRequestType() {
		return new TypeToken<DCP_SupPriceTemplateGoodsQueryReq>() {};
	}
	
	@Override
	protected DCP_SupPriceTemplateGoodsQueryRes getResponseType() {
		return new DCP_SupPriceTemplateGoodsQueryRes();
	}
	
	@Override
	protected DCP_SupPriceTemplateGoodsQueryRes processJson(DCP_SupPriceTemplateGoodsQueryReq req) throws Exception {
		DCP_SupPriceTemplateGoodsQueryRes res=this.getResponse();
		int totalRecords = 0;
		int totalPages = 0;
		try{
			String sql=getQuerySql(req);
			List<Map<String , Object>> getData=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_SupPriceTemplateGoodsQueryRes.level1Elm>());
			if (getData!=null && !getData.isEmpty()) {
				//算總頁數
				String num = getData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> oneData : getData) {
					DCP_SupPriceTemplateGoodsQueryRes.level1Elm lv1=res.new level1Elm();
					lv1.setBeginDate(oneData.get("BEGINDATE").toString());
					lv1.setEndDate(oneData.get("ENDDATE").toString());
					lv1.setFeatureNo(oneData.get("FEATURENO").toString());
					lv1.setItem(oneData.get("ITEM").toString());
					lv1.setPluNo(oneData.get("PLUNO").toString());
					lv1.setPluName(oneData.get("PLU_NAME").toString());
					lv1.setPrice(oneData.get("PRICE").toString());
					lv1.setUnit(oneData.get("UNIT").toString());
					lv1.setUnitName(oneData.get("UNAME").toString());
					
					res.getDatas().add(lv1);
					lv1=null;
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			return res;
			
		}catch (Exception e){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_SupPriceTemplateGoodsQueryReq req) throws Exception {
		String eId = req.geteId();
		String langtype = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		String templateId = req.getRequest().getTemplateId();
		String sortType = req.getRequest().getSortType();
		String category=req.getRequest().getCategory();
		//ID1024837】【潮品3.0】零售价模板04006底下商品没有显示，数据表DCP_SALEPRICETEMPLATE_PRICE中有
		String effectDate = req.getRequest().getEffectDate();
		String effectStatus = req.getRequest().getEffectStatus();
		if (Check.Null(effectStatus)){
			effectStatus="0";
		}
		//計算起啟位置
		int pageSize = req.getPageSize();
		int startRow=(req.getPageNumber()-1) * pageSize;
		StringBuffer sqlbuf=new StringBuffer();
		sqlbuf.append(""
				+ " select * from ( "
				+ " select count(*) over() num, ");
		if(sortType != null && sortType.length() >0) {
			if (sortType.equals("1")) {
				sqlbuf.append(" row_number() over(order by a.item desc) rn, ");
			} else {
				sqlbuf.append(" row_number() over(order by a.pluno asc) rn, ");
			}
		}
		sqlbuf.append(""
				+ " a.*,b.plu_name,c.uname from DCP_SUPPRICETEMPLATE_PRICE a  "
				+ " left join DCP_GOODS_LANG b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langtype+"' "
				+ " left join DCP_UNIT_LANG c on a.eid=c.eid and a.unit=c.unit and c.lang_type='"+langtype+"' "
				+ " left join dcp_goods d on a.eid=d.eid and a.pluno=d.pluno "
				+ " where a.eid='"+eId+"' ");
		
		if(!Check.Null(templateId)) {
			sqlbuf.append(" and a.templateid='"+templateId +"' ");
		}
		if(!Check.Null(ketTxt)) {
			sqlbuf.append(" and (a.pluno like '%%"+ketTxt+"%%' or b.plu_name like '%%"+ketTxt+"%%') ");
		}
		if(!Check.Null(category)) {
			sqlbuf.append(" and d.category='"+category+"' ");
		}
		if (!effectStatus.equals("0")){
			/*增加两个字段：effectDate：日期YYYYMMDD，effectStatus：0全部，1已生效，2未生效，3已失效
		      当effectStatus 值为0的时候，就不要关联effectDate值，直接查询模板下的全部商品
		      当effectStatus 值为1的时候，就需要查effectDate大于等于商品生效开始日期，且effectDate小于等于商品生效结束日期；
		      当effectStatus 值为2的时候，就需要查effectDate小于商品生效开始日期
		      当effectStatus 值为3的时候，就需要查effectDate大于商品生效结束日期；*/
			switch (effectStatus){
				case "1":
					sqlbuf.append(" and to_date('"+effectDate+"','yyyy-MM-dd')>=a.begindate "
							+ " and to_date('"+effectDate+"','yyyy-MM-dd')<=a.enddate ");
					break;
				case "2":
					sqlbuf.append(" and to_date('"+effectDate+"','yyyy-MM-dd')<a.begindate");
					break;
				case "3":
					sqlbuf.append(" and to_date('"+effectDate+"','yyyy-MM-dd')>a.enddate");
					break;
			}
		}
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		return sqlbuf.toString();
	}
	
}
