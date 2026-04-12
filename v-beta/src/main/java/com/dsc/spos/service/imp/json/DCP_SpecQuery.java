package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_SpecQueryReq;
import com.dsc.spos.json.cust.res.DCP_SpecQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.google.gson.reflect.TypeToken;

/**
 * 规格查询服务 2018-10-15
 * 
 * @author yuanyy
 *
 */
public class DCP_SpecQuery extends SPosBasicService<DCP_SpecQueryReq, DCP_SpecQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_SpecQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_SpecQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_SpecQueryReq>() {
		};
	}

	@Override
	protected DCP_SpecQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_SpecQueryRes();
	}

	@Override
	protected DCP_SpecQueryRes processJson(DCP_SpecQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_SpecQueryRes res = null;
		res = this.getResponse();
		String sql = null;
		res.setDatas(new ArrayList<DCP_SpecQueryRes.level1Elm>());
		sql = this.getQuerySql(req);
		List<Map<String, Object>> specDatas = this.doQueryData(sql, null);

		int totalRecords; // 总笔数
		int totalPages;

		if (specDatas != null && specDatas.isEmpty() == false) {

			String num = specDatas.get(0).get("NUM").toString();
			totalRecords = Integer.parseInt(num);
			// 算總頁數
			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;

			res.setPageNumber(req.getPageNumber());
			res.setPageSize(req.getPageSize());
			res.setTotalRecords(totalRecords);
			res.setTotalPages(totalPages);

			// 单头主键字段
			res.setDatas(new ArrayList<DCP_SpecQueryRes.level1Elm>());
			for (Map<String, Object> oneData : specDatas) {
				DCP_SpecQueryRes.level1Elm oneLv1 = new DCP_SpecQueryRes().new level1Elm();
				String specNO = oneData.get("SPECNO").toString();
				String specName = oneData.get("SPECNAME").toString();
				String status = oneData.get("STATUS").toString();
				oneLv1.setSpecNo(specNO);
				oneLv1.setSpecName(specName);
				oneLv1.setStatus(status);
				res.getDatas().add(oneLv1);
			}
		} else {
			res.setDatas(new ArrayList<DCP_SpecQueryRes.level1Elm>());
		}
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_SpecQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		int pageNumber = req.getPageNumber();
		int pageSize = req.getPageSize();

		// 計算起啟位置
		int startRow = (pageNumber - 1) * pageSize;

		String keyTxt = "";
		String status = "";
		if(req.getRequest()!=null)
		{
			keyTxt = req.getRequest().getKeyTxt();
			status = req.getRequest().getStatus();
		}
		String eId = req.geteId();
		sqlbuf.append("select * from (SELECT COUNT(*) OVER() NUM , row_number() OVER(ORDER BY spec_NO) rn , "
				+ " spec_NO as specNO, spec_Name as specName ,status, EID FROM DCP_SPEC  ) "
				+ " where 1 = 1 and EID = '" + eId + "' ");

		if (status != null && status.length() > 0) {
			sqlbuf.append(" and status='" + status + "' ");
		}
		if (keyTxt != null && keyTxt.length() > 0) {
			sqlbuf.append(" and  (specNO like '%%" + keyTxt + "%%' or specName like '%%%" + keyTxt + "%%' )");
		}

		sqlbuf.append(" and rn > " + startRow + " and rn <= " + (startRow + pageSize) + "");
		sql = sqlbuf.toString();
		return sql;
	}

}
