package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_GoodsWeightPluQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsWeightPluQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

public class DCP_GoodsWeightPluQuery extends SPosBasicService<DCP_GoodsWeightPluQueryReq,DCP_GoodsWeightPluQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_GoodsWeightPluQueryReq req) throws Exception {
		return false;
	}
	
	@Override
	protected TypeToken<DCP_GoodsWeightPluQueryReq> getRequestType() {
		return new TypeToken<DCP_GoodsWeightPluQueryReq>(){};
	}
	
	@Override
	protected DCP_GoodsWeightPluQueryRes getResponseType()
	{
		return new DCP_GoodsWeightPluQueryRes();
	}
	
	@Override
	protected DCP_GoodsWeightPluQueryRes processJson(DCP_GoodsWeightPluQueryReq req) throws Exception {
		DCP_GoodsWeightPluQueryRes res=this.getResponse();
		
		int totalRecords = 0; //总笔数
		int totalPages = 0;
		
		String sql=getQuerySql(req);
		List<Map<String,Object>> getData=this.doQueryData(sql, null);
		
		res.setDatas(new ArrayList<>());
		
		if (getData!=null && !getData.isEmpty()) {
			String num = getData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			for (Map<String, Object> oneData : getData) {
				DCP_GoodsWeightPluQueryRes.level1Elm lv1=res.new level1Elm();
				
				//【ID1028591】【意诺-3.0】商品设置-称重PLU设置保存报错 by jinzma 20220922
				String pluType = oneData.get("PLUTYPE").toString();
				String featureNo = oneData.get("FEATURENO").toString();
				if (Check.Null(featureNo) && !pluType.equals("FEATURE")){
					featureNo =" ";
				}
				
				lv1.setPluNo(oneData.get("PLUNO").toString());
				lv1.setPluName(oneData.get("PLU_NAME").toString());
				lv1.setUnit(oneData.get("OUNIT").toString());
				lv1.setUnitName(oneData.get("UNAME").toString());
				lv1.setFeatureNo(featureNo);
				lv1.setFeatureName(oneData.get("FEATURENAME").toString());
				lv1.setWeightPluNo(oneData.get("WEIGHTPLUNO").toString());
				
				res.getDatas().add(lv1);
			}
		}
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_GoodsWeightPluQueryReq req) throws Exception {
		String eId = req.geteId();
		String langtype = req.getLangType();
		String ketTxt = req.getRequest().getKeyTxt();
		
		//計算起啟位置
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		StringBuffer sqlbuf=new StringBuffer();
		sqlbuf.append(" select * from ( "
				+ " select count(*) over() num, rownum rn,a.pluno,b.plu_name,c1.featureno,c2.featurename,d.ounit,e.uname,f.weightpluno,a.plutype"
				+ " from dcp_goods a"
				+ " left join dcp_goods_lang b on a.eid=b.eid and a.pluno=b.pluno and b.lang_type='"+langtype+"'"
				+ " left join dcp_goods_feature c1 on a.eid=c1.eid and a.pluno=c1.pluno"
				+ " left join dcp_goods_feature_lang c2 on a.eid=c2.eid and a.pluno=c2.pluno and c1.featureno=c2.featureno and c2.lang_type='"+langtype+"'"
				+ " left join DCP_GOODS_UNIT d on a.eid=d.eid and a.pluno=d.pluno and d.sunit_use='Y' and d.unit=a.baseunit"
				+ " left join dcp_unit_lang e on d.eid=e.eid and d.ounit=e.unit and e.lang_type='"+langtype+"'"
				+ " left join Dcp_Goods_Weightplu f on a.eid=f.eid and a.pluno=f.pluno and (c1.featureno=f.featureno or f.featureno=' ') and f.unit=d.ounit"
				+ " where a.isweight='Y' and a.eid='"+eId+"'"
				+ " " );
		
		if(ketTxt != null && ketTxt.length() >0 ) {
			sqlbuf.append(" AND (a.pluno like '%%"+ketTxt+"%%' or b.plu_name like '%%"+ketTxt+"%%') ");
		}
		
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+(startRow+pageSize));
		
		return sqlbuf.toString();
	}
	
	
}
