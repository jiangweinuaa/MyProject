package com.dsc.spos.scheduler.job;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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


import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.ExecBean;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.hll.api.HllClient;
import com.dsc.spos.hll.api.response.HllFoodDetail;
import com.dsc.spos.hll.api.response.HllOrderDetail;
import com.dsc.spos.hll.api.response.HllPageResponse;
import com.dsc.spos.hll.api.response.HllPayDetail;
import com.dsc.spos.hll.api.response.HllShopBillDetailResponse;
import com.dsc.spos.hll.api.util.HllUtils;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HllShopBillDetail extends InitJob 
{
	public static String loggerInfoTitle = "获取哗啦啦历史账单数据";
	
	public static String logFileName = "HllShopBillJob";
	
	/**
	 * 企业编号 给固定值99
	 */
	public static String eId="99";
	
	public static String errorUnit="无单位资料";
//	
	public static String errorPluNo="无商品编码资料";
//	
	public static String errorPayCode="无支付方式资料";
	
	/**
	 * 除法保留小数位数
	 */
	public static int scale=4;
	
	//一天执行一次
	static boolean bFirst=true;

	static String sDate="";
	
	static boolean bRun = false;// 标记此服务是否正在执行中
	
	public HllShopBillDetail()
	{
	}


	public String doExe() 
	{
		writelogFileName("\r\n***************"+loggerInfoTitle+"START****************\r\n");

		if (bRun) {
			writelogFileName("\r\n***************" + loggerInfoTitle + " 同步正在执行中,本次调用取消****************\r\n");
			writelogFileName("\r\n***************" + loggerInfoTitle + " END****************\r\n");
			return null;
		}
		bRun = true;
		//返回信息
		String sReturnInfo="";

		Calendar cal = Calendar.getInstance();// 获得当前时间
		SimpleDateFormat dfDate = new SimpleDateFormat("yyyyMMdd");
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		
		String thisDate="";
		List<Map<String, Object>> baseListBase=new ArrayList<Map<String, Object>>();
		try{
			//Platform_BaseSet查日期，如果能查找到，则同步此日期的单据，否则同步当天单据
			String sqlBase = "select ITEMVALUE from Platform_BaseSet where ITEM='HllOrderDate' and status='100' ";
			String[] conditionValuesBase = {};
			baseListBase = StaticInfo.dao.executeQuerySQL(sqlBase, conditionValuesBase);
			if (baseListBase != null && baseListBase.size()>0 ) {
				thisDate=baseListBase.get(0).get("ITEMVALUE").toString();
			}
		}catch(Exception e){
			
		}
		
		//每天4点到5点同步前一天的资料
		if((hour>=4&&hour<6)||(baseListBase != null && baseListBase.size()>0 )){
			if(baseListBase != null && baseListBase.size()>0 ){
				//使用设定日期
			}else{
				//得到前一天
				cal.add(Calendar.DATE, -1);
				//获取当前日期 
				thisDate=dfDate.format(cal.getTime());
			}
			String pageSize="5";
			
			try{
				
				//ORG_FORM='2' 代表门店
				//根据顾问要求，暂时只上线两家门店，特作出限制  001-总部测试   1007，1011,001
				String sql = "select ORGANIZATIONNO,THIRD_SHOP from DCP_ORG where status='100' and ORG_FORM='2' and eid=?  and THIRD_SHOP is not null ";
				String[] conditionValues = {eId};
				List<Map<String, Object>> shopList = StaticInfo.dao.executeQuerySQL(sql, conditionValues);
				if (shopList != null && shopList.size()>0 ) {
					Map<String, Object> thirdShopMap=new HashMap<String, Object>();
					for (Map<String, Object> shopMap : shopList) {
						Object thirdShopObj=shopMap.get("THIRD_SHOP");
						if(thirdShopObj!=null&&!thirdShopObj.toString().isEmpty()){
							thirdShopMap.put(thirdShopObj.toString(), shopMap.get("ORGANIZATIONNO"));
						}
					}
					
					//删除
					deletePlatform_BaseSet(thisDate);
					for (Map<String, Object> shopMap : shopList) {
						String shopID=shopMap.get("THIRD_SHOP").toString();
						
						String sqlPlatform="select * from Platform_BaseSet where eId=? and ORGANIZATIONNO ='A01' and MACHINE='A01' and ITEM='REPORTDATE' and ITEMVALUE=? and SHOPID=? and status='100'";
						String[] paramPlatform = {eId,thisDate,shopID};
						List<Map<String, Object>> platformList = StaticInfo.dao.executeQuerySQL(sqlPlatform, paramPlatform);
						if(platformList!=null&&platformList.size()>0){
							writelogFileName("\r\n***********"+loggerInfoTitle+"第三方门店:"+shopID+"单据日期:"+thisDate+"已同步完成，跳过执行");
						}else{
							String sqlTdSale = "select SALENO from DCP_SALE where eId=? and BDATE=? and SHOPID=? ";
							String[] conditionValuesTdSale = {eId,thisDate,shopMap.get("ORGANIZATIONNO").toString()};
							List<String> saleNoList=new ArrayList<String>();
							List<Map<String, Object>> tdSaleList = StaticInfo.dao.executeQuerySQL(sqlTdSale, conditionValuesTdSale);
							for(Map<String, Object> tdSaleMap:tdSaleList){
								saleNoList.add(tdSaleMap.get("SALENO").toString());
							}
							doGetShopBillDetailLoop(pageSize, thisDate, shopID,saleNoList,thirdShopMap);
						}
					}
				}else{
					writelogFileName("\r\n***************"+loggerInfoTitle+"  erp门店资料不存在****************\r\n");
				}
			}
			catch (Exception e) 
			{
//			logger.error("\r\n***********"+loggerInfoTitle+"发生异常:" ,e);
				try{
					writelogFileName("\r\n***********"+loggerInfoTitle+"发生异常:"+getTrace(e));
				}catch (Exception ex) 
				{
					
				}
			}  finally {
				bRun = false;
			}
		}else{
			//不在执行时间范围内
			bRun = false;
			writelogFileName("\r\n***********"+loggerInfoTitle+"不在执行时间范围内");
		}


		writelogFileName("\r\n***************"+loggerInfoTitle+"END****************\r\n");

		return sReturnInfo;	

	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void deletePlatform_BaseSet(String reportDate) throws Exception{
		ArrayList<DataProcessBean> dpb = new ArrayList<DataProcessBean>();
		String deleteSql ="delete from Platform_BaseSet where ORGANIZATIONNO ='A01' and MACHINE='A01' and ITEM='REPORTDATE' and ITEMVALUE!='"+reportDate+"'";
		ExecBean exc1 = new ExecBean(deleteSql);
		dpb.add(new DataProcessBean(exc1));
		StaticInfo.dao.useTransactionProcessData(dpb);
	}
	
	public void doGetShopBillDetailLoop(String pageSize,String reportDate,String shopId,List<String> saleNoList,Map<String, Object> thirdShopMap) throws Exception{
		HllClient hllClient=new HllClient();
		int pageNo=1;
		//循环时，跳过数据笔数
//		int ignore=0;
//		//当前门店，当前日期，DCP_SALE已有数据笔数
//		int countTdSale=Integer.valueOf(countNo);
//		if(countTdSale!=0){
//			//页码
//			int pSize=Integer.valueOf(pageSize);
//			pageNo=pageNo+countTdSale/pSize;
//			ignore=countTdSale%pSize;
//		}
		boolean isSuccess=true;
		try{
			String sqlPayment = "select PAYCODE,PAYCODEERP,PAYNAME from DCP_PAYMENT where status='100' and eId=?  ";
			String[] conValPayment = {eId};
			List<Map<String, Object>> paymentList = StaticInfo.dao.executeQuerySQL(sqlPayment, conValPayment);
			Map<String, Map<String, Object>> paymentMap=new HashMap<String, Map<String, Object>>();
			for (Map<String, Object> payMap : paymentList) {
				Object payNameObj=payMap.get("PAYNAME");
				if(payNameObj!=null&&!payNameObj.toString().isEmpty()){
					paymentMap.put(payNameObj.toString(), payMap);
				}
			}
			
			String sqlUnit = "select UNAME,UNIT from DCP_UNIT where status='100' and eId=?  ";
			String[] conValUnit = {eId};
			List<Map<String, Object>> unitList = StaticInfo.dao.executeQuerySQL(sqlUnit, conValUnit);
			Map<String, Object> unitMap=new HashMap<String, Object>();
			for (Map<String, Object> uMap : unitList) {
				Object unameObj=uMap.get("UNAME");
				if(unameObj!=null&&!unameObj.toString().isEmpty()){
					unitMap.put(unameObj.toString(), uMap.get("UNIT"));
				}
			}
			
			String sqlOpno = "select ORGANIZATIONNO,OPNO from PLATFORM_STAFFS where ORGANIZATIONNO is not null and status='100' and eId=?  ";
			String[] conValOpno = {eId};
			List<Map<String, Object>> opnoList = StaticInfo.dao.executeQuerySQL(sqlOpno, conValOpno);
			Map<String, Object> opnoMap=new HashMap<String, Object>();
			for (Map<String, Object> oMap : opnoList) {
				Object orgObj=oMap.get("ORGANIZATIONNO");
				if(orgObj!=null&&!orgObj.toString().isEmpty()){
					opnoMap.put(orgObj.toString(), oMap.get("OPNO"));
				}
			}
			
			HllShopBillDetailResponse billDetailRes=hllClient.getShopBillDetail(String.valueOf(pageNo), pageSize, reportDate,shopId);
			if(billDetailRes!=null&&"000".equals(billDetailRes.getCode())){
				HllPageResponse pageResponse=billDetailRes.getData().getPageResponseInfo();
				int pageTotal=Integer.parseInt(pageResponse.getPageTotal());
				int totalSize=Integer.parseInt(pageResponse.getTotalSize());
				if(totalSize==saleNoList.size()){
					writelogFileName("\r\n***********"+loggerInfoTitle+"第三方门店:"+shopId+"单据日期:"+reportDate+"已同步完成，不再执行");
					savePlatform_BaseSet(shopId, reportDate);
				}else{
					try{
						saveErp(billDetailRes, saleNoList,thirdShopMap,paymentMap,unitMap,opnoMap);
					}catch(Exception e){
						writelogFileName(getTrace(e));
					}
					while(pageNo<pageTotal&&isSuccess){
						pageNo++;
						HllShopBillDetailResponse billDetailResNext=hllClient.getShopBillDetail(String.valueOf(pageNo), pageSize, reportDate,shopId);
						if(billDetailRes!=null&&"000".equals(billDetailRes.getCode())){
							try{
								saveErp(billDetailResNext, saleNoList,thirdShopMap,paymentMap,unitMap,opnoMap);
							}catch(Exception e){
								writelogFileName(getTrace(e));
							}
						}
					}
				}
			}else{
				writelogFileName("\r\n***********"+loggerInfoTitle+"发生异常:" + pageNo+ pageSize+reportDate+shopId+billDetailRes);
			}
		}
		catch(Exception e) 
		{
			writelogFileName("\r\n***********"+loggerInfoTitle+"发生异常:"+getTrace(e));
		}  
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public void savePlatform_BaseSet(String thirdShop,String reportDate) throws Exception{
		String[] columnsSaleDetail = {
				"EID",
				"SHOPID",
				"MACHINE",
				"ITEM",
				"ITEMVALUE",
				"ORGANIZATIONNO",
				"STATUS"
		};
		DataValue[] insValue = null;
		insValue = new DataValue[]
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(thirdShop, Types.VARCHAR),
						new DataValue("A001", Types.VARCHAR),
						new DataValue("REPORTDATE", Types.VARCHAR),
						new DataValue(reportDate, Types.VARCHAR),
						
						new DataValue("A001", Types.VARCHAR),
						new DataValue("100", Types.VARCHAR)
				};
		InsBean ibPlatform = new InsBean("PLATFORM_BASESET", columnsSaleDetail);
		ibPlatform.addValues(insValue);
		List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
		lstData.add(new DataProcessBean(ibPlatform));
		StaticInfo.dao.useTransactionProcessData(lstData); 
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public boolean saveErp(HllShopBillDetailResponse billDetailRes,List<String> saleNoList,Map<String, Object> thirdShopMap,Map<String, Map<String, Object>> paymentMap,Map<String, Object> unitMap,Map<String, Object> opnoMap) throws Exception{
		boolean isSuccess=false;
		if(billDetailRes!=null&&"000".equals(billDetailRes.getCode())){
			
			//列表SQL
			List<DataProcessBean> lstData=new ArrayList<DataProcessBean>();	
			
			//账单主信息
			List<HllOrderDetail> orderDetailList= billDetailRes.getData().getBillDetail();
			//本次执行存入单据笔数
			int count=0;

			//shopID	店铺ID
			String shopId="";
			String bDate="";
			for(HllOrderDetail orderDetail:orderDetailList){
				//只取已结账的
				//订单状态 10：存单 20：已落单 40：已结账 30：废单,废单的相关菜品及结算明细不会出现在统计报表中。另外单独提供废单列表、存单列表，可以查看详情
				String orderStatus=orderDetail.getOrderStatus();
				//单号
				String saleNo=HllUtils.getDefaultValueStr(orderDetail.getSaasOrderKey());//餐饮系统订单Key(YYYYMMDD-ID-日流水号）
				String isTestOrder=HllUtils.getDefaultValueStr(orderDetail.getIsTestOrder());
				if("".equals(bDate)){
					bDate=HllUtils.getDefaultValueStr(orderDetail.getReportDate());
				}
				String shopGet=HllUtils.getDefaultValueStr(orderDetail.getShopID());
				Object thirdShop=thirdShopMap.get(shopGet);
				if(thirdShop==null||thirdShop.toString().isEmpty()){
					shopId=shopGet;
				}else{
					shopId=thirdShop.toString();
				}
				
				if("30".equals(orderStatus)){
					writelogFileName("\r\n***************"+loggerInfoTitle+"  单据日期:"+bDate+" 门店:"+shopId+"单据编号:"+saleNo+"为作废单，不执行保存****************\r\n");
					continue;
				}
				//单号在咱们的数据库已存在
				else if(saleNoList.contains(saleNo)){
					saleNoList.remove(saleNo);
					continue;
				}
				//isTestOrder	是否是测试账单 0：不是（默认） 1：是
				//测试单据，不进行同步
				else if("1".equals(isTestOrder)){
					writelogFileName("\r\n***************"+loggerInfoTitle+"  单据日期:"+bDate+" 门店:"+shopId+"单据编号:"+saleNo+"为测试单，不执行保存****************\r\n");
					continue;
				}
				else{
					count++;
					
					String opno=opnoMap.get(shopId)==null?"":opnoMap.get(shopId).toString();
					//createTime	记录创建时间【yyyyMMddHHmmss，默认值0】
					String createTime=orderDetail.getCreateTime();
					String sDate=getSDate(createTime);
					String sTime=getSTime(createTime);
					
					//存放foodDetail中，foodname与原金额的对应关系
					Map<String, Object> amtMap=new HashMap<String, Object>();
					
					//checkoutBy	结账人员
//					String checkoutBy=HllUtils.getDefaultValueStr(orderDetail.getCheckoutBy());
					//总数量
					BigDecimal Tot_Qty =new BigDecimal(0);
					//账单菜品信息
					List<HllFoodDetail> foodDetailList=orderDetail.getFoodDetail();
					if(foodDetailList!=null&&foodDetailList.size()>0){
						//作为项次使用
						int item=0;
						for(HllFoodDetail foodDetail:foodDetailList){
							//数量	M	 Qty                            number(23,8),
							//foodSendNumber	赠菜数量
							BigDecimal foodSendNumber=new BigDecimal(foodDetail.getFoodSendNumber());
							//foodNumber	点菜数量
							BigDecimal foodNumber=new BigDecimal(foodDetail.getFoodNumber());
							BigDecimal Qty=foodSendNumber.add(foodNumber);
							//已退货数量	O	 Rqty                           number(23,8),
							//foodCancelNumber	退菜数量
							BigDecimal foodCancelNumber=new BigDecimal(foodDetail.getFoodCancelNumber());
							BigDecimal Rqty=foodCancelNumber;
							
							String hllUnit=HllUtils.getDefaultValueStr(foodDetail.getUnit());
							String unit=unitMap.get(hllUnit)==null?hllUnit:unitMap.get(hllUnit).toString();
							if("".equals(unit)){
								if(hllUnit==null||hllUnit.trim().isEmpty()){
									unit=errorUnit;
								}else{
									unit=hllUnit;
								}
							}
							
							//原价
							BigDecimal oldPrice=new BigDecimal(foodDetail.getFoodPayPrice());
							//原价*数量=原金额
							BigDecimal total=Qty.subtract(Rqty);
							BigDecimal amt=total.multiply(oldPrice);
							String foodName=HllUtils.getDefaultValueStr(foodDetail.getFoodName());
							amtMap.put(foodName, amt);
							
							Tot_Qty=Tot_Qty.add(Qty).subtract(Rqty);
							
							item++;
							
							//DCP_SALE_DETAIL 订单商品明细表   FoodDetail (账单菜品信息)
							InsBean ibSaleDetail=getIbSaleDetail();
							DataValue[] insValueSaleDetail=doPutTdSaleDetail(foodDetail,saleNo,shopId,bDate,item,sDate,sTime,Qty,Rqty,unit);
							ibSaleDetail.addValues(insValueSaleDetail);
							lstData.add(new DataProcessBean(ibSaleDetail));
							
							//DCP_STOCK_DETAIL  库存流水账
							InsBean ibTwStockDetail=getIbTwStockDetail();
							DataValue[] insValueTwStockDetail=doPutTwStockDetail(foodDetail,saleNo,shopId,item,Qty,Rqty,sDate,sTime,opno,unit);
							ibTwStockDetail.addValues(insValueTwStockDetail);
							lstData.add(new DataProcessBean(ibTwStockDetail));
						}
					}
					
					//账单中支付信息详情
					List<HllPayDetail> payDetailList=orderDetail.getPayDetail();
					if(payDetailList!=null&&payDetailList.size()>0){
						//作为项次使用
						int itemPay=0;
						int itemAgio=0;
						for(HllPayDetail payDetail:payDetailList){
							
							String hllPaySubjectName=HllUtils.getDefaultValueStr(payDetail.getPaySubjectName());
							Map<String, Object> paySubMap=paymentMap.get(hllPaySubjectName);
							//支付类型(大类)	 PAYCODEERP                 NVARCHAR2(10),	ERP的大类
							String payCode=hllPaySubjectName;
							//PaySubjectName	支付科目分组名称，未定义分组名称，则此处为科目名称
							String payCodeErp=hllPaySubjectName;
							if(paySubMap!=null&&!paySubMap.isEmpty()){
								Object payCodeObj=paySubMap.get("PAYCODE");
								if(payCodeObj!=null&&!payCodeObj.toString().isEmpty()){
									payCode=payCodeObj.toString();
								}
								Object payCodeErpObj=paySubMap.get("PAYCODEERP");
								if(payCodeErpObj!=null&&!payCodeErpObj.toString().isEmpty()){
									payCodeErp=payCodeErpObj.toString();
								}
							}
							if("".equals(payCode)){
								if(hllPaySubjectName==null||hllPaySubjectName.trim().isEmpty()){
									payCode=errorPayCode;
								}else{
									payCode=hllPaySubjectName;
								}
							}
							if("".equals(payCodeErp)){
								if(hllPaySubjectName==null||hllPaySubjectName.trim().isEmpty()){
									payCode=errorPayCode;
								}else{
									payCode=hllPaySubjectName;
								}
							}
							BigDecimal pay=new BigDecimal(payDetail.getPaySubjectRealAmount());
							//按实际金额拆分，实际支付金额=0，存入折扣表，其他存入付款表
							if(pay.compareTo(BigDecimal.ZERO)==0){
								BigDecimal paySubjectAllDiscountAmount=new BigDecimal(payDetail.getPaySubjectAllDiscountAmount());
								if(paySubjectAllDiscountAmount.compareTo(BigDecimal.ZERO)!=0){
									itemAgio++;
									//DCP_SALE_DETAIL_AGIO  订单商品折扣明细表    promotionDetail(营销活动详情)
									InsBean ibSaleDetailAgio=getIbSaleDetailAgio();
									DataValue[] insValueSaleDetailAgio=doPutTdSaleDetailAgio(payDetail,saleNo,bDate,itemAgio,shopId,amtMap);
									ibSaleDetailAgio.addValues(insValueSaleDetailAgio);
									lstData.add(new DataProcessBean(ibSaleDetailAgio));
								}
							}else{
								itemPay++;
								//DCP_SALE_PAY 订单付款方式表    PayDetail(账单支付信息)
								InsBean ibSalePay=getIbSalePay();
								DataValue[] insValueSalePay=doPutTdSalePay(payDetail,saleNo,shopId,itemPay,sDate,sTime,payCode,payCodeErp);
								ibSalePay.addValues(insValueSalePay);
								lstData.add(new DataProcessBean(ibSalePay));
							}
						}
					}
					
					//DCP_SALE 订单主表    OrderDetail（账单主信息）
					InsBean ibSale = getIbSale();
					//主菜单
					DataValue[] insValueSale=doPutTdSale(orderDetail,saleNo,shopId,sDate,sTime,Tot_Qty,opno);
					ibSale.addValues(insValueSale);
					lstData.add(new DataProcessBean(ibSale));
				}
			}
			try{
				isSuccess=StaticInfo.dao.useTransactionProcessData(lstData);
			}catch(Exception e){
				writelogFileName("\r\n***********"+loggerInfoTitle+"保存时发生异常:"+getTrace(e));
			}
			
			writelogFileName("\r\n***************"+loggerInfoTitle+"  单据日期:"+bDate+" 门店:"+shopId+"本次存入"+count+"笔单据****************\r\n");
//			return isSuccess;
			return true;
		}else{
//			logger.error("\r\n***********"+loggerInfoTitle+"发生异常:" + billDetailRes.getCode()+billDetailRes.getMessage());
			writelogFileName("\r\n***********"+loggerInfoTitle+"发生异常:" + billDetailRes.getCode()+billDetailRes.getMessage());
//			return false;
			return true;
		}
		//原逻辑，每次插入5笔，发生异常不再继续插入资料，以免造成顺序混乱，现弃用
	}
	
	/**
	 * DCP_SALE 订单主表
	 * OrderDetail（账单主信息）
	 * @return
	 */
	public static InsBean getIbSale(){
		//对应OrderDetail（账单主信息）
		//每五个参数，空一行，便于后期增添对照、纠错  请勿改动
		String[] columnsSale = {
				"EID",
				"SHOPID",
				"SALENO",
				"TRNO",
				"MACHINE",
				"BDATE",
				"WORKNO",
				"OPNO",
				"CARDNO",
				"SHOPINCOME",
				"TOT_DISC",
				"TABLENO",
				"INVOICETITLE",
				"TAX",
				"TAXREGNUMBER",
				
				"GUESTNUM",
				"TYPE",
				"STATUS",
				"SDATE",
				"STIME",
				
				"TOT_AMT",
				"TOT_UAMT",
				"PAY_AMT",
				"TOT_QTY",
				"ORDERCHANNEL",
                "PARTITION_DATE",
		};
		InsBean ibSale = new InsBean("DCP_SALE", columnsSale);//分区字段已处理
		return ibSale;
	}
	
	public static DataValue[] doPutTdSale(HllOrderDetail orderDetail,String saleNo,String shopId,String sDate,String sTime,BigDecimal Tot_Qty,String opno){
		//企业编号	 EID               NVARCHAR2(5),
		//门店编号	 SHOPID                       nvarchar2(10),
		//销售单号	 SALENO                    nvarchar2(30),
		//流水号	 TRNO                        nvarchar2(6),
		String trno=HllUtils.getDefaultValueStr(orderDetail.getSaasOrderNo());//saasOrderNo 日流水号
		//机台号	 MACHINE                 NVARCHAR2(6),
		//deviceCode 设备编号  deviceKey 设备Key deviceName 设备名称
		//deviceKey及deviceName数据过长，故使用deviceCode
		String machine=HllUtils.getDefaultValueStr(orderDetail.getDeviceCode());
		//营业日期	 BDATE                      NVARCHAR2(8),
		//报表统计日期，门店增加数据清机结转时间设定，该时间为次日小时数（1~12），默认为1，即次日凌晨1点【yyyyMMdd，默认值0】
		String bdate=HllUtils.getDefaultValueStr(orderDetail.getReportDate());
		//班别编号	 SQUADNO                INT,
		//班号	 WORKNO                  NVARCHAR2(5),
		String workno=HllUtils.getDefaultValueStr(orderDetail.getShiftName());//shiftName	收银班次  示例"shiftName": "夜班"
		//用户编码	 OPNO                     NVARCHAR2(10),
//		String opno=HllUtils.getDefaultValueStr(orderDetail.getCheckoutBy());//checkoutBy	结账人员
		
		//会员卡相关
		//会员卡号	 CARDNO                   NVARCHAR2(30),
		String cardNo=HllUtils.getDefaultValueStr(orderDetail.getCardNo());//
		//积分点数	 POINT_QTY                NUMBER(23,8),
		//当前会员剩余积分	REMAINPOINT NUMBER(23,8) DEFAULT 0
		
		//金额部分
		
		//总找零金额	 TOT_CHANGED           NUMBER(23,8),
		//总折扣基恩	 TOT_DISC                  NUMBER(23,8),
		BigDecimal totDisc=new BigDecimal(orderDetail.getPromotionAmount());	//promotionAmount	优惠金额
		//抹零金额	 ERASE_AMT               NUMBER(23,8),
		//商家实际到账金额	 SHOPINCOME            NUMBER(23,8),
		BigDecimal shopIncome=new BigDecimal(orderDetail.getPaidAmount());	//paidAmount	订单实收金额
		
		//销售总金额(含税)	 TOT_AMT                  NUMBER(23,8),
		//销售总金额(未税)	 TOT_UAMT                NUMBER(23,8),
		BigDecimal foodAmount=new BigDecimal(orderDetail.getFoodAmount());//foodAmount	菜品金额合计。原始数据为Decimal，为保证数据精度，以String传输
		BigDecimal sendFoodAmount=new BigDecimal(orderDetail.getSendFoodAmount());//sendFoodAmount	赠菜金额合计
		BigDecimal totAmt=foodAmount.add(sendFoodAmount);
		BigDecimal totUamt=foodAmount.add(sendFoodAmount);
		//已收金额	M	 Pay_Amt                    number(23,8),
		BigDecimal payAmt=shopIncome;//paidAmount	订单实收金额
		
		//桌台号	 TABLENO                   NVARCHAR2(20),
		String tableNo=HllUtils.getDefaultValueStr(orderDetail.getTableName());////tableName	桌台名称
		
		//发票相关信息
		//发票抬头	 INVOICETITLE                NVARCHAR2(80),
		String invoiceTitle=HllUtils.getDefaultValueStr(orderDetail.getInvoiceTitle());//invoiceTitle	发票抬头
		//开户行(发票)	 INVOICEBANK               NVARCHAR2(30),
		//开户账号(发票）	 INVOICEACCOUNT          NVARCHAR2(25),
		//联系方式(发票)	 INVOICETEL                  NVARCHAR2(20),
		//公司地址(发票)	 INVOICEADDR               NVARCHAR2(80),
		//纳税人识别号	 TAXREGNUMBER         NVARCHAR2(30),
		//invoiceTaxpayerIdentCode	纳税人识别码（税务登记证上15或者18或者29位的数字）
		String taxregNumber=HllUtils.getDefaultValueStr(orderDetail.getInvoiceTaxpayerIdentCode());
		//零税金额	 ZEROTAXAMT            NUMBER(23,8)
		//免税金额	 FREETAXAMT            NUMBER(23,8)
		
		//税额	 TAX                   NUMBER(23,8)
		BigDecimal tax=new BigDecimal(orderDetail.getInvoiceTaxAmount());//invoiceTaxAmount	税额
		//留抵税额	 ACCTAX                NUMBER(23,8)
		//免税证号	 FREECODE              NVARCHAR2(20)
		
		//桌台人数	 GUESTNUM                INT,
		String person=orderDetail.getPerson();
		int guestNum=person==null?0:Integer.parseInt(person);//person	消费人数
		

		//单据类型	M	 Type                        INT,  0、销售单 1、原价退货 2、议价退货，
		int type=0;
		String status="100";
		//总数量	 TOT_QTY                   NUMBER(23,8),
		//单据来源方式		 ORDERCHANNEL             INT,
		//暂时给固定值39
		int orderChannel=39;
		
		DataValue[] insValue = null;
		//每五个参数，空一行，便于后期增添对照、纠错  请勿改动
		insValue = new DataValue[]
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(saleNo, Types.VARCHAR),
						new DataValue(trno, Types.VARCHAR),
						new DataValue(machine, Types.VARCHAR),
						
						new DataValue(bdate, Types.VARCHAR),
						new DataValue(workno, Types.VARCHAR),
						new DataValue(opno, Types.VARCHAR),
						new DataValue(cardNo, Types.VARCHAR),
						new DataValue(shopIncome, Types.DECIMAL),
						
						new DataValue(totDisc, Types.DECIMAL),
						new DataValue(tableNo, Types.VARCHAR),
						new DataValue(invoiceTitle, Types.VARCHAR),
						new DataValue(tax, Types.DECIMAL),
						new DataValue(taxregNumber, Types.VARCHAR),
						
						new DataValue(guestNum, Types.INTEGER),
						new DataValue(type, Types.INTEGER),
						new DataValue(status, Types.VARCHAR),
						new DataValue(sDate, Types.VARCHAR),
						new DataValue(sTime, Types.VARCHAR),
						
						new DataValue(totAmt, Types.DECIMAL),
						new DataValue(totUamt, Types.DECIMAL),
						new DataValue(payAmt, Types.DECIMAL),
						new DataValue(Tot_Qty, Types.DECIMAL),
						new DataValue(orderChannel, Types.INTEGER),
                        new DataValue(bdate, Types.NUMERIC),//分区字段
				};
		
		
//		班别编号	M	 SquadNo                INT,
//		班号	M	 WorkNo                  nvarchar2(5),
//		总找零金额	M	 Tot_Changed           number(23,8),
//		抹零金额	M	 Erase_Amt               number(23,8),

//		退货理由码	M	  BSNO                       nvarchar2(10),
//		时间标记	M	 Tran_Time    nvarchar2(20) default to_Char(SYSDATE,'YYYYMMDDHH24MISSSSS'),




		

		return insValue;
	}
	
	/**
	 * DCP_SALE_DETAIL 订单商品明细表
	 * FoodDetail (账单菜品信息)
	 * @return
	 */
	public static InsBean getIbSaleDetail(){
		//对应FoodDetail (账单菜品信息)
		String[] columnsSaleDetail = {
				"EID",
				"SHOPID",
				"SALENO",
				"ITEM",
				"PLUNO",
				"ISPACKAGE",
				"PNAME",
				"UNIT",
				"WUNIT",
				"WAREHOUSE",
				"STATUS",
				"SDATE",
				"STIME",
				"QTY",
				"RQTY",
				"OLDPRICE",
				"PRICE",
				"DISC",
				"AMT",
				"UAMT",
				"PARTITION_DATE",
                "UNITRATIO","BASEQTY",
		};
		InsBean ibSaleDetail = new InsBean("DCP_SALE_DETAIL", columnsSaleDetail);//分区字段已处理
		return ibSaleDetail;
	}
	
	/**
	 * DCP_SALE_DETAIL表赋值
	 * @return
	 */
	public static DataValue[] doPutTdSaleDetail(HllFoodDetail foodDetail,String saleNo,String shopId,String bdate,int item,String sDate,String sTime,BigDecimal Qty,BigDecimal Rqty,String unit){
		
		//企业编号	 EID                        NVARCHAR2(5),
		//门店编号	 SHOPID                       nvarchar2(10),
		//项次	 ITEM                          INT,	
		//是否套餐	 ISPACKAGE                    NVARCHAR2(1)  DEFAULT 'N',
		String isPackage="N";//isSetFood	是否套餐（套餐及套餐明细此标记均为1）
		if("1".equals(HllUtils.getDefaultValueStr(foodDetail.getIsSetFood()))){
			isPackage="Y";
		}
		//商品编码	 PLUNO                        NVARCHAR2(40),
		String pluNo=HllUtils.getDefaultValueStr(foodDetail.getFoodCode());//foodCode	菜品编码，对应基本档-获取店铺菜品返回的foodCode
		if("".equals(pluNo)){
			pluNo=errorPluNo;
		}
		
		String foodName=HllUtils.getDefaultValueStr(foodDetail.getFoodName());
		//0102109 是餐盒费
		//0102111 是配送费
		//单位都是PCS
		if(foodName.equals("餐盒费")){
			unit="PCS";
			pluNo="0102109";
		}else if(foodName.equals("配送费")){
			unit="PCS";
			pluNo="0102111";
		}
		
		//商品名称	 PNAME                      NVARCHAR2(80),
		String pName=HllUtils.getDefaultValueStr(foodDetail.getFoodName());//foodName	菜品名称，对应基本档-获取店铺菜品返回的foodName
		
		//单位编号	 UNIT                           NVARCHAR2(10),	
		//库存单位	 WUNIT                        NVARCHAR2(10),
//		String unit=HllUtils.getDefaultValueStr(foodDetail.getUnit());		//unit	菜品规格
		
		//出货仓库	M	 Warehouse              nvarchar2(10),
		String wareHouse=shopId;
		String status="100";
		//系统日期	M	 Sdate                         nvarchar2(8),
		//系统时间	M	 Stime                         nvarchar2(6),
		//数量	M	 Qty                            number(23,8),
		//已退货数量	O	 Rqty                           number(23,8),

//		原价	M	 OldPrice                    number(23,8),
//		售价	M	 Price                          number(23,8),
//		总折扣金额	M	 Disc                           number(23,8),
//		单据小计(含税)	M	 amt                           number(23,8),
//		单项小计(未税)	M	 Uamt                         number(23,8),
		
		//小计
		BigDecimal total=Qty.subtract(Rqty);
//		售价：foodpayprice
//		原价：foodrealprice
//		折扣：foodpriceamount减去foodpaypricereal
		BigDecimal oldAmount=new BigDecimal(foodDetail.getFoodPriceAmount());
		BigDecimal priceAmount=new BigDecimal(foodDetail.getFoodRealAmount());
		//原价
		BigDecimal oldPrice=new BigDecimal(0);
		//售价
		BigDecimal price=new BigDecimal(0);
		if(total.compareTo(BigDecimal.ZERO)!=0){
			oldPrice=oldAmount.divide(total,scale,BigDecimal.ROUND_HALF_UP);
			price=priceAmount.divide(total,scale,BigDecimal.ROUND_HALF_UP);
		}
		BigDecimal amt=priceAmount;
		BigDecimal uamt=priceAmount;
		//折扣
		BigDecimal disc=oldAmount.subtract(priceAmount);
		
		DataValue[] insValue = null;
		insValue = new DataValue[]
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(saleNo, Types.VARCHAR),
						new DataValue(item, Types.INTEGER),
						new DataValue(pluNo, Types.VARCHAR),
						
						new DataValue(isPackage, Types.VARCHAR),
						new DataValue(pName, Types.VARCHAR),
						new DataValue(unit, Types.VARCHAR),
						new DataValue(unit, Types.VARCHAR),
						new DataValue(wareHouse, Types.VARCHAR),
						
						new DataValue(status, Types.VARCHAR),
						new DataValue(sDate, Types.VARCHAR),
						new DataValue(sTime, Types.VARCHAR),
						new DataValue(Qty, Types.DECIMAL),
						new DataValue(Rqty, Types.DECIMAL),
						
						new DataValue(oldPrice, Types.DECIMAL),
						new DataValue(price, Types.DECIMAL),
						new DataValue(disc, Types.DECIMAL),
						new DataValue(amt, Types.DECIMAL),
						new DataValue(uamt, Types.DECIMAL),
						new DataValue(bdate, Types.NUMERIC),//分区字段
                        new DataValue(1d, Types.NUMERIC),
                        new DataValue(Qty, Types.NUMERIC),
				};
		

		//foodRealAmount	菜品除去优惠后均摊实收金额

		return insValue;
	}
	
	/**
	 * DCP_SALE_PAY 订单付款方式表
	 * PayDetail(账单支付信息)
	 * @return
	 */
	public static InsBean getIbSalePay(){
		//对应PayDetail(账单支付信息)
		String[] columns = {
				"EID",
				"SHOPID",
				"SALENO",
				"ITEM",
				"PAYCODE",
				
				"PAYCODEERP",
				"STATUS",
				"BDATE",
				"SDATE",
				"STIME",
				
				"PAYNAME",
				"PAY",
				"EXTRA",
				"CHANGED",
				"ISORDERPAY",
				"POS_PAY",
				"PARTITION_DATE",
		};
		InsBean ib = new InsBean("DCP_SALE_PAY", columns);//分区字段已处理
		return ib;
	}
	
	/**
	 * DCP_SALE_PAY表赋值
	 * @return
	 */
	public static DataValue[] doPutTdSalePay(HllPayDetail payDetail,String saleNo,String shopId,int item,String sDate,String sTime,String payCode,String payCodeErp){
		
		//企业编号	 EID               NVARCHAR2(5),
		//门店编号	 SHOPID                       nvarchar2(10),
		//支付方式(小类)	 PAYCODE                       NVARCHAR2(10),	ERP的小类
		//paySubjectName	支付科目名，对应基本档-获取店铺科目列表返回的subjectName
//		String payCode=HllUtils.getDefaultValueStr(payDetail.getPaySubjectName());
		//支付类型(大类)	 PAYCODEERP                 NVARCHAR2(10),	ERP的大类
		//PaySubjectName	支付科目分组名称，未定义分组名称，则此处为科目名称
//		String payCodeErp=HllUtils.getDefaultValueStr(payDetail.getPaySubjectName());
//		if("".equals(payCodeErp)){
//			payCodeErp=payCode;
//		}

		String status="100";
		//收银营业日期	M	 BDATE                          NVARCHAR2(8),
		//报表统计日期，门店增加数据清机结转时间设定，该时间为次日小时数（1~12），默认为1，即次日凌晨1点【yyyyMMdd，默认值0】
		String bdate=HllUtils.getDefaultValueStr(payDetail.getReportDate());
		//系统日期	M	 SDATE                            NVARCHAR2(8),	
		//系统时间	M	 STIME                            NVARCHAR2(6),	
		//付款名称	M	 PAYNAME                      NVARCHAR2(60),	
		//paySubjectName	支付科目名，对应基本档-获取店铺科目列表返回的subjectName
		String payName=HllUtils.getDefaultValueStr(payDetail.getPaySubjectName());
		//金额	M	  PAY                               NUMBER(23,8),	录入的金额
		//paySubjectRealAmount	借贷方发生的实收金额，从相关菜品实收金额进行汇总
		BigDecimal pay=new BigDecimal(payDetail.getPaySubjectRealAmount());
		//溢收金额	M	 EXTRA                             NUMBER(23,8),	录入的金额
		BigDecimal extra=pay;
		//找零	M	 CHANGED                      NUMBER(23,8),
		//哗啦啦中无找零金额栏位，可以以20170406门店76037692单据20170406195430123为例。
		BigDecimal changed=new BigDecimal(0);
		
		//是否订金	M	 ISORDERPAY                    NVARCHAR2(1) DEFAULT 'N',
		String isorderpay="N";
		//POS支付	M	 POS_PAY                       NVARCHAR2(10),
		BigDecimal posPay=pay;
		
		DataValue[] insValue = null;
		insValue = new DataValue[]
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(saleNo, Types.VARCHAR),
						new DataValue(item, Types.INTEGER),
						new DataValue(payCode, Types.VARCHAR),
						
						new DataValue(payCodeErp, Types.VARCHAR),
						new DataValue(status, Types.VARCHAR),
						new DataValue(bdate, Types.VARCHAR),
						new DataValue(sDate, Types.VARCHAR),
						new DataValue(sTime, Types.VARCHAR),
						
						new DataValue(payName, Types.VARCHAR),
						new DataValue(pay, Types.DECIMAL),
						new DataValue(extra, Types.DECIMAL),
						new DataValue(changed, Types.DECIMAL),
						new DataValue(isorderpay, Types.VARCHAR),
						
						new DataValue(posPay, Types.DECIMAL),
						new DataValue(bdate, Types.NUMERIC),//分区字段
				};
		
		return insValue;
	}
	
	/**
	 * DCP_SALE_DETAIL_AGIO  订单商品折扣明细表
	 * promotionDetail(营销活动详情)
	 * @return
	 */
	public static InsBean getIbSaleDetailAgio(){
		//对应账单主信息
		String[] columns = {
				"EID",
				"SHOPID",
				"SALENO",
				"ITEM",
				"MITEM",
				"STATUS",
				"QTY",
				"AMT",
				"DISC",
				
				"GIFTCTF",
				"GIFTCTFNO",
				"PARTITION_DATE",
		};
		InsBean ib = new InsBean("DCP_SALE_DETAIL_AGIO", columns);//分区字段已处理
		return ib;
	}
	
	/**
	 * DCP_SALE_DETAIL_AGIO  订单商品折扣明细表 赋值
	 * @return
	 */
	public static DataValue[] doPutTdSaleDetailAgio(HllPayDetail payDetail,String saleNo,String bdate,int item,String shopId,Map<String, Object> amtMap){
		
		//企业编号	 EID               NVARCHAR2(5),
		//门店编号	 SHOPID                       nvarchar2(10),
		//主项次	M	 MITEM                      INT,
		//项次	M	 ITEM                         INT,

		String status="100";

		//参与数量	M	 QTY                          NUMBER(23,8),
//		BigDecimal qty=new BigDecimal(promotionDetail.getFoodCount());//foodCount	点菜数量
		BigDecimal qty=new BigDecimal(1);//foodCount	点菜数量
		//折扣金额	M	 DISC                          NUMBER(23,8),
		BigDecimal disc=new BigDecimal(payDetail.getPaySubjectAllDiscountAmount());
		//参与金额	M	 AMT                         NUMBER(23,8),
//		BigDecimal promotionAmount=new BigDecimal(promotionDetail.getDeltaPromotionAmount());//deltaPromotionAmount	菜品优惠金额
//		String giftctf=HllUtils.getDefaultValueStr(promotionDetail.getPromotionName());
//		String pmtno=HllUtils.getDefaultValueStr(promotionDetail.getFoodName());
		DataValue[] insValue = null;
//		BigDecimal amt=new BigDecimal(0);
		BigDecimal amt=disc;
		//paySubjectGroupName 支付科目分组名称，未定义分组名称，则此处为科目名称
		String paySubjectGroupName=HllUtils.getDefaultValueStr(payDetail.getPaySubjectGroupName());
		//paySubjectName	支付科目名，对应基本档-获取店铺科目列表返回的subjectName
		String paySubjectName=HllUtils.getDefaultValueStr(payDetail.getPaySubjectName());
		insValue = new DataValue[]
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(shopId, Types.VARCHAR),
						new DataValue(saleNo, Types.VARCHAR),
						new DataValue(item, Types.INTEGER),
						new DataValue(1, Types.INTEGER),
						
						new DataValue(status, Types.VARCHAR),
						new DataValue(qty, Types.DECIMAL),
						new DataValue(amt, Types.DECIMAL),
						new DataValue(disc, Types.DECIMAL),
						new DataValue(paySubjectGroupName, Types.VARCHAR),
						
						new DataValue(paySubjectName, Types.VARCHAR),
						new DataValue(bdate, Types.NUMERIC),//分区字段
				};


