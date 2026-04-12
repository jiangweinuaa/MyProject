package com.dsc.spos.scheduler.job;

import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.Size;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.sql.Select;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

import com.dsc.spos.config.SPosConfig.ProdInterface;
import com.dsc.spos.dao.DataProcessBean;
import com.dsc.spos.dao.DataValue;
import com.dsc.spos.dao.InsBean;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class WarningLogCreate extends InitJob 
{
	Logger logger = LogManager.getLogger(WarningLogCreate.class.getName());
	static boolean bRun=false;//标记此服务是否正在执行中
	String warningLogFileName = "WarningLogCreate";
	String langType = "zh_CN";
	
	
	public WarningLogCreate()
	{
		
	}
	public String doExe() throws Exception
	{
		// 返回信息
		String sReturnInfo = "";
		
		logger.info("\r\n***************WarningLogCreate同步START****************\r\n");
		HelpTools.writelog_fileName("【WarningLogCreate循环处理订单消息】同步START！",warningLogFileName);
			
		try 
		{
			//此服务是否正在执行中
			if (bRun)
			{		
				logger.info("\r\n*********WarningLogCreate同步正在执行中,本次调用取消:************\r\n");
				HelpTools.writelog_fileName("【WarningLogCreate循环处理】同步正在执行中,本次调用取消！",warningLogFileName);
				return sReturnInfo;
			}

			bRun=true;
			
			Date jobRunDate = new Date();
			String curDateTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(jobRunDate);
			String curDateStr = new SimpleDateFormat("yyyy-MM-dd").format(jobRunDate);
			String curBDateStr =  new SimpleDateFormat("yyyyMMdd").format(jobRunDate);
			String curTimeStr = new SimpleDateFormat("HH:mm").format(jobRunDate);
			String curMonthSt = curDateStr.substring(5, 7);
			String curDayStr = curDateStr.substring(8);
			
			String sql ="";
			sql = this.getHeadSql(curTimeStr);
			//获取下 云中台前端地址
			List<Map<String,Object>> ShopManagerUrlList = new ArrayList<Map<String,Object>>();
			
 			List<Map<String, Object>> getHeadData = this.doQueryData(sql, null);	
			if(getHeadData!=null&&getHeadData.isEmpty()==false) 
			{
				//获取下多语言
				List<ProdInterface> lstProd=StaticInfo.psc.getT100Interface().getProdInterface();
				if(lstProd!=null&&!lstProd.isEmpty())
				{
					langType=lstProd.get(0).getHostLang().getValue();
				}
				
				if(langType!=null||langType.trim().isEmpty())
				{
					langType = "zh_CN";
				}		
			
			  //region  查询下云中台前端地址
				try 
				{
					ShopManagerUrlList = this.doQueryData(" select * from platform_basesettemp where item='ShopManagerUrl'", null);
		
				} 
				catch (Exception e) 
				{
					
				}									
				//endregion
				
				
				//region 循环监控编号
				for (Map<String, Object> map : getHeadData) 
				{
					String eId = map.get("EID").toString();
					String warningNo = map.get("BILLNO").toString();
					String warningName = map.get("BILLNAME").toString();
					String billType = map.get("BILLTYPE").toString();
					String warningType = map.get("WARNINGTYPE").toString();//order：零售单,point：会员积分,card：储值卡
					String warningItem = map.get("WARNINGITEM").toString();// order_inTime：每日%点后的零售单,order_value：每日单笔金额超过%元的零售单,order_period：每日%点至%点的零售单,point_sun：同一会员的每日交易单超过%笔,point_over：同一会员的每日总积分超过%分,point_close：每日闭店后的会员积分,card_sum：同一储值卡的每日交易数超过%笔,card_close：每日闭店后发生的储值卡交易
					String pushTimeType = map.get("PUSHTIMETYPE").toString();//timely：及时推送,today：当日定时,morrow：次日定时
					String pushTime = map.get("PUSHTIME").toString();					
					String startTime = map.get("STARTTIME").toString();
					String endTime = map.get("ENDTIME").toString();
					String orderQty = map.get("ORDERQTY").toString();
					String orderAmt = map.get("ORDERAMT").toString();
					String pointQty = map.get("POINTQTY").toString();
					String restrictShop = map.get("RESTRICTSHOP").toString();// limit：限用,noLimit：不限用
					String templateType = map.get("TEMPLATETYPE").toString();// orderTem：零售单,pointTem：会员积分,cardTem：储值卡
					try 
					{
						boolean isFalse = false;
						StringBuffer errorBuf = new StringBuffer("");
						if(warningType==null||warningType.trim().isEmpty())
						{
							errorBuf.append("监控类型为空，");
							isFalse = true;
						}
						if(pushTimeType==null||pushTimeType.trim().isEmpty())
						{
							errorBuf.append("推送类型为空，");
							isFalse = true;
						}
						if(templateType==null||templateType.trim().isEmpty())
						{
							errorBuf.append("消息模板为空，");
							isFalse = true;
						}
						if(restrictShop==null||restrictShop.trim().isEmpty())
						{
							errorBuf.append("是否限用门店为空，");
							isFalse = true;
						}
						
						if(isFalse)
						{
							HelpTools.writelog_fileName("【WarningLogCreate资料异常】异常！监控号="+warningNo+" "+errorBuf.toString(),warningLogFileName);
							continue;
						}
						
	          String warningDate = "";//监控单据的日期 yyyy-MM-dd
	          String pushTimeTypeDescription ="";
						
						//监控时间类型  timely：及时推送,today：当日定时,morrow：次日定时
						if(pushTimeType.toLowerCase().equals("timely"))
						{		
							pushTimeTypeDescription ="及时推送 "+pushTime;
							HelpTools.writelog_fileName("【WarningLogCreate推送时间类型timely暂不支持】异常！监控号="+warningNo+" 推送时间类型pushTimeType="+pushTimeType+"暂不支持！",warningLogFileName);
							continue;								
						}					
						else if(pushTimeType.toLowerCase().equals("today"))//监控当天
						{
							pushTimeTypeDescription ="当日定时 "+pushTime;
							warningDate = curDateStr;
						}
						else if(pushTimeType.toLowerCase().equals("morrow"))//监控前一天
						{
							Calendar myTempcal = Calendar.getInstance();
							myTempcal.setTime(jobRunDate);
							myTempcal.add(Calendar.DATE, -1);						
							warningDate = new SimpleDateFormat("yyyy-MM-dd").format(myTempcal.getTime());
							pushTimeTypeDescription ="次日定时 "+pushTime;
						}
						else 
						{
							HelpTools.writelog_fileName("【WarningLogCreate推送时间类型无效】异常！监控号="+warningNo+" 推送时间类型pushTimeType="+pushTimeType+"无效！",warningLogFileName);
							continue;			
			
						}						
						//region 查询看下这监控编号今天有没有运行过
						if(IsRunWarning(eId, warningNo, curDateStr))
						{
							HelpTools.writelog_fileName("【WarningLogCreate今天已经执行过了，】监控号="+warningNo,warningLogFileName);
							continue;	
						}
						//endregion
						
					  //region 查询适用门店						
						sql = "";
						sql = this.getWarningShopSql(eId, warningNo, restrictShop);
						List<Map<String, Object>> getShopList = this.doQueryData(sql, null);
						if(getShopList==null||getShopList.isEmpty())
						{
							HelpTools.writelog_fileName("【WarningLogCreate适用门店列表为空】异常！监控号="+warningNo+" 适用门店列表为空!",warningLogFileName);
							continue;		
						}
						//这里使用康总的 with as 
						String withasSql_ShopList="";		
						String sJoinOrderno="";
						String sJoinShop="";
						for (Map<String, Object> mapShop : getShopList) 
						{
							sJoinOrderno+=mapShop.get("SHOPID").toString()+",";
							sJoinShop+=mapShop.get("SHOPNAME").toString()+",";
			
						}						
						
						Map<String, String> mapshopstr=new HashMap<String, String>();
						mapshopstr.put("SHOPID", sJoinOrderno);
						mapshopstr.put("SHOPNAME", sJoinShop);			
								
						MyCommon cm=new MyCommon();
						withasSql_ShopList=cm.getFormatSourceMultiColWith(mapshopstr);					
						mapshopstr=null;
						cm=null;	
						//endregion
									
						sql = "";
						String warningItemDescription = "";
						String templateTypeName = "";
						String templateTypeTitle = "";
						String msgBegin="";
						String msgMiddle="";
						String msgEnd="请点击链接查看更多数据，谢谢！";
						String linkUrl="链接地址:xxxxxxxx";//格式：http://eliutong2.digiwin.com.cn/retail/#/main/ding_EwarningMonitor
						String fixUrl = "#/main/ding_EwarningMonitor";
						String dcpURL = this.getDCPUrl(eId, ShopManagerUrlList);//格式 :http://eliutong2.digiwin.com.cn/retail/#/main3
						if(dcpURL!=null&&dcpURL.isEmpty()==false)
						{
							int indexOf_1 = dcpURL.indexOf("#");
							if(indexOf_1>0)
							{
								String s1 =  dcpURL.substring(0, indexOf_1);
								linkUrl="链接地址:"+s1+fixUrl;
							}
							
						}
					  
						if(warningType.toLowerCase().equals("order"))
						{		
							templateTypeName = "零售单异常消息模板";
							templateTypeTitle = "零售单异常提醒";
							msgBegin ="嗨！mm月dd号监控到的异常零售单异常如下，请尽快核实：";		
							msgBegin = msgBegin.replace("mm", curMonthSt).replace("dd", curDayStr);
							if(warningItem.toLowerCase().equals("order_value"))//每日单笔金额超过%元的零售单
							{
								double baseAmount = 0;//超过标准金额
								try {
									baseAmount = Double.parseDouble(orderAmt);
								} catch (Exception e) {
									// TODO: handle exception
								}
								warningItemDescription = "每日单笔金额超过"	+baseAmount+"元的零售单";
								msgMiddle="	<xx店>: 当日单笔金额超过<xx>元的零售单有<xx>笔";
								sql = this.getOrder_ValueSql(eId, warningDate, baseAmount, withasSql_ShopList);															
								
							}
							else if(warningItem.toLowerCase().equals("order_intime"))//每日%点后的零售单
							{
								warningItemDescription = "每日"	+startTime+"点后的零售单";
								msgMiddle="	<xx店>: 当日<hh:mm>点后的零售单有<xx>笔 ";
								sql = this.getOrder_inTimeSql(eId, warningDate, startTime, withasSql_ShopList);
								 
							}
							else if(warningItem.toLowerCase().equals("order_period"))//每日%点至%点的零售单,
							{
								warningItemDescription = "每日"	+startTime+"至"+endTime+"点的零售单";
								msgMiddle="	<xx店>: 当日<hh:mm>点至<hh:mm>点的零售单有<xx>笔 ";
								sql = this.getOrder_PeriodSql(eId, warningDate, startTime, endTime, withasSql_ShopList);								
							}
							else 
							{
								HelpTools.writelog_fileName("【WarningLogCreate监控项无效】异常！监控号="+warningNo+" 监控项warningItem="+warningItem+"无效！",warningLogFileName);
								continue;							
							}											
							
						}
						else if(warningType.toLowerCase().equals("point"))
						{
							templateTypeName = "积分消费异常消息模板";
							templateTypeTitle = "积分消费异常提醒";
							msgBegin ="嗨！mm月dd号监控到的积分消费异常如下，请尽快核实：";	
							msgBegin = msgBegin.replace("mm", curMonthSt).replace("dd", curDayStr);
							if(warningItem.toLowerCase().equals("point_sum"))//同一会员的每日交易单超过%笔,
							{
								int baseQty = 0;
								try {
									baseQty = Integer.parseInt(orderQty);
								} catch (Exception e) {
									// TODO: handle exception
								}
								warningItemDescription = "同一会员的每日交易单超过%笔";
								warningItemDescription = warningItemDescription.replace("%", baseQty+"");
								msgMiddle="	当日有count个会员的交易单超过sum笔";
								msgMiddle=msgMiddle.replace("sum", baseQty+"");
								sql = this.getPoint_SumSql(eId, warningDate, baseQty, withasSql_ShopList);															
								
							}
							else if(warningItem.toLowerCase().equals("point_over"))//同一会员的每日总积分超过%分
							{
								warningItemDescription = "同一会员的每日总积分超过%分";
								double basePointQty = 0;//超过标准金额
								try {
									basePointQty = Double.parseDouble(pointQty);
								} catch (Exception e) {
									// TODO: handle exception
								}
								warningItemDescription = warningItemDescription.replace("%", basePointQty+"");
								msgMiddle="	当日有count个会员的获得的积分超过point积分";
								msgMiddle=msgMiddle.replace("point", basePointQty+"");
								sql = this.getPoint_OverSql(eId, warningDate, basePointQty, withasSql_ShopList);
								 
							}
							else if(warningItem.toLowerCase().equals("point_close"))//每日闭店后的会员积分,
							{
								warningItemDescription = "每日闭店后的会员积分";
								msgMiddle="	当日有count个会员存在闭店积分 ";
								sql = this.getPoint_CloseSql(eId, warningDate, pushTimeType, pushTime, withasSql_ShopList);					
							}
							else 
							{
								HelpTools.writelog_fileName("【WarningLogCreate监控项无效】异常！监控号="+warningNo+" 监控项warningItem="+warningItem+"无效！",warningLogFileName);
								continue;							
							}		
						}
						else if(warningType.toLowerCase().equals("card"))
						{
							templateTypeName = "卡消费异常消息模板";
							templateTypeTitle = "储值消费异常提醒";
							msgBegin ="嗨！mm月dd号监控到的储值消费异常如下，请尽快核实：";	
							msgBegin = msgBegin.replace("mm", curMonthSt).replace("dd", curDayStr);
							if(warningItem.toLowerCase().equals("card_sum"))//同一储值卡的每日交易数超过%笔,
							{
								int baseQty = 0;
								try {
									baseQty = Integer.parseInt(orderQty);
								} catch (Exception e) {
									// TODO: handle exception
								}
								warningItemDescription = "同一储值卡的每日交易数超过%笔";
								warningItemDescription = warningItemDescription.replace("%", baseQty+"");
								msgMiddle=" 当日有count个储值卡的交易数超过sum笔";
								msgMiddle=msgMiddle.replace("sum", baseQty+"");
								sql = this.getCard_SumSql(eId, warningDate, baseQty, withasSql_ShopList);															
								
							}						
							else if(warningItem.toLowerCase().equals("card_close"))//每日闭店后发生的储值卡交易
							{							
								warningItemDescription = "每日闭店后发生的储值卡交易";
								msgMiddle="	当日有count个储值卡存在闭店后交易 ";
								sql = this.getCard_CloseSql(eId, warningDate, pushTimeType, pushTime, withasSql_ShopList);					
											
							}
							else 
							{
								HelpTools.writelog_fileName("【WarningLogCreate监控项无效】异常！监控号="+warningNo+" 监控项warningItem="+warningItem+"无效！",warningLogFileName);
								continue;							
							}		
							
							
							
						}
						else
						{
							HelpTools.writelog_fileName("【WarningLogCreate监控类型无效】异常！监控号="+warningNo+" 监控类型warningType="+warningType+"无效！",warningLogFileName);
							continue;
						}
						
						HelpTools.writelog_fileName("【WarningLogCreate查询SQL】监控号="+warningNo+" sql=【"+sql+"】",warningLogFileName);
						//查询监控内容
						PosPub.iTimeoutTime=200;
						List<Map<String, Object>> getWarningLogList = this.doQueryData(sql, null);
						HelpTools.writelog_fileName("【WarningLogCreate查询SQL】执行完成！监控号="+warningNo,warningLogFileName);
						PosPub.iTimeoutTime=30;
						//获取监控日志单号
						String warningLogNo = this.getWarningLogNo(eId, warningType, curBDateStr);
						
						ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
						
					  //DCP_WARNING预警
						String[] columns_warning ={"EID","ID","BILLNO","BILLNAME","BILLTYPE","WARNINGTYPE","WARNINGITEMDESCRIPTION","PUSHTIMEDESCRIPTION","PUSHMAN","PUSHWAY",
								"PUSHTIME","TEMPLATETYPE","TEMPLATETYPENAME","TEMPLATETITLE","MSGBEGIN","MSGMIDDLE","MSGEND","LINKURL","MEMO"};
						
						
					  //region 循环单身
						StringBuilder msgMiddleBuf = new StringBuilder("");
						boolean isExistWarningData = false;//是否监控到异常数据
						//为空 也没写一笔，否则还会不停的执行监控
						if(getWarningLogList==null||getWarningLogList.isEmpty())
						{
							msgMiddleBuf.append("没有发现异常！");
							HelpTools.writelog_fileName("【WarningLogCreate循环处理】监控没有发现异常！监控号="+warningNo,warningLogFileName);
							
						}
						else
						{
							isExistWarningData = true;
							
							String[] columns_warning_detail ={"EID","ID","SERIALNO","BILLNO","ORDERTYPE","ORDERNO","CONTMAN","CONTTEL","MEMBERID","MEMBERNAME","SHOPID","SHOPNAME",
									"CREATE_DATETIME","SHIPTYPE","SHIPPINGSHOP","SHIPPINGSHOPNAME","ISSHIPCOMPANY","TOT_OLDAMT","TOT_AMT","PAYAMT","CARDPAYAMT",
									"PAYCODE","PAYCODEERP","PAYNAME","CARDTYPEID","CARDTYPENAME","CARDNO","POINTQTY","PAYSTATUS","STATUS","REFUNDSTATUS","BDATE"};
							
							String pre_condition="";//前一次循环的分组条件，比如 门店ID ，cardNo
							int pre_condition_count=0;//前一次同一个门店的记录数;
							int serialNo = 1;//总的记录数
							int condition_class_count = 0;//不同会员卡 种类
							
							
							for (int i =0;i<getWarningLogList.size();i++)
							{
								Map<String, Object> logMap = getWarningLogList.get(i);
								String shopId = logMap.get("SHOPID").toString();
								String shopName = logMap.get("SHOPNAME").toString();
								String cardNo = logMap.get("CARDNO").toString();//DCP_SALE单头的会员卡号 
								String memberId = cardNo;//目前memberid没有值
								String cardPayNo =  logMap.get("CARDPAYNO").toString();//DCP_SALE_pay的储值卡 卡号
								String condition ="";
								if(warningType.toLowerCase().equals("order"))
								{
									condition = shopId;
								}
							  else if(warningType.toLowerCase().equals("point"))
								{
									condition = cardNo;
								}
							  else if(warningType.toLowerCase().equals("card"))
								{
									condition = cardPayNo;
									cardNo = cardPayNo;//这里重新赋值 真正的储值卡，后续保存数据库
								}
								
								if (condition==null||condition.isEmpty()) 
								{
									continue;					
				        }
								
								if(i == 0)//第一条记录
								{
									pre_condition_count = 1;
									pre_condition = condition;
									condition_class_count = 1;
								}			
								else
								{
									if(condition.equals(pre_condition))//当前分组条件=等于上一次的条件，
									{
										pre_condition_count ++;
										pre_condition = condition;
										
									}
									else
									{
										condition_class_count++;//和之前不一样的 ，种类+1
										if(warningItem.toLowerCase().equals("order_intime"))
										{
											msgMiddleBuf.append(this.getMsgMiddle_Order_inTime(condition, shopName, startTime, pre_condition_count));
										}
										else if(warningItem.toLowerCase().equals("order_value"))
										{
											msgMiddleBuf.append(this.getMsgMiddle_Order_Value(condition, shopName, orderAmt, pre_condition_count));
										}
										else if(warningItem.toLowerCase().equals("order_period"))
										{
											msgMiddleBuf.append(this.getMsgMiddle_Order_Period(condition, shopName, startTime, endTime,pre_condition_count));
										}
										/*else if(warningItem.toLowerCase().equals("point_sum"))
										{
											
										}
										else if(warningItem.toLowerCase().equals("point_over"))
										{
											
										}
										else if(warningItem.toLowerCase().equals("point_close"))
										{
											
										}
										else if(warningItem.toLowerCase().equals("card_sum"))
										{
											
										}
										else if(warningItem.toLowerCase().equals("card_close"))
										{
											
										}*/
										
										
										//开始下一个分组条件了
										pre_condition_count = 1;
										pre_condition = condition;
										
									}																														
									
								}
																						
								if(i==getWarningLogList.size()-1)//如果是最后一次循环,那么一定要处理
								{
									
									if(warningItem.toLowerCase().equals("order_intime"))
									{
										msgMiddleBuf.append(this.getMsgMiddle_Order_inTime(condition, shopName, startTime, pre_condition_count));
									}
									else if(warningItem.toLowerCase().equals("order_value"))
									{
										msgMiddleBuf.append(this.getMsgMiddle_Order_Value(condition, shopName, orderAmt, pre_condition_count));
									}
									else if(warningItem.toLowerCase().equals("order_period"))
									{
										msgMiddleBuf.append(this.getMsgMiddle_Order_Period(condition, shopName, startTime, endTime,pre_condition_count));
									}
									/*else if(warningItem.toLowerCase().equals("point_sum"))
									{
										
									}
									else if(warningItem.toLowerCase().equals("point_over"))
									{
										
									}
									else if(warningItem.toLowerCase().equals("point_close"))
									{
										
									}
									else if(warningItem.toLowerCase().equals("card_sum"))
									{
										
									}
									else if(warningItem.toLowerCase().equals("card_close"))
									{
										
									}*/
								}
								
								DataValue[] insValue_warning_detail = new DataValue[] 
										{
												new DataValue(eId, Types.VARCHAR),
												new DataValue(warningLogNo, Types.VARCHAR), 
												new DataValue(serialNo, Types.VARCHAR),
												new DataValue(warningNo, Types.VARCHAR),
												new DataValue(logMap.get("TYPE").toString(), Types.VARCHAR),
												new DataValue(logMap.get("SALENO").toString(), Types.VARCHAR),
												new DataValue(logMap.get("CONTMAN").toString(), Types.VARCHAR),
												new DataValue(logMap.get("CONTTEL").toString(), Types.VARCHAR),
												new DataValue(memberId, Types.VARCHAR),
												new DataValue(logMap.get("MEMBERNAME").toString(), Types.VARCHAR),
												new DataValue(shopId, Types.VARCHAR),
												new DataValue(shopName, Types.VARCHAR),
												new DataValue(logMap.get("SDATE").toString()+logMap.get("STIME").toString(), Types.VARCHAR),
												new DataValue(logMap.get("SHIPTYPE").toString(), Types.VARCHAR),
												new DataValue(logMap.get("SHIPPINGSHOP").toString(), Types.VARCHAR),
												new DataValue(logMap.get("SHIPPINGSHOPNAME").toString(), Types.VARCHAR),
												new DataValue(logMap.get("ISSHIPCOMPANY").toString(), Types.VARCHAR),
												new DataValue(logMap.get("TOT_OLDAMT").toString()==null?"0":logMap.get("TOT_OLDAMT").toString(), Types.VARCHAR),
												new DataValue(logMap.get("TOT_AMT").toString()==null?"0":logMap.get("TOT_AMT").toString(), Types.VARCHAR),
												new DataValue(logMap.get("PAYAMT").toString()==null?"0":logMap.get("PAYAMT").toString(), Types.VARCHAR),
												new DataValue(logMap.get("CARDPAYAMT").toString()==null?"0":logMap.get("CARDPAYAMT").toString(), Types.VARCHAR),
												new DataValue(logMap.get("PAYCODE").toString(), Types.VARCHAR),
												new DataValue(logMap.get("PAYCODEERP").toString(), Types.VARCHAR),
												new DataValue(logMap.get("PAYNAME").toString(), Types.VARCHAR),
												new DataValue(logMap.get("CARDTYPEID").toString(), Types.VARCHAR),
												new DataValue(logMap.get("CARDTYPENAME").toString(), Types.VARCHAR),
												new DataValue(cardNo, Types.VARCHAR),	
												new DataValue(logMap.get("POINTQTY").toString()==null?"0":logMap.get("POINTQTY").toString(), Types.VARCHAR),			
												new DataValue(logMap.get("PAYSTATUS").toString(), Types.VARCHAR),			
												new DataValue(logMap.get("STATUS").toString(), Types.VARCHAR),
												new DataValue(logMap.get("REFUNDSTATUS").toString(), Types.VARCHAR),
												new DataValue(logMap.get("BDATE").toString(), Types.VARCHAR)
										};
								
								InsBean ib_warning_detail = new InsBean("DCP_WARNINGLOG_DETAIL", columns_warning_detail);
								ib_warning_detail.addValues(insValue_warning_detail);
								DPB.add(new DataProcessBean(ib_warning_detail)); 	
								serialNo++;
								
							}
							
						
							
							if(warningType.toLowerCase().equals("point")||warningType.toLowerCase().equals("card"))
							{
								msgMiddle= msgMiddle.replace("count", condition_class_count+"");//"	当日有<count>个会员的交易单超过<sum>笔";
								msgMiddleBuf.append(msgMiddle);
							}
							
							
						}
						//endregion
						
						String memo = "";
						msgMiddle = msgMiddleBuf.toString();						
						if (msgMiddle.length()>1000) 
						{
							msgMiddle = msgMiddle.substring(0, 1000);
							memo ="消息内容太多,已截取！";					
						}
						
						DataValue[] insValue_warning = new DataValue[] 
								{
										new DataValue(eId, Types.VARCHAR),
										new DataValue(warningLogNo, Types.VARCHAR), 							
										new DataValue(warningNo, Types.VARCHAR),
										new DataValue(warningName, Types.VARCHAR),
										new DataValue(billType, Types.VARCHAR),
										new DataValue(warningType, Types.VARCHAR),
										new DataValue(warningItemDescription, Types.VARCHAR),
										new DataValue(pushTimeTypeDescription, Types.VARCHAR),
										new DataValue("", Types.VARCHAR),
										new DataValue("", Types.VARCHAR),
										new DataValue(curDateStr+" "+pushTime+":00", Types.VARCHAR),//yyyy-MM-dd HH:mm:ss
										new DataValue(templateType, Types.VARCHAR),
										new DataValue(templateTypeName, Types.VARCHAR),
										new DataValue(templateTypeTitle, Types.VARCHAR),
										new DataValue(msgBegin, Types.VARCHAR),
										new DataValue(msgMiddle, Types.VARCHAR),
										new DataValue(msgEnd, Types.VARCHAR),
										new DataValue(linkUrl, Types.VARCHAR),
										new DataValue(memo, Types.VARCHAR)									
								};
						
						InsBean ib_warning = new InsBean("DCP_WARNINGLOG",columns_warning);
						ib_warning.addValues(insValue_warning);
						DPB.add(new DataProcessBean(ib_warning));
						this.doExecuteDataToDB(DPB);
						HelpTools.writelog_fileName("【WarningLogCreate循环处理】监控日志写入成功！监控号="+warningNo+" 监控日志单号="+warningLogNo,warningLogFileName);
						
						//region 有异常数据才写推送人推送方式
						if(isExistWarningData)
						{
							 this.setPushManWay(eId, langType, warningNo, warningLogNo);	
						}					 				
						//endregion
						
						
			
			
					} 
					catch (Exception e) 
					{
						PosPub.iTimeoutTime=30;
						HelpTools.writelog_fileName("【WarningLogCreate循环处理】异常！监控号="+warningNo+" 异常信息："+e.getMessage(),warningLogFileName);
						continue;					
					}
			
		
				}
				//endregion
				
			}
			else 
			{
				//
				sReturnInfo="无符合要求的数据！";
				HelpTools.writelog_fileName("【JDDJOrderGet循环处理订单消息】没有需要处理的订单消息！",warningLogFileName);
				logger.info("\r\n******JDDJOrderGet没有需要获取的订单ID******\r\n");
			}
		
	
		} 
		catch (Exception e) 
		{
			logger.error("\r\n***************WarningLogCreate异常"+e.getMessage()+"****************\r\n");
			sReturnInfo="错误信息:" + e.getMessage();
			HelpTools.writelog_fileName("【WarningLogCreate循环处理】异常！"+sReturnInfo,warningLogFileName);
	
		}
		finally 
		{
			PosPub.iTimeoutTime=30;
			bRun=false;//
		}

		logger.info("\r\n***************WarningLogCreate同步END****************\r\n");
		HelpTools.writelog_fileName("【WarningLogCreate循环处理】同步END！",warningLogFileName);
		return sReturnInfo;	
		
	}
	
	protected void doExecuteDataToDB(List<DataProcessBean> pData) throws Exception {
		if (pData == null || pData.size() == 0) {
			return;
		}
		StaticInfo.dao.useTransactionProcessData(pData);

	}
	
	private String getHeadSql(String pushTime)
	{
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from dcp_warning");
		sqlbuf.append(" where status='100'");//已经启用
		sqlbuf.append(" and pushTime<='"+pushTime+"'");
		
		return sqlbuf.toString();		
	}
	
	
	private String getWarningShopSql(String eid,String billno,String type)
	{
		StringBuffer sqlbuf = new StringBuffer("");
		
		if(type!=null&&type.toLowerCase().equals("nolimit"))
		{
			sqlbuf.append("select * from (");
			sqlbuf.append("select distinct a.organizationno as shopid,b.org_name as shopname from DCP_ORG a");			
			sqlbuf.append(" inner join DCP_ORG_lang b on a.eId =b.eId and a.organizationno=b.organizationno and b.lang_type='"+langType+"'");
			sqlbuf.append(" where  a.eId='"+eid+"' and a.org_form='2' and a.status='100'");			
			sqlbuf.append(")");
			
		}
		else 
		{
			sqlbuf.append("select * from (");
			sqlbuf.append("select distinct a.shopid,b.org_name as shopname from dcp_warning_pickshop a");			
			sqlbuf.append(" left join DCP_ORG_lang b on a.eid =b.eId and a.shopid=b.organizationno and b.lang_type='"+langType+"'");
			sqlbuf.append(" where a.eid='"+eid+"' and a.billno='"+billno+"' ");			
			sqlbuf.append(")");
		
		}
		
		
		return sqlbuf.toString();		
		
	}

	/**
	 * 监控项order_value：每日单笔金额超过%元的零售单
	 * @param eid
	 * @param warningdate
	 * @param baseamount
	 * @param withasSql
	 * @return
	 */
	private String getOrder_ValueSql(String eid,String warningdate,Double baseamount, String withasSql)
	{
		StringBuffer sqlbuf = new StringBuffer("");  	
		warningdate =warningdate.replace("-", "");//yyyy-MM-dd 转yyymmdd
		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ("+withasSql+")");
		sqlbuf.append(" select 'saleOrder' as type, a.bdate, a.SHOPID,a.saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,N'' as SHIPTYPE,N'' AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.TOT_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from p1");
		sqlbuf.append(" inner join  DCP_SALE a on p1.shopid= a.SHOPID");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=0 and a.status='100' and a.sdate='"+warningdate+"' and a.tot_amt>"+baseamount);
	
		sqlbuf.append(" union all ");
		
		sqlbuf.append(" select 'order' as type, a.bdate, a.SHOPID,a.orderno as saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,a.GETMODE as SHIPTYPE,a.GETSHOP AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.PAY_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO ");
		sqlbuf.append(" from p1");
		sqlbuf.append(" inner join  oc_order a on p1.shopid= a.SHOPID");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=3 and a.status='100' and a.sdate='"+warningdate+"' and a.tot_amt>"+baseamount);
			
		sqlbuf.append(") order by SHOPID,stime");
		
		return sqlbuf.toString();
	}
	
	/**
	 * 监控项order_intime：每日%点后的零售单
	 * @param eid
	 * @param warningdate
	 * @param basetime
	 * @param withasSql
	 * @return
	 */
	private String getOrder_inTimeSql(String eid,String warningdate,String basetime, String withasSql)
	{
		warningdate =warningdate.replace("-", "");//yyyy-MM-dd 转yyymmdd
		basetime = basetime.replace(":", "")+"00"; //传入时间格式 HH:mm，数据库格式hhmmss
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ("+withasSql+")");
		sqlbuf.append(" select 'saleOrder' as type, a.bdate, a.SHOPID,a.saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append("  N'' AS membername,N'' as SHIPTYPE,N'' AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.TOT_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from p1");
		sqlbuf.append(" inner join  DCP_SALE a on p1.shopid= a.SHOPID");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=0 and a.status='100' and a.sdate='"+warningdate+"' and a.stime>='"+basetime+"'");
		
		sqlbuf.append(" union all ");
		
		sqlbuf.append(" select 'order' as type, a.bdate, a.SHOPID,a.orderno as saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append("  N'' AS membername,a.GETMODE as SHIPTYPE,a.GETSHOP AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.PAY_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO ");
		sqlbuf.append(" from p1");
		sqlbuf.append(" inner join  oc_order a on p1.shopid= a.SHOPID");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=3 and a.status='100' and a.sdate='"+warningdate+"' and a.stime>='"+basetime+"'");
		
		sqlbuf.append(") order by SHOPID,stime");
		
		return sqlbuf.toString();
	}
	
	/**
	 * 监控项 order_period：每日%点至%点的零售单,
	 * @param eid
	 * @param warningdate
	 * @param begintime
	 * @param endtime
	 * @param withasSql
	 * @return
	 */
	private String getOrder_PeriodSql(String eid,String warningdate,String begintime,String endtime, String withasSql)
	{
		warningdate =warningdate.replace("-", "");//yyyy-MM-dd 转yyymmdd
		begintime = begintime.replace(":", "")+"00"; //传入时间格式 HH:mm，数据库格式hhmmss
		endtime = endtime.replace(":", "")+"59";
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ("+withasSql+")");
		sqlbuf.append(" select 'saleOrder' as type, a.bdate, a.SHOPID,a.saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,N'' as SHIPTYPE,N'' AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.TOT_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from p1");
		sqlbuf.append(" inner join  DCP_SALE a on p1.shopid= a.SHOPID");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=0 and a.status='100' and a.sdate='"+warningdate+"' and a.stime>='"+begintime+"' and a.stime<='"+endtime+"'");
		
		sqlbuf.append(" union all ");
				
		sqlbuf.append(" select 'order' as type, a.bdate, a.SHOPID,a.orderno as saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,a.GETMODE as SHIPTYPE,a.GETSHOP AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.PAY_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO ");
		sqlbuf.append(" from p1");
		sqlbuf.append(" inner join  oc_order a on p1.shopid= a.SHOPID");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=3 and a.status='100' and a.sdate='"+warningdate+"' and a.stime>='"+begintime+"' and a.stime<='"+endtime+"'");
	
		sqlbuf.append(") order by SHOPID,stime");
		
		return sqlbuf.toString();
	}
	
	/**
	 * 同一会员的每日交易单超过%笔
	 * @param eid
	 * @param warningdate
	 * @param baseqty 
	 * @param withasSql
	 * @return
	 */
	private String getPoint_SumSql(String eid,String warningdate,int baseqty, String withasSql)
	{
		warningdate =warningdate.replace("-", "");//yyyy-MM-dd 转yyymmdd
		StringBuffer sqlbuf = new StringBuffer("");
		//子查询 找出卡消费次数大于 的cardno 销售单的type=0
	  String withasSql_1 = "  select cardno,count(*)   from DCP_SALE  a inner join p1 on a.SHOPID=p1.shopid where a.eId='"+eid+"' and a.cardno is not null and a.type=0 and a.status='100' and a.sdate='"
		+ warningdate + "' group by cardno having count(*)>"+baseqty;
	  
	 //子查询 找出订单卡消费次数大于 的cardno 订单的type=3
	  String withasSql_order = "  select cardno,count(*)   from oc_order  a inner join p1 on a.SHOPID=p1.shopid where a.eId='"+eid+"' and a.cardno is not null and a.type=3 and a.status='100' and a.sdate='"
		+ warningdate + "' group by cardno having count(*)>"+baseqty;

		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ("+withasSql+")");
		sqlbuf.append(" ,P2 AS ("+withasSql_1+") ");
		sqlbuf.append(" ,P3 AS ("+withasSql_order+") ");
		sqlbuf.append(" select 'saleOrder' as type, a.bdate, a.SHOPID,a.saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,N'' as SHIPTYPE,N'' AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.TOT_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from DCP_SALE a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" inner join  p2  on p2.cardno= a.cardno");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=0 and a.status='100' and a.sdate='"+warningdate+"'");
	
		sqlbuf.append(" union all ");
		//订单的type=3
		sqlbuf.append(" select 'order' as type, a.bdate, a.SHOPID,a.orderno as saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,a.GETMODE as SHIPTYPE,a.GETSHOP AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.PAY_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from oc_order a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" inner join  p3  on p3.cardno= a.cardno");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=3 and a.status='100' and a.sdate='"+warningdate+"'");
	
		
		sqlbuf.append(") order by cardno, SHOPID,stime");	
		return sqlbuf.toString();
	}
	
	/**
	 * 同一会员的每日总积分超过%分
	 * @param eid
	 * @param warningdate
	 * @param basepointqty
	 * @param withasSql
	 * @return
	 */
	private String getPoint_OverSql(String eid,String warningdate,Double basepointqty, String withasSql)
	{
		warningdate =warningdate.replace("-", "");//yyyy-MM-dd 转yyymmdd
		StringBuffer sqlbuf = new StringBuffer("");
		StringBuffer sqlbuf_withsql = new StringBuffer("");
	  //子查询 找出销售单订单 同一张卡合计消费大于多少的卡号 ( 销售单的type=0，订单的type=3)
		sqlbuf_withsql.append("select * from (");
		sqlbuf_withsql.append(" select cardno,sum(point_qty) as point_qty from (");
		
		sqlbuf_withsql.append("  select cardno, point_qty  from DCP_SALE  a inner join p1 on a.SHOPID=p1.shopid where a.eId='"+eid+"' and a.cardno is not null and a.type=0 and a.status='100' and a.sdate='"+ warningdate + "'");
		sqlbuf_withsql.append(" union all ");
		sqlbuf_withsql.append("  select cardno, point_qty  from oc_order  a inner join p1 on a.SHOPID=p1.shopid where a.eId='"+eid+"' and a.cardno is not null and a.type=3 and a.status='100' and a.sdate='"+ warningdate + "'");
		
		sqlbuf_withsql.append(") group by cardno");
		sqlbuf_withsql.append(") where point_qty>="+basepointqty);
		
	  String withasSql_con = sqlbuf_withsql.toString();

		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ("+withasSql+")");	
		sqlbuf.append(" ,P2 AS ("+withasSql_con+") ");
		sqlbuf.append(" select 'saleOrder' as type, a.bdate, a.SHOPID,a.saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,N'' as SHIPTYPE,N'' AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.TOT_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from DCP_SALE a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" inner join  p2  on p2.cardno= a.cardno");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=0 and a.status='100' and a.sdate='"+warningdate+"'");
	
		sqlbuf.append(" union all ");
		
		//订单的type=3
		sqlbuf.append(" select 'order' as type, a.bdate, a.SHOPID,a.orderno as saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,a.GETMODE as SHIPTYPE,a.GETSHOP AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.PAY_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from oc_order a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" inner join  p2  on p2.cardno= a.cardno");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=3 and a.status='100' and a.sdate='"+warningdate+"'");
	
		sqlbuf.append(") order by cardno, SHOPID,sdate,stime");	
		return sqlbuf.toString();
	}
	
	/**
	 * 闭店后积分
	 * @param eid
	 * @param warningdate
	 * @param pushtimetype
	 * @param pushtime
	 * @param withasSql
	 * @return
	 * @throws Exception
	 */
	private String getPoint_CloseSql(String eid,String warningdate,String pushtimetype,String pushtime, String withasSql) throws Exception
	{		 
		String edate =warningdate.replace("-", "");//闭店日期
		pushtime = pushtime.replace(":", "")+"00";//HHmmss;
		Date date_edate = new SimpleDateFormat("yyyy-MM-dd").parse(warningdate);//转成date类型			
		Calendar myTempcal = Calendar.getInstance();
		myTempcal.setTime(date_edate);
		myTempcal.add(Calendar.DATE, 1);						
		String bdate = new SimpleDateFormat("yyyyMMdd").format(myTempcal.getTime());//闭店后营业日期
		
		
		StringBuffer sqlbuf_with = new StringBuffer("");
		if(pushtimetype.toLowerCase().equals("today"))//监控当天 监控闭店后异常且推送时间为当日的,就是根据当日推送时间跑监控的时候，有两种情况：一，当日已经闭店了，抓闭店时间-推送时间这段时间内的单据；当日还未闭店，就不用抓了，没异常
		{
			sqlbuf_with.append("Select * from (");
			
			sqlbuf_with.append(" select shopid,shopname,edate,edate_sdate,edate_stime,bdate，bdate_sdate，"
				+ "(case when bdate_stime>'"+pushtime+"' then N'"+pushtime+"' else bdate_stime end) as bdate_stime from (");////监控当天闭店数据  如果当天已经日结需要对比下推送时间和当天日结时间
			sqlbuf_with.append("select shopid,shopname,edate,edate_sdate,edate_stime,"
				+ "NVL(bdate,'"+bdate+"') as bdate ,NVL(bdate_sdate,'"+edate+"') as bdate_sdate,NVL(bdate_stime,'"+pushtime+"') as bdate_stime from (");//没有第2天的营业日期 ，就比较推送时间
			sqlbuf_with.append(" with P1 AS ("+withasSql+")");	
			sqlbuf_with.append(" ,P2 AS (select distinct SHOPID,edate,sdate as edate_sdate,stime as edate_stime from DCP_DATEEND where edate='"+edate+"')");
			sqlbuf_with.append(" ,P3 AS (select SHOPID,bdate,sdate as bdate_sdate,min(stime) as bdate_stime from DCP_SQUAD where bdate='"+bdate+"' group by SHOPID,bdate,sdate)");
			sqlbuf_with.append(" select p1.shopid,p1.shopname,p2.edate,p2.edate_sdate,p2.edate_stime,p3.bdate,p3.bdate_sdate,p3.bdate_stime from p1 ");
			sqlbuf_with.append(" inner join p2 on p1.shopid=p2.SHOPID left join p3 on p1.shopid=p3.SHOPID");		
			sqlbuf_with.append(")");	
			sqlbuf_with.append(")");
			
			sqlbuf_with.append(")");
			
		}
		else
		{
			sqlbuf_with.append("select * from (");
			sqlbuf_with.append("select shopid,shopname,edate,edate_sdate,edate_stime,"
				+ "NVL(bdate,'"+bdate+"') as bdate ,NVL(bdate_sdate,'"+bdate+"') as bdate_sdate,NVL(bdate_stime,'"+pushtime+"') as bdate_stime from (");//没有第2天的营业日期 ，就比较推送时间
			sqlbuf_with.append(" with P1 AS ("+withasSql+")");	
			sqlbuf_with.append(" ,P2 AS (select distinct SHOPID,edate,sdate as edate_sdate,stime as edate_stime from DCP_DATEEND where edate='"+edate+"')");
			sqlbuf_with.append(" ,P3 AS (select SHOPID,bdate,sdate as bdate_sdate,min(stime) as bdate_stime from DCP_SQUAD where bdate='"+bdate+"' group by SHOPID,bdate,sdate)");
			sqlbuf_with.append(" select p1.shopid,p1.shopname,p2.edate,p2.edate_sdate,p2.edate_stime,p3.bdate,p3.bdate_sdate,p3.bdate_stime from p1 ");
			sqlbuf_with.append(" inner join p2 on p1.shopid=p2.SHOPID left join p3 on p1.shopid=p3.SHOPID");		
			sqlbuf_with.append(")");	
			sqlbuf_with.append(")");
		}

		String withSql = sqlbuf_with.toString();
		
	 //region 查询适用门店关联的闭店日期							
		List<Map<String, Object>> getShopList = this.doQueryData(withSql, null);
		if(getShopList==null||getShopList.isEmpty())
		{
			//HelpTools.writelog_fileName("【WarningLogCreate适用门店列表为空】异常！监控号="+warningNo+" 适用门店列表为空!",warningLogFileName);
			return "select * from dual where 1<>1 ";		
		}
		//这里使用康总的 with as 
		String withasSql_ShopList="";		
		String sJoinOrderno="";
		String sJoinShop="";
		String sJoinEDate="";
		String sJoinEDate_sdate="";
		String sJoinEDate_stime="";
		String sJoinBDate="";
		String sJoinBDate_sdate="";
		String sJoinBDate_stime="";
		
		for (Map<String, Object> mapShop : getShopList) 
		{
			sJoinOrderno+=mapShop.get("SHOPID").toString()+",";
			sJoinShop+=mapShop.get("SHOPNAME").toString()+",";
			sJoinEDate += mapShop.get("EDATE").toString()+",";
			sJoinEDate_sdate += mapShop.get("EDATE_SDATE").toString()+",";
			sJoinEDate_stime += mapShop.get("EDATE_STIME").toString()+",";
			sJoinBDate += mapShop.get("BDATE").toString()+",";
			sJoinBDate_sdate += mapShop.get("BDATE_SDATE").toString()+",";
			sJoinBDate_stime += mapShop.get("BDATE_STIME").toString()+",";
		}						
		
		Map<String, String> mapshopstr=new HashMap<String, String>();
		mapshopstr.put("SHOPID", sJoinOrderno);
		mapshopstr.put("SHOPNAME", sJoinShop);		
		mapshopstr.put("EDATE", sJoinEDate);	
		mapshopstr.put("EDATE_SDATE", sJoinEDate_sdate);	
		mapshopstr.put("EDATE_STIME", sJoinEDate_stime);	
		mapshopstr.put("BDATE", sJoinBDate);	
		mapshopstr.put("BDATE_SDATE", sJoinBDate_sdate);	
		mapshopstr.put("BDATE_STIME", sJoinBDate_stime);	
			
				
		MyCommon cm=new MyCommon();
		withasSql_ShopList=cm.getFormatSourceMultiColWith(mapshopstr);					
		mapshopstr=null;
		cm=null;	
		//endregion
		
		
		
		
		StringBuffer sqlbuf = new StringBuffer("");
		
		
		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ("+withasSql_ShopList+")");	
		sqlbuf.append(" select 'saleOrder' as type,a.bdate, a.SHOPID,a.saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,N'' as SHIPTYPE,N'' AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.TOT_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from DCP_SALE a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=0 and a.status='100'  and a.cardno is not null and a.POINT_QTY>0 "
			+ " and a.sdate>=p1.edate_sdate and a.stime>p1.edate_stime "
			+ " and a.sdate<=p1.bdate_sdate and a.stime<p1.bdate_stime");
		
		sqlbuf.append(" union all ");
		
		sqlbuf.append(" select 'order' as type,a.bdate, a.SHOPID,a.orderno as saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,a.GETMODE as SHIPTYPE,a.GETSHOP AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,a.PAY_AMT AS PAYAMT,0 AS CARDPAYAMT,N'' AS PAYCODE,N'' AS PAYCODEERP,N'' AS PAYNAME,N'' AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,N'' AS CARDPAYNO");
		sqlbuf.append(" from oc_order a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");		
		sqlbuf.append(" where a.eId='"+eid+"' and a.type=3 and a.status='100'  and a.cardno is not null  and a.POINT_QTY>0 "
				+ " and a.sdate>=p1.edate_sdate and a.stime>p1.edate_stime "
				+ " and a.sdate<=p1.bdate_sdate and a.stime<p1.bdate_stime");
			
		sqlbuf.append(") order by cardno, SHOPID,sdate,stime");	
		
		
		return sqlbuf.toString();
	}
	
	
	
	
	/**
	 * 同一储值卡的每日交易单超过%笔
	 * @param eid
	 * @param warningdate
	 * @param baseqty
	 * @param withasSql
	 * @return
	 */
	private String getCard_SumSql(String eid,String warningdate,int baseqty, String withasSql)
	{
		warningdate =warningdate.replace("-", "");//yyyy-MM-dd 转yyymmdd
		StringBuffer sqlbuf = new StringBuffer("");
	
	  StringBuffer sqlbuf_withsql = new StringBuffer("");
	  //子查询 找出销售单订单 同一张卡合计消费大于多少的卡号 ( 销售单的type=0，订单的type=3)
		sqlbuf_withsql.append("select * from (");
		sqlbuf_withsql.append("select cardno,count(*) from (");
		
		//销售单
		sqlbuf_withsql.append(" select b.cardno  from DCP_SALE  a inner join p1 on a.SHOPID=p1.shopid");
		sqlbuf_withsql.append(" inner join DCP_SALE_pay b on a.eId=b.eId and a.SHOPID=b.SHOPID and a.saleno=b.saleno");
		sqlbuf_withsql.append(" inner join  DCP_PAYFUNCnoinfo c on b.eId=c.eId and b.paycode=c.paycode and c.funcno in ('301','303')");
		sqlbuf_withsql.append(" where b.cardno is not null and b.ISORDERPAY<>'Y' and  b.eId='"+eid+"' and  a.eId='"+eid+"' and a.type=0 and a.status='100' and a.sdate='"+ warningdate + "' ");
		
		sqlbuf_withsql.append(" union all");
		
		//订单
		sqlbuf_withsql.append(" select b.cardno  from oc_order  a inner join p1 on a.SHOPID=p1.shopid");
		sqlbuf_withsql.append(" inner join oc_order_pay b on a.eId=b.eId and a.SHOPID=b.SHOPID and a.orderno=b.orderno");
		sqlbuf_withsql.append(" inner join  DCP_PAYFUNCnoinfo c on b.eId=c.eId and b.paycode=c.paycode and c.funcno in ('301','303')");
		sqlbuf_withsql.append(" where b.cardno is not null and b.eId='"+eid+"' and a.eId='"+eid+"' and a.type=3 and a.status='100' and a.sdate='"+ warningdate + "' ");

		sqlbuf_withsql.append(") group by cardno having count(*)>="+baseqty);
		sqlbuf_withsql.append(")");
		
	  String withasSql_con = sqlbuf_withsql.toString();
	  
	  
	  
		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ("+withasSql+")");
		sqlbuf.append(" ,P2 AS ("+withasSql_con+") ");
		sqlbuf.append(" select 'saleOrder' as type, a.bdate, a.SHOPID,a.saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,N'' as SHIPTYPE,N'' AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,b.pay as PAYAMT,b.pay as CARDPAYAMT,b.paycode AS PAYCODE,b.paycodeerp AS PAYCODEERP,b.payname AS PAYNAME,b.cttype AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,b.cardno AS CARDPAYNO ");
		sqlbuf.append(" from DCP_SALE a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" inner join DCP_SALE_pay b on a.eId=b.eId and a.SHOPID=b.SHOPID and a.saleno=b.saleno");
		sqlbuf.append(" inner join DCP_PAYFUNCnoinfo c on b.eId=c.eId and b.paycode=c.paycode and c.funcno in ('301','303')");//301 303是储值卡支付方式
		sqlbuf.append(" inner join  p2  on p2.cardno= b.cardno");
		sqlbuf.append(" where  b.cardno is not null and b.ISORDERPAY<>'Y' and b.eId='"+eid+"' and a.eId='"+eid+"' and a.type=0 and a.status='100' and a.sdate='"+warningdate+"'");
		
		sqlbuf.append(" union all ");
		
		sqlbuf.append(" select 'order' as type, a.bdate, a.SHOPID,a.orderno as saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,a.GETMODE as SHIPTYPE,a.GETSHOP AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,b.pay as PAYAMT,b.pay as CARDPAYAMT,b.paycode AS PAYCODE,b.paycodeerp AS PAYCODEERP,b.payname AS PAYNAME,b.cttype AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,b.cardno AS CARDPAYNO ");
		sqlbuf.append(" from oc_order a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" inner join oc_order_pay b on a.eId=b.eId and a.SHOPID=b.SHOPID and a.orderno=b.orderno");
		sqlbuf.append(" inner join DCP_PAYFUNCnoinfo c on b.eId=c.eId and b.paycode=c.paycode and c.funcno in ('301','303')");//301 303是储值卡支付方式
		sqlbuf.append(" inner join  p2  on p2.cardno= b.cardno");
		sqlbuf.append(" where  b.cardno is not null and b.eId='"+eid+"' and a.eId='"+eid+"' and a.type=0 and a.status='100' and a.sdate='"+warningdate+"'");
		
		
		sqlbuf.append(") order by cardno,SHOPID,saleno,stime");	
		return sqlbuf.toString();
	}
	
	/**
	 * 闭店后储值卡交易
	 * @param eid
	 * @param warningdate
	 * @param pushtimetype
	 * @param pushtime
	 * @param withasSql
	 * @return
	 * @throws Exception
	 */
	private String getCard_CloseSql(String eid,String warningdate,String pushtimetype,String pushtime, String withasSql) throws Exception
	{
		
		String edate =warningdate.replace("-", "");//闭店日期
		pushtime = pushtime.replace(":", "")+"00";//HHmmss
		Date date_edate = new SimpleDateFormat("yyyy-MM-dd").parse(warningdate);//转成date类型			
		Calendar myTempcal = Calendar.getInstance();
		myTempcal.setTime(date_edate);
		myTempcal.add(Calendar.DATE, 1);						
		String bdate = new SimpleDateFormat("yyyyMMdd").format(myTempcal.getTime());//闭店后营业日期
		
		
		StringBuffer sqlbuf_with = new StringBuffer("");
		if(pushtimetype.toLowerCase().equals("today"))//监控当天 监控闭店后异常且推送时间为当日的,就是根据当日推送时间跑监控的时候，有两种情况：一，当日已经闭店了，抓闭店时间-推送时间这段时间内的单据；当日还未闭店，就不用抓了，没异常
		{
					
      sqlbuf_with.append("Select * from (");
			
			sqlbuf_with.append(" select shopid,shopname,edate,edate_sdate,edate_stime,bdate，bdate_sdate，"
				+ "(case when bdate_stime>'"+pushtime+"' then N'"+pushtime+"' else bdate_stime end) as bdate_stime from (");//监控当天闭店数据  如果当天已经日结需要对比下推送时间和当天日结时间
			sqlbuf_with.append("select shopid,shopname,edate,edate_sdate,edate_stime,"
				+ "NVL(bdate,'"+bdate+"') as bdate ,NVL(bdate_sdate,'"+edate+"') as bdate_sdate,NVL(bdate_stime,'"+pushtime+"') as bdate_stime from (");//没有第2天的营业日期 ，就比较推送时间
			sqlbuf_with.append(" with P1 AS ("+withasSql+")");	
			sqlbuf_with.append(" ,P2 AS (select distinct SHOPID,edate,sdate as edate_sdate,stime as edate_stime from DCP_DATEEND where edate='"+edate+"')");
			sqlbuf_with.append(" ,P3 AS (select SHOPID,bdate,sdate as bdate_sdate,min(stime) as bdate_stime from DCP_SQUAD where bdate='"+bdate+"' group by SHOPID,bdate,sdate)");
			sqlbuf_with.append(" select p1.shopid,p1.shopname,p2.edate,p2.edate_sdate,p2.edate_stime,p3.bdate,p3.bdate_sdate,p3.bdate_stime from p1 ");
			sqlbuf_with.append(" inner join p2 on p1.shopid=p2.SHOPID left join p3 on p1.shopid=p3.SHOPID");		
			sqlbuf_with.append(")");	
			sqlbuf_with.append(")");
			
			sqlbuf_with.append(")");
			
			
		}
		else
		{
			sqlbuf_with.append("select * from (");
			sqlbuf_with.append("select shopid,shopname,edate,edate_sdate,edate_stime,"
				+ "NVL(bdate,'"+bdate+"') as bdate ,NVL(bdate_sdate,'"+bdate+"') as bdate_sdate,NVL(bdate_stime,'"+pushtime+"') as bdate_stime from (");//没有第2天的营业日期 ，就比较推送时间
			sqlbuf_with.append(" with P1 AS ("+withasSql+")");	
			sqlbuf_with.append(" ,P2 AS (select distinct SHOPID,edate,sdate as edate_sdate,stime as edate_stime from DCP_DATEEND where edate='"+edate+"')");
			sqlbuf_with.append(" ,P3 AS (select SHOPID,bdate,sdate as bdate_sdate,min(stime) as bdate_stime from DCP_SQUAD where bdate='"+bdate+"' group by SHOPID,bdate,sdate)");
			sqlbuf_with.append(" select p1.shopid,p1.shopname,p2.edate,p2.edate_sdate,p2.edate_stime,p3.bdate,p3.bdate_sdate,p3.bdate_stime from p1 ");
			sqlbuf_with.append(" inner join p2 on p1.shopid=p2.SHOPID left join p3 on p1.shopid=p3.SHOPID");		
			sqlbuf_with.append(")");	
			sqlbuf_with.append(")");
		}

		String withSql = sqlbuf_with.toString();
		
	 //region 查询适用门店关联的闭店日期							
		List<Map<String, Object>> getShopList = this.doQueryData(withSql, null);
		if(getShopList==null||getShopList.isEmpty())
		{
			//HelpTools.writelog_fileName("【WarningLogCreate适用门店列表为空】异常！监控号="+warningNo+" 适用门店列表为空!",warningLogFileName);
			return "select * from dual where 1<>1 ";		
		}
		//这里使用康总的 with as 
		String withasSql_ShopList="";		
		String sJoinOrderno="";
		String sJoinShop="";
		String sJoinEDate="";
		String sJoinEDate_sdate="";
		String sJoinEDate_stime="";
		String sJoinBDate="";
		String sJoinBDate_sdate="";
		String sJoinBDate_stime="";
		
		for (Map<String, Object> mapShop : getShopList) 
		{
			sJoinOrderno+=mapShop.get("SHOPID").toString()+",";
			sJoinShop+=mapShop.get("SHOPNAME").toString()+",";
			sJoinEDate += mapShop.get("EDATE").toString()+",";
			sJoinEDate_sdate += mapShop.get("EDATE_SDATE").toString()+",";
			sJoinEDate_stime += mapShop.get("EDATE_STIME").toString()+",";
			sJoinBDate += mapShop.get("BDATE").toString()+",";
			sJoinBDate_sdate += mapShop.get("BDATE_SDATE").toString()+",";
			sJoinBDate_stime += mapShop.get("BDATE_STIME").toString()+",";
		}						
		
		Map<String, String> mapshopstr=new HashMap<String, String>();
		mapshopstr.put("SHOPID", sJoinOrderno);
		mapshopstr.put("SHOPNAME", sJoinShop);		
		mapshopstr.put("EDATE", sJoinEDate);	
		mapshopstr.put("EDATE_SDATE", sJoinEDate_sdate);	
		mapshopstr.put("EDATE_STIME", sJoinEDate_stime);	
		mapshopstr.put("BDATE", sJoinBDate);	
		mapshopstr.put("BDATE_SDATE", sJoinBDate_sdate);	
		mapshopstr.put("BDATE_STIME", sJoinBDate_stime);	
			
				
		MyCommon cm=new MyCommon();
		withasSql_ShopList=cm.getFormatSourceMultiColWith(mapshopstr);					
		mapshopstr=null;
		cm=null;	
		//endregion
		
		
		
		
		StringBuffer sqlbuf = new StringBuffer("");
	
		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ("+withasSql_ShopList+")");
		sqlbuf.append(" select 'saleOrder' as type, a.bdate, a.SHOPID,a.saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,N'' as SHIPTYPE,N'' AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,b.pay as PAYAMT,b.pay as CARDPAYAMT,b.paycode AS PAYCODE,b.paycodeerp AS PAYCODEERP,b.payname AS PAYNAME,b.cttype AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,b.cardno AS CARDPAYNO ");
		sqlbuf.append(" from DCP_SALE a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" inner join DCP_SALE_pay b on a.eId=b.eId and a.SHOPID=b.SHOPID and a.saleno=b.saleno");
		sqlbuf.append(" inner join DCP_PAYFUNCnoinfo c on b.eId=c.eId and b.paycode=c.paycode and c.funcno in ('301','303')");//301 303是储值卡支付方式	
		sqlbuf.append(" where  b.cardno is not null and b.eId='"+eid+"' and a.eId='"+eid+"' and a.type=0 and a.status='100' "
				+ "and a.sdate>=p1.edate_sdate and a.stime>p1.edate_stime "
				+ "and a.sdate<=p1.bdate_sdate and a.stime<p1.bdate_stime");
		
		sqlbuf.append(" union all ");
		
		sqlbuf.append(" select 'order' as type, a.bdate, a.SHOPID,a.orderno as saleno,a.CARDNO,a.CONTMAN,a.CONTTEL,a.MEMBERID,a.SDATE,a.STIME,a.TOT_AMT,p1.shopname,");
		sqlbuf.append(" N'' AS membername,a.GETMODE as SHIPTYPE,a.GETSHOP AS SHIPPINGSHOP, N'' AS SHIPPINGSHOPNAME,N'N' AS ISSHIPCOMPANY ,a.TOT_AMT AS TOT_OLDAMT,b.pay as PAYAMT,b.pay as CARDPAYAMT,b.paycode AS PAYCODE,b.paycodeerp AS PAYCODEERP,b.payname AS PAYNAME,b.cttype AS CARDTYPEID,N'' AS CARDTYPENAME,a.POINT_QTY AS POINTQTY,N'' as PAYSTATUS,N'' as STATUS,N'' as REFUNDSTATUS,b.cardno AS CARDPAYNO ");
		sqlbuf.append(" from oc_order a");
		sqlbuf.append(" inner join  p1  on p1.shopid= a.SHOPID");
		sqlbuf.append(" inner join oc_order_pay b on a.eId=b.eId and a.SHOPID=b.SHOPID and a.orderno=b.orderno");
		sqlbuf.append(" inner join DCP_PAYFUNCnoinfo c on b.eId=c.eId and b.paycode=c.paycode and c.funcno in ('301','303')");//301 303是储值卡支付方式		
		sqlbuf.append(" where  b.cardno is not null and b.eId='"+eid+"' and a.eId='"+eid+"' and a.type=3 and a.status='100'"
				+ "and a.sdate>=p1.edate_sdate and a.stime>p1.edate_stime "
				+ "and a.sdate<=p1.bdate_sdate and a.stime<p1.bdate_stime");
		
		
		sqlbuf.append(") order by cardno,SHOPID,saleno,stime");	
		return sqlbuf.toString();
	}
	
	
	
	
	private boolean IsRunWarning(String eid,String warningno,String rundate) throws Exception
	{
		boolean flag = true ;//默认存在，防止一直运行
		try 
		{
			String sql="";
			sql=" select * from dcp_warninglog where eid='"+eid+"' and billno='"+warningno+"' and substr(pushtime ,0,10)='"+rundate+"'";
			List<Map<String, Object>> getData = this.doQueryData(sql, null);
			if(getData!=null&&getData.isEmpty()==false)
			{
				flag = true;
			}
			else
			{
				flag = false;
			}
	
		} 
		catch (Exception e) 
		{
		// TODO: handle exception
			HelpTools.writelog_fileName("【WarningLogCreate判断是否已经执行过】异常！监控号="+warningno+" 异常："+e.getMessage(),warningLogFileName);
	
		}
		return flag;
		
	}
	
	
	private String getWarningLogNo(String eid,String warningtype,String rundate_no) throws Exception
	{
		String sql = "";
		String warningLogNo = "";
		
		StringBuffer sqlbuf = new StringBuffer("");
		
		String warningLogType = warningtype.toLowerCase();//日志单号前缀 order
		int index_num = warningLogType.length();//order 20190101，排除前缀的索引
		warningLogNo = warningLogType + rundate_no;//order+20190101
	
		sqlbuf.append( "select id  from ( " + "select max(id) as id "
				+ "  from DCP_WARNINGLOG " + " where eid ='" + eid
				+ "' and id like '%%" + warningLogNo + "%%' "); 
		sqlbuf.append(" ) TBL ");
		sql = sqlbuf.toString();
		List<Map<String, Object>> getQData = this.doQueryData(sql, null);

		if (getQData != null && getQData.isEmpty() == false) 
		{
			warningLogNo = (String) getQData.get(0).get("ID");//order2019122100001
			if (warningLogNo != null && warningLogNo.length() > 0) 
			{
				long i;
				warningLogNo = warningLogNo.substring(index_num, warningLogNo.length());//2019122100001这里动态的，因为单号开头字符串长度不一样
				i = Long.parseLong(warningLogNo) + 1;//2019122100002
				warningLogNo = i + "";//2019122100002
				warningLogNo = warningLogType + warningLogNo;//加上前缀 order2019122100002    
			} 
			else 
			{
				warningLogNo = warningLogType + rundate_no + "00001";
			}
		} 
		else {
			warningLogNo = warningLogType + rundate_no + "00001";
		}

		return warningLogNo;
	}
	
	
	private String getMsgMiddle_Order_Value(String shopId,String shopname,String baseamount,int count)
	{
		String msg = "shopidshopname:当日单笔金额超过amt元的零售单有count笔"+"\n";
		
		msg = msg.replace("shopid", shopId);
		msg = msg.replace("shopname", shopname);
		msg = msg.replace("amt", baseamount);
		msg = msg.replace("count", count+"");
		return msg;
	}
	
	private String getMsgMiddle_Order_inTime(String shopId,String shopname,String basetime,int count)
	{
		String msg = "shopidshopname:当日hh:mm点后的零售单有count笔 "+"\n";
		
		msg = msg.replace("shopid", shopId);
		msg = msg.replace("shopname", shopname);
		msg = msg.replace("hh:mm", basetime);
		msg = msg.replace("count", count+"");
		return msg;
	}
	
	
	private String getMsgMiddle_Order_Period(String shopId,String shopname,String begintime,String endtime,int count)
	{
		String msg = "shopidshopname:当日hh:mmB点至hh:mmE点的零售单有count笔"+"\n";
		
		msg = msg.replace("shopid", shopId);
		msg = msg.replace("shopname", shopname);
		msg = msg.replace("hh:mmB", begintime);
		msg = msg.replace("hh:mmE", endtime);
		msg = msg.replace("count", count+"");
		return msg;
	}
	
	
	
	
	/**
	 * 
	 * @param eid 企业编码
	 * @param langtype 多语言
	 * @param warningno 监控号
	 * @param warninglogno 监控日志单号
	 * @throws Exception
	 */
	private void setPushManWay(String eid,String langtype,String warningno,String warninglogno) throws Exception	
	{
		try 
		{
			HelpTools.writelog_fileName("【WarningLogCreate写推送人日志】开始，监控号="+warningno+" 监控日志单号="+warninglogno,warningLogFileName);
			StringBuffer sqlbuf = new StringBuffer("");
			sqlbuf.append("select * from (");		
			sqlbuf.append(" select a.*,b.opno as push_opno,b.pushway,c.userid,c.username,s.op_name as opname  from dcp_warning_pushman a");
			sqlbuf.append(" inner join dcp_warning_pushway b on a.eid=b.eid and a.billno = b.billno and a.opno=b.opno ");
			sqlbuf.append(" left join DCP_DING_USERSET c on a.eid=c.eId and a.opno=c.opno");
			sqlbuf.append(" left join platform_staffs_lang s on a.eid = s.eId and a.opno=s.opno and s.lang_type='"+langtype+"'");
			sqlbuf.append(" where  a.eid='"+eid+"' and a.billno='"+warningno+"'");
			sqlbuf.append(")");
			String sql = sqlbuf.toString();
			
			List<Map<String, Object>> getQData = this.doQueryData(sql, null);
			if(getQData==null||getQData.isEmpty())
			{
				HelpTools.writelog_fileName("【WarningLogCreate写推送人日志】没有设置推送人，监控号="+warningno+" 监控日志单号="+warninglogno,warningLogFileName);
				return;
			}
			ArrayList<DataProcessBean> DPB = new ArrayList<DataProcessBean>();
		  //写推送人 日志表
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("OPNO", true);	
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData, condition);
				
			String[] columns_warning_pushMan = { "EID", "ID", "SERIALNO","BILLNO", "OPNO", "OPNAME"};
			int serialNo_pushMan = 1;
			for (Map<String, Object> mapPushMan : getQHeader) 
			{
				String opNo_pushMan = mapPushMan.get("OPNO").toString();
				if(opNo_pushMan==null||opNo_pushMan.isEmpty())
				{
					continue;
				}
				DataValue[] insValue_warning_pushMan = new DataValue[] 
						{
							new DataValue(eid, Types.VARCHAR),
							new DataValue(warninglogno, Types.VARCHAR),
							new DataValue(serialNo_pushMan, Types.VARCHAR),
							new DataValue(warningno, Types.VARCHAR),
							new DataValue(mapPushMan.get("OPNO").toString(), Types.VARCHAR),					
							new DataValue(mapPushMan.get("OPNAME").toString(), Types.VARCHAR)
								
						};
				
				InsBean ib_warning_pushMan = new InsBean("DCP_WARNINGLOG_PUSHMAN", columns_warning_pushMan);
				ib_warning_pushMan.addValues(insValue_warning_pushMan);
				DPB.add(new DataProcessBean(ib_warning_pushMan));
				serialNo_pushMan++;		
			}
			
			//推送方式保存
			String[] columns_warning_pushWay = { "EID","ID","SERIALNO", "BILLNO", "OPNO", "PUSHWAY", "PUSHWAYNAME",
					"MOBILEPHONE", "EMAIL","USERID","USERNAME","PUSHFLAG","FAILMSG"};
			int serialNo_pushWay = 1;
			for (Map<String, Object> mapPushWay : getQData) 
			{
				String opNo_pushWay = mapPushWay.get("PUSH_OPNO").toString();
				String pushWay_pushWay = mapPushWay.get("PUSHWAY").toString();
				String pushWayName = "";		
				String pushFlag = "-1";//未推送
				String failMsg ="未推送";					
				if(opNo_pushWay==null||opNo_pushWay.isEmpty()||pushWay_pushWay==null||pushWay_pushWay.isEmpty())
				{
					continue;
				}
				//region 推送方式名称赋值
				if(pushWay_pushWay.toUpperCase().equals("EMAIL"))
				{
					pushWayName = "邮件";
					
				}
			  else if(pushWay_pushWay.toUpperCase().equals("PHONE"))
				{
			  	pushWayName = "手机短信";
				}
			  else if(pushWay_pushWay.toUpperCase().equals("DING"))
				{
			  	pushWayName = "钉钉";
				}
				else 
				{
					pushWayName = "其他";				
				}
				//endregion
				DataValue[] insValue_warning_pushWay = new DataValue[] 
						{
							new DataValue(eid, Types.VARCHAR),
							new DataValue(warninglogno, Types.VARCHAR),
							new DataValue(serialNo_pushWay, Types.VARCHAR),
							new DataValue(warningno, Types.VARCHAR),
							new DataValue(opNo_pushWay, Types.VARCHAR),					
							new DataValue(pushWay_pushWay, Types.VARCHAR),
							new DataValue(pushWayName, Types.VARCHAR),
							new DataValue(mapPushWay.get("MOBILEPHONE").toString(), Types.VARCHAR),
							new DataValue(mapPushWay.get("EMAIL").toString(), Types.VARCHAR),
							new DataValue(mapPushWay.get("USERID").toString(), Types.VARCHAR),
							new DataValue(mapPushWay.get("USERNAME").toString(), Types.VARCHAR),
							new DataValue(pushFlag, Types.VARCHAR),
							new DataValue(failMsg, Types.VARCHAR)						
						};
				
				InsBean ib_warning_pushWay = new InsBean("DCP_WARNINGLOG_PUSHWAY", columns_warning_pushWay);
				ib_warning_pushWay.addValues(insValue_warning_pushWay);
				DPB.add(new DataProcessBean(ib_warning_pushWay)); 
				serialNo_pushWay++;			
			}
			
			this.doExecuteDataToDB(DPB);
			HelpTools.writelog_fileName("【WarningLogCreate写推送人日志】保存数据成功，监控号="+warningno+" 监控日志单号="+warninglogno,warningLogFileName);
	
		}
		catch (Exception e) 
		{
			HelpTools.writelog_fileName("【WarningLogCreate写推送人日志】保存数据异常，监控号="+warningno+" 监控日志单号="+warninglogno+" 异常:"+e.getMessage(),warningLogFileName);
	
		}
		
		
	}
	
	
	private String getDCPUrl(String eId,List<Map<String, Object>> urlList) throws Exception
	{
		String url = null;
		try 
		{
			if(urlList!=null&&urlList.isEmpty()==false)
			{
				for (Map<String, Object> map : urlList) 
				{
					String map_eId = map.get("EID").toString();
					String def = map.get("DEF").toString();
					if(def!=null && def.trim().length()>0 && map_eId!=null && map_eId.equals(eId))
					{
						url = def;
						break;
					}				
				}
			}
		
	
		} 
		catch (Exception e) 
		{
		
	
		}
					
		return url;
	}
	
}
