package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_AttrGroupQueryReq;
import com.dsc.spos.json.cust.res.DCP_AttrGroupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

public class DCP_AttrGroupQuery extends SPosBasicService<DCP_AttrGroupQueryReq, DCP_AttrGroupQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_AttrGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_AttrGroupQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_AttrGroupQueryReq>() {
		};
	}

	@Override
	protected DCP_AttrGroupQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_AttrGroupQueryRes();
	}

	@Override
	protected DCP_AttrGroupQueryRes processJson(DCP_AttrGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;

		// 查詢資料
		DCP_AttrGroupQueryRes res = null;
		res = this.getResponse();
		// 获取当前登陆用户的语言类型
		String langType_cur = req.getLangType();
		// 单头总数
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		int totalRecords; // 总笔数
		int totalPages;
		res.setDatas(new ArrayList<DCP_AttrGroupQueryRes.level1Elm>());
		if (getQData != null && getQData.isEmpty() == false) {

			/*
			 * String num = getQData.get(0).get("NUM").toString();
			 * totalRecords=Integer.parseInt(num);
			 * 
			 * //算總頁數 totalPages = totalRecords / req.getPageSize(); totalPages
			 * = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 :
			 * totalPages;
			 * 
			 * res.setPageNumber(req.getPageNumber());
			 * res.setPageSize(req.getPageSize());
			 * res.setTotalRecords(totalRecords); res.setTotalPages(totalPages);
			 */

			Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
			condition.put("ATTRGROUPID", true);
			// 调用过滤函数
			List<Map<String, Object>> groupDatas = MapDistinct.getMap(getQData, condition);

			Map<String, Boolean> condition_lang = new HashMap<String, Boolean>(); // 查詢條件
			condition_lang.put("ATTRGROUPID", true);
			condition_lang.put("LANGTYPE", true);
			// 调用过滤函数
			List<Map<String, Object>> groupLangDatas = MapDistinct.getMap(getQData, condition_lang);

			Map<String, Boolean> condition_attr = new HashMap<String, Boolean>(); // 查詢條件
			condition_attr.put("ATTRGROUPID", true);
			condition_attr.put("ATTRID", true);

			List<Map<String, Object>> attrDatas = MapDistinct.getMap(getQData, condition_attr);

			res.setDatas(new ArrayList<DCP_AttrGroupQueryRes.level1Elm>());

			for (Map<String, Object> oneData : groupDatas) {
				DCP_AttrGroupQueryRes.level1Elm oneLv1 = res.new level1Elm();
				String attrGroupId = oneData.get("ATTRGROUPID").toString();
				String sortId = oneData.get("SORTID").toString();

				oneLv1.setAttrGroupId(attrGroupId);
				oneLv1.setSortId(sortId);
				oneLv1.setStatus(oneData.get("STATUS").toString());
				oneLv1.setAttrGroupName_lang(new ArrayList<DCP_AttrGroupQueryRes.level2Elm>());
				oneLv1.setAttrList(new ArrayList<DCP_AttrGroupQueryRes.attr>());

				for (Map<String, Object> langDatas : groupLangDatas) {
					// 过滤属于此单头的明细
					if (attrGroupId.equals(langDatas.get("ATTRGROUPID").toString()) == false)
						continue;

					DCP_AttrGroupQueryRes.level2Elm fstLang = res.new level2Elm();

					String langType = langDatas.get("LANGTYPE").toString();
					String attrGroupName = langDatas.get("ATTRGROUPNAME").toString();
					if (langType.equals(langType_cur)) {
						oneLv1.setAttrGroupName(attrGroupName);
					}

					fstLang.setLangType(langType);
					fstLang.setName(attrGroupName);

					oneLv1.getAttrGroupName_lang().add(fstLang);
					fstLang = null;
				}
				for (Map<String, Object> attr : attrDatas) {
					String attrId = attr.get("ATTRID").toString();
					String attrName = attr.get("ATTRNAME").toString();
					String productParam = attr.get("PRODUCTPARAM").toString();
					String attrGroupId_detail = attr.get("ATTRGROUPID").toString();
					if (attrId == null || attrGroupId_detail == null || attrId.isEmpty()
							|| attrGroupId_detail.isEmpty())
						continue;
					// 过滤属于此单头的明细
					if (attrGroupId_detail.equals(attrGroupId)) {
						DCP_AttrGroupQueryRes.attr item = res.new attr();
						item.setAttrId(attrId);
						item.setAttrName(attrName);
						item.setProductParam(productParam);
						item.setSortId(attr.get("SORTID_ATTR").toString());
						oneLv1.getAttrList().add(item);
					}

				}

				res.getDatas().add(oneLv1);
				oneLv1 = null;
			}
		}

		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_AttrGroupQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();

		String eId = req.geteId();
		String keyTxt = null;
		String status = null;
		if (req.getRequest() != null) {
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();

		}

		String langType = req.getLangType();
		if (langType == null || langType.isEmpty()) {
			langType = "zh_CN";
		}
		
		String productParam = req.getRequest().getProductParam();

		// 后面说了 不分页，先留着万一又说要分页呢
		/*
		 * int pageNumber = req.getPageNumber(); int pageSize =
		 * req.getPageSize(); //分页起始位置 int startRow=(pageNumber-1) * pageSize;
		 * 
		 * sqlbuf.append(" SELECT * FROM (" +
		 * " SELECT  COUNT(DISTINCT a.ATTRGROUPID ) OVER() NUM ,dense_rank() over(ORDER BY a.ATTRGROUPID) rn, a. *  FROM "
		 * +
		 * " ( SELECT a.ATTRGROUPID , b.ATTRGROUPNAME AS ATTRGROUPNAME, a.status,a.SORTID, b.lang_Type AS langType ,"
		 * + "  a.EID  FROM DCP_ATTRGROUP a   " +
		 * " LEFT JOIN DCP_ATTRGROUP_LANG b ON a.EID = b.EID AND a.ATTRGROUPID = b.ATTRGROUPID )   a "
		 * + " WHERE  EID = '"+eId+"'  " ); if (keyTxt != null &&
		 * keyTxt.length()>0) { sqlbuf.append(" AND (ATTRGROUPID like '%%"
		 * +keyTxt+"%%' or ATTRGROUPNAME LIKE '%%"+keyTxt+"%%' )  "); }
		 * if(status!=null && status.length()>0) { sqlbuf.append(" and status='"
		 * + status + "' "); } sqlbuf.append(
		 * " )   WHERE rn >  "+startRow+" and rn < "+(startRow + pageSize)+" "
		 * );
		 */

		sqlbuf.append(" SELECT * FROM ("
				+ " SELECT a.ATTRGROUPID , b.ATTRGROUPNAME AS ATTRGROUPNAME, a.status,a.SORTID, "
				+ " b.lang_Type AS langType ,a.EID,C.ATTRID,D.ATTRNAME,C.SORTID AS SORTID_ATTR"
				+ " ,e.productParam  "
				+ " FROM DCP_ATTRGROUP a"
				+ " LEFT JOIN DCP_ATTRGROUP_LANG b ON a.EID = b.EID AND a.ATTRGROUPID = b.ATTRGROUPID"
				+ " LEFT JOIN DCP_ATTRGROUP_DETAIL C ON a.eid = c.eid and a.attrgroupid = c.attrgroupid"
				+ " LEFT JOIN DCP_ATTRIBUTION E on c.eId = E.eid and c.attrId = e.attrId and e.status = '100'  " 
				+ " left JOIN DCP_ATTRIBUTION_LANG D ON a.eid = d.eid and c.attrid = d.attrid and d.lang_type='"
				+ langType + "'" + " WHERE a.EID='" + eId + "'  ");
		if (keyTxt != null && keyTxt.length() > 0) {
			sqlbuf.append(
					" AND (a.ATTRGROUPID like '%%" + keyTxt + "%%' or b.ATTRGROUPNAME LIKE '%%" + keyTxt + "%%' )  ");
		}
		if (status != null && status.length() > 0) {
			sqlbuf.append(" and a.status='" + status + "' ");

		}
		
		if (productParam != null && productParam.length() > 0) {
			sqlbuf.append(" and E.productParam='" + productParam + "' ");
		}
		
		sqlbuf.append(" ) order by  ATTRGROUPID,SORTID_ATTR");
		sql = sqlbuf.toString();
		return sql;

	}

}
