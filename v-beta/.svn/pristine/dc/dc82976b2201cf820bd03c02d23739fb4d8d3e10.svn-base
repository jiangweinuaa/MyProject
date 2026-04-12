package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetUpdateReq;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetUpdateReq.level2Elm;
import com.dsc.spos.json.cust.req.DCP_TakeOutOrderBaseSetUpdateReq.level4Elm;
import com.dsc.spos.json.cust.res.DCP_TakeOutOrderBaseSetUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 外卖基础设置新增
 * @author Huawei
 *
 */
public class DCP_TakeOutOrderBaseSetUpdate extends SPosAdvanceService<DCP_TakeOutOrderBaseSetUpdateReq, DCP_TakeOutOrderBaseSetUpdateRes> {

	@Override
	protected void processDUID(DCP_TakeOutOrderBaseSetUpdateReq req, DCP_TakeOutOrderBaseSetUpdateRes res)
			throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
        DCP_TakeOutOrderBaseSetUpdateReq.level1Elm request = req.getRequest();
        try {
			String baseSetNo = request.getBaseSetNo();
			String baseSetName = request.getBaseSetName();
			String choosableTime = request.getChoosableTime();
			String prepareTime = request.getPrepareTime();
			String deliveryTime = request.getDeliveryTime();
			String lowestMoney = request.getLowestMoney();
			String freightWay = request.getFreightWay();
			String freight = request.getFreight();
			String status = request.getStatus();
			String restrictShop = request.getRestrictShop();
            String isPayCard = request.getPromSet().getIsPayCard();
            String freeShippingPrice = request.getFreeShippingPrice();
            if (freeShippingPrice==null||freeShippingPrice.isEmpty())
            {
                freeShippingPrice = "0";
            }
            

            DCP_TakeOutOrderBaseSetUpdateReq.level3Elm promSet = request.getPromSet();
			
			//【ID1035591】扫码点餐及外卖点单加详情配置项-服务  by jinzma 20230828
			String isGoodsDetailDisplay = promSet.getIsGoodsDetailDisplay();
			if (Check.Null(isGoodsDetailDisplay)){
				isGoodsDetailDisplay="0";
			}

			List<DCP_TakeOutOrderBaseSetUpdateReq.level2Elm> cdfDatas = request.getCityDeliverFreight();
			List<DCP_TakeOutOrderBaseSetUpdateReq.level5Elm> paySet = request.getPaySet();
			List<DCP_TakeOutOrderBaseSetUpdateReq.level6Elm> shopDatas = request.getRangeList();
			List<DCP_TakeOutOrderBaseSetUpdateReq.level4Elm> orderTimesDatas = request.getOrderTimes();
			//**************** 管控：不允许一个门店出现多种配置信息 ***********

			String shopStr = "''";
			if(shopDatas != null && restrictShop.equals("1")){ // 适用门店：0-所有门店1-指定门店2-排除门店

				for (DCP_TakeOutOrderBaseSetUpdateReq.level6Elm lv6 : shopDatas) {
					String shopId = lv6.getShopId();
					shopStr = shopStr + ",'"+shopId+"'";
				}

			}

			StringBuffer shopSqlBuffer = new StringBuffer();
			String shopSql = "";

			shopSqlBuffer.append( ""
					+ " SELECT DISTINCT  a.basesetno, a.RESTRICTSHOP, b.SHOPID "
					+ " from dcp_takeout_baseset a "
					+ " left join dcp_takeout_baseset_detail b "
					+ " on a.EID = b.EID "
					+ " and a.basesetno = b.basesetno "
					+ " where a.EID = '"+eId+"' "
					+ " and a.baseSetNo != '"+baseSetNo+"' ");

			shopSql = shopSqlBuffer.toString();

			List<Map<String, Object>> repeatDatas = this.doQueryData(shopSql, null);

			Map<String, Object> condition = new HashMap<String, Object>(); //查询条件
			condition.put("RESTRICTSHOP", "0");
			//调用过滤函数
			List<Map<String, Object>> allTypeDatas = MapDistinct.getWhereMap(repeatDatas, condition, true);

			Map<String, Object> condition2 = new HashMap<String, Object>(); //查询条件
			condition2.put("RESTRICTSHOP", "1");
			//调用过滤函数
			List<Map<String, Object>> assignTypeDatas = MapDistinct.getWhereMap(repeatDatas, condition2, true);

			if(allTypeDatas != null && !allTypeDatas.isEmpty()){ // 已存在的全部门店信息
				for (Map<String, Object> map : allTypeDatas) {

					if(restrictShop.equals("0")){
						if(baseSetNo.equals(map.get("BASESETNO").toString()) ){
							continue;
						}
						else{
							String repeatSetNo = allTypeDatas.get(0).get("BASESETNO").toString();
							res.setServiceDescription("已存在配置信息"+repeatSetNo+"适用于全部门店,不可重复添加");
							res.setSuccess(false);
							res.setServiceStatus("200");
							throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在配置信息"+repeatSetNo+"适用于全部门店,不可重复添加");
						}
					}

				}

			}

			if(assignTypeDatas != null && !assignTypeDatas.isEmpty()){ //已存在的指定门店信息

				if(restrictShop.equals("0")){
					res.setServiceDescription( "已存在配置信息适用于指定门店,不可重复添加");
					res.setSuccess(false);
					res.setServiceStatus("200");
					throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在配置信息适用于指定门店,不可重复添加");
				}

				if(restrictShop.equals("1")){
					String evShopStr = "";
					for (Map<String, Object> map : assignTypeDatas) {
						String shopId = map.get("SHOPID").toString();
						if(Check.Null(shopId) ){
							continue;
						}
						for (DCP_TakeOutOrderBaseSetUpdateReq.level6Elm par : shopDatas) {
							if(shopId.equals(par.getShopId()) && !baseSetNo.equals(map.get("BASESETNO").toString())){
								evShopStr = evShopStr + shopId + " ";
							}
						}
					}

					if(evShopStr.trim().length() > 0 ){
						res.setServiceDescription( "已存在适用于以下门店的配置信息"+evShopStr);
						res.setSuccess(false);
						res.setServiceStatus("200");
						throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "已存在适用于以下门店的配置信息"+evShopStr);
					}

				}

			}

