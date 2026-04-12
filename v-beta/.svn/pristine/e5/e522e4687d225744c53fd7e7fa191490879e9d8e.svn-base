package com.dsc.spos.scheduler.job;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
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
import com.dsc.spos.ninetyone.response.NOShippingOrderReq;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.ninetyone.util.NinetyOneUtils;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NinetyOneShippingOrder extends InitJob 
{

	public String logFileName = "NinetyOneShippingOrder";

	public String logFileNameError = "NinetyOneShippingOrderError";

	//	//redis参数，用来记录同步时间。
	//	public String redisKey = "ORDER_91APP";
	//	//用来记录上一次订单同步的时间
	//	
	//	public String hashKey1 = "ShippingOrder_";
	//91APP对应LOAD_DOCTYPE
	public String loadDoctype = "91app";

	public int pageSize = 100;

	//一天执行一次
	static boolean bFirst=true;

	static boolean bRun = false;// 标记此服务是否正在执行中

	public NinetyOneShippingOrder()
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
			//			String sql="select A.* from OC_ECOMMERCE A where LOWER(A.ECPLATFORMNO) LIKE '91app%'";
			//日志好像会记录sql  sql中带有%，导致在输出日志时报错java.util.UnknownFormatConversionException
			String sql="select A.* from OC_ECOMMERCE A "
					+ " where INSTR(LOWER(A.ECPLATFORMNO),'91app')>0"
					+ " AND A.API_URL IS NOT NULL AND A.SHOPSN IS NOT NULL AND A.SHOPID IS NOT NULL AND APPKEY IS NOT NULL";
			String[] conditionValuesBase = {};
			List<Map<String, Object>> basicList = StaticInfo.dao.executeQuerySQL(sql, conditionValuesBase);
			if (basicList != null && basicList.size()>0 ) {
				NinetyOneClient nc=new NinetyOneClient();
				for(Map<String, Object> basicMap:basicList){
					//EID	企业编号
					String eId=NinetyOneUtils.getDefaultValueStr(basicMap.get("EID"));
					//API_URL	API地址
					String mainUrl=NinetyOneUtils.getDefaultValueStr(basicMap.get("API_URL"));
					//SHOPSN	店铺编号 91APP店铺编号
					long storeId=NinetyOneUtils.isEmpty(basicMap.get("SHOPSN"))?0:Long.valueOf(basicMap.get("SHOPSN").toString().trim()).longValue();
					//SHOPID	门店  记录91APP订单同步到中台后归属于哪个门店
					String shopId=NinetyOneUtils.getDefaultValueStr(basicMap.get("SHOPID"));
					//APPKEY	APIKEY 存放91APP x—api-key
					String xApiKey=NinetyOneUtils.getDefaultValueStr(basicMap.get("APPKEY"));

					doTransport(nc, mainUrl, xApiKey, storeId, eId, shopId);
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
			String shopId) throws Exception{

		String sqlOrder="SELECT A.*,B.ORDER_SN AS BORDERSN "
				+ " FROM OC_ORDER A "
				+ " LEFT JOIN OC_ORDER_DETAIL B "
				+ " ON A.EID=B.EID AND A.LOAD_DOCTYPE=B.LOAD_DOCTYPE AND A.ORDERNO=B.ORDERNO "
				+ " WHERE A.EID=? and A.LOAD_DOCTYPE=? and A.STATUS!='3' and A.STATUS!='5' and A.STATUS!='11' and A.STATUS!='12' and A.DELIVERYNO IS NOT NULL ";
		String[] conditionValues=new String[]{eId,loadDoctype};
		List<Map<String, Object>> listSqlOrder=this.doQueryData(sqlOrder, conditionValues);
		if(listSqlOrder!=null&&listSqlOrder.size()>0){
			doTransportLoop(nc, mainUrl, xApiKey, storeId, eId,shopId, listSqlOrder);
		}

	}

	public void doTransportLoop(NinetyOneClient nc,String mainUrl,String xApiKey,long storeId,String eId,
			String shopId,List<Map<String, Object>> listSqlOrder) throws Exception{
		for(Map<String, Object> orderSNMap:listSqlOrder){
			if(NinetyOneUtils.isEmpty(orderSNMap.get("BORDERSN"))){
				continue;
			}
			String orderNo=orderSNMap.get("ORDERNO")==null?"":orderSNMap.get("ORDERNO").toString().trim();
			String sqlOrder="SELECT * FROM OC_ORDER WHERE EID=? and LOAD_DOCTYPE=? and ORDERNO=? ";
			String[] conditionValues=new String[]{eId,loadDoctype,orderNo};
			List<Map<String, Object>> orderList=this.doQueryData(sqlOrder, conditionValues);
			if(orderList!=null&&orderList.size()>0){
				orderSNMap.putAll(orderList.get(0));
			}

			//订单号
			String orderSN=orderSNMap.get("BORDERSN").toString().trim();
			String detailTranTime=orderSNMap.get("TRAN_TIME")==null?"":orderSNMap.get("TRAN_TIME").toString().trim();
			String deliveryNo=orderSNMap.get("DELIVERYNO")==null?"":orderSNMap.get("DELIVERYNO").toString().trim();
			Map<String, Object> detailParams=getShippingOrderParams(orderSN,storeId,deliveryNo);
			NOShippingOrderReq.ShippingOrderData orderData=getShippingOrderData(nc.getShippingOrder(mainUrl, xApiKey, detailParams));
			if(orderData!=null){
				//出貨狀態最後更新日
				String tranTime=orderData.getShippingOrderStatusUpdatedDateTime();
				if(detailTranTime.equals(tranTime)){
					continue;
				}
				doUpdateOrder(eId, shopId, orderSNMap, orderData);
			}
			else{
				writelogFileName("\r\n***************订单号[TSCode]:"+orderSN+"未获取到配送资料,参数:url-"+mainUrl+"xApiKey-"+xApiKey + "detailParams-"+detailParams+"****************\r\n");
			}
		}
	}


	/**
	 * 更新单据
	 * @param nc
	 * @param params
	 * @throws Exception
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void doUpdateOrder(String eId,String shopId,Map<String, Object> orderSNMap,NOShippingOrderReq.ShippingOrderData orderData) throws Exception{
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date dateNow=new Date();
		//列表SQL
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
		//主订单号
		String orderNo=orderData.getTMCode();
		//订单号 对应单身 ORDER_SN
		String orderSN=orderSNMap.get("BORDERSN").toString().trim();
		String tranTime=orderData.getShippingOrderStatusUpdatedDateTime();

		//ShippingOrderStatus string(30) Finish 出貨單狀態
		//-Finish : 已出貨至消費者  
		//**超商付款取貨 / 超商純取貨專用貨態
		//-AllocatedCode : 已配號                                                         -VerifySuccess : 超商驗收成功
		//-VerifyFailLost : 超商驗收失敗.商品遺失                              -VerifyFailAbnormalPackage :超商 驗收失敗.包裝異常
		//-VerifyFailRenovation : 超商驗收失敗.門市閉店/整修         -VerifyFailErrorCode : 超商驗收失敗.配送編號異常
		//-VerifyFailInvalIdCode : 超商驗收失敗.編號失效(未到貨) -ShippingProcessing : 出貨處理中
		//-ShippingFail : 消費者逾期未取，出貨失敗                              -ShippingArrived : 貨到門市
		//**門市自取專用貨態
		//-AllocatedCode : 已配號                                                          -ShippingProcessing : 出貨處理中
		//-ShippingFail : 消費者逾期未取，出貨失敗(逾期未取)      -ShippingArrived : 貨到門市
		//**貨到付款專用貨態
		//-AllocatedCode 已配號                                                              -CashOnDeliveryTransferring 宅配轉運中
		//-CashOnDeliveryNotAtHome 宅配不在家                                   -CashOnDeliveryDistributing 宅配已配送
		//-CashOnDeliveryFailDamage 宅配異常-損壞                            -CashOnDeliveryFailLost 宅配異常-遺失
		//-CashOnDeliveryFail 宅配出貨失敗                                          -CashOnDeliveryAddressError 宅配地址錯誤
		//-CashOnDeliveryForwarding 宅配轉寄配送中                           -ShippingProcessing 出貨處理中
		String ShippingOrderStatus=orderData.getShippingOrderStatus();//ShippingOrderStatus string(30) Finish 出貨單狀態

		//物流类型(虾皮用到)		DELIVERYTYPE
		//0、无 1 自配送 2 顺丰 3百度 4达达 5 人人 6 闪送 7.7-11超商 8.全家超商 9.黑猫宅急便 10.莱而富超商 11.OK超商 12.mingjie大件物流 
		//13.中华邮政 14.卖家宅配 15.新竹物流 16.绿界7-11超商 17.绿界全家超商 18.绿界莱而富超商 19.绿界OK超商
		String deliverytype="";
		String pickupWay="";//取货方式 1：超取 2：宅配
		//91配送方式
		//-宅配(含離島宅配) :Home
		//-超商取貨付款 : StoreCashOnDelivery
		//-付款後超商取貨 :StorePickup
		//-付款後門市自取 : LocationPickup
		//-貨到付款： CashOnDelivery
		//-海外宅配： Oversea
		String orderDeliverType=orderData.getOrderDeliverType();
		if ("Home".equals(orderDeliverType)||"CashOnDelivery".equals(orderDeliverType)||"Oversea".equals(orderDeliverType)) 
		{
			pickupWay="2";
			deliverytype="14";//卖家宅配
		}
		else 
		{
			pickupWay="1";
			String distributorDef=orderData.getDistributorDef();//通路商  -全家： Family  -7-11： SevenEleven
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

		//温层代码  TEMPERATELAYERNO INT  1.常溫 2.冷藏 3.冷凍
		//温层名称  TEMPERATELAYERNAME	 NVARCHAR2(20)  常溫
		int temperateLayerNo=0;
		String temperateLayerName="";
		//91温层类别 -常温： Normal -冷藏： Refrigerator -冷冻： Freezer
		String temperatureTypeDef=orderData.getTemperatureTypeDef();
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

		String ShippingOrderCode=orderData.getShippingOrderCode();
		String OrderReceiverName=orderData.getOrderReceiverName();//91 OrderReceiverName string(50) 趙曉明 收件人姓名
		String OrderReceiverMobile=orderData.getOrderReceiverMobile();//91 OrderReceiverMobile string(20) 0912345678 收件人電話

		Map<String, Object> statusMap=getStatusMap(ShippingOrderStatus,orderDeliverType);
		//物流状态 DELIVERYSTUTAS
		String status=statusMap.get("status").toString();
		//物流状态名称
		String statusName=statusMap.get("statusName").toString();

		//单头  更新订单状态STATUS、TRAN_TIME、UPDATE_TIME等
		UptBean ub2 = new UptBean("OC_ORDER");
		ub2.addUpdateValue("TRAN_TIME", new DataValue(tranTime, Types.VARCHAR));
		ub2.addUpdateValue("UPDATE_TIME", new DataValue(sdf5.format(dateNow), Types.VARCHAR));
		if(NinetyOneUtils.isNotEmpty(status)){
			ub2.addUpdateValue("DELIVERYSTUTAS", new DataValue(status, Types.VARCHAR));//物流状态 DELIVERYSTUTAS
		}
		if("Finish".equals(ShippingOrderStatus)){
			ub2.addUpdateValue("STATUS", new DataValue("11", Types.VARCHAR));//订单状态 STATUS 11.已完成
		}
		if(NinetyOneUtils.isNotEmpty(deliverytype)){
			ub2.addUpdateValue("DELIVERYTYPE", new DataValue(deliverytype, Types.VARCHAR));//物流类型(虾皮用到) DELIVERYTYPE
		}
		if(NinetyOneUtils.isNotEmpty(pickupWay)){
			ub2.addUpdateValue("PICKUPWAY", new DataValue(pickupWay, Types.VARCHAR));//取货方式 1：超取 2：宅配 PICKUPWAY
		}
		if(temperateLayerNo!=0){
			ub2.addUpdateValue("TEMPERATELAYERNO", new DataValue(temperateLayerNo, Types.VARCHAR));//温层代码
		}
		if(NinetyOneUtils.isNotEmpty(temperateLayerName)){
			ub2.addUpdateValue("TEMPERATELAYERNAME", new DataValue(temperateLayerName, Types.VARCHAR));//温层名称  TEMPERATELAYERNAME
		}
		if(NinetyOneUtils.isNotEmpty(ShippingOrderCode)){
			ub2.addUpdateValue("DELIVERYNO", new DataValue(ShippingOrderCode, Types.VARCHAR));//物流单号 DELIVERYNO
		}
		//收件人姓名 string(50)
		if(NinetyOneUtils.isNotEmpty(OrderReceiverName)){
			if(NinetyOneUtils.isEmpty(orderSNMap.get("GETMAN"))){
				ub2.addUpdateValue("GETMAN", new DataValue(getSubString(OrderReceiverName, 40), Types.VARCHAR));//取货人/收货人 GETMAN NVARCHAR2(40)
			}
			if(NinetyOneUtils.isEmpty(orderSNMap.get("CONTMAN"))){
				ub2.addUpdateValue("CONTMAN", new DataValue(getSubString(OrderReceiverName, 20), Types.VARCHAR));//联系人/订购人 CONTMAN	NVARCHAR2(20)
			}
		}
		//收件人電話 string(20)
		if(NinetyOneUtils.isNotEmpty(OrderReceiverMobile)){
			if(NinetyOneUtils.isEmpty(orderSNMap.get("GETMANTEL"))){
				ub2.addUpdateValue("GETMANTEL", new DataValue(OrderReceiverMobile, Types.VARCHAR));//取货人电话  GETMANTEL  NVARCHAR2(40)
			}
			if(NinetyOneUtils.isEmpty(orderSNMap.get("CONTTEL"))){
				ub2.addUpdateValue("CONTTEL", new DataValue(OrderReceiverMobile, Types.VARCHAR));//联系电话/订购人  CONTTEL  NVARCHAR2(30)
			}
		}
		if(NinetyOneUtils.isEmpty(orderSNMap.get("PROVINCE"))){
			ub2.addUpdateValue("PROVINCE", new DataValue("台湾省", Types.VARCHAR));//省  PROVINCE  NVARCHAR2(100)
		}
		if(NinetyOneUtils.isNotEmpty(orderData.getOrderReceiverCity())&&NinetyOneUtils.isEmpty(orderSNMap.get("CITY"))){
			//91 OrderReceiverCity string(10) 台北市 收件人縣市
			//市	  CITY  NVARCHAR2(100)
			ub2.addUpdateValue("CITY", new DataValue(orderData.getOrderReceiverCity(), Types.VARCHAR));

		}
		if(NinetyOneUtils.isNotEmpty(orderData.getOrderReceiverDistrict())&&NinetyOneUtils.isEmpty(orderSNMap.get("COUNTY"))){
			//91 OrderReceiverDistrict string(10) 文山區 收件人鄉鎮市區
			//区县		COUNTY	NVARCHAR2(100)
			ub2.addUpdateValue("COUNTY", new DataValue(orderData.getOrderReceiverDistrict(), Types.VARCHAR));
		}
		if(NinetyOneUtils.isNotEmpty(orderData.getOrderReceiverAddress())&&NinetyOneUtils.isEmpty(orderSNMap.get("ADDRESS"))){
			//91 OrderReceiverAddress string(200) 萬安街 200 號 收件人地址
			//配送地址  ADDRESS  NVARCHAR2(100)
			ub2.addUpdateValue("ADDRESS", new DataValue(getSubString(orderData.getOrderReceiverAddress(), 100), Types.VARCHAR));
		}

		//condition
		ub2.addCondition("LOAD_DOCTYPE", new DataValue(loadDoctype, Types.VARCHAR));
		ub2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		lstData.add(new DataProcessBean(ub2));


		//单身更新TRAN_TIME、
		UptBean ub1 = new UptBean("OC_ORDER_DETAIL");
		//add Value
		ub1.addUpdateValue("TRAN_TIME", new DataValue(tranTime, Types.VARCHAR));
		//condition
		ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDoctype, Types.VARCHAR));
		ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		ub1.addCondition("ORDER_SN", new DataValue(orderSN, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		lstData.add(new DataProcessBean(ub1));
		//写入历程
		lstData.add(getTvOrderStatusLog(eId, shopId, tranTime, orderNo, "2", "配送状态", status, statusName));
		boolean isSuccess=false;
		try{
			isSuccess=StaticInfo.dao.useTransactionProcessData(lstData);
		}catch(Exception e){
			NinetyOneUtils.writelogFileName(logFileNameError,"\r\n***********单据:["+orderNo+"],ORDER_SN["+orderSN+"]保存时发生异常:"+ExceptionUtils.getRootCauseMessage(e));
		}
	}

	public Map<String, Object> getStatusMap(String ShippingOrderStatus,String orderDeliverType) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		String status="";
		// 状态名称
		String statusName="";

		//ShippingOrderStatus string(30) Finish 出貨單狀態
		//-Finish : 已出貨至消費者  
		//**超商付款取貨 / 超商純取貨專用貨態
		//-AllocatedCode : 已配號                                                         -VerifySuccess : 超商驗收成功
		//-VerifyFailLost : 超商驗收失敗.商品遺失                              -VerifyFailAbnormalPackage :超商 驗收失敗.包裝異常
		//-VerifyFailRenovation : 超商驗收失敗.門市閉店/整修         -VerifyFailErrorCode : 超商驗收失敗.配送編號異常
		//-VerifyFailInvalIdCode : 超商驗收失敗.編號失效(未到貨) -ShippingProcessing : 出貨處理中
		//-ShippingFail : 消費者逾期未取，出貨失敗                              -ShippingArrived : 貨到門市
		//**門市自取專用貨態
		//-AllocatedCode : 已配號                                                          -ShippingProcessing : 出貨處理中
		//-ShippingFail : 消費者逾期未取，出貨失敗(逾期未取)      -ShippingArrived : 貨到門市
		//**貨到付款專用貨態
		//-AllocatedCode 已配號                                                              -CashOnDeliveryTransferring 宅配轉運中
		//-CashOnDeliveryNotAtHome 宅配不在家                                   -CashOnDeliveryDistributing 宅配已配送
		//-CashOnDeliveryFailDamage 宅配異常-損壞                            -CashOnDeliveryFailLost 宅配異常-遺失
		//-CashOnDeliveryFail 宅配出貨失敗                                          -CashOnDeliveryAddressError 宅配地址錯誤
		//-CashOnDeliveryForwarding 宅配轉寄配送中                           -ShippingProcessing 出貨處理中

		//91配送方式
		//-宅配(含離島宅配) : Home
		//-超商取貨付款 : StoreCashOnDelivery
		//-付款後超商取貨 : StorePickup
		//-付款後門市自取 : LocationPickup
		//-貨到付款： CashOnDelivery

		//DELIVERYSTUTAS 1预下单  0 已下单 1 接单，2=取件，3=签收，4=物流取消或异常 5=手动撤销  6 到店 7重下单 8货到物流中心 9消费者七天未取件  
		if("Finish".equals(ShippingOrderStatus)){
			status="3";
			statusName="簽收";
		}else{
			if("AllocatedCode".equals(ShippingOrderStatus)){
				//已配號
				status="0";
				statusName="已下單";
			}
			else if("VerifySuccess".equals(ShippingOrderStatus)){
				status="6";
				statusName="超商驗收成功";
			}
			else if("VerifyFailLost".equals(ShippingOrderStatus)){
				status="4";
				statusName="超商驗收失敗.商品遺失";
			}
			else if("VerifyFailAbnormalPackage".equals(ShippingOrderStatus)){
				status="4";
				statusName="超商 驗收失敗.包裝異常";
			}
			else if("VerifyFailRenovation".equals(ShippingOrderStatus)){
				status="4";
				statusName="超商驗收失敗.門市閉店/整修";
			}
			else if("VerifyFailErrorCode".equals(ShippingOrderStatus)){
				status="4";
				statusName="超商驗收失敗.配送編號異常";
			}
			else if("VerifyFailInvalIdCode".equals(ShippingOrderStatus)){
				status="4";
				statusName="超商驗收失敗.編號失效(未到貨)";
			}
			else if("ShippingProcessing".equals(ShippingOrderStatus)){
				status="1";
				statusName="出貨處理中";
			}
			else if("ShippingFail".equals(ShippingOrderStatus)){
				status="9";
				statusName="消費者逾期未取，出貨失敗";
			}
			else if("ShippingArrived".equals(ShippingOrderStatus)){
				status="8";
				statusName="貨到門市";
			}

			else if("CashOnDeliveryTransferring".equals(ShippingOrderStatus)){
				status="1";
				statusName="宅配轉運中";
			}
			else if("CashOnDeliveryNotAtHome".equals(ShippingOrderStatus)){
				status="4";
				statusName="宅配不在家";
			}
			else if("CashOnDeliveryDistributing".equals(ShippingOrderStatus)){
				status="8";
				statusName="宅配已配送";
			}
			else if("CashOnDeliveryFailDamage".equals(ShippingOrderStatus)){
				status="4";
				statusName="宅配異常-損壞";
			}
			else if("CashOnDeliveryFailLost".equals(ShippingOrderStatus)){
				status="4";
				statusName="宅配異常-遺失";
			}
			else if("CashOnDeliveryFail".equals(ShippingOrderStatus)){
				status="4";
				statusName="宅配出貨失敗";
			}
			else if("CashOnDeliveryAddressError".equals(ShippingOrderStatus)){
				status="4";
				statusName="宅配地址錯誤";
			}
			else if("CashOnDeliveryForwarding".equals(ShippingOrderStatus)){
				status="1";
				statusName="宅配轉寄配送中";
			}
			else{
				//4=物流取消或异常
				status="4";//1 接单
				statusName="物流異常或取消";
			}
		}

		map.put("status", status);
		map.put("statusName", statusName);
		return map;
	}

	/**
	 * 
	 * result转换为实体类NOShippingOrderReq
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public NOShippingOrderReq.ShippingOrderData getShippingOrderData(String result) throws Exception{
		NOShippingOrderReq.ShippingOrderData data=null;
		NOShippingOrderReq orderDetailReq=JSON.parseObject(result, NOShippingOrderReq.class);
		if(orderDetailReq!=null&&"success".equals(orderDetailReq.getStatus())){
			data= orderDetailReq.getData().getList().get(0);
		}else{
			writelogFileName("\r\n***************result转换实体类NOShippingOrderReq异常"+result+"****************\r\n");
		}
		return data;
	}

	public Map<String, Object> getShippingOrderParams(String TSCode,long storeId,String deliveryNo) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("ShippingOrderCode", deliveryNo);
		map.put("TSCode", TSCode);
		map.put("ShopId", storeId);//商店序號
		return map;
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
		columns.Add("STATUS", "100", Types.VARCHAR);//STATUS
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

	public void writelogFileName(String log){
		try{
			NinetyOneUtils.writelogFileName(logFileName,log);
		}catch(Exception e){

		}
	}

	public String getSubString(Object obj,int lenSize){
		String str="";
		if(NinetyOneUtils.isNotEmpty(obj)){
			str=obj.toString().trim();
			if(str.length()>=lenSize){
				str.substring(0, lenSize-1);
			}
		}
		return str;
	}

}
