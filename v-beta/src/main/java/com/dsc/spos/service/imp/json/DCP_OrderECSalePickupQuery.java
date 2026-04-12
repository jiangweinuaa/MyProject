package com.dsc.spos.service.imp.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dsc.spos.json.cust.req.DCP_OrderECSalePickupQueryReq;
import com.dsc.spos.json.cust.res.DCP_OrderECSalePickupQueryRes;
import com.dsc.spos.service.SPosBasicService;
import com.dsc.spos.utils.MapDistinct;
import com.dsc.spos.utils.PosPub;
import com.google.gson.reflect.TypeToken;

/**
 * 订单/销售单点货查询
 * @author yuanyy 2019-03-12
 *
 */
public class DCP_OrderECSalePickupQuery extends SPosBasicService<DCP_OrderECSalePickupQueryReq, DCP_OrderECSalePickupQueryRes> {

	@Override
	protected boolean isVerifyFail(DCP_OrderECSalePickupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected TypeToken<DCP_OrderECSalePickupQueryReq> getRequestType() {
		// TODO Auto-generated method stub
		return new TypeToken<DCP_OrderECSalePickupQueryReq>(){};
	}

	@Override
	protected DCP_OrderECSalePickupQueryRes getResponseType() {
		// TODO Auto-generated method stub
		return new DCP_OrderECSalePickupQueryRes();
	}

	@Override
	protected DCP_OrderECSalePickupQueryRes processJson(DCP_OrderECSalePickupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		DCP_OrderECSalePickupQueryRes res = null;
		res = this.getResponse();
		int totalRecords = 0; //总笔数
		int totalPages = 0;	
		try {
			String sql = null;
			sql = this.getQuerySql(req);
			String[] conditionValues = {};
			List<Map<String , Object>> getDatas = this.doQueryData(sql, conditionValues);

			if(getDatas.size() > 0)
			{
				//處理訂單以配送門店為歸屬門店
				String myshop=getDatas.get(0).get("SHOPID").toString();
				
				String num = getDatas.get(0).get("NUM").toString();
				totalRecords=Integer.parseInt(num);

				//算總頁數
				totalPages = 0;
				totalPages = totalRecords / req.getPageSize();
				totalPages = (totalRecords % req.getPageSize() > 0) ? totalPages + 1 : totalPages;	

				//点货完成自动打印托运单
				String ecPickupAutoPrintExpress=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "ecPickupAutoPrintExpress");

				if (ecPickupAutoPrintExpress==null || ecPickupAutoPrintExpress.equals("")) 
				{
					ecPickupAutoPrintExpress=PosPub.getPARA_SMS(dao, req.geteId(), "", "ecPickupAutoPrintExpress");
				}

				if (ecPickupAutoPrintExpress==null||ecPickupAutoPrintExpress.equals("Y")==false) 
				{
					ecPickupAutoPrintExpress="N";
				}

				//新竹的发货所4码,如果单据中此栏位没有值，给此值
				String sSendsiteno=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "SenderSiteno");
				if (sSendsiteno==null || sSendsiteno.equals("")) 
				{
					sSendsiteno=PosPub.getPARA_SMS(dao, req.geteId(), req.getShopId(), "SenderSiteno");
				}
				if (sSendsiteno==null) 
				{
					sSendsiteno="";
				}

				Map<String, Boolean> condition = new HashMap<String, Boolean>(); //查詢條件
				condition.put("ECORDERNO", true);		
				//调用过滤函数
				List<Map<String, Object>> getQHeader=MapDistinct.getMap(getDatas, condition);
				res.setDatas(new ArrayList<DCP_OrderECSalePickupQueryRes.level1Elm>());

				for (Map<String, Object> oneData : getQHeader) 
				{
					DCP_OrderECSalePickupQueryRes.level1Elm lev1 = res.new level1Elm();
					lev1.setDatas(new ArrayList<DCP_OrderECSalePickupQueryRes.level2Elm>());

					String ecOrderNo = oneData.get("ECORDERNO").toString();
					String ecLoadType = oneData.get("ECLOADTYPE").toString();
					String shipDate = oneData.get("SHIPDATE").toString();
					String shipHourType = oneData.get("SHIPHOURTYPE").toString();

					String orderer = oneData.get("ORDERER").toString();
					String ordererPhone = oneData.get("ORDERERPHONE").toString();
					String receiver = oneData.get("RECEIVER").toString();
					String receiverMobile = oneData.get("RECEIVERMOBILE").toString();
					String receiverPhone = oneData.get("RECEIVERPHONE").toString();
					String receiverAddress = oneData.get("RECEIVERADDRESS").toString();
					String receiverEmail = oneData.get("RECEIVEREMAIL").toString();
					String memo = oneData.get("MEMO").toString();
					String totQty = oneData.get("TOTQTY").toString();
					String getShop = oneData.get("GETSHOP").toString();
					String getShopName = oneData.get("GETSHOPNAME").toString();
					String deliveryType = oneData.get("DELIVERYTYPE").toString();
					String payStatus = oneData.get("PAYSTATUS").toString();
					String payAmt = oneData.get("PAYAMT").toString();
					String totAmt = oneData.get("TOTAMT").toString();
					String shopeeMode = oneData.get("SHOPEEMODE").toString();
					String shopeeAddressId = oneData.get("SHOPEEADDRESSID").toString();
					String shopeePickuptimeId = oneData.get("SHOPEEPICKUPTIMEID").toString();
					String shopeeBranchId = oneData.get("SHOPEEBRANCHID").toString();
					String shopeeSenderRealName = oneData.get("SHOPEESENDERREALNAME").toString();
					String orderPackageId = oneData.get("SHIP_ORDERPACKAGEID").toString();
					
					String customerNo = oneData.get("CUSTOMERNO").toString();
					String customerName = oneData.get("CUSTOMERNAME").toString();
					String receiverSiteno = oneData.get("RECEIVER_SITENO").toString();
					String distanceNo = oneData.get("DISTANCENO").toString();
					String distanceName = oneData.get("DISTANCENAME").toString();
					String receiverFivecode = oneData.get("RECEIVER_FIVECODE").toString();
					String receiverSevencode = oneData.get("RECEIVER_SEVENCODE").toString();
					String senderSiteno = oneData.get("SENDER_SITENO").toString();
					String ecPlatformNo = oneData.get("ECPLATFORMNO").toString();
					String ecPlatformName = oneData.get("ECPLATFORMNAME").toString();
					
					String lgPlatformNo=deliveryType;
					String lgPlatformName="";
					//0、无 1 自配送 2 顺丰 3百度 4达达 5 人人 6 闪送 7.7-11超商 8.全家超商 9.黑猫宅急便 10.莱而富超商 11.OK超商 12.mingjie大件物流 13.中华邮政 14.卖家宅配 15.新竹物流 16.绿界7-11超商 17.绿界全家超商 18.绿界莱而富超商 19.绿界OK超商
					if (lgPlatformNo.equals("7")) //
					{
						lgPlatformNo="dzt";
						lgPlatformName="大智通";
					}
					else if (lgPlatformNo.equals("8") ||lgPlatformNo.equals("10") || lgPlatformNo.equals("11") ) //
					{
						lgPlatformNo="cvs";
						lgPlatformName="便利達康";
					}
					else if (lgPlatformNo.equals("9")) //
					{
						lgPlatformNo="egs";
						lgPlatformName="黑貓宅急便";
					}
					else if (lgPlatformNo.equals("15")) //
					{
						lgPlatformNo="htc";
						lgPlatformName="新竹物流";
					}
					else if (lgPlatformNo.equals("16") ||lgPlatformNo.equals("17")||lgPlatformNo.equals("18")||lgPlatformNo.equals("19") ) //
					{
						lgPlatformNo="greenworld";
						lgPlatformName="綠界";
					}
					
					//为空，给系统参数值
					if (senderSiteno==null || senderSiteno.equals("")) 
					{
						senderSiteno=sSendsiteno;
					}

					String expressBilltype = oneData.get("EXPRESSBILLTYPE").toString();
					String expressNo = oneData.get("DELIVERYNO").toString();

					lev1.setEcOrderNo(ecOrderNo);
					lev1.setEcLoadType(ecLoadType);
					lev1.setShipDate(shipDate);
					lev1.setShipHourType(shipHourType);
					lev1.setOrderer(orderer);
					lev1.setOrdererPhone(ordererPhone);
					lev1.setReceiver(receiver);
					lev1.setReceiverMobile(receiverMobile);
					lev1.setReceiverPhone(receiverPhone);
					lev1.setReceiverAddress(receiverAddress);
					lev1.setReceiverEmail(receiverEmail);
					lev1.setMemo(memo);
					lev1.setTotQty(totQty);
					lev1.setGetShop(getShop);
					lev1.setGetShopName(getShopName);
					lev1.setDeliveryType(deliveryType);
					lev1.setPayStatus(payStatus);
					lev1.setPayAmt(payAmt);
					lev1.setTotAmt(totAmt);
					lev1.setShopeeMode(shopeeMode);
					lev1.setShopeeAddressId(shopeeAddressId);
					lev1.setShopeeBranchId(shopeeBranchId);
					lev1.setShopeePickuptimeId(shopeePickuptimeId);
					lev1.setShopeeSenderRealName(shopeeSenderRealName);
					lev1.setOrderPackageId(orderPackageId);
					
					lev1.setCustomerNO(customerNo);
					lev1.setCustomerName(customerName);
					lev1.setEcPickupAutoPrintExpress(ecPickupAutoPrintExpress);
					lev1.setExpressBilltype(expressBilltype);
					lev1.setReceiverSiteno(receiverSiteno);
					lev1.setSenderSiteno(senderSiteno);
					lev1.setDistanceNo(distanceNo);
					lev1.setDistanceName(distanceName);
					lev1.setReceiverFivecode(receiverFivecode);
					lev1.setReceiverSevencode(receiverSevencode);
					lev1.setExpressNo(expressNo);
					lev1.setEcPlatformNo(ecPlatformNo);
					lev1.setEcPlatformName(ecPlatformName);
					lev1.setLgPlatformNo(lgPlatformNo);
					lev1.setLgPlatformName(lgPlatformName);
					
					//oneLv1.setNotGoodsMode(notGoodsMode);
					for (Map<String, Object> oneData2 : getDatas) 
					{
						if(ecOrderNo.equals(oneData2.get("ECORDERNO"))){
							DCP_OrderECSalePickupQueryRes.level2Elm lev2 = res.new level2Elm();
							lev2.setItem(oneData2.get("ITEM").toString());
							lev2.setoItem(oneData2.get("OITEM").toString());
							lev2.setPluNo(oneData2.get("PLUNO").toString());
							lev2.setPluName(oneData2.get("PLUNAME").toString());
							lev2.setBarcode(oneData2.get("BARCODE").toString());

							lev2.setQty(oneData2.get("QTY").toString());
							lev2.setSpecName(oneData2.get("SPECNAME").toString());
							lev2.setAmt(oneData2.get("AMT").toString());

							lev1.getDatas().add(lev2);
							lev2 = null;
						}
					}
					res.getDatas().add(lev1);
					lev1 = null;
				}
			}

		}
		catch (Exception e) {
	

		}
		res.setPageNumber(req.getPageNumber());
		res.setPageSize(req.getPageSize());
		res.setTotalRecords(totalRecords);
		res.setTotalPages(totalPages);
		return res;
	}

	@Override
	protected void processRow(Map<String, Object> row) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getQuerySql(DCP_OrderECSalePickupQueryReq req) throws Exception {
		// TODO Auto-generated method stub
		String sql = null;
		StringBuffer sqlbuf = new StringBuffer();
		String eId = req.geteId();
		String shopId = req.getShopId();
		String langType = req.getLangType();

		String ecType = req.getEcType();
		String ecOrderNo = req.getEcOrderNo();
		String ecPlatformNo = req.getEcPlatformNo();

		int pageNumber = req.getPageNumber();
		if (req.getPageNumber()==0) 
		{
			pageNumber=1;
		}

		int pageSize = req.getPageSize();
		if (req.getPageSize()==0) 
		{
			pageSize=20;
		}

		//計算起啟位置
		int startRow=(pageNumber-1) * pageSize;

		sqlbuf.append(" select * from ( "
				+ " SELECT count(distinct a.orderNO) over() num, dense_rank() over(order by a.orderNO) rn,  a.shipdate, a.shiphourType , "
				+ " a.contman AS orderer, a.conttel AS ordererPhone , a.getman AS receiver , a.getmantel AS receiverPhone , a.getmantel AS receiverMobile ,"
				+ " a.address AS receiverAddress , a.getmanemail AS receiverEmail ,"
				+ " a.memo , a.tot_qty AS totQty , a.shippingshop  AS getShop , a.shippingshopName AS getShopName  , "
				+ " b.item , b.item as oitem, b.pluno , b.pluname, b.plubarcode AS barCode , b.qty , b.specName , b.amt ,"
				+ " a.orderNO AS ecOrderNo , '1'  AS ecLoadType  , a.deliveryType , a.payStatus , a.payAmt ,a.tot_amt as totAmt, "
				+ " a.SHOPEE_MODE as shopeeMode, a.SHOPEE_ADDRESS_ID  as shopeeAddressId , "
				+ " a.SHOPEE_PICKUP_TIME_ID as shopeePickuptimeId ,"
				+ " a.SHOPEE_BRANCH_ID  as shopeeBranchId , a.SHOPEE_SENDER_REAL_NAME as shopeeSenderRealName ,"
				+ " a.SHIP_ORDERPACKAGEID, "
				+ " a.ECCUSTOMERNO as customerNo , d.customer_Name as customerName , "
				+ " a.RECEIVER_SITENO,a.DISTANCENO,a.DISTANCENAME,a.RECEIVER_FIVECODE,a.RECEIVER_SEVENCODE,"
				+ " a.SENDER_SITENO,a.EXPRESSBILLTYPE,a.DELIVERYNO,a.load_doctype ecPlatformNo , "
				+ " (case when a.load_doctype= 'shopee' then '虾皮' " 
				+ " when a.load_doctype= '91app' then '91APP' " 
				+ " when a.load_doctype='yahoosuper' then 'Yahoo' " 
				+ " when a.load_doctype='letian' then '乐天' " 
				+ " when a.load_doctype='pchome' then 'PChome' " 
				+ " when a.load_doctype='momo' then 'MOMO' " 
				+ " when a.load_doctype='3' then '微商城' " 
				+ " else '其他' end ) ecPlatformName ,"
				+ " a.SHOPID "
				+ " FROM  OC_Order a  "
				+ " LEFT JOIN OC_order_detail b ON a.EID = b.EID AND a.SHOPID = b.SHOPID AND a.orderNo = b.orderNo "
				+ " LEFT JOIN DCP_CUSTOMER_LANG d on a.EID = d.EID and a.ecCustomerNo = d.Customer "
				+ " and d.lang_type = '"+langType+"' "
				// status 固定为2 ，已接单。   原来写的是1 订单开立
				//0需调度 1.订单开立 2.已接单 3.已拒单 4.生产接单 5.生产拒单 6.完工入库 7.内部调拨 8.待提货 9.待配送 10.已发货 11.已完成 12.已退单 13.电商已点货 14开始制作
				+ " WHERE a.EID = '"+eId+"' "
				+ " and (a.status = '2' or a.status = '4' or a.status = '6' or a.status = '7') "
				+ " and (a.SHOPID = '"+shopId+"' or a.shippingshop='"+shopId+"')  "
				);

		//		if (ecType != null && ecType.length()!=0) { 	
		//			sqlbuf.append( " AND  a.LOAD_DOCTYPE = '"+ecType+"'");
		//		}

		if (ecOrderNo != null && ecOrderNo.length()!=0) { 	
			sqlbuf.append( " AND  a.OrderNo = '"+ecOrderNo+"'");
		}

		if (req.getKeyTxt() != null && req.getKeyTxt().length()!=0) { 	
			sqlbuf.append( " AND  a.OrderNo like  '%%"+req.getKeyTxt()+"%%' ");
		}

		if (ecPlatformNo != null && ecPlatformNo.length()!=0) { 	
			sqlbuf.append( " AND  a.LOAD_DOCTYPE = '"+ecPlatformNo+"'");
		}

		sqlbuf.append( "  ORDER BY a.orderNo  )  " 
				+  " where rn > "+startRow+" and rn <= "+(startRow+pageSize)+ " order by ecOrderNO ");

		sql = sqlbuf.toString();
		return sql;
	}

}