			//***************** 管控结束 **************
			
			DelBean db1 = new DelBean("DCP_TAKEOUT_BASESET_FREIGHT");
			db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db1.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db1));
			
			DelBean db2 = new DelBean("DCP_TAKEOUT_BASESET_DETAIL");
			db2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db2.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db2));
			
			DelBean db3 = new DelBean("DCP_TAKEOUT_BASESET_ORDERTIME");
			db3.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db3.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db3));

			DelBean db4 = new DelBean("DCP_TAKEOUT_BASESET_PAYTYPE");
			db4.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			db4.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(db4));
			
			String createBy = req.getOpNO();
			String createByName = req.getOpName();
			
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = df.format(cal.getTime());
			
			/**
			 * 插入同城距离配送费用
			 */
			if(cdfDatas != null && !cdfDatas.isEmpty()){
				for (level2Elm par : cdfDatas) {
					String[] detailCol = {
							"EID", "BASESETNO", "ITEM", "MAXDISTANCE", "FREIGHT" ,"LOWESTMONEY","FREESHIPPINGPRICE","DELIVERYTIME"
					};
					DataValue[] detailVal = null; 
					detailVal = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(baseSetNo, Types.VARCHAR), 
							new DataValue(par.getSerialNo(), Types.VARCHAR),
							new DataValue(par.getMaxDistance(), Types.VARCHAR), 
							new DataValue(par.getFreight(), Types.VARCHAR),
                            new DataValue(par.getLowestMoney(), Types.VARCHAR),
                            new DataValue(par.getFreeShippingPrice(), Types.VARCHAR),
                            new DataValue(par.getDeliveryTime(), Types.VARCHAR)
					};

					InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET_FREIGHT", detailCol);
					ib1.addValues(detailVal);
					this.addProcessData(new DataProcessBean(ib1)); 
				
				}
			}
			
			
			/**
			 * 插入接单时间
			 */
			if(shopDatas != null && !shopDatas.isEmpty() && (restrictShop.equals("1")||restrictShop.equals("2"))){
				for (DCP_TakeOutOrderBaseSetUpdateReq.level6Elm par : shopDatas) {
					String[] detailCol = {
							"EID", "BASESETNO", "SHOPID", "SHOPNAME"
					};
					DataValue[] detailVal = null; 
					detailVal = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(baseSetNo, Types.VARCHAR), 
							new DataValue(par.getShopId(), Types.VARCHAR),
							new DataValue(par.getName(), Types.VARCHAR)
					};

					InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET_DETAIL", detailCol);
					ib1.addValues(detailVal);
					this.addProcessData(new DataProcessBean(ib1)); 
					
				}
			}
			
			/**
			 * 插入适用门店
			 */
			if(orderTimesDatas != null && !orderTimesDatas.isEmpty()){
				for (level4Elm par : orderTimesDatas) {
					String[] detailCol = {
							"EID", "BASESETNO", "ITEM", "STARTTIME","ENDTIME"
					};
					DataValue[] detailVal = null; 
					detailVal = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(baseSetNo, Types.VARCHAR), 
							new DataValue(par.getItem(), Types.VARCHAR),
							new DataValue(par.getStartTime(), Types.VARCHAR),
							new DataValue(par.getEndTime(), Types.VARCHAR) 
					};

					InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET_ORDERTIME", detailCol);
					ib1.addValues(detailVal);
					this.addProcessData(new DataProcessBean(ib1)); 
					
				}
			}

			/**
			 * 插入支付设置
			 */
			if (paySet != null && !paySet.isEmpty()) {

				for (DCP_TakeOutOrderBaseSetUpdateReq.level5Elm level5Elm : paySet) {
					String[] detailCol = {
							"EID", "BASESETNO", "SORTID", "PAYTYPE", "PAYNAME"
					};
					DataValue[] detailVal = null;
					detailVal = new DataValue[]{
							new DataValue(eId, Types.VARCHAR),
							new DataValue(baseSetNo, Types.VARCHAR),
							new DataValue(level5Elm.getSortId(), Types.VARCHAR),
							new DataValue(level5Elm.getPayType(), Types.VARCHAR),
							new DataValue(level5Elm.getPayName(), Types.VARCHAR)
					};

					InsBean ib1 = new InsBean("DCP_TAKEOUT_BASESET_PAYTYPE", detailCol);
					ib1.addValues(detailVal);
					this.addProcessData(new DataProcessBean(ib1));

				}
			}
			
			
			UptBean ub2 = new UptBean("DCP_TAKEOUT_BASESET");
			
			ub2.addUpdateValue("BASESETNAME", new DataValue(baseSetName, Types.VARCHAR));

			ub2.addUpdateValue("CHOOSABLETIME", new DataValue(choosableTime, Types.VARCHAR));
			ub2.addUpdateValue("PREPARETIME", new DataValue(prepareTime, Types.VARCHAR));
			ub2.addUpdateValue("DELIVERYTIME", new DataValue(deliveryTime, Types.VARCHAR));
			ub2.addUpdateValue("LOWESTMONEY", new DataValue(lowestMoney, Types.VARCHAR));
			ub2.addUpdateValue("FREIGHTWAY", new DataValue(freightWay, Types.VARCHAR));
			
			ub2.addUpdateValue("FREIGHT", new DataValue(freight, Types.VARCHAR));
			ub2.addUpdateValue("PACKPRICETYPE", new DataValue(request.getPackPriceType(), Types.VARCHAR));
			ub2.addUpdateValue("PACKPRICE", new DataValue(request.getPackPrice(), Types.VARCHAR));
