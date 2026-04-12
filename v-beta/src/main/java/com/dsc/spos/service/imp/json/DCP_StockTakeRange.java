package com.dsc.spos.service.imp.json;

import com.dsc.spos.json.cust.req.DCP_StockTakeRangeReq;
import com.dsc.spos.json.cust.res.DCP_StockTakeRangeRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 服务函数：DCP_StockTakeRange
 * 服务說明：门店盘点范围查询
 * @author JZMA 
 * @since  2018-11-21
 */
public class DCP_StockTakeRange extends SPosBasicService<DCP_StockTakeRangeReq, DCP_StockTakeRangeRes> 
{
	@Override
	protected boolean isVerifyFail(DCP_StockTakeRangeReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String ofNO = req.getRequest().getOfNo();

		if (Check.Null(ofNO)) {
			errMsg.append("盘点任务单号不可为空值, ");
			isFail = true;
		} 	    
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockTakeRangeReq> getRequestType() {
		return new TypeToken<DCP_StockTakeRangeReq>(){};
	}

	@Override
	protected DCP_StockTakeRangeRes getResponseType() {
		return new DCP_StockTakeRangeRes();
	}

	@Override
	protected DCP_StockTakeRangeRes processJson(DCP_StockTakeRangeReq req) throws Exception {
		//查詢資料
		DCP_StockTakeRangeRes res = this.getResponse();
		try 
		{
			//查詢資料
			String sql = this.getQuerySql(req);
			List<Map<String, Object>> getQData1 = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_StockTakeRangeRes.level1Elm>());
			if (getQData1 != null && getQData1.isEmpty() == false) 
			{ // 有資料，取得詳細內容
				for (Map<String, Object> oneData1 : getQData1) 
				{
					DCP_StockTakeRangeRes.level1Elm oneLv1 = res.new level1Elm();

					// 取得第一層資料庫搜尋結果
					String groupNO = oneData1.get("GROUPNO").toString();
					String attrNO = oneData1.get("ATTRNO").toString();
					String valueNO = oneData1.get("VALUENO").toString();
					String attrName = oneData1.get("ATTRNAME").toString();

					// 處理調整回傳值；
					oneLv1.setGroupNo(groupNO);
					oneLv1.setAttrNo(attrNO);
					oneLv1.setValueNo(valueNO);
					oneLv1.setValueName(attrName);

					res.getDatas().add(oneLv1);
				}
			}

			return res;
		} 
		catch (Exception e) 
		{
			// TODO: handle exception
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, e.getMessage());		
		}

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		//調整查出來的資料
	}

	@Override
	protected String getQuerySql(DCP_StockTakeRangeReq req) throws Exception {
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer("");
		String eId=req.geteId();
		String shopId=req.getShopId();
		String stocktaskNo=req.getRequest().getOfNo();
		String langType=req.getLangType();

		//計算起啟位置

		sqlbuf.append(""
				+ "select groupNO,attrNO,valueNO,attrName "
				+ " from ( "
				+ " select groupNO,attrNO,valueNO,b.brand_name as attrName from DCP_stocktask_range a "
				+ " left join DCP_BRAND_lang b on a.valueno = b.brandno and a.EID = b.EID AND b.lang_type='"+langType+"' "
				+ " where a.attrno='8' and a.EID='"+eId+"' and a.organizationNO='"+shopId+"' and a.stocktaskno='"+stocktaskNo+"' "
				+ " union  all  "
				+ " select groupNO,attrNO,valueNO,b.category_name as attrName from DCP_stocktask_range a  "
				+ " left join DCP_CATEGORY_lang b on a.valueno=b.category and a.EID=b.EID AND b.lang_type='"+langType+"' "
				+ " where a.attrno='5'  and a.EID='"+eId+"' and a.organizationNO='"+shopId+"' and a.stocktaskno='"+stocktaskNo+"' "
				+ " union  all "
				+ " select groupNO,attrNO,valueNO,b.series_name as attrName from DCP_stocktask_range a  "
				+ " left join DCP_SERIES_lang b on a.valueno=b.seriesno and a.EID=b.EID AND b.lang_type='"+langType+"' "
				+ " where a.attrno='9'  and a.EID='"+eId+"' and a.organizationNO='"+shopId+"' and a.stocktaskno='"+stocktaskNo+"' "
				+ " union  all "
				+ " select groupNO,attrNO,valueNO,b.plu_name as attrName from DCP_stocktask_range a  "
				+ " left join DCP_GOODS_lang b on a.valueno=b.pluno and a.EID=b.EID AND b.lang_type='"+langType+"' "
				+ " where a.attrno='4' and a.EID='"+eId+"' and a.organizationNO='"+shopId+"' and a.stocktaskno='"+stocktaskNo+"' "
				);
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		return sql;
		
	}



}
