package com.dsc.spos.scheduler.job;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.ninetyone.NinetyOneClient;
import com.dsc.spos.ninetyone.response.NOSalesOrderDetailReq;
import com.dsc.spos.ninetyone.response.NOSalesOrderListReq;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.ninetyone.util.NinetyOneUtils;
import com.dsc.spos.redis.RedisPosPub;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NinetyOneSalesOrder extends InitJob 
{
	//本地测试用
//	public static void main(String[] args) {
//		Map<String, Object> map=new HashMap<String, Object>();
//		map.put("ShopId", "26332");//商店序號
//		map.put("TMCode", "TM200427B00057");//主單編號 -與{購物車編號}需擇一傳入
//		map.put("TSCode", "");//訂單編號 -若須查詢該主單的全部訂單，請填入""
//		NinetyOneClient nc=new NinetyOneClient();
//		try{
//			String XX=nc.getSalesOrderDetail("https://api.91app.com/ec", "81leLwxuLKe33JKlTyg04801mvniD1g5Nv2HTHwa", map);
//			System.out.print("xxx");
//			
//		}catch(Exception e){
//			
//		}
//	}
	Logger logger = LogManager.getLogger(NinetyOneSalesOrder.class.getName());
	
	public String logFileName = "NinetyOneSalesOrder";
	
	public String logFileNameError = "NinetyOneSalesOrderError";
	
	//redis参数，用来记录同步时间。
	public String redisKey = "ORDERNOAPP";
	//用来记录上一次订单同步的时间
	
	public String hashKey1 = "OrderSale";
	//91APP对应LOAD_DOCTYPE
	public String loadDoctype = "91app";
	
	public int pageSize = 100;
	
	//一天执行一次
	static boolean bFirst=true;
	
	static boolean bRun = false;// 标记此服务是否正在执行中
	
	public NinetyOneSalesOrder()
	{
	}


	public String doExe() 
	{
		writelogFileName("\r\n***************START****************\r\n");
		if (bRun) {
			writelogFileName("");
			writelogFileName("\r\n*************** 服务正在执行中,本次调用取消****************\r\n");
			writelogFileName("\r\n*************** END****************\r\n");
			return null;
		}
		bRun = true;
		//返回信息
		String sReturnInfo="";
		
		try{
			//日志好像会记录sql  sql中带有%，导致在输出日志时报错java.util.UnknownFormatConversionException
			String sql="select A.*,B.SNAME AS SHOPNAME from OC_ECOMMERCE A LEFT JOIN DCP_ORG B ON A.EID=B.EID AND A.SHOPID=B.ORGANIZATIONNO "
					+ " where INSTR(LOWER(A.ECPLATFORMNO),'91app')>0"
					+ " AND A.API_URL IS NOT NULL AND A.SHOPSN IS NOT NULL AND A.SHOPID IS NOT NULL AND APPKEY IS NOT NULL";
			String[] conditionValuesBase = {};
			List<Map<String, Object>> basicList = StaticInfo.dao.executeQuerySQL(sql, conditionValuesBase);
			if (basicList != null && basicList.size()>0 ) {
				NinetyOneClient nc=new NinetyOneClient();
				String formatStr="yyyy-MM-dd'T'HH:mm:ss";
				String endDate=getNowTime(formatStr, "Y");
				for(Map<String, Object> basicMap:basicList){
					//EID	企业编号
					String eId=NinetyOneUtils.getDefaultValueStr(basicMap.get("EID"));
					//API_URL	API地址
					String mainUrl=NinetyOneUtils.getDefaultValueStr(basicMap.get("API_URL"));
					//SHOPSN	店铺编号 91APP店铺编号
					long storeId=NinetyOneUtils.isEmpty(basicMap.get("SHOPSN"))?0:Long.valueOf(basicMap.get("SHOPSN").toString().trim()).longValue();
					//SHOPID	门店  记录91APP订单同步到中台后归属于哪个门店
					String shopId=NinetyOneUtils.getDefaultValueStr(basicMap.get("SHOPID"));
					String shopName=NinetyOneUtils.getDefaultValueStr(basicMap.get("SHOPNAME"));
					//APPKEY	APIKEY 存放91APP x—api-key
					String xApiKey=NinetyOneUtils.getDefaultValueStr(basicMap.get("APPKEY"));

					String thisHashKey=hashKey1+eId+storeId;
					String startDate=getLastUpdateTime(thisHashKey,formatStr);
					
					long startDateTime = new SimpleDateFormat("yyyy-MM-dd").parse(startDate).getTime();
					long endDateTime = new SimpleDateFormat("yyyy-MM-dd").parse(endDate).getTime();
					Long l = (endDateTime - startDateTime) / (1000 * 60 * 60 * 24);
					if(l>7){
						Calendar calendar = Calendar.getInstance();
			            calendar.setTime(new SimpleDateFormat(formatStr).parse(startDate));
			            calendar.add(Calendar.DAY_OF_YEAR, 7);
			            endDate=new SimpleDateFormat(formatStr).format(calendar.getTime());
					}
					
					Map<String, Object> params=getSalesOrderListParams(startDate,endDate,storeId);
					//0 从第一页记录开始取
					doTransport(nc, mainUrl, xApiKey, storeId, eId, shopId,shopName, params,0,0);
					saveRedis(redisKey, thisHashKey, endDate);
				}
			}else{
				writelogFileName("\r\n*************** OC_ECOMMERCE未配置,执行结束****************\r\n");
			}
		}catch(Exception e){
			writelogFileName("\r\n***************执行失败"+getErrorMessage(e)+"****************\r\n");
		}finally {
			bRun = false;
		}

		writelogFileName("\r\n***************END****************\r\n");
		return sReturnInfo;	

	}
	
	public static String getErrorMessage(Throwable t){
        StringWriter stringWriter=new StringWriter();
        t.printStackTrace(new PrintWriter(stringWriter,true));
        return stringWriter.getBuffer().toString();
    }
	
	
	/**
	 * 执行传输
	 * @param nc
	 * @param params
	 * @throws Exception
	 */
	public void doTransport(NinetyOneClient nc,String mainUrl,String xApiKey,long storeId,String eId,
			String shopId,String shopName,Map<String, Object> params,int position,int totalPage) throws Exception{
		NOSalesOrderListReq.SalesOrderListData orderListData=getSalesOrderList(nc.getSalesOrderList(mainUrl, xApiKey, params));
		
		if(orderListData!=null&&orderListData.getList()!=null&&orderListData.getList().size()>0){
			List<NOSalesOrderListReq.SalesOrderCode> salesOrderList=orderListData.getList();
			doTransportLoop(nc, mainUrl, xApiKey, storeId, eId, shopId,shopName, params, salesOrderList);
			int totalCount=orderListData.getTotalCount();
			for(int i=1;i<totalCount/pageSize;i++){
				params.put("Position", i*pageSize);//Position 位置 -第一筆資料的位置為 0，第二筆的資料位置為 1，以此類推
				orderListData=getSalesOrderList(nc.getSalesOrderList(mainUrl, xApiKey, params));
				if(orderListData!=null&&orderListData.getList()!=null&&orderListData.getList().size()>0){
					doTransportLoop(nc, mainUrl, xApiKey, storeId, eId, shopId,shopName, params, orderListData.getList());
				}
			}
		}
		
		
	}
	
	public void doTransportLoop(NinetyOneClient nc,String mainUrl,String xApiKey,long storeId,String eId,
			String shopId,String shopName,Map<String, Object> params,List<NOSalesOrderListReq.SalesOrderCode> salesOrderList) throws Exception{
		List<String> orderNoStrList=new ArrayList<String>();
		for(NOSalesOrderListReq.SalesOrderCode salesOrderCode:salesOrderList){
			//主订单号
			String orderNo=salesOrderCode.getTMCode();
			if(orderNoStrList.contains(orderNo)){
				//主订单号不是第一次出现
				continue;
			}
			orderNoStrList.add(orderNo);
			Map<String, Object> detailParams=getSalesOrderDetailParams(salesOrderCode,storeId);
			List<NOSalesOrderDetailReq.SalesOrderDetail> salesOrderDetailList=getSalesOrderDetailList(nc.getSalesOrderDetail(mainUrl, xApiKey, detailParams));
			if(salesOrderDetailList!=null&&salesOrderDetailList.size()>0){
				NOSalesOrderDetailReq.SalesOrderDetail mainOrder=salesOrderDetailList.get(0);
				//91APP订单状态
				String orderStatus=mainOrder.getOrderStatus();
				//取消订单(退货)由NinetyOneReturnOrder处理。
				if("Cancel".equals(orderStatus)){
					writelogFileName("\r\n***************" + orderNo+"订单已退货,由NinetyOneReturnOrder处理,跳过!****************\r\n");
					continue;
				}
				//付款确认中： WaitingToCreditCheck
				else if("WaitingToCreditCheck".equals(orderStatus)){
					writelogFileName("\r\n***************" + orderNo+"订单付款确认中,跳过!****************\r\n");
					continue;
				}
				//待付款： WaitingToPay
				else if("WaitingToPay".equals(orderStatus)){
					writelogFileName("\r\n***************" + orderNo+"订单待付款,跳过!****************\r\n");
					continue;
				}
				//判断一下数据库是否已存在该单据，如果有直接continue掉
				String sqlOrder="SELECT * FROM OC_ORDER where ORDERNO='"+orderNo+"'  and EID='"+eId+"' and LOAD_DOCTYPE='"+loadDoctype+"' ";
				List<Map<String, Object>> listSqlOrder=this.doQueryData(sqlOrder, null);
				if(listSqlOrder!=null&&listSqlOrder.size()>0)
				{
					Map<String, Object> orderMap=listSqlOrder.get(0);
					//訂單狀態日 2013-08-30T17:30:00 "yyyy-MM-dd'T'HH:mm:ss"
					String OrderStatusUpdatedDateTime=salesOrderDetailList.get(0).getOrderStatusUpdatedDateTime();
					String orderTranTime=NinetyOneUtils.getDefaultValueStr(orderMap.get("TRAN_TIME"));
					//11.已完成 12.已退单
					String thisOrderStatus=NinetyOneUtils.getDefaultValueStr(orderMap.get("STATUS"));
					if("11".equals(thisOrderStatus)){
						writelogFileName("\r\n***************" + orderNo+"订单已完成 ,跳过！****************\r\n");
						continue;
					}else if("12".equals(thisOrderStatus)){
						writelogFileName("\r\n***************" + orderNo+"订单已退单 ,跳过！****************\r\n");
						continue;
					}
					if(orderTranTime.equals(OrderStatusUpdatedDateTime)){
						writelogFileName("\r\n***************" + orderNo+"订单已存在！****************\r\n");
						continue;
					}else{
						//传输时间TRAN_TIME与OrderStatusUpdatedDateTime訂單狀態日不一致，表明存在状态变更。
						//执行状态变更作业
						//訂單狀態
						//- 已取消： Cancel
						//- 已完成： Finish
						//- 付款確認中： WaitingToCreditCheck
						//- 已成立： WaitingToShipping
						//- 已確認待出貨： ConfirmedToShipping
						//- 待付款： WaitingToPay
						doUpdateOrder(eId, orderTranTime, salesOrderDetailList);
					}
				}else{
					//写入新单据
					doSaveNewOrder(eId, shopId,shopName, salesOrderDetailList);
				}
			}
			else{
				writelogFileName("\r\n***************主订单号:"+orderNo+"未获取到明细资料,参数:url-"+mainUrl+"xApiKey-"+xApiKey + "detailParams-"+detailParams+"****************\r\n");
			}
		}
	}
	
	/**
	 * 保存新单据
	 * @param nc
	 * @param params
	 * @throws Exception
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void doSaveNewOrder(String eId,String shopId,String shopName,
			List<NOSalesOrderDetailReq.SalesOrderDetail> salesOrderDetailList) throws Exception{
		
		//列表SQL
		String orderNo=salesOrderDetailList.get(0).getTMCode();
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
		InsBean tvOrder=getTvOrder(eId,shopId,shopName, salesOrderDetailList);
		lstData.add(new DataProcessBean(tvOrder));
		
		List<DataProcessBean> listDataDetail=getTvOrderDetail(eId, shopId, salesOrderDetailList);
		List<DataProcessBean> listDataAgio=getTvOrderAgioList(eId, shopId, salesOrderDetailList);
		List<DataProcessBean> listDataPay=getTvOrderPayList(eId, shopId, salesOrderDetailList);
		List<DataProcessBean> listDataStatusLog=getTvOrderStatusLogList(eId, shopId, salesOrderDetailList.get(0));
		lstData.addAll(listDataDetail);
		lstData.addAll(listDataAgio);
		lstData.addAll(listDataPay);
		lstData.addAll(listDataStatusLog);
		
		boolean isSuccess=false;
		try{
			isSuccess=StaticInfo.dao.useTransactionProcessData(lstData);
		}catch(Exception e){
			NinetyOneUtils.writelogFileName(logFileNameError,"\r\n***********单据:["+orderNo+"]保存时发生异常:"+ExceptionUtils.getRootCauseMessage(e));
		}
	}
	
	/**
	 * 更新单据
	 * @param nc
	 * @param params
	 * @throws Exception
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void doUpdateOrder(String eId,String shopId,List<NOSalesOrderDetailReq.SalesOrderDetail> salesOrderDetailList) throws Exception{
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date dateNow=new Date();
		//列表SQL
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
		NOSalesOrderDetailReq.SalesOrderDetail mainOrder=salesOrderDetailList.get(0);
		//91APP订单状态
		String orderStatus=mainOrder.getOrderStatus();
		String orderNo=mainOrder.getTMCode();
		Map<String, Object> statusMap=getStatusMap(orderStatus);
		String tranTime=mainOrder.getOrderStatusUpdatedDateTime();
		String statusType=statusMap.get("statusMap").toString();
		String statusTypeName=statusMap.get("statusTypeName").toString();
		String status=statusMap.get("status").toString();
		String statusName=statusMap.get("statusName").toString();
				
		UptBean ub1 = new UptBean("OC_ORDER");
		//add Value
		ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
		ub1.addUpdateValue("TRAN_TIME", new DataValue(tranTime, Types.VARCHAR));
		ub1.addUpdateValue("UPDATE_TIME", new DataValue(sdf5.format(dateNow), Types.VARCHAR));

		//物流配送状态
		//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
		String deliveryStutas="";
		//91APP訂單狀態 - 已取消： Cancel   - 已成立： WaitingToShipping  - 已確認待出貨： ConfirmedToShipping
		String refundStatus="";
		if("Cancel".equals(orderStatus)){
			refundStatus="13";//13.取消订单成功
		}else if("ConfirmedToShipping".equals(orderStatus)){
			deliveryStutas="0";
		}
		
		//2配送状态
		if("2".equals(statusType)){
			//物流类型(虾皮用到)		DELIVERYTYPE
			//0、无 1 自配送 2 顺丰 3百度 4达达 5 人人 6 闪送 7.7-11超商 8.全家超商 9.黑猫宅急便 10.莱而富超商 11.OK超商 12.mingjie大件物流 
			//13.中华邮政 14.卖家宅配 15.新竹物流 16.绿界7-11超商 17.绿界全家超商 18.绿界莱而富超商 19.绿界OK超商
			String deliverytype="";
			//配送方式
			//-宅配(含離島宅配) :Home
			//-超商取貨付款 : StoreCashOnDelivery
			//-付款後超商取貨 :StorePickup
			//-付款後門市自取 : LocationPickup
			//-貨到付款： CashOnDelivery
			//-海外宅配： Oversea
			String orderDeliverType=mainOrder.getOrderDeliverType();
			String pickupWay="";//取货方式 1：超取 2：宅配
			if ("Home".equals(orderDeliverType)||"CashOnDelivery".equals(orderDeliverType)||"Oversea".equals(orderDeliverType)) 
			{
				pickupWay="2";
				deliverytype="14";//卖家宅配
			}
			else 
			{
				pickupWay="1";
				String distributorDef=mainOrder.getDistributorDef();//通路商  -全家： Family  -7-11： SevenEleven
				if ("Family".equals(distributorDef)) 
				{
					deliverytype="8";//8.全家超商
				}
				else if ("SevenEleven".equals(distributorDef)) 
				{
					deliverytype="7";//7.7-11超商
				}
				else 
				{
					deliverytype="";
				}				

			}
			
			String shipType="";
			if ("2".equals(pickupWay)) 
			{
				shipType="2";
			}
			else 
			{
				shipType="6";
			}
			//实际配送费		SHIPFEE
			BigDecimal shipFee=new BigDecimal(0);
			//配送费减免		RSHIPFEE
			BigDecimal rshipfee=new BigDecimal(0);
			//总配送费		TOTSHIPFEE
			BigDecimal totShipFee=new BigDecimal(0);
			//91主單(TM)運費
			String TMShippingFee=salesOrderDetailList.get(0).getTMShippingFee();
			if(NinetyOneUtils.isNotEmpty(TMShippingFee)){
				shipFee=new BigDecimal(TMShippingFee.trim());
				totShipFee=new BigDecimal(TMShippingFee.trim());
			}
			ub1.addUpdateValue("SHIPFEE", new DataValue(shipFee, Types.DECIMAL));//实际配送费		SHIPFEE
			ub1.addUpdateValue("RSHIPFEE", new DataValue(rshipfee, Types.DECIMAL));//配送费减免		RSHIPFEE
			ub1.addUpdateValue("TOTSHIPFEE", new DataValue(totShipFee, Types.DECIMAL));//总配送费		TOTSHIPFEE
			ub1.addUpdateValue("SHIPTYPE", new DataValue(shipType, Types.VARCHAR));//配送方式 SHIPTYPE
			ub1.addUpdateValue("DELIVERYTYPE", new DataValue(deliverytype, Types.VARCHAR));//物流类型(虾皮用到) DELIVERYTYPE
			ub1.addUpdateValue("PICKUPWAY", new DataValue(pickupWay, Types.VARCHAR));//取货方式 PICKUPWAY 1：超取 2：宅配
			ub1.addUpdateValue("DELIVERYSTUTAS", new DataValue(deliveryStutas, Types.VARCHAR));//物流配送状态
		}
		//3-退单状态   12-已退单
		if("3".equals(statusType)&&"12".equals(status)){
			//退货原因描述
			ub1.addUpdateValue("REFUNDREASON", new DataValue(mainOrder.getCancelOrderSlaveCauseDefDesc(), Types.VARCHAR));
			ub1.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus, Types.VARCHAR));
		}
		
		//condition
		ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDoctype, Types.VARCHAR));
		ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		lstData.add(new DataProcessBean(ub1));
		//写入历程
		lstData.add(getTvOrderStatusLog(eId, shopId, tranTime, orderNo, statusType, statusTypeName, status, statusName));
		boolean isSuccess=false;
		try{
			isSuccess=StaticInfo.dao.useTransactionProcessData(lstData);
		}catch(Exception e){
			NinetyOneUtils.writelogFileName(logFileNameError,"\r\n***********单据:["+orderNo+"]保存时发生异常:"+ExceptionUtils.getRootCauseMessage(e));
		}
	}
	
	
	/**
	 * 
	 * result转换为实体类NOSalesOrderListReq
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public List<NOSalesOrderDetailReq.SalesOrderDetail> getSalesOrderDetailList(String result) throws Exception{
		List<NOSalesOrderDetailReq.SalesOrderDetail> salesOrderDetailList=new ArrayList<NOSalesOrderDetailReq.SalesOrderDetail>();
		NOSalesOrderDetailReq salesOrderDetailReq=JSON.parseObject(result, NOSalesOrderDetailReq.class);
		if(salesOrderDetailReq!=null&&"success".equals(salesOrderDetailReq.getStatus())){
			NOSalesOrderDetailReq.SalesOrderDetailData data= salesOrderDetailReq.getData();
			if(data!=null&&data.getList()!=null&&data.getList().size()>0){
				salesOrderDetailList=data.getList();
			}
		}else{
			writelogFileName("\r\n***************result转换实体类NOSalesOrderListReq异常"+result+"****************\r\n");
		}
		return salesOrderDetailList;
	}
	
	/**
	 * 整理呼叫SalesOrderDetail接口的参数
	 * @param salesOrderCode
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getSalesOrderDetailParams(NOSalesOrderListReq.SalesOrderCode salesOrderCode,long storeId) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("ShopId", storeId);//商店序號
		map.put("TMCode", salesOrderCode.getTMCode());//主單編號 -與{購物車編號}需擇一傳入
		map.put("TSCode", "");//訂單編號 -若須查詢該主單的全部訂單，請填入""
		return map;
	}
	
	/**
	 * 
	 * result转换为实体类NOSalesOrderListReq
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public NOSalesOrderListReq.SalesOrderListData getSalesOrderList(String result) throws Exception{
		NOSalesOrderListReq.SalesOrderListData orderListData=null;
//		List<NOSalesOrderListReq.SalesOrderCode> salesOrderList=new ArrayList<NOSalesOrderListReq.SalesOrderCode>();
		NOSalesOrderListReq salesOrderListReq=JSON.parseObject(result, NOSalesOrderListReq.class);
		if(salesOrderListReq!=null&&"success".equals(salesOrderListReq.getStatus())){
			NOSalesOrderListReq.SalesOrderListData data= salesOrderListReq.getData();
			if(data!=null&&data.getList()!=null&&data.getList().size()>0){
				orderListData=data;
			}
		}else{
			writelogFileName("\r\n***************result转换实体类NOSalesOrderListReq异常"+result+"****************\r\n");
		}
		return orderListData;
	}
	
	/**
	 * 整理呼叫SalesOrderList接口的参数
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getSalesOrderListParams(String startDate,String endDate,long storeId) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("OrderDeliverType", "");//訂單配送方式 -全部 : 空白，請填入""
		map.put("DistributorDef", "");//通路商 -全部 : 空白，請填入""
		//TemperatureTypeDef 溫層類別
		//-全部 : 空白，請填入""
		//-常溫 :Normal
		//-冷藏 :Refrigerator
		//-冷凍 :Freezer
		map.put("TemperatureTypeDef", "");//通路商 -全部 : 空白，請填入""
		//OrderDateType 查詢根據日期
		//-訂單轉單日 : OrderDateTime
		//-訂單預計出貨日 : OrderExpectShippingDate
		//-訂單狀態日 : OrderStatusUpdatedDateTime (訂單被異動狀態的日期)
		map.put("OrderDateType", "OrderStatusUpdatedDateTime");
		//"StartDate": "2014-06-16T00:00:00"
		map.put("StartDate", startDate);
		map.put("EndDate", endDate);
//		map.put("StartDate", "2020-01-08T00:00:00");//测试用
//		map.put("EndDate", "2020-01-15T00:00:00");//测试用
		//OrderStatus 訂單狀態
		//- 全部: 空白，請填入”“
		//- 已成立: WaitingToShipping
		//- 已確認待出貨: ConfirmedToShipping
		//- 已完成: Finish
		//- 已取消: Cancel
		//- 付款確認中: WaitingToCreditCheck
		//- 待付款: WaitingToPay
		map.put("OrderStatus", "");
		map.put("Position", 0);//Position 位置 -第一筆資料的位置為 0，第二筆的資料位置為 1，以此類推
		map.put("Count", pageSize);//Count 取回資料筆數 -單次查詢最多 100 筆訂單清單
		//ShippingOrderStatus 出貨單狀態
		//-全部 : 空白，請填入""
		//-NotYetAllocatedCode : 尚未配號
		//-Finish : 已出貨至消費者
		map.put("ShippingOrderStatus", "");
		map.put("ShopId", storeId);//商店序號
		return map;
	}
	
	
	/**
	 * 获取redis中上次更新时间,减2秒处理(避免可能出现的漏单情况)，如果没有，取当天的0分0秒
	 * "yyyy-MM-dd'T'HH:mm:ss"
	 * @param hashKey
	 * @return
	 * @throws Exception
	 */
	public String getLastUpdateTime(String hashKey,String formatStr) throws Exception{
		RedisPosPub redis = new RedisPosPub();
		String lastUpdatetime=redis.getHashMap(redisKey, hashKey);
		redis.Close();
		SimpleDateFormat sdf1 = new SimpleDateFormat(formatStr);
		//从redis抓取的上次更新时间
		Calendar cal = Calendar.getInstance();
		if(NinetyOneUtils.isEmpty(lastUpdatetime)){
			String sql1 = "SELECT * FROM PLATFORM_BASESET where ITEM = '91UpdateTime'";
			String[] ps = new String[]{};
			List<Map<String,Object>> mps =	StaticInfo.dao.executeQuerySQL(sql1, ps, false);
			if(mps!=null&&mps.size()>0){
				cal.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(mps.get(0).get("ITEMVALUE").toString()));
			}else{
				cal.setTime(new Date());
//			calendar.set(Calendar.HOUR_OF_DAY, 0);
//			calendar.set(Calendar.MINUTE, 0);
//			calendar.set(Calendar.SECOND, 0);
				cal.add(Calendar.DATE, -7);
			}
		}else{
			Date tempDate = sdf1.parse(lastUpdatetime);
			//固定减2秒 避免可能出现的漏单情况 
			cal.setTime(tempDate);
			cal.add(Calendar.SECOND,-2);//					
		}
		lastUpdatetime=sdf1.format(cal.getTime());
		return lastUpdatetime;
	}

	/**
	 * 获取当前时间 
	 * isAdd标记是否做减2秒处理
	 * @param formatStr
	 * @return
	 * @throws Exception
	 */
	public String getNowTime(String formatStr,String isAdd) throws Exception{
		SimpleDateFormat sdf1 = new SimpleDateFormat(formatStr);
		Calendar cal= Calendar.getInstance();
		if("Y".equals(isAdd)){
			cal.add(Calendar.SECOND, -2);
		}
		return sdf1.format(cal.getTime());
	}
	public void saveRedis(String redisKey,String hashKey,String value) throws Exception{
		try 
		{
			RedisPosPub redis = new RedisPosPub();
			boolean isexistHashkey = redis.IsExistHashKey(redisKey, hashKey);
			if(isexistHashkey)
			{
				redis.DeleteHkey(redisKey, hashKey);//
				writelogFileName("【删除存在hashKey的缓存】成功！"+" redisKey:"+redisKey+" hashKey:"+hashKey);
			}
			boolean nret = redis.setHashMap(redisKey, hashKey, value);
			if(nret)
			{
				writelogFileName("【写缓存】OK"+" redisKey:"+redisKey+" hashKey:"+hashKey+" value:"+value);
			}
			else
			{
				writelogFileName("【写缓存】Error"+" redisKey:"+redisKey+" hashKey:"+hashKey+" value:"+value);
			}
			redis.Close();

		} 
		catch (Exception e) 
		{
			writelogFileName("【写缓存】Exception:"+ExceptionUtils.getRootCauseMessage(e));	
		}
	}
	
	// 产生UUID  用于ORDER_ID赋值
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		String uuidStr = str.replace("-", "");
		return uuidStr;
	}
	
	/**
	 * OC_ORDER 订单中心订单主表
	 * @return
	 */
	public InsBean getTvOrder(String eId,String shopId,String shopName,List<NOSalesOrderDetailReq.SalesOrderDetail> salesOrderDetailList) throws Exception{
		NOSalesOrderDetailReq.SalesOrderDetail mainOrder=salesOrderDetailList.get(0);
		BigDecimal zero=BigDecimal.ZERO;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf4 = new SimpleDateFormat("HHmmss");
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		//主单号
		String orderNo=mainOrder.getTMCode();
		ColumnDataValue columns = new ColumnDataValue();
		columns.Add("EID", eId, Types.VARCHAR); //企业编号
		columns.Add("CUSTOMERNO", " ", Types.VARCHAR); //客户编号 参照HelpTools中逻辑
		columns.Add("ORGANIZATIONNO", shopId, Types.VARCHAR); //组织编号
		columns.Add("SHOPID", shopId, Types.VARCHAR); //门店编号 组织门店给相同值
		columns.Add("SHOPNAME", shopName, Types.VARCHAR); //门店编号 组织门店给相同值
		columns.Add("ORDER_ID", getUUID(), Types.VARCHAR); //订单ID
		columns.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号
		columns.Add("LOAD_DOCTYPE", loadDoctype, Types.VARCHAR); //来源类型
		columns.Add("CURRENCYNO", 2, Types.INTEGER); //币别 1:人民币 2：台币
		
		
		
		//91APP订单状态
		String orderStatus=mainOrder.getOrderStatus();
		String status=getStatusMap(orderStatus).get("status").toString();
		columns.Add("STATUS", status, Types.VARCHAR); //订单状态

		//物流配送状态
		//-1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件
		String deliveryStutas="";
		//91APP訂單狀態 - 已取消： Cancel   - 已成立： WaitingToShipping  - 已確認待出貨： ConfirmedToShipping
		String refundStatus="";
		if("Cancel".equals(orderStatus)){
			refundStatus="13";//13.取消订单成功
		}else if("ConfirmedToShipping".equals(orderStatus)){
			deliveryStutas="0";
		}
		columns.Add("REFUNDSTATUS", refundStatus, Types.VARCHAR);//退订状态
		columns.Add("DELIVERYSTUTAS", deliveryStutas, Types.VARCHAR);//物流配送状态
		//columns.Add("HEADORDERNO", mainOrder.getTMCode(), Types.VARCHAR);//主单号 HEADORDERNO
		
		//91APP訂單轉單日  如:2013-08-30T17:30:00
		String orderDateTime=mainOrder.getOrderDateTime();
		Date tempDate = sdf1.parse(orderDateTime);
		//下单时间		CREATE_DATETIME 格式参照数据库
		columns.Add("CREATE_DATETIME", sdf2.format(tempDate), Types.VARCHAR);
		
		columns.Add("STATUS", "Y", Types.VARCHAR);//状态
		columns.Add("TRAN_TIME", mainOrder.getOrderStatusUpdatedDateTime(), Types.VARCHAR);//时间标记
		
		Date dateNow=new Date();
		columns.Add("SDATE", sdf3.format(dateNow), Types.VARCHAR);//系统日期
		columns.Add("STIME", sdf4.format(dateNow), Types.VARCHAR);//系统时间
		columns.Add("UPDATE_TIME", sdf5.format(dateNow), Types.VARCHAR);//修改时间
				
		columns.Add("PROVINCE", "台湾省", Types.VARCHAR);//省
		columns.Add("CITY", mainOrder.getOrderReceiverCity(), Types.VARCHAR);//市
		columns.Add("COUNTY", mainOrder.getOrderReceiverDistrict(), Types.VARCHAR);//区县  91APP-收件人乡镇市区

		//orderReceiverAddress 91APP 收件人地址 string(200)
		String orderReceiverAddress=mainOrder.getOrderReceiverAddress();
		//中台数据库 ADDRESS 长度100，  此处需要判断、截取
		if(orderReceiverAddress!=null&&orderReceiverAddress.length()>99){
			orderReceiverAddress=orderReceiverAddress.substring(0,99);
		}
		columns.Add("ADDRESS", orderReceiverAddress, Types.VARCHAR);//配送地址 ADDRESS
//		街道		STREET
		columns.Add("GETMAN", mainOrder.getOrderReceiverName(), Types.VARCHAR);//取货人/收货人
		columns.Add("GETMANTEL", mainOrder.getOrderReceiverMobile(), Types.VARCHAR);//取货人电话
		//取货人Email GETMANEMAIL


		columns.Add("CONTMAN", mainOrder.getOrderReceiverName(), Types.VARCHAR);//联系人/订购人 CONTMAN
		columns.Add("CONTTEL", mainOrder.getOrderReceiverMobile(), Types.VARCHAR);//联系电话/订购人 CONTTEL
		
		columns.Add("DELIVERYNO", mainOrder.getShippingOrderCode(), Types.VARCHAR);//物流单号 DELIVERYNO
		
		//物流类型(虾皮用到)		DELIVERYTYPE
		//0、无 1 自配送 2 顺丰 3百度 4达达 5 人人 6 闪送 7.7-11超商 8.全家超商 9.黑猫宅急便 10.莱而富超商 11.OK超商 12.mingjie大件物流 
		//13.中华邮政 14.卖家宅配 15.新竹物流 16.绿界7-11超商 17.绿界全家超商 18.绿界莱而富超商 19.绿界OK超商
		String deliverytype="";

		String OrderReceiverStoreId=mainOrder.getOrderReceiverStoreId();
		String OrderReceiverStoreName=mainOrder.getOrderReceiverStoreName();
		if(OrderReceiverStoreId==null||NinetyOneUtils.isEmpty(OrderReceiverStoreId.trim())){
			OrderReceiverStoreId=shopId;
		}
		if(OrderReceiverStoreName==null||NinetyOneUtils.isEmpty(OrderReceiverStoreName.trim())){
			OrderReceiverStoreName=shopName;
		}
		//配送门店/超商取货门店		SHIPPINGSHOP
		//配送门店名称		SHIPPINGSHOPNAME
		
		
		//配送方式
		//-宅配(含離島宅配) :Home
		//-超商取貨付款 : StoreCashOnDelivery
		//-付款後超商取貨 :StorePickup
		//-付款後門市自取 : LocationPickup
		//-貨到付款： CashOnDelivery
		//-海外宅配： Oversea
		String orderDeliverType=mainOrder.getOrderDeliverType();
		String pickupWay="";//取货方式 1：超取 2：宅配
		if ("Home".equals(orderDeliverType)||"CashOnDelivery".equals(orderDeliverType)||"Oversea".equals(orderDeliverType)) 
		{
			pickupWay="2";
			deliverytype="14";//卖家宅配
		}
		else 
		{
			//OrderReceiverStoreId string(20) 002941 取貨的門市序號(超取、門市自取) 91
			//OrderReceiverStoreName string(30) 全家太原店 取貨的門市名稱(超取、門市自取) 91
			
			pickupWay="1";
			String distributorDef=mainOrder.getDistributorDef();//通路商  -全家： Family  -7-11： SevenEleven
			if ("Family".equals(distributorDef)) 
			{
				deliverytype="8";//8.全家超商
			}
			else if ("SevenEleven".equals(distributorDef)) 
			{
				deliverytype="7";//7.7-11超商
			}
			else 
			{
				deliverytype="";
			}				

		}
		columns.Add("SHIPPINGSHOP", shopId, Types.VARCHAR);//我们系统里的门店
		columns.Add("SHIPPINGSHOPNAME", shopName, Types.VARCHAR);
		
		columns.Add("DELIVERYTYPE", deliverytype, Types.VARCHAR);//物流类型(虾皮用到) DELIVERYTYPE
		columns.Add("PICKUPWAY", pickupWay, Types.VARCHAR);//取货方式 PICKUPWAY 1：超取 2：宅配

		String shipType="";
		if ("2".equals(pickupWay)) 
		{
			shipType="2";
		}
		else 
		{
			shipType="6";
		}
		columns.Add("SHIPTYPE", shipType, Types.VARCHAR);//配送方式 SHIPTYPE
		
		//订单原价 TOT_OLDAMT
		BigDecimal totOldAmt=new BigDecimal(0);
		//订单金额 TOT_AMT 用户实际支付金额
		BigDecimal totAmt=new BigDecimal(0);
		//订单优惠总额 TOT_DISC
		BigDecimal totDisc=new BigDecimal(0);
		//总数量 TOT_QTY
		BigDecimal totQty=new BigDecimal(0);
		

		//实际配送费		SHIPFEE
		BigDecimal shipFee=new BigDecimal(0);
		//配送费减免		RSHIPFEE
		BigDecimal rshipfee=new BigDecimal(0);
		//总配送费		TOTSHIPFEE
		BigDecimal totShipFee=new BigDecimal(0);
		//91主單(TM)運費
		String TMShippingFee=salesOrderDetailList.get(0).getTMShippingFee();
		if(NinetyOneUtils.isNotEmpty(TMShippingFee)){
			shipFee=new BigDecimal(TMShippingFee.trim());
			totShipFee=new BigDecimal(TMShippingFee.trim());
		}
		columns.Add("SHIPFEE", shipFee, Types.DECIMAL);//实际配送费		SHIPFEE
		columns.Add("RSHIPFEE", rshipfee, Types.DECIMAL);//配送费减免		RSHIPFEE
		columns.Add("TOTSHIPFEE", totShipFee, Types.DECIMAL);//总配送费		TOTSHIPFEE
		String memo=mainOrder.getOrderMemo();
		
		List<String> isReturnableList=new ArrayList<String>();
		//91點數折抵金額
		BigDecimal LoyaltyPointDiscount =new BigDecimal(0);
		//异常商品列表  即OuterId为空
		List<String> sExceptionGoodsList=new ArrayList<String>();
		for(NOSalesOrderDetailReq.SalesOrderDetail detail:salesOrderDetailList)
		{
			//91APP IsReturnable商品是否可退貨 - true：可退貨（預設值） - false：不可退貨
			String isReturnable=detail.getOrderMemo();
			if("false".equals(isReturnable)){
				isReturnableList.add(detail.getOuterId());
			}
			//商品总金额
			String totalPriceDetail=detail.getTotalPrice();
			if(NinetyOneUtils.isNotEmpty(totalPriceDetail)){
				totOldAmt=totOldAmt.add(new BigDecimal(totalPriceDetail));
			}
			//订单实际付款金额
			String totalPaymentDetail=detail.getTotalPayment();
			if(NinetyOneUtils.isNotEmpty(totalPaymentDetail)){
				totAmt=totAmt.add(new BigDecimal(totalPaymentDetail));
			}
			//商品数量
			String qty=detail.getQty();
			if(NinetyOneUtils.isNotEmpty(qty)){
				totQty=totQty.add(new BigDecimal(qty));
			}
			//91點數折抵金額
			if(NinetyOneUtils.isNotEmpty(detail.getLoyaltyPointDiscount())){
				//绝对值
				LoyaltyPointDiscount=LoyaltyPointDiscount.add(new BigDecimal(detail.getLoyaltyPointDiscount().trim()).abs());
			} 
			//91訂單總折扣金額
			BigDecimal totalDiscountDetail =new BigDecimal(0);
			if(NinetyOneUtils.isNotEmpty(detail.getTotalDiscount())){
				//绝对值
				totalDiscountDetail=new BigDecimal(detail.getTotalDiscount().trim()).abs();
			} 
			totDisc=totDisc.add(totalDiscountDetail);
			
			//商品料号(外部)
			String OuterId=detail.getOuterId();
			if(NinetyOneUtils.isEmpty(OuterId)){
				sExceptionGoodsList.add(detail.getSkuId()+"__("+detail.getSkuName()+")");
			}
		}
		//异常商品
		String sExceptionStatus="N";
		String sExceptionMemo="";
		if(sExceptionGoodsList!=null&&sExceptionGoodsList.size()>0){
			sExceptionStatus="Y";
			sExceptionMemo=String.join(",\r\n", sExceptionGoodsList);
		}
		//商品异常标记		EXCEPTIONSTATUS
		//异常商品备注信息		EXCEPTIONMEMO
		columns.Add("EXCEPTIONSTATUS", sExceptionStatus, Types.VARCHAR);//
		columns.Add("EXCEPTIONMEMO", sExceptionMemo, Types.VARCHAR);//
		
		
		//将不可退货商品记录到备注中
		if(isReturnableList!=null&&isReturnableList.size()>0){
			if(NinetyOneUtils.isNotEmpty(memo)){
				memo=memo+"\r\n";
			}
			memo=memo+"91APP平台记录:商品["+String.join("、", isReturnableList)+"]不可退货";
		}
		if(memo!=null&&memo.length()>254){
			memo=memo.substring(0,254);
		}
		columns.Add("MEMO", memo, Types.VARCHAR);//订单备注 MEMO
		
		totOldAmt=totOldAmt.add(LoyaltyPointDiscount);
		totDisc=totDisc.add(LoyaltyPointDiscount);
		
		//（订单中心）订单总金额（订单原价）=加总（91商品總金額）+91主單(TM)運費+加总（|91點數折抵金額|）
		//（订单中心）订单金额（用户实际支付金额）=加总（91訂單實際付款金額）+主單(TM)運費=89+70=159
		//（订单中心）订单优惠总额=加总（|91訂單總折扣金額|）+|點數折抵金額|=11+7=18
		//（订单中心）总配送费=（订单中心）实际配送费=91主單(TM)運費=70

		totOldAmt=totOldAmt.add(totShipFee);
		totAmt=totAmt.add(totShipFee);
		columns.Add("TOT_OLDAMT", totOldAmt, Types.DECIMAL);//订单原价 TOT_OLDAMT
		columns.Add("TOT_AMT", totAmt, Types.DECIMAL);//订单金额 TOT_AMT
		columns.Add("INCOMEAMT", totAmt, Types.DECIMAL);//商家实收金额 INCOMEAMT 店铺实际本单收入，订单总额扣除服务费、商户补贴金额
		columns.Add("TOT_DISC", totDisc, Types.DECIMAL);//订单优惠总额 TOT_DISC
				

		columns.Add("PLATFORM_DISC", LoyaltyPointDiscount, Types.DECIMAL);//平台优惠总额 PLATFORM_DISC
		columns.Add("SHOPSHARESHIPFEE", zero, Types.DECIMAL);//商家替用户承担的配送费 SHOPSHARESHIPFEE
		columns.Add("SERVICECHARGE", zero, Types.DECIMAL);//服务费 SERVICECHARGE
		columns.Add("ERASE_AMT", zero, Types.DECIMAL);//抹零 ERASE_AMT
		columns.Add("SELLER_DISC", zero, Types.DECIMAL);//商户优惠总额 SELLER_DISC

		
		//付款方式
		//-信用卡一次付款： CreditCardOnce
		//-信用卡分期付款： CreditCardInstallment
		//-超商取货付款： StorePay
		//-ATM 付款： ATM
		//-货到付款： CashOnDelivery
		//- LINE Pay： LinePay
		//- 街口支付： JKOPay
		String orderPayType=mainOrder.getOrderPayType();
		//支付状态/货到付款标记  PAYSTATUS  1.未支付 2.部分支付 3.付清
		String payStatus="";
		//已付金额  PAYAMT NUMBER(23,8)  用户已支付金额
		BigDecimal payAmt=BigDecimal.ZERO;
		//91APP
		String totalPayment=mainOrder.getTotalPayment();
		//orderStatus 待付款： WaitingToPay
		if("WaitingToPay".equals(orderStatus)){
			payStatus="1";
			payAmt=BigDecimal.ZERO;
		}
		//超商取貨付款： StorePay；貨到付款： CashOnDelivery
		else if ("StorePay".equals(orderPayType) && "CashOnDelivery".equals(orderPayType)) 
		{
			payStatus="1";
			payAmt=BigDecimal.ZERO;
		}
		else 
		{
			payStatus="3";
			if(NinetyOneUtils.isNotEmpty(totalPayment)){
				payAmt=new BigDecimal(totalPayment);
			}
		}
		columns.Add("PAYSTATUS", payStatus, Types.VARCHAR);//支付状态/货到付款标记 PAYSTATUS
		columns.Add("PAYAMT", payAmt, Types.DECIMAL);//已付金额  PAYAMT
		
		//温层代码  TEMPERATELAYERNO INT  1.常溫 2.冷藏 3.冷凍
		//温层名称  TEMPERATELAYERNAME	 NVARCHAR2(20)  常溫

		int temperateLayerNo=1;
		String temperateLayerName="";
		//91温层类别 -常温： Normal -冷藏： Refrigerator -冷冻： Freezer
		String temperatureTypeDef=mainOrder.getTemperatureTypeDef();
		if("Normal".equals(temperatureTypeDef)){
			temperateLayerNo=1;
			temperateLayerName="常温";
		}else if("Refrigerator".equals(temperatureTypeDef)){
			temperateLayerNo=2;
			temperateLayerName="冷藏";
		}else if("Freezer".equals(temperatureTypeDef)){
			temperateLayerNo=3;
			temperateLayerName="冷凍";
		}
		columns.Add("TEMPERATELAYERNO", temperateLayerNo, Types.INTEGER);//温层代码
		columns.Add("TEMPERATELAYERNAME", temperateLayerName, Types.VARCHAR);//温层名称
		

//		退货原因代码		REFUNDREASONNO
//		退货编号		RETURNSN
//		退货人名称		RETURNUSERNAME
//		退货通知EMAIL		RETURNEMAIL
//		退货图片地址		RETURNIMAGEURL
		
		//91 CancelOrderSlaveCauseDef 取消代號:
		//1 價格比較貴 2 衝動購買 3 不想等太久 4 其他 5 商店取消 6 消費者未取貨 7 門市閉店 8 訂單繳費過期
		//91 CancelOrderSlaveCauseDefDesc 取消原因 string(200)
		columns.Add("REFUNDREASON", mainOrder.getCancelOrderSlaveCauseDefDesc(), Types.VARCHAR);//退货原因 REFUNDREASON NVARCHAR2(255)
		
		//超商的门店
		columns.Add("ORDERSHOP", OrderReceiverStoreId, Types.VARCHAR);
		columns.Add("ORDERSHOPNAME", OrderReceiverStoreName, Types.VARCHAR);

		String[] columns1 = columns.Columns.toArray(new String[0]);
		DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
		InsBean ib1 = new InsBean("OC_ORDER", columns1);
		ib1.addValues(insValue1);
		return ib1;
	}
	
	/**
	 * 订单单身(商品明细)
	 * @throws Exception
	 */
	public List<DataProcessBean> getTvOrderDetail(String eId,String shopId,List<NOSalesOrderDetailReq.SalesOrderDetail> salesOrderDetailList) throws Exception{
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
		int i=0;
		for(NOSalesOrderDetailReq.SalesOrderDetail detail:salesOrderDetailList){
			i++;
			String orderNo=detail.getTMCode();
			ColumnDataValue columns = new ColumnDataValue();
			columns.Add("EID", eId, Types.VARCHAR); //企业编号
			columns.Add("CUSTOMERNO", " ", Types.VARCHAR); //客户编号
			columns.Add("ORGANIZATIONNO", shopId, Types.VARCHAR); //组织编号	Y	ORGANIZATIONNO
			columns.Add("SHOPID", shopId, Types.VARCHAR); //门店编号	Y	SHOPID
			columns.Add("ORDER_SN", detail.getTSCode(), Types.VARCHAR); //电商交易流水号		ORDER_SN
			columns.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号	Y	ORDERNO
			columns.Add("ITEM", i, Types.INTEGER); //项次	Y	ITEM
			String pluNo="";
			if(NinetyOneUtils.isNotEmpty(detail.getOuterId())){
				pluNo=detail.getOuterId();
			}else{
				pluNo="外部平台OuterId异常";
			}
			
			String pluName="";
			String pluNoSql="SELECT * FROM DCP_GOODS WHERE EID='"+eId+"' AND PLUNO='"+pluNo+"'";
			List<Map<String, Object>> pluNoList = StaticInfo.dao.executeQuerySQL(pluNoSql, null);
			if(pluNoList!=null&&pluNoList.size()>0){
				Map<String, Object> pluNoMap=pluNoList.get(0);
				pluName=pluNoMap.get("PLUNAME")==null?"":pluNoMap.get("PLUNAME").toString();
			}
			
			//商品名称		PLUNAME	NVARCHAR2(120)		
			columns.Add("PLUNAME", pluName, Types.VARCHAR);
			columns.Add("PLUNO", pluNo, Types.VARCHAR); //商品编号		PLUNO
			columns.Add("ATTRNAME", detail.getSkuName(), Types.VARCHAR); //属性名称		ATTRNAME
			columns.Add("ECPLUNO", detail.getSkuId(), Types.VARCHAR); //电商品号		ECPLUNO
			columns.Add("LOAD_DOCTYPE", loadDoctype, Types.VARCHAR); //来源类型	Y	LOAD_DOCTYPE
			columns.Add("STATUS", "100", Types.VARCHAR); //状态	
			columns.Add("TRAN_TIME", detail.getOrderStatusUpdatedDateTime(), Types.VARCHAR); //时间标记		TRAN_TIME	NVARCHAR2(20)
			
			//零售价 		PRICE	NUMBER(23,8)		商品原价
			BigDecimal price=new BigDecimal(0);
			if(NinetyOneUtils.isNotEmpty(detail.getPrice())){
				price=new BigDecimal(detail.getPrice().trim());
			}
			//数量		QTY	NUMBER(23,8)		
			BigDecimal qty=new BigDecimal(0);
			if(NinetyOneUtils.isNotEmpty(detail.getQty())){
				qty=new BigDecimal(detail.getQty().trim());
			}
			//金额		AMT	NUMBER(23,8)		金额 = 原价 * 数量 - 折扣额
			BigDecimal amt=new BigDecimal(0);
			if(NinetyOneUtils.isNotEmpty(detail.getTotalPayment())){
				amt=new BigDecimal(detail.getTotalPayment().trim());
			}
			//折扣额		DISC	NUMBER(23,8)		
			BigDecimal disc=new BigDecimal(0);
			if(NinetyOneUtils.isNotEmpty(detail.getTotalDiscount())){
				disc=new BigDecimal(detail.getTotalDiscount().trim()).abs();
			}
			//点数抵扣金额
			BigDecimal LoyaltyPointDiscount =new BigDecimal(0);
			if(NinetyOneUtils.isNotEmpty(detail.getLoyaltyPointDiscount())){
				//绝对值
				LoyaltyPointDiscount=new BigDecimal(detail.getLoyaltyPointDiscount().trim()).abs();
			}
			amt=amt.add(LoyaltyPointDiscount);
			disc=disc.add(LoyaltyPointDiscount);
			columns.Add("PRICE", price, Types.DECIMAL);
			columns.Add("QTY", qty, Types.DECIMAL);
			columns.Add("AMT", amt, Types.DECIMAL);
			columns.Add("DISC", disc, Types.DECIMAL);
			
//			商品条码		PLUBARCODE	NVARCHAR2(40)
//			规格名称 		SPECNAME	NVARCHAR2(120)		大杯
//			单位		UNIT	NVARCHAR2(10)		
//			组别		GOODSGROUP	NVARCHAR2(10)	0	商品属于第几个分组
//			餐盒数量		BOXNUM 	NUMBER(23,8)		
//			餐盒价格		BOXPRICE 	NUMBER(23,8)	
//			是否有备注		ISMEMO	NVARCHAR2(1)		Y/N
//			已拆数量		RCQTY	NUMBER(23,8)		
//			套餐类型		PACKAGETYPE	NVARCHAR2(10)		1、正常商品 2、套餐主商品  3、套餐子商品
//			套餐主商品项次		PACKAGEMITEM	INT		
//			加料类型		TOPPINGTYPE	NVARCHAR2(10)		1、正常商品 2、加料主商品  3、加料子商品
//			加料主商品项次		TOPPINGMITEM	INT		
//			提货劵劵种		COUPONTYPE	NVARCHAR2(100)		
//			提货劵劵号		COUPONCODE	NVARCHAR2(100)		
//			门店生产		SHOPQTY	NUMBER(23,8)		
//			已经提货数量		RQTY	NUMBER(23,8)
			String[] columns1 = columns.Columns.toArray(new String[0]);
			DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
			InsBean ib1 = new InsBean("OC_ORDER_DETAIL", columns1);
			ib1.addValues(insValue1);
			lstData.add(new DataProcessBean(ib1));
		}
		
		
		return lstData;
	}
	
	/**订单折扣表
	 * @return
	 * @throws Exception
	 */
	public List<DataProcessBean> getTvOrderAgioList(String eId,String shopId,List<NOSalesOrderDetailReq.SalesOrderDetail> salesOrderDetailList) throws Exception{
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
		String orderNo=salesOrderDetailList.get(0).getTMCode();
		//折扣活動折扣金額
		BigDecimal promotionDiscount =new BigDecimal(0);
		//折價券折扣金額
		BigDecimal eCouponDiscount=new BigDecimal(0);
		//LoyaltyPointDiscount  點數折抵金額 -7
		BigDecimal loyaltyPointDiscount=new BigDecimal(0);
		for(NOSalesOrderDetailReq.SalesOrderDetail detail:salesOrderDetailList){
			//PromotionDiscount 折扣活動折扣金額 -6	
			if(NinetyOneUtils.isNotEmpty(detail.getPromotionDiscount())){
				promotionDiscount=promotionDiscount.add(new BigDecimal(detail.getPromotionDiscount().trim()).abs());
			}
			//ECouponDiscount 折價券折扣金額 -5
			if(NinetyOneUtils.isNotEmpty(detail.getECouponDiscount())){
				//绝对值
				eCouponDiscount=eCouponDiscount.add(new BigDecimal(detail.getECouponDiscount().trim()).abs());
			}
			//LoyaltyPointDiscount  點數折抵金額 -7
			if(NinetyOneUtils.isNotEmpty(detail.getLoyaltyPointDiscount())){
				//绝对值
				loyaltyPointDiscount=loyaltyPointDiscount.add(new BigDecimal(detail.getLoyaltyPointDiscount().trim()).abs());
			}
		}
		
		String tranTime=salesOrderDetailList.get(0).getOrderStatusUpdatedDateTime();
		if(promotionDiscount.compareTo(BigDecimal.ZERO)>0){
			lstData.add(getTvOrderAgio(eId, shopId, tranTime, orderNo, lstData.size(), "折扣活动", promotionDiscount));
		}
		if(eCouponDiscount.compareTo(BigDecimal.ZERO)>0){
			lstData.add(getTvOrderAgio(eId, shopId, tranTime, orderNo, lstData.size(), "折价券", eCouponDiscount));
		}
		if(loyaltyPointDiscount.compareTo(BigDecimal.ZERO)>0){
			lstData.add(getTvOrderAgio(eId, shopId, tranTime, orderNo, lstData.size(), "点数折抵", loyaltyPointDiscount));
		}
		return lstData;
	}
	
	/**
	 * 折扣信息
	 * @throws Exception
	 */
	public DataProcessBean getTvOrderAgio(String eId,String shopId,String tranTime,String orderNo,
			int size,String promName,BigDecimal agioAmt) throws Exception{
		SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf4 = new SimpleDateFormat("HHmmss");
		ColumnDataValue columns = new ColumnDataValue();
		columns.Add("EID", eId, Types.VARCHAR); //企业编号
		columns.Add("CUSTOMERNO", " ", Types.VARCHAR); //客户编号
		columns.Add("ORGANIZATIONNO", shopId, Types.VARCHAR); //组织编号	Y	ORGANIZATIONNO
		columns.Add("SHOPID", shopId, Types.VARCHAR); //门店编号	Y	SHOPID
		columns.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号	Y	ORDERNO
		columns.Add("ITEM", size+1, Types.INTEGER); //项次	Y	ITEM
		columns.Add("LOAD_DOCTYPE", loadDoctype, Types.VARCHAR); //来源类型	Y	LOAD_DOCTYPE
		columns.Add("STATUS", "100", Types.VARCHAR); //状态	
		columns.Add("TRAN_TIME", tranTime, Types.VARCHAR); //时间标记		TRAN_TIME	NVARCHAR2(20)
		Date dateNow=new Date();
		columns.Add("SDATE", sdf3.format(dateNow), Types.VARCHAR);//系统日期
		columns.Add("STIME", sdf4.format(dateNow), Types.VARCHAR);//系统时间
		//活动名称		PROMNAME	NVARCHAR2(255)		例：满30减10元
		columns.Add("PROMNAME", promName, Types.VARCHAR);
		//折扣金额		AGIOAMT	NUMBER(23,8)
		columns.Add("AGIOAMT", agioAmt, Types.DECIMAL);
		
		String[] columns1 = columns.Columns.toArray(new String[0]);
		DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
		InsBean ib1 = new InsBean("OC_ORDER_AGIO", columns1);
		ib1.addValues(insValue1);
		return new DataProcessBean(ib1);
	}
	
	
	/**订单支付
	 * @return
	 * @throws Exception
	 */
	public List<DataProcessBean> getTvOrderPayList(String eId,String shopId,List<NOSalesOrderDetailReq.SalesOrderDetail> salesOrderDetailList) throws Exception{
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
		String orderNo=salesOrderDetailList.get(0).getTMCode();
		String tranTime=salesOrderDetailList.get(0).getOrderStatusUpdatedDateTime();
		BigDecimal totalPayment=new BigDecimal(0);
		for(NOSalesOrderDetailReq.SalesOrderDetail detail:salesOrderDetailList){
			//订单实际付款金额
			String totalPaymentDetail=detail.getTotalPayment();
			if(NinetyOneUtils.isNotEmpty(totalPaymentDetail)){
				totalPayment=totalPayment.add(new BigDecimal(totalPaymentDetail));
			}
		}
		//91主單(TM)運費
		BigDecimal totShipFee=new BigDecimal(0);
		String TMShippingFee=salesOrderDetailList.get(0).getTMShippingFee();
		if(NinetyOneUtils.isNotEmpty(TMShippingFee)){
			totShipFee=new BigDecimal(TMShippingFee.trim());
		}
		//（订单中心）订单金额（用户实际支付金额）=加总（91訂單實際付款金額）+主單(TM)運費=89+70=159
		BigDecimal totAmt=totalPayment.add(totShipFee);
		
		lstData.add(getTvOrderPay(eId, shopId, tranTime, orderNo, lstData.size(), "91平台支付", totAmt));
		return lstData;
	}
	
	public DataProcessBean getTvOrderPay(String eId,String shopId,String tranTime,String orderNo,
			int size,String payName,BigDecimal pay) throws Exception{
		ColumnDataValue columns = new ColumnDataValue();
		columns.Add("EID", eId, Types.VARCHAR); //企业编号
		columns.Add("CUSTOMERNO", " ", Types.VARCHAR); //客户编号
		columns.Add("ORGANIZATIONNO", shopId, Types.VARCHAR); //组织编号	Y	ORGANIZATIONNO
		columns.Add("SHOPID", shopId, Types.VARCHAR); //门店编号	Y	SHOPID
		columns.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号	Y	ORDERNO
		columns.Add("ITEM", size+1, Types.INTEGER); //项次	Y	ITEM
		columns.Add("PAYNAME", payName, Types.VARCHAR); //付款名称		PAYNAME
		columns.Add("PAY", pay, Types.DECIMAL); //金额		PAY
		columns.Add("STATUS", "100", Types.VARCHAR);//状态	
		columns.Add("LOAD_DOCTYPE", loadDoctype, Types.VARCHAR);//
		columns.Add("ISONLINEPAY", "Y", Types.VARCHAR);//是否平台支付		ISONLINEPAY
		columns.Add("TRAN_TIME", tranTime, Types.VARCHAR);//时间标记		TRAN_TIME
		
//		支付方式(小类)		PAYCODE  
//		支付类型(大类)		PAYCODEERP
		
		String[] columns1 = columns.Columns.toArray(new String[0]);
		DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
		InsBean ib1 = new InsBean("OC_ORDER_PAY", columns1);
		ib1.addValues(insValue1);
		
		return new DataProcessBean(ib1);
	}
	
	/**
	 * 订单历程表(日志)
	 * @return
	 * @throws Exception
	 */
	public List<DataProcessBean> getTvOrderStatusLogList(String eId,String shopId,NOSalesOrderDetailReq.SalesOrderDetail mainOrder) throws Exception{
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
		String orderNo=mainOrder.getTMCode();
		//91APP订单状态
		String orderStatus=mainOrder.getOrderStatus();
		String tranTime=mainOrder.getOrderStatusUpdatedDateTime();
		Map<String, Object> statusMap=getStatusMap(orderStatus);
		
		String statusType=statusMap.get("statusType").toString();
		String statusTypeName=statusMap.get("statusTypeName").toString();
		String status=statusMap.get("status").toString();
		String statusName=statusMap.get("statusName").toString();
		//91APP訂單狀態
		//- 已取消： Cancel - 已完成： Finish
		//- 付款確認中： WaitingToCreditCheck
		//- 已成立： WaitingToShipping
		//- 已確認待出貨： ConfirmedToShipping
		//- 待付款： WaitingToPay
		
		//订单中心订单状态
		//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 
		//9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
		lstData.add(getTvOrderStatusLog(eId, shopId, tranTime, orderNo, statusType, statusTypeName, status, statusName));
		return lstData;
	}
	/**
	 * 订单历程表(日志)
	 * @return
	 * @throws Exception
	 */
	public DataProcessBean getTvOrderStatusLog(String eId,String shopId,String tranTime,String orderNo,
			String type,String typeName,String status,String statusName) throws Exception{
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date dateNow=new Date();
		ColumnDataValue columns = new ColumnDataValue();
		columns.Add("EID", eId, Types.VARCHAR); //企业编号
		columns.Add("CUSTOMERNO", " ", Types.VARCHAR); //客户编号
		columns.Add("ORGANIZATIONNO", shopId, Types.VARCHAR); //组织编号	Y	ORGANIZATIONNO
		columns.Add("SHOPID", shopId, Types.VARCHAR); //门店编号	Y	SHOPID
		columns.Add("ORDERNO", orderNo, Types.VARCHAR); //订单编号	Y	ORDERNO
		columns.Add("LOAD_DOCTYPE", loadDoctype, Types.VARCHAR);//
		
		
		columns.Add("OPNO", "admin", Types.VARCHAR); //订单编号	Y	ORDERNO
		columns.Add("OPNAME", "管理員", Types.VARCHAR);//
		columns.Add("NEED_NOTIFY", "N", Types.VARCHAR); //NEED_NOTIFY 是否通知云pos,N-不需要调用，Y-需要
		columns.Add("NOTIFY_STATUS", "0", Types.VARCHAR);//NOTIFY_STATUS 通知云pos状态返回，0-未通知，1-已通知
		columns.Add("NEED_CALLBACK", "N", Types.VARCHAR);//NEED_CALLBACK 是否调用第三方接口，N-不需要调用，Y-需要
		columns.Add("CALLBACK_STATUS", "0", Types.VARCHAR);//CALLBACK_STATUS 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
		columns.Add("STATUS", "100", Types.VARCHAR);
		columns.Add("TRAN_TIME", tranTime, Types.VARCHAR);//TRAN_TIME
		columns.Add("UPDATE_TIME", sdf5.format(dateNow), Types.VARCHAR);//UPDATE_TIME  yyyyMMddHHmmssSSS
		
		columns.Add("STATUSTYPE", type, Types.VARCHAR);//STATUSTYPE 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
		columns.Add("STATUSTYPENAME", typeName, Types.VARCHAR);//STATUSTYPENAME 状态类型名称
		columns.Add("STATUS", status, Types.VARCHAR);//STATUSTYPENAME 状态类型名称
		//STATUS  状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 
		//8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
		columns.Add("STATUSNAME", statusName, Types.VARCHAR);//STATUSNAME 状态名称
		String memo = typeName + "-->" + statusName;
		columns.Add("MEMO", memo, Types.VARCHAR);//MEMO 類型名稱+"-->"+狀態名稱
		
		String[] columns1 = columns.Columns.toArray(new String[0]);
		DataValue[] insValue1 = columns.DataValues.toArray(new DataValue[0]);
		InsBean ib1 = new InsBean("OC_ORDER_STATUSLOG", columns1);
		ib1.addValues(insValue1);
		return new DataProcessBean(ib1);
	}
	
	public Map<String, Object> getStatusMap(String orderStatus) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		// 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
		String statusType="";
		// 状态类型名称
		String statusTypeName="";
		//订单中心订单状态
		//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 
		//9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
		String status="";
		//// 状态名称
		String statusName="";
		
		//91APP訂單狀態
		//- 已取消： Cancel - 已完成： Finish
		//- 付款確認中： WaitingToCreditCheck
		//- 已成立： WaitingToShipping
		//- 已確認待出貨： ConfirmedToShipping
		//- 待付款： WaitingToPay
		if("Cancel".equals(orderStatus)){
			statusType="3";
			statusTypeName="退单状态";
			status="12";//另REFUNDSTATUS 13.取消订单成功
			statusName="取消订单成功";
		}else if("Finish".equals(orderStatus)){
			statusType="1";
			statusTypeName="订单状态";
			status="11";
			statusName="已完成";
		}else if("WaitingToCreditCheck".equals(orderStatus)){
			statusType="1";
			statusTypeName="订单状态";
			status="2";
			statusName="已接单";
		}else if("WaitingToShipping".equals(orderStatus)){
			statusType="2";
			statusTypeName="配送状态";
			status="9";//2-配送状态 9.待配送
			statusName="待配送";
		}else if("ConfirmedToShipping".equals(orderStatus)){
			statusType="2";
			statusTypeName="配送状态";
			status="10";//2-配送状态  10.已发货
			statusName="已发货";
		}else if("WaitingToPay".equals(orderStatus)){
			statusType="1";
			statusTypeName="订单状态";
			status="1";
			statusName="待付款";
		}
		map.put("statusType", statusType);
		map.put("statusTypeName", statusTypeName);
		map.put("status", status);
		map.put("statusName", statusName);
		
		return map;
	}
	
	public void writelogFileName(String log){
		try{
			NinetyOneUtils.writelogFileName(logFileName,log);
		}catch(Exception e){
			
		}
	}

}