//			ub2.addUpdateValue("PLUNO", new DataValue(request.getPluNo(), Types.VARCHAR));
			ub2.addUpdateValue("ISTABLEWARE", new DataValue(request.getIsTableware(), Types.VARCHAR));
			ub2.addUpdateValue("ISREGISTER", new DataValue(promSet.getIsRegister(), Types.VARCHAR));
			ub2.addUpdateValue("COUPON", new DataValue(promSet.getCoupon(), Types.VARCHAR));
			ub2.addUpdateValue("INTEGRAL", new DataValue(promSet.getIntegral(), Types.VARCHAR));
			ub2.addUpdateValue("RESTRICTREGISTER", new DataValue(promSet.getRestrictRegister(), Types.VARCHAR));
			ub2.addUpdateValue("RESTRICTSHOP", new DataValue(restrictShop, Types.VARCHAR));
			ub2.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
			ub2.addUpdateValue("LASTMODIOPID", new DataValue(createBy, Types.VARCHAR));
			ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(createByName, Types.VARCHAR));
			ub2.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
			ub2.addUpdateValue("PAYCOUNTDOWN", new DataValue(request.getPayCountdown(), Types.VARCHAR));

            // Add 2021-06-07 王欢 新增isAutoProm、isAutoRegister字段
			ub2.addUpdateValue("ISAUTOREGISTER", new DataValue(promSet.getIsAutoRegister(), Types.VARCHAR));
			ub2.addUpdateValue("ISAUTOPROM", new DataValue(promSet.getIsAutoProm(), Types.VARCHAR));
			ub2.addUpdateValue("ISAUTOFOLD", new DataValue(promSet.getIsAutoFold(), Types.VARCHAR));
			ub2.addUpdateValue("BEFORORDER", new DataValue(request.getBeforOrder(), Types.VARCHAR));
			ub2.addUpdateValue("ISEVALUATEREMIND", new DataValue(promSet.getIsEvaluateRemind(), Types.VARCHAR));
			ub2.addUpdateValue("REMINDTIME", new DataValue(promSet.getRemindTime(), Types.VARCHAR));
            ub2.addUpdateValue("ISPAYCARD", new DataValue(isPayCard, Types.VARCHAR));
			//【ID1035591】扫码点餐及外卖点单加详情配置项-服务  by jinzma 20230828
			ub2.addUpdateValue("ISGOODSDETAILDISPLAY", new DataValue(isGoodsDetailDisplay, Types.VARCHAR));
			if (req.getRequest().getFreeShippingPrice()!=null)
            {
                ub2.addUpdateValue("FREESHIPPINGPRICE", new DataValue(freeShippingPrice, Types.VARCHAR));
            }
			
			// condition
			ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub2.addCondition("BASESETNO", new DataValue(baseSetNo, Types.VARCHAR));
			this.addProcessData(new DataProcessBean(ub2));
			
			this.doExecuteDataToDB();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			res.setServiceDescription("服务执行失败！"+e.getMessage());
			res.setServiceStatus("200");
			res.setSuccess(false);
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_TakeOutOrderBaseSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_TakeOutOrderBaseSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_TakeOutOrderBaseSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_TakeOutOrderBaseSetUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		StringBuffer errMsg = new StringBuffer("");
		String baseSetNo = req.getRequest().getBaseSetNo();
		
		if (Check.Null(baseSetNo)) 
		{
			errMsg.append("编号不可为空值");
			isFail = true;
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_TakeOutOrderBaseSetUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_TakeOutOrderBaseSetUpdateReq>(){};
	}

	@Override
	protected DCP_TakeOutOrderBaseSetUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_TakeOutOrderBaseSetUpdateRes();
	}
	
}
