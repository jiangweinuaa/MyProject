package com.dsc.spos.service.imp.json;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.dao.DataValue.DataExpression;
import com.dsc.spos.json.cust.req.DCP_StockChannelDeleteReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelDeleteReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelDeleteReq.PluList;
import com.dsc.spos.json.cust.res.DCP_StockChannelDeleteRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道库存分配删除
 * http://183.233.190.204:10004/project/144/interface/api/3241
 */
public class DCP_StockChannelDelete extends SPosAdvanceService<DCP_StockChannelDeleteReq, DCP_StockChannelDeleteRes> {

	@Override
	protected void processDUID(DCP_StockChannelDeleteReq req, DCP_StockChannelDeleteRes res) throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId =req.geteId();
			String opNo = req.getOpNO();
			String opName = req.getOpName();
			
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String createTime = df.format(cal.getTime());
			
			List<PluList> pluList = req.getRequest().getPluList();
			
			if(pluList != null && pluList.size() > 0){
				
				//************** DCP_STOCK_CHANNEL_BILL ****************
				String[] columns1 = {"EID", "CHANNELID","BILLNO","BILLTYPE","TOTPQTY" ,"TOTCQTY","MEMO",
						"CREATEOPID","CREATEOPNAME" , "CREATETIME" , "LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME",
						"DEALTYPE" };
				
				// ********** DCP_STOCK_CHANNEL_BILLGOODS *************
				String[] columnsGoods = {"EID", "CHANNELID","BILLNO","BILLTYPE","ITEM" ,"ORGANIZATIONNO","PLUNO",
						"SUNIT","FEATURENO" , "WAREHOUSE" , "DIRECTION","SQTY","BASEUNIT",
						"BQTY" };
				
				String billType = "QDFH001"; //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
				String billNo = this.queryMaxBillNo(req);
				
				// 批量删除，每次只能删除一个渠道的商品。
				String channelId = pluList.get(0).getChannelId();
				
				int item = 1;
				BigDecimal totPQty = new BigDecimal(0);
				
				for (PluList pluMap : pluList) {
				
//					String channelId = pluMap.getChannelId();
					String organizationNo = pluMap.getOrganizationNo();
					String pluNo = pluMap.getPluNo();
					String featureNo = pluMap.getFeatureNo() == null ? " ":pluMap.getFeatureNo();
					String sUnit = pluMap.getsUnit();
					String warehouse = pluMap.getWarehouse();
					String sQty = pluMap.getsQty();
					String baseUnit = pluMap.getBaseUnit();
					
					String baseQty = PosPub.getUnitConvert(dao, eId, pluNo, sUnit, baseUnit, sQty);
					
					totPQty = new BigDecimal(sQty).add(totPQty);
					
					/**
					 * 第一步 ： 更新库存表  （减DCP_STOCK表的预留数ONLINEQTY）
					 */
					UptBean ub2 = null;
					ub2 = new UptBean("DCP_STOCK");
					// condition
					ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					ub2.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
					ub2.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
					ub2.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
					ub2.addCondition("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
					
					ub2.addUpdateValue("ONLINEQTY", new DataValue(baseQty, Types.VARCHAR,DataExpression.SubSelf));
					ub2.addUpdateValue("LASTMODITIME", new DataValue(createTime, Types.DATE));
					ub2.addUpdateValue("LASTMODIOPID", new DataValue(opNo, Types.VARCHAR));
					ub2.addUpdateValue("LASTMODIOPNAME", new DataValue(opName, Types.VARCHAR));
					
					this.addProcessData(new DataProcessBean(ub2));
					
					/**
					 * 第二步 ： 删除 DCP_StockChannel 表信息
					 */
					DelBean db1 = new DelBean("DCP_STOCK_CHANNEL");
					db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
					db1.addCondition("CHANNELID", new DataValue(channelId, Types.VARCHAR));
					db1.addCondition("ORGANIZATIONNO", new DataValue(organizationNo, Types.VARCHAR));
					db1.addCondition("PLUNO", new DataValue(pluNo, Types.VARCHAR));
					db1.addCondition("FEATURENO", new DataValue(featureNo, Types.VARCHAR));
					db1.addCondition("WAREHOUSE", new DataValue(warehouse, Types.VARCHAR));
					
					this.addProcessData(new DataProcessBean(db1));
					
					/**
					 * 第三步： 记录 DCP_STOCK_CHANNEL_BILL 和 DCP_STOCK_CHANNEL_BILLGOODS 
					 * DEALTYPE 给 2：删除 
					 */ 

					DataValue[] insValue2 = null;
					insValue2 = new DataValue[]{
							new DataValue(eId, Types.VARCHAR), 
							new DataValue(channelId, Types.VARCHAR),
							new DataValue(billNo, Types.VARCHAR),
							new DataValue(billType, Types.VARCHAR),
							new DataValue(item, Types.VARCHAR),
							new DataValue(organizationNo, Types.VARCHAR),
							new DataValue(pluNo, Types.VARCHAR),
							new DataValue(sUnit, Types.VARCHAR), //
							new DataValue(featureNo, Types.VARCHAR),
							new DataValue(warehouse, Types.VARCHAR),
							new DataValue("1", Types.VARCHAR),//DIRECTION
							new DataValue(sQty, Types.VARCHAR),
							new DataValue(baseUnit, Types.VARCHAR),
							new DataValue(baseQty, Types.VARCHAR)
						};
					
					InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", columnsGoods);
					ib2.addValues(insValue2);
					this.addProcessData(new DataProcessBean(ib2)); // 新增单身
					
					item += 1;
					
				}
				
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR), 
						new DataValue(channelId, Types.VARCHAR),
						new DataValue(billNo, Types.VARCHAR),
						new DataValue(billType, Types.VARCHAR),
						new DataValue(totPQty, Types.VARCHAR),
						new DataValue(item, Types.VARCHAR),
						new DataValue("", Types.VARCHAR), //memo
						new DataValue(opNo, Types.VARCHAR),
						new DataValue(opName, Types.VARCHAR),
						new DataValue(createTime, Types.DATE),
						
						new DataValue(opNo, Types.VARCHAR),
						new DataValue(opName, Types.VARCHAR),
						new DataValue(createTime, Types.DATE),
						new DataValue("2", Types.VARCHAR) // dealType == 2 ，删除
					};
				
