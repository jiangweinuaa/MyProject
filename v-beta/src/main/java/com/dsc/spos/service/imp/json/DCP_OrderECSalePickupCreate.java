package com.dsc.spos.service.imp.json;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.DelBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.dao.UptBean;
import com.dsc.spos.json.cust.req.DCP_OrderECSalePickupCreateReq;
import com.dsc.spos.json.cust.res.DCP_OrderECSalePickupCreateRes;
import com.dsc.spos.scheduler.job.LgGetExpressNo;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.HttpSend;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 订单及销售单点货作业
 * @author yuanyy 2019-03-13
 *
 */
public class DCP_OrderECSalePickupCreate extends SPosAdvanceService<DCP_OrderECSalePickupCreateReq, DCP_OrderECSalePickupCreateRes> {


	Logger logger = LogManager.getLogger(DCP_OrderECSalePickupCreate.class.getName());

	@Override
	protected void processDUID(DCP_OrderECSalePickupCreateReq req, DCP_OrderECSalePickupCreateRes res) throws Exception {
		// TODO Auto-generated method stub
		try 
		{
			String sql = null;
			String eId = req.geteId();
			//下订门店
			String shopId = req.getShopId();//下面会取单据
			
			//配送门店
			String shipShop = req.getShopId();//下面会取单据
			
			
			String status="";

			String ecPlatformNo = ""; // 电商平台代码
			String ecPlatformName = "";
			String ecOrderNo = ""; // 订单/销售单号
			String lgPlatFormNo = ""; //物流平台代号（货运厂商）
			String lgPlatFormName = "";
			String ecStatus = ""; //单据状态此处固定1，表示出货
			String shipDate = ""; //指定日期 
			String shipHourType = ""; //配送时段类型，1:9~12 時 2:12~17 時 3:17~20 時 4:不指定时段
			String orderer = "";
			String orderPhone = "";
			String receiver = "";
			String receiverMobile = "";
			String receiverPhone = "";
			String receiverAddress = "";
			String receiverEmail = "";
			String memo = "";
			String getShop = ""; //取货门店
			String getShopName = "";
			String totBoxes = "1"; //总箱数,非真实总箱数用户可能漏填错误,根据多个托运单计算
			String ecLoadType = "";
			String deliveryType = "";
			String payStatus = "1";
			String payAmt = "0";
			String totAmt = "0";


			String shopeeMode = "";
			String shopeeAddressId = "";
			String shopeePickuptimeId = "";
			String shopeeBranchId = "";
			String shopeeSenderRealName = "";
			String orderPackageId ="";
			String greenworld_LogisticsId="";
			String greenworld_MerchantTradeno="";
			String greenworld_Validno="";
			
			
			// 2019-04-18 customerNO 和 customerName 规格上有这两个字段， 实际上是不需要的。货运单不用加客户编码，此处不做处理
			String customerNo = "";
			String customerName = "";

			if(req.getCustomerNO() == null || req.getCustomerNO().equals("")){
				customerNo = "";
			}
			else{
				customerNo = req.getCustomerNO();
			}

			if(req.getCustomerName() == null || req.getCustomerName().equals("")){
				customerName = "";
			}
			else{
				customerName = req.getCustomerName();
			}

			if(req.getShopeeMode() == null || req.getShopeeMode().equals("")){
				shopeeMode = "";
			}
			else{
				shopeeMode = req.getShopeeMode();
			}

			if(req.getShopeeAddressId() == null || req.getShopeeAddressId().equals("")){
				shopeeAddressId = "";
			}
			else{
				shopeeAddressId = req.getShopeeAddressId();
			}

			if(req.getShopeePickuptimeId() == null || req.getShopeePickuptimeId().equals("")){
				shopeePickuptimeId = "";
			}
			else{
				shopeePickuptimeId = req.getShopeePickuptimeId();
			}

			if(req.getShopeeBranchId() == null || req.getShopeeBranchId().equals("")){
				shopeeBranchId = "";
			}
			else{
				shopeeBranchId = req.getShopeeBranchId();
			}

			if(req.getShopeeSenderRealName() == null || req.getShopeeSenderRealName().equals("")){
				shopeeSenderRealName = "";
			}
			else{
				shopeeSenderRealName = req.getShopeeSenderRealName();
			}
			
			//
			if(req.getOrderPackageId() == null || req.getOrderPackageId().equals(""))
			{
				orderPackageId = "";
			}
			else
			{
				orderPackageId = req.getOrderPackageId();
			}

			
			//
			if(req.getGreenworld_LogisticsId() == null || req.getGreenworld_LogisticsId().equals(""))
			{
				greenworld_LogisticsId = "";
			}
			else
			{
				greenworld_LogisticsId = req.getGreenworld_LogisticsId();
			}
			
			//
			if(req.getGreenworld_MerchantTradeno() == null || req.getGreenworld_MerchantTradeno().equals(""))
			{
				greenworld_MerchantTradeno = "";
			}
			else
			{
				greenworld_MerchantTradeno = req.getGreenworld_MerchantTradeno();
			}
			
			//
			if(req.getGreenworld_Validno() == null || req.getGreenworld_Validno().equals(""))
			{
				greenworld_Validno = "";
			}
			else
			{
				greenworld_Validno = req.getGreenworld_Validno();
			}
			
						
			
			if(req.getEcPlatformNo() == null || req.getEcPlatformNo().equals("")){
				ecPlatformNo = "";
			}
			else{
				ecPlatformNo = req.getEcPlatformNo(); // 电商平台代码
			}

			if(req.getEcPlatformName() == null || req.getEcPlatformName().equals("")){
				ecPlatformName = "";
			}
			else{
				ecPlatformName = req.getEcPlatformName();
			}

			if(req.getEcOrderNo() == null || req.getEcOrderNo().equals("")){
				ecOrderNo = "";
			}
			else{
				ecOrderNo = req.getEcOrderNo();
			}

			if(req.getEcOrderNo() == null || req.getEcOrderNo().equals("")){
				ecOrderNo = "";
			}
			else{
				ecOrderNo = req.getEcOrderNo(); // 订单/销售单号
			}

			if(req.getLgPlatformNo() == null || req.getLgPlatformNo().equals("")){
				lgPlatFormNo = "";
			}
			else{
				lgPlatFormNo = req.getLgPlatformNo(); //物流平台代号（货运厂商）
			}

			if(req.getLgPlatformName() == null || req.getLgPlatformName().equals("")){
				lgPlatFormName = "";
			}
			else{
				lgPlatFormName = req.getLgPlatformName();
			}

			if(req.getEcStatus() == null || req.getEcStatus().equals("")){
				ecStatus = "";
			}
			else{
				ecStatus = req.getEcStatus(); //单据状态此处固定1，表示出货
			}

			if(req.getEcStatus() == null || req.getEcStatus().equals("")){
				ecStatus = "";
			}
			else{
				ecStatus = req.getEcStatus(); //单据状态此处固定1，表示出货
			}

			if(req.getShipDate() == null || req.getShipDate().equals("")){
				shipDate = "";
			}
			else{
				shipDate = req.getShipDate(); //指定日期 
			}

			if(req.getShipHourtype() == null || req.getShipHourtype().equals("")){
				shipHourType = "";
			}
			else{
				shipHourType = req.getShipHourtype(); //配送时段类型，1:9~12 時 2:12~17 時 3:17~20 時 4:不指定时段
			}

			if(req.getOrderer() == null || req.getOrderer().equals("")){
				orderer = "";
			}
			else{
				orderer = req.getOrderer(); 
			}

			if(req.getOrdererPhone() == null || req.getOrdererPhone().equals("")){
				orderPhone = "";
			}
			else{
				orderPhone = req.getOrdererPhone();
			}

			if(req.getReceiver() == null || req.getReceiver().equals("")){
				receiver = "";
			}
			else{
				receiver = req.getReceiver();
			}

			if(req.getReceiverMobile() == null || req.getReceiverMobile().equals("")){
				receiverMobile = "";
			}
			else{
				receiverMobile = req.getReceiverMobile();
			}

			if(req.getReceiverPhone() == null || req.getReceiverPhone().equals("")){
				receiverPhone = "";
			}
			else{
				receiverPhone = req.getReceiverPhone();
			}

			if(req.getReceiverAddress() == null || req.getReceiverAddress().equals("")){
				receiverAddress = "";
			}
			else{
				receiverAddress = req.getReceiverAddress();
			}

			if(req.getReceiverEmail() == null || req.getReceiverEmail().equals("")){
				receiverEmail = "";
			}
			else{
				receiverEmail = req.getReceiverEmail();
			}

			if(req.getMemo() == null || req.getMemo().equals("")){
				memo = "";
			}
			else{
				memo = req.getMemo();
			}

			if(req.getGetShop() == null || req.getGetShop().equals("")){
				getShop = "";
			}
			else{
				getShop = req.getGetShop(); //取货门店
			}

			if(req.getGetShopName() == null || req.getGetShopName().equals("")){
				getShopName = "";
			}
			else{
				getShopName = req.getGetShopName();
			}
			
			if(req.getTotBoxes() == null || req.getTotBoxes().equals("")){
				totBoxes = "1";
			}
			else{
				totBoxes = req.getTotBoxes(); //总箱数
			}

			if(req.getEcLoadType() == null || req.getEcLoadType().equals("")){
				ecLoadType = "";
			}
			else{
				ecLoadType = req.getEcLoadType(); 
			}

			if(req.getDeliveryType() == null || req.getDeliveryType().equals("")){
				deliveryType = "";
			}
			else{
				deliveryType = req.getDeliveryType();
			}

			if(req.getPayStatus() == null || req.getPayStatus().equals("")){
				payStatus = "";
			}
			else{
				payStatus = req.getPayStatus();
			}

			if(req.getPayAmt() == null || req.getPayAmt().equals("")){
				payAmt = "0";
			}
			else{
				payAmt = req.getPayAmt();
			}

			if(req.getTotAmt() == null || req.getTotAmt().equals("")){
				totAmt = "0";
			}
			else{
				totAmt = req.getTotAmt();
			}

			double totAmt2 = Double.parseDouble(totAmt);
			double payAmt2 = Double.parseDouble(payAmt);
			double collectAmt2 = totAmt2 - payAmt2;

			String collectAmt = String.valueOf(collectAmt2);

			/**
			 *  3、 当前端deliveryType为  8.全家超商 或 10.莱而富超商 或 11.OK超商  数据库插入货运厂商代码为：cvs  名称为：便利达康											
				4、 当前端deliveryType为  7.7-11超商 货运厂商代码为：dzt 名称为:大智通											
				5、 当前端deliveryType为 9.黑猫宅急便  货运厂商代码为：egs  名称为:黑猫宅急便											
				6、 当前端deliveryType为 12.mingjie大件物流  货运厂商代码为：mingjie  名称为:mingjie大物流											
				7、 当前端deliveryType为 13.中华邮政  货运厂商代码为：chinapost  名称为:中华邮政	
				8、 当前端deliveryType为 16.绿界7-11超商 17.绿界全家超商 18.绿界莱而富超商 19.绿界OK超商，货运厂商代码为：greenworld  名称为：綠界												
			 */
			if(deliveryType.equals("8") || deliveryType.equals("10") || deliveryType.equals("11"))
			{
				lgPlatFormNo = "cvs";
				lgPlatFormName = "便利达康";
			}else if(deliveryType.equals("7")){
				lgPlatFormNo = "dzt";
				lgPlatFormName = "大智通";
			}else if(deliveryType.equals("9")){
				lgPlatFormNo = "egs";
				lgPlatFormName = "黑猫宅急便";
			}else if(deliveryType.equals("12")){
				lgPlatFormNo = "mingjie";
				lgPlatFormName = "mingjie大物流";
			}
			else if(deliveryType.equals("13")){
				lgPlatFormNo = "chinapost";
				lgPlatFormName = "中华邮政";
			}
			else if(deliveryType.equals("15"))
			{
				lgPlatFormNo = "htc";
				lgPlatFormName = "新竹物流";
			}
			else if(deliveryType.equals("16")||deliveryType.equals("17") ||deliveryType.equals("18") ||deliveryType.equals("19"))
			{
				lgPlatFormNo = "greenworld";
				lgPlatFormName = "綠界";
			}
			else
			{
				lgPlatFormNo = req.getLgPlatformNo(); //物流平台代号（货运厂商）
				lgPlatFormName = req.getLgPlatformName();
			}
			
			if (receiverAddress.equals("")) 
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("收货地址不能为空！");	
				this.pData.clear();
				return;
			}
			if (totAmt.equals("")) 
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("订单总额不能为空！");	
				this.pData.clear();
				return;
			}
			if (receiver.equals("")) 
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("收货人不能为空！");		
				this.pData.clear();
				return;
			}
			if (receiverPhone.equals("") && receiverMobile.equals("")) 
			{
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("收货人联系电话不能为空！");		
				this.pData.clear();
				return;
			}
			

			String expressNo = ""; //托运单号/配送编号
			String packageNo = ""; 
			String measureNo = "";//尺寸代码
			String measureName = "";
			String temperatelayerNo = "";//温层
			String temperatelayerName = "";
			String transFee = "";
			String boxNo = "";
			String pieces = ""; //件数 （规格上前端给的是点货量，后续可能需要加上重量、件数、包装、实际金额等字段）

			String expressBilltype="";
			String receiverSiteno="";
			String senderSiteno="";
			String distanceNo="";
			String distanceName="";
			String receiverFivecode="";
			String receiverSevencode="";
			
			
			String opNo = req.getOpNO();
			String shipmentNo="";
			
			//處理POS訂單以配送門店為歸屬門店			
			String sqlshipping="select * from OC_ORDER where EID='"+eId+"' and ORDERNO='"+ecOrderNo+"'";
			List<Map<String , Object>> ordernoDatas = this.doQueryData(sqlshipping, null);
			if(ordernoDatas != null && ordernoDatas.size()>0)
			{				
				shopId=ordernoDatas.get(0).get("SHOPID").toString();
				
				shipShop=ordernoDatas.get(0).get("SHIPPINGSHOP").toString();
				
				status=ordernoDatas.get(0).get("STATUS").toString();				
								
				shipmentNo = this.getShipmentNO(req,shopId);
				
				//主订单点不了货
				String DETAILTYPE=ordernoDatas.get(0).get("DETAILTYPE").toString();
				if(DETAILTYPE.equals("3"))
				{
					res.setSuccess(false);
					res.setServiceStatus("100");
					res.setServiceDescription("主单不允许点货");
					this.pData.clear();
					return;
				}
				
			}
			else 
			{				
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("找不到此訂單資料");	
				this.pData.clear();
				return;
			}
			
			Calendar cal = Calendar.getInstance();//获得当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String sDate = df.format(cal.getTime());
			df = new SimpleDateFormat("HHmmss");
			String sTime = df.format(cal.getTime());

