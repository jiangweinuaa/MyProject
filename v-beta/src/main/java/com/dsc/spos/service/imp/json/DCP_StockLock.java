package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.ProcedureBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockLock_OpenReq;
import com.dsc.spos.json.cust.req.DCP_StockLock_OpenReq.PluList;
import com.dsc.spos.json.cust.res.DCP_StockLock_OpenRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.GaoDeUtils;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 库存锁定
 * @author 2020-06-18
 *
 */
public class DCP_StockLock extends SPosAdvanceService<DCP_StockLock_OpenReq, DCP_StockLock_OpenRes> {

	@Override
	protected void processDUID(DCP_StockLock_OpenReq req, DCP_StockLock_OpenRes res) throws Exception {
		// TODO Auto-generated method stub

		/**
		 * 
		 * 门店模式必须给organizationNo（出货机构），后端查询该渠道下该机构的库存
         * 商城模式必须给配送地址对应的省+市+配送地址+配送方式信息，province+city+address+deliveryType:
         * 同城模式，后端查询该渠道下离该配送城市配送地址最近的同城出货机构的库存
         * 全国模式，后端查询该渠道下离该配送城市配送地址最近的全国出货机构的库存
         * 所有行可用数量都足够锁才能锁成功，有一行库存不满足则返回失败；返回的行信息包含可用数量
         * 
		 */
		try {
			String eId = req.geteId();
			
			String billNo = req.getRequest().getBillNo();
			String billType = req.getRequest().getBillType();
			// 根据 SA-孙红艳设计， 此处搞成 固定值 Order 
			billType = "Order";
			String channelId = req.getRequest().getChannelId();//必传
			String bDate = req.getRequest().getbDate();//必传
			String orgNo = req.getRequest().getOrganizationNo();//可为空
			String deliveryType = req.getRequest().getDeliveryType();//配送方式：2全国快递  6同城配送
			String province = req.getRequest().getProvince();
			String city = req.getRequest().getCity();
			String address = req.getRequest().getAddress();
			
//			List<String> deliveryShopList = new ArrayList<String>();
			
			/**
			 * 一：查渠道参数：是否多机构出货
			 * 二：查组织信息： 查同城最近的几个门店 
			 * 三：查询库存信息
			 */
			
			// 查是否允许多机构出货
			String ecSql = " select * from DCP_ECOMMERCE where eId = '"+eId+"' and channelId = '"+channelId+"' ";
			List<Map<String, Object>> ecDatas = this.doQueryData(ecSql, null);
			
			String orderGoodsDeliver = "0";  // 商城模式订单同一商品出货（0不允许多个出货机构；1允许多个出货机构）
			if(ecDatas != null && ecDatas.size() > 0)
			{
				orderGoodsDeliver = ecDatas.get(0).get("ORDERGOODSDELIVER").toString();
			}
			if (Check.Null(orderGoodsDeliver))
			{
                orderGoodsDeliver="0";
			}
			
			List<Map<String, Object>> orgDatas = new ArrayList<>();
			// 看是否传了机构， 如果没传，查离地址最近的几个门店
			if(Check.Null(orgNo)){
				
				String detailAddress = province + city + address;
				String gaoDeKey = PosPub.getPARA_SMS(dao, eId, "", "gaodeKey");
//				GaoDeUtils GDU = new GaoDeUtils();
				JSONObject addressJson = new JSONObject();
				addressJson = GaoDeUtils.getLngAndLat(detailAddress, gaoDeKey);
				
				String latitude = addressJson.getString("latitude");
				String longitude = addressJson.getString("longitude");
				
				String orgSql = "";
				StringBuffer sqlbuf = new StringBuffer();
				
				sqlbuf.append(" select a.* , F_CRM_GetDistance('"+latitude+"','"+longitude+"',a.latitude,a.longitude ) as distance  "
						+ " from DCP_Org a where eId = '"+eId+"' and a.status = '100' "
						+ "  AND a.address IS NOT NULL " // 门店地址不能为空
						+ "  " );
				
				/**
				 * 如果是 同城配送， 需要查 配送城市最近的门店
				 * 如果是 全国配送， 查离配送地址最近的门店
				 */
				if(deliveryType.equals("6")){ //配送方式：2全国快递  6同城配送   
					sqlbuf.append(" and a.city like '%%%"+city+"'%%%");
				}
				sqlbuf.append(" order by distance ");
				
				orgSql = sqlbuf.toString();
				
				orgDatas = this.doQueryData(orgSql, null);
				
				if(orgDatas != null && orgDatas.size() > 0){
//					
//					for (Map<String, Object> map : orgDatas) {
//						deliveryShopList.add(map.get("ORGANIZATIONNO").toString());
//					}
					
				}
				else{
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "无有效出货机构");
				}
					
			}
			else{ 
				// 指定出货机构的时候， 不必查同城配送和距离
				String orgSql = "";
				StringBuffer sqlbuf = new StringBuffer();
				
				sqlbuf.append(" select a.* "
						+ " from DCP_Org a where eId = '"+eId+"' and a.status = '100' "
						+ " AND a.organizationNO = '"+orgNo+"' "
//						+ "  AND a.address IS NOT NULL " // 门店地址不能为空
						+ "  " );
//				/**
//				 * 如果是 同城配送， 需要查 配送城市最近的门店
//				 * 如果是 全国配送， 查离配送地址最近的门店
//				 */
//				if(deliveryType.equals("6")){ //配送方式：2全国快递  6同城配送   
//					sqlbuf.append(" and a.city like '%%%"+city+"'%%%");
//				}
				orgSql = sqlbuf.toString();
				orgDatas = this.doQueryData(orgSql, null);
			}
			
			// 查询渠道库存
			List<PluList> pluList = req.getRequest().getPluList();
			String[] pluArr = new String[pluList.size()] ;
			int n=0;
			for (PluList pluMap : pluList) 
			{
				String pluNo = "";
				if(!Check.Null(pluMap.getPluNo())){
					pluNo = pluMap.getPluNo();
				}
				pluArr[n] = pluNo;
				n++;
			}
			
			MyCommon MC = new MyCommon();
			String pluStr = MC.arrayToString(pluArr);
			
			//MD,查商品信息， 多此一举，连个基准单位都获取不到
			String pluSql = " select pluNo , pluType , baseUnit , sUnit , pUnit , wunit , status from DCP_GOODS A "
					+ " where A.eId = '"+eId+"' and status = '100' and pluNO in ("+pluStr+") ";
			
			List<Map<String , Object>> basePluDatas = this.doQueryData(pluSql, null);
			if(basePluDatas == null || basePluDatas.size() < 1){
				throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品信息无效！");
			}
			

			int item = 1;
			
			next:for (PluList pluMap : pluList) {
				String pluNo = pluMap.getPluNo();
				String featureNo = pluMap.getFeatureNo();
				String sUnit = pluMap.getsUnit();
				String lockQtyStr = pluMap.getQty();//锁定数量 (sUnit)
				String warehouse = ""; //机构下的库存是否充足，  门店默认出货成本仓
//				String baseUnit = pluMap.getBaseUnit();//基准单位
				String baseUnit = "";
				
				for (Map<String, Object> map : basePluDatas) {
					if(pluNo.equals(map.get("PLUNO").toString())){
						baseUnit = map.get("BASEUNIT").toString();
					}
				}
				
				BigDecimal lessQty = new BigDecimal("0");
				boolean oneLockSuccess = true; // 0不允许多个出货机构 ,是否符合条件锁定完成
				Boolean moreLockSuccess = true; // 1允许多个出货机构 ,是否符合条件锁定完成
				
				for (int i = 0; i < orgDatas.size(); i++) {
					String evShop = orgDatas.get(i).get("ORGANIZATIONNO").toString();
					warehouse = orgDatas.get(i).get("OUT_COST_WAREHOUSE").toString(); 
					String stockQtyStr = PosPub.queryStockQty(dao, eId, pluNo, featureNo, evShop , channelId, warehouse, sUnit);
					
					// 比较 商品锁定数量和 机构库存数量
					BigDecimal lockQty = new BigDecimal(lockQtyStr);
					BigDecimal stockQty = new BigDecimal(stockQtyStr);
					
					/**
					 * 当库存量不足时，需要给个提示信息： “所选商品库存量已不足”
					 * 应该是这样 ： 在下单之前，选商品时，需先判断商品库存是否充足 （ 商品可用库存查询服务 ），若不充足，该商品下单量不能超过库存量。
					 * 
					 */
					// 商城模式订单同一商品出货（0不允许多个出货机构；1允许多个出货机构）
					if(orderGoodsDeliver.equals("0")){
						//2020-11-05 根据SA-孙红艳设计， 中台服务此处不校验库存数量是否充足，   无论商城是否检核库存， 中台都让过。
//						if(lockQty.compareTo(stockQty) > 0){ // 锁定数量 > 库存数量
////							lockQty = lockQty.subtract(stockQty);
//							continue;
//						}
//						else {  //锁定数量 <= 库存数量
							String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, lockQty+"");
							
							Map<Integer, Object> inputParameter = new HashMap<>();
							Map<Integer, Integer> outParameter = new HashMap<>();
							inputParameter.put(1, eId);
							inputParameter.put(2, evShop);
							inputParameter.put(3, billType);
							inputParameter.put(4, billNo);
							inputParameter.put(5, item);//item
							inputParameter.put(6, bDate);
							inputParameter.put(7, pluNo);
							inputParameter.put(8, featureNo);
							inputParameter.put(9, warehouse);
							inputParameter.put(10, channelId);
							inputParameter.put(11, sUnit);
							inputParameter.put(12, lockQty);
							inputParameter.put(13, baseUnit);
							inputParameter.put(14, baseQty);
							
							inputParameter.put(15, "R01");
							inputParameter.put(16, "锁定原因描述");
							inputParameter.put(17, "admin");
							
							ProcedureBean pc = new ProcedureBean("SP_DCP_LockStock", inputParameter);
							this.addProcessData(new DataProcessBean(pc));
							
							item = item + 1 ;
							break;
							
//						}
						
					}
					
					//允许多机构出货的时候， 下面还需要查多机构库存是否充足。
					if(orderGoodsDeliver.equals("1")){

						Map<Integer, Object> inputParameter = new HashMap<>();
						Map<Integer, Integer> outParameter = new HashMap<>();
						inputParameter.put(1, eId);
						inputParameter.put(2, evShop);
						inputParameter.put(3, billType);
						inputParameter.put(4, billNo);
						inputParameter.put(5, item);//item
						inputParameter.put(6, bDate);
						inputParameter.put(7, pluNo);
						inputParameter.put(8, featureNo);
						inputParameter.put(9, warehouse);
						inputParameter.put(10, channelId);
						inputParameter.put(11, sUnit);
						inputParameter.put(12, stockQty);// 当锁定量 > 该机构库存量的时候， 该机构锁定量 即为 库存量。  
						inputParameter.put(13, baseUnit);
						// 第 14 个 是baseQty，在下面赋值。 
						inputParameter.put(15, "R01");
						inputParameter.put(16, "锁定原因描述");
						inputParameter.put(17, "admin");
						
						if(lockQty.compareTo(stockQty) > 0){ // 锁定数量 > 库存数量
							lockQty = lockQty.subtract(stockQty); //下个机构该锁定的数量
							  //锁定数量 <= 库存数量
							String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, stockQty+"");
							
							inputParameter.put(14, baseQty);
							ProcedureBean pc = new ProcedureBean("SP_DCP_LockStock", inputParameter);
							this.addProcessData(new DataProcessBean(pc));
							item = item + 1 ;
							continue;
						}
						else {
							
							String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, lockQty+"");
							inputParameter.put(14, baseQty);
							
							ProcedureBean pc = new ProcedureBean("SP_DCP_LockStock", inputParameter);
							this.addProcessData(new DataProcessBean(pc));
							item = item + 1 ;
							break;
						}
					}
					
				}
				
				//库存数量
