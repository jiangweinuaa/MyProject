package com.dsc.spos.service.imp.json;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_PFOrderDetailQtyQueryReq;
import com.dsc.spos.json.cust.res.DCP_PFOrderDetailQtyQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 商品昨日销量查询
 * @author yuanyy
 *
 */
public class DCP_PFOrderDetailQtyQuery extends SPosBasicService<DCP_PFOrderDetailQtyQueryReq, DCP_PFOrderDetailQtyQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_PFOrderDetailQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PFOrderDetailQtyQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PFOrderDetailQtyQueryReq>(){};
	}

	@Override
	protected DCP_PFOrderDetailQtyQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PFOrderDetailQtyQueryRes();
	}

	@Override
	protected DCP_PFOrderDetailQtyQueryRes processJson(DCP_PFOrderDetailQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_PFOrderDetailQtyQueryRes res = null;
		res = this.getResponse();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId); //营业日期
		
		String[] pluNoArr = req.getRequest().getPluNo();
		String pluNoStr = "";
		try {
			Calendar calendar = Calendar.getInstance();  
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");  
			Date day = new Date();
			ParsePosition pos = new ParsePosition(0);
			Date strtodate = format.parse(bDate, pos);
			calendar.setTime(strtodate);
		    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);  
		    day = calendar.getTime();  
		    String preDate = format.format(day);  // 昨天（取前日库存）
			
			for (int i = 0; i < pluNoArr.length; i++) {
				pluNoStr = pluNoStr + "'"+pluNoArr[i].toString()+"'";
				if( i +1 < pluNoArr.length){
					pluNoStr = pluNoStr + ",";
				}
			}
			
			String sql = "select pluNo, sum(sale_qty) as preSaleQty  from DCP_stock_day_static where EID = '"+eId+"' and organizationNo = '"+shopId+"' "
							+ " and eDate = '"+preDate+"' and pluNo in ("+pluNoStr+") "
							+ " group by pluNO "
							+ " order by pluNO ";
			
			List<Map<String, Object>> pluDatas = this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_PFOrderDetailQtyQueryRes.level1Elm>());
		
			for (String pluNo : pluNoArr) {
				DCP_PFOrderDetailQtyQueryRes.level1Elm lv1 = res.new level1Elm();
				
				String preSaleQty = "0"; 
				if(pluDatas != null && pluDatas.size() > 0){
					
					for (Map<String, Object> map : pluDatas) { //正常情况下 pluDatas 里的商品应该和pluNoArr 的相等，如果是新品，可能会出现日结历史档里没有该商品， 所以要循环pluArr，昨日销量默认为0.
						if(pluNo.equals(map.get("PLUNO"))){
							preSaleQty = map.getOrDefault("PRESALEQTY","0").toString(); 
						}
					}
					
				}
				
				lv1.setPluNo(pluNo);
				lv1.setPreSaleQty(preSaleQty);
				res.getDatas().add(lv1);
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
			res.setSuccess(false);
		}
		
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_PFOrderDetailQtyQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		
		return null;
	}
	
}
