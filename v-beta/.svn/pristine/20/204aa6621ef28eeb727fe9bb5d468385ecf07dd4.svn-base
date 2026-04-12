package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_ElementQueryReq;
import com.dsc.spos.json.cust.res.DCP_ElementQueryDCPRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 影响因素查询
 * @author yuanyy 2019-10-21
 *
 */
public class DCP_ElementQuery extends SPosBasicService<DCP_ElementQueryReq, DCP_ElementQueryDCPRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_ElementQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
	    StringBuffer errMsg = new StringBuffer("");
	    if(null==req.getRequest())
	    {
		   	errMsg.append("request不能为空值 ");
		   	isFail = true;
	    } else
	    {
	    	//必传值不为空
	    	String eType = req.getRequest().getE_Type();

	    	if(Check.Null(eType)){
	    		errMsg.append("因素类型不能为空值 ");
	    		isFail = true;
	    	}
	    }
	    if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_ElementQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_ElementQueryReq>(){};
	}

	@Override
	protected DCP_ElementQueryDCPRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_ElementQueryDCPRes();
	}

	@Override
	protected DCP_ElementQueryDCPRes processJson(DCP_ElementQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_ElementQueryDCPRes res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> getDatas = this.doQueryData(sql, null);
			if(!getDatas.isEmpty()){
				res.setDatas(new ArrayList<DCP_ElementQueryDCPRes.level1Elm>());
				
				String num = getDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
				
				for (Map<String, Object> oneData : getDatas) 
				{
					DCP_ElementQueryDCPRes.level1Elm lv1 =  res.new level1Elm();
					String eType = oneData.getOrDefault("ETYPE", "").toString();
					String eNo = oneData.getOrDefault("ENO", "").toString();
					String eName = oneData.getOrDefault("ENAME", "").toString();
					String eRatio = oneData.getOrDefault("ERATIO", "0").toString();
					String status = oneData.getOrDefault("STATUS", "").toString();
					lv1.setE_Type(eType);
					lv1.seteNo(eNo);
					lv1.seteName(eName);
					lv1.seteRatio(eRatio);
					lv1.setStatus(status);
					res.getDatas().add(lv1);
					lv1 = null;
					
				}
				
			}
			else{
				res.setDatas(new ArrayList<DCP_ElementQueryDCPRes.level1Elm>());
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败");
			
		}
		
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_ElementQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		String eType = req.getRequest().getE_Type();
		String keyTxt = req.getRequest().getKeyTxt();
		
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" select * from ("
				+ " SELECT  COUNT( DISTINCT  a.e_no ) OVER() NUM ,  "
				+ " dense_rank() over(ORDER BY a.e_type , a.e_no  ) rn, "
				+ " a.e_type AS eType, a.e_no AS eNo, a.e_name AS eName , a.e_ratio AS eRatio, a.status     "
				+ " FROM DCP_ELEMENT a WHERE EID = '"+req.geteId()+"' and  e_Type = '"+eType+"'  "
				+ " and a.SHOPID = '"+req.getShopId()+"' " );
		if (keyTxt != null && keyTxt.length()>0)
		{
			sqlbuf.append(" AND (a.e_no like '%%"+keyTxt+"%%'  OR a.e_Name LIKE '%%"+keyTxt+"%%'  ) ");
		}
		sqlbuf.append(" GROUP BY a.e_type  , a.e_no , a.e_name   , a.e_ratio  , a.status   )     ");
		sqlbuf.append(" where  rn>"+startRow+" and rn<="+(startRow+pageSize) + " order by rn, eType , eNo ");
		sql = sqlbuf.toString();
		return sql;
	}
	
}
