package com.dsc.spos.service.imp.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderStatusQuery_OpenReq;
import com.dsc.spos.json.cust.res.DCP_OrderStatusQuery_OpenRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.DateUtils;
import com.dsc.spos.utils.MapDistinct;
import com.google.gson.reflect.TypeToken;

/**
 * 订单状态查询
 * @author Huawei
 *
 */
public class DCP_OrderStatusQuery_Open extends SPosBasicService<DCP_OrderStatusQuery_OpenReq, DCP_OrderStatusQuery_OpenRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderStatusQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderStatusQuery_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderStatusQuery_OpenReq>(){};
	}

	@Override
	protected DCP_OrderStatusQuery_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderStatusQuery_OpenRes();
	}

	@Override
	protected DCP_OrderStatusQuery_OpenRes processJson(DCP_OrderStatusQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderStatusQuery_OpenRes res = null;
		res = this.getResponse();
		String sql = "";
		try
		{
			//单头查询
			sql=this.getQuerySql(req);	
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql, null);
			res.setDatas(new ArrayList<DCP_OrderStatusQuery_OpenRes.level1Elm>());
			
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{
				//单头主键字段
				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查询条件
				condition.put("ORDERNO", true);
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQDataDetail, condition);
				
				for (Map<String, Object> oneData : getQHeader) 
				{
					
					DCP_OrderStatusQuery_OpenRes.level1Elm lv1 = res.new level1Elm();
					String orderNo = oneData.get("ORDERNO").toString();
					if(Check.Null(orderNo)){
						continue;
					}
					lv1.setOrderNO(orderNo);
					lv1.setShipType(oneData.get("SHIPTYPE").toString());
					lv1.setStatusDatas(new ArrayList<DCP_OrderStatusQuery_OpenRes.level2Elm>());
					
					/**
					 * 外卖点餐返回 status 对应 枚举值 
					 * 1、接单，2、制作，3、配送，4、取货，5、完成
					 */
					
					for (Map<String, Object> map : getQDataDetail) {
						
						if(orderNo.equals(map.get("ORDERNO").toString())){
							
							String statusType = map.get("STATUSTYPE").toString(); // 1:订单状态  2：配送状态   3：退单状态   4：其他状态
							String status = map.get("STATUS").toString(); 
							
							if(statusType.equals("1") && status.equals("2")){ //对应 外卖点餐 status 1 和 2 ，接单的同时，也是制作状态 
								
								DCP_OrderStatusQuery_OpenRes.level2Elm status1 = res.new level2Elm();
								status1.setStatus("2"); 
								
								String updateTime = map.get("UPDATE_TIME").toString();
								SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSS");  
								DateUtils du = DateUtils.getInstance();
								updateTime = du.format(format2.parse(updateTime), "yyyy-MM-dd HH:mm:ss") ;
								status1.setDateTime(updateTime);
								lv1.getStatusDatas().add(status1);
								
								
								DCP_OrderStatusQuery_OpenRes.level2Elm status2 = res.new level2Elm();
								status2.setStatus("1"); 
								
								status2.setDateTime(updateTime);
								lv1.getStatusDatas().add(status2);
								
							}	
							
							if(statusType.equals("2") && status.equals("2")){ //对应 外卖点餐 status 3 ，配送状态
								
								DCP_OrderStatusQuery_OpenRes.level2Elm status3 = res.new level2Elm();
								status3.setStatus("3"); 
								
								String updateTime = map.get("UPDATE_TIME").toString();
								SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSS");  
								DateUtils du = DateUtils.getInstance();
								updateTime = du.format(format2.parse(updateTime), "yyyy-MM-dd HH:mm:ss") ;
								status3.setDateTime(updateTime);
								lv1.getStatusDatas().add(status3);
								
							}	
							
							if(statusType.equals("2") && status.equals("3")){ //对应 外卖点餐 status 4 ，取货
								
								DCP_OrderStatusQuery_OpenRes.level2Elm status3 = res.new level2Elm();
								status3.setStatus("4"); 
								
								String updateTime = map.get("UPDATE_TIME").toString();
								SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSS");  
								DateUtils du = DateUtils.getInstance();
								updateTime = du.format(format2.parse(updateTime), "yyyy-MM-dd HH:mm:ss") ;
								status3.setDateTime(updateTime);
								lv1.getStatusDatas().add(status3);
								
							}	
							
							
							if(statusType.equals("1") && status.equals("11")){ //对应 外卖点餐 status 5 ，完成
								
								DCP_OrderStatusQuery_OpenRes.level2Elm status3 = res.new level2Elm();
								status3.setStatus("5"); 
								
								String updateTime = map.get("UPDATE_TIME").toString();
								SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMddHHmmssSS");  
								DateUtils du = DateUtils.getInstance();
								updateTime = du.format(format2.parse(updateTime), "yyyy-MM-dd HH:mm:ss") ;
								status3.setDateTime(updateTime);
								lv1.getStatusDatas().add(status3);
								
							}	
							
							
							/**
				StatusType = 1  订单状态 ， status 对应的枚举值			 * 
				 "0": "待调度";
				 "1": "开立";
				 "2": "已接单";
				 "3": "已拒单/已取消";
				 "4": "生产接单";
				 "5": "生产拒单";
				 "6": "生产完成";
				 "7": "门店调拨";
				 "8": "待提货";
				 "9": "待配送";
				 "10": "已发货";
				 "11": "已完成";
				 "12": "已退单";
				 "13": "已点货"; 
				 "14": "开始制作";
							 */
							
							/**
							 * 	StatusType = 2 配送状态 ， status 对应的枚举值			
				case "-1": "预下单";
				case "0": "已下单";
				case "1": "接单";
				case "2": "取件";
				case "3": "签收";
				case "4": "物流异常或取消";
				case "5": "手动撤销";
				case "6": "到店";
				case "7": "重下单";
				case "8": "貨到物流中心";
				case "9": "消費者七天未取件";
							 */
							
							/**
							 * StatusType = 3 退单状态 ， status 对应的枚举值	
				case "1": "未申请";
				case "2": "申请退单";
				case "3": "已拒绝";
				case "4": "客服仲裁中";
				case "5": "退单失败";
				case "6": "已退单成功";
				case "7": "申请部分退款";
				case "8": "已拒绝部分退款";
				case "9": "部分退款失败";
				case "10": "部分退款成功";
							 */
							
						}
						
					}
					
					res.getDatas().add(lv1);
				}
			}
				
			res.setSuccess(true);
			res.setServiceStatus("000");
			res.setServiceDescription("服务执行成功");
			
		} catch (Exception e) {
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行失败！！");
		}
		
		return res;
		
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getQuerySql(DCP_OrderStatusQuery_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = "";
		
		StringBuffer sqlbuf = new StringBuffer();
		
		String eId = req.getRequest().geteId();
		String appId = req.getRequest().getAppId();
		String outSelID = req.getRequest().getOutSelID(); //外部查询Id， 也就是下单人 / 订购人
//		String[] docType = req.getRequest().getDocType();
		//2020-03-17 docType 按SA要求，数组改为字符串，一次只查一个类型
		String docType = req.getRequest().getDocType();
		String beginDate = req.getRequest().getBeginDate();
		String endDate = req.getRequest().getEndDate();
		
		sqlbuf.append(" SELECT a.orderNo , a.load_docType  ,  a.shiptype ,  a.sdate ,  a.stime , "
				+ " b.status , b.statusName , b.statusType, b.statusTypeName   , b.update_time "
				+ " FROM OC_order a "
				+ " LEFT JOIN OC_order_statuslog b ON a.EID = b.EID AND a.organizationNO = b.organizationNo "
				+ " AND a.SHOPID = b.SHOPID AND a.orderNo = b.orderNo "
				+ " where 1=1 " );
		if (!Check.Null(eId))
		{
			sqlbuf.append(" and a.EID = '"+eId+"' ");
		}
		
		if (!Check.Null(outSelID)) //外部查询ID 即 订购人/ 下单人
		{
			sqlbuf.append(" and a.outSelID = '"+outSelID+"' ");
		}
		
//		if(docType.length > 1 ){
//			String docTypeStr = "";
//			for (int i = 0; i < docType.length; i++) {
//				String ecDoc = docType[i];
//				docType[i] = docType[i].replaceAll(ecDoc, "'"+ecDoc+"'");
//				docTypeStr = docTypeStr + docType[i];
//				if(i < docType.length - 1){
//					docTypeStr = docTypeStr + ",";
//				}
//			}
//			sqlbuf.append(" and a.load_docType in ("+docTypeStr+") ");
//		}
		
		if (!Check.Null(docType))  
		{
			sqlbuf.append(" and a.load_docType = '"+docType+"' ");
		}
		
		if (!Check.Null(beginDate) && !Check.Null(endDate))
		{
			sqlbuf.append(" and a.sDate between '"+beginDate+"' and '"+endDate+"' ");
		}
		
		sqlbuf.append(" ORDER BY a.sdate DESC , a.stime DESC,  b.update_time DESC " );
		
		sql = sqlbuf.toString();
		return sql;
		
	}
	
}
