package com.dsc.spos.scheduler.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.MicroMarketService;
import com.dsc.spos.waimai.SWaimaiBasicService;


/**
 * 查询微商城未推送的订单并保存
 * @author ASUS
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class OrderWeiscCreate extends InitJob {


	//定义一个countLoop值，避免陷入死循环
	public  int countLoop=50;

	public  String loggerInfoTitle="微商城订单OrderWeiscCreate";

	public Logger logger = LogManager.getLogger(OrderWeiscCreate.class.getName());

	static boolean bRun=false;//标记此服务是否正在执行中

	public String doExe()  throws Exception
	{

		logger.info("\r\n***************"+loggerInfoTitle+" START****************\r\n");
		HelpTools.writelog_waimai("***************"+loggerInfoTitle+" 主动抓单接口START****************");
		try{
			if (bRun)
			{		
				logger.info("\r\n***************"+loggerInfoTitle+" 同步正在执行中,本次调用取消****************\r\n");
				logger.info("\r\n***************" + loggerInfoTitle + " END****************\r\n");
				return null;
			}

			bRun=true;//

			List<Map<String, Object>> mainInfoList=getMainInfoList();

			Boolean isCoutinue=true;
			if(CollectionUtils.isEmpty(mainInfoList)){
				isCoutinue=false;
				logger.info("\r\n***************"+loggerInfoTitle+" 微商城[crm_channel]资料未配置****************\r\n");
			}

			if(!isCoutinue){
				logger.info("\r\n***************"+loggerInfoTitle+" END****************\r\n");
				bRun=false;
				return null;
			}

			getOrderDataListLoop(mainInfoList,1, 10);

		}catch(Exception e){
			logger.error(loggerInfoTitle+" 异常",e);
		}finally{
			bRun=false;//
		}

		logger.info("\r\n***************"+loggerInfoTitle+" END****************\r\n");
		return null;
	}


	public void getOrderDataListLoop(List<Map<String, Object>> mainInfoList,int pageNumber,int pageSize) throws Exception{
		
		Map<String, Object> map = mainInfoList.get(0);//只取默认的
		Boolean isCoutinue=true;
		String eId = "";
		Map<String,Object> infoMap=new HashMap<String,Object>();
		if(StringUtils.isEmpty(map.get("EID"))){
			isCoutinue=false;
			logger.info("\r\n***************"+loggerInfoTitle+" 微商城接口[crm_channel]EID未配置****************\r\n");
		}else{
			eId = map.get("EID").toString();
			infoMap.put("eId", map.get("EID").toString());
		}
		
		String httpPost = PosPub.getCRM_INNER_URL(eId);
		if(StringUtils.isEmpty(httpPost)){
			isCoutinue=false;
			logger.info("\r\n***************"+loggerInfoTitle+" 参数CrmUrl未配置****************\r\n");
		}else{
			infoMap.put("url", httpPost);
		}
		
		if(StringUtils.isEmpty(map.get("CHANNELID"))){
			isCoutinue=false;
			logger.info("\r\n***************"+loggerInfoTitle+" 微商城接口[crm_channel]CHANNELID未配置****************\r\n");
		}else{
			infoMap.put("key", map.get("CHANNELID").toString());
		}
		
		if(StringUtils.isEmpty(map.get("CHANNELID"))){
			isCoutinue=false;
			logger.info("\r\n***************"+loggerInfoTitle+" 微商城接口[crm_channel]CHANNELID未配置****************\r\n");
		}else{
			infoMap.put("secret", map.get("CHANNELID").toString());
		}
		
		if(isCoutinue){
			//定义一个countLoop值，避免陷入死循环
			int i=0;
			try{
				String url=infoMap.get("url").toString();
				HelpTools.writelog_waimai("【"+loggerInfoTitle+"】， 订单中心单号呼叫微商城urL="+url+",key="+infoMap.get("key").toString()+",secret="+infoMap.get("secret").toString());
				Map<String,Object> jsonMap=getOrderPostMap(infoMap,pageNumber, pageSize);
				OrderPostClient orderPost = new OrderPostClient();
				NewOrderList newOrderList=orderPost.getNewOrderList(url, jsonMap);
				if(newOrderList!=null&&newOrderList.isSuccess()){
					loopDataList(infoMap,newOrderList);
					int pageTotal=newOrderList.getTotalPages();
					while(pageNumber<pageTotal&&i<countLoop){
						pageNumber++;
						i++;
						NewOrderList newOrderListNext=orderPost.getNewOrderList(url, getOrderPostMap(infoMap,pageNumber, pageSize));
						loopDataList(infoMap,newOrderListNext);
					}
				}else{
					HelpTools.writelog_waimai("【"+loggerInfoTitle+"】， 订单中心单号呼叫微商城GetNewOrderList异常,返回资料:"+JSON.toJSONString(newOrderList));
					logger.info("\r\n***************"+loggerInfoTitle+" 订单中心单号呼叫微商城GetNewOrderList异常,返回资料:"+JSON.toJSONString(newOrderList)+"****************\r\n");
				}
			}catch(Exception e){
				HelpTools.writelog_waimai("【"+loggerInfoTitle+"】，订单中心单号呼叫微商城GetNewOrderList异常:"+e.getMessage());
			}
		}
	

	}

	/**
	 * 对返回的单号列表，循环处理，根据单号查询对应的订单详情
	 * @param newOrderList
	 * @throws Exception
	 */
	public void loopDataList(Map<String,Object> infoMap,NewOrderList newOrderList) throws Exception{
		if(newOrderList!=null&&newOrderList.isSuccess()){
			String eId=infoMap.get("eId").toString();
			String url=infoMap.get("url").toString();
			OrderPostClient orderPost = new OrderPostClient();
			List<OrderData> datas=newOrderList.getDatas();
			for(OrderData orderData:datas){
				String orderNo=orderData.getOrderNo();
				try{
					String orderSql = "select EID,ORDERNO,LOADDOCTYPE from DCP_ORDER where  EID=? and ORDERNO=? ";
					List<Map<String, Object>> orderList = this.doQueryData(orderSql, new String[]{eId,orderNo});
					//单号在订单中心已存在，则不需要查询明细，直接呼叫微服务OrderStatusUpdate，进行5=推送状态变更
					if(orderList!=null&&orderList.size()>0){
						logger.info("\r\n***************"+loggerInfoTitle+" 订单中心单号"+orderNo+"已存在****************\r\n");
						postWeisc(infoMap, orderNo);
					}else{
						Map<String,Object> jsonMap=getOrderDetailPostMap(infoMap,orderNo);
						String res=orderPost.getOrderDetailList(url, jsonMap);
						saveOrder(orderNo,res);
						List<Map<String, Object>> checkList = this.doQueryData(orderSql, new String[]{eId,orderNo});
						//保存成功后可查询到单据，再执行post方法，变更推送状态
						if(checkList!=null&&checkList.size()>0){
							postWeisc(infoMap, orderNo);
						}
					}
				}catch(Exception e){
					logger.error(loggerInfoTitle+" loopDataList异常",e);
				}
			}
		}
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void saveOrder(String orderNo,String orderStr) throws Exception{
		logger.info("\r\n***************"+loggerInfoTitle+" 呼叫订单中心，新增订单"+orderNo+"****************\r\n");
		SWaimaiBasicService swm = new MicroMarketService();
		swm.setDao(StaticInfo.dao);
		swm.execute(orderStr);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void postWeisc(Map<String,Object> infoMap,String orderNo) throws Exception{
		logger.info("\r\n***************"+loggerInfoTitle+" 呼叫微商城，执行单据"+orderNo+"推送状态变更****************\r\n");
		OrderPostClient orderPost = new OrderPostClient();
		Map<String,Object> jsonMap=getOrderStatusUpdatePostMap(infoMap,orderNo);
		orderPost.doPostStr(infoMap.get("url").toString(), jsonMap);
	}

	/**
	 * 组出微商城接口GetNewOrderList，需要的post参数
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getOrderPostMap(Map<String,Object> infoMap,int pageNumber,int pageSize) throws Exception{
		JSONObject requestMap=new JSONObject(true);
		requestMap.put("pageNumber", pageNumber);
		requestMap.put("pageSize", pageSize);
		return getPostMap(infoMap,requestMap,"GetNewOrderList");
	}

	/**
	 * 组出微商城接口GetOrderDetail-获取订单详细信息，需要的post参数
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getOrderDetailPostMap(Map<String,Object> infoMap,String orderNo) throws Exception{
		JSONObject requestMap=new JSONObject(true);
		requestMap.put("orderNo", orderNo);
		return getPostMap(infoMap,requestMap,"GetOrderDetail");
	}

	/**
	 * 组出微商城接口OrderStatusUpdate-更新订单状态，需要的post参数
	 * @return
	 * @throws Exception
	 */
	public Map<String,Object> getOrderStatusUpdatePostMap(Map<String,Object> infoMap,String orderNo) throws Exception{
		JSONObject requestMap=new JSONObject(true);
		requestMap.put("orderNo", orderNo);
		//statusType	状态类型	String	1=交易状态变更 2=物流状态变更 3=其他 4= 退单状态变更 5=推送状态变更
		requestMap.put("statusType", "5");
		//status	新状态   推送状态  0=未同步至订单中心 1=已同步至订单中心
		requestMap.put("status", "1");
		return getPostMap(infoMap,requestMap,"OrderStatusUpdate");
	}


	public Map<String,Object> getPostMap(Map<String,Object> infoMap,JSONObject requestMap,String serviceId) throws Exception{
		String key=infoMap.get("key").toString();
		String secret=infoMap.get("secret").toString();
		String sign=DigestUtils.md5Hex(JSON.toJSON(requestMap) + secret);
		Map<String,Object> signMap=new HashMap<String,Object>();
		signMap.put("key", key);
		signMap.put("sign", sign);

		Map<String,Object> postMap=new HashMap<String,Object>();
		postMap.put("serviceId", serviceId);
		postMap.put("sign", signMap);
		postMap.put("request", requestMap);
		Map<String,Object> json=new HashMap<String,Object>();
		//		json.put("json", JSON.toJSON(postMap).toString());
		//		json.put("json", postMap.toString());
		json.put("json", new com.google.gson.Gson().toJson(postMap));
		return json;
	}


	public List<Map<String, Object>> getMainInfoList() throws Exception{
		List<Map<String, Object>> urlList=null;
		String urlSql = " select * from crm_channel where status=100 and APPNO IN('WECHAT','MINI','LINE') ";
		HelpTools.writelog_waimai("【"+loggerInfoTitle+"】 主动抓单接口查询渠道配置sql="+urlSql);
		try{
			urlList = StaticInfo.dao.executeQuerySQL(urlSql, null);
		}catch(Exception e){

		}
		return urlList;
	}

	//实体类
	public class NewOrderList {

		private List<OrderData> datas;

		private String serviceStatus;

		private String serviceDescription;

		//默认给false
		private boolean success = Boolean.FALSE;

		/**
		 * 总页数
		 */
		private int totalPages;

		/**
		 * 总的记录条数
		 */
		private int totalRecords;

		/**
		 * 页码数
		 */
		private int pageNumber;

		/**
		 * 每页记录数
		 */
		private int pageSize;

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public List<OrderData> getDatas() {
			return datas;
		}

		public void setDatas(List<OrderData> datas) {
			this.datas = datas;
		}

		public String getServiceStatus() {
			return serviceStatus;
		}

		public void setServiceStatus(String serviceStatus) {
			this.serviceStatus = serviceStatus;
		}

		public String getServiceDescription() {
			return serviceDescription;
		}

		public void setServiceDescription(String serviceDescription) {
			this.serviceDescription = serviceDescription;
		}

		public int getTotalPages() {
			return totalPages;
		}

		public void setTotalPages(int totalPages) {
			this.totalPages = totalPages;
		}

		public int getTotalRecords() {
			return totalRecords;
		}

		public void setTotalRecords(int totalRecords) {
			this.totalRecords = totalRecords;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

	}

	public class OrderData{

		private String orderNo;

		private String shopId;

		public String getShopId() {
			return shopId;
		}

		public void setShopId(String shopId) {
			this.shopId = shopId;
		}

		public String getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}

	}

	public class OrderDetail{

		private String serviceStatus;

		private String serviceDescription;

		//默认给false
		private boolean success = Boolean.FALSE;

		private OrderInfo datas;

		/**
		 * 为接口HelpTools.GetMicroMarketResponse补充
		 */
		private String eId;

		public String getServiceStatus() {
			return serviceStatus;
		}

		public void setServiceStatus(String serviceStatus) {
			this.serviceStatus = serviceStatus;
		}

		public String getServiceDescription() {
			return serviceDescription;
		}

		public void setServiceDescription(String serviceDescription) {
			this.serviceDescription = serviceDescription;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public OrderInfo getDatas() {
			return datas;
		}

		public void setDatas(OrderInfo datas) {
			this.datas = datas;
		}

		public String geteId() {
			return eId;
		}

		public void seteId(String eId) {
			this.eId = eId;
		}

	}

	public class OrderInfo {

		private String orderNo;

		private String groupOrderId;
		private String groupStatus;
		private String eId;
		private Integer isLeader;
		private long gapNum;
		private String endTime;
		private List<Members> members;
		private String orderDateTime;    //yyyy-MM-dd HH:mm:ss
		private String payStatus;
		private String status;
		private double goodsAmount;
		private double deliverAmount;
		private double totalAmount;
		private double payedAmount;
		private double unpayAmount;
		private List<Goods> goods;
		private String memberId;
		private String deliverType;
		private String shopId;
		private String shopId_create;
		private String shopName;
		private String shopTelephone;
		private String shopAddress;
		private String shopOpenTime;
		private String address;
		private String contactName;
		private String contactTelephone;
		private String needDate;
		private String needTime;
		private String message;
		private double couponAmount;
		private double usePoint;
		private double pointAmount;
		private String finalTime;
		private double payable;

		private Integer statusGoods;
		private Integer statusComment;
		private Double minusAmount;
		private String deliverMessage;
		private String deliverTime;

		private Integer needInvoice;
		private String invoiceTitle;
		private String invoiceNumber;
		private Integer invoiceProvided;


		private String billType;
		private Double point;


		public Double getPoint() {
			return point;
		}

		public void setPoint(Double point) {
			this.point = point;
		}

		public String getBillType() {
			return billType;
		}

		public void setBillType(String billType) {
			this.billType = billType;
		}

		/**
		 * 订单退单状态
		 */
		private String refundStatus="1";

		private List<OrderPay> payDetail;

		public String geteId() {
			return eId;
		}

		public void seteId(String eId) {
			this.eId = eId;
		}

		public String getShopId_create() {
			return shopId_create;
		}

		public void setShopId_create(String shopId_create) {
			this.shopId_create = shopId_create;
		}

		public String getRefundStatus() {
			return refundStatus;
		}

		public void setRefundStatus(String refundStatus) {
			this.refundStatus = refundStatus;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public long getGapNum() {
			return gapNum;
		}

		public void setGapNum(long gapNum) {
			this.gapNum = gapNum;
		}

		public List<Members> getMembers() {
			return members;
		}

		public void setMembers(List<Members> members) {
			this.members = members;
		}

		public String getGroupStatus() {
			return groupStatus;
		}

		public void setGroupStatus(String groupStatus) {
			this.groupStatus = groupStatus;
		}

		public Integer getIsLeader() {
			return isLeader;
		}

		public void setIsLeader(Integer isLeader) {
			this.isLeader = isLeader;
		}

		public String getGroupOrderId() {
			return groupOrderId;
		}

		public void setGroupOrderId(String groupOrderId) {
			this.groupOrderId = groupOrderId;
		}


		public Integer getNeedInvoice() {
			return needInvoice;
		}

		public void setNeedInvoice(Integer needInvoice) {
			this.needInvoice = needInvoice;
		}

		public String getInvoiceTitle() {
			return invoiceTitle;
		}

		public void setInvoiceTitle(String invoiceTitle) {
			this.invoiceTitle = invoiceTitle;
		}

		public String getInvoiceNumber() {
			return invoiceNumber;
		}

		public void setInvoiceNumber(String invoiceNumber) {
			this.invoiceNumber = invoiceNumber;
		}

		public Integer getInvoiceProvided() {
			return invoiceProvided;
		}

		public void setInvoiceProvided(Integer invoiceProvided) {
			this.invoiceProvided = invoiceProvided;
		}

		public String getDeliverMessage() {
			return deliverMessage;
		}

		public void setDeliverMessage(String deliverMessage) {
			this.deliverMessage = deliverMessage;
		}

		public String getDeliverTime() {
			return deliverTime;
		}

		public void setDeliverTime(String deliverTime) {
			this.deliverTime = deliverTime;
		}

		public Integer getStatusGoods() {
			return statusGoods;
		}

		public void setStatusGoods(Integer statusGoods) {
			this.statusGoods = statusGoods;
		}

		public Integer getStatusComment() {
			return statusComment;
		}

		public void setStatusComment(Integer statusComment) {
			this.statusComment = statusComment;
		}

		public Double getMinusAmount() {
			return minusAmount;
		}

		public void setMinusAmount(Double minusAmount) {
			this.minusAmount = minusAmount;
		}

		public String getShopName() {
			return shopName;
		}

		public void setShopName(String shopName) {
			this.shopName = shopName;
		}

		public String getFinalTime() {
			return finalTime;
		}

		public void setFinalTime(String finalTime) {
			this.finalTime = finalTime;
		}

		public double getPayable() {
			return payable;
		}

		public void setPayable(double payable) {
			this.payable = payable;
		}

		public String getOrderNo() {
			return orderNo;
		}


		public void setOrderNo(String orderNo) {
			this.orderNo = orderNo;
		}


		public String getOrderDateTime() {
			return orderDateTime;
		}


		public void setOrderDateTime(String orderDateTime) {
			this.orderDateTime = orderDateTime;
		}


		public String getPayStatus() {
			return payStatus;
		}


		public void setPayStatus(String payStatus) {
			this.payStatus = payStatus;
		}


		public String getStatus() {
			return status;
		}


		public void setStatus(String status) {
			this.status = status;
		}


		public double getGoodsAmount() {
			return goodsAmount;
		}


		public void setGoodsAmount(double goodsAmount) {
			this.goodsAmount = goodsAmount;
		}


		public double getDeliverAmount() {
			return deliverAmount;
		}


		public void setDeliverAmount(double deliverAmount) {
			this.deliverAmount = deliverAmount;
		}


		public double getTotalAmount() {
			return totalAmount;
		}


		public void setTotalAmount(double totalAmount) {
			this.totalAmount = totalAmount;
		}


		public List<Goods> getGoods() {
			return goods;
		}


		public void setGoods(List<Goods> goods) {
			this.goods = goods;
		}


		public String getMemberId() {
			return memberId;
		}


		public void setMemberId(String memberId) {
			this.memberId = memberId;
		}


		public String getDeliverType() {
			return deliverType;
		}


		public void setDeliverType(String deliverType) {
			this.deliverType = deliverType;
		}


		public String getShopId() {
			return shopId;
		}


		public void setShopId(String shopId) {
			this.shopId = shopId;
		}


		public String getShopTelephone() {
			return shopTelephone;
		}


		public void setShopTelephone(String shopTelephone) {
			this.shopTelephone = shopTelephone;
		}


		public String getShopAddress() {
			return shopAddress;
		}


		public void setShopAddress(String shopAddress) {
			this.shopAddress = shopAddress;
		}


		public String getShopOpenTime() {
			return shopOpenTime;
		}


		public void setShopOpenTime(String shopOpenTime) {
			this.shopOpenTime = shopOpenTime;
		}


		public String getAddress() {
			return address;
		}


		public void setAddress(String address) {
			this.address = address;
		}


		public String getContactName() {
			return contactName;
		}


		public void setContactName(String contactName) {
			this.contactName = contactName;
		}


		public String getContactTelephone() {
			return contactTelephone;
		}


		public void setContactTelephone(String contactTelephone) {
			this.contactTelephone = contactTelephone;
		}


		public String getNeedDate() {
			return needDate;
		}


		public void setNeedDate(String needDate) {
			this.needDate = needDate;
		}


		public String getNeedTime() {
			return needTime;
		}


		public void setNeedTime(String needTime) {
			this.needTime = needTime;
		}


		public String getMessage() {
			return message;
		}


		public void setMessage(String message) {
			this.message = message;
		}


		public double getCouponAmount() {
			return couponAmount;
		}


		public void setCouponAmount(double couponAmount) {
			this.couponAmount = couponAmount;
		}


		public double getUsePoint() {
			return usePoint;
		}


		public void setUsePoint(double usePoint) {
			this.usePoint = usePoint;
		}


		public double getPointAmount() {
			return pointAmount;
		}


		public void setPointAmount(double pointAmount) {
			this.pointAmount = pointAmount;
		}


		public double getPayedAmount() {
			return payedAmount;
		}

		public void setPayedAmount(double payedAmount) {
			this.payedAmount = payedAmount;
		}


		public double getUnpayAmount() {
			return unpayAmount;
		}

		public void setUnpayAmount(double unpayAmount) {
			this.unpayAmount = unpayAmount;
		}


		public List<OrderPay> getPayDetail() {
			return payDetail;
		}

		public void setPayDetail(List<OrderPay> payDetail) {
			this.payDetail = payDetail;
		}




	}


	public class Goods {
		private int serialNo;
		private String mallGoodsId;
		private String mallGoodsName;
		private String goodsId;        //CRM系统中定义的商品编号
		private String extGoodsId;    //外部ERP的商品编号
		private String spec;
		private String picUrl;
		private double price;
		private double quantity;
		private double amount_point;
		public double getAmount_point() {
			return amount_point;
		}

		public void setAmount_point(double amount_point) {
			this.amount_point = amount_point;
		}

		private List<GoodsMessage> messages;

		public int getSerialNo() {
			return serialNo;
		}

		public void setSerialNo(int serialNo) {
			this.serialNo = serialNo;
		}

		public String getMallGoodsId() {
			return mallGoodsId;
		}

		public void setMallGoodsId(String mallGoodsId) {
			this.mallGoodsId = mallGoodsId;
		}

		public String getMallGoodsName() {
			return mallGoodsName;
		}

		public void setMallGoodsName(String mallGoodsName) {
			this.mallGoodsName = mallGoodsName;
		}

		public String getSpec() {
			return spec;
		}

		public void setSpec(String spec) {
			this.spec = spec;
		}

		public String getPicUrl() {
			return picUrl;
		}

		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}


		public double getPrice() {
			return price;
		}

		public void setPrice(double price) {
			this.price = price;
		}

		public double getQuantity() {
			return quantity;
		}

		public void setQuantity(double quantity) {
			this.quantity = quantity;
		}

		public List<GoodsMessage> getMessages() {
			return messages;
		}

		public void setMessages(List<GoodsMessage> messages) {
			this.messages = messages;
		}

		public String getGoodsId() {
			return goodsId;
		}

		public void setGoodsId(String goodsId) {
			this.goodsId = goodsId;
		}

		public String getExtGoodsId() {
			return extGoodsId;
		}

		public void setExtGoodsId(String extGoodsId) {
			this.extGoodsId = extGoodsId;
		}

	}

	public class GoodsMessage {
		private String msgName;
		private String msgType;
		private String message;

		public String getMsgName() {
			return msgName;
		}

		public void setMsgName(String msgName) {
			this.msgName = msgName;
		}

		public String getMsgType() {
			return msgType;
		}

		public void setMsgType(String msgType) {
			this.msgType = msgType;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	public class OrderPay {
		private int serailNo;
		private String purpose;
		private String payChannel;
		private String payOrderNo;
		private String payTradeNo;
		private double amount;
		private double custPayReal;
		private double merReceive;
		private double merDiscount;
		private double thirdDiscount;
		private String noCode;
		private double quantity;
		private String payChannelName;

		public String getPayChannelName() {
			return payChannelName;
		}

		public void setPayChannelName(String payChannelName) {
			this.payChannelName = payChannelName;
		}

		public int getSerailNo() {
			return serailNo;
		}

		public void setSerailNo(int serailNo) {
			this.serailNo = serailNo;
		}

		public String getPurpose() {
			return purpose;
		}

		public void setPurpose(String purpose) {
			this.purpose = purpose;
		}

		public String getPayChannel() {
			return payChannel;
		}

		public void setPayChannel(String payChannel) {
			this.payChannel = payChannel;
		}

		public String getPayOrderNo() {
			return payOrderNo;
		}

		public void setPayOrderNo(String payOrderNo) {
			this.payOrderNo = payOrderNo;
		}

		public String getPayTradeNo() {
			return payTradeNo;
		}

		public void setPayTradeNo(String payTradeNo) {
			this.payTradeNo = payTradeNo;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		public double getCustPayReal() {
			return custPayReal;
		}

		public void setCustPayReal(double custPayReal) {
			this.custPayReal = custPayReal;
		}

		public double getMerReceive() {
			return merReceive;
		}

		public void setMerReceive(double merReceive) {
			this.merReceive = merReceive;
		}

		public double getMerDiscount() {
			return merDiscount;
		}

		public void setMerDiscount(double merDiscount) {
			this.merDiscount = merDiscount;
		}

		public double getThirdDiscount() {
			return thirdDiscount;
		}

		public void setThirdDiscount(double thirdDiscount) {
			this.thirdDiscount = thirdDiscount;
		}

		public String getNoCode() {
			return noCode;
		}

		public void setNoCode(String noCode) {
			this.noCode = noCode;
		}

		public double getQuantity() {
			return quantity;
		}

		public void setQuantity(double quantity) {
			this.quantity = quantity;
		}


	}

	public class Members {
		private int isLeader;
		private String memberPicUrl;

		public int getIsLeader() {
			return isLeader;
		}

		public void setIsLeader(int isLeader) {
			this.isLeader = isLeader;
		}

		public String getMemberPicUrl() {
			return memberPicUrl;
		}

		public void setMemberPicUrl(String memberPicUrl) {
			this.memberPicUrl = memberPicUrl;
		}
	}


}
