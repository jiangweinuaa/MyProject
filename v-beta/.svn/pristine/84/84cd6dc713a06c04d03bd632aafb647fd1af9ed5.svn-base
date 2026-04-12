package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_HorseLampDetailReq;
import com.dsc.spos.json.cust.res.DCP_HorseLampDetailRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_HorseLampDetail extends SPosBasicService<DCP_HorseLampDetailReq, DCP_HorseLampDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_HorseLampDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_HorseLampDetailReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_HorseLampDetailReq>(){};
	}

	@Override
	protected DCP_HorseLampDetailRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_HorseLampDetailRes();
	}

	@Override
	protected DCP_HorseLampDetailRes processJson(DCP_HorseLampDetailReq req) throws Exception {
		// TODO Auto-generated method stub

		DCP_HorseLampDetailRes res = null;
		res = this.getResponse();

		try {
			String sql = "";
			sql = this.getQuerySql(req);
			List<Map<String, Object>> queryDatas = this.doQueryData(sql, null);

			//			int totalRecords = 0;								//总笔数
			//			int totalPages = 0;			

			res.setDatas(new ArrayList<DCP_HorseLampDetailRes.level1Elm>());
			if(queryDatas != null && queryDatas.size() > 0){
				//				String num = queryDatas.get(0).get("NUM").toString();
				//				totalRecords=Integer.parseInt(num);
				//				totalPages = totalRecords / req.getPageSize();
				//				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("BILLNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(queryDatas, condition);

				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); //查询条件
				condition.put("BILLNO", true);
				condition2.put("SYSHOPID", true);
				condition2.put("SYCOMPANYID", true);
				//调用过滤函数
				List<Map<String, Object>> syDatas=MapDistinct.getMap(queryDatas, condition2);

				for (Map<String, Object> map : getQHeader) {

					DCP_HorseLampDetailRes.level1Elm lv1 = res.new level1Elm();
					String billDate = map.getOrDefault("BILLDATE", "").toString();
					String billNo = map.getOrDefault("BILLNO", "").toString();
					String billName = map.getOrDefault("BILLNAME", "").toString();
					String billType = map.getOrDefault("BILLTYPE", "").toString();

					String companyId = map.getOrDefault("COMPANYID", "").toString();
					String companyName = map.getOrDefault("COMPANYNAME", "").toString();

					String shopId = map.getOrDefault("SHOPID", "").toString();
					String shopName = map.getOrDefault("SHOPNAME", "").toString();

					String channelId = map.getOrDefault("CHANNELID", "").toString();
					String channelName = map.getOrDefault("CHANNELNAME", "").toString();

					String employeeId = map.getOrDefault("EMPLOYEEID", "").toString();
					String employeeName = map.getOrDefault("EMPLOYEENAME", "").toString();

					String departId = map.getOrDefault("EMPLOYEEID", "").toString();
					String departName = map.getOrDefault("EMPLOYEENAME", "").toString();

					String beginDate = map.getOrDefault("BEGINDATE", "").toString();
					String endDate = map.getOrDefault("ENDDATE", "").toString();

					String noticeContent = map.getOrDefault("NOTICECONTENT", "").toString();
					String restrictShop = map.getOrDefault("RESTRICTSHOP", "noLimit").toString();
					String status = map.getOrDefault("STATUS", "-1").toString();

					String createopid = map.getOrDefault("CREATEOPID", "").toString();
					String createTime = map.getOrDefault("CREATETIME", "").toString();
					String lastmodiopid = map.getOrDefault("LASTMODIOPID", "").toString();
					String lastmodiTime = map.getOrDefault("LASTMODITIME", "").toString();

					lv1.setBillDate(billDate);
					lv1.setBillNo(billNo);
					lv1.setBillName(billName);
					lv1.setBillType(billType);
					lv1.setCompanyId(companyId);
					lv1.setCompanyName(companyName);
					lv1.setShopId(shopId);
					lv1.setShopName(shopName);
					lv1.setCompanyId(companyId);
					lv1.setCompanyName(companyName);
					lv1.setChannelId(channelId);
					lv1.setChannelName(channelName);
					lv1.setEmployeeId(employeeId);
					lv1.setEmployeeName(employeeName);
					lv1.setDepartId(departId);
					lv1.setDepartName(departName);
					lv1.setNoticeContent(noticeContent);
					lv1.setRestrictShop(restrictShop);
					lv1.setBeginDate(beginDate);
					lv1.setEndDate(endDate);
					lv1.setStatus(status);

					lv1.setCreateopid(createopid);
					lv1.setCreateTime(createTime);
					lv1.setLastmodiopid(lastmodiopid);
					lv1.setLastmoditime(lastmodiTime);

					lv1.setShopList(new ArrayList<DCP_HorseLampDetailRes.level2Elm>());

					for (Map<String, Object> map2 : syDatas) {
						DCP_HorseLampDetailRes.level2Elm lv2 = res.new level2Elm();

						String syShopId = map2.get("SYSHOPID").toString();
						if(Check.Null(syShopId)){
							continue;
						}

						String syShopName = map2.get("SYSHOPNAME").toString();
						String syCompanyId = map2.get("SYCOMPANYID").toString();
						String syCompanyName = map2.get("SYCOMPANYNAME").toString();
						lv2.setShopId(syShopId);
						lv2.setShopName(syShopName);
						lv2.setCompanyId(syCompanyId);
						lv2.setCompanyName(syCompanyName);
						lv1.getShopList().add(lv2);
					}

					res.getDatas().add(lv1);

				}
			}

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());


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
	protected String getQuerySql(DCP_HorseLampDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sqlbuf = new StringBuffer();
		String sql = "";
		String langType = req.getLangType();
		sqlbuf.append("  SELECT  "
				+ " to_char(a.billDate,'yyyy-MM-dd') AS billDate ,"
				+ "a.billNO, a.billName , a.billType ,  "
				+ " a.companyId, b.org_name AS companyName , a.shopid , c.org_name AS shopName , a.departid , d.departname , a.channelid , '' AS channelName , "
				+ " a.employeeid , e.op_name AS employeeName ,"
				+ " to_char(a.beginDate,'yyyy-MM-dd') AS beginDate , to_char(a.endDate , 'yyyy-MM-dd') AS endDate  ,  "
				+ " a.noticecontent , a.restrictshop , a.status ,"
				+ " g.shopid AS syShopId , g.shopName AS syShopName , g.companyId AS syCompanyId , g.companyName  AS syCompanyName , "
				+ " a.createopid, to_char( a.createTime , 'yyyy-MM-dd hh24:mi:ss' ) AS createTime , a.lastmodiopid ,   "
				+ " to_char( a.lastmodiTime , 'yyyy-MM-dd hh24:mi:ss' ) AS lastmodiTime  "
				+ " FROM dcp_horselamp a "
				+ " LEFT JOIN DCP_ORG_lang b ON a.eID = b.EID AND a.companyId = b.organizationno  AND b.status='100' AND b.lang_type = '"+langType+"' "
				+ " LEFT JOIN DCP_ORG_lang c ON a.eID = c.EID AND a.shopID = c.organizationno  AND c.status='100' AND c.lang_type =  '"+langType+"' "
				+ " LEFT JOIN DCP_DEPARTMENT_LANG d ON a.eid = d.EID AND a.departid = d.departno AND d.status='100' AND d.lang_type =  '"+langType+"' "
				+ " LEFT JOIN platform_staffs_lang e ON a.eid = e.EID AND a.employeeid = e.opno AND e.status='100' AND e.lang_type =  '"+langType+"' "
				+ " LEFT JOIN ( "
				+ " SELECT a.eid,  a.billno , a.serialNo , a.shopId , a.companyId , b.org_name AS companyName  , c.org_name AS shopName"
				+ " FROM Dcp_Horselamp_Pickshop a "
				+ " LEFT JOIN DCP_ORG_lang b ON a.eID = b.EID AND a.companyId = b.organizationno  AND b.status='100' AND b.lang_type =  '"+langType+"' "
				+ " LEFT JOIN DCP_ORG_lang c ON a.eID = c.EID AND a.shopID = c.organizationno  AND c.status='100' AND c.lang_type =  '"+langType+"' "
				+ " WHERE a.eid = '"+req.geteId()+"' AND a.billNo = '"+req.getRequest().getBillNo()+"' "
				+ " ) g ON   a.eid = g.eid AND a.billNo = g.billno "
				+ " WHERE a.eid = '"+req.geteId()+"' AND a.billno = '"+req.getRequest().getBillNo()+"'  ");

		sqlbuf.append(" order by a.status , a.billNO   " );
		sql = sqlbuf.toString();
		return sql;
	}

}
