package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_POrderStatisticQueryReq;
import com.dsc.spos.json.cust.res.DCP_POrderStatisticQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;

/**
 * 要货品类统计查询  2019-01-10
 * @author yuanyy
 *
 */
public class DCP_POrderStatisticQuery extends SPosBasicService<DCP_POrderStatisticQueryReq, DCP_POrderStatisticQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_POrderStatisticQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		String POrderNO = req.getRequest().getPorderNo();
		if (Check.Null(POrderNO)) {
			isFail = true;
			errMsg.append("单据编号不可为空值, ");
		}
		
		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_POrderStatisticQueryReq> getRequestType() {
		return new TypeToken<DCP_POrderStatisticQueryReq>(){};
	}
	
	@Override
	protected DCP_POrderStatisticQueryRes getResponseType() {
		return new DCP_POrderStatisticQueryRes();
	}
	
	@Override
	protected DCP_POrderStatisticQueryRes processJson(DCP_POrderStatisticQueryReq req) throws Exception {
		DCP_POrderStatisticQueryRes res = this.getResponse();
		String sql = this.getQuerySql(req);
		List<Map<String, Object>> allDatas = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<>());
		if(allDatas != null && !allDatas.isEmpty()){
			for (Map<String, Object> oneData : allDatas) {
				DCP_POrderStatisticQueryRes.level1Elm oneLv1 = res.new level1Elm();
				oneLv1.setCategoryNo(oneData.get("TOP_CATEGORY").toString());
				oneLv1.setCategoryName(oneData.get("CATEGORY_NAME").toString());
				oneLv1.setQty(oneData.get("PQTY").toString());
				oneLv1.setAmt(oneData.get("AMT").toString());
				//【ID1026341】【鑫铭鲜-2.0】要货申请中的品类统计里，需要显示要货单中品类的合计进货金额。
				// 2.0和3.0 都要改 by jinzma 20220606
				oneLv1.setDistriAmt(oneData.get("DISTRIAMT").toString());
				
				res.getDatas().add(oneLv1);
			}
		}
		
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_POrderStatisticQueryReq req) throws Exception {
		String eId = req.geteId();
		String shopId = req.getShopId();
		String porderNo = req.getRequest().getPorderNo();
		String langType = req.getLangType();
		String sql = ""
				+ " select a.*,b.category_name from ("
				+ " select c.top_category,sum(pqty) as pqty,sum(amt) as amt,sum(a.distriamt) as distriamt"
				+ " from dcp_porder_detail a"
				+ " inner join dcp_goods b on a.eid=b.eid and a.pluno=b.pluno"
				+ " inner join dcp_category c on a.eid=c.eid and b.category=c.category"
				+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.porderno='"+porderNo+"'"
				+ " group by top_category"
				+ " ) a"
				+ " left join dcp_category_lang b on b.eid='"+eId+"' and a.top_category=b.category and b.lang_type='"+langType+"'"
				+ " order by a.top_category";
		return sql;
	}
	
}
