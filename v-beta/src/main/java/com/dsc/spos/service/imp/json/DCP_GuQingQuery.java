package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_GuQingQueryReq;
import com.dsc.spos.json.cust.res.DCP_GuQingQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateUtils;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_GuQingQuery extends SPosBasicService<DCP_GuQingQueryReq, DCP_GuQingQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_GuQingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GuQingQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GuQingQueryReq>() {
		};
	}

	@Override
	protected DCP_GuQingQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GuQingQueryRes();
	}

	@Override
	protected DCP_GuQingQueryRes processJson(DCP_GuQingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_GuQingQueryRes res = null;
		res = this.getResponse();

		try {

			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");  
			DateUtils du = DateUtils.getInstance();
			
			SimpleDateFormat format3 = new SimpleDateFormat("HHmmss");  
			DateUtils du3 = DateUtils.getInstance();
			
			int totalRecords = 0; // 总笔数
			int totalPages = 0;
			
			res.setDatas(new DCP_GuQingQueryRes().new level1Elm());
			res.setShopId(req.getShopId());
			res.setShopName(req.getShopName());
			DCP_GuQingQueryRes.level1Elm lv1 = res.new level1Elm();
			lv1.setGuQingList(new ArrayList<DCP_GuQingQueryRes.level2Elm>());
			
			if (queryDatas != null && queryDatas.size() > 0) {
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords = Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				// 单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查询条件
				condition.put("GUQINGNO", true);
				// 调用过滤函数
				List<Map<String, Object>> getQHeader = MapDistinct.getMap(queryDatas, condition);
				
				// 单头主键字段
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查询条件
				condition2.put("GUQINGNO", true);
				condition2.put("PFNO", true);
				// 调用过滤函数
				List<Map<String, Object>> planDatas = MapDistinct.getMap(queryDatas, condition2);
				
				for (Map<String, Object> map : getQHeader) {
					DCP_GuQingQueryRes.level2Elm lv2 = res.new level2Elm();
					
					String guQingNo = map.get("GUQINGNO").toString();
					lv2.setCreateBy(map.get("CREATEBY").toString());
					lv2.setCreateByName(map.get("CREATEBYNAME").toString());
					
					String createDate = map.get("CREATE_DATE").toString();
					String modifyDate = map.get("MODIFY_DATE").toString();
					
					if(!Check.Null(createDate)) createDate = du.format(format2.parse(createDate), "yyyy-MM-dd") ;
					if(!Check.Null(modifyDate)) modifyDate = du.format(format2.parse(modifyDate), "yyyy-MM-dd") ;
					
					String createTime = map.get("CREATE_TIME").toString();
					String modiTime = map.get("MODIFY_TIME").toString();
					
					if(!Check.Null(createTime)) createTime = du3.format(format3.parse(createTime), "HH:mm:ss");
					if(!Check.Null(modiTime)) modiTime = du3.format(format3.parse(modiTime), "HH:mm:ss");
					
					lv2.setCreateDate(createDate);
					lv2.setCreateTime(createTime);
					lv2.setModifyBy(map.get("MODIFYBY").toString());
					lv2.setModifyByName(map.get("MODIFYBYNAME").toString());
					lv2.setModifyDate(modifyDate);
					lv2.setModifyTime(modiTime);
					
					lv2.setMemo(map.get("MEMO").toString());
					lv2.setStatus(map.get("STATUS").toString());
					lv2.setGuQingNo(map.get("GUQINGNO").toString());
					
					String rDate = map.get("RDATE").toString();
					String bDate = map.get("BDATE").toString();
					if(!Check.Null(rDate)) rDate = du.format(format2.parse(rDate), "yyyy-MM-dd") ;
					if(!Check.Null(bDate)) bDate = du.format(format2.parse(bDate), "yyyy-MM-dd") ;
					
					lv2.setrDate(rDate);
					lv2.setbDate(bDate);
					
					lv2.setPfOrderList(new ArrayList<DCP_GuQingQueryRes.level3Elm>());
					
					for (Map<String, Object> map2 : planDatas) {
						if (guQingNo.equals(map2.get("GUQINGNO").toString())) {
							
							if (Check.Null(map2.getOrDefault("PFNO", "").toString())) {
								continue;
							}
							
							DCP_GuQingQueryRes.level3Elm lv3 = res.new level3Elm();
							lv3.setPfNo(map2.get("PFNO").toString());
							lv3.setPfOrderType(map2.get("PFORDERTYPE").toString());
							lv2.getPfOrderList().add(lv3);
						}

					}
					lv1.getGuQingList().add(lv2);
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
	protected String getQuerySql(DCP_GuQingQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		// 分页处理
		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();
		int startRow = (pageNumber - 1) * pageSize;
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();

		String keyTxt = req.getRequest().getKeyTxt(); // 支持单据号模糊查询
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		beginDate = beginDate.replace("-", "");
		endDate = endDate.replace("-", "");

		sqlbuf.append(" select * from ( "
				+ " select count(distinct a.guQIngNo ) OVER() AS NUM,  dense_rank() over (order BY  a.guQIngNo desc ) rn, "
				+ " a.createBy, a.create_date , a.create_time , a.modifyBy, a.modify_date, a.modify_time , "
				+ " a.memo , a.status , d.pfNo , d.pfOrderType, a.bDate, a.rDate, a.guQingNo , "
				+ " b.op_name as createByName , c.op_name as modifyByName " + " from DCP_guqingOrder a "
				+ " LEFT JOIN DCP_GUQINGORDER_detail d ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.guqingNo = d.guqingNo  "
				+ " LEFT JOIN PLATFORM_STAFFS_lang b on a.EID = b.EID and a.createBy = b.opNo and b.lang_Type = '" + langType + "'    and b.status='100'   "
				+ " LEFT JOIN PLATFORM_STAFFS_lang c on a.EID = c.EID and a.modifyBy = c.opNo and c.lang_Type = '" + langType + "'    and c.status='100'   " 
				+ " WHERE a.EID  = '" + eId + "' and a.SHOPID = '" + shopId + "' "
				);

		if (!Check.Null(keyTxt)) {
			sqlbuf.append(" and ( a.guQingNO like '%%" + keyTxt + "%%' or d.pfNO like '%%" + keyTxt + "%%') ");
		}

		// 查询适用门店的广告信息，要包含全部门店
		if (!Check.Null(beginDate) && !Check.Null(endDate)) {
			sqlbuf.append(" and   a.bDate between '" + beginDate + "' and  '" + endDate + "' ");
		}

		sqlbuf.append(" order by a.guQingNO desc     " + " ) t where t.rn > " + startRow + " and t.rn<="
				+ (startRow + pageSize) + " order BY guQingNo desc  ");
		sql = sqlbuf.toString();
		return sql;
	}

}
