package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_WarningMonitoringDetailReq;
import com.dsc.spos.json.cust.req.DCP_WarningMonitoringDetailReq.level1Elm;
import com.dsc.spos.json.cust.res.DCP_WarningMonitoringDetailRes;
import com.dsc.spos.json.cust.res.DCP_WarningMonitoringDetailRes.cardWarningData;
import com.dsc.spos.json.cust.res.DCP_WarningMonitoringDetailRes.pointWarningData;
import com.dsc.spos.json.cust.res.DCP_WarningMonitoringDetailRes.orderWarningData;
import com.dsc.spos.json.cust.res.DCP_WarningMonitoringDetailRes.orderData;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException;
import com.dsc.spos.service.utils.DispatchService.SPosCodeException.CODE_EXCEPTION_TYPE;
import com.dsc.spos.utils.Check;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.MyCommon;
import com.google.gson.reflect.TypeToken;

public class DCP_WarningMonitoringDetail extends SPosBasicService<DCP_WarningMonitoringDetailReq,DCP_WarningMonitoringDetailRes> {

	@Override
	protected boolean isVerifyFail(DCP_WarningMonitoringDetailReq req) throws Exception {
	// TODO Auto-generated method stub
		boolean isFail = false;
    StringBuffer errMsg = new StringBuffer("");
    int errCt = 0;  //考慮當錯誤很多時則直接顯示格式錯誤；  
    level1Elm requestModel =	req.getRequest();
    if(requestModel==null)
    {
    	errCt++;
	 	  errMsg.append("请求节点request不存在, ");
		  isFail = true;
		  throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
	  }
    
    if(Check.Null(requestModel.getWarningType()))
    {
    	errCt++;
	 	  errMsg.append("监控类型warningType不能为空, ");
		  isFail = true;
    	
    }
    
    
    if (isFail)
    {
    	throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, errMsg.toString());
    }    
    return isFail;
	}

	@Override
	protected TypeToken<DCP_WarningMonitoringDetailReq> getRequestType() {
	// TODO Auto-generated method stub
	return new TypeToken<DCP_WarningMonitoringDetailReq>(){};
	}

	@Override
	protected DCP_WarningMonitoringDetailRes getResponseType() {
	// TODO Auto-generated method stub
	return new DCP_WarningMonitoringDetailRes();
	}

	@Override
	protected DCP_WarningMonitoringDetailRes processJson(DCP_WarningMonitoringDetailReq req) throws Exception {
	// TODO Auto-generated method stub
		level1Elm requestModel =	req.getRequest();		
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		
		DCP_WarningMonitoringDetailRes res = this.getResponse();
		res.setOrderWarningDataList(new ArrayList<orderWarningData>());
		res.setPointWarningDataList(new ArrayList<pointWarningData>());
		res.setCardWarningDataList(new ArrayList<cardWarningData>());
		String sql ="";
	  //给分页字段赋值
		sql = this.getCountSql(req);	
		int totalRecords;								//总笔数
		int totalPages;
		//指定页码的订单信息
		String withasSql_Orderno="";
		List<Map<String, Object>> getQData_Count = this.doQueryData(sql, null);
		if (getQData_Count != null && getQData_Count.isEmpty() == false) 
		{ 			
			Map<String, Object> oneData_Count = getQData_Count.get(0);
			String num = oneData_Count.get("NUM").toString();
			totalRecords=Integer.parseInt(num);

			

			totalPages = totalRecords / req.getPageSize();
			totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;
						
			//計算起啟位置
			int startRow = ((req.getPageNumber() - 1) * req.getPageSize());			
			
			//根据页码取记录
			String sJoinId="";
			String sJoinWarningType="";
			String sJoinBillNo ="";
			
			//因为是根据不同会员卡号或者储值卡卡号分组 ID会重复，			
			Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
			condition.put("ID", true);	
			List<Map<String, Object>> getQHeader=MapDistinct.getMap(getQData_Count, condition);
			
			for (int i = 0; i < getQHeader.size(); i++) 
			{			
				sJoinId+=getQData_Count.get(i).get("ID").toString()+",";
				sJoinWarningType+=getQData_Count.get(i).get("WARNINGTYPE").toString()+",";	
				sJoinBillNo += getQData_Count.get(i).get("BILLNO").toString()+",";			
			}		
			
			//
			Map<String, String> map=new HashMap<String, String>();
			map.put("ID", sJoinId);
			map.put("WARNINGTYPE", sJoinWarningType);			
			map.put("BILLNO", sJoinBillNo);		
			//
			MyCommon cm=new MyCommon();
			withasSql_Orderno=cm.getFormatSourceMultiColWith(map);
			
			map=null;
			cm=null;	
			
		}
		else
		{
			totalRecords = 0;
			totalPages = 0;
		}
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		
		if(withasSql_Orderno.isEmpty()==false)
		{
			sql = this.getQuerySql(req,withasSql_Orderno);
			List<Map<String, Object>> getQDataDetail=this.doQueryData(sql,null);
			if (getQDataDetail != null && getQDataDetail.isEmpty() == false)
			{							
				if(warningType.toLowerCase().equals("order"))
				{
					int i = 1;
					for (Map<String, Object> mapHead : getQData_Count) //这个是 group by的汇总数据，
					{
						orderWarningData oneLv1 = res.new orderWarningData();
						oneLv1.setOrderList(new ArrayList<orderData>());
						String billId_head = mapHead.get("ID").toString();//日志单号
						String billNo_head = mapHead.get("BILLNO").toString();
						String shopId_head = mapHead.get("SHOPID").toString();
						String tot_amt = mapHead.get("TOT_AMT").toString()==null?"0":mapHead.get("TOT_AMT").toString();
						String tot_count = mapHead.get("TOT_COUNT").toString()==null?"0":mapHead.get("TOT_COUNT").toString();
						String warningNo = billNo_head;
						String warningName = "";//值在明细有
						String warningItemDescription = "";//值在明细有
						String warningDate = "";//值在明细有
						String shopName = "";
						oneLv1.setSerialNo(i+"");//汇总那有什么序号 ？默认1
						oneLv1.setWarningNo(warningNo);
						oneLv1.setWarningName(warningName);
						oneLv1.setWarningItemDescription(warningItemDescription);//值在明细有
						oneLv1.setWarningDate(warningDate);//
						oneLv1.setShopId(shopId_head);
						oneLv1.setShopName(shopName);
						oneLv1.setOrderQty(tot_count);
						oneLv1.setOrderAmt(tot_amt);
						for (Map<String, Object> mapDetail : getQDataDetail) 
						{
							orderData oneLv2 = res.new orderData();
							String billId_detail = mapDetail.get("ID").toString();//日志单号
							String billNo_detail = mapDetail.get("BILLNO").toString();
							String shopId_detail = mapDetail.get("SHOPID").toString();
							String shopName_detail =  mapDetail.get("SHOPNAME").toString();
							if(billId_detail==null||billId_detail.isEmpty())
							{
								continue;
							}
							if (billId_head.equals(billId_detail)&&shopId_detail.equals(shopId_head))
							{
								if(warningName.isEmpty())
								{
									warningName = mapDetail.get("BILLNAME").toString()==null?"":mapDetail.get("BILLNAME").toString();
								}
								if(warningItemDescription.isEmpty())
								{
									warningItemDescription = mapDetail.get("WARNINGITEMDESCRIPTION").toString()==null?"":mapDetail.get("WARNINGITEMDESCRIPTION").toString();
								}
								if(warningDate.isEmpty())
								{
									warningDate = mapDetail.get("PUSHDATE").toString()==null?"":mapDetail.get("PUSHDATE").toString();
								}
								
								if(shopName.isEmpty())
								{
									shopName = shopName_detail;
								}
								
								oneLv2.setSerialNo(mapDetail.get("SERIALNO").toString());
								oneLv2.setOrderNo(mapDetail.get("ORDERNO").toString());
								oneLv2.setContMan(mapDetail.get("CONTMAN").toString());
								oneLv2.setMemberId(mapDetail.get("MEMBERID").toString());
								oneLv2.setMemberName(mapDetail.get("MEMBERNAME").toString());
								oneLv2.setShopId(shopId_detail);
								oneLv2.setShopName(shopName_detail);
								oneLv2.setPayAmt(mapDetail.get("PAYAMT").toString()==null?"0":mapDetail.get("PAYAMT").toString());
								//20200109110802
								String create_datetime = mapDetail.get("CREATE_DATETIME").toString();
								if(create_datetime!=null&&create_datetime.length()==14)
								{
									try 
									{
										String year = create_datetime.substring(0, 4);
										String month = create_datetime.substring(4, 6);
										String day = create_datetime.substring(6, 8);
										String hour = create_datetime.substring(8, 10);
										String minute = create_datetime.substring(10, 12);
										String second = create_datetime.substring(12, 14);									
										create_datetime = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;								
									} 
									catch (Exception e) 
									{
									
									}																
								}
								oneLv2.setCreate_dateTime(create_datetime);								
								oneLv2.setbDate(mapDetail.get("BDATE").toString());								
								oneLv2.setOrderType(mapDetail.get("ORDERTYPE").toString());
								oneLv2.setCardNo(mapDetail.get("CARDNO").toString());
								oneLv2.setCardTypeId(mapDetail.get("CARDTYPEID").toString());
								oneLv2.setCardTypeName(mapDetail.get("CARDTYPENAME").toString());
								
								oneLv1.getOrderList().add(oneLv2);								
								
							}
																							
						}
						
						
						oneLv1.setWarningNo(warningNo);//值在明细有
						oneLv1.setWarningName(warningName);//值在明细有
						oneLv1.setWarningItemDescription(warningItemDescription);//值在明细有
						oneLv1.setWarningDate(warningDate);//
						oneLv1.setShopName(shopName);//值在明细有
						
						res.getOrderWarningDataList().add(oneLv1);
						i++;
					}
					
				}
			  else if(warningType.toLowerCase().equals("point"))
				{
			  	int i = 1;
			  	for (Map<String, Object> mapHead : getQData_Count) //这个是 group by的汇总数据，
					{
			  		pointWarningData oneLv1 = res.new pointWarningData();
						oneLv1.setOrderList(new ArrayList<orderData>());
						String billId_head = mapHead.get("ID").toString();//日志单号
						String billNo_head = mapHead.get("BILLNO").toString();
						String memberId_head = mapHead.get("MEMBERID").toString();
						String tot_pointQty = mapHead.get("TOT_POINTQTY").toString()==null?"0":mapHead.get("TOT_POINTQTY").toString();
						String tot_count = mapHead.get("TOT_COUNT").toString()==null?"0":mapHead.get("TOT_COUNT").toString();
						String warningNo = billNo_head;
						String warningName = "";//值在明细有
						String warningItemDescription = "";//值在明细有
						String warningDate = "";//值在明细有
						String memberName = "";
						oneLv1.setSerialNo(i+"");//汇总那有什么序号 ？默认1
						oneLv1.setWarningNo(warningNo);
						oneLv1.setWarningName(warningName);
						oneLv1.setWarningItemDescription(warningItemDescription);//值在明细有
						oneLv1.setWarningDate(warningDate);//
						oneLv1.setMemberId(memberId_head);
						oneLv1.setMemberName(memberName);
						oneLv1.setOrderQty(tot_count);//订单数
						oneLv1.setPointNum(tot_count);//积分次数 = 订单数      一单只会积分一次
						oneLv1.setPointQty(tot_pointQty);
						
						for (Map<String, Object> mapDetail : getQDataDetail) 
						{
							orderData oneLv2 = res.new orderData();
							String billId_detail = mapDetail.get("ID").toString();//日志单号
							String billNo_detail = mapDetail.get("BILLNO").toString();
							String memberId_detail = mapDetail.get("MEMBERID").toString();
							String memberName_detail =  mapDetail.get("MEMBERNAME").toString();
							if(billId_detail==null||billId_detail.isEmpty())
							{
								continue;
							}
							if (billId_head.equals(billId_detail)&&memberId_detail.equals(memberId_head))
							{
								if(warningName.isEmpty())
								{
									warningName = mapDetail.get("BILLNAME").toString()==null?"":mapDetail.get("BILLNAME").toString();
								}
								if(warningItemDescription.isEmpty())
								{
									warningItemDescription = mapDetail.get("WARNINGITEMDESCRIPTION").toString()==null?"":mapDetail.get("WARNINGITEMDESCRIPTION").toString();
								}
								if(warningDate.isEmpty())
								{
									warningDate = mapDetail.get("PUSHDATE").toString()==null?"":mapDetail.get("PUSHDATE").toString();
								}
								
								if(memberName.isEmpty())
								{
									memberName = memberName_detail;
								}
								
								oneLv2.setSerialNo(mapDetail.get("SERIALNO").toString());
								oneLv2.setOrderNo(mapDetail.get("ORDERNO").toString());
								oneLv2.setContMan(mapDetail.get("CONTMAN").toString());
								oneLv2.setMemberId(mapDetail.get("MEMBERID").toString());
								oneLv2.setMemberName(mapDetail.get("MEMBERNAME").toString());
								oneLv2.setShopId(mapDetail.get("SHOPID").toString());
								oneLv2.setShopName(mapDetail.get("SHOPNAME").toString());
								oneLv2.setPayAmt(mapDetail.get("PAYAMT").toString()==null?"0":mapDetail.get("PAYAMT").toString());
								oneLv2.setPointQty(mapDetail.get("POINTQTY").toString()==null?"0":mapDetail.get("POINTQTY").toString());
																
							  //20200109110802
								String create_datetime = mapDetail.get("CREATE_DATETIME").toString();
								if(create_datetime!=null&&create_datetime.length()==14)
								{
									try 
									{
										String year = create_datetime.substring(0, 4);
										String month = create_datetime.substring(4, 6);
										String day = create_datetime.substring(6, 8);
										String hour = create_datetime.substring(8, 10);
										String minute = create_datetime.substring(10, 12);
										String second = create_datetime.substring(12, 14);									
										create_datetime = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;								
									} 
									catch (Exception e) 
									{
									
									}																
								}
								oneLv2.setCreate_dateTime(create_datetime);								
								oneLv2.setbDate(mapDetail.get("BDATE").toString());								
								oneLv2.setOrderType(mapDetail.get("ORDERTYPE").toString());
								oneLv2.setCardNo(mapDetail.get("CARDNO").toString());
								oneLv2.setCardTypeId(mapDetail.get("CARDTYPEID").toString());
								oneLv2.setCardTypeName(mapDetail.get("CARDTYPENAME").toString());
								
								oneLv1.getOrderList().add(oneLv2);								
								
							}
																							
						}
						
						
						oneLv1.setWarningNo(warningNo);//值在明细有
						oneLv1.setWarningName(warningName);//值在明细有
						oneLv1.setWarningItemDescription(warningItemDescription);//值在明细有
						oneLv1.setWarningDate(warningDate);//
						oneLv1.setMemberName(memberName);;//值在明细有
						
						res.getPointWarningDataList().add(oneLv1);
						
						i++;
					}
					
					
				}
			  else if(warningType.toLowerCase().equals("card"))
				{
			  	
			  	int i = 1;
			  	for (Map<String, Object> mapHead : getQData_Count) //这个是 group by的汇总数据，
					{
			  		cardWarningData oneLv1 = res.new cardWarningData();
						oneLv1.setOrderList(new ArrayList<orderData>());
						String billId_head = mapHead.get("ID").toString();//日志单号
						String billNo_head = mapHead.get("BILLNO").toString();
						String cardNo_head = mapHead.get("CARDNO").toString();				
						String tot_cardPayAmt = mapHead.get("TOT_CARDPAYAMT").toString()==null?"0":mapHead.get("TOT_CARDPAYAMT").toString();
						String tot_count = mapHead.get("TOT_COUNT").toString()==null?"0":mapHead.get("TOT_COUNT").toString();
						String warningNo = billNo_head;
						String warningName = "";//值在明细有
						String warningItemDescription = "";//值在明细有
						String warningDate = "";//值在明细有
						String cardTypeId = "";
						String cardTypeName = "";
						String memberId = "";
						String memberName = "";
						oneLv1.setSerialNo(i+"");//汇总那有什么序号 ？
						oneLv1.setWarningNo(warningNo);
						oneLv1.setWarningName(warningName);
						oneLv1.setWarningItemDescription(warningItemDescription);//值在明细有
						oneLv1.setWarningDate(warningDate);//
						oneLv1.setCardTypeId(cardTypeId);
						oneLv1.setCardTypeName(cardTypeName);
						oneLv1.setMemberId(memberId);
						oneLv1.setMemberName(memberName);
						oneLv1.setCardNo(cardNo_head);
						oneLv1.setConsumeNum(tot_count);//消费次数 同一个订单 多次刷同一卡，会多次
						oneLv1.setConsumeAmt(tot_cardPayAmt);
											
						for (Map<String, Object> mapDetail : getQDataDetail) 
						{
							orderData oneLv2 = res.new orderData();
							String billId_detail = mapDetail.get("ID").toString();//日志单号
							String billNo_detail = mapDetail.get("BILLNO").toString();
							String cardNo_detail = mapDetail.get("CARDNO").toString();
							String memberId_detail = mapDetail.get("MEMBERID").toString();
							String memberName_detail =  mapDetail.get("MEMBERNAME").toString();
							
							if(billId_detail==null||billId_detail.isEmpty()||cardNo_detail==null||cardNo_detail.isEmpty())
							{
								continue;
							}
							if (billId_head.equals(billId_detail)&&cardNo_detail.equals(cardNo_head))
							{
								if(warningName.isEmpty())
								{
									warningName = mapDetail.get("BILLNAME").toString()==null?"":mapDetail.get("BILLNAME").toString();
								}
								if(warningItemDescription.isEmpty())
								{
									warningItemDescription = mapDetail.get("WARNINGITEMDESCRIPTION").toString()==null?"":mapDetail.get("WARNINGITEMDESCRIPTION").toString();
								}
								if(warningDate.isEmpty())
								{
									warningDate = mapDetail.get("PUSHDATE").toString()==null?"":mapDetail.get("PUSHDATE").toString();
								}
								
								if(memberId.isEmpty())
								{
									memberId = memberId_detail;
								}
								
								if(memberName.isEmpty())
								{
									memberName = memberName_detail;
								}
								
								if(cardTypeId.isEmpty())
								{
									cardTypeId = mapDetail.get("CARDTYPEID").toString()==null?"":mapDetail.get("CARDTYPEID").toString();
								}
								
								if(cardTypeName.isEmpty())
								{
									cardTypeName = mapDetail.get("CARDTYPENAME").toString()==null?"":mapDetail.get("CARDTYPENAME").toString();
								}
								
								oneLv2.setSerialNo(mapDetail.get("SERIALNO").toString());
								oneLv2.setOrderNo(mapDetail.get("ORDERNO").toString());
								oneLv2.setContMan(mapDetail.get("CONTMAN").toString());
								oneLv2.setMemberId(mapDetail.get("MEMBERID").toString());
								oneLv2.setMemberName(mapDetail.get("MEMBERNAME").toString());
								oneLv2.setShopId(mapDetail.get("SHOPID").toString());
								oneLv2.setShopName(mapDetail.get("SHOPNAME").toString());
								oneLv2.setPayAmt(mapDetail.get("PAYAMT").toString()==null?"0":mapDetail.get("PAYAMT").toString());
								oneLv2.setCardPayAmt(mapDetail.get("CARDPAYAMT").toString());
								oneLv2.setCardNo(cardNo_detail);
															
	    					//20200109110802
								String create_datetime = mapDetail.get("CREATE_DATETIME").toString();
								if(create_datetime!=null&&create_datetime.length()==14)
								{
									try 
									{
										String year = create_datetime.substring(0, 4);
										String month = create_datetime.substring(4, 6);
										String day = create_datetime.substring(6, 8);
										String hour = create_datetime.substring(8, 10);
										String minute = create_datetime.substring(10, 12);
										String second = create_datetime.substring(12, 14);									
										create_datetime = year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;								
									} 
									catch (Exception e) 
									{
									
									}																
								}
								oneLv2.setCreate_dateTime(create_datetime);								
								oneLv2.setbDate(mapDetail.get("BDATE").toString());								
								oneLv2.setOrderType(mapDetail.get("ORDERTYPE").toString());						
								oneLv2.setCardTypeId(mapDetail.get("CARDTYPEID").toString());
								oneLv2.setCardTypeName(mapDetail.get("CARDTYPENAME").toString());
								
								oneLv1.getOrderList().add(oneLv2);								
								
							}
																							
						}
						
						
						oneLv1.setWarningNo(warningNo);//值在明细有
						oneLv1.setWarningName(warningName);//值在明细有
						oneLv1.setWarningItemDescription(warningItemDescription);//值在明细有
						oneLv1.setWarningDate(warningDate);//
						oneLv1.setMemberId(memberId);
						oneLv1.setMemberName(memberName);;//值在明细有
						oneLv1.setCardTypeId(cardTypeId);
						oneLv1.setCardTypeName(cardTypeName);
						
						res.getCardWarningDataList().add(oneLv1);
						
						i++;
					}
					
				}
		
				
			}
		}
	
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
	// TODO Auto-generated method stub
	
	}

	@Override
	protected String getQuerySql(DCP_WarningMonitoringDetailReq req) throws Exception {
	// TODO Auto-generated method stub
	return null;
	}
	
	private String getCountSql(DCP_WarningMonitoringDetailReq req) throws Exception {
		// TODO Auto-generated method stub
		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		
		String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningNo = requestModel.getWarningNo();//监控编号
		String orderNo = requestModel.getOrderNo();//销售单号
		String shopId = requestModel.getShopId();
		String memberId = requestModel.getMemberId();
		String cardTypeId = requestModel.getCardTypeId();
		String cardNo = requestModel.getCardNo();
		String beginDate = requestModel.getBeginDate();
		String endDate = requestModel.getEndDate();
		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");
		//分页处理
		int pageNumber=req.getPageNumber();
		int pageSize=req.getPageSize();
	 //没传分页默认 第1页 每页10条
		if(req.getPageSize()==0)
		{
			pageSize = 10;
			req.setPageSize(10);
		}
		if(req.getPageNumber()==0)
		{
			pageNumber = 1;
			req.setPageNumber(1);
		}
		
		
		int startRow=(pageNumber-1) * pageSize;
		int endRow = startRow+pageSize;
		sqlbuf.append("select * from (");	
		sqlbuf.append(" select count(*) over() as num,rownum rn, aa.* from (");	
		if(warningType.toLowerCase().equals("order"))
		{
			//不同类型分组不同，后面group by 也要修改
			sqlbuf.append(" select id,billno,warningtype,shopid,sum(tot_amt) as tot_amt ,count(*) as tot_count  from (");
			
		}
		else if (warningType.toLowerCase().equals("point")) 
		{
			sqlbuf.append(" select id,billno,warningtype,memberId,sum(pointqty) as tot_pointqty ,count(*) as tot_count  from (");
	
		}
		else if (warningType.toLowerCase().equals("card")) 
		{
			sqlbuf.append(" select id,billno,warningtype,cardno,sum(cardpayamt) as tot_cardpayamt ,count(*) as tot_count  from (");
		}
		else 
		{
			throw new SPosCodeException(CODE_EXCEPTION_TYPE.E400, "监控类型warningType("+warningType+")无效,请检查！");	
		}
		
		sqlbuf.append(" select a.*,b.warningtype,b.pushtime from dcp_warninglog_detail a");
		sqlbuf.append(" inner join dcp_warninglog b  on a.eid=b.eid and a.id=b.id ");
		sqlbuf.append("  where a.eid='"+eId+"'");
		
		sqlbuf.append(" and b.warningtype='"+warningType+"'");
		
		if (beginDate != null && beginDate.trim().isEmpty() == false)
		{
			String beginDate_format = beginDate+" 00:00:00";		
			sqlbuf.append(" and b.pushtime>='"+beginDate_format+"'");
							
		}
		if (endDate != null && endDate.trim().isEmpty() == false)
		{
			String endDate_format = endDate+" 23:59:59";		
			sqlbuf.append(" and b.pushtime<='"+endDate_format+"'");
							
		}	
		if (warningNo != null && warningNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.billno='"+warningNo+"'");			
		}
		if (orderNo != null && orderNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.orderNo='"+orderNo+"'");
			
		}			
		if (shopId != null && shopId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.shopId='"+shopId+"'");			
		}
		if (memberId != null && memberId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.memberId='"+memberId+"'");			
		}
		if (cardTypeId != null && cardTypeId.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.cardTypeId='"+cardTypeId+"'");			
		}
		if (cardNo != null && cardNo.trim().isEmpty() == false)
		{
			sqlbuf.append(" and a.cardNo='"+cardNo+"'");			
		}
		
		sqlbuf.append(" order by b.warningtype,b.pushtime desc");
		
		
		if(warningType.toLowerCase().equals("order"))
		{
			//不同类型分组不同，后面group by 也要修改
			sqlbuf.append(" ) group by id,billno,warningtype,shopid ");			
		}
		else if (warningType.toLowerCase().equals("point")) 
		{
		//不同类型分组不同，后面group by 也要修改
			sqlbuf.append(" ) group by id,billno,warningtype,memberid ");		
		
		}
		else if (warningType.toLowerCase().equals("card")) 
		{	
		//不同类型分组不同，后面group by 也要修改
			sqlbuf.append(" ) group by id,billno,warningtype,cardno ");		
		}
		
		sqlbuf.append(" order by id  desc   ) aa");
		sqlbuf.append(" ) where rn>"+startRow+" and rn<="+endRow);
		
		
		 return sqlbuf.toString();
		}

	private String getQuerySql(DCP_WarningMonitoringDetailReq req,String withasSql) throws Exception
	{
		String eId = req.geteId();
		String langType = req.getLangType();
		level1Elm requestModel =	req.getRequest();		
	/*	String warningType = requestModel.getWarningType();//枚举: null：全部,order：零售单,point：会员积分,card：储值卡
		String warningNo = requestModel.getWarningNo();//监控编号
		String orderNo = requestModel.getOrderNo();//销售单号
		String shopId = requestModel.getShopId();
		String memberId = requestModel.getMemberId();
		String cardTypeId = requestModel.getCardTypeId();
		String cardNo = requestModel.getCardNo();
		String beginDate = requestModel.getBeginDate();
		String endDate = requestModel.getEndDate();*/
		String sql= "";
		StringBuffer sqlbuf=new StringBuffer("");
		sqlbuf.append("select * from (");
		sqlbuf.append(" with P1 AS ( "+withasSql+" ) ");//康总的做法
		sqlbuf.append(" select a.*,b.billname,b.warningitemdescription,substr(b.pushtime,0,10) as pushdate ");
		sqlbuf.append(" from p1 ");
		sqlbuf.append(" inner join dcp_warninglog_detail a on a.id=p1.id ");
		sqlbuf.append(" inner join dcp_warninglog b  on a.eid=b.eid and a.id=b.id");
		sqlbuf.append(" where a.eid='"+eId+"'");
		sqlbuf.append(")");
				
		return sqlbuf.toString();
	}
	
}
