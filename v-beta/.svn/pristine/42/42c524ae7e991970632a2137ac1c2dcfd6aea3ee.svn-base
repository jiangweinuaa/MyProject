package com.dsc.spos.service.imp.json;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_StockChannelWhiteUpdateReq;
import com.dsc.spos.json.cust.req.DCP_StockChannelWhiteUpdateReq.PluList;
import com.dsc.spos.json.cust.res.DCP_StockChannelWhiteUpdateRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.google.gson.reflect.TypeToken;

/**
 * 渠道商品分货白名单修改
 * @author 2020-06-04
 *
 */
public class DCP_StockChannelWhiteUpdate extends SPosAdvanceService<DCP_StockChannelWhiteUpdateReq, DCP_StockChannelWhiteUpdateRes> {

	@Override
	protected void processDUID(DCP_StockChannelWhiteUpdateReq req, DCP_StockChannelWhiteUpdateRes res)
			throws Exception {
		// TODO Auto-generated method stub
		
		try {
			String eId = req.geteId();
			String opNo = req.getOpNO();
			String opName = req.getOpName();
			
			Date dt = new Date();
			SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String modiDate =  matter.format(dt);
			
			String deleteSql = "";
			String repeatSql = "";
			StringBuffer sqlbuf = new StringBuffer();
			StringBuffer repeatSqlBuf = new StringBuffer();
			sqlbuf.append( "  delete from DCP_STOCK_CHANNEL_WHITE a "
					+ " where a.Eid = '"+eId+"' " );
			repeatSqlBuf.append( "  select a.eId , a.CHANNELID, a.pluNO ,"
					+ " a.lastmodiOPID, a.lastmodiopname , a.lastmoditime , b.baseUnit "
					+ " from DCP_STOCK_CHANNEL_WHITE a "
					+ " left join DCP_GOODS b on a.eId = b.eId and a.pluNO = b.pluNo "
					+ " where a.Eid = '"+eId+"' " );
			List<PluList> pluDatas = req.getRequest().getPluList();
			
			List<Map<String, Object>> allDatas = new ArrayList<Map<String,Object>>();
			if(pluDatas != null && pluDatas.size() > 0 && !pluDatas.isEmpty() ){
				sqlbuf.append(" and  ( " );
				repeatSqlBuf.append(" and  ( " );
				int totSize = pluDatas.size();
				for (int i = 0; i < pluDatas.size(); i++) {
					String channelId = pluDatas.get(i).getChannelId();
					String pluNo = pluDatas.get(i).getPluNo();

					sqlbuf.append(" ( a.channelId = '"+channelId+"' and  a.pluNo = '"+pluNo+"' )   " );
					repeatSqlBuf.append(" ( a.channelId = '"+channelId+"'  and  a.pluNo = '"+pluNo+"'  )   " );
					
					if(i+1 < totSize ){
						sqlbuf.append(" or ");
						repeatSqlBuf.append(" or ");
					}
					
					Map<String, Object> pluMap = new HashMap<>();
					pluMap.put("CHANNELID", channelId);
					pluMap.put("PLUNO", pluNo);
					allDatas.add(pluMap);
					
				}
				sqlbuf.append(" ) " );
				repeatSqlBuf.append(" ) " );
			}
			deleteSql = sqlbuf.toString();
			ExecBean exc = new ExecBean(deleteSql);
			this.addProcessData(new DataProcessBean(exc));
			
			repeatSql = repeatSqlBuf.toString();
			List<Map<String, Object>> repeatDatas = this.doQueryData(repeatSql, null);
			
			String[] columns1 = {"EID","CHANNELID" ,"PLUNO", "CREATEOPID","CREATEOPNAME","CREATETIME",
					"LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};

			for (PluList pluMap : pluDatas) {
				
				String channelId = pluMap.getChannelId();
				String pluNo = pluMap.getPluNo();
				
				DataValue[] insValue1 = null;
				insValue1 = new DataValue[]{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(channelId, Types.VARCHAR),
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(opNo, Types.VARCHAR),
						new DataValue(opName, Types.VARCHAR),
						new DataValue(modiDate, Types.DATE),
						new DataValue(opNo, Types.VARCHAR),
						new DataValue(opName, Types.VARCHAR),
						new DataValue(modiDate, Types.DATE)
				};
				InsBean ib1 = new InsBean("DCP_STOCK_CHANNEL_WHITE", columns1);
				ib1.addValues(insValue1);
				this.addProcessData(new DataProcessBean(ib1));
				
			}
			
			/**
			 * 
				SELECT * FROM DCP_STOCK_CHANNEL ; 
				SELECT * FROM DCP_STOCK_CHANNEL_bill ; 
				SELECT * FROM DCP_STOCK_CHANNEL_billGoods  ; 
			 */
			
//			if(allDatas.size() > 0 && !allDatas.isEmpty()){
//				for (Map<String, Object> evMap : allDatas) {
//					
//					String channelId = evMap.get("CHANNELID").toString();
//					String pluNo =  evMap.get("PLUNO").toString();
//					String organizationNo = evMap.get("ORGANIZATIONNO").toString();
//					String featureNo =  evMap.get("FEATURENO").toString();
//					String sUnit =  evMap.get("SUNIT").toString();
//					String warehouse =  evMap.get("Warehouse").toString();
//					String baseUnit =  evMap.get("BASEUNIT").toString();
//					
//					for (Map<String, Object> evPluMap : repeatDatas) {
//						//如果 库存白名单表中 已存在这些商品信息， 不用重复插入， 只需将不存在的商品记录插入即可
//						
//						//******************* 去除已存在的商品信息 ******************
//						Iterator<Map<String, Object>> it = allDatas.iterator();
//				        while (it.hasNext()){
//				        	Map<String, Object> s = it.next();
//				        	
//				        	if(channelId.equals(evPluMap.get("CHANNELID").toString() )    
//									&& pluNo.equals(evPluMap.get("PLUNO").toString() ) ){
//								allDatas.remove(s);
//							}
//				        	
//				        }
//						
//					}
//					
//				}
//				
//			}
			
			
			// 此时的 allDatas 已经去除重复数据了
//			String[] channelColumn = {"EID","CHANNELID","ORGANIZATIONNO","PLUNO","SUNIT","FEATURENO","Warehouse",
//					"ONLINEQTY","LOCKQTY","BASEUNIT","BONLINEQTY","BLOCKQTY"};
//
//			String[] billColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","TOTPQTY","TOTCQTY","MEMO",
//					"CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
//			
//			String[] billGoodsColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","ITEM","ORGANIZATIONNO","PLUNO",
//					"SUNIT","FEATURENO","Warehouse","DIRECTION","SQTY","BASEUNIT","BQTY"};
//			
//			if(allDatas.size() > 0 && !allDatas.isEmpty()){
//				
//				String billType = "QDFH001"; //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
////				String billNo = this.queryMaxBillNo(req);
//				int item = 1;
//				
//				String headChannelId = allDatas.get(0).get("CHANNELID").toString();
//				
//				for (Map<String, Object> evMap : allDatas) {
//					
//					String channelId = evMap.get("CHANNELID").toString();
//					String organizationNo = evMap.get("ORGANIZATIONNO").toString();
//					String pluNo =  evMap.get("PLUNO").toString();
//					String featureNo =  evMap.get("FEATURENO").toString();
//					String sUnit =  evMap.get("SUNIT").toString();
//					String warehouse =  evMap.get("Warehouse").toString();
//					String baseUnit =  evMap.get("BASEUNIT").toString();
//					
//					DataValue[] insValue1 = null;
//					insValue1 = new DataValue[]{
//							new DataValue(eId, Types.VARCHAR),
//							new DataValue(channelId, Types.VARCHAR),
//							new DataValue(organizationNo, Types.VARCHAR),
//							new DataValue(pluNo, Types.VARCHAR),
//							new DataValue(sUnit, Types.VARCHAR),
//							new DataValue(featureNo, Types.VARCHAR),
//							new DataValue(warehouse, Types.VARCHAR),
//							new DataValue("0", Types.VARCHAR), //onLineQty 预留数
//							new DataValue("0", Types.VARCHAR),//lockQty 锁定数
//							new DataValue(baseUnit , Types.VARCHAR), //baseUnit 基准单位
//							new DataValue("0", Types.VARCHAR), //bonLineQty 基准单位预留数
//							new DataValue("0", Types.VARCHAR)  //blockQty 基准单位锁定数
//							
//					};
//					InsBean ib1 = new InsBean("DCP_STOCK_CHANNEL", channelColumn);
//					ib1.addValues(insValue1);
//					this.addProcessData(new DataProcessBean(ib1));
//					
//					/**
//					 * String[] billGoodsColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","ITEM","ORGANIZATIONNO","PLUNO",
//					"SUNIT","FEATURENO","Warehouse","DIRECTION","SQTY","BASEUNIT","BQTY"};
//					 */
//					DataValue[] insValue2 = null;
//					insValue2 = new DataValue[]{
//							new DataValue(eId, Types.VARCHAR),
//							new DataValue(channelId, Types.VARCHAR),
//							new DataValue(billNo, Types.VARCHAR), // 没有
//							new DataValue(billType, Types.VARCHAR), //BILLTYPE ，单据类型，此处根据SA要求，固定为QDFH001
//							new DataValue(item, Types.VARCHAR),
//							new DataValue(organizationNo, Types.VARCHAR),
//							
//							new DataValue(pluNo, Types.VARCHAR),
//							new DataValue(sUnit, Types.VARCHAR),
//							new DataValue(featureNo, Types.VARCHAR),
//							new DataValue(warehouse, Types.VARCHAR),
//							new DataValue("1", Types.VARCHAR), //DIRECTION ,调整方向 ： 1增加，  -1 减少
//							new DataValue("0", Types.VARCHAR),
//							
//							new DataValue(baseUnit, Types.VARCHAR),
//							new DataValue("0", Types.VARCHAR)
//							
//					};
//					InsBean ib2 = new InsBean("DCP_STOCK_CHANNEL_BILLGOODS", billGoodsColumn);
//					ib2.addValues(insValue2);
//					this.addProcessData(new DataProcessBean(ib2));
//					
//					item = item + 1;
//					
//				}
//				
//				/**
//				 * String[] billColumn = {"EID","CHANNELID","BILLNO","BILLTYPE","TOTPQTY","TOTCQTY","MEMO",
//					"CREATEOPID","CREATEOPNAME","CREATETIME","LASTMODIOPID","LASTMODIOPNAME","LASTMODITIME"};
//				 */
//				
//				DataValue[] insValue3 = null;
//				insValue3 = new DataValue[]{
//						new DataValue(eId, Types.VARCHAR),
//						new DataValue(headChannelId, Types.VARCHAR),
//						new DataValue(billNo, Types.VARCHAR), // 没有
//						new DataValue(billType, Types.VARCHAR), //没有
//						new DataValue("0", Types.VARCHAR), //总数量
//						new DataValue(item, Types.VARCHAR), // 种类数（有多少个商品 ，即item）
//						
//						new DataValue("", Types.VARCHAR),//memo 
//						new DataValue(opNo, Types.VARCHAR),
//						new DataValue(opName, Types.VARCHAR),
//						new DataValue(modiDate, Types.DATE),
//						new DataValue(opNo, Types.VARCHAR),
//						new DataValue(opName, Types.VARCHAR),
//						new DataValue(modiDate, Types.DATE)
//						
//				};
//				InsBean ib3 = new InsBean("DCP_STOCK_CHANNEL_BILL", billColumn);
//				ib3.addValues(insValue3);
//				this.addProcessData(new DataProcessBean(ib3));
//
//			}
			
			this.doExecuteDataToDB();
			
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(true);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败："+e.getMessage());
		}
		
		
	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_StockChannelWhiteUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_StockChannelWhiteUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_StockChannelWhiteUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_StockChannelWhiteUpdateReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_StockChannelWhiteUpdateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_StockChannelWhiteUpdateReq>(){};
	}

	@Override
	protected DCP_StockChannelWhiteUpdateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_StockChannelWhiteUpdateRes();
	}

