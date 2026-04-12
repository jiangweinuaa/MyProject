package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SeriesQueryReq;
import com.dsc.spos.json.cust.res.DCP_SeriesQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_SeriesQuery extends SPosBasicService<DCP_SeriesQueryReq, DCP_SeriesQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SeriesQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SeriesQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SeriesQueryReq>(){};
	}

	@Override
	protected DCP_SeriesQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SeriesQueryRes();
	}

	@Override
	protected DCP_SeriesQueryRes processJson(DCP_SeriesQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		
		DCP_SeriesQueryRes res = null;
		res = this.getResponse();

		//获取当前登陆用户的语言类型
		String langType_cur = req.getLangType();
		
		int totalRecords;								//总笔数
		int totalPages;									//总页数
		
		sql = this.getQuerySql(req);
		String[] conditionValues1 = { }; //查詢條件
		List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, conditionValues1);
		
		if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
		{
			String num = getQDataDetail.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);
			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			//单头主键字段
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("SERIESNO", true);		
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
			
			res.setDatas(new ArrayList<DCP_SeriesQueryRes.level1Elm>());
			                   
			for (Map<String, Object> oneData : getQHeader) 
			{
				DCP_SeriesQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setSeriesName_lang(new ArrayList<DCP_SeriesQueryRes.level2Elm>());
				// 取出第一层
				String seriesNo = oneData.get("SERIESNO").toString();
				String status = oneData.get("STATUS").toString();
				oneLv1.setSeriesNo(seriesNo);
				oneLv1.setStatus(status);
				for (Map<String, Object> oneData2 : getQDataDetail) 
				{
					if(seriesNo.equals(oneData2.get("SERIESNO")) == false)
						continue;
						
					DCP_SeriesQueryRes.level2Elm oneLv2 = res.new level2Elm();
					String lSeriesNO = oneData2.get("LSERIESNO").toString();
					String lSeriesName = oneData2.get("LSERIESNAME").toString();
					String langType = oneData2.get("LANGTYPE").toString();
								
					if(langType.equals(langType_cur))
					{
					  oneLv1.setSeriesName(lSeriesName);
					}
					
					
					oneLv2.setName(lSeriesName);
					oneLv2.setLangType(langType);				
					oneLv1.getSeriesName_lang().add(oneLv2);
				}
				res.getDatas().add(oneLv1);
			}
		}
		else
		{
			res.setDatas(new ArrayList<DCP_SeriesQueryRes.level1Elm>());
		}
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_SeriesQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		String sql=null;
		String eId = req.geteId();
		
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
    
		String keyTxt = null;
		String status = null;
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
			
		}
		
		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;
		
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append( "SELECT num,rn , seriesNO,STATUS , lSeriesNO , langType , lSeriesNO , langType, "
				+ " lSeriesName  FROM ( "
				+ " SELECT COUNT(DISTINCT a.seriesNO ) OVER() NUM ,dense_rank() over(ORDER BY a.seriesNO) rn, a.EID ,"
				+ " 	a.seriesNO AS seriesNO , a.STATUS AS STATUS, b.seriesno AS lSeriesNO , b.lang_Type AS langType , "
				+ " 	b.series_name AS lSeriesName"
				+ " 	FROM dcp_series  a LEFT JOIN  dcp_series_lang b ON a.EID = b.EID AND a.seriesNO = b.seriesNO )"
				+ " 	WHERE EID = '"+eId+"' " );
		if(keyTxt != null && keyTxt.length() > 0 ){
			sqlbuf.append( " AND (seriesno LIKE '%%"+keyTxt+"%%' or  lseriesName LIKE '%%"+keyTxt+"%%'  )  " );
		}
		if(status != null && status.length() > 0 ){
			sqlbuf.append( " AND STATUS = '"+status+"' " );
		}
		
		sqlbuf.append(" and rn >"+startRow+"  and rn < "+(startRow+pageSize)+" order by seriesNO " );
		
		sql = sqlbuf.toString();
		return sql;
	}
	
}
