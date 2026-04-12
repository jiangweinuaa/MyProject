package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dsc.spos.json.cust.req.DCP_GoodsSaleQueryReq;
import com.dsc.spos.json.cust.res.DCP_GoodsSaleQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

/**
 * 服务函数：DCP_GoodsSaleQuery 
 * 服务说明：商品销售数据查询
 * @author wangzyc
 * @since 2020-11-17
 */
public class DCP_GoodsSaleQuery extends SPosBasicService<DCP_GoodsSaleQueryReq, DCP_GoodsSaleQueryRes> {
	
	@Override
	protected boolean isVerifyFail(DCP_GoodsSaleQueryReq req) throws Exception {
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer();
		
		if (Check.Null(req.getRequest().getShopId())) {
			errMsg.append("门店编码不能为空, 请重新输入 ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getBeginDate())) {
			errMsg.append("开始日期不能为空，请重新输入, ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getEndDate())) {
			errMsg.append("截止日期不能为空, 请重新输入 ");
			isFail = true;
		}
		if (Check.Null(req.getRequest().getSaleType())) {
			errMsg.append("单据类型不能为空, 请重新输入 ");
			isFail = true;
		}
		
		if (isFail) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		return false;
	}
	
	@Override
	protected TypeToken<DCP_GoodsSaleQueryReq> getRequestType() {
		return new TypeToken<DCP_GoodsSaleQueryReq>() {};
	}
	
	@Override
	protected DCP_GoodsSaleQueryRes getResponseType() {
		return new DCP_GoodsSaleQueryRes();
	}
	
	@Override
	protected DCP_GoodsSaleQueryRes processJson(DCP_GoodsSaleQueryReq req) throws Exception {
		// 查詢資料
		DCP_GoodsSaleQueryRes res = this.getResponse();
		String eId = req.geteId();
		String shopId = req.getRequest().getShopId();
		String saleType = req.getRequest().getSaleType();
		String[] pluNos = req.getRequest().getPluNo();
		
		try {
			String sql=this.getQuerySql(req);
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			Map<String, Boolean> condition = new HashMap<>(); //查詢條件
			condition.put("PLUNO", true);
			//调用过滤函数
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
			res.setDatas(new ArrayList<>());
			if (getQHeader != null && !getQHeader.isEmpty()) {
				for (Map<String, Object> oneData : getQHeader) {
					DCP_GoodsSaleQueryRes.Level1Elm oneLv1 = res.new Level1Elm();
					String pluNo = oneData.get("PLUNO").toString();
					
					Map<String, Object> condiV= new HashMap<>();
					condiV.put("PLUNO",pluNo);
					List<Map<String, Object>> orderList= MapDistinct.getWhereMap(getQData, condiV, true);
					condiV.clear();
					if (orderList != null && !orderList.isEmpty()){
						BigDecimal saleQtyTot = new BigDecimal("0");
						oneLv1.setFeature(new ArrayList<>());
						for (Map<String, Object> feature:orderList){
							DCP_GoodsSaleQueryRes.Level2Elm oneLv2 = res.new Level2Elm();
							String featureNo = feature.get("FEATURENO").toString();
							String saleQty = feature.get("SALE_QTY").toString();
							saleQtyTot=saleQtyTot.add(new BigDecimal(saleQty));
							
							if (!Check.Null(featureNo) && !featureNo.equals(" ")){
								oneLv2.setFeatureNo(featureNo);
								oneLv2.setSaleQty(saleQty);
								oneLv1.getFeature().add(oneLv2);
							}
						}
						oneLv1.setRefUAQty(orderList.get(0).get("REFUAQTY").toString());
						oneLv1.setLastSaleQty(orderList.get(0).get("PQTY").toString());
						oneLv1.setSaleDate(orderList.get(0).get("BDATE").toString());
						oneLv1.setSaleQty(saleQtyTot.toPlainString());
					}
					
					oneLv1.setPluNo(pluNo);
					res.getDatas().add(oneLv1);
					
				}
			}
			
		} catch (Exception e) {
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, e.getMessage());
		}
		return res;
	}
	
	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	}
	
