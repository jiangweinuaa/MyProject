package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SalesPerformanceQueryReq;
import com.dsc.spos.json.cust.res.DCP_SalesPerformanceQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 销售业绩查询
 * @author yuanyy
 *
 */
public class DCP_SalesPerformanceQuery extends SPosBasicService<DCP_SalesPerformanceQueryReq, DCP_SalesPerformanceQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SalesPerformanceQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		
		String opNO = req.getRequest().getOpNO();
		String shopId = req.getRequest().getShopId();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		if (Check.Null(opNO)) {
			errCt++;
			errMsg.append("用户编码不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(shopId)) {
			errCt++;
			errMsg.append("门店编码不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(beginDate)) {
			errCt++;
			errMsg.append("开始日期不可为空值, ");
			isFail = true;
		} 
		
		if (Check.Null(endDate)) {
			errCt++;
			errMsg.append("截止日期不可为空值, ");
			isFail = true;
		} 

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_SalesPerformanceQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SalesPerformanceQueryReq>(){};
	}

	@Override
	protected DCP_SalesPerformanceQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SalesPerformanceQueryRes();
	}

	@Override
	protected DCP_SalesPerformanceQueryRes processJson(DCP_SalesPerformanceQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_SalesPerformanceQueryRes res = null;
		res = this.getResponse();
		String sql = "";
		try {
			
			sql = this.getQuerySql(req);
			List<Map<String, Object>> getQDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_SalesPerformanceQueryRes.level1Elm>());
			
			double totAmt = 0;
			int totOQty = 0 ;
			
			if(getQDatas != null && getQDatas.size() > 0){
				for (Map<String, Object> oneData : getQDatas) 
				{
					DCP_SalesPerformanceQueryRes.level1Elm lv1 = res.new level1Elm();
					String saleNo = oneData.get("SALENO").toString();
					String saleType = oneData.get("TYPE").toString();
					String saleAmt = oneData.getOrDefault("TOT_AMT","0").toString();
					String sDate = oneData.get("SDATE").toString();
					lv1.setSaleNo(saleNo);
					lv1.setSaleType(saleType);
					lv1.setSaleAmt(saleAmt);
					lv1.setSaleDate(sDate);
					res.getDatas().add(lv1);

					double saleAmtDob = Double.parseDouble(saleAmt);
					if(saleType != null &&  !saleType.equals("0") ){
						saleAmtDob = -saleAmtDob;
					}
					
					totAmt = totAmt + saleAmtDob;
					totOQty += 1;
					
				}
				
			}
			
			res.setTotOQty(totOQty+ "");
			res.setTotAmt(totAmt + "");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_SalesPerformanceQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();

		if(pageSize == 0){
			pageSize = 99999;
		}
		
		if(pageNumber == 0){
			pageNumber = 1;
		}
		
		int startRow=(pageNumber-1) * pageSize;
		
		String eId = req.geteId();
		
		//规格结构设计的有问题，表里不需要在设计shopId 字段
		String shopId = req.getRequest().getShopId();
		String opNO = req.getRequest().getOpNO();
		
		String defaultBeginDate = "";
		String defaultEndDate = "";
		
		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(new Date());
		calendar2.set(Calendar.DAY_OF_MONTH,1);
		calendar2.add(Calendar.MONTH,0);
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");  
		
		defaultBeginDate = format2.format(calendar2.getTime());
		
		calendar2.set(Calendar.DAY_OF_MONTH,0);
	    calendar2.add(Calendar.MONTH,1);
	    defaultEndDate = format2.format(calendar2.getTime());
		
		String beginDate = req.getRequest().getBeginDate()==null? defaultBeginDate :req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate()==null? defaultEndDate :req.getRequest().getEndDate();
		
		String[] saleType = req.getRequest().getSaleType();
		String saleTypeStr = "";
		if (saleType == null || saleType.length == 0)
		{
			saleTypeStr = "";
		}
		else 
		{
			saleTypeStr = getString(saleType); 
		}

		sqlbuf.append(" SELECT EID  ,  SHOPID , saleNo , tot_amt , pay_amt , type , sDate||sTime as sDate FROM DCP_SALE "
				+ " WHERE EID = '"+eId+"' AND SHOPID = '"+shopId+"' "
				+ " AND opNO = '"+opNO+"' "
				+ " AND sDate BETWEEN '"+beginDate+"' AND '"+endDate+"' " );
		
		if(saleType != null && saleType.length > 0){
			sqlbuf.append(" and type in ("+saleTypeStr+")");
		}
		
		sqlbuf.append( " ORDER BY sdate, stime  , saleNo "
				+ "");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	
	protected String getString(String[] str)
	{
		String str2 = "";

		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		//System.out.println(str2);

		return str2;
	}

	
	
}
