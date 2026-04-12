package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_FlashviewQueryReq;
import com.dsc.spos.json.cust.res.DCP_FlashviewQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 轮播广告查询
 * @author yuanyy
 *
 */
public class DCP_FlashviewQuery extends SPosBasicService<DCP_FlashviewQueryReq, DCP_FlashviewQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_FlashviewQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_FlashviewQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_FlashviewQueryReq>(){};
	}

	@Override
	protected DCP_FlashviewQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_FlashviewQueryRes();
	}
	
	@Override
	protected DCP_FlashviewQueryRes processJson(DCP_FlashviewQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_FlashviewQueryRes res = null;
		res = this.getResponse();
		try {
			
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			int totalRecords = 0;								//总笔数
			int totalPages = 0;			
			res.setDatas(new ArrayList<DCP_FlashviewQueryRes.level1Elm>());
			
			if(queryDatas != null && queryDatas.size() > 0){
				String num = queryDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
				
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("FLASHVIEWID", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(queryDatas, condition);
				
				for (Map<String, Object> map : getQHeader) {
					DCP_FlashviewQueryRes.level1Elm lv1 = res.new level1Elm();
					
					String flashviewId = map.get("FLASHVIEWID").toString();
					lv1.setFlashviewId(map.get("FLASHVIEWID").toString());
					lv1.setFileName(map.get("FILENAME").toString());
					lv1.setLinkUrl(map.get("LINKURL").toString());
					lv1.setShopType(map.get("SHOPTYPE").toString());
					lv1.setPriority(map.get("PRIORITY").toString());
					lv1.setStatus(map.get("STATUS").toString());
					
					lv1.setShops(new ArrayList<DCP_FlashviewQueryRes.level2Elm>());
					for (Map<String, Object> map2 : queryDatas) {
						if(flashviewId.equals(map2.get("FLASHVIEWID").toString())){
							
							if(Check.Null(map2.getOrDefault("SHOPID","").toString())){
								continue;
							}
							
							DCP_FlashviewQueryRes.level2Elm lv2 = res.new level2Elm();
							lv2.setShopId(map2.get("SHOPID").toString());
							lv2.setShopName(map2.get("SHOPNAME").toString());
							lv1.getShops().add(lv2);
						}
						
					}
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
	protected String getQuerySql(DCP_FlashviewQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		int startRow=(pageNumber-1) * pageSize;
		String eId = req.geteId();
		String langType = req.getLangType();
		
		String keyTxt = req.getRequest().getKeyTxt();
		String shopId = req.getRequest().getShopId();
		String status = req.getRequest().getStatus();
		
		sqlbuf.append(" select * from ( "
				+ " SELECT  count(distinct a.flashviewid ) OVER() AS NUM,  dense_rank() over (order BY  a.flashviewid ) rn, "
				+ " a.flashviewid , a.filename , a.shopType , a.linkurl , a.priority , a.status , "
				+ " b.shopID , c.org_Name AS shopName "
				+ " FROM DCP_FLASHVIEW a "
				+ " LEFT JOIN DCP_FLASHVIEW_shop b ON a.eid = b.eid AND a.flashviewid = b.flashviewid "
				+ " LEFT JOIN DCP_ORG_lang c ON b.eid = c.EID AND b.shopId = c.organizationno  AND c.lang_type = '"+langType+"' AND c.status = 'Y'   "
				+ " WHERE a.eid = '"+eId+"' " );
		
		if (!Check.Null(keyTxt))
		{
			sqlbuf.append(" and ( a.flashviewid like '%%"+keyTxt+"%%' or a.filename like '%%"+keyTxt+"%%') ");
		}
		
		//查询适用门店的广告信息，要包含全部门店
		if (!Check.Null(shopId))
		{
			sqlbuf.append(" and ( b.shopId = '"+shopId+"' or a.shopType = '1' ) ");
		}
		
		if (!Check.Null(status))
		{
			sqlbuf.append(" and  a.status = '"+status+"'  ");
		}
		
		sqlbuf.append(" order by a.priority   "
				+ " ) t where t.rn > " + startRow + " and t.rn<=" + (startRow+pageSize)
				+ " order BY priority");
		sql = sqlbuf.toString();
		return sql;
	}
	
	
}	
