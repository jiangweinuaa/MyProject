package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PluSaleFlowQueryReq;
import com.dsc.spos.json.cust.res.DCP_PluSaleFlowQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateUtils;
import com.google.gson.reflect.TypeToken;

/**
 * 商品计划量修改记录查询
 * @author Huawei
 *	
 */
public class DCP_PluSaleFlowQuery extends SPosBasicService<DCP_PluSaleFlowQueryReq, DCP_PluSaleFlowQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PluSaleFlowQueryReq req) throws Exception {
		// TODO  Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PluSaleFlowQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PluSaleFlowQueryReq>(){};
	}

	@Override
	protected DCP_PluSaleFlowQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PluSaleFlowQueryRes();
	}

	@Override
	protected DCP_PluSaleFlowQueryRes processJson(DCP_PluSaleFlowQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PluSaleFlowQueryRes res = null;
		res = this.getResponse();
		
		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
			String sysDate =  matter.format(dt);
			
			int totalRecords = 0; //总笔数
			int totalPages = 0;			
			
			res.setDatas(res.new level1Elm());
			
			DCP_PluSaleFlowQueryRes.level1Elm lv1 = res.new level1Elm();
			lv1.setData(new ArrayList<DCP_PluSaleFlowQueryRes.level2Elm>());			
			lv1.setPluNo(req.getRequest().getPluNo());
			lv1.setGuQingNo(req.getRequest().getGuQingNo());
			
			if(queryDatas != null && queryDatas.size() > 0){
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
					DCP_PluSaleFlowQueryRes.level2Elm lv2 = res.new level2Elm();
					String dtNo = map.getOrDefault("DTNO", "").toString();
					String dtName = map.getOrDefault("DTNAME", "").toString();
//					
					String item = map.getOrDefault("ITEM", "").toString();
					String modify_date = map.getOrDefault("MODIFY_DATE", "").toString();
					String modify_time = map.getOrDefault("MODIFY_TIME", "").toString();
					String saleNo = map.getOrDefault("SALENO", "").toString();
					String qty = map.getOrDefault("SALEQTY", "0").toString();
					String platformType = map.getOrDefault("PLATFORMTYPE", "pos").toString();
					
					SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");  
					DateUtils du = DateUtils.getInstance();
					Date duan = format2.parse(modify_date);
					
					if(!Check.Null(modify_date)) modify_date = du.format(duan, "yyyy-MM-dd") ;
					
					String modifyBy	 = map.getOrDefault("MODIFYBY", "").toString();
					String modifyByName = map.getOrDefault("MODIFYBYNAME", "").toString();
					
					lv2.setPlatformType(platformType);
					lv2.setSaleNo(saleNo);
					lv2.setDtNo(dtNo);
					lv2.setDtName(dtName);
					lv2.setItem(item);
					lv2.setModify_date(modify_date);
					lv2.setModify_time(modify_time);
					lv2.setQty(qty);
//					lv2.setModifyBy(modifyBy);
//					lv2.setModifyByName(modifyByName);
					
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
	protected String getQuerySql(DCP_PluSaleFlowQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		
		if(pageNumber==0){
			pageNumber = 1;
		}
		if(pageSize==0){
			pageSize = 99999;
		}
		
		Date dt = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		String sysDate =  matter.format(dt);
		String langType = req.getLangType();
		String pluNo = req.getRequest().getPluNo();
		String guQingNo = req.getRequest().getGuQingNo();
		
		sqlbuf.append(" SELECT * FROM ( "
				+ " SELECT   count(*) OVER() as num, row_number() over (order by a.item ) rn,  "
				+ " a.guqingNo , a.pluNo, a.pUnit , b.plu_name AS pluName  , a.item , a.dtNo , a.dtName , a.saleQty , "
				+ " a.orderNO AS saleNo , a.modifyBy , a.modifyByName , a.modify_date, a.modify_time , a.platformType  "
				+ " FROM DCP_GUQINGORDER_SALEFLOW a "
				+ " LEFT JOIN DCP_GOODS_lang b ON a.EID = b.EID AND a.pluNo = b.pluNo "
				+ " AND b.status='100' AND b.lang_type = '"+langType+"'"
				+ " WHERE a.EID = '"+req.geteId()+"' and a.pluNo = '"+pluNo+"' "
				+ " and a.guQingNo = '"+guQingNo+"' "
				);

		sqlbuf.append(" order by a.guQingNo , a.pluNO, a.item , a.dtNo    "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)  + " "
				+ " order by  guQingNo , modify_date desc , modify_time desc  "  );
		sql = sqlbuf.toString();
		return sql;
	}
	
}