				InsBean ib1 = new InsBean("DCP_STOCK_CHANNEL_BILL", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1)); // 新增單頭
				
				this.doExecuteDataToDB();
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功！");
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setServiceStatus("200");
			res.setSuccess(false);
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockChannelDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockChannelDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockChannelDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StockChannelDeleteReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockChannelDeleteReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockChannelDeleteReq> (){};
	}

	@Override
	protected DCP_StockChannelDeleteRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockChannelDeleteRes();
	}
	
	/**
	 * 获取最新单据号
	 * @param req
	 * @return
	 * @throws Exception 
	 */
	private String queryMaxBillNo(DCP_StockChannelDeleteReq req) throws Exception{
		String billNo = "";
		String eId = req.geteId();
		String billType = "QDFH001";
		
//		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String bDate = sdf.format(now);
		
		String sql = " select max(billNo) as billNo from DCP_STOCK_CHANNEL_BILL where eId = '"+eId+"' and billNo like 'QDFH"+bDate+"%%%' "
//				 + " and  channelId = '"+channelId+"' "
				;
		
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
		
		if (getQData != null && getQData.isEmpty() == false) {

			billNo = (String) getQData.get(0).get("BILLNO");

			if (billNo != null && billNo.length() > 0) {
				long i;
				billNo = billNo.substring(4, billNo.length());
				i = Long.parseLong(billNo) + 1;
				billNo = i + "";
				billNo = "QDFH" + billNo;

			} else {
				billNo = "QDFH" + bDate + "00001";
			}
		} else {
			billNo = "QDFH" + bDate + "00001";
		}
		
		return billNo;
	}
	
	
	
}
