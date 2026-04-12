package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_promListQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_promListQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 促销查询
 * @author Huawei
 *
 */
public class DCP_promListQuery_Open extends SPosBasicService<DCP_promListQuery_OpenReq, DCP_promListQuery_OpenRes> {

	@Override
	protected boolean isVerifyFail(DCP_promListQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0; // 考慮當錯誤很多時則直接顯示格式錯誤；
		String shopId = req.getRequest().getShopId();

		if (Check.Null(shopId)) {
			errCt++;
			errMsg.append("门店编号不可为空值  ");
			isFail = true;
		}

		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_promListQuery_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_promListQuery_OpenReq>(){};
	}

	@Override
	protected DCP_promListQuery_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_promListQuery_OpenRes();
	}

	@Override
	protected DCP_promListQuery_OpenRes processJson(DCP_promListQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_promListQuery_OpenRes res = null;
		res = this.getResponse();
		int totalRecords = 0;								//总笔数
		int totalPages = 0;		
		String sql = "";
		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_promListQuery_OpenRes.level1Elm>());
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				Map<String, Object> oneData_Count = getQDataDetail.get(0);
				
//				String num = oneData_Count.get("NUM").toString();
//				totalRecords=Integer.parseInt(num);
//				//算總頁數
//				totalPages = totalRecords / req.getPageSize();
//				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				for (Map<String, Object> oneData : getQDataDetail) 
				{
			
					DCP_promListQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
					lv1.setPromNo(oneData.get("PROMNO").toString());
					lv1.setPromName(oneData.get("PROMNAME").toString());
					lv1.setPromCategory(oneData.get("PROMCATEGORY").toString());
					lv1.setPromDiscrep(oneData.get("PROMDISCREP").toString());
					lv1.setStartDate(oneData.get("STARTDATE").toString());
					lv1.setEndDate(oneData.get("ENDDATE").toString());
					res.getDatas().add(lv1);
				}
			}
				
//			res.setPageNumber(req.getPageNumber());
//			res.setPageSize(req.getPageSize());
//			res.setTotalRecords(totalRecords);
//			res.setTotalPages(totalPages);
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！！");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_promListQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
//		int pageNumber=req.getPageNumber();
//		int pageSize=req.getPageSize();
//		if(pageNumber==0){
//			pageNumber = 1;
//		}
//		if(pageSize==0){
//			pageSize = 99999;
//		}
//		int startRow=(pageNumber-1) * pageSize;
		
		String eId = req.getRequest().geteId();
		String shopId = req.getRequest().getShopId();
		
		sqlbuf.append(" SELECT * FROM ( "
			+ " SELECT count(*) OVER() AS NUM,  row_number() over (order BY  a.PromNo ) rn,  "
			+ " a.company , a.promNo , a.promCategory, a.promName  ,  a.PRINT_DESCRIPT as PromDiscrep ,"
			+ " a.startTime as startDate ,  a.endTime as endDate  "
			+ " FROM PROM_OBJECT a  "
			+ " LEFT JOIN PROM_SHOP b ON a.COMPANY = b.COMPANY AND a.PROMNO = b.PROMNO "
			+ " WHERE  b.isValid = 'Y'  AND a.isvalid = 'Y'   and b.SHOPID  = '"+shopId+"'  " );
			
		if (!Check.Null(eId))
		{
			sqlbuf.append(" and a.company = '"+eId+"' ");
		}
		
		sqlbuf.append( " ORDER BY a.COMPANY DESC , PROMNO "
			+ " ) t "
//			+ " WHERE t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize) 
			+ " ORDER BY  COMPANY , PROMNO  "
				);
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	
	
}
