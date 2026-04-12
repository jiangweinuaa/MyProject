package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECExceptionGoodsUpdateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECExceptionGoodsUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 电商异常商品处理
 * @author yuanyy 2019-06-14
 *
 */
public class DCP_OrderECExceptionGoodsUpdate extends SPosAdvanceService<DCP_OrderECExceptionGoodsUpdateReq, DCP_OrderECExceptionGoodsUpdateRes> {

	@Override
	protected void processDUID(DCP_OrderECExceptionGoodsUpdateReq req, DCP_OrderECExceptionGoodsUpdateRes res)
			throws Exception {
		// TODO Auto-generated method stub
		try {
			String eId = req.geteId();
			String orderNo = req.getEcOrderNo();
			String sql = "";
			String exceptionMemo = "";
			sql = this.getOrderSql(req);
			String[] conditionValues = {}; //查詢條件
			List<Map<String, Object>> getQDatas = this.doQueryData(sql, conditionValues);
			if(getQDatas.size() > 0)
			{
				// 后续支持平台批量删除的时候 ，需要根据单号 orderNo 过滤
				// 目前 直取第一个即可
				exceptionMemo = getQDatas.get(0).get("EXCEPTIONMEMO").toString();
			}
			else 
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("找不到此订单商品!");
				return;
			}
			
			String memoList[] = exceptionMemo.split(",\r\n");
			for (int i = 0; i < memoList.length; i++) {
				String str = memoList[i];
				
				//再解析一遍 memo， 验证是否更新，  个人觉得完全没必要
				String pluNo = str.substring(0,str.indexOf("__") ); // 截取下划线之前的内容
				String pluName = str.substring(str.indexOf("__") + 2); // 截取下划线之后的内容( 带括号)
				
				for (DCP_OrderECExceptionGoodsUpdateReq.level1Elm map : req.getDatas()) 
				{
					String ecPluNo = map.getEcPluNO().toString();
					String ecPluName = map.getEcPluName().toString();
					String erpPluNo = map.getErpPluNO();
					String erpPluName = map.getErpPluName();
					
					if(pluNo.equals(ecPluNo)  ){
						
						UptBean ub1 = null;	
						ub1 = new UptBean("OC_ORDER_DETAIL");
						
						ub1.addUpdateValue("PLUNO", new DataValue(erpPluNo, Types.VARCHAR));
						ub1.addUpdateValue("PLUBARCODE", new DataValue(erpPluNo, Types.VARCHAR));
						ub1.addUpdateValue("PLUNAME", new DataValue(erpPluName, Types.VARCHAR));
						
						ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
						ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
						ub1.addCondition("PLUNO", new DataValue(ecPluNo, Types.VARCHAR));
//						ub1.addCondition("PLUNAME", new DataValue(ecPluName, Types.VARCHAR));
						this.addProcessData(new DataProcessBean(ub1));
						
						String replaceStr = pluNo+"__"+pluName+",";
						
						exceptionMemo = exceptionMemo.replace(replaceStr, "");
						
					}
					
				}
				
			}
			
			// exceptionMemo 变成空， 说明该订单已经没有异常商品了
			if(exceptionMemo.trim().equals("")){
				UptBean ub2 = null;	
				ub2 = new UptBean("OC_ORDER");
				
				ub2.addUpdateValue("EXCEPTIONSTATUS", new DataValue("N", Types.VARCHAR));
				ub2.addUpdateValue("EXCEPTIONMEMO", new DataValue("", Types.VARCHAR));
				
				ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(ub2));
				
			}
			
			this.doExecuteDataToDB();
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECExceptionGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECExceptionGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECExceptionGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECExceptionGoodsUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected TypeToken<DCP_OrderECExceptionGoodsUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECExceptionGoodsUpdateReq>(){};
	}
	
	@Override
	protected DCP_OrderECExceptionGoodsUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECExceptionGoodsUpdateRes();
	}
	
	
	/**
	 * 获取订单详情 （若后续支持平台批量更新， 加上平台即可）
	 * @param req
	 * @return
	 */
	protected String getOrderSql(DCP_OrderECExceptionGoodsUpdateReq req){
		
		String eId = req.geteId();
		String orderNo = req.getEcOrderNo();
		String shopId = req.getShopId();
		
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append("SELECT a.EID, a.orderno,  a.exceptionstatus , a.exceptionmemo , "
				+ "b.pluno , b.pluName , b.plubarcode "
				+ " FROM OC_order a "
				+ " LEFT JOIN OC_order_detail b ON a.EID = b.EID AND a.Orderno = b.orderNo "
				+ " AND a.SHOPID = b.SHOPID "
				+ " where a.EID = '"+eId+"'  and (a.SHOPID = '"+shopId+"' or a.SHIPPINGSHOP='"+shopId+"') and a.orderNO = '"+orderNo+"'");
		
		sql = sqlbuf.toString();
		return sql;
	}
	
	

}
