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
import com.dsc.spos.ninetyone.response.NOReturnOrderDetailReq;
import com.dsc.spos.ninetyone.response.NOReturnOrderListReq;
import com.dsc.spos.ninetyone.util.ColumnDataValue;
import com.dsc.spos.ninetyone.util.NinetyOneUtils;
import com.dsc.spos.redis.RedisPosPub;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class NinetyOneReturnOrder extends InitJob 
{
	
	public String logFileName = "NinetyOneReturnOrder";
	
	public String logFileNameError = "NinetyOneReturnOrderError";
	
	//redis参数，用来记录同步时间。
	public String redisKey = "ORDERNOAPP";
	//用来记录上一次订单同步的时间
	
	public String hashKey1 = "ReturnOrder";
	//91APP对应LOAD_DOCTYPE
	public String loadDoctype = "91app";
	
	public int pageSize = 100;
	
	//一天执行一次
	static boolean bFirst=true;
	
	static boolean bRun = false;// 标记此服务是否正在执行中
	
	public NinetyOneReturnOrder()
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
				//当前时间减2秒
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
					Map<String, Object> params=getReturnOrderListParams(startDate,endDate,storeId);
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
		NOReturnOrderListReq.ReturnOrderListData orderListData=getReturnOrderList(nc.getReturnOrderList(mainUrl, xApiKey, params));
		
		if(orderListData!=null&&orderListData.getList()!=null&&orderListData.getList().size()>0){
			List<NOReturnOrderListReq.ReturnOrderCode> orderList=orderListData.getList();
			doTransportLoop(nc, mainUrl, xApiKey, storeId, eId, shopId,shopName, params, orderList);
			int totalCount=orderListData.getTotalCount();
			for(int i=1;i<totalCount/pageSize;i++){
				params.put("Position", i*pageSize);//Position 位置 -第一筆資料的位置為 0，第二筆的資料位置為 1，以此類推
				orderListData=getReturnOrderList(nc.getReturnOrderList(mainUrl, xApiKey, params));
				if(orderListData!=null&&orderListData.getList()!=null&&orderListData.getList().size()>0){
					doTransportLoop(nc, mainUrl, xApiKey, storeId, eId, shopId,shopName, params, orderListData.getList());
				}
			}
		}
		
		
	}
	
	public void doTransportLoop(NinetyOneClient nc,String mainUrl,String xApiKey,long storeId,String eId,
			String shopId,String shopName,Map<String, Object> params,List<NOReturnOrderListReq.ReturnOrderCode> orderList) throws Exception{
		for(NOReturnOrderListReq.ReturnOrderCode orderCode:orderList){
			//订单号
			String orderNo=orderCode.getTSCode();
			Map<String, Object> detailParams=getReturnOrderDetailParams(orderCode,storeId);
			List<NOReturnOrderDetailReq.ReturnOrderDetail> orderDetailList=getReturnOrderDetailList(nc.getReturnOrderDetail(mainUrl, xApiKey, detailParams));
			if(orderDetailList!=null&&orderDetailList.size()>0){
				//判断一下数据库是否已存在该单据，如果有直接continue掉
				String sqlOrder="SELECT * FROM OC_ORDER_DETAIL where EID=? and SHOPID=? and LOAD_DOCTYPE=? and ORDER_SN=? ";
				String[] conditionValues=new String[]{eId,shopId,loadDoctype,orderNo};
				List<Map<String, Object>> listSqlOrder=this.doQueryData(sqlOrder, conditionValues);
				if(listSqlOrder!=null&&listSqlOrder.size()>0){
					Map<String, Object> orderMap=listSqlOrder.get(0);
					//退貨單狀態日 2013-08-30T17:30:00 "yyyy-MM-dd'T'HH:mm:ss"
					String updatedDateTime=orderDetailList.get(0).getReturnGoodStatusUpdatedDateTime();
					String orderTranTime=NinetyOneUtils.getDefaultValueStr(orderMap.get("TRAN_TIME"));
					//11.已完成 12.已退单
					String orderStatus=NinetyOneUtils.getDefaultValueStr(orderMap.get("STATUS"));
					if("11".equals(orderStatus)){
						writelogFileName("\r\n***************" + orderNo+"订单已完成 ,跳过！****************\r\n");
						continue;
					}else if("12".equals(orderStatus)){
						writelogFileName("\r\n***************" + orderNo+"订单已退单 ,跳过！****************\r\n");
						continue;
					}
					if(orderTranTime.equals(updatedDateTime)){
						writelogFileName("\r\n***************单号[ORDER_SN]:" + orderNo+"退货状态已更新,跳过本次！****************\r\n");
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
						doUpdateOrder(eId, shopId, listSqlOrder.get(0), orderDetailList);
					}
				}else{
					writelogFileName("\r\n***************单号[ORDER_SN]:" + orderNo+"还未写入POS库,跳过本次！****************\r\n");
				}
			}
			else{
				writelogFileName("\r\n***************订单号[TSCode]:"+orderNo+"未获取到明细资料,参数:url-"+mainUrl+"xApiKey-"+xApiKey + "detailParams-"+detailParams+"****************\r\n");
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
	public void doUpdateOrder(String eId,String shopId,Map<String, Object> orderDetailMap,List<NOReturnOrderDetailReq.ReturnOrderDetail> orderDetailList) throws Exception{
		SimpleDateFormat sdf5 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		Date dateNow=new Date();
		//列表SQL
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();
		NOReturnOrderDetailReq.ReturnOrderDetail mainOrder=orderDetailList.get(0);
		//对应单身 ORDER_SN
		String orderSN=mainOrder.getTSCode();
		String tranTime=mainOrder.getReturnGoodStatusUpdatedDateTime();
		String returnGoodStatus=mainOrder.getReturnGoodStatus();
		//91退货数量
		BigDecimal returnGoodQty=new BigDecimal(mainOrder.getReturnGoodQty());
		//主订单号
		String orderNo=orderDetailMap.get("ORDERNO").toString();
		//单身历史总数量
		BigDecimal oldQTY=new BigDecimal(0);
		if(NinetyOneUtils.isNotEmpty(orderDetailMap.get("QTY"))){
			oldQTY=new BigDecimal(orderDetailMap.get("QTY").toString().trim());
		}
		
		//单身历史已退数量
		BigDecimal oldRQTY=new BigDecimal(0);
		if(NinetyOneUtils.isNotEmpty(orderDetailMap.get("RQTY"))){
			oldRQTY=new BigDecimal(orderDetailMap.get("RQTY").toString().trim());
		}
		
		Boolean isUpdateQTY=true;
		//单身中历史退货数量大于零，说明已更新过，此时不再更新单头及单身中数值栏位。
		if(oldRQTY.compareTo(BigDecimal.ZERO)>0){
			isUpdateQTY=false;
		}
		if((oldRQTY.add(returnGoodQty).subtract(oldQTY)).compareTo(BigDecimal.ZERO)<0){
			//历史退货数量+本次退货数量>总数   说明异常
			isUpdateQTY=false;
			lstData.add(getTvOrderStatusLog(eId, shopId, tranTime, orderNo, "3", "退单状态", "-1", "退货数量超出可退数量"));
			NinetyOneUtils.writelogFileName(logFileNameError,"\r\n***********单身ORDER_SN["+orderSN+"]/91订单号-退货数量超出可退数量");
		}
		
		
		String sqlOrder="SELECT A.*,B.RQTY AS BRQTY FROM OC_ORDER A "
				+ " LEFT JOIN OC_ORDER_DETAIL B "
				+ " ON A.EID=B.EID AND A.LOAD_DOCTYPE=B.LOAD_DOCTYPE AND A.ORDERNO=B.ORDERNO "
				+ " WHERE A.LOAD_DOCTYPE=? AND A.EID=? AND A.ORDERNO=?"
				+ " ";
		String[] conditionValues=new String[]{loadDoctype,eId,orderNo};
		List<Map<String, Object>> listSqlOrder=this.doQueryData(sqlOrder, conditionValues);
		//单头已退金额REFUNDAMT
		BigDecimal refundAmt=new BigDecimal(0);
		//91实际退货金额 
		BigDecimal returnGoodTotalPayment=new BigDecimal(mainOrder.getReturnGoodTotalPayment());
		
		//单身总数量
		BigDecimal toatalQTY=new BigDecimal(0);
		//单身总的已退数量，判断是否需要变更订单状态
		BigDecimal toatalReturnQTY=new BigDecimal(0);
		if(listSqlOrder!=null&&listSqlOrder.size()>0){
			//已退金额		REFUNDAMT
			if(NinetyOneUtils.isNotEmpty(listSqlOrder.get(0).get("REFUNDAMT"))){
				refundAmt=new BigDecimal(listSqlOrder.get(0).get("REFUNDAMT").toString().trim());
			}
			for(Map<String, Object> detailMap:listSqlOrder){
				//已退数量 RQTY
				if(NinetyOneUtils.isNotEmpty(detailMap.get("RQTY"))){
					toatalReturnQTY=toatalReturnQTY.add(new BigDecimal(detailMap.get("RQTY").toString().trim()));
				}
				//单身数量 QTY
				if(NinetyOneUtils.isNotEmpty(detailMap.get("QTY"))){
					toatalQTY=toatalQTY.add(new BigDecimal(detailMap.get("QTY").toString().trim()));
				}
			}
		}
		//加上本次退货数量
		if(isUpdateQTY){
			toatalReturnQTY=toatalReturnQTY.add(returnGoodQty);
			refundAmt=refundAmt.add(returnGoodTotalPayment);
		}
		String isAllReturn="N";
		if(toatalQTY.compareTo(toatalReturnQTY)==0){
			isAllReturn="Y";
		}
		
		String refundStatus="";
		String refundStatusName="";
		
		//91退貨單狀態
		//-退貨尚未處理 : WaitingToReturnGoods
		//-退貨已取消—不需要退貨 : ReturnGoodsCancel
		//-退貨已取消—不接受退貨 : ReturnGoodsFail
		//-退貨完成 : Finish
		//-等待消費者寄件 : WaitingToSend
		//-消費者已到店寄件 : StoreReceived
		//-物流中心開始驗收 : VerifySuccess
		//-物流中心驗收完成 : SupplierReceived
		//-物流中心驗收失敗_商品遺失 : VerifyFailLost
		//-逾期未寄件_配送編號失效 : VerifyFailInvalidCode
		if("WaitingToReturnGoods".equals(returnGoodStatus)){//退貨尚未處理 : WaitingToReturnGoods
			if("Y".equals(isAllReturn)){
				refundStatus="2";//2.用户申请退单
				refundStatusName="用户申请退单";
			}else{
				refundStatus="7";//7.用户申请部分退款
				refundStatusName="用户申请部分退款";
			}
		}
		else if("ReturnGoodsCancel".equals(returnGoodStatus)){//退貨已取消—不需要退貨 : ReturnGoodsCancel
			refundStatus="11";//11.用户申请取消订单
			refundStatusName="用户申请取消退订";
		}
		else if("ReturnGoodsFail".equals(returnGoodStatus)){//退貨已取消—不接受退貨 : ReturnGoodsFail
			if("Y".equals(isAllReturn)){
				refundStatus="3";//3.拒绝退单
				refundStatusName="拒绝退单";
			}else{
				refundStatus="8";//8.拒绝部分退款
				refundStatusName="拒绝部分退款";
			}
		}
		else if("Finish".equals(returnGoodStatus)){//退貨完成 : Finish
			if("Y".equals(isAllReturn)){
				refundStatus="6";//6.退单成功
				refundStatusName="退单成功";
			}else{
				refundStatus="10";//10.部分退款成功
				refundStatusName="部分退款成功";
			}
		}
		//逾期未寄件_配送編號失效 : VerifyFailInvalidCode
		else if("VerifyFailInvalidCode".equals(returnGoodStatus)){
			if("Y".equals(isAllReturn)){
				refundStatus="5";//5.退单失败
				refundStatusName="退单失败";
			}else{
				refundStatus="9";//9 部分退款失败
				refundStatusName="部分退款失败";
			}
		}
		//退订状态      1.未申请 2.用户申请退单 3.拒绝退单 4.客服仲裁中 5.退单失败 6.退单成功 7.用户申请部分退款
		//8.拒绝部分退款 9 部分退款失败 10.部分退款成功 11.用户申请取消订单 12.取消订单失败 13.取消订单成功 14.拒绝取消订单
		
		//单头  更新已退金额REFUNDAMT、退货原因 REFUNDREASON、退订状态REFUNDSTATUS、订单状态STATUS、TRAN_TIME、UPDATE_TIME
		UptBean ub2 = new UptBean("OC_ORDER");
		ub2.addUpdateValue("TRAN_TIME", new DataValue(tranTime, Types.VARCHAR));
		ub2.addUpdateValue("UPDATE_TIME", new DataValue(sdf5.format(dateNow), Types.VARCHAR));
		ub2.addUpdateValue("REFUNDSTATUS", new DataValue(refundStatus, Types.VARCHAR));
		ub2.addUpdateValue("REFUNDREASON", new DataValue(mainOrder.getReturnGoodCauseDesc(), Types.VARCHAR));
		ub2.addUpdateValue("RETURNSN", new DataValue(mainOrder.getReturnGoodDetailId(), Types.VARCHAR));
		//6.退单成功
		if("6".equals(refundStatus)){
			//12.已退单
//			ub2.addUpdateValue("STATUS", new DataValue("12", Types.VARCHAR));
			//状态由OrderECAgreeOrRejectBuyerCancelDCPServiceImp操作。
		}else{
			//同意退单时，历程由OrderECAgreeOrRejectBuyerCancelDCPServiceImp写入
			//写入历程
			lstData.add(getTvOrderStatusLog(eId, shopId, tranTime, orderNo, "3", "退单状态", refundStatus, refundStatusName));
		}
		if(isUpdateQTY){
			ub2.addUpdateValue("REFUNDAMT", new DataValue(refundAmt, Types.DECIMAL));
		}
		//condition
		ub2.addCondition("LOAD_DOCTYPE", new DataValue(loadDoctype, Types.VARCHAR));
		ub2.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		ub2.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		lstData.add(new DataProcessBean(ub2));
		
		//OC_SHIPMENT
		//发货在91做，不需要写OC_SHIPMENT货运单
		
		//单身更新RQTY退货数量、TRAN_TIME、

		UptBean ub1 = new UptBean("OC_ORDER_DETAIL");
		//add Value
		ub1.addUpdateValue("TRAN_TIME", new DataValue(tranTime, Types.VARCHAR));
		if(isUpdateQTY){
			ub1.addUpdateValue("RQTY", new DataValue(returnGoodQty, Types.DECIMAL));
		}
		//condition
		ub1.addCondition("LOAD_DOCTYPE", new DataValue(loadDoctype, Types.VARCHAR));
		ub1.addCondition("ORDERNO", new DataValue(orderNo, Types.VARCHAR));
		ub1.addCondition("ORDER_SN", new DataValue(orderSN, Types.VARCHAR));
		ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
		ub1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
		lstData.add(new DataProcessBean(ub1));
		
		boolean isSuccess=false;
		try{
			isSuccess=StaticInfo.dao.useTransactionProcessData(lstData);
		}catch(Exception e){
			NinetyOneUtils.writelogFileName(logFileNameError,"\r\n***********单据:["+orderNo+"],ORDER_SN["+orderSN+"]保存时发生异常:"+ExceptionUtils.getRootCauseMessage(e));
		}
	}
	
	
	/**
	 * 
	 * result转换为实体类NOReturnOrderDetailReq
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public List<NOReturnOrderDetailReq.ReturnOrderDetail> getReturnOrderDetailList(String result) throws Exception{
		List<NOReturnOrderDetailReq.ReturnOrderDetail> orderDetailList=new ArrayList<NOReturnOrderDetailReq.ReturnOrderDetail>();
		NOReturnOrderDetailReq orderDetailReq=JSON.parseObject(result, NOReturnOrderDetailReq.class);
		if(orderDetailReq!=null&&"success".equals(orderDetailReq.getStatus())){
			NOReturnOrderDetailReq.ReturnOrderDetailData data= orderDetailReq.getData();
			if(data!=null&&data.getList()!=null&&data.getList().size()>0){
				orderDetailList=data.getList();
			}
		}else{
			writelogFileName("\r\n***************result转换实体类NOReturnOrderDetailReq异常"+result+"****************\r\n");
		}
		return orderDetailList;
	}
	
	/**
	 * 整理呼叫ReturnOrderDetail接口的参数
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getReturnOrderDetailParams(NOReturnOrderListReq.ReturnOrderCode orderCode,long storeId) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("ShopId", storeId);//商店序號
		map.put("ReturnGoodDetailId", orderCode.getReturnGoodDetailId());//退貨單序號
		return map;
	}
	
	/**
	 * 
	 * result转换为实体类NOReturnOrderListReq
	 * @param result
	 * @return
	 * @throws Exception
	 */
	public NOReturnOrderListReq.ReturnOrderListData getReturnOrderList(String result) throws Exception{
		NOReturnOrderListReq.ReturnOrderListData orderListData=null;
		NOReturnOrderListReq returnOrderListReq=JSON.parseObject(result, NOReturnOrderListReq.class);
		if(returnOrderListReq!=null&&"success".equals(returnOrderListReq.getStatus())){
			NOReturnOrderListReq.ReturnOrderListData data= returnOrderListReq.getData();
			if(data!=null&&data.getList()!=null&&data.getList().size()>0){
				orderListData=data;
			}
		}else{
			writelogFileName("\r\n***************result转换实体类NOReturnOrderListReq异常"+result+"****************\r\n");
		}
		return orderListData;
	}
	
	/**
	 * 整理呼叫ReturnOrderList接口的参数
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getReturnOrderListParams(String startDate,String endDate,long storeId) throws Exception{
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("ShopId", storeId);//商店序號
		//查詢根據日期
		//-退貨單成立日 : ReturnGoodDateTime
		//-退貨單狀態日 : ReturnGoodStatusUpdatedDateTime
		map.put("ReturnGoodDateType", "ReturnGoodStatusUpdatedDateTime");
		//"StartDate": "2014-06-16T00:00:00"
		map.put("StartDate", startDate);
		map.put("EndDate", endDate);
//		map.put("StartDate", "2020-01-08T00:00:00");//测试用
//		map.put("EndDate", "2020-01-15T00:00:00");//测试用
		map.put("ReturnGoodStatus", "");//退貨單狀態 -全部 : 空白，請填入""
		map.put("Position", 0);//Position 位置 -第一筆資料的位置為 0，第二筆的資料位置為 1，以此類推
		map.put("Count", pageSize);//Count 取回資料筆數 -單次查詢最多 100 筆訂單清單
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
//					calendar.set(Calendar.HOUR_OF_DAY, 0);
//					calendar.set(Calendar.MINUTE, 0);
//					calendar.set(Calendar.SECOND, 0);
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
		columns.Add("STATUS", "100", Types.VARCHAR);//状态		STATUS
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

}
