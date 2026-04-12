package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_PlanQtyUpdateReq;
import com.dsc.spos.json.cust.req.DCP_PluGuQingConformReq;
import com.dsc.spos.json.cust.req.DCP_PluGuQingConformReq.level2Elm;
import com.dsc.spos.json.cust.res.DCP_PluGuQingConformRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.Check;
import com.google.gson.reflect.TypeToken;
	
/**
 * 商品实售同步  POS 调用
 * @author yuanyy 2020-03-03
 *
 */
public class DCP_PluGuQingConform extends SPosAdvanceService<DCP_PluGuQingConformReq, DCP_PluGuQingConformRes> {

	@Override
	protected void processDUID(DCP_PluGuQingConformReq req, DCP_PluGuQingConformRes res) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.getRequest().getoEId();
		String shopId = req.getRequest().getoShopId();
		
		String rDate = req.getRequest().getrDate();
		String guQingNo = req.getRequest().getGuQingNo();
		String saleNo = req.getRequest().getSaleNo();
		String platformType = "pos";
		if(Check.Null(req.getRequest().getPlatformType())){
			platformType = "pos";
		}else{
			platformType = req.getRequest().getPlatformType();
		}		
		
		String modifyBy = req.getRequest().getModifyBy();
		String modifyByName = req.getRequest().getModifyByName();

		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String modifyDate = df.format(cal.getTime());
		df = new SimpleDateFormat("HHmmss");
		String modifyTime = df.format(cal.getTime());
		
		List<level2Elm> pluList = req.getRequest().getPluList();
		
		if(pluList != null && pluList.size() > 0){
			String itemStr = this.queryMaxItem(req);
			int item = Integer.parseInt(itemStr);
			
			for (level2Elm lv2 : pluList) {
				String pluNo = lv2.getPluNo();
				String pUnit = lv2.getpUnit();
				String dtNo = lv2.getDtNo();
				String dtName = lv2.getDtName()==null ? "":lv2.getDtName();
				String restQty = lv2.getRestQty()==null ? "0":lv2.getRestQty();
				String adjustQty = lv2.getAdjustQty()==null ? "0":lv2.getAdjustQty();
				String adjustDirection = lv2.getAdjustDirection(); //枚举: subtract：扣减，,addBack：加回
				
				UptBean ub1 = null;
				ub1 = new UptBean("DCP_GUQINGORDER_DINNERTIME");
				if(adjustDirection.equals("subtract")){ // 销售单==》 可售数量减，已售数量加
					ub1.addUpdateValue("SALEQTY", new DataValue(adjustQty, Types.VARCHAR,DataExpression.UpdateSelf)); //已售数量 
					ub1.addUpdateValue("RESTQTY", new DataValue(adjustQty, Types.VARCHAR,DataExpression.SubSelf)); //剩余可售数量
					adjustQty = "-"+adjustQty;
					
					// 如果商品同步完后，商品可售数量 = 0， 需要变更沽清状态 为Y （POS 根据数量 去判断是否沽清，这个状态没用了）
					// 如果剩余可售数量 和 售卖量 相等，则该商品 isClear 改为 Y
					if( restQty.equals(adjustQty) ){
						ub1.addUpdateValue("ISCLEAR", new DataValue("Y",Types.VARCHAR));
					}
					
				}
				if(adjustDirection.equals("addBack")){ // 退单 ==》 可售数量加，已售数量减
					ub1.addUpdateValue("SALEQTY", new DataValue(adjustQty, Types.VARCHAR,DataExpression.SubSelf)); //已售数量 
					ub1.addUpdateValue("RESTQTY", new DataValue(adjustQty, Types.VARCHAR,DataExpression.UpdateSelf)); //剩余可售数量
				}
				// condition
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				ub1.addCondition("GUQINGNO", new DataValue(guQingNo, Types.VARCHAR));
				ub1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
//				ub1.addCondition("PUNIT", new DataValue(pUnit, Types.VARCHAR));
				ub1.addCondition("DTNO", new DataValue(dtNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub1));
				
				//************* 销售流水记录  DCP_GUQINGORDER_SALEFLOW ***********
				String[] columns_hm ={"EID","SHOPID","GUQINGNO","PLUNO","PUNIT","ITEM","DTNO","DTNAME",
					 "SALEQTY","ORDERNO","MODIFYBY","MODIFYBYNAME","MODIFY_DATE","MODIFY_TIME" ,"PLATFORMTYPE"
				};
				
				DataValue[] insValue_hm = new DataValue[] 
						{
								new DataValue(eId, Types.VARCHAR),
								new DataValue(shopId, Types.VARCHAR), 
								new DataValue(guQingNo, Types.VARCHAR),
								new DataValue(pluNo, Types.VARCHAR),
								new DataValue(pUnit, Types.VARCHAR),
								new DataValue(item+"" ,Types.VARCHAR),
								new DataValue(dtNo, Types.VARCHAR),
								new DataValue(dtName, Types.VARCHAR),
								new DataValue(adjustQty , Types.VARCHAR),
								new DataValue(saleNo, Types.VARCHAR), //规格上是 saleNo， 数据库字段是orderNo 。。。哎
								new DataValue(modifyBy, Types.VARCHAR),
								new DataValue(modifyByName, Types.VARCHAR),
								new DataValue(modifyDate, Types.VARCHAR),
								new DataValue(modifyTime, Types.VARCHAR),
								new DataValue(platformType, Types.VARCHAR)
						};
				
				InsBean ib_hm = new InsBean("DCP_GUQINGORDER_SALEFLOW", columns_hm);
				ib_hm.addValues(insValue_hm);
				this.addProcessData(new DataProcessBean(ib_hm)); 
				
				item = item + 1;
			}
			
		}
		
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_PluGuQingConformReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_PluGuQingConformReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_PluGuQingConformReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_PluGuQingConformReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_PluGuQingConformReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_PluGuQingConformReq>(){};
	}

	@Override
	protected DCP_PluGuQingConformRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_PluGuQingConformRes();
	}
	
	private String queryMaxItem(DCP_PluGuQingConformReq req) throws Exception{
		String maxItem = "1";
		
		String sql = " select nvl(max(item),'0') + 1 as item from DCP_GUQINGORDER_SALEFLOW "
				+ " where EID = '"+req.getRequest().getoEId()+"' and SHOPID = '"+req.getRequest().getoShopId()+"'"
				+ " and guqingNo = '"+req.getRequest().getGuQingNo()+"' "
				;
		
		List<Map<String, Object>> itemDatas = this.doQueryData(sql, null);
		if(itemDatas != null){
			maxItem = itemDatas.get(0).get("ITEM").toString();
		}
		return maxItem;
	}
	
	
}
