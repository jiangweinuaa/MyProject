package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_GoodsShelfChannelShopQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsShelfChannelShopQueryRes;
import com.dsc.spos.json.cust.res.DCP_GoodsShelfChannelShopQueryRes.shopDatas;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * V3-商品上下架渠道下属门店查询 http://183.233.190.204:10004/project/144/interface/api/3230
 */
public class DCP_GoodsShelfChannelShopQuery
		extends SPosBasicService<DCP_GoodsShelfChannelShopQueryReq, DCP_GoodsShelfChannelShopQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_GoodsShelfChannelShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_GoodsShelfChannelShopQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_GoodsShelfChannelShopQueryReq>() {
		};
	}

	@Override
	protected DCP_GoodsShelfChannelShopQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_GoodsShelfChannelShopQueryRes();
	}

	@Override
	protected DCP_GoodsShelfChannelShopQueryRes processJson(DCP_GoodsShelfChannelShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		// 查詢資料
		DCP_GoodsShelfChannelShopQueryRes res = null;
		res = this.getResponse();
		// 单头总数
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		int totalRecords = 0 ; //总笔数
		int totalPages = 0 ;				
		
		res.setDatas(new ArrayList<shopDatas>());
		if (getQData != null && getQData.isEmpty() == false) {
			
			String num = getQData.get(0).get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			//算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;			

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);
			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
			condition.put("SHOPID", true);
			// 调用过滤函数
			List<Map<String, Object>> getHead = MapDistinct.getMap(getQData, condition);
			condition.clear();

			for (Map<String, Object> oneData : getHead) {
				DCP_GoodsShelfChannelShopQueryRes.shopDatas oneLv1 = res.new shopDatas();
				String channelId = req.getRequest().getChannelId();

				String shopId = oneData.get("SHOPID").toString();
				String shopName = oneData.get("SHOPNAME").toString();
				String status = oneData.get("STATUS_SHOP").toString(); // 0下架
																		// 100上架
				if (shopId == null || shopId.isEmpty()) {
					continue;
				}

				oneLv1.setShopId(shopId);
				oneLv1.setShopName(shopName);
				oneLv1.setStatus(status);

				res.getDatas().add(oneLv1);
			}

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
	protected String getQuerySql(DCP_GoodsShelfChannelShopQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		if (langType == null || langType.isEmpty()) {
			langType = "zh_CN";
		}

		String pluNo = req.getRequest().getPluNo();
		String keyTxt = req.getRequest().getKeyTxt();
		String channelId = req.getRequest().getChannelId();

		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
		//分页起始位置
		int startRow=(pageNumber-1) * pageSize;
		
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		if (pluNo != null && pluNo.isEmpty() == false) {
			
			String restrictShop = req.getRequest().getRestrictShop() == null ? "0" : req.getRequest().getRestrictShop() ; //适用门店：0-全部门店1-指定门店

			sqlbuf.append(" select * from ("
					+ " SELECT  COUNT( DISTINCT temp.shopId ) OVER() NUM , dense_Rank() OVER(ORDER BY shopId ) rn , temp.* "
					+ " from (  ");
			
			if (!Check.Null(restrictShop) && restrictShop.equals("0")) {
				sqlbuf.append(
						" select A.CHANNELID,A.CHANNELNAME,NVL(R1.STATUS,'0') AS STATUS_CHANNEL , B.organizationNO as shopId,  C.org_name as shopName , case NVL(R1.STATUS, '0') when 100 then NVL(R2.STATUS, NVL(R1.STATUS, '0'))else 0 end AS STATUS_SHOP  ");
				sqlbuf.append(" from CRM_CHANNEL A ");
				sqlbuf.append(
						" INNER join PLATFORM_APP P on a.APPNO=P.APPNO and P.ISONLINE='Y' AND P.ISTHIRD='N' and P.status = '100' ");
				sqlbuf.append(" left join DCP_ORG B on A.EID=B.EID   ");
				
				sqlbuf.append(" left join DCP_ORG_LANG C on A.EID=C.EID AND B.ORGANIZATIONNO=C.ORGANIZATIONNO AND C.LANG_TYPE='"
						+ langType + "' ");
				sqlbuf.append(
						" left join DCP_GOODS_SHELF_RANGE R1 on A.EID=R1.EID AND A.CHANNELID=R1.CHANNELID AND R1.SHOPID='ALL' AND R1.PLUNO='"
								+ pluNo + "' ");
				sqlbuf.append(
						" left join DCP_GOODS_SHELF_RANGE R2 on B.EID=R2.EID  AND B.ORGANIZATIONNO = R2.SHOPID AND R2.SHOPID != 'ALL' AND R2.CHANNELID = '"+channelId+"'  AND R2.PLUNO='"
								+ pluNo + "' ");
				

				sqlbuf.append(" where A.EID='" + eId + "' and A.channelId = '" + channelId + "' ");
				if (keyTxt != null && keyTxt.isEmpty() == false) {
					sqlbuf.append(" AND (  A.CHANNELNAME like '%%" + keyTxt + "%%'   or B.organizationNO = '"+keyTxt+"'  or C.org_name like '%%"+keyTxt+"%%'   ) ");
				}
				
				
			}
			
			if (!Check.Null(restrictShop) && restrictShop.equals("1")) {
				sqlbuf.append(
						" select A.CHANNELID,A.CHANNELNAME,NVL(R1.STATUS,'0') AS STATUS_CHANNEL , B.shopId,  C.org_name as shopName , NVL(R1.status , '0') as status_shop,r1.SHOPID rshop  ");
				sqlbuf.append(" from CRM_CHANNEL A ");
				sqlbuf.append(
						" INNER join PLATFORM_APP P on a.APPNO=P.APPNO and P.ISONLINE='Y' AND P.ISTHIRD='N' and P.status = '100' ");
				sqlbuf.append(" left join CRM_CHANNELSHOP B on A.EID=B.EID AND A.CHANNELID=B.CHANNELID  ");
				
				sqlbuf.append(" left join DCP_ORG_LANG C on A.EID=C.EID AND B.SHOPID=C.ORGANIZATIONNO AND C.LANG_TYPE='"
						+ langType + "' ");
				sqlbuf.append(
						" left join DCP_GOODS_SHELF_RANGE R1 on A.EID=R1.EID AND A.CHANNELID=R1.CHANNELID AND (R1.SHOPID='ALL' OR B.SHOPID = R1.SHOPID) AND R1.PLUNO='"
								+ pluNo + "' ");
				

				sqlbuf.append(" where A.EID='" + eId + "' and A.channelId = '" + channelId + "' ");
				if (keyTxt != null && keyTxt.isEmpty() == false) {
					sqlbuf.append(" AND (  A.CHANNELNAME like '%%" + keyTxt + "%%' or B.shopId = '"+keyTxt+"'  or C.org_name like '%%"+keyTxt+"%%' ) ");
				}

			}

			sqlbuf.append(" ORDER BY shopId ");

			if(!Check.Null(restrictShop) && restrictShop.equals("1")){
				sqlbuf.append(" , decode(rshop,'ALL',2,1) ");
			}
			sqlbuf.append(" ) temp ) WHERE  rn>"+startRow+" and rn<="+(startRow+pageSize)+"");

		} else {
			String restrictShop = req.getRequest().getRestrictShop();

			if (!Check.Null(restrictShop) && restrictShop.equals("0")) {
				
				sqlbuf.append(""
						+ " select * from ( "
						+ " SELECT  COUNT( DISTINCT temp.shopId ) OVER() NUM , dense_Rank() OVER(ORDER BY shopId ) rn , temp.*"
						+ "  from ( ");
				
				sqlbuf.append(
						" select A.organizationNo as shopId , B.org_name As shopName , '0' as status_shop from DCP_ORG A "
								+ " LEFT JOIN DCP_ORG_LANG B ON A.eID = B.eId and A.organizationNO = B.organizationNO "
								+ "  and B.lang_type = '" + langType + "' " + " where A.eId = '" + eId
								+ "' and A.status = '100' ");
				
				if (keyTxt != null && keyTxt.isEmpty() == false) {
					sqlbuf.append(" AND (  A.organizationNo = '"+keyTxt+"'  or B.org_name like '%%"+keyTxt+"%%') ");
				}
				
				sqlbuf.append(" ORDER BY A.organizationNo  ) temp ) "
						+ " WHERE  rn>"+startRow+" and rn<="+(startRow+pageSize) );
			}

			if (!Check.Null(restrictShop) && restrictShop.equals("1")) {

				sqlbuf.append(""
						+ " select * from ( "
						+ " SELECT  COUNT( DISTINCT temp.shopId ) OVER() NUM , dense_Rank() OVER(ORDER BY shopId ) rn , temp.*"
						+ "  from ( ");
				sqlbuf.append(" select  A.CHANNELID,A.CHANNELNAME,N'0'as STATUS_CHANNEL , '0' as  status_shop , B.shopId , C.Org_Name AS shopName  ");
				sqlbuf.append(" from CRM_CHANNEL A ");
				sqlbuf.append(
						" INNER join PLATFORM_APP P on a.APPNO=P.APPNO and P.ISONLINE='Y' AND P.ISTHIRD='N' and P.status = '100' ");

				sqlbuf.append(" left join CRM_CHANNELSHOP B on A.EID=B.EID AND A.CHANNELID=B.CHANNELID ");
				sqlbuf.append(" left join DCP_ORG_LANG C on A.EID=C.EID AND B.SHOPID=C.ORGANIZATIONNO AND C.LANG_TYPE='"
						+ langType + "' ");
				sqlbuf.append(" where A.EID='" + eId + "' and A.channelId = '" + channelId + "' ");
				if (keyTxt != null && keyTxt.isEmpty() == false) {
					sqlbuf.append(" AND (  A.CHANNELNAME like '%%" + keyTxt + "%%' or B.shopId = '"+keyTxt+"'  or C.org_name like '%%"+keyTxt+"%%') ");
				}
				sqlbuf.append(" ORDER BY shopId  ) temp ) "
						+ " WHERE  rn>"+startRow+" and rn<="+(startRow+pageSize) );
			}

		}

		sql = sqlbuf.toString();
		return sql;

	}

}
