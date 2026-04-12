package com.dsc.spos.service.imp.json;

import com.dsc.spos.dao.*;
import com.dsc.spos.json.cust.req.DCP_OrderProductionAgreeOrReject_OpenReq;
import com.dsc.spos.json.cust.req.DCP_OrderProductionAgreeOrReject_OpenReq.OrderList;
import com.dsc.spos.json.cust.res.DCP_OrderProductionAgreeOrReject_OpenRes;
import com.dsc.spos.json.cust.res.DCP_OrderProductionAgreeOrReject_OpenRes.levRes;
import com.dsc.spos.service.SPosAdvanceService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.dsc.spos.utils.PosPub;
import com.dsc.spos.waimai.HelpTools;
import com.dsc.spos.waimai.entity.orderLoadDocType;
import com.dsc.spos.waimai.entity.orderStatusLog;
import com.google.gson.reflect.TypeToken;
import org.apache.cxf.common.util.CollectionUtils;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单生产接单同意/拒绝DCP_OrderProductionAgreeOrReject
 * 
 * @author 规格地址  http://183.233.190.204:10004/project/148/interface/api/3204
 *
 */
public class DCP_OrderProductionAgreeOrReject_Open extends SPosAdvanceService<DCP_OrderProductionAgreeOrReject_OpenReq, DCP_OrderProductionAgreeOrReject_OpenRes> {