	protected String getString(String[] str)
	{
		String str2 = "";
		for (String s:str)
		{
			str2 = str2 + "'" + s + "'"+ ",";
		}
		if (str2.length()>0)
		{
			str2=str2.substring(0,str2.length()-1);
		}

		return str2;
	}
	
	
	/**
	 * 获取最新单据号
	 * @param req
	 * @return
	 * @throws Exception 
	 */
//	private String queryMaxBillNo(DCP_StockChannelWhiteUpdateReq req) throws Exception{
//		String billNo = "";
//		String eId = req.geteId();
//		String billType = "QDFH001";
//		String channelId = req.getRequest().getPluList().get(0).getChannelId();
//		
////		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);
//		Date now = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String bDate = sdf.format(now);
//		
//		String sql = " select max(billNo) as billNo from DCP_STOCK_CHANNEL_BILL where eId = '"+eId+"' " ;
//		
//		List<Map<String, Object>> getQData = this.doQueryData(sql, null);
//		
//		if (getQData != null && getQData.isEmpty() == false) {
//
//			billNo = (String) getQData.get(0).get("BILLNO");
//
//			if (billNo != null && billNo.length() > 0) {
//				long i;
//				billNo = billNo.substring(4, billNo.length());
//				i = Long.parseLong(billNo) + 1;
//				billNo = i + "";
//				billNo = "QDFH" + billNo;
//
//			} else {
//				billNo = "QDFH" + bDate + "00001";
//			}
//		} else {
//			billNo = "QDFH" + bDate + "00001";
//		}
//		
//		return billNo;
//	}
	
	
}