	@Override
	protected String getQuerySql(DCP_GoodsSaleQueryReq req) throws Exception {
		String eId = req.geteId();
		String shopId = req.getRequest().getShopId();
		String saleType = req.getRequest().getSaleType(); // 单据类型，0:要货单
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		StringBuffer sqlbuf = new StringBuffer();
		
		String[] plu = req.getRequest().getPluNo();
		String withPlu = "";
		if (plu !=null && plu.length>0 ) {
			MyCommon mc = new MyCommon();
			Map<String,String> map = new HashMap<>();
			String sJoinPlu = "";
			for(String s :plu) {
				///要货单商品导入 商品编号要小写处理，有客户商品编号带字母
				sJoinPlu += s.toLowerCase()+",";
			}
			map.put("PLUNO", sJoinPlu);
			withPlu = mc.getFormatSourceMultiColWith(map);
		}
		
		if (!Check.Null(withPlu)) {
			sqlbuf.append(" with plu as (" + withPlu + ")");
		} else {
			sqlbuf.append(" with plu as ( select pluno from dcp_goods where eid='"+eId+"' and status='100' ) ");
		}
		
		sqlbuf.append(""
				+ " ,porder as ("
				+ " select c.bdate,c.pluno,sum(d.pqty) as pqty from ("
				+ " select row_number() over (partition by b.pluno order by a.porderno desc) as rn,"
				+ " a.eid,a.shopid,a.porderno,a.bdate,b.pluno"
				+ " from dcp_porder a"
				+ " inner join dcp_porder_detail b on a.eid=b.eid and a.shopid=b.shopid and a.porderno=b.porderno"
				+ " inner join plu on b.pluno=plu.pluno"
				+ " where a.eid='"+eId+"' and a.shopid ='"+shopId+"' and a.status='2'"
				+ " ) c"
				+ " inner join dcp_porder_detail d on c.eid=d.eid and c.shopid=d.shopid and c.porderno=d.porderno and c.pluno=d.pluno"
				+ " where rn=1"
				+ " group by c.bdate,c.pluno"
				+ " ) ");
		
		sqlbuf.append(" "
				+ " ,ref as ("
				+ " select b.pluno,sum((b.pqty-nvl(b.stockin_qty,0))) as refuaqty from dcp_receiving a"
				+ " inner join dcp_receiving_detail b on a.eid=b.eid and a.shopid=b.shopid and a.receivingno=b.receivingno"
				+ " inner join plu on b.pluno=plu.pluno"
				+ " where a.eid='"+eId+"' and a.shopid='"+shopId+"' and a.doc_type='0' and a.status='6'"
				+ " and (b.status='0' or b.status is null)"
				+ " group by b.pluno"
				+ " ) ");
		
		sqlbuf.append(" "
				+ " ,stock as ("
				+ " select /*+ use_hash(a)*/  a.pluno,a.featureno,sum(a.sale_qty) as sale_qty"
				+ " from dcp_stock_day_static a"
				+ " inner join plu on a.pluno=plu.pluno"
				+ " where a.eid='"+eId+"' and a.organizationno='"+shopId+"'"
				+ " and a.edate>='"+beginDate+"' and a.edate<='"+endDate+"'"
				+ " group by a.pluno,a.featureno"
				+ " ) ");
		
		sqlbuf.append(" "
				+ " select plu.pluno,porder.bdate,nvl(porder.pqty,0) as pqty,"
				+ " nvl(ref.refuaqty,0) as refuaqty,stock.featureno,"
				+ " nvl(stock.sale_qty,0) as sale_qty"
				+ " from plu"
				+ " left join porder on plu.pluno=porder.pluno"
				+ " left join ref on plu.pluno=ref.pluno"
				+ " left join stock on plu.pluno=stock.pluno"
				+ " order by plu.pluno"
				+ " ");
		
		return sqlbuf.toString();
		
	}
	
}
