package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PluPlanQtyUpdateRecordQueryReq;
import com.dsc.spos.json.cust.res.DCP_PluPlanQtyUpdateRecordQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateUtils;
import com.google.gson.reflect.TypeToken;

/**
 * 商品计划量修改记录查询
 * @author Huawei
 *	
 */
public class DCP_PluPlanQtyUpdateRecordQuery extends SPosBasicService<DCP_PluPlanQtyUpdateRecordQueryReq, DCP_PluPlanQtyUpdateRecordQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PluPlanQtyUpdateRecordQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PluPlanQtyUpdateRecordQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PluPlanQtyUpdateRecordQueryReq>(){};
	}

	@Override
	protected DCP_PluPlanQtyUpdateRecordQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PluPlanQtyUpdateRecordQueryRes();
	}

	@Override
	protected DCP_PluPlanQtyUpdateRecordQueryRes processJson(DCP_PluPlanQtyUpdateRecordQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PluPlanQtyUpdateRecordQueryRes res = null;
		res = this.getResponse();
		
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
			String sysDate =  matter.format(dt);
			
			SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");  
			DateUtils du = DateUtils.getInstance();
			
			int totalRecords = 0; //总笔数
			int totalPages = 0;			
			
			res.setDatas(res.new level1Elm());
			
			DCP_PluPlanQtyUpdateRecordQueryRes.level1Elm lv1 = res.new level1Elm();
			lv1.setData(new ArrayList<DCP_PluPlanQtyUpdateRecordQueryRes.level2Elm>());			
			lv1.setPluNo(req.getRequest().getPluNo());
			lv1.setGuQingNo(req.getRequest().getGuQingNo());
			
			if(queryDatas != null && queryDatas.size() > 0){ //说明没分页
				if(req.getPageSize() == 0 || req.getPageNumber() ==0){
					totalRecords = 0;
					totalPages =0;
				}
				else{
					String num = queryDatas.get(0).get("NUM").toString();
					totalRecords=Integer.parseInt(num);
					totalPages = totalRecords / req.getPageSize();
					totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
					
					lv1.setpUnit(queryDatas.get(0).get("PUNIT").toString());
					
				}
				
				for (Map<String, Object> map : queryDatas) {
					DCP_PluPlanQtyUpdateRecordQueryRes.level2Elm lv2 = res.new level2Elm();
					String dtNo = map.getOrDefault("DTNO", "").toString();
					String dtName = map.getOrDefault("DTNAME", "").toString();
					String beginTime = map.getOrDefault("BEGINTIME", "").toString();
					String endTime = map.getOrDefault("ENDTIME", "").toString();
//					
					String item = map.getOrDefault("ITEM", "").toString();
					String modify_date = map.getOrDefault("MODIFY_DATE", "").toString();
					String modify_time = map.getOrDefault("MODIFY_TIME", "").toString();
					String updateType = map.getOrDefault("UPDATETYPE", "").toString();
					String qty = map.getOrDefault("QTY", "0").toString();
					
					String pfNo = map.getOrDefault("PFNO", "").toString();
					String pfOrderType = map.getOrDefault("PFORDERTYPE", "").toString();
					String modifyBy	 = map.getOrDefault("MODIFYBY", "").toString();
					String modifyByName = map.getOrDefault("MODIFYBYNAME", "").toString();
					
					if(!Check.Null(modify_date)) modify_date = du.format(format2.parse(modify_date), "yyyy-MM-dd") ;
					
					lv2.setDtNo(dtNo);
					lv2.setDtName(dtName);
					lv2.setBeginTime(beginTime);
					lv2.setEndTime(endTime);
					lv2.setItem(item);
					lv2.setModify_date(modify_date);
					lv2.setModify_time(modify_time);
					lv2.setUpdateType(updateType);
					lv2.setQty(qty);
					lv2.setSaleQty(map.get("SALEQTY").toString());
					lv2.setRestQty(map.get("RESTQTY").toString());
					lv2.setPfNo(pfNo);
					lv2.setPfOrderType(pfOrderType);
					lv2.setModifyBy(modifyBy);
					lv2.setModifyByName(modifyByName);
					
					lv1.getData().add(lv2);
					
				}
				
				res.setDatas(lv1);
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
	protected String getQuerySql(DCP_PluPlanQtyUpdateRecordQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		String langType = req.getLangType();
		if(pageNumber==0){
			pageNumber = 1;
		}
		if(pageSize==0){
			pageSize = 99999;
		}
		
		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		String sysDate =  matter.format(dt);
		
		String pluNo = req.getRequest().getPluNo();
		String guQingNo = req.getRequest().getGuQingNo();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		String dtNo = req.getRequest().getDtNo();
		
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT   count(*) OVER() as num, row_number() over (order by a.item ) rn,  "
				+ " a.guqingNO, a.pluNo , b.plu_name as pluName , a.punit , a.item , a.updateType , a.dtNo , a.dtName , a.begin_time as beginTime, a.end_Time as endTime,"
				+ " a.pfNO , a.pfOrderType , a.qty, nvl(a.saleQty,'0') as saleQty  , nvl(a.restQty, '0') as restQty ,  a.modifyBy , a.modifyByName, a.modify_date , a.modify_Time   "
				+ " FROM DCP_GUQINGORDER_PLQTYUPRECORD a "
				+ " left join DCP_GOODS_lang b on a.EID = b.EID and a.pluNO = b.pluNo and b.lang_type = '"+langType+"'"
				+ " WHERE a.EID = '"+req.geteId()+"' and a.SHOPID = '"+req.getShopId()+"' and a.pluNo = '"+pluNo+"' "
				+ " and a.guQingNo = '"+guQingNo+"' "
				);

		if (!Check.Null(dtNo) && !dtNo.equals("all"))
		{
			sqlbuf.append(" and a.dtNo = '"+dtNo+"' ");
		}
		
		if (!Check.Null(beginDate) && !Check.Null(endDate) )
		{
			beginDate = beginDate.replace("-", "");
			endDate = endDate.replace("-", "");
			
			sqlbuf.append(" and a.modify_date between '"+beginDate+"' and '"+endDate+"' ");
		}
		
		sqlbuf.append(" order by a.guQingNo , a.pluNO, a.item , a.dtNo    "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)  + " "
				+ " order by  guQingNo , modify_date desc , modify_Time desc   "  );
		sql = sqlbuf.toString();
		return sql;
	}
	
}