	@Override
	protected void processDUID(DCP_OrderProductionAgreeOrReject_OpenReq req,
			DCP_OrderProductionAgreeOrReject_OpenRes res) throws Exception {
		// TODO Auto-generated method stub
		levRes resDatas = res.new levRes();
		resDatas.setErrorOrderList(new ArrayList<DCP_OrderProductionAgreeOrReject_OpenRes.ErrorOrderList>());
		res.setDatas(resDatas);
		try
		{
			String eId = req.getRequest().geteId();
			String shopId = req.getRequest().getShopId();
			String opNo = req.getRequest().getOpNo();
			String opName = req.getRequest().getOpName();
			String opType = req.getRequest().getOpType();
			List<OrderList> orderList = req.getRequest().getOrderList();
			List<String> sucessOrderList = new ArrayList<String>();//执行成功的 单号列表
			List<Map<String, Object>> failOrderList = new ArrayList<Map<String, Object>>();//失败的列表,以及错误描述
			String companyId = req.getBELFIRM();
			if(req.getApiUser()!=null){
				companyId = req.getApiUser().getCompanyId();
			}

			// 查询门店是否启用KDS 
			String sql = this.getKDSEnable(req);
			List<Map<String, Object>> getKDSEnable = this.doQueryData(sql, null);
			String KDSEnable = "";
			if(getKDSEnable!=null&&!getKDSEnable.isEmpty())
			{
				KDSEnable = getKDSEnable.get(0).getOrDefault("KDS", "N").toString();
			}
			 

			// 查询门店下的KDS 配置中 TagType 是否有等于0 的
			String tagBool = ""; // 如果有 为0:0 默认:空  false
			List<Map<String, Object>> plunos = new ArrayList<Map<String,Object>>();
			if ("Y".equals(KDSEnable))
			{
				
				sql = this.getTagType(req);
				List<Map<String, Object>> getTagType = this.doQueryData(sql, null);
				for (Map<String, Object> tagType : getTagType) {
					String tagtype = tagType.get("TAGTYPE").toString();
					if(tagtype.equals("0")){
						tagBool = "0";
						break;
					}else if(tagtype.equals("1")){
						tagBool = "1";
					}
				}
								
				// 关联查询相关商品
				sql = this.getPluno(req,tagBool);
			    plunos = this.doQueryData(sql, null);
			}
						
			String[] columns_Processtask = {
					"SHOPID", "ORGANIZATIONNO","BDATE","PROCESSTASK_ID","CREATEBY", "CREATE_DATE", "CREATE_TIME",
					"TOT_PQTY","TOT_AMT", "TOT_CQTY", "EID","PROCESSTASKNO", "MEMO", "STATUS", "PROCESS_STATUS",
					"PTEMPLATENO","PDATE","WAREHOUSE","MATERIALWAREHOUSE","TOT_DISTRIAMT","OFNO","OTYPE",
					"CONFIRM_DATE","CONFIRM_TIME","CONFIRMBY","OSTATUS","PRODUCTSTATUS","CREATEDATETIME","CONTMAN","CONTTEL","GETMAN",
					"GETMANTEL","ADDRESS","SHIPDATE","SHIPSTARTTIME","SHIPENDTIME","REFUNDREASONNAME","LOADDOCTYPE","CREATE_CHATUSERID"
			};
			String[] columns_Processtask_Detail = {
					"PROCESSTASKNO", "SHOPID", "item", "pluNO",
					"punit", "pqty", "baseunit", "baseqty","unit_Ratio",
					"price", "amt", "EID", "organizationNO", "mul_Qty",
					"DISTRIPRICE","DISTRIAMT","BDATE","FEATURENO","OFNO","OITEM","GOODSSTATUS","OPNO","OQTY","PLUNAME"
			};
			String processTaskNO = this.getProcessTaskNO(req);
			int createbillNo = 0;
			MyCommon MC = new MyCommon();

			boolean isAllSucess = true;
			String logStrStart = "";
			//循环单号，有一个失败break,后面不执行了
			for (OrderList orderNoList : orderList)
			{
                logStrStart = "";
				
				String orderNo = orderNoList.getOrderNo();
				logStrStart="循环操作【生成接单】单号orderNo="+orderNo+",";
				try
				{
					String orderSql = " select a.*  from DCP_Order a "
	                      	        + " where a.eid = '"+eId+"'  "
							        + " and a.OrderNo='"+orderNo+"' ";
					
					List<Map<String, Object>> orderDatas = this.doQueryData(orderSql, null);
					if(orderDatas==null||orderDatas.isEmpty())
					{
						HelpTools.writelog_waimai(logStrStart+"没有查询到该订单数据！");
						isAllSucess = false;
						Map<String, Object> errorMap = new HashMap<String, Object>();
						errorMap.put("orderNo", orderNo);
						errorMap.put("errorDesc", "没有查询到该订单数据！");
						failOrderList.add(errorMap);
						break;
					}
					
					Map<String, Object> map = orderDatas.get(0);
					
					String status = map.get("STATUS").toString();// 订单状态
					String productStatus = map.get("PRODUCTSTATUS").toString();//生产状态
					String loadDocType = map.get("LOADDOCTYPE").toString();
					String channelId = map.get("CHANNELID").toString();
					String machShop = map.get("MACHSHOP").toString();
                    String canModify = map.getOrDefault("CANMODIFY","").toString();
                    if (orderLoadDocType.POS.equals(loadDocType)||orderLoadDocType.POSANDROID.equals(loadDocType))
                    {
                        if ("Y".equals(canModify))
                        {
                            HelpTools.writelog_waimai(logStrStart+"是否允许补录canModify为Y");
                            isAllSucess = false;
                            Map<String, Object> errorMap = new HashMap<String, Object>();
                            errorMap.put("orderNo", orderNo);
                            errorMap.put("errorDesc", "订单信息未完整，请先到pos完成信息补录！");
                            failOrderList.add(errorMap);
                            break;
                        }
                    }
					
					
					
					HelpTools.writelog_waimai(logStrStart+"数据库中 订单状态status="+status+",生产状态productStatus="+productStatus+",生产机构machShop="+machShop+",当前传入机构shopId="+shopId);
					boolean canProductFinishFlag = true;
					StringBuffer errMsg = new StringBuffer("");
					
					UptBean up1 = new UptBean("DCP_ORDER");
					up1.addCondition("EID", new DataValue(eId,Types.VARCHAR));
					up1.addCondition("ORDERNO", new DataValue(orderNo,Types.VARCHAR));
					//当前机构为订单生产机构
					if(shopId.equals(machShop))
					{
						
					}
					else
					{
						canProductFinishFlag = false;
						errMsg.append("当前机构非订单生产机构，不能进行生产接单！ ");
						HelpTools.writelog_waimai(logStrStart+errMsg.toString());
						isAllSucess = false;
						Map<String, Object> errorMap = new HashMap<String, Object>();
						errorMap.put("orderNo", orderNo);
						errorMap.put("errorDesc", errMsg.toString());
						failOrderList.add(errorMap);
						break;	
					}
					
					
					if("4".equals(opType))
					{
						if( ( status.equals("1") || status.equals("2") ) && (Check.Null(productStatus) || productStatus.equals("5")) )
						{
							//更新updatetime
							up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
							up1.addUpdateValue("TRAN_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
							// 订单状态 status 改为 4( 生产接单 ) ，存疑
							//up1.addUpdateValue("STATUS", new DataValue("4",Types.VARCHAR)); 
							// 生产状态 PRODUCTSTATUS 改为 4 (生产接单)， 这个和订单状态不一样。
							up1.addUpdateValue("PRODUCTSTATUS", new DataValue("4",Types.VARCHAR));
							this.addProcessData(new DataProcessBean(up1));

							// 2021/4/15 应 SA 王欢需求 增加逻辑 BY wangzyc
							// 1: 同意接单后判断对应的生产门店下的KDS 参数是否开启
							// 2.1):开启后判断 KDS 配置中是否存在全部标签为 0 的数据。若有 TAGTYPE =0 则关联【标签明细档】下所有商品与【订单商品明细】
							// 匹配以 qty=1 存入【加工任务档】【加工任务明细档】
							// 2.2): 若没有TAGTYPE 为 0 的数据 则 通过【KDS配置信息指定生产标签】下TAGNO关联【标签明细档】对应商品数据与【订单商品明细】
							// 匹配以qty=1存入【加工任务档】【加工任务明细档】中；

							if(KDSEnable.equals("Y")){
									// plunos 根据订单下的商品 关联标签下商品 来觉得存入加工任务单 哪些商品
									// 可生产范围商品为空的话 不生成加工任务单
								if((!CollectionUtils.isEmpty(plunos))){

									// 启用KDS
									Map<String, Object> mapParam = new HashMap<>();
									mapParam.put("EID",eId);
									mapParam.put("ORDERNO",orderNo);
									mapParam.put("TAGTYPE",tagBool);

									if(!CollectionUtils.isEmpty(plunos)){
										mapParam.put("PLUNOS",plunos);
									}


									sql = this.getOrderDetail(mapParam);
									List<Map<String, Object>> getOrderDetails = this.doQueryData(sql, null);

									// 过滤
									Map<String, Boolean> condition = new HashMap<String, Boolean>(); // 查詢條件
									condition.put("EID", true);
									condition.put("ORDERNO", true);
									// 调用过滤函数
									List<Map<String, Object>> getHeader = MapDistinct.getMap(getOrderDetails, condition);

									// 过滤
									Map<String, Boolean> condition2 = new HashMap<String, Boolean>(); // 查詢條件
									condition2.put("EID", true);
									condition2.put("ORDERNO", true);
									condition2.put("PLUNO", true);
									condition2.put("FEATURENO", true);
									// 调用过滤函数
									List<Map<String, Object>> getPlunos = MapDistinct.getMap(getOrderDetails, condition2);


									// 获取当前时间
									//添加当前时间
									Date now = new Date();
									SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//设置日期格式
									String createDate = dateFormat.format(now);//格式化然后放入字符串中
									dateFormat = new SimpleDateFormat("HHmmss");
									String createTime = dateFormat.format(now);
									String createBy = req.getRequest().getOpNo();

									// 拿到商品信息 存入到加工任务单
									if(!CollectionUtils.isEmpty(getHeader)){
										// 获取商品种类数 总价格 总数量
										Integer tot_cqty = 0;
										Integer tot_qty = 0;
										Double tot_amt = 0.0;
										if(!CollectionUtils.isEmpty(getPlunos)){
											for (Map<String, Object> getPluno : getPlunos) {
												tot_cqty++;
												tot_qty += Integer.parseInt(getPluno.get("QTY").toString());
												tot_amt += Double.parseDouble(getPluno.get("AMT").toString());
											}
										}

										for (Map<String, Object> header : getHeader) {

											String warehouse = header.get("WAREHOUSE").toString();
											String memo = header.get("MEMO").toString();
											String ostatus = header.get("STATUS").toString();
											String contman = header.get("CONTMAN").toString();
											String conttel = header.get("CONTTEL").toString();
											String getman = header.get("GETMAN").toString();
											String getmantel = header.get("GETMANTEL").toString();
											String address = header.get("ADDRESS").toString();
											String shipdate = header.get("SHIPDATE").toString();
											String shipstarttime = header.get("SHIPSTARTTIME").toString();
											String shipendtime = header.get("SHIPENDTIME").toString();
											String createDateTime = header.get("CREATE_DATETIME").toString();
											String refundReasonName = header.get("REFUNDREASONNAME").toString();
											String loadDocType2 = header.get("LOADDOCTYPE").toString();


											if(!CollectionUtils.isEmpty(getOrderDetails)){
												int item = 0;
												for (Map<String, Object> detail : getOrderDetails) {
													String featureno = detail.get("FEATURENO").toString();
													String oitem = detail.get("ITEM").toString();
													String pluno = detail.get("PLUNO").toString();
													String pluName = detail.get("PLUNAME").toString();
													sql = this.getPlunoMultQty(req,pluno);
													List<Map<String, Object>> getMuilQty = this.doQueryData(sql, null);
													String mulqty = "0";
													if(!CollectionUtils.isEmpty(getMuilQty)){
														mulqty = getMuilQty.get(0).get("MULQTY").toString();
													}

													String sUnit = detail.get("SUNIT").toString();
													String price = detail.get("PRICE").toString();
													String amt = detail.get("AMT").toString();
													String sqty = detail.get("QTY").toString();


													int qty = Integer.parseInt(sqty);

													String eId2 = detail.get("EID").toString();
													String orderNo2 = detail.get("ORDERNO").toString();
													if(eId2.equals(eId2) && orderNo2.equals(orderNo)){

														Double damt = 0.0; // 单份总额
														if(!Check.Null(amt)){
															damt = Double.parseDouble(amt)/qty;
														}
														// 查询商品 要货单位编码
														sql = "select PLUNO,PUNIT,BASEUNIT from DCP_GOODS where EID = '"+eId+"' and PLUNO = '"+pluno+"'";
														List<Map<String, Object>> getPunit = this.doQueryData(sql, null);
//													String punit = getPunit.get(0).get("PUNIT").toString();
														String baseUnit = getPunit.get(0).get("BASEUNIT").toString();
														double baseQty = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluno, sUnit, baseUnit, 1 + ""));
														double baseQtys = Double.parseDouble(PosPub.getUnitConvert(dao, eId, pluno, sUnit, baseUnit, qty+""));

														boolean isSingleProduce = false;
														// 是否单份 存储还是整单存储
														for (Map<String, Object> spluno : plunos) {
															String id = spluno.get("ID").toString();
															if(id.equals(pluno)){
																String issingleproduce = spluno.get("ISSINGLEPRODUCE").toString();
																if(!Check.Null(issingleproduce)&&issingleproduce.equals("Y")){
																	isSingleProduce = true;
																	break;
																}
															}
														}
														BigDecimal unitRatio = new BigDecimal(1);
														List<Map<String, Object>> getQData_Ratio = PosPub.getUnit_Ratio_Middle(dao, req.geteId(),
																pluno, sUnit);

														if (getQData_Ratio == null || getQData_Ratio.isEmpty()) {
															throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "商品 " + map.get("PLUNO").toString() + "  单位："
																	+ map.get("UNIT").toString() + " 找不到对应的库存单位换算关系");
														}

														unitRatio = (BigDecimal) getQData_Ratio.get(0).get("UNIT_RATIO");


														//获取成品 和 原料的 进货价 和 零售价 商品mapList
														List<Map<String, Object>> pluList = new ArrayList<>();
														Map<String, Object> plumap = new HashMap<>();
														plumap.put("PLUNO",pluno);
														plumap.put("PUNIT",sUnit);
														plumap.put("BASEUNIT",baseUnit);
														plumap.put("UNITRATIO",unitRatio);
														pluList.add(plumap);
														List<Map<String, Object>> getPluPrice = MC.getSalePrice_distriPrice(dao,eId,companyId, shopId,pluList,companyId);

														Map<String, Object> condiV = new HashMap<>();
														condiV.put("PLUNO",pluno); //订单上的商品编码
														condiV.put("PUNIT",sUnit); //订单上的商品单位
														List<Map<String, Object>> priceList= MapDistinct.getWhereMap(getPluPrice, condiV, false);

														String detailDistriPrice = "0";//成品进货价
														if(priceList!=null && priceList.size()>0 )
														{
															detailDistriPrice=priceList.get(0).get("DISTRIPRICE").toString();//成品进货价
														}
														BigDecimal detailDistriAmt = new BigDecimal(0);


														if(isSingleProduce){ // 根据标签下的isSingleProduce 判断写单份单身 还是整单单身 如果两者都存在 单份优先整单
															detailDistriAmt = new BigDecimal("1").multiply(new BigDecimal( detailDistriPrice));
															// 写单身
															for (int i =0;i<qty;i++){
																item++;
																DataValue[] insValueDetail = new DataValue[]
																		{
																				new DataValue(processTaskNO, Types.VARCHAR),
																				new DataValue(shopId, Types.VARCHAR),
																				new DataValue(item, Types.VARCHAR),
																				new DataValue(pluno, Types.VARCHAR),
																				new DataValue(sUnit, Types.VARCHAR),
																				new DataValue(1, Types.VARCHAR), // 数量为 1
																				new DataValue(baseUnit, Types.VARCHAR),
																				new DataValue(baseQty, Types.VARCHAR),
																				new DataValue(unitRatio, Types.VARCHAR), // 单品比率 默认1
																				new DataValue(price, Types.VARCHAR),
																				new DataValue(damt, Types.VARCHAR),
																				new DataValue(eId, Types.VARCHAR),
																				new DataValue(shopId, Types.VARCHAR),
																				new DataValue(mulqty, Types.VARCHAR), // 倍量 默认0
																				new DataValue(detailDistriPrice, Types.VARCHAR), // 进货价 默认0
																				new DataValue(detailDistriAmt, Types.VARCHAR), // 进货金额 默认0
																				new DataValue(createDate, Types.VARCHAR),
																				new DataValue(featureno, Types.VARCHAR),
																				new DataValue(orderNo, Types.VARCHAR),
																				new DataValue(oitem, Types.VARCHAR),
																				new DataValue(0, Types.VARCHAR), // GOODSSTATUS 商品状态 默认 0
																				new DataValue("", Types.VARCHAR), // opNO
																				new DataValue(qty, Types.VARCHAR), // OQTY
																				new DataValue(pluName, Types.VARCHAR) // PLUNAME
																		};

																InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
																ib1.addValues(insValueDetail);
																this.addProcessData(new DataProcessBean(ib1));
															}

														}else{
															detailDistriAmt = new BigDecimal(qty).multiply(new BigDecimal( detailDistriPrice));
															// 写整单单身
															item++;
															DataValue[] insValueDetail = new DataValue[]
																	{
																			new DataValue(processTaskNO, Types.VARCHAR),
																			new DataValue(shopId, Types.VARCHAR),
																			new DataValue(item, Types.VARCHAR),
																			new DataValue(pluno, Types.VARCHAR),
																			new DataValue(sUnit, Types.VARCHAR),
																			new DataValue(qty, Types.VARCHAR), // qty
																			new DataValue(baseUnit, Types.VARCHAR),
																			new DataValue(baseQtys, Types.VARCHAR),
																			new DataValue(unitRatio, Types.VARCHAR), // 单品比率 默认1
																			new DataValue(price, Types.VARCHAR),
																			new DataValue(amt, Types.VARCHAR),
																			new DataValue(eId, Types.VARCHAR),
																			new DataValue(shopId, Types.VARCHAR),
																			new DataValue(mulqty, Types.VARCHAR), // 倍量 默认0
																			new DataValue(detailDistriPrice, Types.VARCHAR), // 进货价
																			new DataValue(detailDistriAmt, Types.VARCHAR), // 进货金额
																			new DataValue(createDate, Types.VARCHAR),
																			new DataValue(featureno, Types.VARCHAR),
																			new DataValue(orderNo, Types.VARCHAR),
																			new DataValue(oitem, Types.VARCHAR),
																			new DataValue(0, Types.VARCHAR), // GOODSSTATUS 商品状态 默认 0
																			new DataValue("", Types.VARCHAR), // opNO
																			new DataValue(qty, Types.VARCHAR), // oQTY
																			new DataValue(pluName, Types.VARCHAR) // PLUNAME
																	};

															InsBean ib1 = new InsBean("DCP_PROCESSTASK_DETAIL", columns_Processtask_Detail);
															ib1.addValues(insValueDetail);
															this.addProcessData(new DataProcessBean(ib1));
														}
													}

												}
											}

											// 写单头
											DataValue[] insValue = new DataValue[]
													{
															new DataValue(shopId, Types.VARCHAR),
															new DataValue(shopId, Types.VARCHAR),
															new DataValue(createDate, Types.VARCHAR),
															new DataValue("", Types.VARCHAR), // PROCESSTASK_ID
															new DataValue(createBy, Types.VARCHAR),
															new DataValue(createDate, Types.VARCHAR),
															new DataValue(createTime, Types.VARCHAR),
															new DataValue(tot_qty, Types.VARCHAR),
															new DataValue(tot_amt, Types.VARCHAR),
															new DataValue(tot_cqty, Types.VARCHAR), // TOT_CQTY
															new DataValue(eId, Types.VARCHAR),
															new DataValue(processTaskNO, Types.VARCHAR),
															new DataValue(memo, Types.VARCHAR),// MEMO
															new DataValue("6", Types.VARCHAR), // STATUS  默认 6
															new DataValue("N", Types.VARCHAR), // PROCESS_STATUS 默认 N
															new DataValue("", Types.VARCHAR), // PTEMPLATENO
															new DataValue("", Types.VARCHAR), // 生产日期
															new DataValue(warehouse, Types.VARCHAR),
															new DataValue(warehouse, Types.VARCHAR), // MATERIALWAREHOUSE 原料仓库 取默认出货仓库
															new DataValue(0, Types.VARCHAR), // 进货合计
															new DataValue(orderNo, Types.VARCHAR), // ofNo
															new DataValue("ORDER", Types.VARCHAR) ,// oType
															new DataValue(createDate, Types.VARCHAR),
															new DataValue(createTime, Types.VARCHAR),
															new DataValue(createBy, Types.VARCHAR),
															new DataValue(ostatus, Types.VARCHAR), // OSTATUS
															new DataValue("4", Types.VARCHAR), // PRODUCTSTATUS
															new DataValue(createDateTime, Types.VARCHAR), // CREATEDATETIME
															new DataValue(contman, Types.VARCHAR), // CONTMAN
															new DataValue(conttel, Types.VARCHAR), // CONTTEL
															new DataValue(getman, Types.VARCHAR), // GETMAN
															new DataValue(getmantel, Types.VARCHAR), // GETMANTEL
															new DataValue(address, Types.VARCHAR), // ADDRESS
															new DataValue(shipdate, Types.VARCHAR), // SHIPDATE
															new DataValue(shipstarttime, Types.VARCHAR), // SHIPSTARTTIME
															new DataValue(shipendtime, Types.VARCHAR), // SHIPENDTIME
															new DataValue(refundReasonName, Types.VARCHAR), // REFUNDREASONNAME
															new DataValue(loadDocType2, Types.VARCHAR), // loadDocType
															new DataValue(req.getChatUserId(), Types.VARCHAR)
													};

											InsBean ib2 = new InsBean("DCP_PROCESSTASK", columns_Processtask);
											ib2.addValues(insValue);
											this.addProcessData(new DataProcessBean(ib2));
											createbillNo++;
										}

									}
								}

							}
						}
						else
						{
							canProductFinishFlag = false;
							errMsg.append("订单开立 或 已接单 ， 且生产状态 productStatus = 5 或 空 ，才能生产接单！");
						}
						
					}
					else if("5".equals(opType))
					{
						if( ( status.equals("1") || status.equals("2") ) && (Check.Null(productStatus) || productStatus.equals("4")) )
						{
							//更新updatetime
							up1.addUpdateValue("UPDATE_TIME", new DataValue(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()), Types.VARCHAR));
							// 订单状态 status改为 5 (生产拒单) ，存疑
							//up1.addUpdateValue("STATUS", new DataValue("5",Types.VARCHAR));
							// 生产状态PRODUCTSTATUS 改为 5 (生产拒单) ，和订单状态不一样
							up1.addUpdateValue("PRODUCTSTATUS", new DataValue("5",Types.VARCHAR));
							this.addProcessData(new DataProcessBean(up1));
						}
						else
						{
							canProductFinishFlag = false;
							errMsg.append("订单开立 或 已接单 ， 且生产状态 productStatus = 4 或 空 ，才能生产拒单！");
						}
						
					}
					else 
					{
						canProductFinishFlag = false;
						errMsg.append("操作类型opType="+opType+"，未知 ");
					}
									
					
					if(!canProductFinishFlag)
					{
						HelpTools.writelog_waimai(logStrStart+errMsg.toString());
						isAllSucess = false;
						Map<String, Object> errorMap = new HashMap<String, Object>();
						errorMap.put("orderNo", orderNo);
						errorMap.put("errorDesc", errMsg.toString());
						failOrderList.add(errorMap);
						break;						
					}
					
					
					this.doExecuteDataToDB();
					
					//写日志
					List<orderStatusLog> orderStatusLogList = new ArrayList<orderStatusLog>();
					orderStatusLog onelv1 = new orderStatusLog();
					onelv1.setLoadDocType(loadDocType);
					onelv1.setChannelId(channelId);
					onelv1.setLoadDocBillType(map.get("LOADDOCBILLTYPE").toString());
					onelv1.setLoadDocOrderNo(map.get("LOADDOCORDERNO").toString());
					onelv1.seteId(eId);

					onelv1.setOpName(opName);
					onelv1.setOpNo(opNo);				
					onelv1.setShopNo(shopId);
					onelv1.setOrderNo(orderNo);
					onelv1.setMachShopNo(map.get("LOADDOCORDERNO").toString());
					onelv1.setShippingShopNo(map.get("SHIPPINGSHOP").toString());
					String statusType = "4";//订单状态
					String updateStaus = opType; //4-生产接单 ，5-生产拒单				
					onelv1.setStatusType(statusType);
					onelv1.setStatus(updateStaus);
					StringBuilder statusTypeNameObj = new StringBuilder();
					String statusName = HelpTools.GetOrderStatusName(statusType, updateStaus, statusTypeNameObj);
					String statusTypeName = statusTypeNameObj.toString();
					onelv1.setStatusTypeName(statusTypeName);
					onelv1.setStatusName(statusName);

					String memo = "";
					memo += statusName;
					onelv1.setMemo(memo);
					onelv1.setDisplay("1");

					String updateDatetime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
					onelv1.setUpdate_time(updateDatetime);
					
					orderStatusLogList.add(onelv1);
					
					StringBuilder errorStatusLogMessage = new StringBuilder();
					boolean nRet = HelpTools.InsertOrderStatusLog(this.dao, orderStatusLogList, errorStatusLogMessage);
					if (nRet) {
						HelpTools.writelog_waimai("【写表DCP_orderStatuslog保存成功】" + " 订单号orderNO:" + orderNo);
					} else {
						HelpTools.writelog_waimai(
								"【写表DCP_orderStatuslog异常】" + errorStatusLogMessage + " 订单号orderNO:" + orderNo);
					}
					
					
					
				} 
				catch (Exception e)
				{
					// TODO: handle exception
					//只要有一个失败，就不在执行下面的
					// TODO: handle exception
					isAllSucess = false;
					Map<String, Object> errorMap = new HashMap<String, Object>();
					errorMap.put("orderNo", orderNo);
					errorMap.put("errorDesc", e.getMessage());
					failOrderList.add(errorMap);
					break;
				}
			}
			
			if(isAllSucess)
			{
				res.setSuccess(true);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行成功");
				if(req.getApiUser()!=null){
					res.setVersion("3.0");
				}

			}
			else
			{
				StringBuffer errorDescBuffer = new StringBuffer();
				//异常得单号先提示
				for (Map<String, Object> map : failOrderList)
				{
					String orderNo = map.getOrDefault("orderNo", "").toString();
					String errorDesc = map.getOrDefault("errorDesc", "").toString();
					errorDescBuffer.append("单号:"+orderNo+",异常:"+errorDesc);
				}
				
				//已经成功得单号
				if(sucessOrderList!=null&&sucessOrderList.isEmpty()==false)
				{
					errorDescBuffer.append("<br>单号:");
					for (String sucessOrderNo : sucessOrderList)
					{
						errorDescBuffer.append(sucessOrderNo+",");						
					}
					if("4".equals(opType))
					{
						errorDescBuffer.append("生产接单成功！");
					}
					else
					{
						errorDescBuffer.append("生产拒单成功！");
					}
					
				}
				
				
				//找下没有执行的
				List<String> noProcessOrderList = new ArrayList<String>();
				for (OrderList orderNoList : orderList)
				{
					String orderNo = orderNoList.getOrderNo();
					boolean isFind = false;
					for (Map<String, Object> map : failOrderList)
					{
						String orderNo1 = map.getOrDefault("orderNo", "").toString();
						if(orderNo.equals(orderNo1))
						{
							isFind = true;
							break;
						}
					}
					
					if(isFind)
					{
						continue;
					}
					
					for (String sucessOrderNo : sucessOrderList)
					{
						
						if(orderNo.equals(sucessOrderNo))
						{
							isFind = true;
							break;
						}
					}
					
					if(isFind)
					{
						continue;
					}
					noProcessOrderList.add(orderNo);
					
				}
				
				
				//已经成功得单号
				if(noProcessOrderList!=null&&noProcessOrderList.isEmpty()==false)
				{
					errorDescBuffer.append("<br>单号:");
					for (String noProcessOrderNo : noProcessOrderList)
					{
						errorDescBuffer.append(noProcessOrderNo+",");						
					}
					errorDescBuffer.append("请重新操作！");
				}


				
				res.setSuccess(false);
				res.setServiceStatus("200");
				res.setServiceDescription("服务执行失败！<br>"+errorDescBuffer.toString());
				
			}
			
		} 
		catch (Exception e)
		{
			// TODO: handle exception
			res.setSuccess(false);
			res.setServiceStatus("200");
			res.setServiceDescription("服务执行异常:"+e.getMessage());
		}
			
		
	}

	
	@Override
	protected List<InsBean> prepareInsertData(DCP_OrderProductionAgreeOrReject_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<UptBean> prepareUpdateData(DCP_OrderProductionAgreeOrReject_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<DelBean> prepareDeleteData(DCP_OrderProductionAgreeOrReject_OpenReq req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isVerifyFail(DCP_OrderProductionAgreeOrReject_OpenReq req) throws Exception {
		boolean isFail = false; 
		StringBuffer errMsg = new StringBuffer("");
		int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；

		if(req.getRequest()==null)
		{
			isFail = true;
			errMsg.append("request不能为空 ");
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}
		List<OrderList> orderList = req.getRequest().getOrderList();
		if(orderList==null||orderList.isEmpty())
		{
			isFail = true;
			errMsg.append("orderList不能为空 ");
		}
		if(Check.Null(req.getRequest().getShopId()))
		{
			isFail = true;
			errMsg.append("shopId不能为空 ");
		}
		
		if (isFail)
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
		}

		return isFail;
	}

	@Override
	protected TypeToken<DCP_OrderProductionAgreeOrReject_OpenReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderProductionAgreeOrReject_OpenReq>(){};
	}

	@Override
	protected DCP_OrderProductionAgreeOrReject_OpenRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderProductionAgreeOrReject_OpenRes();
	}

	/**
	 * 判断对应门店下 KDS 参数是否开启
	 * @param req
	 * @return
	 */
	private String getKDSEnable(DCP_OrderProductionAgreeOrReject_OpenReq req){
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(" SELECT eid,ORGANIZATIONNO,KDS FROM DCP_ORG_ORDERSET  " +
				"WHERE eid = '"+req.getRequest().geteId()+"' AND ORGANIZATIONNO = '"+req.getRequest().getShopId()+"' ");
		 sql = sqlbuf.toString();
		return sql;
	}

	/**
	 * 查看KDS 配置是否有 TagType 为0 的参数
	 * @param req
	 * @return
	 */
	private String getTagType(DCP_OrderProductionAgreeOrReject_OpenReq req){
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(" SELECT eid,ORGANIZATIONNO,TAGTYPE FROM DCP_ORG_ORDERSET_KDS " +
				"WHERE eid = '"+req.getRequest().geteId()+"' AND ORGANIZATIONNO = '"+req.getRequest().getShopId()+"' ");
		sql = sqlbuf.toString();
		return sql;
	}

	/**
	 * 查询为TagType 为1 的所有生产标签 关联的商品
	 * @param req
	 * @return
	 */
	public String getPluno(DCP_OrderProductionAgreeOrReject_OpenReq req,String tagType){
		DCP_OrderProductionAgreeOrReject_OpenReq.levReq request = req.getRequest();
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(" SELECT  a.ID,b.ISSINGLEPRODUCE " +
				" FROM DCP_TAGTYPE_DETAIL a " +
				" LEFT JOIN DCP_TAGTYPE b ON a.EID  = b.EID  AND a.TAGGROUPTYPE  = b.TAGGROUPTYPE AND a.TAGNO  = b.TAGNO AND a.TAGGROUPNO  = b.TAGGROUPNO " +
				"WHERE a.EID = '"+request.geteId()+"' AND a.TAGGROUPTYPE = 'GOODS_PROD' ");
		if(tagType.equals("1")){
			sqlbuf.append("  AND a.TAGNO IN ( "+
					" SELECT DISTINCT b.TAGNO " +
					" FROM DCP_ORG_ORDERSET_KDS a " +
					" LEFT JOIN DCP_ORG_ORDERSET_KDS_TAG b ON a.EID = b.EID AND a.STALLID = b.STALLID AND a.ORGANIZATIONNO = b.ORGANIZATIONNO ");
			sqlbuf.append(" WHERE a.EID = '"+request.geteId()+"' AND a.ORGANIZATIONNO = '"+request.getShopId()+"' AND a.TAGTYPE = '1')");
		}

		sql = sqlbuf.toString();
		return sql;
	}

	/**
	 * 关联标签查询订单商品明细
	 * @param map
	 * @return
	 */
	private String getOrderDetail(Map<String,Object> map){
		List<Map<String, Object>> getPlunos = (List<Map<String, Object>>) map.get("PLUNOS");
		String sql = "";
		StringBuffer sqlbuf = new StringBuffer("");
		sqlbuf.append(" SELECT DISTINCT a.*,c.TOT_AMT,TOT_QTY,c.status,c.CREATE_DATETIME,c.CONTMAN,c.CONTTEL,c.GETMAN,c.GETMANTEL," +
				" c.ADDRESS,c.SHIPDATE,c.SHIPSTARTTIME,c.SHIPENDTIME,c.REFUNDREASONNAME,c.MEMO ,C.LOADDOCTYPE " +
				"FROM DCP_ORDER_DETAIL a " +
				"LEFT JOIN DCP_TAGTYPE_DETAIL b ON a.EID  = b.EID  AND a.PLUNO  = b.ID  AND b.TAGGROUPTYPE = 'GOODS_PROD' " +
				"LEFT JOIN DCP_ORDER c ON a.EID = c.EID and a.ORDERNO = c.ORDERNO and a.CHANNELID = c.CHANNELID and a.LOADDOCTYPE = c.LOADDOCTYPE " +
				"WHERE a.EID = '"+map.get("EID").toString()+"' AND a.ORDERNO = '"+map.get("ORDERNO").toString()+"'");
		//  查询指定标签下的商品
			if(!CollectionUtils.isEmpty(getPlunos)){
				sqlbuf.append(" AND a.PLUNO IN (");
				for (Map<String, Object> getPluno : getPlunos) {
					sqlbuf.append("'"+getPluno.get("ID").toString()+"',");
				}
				sqlbuf.deleteCharAt(sqlbuf.length() -1);
				sqlbuf.append(")");
			}
		sql = sqlbuf.toString();
		return sql;
	}

	/**
	 * 查询商品倍量
	 * @param req
	 * @param pluno
	 * @return
	 */
	private String getPlunoMultQty(DCP_OrderProductionAgreeOrReject_OpenReq req,String pluno){
		String bomSql = ""
				+ " select distinct a.pluno ,A.eid,  A.Bomno , A.bomType , A.Restrictshop , A.Pluno , A.UNIT , A.MULQTY , A.EFFDATE , "
				+ " C.material_PluNo AS materialPluNo , C.material_Unit AS materialUnit , c.material_Qty AS materialQty , C.isBuckle ,"
				+ " C.Qty , C.isreplace , c.sortId ,  to_char(B.SHOPID ) AS shopId , d.unitRatio ,"
				+ " E.BOM_UNIT , E.Baseunit , E.sunit "
				+ " from dcp_bom a "
				+ " left join dcp_bom_range b on a.eid=b.eid and a.bomno=b.bomno and b.shopid ='"+req.getRequest().getShopId()+"' "
				+ " inner join dcp_bom_material c on a.eid=c.eid and a.bomno=c.bomno "
                + " and trunc(c.material_bdate)<=trunc(sysdate) and trunc(c.material_edate)>=trunc(sysdate) "
				+ " inner join dcp_goods_unit d on a.eid=d.eid and a.pluno=d.pluno and a.unit=d.ounit and d.prod_unit_use='Y' "
				+ " INNER JOIN Dcp_Goods e ON C.eId = E.EID AND C.MATERIAL_Pluno = E.Pluno AND E.Status = '100' "
				+ " where a.eId='"+req.getRequest().geteId()+"' and trunc(a.effdate)<=trunc(sysdate) and a.status='100' and bomtype = '0'  "
				+ " AND A.pluNo = '"+pluno+"'"
				+ " and (a.restrictshop=0 or (a.restrictshop=1 and b.shopid is not null))";
		return bomSql;
	}

	private String getProcessTaskNO(DCP_OrderProductionAgreeOrReject_OpenReq req) throws Exception  {
		/*
		 * 单据编号在后台按规格生成(固定编码+年月日+5位流水号(比如TBCK201607010001)，流水号取门店该单据最大流水号+1)
		 * 注意固定编码：如果是调拨出库则为DBCK 如果是退货出库则为THCK 如果是次品出库则为CPCK
		 */
        String sql = null;
        String processTaskNO = null;
        String shopId = req.getRequest().getShopId();
        String eId = req.geteId();
        StringBuffer sqlbuf = new StringBuffer("select F_DCP_GETBILLNO('"+eId+"','"+shopId+"','JGRW') PROCESSTASKNO FROM dual");
        sql = sqlbuf.toString();
        List<Map<String, Object>> getQData = this.doQueryData(sql, null);

        if (getQData != null && getQData.isEmpty() == false)
        {
            processTaskNO = (String) getQData.get(0).get("PROCESSTASKNO");
        }
        else
        {
            throw new SPosCodeException(CODE_EXCEPTION_TYPE.E500, "取加工任务单号失败！");
        }
        return processTaskNO;
	}
	
}