//			String reqUrl = "";
//			reqUrl=PosPub.getDCP_URL(eId);
			

			//这里声明托运单号，  包裹号，尺寸，温层。  		
			String[] columns1 = {
					"EID","SHOPID","SHIPMENTNO","LGPLATFORMNO", "LGPLATFORMNAME",
					"STATUS","SDATE","STIME","ORDERER","ORDERER_PHONE",
					"RECEIVER","RECEIVER_MOBILE","RECEIVER_PHONE","RECEIVER_ADDRESS","RECEIVER_EMAIL",
					"MEMO","GETSHOP","GETSHOPNAME","TOT_BOXES","SHIPDATE","SHIPHOURTYPE",
					"EXPRESSNO","PACKAGENO","MEASURENO",
					"MEASURENAME","TEMPERATELAYERNO","TEMPERATELAYERNAME","TRANSFEE","BOXNO","ORIGINALTYPE","EC_ORDERNO",
					"PAYSTATUS","PAYAMT","ECPLATFORMNO","ECPLATFORMNAME","DELIVERYTYPE","COLLECTAMT",
					"SHOPEE_MODE","SHOPEE_ADDRESS_ID","SHOPEE_PICKUP_TIME_ID",
					"SHOPEE_BRANCH_ID","SHOPEE_SENDER_REAL_NAME",
					"EXPRESSBILLTYPE","RECEIVER_SITENO","SENDER_SITENO","DISTANCENO","DISTANCENAME",
					"RECEIVER_FIVECODE","RECEIVER_SEVENCODE","SHIP_ORDERPACKAGEID",
					"GREENWORLD_LOGISTICSID","GREENWORLD_MERCHANTTRADENO","GREENWORLD_VALIDNO"
			};
			DataValue[] insValue1 = null;
			List<Map<String, Object> > datas = req.getDatas();

			//记录保存的发货单栏位
			List<Map<String, Object>> listLG=new ArrayList<Map<String, Object>>();

			if(!datas.isEmpty())
			{

				//如果是新竹物流， 根据温层过滤， 有几个温层就有几个货运单
				Map<String, Boolean> condition1 = new HashMap<String, Boolean>(); 
				//过滤条件只设置托运单号：   假设当前订单的商品分分2个箱子，对新竹物流来说是1张托运单，其他物流是2张托运单
				// 有一种特殊情况： 新竹物流，多个商品，选择了不同的包裹，同一个托运单号。 这种情况需要前端控制，不能选择订单商品 ，或者只取最后一次的包裹信息
				condition1.put("temperateNo", true);	
				List<Map<String, Object>> htcDatas=MapDistinct.getMap(datas, condition1);

				if(lgPlatFormNo.equals("htc"))
				{
					//多发货单就是多箱处理
					totBoxes=htcDatas.size()+"";
					
					for (int k = 0; k < htcDatas.size(); k++) 
					{
						Map<String, Object> eDatas = htcDatas.get(k);
						expressNo = eDatas.get("expressNo").toString();
						packageNo = eDatas.get("packageNo").toString();
						
						//
						if (!eDatas.containsKey("measureNo")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的measureNo字段找不到！");		
							this.pData.clear();
							return;
						}
						
						if (!eDatas.containsKey("measureName")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的measureName字段找不到！");		
							this.pData.clear();
							return;
						}
						
						if (!eDatas.containsKey("temperateNo")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的temperateNo字段找不到！");		
							this.pData.clear();
							return;
						}
						
						if (!eDatas.containsKey("temperateName")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的temperateName字段找不到！");		
							this.pData.clear();
							return;
						}
						
						if (!eDatas.containsKey("transFee")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的transFee字段找不到！");		
							this.pData.clear();
							return;
						}
						
						measureNo = eDatas.get("measureNo").toString();
						measureName = eDatas.get("measureName").toString();
						temperatelayerNo= eDatas.get("temperateNo").toString();
						temperatelayerName = eDatas.get("temperateName").toString();
						transFee = eDatas.get("transFee").toString();
						
						
						boxNo = eDatas.get("boxNo").toString();

						
						expressBilltype=eDatas.get("expressBilltype").toString();
						receiverSiteno=eDatas.get("receiverSiteno").toString();
						senderSiteno=eDatas.get("senderSiteno").toString();
						distanceNo=eDatas.get("distanceNo").toString();
						distanceName=eDatas.get("distanceName").toString();
						receiverFivecode=eDatas.get("receiverFivecode").toString();
						receiverSevencode=eDatas.get("receiverSevencode").toString();
						

						if(k != 0){ // 如果是第一笔货运单， 已付金额和支付状态对应订单上； 不是第一笔，往后的单子，都设置为金额0，状态：已付清
							payAmt = "0";
							payStatus = "3";
						}

						for (Map<String, Object> oneData : datas) 
						{
							//			
							if (!oneData.containsKey("temperateNo")) 
							{							
								res.setSuccess(false);
								res.setServiceStatus("100");
								res.setServiceDescription("前端req.getDatas中的temperateNo字段找不到！");	
								
								this.pData.clear();
								return;
							}		
							
							if(temperatelayerNo.equals(oneData.get("temperateNo").toString()) )
							{
								String item = oneData.get("item").toString();
								String oItem = oneData.get("oItem").toString();
								String pluNo = oneData.get("pluNo").toString();
								String pluName = oneData.get("pluName").toString();

								String pluSql = "select a.pluNo ,  a.sunit, b.plubarcode  from DCP_GOODS a "
										+ " left join DCP_BARCODE b on a.EID = b.EID and a.pluNo = b.pluNo "
										+ " where a.EID = '"+eId+"'  "
										+ " and a.pluNo = '"+pluNo+"' "
										+ " and b.status='100' and a.status='100'";
								String[] conditionValues = {};
								List<Map<String, Object>> pluDatas = this.doQueryData(pluSql, conditionValues);
								if(pluDatas.isEmpty())
								{ //如果为空，则说明没有这个商品的信息；  不能加商品名称作为条件，可能有多语言， 名称不一样
									//									res.setSuccess(false);
									//									res.setServiceDescription("服务执行失败：编码为："+pluNo +" , 名称为："+ pluName+" 的商品信息不存在！！");

									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "编码为："+pluNo +" , 名称为："+ pluName+" 的商品信息不存在！！");
								}
								else
								{
									for (Map<String, Object> pluData : pluDatas) 
									{
										String pluNo2 = pluData.get("PLUNO").toString();
										String pluBarCode = pluData.get("PLUBARCODE").toString();

										if(pluNo2== null || pluNo2.trim().equals("") || pluBarCode == null || pluBarCode.trim().equals("")){
											//											res.setSuccess(false);
											//											res.setServiceDescription("服务执行失败：编码为："+pluNo +" , 名称为："+ pluName+" 的商品信息不存在！！");

											throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编码为："+pluNo+"的商品信息不存在  或 对应的条码不存在");
										}

									}
								}

								String qty = oneData.get("qty").toString();
								String barcode = oneData.get("barcode").toString();
								String amt = oneData.get("amt").toString();

								String specName = oneData.get("specName").toString();
								String pickupQty = oneData.get("pickupQty").toString();
								
								if (PosPub.isNumericType(qty)==false ||PosPub.isNumericType(pickupQty)==false) 
								{
									res.setSuccess(false);
									res.setServiceStatus("100");
									res.setServiceDescription("前端req.getDatas中的qty、pickupQty必须是数字类型！");		
									this.pData.clear();
									return;
								}
								
								
								BigDecimal bdm1=new BigDecimal(qty);
								BigDecimal bdm2=new BigDecimal(pickupQty);
								if (bdm1.compareTo(bdm2)!=0) 
								{
									res.setSuccess(false);
									res.setServiceStatus("100");
									res.setServiceDescription("前端req.getDatas中的pickupQty必须等于qty！");		
									this.pData.clear();
									return;
								}
								
								int insColCt = 0;  
								String[] columnsName = {
										"EID","SHOPID","SHIPMENTNO","LGPLATFORMNO", "LGPLATFORMNAME",
										"ITEM","PLUNO","PLUNAME","PLUBARCODE","SPECNAME","QTY",
										"AMT","EC_ORDERNO","ECPLATFORMNO","ECPLATFORMNAME","ORIGINALITEM"
								};

								DataValue[] columnsVal = new DataValue[columnsName.length];
								for (int i = 0; i < columnsVal.length; i++) { 
									String keyVal = null;
									switch (i) { 
									case 0:
										keyVal = eId;
										break;
									case 1:
										keyVal = shopId;
										break;
									case 2:
										keyVal = shipmentNo;
										break;
									case 3:
										keyVal = lgPlatFormNo;
										break;
									case 4:
										keyVal = lgPlatFormName;
										break;
									case 5:
										keyVal = item;
										break;
									case 6:
										keyVal = pluNo;
										break;
									case 7:
										keyVal = pluName;
										break;
									case 8:
										keyVal = barcode;
										break;
									case 9:
										keyVal = specName;
										break;
									case 10:
										keyVal = qty;
										break;
									case 11:
										keyVal = amt;
										break;
									case 12:
										keyVal = ecOrderNo;
										break;
									case 13:
										keyVal = ecPlatformNo;
										break;
									case 14:
										keyVal = ecPlatformName;
										break;
									case 15:
										keyVal = oItem;
										break;
									default:
										break;
									}
									if (keyVal != null) 
									{
										insColCt++;
										columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
									} 
									else 
									{
										columnsVal[i] = null;
									}


								}
								String[] columns2  = new String[insColCt];
								DataValue[] insValue2 = new DataValue[insColCt];
								// 依照傳入參數組譯要insert的欄位與數值；
								insColCt = 0;

								for (int j=0;j<columnsVal.length;j++){
									if (columnsVal[j] != null){
										columns2[insColCt] = columnsName[j];
										insValue2[insColCt] = columnsVal[j];
										insColCt ++;
										if (insColCt >= insValue2.length) 
											break;
									}
								}
								InsBean ib2 = new InsBean("OC_SHIPMENT_ORIGINAL", columns2);
								ib2.addValues(insValue2);
								this.addProcessData(new DataProcessBean(ib2));
							}

						}
						insValue1 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(shipmentNo, Types.VARCHAR),
								new DataValue(lgPlatFormNo, Types.VARCHAR),
								new DataValue(lgPlatFormName, Types.VARCHAR),
								new DataValue(ecStatus, Types.VARCHAR),//
								new DataValue(sDate, Types.VARCHAR),
								new DataValue(sTime, Types.VARCHAR),

								new DataValue(orderer, Types.VARCHAR), 
								new DataValue(orderPhone, Types.VARCHAR),
								new DataValue(receiver, Types.VARCHAR),
								new DataValue(receiverMobile, Types.VARCHAR),
								new DataValue(receiverPhone, Types.VARCHAR),
								new DataValue(receiverAddress, Types.VARCHAR),
								new DataValue(receiverEmail, Types.VARCHAR),
								new DataValue(memo, Types.VARCHAR),
								new DataValue(getShop, Types.VARCHAR),
								new DataValue(getShopName, Types.VARCHAR),
								new DataValue(totBoxes, Types.VARCHAR),
								new DataValue(shipDate, Types.VARCHAR),
								new DataValue(shipHourType, Types.VARCHAR),

								new DataValue(expressNo, Types.VARCHAR),
								new DataValue(packageNo, Types.VARCHAR),
								new DataValue(measureNo, Types.VARCHAR),
								new DataValue(measureName, Types.VARCHAR),
								new DataValue(temperatelayerNo, Types.VARCHAR),
								new DataValue(temperatelayerName, Types.VARCHAR),
								new DataValue(transFee, Types.VARCHAR),
								new DataValue(boxNo, Types.VARCHAR),
								new DataValue(ecLoadType, Types.VARCHAR),
								new DataValue(ecOrderNo, Types.VARCHAR),
								new DataValue(payStatus, Types.VARCHAR),
								new DataValue(payAmt, Types.VARCHAR),
								new DataValue(ecPlatformNo, Types.VARCHAR),
								new DataValue(ecPlatformName, Types.VARCHAR),
								new DataValue(deliveryType, Types.VARCHAR),
								new DataValue(collectAmt, Types.VARCHAR),

								new DataValue(shopeeMode, Types.VARCHAR),
								new DataValue(shopeeAddressId, Types.VARCHAR),
								new DataValue(shopeePickuptimeId, Types.VARCHAR),
								new DataValue(shopeeBranchId, Types.VARCHAR),
								new DataValue(shopeeSenderRealName, Types.VARCHAR),
								
								new DataValue(expressBilltype, Types.VARCHAR),
								new DataValue(receiverSiteno, Types.VARCHAR),
								new DataValue(senderSiteno, Types.VARCHAR),
								new DataValue(distanceNo, Types.VARCHAR),
								new DataValue(distanceName, Types.VARCHAR),
								new DataValue(receiverFivecode, Types.VARCHAR),
								new DataValue(receiverSevencode, Types.VARCHAR),
								new DataValue(orderPackageId, Types.VARCHAR),
								new DataValue(greenworld_LogisticsId, Types.VARCHAR),
								new DataValue(greenworld_MerchantTradeno, Types.VARCHAR),
								new DataValue(greenworld_Validno, Types.VARCHAR),

						};
						InsBean ib1 = new InsBean("OC_SHIPMENT", columns1);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

						//托运单为空才记录
						if (expressNo.equals("")) 
						{
							if (ecPlatformNo.equals("shopee")) 
							{
								//过滤掉虾皮集成的物流厂商
								if (deliveryType.equals("") || deliveryType.equals("0") || deliveryType.equals("14")) 
								{
									Map<String, Object> lgMap=new HashMap<String, Object>();
									lgMap.put("eId", eId);
									lgMap.put("shopId", shopId);
									lgMap.put("organizationno", shopId);
									lgMap.put("shipmentno", shipmentNo);
									lgMap.put("lgplatformno", lgPlatFormNo);

									listLG.add(lgMap);
								}								
							}	
							else 
							{
								Map<String, Object> lgMap=new HashMap<String, Object>();
								lgMap.put("eId", eId);
								lgMap.put("shopId", shopId);
								lgMap.put("organizationno", shopId);
								lgMap.put("shipmentno", shipmentNo);
								lgMap.put("lgplatformno", lgPlatFormNo);

								listLG.add(lgMap);
							}
						}

						long nextShipmentNo;
						shipmentNo = shipmentNo.substring(3, shipmentNo.length());
						nextShipmentNo = Long.parseLong(shipmentNo) + 1;
						shipmentNo = nextShipmentNo + "";
						shipmentNo = "HYD" + shipmentNo;   

					}
				}

				//非新竹物流HTC，用箱号过滤， 有几个箱子就有几个货运单
				Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); 
				//过滤条件只设置托运单号：   假设当前订单的商品分分2个箱子，对新竹物流来说是1张托运单，其他物流是2张托运单
				// 有一种特殊情况： 新竹物流，多个商品，选择了不同的包裹，同一个托运单号。 这种情况需要前端控制，不能选择订单商品 ，或者只取最后一次的包裹信息
				condition2.put("boxNo", true);	
				List<Map<String, Object>> detailDatas=MapDistinct.getMap(datas, condition2);

				if(!lgPlatFormNo.equals("htc"))
				{
					//多发货单就是多箱处理
					totBoxes=detailDatas.size()+"";
					
					for (int k = 0; k < detailDatas.size(); k++) 
					{
						Map<String, Object> eDatas = detailDatas.get(k);
						expressNo = eDatas.get("expressNo").toString();
						packageNo = eDatas.get("packageNo").toString();
						
						//
						if (!eDatas.containsKey("measureNo")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的measureNo字段找不到！");		
							this.pData.clear();
							return;
						}
						
						if (!eDatas.containsKey("measureName")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的measureName字段找不到！");		
							this.pData.clear();
							return;
						}
						
						if (!eDatas.containsKey("temperateNo")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的temperateNo字段找不到！");		
							this.pData.clear();
							return;
						}
						
						if (!eDatas.containsKey("temperateName")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的temperateName字段找不到！");		
							this.pData.clear();
							return;
						}
						
						if (!eDatas.containsKey("transFee")) 
						{							
							res.setSuccess(false);
							res.setServiceStatus("100");
							res.setServiceDescription("前端req.getDatas中的transFee字段找不到！");		
							this.pData.clear();
							return;
						}
						
						
						measureNo = eDatas.get("measureNo").toString();						
						measureName = eDatas.get("measureName").toString();
						temperatelayerNo= eDatas.get("temperateNo").toString();
						temperatelayerName = eDatas.get("temperateName").toString();
						transFee = eDatas.get("transFee").toString();
						
						
						boxNo = eDatas.get("boxNo").toString();						
						
						expressBilltype=eDatas.get("expressBilltype").toString();
						receiverSiteno=eDatas.get("receiverSiteno").toString();
						senderSiteno=eDatas.get("senderSiteno").toString();
						distanceNo=eDatas.get("distanceNo").toString();
						distanceName=eDatas.get("distanceName").toString();
						receiverFivecode=eDatas.get("receiverFivecode").toString();
						receiverSevencode=eDatas.get("receiverSevencode").toString();
						
						
						if(k != 0){ // 如果是第一笔货运单， 已付金额和支付状态对应订单上； 不是第一笔，往后的单子，都设置为金额0，状态：已付清
							payAmt = "0";
							payStatus = "3";
						}

						for (Map<String, Object> oneData : datas) 
						{
							if(boxNo.equals(oneData.get("boxNo").toString()) )
							{
								String item = oneData.get("item").toString();
								String oItem = oneData.get("oItem").toString();
								String pluNo = oneData.get("pluNo").toString();
								String pluName = oneData.get("pluName").toString();

								String pluSql = "select a.pluNo ,  a.sunit, b.plubarcode  from DCP_GOODS a "
										+ " left join DCP_BARCODE b on a.EID = b.EID and a.pluNo = b.pluNo "
										+ " where a.EID = '"+eId+"'  "
										+ " and a.pluNo = '"+pluNo+"' "
										+ " and b.status='100' and a.status='100'";
								String[] conditionValues = {};
								List<Map<String, Object>> pluDatas = this.doQueryData(pluSql, conditionValues);
								if(pluDatas.isEmpty())
								{ //如果为空，则说明没有这个商品的信息；  不能加商品名称作为条件，可能有多语言， 名称不一样
									//									res.setSuccess(false);
									//									res.setServiceDescription("服务执行失败：编码为："+pluNo +" , 名称为："+ pluName+" 的商品信息不存在！！");

									throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "编码为："+pluNo +" , 名称为："+ pluName+" 的商品信息不存在！！");
									//return res;
								}
								else
								{
									for (Map<String, Object> pluData : pluDatas) 
									{
										String pluNo2 = pluData.get("PLUNO").toString();
										String pluBarCode = pluData.get("PLUBARCODE").toString();

										if(pluNo2== null || pluNo2.trim().equals("") || pluBarCode == null || pluBarCode.trim().equals("")){
											//											res.setSuccess(false);
											//											res.setServiceDescription("服务执行失败：编码为："+pluNo +" , 名称为："+ pluName+" 的商品信息不存在！！");
											//								              
											throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品编码为："+pluNo+"的商品信息不存在  或 对应的条码不存在");
										}

									}
								}

								String qty = oneData.get("qty").toString();
								String barcode = oneData.get("barcode").toString();
								String amt = oneData.get("amt").toString();

								String specName = oneData.get("specName").toString();
								String pickupQty = oneData.get("pickupQty").toString();
								
								if (PosPub.isNumericType(qty)==false ||PosPub.isNumericType(pickupQty)==false) 
								{
									res.setSuccess(false);
									res.setServiceStatus("100");
									res.setServiceDescription("前端req.getDatas中的qty、pickupQty必须是数字类型！");		
									this.pData.clear();
									return;
								}
								
								
								BigDecimal bdm1=new BigDecimal(qty);
								BigDecimal bdm2=new BigDecimal(pickupQty);
								if (bdm1.compareTo(bdm2)!=0) 
								{
									res.setSuccess(false);
									res.setServiceStatus("100");
									res.setServiceDescription("前端req.getDatas中的pickupQty必须等于qty！");		
									this.pData.clear();
									return;
								}
								
								int insColCt = 0;  
								String[] columnsName = {
										"EID","SHOPID","SHIPMENTNO","LGPLATFORMNO", "LGPLATFORMNAME",
										"ITEM","PLUNO","PLUNAME","PLUBARCODE","SPECNAME","QTY",
										"AMT","EC_ORDERNO","ECPLATFORMNO","ECPLATFORMNAME","ORIGINALITEM"
								};

								DataValue[] columnsVal = new DataValue[columnsName.length];
								for (int i = 0; i < columnsVal.length; i++) { 
									String keyVal = null;
									switch (i) { 
									case 0:
										keyVal = eId;
										break;
									case 1:
										keyVal = shopId;
										break;
									case 2:
										keyVal = shipmentNo;
										break;
									case 3:
										keyVal = lgPlatFormNo;
										break;
									case 4:
										keyVal = lgPlatFormName;
										break;
									case 5:
										keyVal = item;
										break;
									case 6:
										keyVal = pluNo;
										break;
									case 7:
										keyVal = pluName;
										break;
									case 8:
										keyVal = barcode;
										break;
									case 9:
										keyVal = specName;
										break;
									case 10:
										keyVal = qty;
										break;
									case 11:
										keyVal = amt;
										break;
									case 12:
										keyVal = ecOrderNo;
										break;
									case 13:
										keyVal = ecPlatformNo;
										break;
									case 14:
										keyVal = ecPlatformName;
										break;
									case 15:
										keyVal = oItem;
										break;
									default:
										break;
									}
									if (keyVal != null) 
									{
										insColCt++;
										columnsVal[i] = new DataValue(keyVal, Types.VARCHAR);
									} 
									else 
									{
										columnsVal[i] = null;
									}


								}
								String[] columns2  = new String[insColCt];
								DataValue[] insValue2 = new DataValue[insColCt];
								// 依照傳入參數組譯要insert的欄位與數值；
								insColCt = 0;

								for (int j=0;j<columnsVal.length;j++){
									if (columnsVal[j] != null){
										columns2[insColCt] = columnsName[j];
										insValue2[insColCt] = columnsVal[j];
										insColCt ++;
										if (insColCt >= insValue2.length) 
											break;
									}
								}
								InsBean ib2 = new InsBean("OC_SHIPMENT_ORIGINAL", columns2);
								ib2.addValues(insValue2);
								this.addProcessData(new DataProcessBean(ib2));
							}

						}
						insValue1 = new DataValue[]{
								new DataValue(eId, Types.VARCHAR), 
								new DataValue(shopId, Types.VARCHAR),
								new DataValue(shipmentNo, Types.VARCHAR),
								new DataValue(lgPlatFormNo, Types.VARCHAR),
								new DataValue(lgPlatFormName, Types.VARCHAR),
								new DataValue(ecStatus, Types.VARCHAR),//
								new DataValue(sDate, Types.VARCHAR),
								new DataValue(sTime, Types.VARCHAR),

								new DataValue(orderer, Types.VARCHAR), 
								new DataValue(orderPhone, Types.VARCHAR),
								new DataValue(receiver, Types.VARCHAR),
								new DataValue(receiverMobile, Types.VARCHAR),
								new DataValue(receiverPhone, Types.VARCHAR),
								new DataValue(receiverAddress, Types.VARCHAR),
								new DataValue(receiverEmail, Types.VARCHAR),
								new DataValue(memo, Types.VARCHAR),
								new DataValue(getShop, Types.VARCHAR),
								new DataValue(getShopName, Types.VARCHAR),
								new DataValue(totBoxes, Types.VARCHAR),
								new DataValue(shipDate, Types.VARCHAR),
								new DataValue(shipHourType, Types.VARCHAR),

								new DataValue(expressNo, Types.VARCHAR),
								new DataValue(packageNo, Types.VARCHAR),
								new DataValue(measureNo, Types.VARCHAR),
								new DataValue(measureName, Types.VARCHAR),
								new DataValue(temperatelayerNo, Types.VARCHAR),
								new DataValue(temperatelayerName, Types.VARCHAR),
								new DataValue(transFee, Types.VARCHAR),
								new DataValue(boxNo, Types.VARCHAR),
								new DataValue(ecLoadType, Types.VARCHAR),
								new DataValue(ecOrderNo, Types.VARCHAR),
								new DataValue(payStatus, Types.VARCHAR),
								new DataValue(payAmt, Types.VARCHAR),
								new DataValue(ecPlatformNo, Types.VARCHAR),
								new DataValue(ecPlatformName, Types.VARCHAR),
								new DataValue(deliveryType, Types.VARCHAR),
								new DataValue(collectAmt, Types.VARCHAR),

								new DataValue(shopeeMode, Types.VARCHAR),
								new DataValue(shopeeAddressId, Types.VARCHAR),
								new DataValue(shopeePickuptimeId, Types.VARCHAR),
								new DataValue(shopeeBranchId, Types.VARCHAR),
								new DataValue(shopeeSenderRealName, Types.VARCHAR),
								
								new DataValue(expressBilltype, Types.VARCHAR),
								new DataValue(receiverSiteno, Types.VARCHAR),
								new DataValue(senderSiteno, Types.VARCHAR),
								new DataValue(distanceNo, Types.VARCHAR),
								new DataValue(distanceName, Types.VARCHAR),
								new DataValue(receiverFivecode, Types.VARCHAR),
								new DataValue(receiverSevencode, Types.VARCHAR),
								new DataValue(orderPackageId, Types.VARCHAR),
								new DataValue(greenworld_LogisticsId, Types.VARCHAR),
								new DataValue(greenworld_MerchantTradeno, Types.VARCHAR),
								new DataValue(greenworld_Validno, Types.VARCHAR),


						};
						InsBean ib1 = new InsBean("OC_SHIPMENT", columns1);
						ib1.addValues(insValue1);
						this.addProcessData(new DataProcessBean(ib1)); // 新增單頭

						//托运单为空才记录
						if (expressNo.equals("")) 
						{

							if (ecPlatformNo.equals("shopee")) 
							{
								//过滤掉虾皮集成的物流厂商
								if (deliveryType.equals("") || deliveryType.equals("0") || deliveryType.equals("14")) 
								{
									Map<String, Object> lgMap=new HashMap<String, Object>();
									lgMap.put("eId", eId);
									lgMap.put("shopId", shopId);
									lgMap.put("organizationno", shopId);
									lgMap.put("shipmentno", shipmentNo);
									lgMap.put("lgplatformno", lgPlatFormNo);

									listLG.add(lgMap);
								}
							}	
							else 
							{
								Map<String, Object> lgMap=new HashMap<String, Object>();
								lgMap.put("eId", eId);
								lgMap.put("shopId", shopId);
								lgMap.put("organizationno", shopId);
								lgMap.put("shipmentno", shipmentNo);
								lgMap.put("lgplatformno", lgPlatFormNo);

								listLG.add(lgMap);
							}
						}

						long nextShipmentNo;
						shipmentNo = shipmentNo.substring(3, shipmentNo.length());
						nextShipmentNo = Long.parseLong(shipmentNo) + 1;
						shipmentNo = nextShipmentNo + "";
						shipmentNo = "HYD" + shipmentNo;   

					}
				}

			}	

			// 更新 订单状态 为 13 : 已点货
			UptBean ub1 = null;	
			ub1 = new UptBean("OC_ORDER");
			// Value
			ub1.addUpdateValue("STATUS", new DataValue(13, Types.VARCHAR));
			
			String hPackageno=req.getPackageNo()==null?"":req.getPackageNo();			
			String hPackagename=req.getPackageName()==null?"":req.getPackageName();
			String hMeasureno=req.getMeasureNo()==null?"":req.getMeasureNo();
			String hMeasurename=req.getMeasureName()==null?"":req.getMeasureName();
			String hTemperateno=req.getTemperateNo()==null?"":req.getTemperateNo();
			String hTemperatename=req.getTemperateName()==null?"":req.getTemperateName();
						
			ub1.addUpdateValue("PACKAGENO", new DataValue(hPackageno, Types.VARCHAR));
			ub1.addUpdateValue("PACKAGENAME", new DataValue(hPackagename, Types.VARCHAR));
			ub1.addUpdateValue("MEASURENO", new DataValue(hMeasureno, Types.VARCHAR));
			ub1.addUpdateValue("MEASURENAME", new DataValue(hMeasurename, Types.VARCHAR));
			ub1.addUpdateValue("TEMPERATELAYERNO", new DataValue(hTemperateno, Types.VARCHAR));
			ub1.addUpdateValue("TEMPERATELAYERNAME", new DataValue(hTemperatename, Types.VARCHAR));
			ub1.addUpdateValue("TOT_BOXES", new DataValue(totBoxes, Types.INTEGER));//此字段标明是否多张发货单
			
			// condition	
			ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
			ub1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));

			this.addProcessData(new DataProcessBean(ub1));			

			//訂單日誌時間
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sysDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String orderStatusLogTimes=sysDatetime.format(calendar.getTime());
			
			//接單日誌
			String[] columnsORDER_STATUSLOG = 
				{ 
						"EID", 
						"ORGANIZATIONNO", 
						"SHOPID", 
						"ORDERNO", 
						"LOAD_DOCTYPE",
						"STATUSTYPE", 
						"STATUSTYPENAME", 
						"STATUS", 
						"STATUSNAME", 
						"NEED_NOTIFY", 
						"NOTIFY_STATUS",
						"NEED_CALLBACK", 
						"CALLBACK_STATUS", 
						"OPNO", 
						"OPNAME", 
						"UPDATE_TIME",
						"MEMO", 
						"STATUS" 
				};
			
			//接單日誌
			DataValue[] insValueOrderStatus_LOG = new DataValue[] 
					{ 
							new DataValue(eId, Types.VARCHAR),
							new DataValue(shopId, Types.VARCHAR), // 组织编号=门店编号
							new DataValue(shopId, Types.VARCHAR), // 映射后的门店
							new DataValue(ecOrderNo, Types.VARCHAR), //
							new DataValue(ecLoadType, Types.VARCHAR), //電商平台
							new DataValue("1", Types.VARCHAR), // 状态类型 // 1-订单状态，2-配送状态，3-退单状态，4-其他
							new DataValue("訂單狀態", Types.VARCHAR), // 状态类型名称
							new DataValue("13", Types.VARCHAR), // 状态 0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
							new DataValue("已點貨", Types.VARCHAR), // 状态名称
							new DataValue("N", Types.VARCHAR), // 是否通知云pos,N-不需要调用，Y-需要
							new DataValue("0", Types.VARCHAR), // 通知云pos状态返回，0-未通知，1-已通知
							new DataValue("N", Types.VARCHAR), // 是否调用第三方接口，N-不需要调用，Y-需要
							new DataValue("0", Types.VARCHAR), // 调用第三方接口的返回状态，0-未调用，1-调用成功，2-调用失败
							new DataValue("admin", Types.VARCHAR), //操作員編碼
							new DataValue("管理員", Types.VARCHAR), //操作員名稱
							new DataValue(orderStatusLogTimes, Types.VARCHAR), //yyyyMMddHHmmssSSS
							new DataValue("訂單狀態-->已點貨", Types.VARCHAR), //類型名稱+"-->"+狀態名稱
							new DataValue("100", Types.VARCHAR) 
					};
			InsBean ibOrderStatusLog = new InsBean("OC_ORDER_STATUSLOG", columnsORDER_STATUSLOG);
			ibOrderStatusLog.addValues(insValueOrderStatus_LOG);
			this.addProcessData(new DataProcessBean(ibOrderStatusLog));
			
			this.doExecuteDataToDB();//取物流单也要产生出货单信息
			
			//调用JOB处理托运单必须单据已保存，不然JOB中SQL无法查询到
			//********************************************
			//如果托运单号为空，调用API接口申请
			String sRet="";
			for (int pi = 0; pi < listLG.size(); pi++) 
			{
				String sEId=listLG.get(pi).get("eId").toString();
				String sshop=listLG.get(pi).get("shopId").toString();
				String sorganizationno=listLG.get(pi).get("organizationno").toString();
				String sshipmentno=listLG.get(pi).get("shipmentno").toString();
				String slgplatformno=listLG.get(pi).get("lgplatformno").toString();				

				LgGetExpressNo lg=new LgGetExpressNo(sEId,sshop,sorganizationno,sshipmentno,slgplatformno); 
				sRet= lg.doExe();	
				
				if (sRet.equals("")==false) 
				{
					break;
				}
			}		
			
			//取物流单号失败,不算点货成功,理论上应该先取物流單號,這裡有點事後諸葛亮
			if (sRet.equals("")==false) 
			{				
				//
				DelBean db1 = new DelBean("OC_SHIPMENT_ORIGINAL");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));				
				db1.addCondition("EC_ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));				
				//
				db1 = new DelBean("OC_SHIPMENT");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("EC_ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//更新回去
				ub1 = null;	
				ub1 = new UptBean("OC_ORDER");
				// Value
				ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));		
				
				
				// condition	
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));	
				this.addProcessData(new DataProcessBean(ub1));		
				
				//刪點貨日誌
				db1 = new DelBean("OC_ORDER_STATUSLOG");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
				db1.addCondition("LOAD_DOCTYPE", new DataValue(ecLoadType, Types.VARCHAR));
				db1.addCondition("STATUS", new DataValue("13", Types.VARCHAR));
				db1.addCondition("UPDATE_TIME", new DataValue(orderStatusLogTimes, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				
				this.doExecuteDataToDB();
				
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("点货失败原因：取物流单号失败<br/>" +sRet );		
				this.pData.clear();
				return;
			}


			//***************调用订转销服务OrderToSaleProcess*************

			sql = this.getOrderDetail(eId, shopId, ecOrderNo, ecPlatformNo);
			String[] conditionValues = {};
			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);

			//订转销成功
			boolean success =false;
			String errDesc="";
			
			if(getDatas.size() > 0)
			{

				JSONObject reqOTS = new JSONObject();
				JSONArray datasArr = new JSONArray(); 
				JSONObject header = new JSONObject(); // 存一笔资料（包括单头加单身）
				JSONArray detailArr = new JSONArray(); 
				JSONArray agioArr = new JSONArray(); 
				JSONArray payArr = new JSONArray(); 
				JSONArray invArr = new JSONArray(); 				

				//青哥要orderId=订单号
				String orderId = ecOrderNo;//getDatas.get(0).get("ORDERID").toString();
				String contMan = getDatas.get(0).get("CONTMAN").toString();
				String contTel = getDatas.get(0).get("CONTTEL").toString();
				String address = getDatas.get(0).get("ADDRESS").toString();
				memo = getDatas.get(0).get("MEMO").toString();
				String orderSN = getDatas.get(0).get("ORDERSN").toString();
				String isInvoice = getDatas.get(0).get("ISINVOICE").toString();

				//给个默认值即可
				if(isInvoice == null || isInvoice.trim().equals("")){
					isInvoice = "N";
				}

				String shipFee = getDatas.get(0).get("SHIPFEE").toString();
				totAmt = getDatas.get(0).get("TOTAMT").toString();
				String totQty = getDatas.get(0).get("TOTQTY").toString();
				String serviceCharge = getDatas.get(0).get("SERVICECHARGE").toString();
				String inComeAmt = getDatas.get(0).get("INCOMEAMT").toString();
				String totDisc = getDatas.get(0).get("TOTDISC").toString();
				String sellerDisc = getDatas.get(0).get("SELLERDISC").toString();
				String platformDisc = getDatas.get(0).get("PLATFORMDISC").toString();
				String sellNo = getDatas.get(0).get("SELLNO").toString();
				deliveryType = getDatas.get(0).get("DELIVERYTYPE").toString();
				payStatus = getDatas.get(0).get("PAYSTATUS").toString();
				payAmt = getDatas.get(0).get("PAYAMT").toString();
				String passport = getDatas.get(0).get("PASSPORT").toString();
				String freeCode = getDatas.get(0).get("FREECODE").toString();
				String ecCustomerNo = getDatas.get(0).get("ECCUSTOMERNO").toString();

				String token = req.getToken();

				reqOTS.put("serviceId","DCP_OrderToSaleProcess");
				reqOTS.put("docType","1");
				reqOTS.put("token",token);

				header.put("oEId", eId);
				header.put("oShopId", shipShop);//配送门店
				header.put("bookShopNO", shopId);
				header.put("orderID", orderId);
				header.put("orderNO", ecOrderNo);
				// 单据日期不用传， 直接取系统日期即可
				header.put("o_opNO", opNo);
				header.put("type", "0");// 单据类型： 0 销售单， 1退货
				header.put("loadDocType", ecPlatformNo);
				header.put("shippingShopNO", getShop);
				header.put("contMan", contMan);
				header.put("contTel", contTel);
				header.put("address", address);
				header.put("memo", memo);
				header.put("orderSN", orderSN); 
				header.put("isInvoice", isInvoice);
				header.put("shipFee", shipFee);
				header.put("totAmt", totAmt);
				header.put("totQty", totQty);
				header.put("serviceCharge", serviceCharge);
				header.put("inComeAmt", inComeAmt);//实收金额 
				header.put("totDisc", totDisc);   

				header.put("sellerDisc", sellerDisc); // 商户优惠总额
				header.put("platformDisc", platformDisc); // 平台优惠总额
				header.put("sellNO", sellNo); //订单大客户编号（赊销客户）
				header.put("deliveryType", deliveryType); 
				header.put("payStatus", payStatus); 
				header.put("payamt", payAmt); 
				header.put("passport", passport); 
				header.put("freeCode", freeCode); 
				header.put("ecCustomerNO", ecCustomerNo); 

				Map<String, Boolean> conditionPluDetail = new HashMap<String, Boolean>(); 
				conditionPluDetail.put("DETAILITEM", true);	
				conditionPluDetail.put("PLUNO", true);	
				List<Map<String, Object>> pluDatas=MapDistinct.getMap(getDatas, conditionPluDetail);

				int invItemStr = 1;  /// 逻辑上 invItemStr 不应该程序设置，应该是接口查点发票的item
				for (Map<String, Object> map : pluDatas) 
				{
					String item = map.get("DETAILITEM").toString();
					String pluNo = map.get("PLUNO").toString();
					String pluBarcode = map.get("PLUBARCODE").toString();
					String price = map.get("PRICE").toString();
					String qty = map.get("QTY").toString();
					String amt = map.get("AMT").toString();
					String disc = map.get("DISC").toString();
					String isGift = "N"; //map.get("ISGIFT").toString();
					String isPackage = "N"; //map.get("ISPACKAGE").toString();
					String dealType = "1"; //map.get("DEALTYPE").toString();
					String giftReason = ""; //map.get("GIFTREASON").toString();
					String invItem = invItemStr+""; //map.get("INVITEM").toString();
					String couponInvTax = "0"; //map.get("COUPONINVTAX").toString();

					JSONObject plu_detail = new JSONObject();
					plu_detail.put("item", item);
					plu_detail.put("pluNO", pluNo);
					plu_detail.put("pluBarcode", pluBarcode);
					plu_detail.put("price", price);
					plu_detail.put("qty", qty);
					plu_detail.put("disc", disc);
					plu_detail.put("amt", amt);
					plu_detail.put("isGift", isGift);
					plu_detail.put("isPackage", isPackage);
					plu_detail.put("dealType", dealType);
					plu_detail.put("giftReason", giftReason);
					plu_detail.put("invItem", invItem);
					plu_detail.put("couponInvTax", couponInvTax);

					detailArr.put(plu_detail);

					invItemStr +=1;
				}

				Map<String, Boolean> conditionAgio = new HashMap<String, Boolean>(); 
				conditionAgio.put("AGIOITEM", true);	
				List<Map<String, Object>> agioDatas=MapDistinct.getMap(getDatas, conditionAgio);
				for (Map<String, Object> map : agioDatas) 
				{
					String agioItem = map.get("AGIOITEM").toString();
					String promName = map.get("PROMNAME").toString();
					String agioAmt = map.get("AGIOAMT").toString();
					String agioSellerDisc = map.get("AGIOSELLERDISC").toString();
					String agioPlatformDisc = map.get("AGIOPLATFORMDISC").toString();

					JSONObject agio_detail = new JSONObject();
					agio_detail.put("item", agioItem);
					agio_detail.put("promName", promName);
					agio_detail.put("agioAmt", agioAmt);
					agio_detail.put("sellerDisc", agioSellerDisc);
					agio_detail.put("platformDisc", agioPlatformDisc);

					agioArr.put(agio_detail);

				}

				Map<String, Boolean> conditionPay = new HashMap<String, Boolean>(); 
				conditionPay.put("PAYITEM", true);	
				List<Map<String, Object>> payDatas=MapDistinct.getMap(getDatas, conditionPay);
				for (Map<String, Object> map : payDatas) 
				{
					String payItem = map.get("PAYITEM").toString();
					String payCode = map.get("PAYCODE").toString();
					String payCodeErp = map.get("PAYCODEERP").toString();
					// payCode 可能为空， 如果为空的话， 该节点不用再给值
					if(payCode.equals("") || payCode == null || payCodeErp.equals("") || payCodeErp == null){
						continue;
					}

					String payName = map.get("PAYNAME").toString();
					// payName 如果出现payName 为空的话，说明付款方式资料有问题。
					// 不能加验证是否为空， 要保证点货能走完。 

					String isOnlinePay = map.get("ISONLINEPAY").toString();
					String pay = map.get("PAY").toString();

					JSONObject pay_detail = new JSONObject();
					pay_detail.put("item", payItem);
					pay_detail.put("payCode", payCode);
					pay_detail.put("payCodeErp", payCodeErp);
					pay_detail.put("payName", payName);
					pay_detail.put("isOnlinePay", isOnlinePay);
					pay_detail.put("canInvoice", map.get("CANINVOICE").toString());
					pay_detail.put("invstartNo", map.get("DINVSTARTNO").toString());
					pay_detail.put("isOrderPay", map.get("ISORDERPAY").toString());
					
					pay_detail.put("pay", pay);
					payArr.put(pay_detail);
				}

				Map<String, Boolean> conditionInv = new HashMap<String, Boolean>(); 
				conditionInv.put("INVOICEITEM", true);	
				List<Map<String, Object>> invDatas=MapDistinct.getMap(getDatas, conditionInv);
				for (Map<String, Object> map : invDatas) 
				{
					String invoiceItem = map.get("INVOICEITEM").toString();
					String recordtype = map.get("RECORDTYPE").toString();
					String taxationtype = map.get("TAXATIONTYPE").toString();
					String invtype = map.get("INVTYPE").toString();
					String invformat = map.get("INVFORMAT").toString();
					String buyerguino = map.get("BUYERGUINO").toString();
					String gftinvamt = map.get("GFTINVAMT").toString();
					String gftinvtax = map.get("GFTINVTAX").toString();
					String carriercode = map.get("CARRIERCODE").toString();
					String carriershowid = map.get("CARRIERSHOWID").toString();
					String carrierhiddenid = map.get("CARRIERHIDDENID").toString();
					String lovecode = map.get("LOVECODE").toString();
					String randomcode = map.get("RANDOMCODE").toString();
					String printcount = map.get("PRINTCOUNT").toString();
					String rebateno = map.get("REBATENO").toString();
					String invalidOp = map.get("INVALIDOP").toString();
					String sellerGuiNO = map.get("SELLERGUINO").toString();

					String invalidcode = map.get("INVALIDCODE").toString();
					String invmemo = map.get("INVMEMO").toString();
					String osdate = map.get("OSDATE").toString();

					JSONObject inv_detail = new JSONObject();
					inv_detail.put("item", invoiceItem);
					inv_detail.put("recordType", recordtype);
					inv_detail.put("taxAtionType", taxationtype);
					inv_detail.put("invType", invtype);
					inv_detail.put("invFormat", invformat);
					inv_detail.put("buyerGuiNO", buyerguino);

					inv_detail.put("gftInvAmt", gftinvamt);
					inv_detail.put("gftInvTax", gftinvtax);
					inv_detail.put("carrierCode", carriercode);
					inv_detail.put("carrierShowID", carriershowid);
					inv_detail.put("carrierHiddenID", carrierhiddenid);
					inv_detail.put("loveCode", lovecode);

					inv_detail.put("randomCode", randomcode);
					inv_detail.put("printCount", printcount);
					inv_detail.put("rebateNO", rebateno);
					// 大爷规格上新增参数。invalidOp 作废人员
					inv_detail.put("invalidOp", invalidOp);
					inv_detail.put("sellerGuiNO", sellerGuiNO);

					inv_detail.put("invalidCode", invalidcode);
					inv_detail.put("invMemo", invmemo);
					inv_detail.put("osDate", osdate);

					invArr.put(inv_detail);
				}

				header.put("detail", detailArr);
				header.put("agio", agioArr);
				header.put("pay", payArr);
				header.put("invoiceDetail", invArr);
				datasArr.put(header);
				reqOTS.put("datas", datasArr);

				String sReturnInfo = "";
				String str = reqOTS.toString();// 将json对象转换为字符串						

				PosPub.WriteETLJOBLog("點貨訂轉銷發送： "+ str);
				
				logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"******点货确定时订转销处理OrderToSaleProcessServiceImp传入参数：  " + str + "\r\n");

				//执行请求操作，并拿到结果（同步阻塞）
				try 
				{
					//编码处理
//					str=URLEncoder.encode(str,"UTF-8");
//					
//					String resbody = HttpSend.Sendcom(str,reqUrl);
					DispatchService ds = DispatchService.getInstance();
					String resbody = ds.callService(str, this.dao);					
					
					PosPub.WriteETLJOBLog("點貨訂轉銷返回： "+ resbody);
					
					JSONObject jsonres = new JSONObject(resbody);				
					
					success = jsonres.getBoolean("success");
					errDesc=jsonres.get("serviceDescription").toString();
					
					if(success)
					{
						logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "点货确定时 订转销处理, 转销售单成功，订单号="+ecOrderNo);
					}
					else
					{
						logger.info("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"" + "点货确定时 订转销处理失败：" + "\r\n 订单号="+ ecOrderNo + "\r\n" + resbody + "\r\n");
					}
				}
				catch (Exception e) 
				{
					//
					sReturnInfo="错误信息:" + e.getMessage();

					//System.out.println(e.toString());

					logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"服务***点货确定时订转销处理 OrderECSalePickupCreateDCP 返回参数 ：门店=" +shopId+ "公司编码=" +eId +" \n 单号="  + ecOrderNo  + e.toString());
				}

			}

			//
			if (success) 
			{
				res.setSuccess(true);
				res.setServiceStatus("000");
				res.setServiceDescription("服务执行成功");				
			}
			else 
			{			
				//
				DelBean db1 = new DelBean("OC_SHIPMENT_ORIGINAL");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));				
				db1.addCondition("EC_ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));				
				//
				db1 = new DelBean("OC_SHIPMENT");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("EC_ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				//更新回去
				ub1 = null;	
				ub1 = new UptBean("OC_ORDER");
				// Value
				ub1.addUpdateValue("STATUS", new DataValue(status, Types.VARCHAR));
				// condition	
				ub1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				ub1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));	
				this.addProcessData(new DataProcessBean(ub1));		
				
				//刪點貨日誌
				db1 = new DelBean("OC_ORDER_STATUSLOG");
				db1.addCondition("EID", new DataValue(eId, Types.VARCHAR));
				db1.addCondition("SHOPID", new DataValue(shopId, Types.VARCHAR));
				db1.addCondition("ORDERNO", new DataValue(ecOrderNo, Types.VARCHAR));
				db1.addCondition("LOAD_DOCTYPE", new DataValue(ecLoadType, Types.VARCHAR));
				db1.addCondition("STATUS", new DataValue("13", Types.VARCHAR));
				db1.addCondition("UPDATE_TIME", new DataValue(orderStatusLogTimes, Types.VARCHAR));
				this.addProcessData(new DataProcessBean(db1));
				
				this.doExecuteDataToDB();
				
				res.setSuccess(false);
				res.setServiceStatus("100");
				res.setServiceDescription("点货失败原因：订转销失败:<br/>" + errDesc);			
				this.pData.clear();
				
			}
		} 
		catch (Exception ex) 
		{
			this.pData.clear(); //清空之前残留

			StringWriter errors = new StringWriter();
			PrintWriter pw=new PrintWriter(errors);
			ex.printStackTrace(pw);			
			pw.close();			
			errors.close();

			logger.error("\r\nrequestId="+req.getRequestId()+",plantType="+req.getPlantType()+",version="+req.getVersion()+","+"服务*** OrderECSalePickupCreateDCP 报错 ：门店=" +req.getShopId()+ "公司编码=" +req.geteId() +" \n 电商单号="  + req.getEcOrderNo()  + " \n" +errors.toString());

			String err="";
			if (ex.getMessage()==null) 
			{
				String[] sSplitStrings=errors.toString().split("\r\n");
				err=sSplitStrings[0];
			}
			else 
			{				
				err=ex.getMessage();
			}
			res.setSuccess(false);
			res.setServiceStatus("100");
			res.setServiceDescription(err);				
		}

	}

	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderECSalePickupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderECSalePickupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderECSalePickupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderECSalePickupCreateReq req) throws Exception {
		// TODO Auto-generated method stub
		boolean isFail = false;
		//		StringBuffer errMsg = new StringBuffer("");
		//		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；
		//		String lgPlatFormNo = req.getLgPlatformNo().toString();
		//		String lgPlatFormName = req.getLgPlatformName().toString();
		//		if (Check.Null(lgPlatFormNo)) 
		//		{
		//			errCt++;
		//			errMsg.append("货运厂商编号不可为空值  ");
		//			isFail = true;
		//		}
		//		if (Check.Null(lgPlatFormName)) 
		//		{
		//			errCt++;
		//			errMsg.append("货运厂商名称不可为空值  ");
		//			isFail = true;
		//		}
		//		if (isFail)
		//		{
		//			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		//		}
		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderECSalePickupCreateReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECSalePickupCreateReq>(){};
	}

	@Override
	protected DCP_OrderECSalePickupCreateRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECSalePickupCreateRes();
	}

	/**
	 * 获取门店最大货运单号
	 * @param req
	 * @return
	 * @throws Exception
	 */
	private String getShipmentNO(DCP_OrderECSalePickupCreateReq req,String shopId) throws Exception  {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如HYD201607010001)，流水号取门店该单据最大流水号+1)
		 */
		String sql = null;
		String shipmentNo = null;
		String eId = req.geteId();
		StringBuffer sqlbuf = new StringBuffer("");
		String bDate = PosPub.getAccountDate_SMS(dao, eId, shopId);

		String[] conditionValues = { eId, shopId }; // 查询要货单号
		shipmentNo = "HYD" + bDate;
		sqlbuf.append("" + "select shipmentNo  from ( " + "select max(shipmentNo) as shipmentNo "
				+ "  from OC_SHIPMENT " + " where EID = ? " + " and SHOPID = ? "
				+ " and shipmentNo like '%%" + shipmentNo + "%%' "); // 假資料
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		List<Map<String, Object>> getQData = this.doQueryData(sql, conditionValues);

		if (getQData != null && getQData.isEmpty() == false) {
			shipmentNo = (String) getQData.get(0).get("SHIPMENTNO");
			if (shipmentNo != null && shipmentNo.length() > 0) {
				long i;
				shipmentNo = shipmentNo.substring(3, shipmentNo.length());
				i = Long.parseLong(shipmentNo) + 1;
				shipmentNo = i + "";
				shipmentNo = "HYD" + shipmentNo;    
			} 
			else {
				shipmentNo = "HYD" + bDate + "00001";
			}
		} 
		else {
			shipmentNo = "HYD" + bDate + "00001";
		}

		return shipmentNo;
	}


	private String getOrderDetail(String eId, String shopId, String orderNo, String ecPlatformNo){
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer();
		sqlbuf.append(" SELECT a.EID , a.SHOPID , a.order_Id  AS orderId , a.order_SN as orderSN , a.orderNo , a.sdate AS bdate , a.load_docType , "
				+ " a.shippingshop AS shippingshopNo , a.contman , a.conttel, a.address , a.memo , a.order_sn, a.isinvoice,  "
				+ " a.shipfee , a.tot_amt AS totAmt  ,  a.tot_qty  AS totQty , a.servicecharge , "
				+ " a.incomeamt , a.tot_disc AS totDisc , a.seller_disc AS sellerDisc , a.platform_disc AS platformDisc , a.sellno , a.deliverytype , "
				+ " a.paystatus , a.payamt , a.passport   ,a.freeCode , a.eccustomerno  ,"
				+ " b.item AS detailItem , b.pluNo ,b.plubarcode , b.price ,  b.qty ,  b.disc ,  b.amt ,  "
				+ " c.item AS agioItem , c.promname , c.agioamt ,  c.seller_disc AS agioSellerDisc , c.platform_disc AS agioPlatformDisc ,"
				+ " d.item AS payItem ,  d.paycode ,   d.paycodeerp , d.payname ,  d.isonlinepay ,  d.pay , d.ISORDERPAY, "
				+ " '1' AS invoiceItem ,  '0' as recordtype , '0' as taxationtype ,  '7' as invtype ,  '' as  invformat , "
				+ " '' as buyerguino , '0' as gftinvamt ,  '0' as gftinvtax ,  a.carriercode , a.carriershowid ,  a.carrierhiddenid ,"
				+ " a.lovecode ,  '' as randomcode ,  '' as printcount,  '' as rebateno , '' as invalidop, '' as sellerguiNO,  '' as invalidcode ,  '' as invmemo , '' as osdate,d.CanInvoice,d.INVOICENO DINVSTARTNO    "
				+ " FROM  OC_order a  "
				+ " LEFT JOIN OC_order_detail b ON a.EID = b.EID AND  a.SHOPID = b.SHOPID AND a.orderno = b.orderNo "
				+ " LEFT JOIN OC_Order_agio c  ON a.EID = c.EID AND a.SHOPID = b.SHOPID AND a.orderNo = c.orderNo "
				+ " LEFT JOIN OC_order_pay d  ON a.EID = d.EID AND a.SHOPID = d.SHOPID AND a.orderNo = d.orderNo "
				+ " WHERE a.EID = '"+eId+"' AND a.orderno = '"+orderNo+"' "
				+ " and a.load_docType = '"+ecPlatformNo+"' "
				+ " ORDER BY pluNo ");

		sql = sqlbuf.toString();
		return sql;
	}


}
