package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_POrderPropQtyQueryReq;
import com.dsc.spos.json.cust.res.DCP_POrderPropQtyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 查询要货单商品对应的预估要货量 （最近一次盘点的 盘点盈亏数）
 * 
 * @author yuanyy
 *
 */
public class DCP_POrderPropQtyQuery extends SPosBasicService<DCP_POrderPropQtyQueryReq, DCP_POrderPropQtyQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_POrderPropQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_POrderPropQtyQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_POrderPropQtyQueryReq>() {
		};
	}

	@Override
	protected DCP_POrderPropQtyQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_POrderPropQtyQueryRes();
	}

	@Override
	protected DCP_POrderPropQtyQueryRes processJson(DCP_POrderPropQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_POrderPropQtyQueryRes res = this.getResponse();
		String sql = null;
		sql = this.getQuerySql(req);
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		res.setDatas(new ArrayList<DCP_POrderPropQtyQueryRes.level1Elm>());
		for (DCP_POrderPropQtyQueryReq.level1Elm item : req.getDatas()) {
			Map<String, Object> getitem = getProplist(getQData, item, req);
			if (getitem != null) {
				DCP_POrderPropQtyQueryRes.level1Elm lev1 = res.new level1Elm();
				lev1.setPluNO(item.getPluNO());
				// lev1.setFeatureNO(item.getFeatureNO());
				// 这里还要转换一次单位换算率，因为计算的是库存单位，需要换算成要货单位应该要多少
				BigDecimal unitRatio;
				double unitRatio1 = 0;
				List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
						item.getPluNO(), item.getPunit());
				if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + item.getPluNO() + " 找不到对应的换算关系");
				}

				unitRatio = (BigDecimal) getQData_Ratio.get(0).get("UNIT_RATIO");
				if (unitRatio.compareTo(BigDecimal.valueOf(0)) == 0) {
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + item.getPluNO() + " 找不到对应的换算关系");
				}

				unitRatio1 = unitRatio.doubleValue();
				double sqty = Double.parseDouble(getitem.get("PROPPQTY").toString());

				String propPqty = PosPub.GetdoubleScale(sqty / unitRatio1, 2);
				lev1.setPropPqty(propPqty);
				res.getDatas().add(lev1);
				lev1=null;

			} 
			else 
			{
				DCP_POrderPropQtyQueryRes.level1Elm lev1 = res.new level1Elm();
				lev1.setPluNO(item.getPluNO());
				lev1.setPropPqty("0");

				res.getDatas().add(lev1);
				lev1=null;
			}
		}

		return res;

	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_POrderPropQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub

		String eId = req.geteId();
		String shopId = req.getShopId();

		String sql = null;
		StringBuffer sqlbuf = new StringBuffer("");

		// 增加以下逻辑： 获取最近一次盘点单 中各个商品的差异量
		sqlbuf.append(" select * from ( "
				+ " select a.EID , a.SHOPID , b.pluNO , ( b.ref_wqty - b.pqty ) as propPqty from ( "
				+ " select * from (select * from DCP_stocktake where EID = '" + eId + "' and SHOPID = '" + shopId + "'" 
				+ " and status = '2' order by bdate desc ) where rownum=1 " + " ) a "
				+ " left join DCP_stocktake_detail b on a.EID = b.EID and a.stocktakeNO = b.stocktakeNO and a.SHOPID = b.SHOPID "
				+ " order by b.pluNO  ) ");

		sql = sqlbuf.toString();
		return sql;
	}

	// 获取要货单上各商品的最近一次的盘点盈亏数 （预估报货量）
	protected Map<String, Object> getProplist(List<Map<String, Object>> getQData1,
			DCP_POrderPropQtyQueryReq.level1Elm item, DCP_POrderPropQtyQueryReq req) {
		// 循环需要查找的料号和 门店最近一次盘点单 的匹配
		if (getQData1 != null && getQData1.isEmpty() == false) { // 有資料，取得詳細內容
			for (Map<String, Object> oneData : getQData1) {
				if (oneData.get("PLUNO").toString().equals(item.getPluNO())) {
					return oneData;
				}
			}
		}
		return null;
	}

}
