package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GoodsBrandQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsBrandQueryRes;
import com.dsc.spos.json.cust.res.DCP_GoodsCategoryQueryRes;
import com.dsc.spos.json.cust.res.DCP_GoodsBrandQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 商品品牌查询 2018-10-17
 * @author yuanyy
 *
 */
public class DCP_GoodsBrandQuery extends SPosBasicService<DCP_GoodsBrandQueryReq, DCP_GoodsBrandQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsBrandQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsBrandQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsBrandQueryReq>(){};
	}

	@Override
	protected DCP_GoodsBrandQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsBrandQueryRes();
	}

	@Override
	protected DCP_GoodsBrandQueryRes processJson(DCP_GoodsBrandQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql = null;		
		
		//查詢資料
		DCP_GoodsBrandQueryRes res = null;
		res = this.getResponse();
	//获取当前登陆用户的语言类型
			String langType_cur = req.getLangType();
		//单头总数
		sql = this.getQuerySql(req);			
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		res.setDatas(new ArrayList<DCP_GoodsBrandQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) 
		{
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("BRANDNO", true);		
			//调用过滤函数
			List<Map<String, Object>> catDatas=MapDistinct.getMap(getQData, condition);
			res.setDatas(new ArrayList<DCP_GoodsBrandQueryRes.level1Elm>());
			
			for (Map<String, Object> oneData : catDatas) 
			{
				DCP_GoodsBrandQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String brandNo = oneData.get("BRANDNO").toString();			
				oneLv1.setBrandNo(brandNo);
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setBrandName_lang(new ArrayList<DCP_GoodsBrandQueryRes.level2Elm>());
			
				for (Map<String, Object> langDatas : getQData) 
				{
					//过滤属于此单头的明细
					if(brandNo.equals(langDatas.get("BRANDNO")) == false)
						continue;
					
					DCP_GoodsBrandQueryRes.level2Elm fstLang = res.new level2Elm();
					
					String langType = langDatas.get("LANGTYPE").toString();
					String lBrandName = langDatas.get("BRANDNAME").toString();				
					if(langType.equals(langType_cur))
					{
					  oneLv1.setBrandName(lBrandName);
					}
					
					fstLang.setLangType(langType);
					fstLang.setName(lBrandName);
					
					
					oneLv1.getBrandName_lang().add(fstLang);
					fstLang = null;
				}
				res.getDatas().add(oneLv1);
				oneLv1 = null;
			}
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_GoodsBrandQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer(); 
		
		String eId = req.geteId();
		String keyTxt = null;
		String status = null;
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append(" SELECT * FROM ("
		 + " SELECT  COUNT(DISTINCT a.BRANDNO ) OVER() NUM ,dense_rank() over(ORDER BY a.BRANDNO) rn, a. *  FROM "
		 + " ( SELECT a.BRANDNO , b.brand_Name AS brandName, a.status, b.lang_Type AS langType ,"
		 + "  a.EID  FROM dcp_brand a   "
		 + " LEFT JOIN dcp_brand_lang b ON a.EID = b.EID AND a.BRANDNO = b.BRANDNO )   a "
		 + " WHERE  EID = '"+eId+"'  " );
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (BRANDNO like '%%"+keyTxt+"%%' or brandName LIKE '%%"+keyTxt+"%%' )  ");
		}
		if(status!=null && status.length()>0)
		{
			sqlbuf.append(" and status='" + status + "' ");
		}
		sqlbuf.append( " )   WHERE rn >  "+startRow+" and rn < "+(startRow + pageSize)+" " );
		
		sql = sqlbuf.toString();
		return sql;
	}

}
