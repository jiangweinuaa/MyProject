package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_TGCategoryQueryReq;
import com.dsc.spos.json.cust.res.DCP_TGCategoryQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：TGCategoryGetDCP
 * 服务说明：团务分类查询
 * @author jzma 
 * @since  2019-02-11
 */
public class DCP_TGCategoryQuery extends SPosBasicService <DCP_TGCategoryQueryReq,DCP_TGCategoryQueryRes > {

	@Override
	protected boolean isVerifyFail(DCP_TGCategoryQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		if (Check.Null(req.getGetType())) 
		{
			errMsg.append("查询类型不可为空值,");
			isFail = true;
		}

		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_TGCategoryQueryReq> getRequestType() {
		// TODO 自动生成的方法存根
		return new TypeToken<DCP_TGCategoryQueryReq>(){};
	}

	@Override
	protected DCP_TGCategoryQueryRes getResponseType() {
		// TODO 自动生成的方法存根
		return new DCP_TGCategoryQueryRes();
	}

	@Override
	protected DCP_TGCategoryQueryRes processJson(DCP_TGCategoryQueryReq req) throws Exception {
		// TODO 自动生成的方法存根
		String sql=null;			
		DCP_TGCategoryQueryRes res = this.getResponse();	
		try
		{
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQData=this.doQueryData(sql, null);
			int totalRecords;								//总笔数
			int totalPages;									//总页数
			res.setDatas(new ArrayList<DCP_TGCategoryQueryRes.level1Elm>());
			if (getQData != null && getQData.isEmpty() == false) 
			{
		  	//算總頁數
				String num = getQData.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	
				for (Map<String, Object> oneData : getQData) 
				{
					DCP_TGCategoryQueryRes.level1Elm oneLv1 = res.new level1Elm();
					oneLv1.setTgCategoryNO(oneData.get("TGCATEGORYNO").toString());
					oneLv1.setTgCategoryName(oneData.get("TGCATEGORYNAME").toString());
					res.getDatas().add(oneLv1);
				}
			}
			else
			{
				totalRecords = 0;
				totalPages = 0;			
			}
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			return res;					
		}
		catch (Exception e) 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO 自动生成的方法存根

	}

	@Override
	protected String getQuerySql(DCP_TGCategoryQueryReq req) throws Exception {
		String sql=null;			
		String eId = req.geteId();
		String getType = req.getGetType();
		String keyTxt = req.getKeyTxt();
		String langType= req.getLangType();
		StringBuffer sqlbuf=new StringBuffer("");
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append(  " select * from ( " 
				+ " select count(*) over() num,row_number() over (order by A.TGCATEGORYNO) rn,a.tgcategoryno,b.tgcategoryname "
				+ " from DCP_TGCATEGORY a " 
				+ " left join DCP_TGCATEGORY_LANG b "
				+ " on a.EID=b.EID and a.tgcategoryno=b.tgcategoryno and a.type=b.type and  b.lang_type='"+langType+"'  " 
				+ " where a.EID='"+eId+"'  and  a.status='100' and a.type='"+getType+"'  " );

		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" AND (A.tgcategoryno LIKE '%%"+ keyTxt +"%%' OR b.tgcategoryname LIKE '%%"+ keyTxt +"%%' ) ");
		}
		sqlbuf.append(" ) ");
		sqlbuf.append(" where rn>"+startRow+" and rn<="+(startRow+pageSize));		

		sql = sqlbuf.toString();
		return sql;
	}

}
