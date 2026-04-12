package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PFOrderQueryReq;
import com.dsc.spos.json.cust.res.DCP_PFOrderQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateUtils;
import com.google.gson.reflect.TypeToken;

/**
 * 计划报单列表查询
 * @author yuanyy 
 * 
 */
public class DCP_PFOrderQuery extends SPosBasicService<DCP_PFOrderQueryReq, DCP_PFOrderQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PFOrderQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PFOrderQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderQueryReq>(){};
	}

	@Override
	protected DCP_PFOrderQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderQueryRes();
	}

	@Override
	protected DCP_PFOrderQueryRes processJson(DCP_PFOrderQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PFOrderQueryRes res = null;
		res = this.getResponse();
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

//			Date dt = new Date();
//			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
//			String sysDate =  matter.format(dt);
			
			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_PFOrderQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				for (Map<String, Object> map : queryDatas) {
					DCP_PFOrderQueryRes.level1Elm lv1 = res.new level1Elm();
					String createBy = map.getOrDefault("CREATEBY", "").toString();
					String createByName = map.getOrDefault("CREATEBYNAME", "").toString();
					String createDate = map.getOrDefault("CREATE_DATE", "").toString();
					String createTime = map.getOrDefault("CREATE_TIME", "").toString();
					String modifyBy = map.getOrDefault("MODIFYBY", "").toString();
					String modifyByName = map.getOrDefault("MODIFYBYNAME", "").toString();
					String modifyDate = map.getOrDefault("MODIFY_DATE", "").toString();
					String modifyTime = map.getOrDefault("MODIFY_TIME", "").toString();
					
					String memo = map.getOrDefault("MEMO", "").toString();
					String pfNo = map.getOrDefault("PFNO", "").toString();
					String bDate = map.getOrDefault("BDATE", "").toString();
					String rDate = map.getOrDefault("RDATE", "").toString();
					
					String pfOrderType = map.getOrDefault("PFORDERTYPE", "estimate").toString();
					String status = map.getOrDefault("STATUS", "").toString();
					String totAmt = map.getOrDefault("TOTAMT", "0").toString();
					String totCQty = map.getOrDefault("TOTCQTY", "0").toString();
					String totPQty = map.getOrDefault("TOTPQTY", "0").toString();
					String predictAmt = map.getOrDefault("PREDICTAMT", "0").toString();
					SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");  
					DateUtils du = DateUtils.getInstance();
					Date duan = format2.parse(bDate);
					
					if(!Check.Null(bDate)) bDate = du.format(duan, "yyyy-MM-dd") ;
					if(!Check.Null(rDate)) rDate = du.format(format2.parse(rDate), "yyyy-MM-dd") ;
					if(!Check.Null(createDate)) createDate = du.format(format2.parse(createDate), "yyyy-MM-dd") ;
					if(!Check.Null(modifyDate)) modifyDate = du.format(format2.parse(modifyDate), "yyyy-MM-dd") ;
					
					lv1.setCreateBy(createBy);
					lv1.setCreateByName(createByName);
					lv1.setCreateDate(createDate);
					lv1.setCreateTime(createTime);
					lv1.setModifyBy(modifyBy);
					lv1.setModifyByName(modifyByName);
					lv1.setModifyDate(modifyDate);
					lv1.setModifyTime(modifyTime);
					lv1.setMemo(memo);
					lv1.setPfNo(pfNo);
					lv1.setbDate(bDate);
					lv1.setrDate(rDate);
					if(Check.Null(pfOrderType)){
						pfOrderType = "estimate";
					}
					
					lv1.setPfOrderType(pfOrderType);
					lv1.setStatus(status);
					lv1.setTotAmt(totAmt);
					lv1.setTotCQty(totCQty);
					lv1.setTotPQty(totPQty);
					lv1.setPredictAmt(predictAmt);
					
					res.getDatas().add(lv1);
				}
			}
			
			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常！");
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PFOrderQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;

//		Date dt = new Date();
//		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
//		String sysDate =  matter.format(dt);
		
		String beginDate = req.getRequest().getBeginDate();
		String endDate  = req.getRequest().getEndDate();
		String bDate  = req.getRequest().getbDate();
		String rDate  = req.getRequest().getrDate();
		String keyTxt = req.getRequest().getKeyTxt();
		String status = req.getRequest().getStatus();
		
		String langType = req.getLangType();
		
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT count(*) OVER() as num, row_number() over (order by a.pfNO desc) rn, "  
				+ " pfNO , bDate , rDate, a.memo , a.status , a.tot_PQTY AS totPQty , a.tot_CQty AS totCqty , a.tot_amt AS totAmt , a.pfOrderType , "
				+ " a.createBy, f1.op_name as  createByName , a.create_date , a.create_time , a.modifyBy ,f2.op_name as  modifyByName , "
				+ " a.modify_date , a.modify_time , a.predictAmt " 
				+ " FROM DCP_porder_forecast a "
				+ " LEFT JOIN platform_staffs_lang f1 ON a.EID = f1.EID AND a.createby = f1.opno AND f1.lang_type = '"+langType+"' "
				+ " LEFT JOIN platform_staffs_lang f2 ON a.EID = f2.EID AND a.modifyby = f2.opno AND f2.lang_type = '"+langType+"' "
				+ " WHERE a.EID = '"+req.geteId()+"' AND SHOPID = '"+req.getShopId()+"'  "
				);

		if (!Check.Null(status))
		{
			sqlbuf.append(" and a.status = '"+status+"' ");
		}

//		if (!Check.Null(bDate))
//		{
//			sqlbuf.append(" and a.bDate = '"+bDate+"' ");
//		}
		
		if( !Check.Null(beginDate) && !Check.Null(endDate) ){
			
			beginDate = beginDate.replace("-", "");
			endDate = endDate.replace("-", "");
			
			sqlbuf.append(" and a.bDate between '"+beginDate+"' and  '"+endDate+"' ");
		}
		
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and ( a.pfNO like '%%"+keyTxt+"%%' or a.memo like '%%"+keyTxt+"%%') ");
		}
		sqlbuf.append(" order by a.status , a.pfNo   "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)  + "order by t.pfNo desc "  );
		sql = sqlbuf.toString();
		return sql;
	}
	
}