//				String qty = PosPub.queryStockQty(dao, eId, pluNo, featureNo, orgNo, channelId, warehouse, sUnit);
				
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
	protected List<InsBean> prepareInsertData(DCP_StockLock_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockLock_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockLock_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StockLock_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		
		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if(Check.Null(req.getRequest().getBillNo())){
			isFail = true;
			errMsg.append("单据号billNo不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		
		if(Check.Null(req.getRequest().getBillType())){
			isFail = true;
			errMsg.append("单据类型billType不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
		if(Check.Null(req.getRequest().getChannelId())){
			isFail = true;
			errMsg.append("渠道编号channelId不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		
//		if(Check.Null(req.getRequest().getProvince())){
//			isFail = true;
//			errMsg.append("省份不能为空 ");
//			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//		}
//		
//		
//		if(Check.Null(req.getRequest().getCity())){
//			isFail = true;
//			errMsg.append("城市不能为空 ");
//			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//		}
//		
//		if(Check.Null(req.getRequest().getAddress())){
//			isFail = true;
//			errMsg.append("详细地址不能为空 ");
//			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
//		}
		

		if (isFail){
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}		
		
		
		return isFail;
	}

	@Override
	protected TypeToken<DCP_StockLock_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockLock_OpenReq>(){};
	}

	@Override
	protected DCP_StockLock_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockLock_OpenRes();
	}

}