//		折扣方式	M	 DCTYPE                     INT,

		return insValue;
	}
	
	/**
	 * DCP_STOCK_DETAIL  库存流水账
	 * @return
	 */
	public static InsBean getIbTwStockDetail(){
		//对应账单主信息
		String[] columns = {
				"EID",
				"ORGANIZATIONNO",
				"STOCK_TYPE",
				"DOC_TYPE",
				"ITEM",
				
				"PLUNO",
				"DOCNO",
				"WAREHOUSE",
				"BDATE",
				"PUNIT",
				
				"WUNIT",
				"PQTY",
				"UNIT_RATIO",
				"ACCOUNT_DATE",
				"ACCOUNT_TIME",
				
				"ACCOUNTBY",
				"WQTY",
				
		};
		InsBean ib = new InsBean("DCP_STOCK_DETAIL", columns);
		return ib;
	}
	
	/**
	 * DCP_STOCK_DETAIL  库存流水账 赋值
	 * @return
	 */
	public static DataValue[] doPutTwStockDetail(HllFoodDetail foodDetail,String saleNo,String shopId,int item,BigDecimal Qty,BigDecimal Rqty,String sDate,String sTime,String checkoutBy,String unit){
		
		//企业编号	 EID               NVARCHAR2(5),
		//门店编号	 SHOPID                       nvarchar2(10),
		//商品编码	 PLUNO                        NVARCHAR2(40),
		String pluNo=HllUtils.getDefaultValueStr(foodDetail.getFoodCode());//foodCode	菜品编码，对应基本档-获取店铺菜品返回的foodCode
		if("".equals(pluNo)){
			pluNo=errorPluNo;
		}
		
		String foodName=HllUtils.getDefaultValueStr(foodDetail.getFoodName());
		//0102109 是餐盒费
		//0102111 是配送费
		//单位都是PCS
		if(foodName.equals("餐盒费")){
			unit="PCS";
			pluNo="0102109";
		}else if(foodName.equals("配送费")){
			unit="PCS";
			pluNo="0102111";
		}
		//异动类型	 STOCK_TYPE                 nvarchar2(1),	0:入  1:出  
		String stockType="1";
		//单据类型	 DOC_TYPE                 nvarchar2(2), 20-销售出库
		String docType="20";
		//仓库(门店)	 WareHouse                nvarchar2(10),
		String wareHouse=shopId;
		//组织编号	 ORGANIZATIONNO         NVARCHAR2(10),	 SHOP
		String organizatioNno=shopId;
		//异动单号	 DOCNO                     NVARCHAR2(30),
		//营业日期	 Bdate                       nvarchar2(8),
		String bdate=HllUtils.getDefaultValueStr(foodDetail.getReportDate());
		//异动单位	 PUNit                        nvarchar2(10),
		String pUnit=unit;
		//库存单位	 Wunit                        nvarchar2(10),
		String wNnit=unit;
		//异动数量	 PQTY                         number(23,8),
		BigDecimal pqty=Qty.subtract(Rqty);
		//库存换算率	 UNIT_RATIO                 number(23,8),
		BigDecimal unitRatio=new BigDecimal(1);
		//过账人	 ACCOUNTBY                  NVARCHAR2(10),
		//过账日期	 ACCOUNT_DATE             NVARCHAR2(8),
		//过账时间	 ACCOUNT_TIME             NVARCHAR2(6),
		//库存数量	 WQTY                        number(23,8),
		BigDecimal wqty=pqty;
		
		DataValue[] insValue = null;
		insValue = new DataValue[]
				{
						new DataValue(eId, Types.VARCHAR),
						new DataValue(organizatioNno, Types.VARCHAR),
						new DataValue(stockType, Types.VARCHAR),
						new DataValue(docType, Types.VARCHAR),
						new DataValue(item, Types.INTEGER),
						
						new DataValue(pluNo, Types.VARCHAR),
						new DataValue(saleNo, Types.VARCHAR),
						new DataValue(wareHouse, Types.VARCHAR),
						new DataValue(bdate, Types.VARCHAR),
						new DataValue(pUnit, Types.VARCHAR),
						
						new DataValue(wNnit, Types.VARCHAR),
						new DataValue(pqty, Types.DECIMAL),
						new DataValue(unitRatio, Types.DECIMAL),
						new DataValue(sDate, Types.VARCHAR),
						new DataValue(sTime, Types.VARCHAR),
						
						new DataValue(checkoutBy, Types.VARCHAR),
						new DataValue(wqty, Types.DECIMAL),
						
				};


//		异动单价	 Price                          number(23,8),
//		异动金额	 AMT                           number(23,8),


		return insValue;
	}
	
	public String getSDate(String createTime){
		String sDate=null;
		if(createTime!=null&&createTime.length()==14){
			sDate=createTime.substring(0, 8);
		}
		return sDate;
	}
	
	public String getSTime(String createTime){
		String sTime=null;
		if(createTime!=null&&createTime.length()==14){
			sTime=createTime.substring(8);
		}
		return sTime;
	}
	
	// 写日志
	public static void writelogFileName(String log){
		try{
			// 生成文件路径
			String sdFormat = new SimpleDateFormat("yyyyMMdd").format(new Date());// 当天日期
			String path = System.getProperty("user.dir") + "\\log\\" + logFileName + sdFormat + ".txt";
			File file = new File(path);
			
			String dirpath = System.getProperty("user.dir") + "\\log";
			File dirfile = new File(dirpath);
			if (!dirfile.exists()) {
				dirfile.mkdir();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			
			BufferedWriter output = new BufferedWriter(new FileWriter(file, true));
			// 前面加上时间
			String stFormat = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());// 当天日期
			String slog = stFormat + " " + log + "\r\n";
			output.write(slog);
			output.close();
		}catch(Exception e) {
			
		}
	}
	
	public String getTrace(Throwable t) {
        StringWriter stringWriter= new StringWriter();
        PrintWriter writer= new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer= stringWriter.getBuffer();
        return buffer.toString();
    }


}
